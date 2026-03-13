package com.haizhou.zhishui.service;

import com.haizhou.zhishui.model.User;
import com.haizhou.zhishui.repository.UserRepository;
import com.haizhou.zhishui.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}