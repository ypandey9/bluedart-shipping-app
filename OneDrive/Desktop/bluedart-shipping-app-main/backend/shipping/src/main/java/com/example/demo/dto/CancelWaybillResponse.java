package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.Data;

@Data
public class CancelWaybillResponse {

    @JsonProperty("CancelWaybillResult")
    private CancelWaybillResult cancelWaybillResult;
    
    @Data
    public static class CancelWaybillResult {
        private String AwbNo;
        private Boolean IsError;
        private List<Status> Status;   
    }

    @Data
    public static class Status {
        private String StatusCode;
        private String StatusInformation;
    }
}
