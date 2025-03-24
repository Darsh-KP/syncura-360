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

        Optional<Staff> optionalAccessingUser = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
        Staff accessingUser = null;
        if (optionalAccessingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ScheduleDto("Failed: Unknown accessing user."));
        }

        accessingUser = optionalAccessingUser.get();
        Hospital hospital = accessingUser.getWorksAt(); // will be replaced by jwtUtil.getHospital(authorization);

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
            shiftList = scheduleRepository.findSchedules(start, end, shiftDto.getUsername(), shiftDto.getDepartment(), hospital);
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
    {

        Optional<Staff> optionalAccessingUser = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
        Staff accessingUser = null;
        if (optionalAccessingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown accessing user."));
        }

        accessingUser = optionalAccessingUser.get();
        Hospital hospital = accessingUser.getWorksAt(); // will be replaced by jwtUtil.getHospital(authorization);

        List<Schedule> shifts = new ArrayList<>();

        for (ShiftDto dto : newShiftsRequest.getShifts()) {

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

            LocalDateTime start;
            LocalDateTime end;

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

//    /**
//     * Attempts to apply a list of shift modifications.
//     * @param authorization JWT.
//     * @param updateShiftsRequest DTO to model the list of shift modifications.
//     * @return MessageResponse DTO containing a success or failure message.
//     */
//    @PutMapping
//    @Transactional
//    public ResponseEntity<MessageResponse> updateShifts(
//            @RequestHeader(name="Authorization") String authorization,
//            @RequestBody ShiftUpdateRequestDto updateShiftsRequest)
//    {
//
//        ArrayList<Schedule> toModify = new ArrayList<>();
//        ArrayList<Schedule> originalShifts = new ArrayList<>();
//
//        Optional<Staff> optionalAccessingUser = staffRepository.findByUsername(jwtUtil.getUsername(authorization));
//        Staff accessingUser = null;
//        if (optionalAccessingUser.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed: Unknown accessing user."));
//        }
//
//        accessingUser = optionalAccessingUser.get();
//        Hospital hospital = accessingUser.getWorksAt(); // will be replaced by jwtUtil.getHospital(authorization);
//
//        for (ShiftUpdateDto dto : updateShiftsRequest.getUpdates()) {
//
//            ScheduleId scheduleId = new ScheduleId();
//
//            LocalDateTime start;
//            try {
//                start = LocalDateTime.parse(dto.getId().start());
//            } catch (DateTimeParseException e ) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed. Bad date format."));
//            }
//
//            scheduleId.setStartDateTime(start);
//            scheduleId.setStaffUsername(dto.getId().username());
//
//            Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
//
//            if (optionalSchedule.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Failed. Shift not found."));
//            }
//
//            Schedule modifiedShift = optionalSchedule.get();
//            Schedule originalShift = scheduleRepository.findById(modifiedShift.getId()).get();
//
//            ShiftDto updates = dto.getUpdates();
//
//            if (updates.getUsername() != null || updates.getStart() != null) {
//
//                ScheduleId newId = new ScheduleId();
//
//                if (updates.getUsername() != null) {
//                    Optional<Staff> optionalUpdateStaff = staffRepository.findByUsername(updates.getUsername());
//                    if (optionalUpdateStaff.isEmpty()) {
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Failed. New staff not found."));
//                    }
//                    if (optionalUpdateStaff.get().getWorksAt() != hospital) {
//                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Failed. New staff not found."));
//                    }
//                    newId.setStaffUsername(updates.getUsername());
//                    modifiedShift.setStaff(optionalUpdateStaff.get());
//                }
//                else {
//                    newId.setStaffUsername(modifiedShift.getStaff().getUsername());
//                }
//
//                if (updates.getStart() != null) {
//                    LocalDateTime newStart;
//                    try {
//                        newStart = LocalDateTime.parse(updates.getStart());
//                    } catch (Exception e) {
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed. Bad date format."));
//                    }
//                    newId.setStartDateTime(newStart);
//                }
//                else {
//                    newId.setStartDateTime(modifiedShift.getId().getStartDateTime());
//                }
//
//                modifiedShift.setId(newId);
//
//            }
//            else if (updates.getDepartment() != null) {
//                modifiedShift.setDepartment(updates.getDepartment());
//            }
//            else if (updates.getEnd() != null) {
//
//                try {
//                    modifiedShift.setEndDateTime(LocalDateTime.parse(updates.getEnd()));
//                } catch (Exception e) {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Failed. Bad date format."));
//                }
//            }
//
//            // delete original shift
//            originalShifts.add(originalShift);
//            toModify.add(modifiedShift);
//
//        } // end for
//
//        System.out.println("Modified shifts: " + toModify.toString());
//        System.out.println("Original shifts: " + originalShifts.toString());
//
//
//        try {
//            scheduleRepository.deleteAll(originalShifts);
//            scheduleRepository.saveAll(toModify);
//        }
//        catch (Exception e) {
//            System.out.println(e.toString());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed. Error saving changes to database."));
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Success."));
//
//    }

    /**
     * Models a simple response with only a string message.
     * @param message
     */
    public record MessageResponse(String message) {}

}
