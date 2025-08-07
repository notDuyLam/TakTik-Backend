package com.example.taktik.controller;

import com.example.taktik.model.User;
import com.example.taktik.service.UserService;
import com.example.taktik.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // User registration
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Validate input
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Username is required"));
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Password must be at least 6 characters long"));
            }

            // Create user object
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());

            if (registerRequest.getBio() != null) {
                user.setBio(registerRequest.getBio());
            }

            if (registerRequest.getAvatarUrl() != null) {
                user.setAvatarUrl(registerRequest.getAvatarUrl());
            }

            // Save user (password will be hashed in service)
            User savedUser = userService.createUser(user);

            // Generate JWT token
            String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername());

            // Return user info and token (without password)
            UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getAvatarUrl(),
                savedUser.getBio(),
                savedUser.getCreatedAt()
            );

            return ResponseEntity.ok(new AuthResponse(token, userResponse, "Registration successful", null));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(null, null, null, "Registration failed"));
        }
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Username is required"));
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Password is required"));
            }

            // Log the login username and password for debugging
            System.out.println("Login attempt for username: " + loginRequest.getUsername());
            System.out.println("Password: " + loginRequest.getPassword());

            // Authenticate user
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

            // Return user info and token (without password)
            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCreatedAt()
            );

            return ResponseEntity.ok(new AuthResponse(token, userResponse, "Login successful", null));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, null, null, "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(null, null, null, "Login failed"));
        }
    }

    // Validate token
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody TokenRequest tokenRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = tokenRequest.getToken();
            boolean isValid = jwtUtil.isTokenValid(token);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                String userId = jwtUtil.extractUserId(token);

                response.put("valid", true);
                response.put("username", username);
                response.put("userId", userId);
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Token validation failed");
            return ResponseEntity.ok(response);
        }
    }

    // Get current user info from token
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCreatedAt()
            );

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // DTOs
    public static class RegisterRequest {
        private String username;
        private String password;
        private String bio;
        private String avatarUrl;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenRequest {
        private String token;

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class AuthResponse {
        private String token;
        private UserResponse user;
        private String message;
        private String error;

        public AuthResponse(String token, UserResponse user, String message, String error) {
            this.token = token;
            this.user = user;
            this.message = message;
            this.error = error;
        }

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserResponse getUser() { return user; }
        public void setUser(UserResponse user) { this.user = user; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    public static class UserResponse {
        private String id;
        private String username;
        private String avatarUrl;
        private String bio;
        private java.time.LocalDateTime createdAt;

        public UserResponse(String id, String username, String avatarUrl, String bio, java.time.LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.avatarUrl = avatarUrl;
            this.bio = bio;
            this.createdAt = createdAt;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
