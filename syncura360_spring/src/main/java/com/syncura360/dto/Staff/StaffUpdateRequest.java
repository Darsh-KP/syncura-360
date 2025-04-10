package com.syncura360.dto.Staff;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for representing a request to update staff information, containing a list of staff update details.
 * Each update contains a username and a map of fields to be updated.
 *
 * @author Benjamin Leiby
 */
@Data
public class StaffUpdateRequest {
    @Data
    public static class StaffUpdateDto {
        private String username;
        private Map<String, Object> fields;
    }

    private List<StaffUpdateRequest.StaffUpdateDto> updates;
}