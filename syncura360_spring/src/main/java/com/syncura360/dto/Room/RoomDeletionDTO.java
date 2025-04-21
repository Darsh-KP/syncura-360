package com.syncura360.dto.Room;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for deleting a room by specifying the room name.
 *
 * @author Darsh-KP
 */
@Getter
public class RoomDeletionDTO {
    @NotNull(message = "Room name is required.")
    @Size(max = 50, message = "Max length for room name is 50 characters.")
    private String roomName;
}
