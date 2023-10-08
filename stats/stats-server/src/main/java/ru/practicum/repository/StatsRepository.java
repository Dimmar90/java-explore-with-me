package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.StatsRecord;

public interface StatsRepository extends JpaRepository<StatsRecord, Long> {
}
