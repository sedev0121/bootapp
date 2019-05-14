package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.DeliveryDetail;
import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.searchitem.BoxExportResult;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DeliveryMainRepository extends JpaRepository<DeliveryMain, Long> {
	
	DeliveryMain findOneById(Long id);
	
	DeliveryMain findOneByCode(String code);
	
	@Query(value = "select a.code, b.name inventory_name, b.specs inventory_spec from box a left join inventory b on a.inventory_code=b.code where a.delivery_code=:code and a.box_class_id is null;", nativeQuery = true)
	List<BoxExportResult> findExportBoxListByCode(String code);
}
