package com.ims.nslmotors.services.impl;

import com.ims.nslmotors.services.IPdfService;
import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class PdfServiceImpl implements IPdfService {

    @Override
    public byte[] generatePdfFromHtml(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Create ITextRenderer instance
            ITextRenderer renderer = new ITextRenderer();

            // Add Windows system font (Arial) for Turkish character support
            // Arial is available on Windows and supports Turkish characters
            String fontPath = "C:/Windows/Fonts/arial.ttf";
            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Set the HTML content
            renderer.setDocumentFromString(htmlContent);

            // Layout the document
            renderer.layout();

            // Create PDF
            renderer.createPDF(outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("PDF generated successfully, size: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error generating PDF from HTML: {}", e.getMessage(), e);
            throw new RuntimeException("PDF oluşturulamadı: " + e.getMessage(), e);
        }
    }
}
