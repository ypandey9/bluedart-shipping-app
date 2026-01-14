package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CancelWaybillRequest {

    @JsonProperty("Request")
    private Request request;

    @JsonProperty("Profile")
    private Profile profile;

    @Data
    public static class Request {

        @JsonProperty("AWBNo")
        private String awbNo;

    }

    @Data
    public static class Profile {

        @JsonProperty("Api_type")
        private String apiType;

        @JsonProperty("LicenceKey")
        private String LicenceKey;

        @JsonProperty("LoginID")
        private String LoginId;

    }

}
