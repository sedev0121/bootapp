package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Inventory;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	@Query(value = "SELECT * FROM inventory WHERE code LIKE %?1% or name LIKE %?1% ", nativeQuery = true)
	Page<Inventory> findBySearchTerm(String search, Pageable pageable);
}
