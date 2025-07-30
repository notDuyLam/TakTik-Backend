package com.example.taktik.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to TikTok Clone API");
        response.put("version", "1.0.0");
        response.put("status", "running");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("users", "/api/users");
        endpoints.put("videos", "/api/videos");
        endpoints.put("comments", "/api/comments");
        endpoints.put("likes", "/api/likes");
        endpoints.put("follows", "/api/follows");
        endpoints.put("chats", "/api/chats");
        endpoints.put("hello", "/hello");

        response.put("endpoints", endpoints);
        return response;
    }

    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("name", "TikTok Clone API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "A comprehensive API for a TikTok-like social media application");

        Map<String, String> features = new HashMap<>();
        features.put("authentication", "User registration and login");
        features.put("videos", "Video upload, viewing, and management");
        features.put("social", "Follow/unfollow users");
        features.put("engagement", "Like and comment on videos");
        features.put("messaging", "Direct messaging between users");
        features.put("feed", "Personalized video feed");
        features.put("search", "Search users and videos");

        apiInfo.put("features", features);
        return apiInfo;
    }
}
