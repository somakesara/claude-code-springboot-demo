package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<InventoryItem, UUID> {

    Optional<InventoryItem> findByProductSkuAndDeletedFalse(String productSku);

    Optional<InventoryItem> findByProductIdAndDeletedFalse(UUID productId);

    @Query("SELECT i FROM InventoryItem i WHERE i.deleted = false AND (i.quantityOnHand - i.quantityReserved) < i.reorderThreshold")
    List<InventoryItem> findAllBelowReorderThreshold();

    @Query("SELECT i FROM InventoryItem i WHERE i.deleted = false ORDER BY i.productSku ASC")
    List<InventoryItem> findAllActive();

    @Query("SELECT i FROM InventoryItem i WHERE i.deleted = false AND i.warehouseLocation = :location")
    List<InventoryItem> findAllByWarehouseLocation(@Param("location") String location);
}
