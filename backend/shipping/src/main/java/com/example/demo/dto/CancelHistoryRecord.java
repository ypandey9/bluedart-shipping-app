package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelHistoryRecord {
    private String awbNo;
    private CancelStatus status;
    private String message;
    private LocalDateTime cancelledAt;
    private String source;
    private String requestedBy;
}
