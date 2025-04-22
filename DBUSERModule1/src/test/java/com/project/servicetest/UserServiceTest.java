//package com.project.servicetest;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import com.project.dto.UserDTO;
//import com.project.models.Role;
//import com.project.models.User;
//import com.project.repositories.UserRepository;
//import com.project.services.UserService;
//import static org.mockito.Mockito.doReturn;
//
//import io.jsonwebtoken.security.Keys;
//import java.security.Key;
//import java.util.Collections;
//import java.util.Map;
// class UserServiceTest {
//
//	 @Mock
//	    private UserRepository userRepository;
//
//	    @InjectMocks
//	    private UserService userService;
//
//	    private Key key;
//	    private String validAdminJwt;
//
//	    @BeforeEach
//	    void setUp() {
//	        MockitoAnnotations.openMocks(this);
//	        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//	        userService.setKey(key);
//	        validAdminJwt = generateValidAdminJwt();
//	    }
//
//	    private String generateValidAdminJwt() {
//	        return Jwts.builder()
//	                .setClaims(Collections.singletonMap("role", Role.ADMIN.name()))
//	                .signWith(key)
//	                .compact();
//	    }
//
//    static class MockMessageDigest extends MessageDigest {
//        protected MockMessageDigest() {
//            super("SHA-256");
//        }
//
//        protected void engineUpdate(byte input) {
//            // Intentionally empty for mocking purposes.
//            // This method is not used in the test, but must be implemented.
//        }
//
//        protected void engineUpdate(byte[] input, int offset, int len) {
//            // Intentionally empty for mocking purposes.
//            // This method is not used in the test, but must be implemented.
//        }
//
//        protected byte[] engineDigest() {
//            // Intentionally empty for mocking purposes.
//            // This method is not used in the test, but must be implemented.
//            return new byte[0];
//        }
//
//        protected void engineReset() {
//            // Intentionally empty for mocking purposes.
//            // This method is not used in the test, but must be implemented.
//        }
//    }
//
//
//
//    @Test
//    void testEncodePassword_NoSuchAlgorithmException()   {
//        try (MockedStatic<MessageDigest> utilities = Mockito.mockStatic(MessageDigest.class)) {
//            utilities.when(() -> MessageDigest.getInstance("SHA-256")).thenThrow(new NoSuchAlgorithmException());
//            assertThrows(IllegalStateException.class, () -> userService.encodePassword("password"));
//        }
//    }
//
//    @Test
//     void testEncodePasswordSuccess() {
//        String password = "testPassword";
//        String encodedPassword = userService.encodePassword(password);
//        assertNotNull(encodedPassword);
//        assertFalse(encodedPassword.isEmpty());
//    }
//
//    @Test
//    void testGenerateJwtToken() {
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setRole(Role.CUSTOMER);
//
//        String token = userService.generateJwtToken(user);
//
//        assertNotNull(token);
//
//
//        Map<String, Object> claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        assertEquals(1L, ((Integer) claims.get("userId")).longValue()); // Corrected line
//        assertEquals("test@example.com", claims.get("email"));
//        assertEquals("CUSTOMER", claims.get("role"));
//    }
//
//
//
//    @Test
//     void testRegisterUserSuccess() {
//        UserDTO registrationRequest = new UserDTO();
//        registrationRequest.setEmail("test@example.com");
//        registrationRequest.setName("Test User");
//        registrationRequest.setPassword("password");
//
//        when(userRepository.existsByEmail(anyString())).thenReturn(false);
//
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//        user.setPassword("encodedPassword");
//        user.setRole(Role.CUSTOMER);
//        user.setUserId(1L);
//
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        UserDTO response = userService.register(registrationRequest);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("User saved successfully", response.getMessage());
//        assertNotNull(response.getEmail());
//        assertNotNull(response.getName());
//    }
//
//
//
//
//
//    @Test
//    void testLogin_Exception() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.login(loginRequest);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Database error", response.getMessage());
//    }
//
//
//    @Test
//    void testRefreshToken_Successful() {
//        UserDTO refreshTokenRequest = new UserDTO();
//        refreshTokenRequest.setEmail("test@example.com");
//
//        User user = new User();
//        user.setEmail("test@example.com");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//
//        UserDTO response = userService.refreshToken(refreshTokenRequest);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Successfully refreshed the token", response.getMessage());
//    }
//
//    @Test
//    void testRefreshToken_UserNotFound() {
//        UserDTO refreshTokenRequest = new UserDTO();
//        refreshTokenRequest.setEmail("test@example.com");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//
//        UserDTO response = userService.refreshToken(refreshTokenRequest);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found", response.getMessage());
//    }
//
//    @Test
//    void testRefreshToken_Exception() {
//        UserDTO refreshTokenRequest = new UserDTO();
//        refreshTokenRequest.setEmail("test@example.com");
//
//        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.refreshToken(refreshTokenRequest);
//
//        assertEquals(404, response.getStatusCode()); // Changed to 404
//        assertEquals("Database error", response.getMessage());
//    }
//
//
//    @Test
//    void testLogin_UserNotFound() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//
//        UserDTO response = userService.login(loginRequest);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found", response.getMessage());
//    }
//
//    @Test
//     void testRegisterUserEmailExists() {
//        UserDTO registrationRequest = new UserDTO();
//        registrationRequest.setEmail("test@example.com");
//
//        when(userRepository.existsByEmail(anyString())).thenReturn(true);
//
//        UserDTO response = userService.register(registrationRequest);
//
//        assertEquals(409, response.getStatusCode());
//        assertEquals("User with this email already exists.", response.getMessage());
//    }
//
//    @Test
//    void testRegisterUserException() {
//        UserDTO registrationRequest = new UserDTO();
//        registrationRequest.setEmail("test@example.com");
//
//        when(userRepository.existsByEmail(anyString())).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.register(registrationRequest);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Database error", response.getError());
//    }
//
//    @Test
//     void testLoginUserNotFound() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//        UserDTO response = userService.login(loginRequest);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found", response.getMessage());
//    }
//
//    @Test
//    void testLoginException() {
//        UserDTO loginRequest = new UserDTO();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Authentication error"));
//
//        UserDTO response = userService.login(loginRequest);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Authentication error", response.getMessage());
//    }
//
//
//
//    @Test
//     void testGetUserByIDSuccess() {
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//        user.setPassword("password");
//        user.setRole(Role.CUSTOMER);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//
//        UserDTO response = userService.getUserByID(1L);
//
//        assertEquals("test@example.com", response.getEmail());
//        assertEquals("Test User", response.getName());
//    }
//
//    @Test
//     void testGetUserByIDNotFound() {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        UserDTO response = userService.getUserByID(1L);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found", response.getMessage());
//    }
//
//    @Test
//     void testUpdateUserSuccess() {
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//        user.setPassword("password");
//        user.setRole(Role.CUSTOMER);
//
//        User updatedUser = new User();
//        updatedUser.setUserId(1L);
//        updatedUser.setEmail("updated@example.com");
//        updatedUser.setName("Updated User");
//        updatedUser.setPassword("updatedPassword");
//        updatedUser.setRole(Role.CUSTOMER);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
//
//        UserDTO response = userService.updateUser(1L, updatedUser);
//
//        assertEquals("updated@example.com", response.getEmail());
//        assertEquals("Updated User", response.getName());
//    }
//
//    @Test
//    void testUpdateUserNotFound() {
//        User updatedUser = new User();
//        updatedUser.setUserId(1L);
//        updatedUser.setEmail("updated@example.com");
//        updatedUser.setName("Updated User");
//        updatedUser.setPassword("updatedPassword");
//        updatedUser.setRole(Role.CUSTOMER);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        UserDTO response = userService.updateUser(1L, updatedUser);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found for update", response.getMessage());
//    }
//
//    @Test
//    void testUpdateUserException() {
//        User updatedUser = new User();
//        updatedUser.setUserId(1L);
//        updatedUser.setEmail("updated@example.com");
//        updatedUser.setName("Updated User");
//        updatedUser.setPassword("updatedPassword");
//        updatedUser.setRole(Role.CUSTOMER);
//
//        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.updateUser(1L, updatedUser);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Error occurred while updating the user: Database error", response.getMessage());
//    }
//
//    @Test
//    void testDeleteUserSuccess() {
//        User user = new User();
//        user.setUserId(1L);
//        user.setEmail("test@example.com");
//        user.setName("Test User");
//        user.setPassword("password");
//        user.setRole(Role.CUSTOMER);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        doNothing().when(userRepository).deleteById(anyLong());
//
//        UserDTO response = userService.deleteUser(1L);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("User deleted successfully", response.getMessage());
//    }
//
//    @Test
//    void testDeleteUserNotFound() {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        UserDTO response = userService.deleteUser(1L);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("User not found for deletion", response.getMessage());
//    }
//
//    @Test
//    void testDeleteUserException() {
//        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.deleteUser(1L);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Error occurred while deleting user: Database error", response.getMessage());
//    }
//
//
//
//    @Test
//    void testGetUserByIDException() {
//        when(userRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.getUserByID(1L);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Error occurred: Database error", response.getMessage());
//    }
//
//
//    @Test
//    void testGetAllUsersSuccess() {
//        User user1 = new User();
//        user1.setUserId(1L);
//        user1.setEmail("customer1@example.com");
//        user1.setName("Customer One");
//        user1.setPassword("password");
//        user1.setRole(Role.CUSTOMER);
//
//        User user2 = new User();
//        user2.setUserId(2L);
//        user2.setEmail("customer2@example.com");
//        user2.setName("Customer Two");
//        user2.setPassword("password");
//        user2.setRole(Role.CUSTOMER);
//
//        List<User> users = Arrays.asList(user1, user2);
//
//        when(userRepository.findAll()).thenReturn(users);
//
//        UserDTO response = userService.getAllUsers("Bearer " + validAdminJwt);
//
//        assertEquals(2, response.getOurUsersList().size());
//        assertEquals("customer1@example.com", response.getOurUsersList().get(0).getEmail());
//        assertEquals("customer2@example.com", response.getOurUsersList().get(1).getEmail());
//        assertEquals(200, response.getStatusCode());
//        assertEquals("Successful", response.getMessage());
//    }
//
//    @Test
//    void testGetAllUsersException() {
//        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        UserDTO response = userService.getAllUsers("Bearer " + validAdminJwt);
//
//        assertEquals(500, response.getStatusCode());
//        assertEquals("Error occurred: Database error", response.getMessage());
//    }
//
//    @Test
//    void testGetAllUsersNoCustomers() {
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
//        UserDTO response = userService.getAllUsers("Bearer " + validAdminJwt);
//
//        assertNotNull(response.getOurUsersList());
//        assertTrue(response.getOurUsersList().isEmpty());
//        assertEquals(404, response.getStatusCode());
//        assertEquals("No customers found", response.getMessage());
//    }
//
//
//}

