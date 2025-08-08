package com.example.taktik.dto;

import java.time.LocalDateTime;

public class LikeDTO {
    private String id;
    private LocalDateTime createdAt;
    private UserSummaryDTO user; // Only basic user info to avoid circular reference

    // Constructors
    public LikeDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }
}
