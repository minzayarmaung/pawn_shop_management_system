package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends MasterData {

    private String username;
    private String password;
    private String role;
}
