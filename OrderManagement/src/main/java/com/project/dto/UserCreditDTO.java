package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreditDTO {
    private Long userId;
    private Double credits;
}
