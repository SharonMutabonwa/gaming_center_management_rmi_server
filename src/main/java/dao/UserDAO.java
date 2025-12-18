/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Odeth
 */
import model.UserRole;
import org.hibernate.Session;
import org.hibernate.query.Query;
import model.*;
import utils.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * UserDAO - Data Access Object for User Entity
 */
public class UserDAO extends GenericDAOImpl<User, Long> {
    
    public UserDAO() {
        super(User.class);
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        String hql = "FROM User WHERE username = :username";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding user by username: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        String hql = "FROM User WHERE email = :email";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding user by email: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Authenticate user
     */
    public User authenticate(String username, String password) {
        String hql = "FROM User WHERE username = :username AND password = :password AND isActive = true";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error authenticating user: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Find users by role
     */
    public List<User> findByRole(UserRole role) {
        String hql = "FROM User WHERE role = :role";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("role", role);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding users by role: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Find user by session token
     */
    public User findBySessionToken(String sessionToken) {
        String hql = "FROM User WHERE sessionToken = :sessionToken";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("sessionToken", sessionToken);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding user by session token: {}", e.getMessage());
            return null;
        }
    }
}