package com.psms.pawn_shop_management_system.features.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String username;
    private String email;
    private String password;
    private String role;
}
