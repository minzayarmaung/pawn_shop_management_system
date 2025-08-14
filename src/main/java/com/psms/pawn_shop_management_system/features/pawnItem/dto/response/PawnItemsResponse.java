package com.psms.pawn_shop_management_system.features.pawnItem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PawnItemsResponse {

    private long id;
    private String customerName;
    private String customerNrc;
    private String customerAddress;
    private String customerPhone;
    private String category;
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pawnDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private String status;
    private String description;
    private Map<String, String> details;
}
