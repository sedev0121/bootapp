package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Price;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PriceRepository extends JpaRepository<Price, Long> {
	@Query(value = "SELECT a.*, b.name, c.name FROM price a left join vendor b on a.fsupplyno=b.code left join inventory c on a.cinvcode=c.code WHERE (b.code LIKE %?1% or b.name LIKE %?1%) and (c.code like %?2% or c.name LIKE %?2%) and (a.foldavdate>=?3 and a.foldavdate<=?4)", nativeQuery = true)
	Page<Price> findBySearchTerm(String vendor, String inventory, String start, String end, Pageable pageable);
}
