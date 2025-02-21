package com.example.syncura360_spring.restservice;

import lombok.Data;

@Data
public class User {

    private int userid;

    private String firstname;

    private String lastname;

    private String username;

    private String password;

    private String email;

    private Role usertype;

}
