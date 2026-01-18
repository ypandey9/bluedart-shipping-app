package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.*;
import java.util.List;

@Service
public class BlueDartTrackingService {

    @Value("${base-track-url}")
    private String baseUrl;

    @Value("${tracking-path}")
    private String trackingPath;

    @Value("${loginid}")
    private String loginId;

    @Value("${lickey}")
    private String licenseKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BluedartAuthService authService;

    public BlueDartTrackingService(BluedartAuthService authService) {
        this.authService=authService;
    }


    public String trackShipment(String awbNumber) {
        String jwtToken="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdWJqZWN0LXN1YmplY3QiLCJhdWQiOlsiYXVkaWVuY2UxIiwiYXVkaWVuY2UyIl0sImlzcyI6InVybjovL2FwaWdlZS1lZGdlLUpXVC1wb2xpY3ktdGVzdCIsImV4cCI6MTc2ODgwMDYzMSwiaWF0IjoxNzY4NzE0MjMxLCJqdGkiOiI5ZGJiMjMyYS1hN2FlLTRlMjktYTg0Ny1kNzc1NzgzNTZhOWYifQ.1h4w7-6uzEZauJ0vxZNOxGLKuX1jujG-RHCbJOYP3QA";//authService.getJwtToken();
       UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(baseUrl + trackingPath)
                .queryParam("handler", "tnt")
                .queryParam("action", "custawbquery")
                .queryParam("loginid", loginId)
                .queryParam("awb", "awb")
                .queryParam("numbers", awbNumber)
                .queryParam("format", "xml")
                .queryParam("lickey", licenseKey)
                .queryParam("verno", "1")
                .queryParam("scan", "1");

        HttpHeaders headers = new HttpHeaders();
        headers.set("JWTToken", jwtToken);
        headers.setAccept(List.of(MediaType.TEXT_PLAIN));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        return response.getBody();
    }
}
