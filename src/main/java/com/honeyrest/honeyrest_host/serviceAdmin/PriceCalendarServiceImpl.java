package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.*;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.PriceCalendarRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceCalendarServiceImpl implements PriceCalendarService {

    private final PriceCalendarRepository priceCalendarRepository;
    private final RoomRepository roomRepository;
    private final EntityManager em;
    private final ReservationRepository reservationRepository;

    @Override
    public PriceCalendarDTO getMonth(Long companyId,
                                     Long accommodationId,
                                     YearMonth ym,
                                     Integer minAvailable) {
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // 회사(+선택: 숙소)의 객실 전체
        Page<Room> page = roomRepository.findRoomsOfCompany(companyId, accommodationId, Pageable.unpaged());
        List<Room> rooms = page.getContent();

        // 월 범위 price_calendar
        List<PriceCalendar> rows = priceCalendarRepository.findMonth(start, end, null);

        // roomId -> date -> row
        Set<Long> roomIdSet = rooms.stream().map(Room::getRoomId).collect(Collectors.toSet());
        Map<Long, Map<LocalDate, PriceCalendar>> byRoom = rows.stream()
                .filter(pc -> pc.getRoom() != null && roomIdSet.contains(pc.getRoom().getRoomId()))
                .filter(pc -> minAvailable == null ||
                              (pc.getAvailableRoom() != null && pc.getAvailableRoom() >= minAvailable))
                .collect(Collectors.groupingBy(pc -> pc.getRoom().getRoomId(),
                        Collectors.toMap(PriceCalendar::getDate, Function.identity(), (a, b) -> a)));

        // DTO 조립
        List<RoomCalendarDTO> roomCalendars = new ArrayList<>();
        for (Room r : rooms) {
            List<CalendarCellDTO> days = new ArrayList<>(ym.lengthOfMonth());
            Map<LocalDate, PriceCalendar> perDate = byRoom.getOrDefault(r.getRoomId(), Collections.emptyMap());
            for (int d = 1; d <= ym.lengthOfMonth(); d++) {
                LocalDate date = ym.atDay(d);
                PriceCalendar pc = perDate.get(date);
                days.add(CalendarCellDTO.builder()
                        .date(date)
                        .price(pc != null ? pc.getPrice() : null)
                        .available(pc != null ? pc.getAvailableRoom() : null)
                        .build());
            }

            RoomHeaderDTO header = RoomHeaderDTO.builder()
                    .roomId(r.getRoomId())
                    .roomName(r.getName()) // 엔티티 필드명이 roomName이면 getRoomName()으로 변경
                    .accommodationId(r.getAccommodation().getAccommodationId())
                    .accommodationName(r.getAccommodation().getName())
                    .totalRooms(r.getTotalRooms())
                    .basePrice(r.getPrice())
                    .build();

            roomCalendars.add(RoomCalendarDTO.builder()
                    .room(header)
                    .yearMonth(ym)
                    .days(days)
                    .build());
        }

        return PriceCalendarDTO.builder()
                .companyId(companyId)
                .accommodationId(accommodationId)
                .yearMonth(ym)
                .rooms(roomCalendars)
                .build();
    }

    @Override
    @Transactional
    public boolean upsert(Long roomId, LocalDate date, BigDecimal price, Integer available) {
        int updated = priceCalendarRepository.updateValues(roomId, date, price, available);
        if (updated > 0) return false; // updated
        priceCalendarRepository.upsertMaria(roomId, date, price, available);
        return true; // created
    }

    @Override
    @Transactional
    public void bulkUpsert(List<BulkItem> items) {
        if (items == null || items.isEmpty()) return;

        final int CHUNK = 1000;
        for (int i = 0; i < items.size(); i++) {
            BulkItem it = items.get(i);
            priceCalendarRepository.upsertMaria(
                    it.getRoomId(), it.getDate(), it.getPrice(), it.getAvailable()
            );
            if (i % CHUNK == CHUNK - 1) {
                em.flush();
                em.clear();
            }
        }
    }

    @Override
    @Transactional
    public void bulkUpsert(PriceCalendarDTO payload) {
        if (payload == null || payload.getRooms() == null) return;
        List<BulkItem> items = new ArrayList<>();
        payload.getRooms().forEach(roomDTO ->
                roomDTO.getDays().forEach(cell ->
                        items.add(new BulkItem(
                                roomDTO.getRoom().getRoomId(),
                                cell.getDate(),
                                cell.getPrice(),
                                cell.getAvailable()
                        ))
                )
        );
        bulkUpsert(items);
    }

    @Override
    public List<SalesStatDTO> stats(Long accommodationId, Long roomId,
                                    LocalDate start, LocalDate end,
                                    String granularity) {
        throw new UnsupportedOperationException("매출/정산 통계는 다음 단계에서 구현");
    }

    @Override
    public Map<LocalDate, PriceCalendarDTO> getCalendarData(Long roomId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, PriceCalendarDTO> calendarMap = new LinkedHashMap<>();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Room ID: " + roomId));

        // 기간과 겹치는 예약 전체
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate);


        // 날짜 쿠폰
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate d = date;

            // 체크인 <= d < 체크아웃
            long reservedCount = reservations.stream()
                    .filter(r -> !r.getCheckInDate().isAfter(d) && r.getCheckOutDate().isAfter(d))
                    .count();

            int available = Math.max(room.getTotalRooms() - (int) reservedCount, 0);

            calendarMap.put(date, PriceCalendarDTO.builder()
                    .roomId(room.getRoomId())
                    .date(date)
                    .price(room.getPrice())    // 가격을 price_calendar에 덮어 씌우기
                    .availableRoom(available)
                    .build());
        }

        return calendarMap;
    }

    @Override
    public List<DailyOverviewDTO> getDailyOverview(Long companyId, Long accommodationId, LocalDate start, LocalDate end) {
        return priceCalendarRepository.findDailyOverview(companyId, accommodationId, Date.valueOf(start), Date.valueOf(end))
                .stream().map(p -> DailyOverviewDTO.builder()
                        .date(p.getDate().toLocalDate())
                        .totalRoomsSum(p.getTotalRoomsSum())
                        .availableSum(p.getAvailableSum())
                        .maxPrice(p.getMaxPrice())
                        .minPrice(p.getMinPrice())
                        .build())
                .toList();
    }

    @Override
    public List<GridCellDTO> getGridCells(Long companyId, Long accommodationId, LocalDate start, LocalDate end) {
        return priceCalendarRepository.findGridCells(companyId, accommodationId, Date.valueOf(start), Date.valueOf(end))
                .stream().map(p -> GridCellDTO.builder()
                        .roomId(p.getRoomId())
                        .roomName(p.getRoomName())
                        .date(p.getDate().toLocalDate())
                        .price(p.getPrice())
                        .available(p.getAvailable())
                        .totalRooms(p.getTotalRooms())
                        .build()).toList();
    }


}