package com.example.taktik.repository;

import com.example.taktik.model.Follow;
import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    // Find all users that a specific user is following
    List<Follow> findByFollower(User follower);

    // Find all users that a specific user is following by ID
    List<Follow> findByFollowerId(String followerId);

    // Find all followers of a specific user
    List<Follow> findByFollowing(User following);

    // Find all followers of a specific user by ID
    List<Follow> findByFollowingId(String followingId);

    // Check if user1 is following user2
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // Check if user1 is following user2 by IDs
    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);

    // Check if user1 is following user2 (returns boolean)
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Check if user1 is following user2 by IDs (returns boolean)
    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

    // Count how many people a user is following
    long countByFollower(User follower);

    // Count how many people a user is following by ID
    long countByFollowerId(String followerId);

    // Count how many followers a user has
    long countByFollowing(User following);

    // Count how many followers a user has by ID
    long countByFollowingId(String followingId);

    // Get list of users that a user is following
    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    List<User> findFollowingUsers(@Param("userId") String userId);

    // Get list of followers of a user
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId ORDER BY f.createdAt DESC")
    List<User> findFollowerUsers(@Param("userId") String userId);

    // Get mutual followers (users who follow each other)
    @Query("SELECT f1.following FROM Follow f1 WHERE f1.follower.id = :userId AND EXISTS (SELECT f2 FROM Follow f2 WHERE f2.follower.id = f1.following.id AND f2.following.id = :userId)")
    List<User> findMutualFollows(@Param("userId") String userId);

    // Delete follow relationship
    void deleteByFollowerAndFollowing(User follower, User following);

    // Delete follow relationship by IDs
    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);

    // Find recent follows by follower
    List<Follow> findByFollowerOrderByCreatedAtDesc(User follower);

    // Find suggested users to follow (users not already followed)
    @Query("SELECT u FROM User u WHERE u.id != :userId AND u.id NOT IN (SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId)")
    List<User> findSuggestedUsersToFollow(@Param("userId") String userId);
}
