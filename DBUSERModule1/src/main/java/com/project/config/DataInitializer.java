package com.project.config;

import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import com.project.services.UserService; // Import your UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final UserService userService;  

    
    public DataInitializer(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            User adminUser = new User();
            adminUser.setEmail("admin@example.com");
            adminUser.setName("Admin");
            String encodedPassword = userService.encodePassword("adminpassword");  
            adminUser.setPassword(encodedPassword);
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser); 
        }
    }

 
    
}