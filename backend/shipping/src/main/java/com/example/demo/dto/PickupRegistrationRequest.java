package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PickupRegistrationRequest {
    
    @JsonProperty("request")
    private PickupRequest request;

    @JsonProperty("profile")
    private Profile profile;
}


