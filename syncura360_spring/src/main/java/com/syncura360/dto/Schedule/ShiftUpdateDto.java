package com.syncura360.dto.Schedule;

import lombok.Data;

@Data
public class ShiftUpdateDto {

    private ScheduleIdDto id;
    private ShiftDto updates;
    public record ScheduleIdDto(String username, String start) {}

}
