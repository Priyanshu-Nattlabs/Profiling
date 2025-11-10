package com.profiling.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.profiling.model.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {

    public byte[] generateProfilePDF(Profile profile, String templateText) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile must not be null");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            document.add(new Paragraph(defaultValue(templateText)));

            document.close();

            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate profile PDF", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred during PDF generation", e);
        }
    }

    private String defaultValue(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }
        return value;
    }
}


