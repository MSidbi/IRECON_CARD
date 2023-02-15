package com.recon.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

@SuppressWarnings("unchecked")
public class GenerateReconReport extends AbstractExcelView {
	
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("Inside Daily Excel Download");
		List<Object> Data = (List<Object>) map.get("data");
		String name = (String) map.get("ReportName");
		List<Object> print_data = new ArrayList<>();
		String sheetName = "SHEET";
		List<Object> headers = new ArrayList<>();
		List<Object> sheet_data = new ArrayList<>();
		List<Object> main_data = new ArrayList<>();
		
		System.out.println("Got the data");
		String filename = "Report";
		if(name != null && !name.equalsIgnoreCase(""))
		{
			filename = name;
		}
		
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+filename+".xls");
		
		OutputStream outStream = response.getOutputStream();
		
		workbook = new HSSFWorkbook();
		
		for(int sheets = 0; sheets < Data.size(); sheets++)
		{
			print_data = (ArrayList<Object>) Data.get(sheets);
			
			sheetName = (String) print_data.get(0);
			headers = (ArrayList<Object>) print_data.get(1);
			sheet_data = (ArrayList<Object>) print_data.get(2);
			int rowCount = 0, start_Row = 0;
			 HSSFSheet sheet = workbook.createSheet(sheetName); 
			 
			 for(int i = 0; i < headers.size() ; i++)
			 {
				 List<String> Excel_Headers = (ArrayList<String>) headers.get(i);
				 HSSFRow header = sheet.createRow(rowCount);
				 
				 for(int j =0 ;j < Excel_Headers.size(); j++)
					{
						header.createCell(j).setCellValue(Excel_Headers.get(j));
					}
				 main_data = (ArrayList<Object>) sheet_data.get(i);
				 start_Row = rowCount+1;
				 rowCount = rowCount+main_data.size();
				 HSSFRow rowEntry;
				 
				// for(int k = start_Row; k< rowCount ; k++)
					 
					 for(int m = 0,k = start_Row; m < main_data.size() && k <= rowCount ; m++,k++)
					 {
						 rowEntry = sheet.createRow(k);
						 Map<String, String> map_data =  (Map<String, String>) main_data.get(m);
						 for(int n = 0 ;n < Excel_Headers.size() ; n++)
						{
							 rowEntry.createCell(n).setCellValue(map_data.get(Excel_Headers.get(n)));
						}
					 }
					
				 rowCount = rowCount+2;
				 
			 }
			
			
		}
		
	workbook.write(outStream);
	outStream.close();
	
    
	response.getOutputStream().flush();
	
	}
	

}
