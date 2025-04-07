package com.syncura360.controller;

import com.syncura360.dto.ErrorConvertor;
import com.syncura360.dto.Schedule.*;
import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import com.syncura360.model.Staff;
import com.syncura360.repository.ScheduleRepository;
import com.syncura360.repository.StaffRepository;
import com.syncura360.security.JwtUtil;
import com.syncura360.service.ScheduleService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

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
    ScheduleService scheduleService;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/staff")
    @Transactional
    public ResponseEntity<ScheduleDto> getStaffSchedule(
            @RequestHeader(name="Authorization") String authorization,
            @Valid @RequestBody StaffScheduleRequestDTO staffScheduleRequestDTO,
            BindingResult bindingResult) {

        List<ShiftDto> scheduledShifts;
        LocalDateTime start, end;
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));
        String username = jwtUtil.getUsername(authorization);

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ScheduleDto("Invalid request: " + ErrorConvertor.convertErrorsToString(bindingResult)));
        }

        try {
            start = LocalDateTime.parse(staffScheduleRequestDTO.getStart());
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ScheduleDto("Invalid request: bad start date format."));
        }

        try {
            end = LocalDateTime.parse(staffScheduleRequestDTO.getEnd());
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ScheduleDto("Invalid request: bad end date format."));
        }

        try {
            scheduledShifts = scheduleService.getShifts(start, end, username, hospitalId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ScheduleDto("Success: no shifts found."));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ScheduleDto("Database Error."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ScheduleDto("Success.", scheduledShifts));

    }

    @PutMapping
    @Transactional
    public ResponseEntity<MessageResponse> updateShifts(
            @RequestHeader(name="Authorization") String authorization,
            @RequestBody ShiftUpdateRequestDto shiftUpdateRequestDto)
    {

        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        try {

            for (ShiftUpdateDto dto : shiftUpdateRequestDto.getUpdates()) {

                Schedule shiftToModify = getShiftToModify(dto, hospitalId);
                Schedule newShift = getNewShift(dto.getUpdates(), shiftToModify);

                try {
                    scheduleRepository.delete(shiftToModify);
                    scheduleRepository.save(newShift);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed: Error querying database."));
                }

            }

        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed: Bad date format or missing dates."));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Failed: Shift not found."));
        } catch (InsufficientAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Shift not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed: Error querying database."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Changes Applied."));

    }

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

        ArrayList<ScheduleId> toDelete = new ArrayList<>();
        int hospitalId = Integer.parseInt(jwtUtil.getHospitalID(authorization));

        for (ShiftDto dto : deleteShiftsRequest.getShifts()) {

            Optional<Staff> staff = staffRepository.findByUsername(dto.getUsername());
            if (staff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed: Unknown staff member."));
            }

            if (hospitalId != staff.get().getWorksAt().getId()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown staff member."));
            }

            try { toDelete.add(new ScheduleId(dto.getUsername(), LocalDateTime.parse(dto.getStart()))); }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed: Error parsing date."));
            }

        }

        try { scheduleRepository.deleteByIds(toDelete); } catch (Exception e) {
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

    private Schedule getShiftToModify(ShiftUpdateDto dto, int hospitalId) {

        Optional<Schedule> optionalShift = scheduleRepository.findById(
                new ScheduleId(dto.getId().username(), LocalDateTime.parse(dto.getId().start()))
        );

        if (optionalShift.isEmpty()) {
            throw new NoSuchElementException("Shift not found.");
        }
        else if (optionalShift.get().getStaff().getWorksAt().getId() != hospitalId) {
            throw new InsufficientAuthenticationException("Shift not found.");
        }

        return optionalShift.get();

    }

    @SuppressWarnings("OptionalIsPresent")
    private Schedule getNewShift (ShiftDto updates, Schedule shiftToModify) throws SQLIntegrityConstraintViolationException {

        ScheduleId newId = new ScheduleId();
        Schedule newShift = new Schedule();

        if (updates.getUsername() != null) { newId.setStaffUsername(updates.getUsername()); }
        else { newId.setStaffUsername(shiftToModify.getId().getStaffUsername()); }

        Optional<Staff> staff = staffRepository.findByUsername(newId.getStaffUsername());
        if (staff.isPresent()) { newShift.setStaff(staff.get()); }

        if (updates.getStart() != null) { newId.setStartDateTime(LocalDateTime.parse(updates.getStart())); }
        else { newId.setStartDateTime(shiftToModify.getId().getStartDateTime()); }

        newShift.setId(newId);

        if (updates.getEnd() != null) { newShift.setEndDateTime(LocalDateTime.parse(updates.getEnd())); }
        else { newShift.setEndDateTime(shiftToModify.getEndDateTime()); }

        if (updates.getDepartment() != null) { newShift.setDepartment(updates.getDepartment()); }
        else { newShift.setDepartment(shiftToModify.getDepartment()); }

        if (newShift.getId().getStartDateTime().isAfter(newShift.getEndDateTime())) {
            throw new SQLIntegrityConstraintViolationException("Impossible shift.");
        }

        return newShift;

    }

    /**
     * Models a simple response with only a string message.
     * @param message
     */
    public record MessageResponse(String message) {}

}