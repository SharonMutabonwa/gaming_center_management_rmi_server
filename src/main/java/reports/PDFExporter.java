/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reports;

/**
 *
 * @author Odeth
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDFExporter - Generates PDF reports using iText
 */
public class PDFExporter {
    private static final Logger logger = LoggerFactory.getLogger(PDFExporter.class);

    // Colors and Fonts
    private static final BaseColor HEADER_COLOR = new BaseColor(102, 126, 234);
    private static final BaseColor ROW_COLOR_1 = new BaseColor(248, 248, 248);
    private static final BaseColor ROW_COLOR_2 = BaseColor.WHITE;
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);

    /**
     * Generate Customer Report
     */
    public static byte[] generateCustomerReport(List<Customer> customers) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            // Add header/footer
            writer.setPageEvent(new HeaderFooter());
            
            document.open();

            // Title
            Paragraph title = new Paragraph("Customer Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Generated date
            Paragraph date = new Paragraph(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                NORMAL_FONT
            );
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Summary
            Paragraph summary = new Paragraph("Total Customers: " + customers.size(), NORMAL_FONT);
            summary.setSpacingAfter(15);
            document.add(summary);

            // Create table
            PdfPTable table = new PdfPTable(6); // 6 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            
            float[] columnWidths = {1f, 2f, 2f, 1.5f, 2f, 1.5f};
            table.setWidths(columnWidths);

            // Add headers
            addTableHeader(table, new String[]{"ID", "Name", "Email", "Gender", "Phone", "Balance (RWF)"});

            // Add data rows
            boolean alternateRow = false;
            for (Customer customer : customers) {
                addTableCell(table, String.valueOf(customer.getCustomerId()), alternateRow);
                addTableCell(table, customer.getFullName(), alternateRow);
                addTableCell(table, customer.getUser().getEmail(), alternateRow);
                addTableCell(table, customer.getGender().toString(), alternateRow);
                addTableCell(table, customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A", alternateRow);
                addTableCell(table, customer.getAccountBalance().toString(), alternateRow);
                alternateRow = !alternateRow;
            }

            document.add(table);

            // Statistics
            document.add(new Paragraph(" "));
            Paragraph stats = new Paragraph("Report Statistics:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            stats.setSpacingBefore(20);
            document.add(stats);
            
            double totalBalance = customers.stream()
                .mapToDouble(c -> c.getAccountBalance().doubleValue())
                .sum();
            
            document.add(new Paragraph("Total Account Balance: " + String.format("%.2f", totalBalance) + " RWF", NORMAL_FONT));

            document.close();
            logger.info("Customer PDF report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating customer PDF report", e);
            return null;
        }
    }

    /**
     * Generate Booking Report
     */
    public static byte[] generateBookingReport(List<Booking> bookings) {
        try {
            Document document = new Document(PageSize.A4.rotate()); // Landscape
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooter());
            
            document.open();

            // Title
            Paragraph title = new Paragraph("Booking Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Generated date
            Paragraph date = new Paragraph(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                NORMAL_FONT
            );
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Summary
            Paragraph summary = new Paragraph("Total Bookings: " + bookings.size(), NORMAL_FONT);
            summary.setSpacingAfter(15);
            document.add(summary);

            // Create table
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            float[] columnWidths = {0.8f, 2f, 2f, 1.5f, 1.2f, 1.2f, 1.2f, 1.5f};
            table.setWidths(columnWidths);

            // Add headers
            addTableHeader(table, new String[]{"ID", "Customer", "Station", "Date", "Start", "End", "Duration", "Amount (RWF)"});

            // Add data rows
            boolean alternateRow = false;
            double totalRevenue = 0;
            
            for (Booking booking : bookings) {
                addTableCell(table, String.valueOf(booking.getBookingId()), alternateRow);
                addTableCell(table, booking.getCustomer().getFullName(), alternateRow);
                addTableCell(table, booking.getGamingStation().getStationName(), alternateRow);
                addTableCell(table, booking.getBookingDate().toString(), alternateRow);
                addTableCell(table, booking.getStartTime().toString(), alternateRow);
                addTableCell(table, booking.getEndTime().toString(), alternateRow);
                addTableCell(table, booking.getDurationHours().toString() + "h", alternateRow);
                addTableCell(table, booking.getTotalAmount().toString(), alternateRow);
                
                totalRevenue += booking.getTotalAmount().doubleValue();
                alternateRow = !alternateRow;
            }

            document.add(table);

            // Statistics
            document.add(new Paragraph(" "));
            Paragraph stats = new Paragraph("Report Statistics:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            stats.setSpacingBefore(20);
            document.add(stats);
            document.add(new Paragraph("Total Revenue: " + String.format("%.2f", totalRevenue) + " RWF", NORMAL_FONT));

            document.close();
            logger.info("Booking PDF report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating booking PDF report", e);
            return null;
        }
    }

    /**
     * Generate Revenue Report
     */
    public static byte[] generateRevenueReport(List<Transaction> transactions) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooter());
            
            document.open();

            // Title
            Paragraph title = new Paragraph("Revenue Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Generated date
            Paragraph date = new Paragraph(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                NORMAL_FONT
            );
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Create table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            float[] columnWidths = {1f, 2f, 2f, 1.5f, 1.5f, 2f};
            table.setWidths(columnWidths);

            // Add headers
            addTableHeader(table, new String[]{"ID", "Customer", "Type", "Amount (RWF)", "Method", "Date"});

            // Add data rows
            boolean alternateRow = false;
            double totalRevenue = 0;
            
            for (Transaction transaction : transactions) {
                addTableCell(table, String.valueOf(transaction.getTransactionId()), alternateRow);
                addTableCell(table, transaction.getCustomer().getFullName(), alternateRow);
                addTableCell(table, transaction.getTransactionType().toString().replace("_", " "), alternateRow);
                addTableCell(table, transaction.getAmount().toString(), alternateRow);
                addTableCell(table, transaction.getPaymentMethod().toString(), alternateRow);
                addTableCell(table, transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), alternateRow);
                
                // Only count revenue transactions (not deposits)
                if (transaction.getTransactionType() == TransactionType.BOOKING_PAYMENT ||
                    transaction.getTransactionType() == TransactionType.TOURNAMENT_FEE ||
                    transaction.getTransactionType() == TransactionType.MEMBERSHIP_FEE) {
                    totalRevenue += transaction.getAmount().doubleValue();
                }
                
                alternateRow = !alternateRow;
            }

            document.add(table);

            // Statistics
            document.add(new Paragraph(" "));
            Paragraph stats = new Paragraph("Report Statistics:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            stats.setSpacingBefore(20);
            document.add(stats);
            
            document.add(new Paragraph("Total Transactions: " + transactions.size(), NORMAL_FONT));
            document.add(new Paragraph("Total Revenue: " + String.format("%.2f", totalRevenue) + " RWF", NORMAL_FONT));

            document.close();
            logger.info("Revenue PDF report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating revenue PDF report", e);
            return null;
        }
    }

    /**
     * Generate Station Report
     */
    public static byte[] generateStationReport(List<GamingStation> stations) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooter());
            
            document.open();

            // Title
            Paragraph title = new Paragraph("Gaming Station Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Generated date
            Paragraph date = new Paragraph(
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                NORMAL_FONT
            );
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            // Summary
            Paragraph summary = new Paragraph("Total Stations: " + stations.size(), NORMAL_FONT);
            summary.setSpacingAfter(15);
            document.add(summary);

            // Create table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            float[] columnWidths = {1f, 2f, 1.5f, 1.5f, 1.5f};
            table.setWidths(columnWidths);

            // Add headers
            addTableHeader(table, new String[]{"ID", "Name", "Type", "Hourly Rate (RWF)", "Status"});

            // Add data rows
            boolean alternateRow = false;
            for (GamingStation station : stations) {
                addTableCell(table, String.valueOf(station.getStationId()), alternateRow);
                addTableCell(table, station.getStationName(), alternateRow);
                addTableCell(table, station.getStationType().toString(), alternateRow);
                addTableCell(table, station.getHourlyRate().toString(), alternateRow);
                addTableCell(table, station.getStatus().toString().replace("_", " "), alternateRow);
                alternateRow = !alternateRow;
            }

            document.add(table);

            // Statistics
            document.add(new Paragraph(" "));
            Paragraph stats = new Paragraph("Station Statistics:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            stats.setSpacingBefore(20);
            document.add(stats);
            
            long availableStations = stations.stream()
                .filter(s -> s.getStatus() == StationStatus.AVAILABLE)
                .count();
            
            document.add(new Paragraph("Available Stations: " + availableStations, NORMAL_FONT));
            document.add(new Paragraph("Occupied Stations: " + 
                stations.stream().filter(s -> s.getStatus() == StationStatus.OCCUPIED).count(), NORMAL_FONT));

            document.close();
            logger.info("Station PDF report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating station PDF report", e);
            return null;
        }
    }

    // Helper methods
    private static void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private static void addTableCell(PdfPTable table, String content, boolean alternateColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, NORMAL_FONT));
        cell.setBackgroundColor(alternateColor ? ROW_COLOR_1 : ROW_COLOR_2);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    // Header/Footer event handler
    static class HeaderFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            // Footer
            Phrase footer = new Phrase(
                "Gaming Center Management System | Page " + writer.getPageNumber(),
                new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY)
            );
            
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                (document.right() - document.left()) / 2 + document.leftMargin(),
                document.bottom() - 10, 0);
        }
    }
}
