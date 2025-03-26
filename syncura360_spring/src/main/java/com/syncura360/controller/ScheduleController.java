package com.syncura360.controller;

import com.syncura360.dto.Schedule.*;
import com.syncura360.model.Hospital;
import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import com.syncura360.model.Staff;
import com.syncura360.repository.ScheduleRepository;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

    @PostMapping
    @Transactional
    public ResponseEntity<ScheduleDto> getAllShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftDto shiftDto)
    {

        LocalDateTime start, end;
        List<ShiftDto> dtoList;
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {

            start = LocalDateTime.parse(shiftDto.getStart());
            end = LocalDateTime.parse(shiftDto.getEnd());
            dtoList = createShiftDTOs(
                    scheduleRepository.findSchedules(start, end, shiftDto.getUsername(), shiftDto.getDepartment(), hospitalId)
            );

        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ScheduleDto("Failed: Bad date format or missing dates."));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ScheduleDto("Failed: No shifts found."));
        } catch (Exception e) { // Database exception.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ScheduleDto("Failed: Error fetching from database."));
        }

        ScheduleDto response = new ScheduleDto("Success.", dtoList);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    /**
     * Attempts to add a list of new shifts to the schedule;
     * @param authorization JWT.
     * @param newShiftsRequest DTO to model the list of new shifts.
     * @return MessageResponse DTO containing a success or failure message.
     */
    @PostMapping("/new")
    @Transactional
    public ResponseEntity<MessageResponse> addShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftsRequest newShiftsRequest)
    {

        LocalDateTime start, end;
        List<Schedule> shifts = new ArrayList<>();
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        for (ShiftDto dto : newShiftsRequest.getShifts()) {

            try {
                start = LocalDateTime.parse(dto.getStart());
                end = LocalDateTime.parse(dto.getEnd());
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(new MessageResponse("Failed: Bad date format"));
            }

            Optional<Staff> optionalStaff = staffRepository.findByUsername(dto.getUsername());
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(new MessageResponse("Failed: Unknown staff username."));
            }

            // Return an error if the accessing user and staff member do not work at the same hospital.
            if (hospitalId != optionalStaff.get().getWorksAt().getId()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown staff username."));
            }

            Schedule shift = new Schedule(new ScheduleId(dto.getUsername(), start), optionalStaff.get(), end, dto.getDepartment());
            shifts.add(shift);

        }

        try { scheduleRepository.saveAll(shifts); }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed: Error adding shifts to database."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success."));

    }

    /**
     * Attempts to delete a list of shifts from the schedule.
     * @param authorization JWT.
     * @param deleteShiftsRequest DTO to model the list of shifts to delete.
     * @return MessageResponse DTO containing a success or failure message.
     */
    @DeleteMapping
    @Transactional
    public ResponseEntity<MessageResponse> deleteShifts(
        @RequestHeader(name="Authorization") String authorization,
        @RequestBody ShiftsRequest deleteShiftsRequest)
    {

        ArrayList<Schedule> toDelete = new ArrayList<>();

        Optional<Staff> optionalAccessingUser = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
        Staff accessingUser = null;
        if (optionalAccessingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown accessing user."));
        }

        accessingUser = optionalAccessingUser.get();
        Hospital hospital = accessingUser.getWorksAt(); // will be replaced by jwtUtil.getHospital(authorization);

        for (ShiftDto dto : deleteShiftsRequest.getShifts()) {

            Optional<Staff> staff = staffRepository.findByUsername(dto.getUsername());

            if (staff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed: Unknown staff member."));
            }

            if (hospital != staff.get().getWorksAt()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown staff member."));
            }

            ScheduleId scheduleId = new ScheduleId();

            try {
                scheduleId.setStartDateTime(LocalDateTime.parse(dto.getStart()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed: Error parsing date."));
            }

            scheduleId.setStaffUsername(dto.getUsername());

            Optional<Schedule> optionalShift = scheduleRepository.findById(scheduleId);
            if (optionalShift.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Failed: Shift not found."));
            }

            toDelete.add(optionalShift.get());
        }

        try {
            scheduleRepository.deleteAll(toDelete);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed. Error deleting from database."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success."));

    }

    private List<ShiftDto> createShiftDTOs(List<Schedule> shifts) {

        List<ShiftDto> result = new ArrayList<>();
        if (shifts.isEmpty()) { throw new NoSuchElementException("Shift list is empty."); }

        for (Schedule shift : shifts) {
            ShiftDto dto = new ShiftDto();
            dto.setStart(String.valueOf(shift.getId().getStartDateTime()));
            dto.setEnd(String.valueOf(shift.getEndDateTime()));
            dto.setUsername(shift.getId().getStaffUsername());
            dto.setDepartment(shift.getDepartment());
            result.add(dto);
        }
        return result;
    }

    /**
     * Models a simple response with only a string message.
     * @param message
     */
    public record MessageResponse(String message) {}

}
