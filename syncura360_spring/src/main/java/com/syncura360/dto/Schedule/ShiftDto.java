package com.syncura360.dto.Schedule;

import lombok.Data;

/**
 * DTO for representing a shift, including details such as the username of the employee,
 * shift start and end times, and the department where the shift occurs.
 *
 * @author Benjamin Leiby
 */
@Data
public class ShiftDto {
    private String username;
    private String start;
    private String end;
    private String department;
}