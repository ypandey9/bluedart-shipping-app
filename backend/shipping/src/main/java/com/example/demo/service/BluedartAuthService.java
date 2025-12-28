package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BluedartAuthService {

    @Value("${bluedart.base-url}")
    private String baseUrl;

    @Value("${bluedart.client-id}")
    private String clientId;

    @Value("${bluedart.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedJwtToken;
    private long tokenExpiryTime;

    public synchronized String getJwtToken() {

        if(cachedJwtToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedJwtToken;
        }

        String url = baseUrl + "/in/transportation/token/v1/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("ClientID", clientId);
        headers.set("clientSecret", clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("ClientID", clientId);
        body.add("clientSecret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        cachedJwtToken = response.getBody().get("JWTToken").toString();

        // Assuming token is valid for 23 hour

        tokenExpiryTime = System.currentTimeMillis() + (23 * 60 * 60 * 1000);

        return cachedJwtToken;
    }
}
