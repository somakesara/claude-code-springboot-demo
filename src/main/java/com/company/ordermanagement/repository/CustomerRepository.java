package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmailAndDeletedFalse(String email);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND c.tier = :tier")
    List<Customer> findAllByTier(@Param("tier") Customer.CustomerTier tier);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false ORDER BY c.createdAt DESC")
    List<Customer> findAllActive();
}
