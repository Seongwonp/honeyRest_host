package com.honeyrest.honeyrest_host.controllerOwner;

import com.honeyrest.honeyrest_host.entity.ErrorLog;
import com.honeyrest.honeyrest_host.serviceAdmin.ErrorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/owner/system")
@RequiredArgsConstructor
public class SystemController {

    private final ErrorLogService errorLogService;

    @GetMapping("/errors")
    public String errorLogs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size,
                            @RequestParam(defaultValue = "all") String filter,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ErrorLog> logs = "unresolved".equals(filter)
                ? errorLogService.getUnresolved(pageable)
                : errorLogService.getAll(pageable);

        model.addAttribute("logs", logs);
        model.addAttribute("filter", filter);
        model.addAttribute("unresolvedCount", errorLogService.countUnresolved());
        return "owner/system/errors";
    }

    @PostMapping("/errors/{id}/resolve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resolve(@PathVariable Long id) {
        errorLogService.resolve(id);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
