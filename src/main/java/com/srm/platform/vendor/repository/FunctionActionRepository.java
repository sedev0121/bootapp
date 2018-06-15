package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.FunctionAction;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface FunctionActionRepository extends JpaRepository<FunctionAction, Long> {
	List<FunctionAction> findAllByFunctionId(Long functionId);

	@Query(value = "SELECT * FROM function_action t WHERE "
			+ "t.function_id=?1 and t.action_id=?2 limit 1", nativeQuery = true)
	FunctionAction findOne(Long functionId, Long actionId);
}
