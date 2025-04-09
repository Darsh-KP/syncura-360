package com.syncura360.dto.Room;

import jakarta.validation.Valid;
import lombok.Getter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for submitting new room details, including room name, department, number of beds,
 * and associated equipment.
 *
 * @author Darsh-KP
 */
@Getter
public class RoomFormDTO {
    @NotNull(message = "Room name is required.")
    @Size(max = 50, message = "Max length for room name is 50 characters.")
    String roomName;

    @NotNull(message = "Department is required.")
    @Size(max = 100, message = "Max length for department name is 100 characters.")
    String department;

    @NotNull(message = "Number of beds is required.")
    @PositiveOrZero(message = "Number of beds cannot be negative.")
    @Max(value = 100, message = "Whoa, over 100 beds? Are you opening a mattress factory or a hospital? (Max 100)")
    Integer beds;

    @Valid
    @Size(max = 100, message = "100 pieces of equipment? Are we treating patients or building Iron Man's suit? (Max 100)")
    List<EquipmentFormDTO> equipments;
}
