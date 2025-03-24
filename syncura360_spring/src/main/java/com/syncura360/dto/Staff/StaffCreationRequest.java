package com.syncura360.dto.Staff;

import java.util.List;

import lombok.Data;

@Data
public class StaffCreationRequest {
    private List<StaffCreationDto> staff;
}

