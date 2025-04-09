package com.syncura360.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for a generic response containing a message. This can be used for returning simple message-based responses.
 *
 * @author Darsh-KP
 */
@Getter
@Setter
public class GenericMessageResponseDTO {
    private String message;

    /**
     * Constructor to create a response with a specific message.
     *
     * @param message The message to be included in the response.
     */
    public GenericMessageResponseDTO(String message) {
        this.message = message;
    }
}
