package com.tacticboard.webapp.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfUtils {

    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

    /**
     * Adds a header row to a PDF table
     * 
     * @param table The table to add headers to
     * @param headers The header text values
     */
    public static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
    
    /**
     * Creates a cell with centered content
     * 
     * @param content The content for the cell
     * @return The created PdfPCell
     */
    public static PdfPCell createCenteredCell(String content) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        return cell;
    }
    
    /**
     * Creates a cell with right-aligned content
     * 
     * @param content The content for the cell
     * @return The created PdfPCell
     */
    public static PdfPCell createRightAlignedCell(String content) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        return cell;
    }
    
    /**
     * Creates a cell with a specific background color
     * 
     * @param content The content for the cell
     * @param color The background color
     * @return The created PdfPCell
     */
    public static PdfPCell createColoredCell(String content, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setBackgroundColor(color);
        return cell;
    }
    
    /**
     * Creates a cell that spans multiple columns
     * 
     * @param content The content for the cell
     * @param colspan The number of columns to span
     * @return The created PdfPCell
     */
    public static PdfPCell createSpannedCell(String content, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setColspan(colspan);
        return cell;
    }
}