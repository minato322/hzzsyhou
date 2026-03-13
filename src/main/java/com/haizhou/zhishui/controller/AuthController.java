package com.haizhou.zhishui.controller;

import com.haizhou.zhishui.model.User;
import com.haizhou.zhishui.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            User user = authService.register(name, email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            String token = authService.login(email, password);
            User user = authService.getUserByEmail(email);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new HashMap<String, Object>() {{
                put("token", token);
                put("user", userInfo);
            }});

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }
}