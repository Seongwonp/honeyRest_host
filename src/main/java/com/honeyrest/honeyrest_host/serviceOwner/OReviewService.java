package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.domain.entity.Review;
import com.honeyrest.honeyrest_host.repositoryOwner.OReviewRepository;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OReviewService {
    private final OReviewRepository reviewRepository;
    private final OUserRepository userRepository;
    private final com.honeyrest.honeyrest_host.serviceAdmin.ReviewService adminReviewService;

    private ReviewDTO toDTO(Review r) {
        return ReviewDTO.builder()
                .reviewId(r.getReviewId())
                .reservationId(r.getReservation().getReservationId())
                .userId(r.getUser().getUserId())
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
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .accommodationName(r.getReservation().getAccommodationName())
                .roomName(r.getReservation().getRoomName())
                .userName(r.getUser().getName())
                .build();
    }

    // 과거의 toEntity/create(빌더로 Review 를 만들어 save) 는 호출하는 곳이 없고, 리뷰 작성은 사용자 API 의 몫이라 제거했다.
    // 업체 화면의 리뷰 변경(답변·상태·숨김)은 아래 saveReply/hide 가 관리자 서비스의 도메인 메서드 경로를 쓴다.

    public PageResponseDTO<ReviewDTO> getReviewsByAccommodationIdWithPageable(Long accommodationId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reviewId").descending());

        Page<Review> page;
        if (accommodationId != null && accommodationId > 0) {
            page = reviewRepository.findByAccommodationId(accommodationId, pageable);
        } else {
            page = reviewRepository.findAll(pageable);
        }

        List<ReviewDTO> list = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        long total = page.getTotalElements();

        return PageResponseDTO.<ReviewDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

    }

    public List<ReviewDTO> getReviewsByRoomId(Long roomId) {
        return reviewRepository.findByRoomId(roomId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PageResponseDTO<ReviewDTO> getReviewsWithPageable(Long roomId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reviewId").descending());

        Page<Review> page;

        if (roomId != null && roomId > 0) {
            page = reviewRepository.findByRoomId(roomId, pageable); // 쿼리 메서드 필요
        } else {
            page = reviewRepository.findAll(pageable);
        }

        List<ReviewDTO> list = page.getContent().stream().map(this::toDTO).toList();

        long total = page.getTotalElements();

        PageResponseDTO<ReviewDTO> responseDTO = PageResponseDTO.<ReviewDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    public ReviewDTO getReviewById(Long reviewId) {
        return toDTO(reviewRepository.findByReviewId(reviewId));
    }

    public PageResponseDTO<ReviewDTO> getReviewsByUserId(String name , String phone ,PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reviewId").descending());

        Page<Review> page = reviewRepository.findByUser_UserId(userRepository.findByNameAndPhone(name, phone).getUserId() ,pageable);

        List<ReviewDTO> list = page.getContent().stream().map(this::toDTO).toList();

        long total = page.getTotalElements();

        PageResponseDTO<ReviewDTO> responseDTO = PageResponseDTO.<ReviewDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;

    }
    public PageResponseDTO<ReviewDTO> getReviewsByRoomID(Long roomId, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by("reviewId").descending());

        Page<Review> page;

        if (roomId != null && roomId > 0) {
            page = reviewRepository.findByRoomId(roomId, pageable); // 쿼리 메서드 필요
        } else {
            page = reviewRepository.findAll(pageable);
        }

        List<ReviewDTO> list = page.getContent().stream().map(this::toDTO).toList();

        long total = page.getTotalElements();

        PageResponseDTO<ReviewDTO> responseDTO = PageResponseDTO.<ReviewDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    /**
     * 답글·노출 상태 저장. 과거에는 빌더로 만든 새 엔티티를 merge 해 작성일이 비고(같은 이유로 도메인에 updateContent 가 생김),
     * 컨트롤러 매핑도 /owner/owner/... 로 잘못돼 저장 자체가 불가능했다. 업체 관리자 화면과 같은 부분 수정 로직을 쓴다.
     */
    public void saveReply(Long reviewId, String reply, String status) {
        if (reviewRepository.findByReviewId(reviewId) == null) {
            throw new jakarta.persistence.EntityNotFoundException("리뷰를 찾을 수 없습니다. id=" + reviewId);
        }
        com.honeyrest.honeyrest_host.dtoAdmin.ReviewDTO patch = com.honeyrest.honeyrest_host.dtoAdmin.ReviewDTO.builder()
                .reply(reply == null ? "" : reply.trim())
                .status(status) // 비어 있으면 유지, 값은 Review.changeStatus 가 공유 ReviewStatus 로 검증·정규화
                .build();
        adminReviewService.patch(reviewId, patch);
    }

    /** 숨김(소프트 삭제) */
    public void hide(Long reviewId) {
        adminReviewService.delete(reviewId);
    }

    /** 숙소·객실 조건으로 리뷰 목록 (둘 다 없으면 전체) */
    public PageResponseDTO<ReviewDTO> getReviews(Long accommodationId, Long roomId, PageRequestDTO pageRequestDTO) {
        if (roomId != null && roomId > 0) {
            return getReviewsByRoomID(roomId, pageRequestDTO);
        }
        return getReviewsByAccommodationIdWithPageable(accommodationId, pageRequestDTO);
    }
}
