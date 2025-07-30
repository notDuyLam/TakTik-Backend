package com.example.taktik.service;

import com.example.taktik.model.Chat;
import com.example.taktik.model.User;
import com.example.taktik.repository.ChatRepository;
import com.example.taktik.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all chats for a user
    public List<Chat> getChatsByUser(String userId) {
        return chatRepository.findChatsByUser(userId);
    }

    // Create or get existing chat between two users
    public Chat createOrGetChatBetweenUsers(String user1Id, String user2Id) {
        // Check if users are trying to chat with themselves
        if (user1Id.equals(user2Id)) {
            throw new RuntimeException("User cannot chat with themselves");
        }

        // Check if chat already exists
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(user1Id, user2Id);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        // Validate users exist
        Optional<User> user1 = userRepository.findById(user1Id);
        if (user1.isEmpty()) {
            throw new RuntimeException("User1 not found");
        }

        Optional<User> user2 = userRepository.findById(user2Id);
        if (user2.isEmpty()) {
            throw new RuntimeException("User2 not found");
        }

        // Create new chat
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID().toString());
        chat.setUser1(user1.get());
        chat.setUser2(user2.get());

        return chatRepository.save(chat);
    }

    // Get chat by ID
    public Optional<Chat> getChatById(String id) {
        return chatRepository.findById(id);
    }

    // Get chat between two users
    public Optional<Chat> getChatBetweenUsers(String user1Id, String user2Id) {
        return chatRepository.findChatBetweenUsers(user1Id, user2Id);
    }

    // Delete a chat
    public void deleteChat(String id) {
        if (!chatRepository.existsById(id)) {
            throw new RuntimeException("Chat not found");
        }
        chatRepository.deleteById(id);
    }

    // Get the other user in a chat
    public Optional<User> getOtherUserInChat(String chatId, String userId) {
        return chatRepository.findOtherUserInChat(chatId, userId);
    }

    // Check if chat exists between two users
    public boolean chatExistsBetweenUsers(String user1Id, String user2Id) {
        return chatRepository.existsChatBetweenUsers(user1Id, user2Id);
    }

    // Get recent chats for a user
    public List<Chat> getRecentChatsByUser(String userId) {
        return chatRepository.findRecentChatsByUser(userId);
    }

    // Get chat count for a user
    public long getChatCountByUser(String userId) {
        return chatRepository.countChatsByUser(userId);
    }

    // Update chat timestamp (called when new message is sent)
    public Chat updateChatTimestamp(String id) {
        Optional<Chat> optionalChat = chatRepository.findById(id);
        if (optionalChat.isEmpty()) {
            throw new RuntimeException("Chat not found");
        }

        Chat chat = optionalChat.get();
        // The @PreUpdate annotation will automatically update the updatedAt field
        return chatRepository.save(chat);
    }

    // Check if user is participant in chat
    public boolean isUserParticipantInChat(String chatId, String userId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) {
            return false;
        }

        Chat chatEntity = chat.get();
        return chatEntity.getUser1().getId().equals(userId) ||
               chatEntity.getUser2().getId().equals(userId);
    }
}
