package com.project.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_credits")
@Data
public class UserCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Double credits;
}