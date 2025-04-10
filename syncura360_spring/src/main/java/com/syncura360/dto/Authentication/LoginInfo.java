package com.syncura360.dto.Authentication;

import lombok.Data;
/**
 * Represents the login information of a user.
 *
 * @author Benjamin Leiby
 */
@Data
public class LoginInfo {
    private String username;
    private String password;
}
