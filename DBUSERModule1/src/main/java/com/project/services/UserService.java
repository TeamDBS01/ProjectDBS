package com.project.services;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.maxmind.geoip2.DatabaseReader;
import com.project.dto.*;
import com.project.models.*;
import com.project.repositories.PasswordResetTokenRepository;
import com.project.repositories.UserCreditRepository;
import com.project.repositories.UserDetailsRepository;
import com.project.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import io.jsonwebtoken.Claims;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.io.IOException;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

@Service
public class UserService {

    private final UserRepository usersRepository;

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private UserCreditRepository userCreditRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final HttpServletRequest request;
    private final DatabaseReader databaseReader;

    @Value("${forgot.password.reset.link}")
    private String passwordResetLink; // Configure this in your application.properties

    @Value("${forgot.password.token.expiry.minutes}")
    private int tokenExpiryMinutes;

    private static final String USER_DETAILS_NOT_FOUND = "User details not found";
    private static final String INCORRECT_OLD_PASSWORD = "Incorrect old password";
    private static final String PASSWORD_CHANGE_SUCCESS = "Password changed successfully";
    private static final String PASSWORD_ENCODING_FAILED = "Password encoding failed.";


    private static final String USER_NOT_FOUND = "User not found";

//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Keep the same key as Gateway

    private final byte[] secretKeyBytes = "9rxnn8Qd700nlHOxDqsfnEAmwRAuPHzi".getBytes(StandardCharsets.UTF_8);
    private final Key secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

    public UserService(UserRepository usersRepository,UserCreditRepository userCreditRepository , UserDetailsRepository userDetailsRepository,PasswordResetTokenRepository passwordResetTokenRepository,JavaMailSender mailSender,HttpServletRequest request) throws IOException{
        this.usersRepository = usersRepository;
        this.userCreditRepository = userCreditRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.passwordResetTokenRepository=passwordResetTokenRepository;
        this.mailSender=mailSender;
        this.request=request;
        File databaseFile = new File(getClass().getClassLoader().getResource("GeoLite2-City.mmdb").getFile());
        databaseReader = new DatabaseReader.Builder(databaseFile).build();

    }

