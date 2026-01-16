package com.example.demo.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.FailureRow;
import com.example.demo.model.WaybillRecord;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;



@Service
public class BulkWaybillExcelService {
 

    public byte[] generateSuccessExcel(List<WaybillRecord> records) throws Exception {
        // Implementation goes here
        Workbook wb= new XSSFWorkbook();
        Sheet sheet=wb.createSheet("Success");
        Row header=sheet.createRow(0);
        header.createCell(0).setCellValue("AWB No");
        header.createCell(1).setCellValue("Credit Reference");
        header.createCell(2).setCellValue("Date");

        int r=1;
        for(WaybillRecord record: records) {
            Row row=sheet.createRow(r++);
            row.createCell(0).setCellValue(record.getAwbNo());
            row.createCell(1).setCellValue(record.getCreditReferenceNo());
            row.createCell(2).setCellValue(record.getCreatedAt());
    }
    return write(wb);
    }

    public byte[] generateFailureExcel(List<FailureRow> failures) throws Exception {
        // Implementation goes here
        Workbook wb= new XSSFWorkbook();
        Sheet sheet=wb.createSheet("Failures");
        Row header=sheet.createRow(0);
        header.createCell(0).setCellValue("Row No");
        header.createCell(1).setCellValue("Credit Reference");
        header.createCell(2).setCellValue("Error Message");

        int r=1;
        for(FailureRow failure: failures) {
            Row row=sheet.createRow(r++);
            row.createCell(0).setCellValue(failure.getRowNo());
            row.createCell(1).setCellValue(failure.getReference());
            row.createCell(2).setCellValue(failure.getError());
    }
    return write(wb);
    }


private byte[] write(Workbook wb) throws Exception {
    ByteArrayOutputStream out=new ByteArrayOutputStream();
    wb.write(out);
    wb.close();
    return out.toByteArray();
    }
}
