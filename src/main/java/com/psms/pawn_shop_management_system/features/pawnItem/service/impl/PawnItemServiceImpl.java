package com.psms.pawn_shop_management_system.features.pawnItem.service.impl;

import com.psms.pawn_shop_management_system.common.constant.Status;
import com.psms.pawn_shop_management_system.config.response.dto.ApiResponse;
import com.psms.pawn_shop_management_system.config.response.util.ServerUtils;
import com.psms.pawn_shop_management_system.features.pawnItem.dto.request.PawnItemRequest;
import com.psms.pawn_shop_management_system.features.pawnItem.dto.response.PawnItemsResponse;
import com.psms.pawn_shop_management_system.features.pawnItem.repository.CustomerRepository;
import com.psms.pawn_shop_management_system.features.pawnItem.repository.PawnItemRepository;
import com.psms.pawn_shop_management_system.features.pawnItem.repository.PawnTransactionRepository;
import com.psms.pawn_shop_management_system.features.pawnItem.service.PawnItemService;
import com.psms.pawn_shop_management_system.model.Customer;
import com.psms.pawn_shop_management_system.model.PawnItem;
import com.psms.pawn_shop_management_system.model.PawnItemDetails;
import com.psms.pawn_shop_management_system.model.PawnTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PawnItemServiceImpl implements PawnItemService {

    private final CustomerRepository customerRepository;
    private final PawnItemRepository pawnItemRepository;
    private final PawnTransactionRepository pawnTransactionRepository;
    private final ServerUtils serverUtils;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ApiResponse createPawnItem(PawnItemRequest request) {

        boolean isAlreadyExist = false;
        Customer customer = new Customer();
        PawnItem pawnItem = new PawnItem();
        PawnTransaction transaction = new PawnTransaction();
        LocalDateTime currentDateTime = LocalDateTime.parse(serverUtils.getLocalDateTime(), formatter);

        // Check If the Item is Active in the System
        if (request.getCategory().equalsIgnoreCase("Phone")) {
            isAlreadyExist = pawnItemRepository.existsByDetailAndStatus("imei",request.getDetails().get("imei") , 1) > 0;
        } else if (request.getCategory().equalsIgnoreCase("MotoBike")) {
            isAlreadyExist = pawnItemRepository.existsByDetailAndStatus("PlateNumber",request.getDetails().get("PlateNumber") , 1) > 0;
        } else if (request.getCategory().equalsIgnoreCase("Watches")) {
            isAlreadyExist = pawnItemRepository.existsByDetailAndStatus("serialNumber",request.getDetails().get("serialNumber") , 1) > 0;
        } else if (request.getCategory().equalsIgnoreCase("Bicycle")) {
            isAlreadyExist = pawnItemRepository.existsByDetailAndStatus("serialNumber",request.getDetails().get("serialNumber") , 1) > 0;
        }

        if (!isAlreadyExist) {
            // 1. Create Customer
            customer = customerRepository.findByNationalId(request.getCustomerNrc())
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setCreatedAt(currentDateTime);
                        newCustomer.setName(request.getCustomerName());
                        newCustomer.setPhoneNumber(request.getCustomerPhone());
                        newCustomer.setNationalId(request.getCustomerNrc());
                        newCustomer.setAddress(request.getCustomerAddress());
                        return customerRepository.save(newCustomer);
                    });


            // 2. Create pawn item
            pawnItem.setCreatedAt(currentDateTime);
            pawnItem.setCategory(request.getCategory());
            pawnItem.setAmount(request.getAmount());
            pawnItem.setPawnDate(request.getPawnDate());
            long nextVoucherCode = pawnItemRepository.findMaxVoucherCode() + 1;
            pawnItem.setVoucherCode(nextVoucherCode);
            pawnItem.setDueDate(request.getDueDate());
            pawnItem.setCustomer(customer);
            pawnItem.setDescription(request.getDescription());

            // 3. Add details
            request.getDetails().forEach((key, value) -> {
                PawnItemDetails detail = new PawnItemDetails();
                detail.setCreatedAt(currentDateTime);
                detail.setFieldName(key);
                detail.setFieldValue(value);
                detail.setPawnItem(pawnItem);
                pawnItem.getPawnItemDetailsList().add(detail);
            });

            pawnItemRepository.save(pawnItem);

            // 4. Create transaction
            transaction.setCreatedAt(currentDateTime);
            transaction.setCustomer(customer);
            transaction.setPawnItem(pawnItem);
            transaction.setPawnDate(request.getPawnDate());
            transaction.setDueDate(request.getDueDate());
            transaction.setLoanAmount(BigDecimal.valueOf(request.getAmount()));
            transaction.setRedeemed(false);
            pawnTransactionRepository.save(transaction);

            // 5. Build ApiResponse
            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Pawn item created successfully")
                    .data(Map.of(
                            "customerId", customer.getId(),
                            "pawnItemId", pawnItem.getId(),
                            "transactionId", transaction.getId()
                    ))
                    .meta(Map.of(
                            "timestamp", System.currentTimeMillis()
                    ))
                    .build();

        } else {
            // 5. Build ApiResponse
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Pawn Item Already Exists !")
                    .meta(Map.of(
                            "timestamp", System.currentTimeMillis()
                    ))
                    .build();
        }
    }

    @Override
    public ApiResponse getPawnItems(final String category,final String sortBy) {
        List<PawnItem> items;

        if(category != null && !category.equalsIgnoreCase("All")){
            items = pawnItemRepository.findByCategoryAndStatus(category , Status.ACTIVE);
        } else {
            items = pawnItemRepository.findByStatus(Status.ACTIVE);
        }

        // Optional sorting logic
        if ("amount".equalsIgnoreCase(sortBy)) {
            items.sort(Comparator.comparing(PawnItem::getAmount));
        } else if ("pawnDate".equalsIgnoreCase(sortBy)) {
            items.sort(Comparator.comparing(PawnItem::getPawnDate));
        }

        List<PawnItemsResponse> responseList = items.stream()
                .map(item -> new PawnItemsResponse(
                        item.getId(),
                        item.getCustomer().getName(),
                        item.getCustomer().getNationalId(),
                        item.getCategory(),
                        BigDecimal.valueOf(item.getAmount()),
                        item.getPawnDate(),
                        item.getDueDate(),
                        item.getStatus().name(),
                        item.getPawnItemDetailsList().stream()
                                .collect(Collectors.toMap(PawnItemDetails::getFieldName, PawnItemDetails::getFieldValue))
                ))
                .toList();

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Pawn items fetched successfully")
                .data(responseList)
                .build();
    }

    @Override
    public ApiResponse deletePawnItem(final long id) {
        Optional<PawnItem> pawnItemOpt = pawnItemRepository.findById(id);

        if (pawnItemOpt.isEmpty()) {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("Pawn item not found")
                    .data(null)
                    .build();
        }

        PawnItem pawnItem = pawnItemOpt.get();
        pawnItem.setStatus(Status.INACTIVE);
        pawnItemRepository.save(pawnItem);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Pawn item Deleted successfully")
                .data(null)
                .build();
    }
}
