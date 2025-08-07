package com.example.taktik.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload video to Cloudinary
     * @param file The video file to upload
     * @return Map containing upload result with public_id, url, secure_url, etc.
     * @throws IOException if upload fails
     */
    public Map<String, Object> uploadVideo(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(),
            ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "taktik/videos",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false,
                "quality", "auto",
                "format", "mp4"
            )
        );
    }

    /**
     * Upload image/thumbnail to Cloudinary
     * @param file The image file to upload
     * @return Map containing upload result
     * @throws IOException if upload fails
     */
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(),
            ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "taktik/thumbnails",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false,
                "quality", "auto"
            )
        );
    }

    /**
     * Delete a file from Cloudinary
     * @param publicId The public ID of the file to delete
     * @param resourceType The type of resource ("video" or "image")
     * @return Map containing deletion result
     * @throws IOException if deletion fails
     */
    public Map<String, Object> deleteFile(String publicId, String resourceType) throws IOException {
        return cloudinary.uploader().destroy(publicId,
            ObjectUtils.asMap("resource_type", resourceType)
        );
    }

    /**
     * Generate a thumbnail URL for a video
     * @param publicId The public ID of the video
     * @return Thumbnail URL
     */
    public String generateVideoThumbnail(String publicId) {
        return cloudinary.url()
            .resourceType("video")
            .transformation(new com.cloudinary.Transformation()
                .crop("thumb")
                .width(300)
                .height(300)
                .gravity("center"))
            .format("jpg")
            .generate(publicId);
    }
}
