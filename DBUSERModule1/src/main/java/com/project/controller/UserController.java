package com.project.controller;
import com.project.dto.UserCreditDTO;
import com.project.dto.UserDTO;
import com.project.models.User;
import com.project.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;

@RequestMapping("/dbs/user")
@RestController
@Validated
//@CrossOrigin("http://localhost:4200/")
public class UserController {

    private final UserService usersService;

    public UserController(UserService usersService) {
        this.usersService = usersService;
    }

    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> register(@Valid  @RequestBody UserDTO reg){
//        return ResponseEntity.ok(usersService.register(reg));
        UserDTO userDTO = usersService.register(reg);
        if(userDTO.getName()!=null){
            return new ResponseEntity<>(userDTO,HttpStatus.ACCEPTED);
        } else{
            return new ResponseEntity<>(userDTO,HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Login a user", description = "Logs in a user with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserDTO req){
        UserDTO userDTO = usersService.login(req);
        if(userDTO.getName()!=null){
            return new ResponseEntity<>(userDTO,HttpStatus.ACCEPTED);
        } else{
            return new ResponseEntity<>(userDTO,HttpStatus.FORBIDDEN);
        }
//        return ResponseEntity.ok(usersService.login(req));
    }

    @Operation(summary = "Refresh authentication token", description = "Refreshes the authentication token for the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
            @ApiResponse(responseCode = "400", description = "Invalid token")
    })
    @PostMapping("/auth/refresh")
    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO req){
        return ResponseEntity.ok(usersService.refreshToken(req));
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users. Accessible only to admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<UserDTO> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // The Gateway will ensure the user is authenticated and has the ADMIN role
        return ResponseEntity.ok(usersService.getAllUsers(authorizationHeader));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID. Accessible only to admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get-user/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
        // The Gateway will ensure the user is authenticated and has the ADMIN role
        return ResponseEntity.ok(usersService.getUserByID(userId));
    }

    @Operation(summary = "Update user", description = "Updates the details of a user. Accessible only to admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody User reqRes){
        // The Gateway will ensure the user is authenticated
        return ResponseEntity.ok(usersService.updateUser(userId, reqRes));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their ID. Accessible only to admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/admin/deleteUser/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId){
        // The Gateway will ensure the user is authenticated and has the ADMIN role
        return ResponseEntity.ok(usersService.deleteUser(userId));
    }

    @Operation(summary = "Get user profile", description = "Retrieves the user's profile using the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        return ResponseEntity.ok(usersService.getUserProfile(authorizationHeader));
    }

    @GetMapping("/role")
    public String getUserRole(@RequestHeader("Authorization") String authorizationHeader) {

        return "Role information handled by Gateway";
    }

    //endpoints for usercredit
    @PutMapping("/debit-credits/{userId}/{amount}")
    public ResponseEntity<UserCreditDTO> debitCredits(@PathVariable Long userId, @PathVariable Double amount) {
        return usersService.debitCredits(userId, amount);
    }

    @GetMapping("/get-user-credits/{userId}")
    public UserCreditDTO getUserCredit(@PathVariable Long userId) {

        return usersService.getUserCredit(userId);
    }

    @PutMapping("/add-credits/{userId}/{amount}")
    public ResponseEntity<UserCreditDTO> addCredits(@PathVariable Long userId, @PathVariable Double amount) {

        return usersService.addCredits(userId, amount);
    }
}