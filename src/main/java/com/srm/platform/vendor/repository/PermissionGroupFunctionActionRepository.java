package com.srm.platform.vendor.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.PermissionGroupFunctionAction;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PermissionGroupFunctionActionRepository extends JpaRepository<PermissionGroupFunctionAction, Long> {
	List<PermissionGroupFunctionAction> findAllByGroupId(Long group);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from permission_group_function_action where group_id=:groupId", nativeQuery = true)
	void deleteByGroupId(Long groupId);
}
