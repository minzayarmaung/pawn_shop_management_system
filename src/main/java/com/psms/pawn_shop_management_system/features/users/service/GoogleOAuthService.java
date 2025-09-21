package com.psms.pawn_shop_management_system.features.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@Service
public class GoogleOAuthService {
    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleOAuthService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Verify Google ID Token and extract user information
     */
    public GoogleUserInfo verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken token = verifier.verify(idToken);
        if (token != null) {
            GoogleIdToken.Payload payload = token.getPayload();

            return GoogleUserInfo.builder()
                    .email(payload.getEmail())
                    .name((String) payload.get("name"))
                    .givenName((String) payload.get("given_name"))
                    .familyName((String) payload.get("family_name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified(payload.getEmailVerified())
                    .googleId(payload.getSubject())
                    .build();
        }

        throw new IllegalArgumentException("Invalid Google ID token");
    }

    /**
     * Verify Google Access Token by calling Google's tokeninfo endpoint
     */
    public GoogleUserInfo verifyAccessToken(String accessToken) throws IOException {
        // First, verify the token
        String tokenInfoUrl = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> tokenInfoResponse = restTemplate.exchange(
                    tokenInfoUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (tokenInfoResponse.getStatusCode() != HttpStatus.OK) {
                throw new IllegalArgumentException("Invalid Google access token");
            }

            Map<String, Object> tokenInfo = objectMapper.readValue(tokenInfoResponse.getBody(), Map.class);

            // ⚠️ IMPORTANT: For access tokens, we don't always get 'audience' field
            // Instead, we should check 'scope' contains the required scopes
            String scope = (String) tokenInfo.get("scope");
            if (scope == null || (!scope.contains("email") && !scope.contains("profile"))) {
                throw new IllegalArgumentException("Token doesn't have required scopes");
            }

        } catch (Exception e) {
            throw new IOException("Token verification failed: " + e.getMessage());
        }

        // Get user profile information
        return getUserProfile(accessToken);
    }


    /**
     * Get user profile information using access token
     */
    private GoogleUserInfo getUserProfile(String accessToken) throws IOException {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = objectMapper.readValue(response.getBody(), Map.class);

            return GoogleUserInfo.builder()
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("name"))
                    .givenName((String) userInfo.get("given_name"))
                    .familyName((String) userInfo.get("family_name"))
                    .picture((String) userInfo.get("picture"))
                    .emailVerified((Boolean) userInfo.get("verified_email"))
                    .googleId((String) userInfo.get("id"))
                    .build();
        }

        throw new IOException("Failed to get user profile from Google");
    }

    /**
     * Google User Information DTO
     */
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String givenName;
        private String familyName;
        private String picture;
        private Boolean emailVerified;
        private String googleId;

        // Constructors
        public GoogleUserInfo() {}

        private GoogleUserInfo(Builder builder) {
            this.email = builder.email;
            this.name = builder.name;
            this.givenName = builder.givenName;
            this.familyName = builder.familyName;
            this.picture = builder.picture;
            this.emailVerified = builder.emailVerified;
            this.googleId = builder.googleId;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getGivenName() { return givenName; }
        public void setGivenName(String givenName) { this.givenName = givenName; }

        public String getFamilyName() { return familyName; }
        public void setFamilyName(String familyName) { this.familyName = familyName; }

        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }

        public Boolean getEmailVerified() { return emailVerified; }
        public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

        public String getGoogleId() { return googleId; }
        public void setGoogleId(String googleId) { this.googleId = googleId; }

        // Builder pattern
        public static class Builder {
            private String email;
            private String name;
            private String givenName;
            private String familyName;
            private String picture;
            private Boolean emailVerified;
            private String googleId;

            public Builder email(String email) { this.email = email; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder givenName(String givenName) { this.givenName = givenName; return this; }
            public Builder familyName(String familyName) { this.familyName = familyName; return this; }
            public Builder picture(String picture) { this.picture = picture; return this; }
            public Builder emailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; return this; }
            public Builder googleId(String googleId) { this.googleId = googleId; return this; }

            public GoogleUserInfo build() {
                return new GoogleUserInfo(this);
            }
        }
    }
}
