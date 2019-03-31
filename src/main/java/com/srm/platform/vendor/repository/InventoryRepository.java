package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Inventory;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	
	Inventory findOneByCode(String code);
	
	//TODO: inventory class filter
	@Query(value = "SELECT * FROM inventory a left join inventory_class b on a.sort_code=b.code WHERE a.code LIKE %?1% or a.name LIKE %?1% ", nativeQuery = true)
	Page<Inventory> findBySearchTerm(String search, List<String> inventoryClassCodeList, Pageable pageable);

}
