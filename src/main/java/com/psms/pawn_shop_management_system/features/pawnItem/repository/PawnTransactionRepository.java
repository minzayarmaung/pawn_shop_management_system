package com.psms.pawn_shop_management_system.features.pawnItem.repository;

import com.psms.pawn_shop_management_system.model.PawnTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface PawnTransactionRepository extends JpaRepository<PawnTransaction, Long> {

    Optional<PawnTransaction> findByPawnItemId(Long pawnItemId);

}

