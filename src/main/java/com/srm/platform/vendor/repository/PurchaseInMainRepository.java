package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseInMain;
import com.srm.platform.vendor.utility.PurchaseInSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseInMainRepository extends JpaRepository<PurchaseInMain, Long> {

	PurchaseInMain findOneByCode(String code);

	PurchaseInMain findOneById(Long id);

	@Query(value = "SELECT a.*, b.name vendorname FROM purchase_in_main a left join vendor b on a.vendorcode=b.code WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", countQuery = "SELECT count(*) FROM purchase_in_main a left join vendor b on a.vendorcode=b.code WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", nativeQuery = true)
	Page<PurchaseInSearchItem> findBySearchTerm(String code, String vendor, Pageable pageable);

}
