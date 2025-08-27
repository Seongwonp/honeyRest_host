package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dto.AdminLoginRequestDTO;
import com.honeyrest.honeyrest_host.dto.CompanyDTO;
import com.honeyrest.honeyrest_host.dto.RoomDTO;
import com.honeyrest.honeyrest_host.dto.RoomImageDTO;
import com.honeyrest.honeyrest_host.entity.Company;
import com.honeyrest.honeyrest_host.repository.CompanyRepository;
import com.honeyrest.honeyrest_host.repository.accommodation.AccommodationRepository;
import com.honeyrest.honeyrest_host.service.*;
import com.honeyrest.honeyrest_host.service.accommodation.AccommodationService;
import com.honeyrest.honeyrest_host.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rooms")
public class RoomPageController {
    private final RoomService roomService;
    private final AccommodationRepository accommodationRepository;
    private final FileUploadUtil fileUploadUtil;
    private final RoomImageService roomImageService;
    private final CompanyRepository companyRepository;
    private final AccommodationService accommodationService;
    private final CompanyService companyService;
    private final UserService userService;


    /**
     * 전체 객실 목록 (사이드바 진입)
     */
    @GetMapping("/list_all")
    public String listAll(@PageableDefault(size = 10, sort = "roomId", direction = Sort.Direction.DESC) Pageable pageable,
                          Authentication authentication, Model model) {

        // 로그인 사용자 이메일 가져오기
        String email = (authentication != null && authentication.getPrincipal() instanceof UserDetails ud
                ? ud.getUsername()
                : authentication.getName());

        // 2) 이메일 -> companyId
        Long companyId = companyService.getByUserEmail(email).getCompanyId();

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

        Long companyId = companyRepository.findByEmail(email)
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

        String email = (authentication.getPrincipal() instanceof String s) ? s : authentication.getName();

        AdminLoginRequestDTO admin = userService.getUserByEmail(email);
        CompanyDTO companyDTO = companyService.getByUserEmail(admin.getEmail());
        Long companyId = companyDTO.getCompanyId();

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
        RoomDTO saved = roomService.registerRoom(form);
        ra.addFlashAttribute("msg", "객실이 등록되었습니다.");
        // 숙소별 목록으로 리다이렉트
        return "redirect:/admin/rooms/list/by-accommodation?accommodationId=" + saved.getAccommodationId();
    }

    /* ========== 수정 ========== */
    // 수정 폼
    @GetMapping("/edit/{roomId}")
    public String editForm(@PathVariable Long roomId, Model model) {
        RoomDTO form = roomService.getByRoomId(roomId); // 반드시 null 아니게
        model.addAttribute("form", form);
        model.addAttribute("accommodations", accommodationRepository.findAll());
        return "admin/rooms/edit"; // templates/admin/rooms/edit.html
    }

    @PostMapping("/{roomId}")
    public String update(@PathVariable Long roomId,
                         @Valid @ModelAttribute("form") RoomDTO form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes ra) throws Exception {
        if (binding.hasErrors()) {
            model.addAttribute("accommodations", accommodationRepository.findAll());
            return "admin/rooms/edit";
        }
        // 이미지 업로드
        String image = fileUploadUtil.upload(form.getFile(), "room");

        if (binding.hasErrors()) {
            model.addAttribute("accommodations", accommodationRepository.findAll());
            return "admin/rooms/edit";
        }

        // form에 roomId 수정
        form.setRoomId(roomId);
        roomService.modifyRoom(form);

        // 이미지 저장(필요시에)
        RoomImageDTO roomImageDTO = RoomImageDTO.builder()
                .imageUrl(image)
                .roomId(roomId)
                .build();
        roomImageService.registerRoomImage(roomImageDTO);
        // 알림용 플래시 메시지
        ra.addFlashAttribute("msg", "객실이 수정되었습니다.");

        // 전체 객실 목록으로 이동
        return "redirect:/admin/rooms/list_all";
//        return "redirect:/admin/rooms/list?accommodationId=" + form.getAccommodationId();
    }

    /* ========== 삭제 ========== */
    @PostMapping("/{roomId}/delete")
    public String delete(@PathVariable Long roomId,
                         @RequestParam("accommodationId") Long accommodationId,
                         RedirectAttributes ra) {
        roomService.removeRoom(roomId);
        ra.addFlashAttribute("msg", "객실이 삭제되었습니다.");
        return "redirect:/admin/rooms/list?accommodationId=" + accommodationId;
    }
}
