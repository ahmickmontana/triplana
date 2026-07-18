package com.triplana.backend.validation;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.triplana.backend.entity.Activity;
import com.triplana.backend.exception.AuthException;

@Component
public class ActivityValidator {
    
    public void validateTimes(LocalTime startTime, LocalTime endTime, List<Activity> existingActivities, Long excludeId) {
        if (endTime != null && startTime == null) {
            throw new AuthException("endTime", "End time requires a start time.");
        }

        if (endTime != null && startTime != null && !endTime.isAfter(startTime)) {
            throw new AuthException("endTime", "End time must occur after the start time.");
        }

        if (startTime != null) {
            for (Activity existing : existingActivities) {
                if (excludeId != null && existing.getId().equals(excludeId)) continue;

                if (existing.getStartTime() != null && existing.getStartTime().equals(startTime)) {
                    throw new AuthException("startTime", "An activity is already planned at that time.");
                }

                if (existing.getStartTime() != null && existing.getEndTime() != null) {
                    if (startTime.isAfter(existing.getStartTime()) && startTime.isBefore(existing.getEndTime())) {
                        throw new AuthException("startTime", "An activity is already planned at that time.");
                    }
                    
                    if (endTime != null && startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                        throw new AuthException("endTime", "This time conflicts with another activity.");
                    }
                }
            }
        }
    }
}
