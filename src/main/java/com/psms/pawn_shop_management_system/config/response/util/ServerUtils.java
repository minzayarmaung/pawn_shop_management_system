package com.psms.pawn_shop_management_system.config.response.util;

import com.psms.pawn_shop_management_system.features.users.service.impl.UserDetailServiceImpl;
import com.psms.pawn_shop_management_system.model.UserDetail;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ServerUtils {

    @Getter
    @Value("${jwt.secret}")
    private String SecretKey;

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("role", roles);

        // Add email (assuming your UserDetails implementation has getEmail())
        if(userDetails instanceof UserDetail customUser) {
            claims.put("email", customUser.getEmail());
        }

        long access_Token_ExpireTime = 1000 * 60 * 1;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ access_Token_ExpireTime))
                .signWith(Keys.hmacShaKeyFor(SecretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

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
