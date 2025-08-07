package com.example.taktik.service;

import com.example.taktik.model.Like;
import com.example.taktik.model.User;
import com.example.taktik.model.Video;
import com.example.taktik.repository.LikeRepository;
import com.example.taktik.repository.UserRepository;
import com.example.taktik.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    // Like a video
    public Like likeVideo(String userId, String videoId) {
        // Check if user already liked this video
        if (likeRepository.existsByUser_IdAndVideo_Id(userId, videoId)) {
            throw new RuntimeException("User has already liked this video");
        }

        // Validate user exists
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Validate video exists
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        Like like = new Like();
        like.setId(UUID.randomUUID().toString());
        like.setUser(user.get());
        like.setVideo(video.get());

        return likeRepository.save(like);
    }

    // Unlike a video
    @Transactional
    public void unlikeVideo(String userId, String videoId) {
        if (!likeRepository.existsByUser_IdAndVideo_Id(userId, videoId)) {
            throw new RuntimeException("User has not liked this video");
        }

        likeRepository.deleteByUser_IdAndVideo_Id(userId, videoId);
    }

    // Check if user has liked a video
    public boolean hasUserLikedVideo(String userId, String videoId) {
        return likeRepository.existsByUser_IdAndVideo_Id(userId, videoId);
    }

    // Get like count for a video
    public long getLikeCountByVideoId(String videoId) {
        return likeRepository.countByVideo_Id(videoId);
    }

    // Get likes by video ID
    public List<Like> getLikesByVideoId(String videoId) {
        return likeRepository.findByVideo_Id(videoId);
    }

    // Get users who liked a video
    public List<User> getUsersWhoLikedVideo(String videoId) {
        return likeRepository.findUsersWhoLikedVideo(videoId);
    }

    // Get videos liked by a user
    public List<Video> getVideosLikedByUser(String userId) {
        return likeRepository.findLikedVideosByUser(userId);
    }

    // Get likes by user ID
    public List<Like> getLikesByUserId(String userId) {
        return likeRepository.findByUser_Id(userId);
    }

    // Get like count by user
    public long getLikeCountByUserId(String userId) {
        return likeRepository.countByUser_Id(userId);
    }

    // Toggle like (like if not liked, unlike if already liked)
    @Transactional
    public boolean toggleLike(String userId, String videoId) {
        if (hasUserLikedVideo(userId, videoId)) {
            unlikeVideo(userId, videoId);
            return false; // unliked
        } else {
            likeVideo(userId, videoId);
            return true; // liked
        }
    }

    // Get recent likes by user
    public List<Like> getRecentLikesByUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return likeRepository.findByUserOrderByCreatedAtDesc(user.get());
    }
}
