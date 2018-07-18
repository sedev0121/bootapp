package com.srm.platform.vendor.repository;

import java.util.List;

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

	@Query(value = "SELECT a.*, b.name vendorname, c.realname deployername, d.realname reviewername FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id WHERE b.unit_id in ?1 and a.state='审核' and a.code like %?2% and (b.code LIKE %?3% or b.name LIKE %?3% )", countQuery = "SELECT count(*) FROM purchase_order_main a left join vendor b on a.vencode=b.code WHERE b.unit_id in ?1 and a.code like %?2% and (b.code LIKE %?3% or b.name LIKE %?3% )", nativeQuery = true)
	Page<PurchaseOrderSearchItem> findBySearchTerm(List<String> unitList, String code, String vendor,
			Pageable pageable);

	@Query(value = "SELECT a.*, b.name vendorname, c.realname deployername, d.realname reviewername FROM purchase_order_main a left join vendor b on a.vencode=b.code left join account c on a.deployer=c.id left join account d on a.reviewer=d.id WHERE a.srmstate>0 and a.code like %?1% and a.vencode=?2", countQuery = "SELECT count(*) FROM purchase_order_main a WHERE a.srmstate>0 and a.code like %?1% and a.vencode=?2", nativeQuery = true)
	Page<PurchaseOrderSearchItem> findBySearchTermForVendor(String code, String vendor, Pageable pageable);

}
