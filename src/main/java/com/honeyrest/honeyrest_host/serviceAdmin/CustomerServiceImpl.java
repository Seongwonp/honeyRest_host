package com.honeyrest.honeyrest_host.serviceAdmin;


import com.honeyrest.honeyrest_host.dto.UserDetailDTO;
import com.honeyrest.honeyrest_host.dto.UserListDTO;
import com.honeyrest.honeyrest_host.entity.User;
import com.honeyrest.honeyrest_host.repositoryAdmin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<UserListDTO> list(String q, Integer page, Integer size, String sort) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0) ? 10 : size;

        // 기본 정렬: 생성일이 없으면 userId desc 추천
        Sort sortObj = (sort != null && !sort.isBlank())
                ? Sort.by(sort.split(",")[0]).descending()
                : Sort.by("userId").descending();

        Pageable pageable = PageRequest.of(p, s, sortObj);


        return userRepository.findUserList(q, pageable);
    }

    @Override
    public UserDetailDTO getDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + id));

        return toDetailDTO(user);
    }

    // === 매핑 헬퍼 ===
    private UserDetailDTO toDetailDTO(User user) {
        // NPE 방지: null 안전 처리 필요 시 삼항/디폴트값 추가
        return UserDetailDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .point(user.getPoint())
                .role(user.getRole())               // String 컬럼 가정
                .isVerified(user.getIsVerified())
                .marketingAgree(user.getMarketingAgree())
                .lastLogin(user.getLastLogin())
                .status(user.getStatus())
                .socialType(user.getSocialType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}