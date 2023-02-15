package com.recon.util;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.recon.model.SettlementTypeBean;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;




@SuppressWarnings("unchecked")
public class GenerateReport extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<SettlementTypeBean> generatettum_list = null;
		generatettum_list= (List<SettlementTypeBean>) map.get("processed_data");
		 String table = (String) map.get("table");
		 String type= (String) map.get("type");
		    OracleConn con = new OracleConn();
			Connection conn  =con.getconn();
		    String result=null;
		//	String splitype[]=null;
			String formTablename=null;
			result=formTablename;
			String getTableName=null;
			String getTable = "select distinct filename from main_filesource where fileid="+table+"";

			PreparedStatement pst=conn.prepareStatement(getTable);
			ResultSet rs=pst.executeQuery();
			
			
			
			while(rs.next())
			{
				getTableName=rs.getString("FILENAME");
			}
			
			//added by INT5779 FOR GETTING CATEGORY
			String getCategory = "SELECT FILE_CATEGORY FROM MAIN_FILESOURCE WHERE FILEID ="+table+"";
			PreparedStatement pstmt =conn.prepareStatement(getCategory);
			ResultSet rset = pstmt.executeQuery();
			String stCategory = "" ;
			while(rset.next())
			{
				stCategory = rset.getString("FILE_CATEGORY");
			}
			
			//splitype=type.split("\\_");
			table="SETTLEMENT"+"_"+getTableName;
			System.out.println(table);
			
		Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename="+type+strDate+".xlsx");
		// create a new Excel sheet
		//HSSFSheet sheet = workbook.createSheet("Report");

		OutputStream outStream = response.getOutputStream();
		
		XSSFWorkbook wb = new XSSFWorkbook();
		
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb,1000);
		 SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		
	/*	List<String> Excel_Headers = generateTTUMBeanObj.getStExcelHeader();
		List<List<String>> stTTUM_Data = generateTTUMBeanObj.getStTTUM_Records();
		List<List<String>> stTTUM_DData = generateTTUMBeanObj.getStTTUM_DRecords();*/
		
		
		
		
		
		/*for(int i = 0; i<Excel_Headers.size() ; i++)
		{
			System.out.println("Header is "+Excel_Headers.get(i));
		}*/	
		
		// create style for header cells
		/*CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		style.setFont(font);*/

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		SXSSFRow header = sheet.createRow(0);
		
		
		if(table.equalsIgnoreCase("settlement_switch")) {
			header.createCell(0).setCellValue("PAN");
			header.createCell(1).setCellValue("TERMID");
			header.createCell(2).setCellValue("TRACE");
			/*header.getCell(0).setCellStyle(style);
			header.getCell(1).setCellStyle(style);
			header.getCell(2).setCellStyle(style);*/
			
		}else if(table.equalsIgnoreCase("settlement_cbs")) {
			
			if(stCategory.equalsIgnoreCase("ONUS"))
			{
			header.createCell(0).setCellValue("ACCOUNT_NUMBER");
			header.createCell(1).setCellValue("CONTRA_ACCOUNT");
			header.createCell(2).setCellValue("TRANDATE");
			header.createCell(3).setCellValue("TRAN_PARTICULAR");
			/*header.getCell(0).setCellStyle(style);
			header.getCell(1).setCellStyle(style);
			header.getCell(2).setCellStyle(style);
			header.getCell(3).setCellStyle(style);*/
			}else if(stCategory.equalsIgnoreCase("AMEX") || stCategory.equalsIgnoreCase("RUPAY"))
			{
				header.createCell(0).setCellValue("FORACID");
				header.createCell(1).setCellValue("CONTRA_ACCOUNT");
				header.createCell(2).setCellValue("TRAN_DATE");
				header.createCell(3).setCellValue("PARTICULAR");
				/*header.getCell(0).setCellStyle(style);
				header.getCell(1).setCellStyle(style);
				header.getCell(2).setCellStyle(style);
				header.getCell(3).setCellStyle(style);*/
			}
		}
		
		
		/*for(int i =0 ;i < Excel_Headers.size(); i++)
		{
			header.createCell(i).setCellValue(Excel_Headers.get(i));
			header.getCell(i).setCellStyle(style);
		}*/
		
		// create data rows
	
		int rowCount = 1;
		int sheetCnt=1;
		if(generatettum_list != null)
		{
			
			
			
			for(SettlementTypeBean bean : generatettum_list)
			{
				
				/*if( rowCount == 10000) {
					
					String sheetname ="Report"+(sheetCnt+1);
					sheet=workbook1.createSheet(sheetname);
					rowCount=1;
					sheetCnt=sheetCnt+1;
					HSSFRow header1 = sheet.createRow(0);
					header1=header;
					
				}*/
				SXSSFRow aRow = sheet.createRow(rowCount++);

 				if(rowCount%2 == 0)
				{
					if(table.equalsIgnoreCase("settlement_switch")) {
						
						aRow.createCell(0).setCellValue(bean.getPan());
						aRow.createCell(1).setCellValue(bean.gettERMID());
						aRow.createCell(2).setCellValue(bean.gettRACE());
						//tRACE
						
					}else if(table.equalsIgnoreCase("settlement_cbs")) {

						if(stCategory.equalsIgnoreCase("ONUS"))
						{
							aRow.createCell(0).setCellValue(bean.getaCCOUNT_NUMBER());
							aRow.createCell(1).setCellValue(bean.getcONTRA_ACCOUNT());
							aRow.createCell(2).setCellValue(bean.gettRANDATE());
							aRow.createCell(3).setCellValue(bean.gettRAN_PARTICULAR());
						}else if(stCategory.equalsIgnoreCase("AMEX") || stCategory.equalsIgnoreCase("RUPAY"))
						{
							aRow.createCell(0).setCellValue(bean.getForacid());
							aRow.createCell(1).setCellValue(bean.getcONTRA_ACCOUNT());
							aRow.createCell(2).setCellValue(bean.getTran_Date());
							aRow.createCell(3).setCellValue(bean.getParticularals());
						}
					}
					
				}
				else
				{
					if(table.equalsIgnoreCase("settlement_switch")) {
						
						aRow.createCell(0).setCellValue(bean.getPan());
						aRow.createCell(1).setCellValue(bean.gettERMID());
						aRow.createCell(2).setCellValue(bean.gettRACE());
						//tRACE
						
					}else if(table.equalsIgnoreCase("settlement_cbs")) {

						if(stCategory.equalsIgnoreCase("ONUS"))
						{

							aRow.createCell(0).setCellValue(bean.getaCCOUNT_NUMBER());
							aRow.createCell(1).setCellValue(bean.getcONTRA_ACCOUNT());
							aRow.createCell(2).setCellValue(bean.gettRANDATE());
							aRow.createCell(3).setCellValue(bean.gettRAN_PARTICULAR());
						}
						else if(stCategory.equalsIgnoreCase("AMEX") || stCategory.equalsIgnoreCase("RUPAY"))
						{
							aRow.createCell(0).setCellValue(bean.getForacid());
							aRow.createCell(1).setCellValue(bean.getcONTRA_ACCOUNT());
							aRow.createCell(2).setCellValue(bean.getTran_Date());
							aRow.createCell(3).setCellValue(bean.getParticularals());
						}
					}
				}
			}
		} else
		{
			SXSSFRow aRow = sheet.createRow(1);
			aRow.createCell(1).setCellValue("No Records Found.");
		}
		
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