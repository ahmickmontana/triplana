package com.triplana.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.triplana.backend.dto.request.CreateActivityRequest;
import com.triplana.backend.dto.response.ActivityResponse;
import com.triplana.backend.entity.Activity;
import com.triplana.backend.entity.TripDay;
import com.triplana.backend.exception.AuthException;
import com.triplana.backend.repository.ActivityRepository;
import com.triplana.backend.repository.TripDayRepository;
import com.triplana.backend.validation.ActivityValidator;

import lombok.*;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TripDayRepository tripDayRepository;
    private final ActivityValidator activityValidator;

    public ActivityResponse createActivity(Long dayId, CreateActivityRequest request, Long userId) {
        TripDay tripDay = tripDayRepository.findById(dayId)
            .orElseThrow(() -> new AuthException("Trip day not found."));

        if (!tripDay.getTrip().getUser().getId().equals(userId)) {
            throw new AuthException("You do not have permission to view this trip day");
        }

        List<Activity> activities = activityRepository.findAllByTripDayIdOrderByManualOrderAsc(dayId);

        activityValidator.validateTimes(request.getStartTime(), request.getEndTime(), activities, null);

        Activity activity = Activity.builder()
            .tripDay(tripDay)
            .title(request.getTitle())
            .description(request.getDescription())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .locationName(request.getLocationName())
            .manualOrder(activities.size() + 1)
            .build();

        activityRepository.save(activity);

        return ActivityResponse.from(activity);
    }
}