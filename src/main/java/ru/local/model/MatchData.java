package ru.local.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class MatchData {
    String homeTeam;
    String awayTeam;
    Integer homeGoals;
    Integer awayGoals;
    String tournament;
    LocalDateTime matchDate;
}
