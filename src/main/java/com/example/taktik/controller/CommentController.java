package com.example.taktik.controller;

import com.example.taktik.dto.CommentDTO;
import com.example.taktik.model.Comment;
import com.example.taktik.service.CommentService;
import com.example.taktik.service.DTOMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DTOMapperService dtoMapperService;

    // Get all comments for a video
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByVideo(@PathVariable String videoId) {
        List<Comment> comments = commentService.getCommentsByVideoId(videoId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(dtoMapperService::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    // Get top-level comments for a video (no parent comments)
    @GetMapping("/video/{videoId}/top-level")
    public ResponseEntity<List<CommentDTO>> getTopLevelComments(@PathVariable String videoId) {
        List<Comment> comments = commentService.getTopLevelCommentsByVideoId(videoId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(dtoMapperService::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    // Get replies to a specific comment
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDTO>> getReplies(@PathVariable String commentId) {
        List<Comment> replies = commentService.getRepliesByCommentId(commentId);
        List<CommentDTO> commentDTOs = replies.stream()
                .map(dtoMapperService::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        if (comment.isPresent()) {
            CommentDTO commentDTO = dtoMapperService.convertToCommentDTO(comment.get());
            return ResponseEntity.ok(commentDTO);
        }
        return ResponseEntity.notFound().build();
    }

    // Create new comment
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            Comment savedComment = commentService.createComment(
                commentRequest.getContent(),
                commentRequest.getUserId(),
                commentRequest.getVideoId(),
                commentRequest.getParentCommentId()
            );
            CommentDTO commentDTO = dtoMapperService.convertToCommentDTO(savedComment);
            return ResponseEntity.ok(commentDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable String id, @RequestBody CommentUpdateRequest updateRequest) {
        try {
            Comment updatedComment = commentService.updateComment(id, updateRequest.getContent());
            CommentDTO commentDTO = dtoMapperService.convertToCommentDTO(updatedComment);
            return ResponseEntity.ok(commentDTO);
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
    public ResponseEntity<List<CommentDTO>> getCommentsByUser(@PathVariable String userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(dtoMapperService::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    // Search comments by content
    @GetMapping("/search")
    public ResponseEntity<List<CommentDTO>> searchComments(@RequestParam String query) {
        List<Comment> comments = commentService.searchCommentsByContent(query);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(dtoMapperService::convertToCommentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
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
