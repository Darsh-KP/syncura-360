package com.syncura360.dto.Schedule;

import lombok.Data;

/**
 * DTO for updating a shift, including a schedule identifier and the updated shift details.
 * The identifier is based on the username and the shift start time.
 *
 * @author Benjamin Leiby
 */
@Data
public class ShiftUpdateDto {
    private ScheduleIdDto id;
    private ShiftDto updates;

    public record ScheduleIdDto(String username, String start) {}
}