    public String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] encodedhash = digest.digest(password.getBytes());
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
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
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 )) // 1 hours
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


    public UserDTO changePassword(Long userId, String oldPassword, String newPassword) {
        UserDTO response = new UserDTO();

        try {
            Optional<User> userOptional = usersRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String encodedOldPassword = encodePassword(oldPassword);

                if (encodedOldPassword != null && encodedOldPassword.equals(user.getPassword())) {
                    String encodedNewPassword = encodePassword(newPassword);
                    if (encodedNewPassword == null) {
                        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        response.setMessage(PASSWORD_ENCODING_FAILED);
                        return response;
                    }
                    user.setPassword(encodedNewPassword);
                    usersRepository.save(user);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage(PASSWORD_CHANGE_SUCCESS);
                    sendPasswordChangeConfirmationEmail(user.getEmail());
                } else {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    response.setMessage(INCORRECT_OLD_PASSWORD);
                }
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(USER_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error occurred while changing password: " + e.getMessage());
        }

        return response;
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


    //user-details

    public UserDetailsDTO createUserDetails(Long userId, String name, String phoneNumber, MultipartFile profileImage) {
        UserDetailsDTO response = new UserDetailsDTO();
        try {
            if (!usersRepository.existsById(userId)) {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setMessage(USER_NOT_FOUND);
                return response;
            }
            if (userDetailsRepository.existsByUserId(userId)) {
                response.setStatusCode(HttpStatus.CONFLICT.value());
                response.setMessage("User details already exist for this user.");
                return response;
            }

            String base64Image = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                base64Image = encodeImageToBase64(profileImage);
                if (base64Image == null) {
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setMessage("Error encoding profile image.");
                    return response;
                }
            }

            UserDetails userDetails = new UserDetails();
            userDetails.setUserId(userId);
            userDetails.setName(name);
            userDetails.setPhoneNumber(phoneNumber);
            userDetails.setProfileImage(base64Image);

            UserDetails savedUserDetails = userDetailsRepository.save(userDetails);
            response.setId(savedUserDetails.getId());
            response.setUserId(savedUserDetails.getUserId());
            response.setName(savedUserDetails.getName());
            response.setPhoneNumber(savedUserDetails.getPhoneNumber());
            response.setProfileImage(savedUserDetails.getProfileImage());
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage("User details created successfully.");

        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Error creating user details: " + e.getMessage());
            response.setError(e.getMessage());
        }
        return response;
    }

    public UserDetailsDTO updateUserDetails(Long userId, String name, String phoneNumber, MultipartFile profileImage) {
        UserDetailsDTO response = new UserDetailsDTO();
        Optional<UserDetails> existingUserDetailsOptional = userDetailsRepository.findByUserId(userId);
        if (existingUserDetailsOptional.isPresent()) {
            UserDetails existingUserDetails = existingUserDetailsOptional.get();
            existingUserDetails.setName(name);
            existingUserDetails.setPhoneNumber(phoneNumber);

            if (profileImage != null && !profileImage.isEmpty()) {
                String base64Image = encodeImageToBase64(profileImage);
                if (base64Image == null) {
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    response.setMessage("Error encoding profile image.");
                    return response;
                }
                existingUserDetails.setProfileImage(base64Image);
            }

            UserDetails updatedUserDetails = userDetailsRepository.save(existingUserDetails);
            response.setId(updatedUserDetails.getId());
            response.setUserId(updatedUserDetails.getUserId());
            response.setName(updatedUserDetails.getName());
            response.setPhoneNumber(updatedUserDetails.getPhoneNumber());
            response.setProfileImage(updatedUserDetails.getProfileImage());
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("User details updated successfully.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(USER_DETAILS_NOT_FOUND);
        }
        return response;
    }

    private String encodeImageToBase64(MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            // Handle the exception appropriately (e.g., log it)
            return null;
        }
    }

    public UserDetailsDTO getUserDetailsByUserId(Long userId) {
        UserDetailsDTO response = new UserDetailsDTO();
        Optional<UserDetails> userDetailsOptional = userDetailsRepository.findByUserId(userId);
        if (userDetailsOptional.isPresent()) {
            UserDetails userDetails = userDetailsOptional.get();
            response.setId(userDetails.getId());
            response.setUserId(userDetails.getUserId());
            response.setName(userDetails.getName());
            response.setPhoneNumber(userDetails.getPhoneNumber());
            response.setProfileImage(userDetails.getProfileImage());
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("User details found.");
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(USER_DETAILS_NOT_FOUND);
        }
        return response;
    }


    //forgot password

    public ResponseEntity<String> processForgotPasswordRequest(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        Optional<User> userOptional = usersRepository.findByEmail(forgotPasswordRequestDTO.getEmail());
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("If an account with this email exists, a reset link has been sent.", HttpStatus.OK);
        }

        User user = userOptional.get();

        // Check if an active token already exists for this user
        PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user);
        if (existingToken != null && !isTokenExpired(existingToken)) {
            // Resend the existing token
            sendForgotPasswordEmail(user.getEmail(), existingToken.getToken());
            return new ResponseEntity<>("If an account with this email exists, a reset link has been sent.", HttpStatus.OK);
        }

        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        sendForgotPasswordEmail(user.getEmail(), token);

        return new ResponseEntity<>("If an account with this email exists, a reset link has been sent.", HttpStatus.OK);
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, tokenExpiryMinutes);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, calendar.getTime());
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public void sendForgotPasswordEmail(String userEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Password Reset Request");
        String resetUrl = passwordResetLink + "?token=" + token;
        message.setText("You have requested to reset your password. Please click on the following link to proceed:\n\n" + resetUrl + "\n\nThis link will expire in " + tokenExpiryMinutes + " minutes.\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }

    public ResponseEntity<String> resetPassword(ResetPasswordRequestDTO resetRequestDTO) {
        if (!resetRequestDTO.getNewPassword().equals(resetRequestDTO.getConfirmPassword())) {
            return new ResponseEntity<>("New passwords do not match.", HttpStatus.BAD_REQUEST);
        }

        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(resetRequestDTO.getToken());
        if (tokenOptional.isEmpty()) {
            return new ResponseEntity<>("Invalid reset token.", HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken passwordResetToken = tokenOptional.get();
        if (passwordResetToken.getExpiryDate().before(new Date())) {
            passwordResetTokenRepository.delete(passwordResetToken); // Expired token, remove it
            return new ResponseEntity<>("Reset token has expired.", HttpStatus.BAD_REQUEST);
        }

        User user = passwordResetToken.getUser();
        user.setPassword(encodePassword(resetRequestDTO.getNewPassword()));
        usersRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken); // Invalidate the token after use
        sendPasswordChangeConfirmationEmail(user.getEmail());

        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

    private boolean isTokenExpired(PasswordResetToken token) {
        Calendar cal = Calendar.getInstance();
        return token.getExpiryDate().before(cal.getTime());
    }

    public void sendPasswordChangeConfirmationEmail(String userEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Your Password Has Been Changed");

        String locationInfo = getLocationInfo(); // Get location information

        String emailBody = "Your password has been successfully changed.\n\n";
        if (locationInfo != null && !locationInfo.isEmpty()) {
            emailBody += "This change may have originated from: " + locationInfo + "\n";
        }
        emailBody += "If you did not make this change, please contact us immediately to secure your account.";

        message.setText(emailBody);
        mailSender.send(message);
    }

//    private String getLocationInfo() {
//        try {
//            String ipAddress = getClientIpAddress();
//            if (ipAddress != null && !ipAddress.equals("0:0:0:0:0:0:0:1") && !ipAddress.equals("127.0.0.1")) {
//
//                InetAddress inetAddress = InetAddress.getByName(ipAddress);
//                return "IP Address: " + ipAddress + " (Approximate location based on IP)";
//            } else {
//                return "localhost";
//            }
//        } catch (UnknownHostException e) {
//            return "Could not determine location.";
//        }
//    }
private String getLocationInfo() {
    try {
        String ipAddress = getClientIpAddress();
        if (ipAddress != null && !ipAddress.equals("0:0:0:0:0:0:0:1") && !ipAddress.equals("127.0.0.1")) {
            InetAddress ip = InetAddress.getByName(ipAddress);
            CityResponse response = databaseReader.city(ip);
            if (response != null && response.getCity() != null && response.getCity().getName() != null) {
                return response.getCity().getName();
            } else {
                return "Could not determine specific location.";
            }
        } else {
            return "localhost";
        }
    } catch (Exception e) {
        return "Error determining location.";
    }
}

    private String getClientIpAddress() {
        HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = currentRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = currentRequest.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = currentRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = currentRequest.getRemoteAddr();
        }
        return ip;
    }


}