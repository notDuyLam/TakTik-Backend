package com.example.taktik.repository;

import com.example.taktik.model.Chat;
import com.example.taktik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, String> {

    // Find chats where user is either user1 or user2
    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByUser(@Param("userId") String userId);

    // Find chat between two specific users
    @Query("SELECT c FROM Chat c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    Optional<Chat> findChatBetweenUsers(@Param("user1Id") String user1Id, @Param("user2Id") String user2Id);

    // Find chat between two users using User objects
    @Query("SELECT c FROM Chat c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Chat> findChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    // Find all chats for a user ordered by last update
    List<Chat> findByUser1OrUser2OrderByUpdatedAtDesc(User user1, User user2);

    // Find chats where user1 is the specified user
    List<Chat> findByUser1(User user1);

    // Find chats where user2 is the specified user
    List<Chat> findByUser2(User user2);

    // Find chats by user1 ID
    List<Chat> findByUser1Id(String user1Id);

    // Find chats by user2 ID
    List<Chat> findByUser2Id(String user2Id);

    // Count total chats for a user
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    long countChatsByUser(@Param("userId") String userId);

    // Check if chat exists between two users
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chat c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    boolean existsChatBetweenUsers(@Param("user1Id") String user1Id, @Param("user2Id") String user2Id);

    // Find recent chats for a user (limit to recent conversations)
    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.updatedAt DESC")
    List<Chat> findRecentChatsByUser(@Param("userId") String userId);

    // Get the other user in a chat (given one user ID and chat ID)
    @Query("SELECT CASE WHEN c.user1.id = :userId THEN c.user2 ELSE c.user1 END FROM Chat c WHERE c.id = :chatId AND (c.user1.id = :userId OR c.user2.id = :userId)")
    Optional<User> findOtherUserInChat(@Param("chatId") String chatId, @Param("userId") String userId);
}
