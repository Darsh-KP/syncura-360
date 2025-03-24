package com.syncura360.dto.Staff;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StaffCreationResponse {

    private String message;
    private List<String> staffUsernames;

    public StaffCreationResponse() {
        staffUsernames = new ArrayList<String>();
    }

    public StaffCreationResponse(String message, List<String> staffUsernames) {
        this.message = message;
        this.staffUsernames = staffUsernames;
    }

}
