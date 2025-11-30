package ru.local.service.predict;

import ru.local.dto.MatchPredictDto;

public interface MatchPredictService {
    public MatchPredictDto predictedResultMatch(String homeTeam, String awayTeam, int lastMatchesToAnalyze);
}
