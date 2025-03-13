package com.project.servicetest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import com.project.services.UserService;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
        assertNotNull(response.getEmail());
        assertNotNull(response.getName());
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

    @Test
    public void testLoginUserNotFound() {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

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

        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Authentication error"));

        UserDTO response = userService.login(loginRequest);

        assertEquals(500, response.getStatusCode());
        assertEquals("Authentication error", response.getMessage());
    }

    @Test
    public void testGetAllUsersSuccess() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setEmail("customer1@example.com");
        user1.setName("Customer One");
        user1.setPassword("password");
        user1.setRole(Role.CUSTOMER);

        User user2 = new User();
        user2.setUserId(2L);
        user2.setEmail("customer2@example.com");
        user2.setName("Customer Two");
        user2.setPassword("password");
        user2.setRole(Role.CUSTOMER);

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> response = userService.getAllUsers().getOurUsersList();

        assertEquals(2, response.size());
        assertEquals("customer1@example.com", response.get(0).getEmail());
        assertEquals("customer2@example.com", response.get(1).getEmail());
    }

//    @Test
//    public void testGetAllUsersNoCustomers() {
//        User adminUser = new User();
//        adminUser.setUserId(1L);
//        adminUser.setEmail("admin@example.com");
//        adminUser.setName("Admin User");
//        adminUser.setPassword("password");
//        adminUser.setRole(Role.ADMIN);
//
//        List<User> users = Arrays.asList(adminUser);
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        UserDTO response = userService.getAllUsers();
//
//        assertNotNull(response.getOurUsersList());
//        assertTrue(response.getOurUsersList().isEmpty());
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

        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
    }

    @Test
    public void testGetUserByIDNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDTO response = userService.getUserByID(1L);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

//    @Test
//    public void testGetUserByIDException() {
//        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.getUserByID(1L);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Error occurred: Database error", response.getMessage());
//    }

    @Test
    public void testUpdateUserSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setRole(Role.CUSTOMER);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals("updated@example.com", response.getEmail());
        assertEquals("Updated User", response.getName());
    }

    @Test
    public void testUpdateUserNotFound() {
        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setRole(Role.CUSTOMER);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found for update", response.getMessage());
    }

    @Test
    public void testUpdateUserException() {
        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setEmail("updated@example.com");
        updatedUser.setName("Updated User");
        updatedUser.setPassword("updatedPassword");
        updatedUser.setRole(Role.CUSTOMER);

        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(500, response.getStatusCode());
        assertEquals("Error occurred while updating the user: Database error", response.getMessage());
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
}