package com.example.taktik.service;

import com.example.taktik.controller.UserController.UserStats;
import com.example.taktik.model.User;
import com.example.taktik.repository.UserRepository;
import com.example.taktik.repository.VideoRepository;
import com.example.taktik.repository.FollowRepository;
import com.example.taktik.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private LikeRepository likeRepository;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Create new user
    public User createUser(User user) {
        // Generate ID if not provided
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // In a real application, you would hash the password here
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Update user
    public User updateUser(String id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // Update fields if provided
        if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            // Check if new username is already taken by another user
            Optional<User> existingUser = userRepository.findByUsername(userDetails.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getAvatarUrl() != null) {
            user.setAvatarUrl(userDetails.getAvatarUrl());
        }

        if (userDetails.getBio() != null) {
            user.setBio(userDetails.getBio());
        }

        return userRepository.save(user);
    }

    // Delete user
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Authenticate user (simple version - in production, use proper authentication)
    public User authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        // In a real application, you would compare hashed passwords
        if (!user.get().getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        return user.get();
    }

    // Search users by username
    public List<User> searchUsersByUsername(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // Get user statistics
    public UserStats getUserStats(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User userEntity = user.get();
        long videoCount = videoRepository.countByUser(userEntity);
        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long totalLikes = likeRepository.countByUser(userEntity);

        return new UserStats(videoCount, followerCount, followingCount, totalLikes);
    }

    // Check if user exists
    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }

    // Get users by IDs
    public List<User> getUsersByIds(List<String> ids) {
        return userRepository.findAllById(ids);
    }
}
