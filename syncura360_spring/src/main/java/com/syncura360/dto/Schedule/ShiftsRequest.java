package com.syncura360.dto.Schedule;

import lombok.Data;

import java.util.List;

@Data
public class ShiftsRequest {

    private List<ShiftDto> shifts;
}
