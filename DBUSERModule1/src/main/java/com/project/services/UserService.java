package com.project.services;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import com.project.dto.UserCreditDTO;
import com.project.dto.UserDTO;
import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserCreditRepository;
import com.project.repositories.UserRepository;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.Claims;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import com.project.models.UserCredit;

@Service
public class UserService {

    private final UserRepository usersRepository;

    private UserCreditRepository userCreditRepository;

    private static final String USER_NOT_FOUND = "User not found";

//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Keep the same key as Gateway

    private final byte[] secretKeyBytes = "9rxnn8Qd700nlHOxDqsfnEAmwRAuPHzi".getBytes(StandardCharsets.UTF_8);
    private final Key secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

    public UserService(UserRepository usersRepository,UserCreditRepository userCreditRepository) {
        this.usersRepository = usersRepository;
        this.userCreditRepository = userCreditRepository;
    }

    public String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes());
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // Use email as subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey)
                .compact();
    }

    public UserDTO login(UserDTO loginRequest) {
        UserDTO response = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findByEmail(loginRequest.getEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String encodedLoginPassword = encodePassword(loginRequest.getPassword());

                if (encodedLoginPassword != null && encodedLoginPassword.equals(user.getPassword())) {
                    response.setStatusCode(200);
                    response.setRole(user.getRole());
                    response.setMessage("Successfully logged in");
                    response.setToken(generateJwtToken(user));
                    response.setUserId(user.getUserId());
                    response.setName(user.getName());
                } else {
                    response.setStatusCode(401);
                    response.setMessage("Invalid credentials");
                }
            } else {
                response.setStatusCode(404);
                response.setMessage(USER_NOT_FOUND);
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public UserDTO refreshToken(UserDTO refreshTokenRequest) {
        UserDTO response = new UserDTO();

        try {
            String email = refreshTokenRequest.getEmail();
            usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
            // In a real refresh token scenario, you'd likely have a separate refresh token and logic
            // For simplicity, we're just generating a new access token based on the email.
            Optional<User> userOptional = usersRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                response.setStatusCode(200);
                response.setMessage("Successfully refreshed the token");
                response.setToken(generateJwtToken(userOptional.get()));
            } else {
                response.setStatusCode(404);
                response.setMessage(USER_NOT_FOUND);
            }
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
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

                String encodedPassword = encodePassword(registrationRequest.getPassword());
                if (encodedPassword == null) {
                    resp.setStatusCode(500);
                    resp.setMessage("Password encoding failed.");
                    return resp;
                }
                user.setPassword(encodedPassword);

                user.setRole(Role.CUSTOMER);

                User userResult = usersRepository.save(user);

                if (userResult.getUserId() > 0) {
                    resp.setUserId(userResult.getUserId());
                    resp.setName(userResult.getName());
                    resp.setEmail(userResult.getEmail());
                    resp.setRole(userResult.getRole());
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

    public UserDTO getAllUsers(String authorizationHeader) {
        UserDTO userDTO = new UserDTO();

        try {
            // The Gateway will have already verified the token and the user's role
            // You can optionally extract user info from the headers set by the Gateway if needed
            // For this example, we'll just proceed with fetching users.

            List<User> result = usersRepository.findAll();

            List<User> customers = result.stream()
                    .filter(user -> user.getRole() == Role.CUSTOMER)
                    .toList();

            userDTO.setOurUsersList(new ArrayList<>());

            if (!customers.isEmpty()) {
                List<UserDTO> customerDTOs = customers.stream().map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setRole(user.getRole());
                    return dto;
                }).toList();

                userDTO.getOurUsersList().addAll(customerDTOs);
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
            Optional<User> userOptional = usersRepository.findById(id);

            if (userOptional.isPresent()) {
                User userById = userOptional.get();
                userDTO.setUserId(userById.getUserId());
                userDTO.setName(userById.getName());
                userDTO.setEmail(userById.getEmail());
                userDTO.setRole(userById.getRole());
                userDTO.setStatusCode(200);
                userDTO.setMessage("User with id '" + id + "' found successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage(USER_NOT_FOUND);
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred: " + e.getMessage());
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

                existingUser.setName(updatedUser.getName());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(encodePassword(updatedUser.getPassword()));
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

    public UserDTO getUserProfile(String authorizationHeader) {
        UserDTO userDTO = new UserDTO();

        try {
            // The Gateway will have already verified the token and the user's identity
            // You can extract user identifier (e.g., email or userId) from headers
            // set by the Gateway if needed. For simplicity, we'll assume the Gateway
            // ensures the request is for the authenticated user's profile.

            // For example, the Gateway might set a header "X-Authenticated-User-Email"

            // In this simplified scenario, we'll just extract the email from the token
            // that was passed (Gateway already validated it).
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authorizationHeader.substring(7))
                    .getBody();
            String userEmail = claims.getSubject();

            Optional<User> userOptional = usersRepository.findByEmail(userEmail);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userDTO.setUserId(user.getUserId());
                userDTO.setName(user.getName());
                userDTO.setEmail(user.getEmail());
                userDTO.setRole(user.getRole());
                userDTO.setStatusCode(200);
                userDTO.setMessage("User profile retrieved successfully");
            } else {
                userDTO.setStatusCode(404);
                userDTO.setMessage(USER_NOT_FOUND);
            }
        } catch (Exception e) {
            userDTO.setStatusCode(500);
            userDTO.setMessage("Error occurred: " + e.getMessage());
        }
        return userDTO;
    }

    //service code for userCredit
    public ResponseEntity<UserCreditDTO> debitCredits(Long userId, Double amount) {
        UserCredit userCredit = userCreditRepository.findByUserId(userId);
        if (userCredit == null || userCredit.getCredits() < amount) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userCredit.setCredits(userCredit.getCredits() - amount);
        userCreditRepository.save(userCredit);
        return ResponseEntity.ok(new UserCreditDTO(userId, userCredit.getCredits()));
    }

    public UserCreditDTO getUserCredit(Long userId) {
        UserCredit userCredit = userCreditRepository.findByUserId(userId);
        if(userCredit == null){
            return new UserCreditDTO(userId, 0.0);
        }
        return new UserCreditDTO(userId, userCredit.getCredits());
    }

    public ResponseEntity<UserCreditDTO> addCredits(Long userId, Double amount) {
        UserCredit userCredit = userCreditRepository.findByUserId(userId);
        if(userCredit == null){
            UserCredit newUserCredit = new UserCredit();
            newUserCredit.setUserId(userId);
            newUserCredit.setCredits(amount);
            userCreditRepository.save(newUserCredit);
            return ResponseEntity.ok(new UserCreditDTO(userId, amount));
        }
        userCredit.setCredits(userCredit.getCredits() + amount);
        userCreditRepository.save(userCredit);
        return ResponseEntity.ok(new UserCreditDTO(userId, userCredit.getCredits()));
    }
}