package com.syncura360.dto.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for fetching equipment details such as serial number, name, and maintenance status.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class EquipmentFetchDTO {
    private String serialNo;
    private String name;
    private boolean inMaintenance;
}
