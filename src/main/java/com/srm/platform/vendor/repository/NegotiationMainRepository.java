package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.DeliveryMain;
import com.srm.platform.vendor.model.NegotiationMain;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface NegotiationMainRepository extends JpaRepository<NegotiationMain, Long> {
	
	NegotiationMain findOneByCode(String code);
	
	@Query(value = "SELECT * FROM negotiation_main a left join vendor b on a.vendor_code=b.code WHERE b.code like %?1% and b.name like %?2%", nativeQuery = true)
	Page<NegotiationMain> findBySearchTerm(String code, String search, Pageable pageable);
	
	@Query(value = "SELECT * FROM negotiation_main a left join vendor b on a.vendor_code=b.code WHERE  b.code like %?1% and b.name like %?2% and a.state=?3", nativeQuery = true)
	Page<NegotiationMain> findBySearchTerm(String code, String search, Integer state, Pageable pageable);
	
	@Query(value = "SELECT * FROM negotiation_main a left join vendor b on a.vendor_code=b.code WHERE b.code like %?1% and b.code=?2", nativeQuery = true)
	Page<NegotiationMain> findBySearchTermForVendor(String code, String vendor, Pageable pageable);

	@Query(value = "SELECT * FROM negotiation_main a left join vendor b on a.vendor_code=b.code WHERE  b.code like %?1% and b.code=?2 and a.state=?3", nativeQuery = true)
	Page<NegotiationMain> findBySearchTermForVendor(String code, String vendor, Integer state, Pageable pageable);
}
