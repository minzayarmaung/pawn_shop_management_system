package com.psms.pawn_shop_management_system.config.response.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ServerUtils {

    public String getLocalDateTime() {
        LocalDateTime date = LocalDateTime.now(ZoneId.of("Asia/Rangoon"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    public String getLocalDate(){
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Rangoon"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }
}
