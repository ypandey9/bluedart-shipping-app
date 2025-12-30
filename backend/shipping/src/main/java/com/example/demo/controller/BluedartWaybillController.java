package com.example.demo.controller;

import com.example.demo.repository.WaybillFileRepository;
import com.example.demo.service.BluedartWaybillService;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.WaybillRecord;
import com.example.demo.service.WaybillPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api/bluedart")
@CrossOrigin(origins = "*")
public class BluedartWaybillController {

    private final BluedartWaybillService waybillService;
    private final WaybillFileRepository repository;
    private final WaybillPdfService pdfService;   

    public BluedartWaybillController(BluedartWaybillService waybillService, WaybillFileRepository repository, WaybillPdfService pdfService) {
        this.waybillService = waybillService;
        this.repository = repository;
        this.pdfService = pdfService;
    }

    @PostMapping("/waybill")
public Map<String, Object> createWaybill(@RequestBody Map<String, Object> request) {

    System.out.println("✅ Backend received waybill request");
    System.out.println(request);

    return waybillService.generateWaybill(request);
}

    @GetMapping("/waybills")
    public List<WaybillRecord> getAllWaybills() {
        return repository.findAll();
    }


@GetMapping("/waybill/{awb}/pdf")
public ResponseEntity<byte[]> downloadPdf(@PathVariable String awb) throws Exception {

    WaybillRecord record = repository.findAll().stream()
            .filter(w -> w.getAwbNo().equals(awb))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Waybill not found"));

    byte[] pdf = pdfService.generatePdf(record);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=waybill-" + awb + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}

}