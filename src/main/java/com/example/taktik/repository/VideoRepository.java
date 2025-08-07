package com.example.taktik.repository;

import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {

    // Find videos by user
    List<Video> findByUser(User user);

    // Find videos by user ID (using the User relationship)
    List<Video> findByUser_Id(String userId);

    // Find videos ordered by creation date (newest first)
    List<Video> findAllByOrderByCreatedAtDesc();

    // Find videos by title containing keyword (case insensitive)
    List<Video> findByTitleContainingIgnoreCase(String title);

    // Find videos by description containing keyword (case insensitive)
    List<Video> findByDescriptionContainingIgnoreCase(String description);

    // Find top videos by view count
    List<Video> findTop10ByOrderByViewCountDesc();

    // Custom query to find videos with pagination for feed
    @Query("SELECT v FROM Video v ORDER BY v.createdAt DESC")
    List<Video> findVideosForFeed();

    // Find videos by multiple users (for following feed)
    @Query("SELECT v FROM Video v WHERE v.user.id IN :userIds ORDER BY v.createdAt DESC")
    List<Video> findByUserIdInOrderByCreatedAtDesc(@Param("userIds") List<String> userIds);

    // Count videos by user
    long countByUser(User user);

    // Find videos with minimum view count
    List<Video> findByViewCountGreaterThanEqualOrderByViewCountDesc(Long minViews);
}
