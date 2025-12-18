/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Odeth
 */

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * StationGame Entity - MANY-TO-MANY Relationship Junction Table
 * Represents the relationship between GamingStation and Game
 * One Station can have Many Games installed
 * One Game can be installed on Many Stations
 */
@Entity
@Table(name = "station_games", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "game_id"}))
public class StationGame implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_game_id")
    private Long stationGameId;

    // Many-to-One with GamingStation
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private GamingStation gamingStation;

    // Many-to-One with Game
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "installed_date", nullable = false)
    private LocalDate installedDate;

    @Column(name = "version", length = 20)
    private String version;

    // Constructors
    public StationGame() {
        this.installedDate = LocalDate.now();
    }

    public StationGame(GamingStation gamingStation, Game game, String version) {
        this();
        this.gamingStation = gamingStation;
        this.game = game;
        this.version = version;
    }

    // Business Methods
    public boolean isRecentlyInstalled() {
        return LocalDate.now().minusMonths(1).isBefore(installedDate);
    }

    // Getters and Setters
    public Long getStationGameId() {
        return stationGameId;
    }

    public void setStationGameId(Long stationGameId) {
        this.stationGameId = stationGameId;
    }

    public GamingStation getGamingStation() {
        return gamingStation;
    }

    public void setGamingStation(GamingStation gamingStation) {
        this.gamingStation = gamingStation;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDate getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(LocalDate installedDate) {
        this.installedDate = installedDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "StationGame{" +
                "stationGameId=" + stationGameId +
                ", gamingStation=" + gamingStation.getStationName() +
                ", game=" + game.getGameTitle() +
                ", installedDate=" + installedDate +
                ", version='" + version + '\'' +
                '}';
    }
}
