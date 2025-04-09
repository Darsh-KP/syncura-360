package com.syncura360.model;

import com.syncura360.model.enums.BloodType;
import com.syncura360.model.enums.BloodTypeConvertor;
import com.syncura360.model.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a patient's personal and medical information, including demographics, contact details, and medical history.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "PatientInfo", schema = "syncura360")
public class PatientInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Convert(converter = BloodTypeConvertor.class)
    @Column(name = "blood_type")
    private BloodType bloodType;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "phone", nullable = false, length = 15)
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

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 15)
    private String emergencyContactPhone;

    @Column(name = "medical_notes", length = 65535)
    private String medicalNotes;

    public PatientInfo(String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                       BloodType bloodType, Integer height, Integer weight,
                       String phone, String addressLine1, String addressLine2, String city, String state, String postal, String country,
                       String emergencyContactName, String emergencyContactPhone) {
        this.firstName = firstName == null ? null : firstName.trim();
        this.lastName = lastName == null ? null : lastName.trim();
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;

        if (height != null && height < 0) {
            throw new IllegalArgumentException("Height must be a positive integer.");
        }
        this.height = height;

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Weight must be a positive integer.");
        }
        this.weight = weight;

        this.phone = phone == null ? null : phone.trim();
        this.addressLine1 = addressLine1 == null ? null : addressLine1.trim();
        this.addressLine2 = addressLine2 == null ? null : addressLine2.trim();
        this.city = city == null ? null : city.trim();
        this.state = state == null ? null : state.trim();
        this.postal = postal == null ? null : postal.trim();
        this.country = country == null ? null : country.trim();
        this.emergencyContactName = emergencyContactName == null ? null : emergencyContactName.trim();
        this.emergencyContactPhone = emergencyContactPhone == null ? null : emergencyContactPhone.trim();
        this.medicalNotes = null;
    }
}