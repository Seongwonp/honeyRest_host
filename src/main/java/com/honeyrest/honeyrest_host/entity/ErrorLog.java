package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "error_log_id")
    private Long errorLogId;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "error_class", length = 200)
    private String errorClass;

    @Column(name = "message", length = 2000)
    private String message;

    @Lob
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "resolved", nullable = false)
    @Builder.Default
    private boolean resolved = false;

    public void resolve() {
        this.resolved = true;
    }
}
