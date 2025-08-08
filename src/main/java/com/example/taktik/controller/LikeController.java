package com.example.taktik.controller;

import com.example.taktik.dto.LikeDTO;
import com.example.taktik.dto.UserSummaryDTO;
import com.example.taktik.dto.VideoDTO;
import com.example.taktik.model.Like;
import com.example.taktik.model.User;
import com.example.taktik.model.Video;
import com.example.taktik.service.LikeService;
import com.example.taktik.service.DTOMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private DTOMapperService dtoMapperService;

    // Like a video
    @PostMapping
    public ResponseEntity<LikeDTO> likeVideo(@RequestBody LikeRequest likeRequest) {
        try {
            Like like = likeService.likeVideo(likeRequest.getUserId(), likeRequest.getVideoId());
            LikeDTO likeDTO = dtoMapperService.convertToLikeDTO(like);
            return ResponseEntity.ok(likeDTO);
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
    public ResponseEntity<List<LikeDTO>> getLikesByVideo(@PathVariable String videoId) {
        List<Like> likes = likeService.getLikesByVideoId(videoId);
        List<LikeDTO> likeDTOs = likes.stream()
                .map(dtoMapperService::convertToLikeDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(likeDTOs);
    }

    // Get users who liked a video
    @GetMapping("/video/{videoId}/users")
    public ResponseEntity<List<UserSummaryDTO>> getUsersWhoLikedVideo(@PathVariable String videoId) {
        List<User> users = likeService.getUsersWhoLikedVideo(videoId);
        List<UserSummaryDTO> userDTOs = users.stream()
                .map(dtoMapperService::convertToUserSummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Get videos liked by a user
    @GetMapping("/user/{userId}/videos")
    public ResponseEntity<List<VideoDTO>> getVideosLikedByUser(@PathVariable String userId) {
        List<Video> videos = likeService.getVideosLikedByUser(userId);
        List<VideoDTO> videoDTOs = videos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // Get all likes by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LikeDTO>> getLikesByUser(@PathVariable String userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        List<LikeDTO> likeDTOs = likes.stream()
                .map(dtoMapperService::convertToLikeDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(likeDTOs);
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