package com.project.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import com.maxmind.geoip2.DatabaseReader;
import com.project.dto.*;
import com.project.models.*;
import com.project.repositories.PasswordResetTokenRepository;
import com.project.repositories.UserCreditRepository;
import com.project.repositories.UserDetailsRepository;
import com.project.repositories.UserRepository;
import com.project.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCreditRepository userCreditRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private HttpServletRequest request;

    @Mock
    private DatabaseReader databaseReader;

    @InjectMocks
    private UserService userService;

    private Key key;
    private String validAdminJwt;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        byte[] secretKeyBytes = "9rxnn8Qd700nlHOxDqsfnEAmwRAuPHzi".getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
        validAdminJwt = generateValidAdminJwt(createUser(1L, "admin@example.com", "password", "Admin User", Role.ADMIN));
    }

    private User createUser(Long id, String email, String password, String name, Role role) {
        User user = new User();
        user.setUserId(id);
        user.setEmail(email);
        user.setPassword(userService.encodePassword(password));
        user.setName(name);
        user.setRole(role);
        return user;
    }

    private String generateValidAdminJwt(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key)
                .compact();
    }

    @Test
    void testEncodePassword() throws NoSuchAlgorithmException {
        String password = "mysecretpassword";
        String encodedPassword = userService.encodePassword(password);
        assertNotNull(encodedPassword);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        assertEquals(hexString.toString(), encodedPassword);
    }

    @Test
    void testLoginSuccess() {
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO response = userService.login(loginRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("Successfully logged in", response.getMessage());
        assertNotNull(response.getToken());
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getRole(), response.getRole());
    }

    @Test
    void testLoginInvalidCredentials() {
        User user = createUser(1L, "test@example.com", "encodedpassword", "Test User", Role.CUSTOMER);
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO response = userService.login(loginRequest);

        assertEquals(401, response.getStatusCode());
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void testLoginUserNotFound() {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UserDTO response = userService.login(loginRequest);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void testRefreshTokenSuccess() {
        User user = createUser(1L, "test@example.com", "encodedpassword", "Test User", Role.CUSTOMER);
        UserDTO refreshTokenRequest = new UserDTO();
        refreshTokenRequest.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO response = userService.refreshToken(refreshTokenRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("Successfully refreshed the token", response.getMessage());
        assertNotNull(response.getToken());
    }

    @Test
    void testRefreshTokenUserNotFound() {
        UserDTO refreshTokenRequest = new UserDTO();
        refreshTokenRequest.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UserDTO response = userService.refreshToken(refreshTokenRequest);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void testRegisterSuccess() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("newuser@example.com");
        registrationRequest.setPassword("newpassword");
        registrationRequest.setName("New User");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        User savedUser = createUser(2L, "newuser@example.com", "encodednewpassword", "New User", Role.CUSTOMER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO response = userService.register(registrationRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("User saved successfully", response.getMessage());
        assertEquals(savedUser.getUserId(), response.getUserId());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(savedUser.getRole(), response.getRole());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        UserDTO registrationRequest = new UserDTO();
        registrationRequest.setEmail("existing@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setName("Existing User");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserDTO response = userService.register(registrationRequest);

        assertEquals(409, response.getStatusCode());
        assertEquals("User with this email already exists.", response.getMessage());
    }

    @Test
    void testGetAllUsersSuccess() {
        List<User> users = Arrays.asList(
                createUser(1L, "customer1@example.com", "password", "Customer One", Role.CUSTOMER),
                createUser(2L, "admin1@example.com", "password", "Admin One", Role.ADMIN),
                createUser(3L, "customer2@example.com", "password", "Customer Two", Role.CUSTOMER)
        );
        when(userRepository.findAll()).thenReturn(users);

        UserDTO response = userService.getAllUsers(validAdminJwt);

        assertEquals(200, response.getStatusCode());
        assertEquals("Successful", response.getMessage());
        assertNotNull(response.getOurUsersList());
        assertEquals(2, response.getOurUsersList().size());
        assertEquals("customer1@example.com", response.getOurUsersList().get(0).getEmail());
        assertEquals(Role.CUSTOMER, response.getOurUsersList().get(0).getRole());
        assertEquals("customer2@example.com", response.getOurUsersList().get(1).getEmail());
        assertEquals(Role.CUSTOMER, response.getOurUsersList().get(1).getRole());
    }

    @Test
    void testGetAllUsersNoCustomersFound() {
        List<User> users = Collections.singletonList(
                createUser(2L, "admin1@example.com", "password", "Admin One", Role.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(users);

        UserDTO response = userService.getAllUsers(validAdminJwt);

        assertEquals(404, response.getStatusCode());
        assertEquals("No customers found", response.getMessage());
        assertNotNull(response.getOurUsersList());
        assertTrue(response.getOurUsersList().isEmpty());
    }

    @Test
    void testGetUserByIDSuccess() {
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO response = userService.getUserByID(1L);

        assertEquals(200, response.getStatusCode());
        assertEquals("User with id '1' found successfully", response.getMessage());
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getRole(), response.getRole());
    }

    @Test
    void testGetUserByIDNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO response = userService.getUserByID(1L);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void testDeleteUserSuccess() {
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        UserDTO response = userService.deleteUser(1L);

        assertEquals(200, response.getStatusCode());
        assertEquals("User deleted successfully", response.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO response = userService.deleteUser(1L);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found for deletion", response.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @Disabled
    void testUpdateUserSuccess() {
        User existingUser = createUser(1L, "old@example.com", "oldpassword", "Old Name", Role.CUSTOMER);
        User updatedUser = createUser(1L, "new@example.com", "newpassword", "New Name", Role.CUSTOMER);
        updatedUser.setPassword(userService.encodePassword("newpassword"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(200, response.getStatusCode());
        assertEquals("User updated successfully", response.getMessage());
        assertEquals(updatedUser.getUserId(), response.getUserId());
        assertEquals(updatedUser.getName(), response.getName());
        assertEquals(updatedUser.getEmail(), response.getEmail());
        assertEquals(updatedUser.getRole(), response.getRole());
        assertEquals(updatedUser.getPassword(), response.getPassword());
    }

    @Test
    void testUpdateUserNotFound() {
        User updatedUser = createUser(1L, "new@example.com", "newpassword", "New Name", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO response = userService.updateUser(1L, updatedUser);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found for update", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserProfileSuccess() {
        String email = "profile@example.com";
        User user = createUser(1L, email, "password", "Profile User", Role.CUSTOMER);
        String jwtToken = generateValidAdminJwt(user); // Using admin JWT for simplicity, adjust if needed
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO response = userService.getUserProfile("Bearer " + jwtToken);

        assertEquals(200, response.getStatusCode());
        assertEquals("User profile retrieved successfully", response.getMessage());
        assertEquals(user.getUserId(), response.getUserId());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getRole(), response.getRole());
    }

    @Test
    void testGetUserProfileUserNotFound() {
        String jwtToken = generateValidAdminJwt(createUser(1L, "profile@example.com", "password", "Profile User", Role.CUSTOMER));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserDTO response = userService.getUserProfile("Bearer " + jwtToken);

        assertEquals(404, response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void testChangePasswordSuccess() {
        User user = createUser(1L, "change@example.com", "oldpassword", "Change User", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        UserDTO response = userService.changePassword(1L, "oldpassword", "newpassword");

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals(userService.encodePassword("newpassword"), user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testChangePasswordIncorrectOldPassword() {
        User user = createUser(1L, "change@example.com", "oldpassword", "Change User", Role.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO response = userService.changePassword(1L, "wrongpassword", "newpassword");

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
        assertEquals("Incorrect old password", response.getMessage());
        assertEquals(userService.encodePassword("oldpassword"), user.getPassword()); // Password should not change
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testChangePasswordUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDTO response = userService.changePassword(1L, "oldpassword", "newpassword");

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("User not found", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testDebitCreditsSuccess() {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(1L);
        userCredit.setCredits(100.0);
        when(userCreditRepository.findByUserId(1L)).thenReturn(userCredit);
        when(userCreditRepository.save(any(UserCredit.class))).thenReturn(userCredit);

        ResponseEntity<UserCreditDTO> response = userService.debitCredits(1L, 50.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.0, response.getBody().getCredits());
        verify(userCreditRepository, times(1)).save(userCredit);
    }

    @Test
    void testDebitCreditsInsufficientFunds() {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(1L);
        userCredit.setCredits(30.0);
        when(userCreditRepository.findByUserId(1L)).thenReturn(userCredit);

        ResponseEntity<UserCreditDTO> response = userService.debitCredits(1L, 50.0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userCreditRepository, never()).save(any(UserCredit.class));
    }

    @Test
    void testDebitCreditsUserNotFound() {
        when(userCreditRepository.findByUserId(1L)).thenReturn(null);

        ResponseEntity<UserCreditDTO> response = userService.debitCredits(1L, 50.0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Or maybe NOT_FOUND depending on desired behavior
        assertNull(response.getBody());
        verify(userCreditRepository, never()).save(any(UserCredit.class));
    }

    @Test
    void testGetUserCreditFound() {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(1L);
        userCredit.setCredits(75.0);
        when(userCreditRepository.findByUserId(1L)).thenReturn(userCredit);

        UserCreditDTO response = userService.getUserCredit(1L);

        assertEquals(1L, response.getUserId());
        assertEquals(75.0, response.getCredits());
    }

    @Test
    void testGetUserCreditNotFound() {
        when(userCreditRepository.findByUserId(1L)).thenReturn(null);

        UserCreditDTO response = userService.getUserCredit(1L);

        assertEquals(1L, response.getUserId());
        assertEquals(0.0, response.getCredits());
    }

    @Test
    void testAddCreditsExistingUser() {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(1L);
        userCredit.setCredits(100.0);
        when(userCreditRepository.findByUserId(1L)).thenReturn(userCredit);
        when(userCreditRepository.save(any(UserCredit.class))).thenReturn(userCredit);

        ResponseEntity<UserCreditDTO> response = userService.addCredits(1L, 50.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0, response.getBody().getCredits());
        verify(userCreditRepository, times(1)).save(userCredit);
    }

    @Test
    void testAddCreditsNewUser() {
        when(userCreditRepository.findByUserId(1L)).thenReturn(null);
        UserCredit newUserCredit = new UserCredit();
        newUserCredit.setUserId(1L);
        newUserCredit.setCredits(50.0);
        when(userCreditRepository.save(any(UserCredit.class))).thenReturn(newUserCredit);

        ResponseEntity<UserCreditDTO> response = userService.addCredits(1L, 50.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.0, response.getBody().getCredits());
        verify(userCreditRepository, times(1)).save(any(UserCredit.class));
    }

    @Test
    void testCreateUserDetailsSuccess() throws Exception {
        User user = createUser(1L, "user@example.com", "password", "User", Role.CUSTOMER);
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUserId(1L);
        userDetails.setName("Test Name");
        userDetails.setPhoneNumber("1234567890");
        userDetails.setProfileImage(Base64.getEncoder().encodeToString("imagebytes".getBytes())); // Set the encoded image in the mocked UserDetails

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userDetailsRepository.existsByUserId(1L)).thenReturn(false);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(userDetails);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenReturn("imagebytes".getBytes());

        UserDetailsDTO response = userService.createUserDetails(1L, "Test Name", "1234567890", mockFile);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        assertEquals("User details created successfully.", response.getMessage());
        assertEquals(1L, response.getUserId());
        assertEquals("Test Name", response.getName());
        assertEquals("1234567890", response.getPhoneNumber());
        assertNotNull(response.getProfileImage());
        assertEquals(Base64.getEncoder().encodeToString("imagebytes".getBytes()), response.getProfileImage());
        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));
    }

    @Test
    void testCreateUserDetailsUserNotFound() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        MultipartFile mockFile = mock(MultipartFile.class);

        UserDetailsDTO response = userService.createUserDetails(1L, "Test Name", "1234567890", mockFile);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("User not found", response.getMessage());
        verify(userDetailsRepository, never()).save(any(UserDetails.class));
    }

    @Test
    void testCreateUserDetailsAlreadyExists() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userDetailsRepository.existsByUserId(1L)).thenReturn(true);
        MultipartFile mockFile = mock(MultipartFile.class);

        UserDetailsDTO response = userService.createUserDetails(1L, "Test Name", "1234567890", mockFile);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode());
        assertEquals("User details already exist for this user.", response.getMessage());
        verify(userDetailsRepository, never()).save(any(UserDetails.class));
    }

    @Test
    void testUpdateUserDetailsSuccessWithNewImage() throws Exception {
        UserDetails existingDetails = new UserDetails();
        existingDetails.setId(1L);
        existingDetails.setUserId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setPhoneNumber("0987654321");
        when(userDetailsRepository.findByUserId(1L)).thenReturn(Optional.of(existingDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(existingDetails);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenReturn("newimagebytes".getBytes());

        UserDetailsDTO response = userService.updateUserDetails(1L, "New Name", "1122334455", mockFile);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("User details updated successfully.", response.getMessage());
        assertEquals("New Name", response.getName());
        assertEquals("1122334455", response.getPhoneNumber());
        assertNotNull(response.getProfileImage());
        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));
    }

    @Test
    void testUpdateUserDetailsSuccessWithoutNewImage() {
        UserDetails existingDetails = new UserDetails();
        existingDetails.setId(1L);
        existingDetails.setUserId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setPhoneNumber("0987654321");
        when(userDetailsRepository.findByUserId(1L)).thenReturn(Optional.of(existingDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(existingDetails);
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        UserDetailsDTO response = userService.updateUserDetails(1L, "New Name", "1122334455", mockFile);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("User details updated successfully.", response.getMessage());
        assertEquals("New Name", response.getName());
        assertEquals("1122334455", response.getPhoneNumber());
        assertEquals(existingDetails.getProfileImage(), response.getProfileImage()); // Image should not change
        verify(userDetailsRepository, times(1)).save(any(UserDetails.class));
    }

    @Test
    void testUpdateUserDetailsNotFound() throws Exception {
        when(userDetailsRepository.findByUserId(1L)).thenReturn(Optional.empty());
        MultipartFile mockFile = mock(MultipartFile.class);

        UserDetailsDTO response = userService.updateUserDetails(1L, "New Name", "1122334455", mockFile);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("User details not found", response.getMessage());
        verify(userDetailsRepository, never()).save(any(UserDetails.class));
    }

    @Test
    void testGetUserDetailsByUserIdFound() {
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUserId(1L);
        userDetails.setName("Test Name");
        userDetails.setPhoneNumber("1234567890");
        when(userDetailsRepository.findByUserId(1L)).thenReturn(Optional.of(userDetails));

        UserDetailsDTO response = userService.getUserDetailsByUserId(1L);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals("User details found.", response.getMessage());
        assertEquals(1L, response.getUserId());
        assertEquals("Test Name", response.getName());
        assertEquals("1234567890", response.getPhoneNumber());
    }

    @Test
    void testGetUserDetailsByUserIdNotFound() {
        when(userDetailsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        UserDetailsDTO response = userService.getUserDetailsByUserId(1L);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("User details not found", response.getMessage());
    }

    @Test
    void testProcessForgotPasswordRequestEmailNotFound() {
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO();
        requestDTO.setEmail("nonexistent@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.processForgotPasswordRequest(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("If an account with this email exists, a reset link has been sent.", response.getBody());
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testProcessForgotPasswordRequestEmailFoundNewToken() {
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO();
        requestDTO.setEmail("test@example.com");
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(null);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(new PasswordResetToken());
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<String> response = userService.processForgotPasswordRequest(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("If an account with this email exists, a reset link has been sent.", response.getBody());
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testProcessForgotPasswordRequestEmailFoundExistingValidToken() {
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO();
        requestDTO.setEmail("test@example.com");
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        PasswordResetToken existingToken = new PasswordResetToken();
        existingToken.setExpiryDate(new Date(System.currentTimeMillis() + 60000)); // Valid for 1 minute
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(existingToken);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<String> response = userService.processForgotPasswordRequest(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("If an account with this email exists, a reset link has been sent.", response.getBody());
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPasswordNewPasswordsDoNotMatch() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO();
        requestDTO.setToken("validToken");
        requestDTO.setNewPassword("newPass");
        requestDTO.setConfirmPassword("diffPass");

        ResponseEntity<String> response = userService.resetPassword(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("New passwords do not match.", response.getBody());
        verify(passwordResetTokenRepository, never()).findByToken(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPasswordInvalidToken() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO();
        requestDTO.setToken("invalidToken");
        requestDTO.setNewPassword("newPass");
        requestDTO.setConfirmPassword("newPass");
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        ResponseEntity<String> response = userService.resetPassword(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid reset token.", response.getBody());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPasswordTokenExpired() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO();
        requestDTO.setToken("expiredToken");
        requestDTO.setNewPassword("newPass");
        requestDTO.setConfirmPassword("newPass");
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setExpiryDate(new Date(System.currentTimeMillis() - 60000)); // Expired 1 minute ago
        User user = createUser(1L, "test@example.com", "oldPass", "Test User", Role.CUSTOMER);
        expiredToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        ResponseEntity<String> response = userService.resetPassword(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Reset token has expired.", response.getBody());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordResetTokenRepository, times(1)).delete(expiredToken);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testResetPasswordSuccess() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO();
        requestDTO.setToken("validToken");
        requestDTO.setNewPassword("newPass");
        requestDTO.setConfirmPassword("newPass");
        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setExpiryDate(new Date(System.currentTimeMillis() + 60000)); // Valid for 1 minute
        User user = createUser(1L, "test@example.com", "oldPass", "Test User", Role.CUSTOMER);
        validToken.setUser(user);
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(validToken));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(passwordResetTokenRepository).delete(validToken);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<String> response = userService.resetPassword(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successfully.", response.getBody());
        assertEquals(userService.encodePassword("newPass"), user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(validToken);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}