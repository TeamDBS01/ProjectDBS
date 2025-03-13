package com.project.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository usersRepository;

    @Autowired
    public UserService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
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
                user.setPassword(registrationRequest.getPassword());
                // Set default role to CUSTOMER
                user.setRole(Role.CUSTOMER);

                User userResult = usersRepository.save(user);

                if (userResult.getUserId() > 0) {
                    resp.setUserId(userResult.getUserId()); // Set userId
                    resp.setName(userResult.getName()); // Set name
                    resp.setEmail(userResult.getEmail()); // Set email
                    resp.setRole(userResult.getRole()); // Set role
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
            User user = usersRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
            if (loginRequest.getPassword().equals(user.getPassword())) { // No encoding check
                response.setStatusCode(200);
                response.setRole(user.getRole());
                response.setMessage("Successfully logged in");
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid credentials");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Generate refresh token
    public UserDTO refreshToken(UserDTO refreshTokenRequest) {
        UserDTO response = new UserDTO();

        try {
            String email = refreshTokenRequest.getEmail();
            User user = usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            response.setStatusCode(200);
            response.setMessage("Successfully refreshed the token");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public UserDTO getAllUsers() {
        UserDTO userDTO = new UserDTO();

        try {
            List<User> result = usersRepository.findAll();

            // Filter out admins and only include customers
            List<User> customers = result.stream()
                    .filter(user -> user.getRole() == Role.CUSTOMER)
                    .collect(Collectors.toList());

            if (!customers.isEmpty()) {
                List<UserDTO> customerDTOs = customers.stream().map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole());
                    return dto;
                }).collect(Collectors.toList());

                userDTO.setOurUsersList(customerDTOs);
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

    public UserDTO getUserByID(Long id) {
        UserDTO userDTO = new UserDTO();

        try {
            User userById = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            userDTO.setUserId(userById.getUserId());
            userDTO.setName(userById.getName());
            userDTO.setEmail(userById.getEmail());
            userDTO.setRole(userById.getRole());
            userDTO.setStatusCode(200);
            userDTO.setMessage("User with id '" + id + "' found successfully");
            System.out.println(userDTO);
        } catch (Exception e) {
            userDTO.setStatusCode(404);
            userDTO.setMessage(e.getMessage());
        }
        return userDTO;
    }

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
                    existingUser.setPassword(updatedUser.getPassword()); // No encoding
                }

                User savedUser = usersRepository.save(existingUser);
                userDTO.setUserId(savedUser.getUserId());
                userDTO.setName(savedUser.getName());
                userDTO.setEmail(savedUser.getEmail());
                userDTO.setRole(savedUser.getRole());
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

//    public UserDTO getMyInfo(String email) {
//        Optional<User> user = usersRepository.findByEmail(email);
//        if (user != null) {
//            return new UserDTO(user);
//        } else {
//            return null; // Or handle the case where the user is not found
//        }
//    }

}