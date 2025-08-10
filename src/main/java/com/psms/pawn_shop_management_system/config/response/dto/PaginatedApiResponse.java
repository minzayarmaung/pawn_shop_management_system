package com.psms.pawn_shop_management_system.config.response.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedApiResponse<T>  {
	private int success; 
    private int code; 
    private String message;
    private PaginationMeta meta;
    private List<T> data;
}
