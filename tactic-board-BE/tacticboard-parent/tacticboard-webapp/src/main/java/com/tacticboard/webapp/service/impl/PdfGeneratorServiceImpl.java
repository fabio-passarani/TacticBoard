package com.tacticboard.webapp.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tacticboard.core.model.entity.team.Player;
import com.tacticboard.core.model.entity.team.Team;
import com.tacticboard.core.model.entity.training.TrainingPlan;
import com.tacticboard.core.service.PdfGeneratorService;
import com.tacticboard.webapp.util.PdfUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12);
    
    @Override
    public byte[] generateTeamReport(Team team, List<Player> players) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Team Report: " + team.getName(), TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add team logo if available
            if (team.getLogoUrl() != null && !team.getLogoUrl().isEmpty()) {
                try {
                    Image logo = Image.getInstance(team.getLogoUrl());
                    logo.setAlignment(Element.ALIGN_CENTER);
                    logo.scaleToFit(100, 100);
                    document.add(logo);
                } catch (IOException e) {
                    log.warn("Could not add team logo to PDF", e);
                }
            }
            
            // Add team details
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Team Details", SUBTITLE_FONT));
            document.add(new Paragraph("Name: " + team.getName(), NORMAL_FONT));
            document.add(new Paragraph("Category: " + team.getCategory(), NORMAL_FONT));
            document.add(new Paragraph("Season: " + team.getSeason(), NORMAL_FONT));
            document.add(new Paragraph("Coach: " + team.getOwner().getFirstName() + " " + team.getOwner().getLastName(), NORMAL_FONT));
            
            // Add players table
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Players", SUBTITLE_FONT));
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Add table headers
            PdfUtils.addTableHeader(table, "Name", "Position", "Number", "Age", "Status");
            
            // Add player rows
            for (Player player : players) {
                PdfPCell nameCell = new PdfPCell(new Paragraph(player.getFirstName() + " " + player.getLastName(), NORMAL_FONT));
                PdfPCell positionCell = new PdfPCell(new Paragraph(player.getPosition(), NORMAL_FONT));
                PdfPCell numberCell = new PdfPCell(new Paragraph(player.getNumber() != null ? player.getNumber().toString() : "", NORMAL_FONT));
                PdfPCell ageCell = new PdfPCell(new Paragraph(player.getAge() != null ? player.getAge().toString() : "", NORMAL_FONT));
                PdfPCell statusCell = new PdfPCell(new Paragraph(player.isActive() ? "Active" : "Inactive", NORMAL_FONT));
                
                table.addCell(nameCell);
                table.addCell(positionCell);
                table.addCell(numberCell);
                table.addCell(ageCell);
                table.addCell(statusCell);
            }
            
            document.add(table);
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (DocumentException e) {
            log.error("Error generating team report PDF", e);
            throw new RuntimeException("Failed to generate team report PDF", e);
        }
    }

    @Override
    public byte[] generateTrainingPlanPdf(TrainingPlan trainingPlan) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Training Plan: " + trainingPlan.getTitle(), TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add training plan details
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Plan Details", SUBTITLE_FONT));
            document.add(new Paragraph("Title: " + trainingPlan.getTitle(), NORMAL_FONT));
            document.add(new Paragraph("Team: " + trainingPlan.getTeam().getName(), NORMAL_FONT));
            document.add(new Paragraph("Date: " + trainingPlan.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE), NORMAL_FONT));
            document.add(new Paragraph("Duration: " + trainingPlan.getDuration() + " minutes", NORMAL_FONT));
            
            // Add objectives
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Objectives", SUBTITLE_FONT));
            document.add(new Paragraph(trainingPlan.getObjectives(), NORMAL_FONT));
            
            // Add notes if available
            if (trainingPlan.getNotes() != null && !trainingPlan.getNotes().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Notes", SUBTITLE_FONT));
                document.add(new Paragraph(trainingPlan.getNotes(), NORMAL_FONT));
            }
            
            // Add sessions table if available
            if (trainingPlan.getSessions() != null && !trainingPlan.getSessions().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Sessions", SUBTITLE_FONT));
                
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                
                // Add table headers
                PdfUtils.addTableHeader(table, "Title", "Type", "Duration", "Description");
                
                // Add session rows
                trainingPlan.getSessions().forEach(session -> {
                    table.addCell(new PdfPCell(new Paragraph(session.getTitle(), NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Paragraph(session.getType(), NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Paragraph(session.getDuration() + " min", NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Paragraph(session.getDescription(), NORMAL_FONT)));
                });
                
                document.add(table);
            }
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (DocumentException e) {
            log.error("Error generating training plan PDF", e);
            throw new RuntimeException("Failed to generate training plan PDF", e);
        }
    }
}