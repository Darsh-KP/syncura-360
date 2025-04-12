package com.syncura360.dto.Visit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DoctorListDTO {
    private List<DoctorDTO> doctors;
}
