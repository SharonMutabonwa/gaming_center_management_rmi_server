/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.time.LocalDate;
import java.util.List;
import model.Tournament;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * TournamentDAO
 */
public class TournamentDAO extends GenericDAOImpl<Tournament, Long> {
    
    public TournamentDAO() {
        super(Tournament.class);
    }

    public List<Tournament> getUpcomingTournaments() {
        String hql = "FROM Tournament WHERE status IN ('UPCOMING', 'REGISTRATION_OPEN') " +
                     "AND startDate >= :today ORDER BY startDate ASC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tournament> query = session.createQuery(hql, Tournament.class);
            query.setParameter("today", LocalDate.now());
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting upcoming tournaments: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Tournament> getOngoingTournaments() {
        String hql = "FROM Tournament WHERE status = 'ONGOING'";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tournament> query = session.createQuery(hql, Tournament.class);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting ongoing tournaments: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Tournament> findByGameId(Long gameId) {
        String hql = "FROM Tournament WHERE game.gameId = :gameId ORDER BY startDate DESC";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Tournament> query = session.createQuery(hql, Tournament.class);
            query.setParameter("gameId", gameId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding tournaments by game: {}", e.getMessage());
            return List.of();
        }
    }
}
