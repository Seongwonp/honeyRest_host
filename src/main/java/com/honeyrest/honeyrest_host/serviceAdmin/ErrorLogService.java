package com.honeyrest.honeyrest_host.serviceAdmin;

import com.honeyrest.honeyrest_host.entity.ErrorLog;
import com.honeyrest.honeyrest_host.repositoryAdmin.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class ErrorLogService {

    private final ErrorLogRepository errorLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAsync(Exception e, String url, String method) {
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            if (stackTrace.length() > 10000) stackTrace = stackTrace.substring(0, 10000);

            String message = e.getMessage();
            if (message != null && message.length() > 2000) message = message.substring(0, 2000);

            ErrorLog log = ErrorLog.builder()
                    .occurredAt(LocalDateTime.now())
                    .requestUrl(url)
                    .requestMethod(method)
                    .errorClass(e.getClass().getName())
                    .message(message)
                    .stackTrace(stackTrace)
                    .build();

            errorLogRepository.save(log);
        } catch (Exception ex) {
            log.error("에러 로그 저장 실패", ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<ErrorLog> getAll(Pageable pageable) {
        return errorLogRepository.findAllByOrderByOccurredAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ErrorLog> getUnresolved(Pageable pageable) {
        return errorLogRepository.findByResolvedOrderByOccurredAtDesc(false, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnresolved() {
        return errorLogRepository.countByResolved(false);
    }

    @Transactional
    public void resolve(Long id) {
        errorLogRepository.markResolved(id);
    }
}
