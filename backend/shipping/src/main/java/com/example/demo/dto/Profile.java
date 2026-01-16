package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile {

    @JsonProperty("LoginID")
    private String loginId;

    @JsonProperty("LicenceKey")
    private String licenceKey;

    @JsonProperty("Api_type")
    private String apiType;
}
