package com.rayen.userManaement.service;

import com.rayen.userManaement.model.AuthResponseDTO;
import com.rayen.userManaement.model.SigninRequestDTO;
import com.rayen.userManaement.model.SignupRequestDTO;
import com.rayen.userManaement.security.JwtUtils;
import com.rayen.userManaement.entity.AssistantUser;
import com.rayen.userManaement.entity.ObserverUser;
import com.rayen.userManaement.entity.Permission;
import com.rayen.userManaement.entity.User;
import com.rayen.userManaement.repository.ObserverUserRepository;
import com.rayen.userManaement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ObserverUserRepository observerUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponseDTO signup(SignupRequestDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user based on type
        User user;
        if ("OBSERVER".equalsIgnoreCase(request.getUserType())) {
            ObserverUser observer = new ObserverUser();
            observer.setUsername(request.getUsername());
            observer.setEmail(request.getEmail());
            observer.setPassword(hashedPassword);
            if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
                observer.setPermissions(convertToPermissionEnums(request.getPermissions()));
            }
            user = observerUserRepository.save(observer);
        } else {
            AssistantUser assistant = new AssistantUser();
            assistant.setUsername(request.getUsername());
            assistant.setEmail(request.getEmail());
            assistant.setPassword(hashedPassword);
            user = userRepository.save(assistant);
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail());

        // Return response
        String userType = user instanceof ObserverUser ? "OBSERVER" : "ASSISTANT";
        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getEmail(), userType);
    }

    public AuthResponseDTO signin(SigninRequestDTO request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail());

        // Return response
        String userType = user instanceof ObserverUser ? "OBSERVER" : "ASSISTANT";
        return new AuthResponseDTO(token, user.getId(), user.getUsername(), user.getEmail(), userType);
    }

    private List<Permission> convertToPermissionEnums(List<String> permissionStrings) {
        return permissionStrings.stream()
                .map(str -> {
                    try {
                        return Permission.valueOf(str.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid permission: " + str + ". Valid permissions are: " +
                                java.util.Arrays.toString(Permission.values()));
                    }
                })
                .collect(Collectors.toList());
    }
}
