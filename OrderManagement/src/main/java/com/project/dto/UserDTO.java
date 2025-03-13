package com.project.dto;

import com.project.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Integer statusCode;
	private String error;
	private String message;
	private String token;
	private Long userId;
	private String name;
	private String email;
	private String password;
	private Role role;
}
