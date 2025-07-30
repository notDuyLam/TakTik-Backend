package com.example.taktik.repository;

import com.example.taktik.model.Like;
import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {

    // Find likes by video
    List<Like> findByVideo(Video video);

    // Find likes by video ID
    List<Like> findByVideoId(String videoId);

    // Find likes by user
    List<Like> findByUser(User user);

    // Find likes by user ID
    List<Like> findByUserId(String userId);

    // Check if user has liked a specific video
    Optional<Like> findByUserAndVideo(User user, Video video);

    // Check if user has liked a video by IDs
    Optional<Like> findByUserIdAndVideoId(String userId, String videoId);

    // Count likes for a video
    long countByVideo(Video video);

    // Count likes by video ID
    long countByVideoId(String videoId);

    // Count likes by user
    long countByUser(User user);

    // Count likes by user ID
    long countByUserId(String userId);

    // Check if user liked a video (returns boolean)
    boolean existsByUserAndVideo(User user, Video video);

    // Check if user liked a video by IDs (returns boolean)
    boolean existsByUserIdAndVideoId(String userId, String videoId);

    // Find recent likes by user
    List<Like> findByUserOrderByCreatedAtDesc(User user);

    // Find videos liked by user (ordered by like date)
    @Query("SELECT l.video FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    List<Video> findLikedVideosByUser(@Param("userId") String userId);

    // Find users who liked a video
    @Query("SELECT l.user FROM Like l WHERE l.video.id = :videoId ORDER BY l.createdAt DESC")
    List<User> findUsersWhoLikedVideo(@Param("videoId") String videoId);

    // Delete like by user and video
    void deleteByUserAndVideo(User user, Video video);

    // Delete like by user ID and video ID
    void deleteByUserIdAndVideoId(String userId, String videoId);
}
