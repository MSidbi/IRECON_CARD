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
import org.aspectj.apache.bcel.classfile.LineNumber;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import com.recon.auto.Switch_POS;
//import com.recon.auto.AutoFilterNKnockoff;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.model.FisdomFileUploadBean;


/**
 *
 * @author int6261
 */
public class ReadFisdomSwitchFile extends JdbcDaoSupport {

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
	
	public HashMap<String, Object> uploadFisdomSwitchData(FisdomFileUploadBean setupBean,Connection con,MultipartFile file) {
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String INSERT_QUERY = "INSERT INTO SWITCH_FISDOM_RAWDATA(FISDOM_TXN_ID, BANK_TXN_ID, AMOUNT, CUST_ACC_NO, CUST_IFSC, DT_INVESTMENT, SIP,"
				+ " TRANSACTION_STATUS, FILEDATE, CREATEDBY)"+
							"VALUES(?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'DD/MM/YYYY'), ?)";
		
		
		try
		{
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			
			while((stLine = br.readLine()) != null)
			{
				if(!stLine.trim().equalsIgnoreCase(""))
				{
					sr_no = 1;

					if(lineNumber >= 1)
					{
						lineNumber++;
						String[] splitedData = stLine.split("\\|");
						for(int i = 1 ; i <= splitedData.length ; i++)
						{
							ps.setString(sr_no++, splitedData[i-1]);
						}
						ps.setString(sr_no++, setupBean.getFileDate());
						ps.setString(sr_no++, setupBean.getCreatedBy());	

						ps.addBatch();
						batchNumber++;

						if(batchNumber == 1000)
						{
							ps.executeBatch();
							System.out.println("Batch Executed "+(++executedBatch));
						}
					}
					else
					{
						lineNumber++;
					}
				}
			}
				ps.executeBatch();
				
				//UPDATING TRANSACTION STATUS AND DT INVESTMENT AS PER UCO
				String updateRaw = "UPDATE SWITCH_FISDOM_RAWDATA SET TRANSACTION_STATUS = SUBSTR(TRANSACTION_STATUS,1,1)||LOWER(SUBSTR(TRANSACTION_STATUS,2,LENGTH(TRANSACTION_STATUS))),"
						+ "DT_INVESTMENT = REPLACE(TRIM(DT_INVESTMENT),' ','') "
						+ "WHERE FILEDATE = '"+setupBean.getFileDate()+"'";
				
				PreparedStatement upd_pstm = con.prepareStatement(updateRaw);
				upd_pstm.execute();
				
				logger.info("Executing update query completed");
				
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count is "+(lineNumber-1));
	        	return retOutput;
		}
		catch(Exception e)
		{
			System.out.println("Exception in ReadUCOATMSwitchData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	
}
