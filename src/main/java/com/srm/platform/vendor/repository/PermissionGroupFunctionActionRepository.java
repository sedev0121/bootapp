package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.srm.platform.vendor.model.PermissionGroupFunctionAction;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PermissionGroupFunctionActionRepository extends JpaRepository<PermissionGroupFunctionAction, Long> {
	List<PermissionGroupFunctionAction> findAllByGroupId(Long group);
}
