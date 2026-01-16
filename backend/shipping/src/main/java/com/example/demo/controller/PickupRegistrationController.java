package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PickupRegistrationRequest;
import com.example.demo.service.PickupRegistrationService;

import reactor.core.publisher.Mono;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/pickup")
public class PickupRegistrationController {


    private final PickupRegistrationService service;

    

    public PickupRegistrationController(PickupRegistrationService service){
        this.service=service;
    }


 
    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerPickup(
        @RequestBody PickupRegistrationRequest request
    ) {
        return service.registerPickup(request)
        .map(ResponseEntity::ok)
        .onErrorResume(ex->Mono.just(ResponseEntity.badRequest().body(ex.getMessage())));
    }
    
}
