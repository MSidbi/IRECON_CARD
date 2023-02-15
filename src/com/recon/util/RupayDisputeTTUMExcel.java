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
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.GenerateTTUMBean;

public class RupayDisputeTTUMExcel extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<List<GenerateTTUMBean>> generatettum_list = (List<List<GenerateTTUMBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
		//List<GenerateTTUMBean> ttum_data = new ArrayList<GenerateTTUMBean>();
		/*if(generatettum_list.get(1)!=null)
		{	ttum_data =  generatettum_list.get(1);}
		*/
		List<GenerateTTUMBean> TTUM_SWITCH_Data = new ArrayList<>();
		List<GenerateTTUMBean> TTUM_CBS_Data = new ArrayList<>();
		List<GenerateTTUMBean> TTUM_Other_Data = new ArrayList<>();
		
			TTUM_SWITCH_Data = generatettum_list.get(2);
			TTUM_CBS_Data = generatettum_list.get(3);		
			TTUM_Other_Data = generatettum_list.get(4);
		
		
		//GenerateTTUMBean generateBean = generatettum_list.get(1);
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		response.setContentType("application/vnd.ms-excel");
	//	response.setHeader("Content-disposition", "attachment; filename=TTUM_"+generateBean.getStCategory()+"_REV_RECON_"+strDate+".xls");
		
		if(!generatettum_list.get(0).get(0).getStDate().equals(""))
		{
		
			response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
						+generatettum_list.get(0).get(0).getStDate()+"_"+strDate+".xlsx");
		}
		else
		{
			response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
					+generatettum_list.get(0).get(0).getStDate()+"_"+strDate+".xlsx");
		}
		// create a new Excel sheet
		//HSSFSheet sheet = workbook.createSheet("TTUM");
		OutputStream outStream = response.getOutputStream();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(1000);
		
		

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("SWITCHTTUM");	

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = sheet.createRow(0);
		
		
		
		for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
		//	header.getCell(i).setCellStyle(style);
		}
		int loop_count=1;
		// create data rows
		int inRowCount = 1;
		
		if(TTUM_SWITCH_Data.size() != 0)
		{
			isEmpty = false;
			for(int i = 0; i< TTUM_SWITCH_Data.size(); i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_SWITCH_Data.get(i);

				int k = 0;
				
				if(generateTTUMBeanObj.getPart_tran_type().equals("C")){
					header2.createCell(k).setCellValue(generateTTUMBeanObj.getStCreditAcc());
				}else{
					header2.createCell(k).setCellValue(generateTTUMBeanObj.getStDebitAcc());
				}
				//header2.createCell(k).setCellValue("HELL");
				//System.out.println("generateTTUMBeanObj.getStCreditAcc() "+generateTTUMBeanObj.getStCreditAcc());
				header2.createCell(++k).setCellValue("INR");
				header2.createCell(++k).setCellValue("999");
				//header2.createCell(++k).setCellValue("C");
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getPart_tran_type());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStTran_particulars());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());
				header2.createCell(++k).setCellValue("INR");				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getAccount_repo());

				inRowCount++;


			}

		}
	    
		if(TTUM_CBS_Data.size() != 0)
		{
			SXSSFSheet sheet1 = (SXSSFSheet) workbook1.createSheet("CBSTTUM");
			SXSSFRow header1 = sheet1.createRow(0);			
			for(int i =0 ;i < ExcelHeaders.size(); i++)
			{
				header1.createCell(i).setCellValue(ExcelHeaders.get(i));
			//	header.getCell(i).setCellStyle(style);
			}
			isEmpty = false;
			inRowCount = 1;
			for(int i = 0; i< TTUM_CBS_Data.size(); i++)
			{
				
				SXSSFRow header2 = sheet1.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_CBS_Data.get(i);
				
				int k = 0;
				
				if(generateTTUMBeanObj.getPart_tran_type().equals("D")){
					header2.createCell(k).setCellValue(generateTTUMBeanObj.getStDebitAcc());
				}else{
					header2.createCell(k).setCellValue(generateTTUMBeanObj.getStCreditAcc());
				}
			
				header2.createCell(++k).setCellValue("INR");
				header2.createCell(++k).setCellValue("999");
				//header2.createCell(++k).setCellValue("D");
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getPart_tran_type());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStTran_particulars());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());
				header2.createCell(++k).setCellValue("INR");				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getAccount_repo());

				inRowCount++;


			}

		}		
		
		if(TTUM_Other_Data.size() != 0)
		{
			SXSSFSheet sheet2 = (SXSSFSheet) workbook1.createSheet("OTHER");
			SXSSFRow header2 = sheet2.createRow(0);			
			
				header2.createCell(0).setCellValue("PAN");
				header2.createCell(1).setCellValue("TRACE");
				header2.createCell(2).setCellValue("LOCAL DATE");
				header2.createCell(3).setCellValue("AMOUNT EQUIV");
				header2.createCell(4).setCellValue("FUNCATION_CODE");
				header2.createCell(5).setCellValue("SETTLEMENT_DATE");
				header2.createCell(6).setCellValue("SETTLEMENT_AMOUNT");
				header2.createCell(7).setCellValue("SWITCH_REMARKS");
				header2.createCell(8).setCellValue("CBS_REMARKS");
			
			isEmpty = false;
			inRowCount = 1;
			for(int i = 0; i< TTUM_Other_Data.size(); i++)
			{
				
				SXSSFRow header3 = sheet2.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_Other_Data.get(i);
				
				int k = 0;			
				
				header3.createCell(k).setCellValue(generateTTUMBeanObj.getPan());		
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getTrace());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getLocal_date());				
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getAmount_equiv());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getFuncation_code());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getSettlement_date());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getSettlement_amount());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getSwitch_remarks());
				header3.createCell(++k).setCellValue(generateTTUMBeanObj.getCbs_remarks());
				
				inRowCount++;

			}

		}		
		
		if(isEmpty)
		{
			SXSSFRow aRow = sheet.createRow(1);
			aRow.createCell(1).setCellValue("No Records Found.");
		}
		workbook1.write(outStream);
        outStream.close();
        generatettum_list.clear();
		response.getOutputStream().flush();
		
		
		
	}}
