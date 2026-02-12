package com.test.simple_chat.controller;


import com.test.simple_chat.model.ChatMessage;
import com.test.simple_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatapp/chatrooms")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> body) {
        chatService.createRoom(body.get("roomName"));
        return ResponseEntity.ok(Map.of("message", "Room created successfully.", "status", "success"));
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId, @RequestBody Map<String, String> body) {
        chatService.joinRoom(roomId, body.get("participant"));
        return ResponseEntity.ok(Map.of("message", "Joined successfully.", "status", "success"));
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable String roomId, @RequestBody ChatMessage msg) {
        chatService.sendMessage(roomId, msg);
        return ResponseEntity.ok(Map.of("message", "Message sent.", "status", "success"));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getHistory(@PathVariable String roomId, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(Map.of("messages", chatService.getHistory(roomId, limit)));
    }
}