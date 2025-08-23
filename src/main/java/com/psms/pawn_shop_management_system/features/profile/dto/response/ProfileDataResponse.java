package com.psms.pawn_shop_management_system.features.profile.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDataResponse {
    private long profileid;
    private String profilePic;
    private String name;
    private String nrc;
    private String phone;
    private String dob;
    private String gender;
    private long userid;
    private String email;

}
