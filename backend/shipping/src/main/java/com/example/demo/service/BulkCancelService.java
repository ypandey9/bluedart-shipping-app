package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.BulkCancelResponse;
import com.example.demo.dto.BulkCancelResult;
import com.example.demo.dto.CancelWaybillResponse;

@Service
public class BulkCancelService {

    private final WaybillCancellationService blueDartService;

    public BulkCancelService(WaybillCancellationService blueDartService) {
        this.blueDartService = blueDartService;
    }

    public BulkCancelResponse processExcel(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        List<BulkCancelResult> results = new ArrayList<>();
        int success = 0;
        int failed = 0;

        try (Workbook workbook =
                     WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            // Start from row 1 (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(0);
                if (cell == null) continue;

                String awbNo = formatter.formatCellValue(cell).trim();


                if (awbNo.isEmpty()) continue;

                try {
                    CancelWaybillResponse response =
                            blueDartService.cancelWaybill(awbNo);

                    boolean isError =
                            response.getCancelWaybillResult().getIsError();

                    String message =
                            response.getCancelWaybillResult()
                                    .getStatus().get(0).getStatusInformation();

                    if (!isError) {
                        success++;
                        results.add(new BulkCancelResult(
                                awbNo, "SUCCESS", message));
                    } else {
                        failed++;
                        results.add(new BulkCancelResult(
                                awbNo, "FAILED", message));
                    }

                } catch (Exception ex) {
                    failed++;
                    results.add(new BulkCancelResult(
                            awbNo, "FAILED", ex.getMessage()));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid Excel file", e);
        }

        return new BulkCancelResponse(
                results.size(), success, failed, results);
    }
}
