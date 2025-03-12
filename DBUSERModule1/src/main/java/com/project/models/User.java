package com.project.models;
import jakarta.persistence.*;
import lombok.Data;
 

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users_table")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private Role role;
}
 