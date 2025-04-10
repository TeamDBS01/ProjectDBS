package com.project.servicetest;//package com.project.servicetest;
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