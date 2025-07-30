package com.example.taktik.service;

import com.example.taktik.model.Comment;
import com.example.taktik.model.User;
import com.example.taktik.model.Video;
import com.example.taktik.repository.CommentRepository;
import com.example.taktik.repository.UserRepository;
import com.example.taktik.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    // Get comments by video ID
    public List<Comment> getCommentsByVideoId(String videoId) {
        return commentRepository.findByVideoId(videoId);
    }

    // Get top-level comments by video ID
    public List<Comment> getTopLevelCommentsByVideoId(String videoId) {
        return commentRepository.findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(videoId);
    }

    // Get replies by comment ID
    public List<Comment> getRepliesByCommentId(String commentId) {
        return commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
    }

    // Get comment by ID
    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    // Create new comment
    public Comment createComment(String content, String userId, String videoId, String parentCommentId) {
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

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setContent(content);
        comment.setUser(user.get());
        comment.setVideo(video.get());

        // Set parent comment if this is a reply
        if (parentCommentId != null && !parentCommentId.isEmpty()) {
            Optional<Comment> parentComment = commentRepository.findById(parentCommentId);
            if (parentComment.isEmpty()) {
                throw new RuntimeException("Parent comment not found");
            }
            comment.setParentComment(parentComment.get());
        }

        return commentRepository.save(comment);
    }

    // Update comment
    public Comment updateComment(String id, String content) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }

        Comment comment = optionalComment.get();
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    // Delete comment
    public void deleteComment(String id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(id);
    }

    // Get comments by user ID
    public List<Comment> getCommentsByUserId(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return commentRepository.findByUserOrderByCreatedAtDesc(user.get());
    }

    // Search comments by content
    public List<Comment> searchCommentsByContent(String keyword) {
        return commentRepository.findByContentContaining(keyword);
    }

    // Get comment count by video ID
    public long getCommentCountByVideoId(String videoId) {
        return commentRepository.countByVideoId(videoId);
    }

    // Get reply count by comment ID
    public long getReplyCountByCommentId(String commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        return commentRepository.countByParentComment(comment.get());
    }

    // Check if comment exists
    public boolean commentExists(String id) {
        return commentRepository.existsById(id);
    }

    // Get comments on user's videos
    public List<Comment> getCommentsOnUserVideos(String userId) {
        return commentRepository.findCommentsOnUserVideos(userId);
    }
}
