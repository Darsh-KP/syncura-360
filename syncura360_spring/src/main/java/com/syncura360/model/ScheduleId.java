package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ScheduleId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1329689031031810698L;

    @Column(name = "staff_username", nullable = false, length = 20)
    private String staffUsername;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ScheduleId entity = (ScheduleId) o;
        return Objects.equals(this.startDateTime, entity.startDateTime) &&
                Objects.equals(this.staffUsername, entity.staffUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDateTime, staffUsername);
    }
}