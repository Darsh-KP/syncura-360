package com.syncura360.dto.Room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * DTO for submitting a new equipment registration with details such as serial number and equipment name.
 *
 * @author Darsh-KP
 */
@Getter
public class EquipmentFormDTO {
    @NotNull(message = "Serial number is required.")
    @Size(max = 25, message = "Max length for serial number is 25 characters.")
    String serialNo;

    @NotNull(message = "Equipment name is required.")
    @Size(max = 255, message = "Max length for equipment name is 255 characters.")
    String name;
}
