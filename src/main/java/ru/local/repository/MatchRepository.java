package ru.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.local.model.Match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByHomeTeamIdAndAwayTeamIdAndMatchDate(Long homeTeamId, Long awayTeamId, LocalDateTime matchDate);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId " +
            "ORDER BY m.matchDate DESC LIMIT :lastMatchesCount")
    List<Match> getRecentMatchesByTeam(@Param("teamId") Long teamId, @Param("lastMatchesCount") int lastMatchesCount);

    @Query("SELECT m FROM Match m ORDER BY m.matchDate DESC LIMIT :countMatches")
    List<Match> getRecentMatches(@Param("countMatches") int countMatches);
}
