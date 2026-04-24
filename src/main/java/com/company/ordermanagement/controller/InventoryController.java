package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.dto.response.InventoryAuditLogResponse;
import com.company.ordermanagement.model.dto.response.InventoryResponse;
import com.company.ordermanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Activity 5 — Documentation exercise.
// This controller has 8 endpoints but no documentation yet.
// Audience uses Claude Code to generate the API docs from this code.
// See docs/prompts/activity5-documentation.md for the demo prompt.

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryBySku(sku)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllInventory()));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockItems()));
    }

    @GetMapping("/warehouse/{location}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getByWarehouse(
            @PathVariable String location) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getInventoryByWarehouse(location)));
    }

    @PostMapping("/{id}/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustQuantity(
            @PathVariable UUID id,
            @RequestParam int delta,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventoryService.adjustQuantity(id, delta, reason), "Inventory adjusted"));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryResponse>> receiveStock(
            @PathVariable UUID id,
            @RequestParam int quantity,
            @RequestParam UUID purchaseOrderId) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventoryService.receiveStock(id, quantity, purchaseOrderId), "Stock received"));
    }

    @GetMapping("/{id}/audit-log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<InventoryAuditLogResponse>>> getAuditLog(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAuditLog(id)));
    }
}
