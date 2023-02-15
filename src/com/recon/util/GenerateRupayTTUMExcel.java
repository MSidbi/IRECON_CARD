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

public class GenerateRupayTTUMExcel extends AbstractExcelView{

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<List<GenerateTTUMBean>> generatettum_list = (List<List<GenerateTTUMBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
		List<GenerateTTUMBean> ttum_data = new ArrayList<GenerateTTUMBean>();
		if(generatettum_list.get(1)!=null)
		{	ttum_data =  generatettum_list.get(1);}
		
		List<GenerateTTUMBean> TTUM_C_Data = new ArrayList<>();
		List<GenerateTTUMBean> TTUM_D_Data = new ArrayList<>();
		
		if(generatettum_list.size() > 2 ||generatettum_list.size() == 4)
		{
			TTUM_C_Data = generatettum_list.get(2);
			TTUM_D_Data = generatettum_list.get(3);		
		}
		
		
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
		
		//XSSFWorkbook wb = new XSSFWorkbook();
		
		//SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(1000);
		
		

        SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("TTUM");	
		
		/*for(int i = 0; i<Excel_Headers.size() ; i++)
		{
			System.out.println("Header is "+Excel_Headers.get(i));
		}*/	
		
		// create style for header cells
		/*CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
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
		int loop_count=1;
		// create data rows
		int inRowCount = 1;
		/*if(TTUM_D_Data.size()==0 || TTUM_C_Data.size()==0)
		{*/
		if(ttum_data.size() != 0)
		{
			isEmpty = false;
			for(int i = 0; i< ttum_data.size(); i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = ttum_data.get(i);
				double total_amount=0;
                String amount=null;
				//DR entry
				/*for(int j = 0;j<ExcelHeaders.size() ; j++)
				{*/
                if(loop_count==1)
                {
                	for(int m = 0; m< ttum_data.size(); m++)
        			{
					int j = 0;
					//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
					header2.createCell(j).setCellValue(generateTTUMBeanObj.getStCreditAcc());
					header2.createCell(++j).setCellValue("INR");
					header2.createCell(++j).setCellValue("999");
					header2.createCell(++j).setCellValue("C");
					amount=generateTTUMBeanObj.getStAmount();
					total_amount+=Double.valueOf(amount);
					header2.createCell(++j).setCellValue(total_amount);
					//header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
					header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStTran_particulars());
					header2.createCell(++j).setCellValue("INR");
					
					
					header2.createCell(++j).setCellValue(total_amount);
					
					header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRemark());
					header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStCard_Number());
				//}
        			}
                	loop_count++;
                	inRowCount++;
                }
				header2 = sheet.createRow(inRowCount);
				int k = 0;
				//CR Entry
				//for(int j = 0;j<ExcelHeaders.size() ; j++)
				
					//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
					header2.createCell(k).setCellValue(generateTTUMBeanObj.getStDebitAcc());
					//header2.createCell(k).setCellValue("HELL");
					//System.out.println("generateTTUMBeanObj.getStCreditAcc() "+generateTTUMBeanObj.getStCreditAcc());
					header2.createCell(++k).setCellValue("INR");
					header2.createCell(++k).setCellValue("999");
					header2.createCell(++k).setCellValue("D");
					header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
					header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStTran_particulars());
					header2.createCell(++k).setCellValue("INR");
					
					
					header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
					
					header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());
					header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());
				inRowCount++;


			}

		}
		
		if(TTUM_C_Data.size() != 0)
		{
			isEmpty = false;
			for(int i = 0; i< TTUM_C_Data.size(); i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_C_Data.get(i);

				//DR entry
				/*for(int j = 0;j<ExcelHeaders.size() ; j++)
					{*/
				/*	int j = 0;
						//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
						header2.createCell(j).setCellValue(generateTTUMBeanObj.getStDebitAcc());
						header2.createCell(++j).setCellValue("INR");
						header2.createCell(++j).setCellValue("999");
						header2.createCell(++j).setCellValue("D");
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStTran_particulars());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRRNo());
						header2.createCell(++j).setCellValue("INR");
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRemark());*/
				//}
				//inRowCount++;
				//	header2 = sheet.createRow(inRowCount);
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
				header2.createCell(++k).setCellValue("INR");
				
				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());

				inRowCount++;


			}

		}
	    
		if(TTUM_D_Data.size() != 0)
		{
			isEmpty = false;
			for(int i = 0; i< TTUM_D_Data.size(); i++)
			{
				SXSSFRow header2 = sheet.createRow(inRowCount);
				GenerateTTUMBean generateTTUMBeanObj  = new GenerateTTUMBean();
				generateTTUMBeanObj = TTUM_D_Data.get(i);

				//DR entry
				/*for(int j = 0;j<ExcelHeaders.size() ; j++)
					{*/
				/*	int j = 0;
						//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
						header2.createCell(j).setCellValue(generateTTUMBeanObj.getStDebitAcc());
						header2.createCell(++j).setCellValue("INR");
						header2.createCell(++j).setCellValue("999");
						header2.createCell(++j).setCellValue("D");
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStTran_particulars());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRRNo());
						header2.createCell(++j).setCellValue("INR");
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStAmount());
						header2.createCell(++j).setCellValue(generateTTUMBeanObj.getStRemark());*/
				//}
				//inRowCount++;
				//	header2 = sheet.createRow(inRowCount);
				int k = 0;
				//CR Entry
				//for(int j = 0;j<ExcelHeaders.size() ; j++)

				//aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
				header2.createCell(k).setCellValue(generateTTUMBeanObj.getStDebitAcc());
				//header2.createCell(k).setCellValue("HELL");
				//System.out.println("generateTTUMBeanObj.getStCreditAcc() "+generateTTUMBeanObj.getStCreditAcc());
				header2.createCell(++k).setCellValue("INR");
				header2.createCell(++k).setCellValue("999");
				header2.createCell(++k).setCellValue("D");
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStTran_particulars());
				header2.createCell(++k).setCellValue("INR");
				
				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStAmount());
				
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStRemark());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getStCard_Number());
				header2.createCell(++k).setCellValue(generateTTUMBeanObj.getAccount_repo());

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
		
		
		
		
	}

}
