package com.syncura360.dto;

import java.util.List;

import lombok.Data;

@Data
public class StaffCreationRequest {
    private List<StaffCreationDto> staff;
}

