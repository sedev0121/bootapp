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

}
