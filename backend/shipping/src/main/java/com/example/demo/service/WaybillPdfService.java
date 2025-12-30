package com.example.demo.service;

import com.example.demo.model.WaybillRecord;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
@Service
public class WaybillPdfService {

    private static final DateTimeFormatter OUTPUT_DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @SuppressWarnings("unchecked")
    public byte[] generatePdf(WaybillRecord record) throws Exception {

        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        /* ---------- Fonts ---------- */
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        /* ---------- Parent Grid (2 x 2) ---------- */
        PdfPTable grid = new PdfPTable(2);
        grid.setWidthPercentage(100);
        grid.setWidths(new float[]{1f, 1f});

        for (int i = 0; i < 4; i++) {
            PdfPTable waybill = createWaybillBlock(
                    record, titleFont, sectionFont, labelFont, valueFont
            );

            PdfPCell cell = new PdfPCell(waybill);
            cell.setPadding(6);
            cell.setBorderWidth(0.5f);
            cell.setBorderColor(BaseColor.GRAY);
            grid.addCell(cell);
        }

        document.add(grid);
        document.close();
        return out.toByteArray();
    }

    /* ================= ONE WAYBILL COPY ================= */

    @SuppressWarnings("unchecked")
    private PdfPTable createWaybillBlock(
            WaybillRecord record,
            Font titleFont,
            Font sectionFont,
            Font labelFont,
            Font valueFont
    ) throws Exception {

        PdfPTable block = new PdfPTable(1);
        block.setWidthPercentage(100);

        /* ---------- Title ---------- */
        PdfPCell titleCell = new PdfPCell(
                new Paragraph("SHIPPING WAYBILL", titleFont)
        );
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(4);
        titleCell.setBorder(Rectangle.NO_BORDER);
        block.addCell(titleCell);

        /* ---------- Extract Data ---------- */
        Map<String, Object> root = record.getRequest();
        Map<String, Object> request = (Map<String, Object>) root.get("Request");
        Map<String, Object> shipper = (Map<String, Object>) request.get("Shipper");
        Map<String, Object> consignee = (Map<String, Object>) request.get("Consignee");
        Map<String, Object> services = (Map<String, Object>) request.get("Services");

        /* ---------- Meta ---------- */
        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.setWidths(new float[]{1f, 2f});

        addCell(meta, "AWB No", labelFont);
        addCell(meta, record.getAwbNo(), valueFont);
        addCell(meta, "Reference", labelFont);
        addCell(meta, record.getCreditReferenceNo(), valueFont);
        addCell(meta, "Pickup Date", labelFont);
        addCell(meta, formatDate(record.getCreatedAt()), valueFont);

        PdfPCell metaCell = new PdfPCell(meta);
        metaCell.setBorder(Rectangle.NO_BORDER);
        metaCell.setPadding(2);
        block.addCell(metaCell);

        /* ---------- Shipper / Consignee ---------- */
        PdfPTable party = new PdfPTable(2);
        party.setWidthPercentage(100);

        party.addCell(sectionCell("SHIPPER", sectionFont));
        party.addCell(sectionCell("CONSIGNEE", sectionFont));

        party.addCell(detailsCell(shipper, valueFont));
        party.addCell(detailsCell(consignee, valueFont));

        block.addCell(party);

        /* ---------- Services ---------- */
        PdfPTable service = new PdfPTable(1);
        service.setWidthPercentage(100);

        service.addCell(sectionCell("SERVICES", sectionFont));
        service.addCell(serviceDetailsCell(services, valueFont));

        block.addCell(service);

        /* ---------- Barcode ---------- */
        Image barcode = barcodeImage(record.getAwbNo());
        barcode.scaleToFit(200, 50);
        barcode.setAlignment(Image.ALIGN_CENTER);

        PdfPCell barcodeCell = new PdfPCell(barcode);
        barcodeCell.setBorder(Rectangle.NO_BORDER);
        barcodeCell.setPadding(4);
        block.addCell(barcodeCell);

        return block;
    }

    /* ================= HELPERS ================= */

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private PdfPCell sectionCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell detailsCell(Map<String, Object> map, Font font) {
        String text =
                safe(map, "CustomerName") + "\n" +
                "Mob: " + safe(map, "CustomerMobile") + "\n" +
                safe(map, "CustomerAddress1") + ", " +
                safe(map, "CustomerCity") + " - " +
                safe(map, "CustomerPincode");

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell serviceDetailsCell(Map<String, Object> map, Font font) {
        String text =
                "Weight: " + safe(map, "ActualWeight") + "\n" +
                "DeclaredValue: " + safe(map, "DeclaredValue") + "\n" +
                "PieceCount: " + safe(map, "PieceCount") + "\n " +
                "ItemName: " + getItemName(map) + " \n " +
                "CollectableAmount: " + safe(map, "CollectableAmount");

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        return cell;
    }


    @SuppressWarnings("unchecked")
private String getItemName(Map<String, Object> services) {

    Object itemDtlObj = services.get("itemdtl");

    if (!(itemDtlObj instanceof List)) {
        return "NA";
    }

    List<Map<String, Object>> itemList =
            (List<Map<String, Object>>) itemDtlObj;

    if (itemList.isEmpty()) {
        return "NA";
    }

    Object itemName = itemList.get(0).get("ItemName");
    return itemName == null ? "NA" : itemName.toString();
}


    private Image barcodeImage(String text) throws Exception {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.CODE_128, 300, 60);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "png", baos);
        return Image.getInstance(baos.toByteArray());
    }

    private String formatDate(String date) {
        try {
            return LocalDateTime.parse(date).format(OUTPUT_DATE);
        } catch (Exception e) {
            try {
                return LocalDate.parse(date).format(OUTPUT_DATE);
            } catch (Exception ex) {
                return date;
            }
        }
    }

    private String safe(Map<String, Object> map, String key) {
        if (map == null) return "NA";
        Object val = map.get(key);
        return val == null ? "NA" : val.toString();
    }
}
