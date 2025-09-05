package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.dtoAdmin.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.DashboardDTO;


public interface DashboardService {
    /*
     * 로그인 사용자 이메일로 집계 + 현재 관리자 반환 준비
     */
    DashboardDTO getCountsFor(String adminEmail);

    /*
     * 현재 관리자(User) 객체 조회(컨트롤러에서 model에 넣기 용)
     */
    AdminLoginRequestDTO getCurrentAdmin(String adminEmail);

}
