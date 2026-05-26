package com.triplana.backend.dto.response;

import com.triplana.backend.entity.TripDay;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TripDayResponse {
    private Long id;
    private LocalDate date;
    private Integer dayNumber;

    public static TripDayResponse from(TripDay tripDay) {
        TripDayResponse response = new TripDayResponse();
        response.setId(tripDay.getId());
        response.setDate(tripDay.getDate());
        response.setDayNumber(tripDay.getDayNumber());
        return response;
    }
}