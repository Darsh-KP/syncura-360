package com.syncura360.dto;

import lombok.Data;

import java.util.List;


//{
//
//shifts: [
//
//        {
//
//start: localdate string,
//
//end: localdate string,
//
//username: string,
//
//department: string
//
//} ] }

@Data
public class ShiftsRequest {

    private List<ShiftDto> shifts;
}
