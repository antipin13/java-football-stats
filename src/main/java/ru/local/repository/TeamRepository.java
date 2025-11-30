package ru.local.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.local.model.Team;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
}
