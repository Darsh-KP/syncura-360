package com.syncura360.dto.Staff;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * DTO for handling staff password change requests with current and new password fields.
 *
 * @author Darsh-KP
 */
@Getter
public class StaffPasswordChangeForm {
    @NotNull(message = "Current password is required.")
    private String currentPassword;

    @NotNull(message = "New password is required.")
    private String newPassword;
}
