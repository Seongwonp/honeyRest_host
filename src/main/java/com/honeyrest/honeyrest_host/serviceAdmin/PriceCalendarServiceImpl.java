package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.*;
import com.honeyrest.honeyrest_host.dtoAdmin.reports.SalesStatDTO;
import com.honeyrest.honeyrest_host.entity.PriceCalendar;
import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Room;
import com.honeyrest.honeyrest_host.repositoryAdmin.price.PriceCalendarRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
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
    private final ReservationRepository reservationRepository;

    // 예약 수량 맵: (roomId -> (date -> 예약수량))
    private Map<Long, Map<LocalDate, Integer>> buildBookedQtyMap(
            Long companyId,
            Long accommodationId,
            LocalDate start,
            LocalDate end
    ) {
        // 회사(+선택 숙소) 범위의 겹치는 예약들 (CANCELLED 제외는 쿼리에서 이미 처리됨)
        List<Reservation> rs = reservationRepository.findOverlappedReservationsForMonth(
                companyId, accommodationId, start, end);

        Map<Long, Map<LocalDate, Integer>> booked = new HashMap<>();

        for (Reservation r : rs) {
            // 엔티티에 quantity가 없으므로 "예약 1건 = 객실 1개"로 처리
            final int qty = 1;

            LocalDate in  = r.getCheckInDate();
            LocalDate out = r.getCheckOutDate(); // 체크아웃 당일은 점유하지 않음 → out 전날까지 포함

            for (LocalDate d = in; d.isBefore(out); d = d.plusDays(1)) {
                if (d.isBefore(start) || d.isAfter(end)) continue; // 요청 범위 밖 스킵

                booked
                        .computeIfAbsent(r.getRoom().getRoomId(), k -> new HashMap<>())
                        .merge(d, qty, Integer::sum);
            }
        }
        return booked;

    }




    // 가격 캘린더 맵
    private Map<Long, Map<LocalDate, PriceCalendar>> buildCalendarMapForRooms(
            List<Room> rooms, LocalDate start, LocalDate end) {

        Map<Long, Map<LocalDate, PriceCalendar>> byRoom = new HashMap<>();
        for (Room r : rooms) {
            List<PriceCalendar> rows = priceCalendarRepository
                    .findByRoom_RoomIdAndDateBetweenOrderByDateAsc(r.getRoomId(), start, end);
            Map<LocalDate, PriceCalendar> perDate = rows.stream()
                    .collect(Collectors.toMap(PriceCalendar::getDate, Function.identity(), (a, b) -> a));
            byRoom.put(r.getRoomId(), perDate);
        }
        return byRoom;
    }

    // ================================헬퍼임 ===================
    // 회사(+선택 숙소)의 체크인 기준 일별 매출 맵
