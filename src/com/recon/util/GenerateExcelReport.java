package com.recon.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;




@SuppressWarnings("unchecked")
public class GenerateExcelReport extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<List<String>> generatettum_list = null;
		generatettum_list= (List<List<String>>) map.get("DATA");
		System.out.println("GENERATEEXCELrEPORT ENTRY ");
		List<String> File_Headers = generatettum_list.get(0);
		
		generatettum_list.remove(0);
		
		//get filename
		String filename = (String)map.get("filename");
		
		List<String> Records = new ArrayList<>();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		
		 System.out.println(strDate);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+filename+".xlsx");
		
		OutputStream outStream = response.getOutputStream();
		
		XSSFWorkbook wb = new XSSFWorkbook();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,10000);
		
		

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		
		//HSSFSheet sheet = workbook.createSheet("Records");
		
		/*CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREEN.index);
		style.setFont(font);*/

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));
		
		SXSSFRow header = sheet.createRow(0);
		
		for(int i =0 ;i < File_Headers.size(); i++)
		{
			header.createCell(i).setCellValue(File_Headers.get(i));
			//header.getCell(i).setCellStyle(style);
		}
		
		
		int inRowCount = 1;
		int sheet_No = 2;
		for(int i = 0 ;i<generatettum_list.size() ; i++)
		{
			/*if(inRowCount > 50000)
			{
				inRowCount = 1;
				sheet = workbook1.createSheet("Records "+sheet_No);
				
				SXSSFRow header1 = sheet.createRow(0);
				
				for(int k =0 ;k < File_Headers.size(); k++)
				{
					header1.createCell(k).setCellValue(File_Headers.get(k));
					//header1.getCell(k).setCellStyle(style);
				}
				sheet_No++;
			}*/
			SXSSFRow header2 = sheet.createRow(inRowCount);
			Records = generatettum_list.get(i);
			
			for(int j = 0 ;j<Records.size(); j++)
			{
				header2.createCell(j).setCellValue(Records.get(j));
			}
			
			inRowCount++;
			
			
			
		}
		
		workbook1.write(outStream);
        outStream.close();
        //generatettum_list.clear();
		response.getOutputStream().flush();
		
		
	}

}