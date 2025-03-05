package com.project.dto;

import com.project.models.Role;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private int userID;
    private String name;
    private String email;
    private String password;
    private Role role;
}
