package com.triplana.backend.repository;

import com.triplana.backend.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findAllByTripIdOrderByCheckInDateAsc(Long tripId);
}