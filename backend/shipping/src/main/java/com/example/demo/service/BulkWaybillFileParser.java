package com.example.demo.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
public class BulkWaybillFileParser {

    public List<Map<String, Object>> parse(MultipartFile file) throws Exception {

        String filename = file.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".csv")) {
            return parseCsv(file);
        } else if (filename.endsWith(".xlsx")) {
            return parseXlsx(file); // (we add this later)
        }

        throw new IllegalArgumentException("Unsupported file type");
    }

    /* ================= CSV PARSING ================= */

    private List<Map<String, Object>> parseCsv(MultipartFile file) throws Exception {

        List<Map<String, Object>> requests = new ArrayList<>();

        CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
                .parse(new InputStreamReader(file.getInputStream()));

        for (CSVRecord row : parser) {

            Map<String, Object> request = buildWaybillRequestFromCsv(row);

            requests.add(request);
        }

        return requests;
    }


    private Map<String, Object> buildWaybillRequestFromCsv(CSVRecord row) {

    Map<String, Object> shipper = new HashMap<>();
    shipper.put("CustomerCode", row.get("CustomerCode"));
    shipper.put("CustomerName", row.get("CustomerName"));
    shipper.put("CustomerMobile", row.get("CustomerMobile"));
    shipper.put("CustomerAddress1", row.get("CustomerAddress1"));
    shipper.put("CustomerPincode", row.get("CustomerPincode"));
    shipper.put("OriginArea", row.get("OriginArea"));

    Map<String, Object> consignee = new HashMap<>();
    consignee.put("ConsigneeName", row.get("ConsigneeName"));
    consignee.put("ConsigneeMobile", row.get("ConsigneeMobile"));
    consignee.put("ConsigneeAddress1", row.get("ConsigneeAddress1"));
    consignee.put("ConsigneeAddress2", "Thsi is a test consinee addr2");
    consignee.put("ConsigneeAddress3", "Thsi is a test consinee addr3");
    consignee.put("ConsigneePincode", row.get("ConsigneePincode"));
    consignee.put("ConsigneeAttention", "ABCD");
    consignee.put("ConsigneeEmailID", "testemail@bluedart.com");

    Map<String, Object> services = new HashMap<>();
    services.put("SubProductCode", row.get("SubProductCode"));
    services.put("ProductCode", row.get("ProductCode"));
    services.put("ActualWeight", row.get("ActualWeight"));
    services.put("DeclaredValue", row.get("DeclaredValue"));
    services.put("PieceCount", row.get("PieceCount"));
    services.put("CollectableAmount", row.get("CollectableAmount"));
    services.put("CreditReferenceNo", row.get("CreditReferenceNo"));
    services.put("PickupDate",toBluedartDate(row.get("PickupDate")));
    services.put("PickupTime", "1600");
    services.put("ProductType", 1);
    services.put("RegisterPickup", true);
    services.put("PDFOutputNotRequired", true);

    services.put("PackType", "");
    services.put("PickupMode", "");
    services.put("PayableAt", "");
    services.put("ParcelShopCode", "");

    Map<String, Object> commodity = new HashMap<>();
    commodity.put("CommodityDetail1", "test1");
    commodity.put("CommodityDetail2", "test2");
    commodity.put("CommodityDetail3", "test3");

    services.put("Commodity", List.of(commodity));

    Map<String,Object> dimensions = new HashMap<>();
    dimensions.put("Length", "10");
    dimensions.put("Breadth", "10");
    dimensions.put("Height", "10");
    dimensions.put("Count", "1");
    services.put("Dimensions", List.of(dimensions));

    Map<String,Object> profile = new HashMap<>();
    profile.put("LoginId","GG940111");
    profile.put("LicenceKey", "kh7mnhqkmgegoksipxr0urmqesesseup");
    profile.put("Api_type", "S");

    Map<String, Object> request = new HashMap<>();
    request.put("Shipper", shipper);
    request.put("Consignee", consignee);
    request.put("Services", services);
    request.put("Profile", profile);

    return Map.of("Request", request);
}

private String toBluedartDate(String yyyyMMdd) {

    if (yyyyMMdd == null || yyyyMMdd.isBlank()) {
        throw new RuntimeException("PickupDate is mandatory");
    }

    LocalDate date = LocalDate.parse(yyyyMMdd);
    long millis = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

    return "/Date(" + millis + ")/";
}

    /* ================= XLSX PARSING ================= */

    private List<Map<String, Object>> parseXlsx(MultipartFile file) throws Exception {

    List<Map<String, Object>> requests = new ArrayList<>();

    Workbook workbook = new XSSFWorkbook(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0); // single sheet only

    Row headerRow = sheet.getRow(0);
    if (headerRow == null) {
        throw new RuntimeException("Excel header row is missing");
    }

    DataFormatter formatter = new DataFormatter(); // ⭐ KEY FIX

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) continue;

        Map<String, String> rowData = new HashMap<>();

        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
            Cell headerCell = headerRow.getCell(j);
            Cell cell = row.getCell(j);

            if (headerCell == null) continue;

            String key = headerCell.getStringCellValue().trim();
            String value = cell == null ? "" : formatter.formatCellValue(cell).trim();

            rowData.put(key, value);
        }

        requests.add(buildWaybillRequest(rowData));
    }

    workbook.close();
    return requests;
}



    /* ================= ROW → REQUEST ================= */
