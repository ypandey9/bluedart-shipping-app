package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ShipperProfile;
import com.example.demo.service.ProfileFileService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileFileService fileService;

    public ProfileController(ProfileFileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ShipperProfile getProfile() throws Exception {
        return fileService.getProfile();
    }

    @PostMapping
    public String saveProfile(@RequestBody ShipperProfile profile) throws Exception {
        fileService.saveProfile(profile);
        return "Profile saved to data/shipper-profile.json";
    }
}
