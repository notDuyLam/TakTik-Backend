package com.example.taktik.service;

import com.example.taktik.controller.VideoController.VideoStats;
import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import com.example.taktik.repository.VideoRepository;
import com.example.taktik.repository.UserRepository;
import com.example.taktik.repository.FollowRepository;
import com.example.taktik.repository.LikeRepository;
import com.example.taktik.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Get all videos
    public List<Video> getAllVideos() {
        return videoRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get video by ID
    public Optional<Video> getVideoById(String id) {
        return videoRepository.findById(id);
    }

    // Get videos by user ID
    public List<Video> getVideosByUserId(String userId) {
        return videoRepository.findByUserId(userId);
    }

    // Create new video
    public Video createVideo(Video video) {
        // Generate ID if not provided
        if (video.getId() == null || video.getId().isEmpty()) {
            video.setId(UUID.randomUUID().toString());
        }

        // Validate user exists
        if (video.getUser() == null || video.getUser().getId() == null) {
            throw new RuntimeException("User is required for video creation");
        }

        Optional<User> user = userRepository.findById(video.getUser().getId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        video.setUser(user.get());

        // Initialize view count if not set
        if (video.getViewCount() == null) {
            video.setViewCount(0L);
        }

        return videoRepository.save(video);
    }

    // Update video
    public Video updateVideo(String id, Video videoDetails) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        Video video = optionalVideo.get();

        // Update fields if provided
        if (videoDetails.getTitle() != null) {
            video.setTitle(videoDetails.getTitle());
        }
        if (videoDetails.getDescription() != null) {
            video.setDescription(videoDetails.getDescription());
        }
        if (videoDetails.getVideoUrl() != null) {
            video.setVideoUrl(videoDetails.getVideoUrl());
        }

        return videoRepository.save(video);
    }

    // Delete video
    public void deleteVideo(String id) {
        if (!videoRepository.existsById(id)) {
            throw new RuntimeException("Video not found");
        }
        videoRepository.deleteById(id);
    }

    // Get feed for user (videos from followed users)
    public List<Video> getFeedForUser(String userId) {
        // Get list of users that this user follows
        List<User> followingUsers = followRepository.findFollowingUsers(userId);

        if (followingUsers.isEmpty()) {
            // If user doesn't follow anyone, return trending videos
            return getTrendingVideos();
        }

        List<String> followingUserIds = followingUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        return videoRepository.findByUserIdInOrderByCreatedAtDesc(followingUserIds);
    }

    // Get trending videos
    public List<Video> getTrendingVideos() {
        return videoRepository.findTop10ByOrderByViewCountDesc();
    }

    // Search videos
    public List<Video> searchVideos(String query) {
        List<Video> titleResults = videoRepository.findByTitleContainingIgnoreCase(query);
        List<Video> descriptionResults = videoRepository.findByDescriptionContainingIgnoreCase(query);

        // Combine results and remove duplicates
        titleResults.addAll(descriptionResults);
        return titleResults.stream().distinct().collect(Collectors.toList());
    }

    // Increment view count
    public void incrementViewCount(String id) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        Video video = optionalVideo.get();
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
    }

    // Get video statistics
    public VideoStats getVideoStats(String id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        Video videoEntity = video.get();
        long viewCount = videoEntity.getViewCount();
        long likeCount = likeRepository.countByVideo(videoEntity);
        long commentCount = commentRepository.countByVideo(videoEntity);

        return new VideoStats(viewCount, likeCount, commentCount);
    }

    // Get videos with minimum view count
    public List<Video> getVideosWithMinViews(Long minViews) {
        return videoRepository.findByViewCountGreaterThanEqualOrderByViewCountDesc(minViews);
    }

    // Check if video exists
    public boolean videoExists(String id) {
        return videoRepository.existsById(id);
    }
}
