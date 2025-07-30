package com.example.taktik.controller;

import com.example.taktik.model.User;
import com.example.taktik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Create new user (registration)
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // User login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Search users by username
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsersByUsername(query);
        return ResponseEntity.ok(users);
    }

    // Get user statistics
    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStats> getUserStats(@PathVariable String id) {
        try {
            UserStats stats = userService.getUserStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTOs
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UserStats {
        private long videoCount;
        private long followerCount;
        private long followingCount;
        private long totalLikes;

        public UserStats(long videoCount, long followerCount, long followingCount, long totalLikes) {
            this.videoCount = videoCount;
            this.followerCount = followerCount;
            this.followingCount = followingCount;
            this.totalLikes = totalLikes;
        }

        // Getters and setters
        public long getVideoCount() { return videoCount; }
        public void setVideoCount(long videoCount) { this.videoCount = videoCount; }
        public long getFollowerCount() { return followerCount; }
        public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
        public long getFollowingCount() { return followingCount; }
        public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }
        public long getTotalLikes() { return totalLikes; }
        public void setTotalLikes(long totalLikes) { this.totalLikes = totalLikes; }
    }
}
