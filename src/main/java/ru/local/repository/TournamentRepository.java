package ru.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.local.model.Tournament;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Optional<Tournament> findByName(String name);
}
