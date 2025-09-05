package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.ReservationRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.RoomRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Override
    public DashboardDTO getCountsFor(String email) {
        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        if (admin == null) {
            // 빈 값으로 응답(컨트롤러에서 로그인 페이지 리다이렉트)
            return DashboardDTO.builder()
                    .accCount(0).resCount(0).roomCount(0).build();
        }

        long accCount = 0L;
        long resCount = 0L;
        long roomCount = 0L;

        if ("SUPER_ADMIN".equals(admin.getRole())) {
            accCount     = accommodationRepository.count();
            resCount     = reservationRepository.countActiveAll();      // 취소 제외 정책 반영 메서드
            roomCount    = roomRepository.count();
        } else if ("COMPANY_ADMIN".equals(admin.getRole())) {
            Long companyId = companyRepository.findCompanyIdByUserEmail(admin.getEmail()).orElse(null);
            if (companyId != null) {
                accCount     = accommodationRepository.countByCompany_CompanyId(companyId);
                resCount     = reservationRepository.countActiveByCompanyId(companyId);
                roomCount    = roomRepository.countByCompanyId(companyId);
            }
        }
        return DashboardDTO.builder()
                .accCount(accCount)
                .resCount(resCount)
                .roomCount(roomCount)
                .build();
    }

    @Override
    public AdminLoginRequestDTO getCurrentAdmin(String email) {
        return userService.getUserByEmail(email);
    }
}
