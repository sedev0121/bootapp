package com.srm.platform.vendor.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Account;
import com.srm.platform.vendor.model.PermissionGroupUser;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PermissionGroupUserRepository extends JpaRepository<PermissionGroupUser, Long> {

	Account findOneById(Long id);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from permission_group_user where group_id=:groupId", nativeQuery = true)
	void deleteByGroupId(Long groupId);

}
