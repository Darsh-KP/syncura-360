package com.syncura360.dto.Staff;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for the response after creating staff members, containing a message and a list of the created staff usernames.
 *
 * @author Benjamin Leiby
 */
@Data
public class StaffCreationResponse {
    private String message;
    private List<String> staffUsernames;

    public StaffCreationResponse() {
        staffUsernames = new ArrayList<>();
    }

    public StaffCreationResponse(String message, List<String> staffUsernames) {
        this.message = message;
        this.staffUsernames = staffUsernames;
    }
}
