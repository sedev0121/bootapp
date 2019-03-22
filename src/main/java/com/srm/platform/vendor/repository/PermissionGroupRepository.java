package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.srm.platform.vendor.model.PermissionGroup;
import com.srm.platform.vendor.searchitem.AccountSearchItem;
import com.srm.platform.vendor.searchitem.PermissionItem;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

	PermissionGroup findOneById(Long id);

	PermissionGroup findOneByName(String name);

	@Query(value = "select distinct b.id, b.realname, b.username from permission_group_user a left join account b on a.account_id = b.id where b.id is not null and a.group_id=:group_id", nativeQuery = true)
	List<AccountSearchItem> findAccountsInGroupById(@Param("group_id") Long id);

	@Query(value = "SELECT * FROM permission_group t WHERE "
			+ "t.name LIKE CONCAT('%',:search, '%') or t.description LIKE CONCAT('%',:search, '%')", nativeQuery = true)
	Page<PermissionGroup> findBySearchTerm(@Param("search") String search, Pageable pageable);

	@Query(value = "select c.name function, d.name action from permission_group_function_action a left join function_action b on a.function_action_id=b.id left join function c on b.function_id=c.id left join action d on b.action_id=d.id left join permission_group e on a.group_id=e.id LEFT JOIN permission_group_user f on e.id=f.group_id left join account g on f.account_id=g.id where g.id=:account_id", nativeQuery = true)
	List<PermissionItem> findPermissionForAccount(Long account_id);

	@Query(value = "SELECT id code, name FROM permission_group WHERE name LIKE %?1%", countQuery = "SELECT count(*) FROM permission_group WHERE name LIKE %?1%", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);
	
	@Query(value = "select a.* from permission_group a left join permission_group_user b on a.id=b.group_id where b.account_id=:accountId", nativeQuery = true)
	List<PermissionGroup> findGroupListOfAccount(@Param("accountId") Long accountId);

}
