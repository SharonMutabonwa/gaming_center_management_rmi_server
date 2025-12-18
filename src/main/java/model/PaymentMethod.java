/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Odeth
 */
public enum PaymentMethod {
    CASH,
    CARD,
    MOBILE_MONEY,
    ACCOUNT_BALANCE;
    
    // Add a custom method to get a user-friendly name
    public String getDisplayName() {
        return this.name().replace("_", " ").toLowerCase();
    }
}