package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PhoneInfo extends MasterData {

    private String ram;
    private String storage;
    private String phoneCondition;
    private String color;
    private String imei;
}
