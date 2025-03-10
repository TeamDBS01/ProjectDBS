package com.project.controller;

import com.project.dto.UserDTO;
import com.project.models.User;
import com.project.services.UserService;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService usersService;

    // Register
    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO reg){
        return ResponseEntity.ok(usersService.register(reg));
    }

    // Login
    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO req){
        return ResponseEntity.ok(usersService.login(req));
    }
    
//    // New logout endpoint
//    @PostMapping("/logout")
//    public ResponseEntity<UserDTO> logout(@RequestHeader("Authorization") String token) {
//        // Extract the token from the Authorization header
//        String jwt = token.substring(7);
//        UserDTO response = usersService.logout(jwt);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }


    // Refresh Token
    @PostMapping("/auth/refresh")
    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO req){
        return ResponseEntity.ok(usersService.refreshToken(req));
    }

    // Get all users - permit only to admin
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<UserDTO> getAllUsers(){
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Get User by ID
    @GetMapping("/admin/get-user/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
        return ResponseEntity.ok(usersService.getUserByID(userId));
    }

    // Update user
    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody User reqRes){
        return ResponseEntity.ok(usersService.updateUser(userId, reqRes));
    }

    // Get the profile
    @GetMapping("/user/get-profile")
    public ResponseEntity<UserDTO> getMyProfile(){

        // Get the Authentication object for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // getName() -> principle name(Username) for the current authenticated user
        String email = authentication.getName();
        UserDTO response = usersService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Delete User
    @DeleteMapping("/admin/deleteUser/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(usersService.deleteUser(userId));
    }
    
    
    @PutMapping("/update-profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody User reqRes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(usersService.updateProfile(email, reqRes));
    }
}



