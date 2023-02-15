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

import com.recon.model.GenerateTTUMBean;

public class GenerateAmexTTUMExcel extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<List<GenerateTTUMBean>> generatettum_list = (List<List<GenerateTTUMBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
		List<GenerateTTUMBean> TTUM_Data = generatettum_list.get(1);
		Date date = new Date();
		
SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
					+generatettum_list.get(0).get(0).getStStart_Date()+"_"
				+generatettum_list.get(0).get(0).getStEnd_Date()+"_"+strDate+".xlsx");
		
		OutputStream outStream = response.getOutputStream();
		
		XSSFWorkbook wb = new XSSFWorkbook();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
		
		SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		
	//	HSSFSheet sheet = workbook.createSheet("TTUM");
		
		//CellStyle style = workbook.createCellStyle();
		/*Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREEN.index);
		style.setFont(font);*/

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = sheet.createRow(0);
		
		
		
		for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
		//	header.getCell(i).setCellStyle(style);
		}
		
		
		int inRowCount = 1;
		if(TTUM_Data.size() != 0)
		{
			
			for(int i =0; i <TTUM_Data.size();i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_Data.get(i);
				
				int j = 0;
				//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
				header2.createCell(j).setCellValue(generateTTUMBeanObj.getStDebitAcc());
				header2.createCell(++j).setCellValue("INR");
				header2.createCell(++j).setCellValue("999");
				header2.createCell(++j).setCellValue("D");
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStTran_particulars());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStCard_Number());
				header2.createCell(++j).setCellValue("INR");
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRemark());
			//}
			inRowCount++;
			header2 = sheet.createRow(inRowCount);
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
			
			inRowCount++;

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
		
		
		
	}
	
}
