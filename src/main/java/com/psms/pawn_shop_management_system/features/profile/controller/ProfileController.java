package com.psms.pawn_shop_management_system.features.profile.controller;

import com.psms.pawn_shop_management_system.config.cloud.aws.service.S3Service;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ResponseUtils;
import com.psms.pawn_shop_management_system.features.profile.dto.request.PresignedUrlRequest;
import com.psms.pawn_shop_management_system.features.profile.dto.request.ProfileDataRequest;
import com.psms.pawn_shop_management_system.features.profile.dto.response.PresignedUrlResponse;
import com.psms.pawn_shop_management_system.features.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/profile")
@Tag(name  = "Profile API" , description = "End Points for Managing Profile Data")
public class ProfileController {

    private final S3Service s3Service;

    private final ProfileService profileService;

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(@RequestBody PresignedUrlRequest request) {
        try {
            PresignedUrlResponse response = s3Service.generatePresignedUrl(request.getFileName(), request.getFileType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getProfileData")
    public ResponseEntity<ApiResponse> getProfileData(
            @RequestParam("userid") Long userId,
            HttpServletRequest request
    ){
        ApiResponse response = this.profileService.getProfileData(userId);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse> uploadProfile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("userName") String userName,
            HttpServletRequest request) throws IOException {

        ApiResponse response;
        if (profileService.checkImageAlreadyExist(file, userId , userName)) {
            response = ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .data(file.getOriginalFilename())
                    .message("Same profile picture already exists, skipping upload.")
                    .build();
        } else {

            response = s3Service.uploadFile(file , userName);
        }
        return ResponseUtils.buildResponse(request, response);
    }


    @PostMapping("/upload-profile")
    public ResponseEntity<ApiResponse> uploadProfile(@RequestBody ProfileDataRequest profileDataRequest, HttpServletRequest request){
        ApiResponse response = profileService.uploadProfileData(profileDataRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity<String> getFile(@PathVariable String fileName) {
        try {
            String presignedUrl = s3Service.generatePresignedGetUrl(fileName);
            return ResponseEntity.ok(presignedUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to get file: " + e.getMessage());
        }
    }

    @DeleteMapping("/file/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            s3Service.deleteFile(fileName);
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Delete failed: " + e.getMessage());
        }
    }
}
