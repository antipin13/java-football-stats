package ru.local.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.local.dto.MatchDto;
import ru.local.service.match.MatchServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/match")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchController {
    final MatchServiceImpl matchService;

    @GetMapping
    public List<MatchDto> getRecentMatchesByTeam(@RequestParam String teamName,
                                                 @RequestParam Integer countLastMatches) {
        log.info("{} последних матчей для команды - {}", countLastMatches, teamName);
        return matchService.getRecentMatchByTeam(teamName, countLastMatches);
    }
}
