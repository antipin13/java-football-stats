package ru.local.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchDto {
    String homeTeam;
    String awayTeam;
    Integer homeTeamGoals;
    Integer awayTeamGoals;
    LocalDateTime matchDate;
    String tournament;
}
