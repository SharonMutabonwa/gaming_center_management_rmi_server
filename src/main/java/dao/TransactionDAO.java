/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.time.LocalDate;
import java.util.List;
import model.Transaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * TransactionDAO
 */
public class TransactionDAO extends GenericDAOImpl<Transaction, Long> {
    
    public TransactionDAO() {
        super(Transaction.class);
    }

    public List<Transaction> findByCustomerId(Long customerId) {
        String hql = "FROM Transaction WHERE customer.customerId = :customerId ORDER BY transactionDate DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery(hql, Transaction.class);
            query.setParameter("customerId", customerId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding transactions by customer: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String hql = "FROM Transaction WHERE DATE(transactionDate) BETWEEN :startDate AND :endDate";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery(hql, Transaction.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding transactions by date range: {}", e.getMessage());
            return List.of();
        }
    }
}

