package ru.local.service.team;

import ru.local.dto.TeamDto;
import ru.local.model.Team;

import java.util.List;

public interface TeamService {
    public List<TeamDto> getAllTeams();
}
