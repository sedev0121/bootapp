package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.searchitem.InventoryCheckItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	
	Inventory findOneByCode(String code);
	
	@Query(value = "SELECT * FROM inventory a left join inventory_class b on a.sort_code=b.code left join box_class c on a.box_class_id=c.id WHERE (a.specs LIKE %?1% or a.code LIKE %?1% or a.name LIKE %?1%) and (b.code LIKE %?2% or b.name LIKE %?2%)", nativeQuery = true)
	Page<Inventory> findBySearchTerm(String inventory, String inventoryClass, Pageable pageable);

	@Query(value = "SELECT * FROM inventory a left join inventory_class b on a.sort_code=b.code left join box_class c on a.box_class_id=c.id WHERE (a.specs LIKE %?1% or a.code LIKE %?1% or a.name LIKE %?1%) and (b.code LIKE %?2% or b.name LIKE %?2%) and (c.code LIKE %?3% or c.name LIKE %?3%)", nativeQuery = true)
	Page<Inventory> findBySearchTerm(String inventory, String inventoryClass, String boxClass, Pageable pageable);

	@Query(value = "SELECT * FROM inventory WHERE code in ?1", nativeQuery = true)
	List<InventoryCheckItem> checkCodes(String[] inventoryList);

}
