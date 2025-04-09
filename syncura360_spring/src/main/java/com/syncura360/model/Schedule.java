package com.syncura360.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a schedule entity with details like staff, department, and end date-time.
 *
 * @author Darsh-KP
 */
@Data
@Entity
@Table(name = "Schedule")
public class Schedule {
    @EmbeddedId
    private ScheduleId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_username", referencedColumnName = "username", nullable = false, insertable = false, updatable = false)
    private Staff staff;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    public Schedule(ScheduleId id, Staff staff, LocalDateTime endDateTime, String department) {

        this.id = id;
        this.staff = staff;
        this.endDateTime = endDateTime;

        this.department = Objects.requireNonNullElse(department, "None");

    }

    public Schedule() {}
}