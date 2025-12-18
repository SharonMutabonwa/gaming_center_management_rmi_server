/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author Odeth
 */
 /**
 * ValidationResult - Encapsulates validation outcome
 */
public class ValidationResult {
    private final boolean success;
    private final String message;
    private final ValidationType type;

    private ValidationResult(boolean success, String message, ValidationType type) {
        this.success = success;
        this.message = message;
        this.type = type;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "Validation successful", ValidationType.SUCCESS);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message, ValidationType.ERROR);
    }

    public static ValidationResult warning(String message) {
        return new ValidationResult(true, message, ValidationType.WARNING);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return type == ValidationType.ERROR;
    }

    public boolean isWarning() {
        return type == ValidationType.WARNING;
    }

    public String getMessage() {
        return message;
    }

    public ValidationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": " + message;
    }
}
