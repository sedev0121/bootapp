package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.TaskLog;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

	TaskLog findOneById(Long id);
	
	@Query(value = "SELECT * FROM task_log a left join task b on a.task_id=b.id left join vendor c on a.vendor_code=c.code where (b.code LIKE %?1% or c.name LIKE %?1%)", nativeQuery = true)
	Page<TaskLog> findBySearchTerm(String search, Pageable pageable);
	
}
