package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.*;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repository.PriceCalendarRepository;
import com.honeyrest.honeyrest_host.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    public PriceInventoryCalendarDTO getMonth(Long companyId,
                                              Long accommodationId,
                                              YearMonth ym,
                                              Integer minAvailable) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        // 회사(+선택: 숙소)의 객실 전체
        Page<Room> page = roomRepository.findRoomsOfCompany(companyId, accommodationId,Pageable.unpaged());
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
                        Collectors.toMap(PriceCalendar::getDate, Function.identity(), (a,b)->a)));

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

        return PriceInventoryCalendarDTO.builder()
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
    public void bulkUpsert(PriceInventoryCalendarDTO payload) {
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
}