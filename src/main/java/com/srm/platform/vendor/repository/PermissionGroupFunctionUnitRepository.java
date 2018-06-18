package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.IGroupFunctionUnit;
import com.srm.platform.vendor.model.PermissionGroupFunctionUnit;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PermissionGroupFunctionUnitRepository extends JpaRepository<PermissionGroupFunctionUnit, Long> {
	List<PermissionGroupFunctionUnit> findAllByGroupId(Long group);

	@Query(value = "select group_id groupId, function_id functionId, GROUP_CONCAT(unit_id) units from permission_group_function_unit where group_id=?1 GROUP BY group_id, function_id", nativeQuery = true)
	List<IGroupFunctionUnit> findUnitsByGroupId(Long groupId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from permission_group_function_unit where group_id=:groupId", nativeQuery = true)
	void deleteByGroupId(Long groupId);
}
