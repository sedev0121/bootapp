package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.utility.SearchItem;

@Repository
public interface ProvideClassRepository extends JpaRepository<ProvideClass, Long> {
	ProvideClass findOneById(Long id);
	
	
	@Query(value = "SELECT * FROM provide_class where code=?1 and id<>?2", nativeQuery = true)
	List<ProvideClass> findDuplicatedCode(Long code, Long id);

	@Query(value = "select a.* from provide_class a left join unit_provide b on a.id=b.provide_id left join vendor_provide c on a.id=c.provide_id where a.id=?1 and (b.unit_id is not null or c.vendor_code is not null)", nativeQuery = true)
	List<ProvideClass> findListUsingId(Long id);
	
	@Query(value = "SELECT id code, concat(name, '(', code, ')') name FROM provide_class a WHERE name LIKE %?1% order by a.code asc", countQuery = "SELECT count(*) FROM provide_class WHERE name LIKE %?1%", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);
}