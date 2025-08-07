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

    // Find likes by video ID using relationship navigation
    List<Like> findByVideo_Id(String videoId);

    // Find likes by user
    List<Like> findByUser(User user);

    // Find likes by user ID using relationship navigation
    List<Like> findByUser_Id(String userId);

    // Check if user has liked a specific video
    Optional<Like> findByUserAndVideo(User user, Video video);

    // Check if user has liked a video by IDs using relationship navigation
    Optional<Like> findByUser_IdAndVideo_Id(String userId, String videoId);

    // Count likes for a video
    long countByVideo(Video video);

    // Count likes by video ID using relationship navigation
    long countByVideo_Id(String videoId);

    // Count likes by user
    long countByUser(User user);

    // Count likes by user ID using relationship navigation
    long countByUser_Id(String userId);

    // Delete like by user and video
    void deleteByUserAndVideo(User user, Video video);

    // Delete like by user and video IDs using relationship navigation
    void deleteByUser_IdAndVideo_Id(String userId, String videoId);

    // Check if like exists by user and video
    boolean existsByUserAndVideo(User user, Video video);

    // Check if like exists by user and video IDs using relationship navigation
    boolean existsByUser_IdAndVideo_Id(String userId, String videoId);

    // Find recent likes by user
    List<Like> findByUserOrderByCreatedAtDesc(User user);

    // Find videos liked by user (ordered by like date)
    @Query("SELECT l.video FROM Like l WHERE l.user.id = :userId ORDER BY l.createdAt DESC")
    List<Video> findLikedVideosByUser(@Param("userId") String userId);

    // Find users who liked a video
    @Query("SELECT l.user FROM Like l WHERE l.video.id = :videoId ORDER BY l.createdAt DESC")
    List<User> findUsersWhoLikedVideo(@Param("videoId") String videoId);
}
