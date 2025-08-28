package com.psms.pawn_shop_management_system.features.users.controller;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ResponseUtils;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.users.dto.request.LoginRequest;
import com.psms.pawn_shop_management_system.features.users.dto.request.SignUpRequest;
import com.psms.pawn_shop_management_system.features.users.service.OtpService;
import com.psms.pawn_shop_management_system.features.users.service.UserService;
import com.psms.pawn_shop_management_system.features.users.service.impl.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/user")
@Tag(name  = "User API" , description = "End Points for Managing User Data")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final ServerUtils serverUtils;
    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
            ){
        final ApiResponse response = this.userService.loginUser(loginRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> createNewUser(
            @RequestBody SignUpRequest signUpRequest,
            HttpServletRequest request
            ){
        final ApiResponse response = this.userService.createNewUser(signUpRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) throws MessagingException, IOException, ExecutionException, InterruptedException {
        String email = body.get("email");
        final ApiResponse response = this.otpService.sendOtp(email);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(
            @RequestBody Map<String , String> body,
            HttpServletRequest request
    ) throws ExecutionException, InterruptedException {
        final String email = body.get("email");
        final String otpCode = body.get("otp");
        final ApiResponse response = this.otpService.verifyOtp(email , otpCode);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody Map<String , String> body,
            HttpServletRequest request
    ) throws MessagingException, IOException, ExecutionException, InterruptedException {
        final String email = body.get("email");
        final ApiResponse response = this.otpService.sendResetOtp(email);
        return ResponseUtils.buildResponse(request , response);
    }

    // To Test Token Data
    @PostMapping("/extractToken")
    public Claims extractToken(@RequestBody Map<String , String> body){
        String token = body.get("token");
        return Jwts.parserBuilder()
                .setSigningKey(serverUtils.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // For Testing
    @PostMapping("/generateNewToken")
    public String generateNewToken(@RequestBody Map<String , String> body){
        String email = body.get("email");
        UserDetails userDetails = userDetailService.loadUserByUsername(email);
        String token =serverUtils.generateToken(userDetails);
        return token;
    }
}
