package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.PageRequestDTO;
import com.honeyrest.honeyrest_host.dto.PageResponseDTO;
import com.honeyrest.honeyrest_host.dto.ReviewDTO;

import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.entity.enums.ReviewStatus;
import com.honeyrest.honeyrest_host.repository.ReviewRepository;
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
import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    // ====== 헬퍼: 들어온 평점들만 검증(1~5) ======
    private void validateRatings(ReviewDTO dto) {
        java.util.function.Consumer<BigDecimal> check = v -> {
            if (v.compareTo(BigDecimal.ONE) < 0 || v.compareTo(BigDecimal.valueOf(5)) > 0) {
                throw new IllegalArgumentException("평점은 1~5 사이여야 합니다.");
            }
        };

        if (dto.getRating() != null)            check.accept(dto.getRating());
        if (dto.getCleanlinessRating() != null) check.accept(dto.getCleanlinessRating());
        if (dto.getServiceRating() != null)     check.accept(dto.getServiceRating());
        if (dto.getLocationRating() != null)    check.accept(dto.getLocationRating());
    }


    @Override
    public PageResponseDTO<ReviewDTO> getList(String status, Long roomId, Long accommodationId, String sort, PageRequestDTO pageRequestDTO) {
        // 1) 정렬 구성
        Sort sortSpec = switch (sort == null ? "" : sort) {
            case "ratingDesc" -> Sort.by(Sort.Direction.DESC, "rating");
            case "ratingAsc" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "reviewId"); // 최신순
        };
        Pageable pageable = pageRequestDTO.getPageable(sortSpec);

        // 2) 상태 파싱(옵션)
        ReviewStatus st = null;
        if (status != null && !status.isBlank()) {
            st = ReviewStatus.valueOf(status.toUpperCase()); // 잘못된 값이면 IllegalArgumentException
        }

        // 3) 조건 조합에 따라 Repository 호출 분기
        Page<Review> page;
        if (roomId != null) {
            page = (st != null)
                    ? reviewRepository.findByRoomIdAndStatus(roomId, st, pageable)
                    : reviewRepository.findByRoomId(roomId, pageable);
        } else if (accommodationId != null) {
            page = (st != null)
                    ? reviewRepository.findByAccommodationIdAndStatus(accommodationId, st, pageable)
                    : reviewRepository.findByAccommodationId(accommodationId, pageable);
        } else {
            // 조건 없음: 전체 (상태 필터 있으면 여기서 처리하는 전용 메서드를 추가해도 됨)
            page = reviewRepository.findAll(pageable);
        }

        // 4) 엔티티 → DTO 변환
        List<ReviewDTO> list = page.getContent().stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .toList();

        // 5) 표준 PageResponseDTO 조립
        return PageResponseDTO.<ReviewDTO>withALl()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(list)
                .total((int) page.getTotalElements())
                .build();
    }

    // 예약 리뷰 단건 조회
    @Override
    public ReviewDTO getOne(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException(" id=" + reviewId));
        return modelMapper.map(review, ReviewDTO.class);
    }

    // 리뷰 등록
    @Override
    public ReviewDTO insert(ReviewDTO reviewDTO) {
        // 필요한 유효성 체크(별점 1~5 등)
        BigDecimal rating = reviewDTO.getRating();
        if (rating == null
                || rating.compareTo(BigDecimal.ONE) < 0  // 1보다 작은 경우
                || rating.compareTo(BigDecimal.valueOf(5)) > 0) { // 5보다 큰 경우
            throw new IllegalArgumentException("평점은 1~5 사이여야 합니다.");
        }
        // 선택: 소수 1자리로 정규화(원하면)
        reviewDTO.setRating(rating.setScale(1, java.math.RoundingMode.HALF_UP));

        // 선택: 서브 평점들도 있다면 동일하게 검증
        if (reviewDTO.getCleanlinessRating() != null &&
                (reviewDTO.getCleanlinessRating().compareTo(BigDecimal.ONE) < 0 ||
                        reviewDTO.getCleanlinessRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new IllegalArgumentException("청결 평점은 1~5 사이여야 합니다.");
        }
        if (reviewDTO.getServiceRating() != null &&
                (reviewDTO.getServiceRating().compareTo(BigDecimal.ONE) < 0 ||
                        reviewDTO.getServiceRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new IllegalArgumentException("서비스 평점은 1~5 사이여야 합니다.");
        }
        if (reviewDTO.getLocationRating() != null &&
                (reviewDTO.getLocationRating().compareTo(BigDecimal.ONE) < 0 ||
                        reviewDTO.getLocationRating().compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new IllegalArgumentException("위치 평점은 1~5 사이여야 합니다.");
        }

        // 2) (선택) 기본 상태값 주기 — DTO가 null 이면 PUBLISHED/PENDING 등 프로젝트 규칙대로
        if (reviewDTO.getStatus() == null) {
            reviewDTO.setStatus(ReviewStatus.VISIBLE); // 이넘을 가지고 옴
        }

        // 3) (중요) 연관관계가 엔티티(User/Room/Reservation)라면, id만 있는 DTO를 그대로 map하면
        //    영속 참조가 안 걸릴 수 있어요. 필요시 getReference/레포로 조회해서 세팅하세요.
        //    예시:
        //    var user = entityManager.getReference(User.class, reviewDTO.getUserId());
        //    var room = entityManager.getReference(Room.class, reviewDTO.getRoomId());
        //    Review entity = Review.of(reviewDTO, user, room); // 팩토리/생성자 사용 권장

        // 지금은 단순 매핑만:
        Review entity = modelMapper.map(reviewDTO, Review.class);

        // 4) 저장
        Review saved = reviewRepository.save(entity);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    // 리뷰 수정
    @Override
    public ReviewDTO update(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        ReviewDTO patch = ReviewDTO.builder()
                .rating(reviewDTO.getRating())
                .cleanlinessRating(reviewDTO.getCleanlinessRating())
                .serviceRating(reviewDTO.getServiceRating())
                .locationRating(reviewDTO.getLocationRating())
                .content(reviewDTO.getContent())
                .reply(reviewDTO.getReply())
                .status(reviewDTO.getStatus())
                .build();

        // dto -> 엔티티
        modelMapper.map(review, patch);

        return modelMapper.map(patch, ReviewDTO.class);
    }


    // 리뷰 삭제
    @Override
    public void delete(Long reviewId) {
        int updated = reviewRepository.softHide(reviewId);
        if (updated == 0) {
            // softHide 메서드를 쓰지 않고 진짜 삭제하려면 아래 한 줄 사용
            // reviewRepository.deleteById(reviewId);
            throw new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId);
        }
    }

    @Override
    public ReviewDTO patch(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        validateRatings(reviewDTO);

        // 선택: 들어온 별점만 반올림
        if (reviewDTO.getRating() != null)
            reviewDTO.setRating(reviewDTO.getRating().setScale(1, java.math.RoundingMode.HALF_UP));
        if (reviewDTO.getCleanlinessRating() != null)
            reviewDTO.setCleanlinessRating(reviewDTO.getCleanlinessRating().setScale(1, java.math.RoundingMode.HALF_UP));
        if (reviewDTO.getServiceRating() != null)
            reviewDTO.setServiceRating(reviewDTO.getServiceRating().setScale(1, java.math.RoundingMode.HALF_UP));
        if (reviewDTO.getLocationRating() != null)
            reviewDTO.setLocationRating(reviewDTO.getLocationRating().setScale(1, java.math.RoundingMode.HALF_UP));

        // 핵심: 세터 없이 필드 매핑 + null 무시는 Config로 처리됨
        modelMapper.map(reviewDTO, review);

        Review saved = reviewRepository.save(review);
        return modelMapper.map(saved, ReviewDTO.class);
    }
}
