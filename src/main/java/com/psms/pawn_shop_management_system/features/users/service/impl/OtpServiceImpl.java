package com.psms.pawn_shop_management_system.features.users.service.impl;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.common.util.SystemUtils;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.users.repository.UserRepository;
import com.psms.pawn_shop_management_system.features.users.service.OtpService;
import com.psms.pawn_shop_management_system.model.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private LocalDateTime currentDateTime;
    private final ServerUtils serverUtils;

    private final Firestore firestore;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final SystemUtils systemUtils;

    public ApiResponse sendOtp(String email) throws ExecutionException, InterruptedException, IOException, MessagingException {
        // Check if the email is already registered in the system.
        Optional<User> optionalUser = userRepository.findByEmailAndStatus(email, Status.ACTIVE);
        if (optionalUser.isPresent()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Email Already registered in the system.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        String otpCode = SystemUtils.generateOtp();
        long currentTime = System.currentTimeMillis();

        //Save OTP to Firebase Store
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("otpCode", otpCode);
        otpData.put("timeStamp", currentTime);
        firestore.collection("otpCodes").document(email).set(otpData).get();

        //Send otp via Email
        systemUtils.sendOtpEmail(email, otpCode, "VerifyUserMail");

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Email sent Successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse verifyOtp(String email, String otpCode) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("otpCodes")
                .document(email).get().get();

        if (!snapshot.exists()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Failed Verifying OTP.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        String savedOtp = snapshot.getString("otpCode");
        long timeStamp = snapshot.getLong("timeStamp");

        // Check Expiry
        if (System.currentTimeMillis() - timeStamp > 15 * 60 * 1000) {
            return ApiResponse.builder()
                    .success(0)
                    .code(498)
                    .message("OTP Expired.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        if (savedOtp.equals(otpCode)) {
            firestore.collection("otpCodes").document(email).delete();
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Verify OTP Successfully.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        return null;
    }

    @Override
    public ApiResponse sendResetOtp(String email) throws ExecutionException, InterruptedException, MessagingException, IOException {
        // Check if the email exists in the System
        Optional<User> optionalUser = userRepository.findByEmailAndStatus(email, Status.ACTIVE);
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Email does not exist in the System..")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        String otpCode = SystemUtils.generateOtp();
        long currentTime = System.currentTimeMillis();

        //Save OTP to Firebase Store
        Map<String, Object> otpData = new HashMap<>();
        otpData.put("resetOtpCode", otpCode);
        otpData.put("timeStamp", currentTime);
        firestore.collection("otpCodes").document(email).set(otpData).get();

        //Send otp via Email
        systemUtils.sendOtpEmail(email, otpCode, "ResetPasswordMail");

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Email sent Successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
}
