package ru.local.service.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.local.model.TeamStats;
import ru.local.model.Match;
import ru.local.repository.MatchRepository;
import ru.local.repository.TeamRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StatsCalculationServiceImpl implements StatsCalculationService {
    final MatchRepository matchRepository;

    @Override
    public TeamStats calculateTeamStats(Long teamId, int lastMatchesCount) {
        List<Match> recentMatches = getRecentMatches(teamId, lastMatchesCount);

        int countMatches = recentMatches.size();

        TeamStats stats = TeamStats.builder()
                .matchesPlayed(countMatches)
                .build();

        int homeMatchesCount = 0;
        int awayMatchesCount = 0;
        int totalGoalsScored = 0;
        int totalGoalsConceded = 0;
        int wins = 0, draws = 0, losses = 0;
        int homeGoalsScored = 0, awayGoalsScored = 0;
        int homeGoalsConceded = 0, awayGoalsConceded = 0;

        for (Match match : recentMatches) {
            boolean isHomeTeam = match.getHomeTeam().getId().equals(teamId);
            int goalsScored = isHomeTeam ? match.getHomeGoals() : match.getAwayGoals();
            int goalsConceded = isHomeTeam ? match.getAwayGoals() : match.getHomeGoals();

            totalGoalsScored += goalsScored;
            totalGoalsConceded += goalsConceded;

            if(isHomeTeam) {
                homeGoalsScored += goalsScored;
                homeGoalsConceded += goalsConceded;
                homeMatchesCount++;
            } else {
                awayGoalsScored += goalsScored;
                awayGoalsConceded += goalsConceded;
                awayMatchesCount++;
            }

            if (goalsScored > goalsConceded) {
                wins++;
            } else if (goalsScored == goalsConceded) {
                draws++;
            } else {
                losses++;
            }
        }

        stats.setAverageGoalsScored((double) totalGoalsScored / countMatches);
        stats.setAverageGoalsConceded((double) totalGoalsConceded / countMatches);
        stats.setWinPercentage((double) wins / countMatches);
        stats.setDrawPercentage((double) draws / countMatches);
        stats.setLossPercentage((double) losses / countMatches);

        stats.setHomeAttackStrength((double) homeGoalsScored / Math.max(1, homeMatchesCount));
        stats.setAwayAttackStrength((double) awayGoalsScored / Math.max(1, awayMatchesCount));
        stats.setHomeDefenseStrength((double) homeGoalsConceded / Math.max(1, homeMatchesCount));
        stats.setAwayDefenseStrength((double) awayGoalsConceded / Math.max(1, awayMatchesCount));

        return stats;
    }

    private List<Match> getRecentMatches(Long teamId, int lastMatchesCount) {
        return matchRepository.getRecentMatchesByTeam(teamId, lastMatchesCount);
    }
}
