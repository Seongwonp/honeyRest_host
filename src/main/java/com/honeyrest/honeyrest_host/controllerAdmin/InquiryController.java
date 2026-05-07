package com.honeyrest.honeyrest_host.controllerAdmin;


import com.honeyrest.honeyrest_host.dtoAdmin.InquiryDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CompanyService;
import com.honeyrest.honeyrest_host.serviceAdmin.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String detail(@PathVariable("id") Long inquiryId, Model model) {
        InquiryDTO dto = inquiryService.get(inquiryId);
        if (dto == null) return "redirect:/admin/inquiries/list";
        model.addAttribute("inquiry", dto);
        return "admin/inquiries/detail";
    }

    // 관리자 - 답글 등록/수정
    @PostMapping("/{id}/reply")
    public String reply(@PathVariable("id") Long inquiryId,
                        @RequestParam("relpyText") String relpyText,
                        RedirectAttributes ra,
                        @RequestParam(required = false) Long accommodationId,
                        @RequestParam(required = false) String q,
                        @RequestParam(required = false) Boolean replied,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size) {
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
    public String delete(@PathVariable("id") Long inquiryId, RedirectAttributes ra) {
        inquiryService.delete(inquiryId);
        ra.addFlashAttribute("message", "문의가 삭제되었습니다.");
        return "redirect:/admin/inquiries/list";
    }


}
