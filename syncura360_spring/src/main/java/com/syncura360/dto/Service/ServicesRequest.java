package com.syncura360.dto.Service;

import lombok.Data;

import java.util.List;

/**
 * DTO for representing a request containing a list of services.
 *
 * @author Benjamin Leiby
 */
@Data
public class ServicesRequest {
    private List<ServiceDTO> services;
}
