package com.psms.pawn_shop_management_system.features.users.service;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.features.users.dto.request.LoginRequest;
import com.psms.pawn_shop_management_system.features.users.dto.request.SignUpRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    ApiResponse loginUser(LoginRequest loginRequest);

    ApiResponse createNewUser(SignUpRequest signUpRequest);
}
