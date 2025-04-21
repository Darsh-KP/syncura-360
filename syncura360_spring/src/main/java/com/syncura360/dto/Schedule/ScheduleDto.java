package com.syncura360.dto.Schedule;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for representing a schedule, including a message and a list of scheduled shifts.
 *
 * @author Benjamin Leiby
 */
@Data
public class ScheduleDto {
    private String message;
    private List<ShiftDto> scheduledShifts;

    public ScheduleDto() {
        scheduledShifts = new ArrayList<>();
    }
    public ScheduleDto(String message) {
        scheduledShifts = new ArrayList<>();
        this.message = message;
    }

    public ScheduleDto(String message, List<ShiftDto> shifts) {
        this.message = message;
        this.scheduledShifts = shifts;
    }
}
