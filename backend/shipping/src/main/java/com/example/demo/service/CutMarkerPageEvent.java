package com.example.demo.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class CutMarkerPageEvent extends PdfPageEventHelper {

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfContentByte canvas = writer.getDirectContent();
        Rectangle page = document.getPageSize();

        float left = document.left();
        float right = document.right();
        float top = document.top();
        float bottom = document.bottom();

        float midX = (left + right) / 2;
        float midY = (top + bottom) / 2;

        canvas.saveState();

        // Thin dashed line
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(4f, 4f);
        canvas.setColorStroke(BaseColor.GRAY);

        // Vertical cut line
        canvas.moveTo(midX, bottom);
        canvas.lineTo(midX, top);

        // Horizontal cut line
        canvas.moveTo(left, midY);
        canvas.lineTo(right, midY);

        canvas.stroke();
        canvas.restoreState();
    }
}
