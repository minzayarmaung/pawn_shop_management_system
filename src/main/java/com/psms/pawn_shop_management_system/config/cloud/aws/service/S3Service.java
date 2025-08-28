package com.psms.pawn_shop_management_system.config.cloud.aws.service;

import com.psms.pawn_shop_management_system.common.util.SystemUtils;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.features.profile.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final SystemUtils systemUtils;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public ApiResponse uploadFile(MultipartFile file , String userName) throws IOException {
        //String fileName = userName +"_"+ UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileName =  systemUtils.removeSpace(userName) +"_"+ file.getOriginalFilename();
        String key = "profile-pics/" + fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(fileName)
                .message("Upload Successfully!")
                .build();
    }

    public String getFileUrl(String fileName) {
        return s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        ).toString();
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        );
    }

    public String generatePresignedGetUrl(String fileName) {
        try {

            String s3Key = fileName;
            if (!fileName.startsWith("profile-pics/")) {
                s3Key = "profile-pics/" + fileName;
            }

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1)) // URL valid for 1 hour
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            return presignedRequest.url().toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage());
        }
    }

    public PresignedUrlResponse generatePresignedUrl(String fileName, String fileType) {
        String s3Key = "profile-pics/" + UUID.randomUUID() + "_" + fileName;

        // Build presigned PUT request with CORS headers
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(fileType)
//                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest)
                .url().toString();

        // Return response
        String publicUrl = s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build()
        ).toString();

        return new PresignedUrlResponse(presignedUrl, publicUrl, s3Key);
    }
}