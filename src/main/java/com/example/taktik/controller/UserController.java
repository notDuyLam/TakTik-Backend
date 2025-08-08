package com.example.taktik.controller;

import com.example.taktik.dto.UserDTO;
import com.example.taktik.dto.UserSummaryDTO;
import com.example.taktik.model.User;
import com.example.taktik.service.UserService;
import com.example.taktik.service.DTOMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DTOMapperService dtoMapperService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserSummaryDTO> userDTOs = users.stream()
                .map(dtoMapperService::convertToUserSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            UserDTO userDTO = dtoMapperService.convertToUserDTO(user.get());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.notFound().build();
    }

    // Test endpoint - no database required
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("UserController is working! Security permitAll() is functioning.");
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> user = userService.getUserByUsername(username);
            if (user.isPresent()) {
                UserDTO userDTO = dtoMapperService.convertToUserDTO(user.get());
                return ResponseEntity.ok(userDTO);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Add error logging to see what's happening
            System.out.println("Error finding user by username '" + username + "': " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            UserDTO userDTO = dtoMapperService.convertToUserDTO(updatedUser);
            return ResponseEntity.ok(userDTO);
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

    // Search users by username
    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryDTO>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsersByUsername(query);
        List<UserSummaryDTO> userDTOs = users.stream()
                .map(dtoMapperService::convertToUserSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
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
