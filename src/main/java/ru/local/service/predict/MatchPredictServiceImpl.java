package ru.local.service.predict;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.local.dto.MatchPredictDto;
import ru.local.exception.NotValidValueException;
import ru.local.mapper.MatchPredictMapper;
import ru.local.model.MatchPredict;
import ru.local.model.TeamStats;
import ru.local.exception.NotFoundException;
import ru.local.model.Team;
import ru.local.repository.TeamRepository;
import ru.local.service.stats.StatsCalculationServiceImpl;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class MatchPredictServiceImpl implements MatchPredictService {
    final TeamRepository teamRepository;
    final StatsCalculationServiceImpl statsCalculationService;
    final MatchPredictMapper matchPredictMapper;

    @Override
    public MatchPredictDto predictedResultMatch(String homeTeam, String awayTeam, int lastMatchesToAnalyze) {
        if (lastMatchesToAnalyze <= 0) {
            throw new NotValidValueException(String.format("Неккоректное количество матчей: %d", lastMatchesToAnalyze));
        }

        Team teamHome = getTeamOrThrow(homeTeam);
        Team teamAway = getTeamOrThrow(awayTeam);

        TeamStats homeTeamStats = statsCalculationService.calculateTeamStats(teamHome.getId(), lastMatchesToAnalyze);
        TeamStats awayTeamStats = statsCalculationService.calculateTeamStats(teamAway.getId(), lastMatchesToAnalyze);

        log.info("Статистика команды {}: /n{}", teamHome.getName(), homeTeamStats.toString());
        log.info("Статистика команды {}: /n{}", teamAway.getName(), awayTeamStats.toString());

        double homeAttack = homeTeamStats.getHomeAttackStrength();
        double homeDefense = homeTeamStats.getHomeDefenseStrength();

        double awayAttack = awayTeamStats.getAwayAttackStrength();
        double awayDefense = awayTeamStats.getAwayDefenseStrength();

        double expectedHomeGoals = homeAttack + (1 - awayDefense);
        double expectedAwayGoals = awayAttack + (1 - homeDefense);

        MatchPredictDto matchPredictDto = matchPredictMapper
                .toMatchPredictDto(calculatePoissonPredict(expectedHomeGoals, expectedAwayGoals));

        log.info("Прогноз на матч: {}", matchPredictDto.toString());

        return matchPredictDto;
    }

    private MatchPredict calculatePoissonPredict(double lambdaHome, double lambdaAway) {
        double homeWinProb = 0;
        double drawProb = 0;
        double awayWinProb = 0;

        Map<String, Double> scoreProbabilities = new HashMap<>();

        for (int homeGoals = 0; homeGoals <= 5; homeGoals++) {
            for (int awayGoals = 0; awayGoals <= 5; awayGoals++) {
                double prob = poissonProbability(homeGoals, lambdaHome) * poissonProbability(awayGoals, lambdaAway);
                scoreProbabilities.put(homeGoals + " - " + awayGoals, prob);

                if (homeGoals > awayGoals) {
                    homeWinProb += prob;
                } else if (homeGoals == awayGoals) {
                    drawProb += prob;
                } else {
                    awayWinProb += prob;
                }
            }
        }

        double total = homeWinProb + drawProb + awayWinProb;
        double normalizationFactor = 1 / total;

        return MatchPredict.builder()
                .homeWin(homeWinProb * normalizationFactor)
                .draw(drawProb * normalizationFactor)
                .awayWin(awayWinProb * normalizationFactor)
                .homeExpectedGoals(lambdaHome)
                .awayExpectedGoals(lambdaAway)
                .scoreProbabilities(scoreProbabilities)
                .build();
    }

    private double poissonProbability(int k, double lambda) {
        return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
    }

    private long factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    private Team getTeamOrThrow(String team) {
        return teamRepository.findByName(team)
                .orElseThrow(() -> new NotFoundException(String.format("Команда %s не найдена", team)));
    }
}
