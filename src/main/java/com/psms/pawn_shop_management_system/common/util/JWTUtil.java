package com.psms.pawn_shop_management_system.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long TOKEN_VALID_TIME_MILLIS = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token) throws ExpiredJwtException, JwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Try to get email from custom claim first, then from subject
        String email = claims.get("email", String.class);
        if (email == null) {
            email = claims.getSubject();
        }
        return email;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractEmail(token); // will throw if expired
            System.out.println("extract : " + username);
            System.out.println("user.get : " + userDetails.getUsername());
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String generateToken(String email, long expirationMillis) {
        return Jwts.builder()
                .setSubject(email)
                .claim("email", email) // Add email as a custom claim for consistency
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}