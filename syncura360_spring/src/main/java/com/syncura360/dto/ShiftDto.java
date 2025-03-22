package com.syncura360.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShiftDto {

    private String username;
    private String start;
    private String end;
    private String department;

}