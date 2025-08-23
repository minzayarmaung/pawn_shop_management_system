package com.psms.pawn_shop_management_system.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemUtils {

    public String removeSpace(String value) {
        if (value == null || value.isEmpty()) {
            return value; // return as is if null or empty
        }
        // Replace one or more spaces with a single underscore
        return value.trim().replaceAll("\\s+", "_");
    }
}
