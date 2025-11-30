package ru.local.service.stats;

import ru.local.model.TeamStats;

public interface StatsCalculationService {
    public TeamStats calculateTeamStats(Long teamId, int lastMatchesCount);
}
