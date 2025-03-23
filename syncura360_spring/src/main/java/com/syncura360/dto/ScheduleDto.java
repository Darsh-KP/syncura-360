package com.syncura360.dto;

import com.syncura360.repository.ScheduleRepository;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleDto {

    private String message;
    private List<ScheduleRepository> scheduledShifts;

    public ScheduleDto() {
        scheduledShifts = new ArrayList<>();
    }

}
