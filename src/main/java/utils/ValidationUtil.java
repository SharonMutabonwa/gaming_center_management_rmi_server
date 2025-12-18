/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author Odeth
 */

import model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * ValidationUtil - Implements Business and Technical Validation Rules
 * 
 * BUSINESS VALIDATION RULES (5):
 * 1. Booking Conflict Prevention
 * 2. Age Restriction Enforcement
 * 3. Membership Expiry Check
 * 4. Insufficient Balance Check
 * 5. Tournament Registration Limit
 * 
 * TECHNICAL VALIDATION RULES (5):
 * 1. Email Format Validation
 * 2. Phone Number Validation
 * 3. Date Range Validation
 * 4. Numeric Range Validation
 * 5. Required Field Validation
 */
public class ValidationUtil {

    // ============================================
    // TECHNICAL VALIDATION RULES
    // ============================================

    /**
     * TECHNICAL RULE #1: Email Format Validation
     * Email must match standard format (user@domain.ext)
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("Email is required");
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            return ValidationResult.error("Invalid email format. Example: user@example.com");
        }
        
        return ValidationResult.success();
    }

    /**
     * TECHNICAL RULE #2: Phone Number Validation
     * Phone must match Rwanda format (+250XXXXXXXXX)
     */
    public static ValidationResult validatePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ValidationResult.error("Phone number is required");
        }
        
        // Rwanda phone format: +250XXXXXXXXX or 250XXXXXXXXX
        String phoneRegex = "^(\\+?250|0)?[7][0-9]{8}$";
        if (!Pattern.matches(phoneRegex, phone.replaceAll("[\\s-]", ""))) {
            return ValidationResult.error("Invalid phone format. Use: +250XXXXXXXXX or 07XXXXXXXX");
        }
        
        return ValidationResult.success();
    }

    /**
     * TECHNICAL RULE #3: Date Range Validation
     * Start date must be before or equal to end date
     */
    public static ValidationResult validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return ValidationResult.error("Start date and end date are required");
        }
        
        if (startDate.isAfter(endDate)) {
            return ValidationResult.error("Start date cannot be after end date");
        }
        
        return ValidationResult.success();
    }

    /**
     * TECHNICAL RULE #4: Numeric Range Validation
     * Numbers must be within valid ranges
     */
    public static ValidationResult validateNumericRange(BigDecimal value, BigDecimal min, BigDecimal max, String fieldName) {
        if (value == null) {
            return ValidationResult.error(fieldName + " is required");
        }
        
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            return ValidationResult.error(fieldName + " must be between " + min + " and " + max);
        }
        
        return ValidationResult.success();
    }

    public static ValidationResult validateAge(int age) {
        if (age < 1 || age > 150) {
            return ValidationResult.error("Age must be between 1 and 150");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validateHourlyRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.error("Hourly rate must be greater than 0");
        }
        if (rate.compareTo(new BigDecimal("100000")) > 0) {
            return ValidationResult.error("Hourly rate cannot exceed 100,000 RWF");
        }
        return ValidationResult.success();
    }

    /**
     * TECHNICAL RULE #5: Required Field Validation
     * Fields cannot be null or empty
     */
    public static ValidationResult validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " is required");
        }
        return ValidationResult.success();
    }

    public static ValidationResult validateRequired(Object value, String fieldName) {
        if (value == null) {
            return ValidationResult.error(fieldName + " is required");
        }
        return ValidationResult.success();
    }

    // ============================================
    // BUSINESS VALIDATION RULES
    // ============================================

    /**
     * BUSINESS RULE #1: Booking Conflict Prevention
     * No overlapping bookings for the same station
     */
    public static ValidationResult validateBookingConflict(GamingStation station, LocalDate date, 
                                                          LocalTime startTime, LocalTime endTime,
                                                          dao.BookingDAO bookingDAO) {
        if (station == null || date == null || startTime == null || endTime == null) {
            return ValidationResult.error("All booking details are required");
        }

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            return ValidationResult.error("Start time must be before end time");
        }

        // Check for conflicts
        boolean hasConflict = bookingDAO.hasConflict(station.getStationId(), date, startTime, endTime);
        if (hasConflict) {
            return ValidationResult.error("Booking conflict! This station is already booked for the selected time slot");
        }

        return ValidationResult.success();
    }

    /**
     * BUSINESS RULE #2: Age Restriction Enforcement
     * Customer must meet minimum age requirement for game
     */
    public static ValidationResult validateAgeRestriction(Customer customer, Game game) {
        if (customer == null || game == null) {
            return ValidationResult.error("Customer and game information required");
        }

        int customerAge = customer.calculateAge();
        int minAge = game.getMinAgeRequirement();

        if (customerAge < minAge) {
            return ValidationResult.error(String.format(
                "Age restriction: Customer must be at least %d years old to play '%s'. Current age: %d",
                minAge, game.getGameTitle(), customerAge
            ));
        }

        return ValidationResult.success();
    }

    /**
     * BUSINESS RULE #3: Membership Expiry Check
     * Cannot book if membership card is expired
     */
    public static ValidationResult validateMembershipExpiry(Customer customer) {
        if (customer == null) {
            return ValidationResult.error("Customer information required");
        }

        MembershipCard card = customer.getMembershipCard();
        if (card == null) {
            return ValidationResult.warning("Customer does not have a membership card");
        }

        if (card.isExpired()) {
            return ValidationResult.error("Membership card has expired on " + card.getExpiryDate() + 
                                        ". Please renew to continue booking");
        }

        if (!card.getIsActive()) {
            return ValidationResult.error("Membership card is not active");
        }

        // Warning if expiring soon (within 7 days)
        int daysUntilExpiry = card.getDaysUntilExpiry();
        if (daysUntilExpiry <= 7 && daysUntilExpiry > 0) {
            return ValidationResult.warning("Membership card expires in " + daysUntilExpiry + " days");
        }

        return ValidationResult.success();
    }

    /**
     * BUSINESS RULE #4: Insufficient Balance Check
     * Customer must have sufficient account balance
     */
    public static ValidationResult validateSufficientBalance(Customer customer, BigDecimal requiredAmount) {
        if (customer == null) {
            return ValidationResult.error("Customer information required");
        }

        if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) < 0) {
            return ValidationResult.error("Invalid amount");
        }

        if (!customer.hasSufficientBalance(requiredAmount)) {
            return ValidationResult.error(String.format(
                "Insufficient balance! Required: %s RWF, Available: %s RWF",
                requiredAmount, customer.getAccountBalance()
            ));
        }

        // Warning if balance will be low after transaction
        BigDecimal balanceAfter = customer.getAccountBalance().subtract(requiredAmount);
        if (balanceAfter.compareTo(new BigDecimal("5000")) < 0) {
            return ValidationResult.warning("Low balance warning: " + balanceAfter + " RWF remaining after transaction");
        }

        return ValidationResult.success();
    }

    /**
     * BUSINESS RULE #5: Tournament Registration Limit
     * Cannot register if tournament is full or deadline passed
     */
    public static ValidationResult validateTournamentRegistration(Tournament tournament, Customer customer) {
        if (tournament == null || customer == null) {
            return ValidationResult.error("Tournament and customer information required");
        }

        // Check registration deadline
        if (LocalDate.now().isAfter(tournament.getRegistrationDeadline())) {
            return ValidationResult.error("Registration deadline has passed (" + tournament.getRegistrationDeadline() + ")");
        }

        // Check if tournament is full
        if (tournament.isFull()) {
            return ValidationResult.error("Tournament is full! Maximum participants: " + tournament.getMaxParticipants());
        }

        // Check if registration is open
        if (!tournament.isRegistrationOpen()) {
            return ValidationResult.error("Tournament registration is not open. Status: " + tournament.getStatus());
        }

        // Check entry fee
        if (tournament.getEntryFee().compareTo(BigDecimal.ZERO) > 0) {
            ValidationResult balanceCheck = validateSufficientBalance(customer, tournament.getEntryFee());
            if (!balanceCheck.isSuccess()) {
                return ValidationResult.error("Insufficient balance for tournament entry fee: " + tournament.getEntryFee() + " RWF");
            }
        }

        return ValidationResult.success();
    }

    // ============================================
    // COMBINED VALIDATION FOR ENTITIES
    // ============================================

    /**
     * Validate User Registration
     */
    public static ValidationResult validateUserRegistration(String username, String password, String email, String phone) {
        ValidationResult result;
        
        result = validateRequired(username, "Username");
        if (!result.isSuccess()) return result;

        result = validateRequired(password, "Password");
        if (!result.isSuccess()) return result;

        if (password.length() < 6) {
            return ValidationResult.error("Password must be at least 6 characters long");
        }

        result = validateEmail(email);
        if (!result.isSuccess()) return result;

        if (phone != null && !phone.isEmpty()) {
            result = validatePhoneNumber(phone);
            if (!result.isSuccess()) return result;
        }

        return ValidationResult.success();
    }

    /**
     * Validate Booking Creation
     */
    public static ValidationResult validateBookingCreation(Customer customer, GamingStation station, 
                                                          LocalDate date, LocalTime startTime, LocalTime endTime,
                                                          dao.BookingDAO bookingDAO) {
        ValidationResult result;

        // Check if booking date is not in the past
        if (date.isBefore(LocalDate.now())) {
            return ValidationResult.error("Cannot book for past dates");
        }

        // Check time validity
        if (date.isEqual(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
            return ValidationResult.error("Cannot book for past times");
        }

        // Check membership expiry
        result = validateMembershipExpiry(customer);
        if (result.isError()) return result;

        // Check booking conflict
        result = validateBookingConflict(station, date, startTime, endTime, bookingDAO);
        if (!result.isSuccess()) return result;

        // Calculate and check balance
        // This would be done in the service layer with actual calculation

        return ValidationResult.success();
    }
}