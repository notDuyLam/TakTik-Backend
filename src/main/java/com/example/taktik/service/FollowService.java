package com.example.taktik.service;

import com.example.taktik.model.Follow;
import com.example.taktik.model.User;
import com.example.taktik.repository.FollowRepository;
import com.example.taktik.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    // Follow a user
    public Follow followUser(String followerId, String followingId) {
        // Check if user is trying to follow themselves
        if (followerId.equals(followingId)) {
            throw new RuntimeException("User cannot follow themselves");
        }

        // Check if already following
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("User is already following this user");
        }

        // Validate follower exists
        Optional<User> follower = userRepository.findById(followerId);
        if (follower.isEmpty()) {
            throw new RuntimeException("Follower user not found");
        }

        // Validate following exists
        Optional<User> following = userRepository.findById(followingId);
        if (following.isEmpty()) {
            throw new RuntimeException("Following user not found");
        }

        Follow follow = new Follow();
        follow.setId(UUID.randomUUID().toString());
        follow.setFollower(follower.get());
        follow.setFollowing(following.get());

        return followRepository.save(follow);
    }

    // Unfollow a user
    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("User is not following this user");
        }

        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    // Check if user1 is following user2
    public boolean isFollowing(String followerId, String followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    // Get followers of a user
    public List<User> getFollowers(String userId) {
        return followRepository.findFollowerUsers(userId);
    }

    // Get users that a user is following
    public List<User> getFollowing(String userId) {
        return followRepository.findFollowingUsers(userId);
    }

    // Get follower count
    public long getFollowerCount(String userId) {
        return followRepository.countByFollowingId(userId);
    }

    // Get following count
    public long getFollowingCount(String userId) {
        return followRepository.countByFollowerId(userId);
    }

    // Get mutual follows
    public List<User> getMutualFollows(String userId) {
        return followRepository.findMutualFollows(userId);
    }

    // Get suggested users to follow
    public List<User> getSuggestedUsersToFollow(String userId) {
        return followRepository.findSuggestedUsersToFollow(userId);
    }

    // Toggle follow (follow if not following, unfollow if already following)
    @Transactional
    public boolean toggleFollow(String followerId, String followingId) {
        if (isFollowing(followerId, followingId)) {
            unfollowUser(followerId, followingId);
            return false; // unfollowed
        } else {
            followUser(followerId, followingId);
            return true; // followed
        }
    }

    // Get recent follows by a user
    public List<Follow> getRecentFollowsByUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return followRepository.findByFollowerOrderByCreatedAtDesc(user.get());
    }
}
