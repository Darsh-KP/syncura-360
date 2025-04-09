package com.syncura360.dto.Staff;

import java.util.List;

import lombok.Data;

/**
 * DTO for representing a request to create multiple staff members, containing a list of staff creation details.
 *
 * @author Benjamin Leiby
 */
@Data
public class StaffCreationRequest {
    private List<StaffCreationDto> staff;
}

