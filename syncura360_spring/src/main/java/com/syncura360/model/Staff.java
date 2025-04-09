package com.syncura360.model;

import com.syncura360.model.enums.Role;
import com.syncura360.model.enums.RoleConvertor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents a staff member working at a hospital, including personal and professional details.
 *
 * @author Darsh-KP
 */
@Data
@Entity
@Table(name = "Staff", uniqueConstraints = @UniqueConstraint(columnNames = {"works_at", "email"}))
public class Staff {
    @Id
    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "works_at", referencedColumnName = "hospital_id", nullable = false)
    private Hospital worksAt;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Convert(converter = RoleConvertor.class)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "postal", nullable = false, length = 20)
    private String postal;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "years_experience")
    private Integer yearsExperience;
}