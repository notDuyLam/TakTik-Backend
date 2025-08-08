package com.example.taktik.controller;

import com.example.taktik.dto.VideoDTO;
import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import com.example.taktik.service.VideoService;
import com.example.taktik.service.CloudinaryService;
import com.example.taktik.service.UserService;
import com.example.taktik.service.DTOMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private DTOMapperService dtoMapperService;

    // Get all videos (for feed)
    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        List<VideoDTO> videoDTOs = videos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // Get video by ID
    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable String id) {
        Optional<Video> video = videoService.getVideoById(id);
        if (video.isPresent()) {
            VideoDTO videoDTO = dtoMapperService.convertToVideoDTO(video.get());
            return ResponseEntity.ok(videoDTO);
        }
        return ResponseEntity.notFound().build();
    }

    // Get videos by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VideoDTO>> getVideosByUser(@PathVariable String userId) {
        List<Video> videos = videoService.getVideosByUserId(userId);
        List<VideoDTO> videoDTOs = videos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // Create new video
    @PostMapping
    public ResponseEntity<VideoDTO> createVideo(@RequestBody Video video) {
        try {
            Video savedVideo = videoService.createVideo(video);
            VideoDTO videoDTO = dtoMapperService.convertToVideoDTO(savedVideo);
            return ResponseEntity.ok(videoDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update video
    @PutMapping("/{id}")
    public ResponseEntity<VideoDTO> updateVideo(@PathVariable String id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            VideoDTO videoDTO = dtoMapperService.convertToVideoDTO(updatedVideo);
            return ResponseEntity.ok(videoDTO);
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
    public ResponseEntity<List<VideoDTO>> getFeedForUser(@PathVariable String userId) {
        List<Video> feedVideos = videoService.getFeedForUser(userId);
        List<VideoDTO> videoDTOs = feedVideos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // Get trending videos
    @GetMapping("/trending")
    public ResponseEntity<List<VideoDTO>> getTrendingVideos() {
        List<Video> trendingVideos = videoService.getTrendingVideos();
        List<VideoDTO> videoDTOs = trendingVideos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
    }

    // Search videos
    @GetMapping("/search")
    public ResponseEntity<List<VideoDTO>> searchVideos(@RequestParam String query) {
        List<Video> videos = videoService.searchVideos(query);
        List<VideoDTO> videoDTOs = videos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
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
    public ResponseEntity<List<VideoDTO>> getPopularVideos(@RequestParam(defaultValue = "1000") Long minViews) {
        List<Video> popularVideos = videoService.getVideosWithMinViews(minViews);
        List<VideoDTO> videoDTOs = popularVideos.stream()
                .map(dtoMapperService::convertToVideoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(videoDTOs);
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
            // Add logging for debugging
            System.out.println("=== VIDEO UPLOAD DEBUG ===");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("User ID: " + userId);
            System.out.println("Video file size: " + videoFile.getSize());
            System.out.println("Video file type: " + videoFile.getContentType());

            // Check if user exists first
            Optional<User> userOptional = userService.getUserById(userId);
            if (userOptional.isEmpty()) {
                System.out.println("ERROR: User not found with ID: " + userId);
                throw new RuntimeException("User not found with ID: " + userId);
            }
            System.out.println("User found: " + userOptional.get().getUsername());

            // Upload video to Cloudinary
            System.out.println("Starting video upload to Cloudinary...");
            Map<String, Object> videoUploadResult = cloudinaryService.uploadVideo(videoFile);
            System.out.println("Video upload completed");

            String videoUrl = (String) videoUploadResult.get("secure_url");
            String videoPublicId = (String) videoUploadResult.get("public_id");
            String thumbnailUrl;

            // Upload thumbnail if provided, otherwise generate from video
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                System.out.println("Uploading custom thumbnail...");
                Map<String, Object> thumbnailUploadResult = cloudinaryService.uploadImage(thumbnailFile);
                thumbnailUrl = (String) thumbnailUploadResult.get("secure_url");
            } else {
                System.out.println("Generating thumbnail from video...");
                thumbnailUrl = cloudinaryService.generateVideoThumbnail(videoPublicId);
            }
            System.out.println("Thumbnail ready: " + thumbnailUrl);

            // Create video object and save to database
            System.out.println("Saving video to database...");
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setVideoUrl(videoUrl);
            video.setThumbnailUrl(thumbnailUrl);
            User user = userOptional.get();
            video.setUser(user);
            video.setCloudinaryPublicId(videoPublicId);

            Video savedVideo = videoService.createVideo(video);
            System.out.println("Video saved successfully with ID: " + savedVideo.getId());

            return ResponseEntity.ok(new VideoUploadResponse(
                savedVideo.getId(),
                videoUrl,
                thumbnailUrl,
                "Video uploaded successfully"
            ));
            
        } catch (Exception e) {
            System.out.println("ERROR in video upload: " + e.getMessage());
            e.printStackTrace(); // This will show the full stack trace
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
