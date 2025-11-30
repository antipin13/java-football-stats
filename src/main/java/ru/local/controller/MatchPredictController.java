package ru.local.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.local.dto.MatchPredictDto;
import ru.local.service.predict.MatchPredictServiceImpl;

@RestController
@RequestMapping("/match-predict")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MatchPredictController {
    final MatchPredictServiceImpl matchPredictService;

    @GetMapping
    public MatchPredictDto getMatchPredict(@RequestParam String homeTeam,
                                           @RequestParam String awayTeam,
                                           @RequestParam int countLastMatches) {
        return matchPredictService.predictedResultMatch(homeTeam, awayTeam, countLastMatches);
    }
}
