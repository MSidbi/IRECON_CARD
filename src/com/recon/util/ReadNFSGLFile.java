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
import com.recon.model.Act4Bean;
//import com.recon.auto.AutoFilterNKnockoff;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.model.FisdomFileUploadBean;


/**
 *
 * @author int6261
 */
public class ReadNFSGLFile extends JdbcDaoSupport {

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
	
	public HashMap<String, Object> uploadNFSGLData(Act4Bean beanObj,Connection con,MultipartFile file) {
		String stLine = null;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0;
		boolean readingBlock = false;
		HashMap<String, Object> retOutput = new HashMap<String, Object>();
		int fileLine = 1;
		//String gl_acc_no = "";
		
		/*String INSERT_QUERY = "INSERT INTO GL_FISDOM_RAWDATA(TXN_DATE, TXN_TYPE, PARTICULARS, CHQ_NO, WITHDRAWALS, DEPOSITS, BALANCE, CR_DR, FILEDATE, CREATEDBY)"
				+" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?)";*/
		String INSERT_QUERY = "INSERT INTO GL_NFS_RAWDATA(GL_DATE, TRAN_ID, PARTICULARS, DEBIT_AMT, CREDIT_AMT, BALANCE, CR_DR, REMARKS, FILEDATE, CREATEDBY, GL_ACCOUNT, SUBCATEGORY)"
				+" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?,?)";
		
		
		try
		{
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			
			while((stLine = br.readLine()) != null)
			{
				fileLine++;
				sr_no = 1;
				if(!stLine.trim().equalsIgnoreCase(""))
				{
					//if(stLine.trim().startsWith("STATEMENT OF ACCOUNT FOR THE PERIOD OF"))
					if(stLine.trim().startsWith("STATEMENT OF ACCOUNT FOR THE PERIOD OF") ||
							stLine.contains("  Date      Id"))
					{
						readingBlock = true;
						continue;
					}
					//else if(stLine.trim().startsWith("Page Total:"))
					else if(stLine.trim().contains("Page Total Credit  :")|| stLine.trim().contains("Limits(S.L.+TODs)")||stLine.trim().contains("Page "))
					{
						readingBlock = false;
					}
					else if(stLine.trim().contains("B/F")|| stLine.trim().contains("Order by GL. Date."))
					{
						continue;
					}
					
					/*if(stLine.trim().startsWith("A/C NO:") && gl_acc_no.equalsIgnoreCase(""))
					{
						gl_acc_no = stLine.substring(44, 60);
						System.out.println("GL Account number "+gl_acc_no);
					}*/

					if(readingBlock)
					{
						
						//if(stLine.trim().contains("------------------") || stLine.trim().startsWith("DATE     PARTICULARS"))
						if(stLine.trim().contains("------------------") || stLine.trim().startsWith("DATE     PARTICULARS")
								|| stLine.trim().startsWith("Order by GL. Date."))
						{
							continue;
						}
						else
						{
							if(stLine.trim().contains("No transactions for this period")) {
								retOutput.put("result", true);
								retOutput.put("msg", "No transactions for this period... ");
						        	return retOutput;
							}
							System.out.println("line is "+stLine);
							System.out.println("Records is ");
							System.out.println(stLine.substring(0, 12).trim());
							System.out.println(stLine.substring(13, 23).trim());
							System.out.println(stLine.substring(40, 80).trim());
							System.out.println(stLine.substring(80, 100).trim());
							System.out.println(stLine.substring(100, 120).trim());
							System.out.println(stLine.substring(120, 143).trim());
							System.out.println(stLine.substring(143, 145).trim());
							if(stLine.length()>146)
								System.out.println(stLine.substring(147, stLine.length()).trim());
							
							
							lineNumber++;						
							ps.setString(sr_no++, stLine.substring(0, 12).trim());
							ps.setString(sr_no++, stLine.substring(13, 23).trim());
							ps.setString(sr_no++, stLine.substring(40, 80).trim());
							ps.setString(sr_no++, stLine.substring(80, 100).trim());
							ps.setString(sr_no++, stLine.substring(100, 120).trim());
							ps.setString(sr_no++, stLine.substring(120, 143).trim());
							ps.setString(sr_no++, stLine.substring(143, 145).trim());
							if(stLine.length()>146)
								ps.setString(sr_no++, stLine.substring(147, stLine.length()).trim());
							else
								ps.setString(sr_no++, "");
							ps.setString(sr_no++, beanObj.getDatepicker());
							ps.setString(sr_no++, beanObj.getCreatedby());
							ps.setString(sr_no++, file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")).trim());
							ps.setString(sr_no++, beanObj.getStSubCategory());
							
							ps.addBatch();
							batchNumber++;
							
							if(batchNumber == 500)
							{
								ps.executeBatch();
								System.out.println("Batch Executed");
							}
							
							
						}
					}
				}
			}
				ps.executeBatch();
				
			retOutput.put("result", true);
			retOutput.put("msg", "Total Count is "+lineNumber);
	        	return retOutput;
		}
		catch(Exception e)
		{
			System.out.println("Exception in uploadFisdomCBSData "+e);
			logger.info("Issue at file line "+fileLine);
			retOutput.put("result", false);
			retOutput.put("msg", "Issue at Line Number "+lineNumber);
                return retOutput;  
		}
	
	}
	
	
}
