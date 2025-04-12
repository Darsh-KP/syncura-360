package com.syncura360.dto.Visit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitDetailsDTO {
    private List<TimelineElementDTO> timeline;
    private String visitNote;
}
