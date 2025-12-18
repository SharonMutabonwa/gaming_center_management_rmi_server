/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmi;

/**
 *
 * @author Odeth
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dao.*;
import messaging.MessageProducer;
import model.*;
import reports.*;
import utils.ValidationUtil;
import utils.ValidationResult;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * RemoteServiceImpl - RMI Server Implementation
 * Implements all remote methods defined in RemoteService interface
 */
public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceImpl.class);

    // DAOs
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;
    private final GamingStationDAO stationDAO;
    private final GameDAO gameDAO;
    private final BookingDAO bookingDAO;
    private final MembershipCardDAO membershipDAO;
    private final TournamentDAO tournamentDAO;
    private final TransactionDAO transactionDAO;
    private final NotificationDAO notificationDAO;

    // Message Producer for OTP
    private final MessageProducer messageProducer;

    public RemoteServiceImpl() throws RemoteException {
        super();
        // Initialize DAOs
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
        this.stationDAO = new GamingStationDAO();
        this.gameDAO = new GameDAO();
        this.bookingDAO = new BookingDAO();
        this.membershipDAO = new MembershipCardDAO();
        this.tournamentDAO = new TournamentDAO();
        this.transactionDAO = new TransactionDAO();
        this.notificationDAO = new NotificationDAO();
        this.messageProducer = new MessageProducer();
        
        logger.info("RemoteServiceImpl initialized successfully");
    }

    // ============================================
    // AUTHENTICATION & SESSION MANAGEMENT
    // ============================================

    @Override
    public boolean generateAndSendOTP(String username) throws RemoteException {
        try {
            logger.info("Generating OTP for user: {}", username);
            
            User user = userDAO.findByUsername(username);
            if (user == null) {
                logger.warn("User not found: {}", username);
                return false;
            }

            // Generate 6-digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            // Set OTP expiry (5 minutes from now)
            user.setOtpCode(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            userDAO.update(user);

            // Send OTP via ActiveMQ
            messageProducer.sendOTPEmail(user.getEmail(), user.getUsername(), otp);
            
            // Create notification
            Notification notification = new Notification(
                user,
                NotificationType.OTP,
                "Your OTP code is: " + otp + ". Valid for 5 minutes."
            );
            notificationDAO.save(notification);

            logger.info("OTP generated and sent successfully to {}", user.getEmail());
            return true;

        } catch (Exception e) {
            logger.error("Error generating OTP: {}", e.getMessage(), e);
            throw new RemoteException("Failed to generate OTP", e);
        }
    }

    @Override
    public User login(String username, String password, String otp) throws RemoteException {
        try {
            logger.info("Login attempt for user: {}", username);

            // Validate inputs
            ValidationResult result = ValidationUtil.validateRequired(username, "Username");
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            result = ValidationUtil.validateRequired(password, "Password");
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            // Find user
            User user = userDAO.findByUsername(username);
            if (user == null) {
                logger.warn("Login failed: User not found - {}", username);
                throw new RemoteException("Invalid username or password");
            }

            // Verify password (In production, use BCrypt)
            if (!user.getPassword().equals(password)) {
                logger.warn("Login failed: Invalid password for user - {}", username);
                throw new RemoteException("Invalid username or password");
            }

            // Verify OTP
            if (user.getOtpCode() == null || !user.getOtpCode().equals(otp)) {
                logger.warn("Login failed: Invalid OTP for user - {}", username);
                throw new RemoteException("Invalid OTP code");
            }

            // Check OTP expiry
            if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
                logger.warn("Login failed: OTP expired for user - {}", username);
                throw new RemoteException("OTP has expired. Please request a new one.");
            }

            // Check if user is active
            if (!user.getIsActive()) {
                logger.warn("Login failed: User account inactive - {}", username);
                throw new RemoteException("Your account is inactive. Please contact support.");
            }

            // Generate session token
            String sessionToken = UUID.randomUUID().toString();
            user.setSessionToken(sessionToken);
            user.setLastLogin(LocalDateTime.now());
            
            // Clear OTP after successful login
            user.setOtpCode(null);
            user.setOtpExpiry(null);
            
            userDAO.update(user);

            logger.info("Login successful for user: {}", username);
            return user;

        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            throw new RemoteException("Login failed due to server error", e);
        }
    }

    @Override
    public boolean logout(String sessionToken) throws RemoteException {
        try {
            User user = userDAO.findBySessionToken(sessionToken);
            if (user != null) {
                user.setSessionToken(null);
                userDAO.update(user);
                logger.info("User logged out: {}", user.getUsername());
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage(), e);
            throw new RemoteException("Logout failed", e);
        }
    }

    @Override
    public boolean validateSession(String sessionToken) throws RemoteException {
        try {
            return userDAO.findBySessionToken(sessionToken) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User getUserBySession(String sessionToken) throws RemoteException {
        try {
            return userDAO.findBySessionToken(sessionToken);
        } catch (Exception e) {
            throw new RemoteException("Failed to get user by session", e);
        }
    }

    // ============================================
    // USER MANAGEMENT
    // ============================================

    @Override
    public User createUser(User user) throws RemoteException {
        try {
            // Validate user data
            ValidationResult result = ValidationUtil.validateUserRegistration(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoneNumber()
            );
            
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            // Check if username exists
            if (userDAO.findByUsername(user.getUsername()) != null) {
                throw new RemoteException("Username already exists");
            }

            // Check if email exists
            if (userDAO.findByEmail(user.getEmail()) != null) {
                throw new RemoteException("Email already exists");
            }

            return userDAO.save(user);
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RemoteException("Failed to create user", e);
        }
    }

    @Override
    public User updateUser(User user) throws RemoteException {
        try {
            return userDAO.update(user);
        } catch (Exception e) {
            throw new RemoteException("Failed to update user", e);
        }
    }

    @Override
    public void deleteUser(Long userId) throws RemoteException {
        try {
            userDAO.delete(userId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete user", e);
        }
    }

    @Override
    public User getUserById(Long userId) throws RemoteException {
        try {
            return userDAO.findById(userId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get user", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws RemoteException {
        try {
            return userDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all users", e);
        }
    }

    @Override
    public User getUserByUsername(String username) throws RemoteException {
        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            throw new RemoteException("Failed to get user by username", e);
        }
    }

    // ============================================
    // CUSTOMER MANAGEMENT
    // ============================================

    @Override
    public Customer createCustomer(Customer customer) throws RemoteException {
        try {
            return customerDAO.save(customer);
        } catch (Exception e) {
            throw new RemoteException("Failed to create customer", e);
        }
    }

    @Override
    public Customer updateCustomer(Customer customer) throws RemoteException {
        try {
            return customerDAO.update(customer);
        } catch (Exception e) {
            throw new RemoteException("Failed to update customer", e);
        }
    }

    @Override
    public void deleteCustomer(Long customerId) throws RemoteException {
        try {
            customerDAO.delete(customerId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete customer", e);
        }
    }
//    @Override
//public void deleteCustomer(Long customerId) throws RemoteException {
//    try {
//        // Assuming customerDAO is available in the service layer
//        customerDAO.delete(customerId); // This will cascade to the related entities
//    } catch (Exception e) {
//        throw new RemoteException("Failed to delete customer", e);
//    }
//}


    @Override
    public Customer getCustomerById(Long customerId) throws RemoteException {
        try {
            return customerDAO.findById(customerId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get customer", e);
        }
    }

    @Override
    public List<Customer> getAllCustomers() throws RemoteException {
        try {
            return customerDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all customers", e);
        }
    }

    @Override
    public List<Customer> searchCustomersByName(String searchTerm) throws RemoteException {
        try {
            return customerDAO.searchByName(searchTerm);
        } catch (Exception e) {
            throw new RemoteException("Failed to search customers", e);
        }
    }

    @Override
    public Customer getCustomerByUserId(Long userId) throws RemoteException {
        try {
            return customerDAO.findByUserId(userId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get customer by user ID", e);
        }
    }

    // ============================================
    // GAMING STATION MANAGEMENT
    // ============================================

    @Override
    public GamingStation createGamingStation(GamingStation station) throws RemoteException {
        try {
            // Validate hourly rate
            ValidationResult result = ValidationUtil.validateHourlyRate(station.getHourlyRate());
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }
            return stationDAO.save(station);
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException("Failed to create gaming station", e);
        }
    }

    @Override
    public GamingStation updateGamingStation(GamingStation station) throws RemoteException {
        try {
            return stationDAO.update(station);
        } catch (Exception e) {
            throw new RemoteException("Failed to update gaming station", e);
        }
    }

    @Override
    public void deleteGamingStation(Long stationId) throws RemoteException {
        try {
            stationDAO.delete(stationId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete gaming station", e);
        }
    }

    @Override
    public GamingStation getGamingStationById(Long stationId) throws RemoteException {
        try {
            return stationDAO.findById(stationId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get gaming station", e);
        }
    }

    @Override
    public List<GamingStation> getAllGamingStations() throws RemoteException {
        try {
            return stationDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all gaming stations", e);
        }
    }

    @Override
    public List<GamingStation> getAvailableStations() throws RemoteException {
        try {
            return stationDAO.getAvailableStations();
        } catch (Exception e) {
            throw new RemoteException("Failed to get available stations", e);
        }
    }

    @Override
    public List<GamingStation> getStationsByType(String stationType) throws RemoteException {
        try {
            StationType type = StationType.valueOf(stationType.toUpperCase());
            return stationDAO.findByType(type);
        } catch (Exception e) {
            throw new RemoteException("Failed to get stations by type", e);
        }
    }

    // ============================================
    // GAME MANAGEMENT
    // ============================================

    @Override
    public Game createGame(Game game) throws RemoteException {
        try {
            return gameDAO.save(game);
        } catch (Exception e) {
            throw new RemoteException("Failed to create game", e);
        }
    }

    @Override
    public Game updateGame(Game game) throws RemoteException {
        try {
            return gameDAO.update(game);
        } catch (Exception e) {
            throw new RemoteException("Failed to update game", e);
        }
    }

    @Override
    public void deleteGame(Long gameId) throws RemoteException {
        try {
            gameDAO.delete(gameId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete game", e);
        }
    }

    @Override
    public Game getGameById(Long gameId) throws RemoteException {
        try {
            return gameDAO.findById(gameId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get game", e);
        }
    }

    @Override
    public List<Game> getAllGames() throws RemoteException {
        try {
            return gameDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all games", e);
        }
    }

    @Override
    public List<Game> searchGamesByTitle(String title) throws RemoteException {
        try {
            return gameDAO.searchByTitle(title);
        } catch (Exception e) {
            throw new RemoteException("Failed to search games", e);
        }
    }

    // ============================================
    // BOOKING MANAGEMENT (WITH VALIDATION)
    // ============================================

    @Override
    public Booking createBooking(Long customerId, Long stationId, LocalDate date, 
                                 LocalTime startTime, LocalTime endTime) throws RemoteException {
        try {
            // Get customer and station
            Customer customer = customerDAO.findById(customerId);
            GamingStation station = stationDAO.findById(stationId);

            if (customer == null) {
                throw new RemoteException("Customer not found");
            }
            if (station == null) {
                throw new RemoteException("Gaming station not found");
            }

            // Validate booking
            ValidationResult result = ValidationUtil.validateBookingCreation(
                customer, station, date, startTime, endTime, bookingDAO
            );
            
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            // Create booking
            Booking booking = new Booking(customer, station, date, startTime, endTime);
            booking.calculateDurationAndAmount();

            // Check sufficient balance (BUSINESS RULE #4)
            result = ValidationUtil.validateSufficientBalance(customer, booking.getTotalAmount());
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            // Save booking
            booking = bookingDAO.save(booking);

            // Deduct amount from customer balance
            customer.deductBalance(booking.getTotalAmount());
            customerDAO.update(customer);

            // Create transaction record
            Transaction transaction = new Transaction(
                customer,
                TransactionType.BOOKING_PAYMENT,
                booking.getTotalAmount(),
                PaymentMethod.ACCOUNT_BALANCE
            );
            transaction.setDescription("Booking #" + booking.getBookingId());
            transactionDAO.save(transaction);

            // Send notification
            Notification notification = new Notification(
                customer.getUser(),
                NotificationType.BOOKING_CONFIRMATION,
                String.format("Booking confirmed! Station: %s, Date: %s, Time: %s-%s",
                    station.getStationName(), date, startTime, endTime)
            );
            notificationDAO.save(notification);

            // Send email notification via ActiveMQ
            messageProducer.sendBookingConfirmation(
                customer.getUser().getEmail(),
                customer.getFullName(),
                station.getStationName(),
                date.toString(),
                startTime + " - " + endTime
            );

            logger.info("Booking created successfully: {}", booking.getBookingId());
            return booking;

        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            throw new RemoteException("Failed to create booking", e);
        }
    }

    @Override
    public Booking updateBooking(Booking booking) throws RemoteException {
        try {
            return bookingDAO.update(booking);
        } catch (Exception e) {
            throw new RemoteException("Failed to update booking", e);
        }
    }

    @Override
    public void deleteBooking(Long bookingId) throws RemoteException {
        try {
            bookingDAO.delete(bookingId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete booking", e);
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) throws RemoteException {
        try {
            return bookingDAO.findById(bookingId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get booking", e);
        }
    }

    @Override
    public List<Booking> getAllBookings() throws RemoteException {
        try {
            return bookingDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all bookings", e);
        }
    }

    @Override
    public List<Booking> getBookingsByCustomerId(Long customerId) throws RemoteException {
        try {
            return bookingDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get customer bookings", e);
        }
    }

    @Override
    public List<Booking> getBookingsByStationId(Long stationId) throws RemoteException {
        try {
            return bookingDAO.findByStationId(stationId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get station bookings", e);
        }
    }

    @Override
    public List<Booking> getUpcomingBookings() throws RemoteException {
        try {
            return bookingDAO.getUpcomingBookings();
        } catch (Exception e) {
            throw new RemoteException("Failed to get upcoming bookings", e);
        }
    }

    @Override
    public boolean isSlotAvailable(Long stationId, LocalDate date, LocalTime startTime, LocalTime endTime) throws RemoteException {
        try {
            return !bookingDAO.hasConflict(stationId, date, startTime, endTime);
        } catch (Exception e) {
            throw new RemoteException("Failed to check slot availability", e);
        }
    }

    // ============================================
    // MEMBERSHIP CARD MANAGEMENT
    // ============================================

    @Override
    public MembershipCard createMembershipCard(MembershipCard card) throws RemoteException {
        try {
            return membershipDAO.save(card);
        } catch (Exception e) {
            throw new RemoteException("Failed to create membership card", e);
        }
    }

    @Override
    public MembershipCard updateMembershipCard(MembershipCard card) throws RemoteException {
        try {
            return membershipDAO.update(card);
        } catch (Exception e) {
            throw new RemoteException("Failed to update membership card", e);
        }
    }

    @Override
    public void deleteMembershipCard(Long cardId) throws RemoteException {
        try {
            membershipDAO.delete(cardId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete membership card", e);
        }
    }

    @Override
    public MembershipCard getMembershipCardById(Long cardId) throws RemoteException {
        try {
            return membershipDAO.findById(cardId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get membership card", e);
        }
    }

    @Override
    public MembershipCard getMembershipCardByCustomerId(Long customerId) throws RemoteException {
        try {
            return membershipDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get membership card by customer", e);
        }
    }

    @Override
    public List<MembershipCard> getAllMembershipCards() throws RemoteException {
        try {
            return membershipDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all membership cards", e);
        }
    }

    // ============================================
    // TOURNAMENT MANAGEMENT
    // ============================================

    @Override
    public Tournament createTournament(Tournament tournament) throws RemoteException {
        try {
            // Validate dates
            ValidationResult result = ValidationUtil.validateDateRange(
                tournament.getStartDate(),
                tournament.getEndDate()
            );
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }
            return tournamentDAO.save(tournament);
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteException("Failed to create tournament", e);
        }
    }

    @Override
    public Tournament updateTournament(Tournament tournament) throws RemoteException {
        try {
            return tournamentDAO.update(tournament);
        } catch (Exception e) {
            throw new RemoteException("Failed to update tournament", e);
        }
    }

    @Override
    public void deleteTournament(Long tournamentId) throws RemoteException {
        try {
            tournamentDAO.delete(tournamentId);
        } catch (Exception e) {
            throw new RemoteException("Failed to delete tournament", e);
        }
    }

    @Override
    public Tournament getTournamentById(Long tournamentId) throws RemoteException {
        try {
            return tournamentDAO.findById(tournamentId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get tournament", e);
        }
    }

    @Override
    public List<Tournament> getAllTournaments() throws RemoteException {
        try {
            return tournamentDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all tournaments", e);
        }
    }

    @Override
    public List<Tournament> getUpcomingTournaments() throws RemoteException {
        try {
            return tournamentDAO.getUpcomingTournaments();
        } catch (Exception e) {
            throw new RemoteException("Failed to get upcoming tournaments", e);
        }
    }

    @Override
    public TournamentParticipant registerForTournament(Long tournamentId, Long customerId, String teamName) throws RemoteException {
        try {
            Tournament tournament = tournamentDAO.findById(tournamentId);
            Customer customer = customerDAO.findById(customerId);

            if (tournament == null || customer == null) {
                throw new RemoteException("Tournament or Customer not found");
            }

            // Validate tournament registration (BUSINESS RULE #5)
            ValidationResult result = ValidationUtil.validateTournamentRegistration(tournament, customer);
            if (!result.isSuccess()) {
                throw new RemoteException(result.getMessage());
            }

            // Create participant
            TournamentParticipant participant = new TournamentParticipant(tournament, customer, teamName);
            
            // This would need a TournamentParticipantDAO - implement similarly
            // For now, we'll increment tournament participants
            tournament.incrementParticipants();
            tournamentDAO.update(tournament);

            // Deduct entry fee if applicable
            if (tournament.getEntryFee().compareTo(BigDecimal.ZERO) > 0) {
                customer.deductBalance(tournament.getEntryFee());
                customerDAO.update(customer);

                Transaction transaction = new Transaction(
                    customer,
                    TransactionType.TOURNAMENT_FEE,
                    tournament.getEntryFee(),
                    PaymentMethod.ACCOUNT_BALANCE
                );
                transaction.setDescription("Tournament Entry: " + tournament.getTournamentName());
                transactionDAO.save(transaction);
            }

            // Send notification
            Notification notification = new Notification(
                customer.getUser(),
                NotificationType.TOURNAMENT_UPDATE,
                "Successfully registered for tournament: " + tournament.getTournamentName()
            );
            notificationDAO.save(notification);

            logger.info("Customer {} registered for tournament {}", customerId, tournamentId);
            return participant;

        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error registering for tournament: {}", e.getMessage(), e);
            throw new RemoteException("Failed to register for tournament", e);
        }
    }

    @Override
    public List<TournamentParticipant> getTournamentParticipants(Long tournamentId) throws RemoteException {
        try {
            // Would need TournamentParticipantDAO
            return List.of();
        } catch (Exception e) {
            throw new RemoteException("Failed to get tournament participants", e);
        }
    }

    // ============================================
    // TRANSACTION MANAGEMENT
    // ============================================

    @Override
    public Transaction createTransaction(Transaction transaction) throws RemoteException {
        try {
            return transactionDAO.save(transaction);
        } catch (Exception e) {
            throw new RemoteException("Failed to create transaction", e);
        }
    }

    @Override
    public Transaction getTransactionById(Long transactionId) throws RemoteException {
        try {
            return transactionDAO.findById(transactionId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get transaction", e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() throws RemoteException {
        try {
            return transactionDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Failed to get all transactions", e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(Long customerId) throws RemoteException {
        try {
            return transactionDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get customer transactions", e);
        }
    }

    @Override
    public boolean addBalance(Long customerId, double amount, String paymentMethod) throws RemoteException {
        try {
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                throw new RemoteException("Customer not found");
            }

            BigDecimal depositAmount = BigDecimal.valueOf(amount);
            customer.addBalance(depositAmount);
            customerDAO.update(customer);

            // Create transaction record
            Transaction transaction = new Transaction(
                customer,
                TransactionType.DEPOSIT,
                depositAmount,
                PaymentMethod.valueOf(paymentMethod.toUpperCase())
            );
            transaction.setDescription("Account top-up");
            transactionDAO.save(transaction);

            logger.info("Balance added for customer {}: {}", customerId, amount);
            return true;

        } catch (Exception e) {
            logger.error("Error adding balance: {}", e.getMessage(), e);
            throw new RemoteException("Failed to add balance", e);
        }
    }

    // ============================================
    // NOTIFICATION MANAGEMENT
    // ============================================

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) throws RemoteException {
        try {
            return notificationDAO.findByUserId(userId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get notifications", e);
        }
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) throws RemoteException {
        try {
            return notificationDAO.getUnreadNotifications(userId);
        } catch (Exception e) {
            throw new RemoteException("Failed to get unread notifications", e);
        }
    }

    @Override
    public void markNotificationAsRead(Long notificationId) throws RemoteException {
        try {
            Notification notification = notificationDAO.findById(notificationId);
            if (notification != null) {
                notification.markAsRead();
                notificationDAO.update(notification);
            }
        } catch (Exception e) {
            throw new RemoteException("Failed to mark notification as read", e);
        }
    }

    // ============================================
    // REPORTS & EXPORT
    // ============================================

    @Override
    public byte[] generatePDFReport(String reportType) throws RemoteException {
        try {
            logger.info("Generating PDF report: {}", reportType);
            
            switch (reportType.toUpperCase()) {
                case "CUSTOMERS":
                    return PDFExporter.generateCustomerReport(customerDAO.findAll());
                case "BOOKINGS":
                    return PDFExporter.generateBookingReport(bookingDAO.findAll());
                case "REVENUE":
                    return PDFExporter.generateRevenueReport(transactionDAO.findAll());
                case "STATIONS":
                    return PDFExporter.generateStationReport(stationDAO.findAll());
                default:
                    throw new RemoteException("Unknown report type: " + reportType);
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            throw new RemoteException("Failed to generate PDF report", e);
        }
    }

    @Override
    public byte[] generateExcelReport(String reportType) throws RemoteException {
        try {
            logger.info("Generating Excel report: {}", reportType);
            
            switch (reportType.toUpperCase()) {
                case "CUSTOMERS":
                    return ExcelExporter.generateCustomerReport(customerDAO.findAll());
                case "BOOKINGS":
                    return ExcelExporter.generateBookingReport(bookingDAO.findAll());
                case "REVENUE":
                    return ExcelExporter.generateRevenueReport(transactionDAO.findAll());
                case "STATIONS":
                    return ExcelExporter.generateStationReport(stationDAO.findAll());
                default:
                    throw new RemoteException("Unknown report type: " + reportType);
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating Excel report: {}", e.getMessage(), e);
            throw new RemoteException("Failed to generate Excel report", e);
        }
    }

    @Override
    public String generateCSVReport(String reportType) throws RemoteException {
        try {
            logger.info("Generating CSV report: {}", reportType);
            
            switch (reportType.toUpperCase()) {
                case "CUSTOMERS":
                    return CSVExporter.generateCustomerReport(customerDAO.findAll());
                case "BOOKINGS":
                    return CSVExporter.generateBookingReport(bookingDAO.findAll());
                case "REVENUE":
                    return CSVExporter.generateRevenueReport(transactionDAO.findAll());
                case "STATIONS":
                    return CSVExporter.generateStationReport(stationDAO.findAll());
                default:
                    throw new RemoteException("Unknown report type: " + reportType);
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating CSV report: {}", e.getMessage(), e);
            throw new RemoteException("Failed to generate CSV report", e);
        }
    }

    // ============================================
    // DASHBOARD STATISTICS
    // ============================================

    @Override
    public DashboardStats getDashboardStats() throws RemoteException {
        try {
            DashboardStats stats = new DashboardStats();
            
            stats.setTotalCustomers(customerDAO.count());
            stats.setTotalBookings(bookingDAO.count());
            stats.setActiveStations(stationDAO.getAvailableStations().size());
            stats.setUpcomingTournaments(tournamentDAO.getUpcomingTournaments().size());
            
            // Calculate today's bookings
            List<Booking> todayBookings = bookingDAO.findByDateRange(LocalDate.now(), LocalDate.now());
            stats.setTodayBookings(todayBookings.size());
            
            // Calculate total revenue
            List<Transaction> allTransactions = transactionDAO.findAll();
            double totalRevenue = allTransactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.BOOKING_PAYMENT || 
                            t.getTransactionType() == TransactionType.TOURNAMENT_FEE)
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();
            stats.setTotalRevenue(totalRevenue);
            
            logger.info("Dashboard stats generated successfully");
            return stats;
            
        } catch (Exception e) {
            logger.error("Error generating dashboard stats: {}", e.getMessage(), e);
            throw new RemoteException("Failed to generate dashboard stats", e);
        }
    }
}
