package com.psms.pawn_shop_management_system.model;

import com.psms.pawn_shop_management_system.common.entity.MasterData;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class PawnTransaction extends MasterData {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "pawn_item")
    private PawnItem pawnItem;

    private LocalDate pawnDate;
    private LocalDate dueDate; // pawnDate + 30 days
    private BigDecimal loanAmount; // e.g., 1,000,000 MMK

    @OneToOne
    @JoinColumn(name = "setup")
    private Setup setup;

    private BigDecimal totalRepayment; // loanAmount + serviceFee
    private boolean isRedeemed;
    private LocalDate redeemedDate; // nullable
    private String voucherNumber;
}
