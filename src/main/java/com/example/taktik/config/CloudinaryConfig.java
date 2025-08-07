package com.example.taktik.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class CloudinaryConfig {

    @Autowired
    private Environment env;

    @Bean
    public Cloudinary cloudinary() {
        // Try to load from .env file first
        String cloudName = null;
        String apiKey = null;
        String apiSecret = null;

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
            apiKey = dotenv.get("CLOUDINARY_API_KEY");
            apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

            System.out.println("🔧 Loading Cloudinary config from .env file");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load .env file, trying system properties");
        }

        // Fallback to Spring properties if .env didn't work
        if (cloudName == null || cloudName.equals("your_cloud_name")) {
            cloudName = env.getProperty("cloudinary.cloud-name", "your_cloud_name");
            apiKey = env.getProperty("cloudinary.api-key", "your_api_key");
            apiSecret = env.getProperty("cloudinary.api-secret", "your_api_secret");
            System.out.println("🔧 Loading Cloudinary config from application properties");
        }

        System.out.println("🔧 CloudinaryConfig - Creating Cloudinary bean with:");
        System.out.println("   Cloud Name: " + cloudName);
        System.out.println("   API Key: " + apiKey);
        System.out.println("   API Secret: " + (apiSecret != null && !apiSecret.equals("your_api_secret") ? "***SET***" : "NOT_SET_OR_DEFAULT"));

        if (cloudName.equals("your_cloud_name") || apiKey.equals("your_api_key") || apiSecret.equals("your_api_secret")) {
            System.err.println("❌ ERROR: Cloudinary credentials are still using default values!");
            System.err.println("   Please check your .env file or environment variables");
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
