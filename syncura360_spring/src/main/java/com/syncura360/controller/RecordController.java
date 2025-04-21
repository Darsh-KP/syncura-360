package com.syncura360.controller;

import com.syncura360.dto.Visit.RecordDTO;
import com.syncura360.dto.Visit.VisitDetailsDTO;
import com.syncura360.dto.Visit.VisitListDTO;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.VisitService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/record")
public class RecordController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    VisitService visitService;

    @GetMapping
    public ResponseEntity<List<RecordDTO>> getRecords(@RequestHeader(name="Authorization") String authorization) {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        List<RecordDTO> response;
        try {
            response = visitService.getRecords(hospitalId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ArrayList<>());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{patientId}/{dateTime}")
    public ResponseEntity<VisitDetailsDTO> getRecordDetails(
            @RequestHeader(name="Authorization") String authorization,
            @NotNull @PathVariable("patientId") int patientId,
            @NotNull @PathVariable("dateTime") String dateTime)
    {
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        VisitDetailsDTO response;
        try {
            response = new VisitDetailsDTO();
            response.setTimeline(visitService.getTimeline(hospitalId, patientId, dateTime, true));
            response.setVisitNote(visitService.getNote(hospitalId, patientId, dateTime, true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new VisitDetailsDTO());
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
