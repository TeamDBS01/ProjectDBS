package com.project.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
 
	private int userID;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;

}
 