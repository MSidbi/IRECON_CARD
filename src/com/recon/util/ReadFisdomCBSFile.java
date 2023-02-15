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
import com.recon.model.FisdomFileUploadBean;


/**
 *
 * @author int6261
 */
public class ReadFisdomCBSFile extends JdbcDaoSupport {

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
	
	public HashMap<String, Object> uploadFisdomCBSData(FisdomFileUploadBean setupBean,Connection con,MultipartFile file) {
		String stLine = null;
		int lineNumber = 0;
		int startLine = 1;
		int sr_no = 1;
		int batchNumber = 0;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		
		String INSERT_QUERY = "INSERT INTO CBS_FISDOM_RAWDATA(SOL_ID, TRAN_AMOUNT, PART_TRAN_TYPE, CMD, DEBIT_ACCOUNT, SNO, POOL_ACCOUNT, POSTED_DATE, TRAN_PARTICULAR, MFUND, TRANSACTION_REMARKS, TRANSACTION_ID, TRAN_DATE, VALUE_DATE,"
				+ "	 FILEDATE, CREATEDBY)"+
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'), ?)";
		
		
		try
		{
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			
			while((stLine = br.readLine()) != null)
			{
				if(!stLine.trim().equals(""))
				{
					sr_no = 1;

					if(startLine > 2)
					{
						lineNumber++;
						startLine++;
						String[] splitedData = stLine.split("\\|");
						for(int i = 1 ; i <= splitedData.length ; i++)
						{
							ps.setString(sr_no++, splitedData[i-1].trim());
						}
						ps.setString(sr_no++, setupBean.getFileDate());
						ps.setString(sr_no++, setupBean.getCreatedBy());	

						ps.addBatch();
						batchNumber++;

						if(batchNumber == 100)
						{
							ps.executeBatch();
							System.out.println("Batch Executed");
						}
					}
					else
					{
						startLine++;
					}
				}
			}
				ps.executeBatch();
				
				System.out.println("startLine "+startLine+" lineNumber "+lineNumber);
				
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count is "+(lineNumber));
	        	return retOutput;
		}
		catch(Exception e)
		{
			System.out.println("Exception in uploadFisdomCBSData "+e);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	
}
