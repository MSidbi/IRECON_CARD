package com.recon.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.view.document.AbstractExcelView;

public class SettlementTTUMReportExcel extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> map,HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)throws Exception {
		

	//	List<List<Settlement_FinalReportBean>> generatettum_list = (List<List<Settlement_FinalReportBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		//List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
	//	List<Settlement_FinalReportBean> TTUM_Data = generatettum_list.get(1);
		Date date = new Date();
		List<GenerateSettleTTUMBean> reportBean = (List<GenerateSettleTTUMBean>) map.get("generate_ttum");
		 
		 String filedate = (String) map.get("filedate");
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		 
		/* sdf = new SimpleDateFormat("dd-MMM-yyyy");
		 Date fdate =sdf.parse(filedate);
		 sdf = new SimpleDateFormat("ddMMYYYY");
		 sdf.format(fdate);
		 System.out.println("file_date"+sdf.format(fdate));*/
		 
		 //reportBean.setFileDate(sdf.format(fdate));
		// Date fdate= new 
		 
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=Settlement_TTUM.xls");
		
		
		OutputStream outStream = response.getOutputStream();
		
		//Settlement_FinalBean  reportBean  = new  Settlement_FinalBean();
		
		
		
		  workbook = new HSSFWorkbook();
         HSSFSheet sheet = workbook.createSheet("Report");  
		
	

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		
		sheet= createsheet(sheet,reportBean); 
		
		
		
		workbook.write(outStream);
        outStream.close();
       // generatettum_list.clear();
		response.getOutputStream().flush();
		
		
		
	
		
	}

	private HSSFSheet createsheet(HSSFSheet sheet, List<GenerateSettleTTUMBean> reportBean) {
		//HSSFSheet usersheet = sheet;
		
		HSSFRow header = sheet.createRow(0);
		
		
			header.createCell(0).setCellValue("ACCOUNT NUMBER"); 
			header.createCell(1).setCellValue("CURRENCY CODE OF ACCOUNT NUMBER");
			header.createCell(2).setCellValue("SERVICE OUTLET"); 
			header.createCell(3).setCellValue("PART TRAN TYPE"); 
			header.createCell(4).setCellValue("TRANSACTION AMOUNT"); 
			header.createCell(5).setCellValue("TRANSACTION PARTICULAR"); 
			header.createCell(6).setCellValue("REMARKS"); 
			header.createCell(7).setCellValue("REFERENCE CURRENCY CODE"); 
			header.createCell(8).setCellValue("REFERENCE AMOUNT"); 
			header.createCell(9).setCellValue("ACCOUNT REPORT CODE"); 
			
			
			
			for (int i=1;i< reportBean.size();i++) {
				HSSFRow row = sheet.createRow(i);
				row.createCell(0).setCellValue(reportBean.get(i).getACCOUNT_NUMBER());
				row.createCell(1).setCellValue(reportBean.get(i).getCURRENCY_CODE_OF_ACC_NO() );
				row.createCell(2).setCellValue(reportBean.get(i).getSERVICE_OUTLET() );
				row.createCell(3).setCellValue(reportBean.get(i).getPART_TRAN_TYPE() );
				row.createCell(4).setCellValue(reportBean.get(i).getTRANSACTION_AMOUNT());
				row.createCell(5).setCellValue(reportBean.get(i).getTRANSACTION_PARTICULAR());
				row.createCell(6).setCellValue(reportBean.get(i).getREMARKS() );
				row.createCell(7).setCellValue(reportBean.get(i).getREFERENCE_CURRENCY_CODE() );
				row.createCell(8).setCellValue(reportBean.get(i).getREFERENCE_AMOUNT());
				row.createCell(9).setCellValue(reportBean.get(i).getACCOUNT_REPORT_CODE());
				
			}
		
		return sheet;
	}

}
