package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.srm.platform.vendor.model.ProvideClass;

@Repository
public interface ProvideClassRepository extends JpaRepository<ProvideClass, Long> {
	ProvideClass findOneById(Long id);
	
	
	@Query(value = "SELECT * FROM provide_class where code=?1 and id<>?2", nativeQuery = true)
	List<ProvideClass> findDuplicatedCode(Long code, Long id);

}