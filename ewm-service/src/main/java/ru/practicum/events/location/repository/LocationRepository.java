package ru.practicum.events.location.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.events.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}