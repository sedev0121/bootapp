package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.BoxClass;
import com.srm.platform.vendor.model.Company;
import com.srm.platform.vendor.model.NoticeClass;
import com.srm.platform.vendor.searchitem.SearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface NoticeClassRepository extends JpaRepository<NoticeClass, Long> {

	NoticeClass findOneById(Long id);
	
	@Query(value = "SELECT * FROM notice_class WHERE name like %?1% order by code asc", nativeQuery = true)
	List<NoticeClass> findBySearchTerm(String search);
	
	@Query(value = "SELECT * FROM notice_class order by id asc", nativeQuery = true)
	List<NoticeClass> findAllNodes();
	
	@Query(value = "SELECT id code, name FROM notice_class", nativeQuery = true)
	Page<SearchItem> findForSelect(Pageable pageable);	
	
	@Query(value = "select a.* from notice_class a left join (select class_id from notice GROUP BY class_id ORDER BY max(verify_date) desc limit 3) b on a.id=b.class_id where b.class_id is not null", nativeQuery = true)
	List<NoticeClass> findLast3Class();
}
