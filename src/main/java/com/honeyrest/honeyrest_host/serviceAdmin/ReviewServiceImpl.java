package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.domain.type.ReviewStatus;

import com.honeyrest.honeyrest_host.dtoAdmin.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.ReviewDTO;

import com.honeyrest.domain.entity.Reservation;
import com.honeyrest.domain.entity.Review;
import com.honeyrest.domain.entity.User;
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
import java.util.Locale;
import java.util.Optional;

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

    /**
     * 목록 상태 필터 값 (공유 ReviewStatus 로 정규화: 대문자, 과거 VISIBLE → PUBLISHED).
     * V12 이후 저장 값은 PUBLISHED/HIDDEN/DELETED 뿐이라 노출 필터는 PUBLISHED 하나로 조회한다.
     * 허용되지 않은 값(옛 북마크의 PENDING/REPORTED 등)은 오류 대신 그대로 조회해 빈 목록이 된다.
     */
    private static String filterStatus(String status) {
        try {
            return ReviewStatus.normalize(status);
        } catch (IllegalArgumentException e) {
            return status.trim().toUpperCase(Locale.ROOT);
        }
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
                page = reviewRepository.findByRoomIdAndStatus(roomId, filterStatus(status), pageable); // 특정 상태만
            }
        } else if (accommodationId != null) {
            if (all) {
                page = reviewRepository.findByAccommodationId(accommodationId, pageable);
            } else {
                page = reviewRepository.findByAccommodationIdAndStatus(accommodationId, filterStatus(status), pageable);
            }
        } else {
            if (all) {
                page = reviewRepository.findAll(pageable);                               // ★ 전체 상태
                // (기존처럼 HIDDEN 빼고 보이고 싶다면: findByStatusNot("HIDDEN", pageable))
            } else {
                page = reviewRepository.findByStatus(filterStatus(status), pageable);                  // 특정 상태만
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


    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ReviewDTO> getListForCompany(List<Long> accommodationIds,
                                                        String status,
                                                        Long roomId,
                                                        Long accommodationId,
                                                        String sort,
                                                        PageRequestDTO pageRequestDTO) {
        if (accommodationIds == null || accommodationIds.isEmpty()) {
            return PageResponseDTO.<ReviewDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(List.of())
                    .total(0)
                    .build();
        }

        Pageable pageable = pageRequestDTO.getPageable(toSort(sort));
        boolean all = (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status));
        Page<Review> page;

        if (roomId != null) {
            if (all) {
                page = reviewRepository.findByRoomId(roomId, pageable);
            } else {
                page = reviewRepository.findByRoomIdAndStatus(roomId, filterStatus(status), pageable);
            }
        } else if (accommodationId != null) {
            if (all) {
                page = reviewRepository.findByAccommodationId(accommodationId, pageable);
            } else {
                page = reviewRepository.findByAccommodationIdAndStatus(accommodationId, filterStatus(status), pageable);
            }
        } else {
            if (all) {
                page = reviewRepository.findByAccommodationIdIn(accommodationIds, pageable);
            } else {
                page = reviewRepository.findByAccommodationIdInAndStatus(accommodationIds, filterStatus(status), pageable);
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
            dto.setStatus(ReviewStatus.PUBLISHED);
        } else {
            dto.setStatus(ReviewStatus.normalize(dto.getStatus())); // PUBLISHED/HIDDEN/DELETED 만 허용
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

    /*
     * -------------------- 수정 공통 --------------------
     * 과거에는 Review.builder() 로 새 엔티티를 만들어 save(merge) 했다. 빌더에 빠진 필드는 null 로 덮어써지고
     * 영속성 컨텍스트의 createdAt 도 null 이 됐다. 이제 관리 중인 엔티티를 도메인 메서드
     * (updateContent / reply·deleteReply / changeStatus / setLikeCount)로 바꾸고 dirty checking 으로 저장한다.
     */

    /** DTO 의 null 이 아닌 값만 관리 엔티티에 반영한다. */
    private void applyChanges(Review review, ReviewDTO dto) {
        rejectIdentityChange(review, dto);

        // 평점·본문: 하나라도 들어오면 나머지는 기존 값으로 채워 한 번에 바꾼다
        if (dto.getRating() != null || dto.getCleanlinessRating() != null || dto.getServiceRating() != null
                || dto.getFacilitiesRating() != null || dto.getLocationRating() != null || dto.getContent() != null) {
            review.updateContent(
                    dto.getRating() != null ? dto.getRating() : review.getRating(),
                    dto.getCleanlinessRating() != null ? dto.getCleanlinessRating() : review.getCleanlinessRating(),
                    dto.getServiceRating() != null ? dto.getServiceRating() : review.getServiceRating(),
                    dto.getFacilitiesRating() != null ? dto.getFacilitiesRating() : review.getFacilitiesRating(),
                    dto.getLocationRating() != null ? dto.getLocationRating() : review.getLocationRating(),
                    dto.getContent() != null ? dto.getContent() : review.getContent());
        }

        // 답변: null 이면 그대로, 빈 값(답변란을 비우고 저장)이면 삭제, 그 외에는 등록/수정
        if (dto.getReply() != null) {
            if (dto.getReply().isBlank()) {
                review.deleteReply();
            } else {
                review.reply(dto.getReply());
            }
        }

        if (dto.getLikeCount() != null) {
            review.setLikeCount(dto.getLikeCount());
        }

        // 상태: 비어 있으면 그대로. 허용 값은 공유 ReviewStatus.ALL(PUBLISHED/HIDDEN/DELETED), VISIBLE 은 PUBLISHED 로 저장
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            review.changeStatus(dto.getStatus());
        }
    }

    /**
     * 리뷰의 예약·작성자·숙소·객실은 예약에서 정해지는 값이라 수정 대상이 아니다(도메인에도 변경 메서드가 없다).
     * 같은 값이 다시 들어오는 것은 허용하고, 다른 값이면 조용히 무시하지 않고 거부한다.
     */
    private static void rejectIdentityChange(Review review, ReviewDTO dto) {
        boolean changed =
                (dto.getReservationId() != null && review.getReservation() != null
                        && !dto.getReservationId().equals(review.getReservation().getReservationId()))
                || (dto.getUserId() != null && review.getUser() != null
                        && !dto.getUserId().equals(review.getUser().getUserId()))
                || (dto.getAccommodationId() != null && !dto.getAccommodationId().equals(review.getAccommodationId()))
                || (dto.getRoomId() != null && !dto.getRoomId().equals(review.getRoomId()));
        if (changed) {
            throw new IllegalArgumentException("리뷰의 예약·작성자·숙소·객실은 변경할 수 없습니다.");
        }
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));
    }

    /* -------------------- 전체 수정(put 느낌) -------------------- */

    @Override
    @Transactional
    public ReviewDTO update(Long reviewId, ReviewDTO dto) {
        Review review = findReview(reviewId);

        // 검증 & 정규화
        validateRatings(dto);
        normalizeScales(dto);

        // 필수값이 비어있다면 예외(프로젝트 정책에 맞추세요)
        if (dto.getRating() == null) throw new IllegalArgumentException("rating 은 필수입니다.");

        applyChanges(review, dto);
        return modelMapper.map(review, ReviewDTO.class);
    }

    /* -------------------- 부분 수정(patch 느낌) -------------------- */

    @Override
    @Transactional
    public ReviewDTO patch(Long reviewId, ReviewDTO dto) {
        Review review = findReview(reviewId);

        validateRatings(dto);
        normalizeScales(dto);

        applyChanges(review, dto);
        return modelMapper.map(review, ReviewDTO.class);
    }

    // 리뷰 삭제 (소프트 삭제 → HIDDEN)
    @Override
    @Transactional
    public void delete(Long reviewId) {
        log.info("[delete] soft hide try, id={}", reviewId);
        findReview(reviewId).changeStatus(ReviewStatus.HIDDEN);
    }

    @Transactional
    @Override
    public void changeStatus(Long reviewId, String status) {
        // 허용 값은 공유 ReviewStatus.ALL (PUBLISHED/HIDDEN/DELETED). VISIBLE 은 PUBLISHED 로 저장되고,
        // 그 밖의 값(과거 화면의 PENDING/REPORTED 등)은 IllegalArgumentException.
        Review review = findReview(reviewId);
        review.changeStatus(status);
    }

    @Transactional
    @Override
    public ReviewDTO toggleVisibleHidden(Long reviewId) {
        Review r = findReview(reviewId);

        // 규칙: 노출(PUBLISHED, 과거 VISIBLE)이면 HIDDEN 으로, 그 외(HIDDEN/DELETED/기타)는 PUBLISHED 로.
        String after = ReviewStatus.isPublished(r.getStatus()) ? ReviewStatus.HIDDEN : ReviewStatus.PUBLISHED;
        r.changeStatus(after);
        return modelMapper.map(r, ReviewDTO.class);
    }
}
