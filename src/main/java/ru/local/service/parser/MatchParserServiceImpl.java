package ru.local.service.parser;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.local.model.MatchData;
import ru.local.model.Match;
import ru.local.model.Team;
import ru.local.model.Tournament;
import ru.local.repository.MatchRepository;
import ru.local.repository.TeamRepository;
import ru.local.repository.TournamentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MatchParserServiceImpl implements MatchParserService {
    final TeamRepository teamRepository;
    final TournamentRepository tournamentRepository;
    final MatchRepository matchRepository;
    final static String TEAM_EXIST_MESSAGE = "Команда {} уже существует в БД";

    @Override
    public void parseMatchesFromHtml(String htmlContent) {
        List<MatchData> matches = new ArrayList<>();

        Document document = Jsoup.parse(htmlContent);
        Elements gameBlocks = document.select("div.game_block");

        log.info("Найдено game_block: {}", gameBlocks.size());

        for (Element gameBlock : gameBlocks) {
            MatchData match = parseSingleGameBlock(gameBlock);
            matches.add(match);
        }

        saveDataMatches(matches);
    }

    private void saveDataMatches(List<MatchData> matches) {
        for (MatchData matchData : matches) {
            Team homeTeam = Team.builder()
                    .name(matchData.getHomeTeam())
                    .build();

            Team awayTeam = Team.builder()
                    .name(matchData.getAwayTeam())
                    .build();

            Tournament tournament = Tournament.builder()
                    .name(matchData.getTournament())
                    .build();

            try {
                homeTeam = teamRepository.save(homeTeam);
            } catch (DataIntegrityViolationException e) {
                log.info(TEAM_EXIST_MESSAGE, matchData.getHomeTeam());
                homeTeam = teamRepository.findByName(matchData.getHomeTeam())
                        .orElseThrow(() -> new RuntimeException("Команда не найдена"));
            }

            try {
                awayTeam = teamRepository.save(awayTeam);
            } catch (DataIntegrityViolationException e) {
                log.info(TEAM_EXIST_MESSAGE, matchData.getAwayTeam());
                awayTeam = teamRepository.findByName(matchData.getAwayTeam())
                        .orElseThrow(() -> new RuntimeException("Команда не найдена"));
            }

            try {
                tournament = tournamentRepository.save(tournament);
            } catch (DataIntegrityViolationException e) {
                log.info("Турнир {} уже существует в БД", matchData.getHomeTeam());
                tournament = tournamentRepository.findByName(matchData.getTournament())
                        .orElseThrow(() -> new RuntimeException("Турнир не найден"));
            }

            Match match = Match.builder()
                    .homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .homeGoals(matchData.getHomeGoals())
                    .awayGoals(matchData.getAwayGoals())
                    .matchDate(matchData.getMatchDate())
                    .tournament(tournament)
                    .build();

            if (existMatch(match).isEmpty()) {
                matchRepository.save(match);
            }
        }
    }

    private MatchData parseSingleGameBlock(Element gameBlock) {
        MatchData match = new MatchData();

        Element dateElement = gameBlock.select("div.status span.size10").first();
        if (dateElement != null) {
            match.setMatchDate(parseDate(dateElement.text()));
        }

        Element tournamentElement = gameBlock.select("div.cmp span").first();
        if (tournamentElement != null) {
            match.setTournament(tournamentElement.text());
        }

        Element homeTeamElement = gameBlock.select("div.ht div.name span").first();
        Element homeGoalsElement = gameBlock.select("div.ht div.gls").first();

        if (homeTeamElement != null) {
            match.setHomeTeam(homeTeamElement.text());
        }
        if (homeGoalsElement != null) {
            match.setHomeGoals(parseGoals(homeGoalsElement.text()));
        }

        Element awayTeamElement = gameBlock.select("div.at div.name span").first();
        Element awayGoalsElement = gameBlock.select("div.at div.gls").first();

        if (awayTeamElement != null) {
            match.setAwayTeam(awayTeamElement.text());
        }
        if (awayGoalsElement != null) {
            match.setAwayGoals(parseGoals(awayGoalsElement.text()));
        }

        return match;
    }

    private LocalDateTime parseDate(String dateString) {
        dateString = dateString.trim();

        LocalDateTime result;

        if (dateString.matches("\\d{2}\\.\\d{2}\\.\\d{2},\\s*\\d{2}:\\d{2}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy, HH:mm");
            result = LocalDateTime.parse(dateString, formatter);
        } else if (dateString.matches("\\d{2}\\.\\d{2},\\s*\\d{2}:\\d{2}")) {
            String currentYear = String.valueOf(LocalDateTime.now().getYear());
            String fullDateString = dateString + "." + currentYear;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM, HH:mm.yyyy");
            result = LocalDateTime.parse(fullDateString, formatter);
        } else {
            log.warn("Неизвестный формат даты: {}", dateString);
            return null;
        }

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = result.format(outputFormatter);

        return result;
    }

    private Integer parseGoals(String goalsText) {
        return Integer.parseInt(goalsText.trim());
    }

    private Optional<Match> existMatch(Match match) {
        return matchRepository.findByHomeTeamIdAndAwayTeamIdAndMatchDate(match.getHomeTeam().getId(),
                match.getAwayTeam().getId(), match.getMatchDate());
    }
}
