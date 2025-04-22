package com.project.controllerTest;

import com.project.controller.UserController;
import com.project.dto.*;
import com.project.models.Role;
import com.project.models.User;
import com.project.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void register_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setName("Test User");
        when(userService.register(any(UserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/dbs/user/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"name\":\"Test User\",\"password\":\"password\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void register_Failure() throws Exception {
        when(userService.register(any(UserDTO.class))).thenReturn(new UserDTO()); // Simulate failure with an empty DTO
        mockMvc.perform(post("/dbs/user/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"name\":\"Test User\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setName("Test User");
        responseDTO.setToken("mockToken");
        when(userService.login(any(UserDTO.class))).thenReturn(responseDTO);
        mockMvc.perform(post("/dbs/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void login_Failure() throws Exception {
        when(userService.login(any(UserDTO.class))).thenReturn(new UserDTO()); // Simulate failure
        mockMvc.perform(post("/dbs/user/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshToken_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setToken("newToken");
        when(userService.refreshToken(any(UserDTO.class))).thenReturn(responseDTO);
        mockMvc.perform(post("/dbs/user/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("newToken"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        User user = createUser(1L, "test@example.com", "password", "Test User", Role.CUSTOMER);
        UserDTO userDTO = convertUserToUserDTO(user); // Create a method to convert User to UserDTO

        UserDTO responseDTO = new UserDTO();
        responseDTO.setOurUsersList(Collections.singletonList(userDTO));
        when(userService.getAllUsers(anyString())).thenReturn(responseDTO);

        mockMvc.perform(get("/dbs/user/admin/get-all-users")
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ourUsersList").isArray())
                .andExpect(jsonPath("$.ourUsersList[0].userId").value(1))
                .andExpect(jsonPath("$.ourUsersList[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.ourUsersList[0].name").value("Test User"))
                .andExpect(jsonPath("$.ourUsersList[0].role").value("CUSTOMER"));
    }

    // Helper method to create User (as you already have)
    private User createUser(Long userId, String email, String password, String name, Role role) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setRole(role);
        return user;
    }

    // Helper method to convert User to UserDTO
    private UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        // You might need to map other relevant fields as well
        return userDTO;
    }
    @Test
    void getUserById_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(1L);
        when(userService.getUserByID(1L)).thenReturn(responseDTO);
        mockMvc.perform(get("/dbs/user/get-user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(1L);
        responseDTO.setName("Updated User");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/dbs/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void deleteUser_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setMessage("User deleted successfully");
        when(userService.deleteUser(1L)).thenReturn(responseDTO);

        mockMvc.perform(delete("/dbs/user/admin/deleteUser/1")
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void getUserProfile_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setEmail("test@example.com");
        when(userService.getUserProfile(anyString())).thenReturn(responseDTO);
        mockMvc.perform(get("/dbs/user/profile")
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void changePassword_Success() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setStatusCode(HttpStatus.OK.value());
        responseDTO.setMessage("Password changed successfully");
        when(userService.changePassword(eq(1L), anyString(), anyString())).thenReturn(responseDTO);

        mockMvc.perform(put("/dbs/user/1/change-password")
                        .param("oldPassword", "old")
                        .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    void changePassword_Unauthorized() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        responseDTO.setMessage("Incorrect old password");
        when(userService.changePassword(eq(1L), anyString(), anyString())).thenReturn(responseDTO);
        mockMvc.perform(put("/dbs/user/1/change-password")
                        .param("oldPassword", "wrong")
                        .param("newPassword", "new"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect old password"));
    }

    @Test
    void changePassword_NotFound() throws Exception {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        responseDTO.setMessage("User not found");
        when(userService.changePassword(eq(1L), anyString(), anyString())).thenReturn(responseDTO);

        mockMvc.perform(put("/dbs/user/1/change-password")
                        .param("oldPassword", "old")
                        .param("newPassword", "new"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void debitCredits_Success() throws Exception {
        UserCreditDTO responseDTO = new UserCreditDTO(1L, 50.0);
        ResponseEntity<UserCreditDTO> responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        when(userService.debitCredits(1L, 20.0)).thenReturn(responseEntity);
        mockMvc.perform(put("/dbs/user/debit-credits/1/20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.credits").value(50.0));
    }

    @Test
    void debitCredits_BadRequest() throws Exception {
        ResponseEntity<UserCreditDTO> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(userService.debitCredits(1L, 20.0)).thenReturn(responseEntity);
        mockMvc.perform(put("/dbs/user/debit-credits/1/20.0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getUserCredit_Success() throws Exception {
        UserCreditDTO responseDTO = new UserCreditDTO(1L, 100.0);
        when(userService.getUserCredit(1L)).thenReturn(responseDTO);
        mockMvc.perform(get("/dbs/user/get-user-credits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.credits").value(100.0));
    }

    @Test
    void addCredits_Success() throws Exception {
        UserCreditDTO responseDTO = new UserCreditDTO(1L, 150.0);
        ResponseEntity<UserCreditDTO> responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        when(userService.addCredits(1L, 50.0)).thenReturn(responseEntity);
        mockMvc.perform(put("/dbs/user/add-credits/1/50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.credits").value(150.0));
    }

    @Test
    @Disabled
    void createUserDetails_Success() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.CREATED.value());
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setName("Test Name");
        responseDTO.setPhoneNumber("1234567890");

        // Mock the service method to return the expected DTO
        when(userService.createUserDetails(eq(1L), anyString(), anyString(), any())).thenReturn(responseDTO);

        MockMultipartFile file = new MockMultipartFile("profileImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "someimage".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/user/1/details")
                        .file(file)
                        .param("name", "Test Name")
                        .param("phoneNumber", "1234567890")
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Explicitly set content type for multipart
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    void createUserDetails_BadRequest() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        when(userService.createUserDetails(eq(1L), anyString(), anyString(), any())).thenReturn(responseDTO);
        MockMultipartFile file = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", "someimage".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/user/1/details")
                        .file(file)
                        .param("name", "Test Name")
                        .param("phoneNumber", "1234567890"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Disabled
    void updateUserDetails_Success() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.OK.value());
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setName("Updated Name");
        responseDTO.setPhoneNumber("0987654321");
        when(userService.updateUserDetails(eq(1L), anyString(), anyString(), any())).thenReturn(responseDTO);
        MockMultipartFile file = new MockMultipartFile("profileImage", "new_image.png", "image/png", "newimage".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/user/1/details")
                        .file(file)
                        .param("name", "Updated Name")
                        .param("phoneNumber", "0987654321")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"));
    }

    @Test
    @Disabled
    void updateUserDetails_NotFound() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        when(userService.updateUserDetails(eq(1L), anyString(), anyString(), any())).thenReturn(responseDTO);
        MockMultipartFile file = new MockMultipartFile("profileImage", "new_image.png", "image/png", "newimage".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/user/1/details")
                        .file(file)
                        .param("name", "Updated Name")
                        .param("phoneNumber", "0987654321")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserDetailsByUserId_Success() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.OK.value());
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setName("Test Name");
        responseDTO.setPhoneNumber("1234567890");
        when(userService.getUserDetailsByUserId(1L)).thenReturn(responseDTO);
        mockMvc.perform(get("/dbs/user/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
    }

    @Test
    void getUserDetailsByUserId_NotFound() throws Exception {
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        when(userService.getUserDetailsByUserId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/dbs/user/1/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    void forgotPassword_Success() throws Exception {
        when(userService.processForgotPasswordRequest(any(ForgotPasswordRequestDTO.class))).thenReturn(new ResponseEntity<>("If an account with this email exists, a reset link has been sent.", HttpStatus.OK));

        mockMvc.perform(post("/dbs/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON) // Set the content type
                        .content("{\"email\":\"test@example.com\"}")) // Set the request body content
                .andExpect(status().isOk())
                .andExpect(content().string("If an account with this email exists, a reset link has been sent."));
    }

    @Test
    void forgotPassword_NotFound() throws Exception {
        when(userService.processForgotPasswordRequest(any(ForgotPasswordRequestDTO.class))).thenReturn(new ResponseEntity<>("No user found with this email.", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/dbs/user/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No user found with this email."));
    }

    @Test
    void resetPassword_Success() throws Exception {
        when(userService.resetPassword(any(ResetPasswordRequestDTO.class))).thenReturn(new ResponseEntity<>("Password reset successfully.", HttpStatus.OK));

        mockMvc.perform(post("/dbs/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"validToken\",\"newPassword\":\"newSecurePassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully."));
    }

    @Test
    void resetPassword_InvalidToken() throws Exception {
        when(userService.resetPassword(any(ResetPasswordRequestDTO.class))).thenReturn(new ResponseEntity<>("Invalid or expired token.", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/dbs/user/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"invalidToken\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired token."));
    }
}