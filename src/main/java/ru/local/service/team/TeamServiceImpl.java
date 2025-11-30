package ru.local.service.team;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.local.dto.TeamDto;
import ru.local.mapper.TeamMapper;
import ru.local.model.Team;
import ru.local.repository.TeamRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements TeamService{
    final TeamRepository teamRepository;
    final TeamMapper teamMapper;

    @Override
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(teamMapper::toTeamDto)
                .toList();
    }
}
