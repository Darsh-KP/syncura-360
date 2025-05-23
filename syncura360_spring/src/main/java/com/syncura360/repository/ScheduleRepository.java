package com.syncura360.repository;

import com.syncura360.model.Schedule;
import com.syncura360.model.ScheduleId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Schedule entity.
 *
 * @author Benjamin Leiby
 */
public interface ScheduleRepository extends JpaRepository<Schedule, ScheduleId> {

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.id.startDateTime >= :startDate " +
            "AND s.endDateTime <= :endDate " +
            "AND (:username IS NULL OR s.id.staffUsername = :username) " +
            "AND (:department IS NULL OR s.department = :department) " +
            "AND (s.staff.worksAt.id = :hospitalId)")
    List<Schedule> findSchedules(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("username") String username,
            @Param("department") String department,
            @Param("hospitalId") int hospitalId
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.id IN :scheduleIds")
    void deleteByIds(@Param("scheduleIds") Iterable<ScheduleId> scheduleIds);

}
