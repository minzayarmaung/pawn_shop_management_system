package com.psms.pawn_shop_management_system.features.pawnItem.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
public class PawnItemRequest {
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerNrc;
    private String customerAddress;
    private String category;
    private double amount;
    private long pawnId;
    private LocalDate pawnDate;
    private LocalDate dueDate;
    private String description;
    private String voucherCode;
    private Map<String, String> details; // brand, model, imei, etc.
}
