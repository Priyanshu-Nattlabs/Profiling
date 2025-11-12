package com.profiling.service;

import com.profiling.model.Profile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PDFService {
    
    public byte[] generateProfilePDF(Profile profile, String templateText) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile must not be null");
        }
        
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            float margin = 50;
            float lineHeight = 20;
            float titleFontSize = 18;
            float headingFontSize = 14;
            float bodyFontSize = 11;
            
            PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font headingFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            
            PDPage currentPage = new PDPage(PDRectangle.A4);
            document.addPage(currentPage);
            PDPageContentStream contentStream = new PDPageContentStream(document, currentPage);
            float yPosition = currentPage.getMediaBox().getHeight() - margin;
            float maxWidth = currentPage.getMediaBox().getWidth() - (2 * margin);
            
            try {
                // Title
                String title = profile.getTemplateType() != null && 
                              profile.getTemplateType().toLowerCase().equals("cover") 
                              ? "Cover Letter" : "Profile";
                contentStream.beginText();
                contentStream.setFont(titleFont, titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= lineHeight * 1.5f;
                
                // If template text is provided, use it
                if (templateText != null && !templateText.trim().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(bodyFont, bodyFontSize);
                    contentStream.newLineAtOffset(margin, yPosition);
                    
                    String[] lines = templateText.split("\n");
                    for (String line : lines) {
                        if (yPosition < margin + lineHeight) {
                            break; // Stop if we run out of space
                        }
                        
                        // Handle long lines by wrapping
                        String[] wrappedLines = wrapText(line, bodyFont, bodyFontSize, maxWidth);
                        
                        for (String wrappedLine : wrappedLines) {
                            if (yPosition < margin + lineHeight) {
                                break;
                            }
                            
                            contentStream.showText(wrappedLine);
                            yPosition -= lineHeight;
                            contentStream.newLineAtOffset(0, -lineHeight);
                        }
                    }
                    contentStream.endText();
                } else {
                    // Generate profile content from profile data
                    yPosition = addSection(contentStream, "Personal Information", headingFont, headingFontSize, 
                                         margin, yPosition, lineHeight);
                    
                    yPosition = addField(contentStream, "Name", profile.getName(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Email", profile.getEmail(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Date of Birth", profile.getDob(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "LinkedIn", profile.getLinkedin(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    
                    yPosition = addSection(contentStream, "Education", headingFont, headingFontSize,
                                         margin, yPosition, lineHeight);
                    
                    yPosition = addField(contentStream, "Institute", profile.getInstitute(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Degree", profile.getCurrentDegree(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Branch", profile.getBranch(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Year of Study", profile.getYearOfStudy(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    
                    if (profile.getCertifications() != null && !profile.getCertifications().trim().isEmpty()) {
                        yPosition = addSection(contentStream, "Certifications", headingFont, headingFontSize,
                                             margin, yPosition, lineHeight);
                        yPosition = addField(contentStream, "", profile.getCertifications(), bodyFont, bodyFontSize,
                                           margin, yPosition, lineHeight, maxWidth);
                    }
                    
                    if (profile.getAchievements() != null && !profile.getAchievements().trim().isEmpty()) {
                        yPosition = addSection(contentStream, "Achievements", headingFont, headingFontSize,
                                             margin, yPosition, lineHeight);
                        yPosition = addField(contentStream, "", profile.getAchievements(), bodyFont, bodyFontSize,
                                           margin, yPosition, lineHeight, maxWidth);
                    }
                    
                    yPosition = addSection(contentStream, "Skills", headingFont, headingFontSize,
                                         margin, yPosition, lineHeight);
                    
                    yPosition = addField(contentStream, "Technical Skills", profile.getTechnicalSkills(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    yPosition = addField(contentStream, "Soft Skills", profile.getSoftSkills(), bodyFont, bodyFontSize,
                                       margin, yPosition, lineHeight, maxWidth);
                    
                    if (profile.getHasInternship() != null && profile.getHasInternship() && 
                        profile.getInternshipDetails() != null && !profile.getInternshipDetails().trim().isEmpty()) {
                        yPosition = addSection(contentStream, "Internship", headingFont, headingFontSize,
                                             margin, yPosition, lineHeight);
                        yPosition = addField(contentStream, "", profile.getInternshipDetails(), bodyFont, bodyFontSize,
                                           margin, yPosition, lineHeight, maxWidth);
                    }
                    
                    if (profile.getHasExperience() != null && profile.getHasExperience() && 
                        profile.getExperienceDetails() != null && !profile.getExperienceDetails().trim().isEmpty()) {
                        yPosition = addSection(contentStream, "Experience", headingFont, headingFontSize,
                                             margin, yPosition, lineHeight);
                        yPosition = addField(contentStream, "", profile.getExperienceDetails(), bodyFont, bodyFontSize,
                                           margin, yPosition, lineHeight, maxWidth);
                    }
                }
            } finally {
                contentStream.close();
            }
            
            document.save(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF document", e);
        }
    }
    
    private float addSection(PDPageContentStream contentStream, String sectionName,
                            PDType1Font font, float fontSize,
                            float margin, float yPosition, float lineHeight) throws IOException {
        if (yPosition < margin + lineHeight * 2) {
            return yPosition; // Not enough space
        }
        
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(sectionName);
        contentStream.endText();
        
        return yPosition - lineHeight * 1.5f;
    }
    
    private float addField(PDPageContentStream contentStream, String label, String value,
                          PDType1Font font, float fontSize,
                          float margin, float yPosition, float lineHeight, float maxWidth) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            return yPosition;
        }
        
        String text = label.isEmpty() ? value : (label + ": " + value);
        String[] lines = wrapText(text, font, fontSize, maxWidth);
        
        for (String line : lines) {
            if (yPosition < margin + lineHeight) {
                return yPosition; // Not enough space
            }
            
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(line);
            contentStream.endText();
            yPosition -= lineHeight;
        }
        
        return yPosition;
    }
    
    private String[] wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        // Simple text wrapping - split by words and measure
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;
            
            if (width > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
}
