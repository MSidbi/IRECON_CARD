package com.recon.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
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

import com.recon.model.Mastercbs_respbean;

public class Mastercard_Iss_Cbs extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<List<Mastercbs_respbean>> generatettum_list = (List<List<Mastercbs_respbean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
/*		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();*/
		List<Mastercbs_respbean> TTUM_Data = generatettum_list.get(1);
		Date date = new Date();
		
SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		/*response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
					+generatettum_list.get(0).get(0).getStStart_Date()+"_"
				+generatettum_list.get(0).get(0).getStEnd_Date()+"_"+strDate+".xls");
		
		HSSFSheet sheet = workbook.createSheet("TTUM");
		
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREEN.index);
		style.setFont(font);*/

		 
		 
		 response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			response.setHeader("Content-disposition", "attachment; filename="+"CYCLE-1_CBS-1"+strDate+".xlsx");
			// create a new Excel sheet
			OutputStream outStream = response.getOutputStream();
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
			
			

	        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		 
		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		
		
		SXSSFRow header = sheet.createRow(0);
		/*
		 * SXSSFRow header3 = sheet1.createRow(0); SXSSFRow header4 =
		 * sheet2.createRow(0);
		 */

		header.createCell(0).setCellValue("ACCOUNT_NUMBER");
		header.createCell(1).setCellValue("CURRENCY_CODE");
		header.createCell(2).setCellValue("SERVICE_OUTLET");
		header.createCell(3).setCellValue("PART_TRAN_TYPE");
		header.createCell(4).setCellValue("TRANSACTION_AMOUNT");
		header.createCell(5).setCellValue("TRANSACTION_PARTICULARS");
		header.createCell(6).setCellValue("REFERENCE_NUMBER");
		header.createCell(7).setCellValue("REF_CURR_CODE");
		header.createCell(8).setCellValue("REF_TRAN_AMOUNT");
		header.createCell(9).setCellValue("REMARKS");
		header.createCell(10).setCellValue("REPORT_CODE");
		
		/*for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
			//header.getCell(i).setCellStyle(style);
		}*/
		
		
		int inRowCount = 1;
		if(TTUM_Data.size() != 0)
		{
			
			for(int i =0; i <TTUM_Data.size();i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				Mastercbs_respbean generateTTUMBeanObj  = new Mastercbs_respbean();
				generateTTUMBeanObj = TTUM_Data.get(i);
				
				int j = 0;
				//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
				
				header2.createCell(j).setCellValue(generateTTUMBeanObj.getAccount_number());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCurrency_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getService_outlet());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPart_tran_type());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getService_outlet());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTransaction_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTransaction_particulars());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getReference_number());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRef_curr_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRef_tran_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRemarks());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getReport_code());
				
				
				
			//}
			inRowCount++;
			/*header2 = sheet.createRow(inRowCount);
			int k = 0;
			//CR Entry
			//for(int j = 0;j<ExcelHeaders.size() ; j++)
			
				//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
				header2.createCell(k).setCellValue(generateTTUMBeanObj.getStCreditAcc());
				//header2.createCell(k).setCellValue("HELL");
				//System.out.println("generateTTUMBeanObj.getStCreditAcc() "+generateTTUMBeanObj.getStCreditAcc());
				header2.createCell(++k).setCellValue("INR");
				header2.createCell(++k).setCellValue("999");
				header2.createCell(++k).setCellValue("C");
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStTran_particulars());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());
				header2.createCell(++k).setCellValue("INR");
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());
			
			inRowCount++;*/

			}
			
		}
		else
		{
				SXSSFRow aRow = sheet.createRow(1);
				aRow.createCell(1).setCellValue("No Records Found.");
		}
		
		workbook1.write(outStream);
	    outStream.close();
					
	}
	
}
