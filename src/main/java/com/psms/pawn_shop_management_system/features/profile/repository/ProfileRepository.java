package com.psms.pawn_shop_management_system.features.profile.repository;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.model.Profile;
import com.psms.pawn_shop_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ProfileRepository extends JpaRepository<Profile , Long> {
    Optional<Profile> findByUserId(Long userId);

    List<Profile> findByUserIdAndStatus(Long userId, Status status);

    Optional<Profile> findByUser(User user);

}
