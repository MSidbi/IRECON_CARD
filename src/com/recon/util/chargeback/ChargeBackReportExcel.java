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



public class ChargeBackReportExcel extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		@SuppressWarnings("unchecked")
		List<List<ChargeBackBean>> chargeback_list = (List<List<ChargeBackBean>>) map.get("reportChargeBack");
		String filedate = (String) map.get("filedate");
		//String filename = (String) map.get("filename");
		System.out.println(filedate);
		//System.out.println(filename);
		Boolean isEmpty = true;
		List<String> ExcelHeaders = chargeback_list.get(0).get(0).getStExcelHeader();
		
		List<ChargeBackBean> CHARGE_BACK_Data = new ArrayList<>();
		CHARGE_BACK_Data = chargeback_list.get(1);
		
		Date date = new Date();		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");		
		String strDate = sdf.format(date);
		 System.out.println(strDate);

		response.setContentType("application/vnd.ms-excel");
			
		response.setHeader("Content-disposition", "attachment; filename="+"REPORT_CASHNET_CHARGEBACK_"
					+strDate+".xlsx");
		
		// create a new Excel sheet
		//HSSFSheet sheet = workbook.createSheet("TTUM");
		OutputStream outStream = response.getOutputStream();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(1000);	

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("CASHNET_CHARGEBACK_REPORT");	

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = (SXSSFRow) sheet.createRow(0);
		
		
		
		for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
		//	header.getCell(i).setCellStyle(style);
		}
		int loop_count=1;
		// create data rows
		int inRowCount = 1;
		
		if(CHARGE_BACK_Data.size() != 0)
		{
			isEmpty = false;
			for(int i = 0; i< CHARGE_BACK_Data.size(); i++)
			{
				SXSSFRow header2 = (SXSSFRow) sheet.createRow(inRowCount);
				ChargeBackBean bean  = new ChargeBackBean();
				bean = CHARGE_BACK_Data.get(i);
				//bean =  prepareBean(CHARGE_BACK_Data);

				int k = 0;				
				
				header2.createCell(k).setCellValue(bean.getNETWORK());	
				header2.createCell(++k).setCellValue(bean.getRAISED_DATE());
				header2.createCell(++k).setCellValue(bean.getCARD_NUMBER());
				header2.createCell(++k).setCellValue(bean.getRRN());				
				header2.createCell(++k).setCellValue(bean.getSEQ_NUMBER());
				header2.createCell(++k).setCellValue(bean.getACCOUNT_NUMBER());
				header2.createCell(++k).setCellValue(bean.getTRAN_DATE());
				header2.createCell(++k).setCellValue(bean.getTRAN_TIME());
				header2.createCell(++k).setCellValue(bean.getDISPUTED_AMT());				
				header2.createCell(++k).setCellValue(bean.getDISPUTE_REASON());				
				header2.createCell(++k).setCellValue(bean.getDISPUTE_REMARKS());				
				header2.createCell(++k).setCellValue(bean.getACQ_AMT());
				header2.createCell(++k).setCellValue(bean.getRAISED_ID());
				header2.createCell(++k).setCellValue(bean.getSTATUS());	
				header2.createCell(++k).setCellValue(bean.getREMARKS());	
				header2.createCell(++k).setCellValue(bean.getDOWNLOAD_DATE());
				header2.createCell(++k).setCellValue(bean.getDATE_CREATE());				
				header2.createCell(++k).setCellValue(bean.getACQ());
				header2.createCell(++k).setCellValue(bean.getTXN_DATE());
				header2.createCell(++k).setCellValue(bean.getTXN_TIME());				
				header2.createCell(++k).setCellValue(bean.getATM_ID());
				header2.createCell(++k).setCellValue(bean.getTXN_AMOUNT());
				header2.createCell(++k).setCellValue(bean.getADJ_AMOUNT());
				header2.createCell(++k).setCellValue(bean.getACQ_FEE());
				header2.createCell(++k).setCellValue(bean.getISS_FEE());				
				header2.createCell(++k).setCellValue(bean.getISS_FEE_SW());				
				header2.createCell(++k).setCellValue(bean.getADJ_FEE());				
				header2.createCell(++k).setCellValue(bean.getADJ_REF());
				header2.createCell(++k).setCellValue(bean.getADJ_PROOF());
				header2.createCell(++k).setCellValue(bean.getEMV_STATUS());	
				header2.createCell(++k).setCellValue(bean.getUSER_ID());	
				header2.createCell(++k).setCellValue(bean.getIP_ADDRESS());
				header2.createCell(++k).setCellValue(bean.getCHARGEBACK());					
				header2.createCell(++k).setCellValue(bean.getCHARGEBACK_DATE());
				header2.createCell(++k).setCellValue(bean.getREPRESENTMENT());
				header2.createCell(++k).setCellValue(bean.getREPRESENTMENT_DATE());				
				header2.createCell(++k).setCellValue(bean.getPRE_ARBITRATION());
				header2.createCell(++k).setCellValue(bean.getPRE_ARBITRATION_DATE());
				header2.createCell(++k).setCellValue(bean.getPRE_ARBITRATION_REJECT());
				header2.createCell(++k).setCellValue(bean.getPRE_ARBITRATION_REJECT_DATE());
				header2.createCell(++k).setCellValue(bean.getARBITRATION());
				header2.createCell(++k).setCellValue(bean.getARBITRATION_DATE());				
				header2.createCell(++k).setCellValue(bean.getCREDIT_ADJUSTMENT());				
				header2.createCell(++k).setCellValue(bean.getCREDIT_ADJUSTMENT_DATE());				
				header2.createCell(++k).setCellValue(bean.getDEBIT_ADJUSTMENT());
				header2.createCell(++k).setCellValue(bean.getDEBIT_ADJUSTMENT_DATE());
				header2.createCell(++k).setCellValue(bean.getFILEDATE());	
			
				inRowCount++;


			}

		}
		
		if(isEmpty)
		{
			SXSSFRow aRow = (SXSSFRow) sheet.createRow(1);
			aRow.createCell(1).setCellValue("No Records Found.");
		}
		workbook1.write(outStream);
        outStream.close();
        chargeback_list.clear();
		response.getOutputStream().flush();
		
		
	}

	


		}

