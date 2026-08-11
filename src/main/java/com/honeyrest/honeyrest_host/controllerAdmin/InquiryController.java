package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.InquiryDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyResourceAccessService;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminInquiryController")
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final CompanyService companyService;
    private final CompanyResourceAccessService resourceAccessService;

    /** 목록은 이미 companyId로 스코프되지만 단건 3개(detail/reply/delete)는 검증이 없었다(P0-5). */
    private void requireOwnInquiry(Authentication authentication, Long inquiryId) {
        Integer companyId = resourceAccessService.currentCompanyId(authentication);
        if (!resourceAccessService.ownsInquiry(companyId, inquiryId)) {
            throw new AccessDeniedException("해당 문의에 접근할 권한이 없습니다.");
        }
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long accommodationId,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) Boolean replied,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Integer companyId = companyService.getCompanyIdByOfCurrentUser();

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

        Page<InquiryDTO> result = inquiryService.listByCompany(companyId, q, replied,pageable);

        model.addAttribute("list", result.getContent());
        model.addAttribute("total", result.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("q",q == null ? "" : q.trim());
        model.addAttribute("replied",replied);

        return "admin/inquiries/list";

    }
    // 관리자모드에서 상세보기
    @GetMapping("/detail/{id}")
    public String detail(Authentication authentication, @PathVariable("id") Long inquiryId, Model model) {
        requireOwnInquiry(authentication, inquiryId);
        InquiryDTO dto = inquiryService.get(inquiryId);
        if (dto == null) return "redirect:/admin/inquiries/list";
        model.addAttribute("inquiry", dto);
        return "admin/inquiries/detail";
    }

    // 관리자 - 답글 등록/수정
    @PostMapping("/{id}/reply")
    public String reply(Authentication authentication,
                        @PathVariable("id") Long inquiryId,
                        @RequestParam("relpyText") String relpyText,
                        RedirectAttributes ra,
                        @RequestParam(required = false) Long accommodationId,
                        @RequestParam(required = false) String q,
                        @RequestParam(required = false) Boolean replied,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size) {
        requireOwnInquiry(authentication, inquiryId);
        try {
            inquiryService.reply(inquiryId, relpyText);
            ra.addAttribute("msg", "답변이 저장되었습니다.");
        } catch (Exception e) {
            ra.addAttribute("msg", "답변 저장 실패하셨습니다." + e.getMessage());
        }
        return "redirect:/admin/inquiries/list?accommodationId=" +(accommodationId == null ? "" : accommodationId)
               + "&q=" + (q == null ? "" : q)
               + "&replied=" + (replied == null ? "" : replied)
               + "&page=" + page + "&size=" + size;
    }



    @PostMapping("/{id}/delete")
    public String delete(Authentication authentication, @PathVariable("id") Long inquiryId, RedirectAttributes ra) {
        requireOwnInquiry(authentication, inquiryId);
        inquiryService.delete(inquiryId);
        ra.addFlashAttribute("message", "문의가 삭제되었습니다.");
        return "redirect:/admin/inquiries/list";
    }


}
