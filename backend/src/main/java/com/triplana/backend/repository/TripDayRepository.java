package com.triplana.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.triplana.backend.entity.TripDay;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
    List<TripDay> findAllByTripIdOrderByDayNumberAsc(Long tripId);
    List<TripDay> findAllByTripIdOrderByDateAsc(Long tripId);
}