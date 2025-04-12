package com.syncura360.dto.Service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for representing a service, including the name, category, description, and cost of the service.
 *
 * @author Benjamin Leiby
 */
@Data
@AllArgsConstructor
public class ServiceDTO {
    private String name;
    private String category;
    private String description;
    private BigDecimal cost;

    public ServiceDTO() {}
}
