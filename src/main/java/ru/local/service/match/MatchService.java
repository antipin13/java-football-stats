package ru.local.service.match;

import ru.local.dto.MatchDto;

import java.util.List;

public interface MatchService {
    List<MatchDto> getRecentMatchByTeam(String teamName, Integer countMatches);
}
