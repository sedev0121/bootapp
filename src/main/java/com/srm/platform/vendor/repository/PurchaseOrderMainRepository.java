package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseOrderMain;
import com.srm.platform.vendor.utility.PurchaseOrderSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseOrderMainRepository extends JpaRepository<PurchaseOrderMain, Long> {

	PurchaseOrderMain findOneByCode(String code);

	@Query(value = "SELECT a.*, b.name vendorname, c.realname deployername, d.realname reviewername FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", countQuery = "SELECT count(*) FROM purchase_order_main a left join vendor b on a.vencode=b.code WHERE a.code like %?1% and (b.code LIKE %?2% or b.name LIKE %?2% )", nativeQuery = true)
	Page<PurchaseOrderSearchItem> findBySearchTerm(String code, String vendor, Pageable pageable);

	@Query(value = "SELECT a.*, b.name vendorname, c.realname deployername, d.realname reviewername FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id WHERE a.code like %?1% and a.vencode=?2", countQuery = "SELECT count(*) FROM purchase_order_main a WHERE a.code like %?1% and a.vencode=?2", nativeQuery = true)
	Page<PurchaseOrderSearchItem> findBySearchTermForVendor(String code, String vendor, Pageable pageable);

}
