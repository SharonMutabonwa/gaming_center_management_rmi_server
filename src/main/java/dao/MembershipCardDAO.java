/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.time.LocalDate;
import java.util.List;
import model.MembershipCard;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * MembershipCardDAO
 */
public class MembershipCardDAO extends GenericDAOImpl<MembershipCard, Long> {
    
    public MembershipCardDAO() {
        super(MembershipCard.class);
    }

    public MembershipCard findByCardNumber(String cardNumber) {
        String hql = "FROM MembershipCard WHERE cardNumber = :cardNumber";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MembershipCard> query = session.createQuery(hql, MembershipCard.class);
            query.setParameter("cardNumber", cardNumber);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding membership card: {}", e.getMessage());
            return null;
        }
    }

    public MembershipCard findByCustomerId(Long customerId) {
        String hql = "FROM MembershipCard WHERE customer.customerId = :customerId";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MembershipCard> query = session.createQuery(hql, MembershipCard.class);
            query.setParameter("customerId", customerId);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error finding membership card by customer: {}", e.getMessage());
            return null;
        }
    }

    public List<MembershipCard> getExpiringCards(int daysAhead) {
        String hql = "FROM MembershipCard WHERE expiryDate BETWEEN :today AND :futureDate AND isActive = true";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MembershipCard> query = session.createQuery(hql, MembershipCard.class);
            query.setParameter("today", LocalDate.now());
            query.setParameter("futureDate", LocalDate.now().plusDays(daysAhead));
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding expiring cards: {}", e.getMessage());
            return List.of();
        }
    }
}

