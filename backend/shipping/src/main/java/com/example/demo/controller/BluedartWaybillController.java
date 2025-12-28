package com.example.demo.controller;

import com.example.demo.service.BluedartWaybillService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bluedart")
@CrossOrigin
public class BluedartWaybillController {

    private final BluedartWaybillService waybillService;

    public BluedartWaybillController(BluedartWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    @PostMapping("/waybill")
public Map<String, Object> createWaybill(@RequestBody Map<String, Object> request) {

    System.out.println("✅ Backend received waybill request");
    System.out.println(request);

    return waybillService.generateWaybill(request);
}

}
