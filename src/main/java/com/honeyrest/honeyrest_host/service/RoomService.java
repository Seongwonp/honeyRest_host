package com.honeyrest.honeyrest_host.service;

import com.honeyrest.honeyrest_host.dto.RoomDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface RoomService {

    // 회사 전체 또는 특정 숙소 페이징
    Page<RoomDTO> findPageByCompany(Long companyId, Long accommodationId, Pageable pageable);
    // 특정 숙소만
    Page<RoomDTO> findPageByAccommodationId(Long accommodationId, Pageable pageable);

    // 단건/CRUD
    RoomDTO getByRoomId(Long roomId);
    RoomDTO registerRoom(RoomDTO dto);
    void modifyRoom(RoomDTO dto);
    void removeRoom(Long roomId);

    List<RoomDTO> findAllByCompanyId(Long companyId);
}