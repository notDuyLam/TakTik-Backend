package com.example.taktik.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/", "/api", "/hello").permitAll()
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/validate").permitAll()
                .requestMatchers("/api/users/search").permitAll() // Allow searching users without auth
                .requestMatchers("/api/videos/public/**").permitAll() // Public video viewing
                // Protected endpoints - authentication required
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/videos/**").authenticated()
                .requestMatchers("/api/comments/**").authenticated()
                .requestMatchers("/api/likes/**").authenticated()
                .requestMatchers("/api/follows/**").authenticated()
                .requestMatchers("/api/chats/**").authenticated()
                .anyRequest().authenticated()
            )
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
