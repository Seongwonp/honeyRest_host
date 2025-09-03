package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.*;
import com.honeyrest.honeyrest_host.entity.Review;
import com.honeyrest.honeyrest_host.repository.OReservationRepository;
import com.honeyrest.honeyrest_host.repository.OReviewRepository;
import com.honeyrest.honeyrest_host.repository.OUserRepository;
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
    private final OReservationRepository reservationRepository;
    private final OUserRepository userRepository;

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
                .build();
    }

    private Review toEntity(ReviewDTO r) {
        return Review.builder()
                .reviewId(r.getReviewId())
                .reservation(reservationRepository.getReferenceById(r.getReservationId()))
                .user(userRepository.getReferenceById(r.getUserId()))
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
                .build();
    }

    public void create(ReviewDTO reviewDTO) {
        reviewRepository.save(toEntity(reviewDTO));
    }


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

    public void saveReply(Long reviewId, String reply){
        Review review= reviewRepository.findByReviewId(reviewId);
        ReviewDTO dto = toDTO(review);
        dto.setReply(reply);

        reviewRepository.save(toEntity(dto));
    }
}
