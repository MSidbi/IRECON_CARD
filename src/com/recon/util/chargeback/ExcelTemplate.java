package com.recon.util.chargeback;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class ExcelTemplate extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> arg0,
			HSSFWorkbook workBook, HttpServletRequest arg2, HttpServletResponse arg3)
			throws Exception {

 

		// create a new Excel sheet
		HSSFSheet sheet = workBook.createSheet("NPS Upload ");
		// create style for header cells

		HSSFRow header = sheet.createRow(0);

		header.createCell(0).setCellValue("CRDNO");
		
		header.createCell(1).setCellValue("RRN");
		
		header.createCell(2).setCellValue("MEMMSG");
		
		header.createCell(3).setCellValue("MSGRSNCD");
		
		header.createCell(4).setCellValue("AMOUNT");
		
		header.createCell(5).setCellValue("LOCALTXNTIME");
		
	}

}
