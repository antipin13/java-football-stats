package ru.local.mapper;

import org.springframework.stereotype.Component;
import ru.local.dto.MatchPredictDto;
import ru.local.model.MatchPredict;

@Component
public class MatchPredictMapper {
    public MatchPredictDto toMatchPredictDto (MatchPredict matchPredict) {
        return MatchPredictDto.builder()
                .homeWinChance(matchPredict.getHomeWin())
                .drawChance(matchPredict.getDraw())
                .awayWinChance(matchPredict.getAwayWin())
                .homeExpectedGoals(matchPredict.getHomeExpectedGoals())
                .awayExpectedGoals(matchPredict.getAwayExpectedGoals())
                .scoreProbabilities(matchPredict.getScoreProbabilities())
                .build();
    }
}
