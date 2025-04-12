package com.syncura360.dto.Visit;

import com.syncura360.dto.Service.ServiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServiceListDTO {
    private List<ServiceDTO> services;
}
