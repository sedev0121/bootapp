package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PermissionGroupFunctionUnit;
import com.srm.platform.vendor.utility.IGroupFunctionUnit;
import com.srm.platform.vendor.utility.PermissionUnit;

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

	@Query(value = "select c.id, c.name, GROUP_CONCAT(unit_id) units from permission_group_function_unit a left join permission_group_user b on a.group_id=b.group_id left join function c on a.function_id=c.id where b.account_id=:accountId group by c.id, c.name ORDER BY c.id", nativeQuery = true)
	List<PermissionUnit> findPermissionUnitsForAccount(Long accountId);

	@Query(value = "select GROUP_CONCAT(id) units from unit where parent_id in :parentIds", nativeQuery = true)
	List<PermissionUnit> findChildrenByParentId(String[] parentIds);
}
