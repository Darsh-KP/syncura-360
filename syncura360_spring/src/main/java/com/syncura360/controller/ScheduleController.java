package com.syncura360.controller;

import com.syncura360.dto.Operation;
import com.syncura360.dto.ScheduleDto;
import com.syncura360.dto.ShiftDto;
import com.syncura360.dto.ShiftsRequest;
import com.syncura360.model.Hospital;
import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import com.syncura360.model.Staff;
import com.syncura360.repository.ScheduleRepository;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations for staff scheduling.
 * @author Benjamin Leiby
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping()
    public ResponseEntity<ScheduleDto> getAllShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftDto shiftDto)
    {

        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            start = LocalDateTime.parse(shiftDto.getStart());
            end = LocalDateTime.parse(shiftDto.getEnd());
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ScheduleDto("Failed: Bad date format or missing dates."));
        }

        List<Schedule> shiftList = new ArrayList<>();
        List<ShiftDto> dtoList = new ArrayList<>();

        try {
            shiftList = scheduleRepository.findSchedules(start, end, shiftDto.getUsername(), shiftDto.getDepartment());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ScheduleDto("Failed: Error fetching from database."));
        }

        if (shiftList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ScheduleDto("Failed: No shifts found."));
        }

        for (Schedule shift : shiftList) {
            ShiftDto dto = new ShiftDto();
            dto.setStart(String.valueOf(shift.getId().getStartDateTime()));
            dto.setEnd(String.valueOf(shift.getEndDateTime()));
            dto.setUsername(shift.getId().getStaffUsername());
            dto.setDepartment(shift.getDepartment());
            dtoList.add(dto);
        }

        ScheduleDto response = new ScheduleDto("Success.");
        response.setScheduledShifts(dtoList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
     * Attempts to add a list of new shifts to the schedule;
     * @param authorization JWT.
     * @param newShiftsRequest DTO to model the list of new shifts.
     * @return MessageResponse DTO containing a success or failure message.
     */
    @PostMapping("/new")
    public ResponseEntity<MessageResponse> addShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftsRequest newShiftsRequest)
    { return modifyShifts(authorization, newShiftsRequest, Operation.CREATE); }

    /**
     * Attempts to apply a list of shift modifications.
     * @param authorization JWT.
     * @param updateShiftsRequest DTO to model the list of shift modifications.
     * @return MessageResponse DTO containing a success or failure message.
     */
    @PutMapping
    public ResponseEntity<MessageResponse> updateShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftsRequest updateShiftsRequest)
    { return modifyShifts(authorization, updateShiftsRequest, Operation.UPDATE); }

    /**
     * Attempts to delete a list of shifts from the schedule.
     * @param authorization JWT.
     * @param deleteShiftsRequest DTO to model the list of shifts to delete.
     * @return MessageResponse DTO containing a success or failure message.
     */
    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteShifts(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody ShiftsRequest deleteShiftsRequest)
    { return modifyShifts(authorization, deleteShiftsRequest, Operation.DELETE); }


    private ResponseEntity<MessageResponse> modifyShifts(String authorization, ShiftsRequest shiftsRequest, Operation operation) {

        Optional<Staff> optionalAccessingUser = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
        Staff accessingUser = null;
        if (optionalAccessingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown accessing user."));
        }

        accessingUser = optionalAccessingUser.get();
        Hospital hospital = accessingUser.getWorksAt(); // will be replaced by jwtUtil.getHospital(authorization);

        List<Schedule> shifts = new ArrayList<>();

        for (ShiftDto dto : shiftsRequest.getShifts()) {

            Optional<Staff> optionalStaff = staffRepository.findByUsername(dto.getUsername());

            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(new MessageResponse("Failed: Unknown staff username."));
            }

            // Shift staff member
            Staff staff = optionalStaff.get();

            // Return an error if the accessing user and staff member do not work at the same hospital.
            if (hospital != staff.getWorksAt()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown staff username."));
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            try {
                start = LocalDateTime.parse(dto.getStart());
                end = LocalDateTime.parse(dto.getEnd());
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(new MessageResponse("Failed: Bad date format"));
            }

            ScheduleId id = new ScheduleId();
            id.setStaffUsername(dto.getUsername());
            id.setStartDateTime(start);
            Schedule shift = new Schedule();
            shift.setId(id);
            shift.setStaff(staff);
            shift.setEndDateTime(end);

            if (dto.getDepartment() != null) { shift.setDepartment(dto.getDepartment()); }
            else { shift.setDepartment("None"); }

            shifts.add(shift);

        }

        try {

            if (operation == Operation.CREATE || operation == Operation.UPDATE) {
                scheduleRepository.saveAll(shifts);
            }
            else { scheduleRepository.deleteAll(shifts); }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed: Error applying changes to database"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success."));

    }

    /**
     * Models a simple response with only a string message.
     * @param message
     */
    public record MessageResponse(String message) {}

}
