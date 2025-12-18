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
import java.util.ArrayList;
import java.util.List;

/**
 * GamingStation Entity
 */
@Entity
@Table(name = "gaming_stations")
public class GamingStation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    @Column(name = "station_name", unique = true, nullable = false, length = 50)
    private String stationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @Column(name = "specifications", columnDefinition = "TEXT")
    private String specifications;

    @Column(name = "hourly_rate", precision = 8, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StationStatus status = StationStatus.AVAILABLE;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many relationship with Bookings
    @OneToMany(mappedBy = "gamingStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // Many-to-Many relationship with Games (via StationGame)
    @OneToMany(mappedBy = "gamingStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StationGame> installedGames = new ArrayList<>();

    // Constructors
    public GamingStation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public GamingStation(String stationName, StationType stationType, BigDecimal hourlyRate) {
        this();
        this.stationName = stationName;
        this.stationType = stationType;
        this.hourlyRate = hourlyRate;
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
    public boolean isAvailable() {
        return status == StationStatus.AVAILABLE;
    }

    public void markAsOccupied() {
        this.status = StationStatus.OCCUPIED;
    }

    public void markAsAvailable() {
        this.status = StationStatus.AVAILABLE;
    }

    public void markForMaintenance() {
        this.status = StationStatus.MAINTENANCE;
    }

    public boolean needsMaintenance() {
        if (nextMaintenanceDate == null) return false;
        return LocalDate.now().isAfter(nextMaintenanceDate);
    }

    // Getters and Setters
    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(StationType stationType) {
        this.stationType = stationType;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<StationGame> getInstalledGames() {
        return installedGames;
    }

    public void setInstalledGames(List<StationGame> installedGames) {
        this.installedGames = installedGames;
    }

    @Override
    public String toString() {
        return "GamingStation{" +
                "stationId=" + stationId +
                ", stationName='" + stationName + '\'' +
                ", stationType=" + stationType +
                ", hourlyRate=" + hourlyRate +
                ", status=" + status +
                '}';
    }
}