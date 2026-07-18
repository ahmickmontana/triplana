package com.triplana.backend.repository;

import com.triplana.backend.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByTripDayIdOrderByManualOrderAsc(Long tripDayId);
}