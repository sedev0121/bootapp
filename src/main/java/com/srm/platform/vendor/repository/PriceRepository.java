package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Price;
import com.srm.platform.vendor.utility.PriceSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PriceRepository extends JpaRepository<Price, Long> {
	@Query(value = "SELECT a.*, d.realname createname, b.name vendorname, c.name inventoryname FROM price a left join vendor b on a.fsupplyno=b.code left join inventory c on a.cinvcode=c.code left join account d on a.createby=d.id WHERE b.unit_id in ?1 and (b.code LIKE %?2% or b.name LIKE %?2%) and (c.code like %?3% or c.name LIKE %?3%)", nativeQuery = true)
	Page<PriceSearchItem> findBySearchTerm(List<String> unitList, String vendor, String inventory, String start,
			String end, Pageable pageable);
}