private Map<String, Object> buildWaybillRequest(Map<String, String> row) {

    /* ---------- SHIPPER ---------- */
    Map<String, Object> shipper = new HashMap<>();
    shipper.put("CustomerCode", row.get("CustomerCode"));
    shipper.put("CustomerName", row.get("CustomerName"));
    shipper.put("CustomerMobile", row.get("CustomerMobile"));
    shipper.put("CustomerAddress1", row.get("CustomerAddress1"));
    shipper.put("CustomerAddress2", "");
    shipper.put("CustomerAddress3", "");
    shipper.put("CustomerAddressinfo", "");
    shipper.put("CustomerPincode", row.get("CustomerPincode"));
    shipper.put("CustomerTelephone", "");
    shipper.put("CustomerEmailID", "test@bd.com");
    shipper.put("IsToPayCustomer", true);
    shipper.put("OriginArea", row.get("OriginArea"));
    shipper.put("Sender", "BulkUpload");
    shipper.put("VendorCode", "");

    /* ---------- CONSIGNEE ---------- */
    Map<String, Object> consignee = new HashMap<>();
    consignee.put("ConsigneeName", row.get("ConsigneeName"));
    consignee.put("ConsigneeMobile", row.get("ConsigneeMobile"));
    consignee.put("ConsigneeAddress1", row.get("ConsigneeAddress1"));
    consignee.put("ConsigneeAddress2", "");
    consignee.put("ConsigneeAddress3", "");
    consignee.put("ConsigneeAddressinfo", "");
    consignee.put("ConsigneePincode", row.get("ConsigneePincode"));
    consignee.put("ConsigneeTelephone", "");
    consignee.put("ConsigneeEmailID", "test@bd.com");
    consignee.put("ConsigneeAttention", "Bulk");
    consignee.put("AvailableDays", "");
    consignee.put("AvailableTiming", "");

    /* ---------- SERVICES ---------- */
    Map<String, Object> services = new HashMap<>();
    services.put("AWBNo", "");
    services.put("ProductCode", row.get("ProductCode"));
    services.put("SubProductCode", row.get("SubProductCode"));
    services.put("ProductType", 1);
    services.put("ActualWeight", row.get("ActualWeight"));
    services.put("DeclaredValue", Integer.parseInt(row.get("DeclaredValue")));
    services.put("PieceCount", row.get("PieceCount"));
    services.put("ItemCount", Integer.parseInt(row.get("PieceCount")));
    services.put("CollectableAmount", Integer.parseInt(row.get("CollectableAmount")));
    services.put("CreditReferenceNo", row.get("CreditReferenceNo"));
    services.put("CreditReferenceNo2", "");
    services.put("CreditReferenceNo3", "");

    services.put("PickupDate", toBluedartDate(row.get("PickupDate")));
    services.put("PickupTime", "1600");
    services.put("PickupMode", "");
    services.put("PickupType", "");
    services.put("RegisterPickup", true);

    services.put("PDFOutputNotRequired", true);
    services.put("PackType", "");
    services.put("ParcelShopCode", "");
    services.put("PayableAt", "");

    services.put("IsReversePickup", true);
    services.put("IsPartialPickup", false);
    services.put("IsForcePickup", false);
    services.put("IsDedicatedDeliveryNetwork", false);
    services.put("IsDutyTaxPaidByShipper", false);

    services.put("TotalCashPaytoCustomer", 0);
    services.put("Officecutofftime", "");
    services.put("PreferredPickupTimeSlot", "");
    services.put("DeliveryTimeSlot", "");
    services.put("ProductFeature", "");
    services.put("SpecialInstruction", "");
    services.put("noOfDCGiven", 0);

    /* ---------- COMMODITY ---------- */
    Map<String, Object> commodity = new HashMap<>();
    commodity.put("CommodityDetail1", "General Goods");
    commodity.put("CommodityDetail2", "");
    commodity.put("CommodityDetail3", "");
    services.put("Commodity", commodity);

    /* ---------- DIMENSIONS ---------- */
    Map<String, Object> dimension = new HashMap<>();
    dimension.put("Length", 10.0);
    dimension.put("Breadth", 10.0);
    dimension.put("Height", 10.0);
    dimension.put("Count", 1);
    services.put("Dimensions", List.of(dimension));

    /* ---------- ITEM ---------- */
    Map<String, Object> item = new HashMap<>();
    item.put("ItemName", row.get("ItemName"));
    item.put("ItemValue", Integer.parseInt(row.get("ItemValue")));
    item.put("Itemquantity", Integer.parseInt(row.get("Itemquantity")));
    item.put("TotalValue", Integer.parseInt(row.get("ItemValue")));
    item.put("InvoiceNumber", "");
    item.put("InvoiceDate", toBluedartDate(row.get("PickupDate")));
    services.put("itemdtl", List.of(item));

    /* ---------- REQUEST ---------- */
    Map<String, Object> request = new HashMap<>();
    request.put("Shipper", shipper);
    request.put("Consignee", consignee);
    request.put("Services", services);

    /* ---------- PROFILE ---------- */
    Map<String, Object> profile = new HashMap<>();
    profile.put("LoginID", "GG940111");
    profile.put("LicenceKey", "kh7mnhqkmgegoksipxr0urmqesesseup");
    profile.put("Api_type", "S");

    /* ---------- FINAL PAYLOAD ---------- */
    Map<String, Object> finalPayload = new HashMap<>();
    finalPayload.put("Request", request);
    finalPayload.put("Profile", profile);

    return finalPayload;
}
}
