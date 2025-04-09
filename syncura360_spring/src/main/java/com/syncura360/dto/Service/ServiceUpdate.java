package com.syncura360.dto.Service;

import lombok.Data;

/**
 * DTO for updating a service, including the service name and the updated service details.
 *
 * @author Benjamin Leiby
 */
@Data
public class ServiceUpdate {
    private String name;
    private ServiceDTO updates;
}
