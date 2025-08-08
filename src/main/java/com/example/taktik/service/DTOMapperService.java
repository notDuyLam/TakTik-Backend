package com.example.taktik.service;

import com.example.taktik.dto.*;
import com.example.taktik.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOMapperService {

    public UserDTO convertToUserDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Convert videos to DTOs
        if (user.getVideos() != null) {
            List<VideoDTO> videoDTOs = user.getVideos().stream()
                    .map(this::convertToVideoDTO)
                    .collect(Collectors.toList());
            dto.setVideos(videoDTOs);
        }

        return dto;
    }

    public UserSummaryDTO convertToUserSummaryDTO(User user) {
        if (user == null) return null;

        return new UserSummaryDTO(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public VideoDTO convertToVideoDTO(Video video) {
        if (video == null) return null;

        VideoDTO dto = new VideoDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setVideoUrl(video.getVideoUrl());
        dto.setDescription(video.getDescription());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        dto.setCloudinaryPublicId(video.getCloudinaryPublicId());
        dto.setViewCount(video.getViewCount());
        dto.setCreatedAt(video.getCreatedAt());
        dto.setUpdatedAt(video.getUpdatedAt());

        // Convert comments to DTOs
        if (video.getComments() != null) {
            List<CommentDTO> commentDTOs = video.getComments().stream()
                    .map(this::convertToCommentDTO)
                    .collect(Collectors.toList());
            dto.setComments(commentDTOs);
        }

        // Convert likes to DTOs
        if (video.getLikes() != null) {
            List<LikeDTO> likeDTOs = video.getLikes().stream()
                    .map(this::convertToLikeDTO)
                    .collect(Collectors.toList());
            dto.setLikes(likeDTOs);
        }

        return dto;
    }

    public CommentDTO convertToCommentDTO(Comment comment) {
        if (comment == null) return null;

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        // Convert user to summary DTO to avoid circular reference
        dto.setUser(convertToUserSummaryDTO(comment.getUser()));

        return dto;
    }

    public LikeDTO convertToLikeDTO(Like like) {
        if (like == null) return null;

        LikeDTO dto = new LikeDTO();
        dto.setId(like.getId());
        dto.setCreatedAt(like.getCreatedAt());

        // Convert user to summary DTO to avoid circular reference
        dto.setUser(convertToUserSummaryDTO(like.getUser()));

        return dto;
    }
}
