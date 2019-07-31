package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ContractDetail;
import com.srm.platform.vendor.searchitem.ContractDetailItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContractDetailRepository extends JpaRepository<ContractDetail, Long> {

	ContractDetail findOneById(Long id);

	@Query(value = "SELECT a.id, a.row_no, b.code, b.name, b.specs, b.main_measure, a.quantity, a.tax_price, a.floating_direction, a.floating_price, a.memo FROM contract_detail a left join inventory b on a.inventory_code=b.code WHERE a.code= :code order by row_no", nativeQuery = true)
	List<ContractDetailItem> searchDetailsByCode(String code);

	@Query(value = "SELECT * FROM contract_detail WHERE code= :code order by row_no", nativeQuery = true)
	List<ContractDetail> findDetailsByCode(String code);

}
