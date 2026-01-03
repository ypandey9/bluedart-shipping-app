package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class BulkWaybillTemplateService {

    public byte[] generateTemplate() throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Waybills");

        String[] headers = {
            "CustomerCode","CustomerName","CustomerMobile","CustomerEmailID",
            "CustomerAddress1","CustomerAddress2","CustomerAddress3","CustomerPincode",
            "OriginArea","IsToPayCustomer",

            "ConsigneeName","ConsigneeMobile","ConsigneeEmailID",
            "ConsigneeAddress1","ConsigneeAddress2","ConsigneeAddress3",
            "ConsigneePincode","ConsigneeAttention",

            "SubProductCode","ProductCode","ActualWeight","DeclaredValue",
            "PieceCount","CollectableAmount","PickupDate","PickupTime",
            "CreditReferenceNo",

            "ItemName","ItemValue","Itemquantity","HSCode",

            "ReturnAddress1","ReturnAddress2","ReturnAddress3",
            "ReturnMobile","ReturnPincode"
        };

        // Header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.autoSizeColumn(i);
        }

        // Sample row
        Row sample = sheet.createRow(1);
        sample.createCell(0).setCellValue("940111");
        sample.createCell(1).setCellValue("Test Cust Name");
        sample.createCell(2).setCellValue("9996665554");
        sample.createCell(4).setCellValue("Test Cust Addr1");
        sample.createCell(7).setCellValue("122002");
        sample.createCell(8).setCellValue("GGN");
        sample.createCell(10).setCellValue("Test Consignee");
        sample.createCell(11).setCellValue("9995554441");
        sample.createCell(13).setCellValue("Test Cngee Addr1");
        sample.createCell(16).setCellValue("110027");
        sample.createCell(18).setCellValue("P");
        sample.createCell(19).setCellValue("A");
        sample.createCell(20).setCellValue("0.50");
        sample.createCell(21).setCellValue("100");
        sample.createCell(22).setCellValue("1");
        sample.createCell(23).setCellValue("0");
        sample.createCell(24).setCellValue("2026-01-02");
        sample.createCell(25).setCellValue("1600");
        sample.createCell(26).setCellValue("CR12345");
        sample.createCell(27).setCellValue("Test Item1");
        sample.createCell(28).setCellValue("100");
        sample.createCell(29).setCellValue("1");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
