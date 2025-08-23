package com.psms.pawn_shop_management_system.features.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
    private String fileName;
    private String fileType;
}
