package com.triplana.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "accommodations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Accommodation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "google_place_id", length = 255)
    private String googlePlaceId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
