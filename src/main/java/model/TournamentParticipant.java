/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Odeth
 */
public /**
 * TournamentParticipant Entity - MANY-TO-MANY Junction Table
 * Represents the relationship between Tournament and Customer
 * One Tournament can have Many Participants
 * One Customer can participate in Many Tournaments
 */
@Entity
@Table(name = "tournament_participants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "customer_id"}))
class TournamentParticipant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    // Many-to-One with Tournament
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // Many-to-One with Customer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "team_name", length = 100)
    private String teamName;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "prize_won", precision = 10, scale = 2)
    private BigDecimal prizeWon = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipantStatus status = ParticipantStatus.REGISTERED;

    // Constructors
    public TournamentParticipant() {
        this.registrationDate = LocalDateTime.now();
    }

    public TournamentParticipant(Tournament tournament, Customer customer) {
        this();
        this.tournament = tournament;
        this.customer = customer;
    }

    public TournamentParticipant(Tournament tournament, Customer customer, String teamName) {
        this(tournament, customer);
        this.teamName = teamName;
    }

    // Business Methods
    public boolean isWinner() {
        return status == ParticipantStatus.WINNER;
    }

    public void setAsWinner(Integer rank, BigDecimal prize) {
        this.status = ParticipantStatus.WINNER;
        this.rankPosition = rank;
        this.prizeWon = prize;
    }

    public void eliminate() {
        this.status = ParticipantStatus.ELIMINATED;
    }

    public void withdraw() {
        this.status = ParticipantStatus.WITHDRAWN;
    }

    public void confirm() {
        this.status = ParticipantStatus.CONFIRMED;
    }

    // Getters and Setters
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public BigDecimal getPrizeWon() {
        return prizeWon;
    }

    public void setPrizeWon(BigDecimal prizeWon) {
        this.prizeWon = prizeWon;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TournamentParticipant{" +
                "participantId=" + participantId +
                ", tournament=" + tournament.getTournamentName() +
                ", customer=" + customer.getFullName() +
                ", teamName='" + teamName + '\'' +
                ", status=" + status +
                '}';
    }
}