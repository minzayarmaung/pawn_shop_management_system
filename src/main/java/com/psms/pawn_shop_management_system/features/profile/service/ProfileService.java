package com.psms.pawn_shop_management_system.features.profile.service;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.features.profile.dto.request.ProfileDataRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ProfileService {
    ApiResponse uploadProfileData(ProfileDataRequest request);

    boolean checkImageAlreadyExist(MultipartFile file, Long userId , String userName);

    ApiResponse getProfileData(Long userId);
}
