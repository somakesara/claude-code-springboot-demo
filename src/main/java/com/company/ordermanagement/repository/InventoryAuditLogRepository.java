package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.entity.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, UUID> {

    @Query("SELECT l FROM InventoryAuditLog l WHERE l.inventoryItemId = :itemId ORDER BY l.recordedAt DESC")
    List<InventoryAuditLog> findByInventoryItemId(@Param("itemId") UUID itemId);
}
