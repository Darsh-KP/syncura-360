package com.syncura360.dto.Room;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
public class RoomDeletionDTO {
    @NotNull(message = "Room name is required.")
    @Size(max = 50, message = "Max length for room name is 50 characters.")
    String roomName;
}
