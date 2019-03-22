package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.model.Unit;
import com.srm.platform.vendor.model.UnitProvide;
import com.srm.platform.vendor.searchitem.PermissionItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UnitProvideRepository extends JpaRepository<UnitProvide, Long> {


	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from unit_provide where unit_id = ?1", nativeQuery = true)
	void deleteByUnitId(Long unitId);
	

}
