package com.psms.pawn_shop_management_system.features.users.service.impl;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.users.dto.request.GoogleOAuthRequest;
import com.psms.pawn_shop_management_system.features.users.dto.request.LoginRequest;
import com.psms.pawn_shop_management_system.features.users.dto.request.SignUpRequest;
import com.psms.pawn_shop_management_system.features.users.dto.response.GoogleOAuthResponse;
import com.psms.pawn_shop_management_system.features.users.repository.UserRepository;
import com.psms.pawn_shop_management_system.features.users.service.AuthService;
import com.psms.pawn_shop_management_system.features.users.service.GoogleOAuthService;
import com.psms.pawn_shop_management_system.features.users.service.UserService;
import com.psms.pawn_shop_management_system.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private LocalDateTime currentDateTime;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final ServerUtils serverUtils;

    @Autowired
    private GoogleOAuthService googleOAuthService;
    @Autowired
    private AuthService authService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public ApiResponse loginUser(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmailAndStatus(loginRequest.getEmail(), Status.ACTIVE);

        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("User does not exist.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        User user = optionalUser.get();
         if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
             return ApiResponse.builder()
                     .success(0)
                     .code(401)
                     .message("Incorrect password.")
                     .meta(Map.of("timestamp", System.currentTimeMillis()))
                     .build();
         }

         if(!user.getRole().equalsIgnoreCase(loginRequest.getRole())){
             return ApiResponse.builder()
                     .success(0)
                     .code(401)
                     .message("Invalid Role.")
                     .meta(Map.of("timestamp", System.currentTimeMillis()))
                     .build();
         }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail() , loginRequest.getPassword()));

         String token = serverUtils.generateToken((UserDetails) authentication.getPrincipal());

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Login Successfully.")
                .data(Map.of(
                        "userId",user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "role", user.getRole(),
                        "token", token
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse createNewUser(SignUpRequest signUpRequest) {
        currentDateTime = LocalDateTime.parse(serverUtils.getLocalDateTime(), formatter);
        if(this.userRepository.existsByUsername(signUpRequest.getUsername())){
            return ApiResponse.builder()
                    .success(0)
                    .code(409)
                    .message("Username's Already In Use.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        User user = new User();
        user.setCreatedAt(currentDateTime);
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(signUpRequest.getRole());
        userRepository.save(user);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("User Created Successfully.")
                .data(Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                ))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse oAuthService(GoogleOAuthRequest googleOAuthRequest) {

        // Add validation for token
        if (googleOAuthRequest.getToken() == null || googleOAuthRequest.getToken().trim().isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(400)
                    .message("Token is required")
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        try {
            GoogleOAuthService.GoogleUserInfo googleUserInfo;

            // Try to verify as ID token first, then as access token
            try {
                googleUserInfo = googleOAuthService.verifyIdToken(googleOAuthRequest.getToken());
            } catch (Exception e) {
                // If ID token verification fails, try as access token
                googleUserInfo = googleOAuthService.verifyAccessToken(googleOAuthRequest.getToken());
            }

            // Process the OAuth and get the response with user data and JWT token
            GoogleOAuthResponse googleOAuthResponse = authService.processGoogleOAuth(googleUserInfo);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Google OAuth2 Successfully.")
                    .data(googleOAuthResponse)  // ✅ IMPORTANT: Return the actual data
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();

        } catch (IOException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Invalid Google token: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
    }
}
