//package com.project.controller;
//import com.project.dto.UserCreditDTO;
//import com.project.dto.UserDTO;
//import com.project.models.User;
//import com.project.services.UserService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import org.springframework.http.HttpHeaders;
//import io.jsonwebtoken.Claims;
//
//@RequestMapping("/dbs/user")
//@RestController
//@Validated
////@CrossOrigin(origins = "http://localhost:4200")
//public class UserController {
//
//
//	 private final UserService usersService;
//
//	    public UserController(UserService usersService) {
//	        this.usersService = usersService;
//	    }
//
//    /**
//     * Registers a new user.
//     *
//     * @param reg UserDTO containing registration details
//     * @return ResponseEntity<UserDTO> - registered user details
//     */
//    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully registered"),
//            @ApiResponse(responseCode = "400", description = "Invalid input")
//    })
//    @PostMapping("/auth/register")
//    public ResponseEntity<UserDTO> register(@Valid  @RequestBody UserDTO reg){
//        return ResponseEntity.ok(usersService.register(reg));
//    }
//
//    /**
//     * Logs in a user
//     *
//     * @param req UserDTO containing login details
//     * @return ResponseEntity<UserDTO> - logged in user details
//     */
//    @Operation(summary = "Login a user", description = "Logs in a user with the provided credentials.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized")
//    })
//    @PostMapping("/auth/login")
//    public ResponseEntity<UserDTO> login(@RequestBody UserDTO req){
//        return ResponseEntity.ok(usersService.login(req));
//    }
//
//    /**
//     * Refreshes the authentication token.
//     *
//     * @param req UserDTO containing refresh token details
//     * @return ResponseEntity<UserDTO> - new authentication token
//     */
//    @Operation(summary = "Refresh authentication token", description = "Refreshes the authentication token for the user.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
//            @ApiResponse(responseCode = "400", description = "Invalid token")
//    })
//    @PostMapping("/auth/refresh")
//    public ResponseEntity<UserDTO> refreshToken(@RequestBody UserDTO req){
//        return ResponseEntity.ok(usersService.refreshToken(req));
//    }
//
//    /**
//     * Retrieves a list of all users.
//     *
//     * @return ResponseEntity<List<UserDTO>> - list of all users
//     */
//    @Operation(summary = "Get all users", description = "Retrieves a list of all users. Accessible only to admin.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
//            @ApiResponse(responseCode = "403", description = "Forbidden")
//    })
//
//    @GetMapping("/admin/get-all-users")
//    public ResponseEntity<UserDTO> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
//        UserDTO response = usersService.getAllUsers(authorizationHeader);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }
//
//    /**
//     * Retrieves a user by ID.
//     *
//     * @param userId ID of the user to retrieve
//     * @return ResponseEntity<UserDTO> - user details
//     */
//    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID. Accessible only to admin.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @GetMapping("/get-user/{userId}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
//        return ResponseEntity.ok(usersService.getUserByID(userId));
//    }
//
//    /**
//     * Updates a user.
//     *
//     * @param userId ID of the user to update
//     * @param reqRes User object containing updated details
//     * @return ResponseEntity<UserDTO> - updated user details
//     */
//    @Operation(summary = "Update user", description = "Updates the details of a user. Accessible only to admin.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody User reqRes){
//        return ResponseEntity.ok(usersService.updateUser(userId, reqRes));
//    }
//
//    /**
//     *
//     * Deletes a user.
//     *
//     * @param userId ID of the user to delete
//     * @return ResponseEntity<UserDTO> - confirmation of deletion
//     */
//    @Operation(summary = "Delete user", description = "Deletes a user by their ID. Accessible only to admin.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @DeleteMapping("/admin/deleteUser/{userId}")
//    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId){
//        return ResponseEntity.ok(usersService.deleteUser(userId));
//    }
//
//    @Operation(summary = "Get user profile", description = "Retrieves the user's profile using the JWT token.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized")
//    })
//    @GetMapping("/profile")
//    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
//        UserDTO response = usersService.getUserProfile(authorizationHeader);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }
//
//    @GetMapping("/role")
//    public String getUserRole(@RequestHeader("Authorization") String authorizationHeader) {
//        Claims claims = usersService.verifyJwtAndGetClaims(authorizationHeader);
//        return claims.get("role", String.class);
//    }
//
//
//    //endpoints for usercredit
//    @PutMapping("/debit-credits/{userId}/{amount}")
//    public ResponseEntity<UserCreditDTO> debitCredits(@PathVariable Long userId, @PathVariable Double amount) {
//        return usersService.debitCredits(userId, amount);
//    }
//
//    @GetMapping("/get-user-credits/{userId}")
//    public UserCreditDTO getUserCredit(@PathVariable Long userId) {
//        return usersService.getUserCredit(userId);
//    }
//
//    @PutMapping("/add-credits/{userId}/{amount}")
//    public ResponseEntity<UserCreditDTO> addCredits(@PathVariable Long userId, @PathVariable Double amount) {
//        return usersService.addCredits(userId, amount);
//    }
//}

package com.project.controller;
import com.project.dto.UserCreditDTO;
import com.project.dto.UserDTO;
import com.project.models.User;
import com.project.services.UserService;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(usersService.register(reg));
    }

    @Operation(summary = "Login a user", description = "Logs in a user with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO req){
        return ResponseEntity.ok(usersService.login(req));
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
        // The Gateway will ensure the user is authenticated
        return ResponseEntity.ok(usersService.getUserProfile(authorizationHeader));
    }

    @GetMapping("/role")
    public String getUserRole(@RequestHeader("Authorization") String authorizationHeader) {
        // The Gateway will have already validated the token
        // You might still want to extract the role here for internal use if needed
        // Claims claims = usersService.verifyJwtAndGetClaims(authorizationHeader); // Removed method
        // return claims.get("role", String.class);
        // Consider how you want to pass the role information if needed internally
        return "Role information handled by Gateway";
    }

    //endpoints for usercredit
    @PutMapping("/debit-credits/{userId}/{amount}")
    public ResponseEntity<UserCreditDTO> debitCredits(@PathVariable Long userId, @PathVariable Double amount) {
        // The Gateway will ensure the user is authenticated
        return usersService.debitCredits(userId, amount);
    }

    @GetMapping("/get-user-credits/{userId}")
    public UserCreditDTO getUserCredit(@PathVariable Long userId) {
        // The Gateway will ensure the user is authenticated
        return usersService.getUserCredit(userId);
    }

    @PutMapping("/add-credits/{userId}/{amount}")
    public ResponseEntity<UserCreditDTO> addCredits(@PathVariable Long userId, @PathVariable Double amount) {
        // The Gateway will ensure the user is authenticated
        return usersService.addCredits(userId, amount);
    }
}