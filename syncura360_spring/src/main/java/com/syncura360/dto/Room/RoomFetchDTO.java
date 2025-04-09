package com.syncura360.dto.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * DTO for fetching detailed information about a room, including room name, department,
 * bed availability (vacant, occupied, and under maintenance), and associated equipment.
 *
 * @author Darsh-KP
 */
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
