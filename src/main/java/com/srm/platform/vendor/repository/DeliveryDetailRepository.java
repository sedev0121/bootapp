package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.StatementDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Long> {
	
	DeliveryDetail findOneById(Long id);
	
	@Query(value = "SELECT * FROM delivery_detail WHERE code=:code and row_no=:rowNo", nativeQuery = true)
	DeliveryDetail findOneByCodeAndRowNo(String code, Integer rowNo);

	@Query(value = "SELECT * FROM delivery_detail WHERE code=:code", nativeQuery = true)
	List<DeliveryDetail> findDetailsByCode(String code);
	
	@Modifying
	@Query(value = "delete FROM delivery_detail WHERE code= :code", nativeQuery = true)
	void DeleteByCode(String code);
	
	@Query(value = "select count(a.order_detail_id) count from delivery_detail a left join purchase_order_detail b on a.order_detail_id=b.id left join delivery_main c on a.code=c.code where b.code=:code and c.state<5", nativeQuery = true)
	Integer findDetailCountIsDelivering(String code);
}
