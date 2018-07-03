package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.utility.InventorySearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	@Query(value = "SELECT * FROM inventory WHERE code LIKE %?1% or name LIKE %?1% ", nativeQuery = true)
	Page<Inventory> findBySearchTerm(String search, Pageable pageable);

	Inventory findByCode(String code);

	@Query(value = "select a.code, a.name, a.specs, a.puunit_name, ifnull(b.favdate, a.start_date) start_date, ifnull(b.fcanceldate, a.end_date) end_date, ifnull(b.fprice, a.ref_sale_price) price from inventory a left join price b on a.code=b.cinvcode and b.fisoutside=0 and b.fsupplyno=?1 where a.name like %?2%", countQuery = "select count(a.code) from inventory a left join price b on a.code=b.cinvcode and b.fisoutside=0 and b.fsupplyno=?1 where a.name like %?2%", nativeQuery = true)
	Page<InventorySearchItem> findSelectListBySearchTerm(String vendorCode, String invName, Pageable pageable);

}
