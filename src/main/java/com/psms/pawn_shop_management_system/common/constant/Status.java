package com.psms.pawn_shop_management_system.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status implements BaseEnum<Integer>{
    ACTIVE(1),
    INACTIVE(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
