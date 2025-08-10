package com.psms.pawn_shop_management_system.features.pawnItem.service;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.features.pawnItem.dto.request.PawnItemRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PawnItemService {
    public ApiResponse createPawnItem(PawnItemRequest request);

    ApiResponse getPawnItems(String category, String sortBy);
}
