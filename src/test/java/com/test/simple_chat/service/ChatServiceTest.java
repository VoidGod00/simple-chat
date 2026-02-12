package com.test.simple_chat.service;

import com.test.simple_chat.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private SetOperations<String, Object> setOps;

    @Mock
    private ListOperations<String, Object> listOps;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // Lenient tells Mockito it's okay if some mocks aren't used in every single test
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOps);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOps);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOps);
    }

    // --- TEST CASE 1: Create and Join Chat Room ---

    @Test
    @DisplayName("Test Case 1: Create Room - Success")
    void testCreateRoom_Success() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("general"))).thenReturn(false);

        // Act
        chatService.createRoom("general");

        // Assert
        verify(hashOps, times(1)).put(eq("chat:rooms"), eq("general"), anyString());
    }

    @Test
    @DisplayName("Test Case 1: Create Room - Duplicate Error")
    void testCreateRoom_Duplicate() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("general"))).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatService.createRoom("general");
        });

        // FIXED: Updated message to match your Service code
        assertEquals("Duplicate chat room name: general", exception.getMessage());
    }

    @Test
    @DisplayName("Test Case 1: Join Room - Success")
    void testJoinRoom_Success() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("general"))).thenReturn(true);

        // Act
        chatService.joinRoom("general", "Sahil");

        // Assert
        verify(setOps, times(1)).add(eq("room:general:users"), eq("Sahil"));
    }

    // --- TEST CASE 2: Send and Retrieve Messages ---

    @Test
    @DisplayName("Test Case 2: Send Message - Stores in List & Publishes")
    void testSendMessage_Success() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("general"))).thenReturn(true);
        ChatMessage msg = new ChatMessage("Sahil", "Hello Redis", null);

        // Act
        chatService.sendMessage("general", msg);

        // Assert
        // FIXED: Changed "msgs" to "history" to match your Service code
        verify(listOps, times(1)).rightPush(eq("room:general:history"), any(ChatMessage.class));
        verify(redisTemplate, times(1)).convertAndSend(eq("chat:channel:general"), any(ChatMessage.class));
    }

    @Test
    @DisplayName("Test Case 2: Retrieve History")
    void testGetHistory() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("general"))).thenReturn(true);

        // FIXED: Changed "msgs" to "history" here too
        when(listOps.range("room:general:history", -10, -1))
                .thenReturn(List.of(new ChatMessage("Sahil", "Hi", "time")));

        // Act
        List<Object> history = chatService.getHistory("general", 10);

        // Assert
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    // --- TEST CASE 4: Error Handling ---

    @Test
    @DisplayName("Test Case 4: Send Message to Non-Existent Room")
    void testSendMessage_InvalidRoom() {
        // Arrange
        when(hashOps.hasKey(anyString(), eq("missing_room"))).thenReturn(false);
        ChatMessage msg = new ChatMessage("Sahil", "Hello", null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendMessage("missing_room", msg);
        });

        // FIXED: Updated message to include the room name
        assertEquals("Room does not exist: missing_room", exception.getMessage());
    }
}