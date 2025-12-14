package ru.local.service.match;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.local.dto.MatchDto;
import ru.local.exception.NotFoundException;
import ru.local.exception.NotValidValueException;
import ru.local.mapper.MatchMapper;
import ru.local.model.Team;
import ru.local.repository.MatchRepository;
import ru.local.repository.TeamRepository;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MatchServiceImpl implements MatchService{
    final MatchRepository matchRepository;
    final TeamRepository teamRepository;
    final MatchMapper matchMapper;

    public List<MatchDto> getRecentMatchByTeam(String teamName, Integer countLastMatches) {
        Team team = teamRepository.findByName(teamName)
                .orElseThrow(()-> new NotFoundException(String.format("Команды - %s не существует", teamName)));

        if (countLastMatches <= 0) {
            throw new NotValidValueException(String.format("Неккоректное количество матчей: %d", countLastMatches));
        }

        return matchRepository.getRecentMatchesByTeam(team.getId(), countLastMatches).stream()
                .map(matchMapper::toMatchDto)
                .toList();
    }
}
