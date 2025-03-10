package com.project.servicetest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import com.project.services.JWTUtils;
import com.project.services.UserService;

import java.util.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private AuthenticationManager  authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setName("Test User");
        registrationRequest.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
        user.setUserId(1L);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO response = userService.register(registrationRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("User saved successfully", response.getMessage());
        assertNotNull(response.getUsers());
    }

    @Test
    public void testRegisterUserEmailExists() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        UserDTO response = userService.register(registrationRequest);

        assertEquals(409, response.getStatusCode());
        assertEquals("User with this email already exists.", response.getMessage());
    }

    @Test
    public void testRegisterUserException() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.register(registrationRequest);

        assertEquals(500, response.getStatusCode());
        assertEquals("Database error", response.getError());
    }

//    @Test
//    public void testLoginSuccess() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//        user.setPassword("encodedPassword");
//        user.setRole(Role.CUSTOMER);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//        when(jwtUtils.generateToken(any(User.class))).thenReturn("jwtToken");
//
//        HashMap<String, Object> claims = new HashMap<>();
//        when(jwtUtils.generateRefreshToken(eq(claims), any(UserDetails.class))).thenReturn("refreshToken");
//
//        UserDTO response = userService.login(loginRequest);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Successfully logged in", response.getMessage());
//        assertEquals("jwtToken", response.getToken());
//        assertEquals("refreshToken", response.getRefreshToken());
//    }

    @Test
    public void testLoginUserNotFound() {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserDTO response = userService.login(loginRequest);

        assertEquals(500, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void testLoginException() {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Authentication error"));

        UserDTO response = userService.login(loginRequest);

        assertEquals(500, response.getStatusCode());
        assertEquals("Authentication error", response.getMessage());
    }

//    @Test
//    public void testGetAllUsersSuccess() {
//        List<User> users = Arrays.asList(
//                new User(1L, "customer1@example.com", "Customer One", "password", Role.CUSTOMER),
//                new User(2L, "customer2@example.com", "Customer Two", "password", Role.CUSTOMER)
//        );
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        UserDTO response = userService.getAllUsers();
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Successful", response.getMessage());
//        assertEquals(2, response.getOurUsersList().size());
//    }

//    @Test
//    public void testGetAllUsersNoCustomers() {
//        List<User> users = Arrays.asList(
//                new User(1L, "admin@example.com", "Admin User", "password", Role.ADMIN)
//        );
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        UserDTO response = userService.getAllUsers();
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("No customers found", response.getMessage());
//    }

    @Test
    public void testGetAllUsersException() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.getAllUsers();

        assertEquals(500, response.getStatusCode());
        assertEquals("Error occurred: Database error", response.getMessage());
    }

    @Test
    public void testGetUserByIDSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDTO response = userService.getUserByID(1L);

        assertEquals(200, response.getStatusCode());
        assertEquals("User with id '1' found successfully", response.getMessage());
        assertNotNull(response.getUsers());
    }

    @Test
    public void testGetUserByIDNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDTO response = userService.getUserByID(1L);

        assertEquals(500, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void testDeleteUserSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(anyLong());

        UserDTO response = userService.deleteUser(1L);

        assertEquals(200, response.getStatusCode());
        assertEquals("User deleted successfully", response.getMessage());
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDTO response = userService.deleteUser(1L);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found for deletion", response.getMessage());
    }

    @Test
    public void testDeleteUserException() {
        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.deleteUser(1L);

        assertEquals(500, response.getStatusCode());
        assertEquals("Error occurred while deleting user: Database error", response.getMessage());
    }

    @Test
    public void testUpdateUserSuccess() {
        User existingUser = new User();
        existingUser.setUserId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setName("Test User");
        existingUser.setPassword("password");
        existingUser.setRole(Role.CUSTOMER);

        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setRole(Role.ADMIN);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(200, response.getStatusCode());
        assertEquals("User updated successfully", response.getMessage());
        assertNotNull(response.getUsers());
    }

    @Test
    public void testUpdateUserException() {
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setRole(Role.ADMIN);

        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(500, response.getStatusCode());
        assertEquals("Error occurred while updating the user: Database error", response.getMessage());
    }

    @Test
    public void testGetMyInfoSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDTO response = userService.getMyInfo("test@example.com");

        assertEquals(200, response.getStatusCode());
        assertEquals("Successful", response.getMessage());
        assertNotNull(response.getUsers());
    }

    @Test
    public void testGetMyInfoNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserDTO response = userService.getMyInfo("test@example.com");

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void testGetMyInfoException() {
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.getMyInfo("test@example.com");

        assertEquals(500, response.getStatusCode());
        assertEquals("Error occurred while getting your info: Database error", response.getMessage());
    }
}