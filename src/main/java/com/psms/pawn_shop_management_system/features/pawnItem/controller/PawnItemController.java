package com.psms.pawn_shop_management_system.features.pawnItem.controller;

import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.dto.PaginatedApiResponse;
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

import java.util.Map;

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

    @PostMapping("/check-Out-Item")
    @Operation(
            summary = "Check Out Pawn Item",
            description = "Check Out Pawn Item.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pawn Item Check Out Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PawnItemRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn Item Checked Out Successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> checkOutPawnItem(
            @RequestBody PawnItemRequest pawnItemRequest,
            HttpServletRequest request
    ){
        ApiResponse response = pawnItemService.checkOutPawnItem(pawnItemRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @GetMapping("/all-pawn-items")
    @Operation(
            summary = "Get All Pawn Items",
            description = "Get All Pawn Items",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Get All Pawn Items",
                    required = true
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn items fetched successfully"),
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

    @PostMapping("/delete-pawn-item")
    @Operation(
            summary = "Delete Pawn Item",
            description = "Delete Pawn Item",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Delete Pawn Item",
                    required = true
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn Item Deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> deletePawnItem(
            @RequestParam(required = false) long id,
            HttpServletRequest servletRequest
    ){
        ApiResponse response = pawnItemService.deletePawnItem(id);
        return ResponseUtils.buildResponse(servletRequest , response);
    }

    @PostMapping("/update-pawn-item")
    @Operation(
            summary = "Update Pawn Item",
            description = "Update Pawn Item.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pawn Item Update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PawnItemRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pawn Item updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    public ResponseEntity<ApiResponse> updatePawnItem(
            @RequestBody PawnItemRequest request,
            HttpServletRequest servletRequest) {

        ApiResponse response = pawnItemService.updatePawnItem(request);
        return ResponseUtils.buildResponse(servletRequest, response);
    }

    @PostMapping("/getPawnItemReportList")
    public ResponseEntity<PaginatedApiResponse> getPawnItemReportList(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
            ){
        PaginatedApiResponse response = pawnItemService.getPawnItemReportList(body);
        return ResponseUtils.buildPaginatedResponse(request , response);
    }

}
