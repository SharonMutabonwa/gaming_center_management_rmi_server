/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmi;

/**
 *
 * @author Odeth
 */

import model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * RemoteService Interface - RMI Remote Interface
 * Defines all remote methods accessible by clients
 */
public interface RemoteService extends Remote {

    // ============================================
    // AUTHENTICATION & SESSION MANAGEMENT
    // ============================================
    
    /**
     * Generate and send OTP for login
     * @return true if OTP sent successfully
     */
    boolean generateAndSendOTP(String username) throws RemoteException;
    
    /**
     * Login with username, password, and OTP
     * @return User object if successful, null otherwise
     */
    User login(String username, String password, String otp) throws RemoteException;
    
    /**
     * Logout user
     */
    boolean logout(String sessionToken) throws RemoteException;
    
    /**
     * Validate session token
     */
    boolean validateSession(String sessionToken) throws RemoteException;
    
    /**
     * Get user by session token
     */
    User getUserBySession(String sessionToken) throws RemoteException;

    // ============================================
    // USER MANAGEMENT
    // ============================================
    
    User createUser(User user) throws RemoteException;
    User updateUser(User user) throws RemoteException;
    void deleteUser(Long userId) throws RemoteException;
    User getUserById(Long userId) throws RemoteException;
    List<User> getAllUsers() throws RemoteException;
    User getUserByUsername(String username) throws RemoteException;

    // ============================================
    // CUSTOMER MANAGEMENT
    // ============================================
    
    Customer createCustomer(Customer customer) throws RemoteException;
    Customer updateCustomer(Customer customer) throws RemoteException;
    void deleteCustomer(Long customerId) throws RemoteException;
    Customer getCustomerById(Long customerId) throws RemoteException;
    List<Customer> getAllCustomers() throws RemoteException;
    List<Customer> searchCustomersByName(String searchTerm) throws RemoteException;
    Customer getCustomerByUserId(Long userId) throws RemoteException;

    // ============================================
    // GAMING STATION MANAGEMENT
    // ============================================
    
    GamingStation createGamingStation(GamingStation station) throws RemoteException;
    GamingStation updateGamingStation(GamingStation station) throws RemoteException;
    void deleteGamingStation(Long stationId) throws RemoteException;
    GamingStation getGamingStationById(Long stationId) throws RemoteException;
    List<GamingStation> getAllGamingStations() throws RemoteException;
    List<GamingStation> getAvailableStations() throws RemoteException;
    List<GamingStation> getStationsByType(String stationType) throws RemoteException;

    // ============================================
    // GAME MANAGEMENT
    // ============================================
    
    Game createGame(Game game) throws RemoteException;
    Game updateGame(Game game) throws RemoteException;
    void deleteGame(Long gameId) throws RemoteException;
    Game getGameById(Long gameId) throws RemoteException;
    List<Game> getAllGames() throws RemoteException;
    List<Game> searchGamesByTitle(String title) throws RemoteException;

    // ============================================
    // BOOKING MANAGEMENT
    // ============================================
    
    /**
     * Create booking with validation
     * @return Booking object if successful, throws exception if validation fails
     */
    Booking createBooking(Long customerId, Long stationId, LocalDate date, 
                         LocalTime startTime, LocalTime endTime) throws RemoteException;
    
    Booking updateBooking(Booking booking) throws RemoteException;
    void deleteBooking(Long bookingId) throws RemoteException;
    Booking getBookingById(Long bookingId) throws RemoteException;
    List<Booking> getAllBookings() throws RemoteException;
    List<Booking> getBookingsByCustomerId(Long customerId) throws RemoteException;
    List<Booking> getBookingsByStationId(Long stationId) throws RemoteException;
    List<Booking> getUpcomingBookings() throws RemoteException;
    
    /**
     * Check if a booking slot is available
     */
    boolean isSlotAvailable(Long stationId, LocalDate date, LocalTime startTime, LocalTime endTime) throws RemoteException;

    // ============================================
    // MEMBERSHIP CARD MANAGEMENT
    // ============================================
    
    MembershipCard createMembershipCard(MembershipCard card) throws RemoteException;
    MembershipCard updateMembershipCard(MembershipCard card) throws RemoteException;
    void deleteMembershipCard(Long cardId) throws RemoteException;
    MembershipCard getMembershipCardById(Long cardId) throws RemoteException;
    MembershipCard getMembershipCardByCustomerId(Long customerId) throws RemoteException;
    List<MembershipCard> getAllMembershipCards() throws RemoteException;

    // ============================================
    // TOURNAMENT MANAGEMENT
    // ============================================
    
    Tournament createTournament(Tournament tournament) throws RemoteException;
    Tournament updateTournament(Tournament tournament) throws RemoteException;
    void deleteTournament(Long tournamentId) throws RemoteException;
    Tournament getTournamentById(Long tournamentId) throws RemoteException;
    List<Tournament> getAllTournaments() throws RemoteException;
    List<Tournament> getUpcomingTournaments() throws RemoteException;
    
    /**
     * Register customer for tournament with validation
     */
    TournamentParticipant registerForTournament(Long tournamentId, Long customerId, String teamName) throws RemoteException;
    
    List<TournamentParticipant> getTournamentParticipants(Long tournamentId) throws RemoteException;

    // ============================================
    // TRANSACTION MANAGEMENT
    // ============================================
    
    Transaction createTransaction(Transaction transaction) throws RemoteException;
    Transaction getTransactionById(Long transactionId) throws RemoteException;
    List<Transaction> getAllTransactions() throws RemoteException;
    List<Transaction> getTransactionsByCustomerId(Long customerId) throws RemoteException;
    
    /**
     * Add balance to customer account
     */
    boolean addBalance(Long customerId, double amount, String paymentMethod) throws RemoteException;

    // ============================================
    // NOTIFICATION MANAGEMENT
    // ============================================
    
    List<Notification> getNotificationsByUserId(Long userId) throws RemoteException;
    List<Notification> getUnreadNotifications(Long userId) throws RemoteException;
    void markNotificationAsRead(Long notificationId) throws RemoteException;

    // ============================================
    // REPORTS & EXPORT
    // ============================================
    
    /**
     * Generate PDF report
     * @param reportType Type of report (customers, bookings, revenue, etc.)
     * @return byte array of PDF file
     */
    byte[] generatePDFReport(String reportType) throws RemoteException;
    
    /**
     * Generate Excel report
     * @param reportType Type of report
     * @return byte array of Excel file
     */
    byte[] generateExcelReport(String reportType) throws RemoteException;
    
    /**
     * Generate CSV report
     * @param reportType Type of report
     * @return CSV content as String
     */
    String generateCSVReport(String reportType) throws RemoteException;

    // ============================================
    // DASHBOARD STATISTICS
    // ============================================
    
    /**
     * Get dashboard statistics
     */
    DashboardStats getDashboardStats() throws RemoteException;
}

