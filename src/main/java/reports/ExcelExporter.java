/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reports;

/**
 *
 * @author Odeth
 */


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.*;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ExcelExporter - Generates Excel reports using Apache POI
 */
public class ExcelExporter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelExporter.class);

    /**
     * Generate Customer Report Excel
     */
    public static byte[] generateCustomerReport(List<Customer> customers) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Customers");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "First Name", "Last Name", "Email", "Phone", "Gender", "DOB", "Balance (RWF)", "Hours Played"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Customer customer : customers) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, customer.getCustomerId(), dataStyle);
                createCell(row, 1, customer.getFirstName(), dataStyle);
                createCell(row, 2, customer.getLastName(), dataStyle);
                createCell(row, 3, customer.getUser().getEmail(), dataStyle);
                createCell(row, 4, customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A", dataStyle);
                createCell(row, 5, customer.getGender().toString(), dataStyle);
                createCell(row, 6, customer.getDateOfBirth().toString(), dataStyle);
                createCell(row, 7, customer.getAccountBalance().doubleValue(), dataStyle);
                createCell(row, 8, customer.getTotalHoursPlayed(), dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            logger.info("Customer Excel report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating customer Excel report", e);
            return null;
        }
    }

    /**
     * Generate Booking Report Excel
     */
    public static byte[] generateBookingReport(List<Booking> bookings) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Bookings");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Customer", "Station", "Date", "Start Time", "End Time", "Duration (hrs)", "Amount (RWF)", "Status"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, booking.getBookingId(), dataStyle);
                createCell(row, 1, booking.getCustomer().getFullName(), dataStyle);
                createCell(row, 2, booking.getGamingStation().getStationName(), dataStyle);
                createCell(row, 3, booking.getBookingDate().toString(), dataStyle);
                createCell(row, 4, booking.getStartTime().toString(), dataStyle);
                createCell(row, 5, booking.getEndTime().toString(), dataStyle);
                createCell(row, 6, booking.getDurationHours().doubleValue(), dataStyle);
                createCell(row, 7, booking.getTotalAmount().doubleValue(), dataStyle);
                createCell(row, 8, booking.getStatus().toString(), dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            logger.info("Booking Excel report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating booking Excel report", e);
            return null;
        }
    }

    /**
     * Generate Revenue Report Excel
     */
    public static byte[] generateRevenueReport(List<Transaction> transactions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Revenue");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Customer", "Type", "Amount (RWF)", "Payment Method", "Date", "Reference ID"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, transaction.getTransactionId(), dataStyle);
                createCell(row, 1, transaction.getCustomer().getFullName(), dataStyle);
                createCell(row, 2, transaction.getTransactionType().toString().replace("_", " "), dataStyle);
                createCell(row, 3, transaction.getAmount().doubleValue(), dataStyle);
                createCell(row, 4, transaction.getPaymentMethod().toString(), dataStyle);
                createCell(row, 5, transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), dataStyle);
                createCell(row, 6, transaction.getReferenceId(), dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            logger.info("Revenue Excel report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating revenue Excel report", e);
            return null;
        }
    }

    /**
     * Generate Station Report Excel
     */
    public static byte[] generateStationReport(List<GamingStation> stations) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Gaming Stations");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Station Name", "Type", "Hourly Rate (RWF)", "Status", "Location"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (GamingStation station : stations) {
                Row row = sheet.createRow(rowNum++);
                
                createCell(row, 0, station.getStationId(), dataStyle);
                createCell(row, 1, station.getStationName(), dataStyle);
                createCell(row, 2, station.getStationType().toString(), dataStyle);
                createCell(row, 3, station.getHourlyRate().doubleValue(), dataStyle);
                createCell(row, 4, station.getStatus().toString().replace("_", " "), dataStyle);
                createCell(row, 5, station.getLocation() != null ? station.getLocation() : "N/A", dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            logger.info("Station Excel report generated successfully");
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating station Excel report", e);
            return null;
        }
    }

    // Helper methods
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        
        cell.setCellStyle(style);
    }
}