package com.psms.pawn_shop_management_system.config.response.dto;

import lombok.Data;

@Data
public class PaginationMeta {
    private long totalItems;
    private int totalPages;
    private int currentPage;
    private String method;
    private String endpoint;
}
