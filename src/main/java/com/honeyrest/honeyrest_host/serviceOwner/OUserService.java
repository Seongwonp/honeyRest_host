package com.honeyrest.honeyrest_host.serviceOwner;

import com.honeyrest.honeyrest_host.dtoOwner.PageRequestDTO;
import com.honeyrest.honeyrest_host.dtoOwner.PageResponseDTO;
import com.honeyrest.honeyrest_host.dtoOwner.UserDTO;
import com.honeyrest.domain.entity.User;
import com.honeyrest.honeyrest_host.repositoryOwner.OUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OUserService {

    private final OUserRepository userRepository;
    private final ModelMapper modelMapper;

    public PageResponseDTO<UserDTO> getUsersWithPage(PageRequestDTO pageRequestDTO) {
        return getUsersWithPage(pageRequestDTO, null);
    }

    /** 회원 목록. keyword 는 이름·이메일 부분 일치 */
    public PageResponseDTO<UserDTO> getUsersWithPage(PageRequestDTO pageRequestDTO, String keyword) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(), Sort.by(Sort.Order.desc("lastLogin"), Sort.Order.desc("userId")));

        Page<User> page = (keyword == null || keyword.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.searchByKeyword(keyword.trim(), pageable);

        List<UserDTO> list = page.getContent().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();

        long total = page.getTotalElements();

        PageResponseDTO<UserDTO> responseDTO = PageResponseDTO.<UserDTO>withAll()
                .dtoList(list)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(total)
                .build();

        return responseDTO;
    }

    /** 이메일로 회원 조회 (없으면 null) */
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user == null ? null : modelMapper.map(user, UserDTO.class);
    }

    public long countUsers() {
        return userRepository.count();
    }

    /** 이름·전화번호로 회원 조회 (없으면 null) */
    public UserDTO getUserByNameAndPhone(String name, String phone) {
        User user = userRepository.findByNameAndPhone(name, phone);
        return user == null ? null : modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUser(Long userId) {
        return userRepository.findById(userId).map(u -> modelMapper.map(u, UserDTO.class)).orElse(null);
    }

    /** 예약 등록 화면의 회원 선택 목록 */
    public List<UserDTO> getBookableUsers() {
        return userRepository.findByRoleAndStatusOrderByNameAsc("GENERAL", "ACTIVE").stream()
                .map(u -> modelMapper.map(u, UserDTO.class)).toList();
    }

    public List<UserDTO> searchByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream().map(u -> modelMapper.map(u, UserDTO.class)).toList();

    }
}
