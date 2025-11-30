package ru.local.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamStats {
    int matchesPlayed;
    double averageGoalsScored;
    double averageGoalsConceded;
    double winPercentage;
    double drawPercentage;
    double lossPercentage;
    double homeAttackStrength;
    double awayAttackStrength;
    double homeDefenseStrength;
    double awayDefenseStrength;
}
