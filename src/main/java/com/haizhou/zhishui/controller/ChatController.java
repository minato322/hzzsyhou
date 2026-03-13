package com.haizhou.zhishui.controller;

import com.haizhou.zhishui.ai.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {
    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            if (content == null || content.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Message content is required");
                return ResponseEntity.badRequest().body(response);
            }

            String response = deepSeekService.getChatResponse(content);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", new HashMap<String, Object>() {{
                put("messageContent", response);
            }});

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get AI response: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}