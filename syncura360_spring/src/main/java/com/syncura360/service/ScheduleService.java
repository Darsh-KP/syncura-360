package com.syncura360.service;

import com.syncura360.dto.Schedule.ShiftDto;
import com.syncura360.model.Schedule;
import com.syncura360.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class responsible for managing schedule-related operations in the hospital.
 * Provides functionality to fetch shifts for a specific user within a time range.
 *
 * @author Benjamin Leiby
 */
@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    /**
     * Fetches a list of shifts for a given user within a specific time range.
     *
     * @param start The start time of the shift range.
     * @param end The end time of the shift range.
     * @param username The username of the user whose shifts are being fetched.
     * @param hospitalId The ID of the hospital to filter schedules by.
     * @return A list of {@link ShiftDto} containing shift details.
     * @throws NoSuchElementException If no shifts are found within the given range.
     */
    public List<ShiftDto> getShifts(LocalDateTime start, LocalDateTime end, String username, int hospitalId) {
        List<Schedule> entities = scheduleRepository.findSchedules(start, end, username, null, hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException();
        }
        List<ShiftDto> DTOs = new ArrayList<>();

        for (Schedule entity : entities) {
            ShiftDto dto = new ShiftDto();
            dto.setUsername(username);
            dto.setDepartment(entity.getDepartment());
            dto.setEnd(entity.getEndDateTime().toString());
            dto.setStart(entity.getId().getStartDateTime().toString());
            DTOs.add(dto);
        }

        return DTOs;
    }
}
