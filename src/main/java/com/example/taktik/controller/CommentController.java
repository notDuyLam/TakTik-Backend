package com.example.taktik.controller;

import com.example.taktik.model.Comment;
import com.example.taktik.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Get all comments for a video
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<Comment>> getCommentsByVideo(@PathVariable String videoId) {
        List<Comment> comments = commentService.getCommentsByVideoId(videoId);
        return ResponseEntity.ok(comments);
    }

    // Get top-level comments for a video (no parent comments)
    @GetMapping("/video/{videoId}/top-level")
    public ResponseEntity<List<Comment>> getTopLevelComments(@PathVariable String videoId) {
        List<Comment> comments = commentService.getTopLevelCommentsByVideoId(videoId);
        return ResponseEntity.ok(comments);
    }

    // Get replies to a specific comment
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable String commentId) {
        List<Comment> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Create new comment
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            Comment savedComment = commentService.createComment(
                commentRequest.getContent(),
                commentRequest.getUserId(),
                commentRequest.getVideoId(),
                commentRequest.getParentCommentId()
            );
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody CommentUpdateRequest updateRequest) {
        try {
            Comment updatedComment = commentService.updateComment(id, updateRequest.getContent());
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get comments by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUser(@PathVariable String userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    // Search comments by content
    @GetMapping("/search")
    public ResponseEntity<List<Comment>> searchComments(@RequestParam String query) {
        List<Comment> comments = commentService.searchCommentsByContent(query);
        return ResponseEntity.ok(comments);
    }

    // Get comment count for a video
    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable String videoId) {
        long count = commentService.getCommentCountByVideoId(videoId);
        return ResponseEntity.ok(count);
    }

    // Get reply count for a comment
    @GetMapping("/{commentId}/reply-count")
    public ResponseEntity<Long> getReplyCount(@PathVariable String commentId) {
        long count = commentService.getReplyCountByCommentId(commentId);
        return ResponseEntity.ok(count);
    }

    // DTOs
    public static class CommentRequest {
        private String content;
        private String userId;
        private String videoId;
        private String parentCommentId; // null for top-level comments

        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getVideoId() { return videoId; }
        public void setVideoId(String videoId) { this.videoId = videoId; }
        public String getParentCommentId() { return parentCommentId; }
        public void setParentCommentId(String parentCommentId) { this.parentCommentId = parentCommentId; }
    }

    public static class CommentUpdateRequest {
        private String content;

        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
