package com.psms.pawn_shop_management_system.features.users.service.impl;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.config.exceptions.DuplicateEntityException;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.users.dto.request.LoginRequest;
import com.psms.pawn_shop_management_system.features.users.dto.request.SignUpRequest;
import com.psms.pawn_shop_management_system.features.users.repository.UserRepository;
import com.psms.pawn_shop_management_system.features.users.service.UserService;
import com.psms.pawn_shop_management_system.model.User;
import com.psms.pawn_shop_management_system.model.UserDetail;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
