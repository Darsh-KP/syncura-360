package com.syncura360.dto.Room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.Size;

/**
 * DTO for fetching room details by specifying the room name.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class RoomFetchRequestDTO {
    @Size(max = 50, message = "Max length for room name is 50 characters.")
    String roomName;
}
