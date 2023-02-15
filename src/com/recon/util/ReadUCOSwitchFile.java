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
public class ReadUCOSwitchFile extends JdbcDaoSupport {

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
	
	public HashMap<String, Object> uploadATMSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readATMSwitch();
		int start_pos = 0;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String InsertQuery = "INSERT INTO SWITCH_UCO_ATM_RAWDATA_temp (RECORD_LENGTH, DATA_LENGTH, DATE_TIME, RECORD_TYPE, AUTH_PPD, TERM_LN, TERM_FIID, TERM_ID, CARD_LN, CARD_FIID, CARD_NUMBER, BRANCH_ID, CODE_INDICATOR_ENVE, MESSAGE_TYPE, AUTH_ORIGINATOR, AUTH_RESPONDER, TRAN_BEGIN_DATE, TIME, AUTH_SEQ_NUMB, TERMINAL_TYPE, ACQU_INST_ID, RECEIVING_INST_ID, TRANSACTION_TYPE, FROM_ACCOUNT, TO_ACCOUNT, AMOUNT1, AMOUNT2, AVAILABLE_BAL, RESPONSE_CODE, TERM_NAME_LOC, TERM_OWNER_NAME, TERM_CITY, STATE, TERM_COUNTRY, ORIG_SEQ_NUMBER, TRAN_ORIGIN_DATE, TRAN_ORIGIN_TIME, CURRENCY_CODE, RRN, CREATEDBY, FILEDATE) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";
		
		String delete_query = "delete from SWITCH_UCO_ATM_RAWDATA_temp";
		
