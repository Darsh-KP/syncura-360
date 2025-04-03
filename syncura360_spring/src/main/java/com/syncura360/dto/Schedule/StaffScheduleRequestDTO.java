package com.syncura360.dto.Schedule;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class StaffScheduleRequestDTO {

    private String start;
    private String end;

}
