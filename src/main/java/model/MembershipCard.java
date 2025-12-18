/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Odeth
 */

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MembershipCard Entity - ONE-TO-ONE Relationship with Customer
 * This demonstrates the required One-to-One relationship
 */
@Entity
@Table(name = "membership_cards")
public class MembershipCard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    // ONE-TO-ONE relationship with Customer
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", unique = true, nullable = false)
    private Customer customer;

    @Column(name = "card_number", unique = true, nullable = false, length = 20)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "points_earned")
    private Integer pointsEarned = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public MembershipCard() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MembershipCard(Customer customer, String cardNumber, MembershipType membershipType) {
        this();
        this.customer = customer;
        this.cardNumber = cardNumber;
        this.membershipType = membershipType;
        this.issueDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusYears(1);
        setDiscountBasedOnType(membershipType);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (expiryDate == null) {
            expiryDate = issueDate.plusYears(1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }

    public void renewMembership(int years) {
        this.expiryDate = this.expiryDate.plusYears(years);
        this.isActive = true;
    }

    public void addPoints(int points) {
        this.pointsEarned += points;
    }

    public void upgradeMembership(MembershipType newType) {
        this.membershipType = newType;
        setDiscountBasedOnType(newType);
    }

    private void setDiscountBasedOnType(MembershipType type) {
        switch (type) {
            case BRONZE:
                this.discountPercentage = new BigDecimal("5.00");
                break;
            case SILVER:
                this.discountPercentage = new BigDecimal("10.00");
                break;
            case GOLD:
                this.discountPercentage = new BigDecimal("15.00");
                break;
            case PLATINUM:
                this.discountPercentage = new BigDecimal("20.00");
                break;
        }
    }

    public BigDecimal calculateDiscount(BigDecimal originalAmount) {
        return originalAmount.multiply(discountPercentage).divide(new BigDecimal("100"));
    }

    public int getDaysUntilExpiry() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MembershipCard{" +
                "cardId=" + cardId +
                ", cardNumber='" + cardNumber + '\'' +
                ", membershipType=" + membershipType +
                ", discountPercentage=" + discountPercentage +
                ", expiryDate=" + expiryDate +
                ", isActive=" + isActive +
                '}';
    }
}