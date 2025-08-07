package com.example.taktik.controller;

import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import com.example.taktik.service.VideoService;
import com.example.taktik.service.CloudinaryService;
import com.example.taktik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserService userService;

    // Get all videos (for feed)
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    // Get video by ID
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable String id) {
        Optional<Video> video = videoService.getVideoById(id);
        return video.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // Get videos by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Video>> getVideosByUser(@PathVariable String userId) {
        List<Video> videos = videoService.getVideosByUserId(userId);
        return ResponseEntity.ok(videos);
    }

    // Create new video
    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        try {
            Video savedVideo = videoService.createVideo(video);
            return ResponseEntity.ok(savedVideo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update video
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable String id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            return ResponseEntity.ok(updatedVideo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete video
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable String id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get feed for user (videos from followed users)
    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<Video>> getFeedForUser(@PathVariable String userId) {
        List<Video> feedVideos = videoService.getFeedForUser(userId);
        return ResponseEntity.ok(feedVideos);
    }

    // Get trending videos
    @GetMapping("/trending")
    public ResponseEntity<List<Video>> getTrendingVideos() {
        List<Video> trendingVideos = videoService.getTrendingVideos();
        return ResponseEntity.ok(trendingVideos);
    }

    // Search videos
    @GetMapping("/search")
    public ResponseEntity<List<Video>> searchVideos(@RequestParam String query) {
        List<Video> videos = videoService.searchVideos(query);
        return ResponseEntity.ok(videos);
    }

    // Increment view count
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable String id) {
        try {
            videoService.incrementViewCount(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get video statistics
    @GetMapping("/{id}/stats")
    public ResponseEntity<VideoStats> getVideoStats(@PathVariable String id) {
        try {
            VideoStats stats = videoService.getVideoStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get videos with minimum view count
    @GetMapping("/popular")
    public ResponseEntity<List<Video>> getPopularVideos(@RequestParam(defaultValue = "1000") Long minViews) {
        List<Video> popularVideos = videoService.getVideosWithMinViews(minViews);
        return ResponseEntity.ok(popularVideos);
    }

    // Upload video to Cloudinary
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnailFile,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("userId") String userId) {
        
        try {
            // Upload video to Cloudinary
            Map<String, Object> videoUploadResult = cloudinaryService.uploadVideo(videoFile);
            
            String videoUrl = (String) videoUploadResult.get("secure_url");
            String videoPublicId = (String) videoUploadResult.get("public_id");
            String thumbnailUrl;

            // Upload thumbnail if provided, otherwise generate from video
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                Map<String, Object> thumbnailUploadResult = cloudinaryService.uploadImage(thumbnailFile);
                thumbnailUrl = (String) thumbnailUploadResult.get("secure_url");
            } else {
                // Generate thumbnail from video
                thumbnailUrl = cloudinaryService.generateVideoThumbnail(videoPublicId);
            }

            // Create video object and save to database
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setVideoUrl(videoUrl);
            video.setThumbnailUrl(thumbnailUrl);
            // Set the user object instead of userId
            Optional<User> userOptional = userService.getUserById(userId);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            User user = userOptional.get();
            video.setUser(user);
            video.setCloudinaryPublicId(videoPublicId);

            Video savedVideo = videoService.createVideo(video);
            
            return ResponseEntity.ok(new VideoUploadResponse(
                savedVideo.getId(),
                videoUrl,
                thumbnailUrl,
                "Video uploaded successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Failed to upload video: " + e.getMessage())
            );
        }
    }

    // Upload thumbnail separately
    @PostMapping("/{videoId}/thumbnail")
    public ResponseEntity<?> uploadThumbnail(
            @PathVariable String videoId,
            @RequestParam("thumbnail") MultipartFile thumbnailFile) {
        
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(thumbnailFile);
            String thumbnailUrl = (String) uploadResult.get("secure_url");
            
            videoService.updateVideoThumbnail(videoId, thumbnailUrl);
            
            return ResponseEntity.ok(new ThumbnailUploadResponse(
                thumbnailUrl,
                "Thumbnail uploaded successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Failed to upload thumbnail: " + e.getMessage())
            );
        }
    }

    // DTO for video statistics
    public static class VideoStats {
        private long viewCount;
        private long likeCount;
        private long commentCount;

        public VideoStats(long viewCount, long likeCount, long commentCount) {
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
        }

        // Getters and setters
        public long getViewCount() { return viewCount; }
        public void setViewCount(long viewCount) { this.viewCount = viewCount; }
        public long getLikeCount() { return likeCount; }
        public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
        public long getCommentCount() { return commentCount; }
        public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
    }

    // Response DTOs for upload endpoints
    public static class VideoUploadResponse {
        private String videoId;
        private String videoUrl;
        private String thumbnailUrl;
        private String message;

        public VideoUploadResponse(String videoId, String videoUrl, String thumbnailUrl, String message) {
            this.videoId = videoId;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
            this.message = message;
        }

        // Getters and setters
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ThumbnailUploadResponse {
        private String thumbnailUrl;
        private String message;

        public ThumbnailUploadResponse(String thumbnailUrl, String message) {
            this.thumbnailUrl = thumbnailUrl;
            this.message = message;
        }

        // Getters and setters
        public String getThumbnailUrl() { return thumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        // Getters and setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
