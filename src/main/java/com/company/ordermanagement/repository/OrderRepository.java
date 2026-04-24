package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"lineItems", "customer"})
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.deleted = false")
    Optional<Order> findByIdWithLineItems(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"lineItems"})
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.deleted = false ORDER BY o.createdAt DESC")
    List<Order> findAllByCustomerIdWithLineItems(@Param("customerId") UUID customerId);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.deleted = false")
    List<Order> findAllByStatus(@Param("status") Order.OrderStatus status);
}
