package com.psms.pawn_shop_management_system.features.users.service;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public interface OtpService {

    ApiResponse sendOtp(String email) throws ExecutionException, InterruptedException, IOException, MessagingException;

    ApiResponse verifyOtp(String userIdentifier , String otpCode) throws ExecutionException, InterruptedException;

    ApiResponse sendResetOtp(String email) throws ExecutionException, InterruptedException, MessagingException, IOException;
}
