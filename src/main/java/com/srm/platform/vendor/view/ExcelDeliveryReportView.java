package com.srm.platform.vendor.view;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.srm.platform.vendor.utility.Utils;

public class ExcelDeliveryReportView extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("application/ms-excel; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("准时交货率.xls", "UTF-8"));
		
		JSONObject json = (JSONObject)model.get("exportList");
		Sheet sheet = workbook.createSheet("准时交货率");
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("No");
		header.createCell(1).setCellValue("订单号");
		header.createCell(2).setCellValue("订单日期");
		header.createCell(3).setCellValue("计划到货日期");
		header.createCell(4).setCellValue("供应商编码");
		header.createCell(5).setCellValue("供应商简称");
		header.createCell(6).setCellValue("存货编码");
		header.createCell(7).setCellValue("存货名称");
		header.createCell(8).setCellValue("存货大类");
		header.createCell(9).setCellValue("主计量单位");
		header.createCell(10).setCellValue("订单数量");
		header.createCell(11).setCellValue("累计到货数量");
		header.createCell(12).setCellValue("准时到货数量");
		header.createCell(13).setCellValue("准时交货率（%）");
		
		JSONArray dataList = json.getJSONArray("list");
		for (int i = 0; i < dataList.length(); ++i) {
			JSONObject objects = dataList.getJSONObject(i);
			int mainQty = objects.getInt("iQuantity");
			int timeQty = objects.getInt("itimelyQuantity");
			float percent = 0.0f;
			if (mainQty > 0) {
				percent = (float)timeQty / (float)mainQty * 100.0f;
				percent = (float) Utils.costRound(percent);
			}
			
			// create the row data
			Row row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(objects.getString("cPoid"));
			row.createCell(2).setCellValue(objects.getString("dDate"));
			row.createCell(3).setCellValue(objects.getString("dArriveDate"));
			row.createCell(4).setCellValue(objects.getString("cVenCode"));
			row.createCell(5).setCellValue(objects.getString("cVenAbbName"));
			row.createCell(6).setCellValue(objects.getString("cInvCode"));
			row.createCell(7).setCellValue(objects.getString("cInvName"));
			row.createCell(8).setCellValue(objects.getString("cInvCName"));
			row.createCell(9).setCellValue(objects.getString("ccomunitname"));
			row.createCell(10).setCellValue(objects.getString("iQuantity"));
			row.createCell(11).setCellValue(objects.getString("iArrQTY"));
			row.createCell(12).setCellValue(objects.getString("itimelyQuantity"));
			row.createCell(13).setCellValue(percent);

		}
	}

}
