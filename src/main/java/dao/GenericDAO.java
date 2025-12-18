/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Odeth
 */

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HibernateUtil;

import java.io.Serializable;
import java.util.List;
import model.Customer;
import org.hibernate.HibernateException;

/**
 * GenericDAO Interface - DAO Design Pattern
 * Defines CRUD operations for all entities
 */
public interface GenericDAO<T, ID extends Serializable> {
    T save(T entity);
    T update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
    long count();
}

/**
 * GenericDAOImpl - Abstract Implementation
 * Provides common CRUD operations using Hibernate
 */
abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<T> entityClass;

    public GenericDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            logger.info("{} saved successfully: {}", entityClass.getSimpleName(), entity);
            return entity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to save " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public T update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T updatedEntity = session.merge(entity);
            transaction.commit();
            logger.info("{} updated successfully: {}", entityClass.getSimpleName(), entity);
            return updatedEntity;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to update " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public void delete(ID id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                Customer customer = (Customer) entity;

                    // Explicitly clear associations if needed
                if (entity instanceof Customer) {

                    // Remove associations before deleting customer
                    customer.getBookings().clear();
                    customer.getTransactions().clear();
                    customer.getTournamentParticipations().clear();
                    if (customer.getMembershipCard() != null) {
                        session.remove(customer.getMembershipCard());
                    }
                }
                session.remove(entity);
                logger.info("{} deleted successfully with ID: {}", entityClass.getSimpleName(), id);
            } else {
                logger.warn("{} with ID {} not found for deletion", entityClass.getSimpleName(), id);
            }
            transaction.commit();
        }
        catch (HibernateException he) {
        if (transaction != null) {
            transaction.rollback();
        }
        logger.error("Hibernate exception while deleting customer: ", he);
        throw he;
//        }catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            logger.error("Error deleting {} with ID {}: {}", entityClass.getSimpleName(), id, e.getMessage());
//            throw new RuntimeException("Failed to delete " + entityClass.getSimpleName(), e);
        }
    }
    
//    try {
//    // Attempt to delete
//    session.delete(customer);
//    transaction.commit(); // Commit the transaction
//} catch (HibernateException he) {
//    if (transaction != null) {
//        transaction.rollback();
//    }
//    logger.error("Hibernate exception while deleting customer: ", he);
//    throw he;
//} catch (Exception e) {
//    if (transaction != null) {
//        transaction.rollback();
//    }
//    logger.error("General exception while deleting customer: ", e);
//    throw new RuntimeException("Failed to delete customer", e);
//}


    @Override
    public T findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                logger.info("{} found with ID: {}", entityClass.getSimpleName(), id);
            } else {
                logger.warn("{} with ID {} not found", entityClass.getSimpleName(), id);
            }
            return entity;
        } catch (Exception e) {
            logger.error("Error finding {} by ID {}: {}", entityClass.getSimpleName(), id, e.getMessage());
            throw new RuntimeException("Failed to find " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM " + entityClass.getSimpleName();
            Query<T> query = session.createQuery(hql, entityClass);
            List<T> results = query.list();
            logger.info("Retrieved {} {}(s)", results.size(), entityClass.getSimpleName());
            return results;
        } catch (Exception e) {
            logger.error("Error finding all {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to find all " + entityClass.getSimpleName(), e);
        }
    }

    @Override
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(*) FROM " + entityClass.getSimpleName();
            Query<Long> query = session.createQuery(hql, Long.class);
            Long count = query.uniqueResult();
            logger.info("Total {} count: {}", entityClass.getSimpleName(), count);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error counting {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Failed to count " + entityClass.getSimpleName(), e);
        }
    }

    /**
     * Execute custom HQL query
     */
    protected List<T> executeQuery(String hql, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery(hql, entityClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            return query.list();
        } catch (Exception e) {
            logger.error("Error executing query: {}", e.getMessage());
            throw new RuntimeException("Failed to execute query", e);
        }
    }

    /**
     * Execute custom query returning single result
     */
    protected T executeQuerySingleResult(String hql, Object... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery(hql, entityClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            return query.uniqueResult();
        } catch (Exception e) {
            logger.error("Error executing query for single result: {}", e.getMessage());
            throw new RuntimeException("Failed to execute query", e);
        }
    }
}
