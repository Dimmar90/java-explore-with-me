package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.model.StatsRecord;

public interface StatsRepository extends JpaRepository<StatsRecord, Long> {
}
