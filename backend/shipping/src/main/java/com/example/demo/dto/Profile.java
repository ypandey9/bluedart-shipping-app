package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {

    @JsonProperty("LoginId")
    private String LoginId;

    @JsonProperty("LicenseKey")
    private String LicenseKey;

    @JsonProperty("Api_type")   
    private String Api_type;

    public String getLoginId() {
        return LoginId;
    }
    public void setLoginId(String loginId) {
        LoginId = loginId;
    }
    public String getLicenseKey() {
        return LicenseKey;
    }
    public void setLicenseKey(String licenseKey) {
        LicenseKey = licenseKey;            
}
    public String getApi_type() {
        return Api_type;
    }
    public void setApi_type(String api_type) {
        Api_type = api_type;
    }
}   