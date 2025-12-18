/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reports;

import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Booking;
import model.Customer;
import model.GamingStation;
import model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Odeth
 */
 /**
 * CSVExporter - Generates CSV reports using Apache Commons CSV
 */
public class CSVExporter {
    private static final Logger logger = LoggerFactory.getLogger(CSVExporter.class);

    /**
     * Generate Customer Report CSV
     */
    public static String generateCustomerReport(List<Customer> customers) {
        try {
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,First Name,Last Name,Email,Phone,Gender,Date of Birth,Balance (RWF),Hours Played\n");
            
            // Data rows
            for (Customer customer : customers) {
                csv.append(customer.getCustomerId()).append(",");
                csv.append(escapeCSV(customer.getFirstName())).append(",");
                csv.append(escapeCSV(customer.getLastName())).append(",");
                csv.append(escapeCSV(customer.getUser().getEmail())).append(",");
                csv.append(escapeCSV(customer.getUser().getPhoneNumber() != null ? customer.getUser().getPhoneNumber() : "N/A")).append(",");
                csv.append(customer.getGender().toString()).append(",");
                csv.append(customer.getDateOfBirth().toString()).append(",");
                csv.append(customer.getAccountBalance().toString()).append(",");
                csv.append(customer.getTotalHoursPlayed()).append("\n");
            }
            
            logger.info("Customer CSV report generated successfully");
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error generating customer CSV report", e);
            return null;
        }
    }

    /**
     * Generate Booking Report CSV
     */
    public static String generateBookingReport(List<Booking> bookings) {
        try {
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,Customer,Station,Date,Start Time,End Time,Duration (hrs),Amount (RWF),Status\n");
            
            // Data rows
            for (Booking booking : bookings) {
                csv.append(booking.getBookingId()).append(",");
                csv.append(escapeCSV(booking.getCustomer().getFullName())).append(",");
                csv.append(escapeCSV(booking.getGamingStation().getStationName())).append(",");
                csv.append(booking.getBookingDate().toString()).append(",");
                csv.append(booking.getStartTime().toString()).append(",");
                csv.append(booking.getEndTime().toString()).append(",");
                csv.append(booking.getDurationHours().toString()).append(",");
                csv.append(booking.getTotalAmount().toString()).append(",");
                csv.append(booking.getStatus().toString()).append("\n");
            }
            
            logger.info("Booking CSV report generated successfully");
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error generating booking CSV report", e);
            return null;
        }
    }

    /**
     * Generate Revenue Report CSV
     */
    public static String generateRevenueReport(List<Transaction> transactions) {
        try {
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,Customer,Type,Amount (RWF),Payment Method,Date,Reference ID\n");
            
            // Data rows
            for (Transaction transaction : transactions) {
                csv.append(transaction.getTransactionId()).append(",");
                csv.append(escapeCSV(transaction.getCustomer().getFullName())).append(",");
                csv.append(transaction.getTransactionType().toString().replace("_", " ")).append(",");
                csv.append(transaction.getAmount().toString()).append(",");
                csv.append(transaction.getPaymentMethod().toString()).append(",");
                csv.append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append(",");
                csv.append(escapeCSV(transaction.getReferenceId())).append("\n");
//                csv.append(transaction.getTransactionType().getDisplayName()).append(",");
//                csv.append(transaction.getPaymentMethod().getDisplayName()).append(",");

            }
            
            logger.info("Revenue CSV report generated successfully");
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error generating revenue CSV report", e);
            return null;
        }
    }

    /**
     * Generate Station Report CSV
     */
    public static String generateStationReport(List<GamingStation> stations) {
        try {
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,Station Name,Type,Hourly Rate (RWF),Status,Location\n");
            
            // Data rows
            for (GamingStation station : stations) {
                csv.append(station.getStationId()).append(",");
                csv.append(escapeCSV(station.getStationName())).append(",");
                csv.append(station.getStationType().toString()).append(",");
                csv.append(station.getHourlyRate().toString()).append(",");
                csv.append(station.getStatus().toString().replace("_", " ")).append(",");
                csv.append(escapeCSV(station.getLocation() != null ? station.getLocation() : "N/A")).append("\n");
            }
            
            logger.info("Station CSV report generated successfully");
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error generating station CSV report", e);
            return null;
        }
    }

    /**
     * Escape special characters in CSV
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

