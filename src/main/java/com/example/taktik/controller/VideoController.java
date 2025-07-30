package com.example.taktik.controller;

import com.example.taktik.model.Video;
import com.example.taktik.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoService videoService;

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
}
