package com.syncura360.dto.Service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for representing a collection of services, including a message and a list of service details.
 *
 * @author Benjamin Leiby
 */
@Data
public class ServicesDTO {
    private String message;
    private List<ServiceDTO> services;

    public ServicesDTO(String message) {

        this.message = message;
        this.services = new ArrayList<>();

    }

    public ServicesDTO(String message, List<ServiceDTO> services) {

        this.message = message;
        this.services = services;

    }

    public ServicesDTO() {
        this.services = new ArrayList<>();
    }
}
