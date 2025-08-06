package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Item extends MasterData {

    private String type;
    private String brand;
    private String model;
    private ArrayList<PhoneInfo> phoneInfo;
    private String description;
}
