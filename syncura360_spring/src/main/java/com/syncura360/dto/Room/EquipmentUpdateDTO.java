package com.syncura360.dto.Room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EquipmentUpdateDTO {
    @NotNull(message = "Serial number is required.")
    @Size(max = 25, message = "Max length for serial number is 25 characters.")
    String serialNo;

    @NotNull(message = "Equipment name is required.")
    @Size(max = 255, message = "Max length for equipment name is 255 characters.")
    String name;

    @NotNull(message = "InMaintenance is required.")
    Boolean inMaintenance;
}