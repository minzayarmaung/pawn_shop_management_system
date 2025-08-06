package com.psms.pawn_shop_management_system.common.converter;

import com.psms.pawn_shop_management_system.common.constant.Status;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter extends BaseEnumConverter<Status, Integer>{

     public StatusConverter() {
        super(Status.class);
    }
}