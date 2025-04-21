package com.syncura360.dto.Schedule;

import lombok.Data;

import java.util.List;

/**
 * DTO for representing a request containing a list of shift updates.
 *
 * @author Benjamin Leiby
 */
@Data
public class ShiftUpdateRequestDto {
    private List<ShiftUpdateDto> updates;
}
