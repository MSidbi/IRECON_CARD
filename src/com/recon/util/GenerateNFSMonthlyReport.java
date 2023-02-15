package com.recon.util;

import java.util.ArrayList;
import java.util.Date;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;


@SuppressWarnings("unchecked")
public class GenerateNFSMonthlyReport extends AbstractExcelView {
	
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("Inside Excel Download");
		List<Object> Data = (List<Object>) map.get("Monthly_data");
		String name = (String) map.get("ReportName");
		List<String> Excel_Headers  = new ArrayList<String>();
		List<Object> monthly_Data = new ArrayList<Object>();
		List<String> Excel_Headers2  = new ArrayList<String>();
		List<Object> sum_Data = new ArrayList<Object>();
		if(Data != null && Data.size()>0 )
		{
			Excel_Headers = (List<String>) Data.get(0);
		}
		
		System.out.println("Got columns list");
		
		if(Data != null && Data.size() >1 )
		{
			monthly_Data = (List<Object>) Data.get(1);
		}
		
		System.out.println("Got the data");
		String filename = "NFS_SETTLEMENT_REPORT";	
		
		if(name != null && !name.equalsIgnoreCase(""))
		{
			filename = name;
		}
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+filename+".xls");
		
		OutputStream outStream = response.getOutputStream();
		
		XSSFWorkbook wb = new XSSFWorkbook();
	
		workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Report");
        
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        CellStyle calculatedHeader = workbook.createCellStyle();
        calculatedHeader.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        calculatedHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font calculatedfont = workbook.createFont();
        calculatedfont.setFontName("Arial");
        calculatedfont.setBold(true);
        calculatedHeader.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        calculatedHeader.setFont(calculatedfont);

        CellStyle numberStyle = workbook.createCellStyle();
    	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

    	// create header row
    	HSSFRow header = sheet.createRow(0);    

	for(int i =0 ;i < Excel_Headers.size(); i++)
	{
		header.createCell(i).setCellValue(Excel_Headers.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	HSSFRow rowEntry;
	//ADDING REMAINING DATA
	for(int i =0; i< monthly_Data.size() ; i++)
	{
		/*NFSInterchangeMonthly beanObj = (NFSInterchangeMonthly) monthly_Data.get(i);
		rowEntry = sheet.createRow(i+1);
		
		rowEntry.createCell(0).setCellValue(beanObj.getFiledate());
		rowEntry.createCell(1).setCellValue(beanObj.getCard_acc_term_id());
		rowEntry.createCell(2).setCellValue(beanObj.getSol());
		rowEntry.createCell(3).setCellValue(beanObj.getAadhar_App());
		rowEntry.createCell(4).setCellValue(beanObj.getAcquirer_bi_appr());
		rowEntry.createCell(5).setCellValue(beanObj.getAcquirer_mob_appr());
		rowEntry.createCell(6).setCellValue(beanObj.getAcquirer_ms_appr());
		rowEntry.createCell(7).setCellValue(beanObj.getAcquirer_pc_appr());
		rowEntry.createCell(8).setCellValue(beanObj.getAcquirer_wd_appr());*/

		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) monthly_Data.get(i);
		if(map_data.size()>0)
		{

			for(int m= 0 ;m < Excel_Headers.size() ; m++)
			{
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
			}
		}

	
		
	}
	//workbook1.write(outStream);

	sheet = (HSSFSheet) workbook.createSheet("REPORT2");
	 numberStyle = workbook.createCellStyle();
	numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

	// create header row
	 header = sheet.createRow(0);
	 if(Data!= null && Data.size() >2)
	 {
		 Excel_Headers2 = (List<String>) Data.get(2);
	 }
	 if(Data!= null && Data.size() >3)
	 {
		 sum_Data = (List<Object>) Data.get(3);
	 }
	for(int i =0 ;i < Excel_Headers2.size(); i++)
	{
		header.createCell(i).setCellValue(Excel_Headers2.get(i));
		header.getCell(i).setCellStyle(calculatedHeader);
	}
	
	SXSSFRow rowEntry2;
	//ADDING REMAINING DATA
	for(int i =0; i< sum_Data.size() ; i++)
	{
		/*NFSInterchangeMonthly beanObj = (NFSInterchangeMonthly) sum_Data.get(i);
		rowEntry2 = sheet.createRow(i+1);
		
		rowEntry2.createCell(0).setCellValue(beanObj.getFiledate());
		rowEntry2.createCell(1).setCellValue(beanObj.getSum_chrg());*/

		rowEntry = sheet.createRow(i+1);
		Map<String, String> map_data =  (Map<String, String>) sum_Data.get(i);
		if(map_data.size()>0)
		{

			for(int m= 0 ;m < Excel_Headers2.size() ; m++)
			{
				rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers2.get(m)));
			}
		}

	
		
		
	}
	workbook.write(outStream);
	outStream.close();
	
    
	response.getOutputStream().flush();
	
	}
	

}
