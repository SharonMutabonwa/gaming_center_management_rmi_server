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
import org.hibernate.query.Query;
import model.*;
import utils.HibernateUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * GamingStationDAO
 */
public class GamingStationDAO extends GenericDAOImpl<GamingStation, Long> {
    
    public GamingStationDAO() {
        super(GamingStation.class);
    }

    public List<GamingStation> findByStatus(StationStatus status) {
        String hql = "FROM GamingStation WHERE status = :status";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GamingStation> query = session.createQuery(hql, GamingStation.class);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding stations by status: {}", e.getMessage());
            return List.of();
        }
    }

    public List<GamingStation> findByType(StationType type) {
        String hql = "FROM GamingStation WHERE stationType = :type";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GamingStation> query = session.createQuery(hql, GamingStation.class);
            query.setParameter("type", type);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding stations by type: {}", e.getMessage());
            return List.of();
        }
    }

    public List<GamingStation> getAvailableStations() {
        return findByStatus(StationStatus.AVAILABLE);
    }

    public List<GamingStation> getStationsNeedingMaintenance() {
        String hql = "FROM GamingStation WHERE nextMaintenanceDate <= :today";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GamingStation> query = session.createQuery(hql, GamingStation.class);
            query.setParameter("today", LocalDate.now());
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding stations needing maintenance: {}", e.getMessage());
            return List.of();
        }
    }
}


