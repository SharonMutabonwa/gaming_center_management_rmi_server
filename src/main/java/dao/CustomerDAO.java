/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Customer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * CustomerDAO - Data Access Object for Customer Entity
 */
public class CustomerDAO extends GenericDAOImpl<Customer, Long> {
    
    public CustomerDAO() {
        super(Customer.class);
    }

    /**
     * Find customer by user ID
     */
    public Customer findByUserId(Long userId) {
        String hql = "FROM Customer WHERE user.userId = :userId";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setParameter("userId", userId);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding customer by user ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Search customers by name
     */
    public List<Customer> searchByName(String searchTerm) {
        String hql = "FROM Customer WHERE LOWER(firstName) LIKE :term OR LOWER(lastName) LIKE :term";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setParameter("term", "%" + searchTerm.toLowerCase() + "%");
            return query.list();
        } catch (Exception e) {
            logger.error("Error searching customers: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get top customers by hours played
     */
    public List<Customer> getTopCustomersByHoursPlayed(int limit) {
        String hql = "FROM Customer ORDER BY totalHoursPlayed DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setMaxResults(limit);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting top customers: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Get customers with low balance
     */
    public List<Customer> getCustomersWithLowBalance(double threshold) {
        String hql = "FROM Customer WHERE accountBalance < :threshold";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setParameter("threshold", threshold);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting customers with low balance: {}", e.getMessage());
            return List.of();
        }
    }
    
    // In CustomerDAO (no need for custom delete method unless necessary)
    public void deleteCustomer(Long customerId) {
        super.delete(customerId); // Calls the inherited delete method from GenericDAOImpl
    }

}

