package com.example.taktik.repository;

import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    // Search users by username containing keyword (case insensitive)
    List<User> findByUsernameContainingIgnoreCase(String username);
}
