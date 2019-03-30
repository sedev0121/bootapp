package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.InventoryClass;
import com.srm.platform.vendor.searchitem.VendorSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryClassRepository extends JpaRepository<InventoryClass, Long> {

	InventoryClass findOneByCode(String code);
	
	@Query(value = "SELECT * FROM inventory_class where (code LIKE %?1% or name LIKE %?1%)", nativeQuery = true)
	Page<VendorSearchItem> findBySearchTerm(String search, Pageable pageable);
}
