package com.syncura360.dto.Schedule;

import lombok.Data;

/**
 * DTO for representing a request to fetch the staff schedule, including a start and end time.
 *
 * @author Benjamin Leiby
 */
@Data
public class StaffScheduleRequestDTO {
    private String start;
    private String end;
}
