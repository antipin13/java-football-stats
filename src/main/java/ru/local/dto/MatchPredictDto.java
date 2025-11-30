package ru.local.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class MatchPredictDto {
    Double homeWinChance;
    Double drawChance;
    Double awayWinChance;
    Double homeExpectedGoals;
    Double awayExpectedGoals;
    Map<String, Double> scoreProbabilities = new HashMap<>();
}
