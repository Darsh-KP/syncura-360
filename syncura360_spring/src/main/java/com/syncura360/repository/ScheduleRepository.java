package com.syncura360.repository;


import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, ScheduleId> {

}
