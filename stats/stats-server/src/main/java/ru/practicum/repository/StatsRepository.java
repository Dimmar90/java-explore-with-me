package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.StatsRecord;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatsRecord, Long> {

    @Query("SELECT new ru.practicum.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM StatsRecord s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> getStatsByUniqueWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM StatsRecord s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> getStatsWithoutUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM StatsRecord s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> getStatsByUniqueWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM StatsRecord s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
