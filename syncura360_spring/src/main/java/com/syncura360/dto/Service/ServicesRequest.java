package com.syncura360.dto.Service;

import lombok.Data;

import java.util.List;

@Data
public class ServicesRequest {
    private List<ServiceDTO> services;
}
