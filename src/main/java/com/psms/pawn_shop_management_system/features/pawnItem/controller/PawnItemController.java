package com.psms.pawn_shop_management_system.features.pawnItem.controller;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ResponseUtils;
import com.psms.pawn_shop_management_system.features.pawnItem.dto.request.PawnItemRequest;
import com.psms.pawn_shop_management_system.features.pawnItem.service.PawnItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/pawn-item")
@Tag(name  = "Pawn Item API" , description = "End Points for Managing Pawn Items")
public class PawnItemController {

    private final PawnItemService pawnItemService;

    @PostMapping
    @Operation(
            summary = "Create a new Pawn Item",
            description = "Create a new Item.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pawn Item creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PawnItemRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn Item created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> createPawnItem(
            @RequestBody PawnItemRequest request,
            HttpServletRequest servletRequest) {

        ApiResponse response = pawnItemService.createPawnItem(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @RequestMapping("/all-pawn-items")
    @GetMapping
    @Operation(
            summary = "Create a new Pawn Item",
            description = "Create a new Item.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pawn Item creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PawnItemRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn Item created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> allPawnItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy , HttpServletRequest servletRequest
    ){
        ApiResponse response = pawnItemService.getPawnItems(category , sortBy);
        return ResponseUtils.buildResponse(servletRequest , response);
    }

}
