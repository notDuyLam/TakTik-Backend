package com.example.taktik.controller;

import com.example.taktik.model.Follow;
import com.example.taktik.model.User;
import com.example.taktik.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    // Follow a user
    @PostMapping
    public ResponseEntity<Follow> followUser(@RequestBody FollowRequest followRequest) {
        try {
            Follow follow = followService.followUser(followRequest.getFollowerId(), followRequest.getFollowingId());
            return ResponseEntity.ok(follow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unfollow a user
    @DeleteMapping
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowRequest followRequest) {
        try {
            followService.unfollowUser(followRequest.getFollowerId(), followRequest.getFollowingId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if user1 is following user2
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFollowing(@RequestParam String followerId, @RequestParam String followingId) {
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(isFollowing);
    }

    // Get followers of a user
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String userId) {
        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    // Get users that a user is following
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable String userId) {
        List<User> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    // Get follower count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> getFollowerCount(@PathVariable String userId) {
        long count = followService.getFollowerCount(userId);
        return ResponseEntity.ok(count);
    }

    // Get following count
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable String userId) {
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(count);
    }

    // Get mutual follows (users who follow each other)
    @GetMapping("/{userId}/mutual")
    public ResponseEntity<List<User>> getMutualFollows(@PathVariable String userId) {
        List<User> mutualFollows = followService.getMutualFollows(userId);
        return ResponseEntity.ok(mutualFollows);
    }

    // Get suggested users to follow
    @GetMapping("/{userId}/suggestions")
    public ResponseEntity<List<User>> getSuggestedUsers(@PathVariable String userId) {
        List<User> suggestions = followService.getSuggestedUsersToFollow(userId);
        return ResponseEntity.ok(suggestions);
    }

    // Toggle follow (follow if not following, unfollow if already following)
    @PostMapping("/toggle")
    public ResponseEntity<FollowResponse> toggleFollow(@RequestBody FollowRequest followRequest) {
        try {
            boolean isFollowing = followService.toggleFollow(followRequest.getFollowerId(), followRequest.getFollowingId());
            long followerCount = followService.getFollowerCount(followRequest.getFollowingId());
            return ResponseEntity.ok(new FollowResponse(isFollowing, followerCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get recent follows by a user
    @GetMapping("/{userId}/recent")
    public ResponseEntity<List<Follow>> getRecentFollows(@PathVariable String userId) {
        List<Follow> recentFollows = followService.getRecentFollowsByUser(userId);
        return ResponseEntity.ok(recentFollows);
    }

    // DTOs
    public static class FollowRequest {
        private String followerId;
        private String followingId;

        // Getters and setters
        public String getFollowerId() { return followerId; }
        public void setFollowerId(String followerId) { this.followerId = followerId; }
        public String getFollowingId() { return followingId; }
        public void setFollowingId(String followingId) { this.followingId = followingId; }
    }

    public static class FollowResponse {
        private boolean isFollowing;
        private long followerCount;

        public FollowResponse(boolean isFollowing, long followerCount) {
            this.isFollowing = isFollowing;
            this.followerCount = followerCount;
        }

        // Getters and setters
        public boolean isFollowing() { return isFollowing; }
        public void setFollowing(boolean following) { isFollowing = following; }
        public long getFollowerCount() { return followerCount; }
        public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
    }
}
