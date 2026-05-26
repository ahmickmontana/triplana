package com.triplana.backend.dto.response;

import com.triplana.backend.entity.Activity;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class ActivityResponse {
    private Long id;
    private String title;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;
    private Integer manualOrder;
    private LocalDateTime createdAt;

    public static ActivityResponse from(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setTitle(activity.getTitle());
        response.setDescription(activity.getDescription());
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setLocationName(activity.getLocationName());
        response.setLatitude(activity.getLatitude());
        response.setLongitude(activity.getLongitude());
        response.setGooglePlaceId(activity.getGooglePlaceId());
        response.setManualOrder(activity.getManualOrder());
        response.setCreatedAt(activity.getCreatedAt());
        return response;
    }
}