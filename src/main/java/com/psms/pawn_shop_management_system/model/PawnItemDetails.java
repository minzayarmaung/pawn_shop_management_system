package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pawn_item_details")
@Getter
@Setter
public class PawnItemDetails extends MasterData {

    private String fieldName;
    private String fieldValue;

    @ManyToOne
    @JoinColumn(name = "pawn_item_id")
    private PawnItem pawnItem;
}
