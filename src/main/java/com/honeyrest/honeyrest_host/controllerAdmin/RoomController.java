package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.*;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repositoryAdmin.CompanyRepository;
import com.honeyrest.honeyrest_host.repositoryAdmin.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.serviceAdmin.*;
import com.honeyrest.honeyrest_host.serviceAdmin.accommodation.AccommodationService;
import com.honeyrest.honeyrest_host.utilAdmin.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Log4j2
@Controller("adminRoomController")
@RequiredArgsConstructor
@RequestMapping("/admin/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomImageService roomImageService;
    private final CompanyRepository companyRepository;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final UserService userService;

    private final AccommodationRepository accommodationRepository;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 전체 객실 목록 (사이드바 진입)
     */
    @GetMapping("/list_all")
    public String listAll(@PageableDefault(size = 12, sort = "roomId", direction = Sort.Direction.DESC) Pageable pageable,
                          Authentication authentication, Model model) {

        // 로그인 사용자 이메일 가져오기
        String email = (authentication != null && authentication.getPrincipal() instanceof UserDetails ud
                ? ud.getUsername()
                : authentication.getName());

        // 2) 이메일 -> companyId
        Integer companyId = companyService.getByUserEmail(email).getCompanyId();

        // 3) '내' 회사 객실만 페이징 조회
        Page<RoomDTO> page = roomService.findPageByCompany(companyId, null, pageable);
        // 4) 숙소명으로 그룹핑
        Map<String, List<RoomDTO>> groups = page.getContent().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> (r.getAccommodationName() == null || r.getAccommodationName().isBlank())
                                ? "(숙소 미지정)" : r.getAccommodationName(),
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        model.addAttribute("page", page);
        model.addAttribute("groups", groups);
        return "admin/rooms/list_all";
    }

    /**
     * 라우팅 헬퍼: /list?accommodationId=... -> by-accommodation, 없으면 list_all
     */
    @GetMapping("/list")
    public String listRouter(@RequestParam(required = false) Long accommodationId) {
        if (accommodationId != null) {
            return "redirect:/admin/rooms/list/by-accommodation?accommodationId=" + accommodationId;
        }
        return "redirect:/admin/rooms/list_all";
    }


    /**
     * 특정 숙소의 객실 목록 (등록/수정 후 이동) (업체 관리자 범위)
     */
    @GetMapping("/list/by-accommodation")
    public String listByAccommodation(@RequestParam Long accommodationId,
                                      @PageableDefault(size = 10, sort = "roomId", direction = Sort.Direction.DESC)
                                      Pageable pageable,
                                      Authentication authentication,
                                      Model model) {

        String email = (authentication != null && authentication.getPrincipal() instanceof UserDetails ud)
                ? ud.getUsername()
                : authentication.getName();

        Integer companyId = companyRepository.findByEmail(email)
                .map(Company::getCompanyId)
                .orElseThrow(() -> new UsernameNotFoundException("업체 관리자 이메일에 해당하는 회사가 없습니다."));

        // 내 회사 + 특정 숙소로 제한
        Page<RoomDTO> page = roomService.findPageByCompany(companyId, accommodationId, pageable);

        Map<String, List<RoomDTO>> groups = page.getContent().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> (r.getAccommodationName() == null || r.getAccommodationName().isBlank())
                                ? "(숙소 미지정)" : r.getAccommodationName(),
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        model.addAttribute("page", page);
        model.addAttribute("groups", groups);
        model.addAttribute("accommodationId", accommodationId);
        return "admin/rooms/list_all"; // 같은 템플릿 재사용
    }

    /**
     * 등록 폼
     */
    @GetMapping("/add")
    public String addForm(Authentication authentication, @RequestParam(value = "accommodationId", required = false) Long accommodationId,
                          Pageable pageable,
                          Model model) {

        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails ud) {
            email = ud.getUsername();
        } else {
            email = authentication.getName();
        }

        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        Integer companyId = companyDTO.getCompanyId();

        RoomDTO form = new RoomDTO();
        form.setAccommodationId(accommodationId);
        model.addAttribute("form", form);
        model.addAttribute("accommodations", accommodationService.getAllById(companyId));
        return "admin/rooms/add";
    }

    /**
     * 등록 처리
     */
    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("form") RoomDTO form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            // 에러 시 다시 렌더링할 데이터들 채워줌
            model.addAttribute("accommodations", accommodationRepository.findAll());
            return "admin/rooms/add";
        }
        try {
            // 1) 우선 방을 생성 (이미지는 나중에)
            RoomDTO saved = roomService.registerRoom(form);  // 서비스는 RoomDTO를 반환
            Long roomId = saved.getRoomId();

            // 2) 이미지가 들어온 경우에만 업로드 + DB 저장 (교체)
            List<RoomImageDTO> images = new ArrayList<>();

            // 메인(단일) 파일
            MultipartFile main = form.getFile();
            if (main != null && !main.isEmpty()) {
                String url = fileUploadUtil.upload(main, "room");
                images.add(RoomImageDTO.builder()
                        .roomId(roomId)
                        .imageUrl(url)
                        .sortOrder(0)  // MAIN
                        .build());
            }

            // 서브(다중) 파일
            if (form.getFiles() != null && !form.getFiles().isEmpty()) {
                int idx = images.isEmpty() ? 0 : 1; // 메인 넣었으면 1부터, 없었으면 0부터
                for (MultipartFile f : form.getFiles()) {
                    if (f != null && !f.isEmpty()) {
                        String url = fileUploadUtil.upload(f, "room");
                        images.add(RoomImageDTO.builder()
                                .roomId(roomId)
                                .imageUrl(url)
                                .sortOrder(idx++) // SUB
                                .build());
                    }
                }
            }

            if (!images.isEmpty()) {
                roomImageService.replaceAll(roomId, images); // 전체 교체(메인=0 보장)
            }

            ra.addFlashAttribute("success", "객실이 등록되었습니다.");
            return "redirect:/admin/rooms/list";

        } catch (Exception e) {
            log.error("room create error", e);
            ra.addFlashAttribute("error", "등록 중 오류: " + e.getMessage());
            return "redirect:/admin/rooms/add";
        }
    }

    /* ========== 수정 ========== */
    // 수정 폼
    @GetMapping("/edit/{roomId}")
    public String editForm(@PathVariable Long roomId, Model model) {
        RoomDTO form = roomService.getByRoomId(roomId); // 반드시 null 아니게

        // 숙소의 체크인/체크아웃을 표시용으로 세팅
        if (form.getAccommodationId() != null) {
            var acc = accommodationService.getById(form.getAccommodationId()); // 숙소 조회(아무 DTO든 checkInTime/OutTime 포함)
            if (acc != null) {
                form.setDisplayCheckInTime(acc.getCheckInTime());   // LocalTime
                form.setDisplayCheckOutTime(acc.getCheckOutTime()); // LocalTime
            }
        }
        model.addAttribute("form", form);
        model.addAttribute("accommodations", accommodationRepository.findAll());
        return "admin/rooms/edit"; // templates/admin/rooms/edit.html
    }

    /* =============== 저장 =================== */
    @PostMapping("/{roomId}")
    public String update(@PathVariable Long roomId,
                         @Valid @ModelAttribute("form") RoomDTO form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) throws Exception {
//        log.info("[ROOM UPDATE] id={}, checkIn={}, checkOut={}",
//                roomId, form.getdCheckInTime(), form.getCheckOutTime());
        if (binding.hasErrors()) {
            binding.getAllErrors().forEach(err -> log.error("bind err: {}", err));
            model.addAttribute("accommodations", accommodationRepository.findAll());
            // 에러 시에도 사용자가 입력한 정책을 유지
            return "admin/rooms/edit";
        }
        try {
            // 1) 우선 방 정보 업데이트
            form.setRoomId(roomId);          // 서비스 시그니처에 맞춰 DTO에 id 세팅
            roomService.modifyRoom(form);    // 서비스 구현 이름과 일치

            // 2) 이미지 파일이 들어온 경우에만 이미지 교체 수행
            boolean hasMain = (form.getFile() != null && !form.getFile().isEmpty());
            boolean hasSubs = (form.getFiles() != null && form.getFiles().stream().anyMatch(f -> f != null && !f.isEmpty()));

            if (hasMain || hasSubs) {
                List<RoomImageDTO> images = new ArrayList<>();

                if (hasMain) {
                    String url = fileUploadUtil.upload(form.getFile(), "room");
                    images.add(RoomImageDTO.builder()
                            .roomId(roomId)
                            .imageUrl(url)
                            .sortOrder(0)
                            .build());
                }

                if (hasSubs) {
                    int idx = images.isEmpty() ? 0 : 1;
                    for (MultipartFile f : form.getFiles()) {
                        if (f != null && !f.isEmpty()) {
                            String url = fileUploadUtil.upload(f, "room");
                            images.add(RoomImageDTO.builder()
                                    .roomId(roomId)
                                    .imageUrl(url)
                                    .sortOrder(idx++)
                                    .build());
                        }
                    }
                }

                roomImageService.replaceAll(roomId, images); // 새로 들어온 파일들로 전체 교체
            }

            ra.addFlashAttribute("success", "객실이 수정되었습니다.");
            return "redirect:/admin/rooms/list_all";

        } catch (Exception e) {
            log.error("room update error", e);
            ra.addFlashAttribute("error", "수정 중 오류: " + e.getMessage());
            return "redirect:/admin/rooms/edit/" + roomId;
        }
    }


    /* ========== 삭제 ========== */
    @PostMapping("/{roomId}/delete")
    public String delete(@PathVariable Long roomId,
                         @RequestParam("accommodationId") Long accommodationId,
                         RedirectAttributes ra) {
        try {
            roomService.removeRoom(roomId);
            ra.addFlashAttribute("success", "객실이 삭제되었습니다.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "해당 객실에 예약 이력이 있어 삭제할 수 없습니다. "
                                          + "대신 객실 상태를 'INACTIVE'로 변경하세요.");
        }
        return "redirect:/admin/rooms/list_all"; // 전체 객실 목록으로 이동
    }

    @GetMapping("/detail/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model) {
        RoomDTO room = roomService.findDetailById(roomId);
        if (room == null) {
            return "redirect:/admin/rooms/list_all";
        }
        model.addAttribute("room", room);

        // 2) 편의시설/침대 (JSON 배열 또는 CSV 모두 지원)
        model.addAttribute("amenitiesList", toList(room.getAmenities()));
        model.addAttribute("bedList", toList(room.getBedInfo()));


        return "admin/rooms/detail";
    }

    /* ====== 아래 유틸은 컨트롤러 private 메서드로 두면 편해요 ====== */

    // JSON 배열 문자열(["TV","Wi-Fi"]) 또는 CSV("TV, Wi-Fi")를 List<String>으로 변환
    private List<String> toList(String jsonOrCsv) {
        if (jsonOrCsv == null || jsonOrCsv.isBlank()) return List.of();
        String s = jsonOrCsv.trim();

        // JSON 배열이면 파싱
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);            // 양끝 대괄호 제거
            s = s.replaceAll("^\\s*\"|\"\\s*$", "");        // 양끝 큰따옴표 정리(안전빵)
            String[] parts = s.split("\\s*,\\s*\"");       // "...,"
            return Arrays.stream(parts)
                    .map(v -> v.replaceAll("^\"|\"$", ""))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .toList();

        }
        return Arrays.stream(jsonOrCsv.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();

    }

    // 정책 상세 문자열을 줄바꿈 기준으로 분리 (CR/LF 모두 지원) 후 공백/빈줄 제거
    private List<String> splitLines(String text) {
        if (text == null) return List.of();
        String[] lines = text.split("\\r?\\n");
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            String v = line == null ? "" : line.trim();
            if (!v.isEmpty()) out.add(v);
        }
        return out;
    }


    // 상태변화
    @PostMapping("/{roomId}/toggle")
    public String toggle(@PathVariable Long roomId,
                         RedirectAttributes ra) {
        roomService.toggleStatus(roomId);   // ACTIVE ↔ INACTIVE
        ra.addFlashAttribute("msg", "상태가 변경되었습니다.");
        return "redirect:/admin/rooms/list_all";
    }

}