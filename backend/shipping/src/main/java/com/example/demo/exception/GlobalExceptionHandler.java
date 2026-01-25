package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(HttpStatusCodeException.class)
    // public ResponseEntity<Map<String, Object>> handleHttpError(HttpStatusCodeException ex) {
    //     Map<String, Object> error = new HashMap<>();
    //     error.put("success", false);
    //     error.put("message", "Bluedart API error");
    //     error.put("details", ex.getResponseBodyAsString());

    //     return ResponseEntity
    //             .status(ex.getStatusCode())
    //             .body(error);
    // }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
    //     Map<String, Object> error = new HashMap<>();
    //     error.put("success", false);
    //     error.put("message", "Internal server error");
    //     error.put("details", ex.getMessage());

    //     return ResponseEntity
    //             .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(error);
    // }

    @ExceptionHandler(BluedartApiException.class)
    public ResponseEntity<Map<String, Object>> handleBluedartError(
            BluedartApiException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", ex.getUserMessage(),
                        "source", "BLUEDART"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "success", false,
                        "message", "Internal server error"
                ));
    }
}


