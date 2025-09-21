package com.psms.pawn_shop_management_system.features.users.repository;

import com.psms.pawn_shop_management_system.model.User;
import com.psms.pawn_shop_management_system.model.UserOAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {
    Optional<UserOAuth> findByProviderAndProviderUserId(String provider, String providerUserId);
    Optional<UserOAuth> findByUser(User user);
}
