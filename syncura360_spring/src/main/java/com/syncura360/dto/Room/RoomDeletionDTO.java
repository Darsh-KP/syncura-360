package com.syncura360.dto.Room;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class RoomDeletionDTO {
    @NotNull(message = "Room name is required.")
    @Size(max = 50, message = "Max length for room name is 50 characters.")
    String roomName;
}
