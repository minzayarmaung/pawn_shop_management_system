package com.psms.pawn_shop_management_system.features.profile.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDataRequest {

    private String profilePic;
    private String name;
    private String nrc;
    private String phone;
    private String dob;
    private String gender;
    private long userid;
}
