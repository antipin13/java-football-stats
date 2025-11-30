package ru.local.mapper;

import org.springframework.stereotype.Component;
import ru.local.dto.TeamDto;
import ru.local.model.Team;

@Component
public class TeamMapper {
    public TeamDto toTeamDto(Team team) {
        return TeamDto.builder()
                .name(team.getName())
                .build();
    }
}
