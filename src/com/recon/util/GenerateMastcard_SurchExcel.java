package com.recon.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.GenerateTTUMBean;

public class GenerateMastcard_SurchExcel extends AbstractExcelView{

	private static final Logger logger = Logger.getLogger(GenerateMastcard_SurchExcel.class); 
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
			
		
		List<List<GenerateTTUMBean>> generatettum_list = (List<List<GenerateTTUMBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
		List<GenerateTTUMBean> TTUM_Data = generatettum_list.get(1);
		Date date = new Date();
		
SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		  response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_"+generatettum_list.get(0).get(0).getM_surch4()+strDate+".xlsx");
			// create a new Excel sheet
			OutputStream outStream = response.getOutputStream();
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
			
			

	        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = sheet.createRow(0);
		
		
		
		for(int i =0 ;i < ExcelHeaders.size(); i++)
		{
			header.createCell(i).setCellValue(ExcelHeaders.get(i));
			//header.getCell(i).setCellStyle(style);
		}
		
		
		int inRowCount = 1;
		if(TTUM_Data.size() != 0)
		{
			
			for(int i =0; i <TTUM_Data.size();i++)
			{
				try{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_Data.get(i);
				
				int j = 0;
				//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
				String dateval=generateTTUMBeanObj.getCreatedt();
				header2.createCell(j).setCellValue(dateval);
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCreatedby());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getFiledate());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getSeg_tran_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getMsgtype());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPan());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTermid());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getLocal_date());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getLocal_time());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPcode());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTrace());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAmount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAcceptorname());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRespcode());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTermloc());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getNew_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTxnsrc());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTxndest());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRevcode());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAmount_equiv());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCh_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getSettlement_date());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getIss_currency_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAcq_currency_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getMerchant_type());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAuthnum());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAcctnum());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTrans_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAcquirer());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPan2());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getIssuer());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRefnum());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCbs_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCbs_contra());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getSettlement_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getSettlement_curr_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCurrency_amount());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCurrency_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getVariation());
				
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

			}catch(Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			
			}}
		}
		else
		{
				SXSSFRow aRow = sheet.createRow(1);
				aRow.createCell(1).setCellValue("No Records Found.");
		}
		
		workbook1.write(outStream);
	    outStream.close();
		response.getOutputStream().flush();
		
	}catch(Exception e)
	{
		e.printStackTrace();
		logger.error(e.getMessage());
	}}
	
}
