package com.syncura360.dto.Service;

import lombok.Data;

import java.util.List;

@Data
public class UpdateServicesRequest {

    private List<ServiceUpdate> updates;

}
