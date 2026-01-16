package com.example.demo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class WaybillRecord {

    private String awbNo;
    private String creditReferenceNo;
    private String createdAt;   // ✅ String instead of LocalDateTime
    private Map<String, Object> request;
    private Map<String, Object> response;

    public WaybillRecord() {
    }

    public WaybillRecord(
            String awbNo,
            String creditReferenceNo,
            Map<String, Object> request,
            Map<String, Object> response
    ) {
        this.awbNo = awbNo;
        this.creditReferenceNo = creditReferenceNo;
        this.createdAt = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.request = request;
        this.response = response;
    }

    public String getAwbNo() {
        return awbNo;
    }

    public String getCreditReferenceNo() {
        return creditReferenceNo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

    public Map<String, Object> getResponse() {
        return response;
    }
}
