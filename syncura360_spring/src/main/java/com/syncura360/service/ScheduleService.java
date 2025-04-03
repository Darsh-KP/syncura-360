package com.syncura360.service;

import com.syncura360.dto.Schedule.ShiftDto;
import com.syncura360.model.Schedule;
import com.syncura360.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

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
