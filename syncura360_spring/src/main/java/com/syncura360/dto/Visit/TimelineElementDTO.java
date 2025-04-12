package com.syncura360.dto.Visit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimelineElementDTO {

    private String dateTime;

    private String title;

    private String description;
}
