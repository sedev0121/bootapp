package com.srm.platform.vendor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.srm.platform.vendor.model.Inventory;
import com.srm.platform.vendor.utility.StockReportSearchItem;

public interface StockReportRepository extends JpaRepository<Inventory, Long> {
	@Query (value = "SELECT a.code, a.name, a.specs, b.name unitname from inventory a left join measurement_unit b on a.main_measure=b.code ",
			 countQuery = "select count(a.code)  from inventory a left join measurement_unit b on a.main_measure=b.code ", nativeQuery = true)
	Page<StockReportSearchItem> findStockReport(Pageable pageable);
}
