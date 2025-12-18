/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.AgeRating;
import model.Game;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

/**
 *
 * @author Odeth
 */
/**
 * GameDAO
 */
public class GameDAO extends GenericDAOImpl<Game, Long> {
    
    public GameDAO() {
        super(Game.class);
    }

    public List<Game> findByGenre(String genre) {
        String hql = "FROM Game WHERE genre = :genre";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Game> query = session.createQuery(hql, Game.class);
            query.setParameter("genre", genre);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding games by genre: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Game> findByAgeRating(AgeRating rating) {
        String hql = "FROM Game WHERE ageRating = :rating";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Game> query = session.createQuery(hql, Game.class);
            query.setParameter("rating", rating);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding games by age rating: {}", e.getMessage());
            return List.of();
        }
    }

    public List<Game> searchByTitle(String title) {
        String hql = "FROM Game WHERE LOWER(gameTitle) LIKE :title";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Game> query = session.createQuery(hql, Game.class);
            query.setParameter("title", "%" + title.toLowerCase() + "%");
            return query.list();
        } catch (Exception e) {
            logger.error("Error searching games by title: {}", e.getMessage());
            return List.of();
        }
    }
}
