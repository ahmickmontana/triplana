package com.triplana.backend.entity;

import java.sql.Time;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "activities")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_day_id", nullable = false)
    private TripDay tripDay;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "google_place_id", length = 255)
    private String googlePlaceId;

    @Column(name = "manual_order")
    private Integer manualOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
