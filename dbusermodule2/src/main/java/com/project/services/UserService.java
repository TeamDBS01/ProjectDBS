package com.project.services;


import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository usersRepository;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository usersRepository, JWTUtils jwtUtils, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO register(UserDTO registrationRequest) {
        UserDTO resp = new UserDTO();

        try {
            if (usersRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setStatusCode(409);
                resp.setMessage("User with this email already exists.");
            } else {
                User user = new User();
                user.setEmail(registrationRequest.getEmail());
                user.setName(registrationRequest.getName());
                user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

                // Set default role to CUSTOMER
                user.setRole(Role.CUSTOMER);

                User userResult = usersRepository.save(user);

                if (userResult.getUserId() > 0) {
                    resp.setUsers(userResult);
                    resp.setMessage("User saved successfully");
                    resp.setStatusCode(200);
                }
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    // Login implementation
    public UserDTO login(UserDTO loginRequest) {
        UserDTO response = new UserDTO();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            User user = usersRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully logged in");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    
    
//    // New logout method
//    public UserDTO logout(String token) {
//        UserDTO response = new UserDTO();
//
//        try {
//            // Invalidate the token (implement your own logic here)
//            jwtUtils.invalidateToken(token);
//
//            response.setStatusCode(200);
//            response.setMessage("Successfully logged out");
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error occurred while logging out: " + e.getMessage());
//        }
//
//        return response;
//    }
    

    // Generate refresh token
    public UserDTO refreshToken(UserDTO refreshTokenRequest) {
        UserDTO response = new UserDTO();

        try {
            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User user = usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                String jwt = jwtUtils.generateToken(user);

                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hrs");
                response.setMessage("Successfully refreshed the token");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

//    // Get All Users
//    public UserDTO getAllUsers() {
//        UserDTO userDTO = new UserDTO();
//
//        try {
//            List<Users> result = usersRepository.findAll();
//
//            if (!result.isEmpty()) {
//                userDTO.setOurUsersList(result);
//                userDTO.setStatusCode(200);
//                userDTO.setMessage("Successful");
//            } else {
//                userDTO.setStatusCode(404);
//                userDTO.setMessage("No users found");
//            }
//        } catch (Exception e) {
//            userDTO.setStatusCode(500);
//            userDTO.setMessage("Error occurred: " + e.getMessage());
//        }
//        return userDTO;
//    }
    
    public UserDTO getAllUsers() {
        UserDTO userDTO = new UserDTO();

        try {
            List<User> result = usersRepository.findAll();

            // Filter out admins and only include customers
            List<User> customers = result.stream()
                                          .filter(user -> user.getRole() == Role.CUSTOMER)
                                          .collect(Collectors.toList());

            if (!customers.isEmpty()) {
                userDTO.setOurUsersList(customers);
                userDTO.setStatusCode(200);
                userDTO.setMessage("Successful");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("No customers found");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred: " + e.getMessage());
        }
        return userDTO;
    }

    // Get user by ID
    public UserDTO getUserByID(Long id) {
        UserDTO userDTO = new UserDTO();

        try {
            User userById = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            userDTO.setUsers(userById);
            userDTO.setStatusCode(200);
            userDTO.setMessage("User with id '" + id + "' found successfully");
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage(e.getMessage());
        }
        return userDTO;
    }

    // Delete User
    public UserDTO deleteUser(Long userId) {
        UserDTO userDTO = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findById(userId);

            if (userOptional.isPresent()) {
                usersRepository.deleteById(userId);
                userDTO.setStatusCode(200);
                userDTO.setMessage("User deleted successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return userDTO;
    }

    // Update User
    public UserDTO updateUser(Long userId, User updatedUser) {
        UserDTO userDTO = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = usersRepository.save(existingUser);
                userDTO.setUsers(savedUser);
                userDTO.setStatusCode(200);
                userDTO.setMessage("User updated successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for update");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while updating the user: " + e.getMessage());
        }
        return userDTO;
    }

    // Get my info
    public UserDTO getMyInfo(String email) {
        UserDTO userDTO = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                userDTO.setUsers(userOptional.get());
                userDTO.setStatusCode(200);
                userDTO.setMessage("Successful");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while getting your info: " + e.getMessage());
        }
        return userDTO;
    }
    
    
    public UserDTO updateProfile(String email, User updatedUser) {
        UserDTO userDTO = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                User savedUser = usersRepository.save(existingUser);
                userDTO.setUsers(savedUser);
                userDTO.setStatusCode(200);
                userDTO.setMessage("Profile updated successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage("User not found for update");
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred while updating the profile: " + e.getMessage());
        }
        return userDTO;
    }
    
}