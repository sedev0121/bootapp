package com.srm.platform.vendor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.srm.platform.vendor.model.ProvideClass;
import com.srm.platform.vendor.searchitem.SearchItem;

@Repository
public interface ProvideClassRepository extends JpaRepository<ProvideClass, Long> {
	ProvideClass findOneById(Long id);
	
	
	@Query(value = "SELECT * FROM provide_class where code=?1 and id<>?2", nativeQuery = true)
	List<ProvideClass> findDuplicatedCode(Long code, Long id);

	@Query(value = "SELECT * from provide_class a left join vendor_provide b on a.id=b.provide_id where b.vendor_code is not null and provide_id=?1", nativeQuery = true)
	List<ProvideClass> checkVendorsUsingId(Long id);

	@Query(value = "SELECT * from provide_class a left join unit_provide b on a.id=b.provide_id where b.unit_id is not null and provide_id=?1", nativeQuery = true)
	List<ProvideClass> checkUnitsUsingId(Long id);

	
	@Query(value = "select a.* from provide_class a left join unit_provide b on a.id=b.provide_id left join vendor_provide c on a.id=c.provide_id where a.id=?1 and (b.unit_id is not null or c.vendor_code is not null)", nativeQuery = true)
	List<ProvideClass> findListUsingId(Long id);
	
	@Query(value = "SELECT id code, concat(name, '(', code, ')') name FROM provide_class WHERE name LIKE %?1% order by code asc", countQuery = "SELECT count(*) FROM provide_class WHERE name LIKE %?1%", nativeQuery = true)
	Page<SearchItem> findForSelect(String search, Pageable pageable);
	
	@Query(value = "SELECT distinct a.id code, concat(a.name, '(', a.code, ')') name FROM provide_class a left join unit_provide b on a.id=b.provide_id WHERE b.unit_id is null && name LIKE %?1% order by a.code asc", countQuery = "SELECT count(distinct a.id) FROM provide_class a left join unit_provide b on a.id=b.provide_id WHERE b.unit_id is null && name LIKE %?1%", nativeQuery = true)
	Page<SearchItem> findForSelectAdmin(String search, Pageable pageable);
	
	
	@Query(value = "select a.* from provide_class a left join unit_provide b on a.id=b.provide_id where b.unit_id=?1", nativeQuery = true)
	List<ProvideClass> findProvideClassesByUnitId(Long id);
	
	@Query(value = "select a.* from provide_class a left join vendor_provide b on a.id=b.provide_id where b.vendor_code=?1 and b.provide_id in (select provide_id from unit_provide where unit_id in ?2)", nativeQuery = true)
	List<ProvideClass> findProvideClassesByVendorCodeAndUnitId(String code, List<String> unitList);

	@Query(value = "select a.* from provide_class a left join vendor_provide b on a.id=b.provide_id where b.vendor_code=?1", nativeQuery = true)
	List<ProvideClass> findProvideClassesByVendorCode(String code);

	@Query(value = "select a.* from provide_class a left join vendor_provide b on a.id=b.provide_id where b.vendor_code=?1 and b.provide_id in (select provide_id from unit_provide where unit_id in ?2)", nativeQuery = true)
	List<ProvideClass> findVendorProvideClassesForUnits(String vendorCode, List<String> unitList);
}