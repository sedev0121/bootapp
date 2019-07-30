package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ContractDetail;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ContractDetailRepository extends JpaRepository<ContractDetail, Long> {

	ContractDetail findOneById(Long id);

	@Query(value = "SELECT * FROM contract_detail WHERE code= :code order by row_no", nativeQuery = true)
	List<ContractDetail> findDetailsByCode(String code);


}
