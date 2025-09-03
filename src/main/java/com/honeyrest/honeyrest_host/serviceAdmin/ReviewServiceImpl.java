package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReviewDTO;

import com.honeyrest.honeyrest_host.entity.Reservation;
import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReviewImageRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReviewRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final EntityManager em;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;

    // ====== 헬퍼: 들어온 평점들만 검증(1~5) ======
    private void validateRatings(ReviewDTO dto) {
        var check = (java.util.function.Consumer<BigDecimal>) v -> {
            if (v.compareTo(BigDecimal.ZERO) < 0 || v.compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new IllegalArgumentException("평점은 0~5 사이여야 합니다.");
            }
        };
        if (dto.getRating() != null) check.accept(dto.getRating());
        if (dto.getCleanlinessRating() != null) check.accept(dto.getCleanlinessRating());
        if (dto.getServiceRating() != null) check.accept(dto.getServiceRating());
        if (dto.getFacilitiesRating() != null) check.accept(dto.getFacilitiesRating()); // 빠져있었음
        if (dto.getLocationRating() != null) check.accept(dto.getLocationRating());
    }

    /**
     * 반올림(소수 1자리) – 값이 존재하는 것만
     */
    private void normalizeScales(ReviewDTO dto) {
        java.util.function.Function<BigDecimal, BigDecimal> round1 =
                v -> v == null ? null : v.setScale(1, RoundingMode.HALF_UP);
        dto.setRating(round1.apply(dto.getRating()));
        dto.setCleanlinessRating(round1.apply(dto.getCleanlinessRating()));
        dto.setServiceRating(round1.apply(dto.getServiceRating()));
        dto.setFacilitiesRating(round1.apply(dto.getFacilitiesRating()));
        dto.setLocationRating(round1.apply(dto.getLocationRating()));
    }

    /* 반올림(소수 1자리) - 값 존재하는 것 */
    private Sort toSort(String sort) {
        if (sort == null) sort = "";
        return switch (sort) {
            case "ratingDesc" -> Sort.by(Sort.Direction.DESC, "rating");
            case "ratingAsc" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "reviewId"); // 최신순
        };
    }

    /* 상태 normalize 및 허용값 체크 */
    private String normalizeStatusOrNull(String status) {
        if (status == null || status.isBlank()) {
            return "VISIBLE"; // 기본 조회는 노출된 것만
        }
        String st = status.toUpperCase();
        List<String> allowed = List.of("VISIBLE", "HIDDEN", "DELETED");
        if (!allowed.contains(st)) throw new IllegalArgumentException("유효하지 않은 리뷰 상태: " + status);
        return st;
    }

    private ReviewDTO mapBase(Review r) {
        return ReviewDTO.builder()
                .reviewId(r.getReviewId())
                .reservationId(r.getReservation() != null ? r.getReservation().getReservationId() : null)
                .userId(r.getUser() != null ? r.getUser().getUserId() : null)
                .accommodationId(r.getAccommodationId())
                .roomId(r.getRoomId())
                .rating(r.getRating())
                .cleanlinessRating(r.getCleanlinessRating())
                .serviceRating(r.getServiceRating())
                .facilitiesRating(r.getFacilitiesRating())
                .locationRating(r.getLocationRating())
                .content(r.getContent())
                .reply(r.getReply())
                .likeCount(r.getLikeCount() == null ? 0 : r.getLikeCount())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private void fillNames(ReviewDTO dto) {
        if (dto.getAccommodationId() != null) {
            dto.setAccommodationName(accommodationRepository.findNameById(dto.getAccommodationId()).orElse(null));
        }
        if (dto.getRoomId() != null) {
            dto.setRoomName(roomRepository.findNameById(dto.getRoomId()).orElse(null));
        }
        if (dto.getUserId() != null) {
            dto.setUserName(userRepository.findNameById(dto.getUserId()).orElse(null));
        }

    }

    private void fillImages(ReviewDTO dto) {
        dto.setImageList(reviewImageRepository.findDtosByReviewId(dto.getReviewId()));
    }

    /* ====================조회 ================= */

    // 인터페이스에 둘 중 하나만 노출 (getOne 추천)
    @Transactional(readOnly = true)
    @Override
    public Optional<ReviewDTO> getOne(Long reviewId) {
        return reviewRepository.findById(reviewId).map(r -> {
            ReviewDTO dto = mapBase(r);

            // 빠른 이름 세팅(예약/유저에서 바로)
            if (r.getReservation() != null) {
                dto.setAccommodationName(r.getReservation().getAccommodationName());
                dto.setRoomName(r.getReservation().getRoomName());
            }
            if (r.getUser() != null) dto.setUserName(r.getUser().getName());

            // FK만 있을 때 보강 조회
            fillNames(dto);
            // 이미지 묶음
            fillImages(dto);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ReviewDTO> getList(String status,
                                              Long roomId,
                                              Long accommodationId,
                                              String sort,
                                              PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(toSort(sort));
        boolean all = (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status));
        Page<Review> page;

        if (roomId != null) {
            if (all) {
                page = reviewRepository.findByRoomId(roomId, pageable);                 // 모든 상태
            } else {
                page = reviewRepository.findByRoomIdAndStatus(roomId, status, pageable); // 특정 상태만
            }
        } else if (accommodationId != null) {
            if (all) {
                page = reviewRepository.findByAccommodationId(accommodationId, pageable);
            } else {
                page = reviewRepository.findByAccommodationIdAndStatus(accommodationId, status, pageable);
            }
        } else {
            if (all) {
                page = reviewRepository.findAll(pageable);                               // ★ 전체 상태
                // (기존처럼 HIDDEN 빼고 보이고 싶다면: findByStatusNot("HIDDEN", pageable))
            } else {
                page = reviewRepository.findByStatus(status, pageable);                  // 특정 상태만
            }
        }

        List<ReviewDTO> list = page.getContent().stream()
                .map(r -> {
                    ReviewDTO dto = modelMapper.map(r, ReviewDTO.class);
                    if (r.getReservation() != null) {
                        dto.setAccommodationName(r.getReservation().getAccommodationName());
                        dto.setRoomName(r.getReservation().getRoomName());
                    }
                    if (r.getUser() != null) {
                        dto.setUserName(r.getUser().getName());
                    }
                    return dto;
                })
                .toList();

        return PageResponseDTO.<ReviewDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(list)
                .total((int) page.getTotalElements())
                .build();
    }


    // 리뷰 등록
    @Override
    @Transactional
    public ReviewDTO insert(ReviewDTO dto) {
        // 기본 검증 및 정규화
        validateRatings(dto);
        normalizeScales(dto);

        // 상태 기본값
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("VISIBLE");
        }

        // 연관관계 영속 참조 (필드가 not null 제약이라면 반드시 세팅)
        if (dto.getReservationId() == null || dto.getUserId() == null) {
            throw new IllegalArgumentException("reservationId, userId 는 필수입니다.");
        }
        // 예약을 참조
        Reservation reservationRef = em.getReference(Reservation.class, dto.getReservationId());

        // 예약에서 유저/숙소/객실 자동으로 세팅
        User userRef = reservationRef.getUser();

        // 엔티티 조립
        Review entity = Review.builder()
                .reservation(reservationRef)
                .user(userRef)
                .accommodationId(reservationRef.getAccommodation().getAccommodationId())
                .roomId(reservationRef.getRoom().getRoomId())
                .rating(dto.getRating())
                .cleanlinessRating(dto.getCleanlinessRating())
                .serviceRating(dto.getServiceRating())
                .facilitiesRating(dto.getFacilitiesRating())
                .locationRating(dto.getLocationRating())
                .content(dto.getContent())
                .reply(dto.getReply())
                .likeCount(dto.getLikeCount() == null ? 0 : dto.getLikeCount())
                .status(dto.getStatus())
                .build();

        Review saved = reviewRepository.save(entity);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    /* -------------------- 전체 수정(put 느낌) -------------------- */

    @Override
    @Transactional
    public ReviewDTO update(Long reviewId, ReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        // 검증 & 정규화
        validateRatings(dto);
        normalizeScales(dto);

        // 필수값이 비어있다면 예외(프로젝트 정책에 맞추세요)
        if (dto.getRating() == null) throw new IllegalArgumentException("rating 은 필수입니다.");

        // 연관관계 바꾸는 업데이트가 필요하다면 여기도 처리(옵션)
        if (dto.getReservationId() != null && !dto.getReservationId().equals(review.getReservation().getReservationId())) {
            review = Review.builder()
                    .reviewId(review.getReviewId())
                    .reservation(em.getReference(Reservation.class, dto.getReservationId()))
                    .user(review.getUser())
                    .accommodationId(review.getAccommodationId())
                    .roomId(review.getRoomId())
                    .rating(review.getRating())
                    .cleanlinessRating(review.getCleanlinessRating())
                    .serviceRating(review.getServiceRating())
                    .facilitiesRating(review.getFacilitiesRating())
                    .locationRating(review.getLocationRating())
                    .content(review.getContent())
                    .reply(review.getReply())
                    .likeCount(review.getLikeCount())
                    .status(review.getStatus())
                    .build();
        }
        if (dto.getUserId() != null && !dto.getUserId().equals(review.getUser().getUserId())) {
            // 위에서 새로 빌더로 만들지 않고, 단순히 교체만 하고 싶다면
            User userRef = em.getReference(User.class, dto.getUserId());
            // JPA Immutable 패턴이 아니라면 세터가 필요하지만, 현재 엔티티가 세터가 없으므로
            // 연관관계 변경을 자주 하지 않는다는 가정하에 위처럼 새 빌더로 만드는 방식을 권장.
            // 필요 시 엔티티에 변경 메서드를 추가하세요.
        }

        // 스칼라 필드 업데이트 (null 은 무시)
        Review updated = Review.builder()
                .reviewId(review.getReviewId())
                .reservation(review.getReservation())
                .user(review.getUser())
                .accommodationId(dto.getAccommodationId() != null ? dto.getAccommodationId() : review.getAccommodationId())
                .roomId(dto.getRoomId() != null ? dto.getRoomId() : review.getRoomId())
                .rating(dto.getRating() != null ? dto.getRating() : review.getRating())
                .cleanlinessRating(dto.getCleanlinessRating() != null ? dto.getCleanlinessRating() : review.getCleanlinessRating())
                .serviceRating(dto.getServiceRating() != null ? dto.getServiceRating() : review.getServiceRating())
                .facilitiesRating(dto.getFacilitiesRating() != null ? dto.getFacilitiesRating() : review.getFacilitiesRating())
                .locationRating(dto.getLocationRating() != null ? dto.getLocationRating() : review.getLocationRating())
                .content(dto.getContent() != null ? dto.getContent() : review.getContent())
                .reply(dto.getReply() != null ? dto.getReply() : review.getReply())
                .likeCount(dto.getLikeCount() != null ? dto.getLikeCount() : review.getLikeCount())
                .status(dto.getStatus() != null ? dto.getStatus() : review.getStatus())
                .build();

        Review saved = reviewRepository.save(updated);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    /* -------------------- 부분 수정(patch 느낌) -------------------- */

    @Override
    @Transactional
    public ReviewDTO patch(Long reviewId, ReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        validateRatings(dto);
        normalizeScales(dto);

        Review patched = Review.builder()
                .reviewId(review.getReviewId())
                .reservation(review.getReservation())
                .user(review.getUser())
                .accommodationId(dto.getAccommodationId() != null ? dto.getAccommodationId() : review.getAccommodationId())
                .roomId(dto.getRoomId() != null ? dto.getRoomId() : review.getRoomId())
                .rating(dto.getRating() != null ? dto.getRating() : review.getRating())
                .cleanlinessRating(dto.getCleanlinessRating() != null ? dto.getCleanlinessRating() : review.getCleanlinessRating())
                .serviceRating(dto.getServiceRating() != null ? dto.getServiceRating() : review.getServiceRating())
                .facilitiesRating(dto.getFacilitiesRating() != null ? dto.getFacilitiesRating() : review.getFacilitiesRating())
                .locationRating(dto.getLocationRating() != null ? dto.getLocationRating() : review.getLocationRating())
                .content(dto.getContent() != null ? dto.getContent() : review.getContent())
                .reply(dto.getReply() != null ? dto.getReply() : review.getReply())
                .likeCount(dto.getLikeCount() != null ? dto.getLikeCount() : review.getLikeCount())
                .status(dto.getStatus() != null ? dto.getStatus() : review.getStatus())
                .build();

        Review saved = reviewRepository.save(patched);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    // 리뷰 삭제
    @Override
    @Transactional
    public void delete(Long reviewId) {
        log.info("[delete] soft hide try, id={}", reviewId);
        int updated = reviewRepository.softHide(reviewId);
        if (updated == 0) {
            throw new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId);
        }
    }
    @Transactional
    @Override
    public void changeStatus(Long reviewId, String status) {
        // 허용값만 통과
        Set<String> allowed = Set.of("VISIBLE", "HIDDEN", "PENDING", "REPORTED");
        if (!allowed.contains(status)) {
            throw new IllegalArgumentException("허용되지 않는 상태: " + status);
        }
        int updated = reviewRepository.updateStatus(reviewId, status);
        if (updated == 0) throw new EntityNotFoundException("리뷰 없음: " + reviewId);
    }
    @Transactional
    @Override
    public ReviewDTO toggleVisibleHidden(Long reviewId) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        String before = (r.getStatus() == null) ? "HIDDEN" : r.getStatus().toUpperCase();
        // 규칙: VISIBLE이면 HIDDEN으로, 그 외(HIDDEN/REPORTED/PENDING/기타)는 VISIBLE로
        String after  = "VISIBLE".equals(before) ? "HIDDEN" : "VISIBLE";

        Review patched = Review.builder()
                .reviewId(r.getReviewId())
                .reservation(r.getReservation())
                .user(r.getUser())
                .accommodationId(r.getAccommodationId())
                .roomId(r.getRoomId())
                .rating(r.getRating())
                .cleanlinessRating(r.getCleanlinessRating())
                .serviceRating(r.getServiceRating())
                .facilitiesRating(r.getFacilitiesRating())
                .locationRating(r.getLocationRating())
                .content(r.getContent())
                .reply(r.getReply())
                .likeCount(r.getLikeCount())
                .status(after)
                .build();

        Review saved = reviewRepository.save(patched);
        return modelMapper.map(saved, ReviewDTO.class);
    }
}
