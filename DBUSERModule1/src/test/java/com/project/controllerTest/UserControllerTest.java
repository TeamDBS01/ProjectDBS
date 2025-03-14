//package com.project.controllerTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import com.project.controller.UserController;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.project.dto.UserDTO;
//import com.project.models.Role;
//import com.project.models.User;
//import com.project.services.UserService;
//
//public class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    public void testRegisterUserSuccess() {
//        UserDTO registrationRequest = new UserDTO();
//        registrationRequest.setEmail("test@example.com");
//        registrationRequest.setName("Test User");
//        registrationRequest.setPassword("password123");
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(200);
//        response.setMessage("User saved successfully");
//        response.setName("Test User");
//        response.setEmail("test@example.com");
//        response.setRole(Role.CUSTOMER);
//        response.setUserId(1L);
//
//        when(userService.register(any(UserDTO.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.register(registrationRequest);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("User saved successfully", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testLoginUserSuccess() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password123");
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(200);
//        response.setMessage("Successfully logged in");
//        response.setName("Test User");
//        response.setEmail("test@example.com");
//        response.setRole(Role.CUSTOMER);
//        response.setUserId(1L);
//
//        when(userService.login(any(UserDTO.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.login(loginRequest);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("Successfully logged in", result.getBody().getMessage());
//    }
//
//
//    @Test
//    public void testGetAllUsersSuccess() {
//        UserDTO user1 = new UserDTO();
//        user1.setUserId(1L);
//        user1.setName("Customer One");
//        user1.setEmail("customer1@example.com");
//        user1.setRole(Role.CUSTOMER);
//
//        UserDTO user2 = new UserDTO();
//        user2.setUserId(2L);
//        user2.setName("Customer Two");
//        user2.setEmail("customer2@example.com");
//        user2.setRole(Role.CUSTOMER);
//
//        List<UserDTO> users = Arrays.asList(user1, user2);
//
//        UserDTO response = new UserDTO();
//        response.setOurUsersList(users);
//        response.setStatusCode(200);
//        response.setMessage("Successful");
//
//        when(userService.getAllUsers()).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.getAllUsers();
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(2, result.getBody().getOurUsersList().size());
//        assertEquals("Successful", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testGetUserByIdSuccess() {
//        UserDTO response = new UserDTO();
//        response.setStatusCode(200);
//        response.setMessage("User with id '1' found successfully");
//        response.setName("Test User");
//        response.setEmail("test@example.com");
//        response.setRole(Role.CUSTOMER);
//        response.setUserId(1L);
//
//        when(userService.getUserByID(anyLong())).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.getUserById(1L);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("User with id '1' found successfully", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testUpdateUserSuccess() {
//        User updatedUser = new User();
//        updatedUser.setEmail("updated@example.com");
//        updatedUser.setName("Updated User");
//        updatedUser.setPassword("updatedPassword");
//        updatedUser.setRole(Role.CUSTOMER);
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(200);
//        response.setMessage("User updated successfully");
//        response.setName("Updated User");
//        response.setEmail("updated@example.com");
//        response.setRole(Role.CUSTOMER);
//        response.setUserId(1L);
//
//        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.updateUser(1L, updatedUser);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("User updated successfully", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testDeleteUserSuccess() {
//        UserDTO response = new UserDTO();
//        response.setStatusCode(200);
//        response.setMessage("User deleted successfully");
//        response.setUserId(1L);
//
//        when(userService.deleteUser(anyLong())).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.deleteUser(1L);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("User deleted successfully", result.getBody().getMessage());
//    }
//
//    // Negative Test Cases
//
//    @Test
//    public void testRegisterUserInvalidInput() {
//        UserDTO registrationRequest = new UserDTO();
//        registrationRequest.setEmail("invalid-email");
//        registrationRequest.setPassword("short");
//
//        ResponseEntity<UserDTO> result = userController.register(registrationRequest);
//
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//    }
//
//    @Test
//    public void testLoginUserNotFound() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("nonexistent@example.com");
//        loginRequest.setPassword("password123");
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(500);
//        response.setMessage("User not found");
//
//        when(userService.login(any(UserDTO.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.login(loginRequest);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
//        assertEquals("User not found", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testLoginIncorrectPassword() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("wrongpassword");
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(401);
//        response.setMessage("Incorrect password");
//
//        when(userService.login(any(UserDTO.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.login(loginRequest);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
//        assertEquals("Incorrect password", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testRefreshTokenInvalid() {
//        UserDTO refreshTokenRequest = new UserDTO();
//        refreshTokenRequest.setEmail("test@example.com");
//
//        UserDTO response = new UserDTO();
//        response.setStatusCode(400);
//        response.setMessage("Invalid token");
//
//        when(userService.refreshToken(any(UserDTO.class))).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.refreshToken(refreshTokenRequest);
//
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Invalid token", result.getBody().getMessage());
//    }
//
//    @Test
//    public void testGetAllUsersNoCustomers() {
//        UserDTO response = new UserDTO();
//        response.setOurUsersList(Collections.emptyList());
//        response.setStatusCode(404);
//        response.setMessage("No customers found");
//
//        when(userService.getAllUsers()).thenReturn(response);
//
//        ResponseEntity<UserDTO> result = userController.getAllUsers();
//
//        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
//        assertTrue(result.getBody().getOurUsersList().isEmpty());
//        assertEquals("No customers found", result.getBody().getMessage());
//    }
//}