		try
		{
			PreparedStatement del_pst = con.prepareStatement(delete_query);
			del_pst.execute();
			
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			con.setAutoCommit(false);
			
			while((stLine = br.readLine()) != null)
			{
				sr_no = 1;
				batchExecuted = false;
				start_pos = 0;
				
				if(!stLine.contains("\\FSS2.$"))
				{
					lineNumber++;
					for(int i = 0; i < elements.size(); i++)
					{
						String[] data = elements.get(i).split("\\|");
						if(data.length > 2)
						{
							if(data[0].equalsIgnoreCase("Transaction begin date") )
							{
								start_pos = Integer.parseInt(data[1]);
								DateFormat fmt1 = new SimpleDateFormat("yymmDD");
								Date date = fmt1.parse(stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))).trim());
								DateFormat fmt2 = new SimpleDateFormat("dd/mm/yyyy");
								String output = fmt2.format(date);
								//System.out.println("Date time "+output);
								ps.setString(sr_no++ , output);
								start_pos = start_pos+Integer.parseInt(data[2]);
							}
							else if(data[0].equalsIgnoreCase("Date Time") )
							{
								start_pos = Integer.parseInt(data[1]);
								DateFormat fmt1 = new SimpleDateFormat("yyDDD");
								Date date = fmt1.parse(stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))).trim());
								DateFormat fmt2 = new SimpleDateFormat("dd/MM/yyyy");
								String output = fmt2.format(date);
								//System.out.println(output);
								ps.setString(sr_no++ , output);
								start_pos = start_pos+Integer.parseInt(data[2]);
							
							}
							else
							{
								start_pos = Integer.parseInt(data[1]);
//								System.out.println("1 sr no is "+sr_no+data[0]+" "+stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))));


								//System.out.println("1 sr no is "+sr_no);
								ps.setString(sr_no++, stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))).trim());

								start_pos = start_pos+Integer.parseInt(data[2]);
							}
							
						}
						else
						{
								//System.out.println("2 sr no is  "+sr_no+data[0]+" "+stLine.substring(start_pos, (start_pos+Integer.parseInt(data[1]))));

								//System.out.println("2 sr no is "+sr_no);
								ps.setString(sr_no++, stLine.substring(start_pos,(start_pos+Integer.parseInt(data[1]))).trim());

								start_pos  = start_pos + Integer.parseInt(data[1]);
						}
					}
					
					ps.setString(sr_no++, setupBean.getCreatedBy());
					ps.setString(sr_no, setupBean.getFileDate());
					
					ps.addBatch();
					batchNumber++;
					
					if(batchNumber == 20000)
					{
						executedBatch++;
						System.out.println("Batch Executed is "+executedBatch);
						ps.executeBatch();
						batchNumber = 0;
						batchExecuted = true;
					}
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
			
			
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count of switch ATM file is "+lineNumber);
	        	return retOutput;
		}
		catch(Exception e)
		{
			logger.info("Exception in ReadUCOATMSwitchData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	public HashMap<String, Object> uploadPOSSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {

		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readPOSSwitch();
		int start_pos = 0;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String InsertQuery = "INSERT INTO switch_uco_pos_rawdata_temp(RECORD_LENGTH, DATA_LENGTH, DATA_RECORD, DATE_TIME, RECORD_TYPE, LOGICAL_NW, CARD_FIID, CARD_NUMBER, MEMBER_NUMBER, NETWORK, INSTITUTION_FIID, RETAILER_GRP, RETAILER_RG_GRP, RETAILER_ID, TERMINAL_ID, SIFT_NUMBER, TRAN_OCCURRED_TIM, INFORMATION_CODE, USER_DATA, MSG_TYPE, STATUS_CODE, AUTH_ORIG, AUTH_RESP, ISSUER_CODE, AUTH_SEQ, TERM_LOCATION, TERMINAL_OWNER_NAME, TERMINAL_CITY, TERM_STATE, TERM_COUNTRY, ACQ_INST_ID, REC_INST_ID, TERM_TYPE, SIC_CODE, AUTH_TRAN_CODE, TRANSACTION_CATEGORY, CART_TYPE, ACCOUNT_NUMBER, RESP_CODE, AMOUNT1, AMOUNT2, TRACK_2, PRE_AUTH_SEQ_NO, INVOICE_NUMBER, ORIG_INVOICE_NUM, AUTH_INDICATOR, APPROVAL_CODE, APPROVAL_CODE_LEN, CAPTURE_NUMBER, REVERSAL_REASON, CHARGEBACK_INDICT, OCCURRENCE_OF_CHBK, AUTH_CODE, CURRENCY_CODE, CREATEDBY, FILEDATE) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";
		
		String deleteQuery = "DELETE FROM SWITCH_UCO_POS_RAWDATA_temp";
		try
		{
			PreparedStatement del_pst = con.prepareStatement(deleteQuery);
			del_pst.execute();
		
			PreparedStatement ps = con.prepareStatement(InsertQuery);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			con.setAutoCommit(false);
			
			while((stLine = br.readLine()) != null)
			{
				sr_no = 1;
				batchExecuted = false;
				start_pos = 0;
				
				if(!stLine.contains("\\FSS2.$"))
				{
					lineNumber++;
					for(int i = 0; i < elements.size(); i++)
					{
						String[] data = elements.get(i).split("\\|");
						
						if(data.length > 2)
						{
							start_pos = Integer.parseInt(data[1]);
							//System.out.println(data[0]+" "+stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))));
							
							ps.setString(sr_no++ , stLine.substring(start_pos,(start_pos+Integer.parseInt(data[2]))).trim());
							start_pos = start_pos+Integer.parseInt(data[2]);
							
						}
						else
						{
							if(data[0].equalsIgnoreCase("Date Time"))
							{
								
								DateFormat fmt1 = new SimpleDateFormat("yyDDD");
								Date date = fmt1.parse(stLine.substring(start_pos, (start_pos+Integer.parseInt(data[1]))).trim());
								DateFormat fmt2 = new SimpleDateFormat("dd/MM/yyyy");
								String output = fmt2.format(date);
								//System.out.println("Date time "+output);
								ps.setString(sr_no++ , output);
								start_pos  = start_pos + Integer.parseInt(data[1]);
							}
							else
							{
								//System.out.println(data[0]+" "+stLine.substring(start_pos, (start_pos+Integer.parseInt(data[1]))));

								ps.setString(sr_no++ , stLine.substring(start_pos, (start_pos+Integer.parseInt(data[1]))).trim());
								start_pos  = start_pos + Integer.parseInt(data[1]);
							}
						}
					}
					//System.out.println("sr no is "+sr_no);
					ps.setString(sr_no++, setupBean.getCreatedBy());
					ps.setString(sr_no, setupBean.getFileDate());
					
					ps.addBatch();
					batchNumber++;
					
					if(batchNumber == 20000)
					{
						executedBatch++;
						System.out.println("Batch Executed is "+executedBatch);
						ps.executeBatch();
						batchNumber = 0;
						batchExecuted = true;
					}
				}
			}
			
			if(!batchExecuted)
			{
				executedBatch++;
				logger.info("Batch Executed  of switch ATM file is "+executedBatch);
				ps.executeBatch();
			}
			con.commit();
			br.close();
			ps.close();
			
			
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count is "+lineNumber);
	        	return retOutput;
		}
		catch(Exception e)
		{
			logger.info("Exception in  of switch ATM file ReadUCOSwitchData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	
	
}
