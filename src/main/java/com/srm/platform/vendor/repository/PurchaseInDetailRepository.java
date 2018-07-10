package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseInDetail;
import com.srm.platform.vendor.utility.PurchaseInDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseInDetailRepository extends JpaRepository<PurchaseInDetail, Long> {

	PurchaseInDetail findOneById(Long id);

	@Query(value = "select b.name inventoryname, b.specs, b.puunit_name unitname, a.* from purchase_in_detail a left join inventory b on a.inventorycode=b.code where a.code= :code order by a.rowno", nativeQuery = true)
	List<PurchaseInDetailItem> findDetailsByCode(String code);

	@Query(value = "select a.*, b.date, c.name inventoryname,c.specs from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventorycode=c.code where b.vendorcode=?1 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", countQuery = "select count(*) from purchase_in_detail a left join purchase_in_main b on a.code=b.code left join inventory c on a.inventorycode=c.code where b.vendorcode=?1 and a.code like %?2% and (c.name like %?3% or c.code like %?3%)", nativeQuery = true)
	Page<PurchaseInDetailItem> findForSelect(String vendor, String code, String inventory, Pageable pageable);
}
