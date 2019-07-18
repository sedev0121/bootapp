package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Task;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface TaskRepository extends JpaRepository<Task, Long> {

	Task findOneById(Long id);
	Task findOneByCode(String code);
	
	@Query(value = "SELECT * FROM task a left join account b on a.make_id=b.id where (a.code LIKE %?1% or b.realname LIKE %?1%)", nativeQuery = true)
	Page<Task> findBySearchTerm(String search, Pageable pageable);
	
}
