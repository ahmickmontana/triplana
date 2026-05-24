package com.triplana.backend.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "trip_days")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TripDay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;
}
