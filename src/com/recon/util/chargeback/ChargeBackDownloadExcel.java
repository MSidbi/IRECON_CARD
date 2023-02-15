package com.recon.util.chargeback;

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
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.ChargeBackBean;
import com.recon.model.CashnetChargeBack;



public class ChargeBackDownloadExcel extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		@SuppressWarnings("unchecked")
		List<List<ChargeBackBean>> chargeback_list = (List<List<ChargeBackBean>>) map.get("downloadChargeBack");
		String filedate = (String) map.get("filedate");
		//String filename = (String) map.get("filename");
		System.out.println(filedate);
		//System.out.println(filename);
		Boolean isEmpty = true;
		List<String> ExcelHeaders = chargeback_list.get(0).get(0).getStExcelHeader();
		List<String> ExcelHeadersUnmatched = chargeback_list.get(0).get(0).getExcelHeaders();
		
		List<ChargeBackBean> CHARGE_BACK_Data = new ArrayList<>();
		CHARGE_BACK_Data = chargeback_list.get(1);
		
		List<ChargeBackBean> unmatched_Data = new ArrayList<>();
		unmatched_Data = chargeback_list.get(2);
		
		Date date = new Date();		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		 
		/* SimpleDateFormat sdf1 = new SimpleDateFormat("YYYYMMDD");			
			String filedate1 = sdf1.format(filedate);
			filedate1 = "IDBI"+filedate1;
			 System.out.println(filedate1);*/
		 
		 SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
		 Date date1 = sdf1.parse(filedate);
		 sdf1.applyPattern("DDMMYY");
		 String filedate1 = sdf1.format(date1);
		 System.out.println(filedate1);
		 
		 String filename = "IECB"+filedate1;
		 System.out.println(filename);
				
		response.setContentType("application/vnd.ms-excel");
			
			response.setHeader("Content-disposition", "attachment; filename="+"CASHNET_CHARGEBACK_"
					+strDate+".xlsx");
		
		// create a new Excel sheet
		//HSSFSheet sheet = workbook.createSheet("TTUM");
		OutputStream outStream = response.getOutputStream();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(1000);
		
		

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("CASHNET_CHARGEBACK");	
        SXSSFSheet sheet1 = (SXSSFSheet) workbook1.createSheet("CASHNET_CHARGEBACK_UNMATCHED");	

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = (SXSSFRow) sheet.createRow(0);
		SXSSFRow header1 = (SXSSFRow) sheet1.createRow(0);
		
		
		for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
		//	header.getCell(i).setCellStyle(style);
		}
		
		for(int i =0 ;i < ExcelHeadersUnmatched.size(); i++)
		{
			header1.createCell(i).setCellValue(ExcelHeadersUnmatched.get(i));
		//	header.getCell(i).setCellStyle(style);
		}
		int loop_count=1;
		// create data rows
		int inRowCount = 1;
		
		if(CHARGE_BACK_Data != null)
		{
			isEmpty = false;
			for(int i = 0; i< CHARGE_BACK_Data.size(); i++)
			{
				SXSSFRow header2 = (SXSSFRow) sheet.createRow(inRowCount);
				ChargeBackBean bean  = new ChargeBackBean();
				bean = CHARGE_BACK_Data.get(i);
				int k = 0;	
				String rrn = bean.getRRN();
				String rrn1="";
				if (rrn.length() > 4)
				{
					rrn1 = rrn.substring(rrn.length() - 4);
				}
				else
				{
					rrn1 = rrn;
				}
				SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MMM-yyyy");
				 Date date3 = sdf3.parse(bean.getTRAN_DATE());
				 sdf3.applyPattern("DDMMYY");
				 String trndt1 = "IDB/"+sdf3.format(date3)+"/"+bean.getACQ_AMT()+"/"+(rrn1);
				 System.out.println(trndt1);
				 
				header2.createCell(k).setCellValue(trndt1);
				//if(bean.getCHARGEBACK()!=null){
					header2.createCell(++k).setCellValue("B");
				//}
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
					 Date date2 = sdf2.parse(bean.getTRAN_DATE());
					 sdf2.applyPattern("yyyy/MM/dd");
					 String trndt = sdf2.format(date2);
					 System.out.println(trndt);
					 
				header2.createCell(++k).setCellValue(trndt);
				double adjAmt = (Double.parseDouble(bean.getDISPUTED_AMT()))- (Double.parseDouble(bean.getACQ_AMT()));
				header2.createCell(++k).setCellValue(adjAmt);
				header2.createCell(++k).setCellValue(bean.getRRN());
				header2.createCell(++k).setCellValue(bean.getCARD_NUMBER());
				header2.createCell(++k).setCellValue(filename);

				inRowCount++;


			}

		}
		
		int inRowCount1 = 1;
		if(unmatched_Data != null)
		{
			isEmpty = false;
			for(int i = 0; i< unmatched_Data.size(); i++)
			{
				SXSSFRow header2 = (SXSSFRow) sheet1.createRow(inRowCount1);
				ChargeBackBean bean  = new ChargeBackBean();
				bean = unmatched_Data.get(i);
				//bean =  prepareBean(CHARGE_BACK_Data);

				int k = 0;
								
				header2.createCell(k).setCellValue(bean.getNETWORK());	
				header2.createCell(++k).setCellValue(bean.getRAISED_ID());	
				header2.createCell(++k).setCellValue(bean.getCARD_NUMBER());
				header2.createCell(++k).setCellValue(bean.getRESPONSE_CODE());	
				header2.createCell(++k).setCellValue(bean.getRRN());		
				header2.createCell(++k).setCellValue(bean.getAUTHORIZATION_CODE());	
				header2.createCell(++k).setCellValue(bean.getSEQ_NUMBER());
				header2.createCell(++k).setCellValue(bean.getACCOUNT_NUMBER());
				header2.createCell(++k).setCellValue(bean.getTRAN_DATE());
				header2.createCell(++k).setCellValue(bean.getTRAN_TIME());
				header2.createCell(++k).setCellValue(bean.getMCC());	
				header2.createCell(++k).setCellValue(bean.getTERMINAL_ID());	
				header2.createCell(++k).setCellValue(bean.getTERMINAL_LOCATION());	
				header2.createCell(++k).setCellValue(bean.getCLAIM_DATE());	
				header2.createCell(++k).setCellValue(bean.getARN());	
				header2.createCell(++k).setCellValue(bean.getDISPUTED_AMT());				
				header2.createCell(++k).setCellValue(bean.getDISPUTE_REASON());				
				header2.createCell(++k).setCellValue(bean.getDISPUTE_REMARKS());				
				header2.createCell(++k).setCellValue(bean.getACQ_AMT());
				header2.createCell(++k).setCellValue(bean.getACQ_CURRENCY());	
				header2.createCell(++k).setCellValue(bean.getRAISED_ID());
				header2.createCell(++k).setCellValue(bean.getSTATUS());	
				header2.createCell(++k).setCellValue(bean.getDISPUTE_TYPE());
				header2.createCell(++k).setCellValue(bean.getACTION_DATE());	
				header2.createCell(++k).setCellValue(bean.getREMARKS());
				header2.createCell(++k).setCellValue(bean.getDOWNLOAD_DATE());
				header2.createCell(++k).setCellValue(bean.getDATE_CREATE());			
				header2.createCell(++k).setCellValue(filedate);
				
				inRowCount1++;


			}

		}
		
		
		if(isEmpty)
		{
			SXSSFRow aRow = (SXSSFRow) sheet.createRow(1);
			aRow.createCell(1).setCellValue("No Records Found.");
			
			SXSSFRow aRow1 = (SXSSFRow) sheet1.createRow(1);
			aRow1.createCell(1).setCellValue("No Records Found.");
		}
		workbook1.write(outStream);
        outStream.close();
        chargeback_list.clear();
        unmatched_Data.clear();
		response.getOutputStream().flush();
		
		
	}

	


		}

