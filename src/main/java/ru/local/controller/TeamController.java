package ru.local.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.local.dto.TeamDto;
import ru.local.service.team.TeamServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/teams")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TeamController {
    final TeamServiceImpl teamService;

    @GetMapping
    public List<TeamDto> getAllTeams() {
        return teamService.getAllTeams();
    }
}
