package com.syncura360.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PatientLogin", schema = "syncura360")
public class PatientLogin {
    @Id
    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientInfo patient;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false)
    private String email;
}