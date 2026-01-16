package com.example.demo.service;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.PickupRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;


@Service
public class PickupRegistrationService {
    
    @Value("${bluedart.base-url}")
    private String baseUrl;

    private String endPoint="/in/transportation/pickup/v1/RegisterPickup";

    private final WebClient webClient;
    private final BluedartAuthService authService;

    public PickupRegistrationService(WebClient webClient,BluedartAuthService authService) {
        this.webClient=webClient;
        this.authService=authService;
    }

    
    public Mono<String> registerPickup(PickupRegistrationRequest request) {

        // 🔍 DEBUG: log outgoing payload (TEMPORARY)
        try {
            System.out.println(
                new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(request)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                JsonNode root = mapper.readTree(body);

                JsonNode result = root.path("RegisterPickupResult");
                String tokenNumber = result.path("TokenNumber").asText();

                JsonNode statusArray = result.path("Status");
                String statusInfo = "";

                if (statusArray.isArray() && statusArray.size() > 0) {
                    statusInfo = statusArray.get(0)
                            .path("StatusInformation")
                            .asText();
                }

                // ✅ PRINT REQUIRED DETAILS
                System.out.println("✅ Pickup Status : " + statusInfo);
                System.out.println("📦 Token Number  : " + tokenNumber);

            } catch (Exception e) {
                System.err.println("❌ Failed to parse pickup response");
                e.printStackTrace();
            }

            // still return full response to controller
            return body;
        });
}


                

                return response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            System.err.println("❌ Blue Dart Error Status: "
                                    + response.statusCode());
                            System.err.println("❌ Blue Dart Error Body: "
                                    + errorBody);

                            return Mono.error(
                                    new RuntimeException(errorBody)
                            );
                        });
            });
}

}
