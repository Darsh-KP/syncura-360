package com.syncura360.repository;


import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, ScheduleId> {

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.id.startDateTime >= :startDate " +
            "AND s.endDateTime <= :endDate " +
            "AND (:username IS NULL OR s.id.staffUsername = :username) " +
            "AND (:department IS NULL OR s.department = :department)")
    List<Schedule> findSchedules(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("username") String username,
            @Param("department") String department
    );

}
