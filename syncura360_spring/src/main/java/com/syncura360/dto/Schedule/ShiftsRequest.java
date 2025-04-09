package com.syncura360.dto.Schedule;

import lombok.Data;

import java.util.List;

/**
 * DTO for representing a request containing a list of shifts.
 *
 * @author Benjamin Leiby
 */
@Data
public class ShiftsRequest {
    private List<ShiftDto> shifts;
}
