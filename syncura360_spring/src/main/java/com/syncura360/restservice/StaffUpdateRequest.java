package com.syncura360.restservice;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StaffUpdateRequest {

    @Data
    public static class StaffUpdateDto {
        private Integer id;
        private Map<String, Object> fields;
    }

    private List<StaffUpdateRequest.StaffUpdateDto> updates;

}
