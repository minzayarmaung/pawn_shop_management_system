package com.psms.pawn_shop_management_system.features.users.repository;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User , Long> {

    Optional<User> findByEmailAndStatus(String email, Status status);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

}
