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

public class ExcelIncomingReportView extends AbstractXlsView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("application/ms-excel; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("来料异常.xls", "UTF-8"));

		JSONObject json = (JSONObject)model.get("exportList");
		Sheet sheet = workbook.createSheet("来料异常");
		
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("No");
		header.createCell(1).setCellValue("供应商编码");
		header.createCell(2).setCellValue("供应商");
		header.createCell(3).setCellValue("表体订单号");
		header.createCell(4).setCellValue("到货日期");
		header.createCell(5).setCellValue("存货编码");
		header.createCell(6).setCellValue("存货名称");
		header.createCell(7).setCellValue("主计量单位");
		header.createCell(8).setCellValue("订单数量");
		header.createCell(9).setCellValue("到货数量");
		header.createCell(10).setCellValue("合格数量");
		header.createCell(11).setCellValue("退货数量");
		header.createCell(12).setCellValue("异常比例");
		header.createCell(13).setCellValue("异常原因");
		header.createCell(14).setCellValue("处理结果");
		
		JSONArray dataList = json.getJSONArray("list");
		for (int i = 0; i < dataList.length(); ++i) {
			JSONObject objects = dataList.getJSONObject(i);
			
			// create the row data
			Row row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(objects.getString("cVenCode"));
			row.createCell(2).setCellValue(objects.getString("cVenAbbName"));
			row.createCell(3).setCellValue(objects.getString("cPoid"));
			row.createCell(4).setCellValue(objects.getString("dArriveDate"));
			row.createCell(5).setCellValue(objects.getString("cInvCode"));
			row.createCell(6).setCellValue(objects.getString("cInvName"));
			row.createCell(7).setCellValue(objects.getString("ccomunitname"));
			row.createCell(8).setCellValue(objects.getString("iQuantity"));
			row.createCell(9).setCellValue(objects.getString("iArrQTY"));
			row.createCell(10).setCellValue(objects.getString("fPoValidQuantity"));
			row.createCell(11).setCellValue(objects.getString("fPoRetQuantity"));

		}
	}

}
