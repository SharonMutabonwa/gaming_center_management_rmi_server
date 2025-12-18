/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Notification;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * NotificationDAO
 */
public class NotificationDAO extends GenericDAOImpl<Notification, Long> {
    
    public NotificationDAO() {
        super(Notification.class);
    }

    public List<Notification> findByUserId(Long userId) {
        String hql = "FROM Notification WHERE user.userId = :userId ORDER BY sentAt DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Notification> query = session.createQuery(hql, Notification.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding notifications by user: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        String hql = "FROM Notification WHERE user.userId = :userId AND isRead = false ORDER BY sentAt DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Notification> query = session.createQuery(hql, Notification.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting unread notifications: {}", e.getMessage());
            return List.of();
        }
    }
}

