package com.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private Long id;
    private Long userId;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private int statusCode;
    private String message;
    private String error;
}
