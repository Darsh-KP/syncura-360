package com.syncura360.dto.Room;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for fetching a list of rooms with additional room details such as room name, department,
 * number of beds, and associated equipment.
 *
 * @author Darsh-KP
 */
@Setter
@Getter
public class RoomFetchContainerDTO {
    private String message;

    private List<RoomFetchDTO> rooms;

    private String roomName;
    private String department;
    private int beds;
    private List<EquipmentFetchDTO> equipments;
}
