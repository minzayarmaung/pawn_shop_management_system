package com.psms.pawn_shop_management_system.features.users.service;

import com.psms.pawn_shop_management_system.common.util.JWTUtil;
import com.psms.pawn_shop_management_system.features.users.dto.response.GoogleOAuthResponse;
import com.psms.pawn_shop_management_system.features.users.repository.UserOAuthRepository;
import com.psms.pawn_shop_management_system.features.users.repository.UserRepository;
import com.psms.pawn_shop_management_system.model.User;
import com.psms.pawn_shop_management_system.model.UserOAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOAuthRepository userOAuthRepository;

    @Autowired
    private JWTUtil jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long TOKEN_VALID_TIME_MILLIS = 12 * 60 * 60 * 1000L;

    public GoogleOAuthResponse processGoogleOAuth(GoogleOAuthService.GoogleUserInfo googleUser) {
        try {
            // Step 1: Check if UserOAuth exists
            Optional<UserOAuth> existingOAuth =
                    userOAuthRepository.findByProviderAndProviderUserId("GOOGLE", googleUser.getGoogleId());

            User user;
            boolean isNewUser = false;

            if (existingOAuth.isPresent()) {
                // Existing Google OAuth login
                user = existingOAuth.get().getUser();
                updateUserOAuth(existingOAuth.get(), googleUser);
            } else {
                // Check by email → might be an existing local user
                Optional<User> existingUser = userRepository.findByEmail(googleUser.getEmail());
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                } else {
                    // Create new user
                    user = createUserFromGoogle(googleUser);
                    isNewUser = true;
                }

                // Link Google account
                createUserOAuth(user, googleUser);
            }

            // Generate JWT
            String jwtToken = jwtService.generateToken(user.getEmail(), TOKEN_VALID_TIME_MILLIS);

            // Build response
            GoogleOAuthResponse response = new GoogleOAuthResponse();
            response.setUser(mapUserToResponse(user));
            response.setToken(jwtToken);
            response.setNewUser(isNewUser);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Google OAuth: " + e.getMessage(), e);
        }
    }

    private User createUserFromGoogle(GoogleOAuthService.GoogleUserInfo googleUser) {
        User user = new User();
        user.setEmail(googleUser.getEmail());
        user.setUsername(googleUser.getEmail().split("@")[0]);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // random
        user.setRole("USER");
        return userRepository.save(user);
    }

    private UserOAuth createUserOAuth(User user, GoogleOAuthService.GoogleUserInfo googleUser) {
        UserOAuth userOAuth = new UserOAuth();
        userOAuth.setProvider("GOOGLE");
        userOAuth.setProviderUserId(googleUser.getGoogleId());
        userOAuth.setProfilePicture(googleUser.getPicture());
        userOAuth.setEmailVerified(Boolean.TRUE.equals(googleUser.getEmailVerified()));
        userOAuth.setUser(user);
        return userOAuthRepository.save(userOAuth);
    }

    private void updateUserOAuth(UserOAuth userOAuth, GoogleOAuthService.GoogleUserInfo googleUser) {
        userOAuth.setProfilePicture(googleUser.getPicture());
        if (Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            userOAuth.setEmailVerified(true);
        }
        userOAuthRepository.save(userOAuth);
    }

    private Object mapUserToResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("username", user.getUsername());
        userResponse.put("role", user.getRole());
        return userResponse;
    }
}
