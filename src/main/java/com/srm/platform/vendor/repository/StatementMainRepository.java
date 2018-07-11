package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.StatementMain;
import com.srm.platform.vendor.utility.StatementSearchItem;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StatementMainRepository extends JpaRepository<StatementMain, Long> {

	StatementMain findOneByCode(String code);

	@Query(value = "select a.*, b.name vendor_name, c.realname maker, d.realname verifier from statement_main a left join vendor b on a.vendor_code=b.code left join account c on a.maker_id=c.id left join account d on a.verifier_id=d.id where a.code like %?1% and (b.code like %?2% or b.name like %?2%)", nativeQuery = true)
	Page<StatementSearchItem> findBySearchTerm(String code, String vendor, Pageable pageable);

	@Query(value = "select a.*, b.name vendor_name, c.realname maker, d.realname verifier from statement_main a left join vendor b on a.vendor_code=b.code left join account c on a.maker_id=c.id left join account d on a.verifier_id=d.id where a.code like %?1% and b.code=?2 and a.state>1", nativeQuery = true)
	Page<StatementSearchItem> findBySearchTermForVendor(String code, String vendor, Pageable pageable);
}
