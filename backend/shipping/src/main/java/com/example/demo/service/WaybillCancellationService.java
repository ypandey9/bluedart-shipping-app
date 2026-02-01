package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.CancelWaybillRequest;
import com.example.demo.dto.CancelWaybillResponse;
import com.example.demo.dto.CancelWaybillResponse.CancelWaybillResult;
import com.example.demo.service.BluedartAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;





@Service
public class WaybillCancellationService {   

    @Value("${bluedart.cancel-url}")
    private String cancelUrl;

    @Value("${bluedart.licence-key}")
    private String licenceKey;

    @Value("${bluedart.login-id}")
    private String loginId;

    private final BluedartAuthService bluedartAuthService;
    private final RestTemplate restTemplate;
    private String baseUrl="https://apigateway-sandbox.bluedart.com/in/transportation/waybill/v1/CancelWaybill";

    public WaybillCancellationService(BluedartAuthService bluedartAuthService) {
        this.bluedartAuthService = bluedartAuthService;
        this.restTemplate = new RestTemplate();
    }

    String getJwtToken() {
        return bluedartAuthService.getJwtToken();
    }


public CancelWaybillResponse cancelWaybillInternal(String awb) {

    CancelWaybillRequest request = new CancelWaybillRequest();

    CancelWaybillRequest.Request req = new CancelWaybillRequest.Request();
    req.setAwbNo(awb);

    CancelWaybillRequest.Profile profile = new CancelWaybillRequest.Profile();
    profile.setApiType("S");
    profile.setLicenceKey(licenceKey);
    profile.setLoginId(loginId);

    request.setRequest(req);
    request.setProfile(profile);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("JWTToken", getJwtToken());

    HttpEntity<CancelWaybillRequest> entity =
            new HttpEntity<>(request, headers);

    try {
        // ✅ Success case (HTTP 200)
        ResponseEntity<CancelWaybillResponse> response =
                restTemplate.exchange(
                        baseUrl,
                        HttpMethod.POST,
                        entity,
                        CancelWaybillResponse.class
                );

        return response.getBody();

    } catch (HttpClientErrorException ex) {
        // ✅ Error case (HTTP 400)
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(ex.getResponseBodyAsString());

            JsonNode errorNode = root
                    .path("error-response")
                    .get(0);

            String message = errorNode
                    .path("Status")
                    .get(0)
                    .path("StatusInformation")
                    .asText("Cancellation failed");

            // 🔹 Build normalized response
            CancelWaybillResponse response = new CancelWaybillResponse();
            CancelWaybillResponse.CancelWaybillResult result =
                    new CancelWaybillResponse.CancelWaybillResult();

            result.setAwbNo(awb);
            result.setIsError(true);

            CancelWaybillResponse.Status status =
                    new CancelWaybillResponse.Status();
            status.setStatusCode("CancelFailure");
            status.setStatusInformation(message);

            result.setStatus(List.of(status));
            response.setCancelWaybillResult(result);

            return response;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse Blue Dart error response", e
            );
        }
    }
}

}

