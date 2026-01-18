package com.example.demo.service;
import org.springframework.http.MediaType;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.PickupRegistrationRequest;
import com.example.demo.repository.PickupFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.databind.JsonNode;


@Service
public class PickupRegistrationService {
    
    @Value("${bluedart.base-url}")
    private String baseUrl;

    private String endPoint="/in/transportation/pickup/v1/RegisterPickup";

    private final WebClient webClient;
    private final BluedartAuthService authService;
    private final PickupFileRepository pickupRepository;

    public PickupRegistrationService(WebClient webClient,BluedartAuthService authService,PickupFileRepository pickupRepository) {
        this.webClient=webClient;
        this.authService=authService;
        this.pickupRepository=pickupRepository;
    }

    
    public Mono<String> registerPickup(PickupRegistrationRequest request) {

    String jwtToken = authService.getJwtToken();

    return webClient.post()
        .uri(baseUrl + endPoint)
        .header("JWTToken", jwtToken)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchangeToMono(response -> {

            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(String.class)
                    .map(body -> {

                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode responseJson = mapper.readTree(body);

                            JsonNode result = responseJson.path("RegisterPickupResult");
                            String tokenNumber = result.path("TokenNumber").asText();

                            String statusInfo = "";
                            JsonNode statusArray = result.path("Status");
                            if (statusArray.isArray() && statusArray.size() > 0) {
                                statusInfo = statusArray.get(0)
                                        .path("StatusInformation")
                                        .asText();
                            }

                            // build history object
                            ObjectNode history = mapper.createObjectNode();
                            history.put("timestamp", Instant.now().toString());
                            history.put("tokenNumber", tokenNumber);
                            history.put("status", statusInfo);

                            history.set("pickupRequest", mapper.valueToTree(request));
                            history.set("pickupResponse", responseJson);

                            // save history
                            pickupRepository.save(history);

                            System.out.println("📦 Pickup saved. Token: " + tokenNumber);

                        } catch (Exception e) {
                            System.err.println("❌ Failed to save pickup history");
                            e.printStackTrace();
                        }

                        return body;
                    });
            }

            return response.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    System.err.println("❌ Blue Dart Error Status: " + response.statusCode());
                    System.err.println("❌ Blue Dart Error Body: " + errorBody);
                    return Mono.error(new RuntimeException(errorBody));
                });
        });
}


}
