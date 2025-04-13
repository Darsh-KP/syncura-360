package com.syncura360.dto.Staff;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StaffPasswordChangeForm {
    @NotNull(message = "Current password is required.")
    private String currentPassword;

    @NotNull(message = "New password is required.")
    private String newPassword;
}
