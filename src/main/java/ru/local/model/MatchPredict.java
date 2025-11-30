package ru.local.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchPredict {
    double homeWin;
    double draw;
    double awayWin;
    double homeExpectedGoals;
    double awayExpectedGoals;
    Map<String, Double> scoreProbabilities;
}
