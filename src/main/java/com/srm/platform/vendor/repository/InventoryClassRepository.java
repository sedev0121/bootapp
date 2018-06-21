package com.srm.platform.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.InventoryClass;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryClassRepository extends JpaRepository<InventoryClass, Long> {

}
