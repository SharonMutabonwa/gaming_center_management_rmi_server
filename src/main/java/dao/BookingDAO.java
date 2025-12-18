/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import model.Booking;
import model.BookingStatus;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * BookingDAO - Data Access Object for Booking Entity
 */
public class BookingDAO extends GenericDAOImpl<Booking, Long> {
    
    public BookingDAO() {
        super(Booking.class);
    }

    /**
     * Find bookings by customer
     */
    public List<Booking> findByCustomerId(Long customerId) {
        String hql = "FROM Booking WHERE customer.customerId = :customerId ORDER BY bookingDate DESC, startTime DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("customerId", customerId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding bookings by customer: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Find bookings by gaming station
     */
    public List<Booking> findByStationId(Long stationId) {
        String hql = "FROM Booking WHERE gamingStation.stationId = :stationId ORDER BY bookingDate DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("stationId", stationId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding bookings by station: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Check for booking conflicts (BUSINESS VALIDATION RULE #1)
     */
    public boolean hasConflict(Long stationId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String hql = "FROM Booking WHERE gamingStation.stationId = :stationId " +
                     "AND bookingDate = :date " +
                     "AND status IN ('PENDING', 'CONFIRMED', 'ONGOING') " +
                     "AND ((startTime <= :startTime AND endTime > :startTime) " +
                     "OR (startTime < :endTime AND endTime >= :endTime) " +
                     "OR (startTime >= :startTime AND endTime <= :endTime))";
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("stationId", stationId);
            query.setParameter("date", date);
            query.setParameter("startTime", startTime);
            query.setParameter("endTime", endTime);
            return !query.list().isEmpty();
        } catch (Exception e) {
            logger.error("Error checking booking conflict: {}", e.getMessage());
            return true; // Return true to prevent booking on error
        }
    }

    /**
     * Get upcoming bookings
     */
    public List<Booking> getUpcomingBookings() {
        String hql = "FROM Booking WHERE bookingDate >= :today AND status IN ('PENDING', 'CONFIRMED') " +
                     "ORDER BY bookingDate ASC, startTime ASC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("today", LocalDate.now());
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting upcoming bookings: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get bookings by date range
     */
    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String hql = "FROM Booking WHERE bookingDate BETWEEN :startDate AND :endDate " +
                     "ORDER BY bookingDate DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding bookings by date range: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get bookings by status
     */
    public List<Booking> findByStatus(BookingStatus status) {
        String hql = "FROM Booking WHERE status = :status ORDER BY bookingDate DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding bookings by status: {}", e.getMessage());
            return List.of();
        }
    }
}
