//package com.project.controller;
//
//import com.project.dto.UserDTO;
//import com.project.models.User;
//import com.project.services.UserService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
////@RequestMapping("/user")
//@RestController
//public class UserController {
//
//    @Autowired
//    private UserService usersService;
//
//    // Register
//    @PostMapping("/auth/register")
//    public ResponseEntity<UserDTO> register(@RequestBody UserDTO reg){
//        return ResponseEntity.ok(usersService.register(reg));
//    }
//
//    // Login
//    @PostMapping("/auth/login")
//    public ResponseEntity<UserDTO> login(@RequestBody UserDTO req){
//        return ResponseEntity.ok(usersService.login(req));
//    }
//
//    // Refresh Token
//    @PostMapping("/auth/refresh")
//    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO req){
//        return ResponseEntity.ok(usersService.refreshToken(req));
//    }
//
//    // Get all users - permit only to admin
//    @GetMapping("/admin/get-all-users")
//    public ResponseEntity<UserDTO> getAllUsers(){
//        return ResponseEntity.ok(usersService.getAllUsers());
//    }
//
//    // Get User by ID
//    @GetMapping("/admin/get-user/{userId}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
//        return ResponseEntity.ok(usersService.getUserByID(userId));
//    }
//
//    // Update user
//    @PutMapping("/admin/update/{userId}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody User reqRes){
//        return ResponseEntity.ok(usersService.updateUser(userId, reqRes));
//    }
//
//    // Get the profile
//    @GetMapping("/user/get-profile")
//    public ResponseEntity<UserDTO> getMyProfile(){
//        // Assuming email is passed in the request body for simplicity
//        String email = "example@example.com"; // Replace with actual email retrieval logic
//        UserDTO response = usersService.getMyInfo(email);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }


//
//    // Delete User
//    @DeleteMapping("/admin/deleteUser/{userId}")
//    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId){
//        return ResponseEntity.ok(usersService.deleteUser(userId));
//    }
//
////    @PutMapping("/update-profile")
////    public ResponseEntity<UserDTO> updateProfile(@RequestBody User reqRes) {
////        String email = "example@example.com"; // Replace with actual email retrieval logic
////        return ResponseEntity.ok(usersService.updateProfile(email, reqRes));
////    }
//}

package com.project.controller;

import com.project.dto.UserDTO;
import com.project.models.User;
import com.project.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

//    // Get the profile
//    @PostMapping("/user/get-profile")
//    public ResponseEntity<UserDTO> getMyProfile(@RequestBody UserDTO req){
//        String email = req.getEmail(); // Get email from request body
//        UserDTO response = usersService.getMyInfo(email);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }

    // Delete User
    @DeleteMapping("/admin/deleteUser/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(usersService.deleteUser(userId));
    }
}