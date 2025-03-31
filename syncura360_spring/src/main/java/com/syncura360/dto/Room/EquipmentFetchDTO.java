package com.syncura360.dto.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EquipmentFetchDTO {
    private String serialNo;
    private String name;
    private boolean inMaintenance;
}