// 결과: roomId -> (date -> 합계 매출)
    private Map<Long, Map<LocalDate, BigDecimal>> buildCheckinRevenuePerDay(
            Long companyId,
            Long accommodationId,
            LocalDate start,
            LocalDate end
    ) {
        // DB에서 체크인 날짜 기준으로 예약 조회
        List<Reservation> reservations = reservationRepository.findCheckinsForRange(
                companyId, accommodationId, null, start, end);

        Map<Long, Map<LocalDate, BigDecimal>> revenueMap = new HashMap<>();

        for (Reservation r : reservations) {
            LocalDate checkinDate = r.getCheckInDate();
            if (checkinDate == null) continue;

            BigDecimal amount = r.getPrice() != null ? r.getPrice() : BigDecimal.ZERO;
            Long roomId = r.getRoom().getRoomId();

            revenueMap
                    .computeIfAbsent(roomId, k -> new HashMap<>())
                    .merge(checkinDate, amount, BigDecimal::add);
        }

        return revenueMap;
    }

    // 한숙소의 모든 객실을 포함에 월 단위 표로 그리고 싶을 때
    @Override
    public PriceCalendarDTO getMonth(Long companyId, Long accommodationId, YearMonth ym, Integer minAvailable) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        // 이 숙소(or 회사)의 객실들 불러오기
        List<Room> rooms = roomRepository.findRoomsOfCompany(companyId, accommodationId, Pageable.unpaged())
                .getContent();


        // 재고(기간 겹침 기준)
        Map<Long, Map<LocalDate, Integer>> bookedMap = buildBookedQtyMap(companyId, accommodationId, start, end);

        // 가격(체크인일 기준)
        Map<Long, Map<LocalDate, BigDecimal>> revenueMap = buildCheckinRevenuePerDay(companyId, accommodationId, start, end);

        List<RoomCalendarDTO> roomCalendars = new ArrayList<>();
        for (Room r : rooms) {
            Map<LocalDate, Integer> bookedPerDate = bookedMap.getOrDefault(r.getRoomId(), Collections.emptyMap());
            Map<LocalDate, BigDecimal> revPerDate = revenueMap.getOrDefault(r.getRoomId(), Collections.emptyMap());

            List<CalendarCellDTO> days = new ArrayList<>(ym.lengthOfMonth());
            for (int i = 1; i <= ym.lengthOfMonth(); i++) {
                LocalDate d = ym.atDay(i);

                int total = Optional.ofNullable(r.getTotalRooms()).orElse(0);
                int booked = bookedPerDate.getOrDefault(d, 0);
                int available = Math.max(0, total - booked);

                if (minAvailable != null && available < minAvailable) {
                    days.add(CalendarCellDTO.builder().date(d).price(null).available(null).build());
                    continue;
                }

                // 체크인 있는 날만 금액 표시(합계). 없으면 null
                BigDecimal priceToShow = revPerDate.get(d); // 없으면 null
                days.add(CalendarCellDTO.builder()
                        .date(d)
                        .price(priceToShow)
                        .available(available)
                        .build());
            }

            RoomHeaderDTO header = RoomHeaderDTO.builder()
                    .roomId(r.getRoomId())
                    .roomName(r.getName())
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
                .startDate(start)
                .endDate(end)
                .build();
    }

    // 단건 여러건 한번에 저장
    @Override
    @Transactional
    public boolean upsert(Long roomId, LocalDate date, BigDecimal price, Integer available) {
        int updated = priceCalendarRepository.updateValues(roomId, date, price, available);
        if (updated > 0) return false; // updated
        priceCalendarRepository.upsert(roomId, date, price, available);
        return true; // created
    }

    @Override
    @Transactional
    public void bulkUpsert(List<BulkItem> items) {
        for (BulkItem it : items) {
            priceCalendarRepository.upsert(it.getRoomId(), it.getDate(), it.getPrice(), it.getAvailable());
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
    public Map<LocalDate, PriceCalendarDTO> getCalendarData(Long roomId,
                                                            LocalDate startDate,
                                                            LocalDate endDate /* inclusive */) {
        Map<LocalDate, PriceCalendarDTO> calendarMap = new LinkedHashMap<>();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Room ID: " + roomId));

        // 레포에는 개구간 끝점(다음날 00:00 개념)으로 전달
        LocalDate endExclusive = endDate.plusDays(1);

        List<Reservation> reservations =
                reservationRepository.findByRoomIdAndDateBetween(roomId, startDate, endExclusive);

        // (선택) price_calendar가 있으면 우선 적용
        Map<LocalDate, BigDecimal> priceByDate = priceCalendarRepository
                .findByRoom_RoomIdAndDateBetweenOrderByDateAsc(roomId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(PriceCalendar::getDate, PriceCalendar::getPrice, (a,b)->a));

        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            // 하루 셀 경계 [d, d+1)로 비교 (off-by-one 방지)
            final LocalDate cellStart = d;
            final LocalDate cellEnd   = d.plusDays(1);

            // 예약 1건 = 1객실 (수량 컬럼 없으므로)
            int booked = (int) reservations.stream()
                    .filter(r -> r.getCheckInDate().isBefore(cellEnd)   // in < d+1
                                 && r.getCheckOutDate().isAfter(cellStart) // out > d
                    )
                    .count();

            int total     = Optional.ofNullable(room.getTotalRooms()).orElse(0);
            int available = Math.max(0, total - booked);
            BigDecimal price = priceByDate.getOrDefault(d, room.getPrice());

            calendarMap.put(d, PriceCalendarDTO.builder()
                    .roomId(room.getRoomId())
                    .date(d)
                    .price(price)
                    .availableRoom(available)
                    .build());
        }
        return calendarMap;
    }

    // 이번달 총 객실수 / 예약 가능 객실 수 / 최저가/ 최고가
    public List<DailyOverviewDTO> getDailyOverview(Long companyId,
                                                   Long accommodationId,
                                                   LocalDate start,
                                                   LocalDate end) {
        return priceCalendarRepository.findDailyOverview(companyId, accommodationId,
                        java.sql.Date.valueOf(start),
                        java.sql.Date.valueOf(end))
                .stream()
                .map(p -> DailyOverviewDTO.builder()
                        .date(p.getDate().toLocalDate())
                        .totalRoomsSum(p.getTotalRoomsSum())
                        .availableSum(p.getAvailableSum())
                        .minPrice(p.getMinPrice())
                        .maxPrice(p.getMaxPrice())
                        .build())
                .toList();
    }

   //  숙소별 × 날짜별 매트릭스 화면 (엑셀 같은 형태) 그릴 때.

    @Override
    public List<GridCellDTO> getGridCells(Long companyId,
                                          Long accommodationId,
                                          LocalDate start,
                                          LocalDate end) {
        return priceCalendarRepository.findGridCells(companyId, accommodationId,
                        java.sql.Date.valueOf(start),
                        java.sql.Date.valueOf(end))
                .stream()
                .map(p -> GridCellDTO.builder()
                        .roomId(p.getRoomId())
                        .roomName(p.getRoomName())
                        .date(p.getDate().toLocalDate())
                        .price(p.getPrice())
                        .available(p.getAvailable())
                        .totalRooms(p.getTotalRooms())
                        .build())
                .toList();
    }

    //체크인 매출 기준
    @Override
    public Map<LocalDate, BigDecimal> getDailyRevenueByCheckin(
            Long companyId, Long accommodationId, LocalDate start, LocalDate end) {

        // 초기화: 요청 구간의 모든 날짜 키를 0으로 세팅(원하면 생략 가능)
        Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            result.put(d, BigDecimal.ZERO);
        }

        // 체크인 기준 매출: 해당 기간의 체크인들만 조회
        List<Reservation> rows = reservationRepository.findCheckinsForRange(
                companyId, accommodationId, null, start, end); // room 필터 없으면 null

        for (Reservation r : rows) {
            LocalDate checkin = r.getCheckInDate();
            if (checkin == null || checkin.isBefore(start) || checkin.isAfter(end)) continue;

            BigDecimal price = Optional.ofNullable(r.getPrice()).orElse(BigDecimal.ZERO);
            result.merge(checkin, price, BigDecimal::add);
        }
        return result;
    }

}