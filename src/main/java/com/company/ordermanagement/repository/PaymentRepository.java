package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @EntityGraph(attributePaths = {"order", "order.lineItems"})
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdWithOrderAndLineItems(@Param("id") UUID id);

    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC")
    List<Payment> findAllByOrderId(@Param("orderId") UUID orderId);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.retryCount < :maxRetries")
    List<Payment> findRetryableByStatus(@Param("status") Payment.PaymentStatus status,
                                        @Param("maxRetries") int maxRetries);
}
