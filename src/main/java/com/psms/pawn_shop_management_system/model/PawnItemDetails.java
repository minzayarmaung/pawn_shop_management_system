package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pawn_item_details")
@Getter
@Setter
public class PawnItemDetails extends MasterData {

    private String fieldName;
    private String fieldValue;

    @Column
    private LocalDateTime checkOutDate;

    @ManyToOne
    @JoinColumn(name = "pawn_item_id")
    private PawnItem pawnItem;

}
