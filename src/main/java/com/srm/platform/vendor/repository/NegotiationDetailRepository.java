package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.NegotiationDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface NegotiationDetailRepository extends JpaRepository<NegotiationDetail, Long> {
	
	NegotiationDetail findOneById(Long id);
	
	@Query(value = "SELECT * FROM negotiation_detail WHERE code=:code", nativeQuery = true)
	List<NegotiationDetail> findDetailsByCode(String code);
	
	@Query(value = "SELECT round(RAND()*1000, 0) id, :code code, tax_rate, inventory_code, price, 1 max_quantity, tax_price, now() start_date, DATE_ADD(now(), interval 3 month) end_date, '' memo, 0 valid FROM purchase_order_detail WHERE code=:orderCode", nativeQuery = true)
	List<NegotiationDetail> findDetailsByOrderCode(String code, String orderCode);
}
