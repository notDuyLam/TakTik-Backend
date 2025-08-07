package com.example.taktik.controller;

import com.example.taktik.model.Chat;
import com.example.taktik.model.User;
import com.example.taktik.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Get all chats for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Chat>> getChatsByUser(@PathVariable String userId) {
        List<Chat> chats = chatService.getChatsByUser(userId);
        return ResponseEntity.ok(chats);
    }

    // Get or create chat between two users
    @PostMapping("/create")
    public ResponseEntity<Chat> createOrGetChat(@RequestBody ChatRequest chatRequest) {
        try {
            Chat chat = chatService.createOrGetChatBetweenUsers(chatRequest.getUser1Id(), chatRequest.getUser2Id());
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get chat by ID
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable String id) {
        Optional<Chat> chat = chatService.getChatById(id);
        return chat.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Get chat between two specific users
    @GetMapping("/between")
    public ResponseEntity<Chat> getChatBetweenUsers(@RequestParam String user1Id, @RequestParam String user2Id) {
        Optional<Chat> chat = chatService.getChatBetweenUsers(user1Id, user2Id);
        return chat.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Delete a chat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        try {
            chatService.deleteChat(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get the other participant in a chat
    @GetMapping("/{chatId}/other-user/{userId}")
    public ResponseEntity<User> getOtherUserInChat(@PathVariable String chatId, @PathVariable String userId) {
        Optional<User> otherUser = chatService.getOtherUserInChat(chatId, userId);
        return otherUser.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // Check if chat exists between two users
    @GetMapping("/exists")
    public ResponseEntity<Boolean> chatExists(@RequestParam String user1Id, @RequestParam String user2Id) {
        boolean exists = chatService.chatExistsBetweenUsers(user1Id, user2Id);
        return ResponseEntity.ok(exists);
    }

    // Get recent chats for a user (most recently updated)
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<Chat>> getRecentChats(@PathVariable String userId) {
        List<Chat> recentChats = chatService.getRecentChatsByUser(userId);
        return ResponseEntity.ok(recentChats);
    }

    // Get chat count for a user
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getChatCountByUser(@PathVariable String userId) {
        long count = chatService.getChatCountByUser(userId);
        return ResponseEntity.ok(count);
    }

    // Update chat (typically called when a new message is sent to update the updatedAt timestamp)
    @PutMapping("/{id}/update")
    public ResponseEntity<Chat> updateChatTimestamp(@PathVariable String id) {
        try {
            Chat updatedChat = chatService.updateChatTimestamp(id);
            return ResponseEntity.ok(updatedChat);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DTOs
    public static class ChatRequest {
        private String user1Id;
        private String user2Id;

        // Getters and setters
        public String getUser1Id() { return user1Id; }
        public void setUser1Id(String user1Id) { this.user1Id = user1Id; }
        public String getUser2Id() { return user2Id; }
        public void setUser2Id(String user2Id) { this.user2Id = user2Id; }
    }
}
