package com.syncura360.dto.Service;

import lombok.Data;

import java.util.List;

/**
 * DTO for representing a request containing a list of service updates.
 *
 * @author Benjamin Leiby
 */
@Data
public class UpdateServicesRequest {
    private List<ServiceUpdate> updates;
}
