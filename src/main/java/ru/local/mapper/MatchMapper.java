package ru.local.mapper;

import org.springframework.stereotype.Component;
import ru.local.dto.MatchDto;
import ru.local.model.Match;

@Component
public class MatchMapper {
    public MatchDto toMatchDto(Match match) {
        return MatchDto.builder()
                .homeTeam(match.getHomeTeam().getName())
                .awayTeam(match.getAwayTeam().getName())
                .homeTeamGoals(match.getHomeGoals())
                .awayTeamGoals(match.getAwayGoals())
                .matchDate(match.getMatchDate())
                .tournament(match.getTournament().getName())
                .build();
    }
}
