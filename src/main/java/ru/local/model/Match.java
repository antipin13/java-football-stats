package ru.local.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "matches")
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    Team awayTeam;

    @JoinColumn(name = "home_goals")
    Integer homeGoals;

    @JoinColumn(name = "away_goals")
    Integer awayGoals;

    @JoinColumn(name = "match_date")
    LocalDateTime matchDate;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    Tournament tournament;
}
