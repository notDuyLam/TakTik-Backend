package com.example.taktik.controller;

import com.example.taktik.model.Like;
import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import com.example.taktik.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class LikeController {

    @Autowired
    private LikeService likeService;

    // Like a video
    @PostMapping
    public ResponseEntity<Like> likeVideo(@RequestBody LikeRequest likeRequest) {
        try {
            Like like = likeService.likeVideo(likeRequest.getUserId(), likeRequest.getVideoId());
            return ResponseEntity.ok(like);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unlike a video
    @DeleteMapping
    public ResponseEntity<Void> unlikeVideo(@RequestBody LikeRequest likeRequest) {
        try {
            likeService.unlikeVideo(likeRequest.getUserId(), likeRequest.getVideoId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if user has liked a video
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserLikedVideo(@RequestParam String userId, @RequestParam String videoId) {
        boolean hasLiked = likeService.hasUserLikedVideo(userId, videoId);
        return ResponseEntity.ok(hasLiked);
    }

    // Get like count for a video
    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable String videoId) {
        long count = likeService.getLikeCountByVideoId(videoId);
        return ResponseEntity.ok(count);
    }

    // Get all likes for a video
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<Like>> getLikesByVideo(@PathVariable String videoId) {
        List<Like> likes = likeService.getLikesByVideoId(videoId);
        return ResponseEntity.ok(likes);
    }

    // Get users who liked a video
    @GetMapping("/video/{videoId}/users")
    public ResponseEntity<List<User>> getUsersWhoLikedVideo(@PathVariable String videoId) {
        List<User> users = likeService.getUsersWhoLikedVideo(videoId);
        return ResponseEntity.ok(users);
    }

    // Get videos liked by a user
    @GetMapping("/user/{userId}/videos")
    public ResponseEntity<List<Video>> getVideosLikedByUser(@PathVariable String userId) {
        List<Video> videos = likeService.getVideosLikedByUser(userId);
        return ResponseEntity.ok(videos);
    }

    // Get all likes by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUser(@PathVariable String userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }

    // Get like count by user
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getLikeCountByUser(@PathVariable String userId) {
        long count = likeService.getLikeCountByUserId(userId);
        return ResponseEntity.ok(count);
    }

    // Toggle like (like if not liked, unlike if already liked)
    @PostMapping("/toggle")
    public ResponseEntity<LikeResponse> toggleLike(@RequestBody LikeRequest likeRequest) {
        try {
            boolean isLiked = likeService.toggleLike(likeRequest.getUserId(), likeRequest.getVideoId());
            long likeCount = likeService.getLikeCountByVideoId(likeRequest.getVideoId());
            return ResponseEntity.ok(new LikeResponse(isLiked, likeCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class LikeRequest {
        private String userId;
        private String videoId;

        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
    }

    public static class LikeResponse {
        private boolean isLiked;
        private long likeCount;

        public LikeResponse(boolean isLiked, long likeCount) {
            this.isLiked = isLiked;
            this.likeCount = likeCount;
        }

        // Getters and setters
        public boolean isLiked() { return isLiked; }
        public void setLiked(boolean liked) { isLiked = liked; }
        public long getLikeCount() { return likeCount; }
        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    }
}
