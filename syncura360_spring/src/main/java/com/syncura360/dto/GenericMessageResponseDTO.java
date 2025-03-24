package com.syncura360.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericMessageResponseDTO {
    private String message;

    public GenericMessageResponseDTO(String message) {
        this.message = message;
    }
}
