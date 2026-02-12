package com.test.simple_chat.service;

import com.test.simple_chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Keys
    private static final String KEY_ROOM_META = "chat:rooms";       // Hash
    private static final String KEY_ROOM_USERS = "room:%s:users";   // Set
    private static final String KEY_ROOM_HIST = "room:%s:history";  // List
    private static final String CHANNEL_PREFIX = "chat:channel:";   // Publish-Subscribe

    // Create Room (Unique Names)
    public void createRoom(String roomName) {
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(KEY_ROOM_META, roomName))) {
            throw new IllegalArgumentException("Duplicate chat room name: " + roomName);
        }
        redisTemplate.opsForHash().put(KEY_ROOM_META, roomName, Instant.now().toString());
    }

    // Join Room (Set)
    public void joinRoom(String roomName, String participant) {
        validateRoom(roomName);
        redisTemplate.opsForSet().add(String.format(KEY_ROOM_USERS, roomName), participant);
    }

    //Messaging (List + Publish-Subscribe)
    public void sendMessage(String roomName, ChatMessage msg) {
        validateRoom(roomName);


        ChatMessage fullMsg = new ChatMessage(
                msg.participant(),
                msg.message(),
                // Add Timestamp
                Instant.now().toString()
        );

        // Store in List (History)
        redisTemplate.opsForList().rightPush(String.format(KEY_ROOM_HIST, roomName), fullMsg);

        // Real-time Publish-Subscribe
        redisTemplate.convertAndSend(CHANNEL_PREFIX + roomName, fullMsg);
    }

    // Retrieve History
    public List<Object> getHistory(String roomName, int limit) {
        validateRoom(roomName);
        return redisTemplate.opsForList().range(String.format(KEY_ROOM_HIST, roomName), -limit, -1);
    }

    // Room validation
    private void validateRoom(String roomName) {
        if (!Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(KEY_ROOM_META, roomName))) {
            throw new IllegalArgumentException("Room does not exist: " + roomName);
        }
    }
}