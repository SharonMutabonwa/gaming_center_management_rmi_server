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
import java.time.LocalTime;

/**
 * Booking Entity - ONE-TO-MANY Relationship
 * One Customer has Many Bookings
 * One GamingStation has Many Bookings
 */
@Entity
@Table(name = "bookings")
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    // Many-to-One with Customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Many-to-One with GamingStation
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private GamingStation gamingStation;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_hours", precision = 5, scale = 2, nullable = false)
    private BigDecimal durationHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Booking(Customer customer, GamingStation gamingStation, LocalDate bookingDate, 
                   LocalTime startTime, LocalTime endTime) {
        this();
        this.customer = customer;
        this.gamingStation = gamingStation;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        calculateDurationAndAmount();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public void calculateDurationAndAmount() {
        // Calculate duration in hours
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        this.durationHours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
        
        // Calculate total amount
        this.totalAmount = gamingStation.getHourlyRate().multiply(durationHours);
        
        // Apply membership discount if available
        if (customer.getMembershipCard() != null && customer.getMembershipCard().isValid()) {
            BigDecimal discount = customer.getMembershipCard().calculateDiscount(totalAmount);
            this.totalAmount = totalAmount.subtract(discount);
        }
    }

    public boolean isUpcoming() {
        return bookingDate.isAfter(LocalDate.now()) || 
               (bookingDate.isEqual(LocalDate.now()) && startTime.isAfter(LocalTime.now()));
    }

    public boolean isOngoing() {
        if (!bookingDate.isEqual(LocalDate.now())) return false;
        LocalTime now = LocalTime.now();
        return !now.isBefore(startTime) && now.isBefore(endTime);
    }

    public boolean isPast() {
        return bookingDate.isBefore(LocalDate.now()) ||
               (bookingDate.isEqual(LocalDate.now()) && endTime.isBefore(LocalTime.now()));
    }

    public boolean overlaps(LocalDate date, LocalTime start, LocalTime end) {
        if (!this.bookingDate.isEqual(date)) return false;
        return !(end.isBefore(this.startTime) || start.isAfter(this.endTime));
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
    }

    public void markAsNoShow() {
        this.status = BookingStatus.NO_SHOW;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public GamingStation getGamingStation() {
        return gamingStation;
    }

    public void setGamingStation(GamingStation gamingStation) {
        this.gamingStation = gamingStation;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(BigDecimal durationHours) {
        this.durationHours = durationHours;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", customer=" + customer.getFullName() +
                ", gamingStation=" + gamingStation.getStationName() +
                ", bookingDate=" + bookingDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
