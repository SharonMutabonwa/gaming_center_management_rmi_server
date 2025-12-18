/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Odeth
 */
 /**
 * DashboardStats - Data Transfer Object for statistics
 */
public class DashboardStats implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private long totalCustomers;
    private long totalBookings;
    private long activeStations;
    private long upcomingTournaments;
    private double totalRevenue;
    private long todayBookings;

    // Constructor
    public DashboardStats() {}

    // Getters and Setters
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getActiveStations() { return activeStations; }
    public void setActiveStations(long activeStations) { this.activeStations = activeStations; }

    public long getUpcomingTournaments() { return upcomingTournaments; }
    public void setUpcomingTournaments(long upcomingTournaments) { this.upcomingTournaments = upcomingTournaments; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTodayBookings() { return todayBookings; }
    public void setTodayBookings(long todayBookings) { this.todayBookings = todayBookings; }
}
