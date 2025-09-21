package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserOAuth extends MasterData {

    private String provider;
    private String providerUserId;
    private String profilePicture;
    private boolean emailVerified;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
