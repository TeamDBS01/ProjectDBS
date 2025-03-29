package com.project.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.springframework.http.ResponseEntity;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; 

import com.project.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
 

import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.services.UserService;

 class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        validAdminJwt = generateValidAdminJwt();
        invalidJwt = generateInvalidJwt();
    }
    private String validAdminJwt;
    private String invalidJwt;
    
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);   

  

 

    private String generateValidAdminJwt() {
        return Jwts.builder()
                .setClaims(Collections.singletonMap("role", Role.ADMIN.name()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key)
                .compact();
    }

    private String generateInvalidJwt() {
         return Jwts.builder()
                .setClaims(Collections.singletonMap("role", Role.CUSTOMER.name())) // token with customer role
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key)
                .compact();
    }

    @Test
      void testRegisterUserSuccess() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setName("Test User");
        registrationRequest.setPassword("password123");

        UserDTO response = new UserDTO();
        response.setStatusCode(200);
        response.setMessage("User saved successfully");
        response.setName("Test User");
        response.setEmail("test@example.com");
        response.setRole(Role.CUSTOMER);
        response.setUserId(1L);

        when(userService.register(any(UserDTO.class))).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.register(registrationRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User saved successfully", result.getBody().getMessage());
    }

    @Test
    void testLoginUserSuccess() {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        UserDTO response = new UserDTO();
        response.setStatusCode(200);
        response.setMessage("Successfully logged in");
        response.setName("Test User");
        response.setEmail("test@example.com");
        response.setRole(Role.CUSTOMER);
        response.setUserId(1L);

        when(userService.login(any(UserDTO.class))).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.login(loginRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Successfully logged in", result.getBody().getMessage());
    }
    
    @Test
    void testGetAllUsersSuccess() {
        UserDTO user1 = new UserDTO();
        user1.setUserId(1L);
        user1.setName("Customer One");
        user1.setEmail("customer1@example.com");
        user1.setRole(Role.CUSTOMER);

        UserDTO user2 = new UserDTO();
        user2.setUserId(2L);
        user2.setName("Customer Two");
        user2.setEmail("customer2@example.com");
        user2.setRole(Role.CUSTOMER);

        List<UserDTO> users = Arrays.asList(user1, user2);

        UserDTO response = new UserDTO();
        response.setOurUsersList(users);
        response.setStatusCode(200);
        response.setMessage("Successful");

        when(userService.getAllUsers("Bearer " + validAdminJwt)).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.getAllUsers("Bearer " + validAdminJwt);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().getOurUsersList().size());
        assertEquals("Successful", result.getBody().getMessage());
    }

    @Test
    void testGetAllUsersNoCustomers() {
        UserDTO response = new UserDTO();
        response.setOurUsersList(Collections.emptyList());
        response.setStatusCode(200);
        response.setMessage("Successful");

        when(userService.getAllUsers("Bearer " + validAdminJwt)).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.getAllUsers("Bearer " + validAdminJwt);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getOurUsersList().isEmpty());
        assertEquals("Successful", result.getBody().getMessage());
    }

     @Test
     void testGetAllUsersInvalidJwt() {
         UserDTO response = new UserDTO();
         response.setStatusCode(401); // Unauthorized
         response.setMessage("Unauthorized access");

         when(userService.getAllUsers("Bearer " + invalidJwt)).thenReturn(response);

         ResponseEntity<UserDTO> result = userController.getAllUsers("Bearer " + invalidJwt);

         assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
         assertEquals("Unauthorized access", result.getBody().getMessage());
     }
  
 

    @Test
    void testGetUserByIdSuccess() {
        UserDTO response = new UserDTO();
        response.setStatusCode(200);
        response.setMessage("User with id '1' found successfully");
        response.setName("Test User");
        response.setEmail("test@example.com");
        response.setRole(Role.CUSTOMER);
        response.setUserId(1L);

        when(userService.getUserByID(anyLong())).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User with id '1' found successfully", result.getBody().getMessage());
    }

    @Test
      void testUpdateUserSuccess() {
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setRole(Role.CUSTOMER);

        UserDTO response = new UserDTO();
        response.setStatusCode(200);
        response.setMessage("User updated successfully");
        response.setName("Updated User");
        response.setEmail("updated@example.com");
        response.setRole(Role.CUSTOMER);
        response.setUserId(1L);

        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User updated successfully", result.getBody().getMessage());
    }

    @Test
     void testDeleteUserSuccess() {
        UserDTO response = new UserDTO();
        response.setStatusCode(200);
        response.setMessage("User deleted successfully");
        response.setUserId(1L);

        when(userService.deleteUser(anyLong())).thenReturn(response);

        ResponseEntity<UserDTO> result = userController.deleteUser(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User deleted successfully", result.getBody().getMessage());
    }

    
    
}