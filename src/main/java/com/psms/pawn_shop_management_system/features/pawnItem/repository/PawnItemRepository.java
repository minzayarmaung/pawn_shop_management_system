package com.psms.pawn_shop_management_system.features.pawnItem.repository;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.model.PawnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//@EnableJpaRepositories
public interface PawnItemRepository extends JpaRepository<PawnItem, Long> {

    @Query("SELECT COALESCE(MAX(p.voucherCode), 0) FROM PawnItem p")
    long findMaxVoucherCode();

    @Query(
            value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM pawn_item p
        JOIN pawn_item_details d ON p.id = d.pawn_item_id
        WHERE d.field_name = :fieldName
          AND d.field_value = :fieldValue
          AND p.status = :status
    """,
            nativeQuery = true
    )
    long existsByDetailAndStatus(String fieldName, String fieldValue, long status);

    List<PawnItem> findByCategoryAndStatus(String category , Status status);

    List<PawnItem> findByStatus(Status status);

    @Query(value = "SELECT * FROM pawn_item WHERE category = :category AND status = :status", nativeQuery = true)
    List<PawnItem> findByCategoryAndStatusNative(@Param("category") String category, @Param("status") int status);


}

