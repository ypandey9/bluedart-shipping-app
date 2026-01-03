package com.example.demo.controller;

import com.example.demo.repository.WaybillFileRepository;
import com.example.demo.service.BluedartWaybillService;
import com.example.demo.service.BulkWaybillTemplateService;
import com.example.demo.service.BulkWaybillFileParser;
import org.springframework.web.multipart.MultipartFile; 

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
    private final BulkWaybillTemplateService templateService;   
    private final BulkWaybillFileParser bulkFileParser;
    
    public BluedartWaybillController(BluedartWaybillService waybillService, WaybillFileRepository repository, 
        WaybillPdfService pdfService,BulkWaybillTemplateService  templateService, BulkWaybillFileParser bulkFileParser) {
        this.waybillService = waybillService;
        this.repository = repository;
        this.pdfService = pdfService;
        this.templateService=templateService;
        this.bulkFileParser=bulkFileParser;
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
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String awb, @RequestParam(defaultValue = "A4") String size) throws Exception {

        WaybillRecord record = repository.findByAwbNo(awb);
if (record == null) {
    throw new RuntimeException("Waybill not found");
}

        byte[] pdf = pdfService.generatePdf(record, size);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=waybill-" + awb + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @PostMapping("/waybill/bulk")    
    public ResponseEntity<byte[]>uploadBulkWaybill(
        @RequestParam("file") MultipartFile file,
        @RequestParam(defaultValue="A4") String size
    ) throws Exception {
        List<Map<String,Object>> requests=bulkFileParser.parse(file);
        List<WaybillRecord> records=waybillService.generateBulkWaybills(requests);
        byte[] pdf=pdfService.generateBulkPdf(records, size);
        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=bulk-waybills.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
    }


@GetMapping("/waybill/bulk/template")
public ResponseEntity<byte[]> downloadTemplate() throws Exception {

    byte[] file = templateService.generateTemplate();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=Bluedart_Bulk_Waybill_Template.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
}

}