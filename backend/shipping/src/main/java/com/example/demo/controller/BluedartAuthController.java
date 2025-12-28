package com.example.demo.controller;

import com.example.demo.service.BluedartAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BluedartAuthController {

    private final BluedartAuthService authService;

    public BluedartAuthController(BluedartAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/bluedart/token")
    public Map<String, String> getToken() {
        String token = authService.getJwtToken();
        return Map.of("JWTToken", token);
    }
}
