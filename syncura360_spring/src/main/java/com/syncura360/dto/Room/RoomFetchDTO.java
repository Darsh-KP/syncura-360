package com.syncura360.dto.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RoomFetchDTO {
    private String roomName;
    private String department;
    private int beds;
    private int bedsVacant;
    private int bedsOccupied;
    private int bedsMaintenance;
    private List<EquipmentFetchDTO> equipments;
}
