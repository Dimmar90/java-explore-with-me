package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.StatsRecord;
import ru.practicum.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatsRecord, Long> {

    @Query(value = "SELECT s.app, s.uri, COUNT(s.ip) as hits " +
            "FROM stats AS s " +
            "WHERE ((?1) IS NULL OR s.uri IN (?1)) " +
            "AND (s.timestamp BETWEEN ?2 AND ?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStat> findStatsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) as hits " +
            "FROM stats AS s " +
            "WHERE ((?1) IS NULL OR s.uri IN (?1)) " +
            "AND (s.timestamp BETWEEN ?2 AND ?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStat> findUniqueStatsByUri(List<String> uris, LocalDateTime start, LocalDateTime end);
}
