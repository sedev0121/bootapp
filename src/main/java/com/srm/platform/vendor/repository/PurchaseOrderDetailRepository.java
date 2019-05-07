package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PurchaseOrderDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {

	PurchaseOrderDetail findOneById(Long id);

	@Query(value = "select * from purchase_order_detail where code= :code order by row_no", nativeQuery = true)
	List<PurchaseOrderDetail> findDetailsByCode(String code);

	@Query(value = "select * from purchase_order_detail where code= :code and row_no=:rowno limit 1", nativeQuery = true)
	PurchaseOrderDetail findOneByCodeAndRowno(String code, String rowno);
	
	@Query(value = "select * from purchase_order_detail where code like %?1%", nativeQuery = true)
	Page<PurchaseOrderDetail> searchAll(String search, Pageable pageable);
	
	@Query(value = "select * from purchase_order_detail a left join purchase_order_main b on a.close_date is null and a.code=b.code where b.srmstate=2 and (a.inventory_code like %?1% or a.inventory_name like %?1%) and b.vencode=?2 and b.company_id=?3 and b.store_id=?4", nativeQuery = true)
	Page<PurchaseOrderDetail> searchAllOfOneVendor(String search, String vendorCode, Long companyId, Long storeId, Pageable pageable);

}
