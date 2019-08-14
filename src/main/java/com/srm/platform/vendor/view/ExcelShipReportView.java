package com.srm.platform.vendor.view;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.srm.platform.vendor.model.PurchaseOrderDetail;
import com.srm.platform.vendor.utility.Utils;

public class ExcelShipReportView extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("application/ms-excel; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("订单发货.xls", "UTF-8"));

		List<PurchaseOrderDetail> list = (List<PurchaseOrderDetail>) model.get("exportList");

		// create a wordsheet
		Sheet sheet = workbook.createSheet("订单发货");

		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("No");
		header.createCell(1).setCellValue("订单号");
		header.createCell(2).setCellValue("订单行号");
		header.createCell(3).setCellValue("物料编码");
		header.createCell(4).setCellValue("物料名称");
		header.createCell(5).setCellValue("物料描述");
		header.createCell(6).setCellValue("单位");
		header.createCell(7).setCellValue("数量");
		header.createCell(8).setCellValue("最新预发货日期");
		header.createCell(9).setCellValue("最新预发货数量");
		header.createCell(10).setCellValue("本次预发货日期");
		header.createCell(11).setCellValue("本次预发货数量");
		header.createCell(12).setCellValue("需求日期");
		header.createCell(13).setCellValue("备注");
		header.createCell(14).setCellValue("承诺交货日期");
		header.createCell(15).setCellValue("供方备注");
		header.createCell(16).setCellValue("识别编码");
		int rowNum = 1;
		for (PurchaseOrderDetail entry : list) {
			// create the row data
			Row row = sheet.createRow(rowNum);
			row.createCell(0).setCellValue(rowNum);
			row.createCell(1).setCellValue(entry.getMain().getCode());
			row.createCell(2).setCellValue(entry.getRowNo());
			row.createCell(3).setCellValue(entry.getInventory().getCode());
			row.createCell(4).setCellValue(entry.getInventory().getName());
			row.createCell(5).setCellValue(entry.getInventory().getSpecs());
			row.createCell(6).setCellValue(entry.getInventory().getMainMeasure());
			row.createCell(7).setCellValue(entry.getQuantity());
			row.createCell(10).setCellValue(Utils.formatDate(new Date()));

			row.createCell(16).setCellValue(entry.getId());
			rowNum++;
		}

	}
}