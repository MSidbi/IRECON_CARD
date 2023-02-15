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

public class Mastercard_Atm_Dcc extends AbstractExcelView{

	private static final Logger logger = Logger.getLogger(Mastercard_Atm_Dcc.class); 
	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
		List<List<GenerateTTUMBean>> generatettum_list = (List<List<GenerateTTUMBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
		String test=generatettum_list.get(0).get(0).getStEnd_Date();
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
		 
		
		/*CellStyle style = workbook1.createCellStyle();
		Font font = workbook1.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.GREEN.index);
		style.setFont(font);*/

		CellStyle numberStyle = workbook1.createCellStyle();
		numberStyle.setDataFormat(workbook1.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = (SXSSFRow)sheet.createRow(0);
		
		
		
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
				SXSSFRow header2 = (SXSSFRow)sheet.createRow(inRowCount);
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
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getSwicth_serial_num());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getProcessor_a_i());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getProcessor_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTran_date());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTran_time());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPan_length());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPan_num());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getProcessing_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTrace_num());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getMercahnt_type());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPos_entry());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRef_no());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAquirer_i_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTerminal_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getRespcode());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getBrand());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAdvaice_reg_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getIntra_curr_aggrmt_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAuth_id());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCurrency_code());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getImplied_dec_tran());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCompltd_amnt_tran());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCompltd_amnt_d_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCash_back_amnt_l());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCash_back_amnt_d_c_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAccess_fee_l());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAccess_fee_l_d_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCurrency_settlment());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getImplied_dec_settlment());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getConversion_rate());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCompltd_amt_settmnt());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCompltd_amnt_d_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getInter_change_fee());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getInter_change_fee_d_c());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getService_lev_ind());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getResp_code1());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getFiler());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPositive_id_ind());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getAtm_surcharge_free());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCross_bord_ind());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getCross_bord_currency_in());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getVisa_ias());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getReq_amnt_tran());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getFiler1());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getTrace_num_adj());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getFiler2());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getType());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getFiledate_1());
				header2.createCell(++j).setCellValue(generateTTUMBeanObj.getPart_id());
				
				
				
				
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
				SXSSFRow aRow = (SXSSFRow)sheet.createRow(1);
				aRow.createCell(1).setCellValue("No Records Found.");
		}
		
		
		//response.getOutputStream().flush();
		
	workbook1.write(outStream);
    outStream.close();
		
	}catch(Exception e)
	{
		e.printStackTrace();
		logger.error(e.getMessage());
	}
	}
	
}
