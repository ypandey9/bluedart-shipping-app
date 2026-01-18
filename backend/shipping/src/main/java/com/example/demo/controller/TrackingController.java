package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BlueDartTrackingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@CrossOrigin("*")
@RestController
@RequestMapping("api/tracking")
public class TrackingController {

    private final BlueDartTrackingService trackingService;

     private final XmlMapper xmlMapper = new XmlMapper();

    public JsonNode parseXmlToJson(String xmlResponse) throws Exception {
        return xmlMapper.readTree(xmlResponse);
    }

    public TrackingController(BlueDartTrackingService trackingService) {
        this.trackingService=trackingService;
    }

    @GetMapping("/{awb}")
    public ResponseEntity<Object> track(@PathVariable String awb) throws Exception {
        System.out.println("Tracking ther shipment. please wait...");
        String responseXml=trackingService.trackShipment(awb);
        JsonNode json=parseXmlToJson(responseXml);
        System.out.println(json);

         ObjectMapper objectMapper = new ObjectMapper();
    Object jsonObject = objectMapper.convertValue(json, Object.class);
        return ResponseEntity.ok(jsonObject);
    }
}
