package com.recon.util;

/*import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;


import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.GenerateTTUMBean;
import com.recon.model.GenerateTTUMBeanold;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.recon.model.GenerateTTUMBean;



@SuppressWarnings("unchecked")
public class GenerateUCOTTUMExcel extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<GenerateTTUMBean> generatettum_list = (List<GenerateTTUMBean>) map.get("generate_ttum");
		
		GenerateTTUMBean generateBean = (GenerateTTUMBean)generatettum_list.get(1);
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=TTUM_"+generateBean.getStCategory()+"_REV_RECON_"+strDate+".xlsx");
		// create a new Excel sheet
		//xlsx code of jyoti
		
		OutputStream outStream = response.getOutputStream();
		
			XSSFWorkbook wb = new XSSFWorkbook();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
		
		

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		
		//HSSFSheet sheet = workbook.createSheet("TTUM");

		GenerateTTUMBean generateTTUMBeanObj = generatettum_list.get(0);
		
		List<String> Excel_Headers = generateTTUMBeanObj.getStExcelHeader();
		List<List<String>> stTTUM_Data = generateTTUMBeanObj.getStTTUM_Records();
		List<List<String>> stTTUM_DData = generateTTUMBeanObj.getStTTUM_DRecords();
		
		
		
		
		/*for(int i = 0; i<Excel_Headers.size() ; i++)
		{
			System.out.println("Header is "+Excel_Headers.get(i));
		}*/	
		
		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		/*Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREEN.index);
		style.setFont(font);*/

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = sheet.createRow(0);
		
		
		
		for(int i =0 ;i < Excel_Headers.size(); i++)
		{
			header.createCell(i).setCellValue(Excel_Headers.get(i));
			header.getCell(i).setCellStyle(style);
		}
		
		// create data rows
	
		if(stTTUM_Data !=null && stTTUM_DData !=null)
		{
			List<String> stTTUM_Records = new ArrayList<>();
			List<String> stTTUM_DRecords = new ArrayList<>();

			int evencount = 0;
			int oddcount = 0;

			int rowCount = 1;
			while(rowCount != (stTTUM_Data.size()+stTTUM_DData.size())+1)
			{
				SXSSFRow aRow = sheet.createRow(rowCount++);

				if(rowCount%2 == 0)
				{
					stTTUM_DRecords = stTTUM_DData.get(evencount);
					
					for(int j = 0 ; j < stTTUM_DRecords.size(); j++)
					{
						aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
					}
					evencount++;
				}
				else
				{
					stTTUM_Records = stTTUM_Data.get(oddcount);
					for(int j = 0;j<stTTUM_Records.size() ; j++)
					{
						aRow.createCell(j).setCellValue(stTTUM_Records.get(j));

					}
					oddcount++;

				}
			}
		}
		else
		{
			SXSSFRow aRow = sheet.createRow(1);
			aRow.createCell(1).setCellValue("No Records Found.");
		}
		

		workbook1.write(outStream);
        outStream.close();
		generatettum_list.clear();
		response.getOutputStream().flush();
		
		/*rowCount = 1;
		for(int i = 0 ;i < (stTTUM_Data.size()); i++)
		{
			HSSFRow aRow = sheet.createRow(rowCount++);
			
			stTTUM_Records = stTTUM_Data.get(i);
			
			if(rowCount%2 != 0)
			{
				for(int j = 0;j<stTTUM_Records.size() ; j++)
				{
					aRow.createCell(j).setCellValue(stTTUM_Records.get(j));
				
				}
			}
			
		}*/
		
		
		
		/*for (GenerateTTUMBean generateTTUMObj : generatettum_list) {
			
			
			HSSFRow aRow = sheet.createRow(rowCount++);
			aRow.createCell(0).setCellValue(loanOutstandingBean.getEmply_cd());
			aRow.createCell(1).setCellValue(loanOutstandingBean.getEmp_nm());
			aRow.createCell(2).setCellValue(loanOutstandingBean.getLn_no());
			aRow.createCell(3).setCellValue(new Double(loanOutstandingBean.getLoan_cd()));
			aRow.getCell(3).setCellStyle(numberStyle);
			aRow.createCell(4).setCellValue(new Double(loanOutstandingBean.getAmt_snc().toString()));
			aRow.getCell(4).setCellStyle(numberStyle);
			aRow.createCell(5).setCellValue(new Double(loanOutstandingBean.getOutstanding_bal().toString()));
			
			aRow.createCell(6).setCellValue((loanOutstandingBean.getSnc_dt()));
			
			aRow.createCell(7).setCellValue((loanOutstandingBean.getAdvc_dt()));
			aRow.createCell(8).setCellValue((loanOutstandingBean.getNo_of_inst()));
			aRow.createCell(9).setCellValue((loanOutstandingBean.getRec_till_dt().toString()));
			aRow.createCell(10).setCellValue((loanOutstandingBean.getPdinst()));
			aRow.createCell(11).setCellValue((loanOutstandingBean.getStrt_dt()));
			aRow.createCell(12).setCellValue((loanOutstandingBean.getEnd_dt()));
			aRow.createCell(13).setCellValue((loanOutstandingBean.getRemarks()));
			
		}*/
	}

}