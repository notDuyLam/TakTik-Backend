package com.example.taktik.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // Look for .env in project root
                    .ignoreIfMissing() // Don't fail if .env file doesn't exist
                    .load();

            // Set environment variables from .env file
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                System.out.println("🔧 Loaded env variable: " + entry.getKey() + " = " + entry.getValue());
            });

            // Debug: Check if Cloudinary variables are loaded
            System.out.println("🔍 CLOUDINARY_CLOUD_NAME: " + System.getProperty("CLOUDINARY_CLOUD_NAME"));
            System.out.println("🔍 CLOUDINARY_API_KEY: " + System.getProperty("CLOUDINARY_API_KEY"));
            System.out.println("🔍 CLOUDINARY_API_SECRET: " + (System.getProperty("CLOUDINARY_API_SECRET") != null ? "***LOADED***" : "NOT_LOADED"));

            System.out.println("✅ Successfully loaded .env file");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load .env file: " + e.getMessage());
            e.printStackTrace();
            System.out.println("💡 This is okay if you're using system environment variables instead");
        }
    }
}
