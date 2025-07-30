package com.example.taktik.repository;

import com.example.taktik.model.Comment;
import com.example.taktik.model.Video;
import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    // Find comments by video
    List<Comment> findByVideo(Video video);

    // Find comments by video ID
    List<Comment> findByVideoId(String videoId);

    // Find comments by user
    List<Comment> findByUser(User user);

    // Find top-level comments (no parent) for a video
    List<Comment> findByVideoAndParentCommentIsNullOrderByCreatedAtDesc(Video video);

    // Find top-level comments by video ID
    List<Comment> findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(String videoId);

    // Find replies to a specific comment
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parentComment);

    // Find replies by parent comment ID
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);

    // Count comments for a video
    long countByVideo(Video video);

    // Count comments by video ID
    long countByVideoId(String videoId);

    // Count replies for a comment
    long countByParentComment(Comment parentComment);

    // Find recent comments by user
    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    // Custom query to find comments with content containing keyword
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Comment> findByContentContaining(@Param("keyword") String keyword);

    // Find all comments for videos by a specific user
    @Query("SELECT c FROM Comment c WHERE c.video.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findCommentsOnUserVideos(@Param("userId") String userId);
}
