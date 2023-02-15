/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import com.recon.auto.Switch_POS;
//import com.recon.auto.AutoFilterNKnockoff;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;


/**
 *
 * @author int6261
 */
public class ReadDHANASwitchFile extends JdbcDaoSupport {

	 PlatformTransactionManager transactionManager;
	 Connection con;
	 Statement st;
	public void setTransactionManager() {
		logger.info("***** ReadSwitchFile.setTransactionManager Start ****");
		   try{
		  
			  
		   ApplicationContext context= new ClassPathXmlApplicationContext();
		   context = new ClassPathXmlApplicationContext("/resources/bean.xml");
		 
		   logger.info("in settransactionManager");
		    transactionManager = (PlatformTransactionManager) context.getBean("transactionManager"); 
		   logger.info(" settransactionManager completed");
		   
		   logger.info("***** ReadSwitchFile.setTransactionManager End ****");
		   
		   ((ClassPathXmlApplicationContext) context).close();
		   }catch (Exception ex) {
			   
			   logger.error(" error in ReadSwitchFile.setTransactionManager", new Exception("ReadSwitchFile.setTransactionManager",ex));
		   }
		   
		   
	   }
	
	public HashMap<String, Object> uploadSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean)
	{
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readDHANASwitch();
		int start_pos = 0;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String InsertQuery = "insert into switch_dhana_rawdata_temp(acquirerid, issuerid, tran_type, from_account_type, to_account_type, rrn, response_code, pan, approval_no, trace, calender_year, tran_date, tran_time, mcc, card_acceptor_id, card_acceptor_term_id, card_acceptor_term_loc, aquirer_id, account_num, tran_currency, issuer_currency, tran_amount, actual_tran_amount, bll_crr, ch_amount, settlement_date, respcode, revcode, createdby, filedate) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'))";
		
		String delete_query = "delete from switch_dhana_rawdata_temp";
		
		try
		{
			PreparedStatement del_pst = con.prepareStatement(delete_query);
			del_pst.execute();
			
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			con.setAutoCommit(false);
			
			while((stLine = br.readLine()) != null)
			{
				System.out.println("Linenumber is "+lineNumber);
				sr_no = 1;
				batchExecuted = false;
				start_pos = 0;
				
					lineNumber++;
					for(int i = 0; i < elements.size(); i++)
					{
						String[] data = elements.get(i).split("\\|");
							
								start_pos = Integer.parseInt(data[1]);
								//System.out.println("1 sr no is "+sr_no+data[0]+" "+stLine.substring((start_pos-1),(Integer.parseInt(data[2]))));


								//System.out.println("1 sr no is "+sr_no);
								ps.setString(sr_no++, stLine.substring((start_pos-1),(Integer.parseInt(data[2]))).trim());

							//	start_pos = start_pos+Integer.parseInt(data[2]);
							
					}
					
					ps.setString(sr_no++, setupBean.getCreatedBy());
					ps.setString(sr_no, setupBean.getFileDate());
					
					ps.addBatch();
					batchNumber++;
					
					if(batchNumber == 5000)
					{
						executedBatch++;
						System.out.println("Batch Executed is "+executedBatch);
						ps.executeBatch();
						batchNumber = 0;
						batchExecuted = true;
					}
			}
			
			if(!batchExecuted)
			{
				executedBatch++;
				logger.info("Batch Executed of switch ATM file is "+executedBatch);
				ps.executeBatch();
			}
			con.commit();
			br.close();
			ps.close();
			
			//inserting in main table
			
			
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count of switch is "+lineNumber);
	        	return retOutput;
		}
		catch(Exception e)
		{
			try
			{
			con.rollback();
			}
			catch(Exception ex)
			{
				logger.info("Exception in ReadSwitchData "+e);
				retOutput.put("result", false);
				retOutput.put("msg", "Issue at Line Number "+lineNumber);
	                return retOutput;  
			}
			
			logger.info("Exception in ReadSwitchData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	public HashMap<String, Object> uploadAGSSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean)
	{
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readDHANASwitch();
		int start_pos = 0;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String InsertQuery = "insert into switch_rawdata_ags(tran_date, tran_time, tran_type, tran_type_desc, msg_type, card_no, from_account, to_account, terminal_id, trace_no, rrn, stan, amount_req, amount_rsp, intl_currency, intl_amount_req, intl_amount_rsp, response_code, response_desc, auth_id_rsp, ext_tran_type, ext_tran_type_desc, merchan_type, merchant_type_code, merchant_type_desc, merchant_name, merchant_location, acquiring_bin, card_product, source, location, node, createdby, filedate) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_TO_DATE(?,'%Y/%m/%d'))";
		
		//String delete_query = "delete from SWITCH_DHANA_RAWDATA_temp";
		
		try
		{
			
			
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			Workbook wb = null;
			//	Workbook wb_hssf = null;
				Sheet sheet = null;
				FormulaEvaluator formulaEvaluate = null;
			con.setAutoCommit(false);
			
			int extn = file.getOriginalFilename().indexOf(".");

			/*if (file.getOriginalFilename().substring(extn)
					.equalsIgnoreCase(".XLS")) {
		 
			//	book = new HSSFWorkbook(file.getInputStream()) ;
				wb = new HSSFWorkbook(file.getInputStream());
				sheet = wb.getSheetAt(0);
				formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
			
			} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX"))*/
			{
				 
				// book = new XSSFWorkbook(file.getInputStream());
				wb = new XSSFWorkbook(file.getInputStream());
				sheet = wb.getSheetAt(0);
				formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
			}
			int cellCount = 1;
			for (Row r : sheet) {
				 if (r.getRowNum() > 0) {
					 cellCount = 1;
					 
					 for (Cell c : r) {
						 switch(formulaEvaluate.evaluateInCell(c).getCellType())
						 {
						 	case Cell.CELL_TYPE_STRING:
						 		logger.info("Its a String "+c.getStringCellValue().replace("'", ""));
						 		ps.setString((cellCount++), c.getStringCellValue().replace("'", "").trim());
						 		break;
						 }
					 }
					 if(cellCount>1 )
					 {
						 lineNumber++;
						 ps.setString((cellCount++), setupBean.getCreatedBy());
						 ps.setString((cellCount++), setupBean.getFileDate());
						 ps.execute();
					 }
				 }
			}
			
			con.commit();
			ps.close();
			
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count of switch is "+lineNumber);
	        	return retOutput;
		}
		catch(Exception e)
		{
			try
			{
			con.rollback();
			}
			catch(Exception ex)
			{
				logger.info("Exception in ReadSwitchData "+e);
				retOutput.put("result", false);
				retOutput.put("msg", "Issue at Line Number "+lineNumber);
	                return retOutput;  
			}
			
			logger.info("Exception in ReadSwitchData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	
	
	
}
