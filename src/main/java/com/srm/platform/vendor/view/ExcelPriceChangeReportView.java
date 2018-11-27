package com.srm.platform.vendor.view;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.srm.platform.vendor.model.PriceChangeReportItem;

public class ExcelPriceChangeReportView extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("application/ms-excel; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("价格变动.xls", "UTF-8"));
		
		List<PriceChangeReportItem> price_list = (List<PriceChangeReportItem>)model.get("exportList");
		Sheet sheet = workbook.createSheet("价格变动");
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("No");
		header.createCell(1).setCellValue("供应商编码");
		header.createCell(2).setCellValue("供应商名称");
		header.createCell(3).setCellValue("存货编码");
		header.createCell(4).setCellValue("存货名称");
		header.createCell(5).setCellValue("规格型号");
		header.createCell(6).setCellValue("主计量单位");
		header.createCell(7).setCellValue("上年度价格");
		header.createCell(8).setCellValue("今年最新价格");
		header.createCell(9).setCellValue("变动幅度");
		header.createCell(10).setCellValue("平均价格");
		
		for (int i = 0; i < price_list.size(); ++i) {
			PriceChangeReportItem objects = price_list.get(i);
			
			// create the row data
			Row row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(objects.getVendorcode());
			row.createCell(2).setCellValue(objects.getVendorname());
			row.createCell(3).setCellValue(objects.getInventorycode());
			row.createCell(4).setCellValue(objects.getInventoryName());
			row.createCell(5).setCellValue(objects.getSpecs());
			row.createCell(6).setCellValue(objects.getUnitname());
			row.createCell(7).setCellValue(objects.getPreviousprice());
			row.createCell(8).setCellValue(objects.getCurrentprice());
			row.createCell(9).setCellValue(objects.getChangepercent());
			row.createCell(10).setCellValue(objects.getAverageprice());
		}
	}

}
