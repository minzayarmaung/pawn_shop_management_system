package com.psms.pawn_shop_management_system.model;


import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pawn_item")
@Getter
@Setter
public class PawnItem extends MasterData {

    private String category;

    @Column(unique = true)
    private long voucherCode;

    private double amount;

    private LocalDate pawnDate;
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String description;


    @OneToMany(mappedBy = "pawnItem" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<PawnItemDetails> pawnItemDetailsList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}

