package com.syncura360.service;

import com.syncura360.dto.Staff.StaffSettingFetch;
import com.syncura360.model.Staff;
import com.syncura360.repository.StaffRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service for handling operations related to staff settings and profile data retrieval.
 *
 * @author Darsh-KP
 */
@Service
public class StaffService {
    StaffRepository staffRepository;

    /**
     * Constructs a StaffService with the given repository dependency.
     *
     * @param staffRepository Repository for accessing staff data.
     */
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Retrieves staff settings for a given username.
     *
     * @param username The unique username of the staff member.
     * @return A {@link StaffSettingFetch} DTO containing staff setting details.
     * @throws EntityNotFoundException If the staff member with the given username is not found.
     */
    public StaffSettingFetch getStaffSetting(String username) {
        // Find the staff if they already exist
        Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
        if (optionalStaff.isEmpty()) {
            // Staff not found
            throw new EntityNotFoundException("Staff with given username not found.");
        }

        // Get the staff
        Staff staff = optionalStaff.get();

        // Return staff setting details
        return new StaffSettingFetch(
                staff.getUsername(),
                staff.getRole().getValue(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getDateOfBirth().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                staff.getPhone(),
                staff.getAddressLine1(),
                staff.getAddressLine2(),
                staff.getCity(),
                staff.getState(),
                staff.getPostal(),
                staff.getCountry(),
                staff.getSpecialty(),
                staff.getYearsExperience()
        );
    }
}
