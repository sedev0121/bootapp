package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseOrderMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseOrderMainRepository extends JpaRepository<PurchaseOrderMain, Long> {

	PurchaseOrderMain findOneByCode(String code);

	@Query(value = "SELECT a.* FROM purchase_order_main a left join vendor b on a.vencode=b.code WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", countQuery = "SELECT count(*) FROM purchase_order_main a left join vendor b on a.vencode=b.code WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", nativeQuery = true)
	Page<PurchaseOrderMain> findBySearchTerm(String code, String vendor, Pageable pageable);
}
