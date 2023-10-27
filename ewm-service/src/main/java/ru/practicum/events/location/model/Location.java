package ru.practicum.events.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;
}