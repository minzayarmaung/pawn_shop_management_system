package com.psms.pawn_shop_management_system.features.profile.service.impl;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.common.util.SystemUtils;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.profile.dto.request.ProfileDataRequest;
import com.psms.pawn_shop_management_system.features.profile.dto.response.ProfileDataResponse;
import com.psms.pawn_shop_management_system.features.profile.repository.ProfileRepository;
import com.psms.pawn_shop_management_system.features.profile.service.ProfileService;
import com.psms.pawn_shop_management_system.features.users.repository.UserRepository;
import com.psms.pawn_shop_management_system.model.Profile;
import com.psms.pawn_shop_management_system.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ServerUtils serverUtils;
    private LocalDateTime currentDateTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SystemUtils systemUtils;

    @Override
    public ApiResponse uploadProfileData(ProfileDataRequest request) {
        currentDateTime = LocalDateTime.parse(serverUtils.getLocalDateTime(), formatter);
        Optional<User> userExist = userRepository.findById(request.getUserid());

        if (userExist.isPresent()) {
            User user = userExist.get();

            Optional<Profile> isProfileAlreadyExist = profileRepository.findByUser(user);
            Profile profile;
            if(isProfileAlreadyExist.isPresent()){
                profile = isProfileAlreadyExist.get();
                profile.setUpdatedAt(currentDateTime);
            } else {
                profile = new Profile();
                profile.setCreatedAt(currentDateTime);
                profile.setUser(user);
            }
            profile.setProfilePic(request.getProfilePic());
            profile.setName(request.getName());
            profile.setNrc(request.getNrc());
            profile.setPhone(request.getPhone());
            profile.setDob(request.getDob());
            profile.setGender(request.getGender());
            profile.setUser(user);

            profileRepository.save(profile);

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message(isProfileAlreadyExist.isPresent() ? "Profile Updated successfully" : "Profile Uploaded successfully")
                    .data(Map.of(
                            "profileName", profile.getName(),
                            "profilePic", profile.getProfilePic(),
                            "profileNrc", profile.getNrc(),
                            "userId", userExist.get().getId(),
                            "profilePhone", profile.getPhone()
                    ))
                    .meta(Map.of(
                            "timestamp", System.currentTimeMillis()
                    ))
                    .build();
        } else {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Error Uploading Profile")
                    .meta(Map.of(
                            "timestamp", System.currentTimeMillis()
                    ))
                    .build();
        }
    }

    @Override
    public boolean checkImageAlreadyExist(MultipartFile file, Long userId , String userName) {
        String imageName = systemUtils.removeSpace(userName) +"_"+file.getOriginalFilename();
        Optional<Profile> profile = profileRepository.findByUserId(userId);
        if(profile.isPresent()){
            if(profile.get().getProfilePic().equalsIgnoreCase(imageName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ApiResponse getProfileData(Long userId) {
        List<Profile> profile;
        profile = profileRepository.findByUserIdAndStatus(userId , Status.ACTIVE);

        if(profile != null && !profile.isEmpty()){
            List<ProfileDataResponse> responseList = profile.stream()
                    .map(profile1 -> new ProfileDataResponse(
                            profile1.getId(),
                            profile1.getProfilePic(),
                            profile1.getName(),
                            profile1.getNrc(),
                            profile1.getPhone(),
                            profile1.getDob(),
                            profile1.getGender(),
                            profile1.getUser().getId(),
                            profile1.getUser().getUsername(),
                            profile1.getUser().getEmail()
                    ))
                    .toList();

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Profile Data fetched successfully")
                    .data(responseList)
                    .build();
        } else {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("Profile Data Not Found")
                    .build();
        }
    }
}
