/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

//import com.recon.auto.AutoFilterNKnockoff;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;


/**
 *
 * @author int6261
 */
public class ReadSwitchFile extends JdbcDaoSupport {

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
	/*public boolean uploadSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {
		logger.info("***** ReadSwitchFile.uploadSwitchData Start ****");
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		try {
			
			 boolean result = false;
		  
	     
	        logger.info("Database connection established");
	        String newTargetFile = "ISG_03-May-2017.txt";//"SWT_29032017.txt";
	        String bnaId = "";
	        //String seefilename = "E:\\LED\\DCRS\\IDBI_SWITCH_05012017\\" + newTargetFile;
	       // logger.info("PROCESSING FILE " + seefilename);
	        InputStream fis = null;
	        
	    	BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	        String thisLine = null;
	        //"+sourceBean.getTableName()+"
	        //MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,NEXT_TRAN_DATE,DCRS_TRAN_NO
	        String insert = "INSERT INTO "+sourceBean.getTableName()+" ("+sourceBean.getTblHeader()+",CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FILEDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
	        logger.info("insert=="+insert);
	        logger.info("Process started"+ new Date().getTime());
	        
	        PreparedStatement ps = con.prepareStatement(insert);
	        int insrt=0,batch=0;
	        int flag=0;
	        String full_pan ="";
	        while ((thisLine = br.readLine()) != null) {
	            int srl = 1;
	            
	            ps.setString(1, null);
	            ps.setString(2, null);
	            ps.setString(3, null);
	            ps.setString(4, null);
	            ps.setString(5, null);
	            ps.setString(6, null);
	            ps.setString(7, null);
	            ps.setString(8, null);
	            ps.setString(9, null);
	            ps.setString(10, null);
	            ps.setString(11, null);
	            ps.setString(12, null);
	            ps.setString(13, null);
	            ps.setString(14, null);
	            ps.setString(15, null);
	            ps.setString(16, null);
	            ps.setString(17, null);
	            ps.setString(18, null);
	            ps.setString(19, null);
	            ps.setString(20, null);
	            ps.setString(21, null);
	            ps.setString(22, null);
	            ps.setString(23, null);
	            ps.setString(24, null);
	            ps.setString(25, null);
	            ps.setString(26, null);
	            ps.setString(27, null);

	            String []splitarray= null;

	            splitarray = thisLine.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

	            for(int i=0;i<splitarray.length;i++){

	                String value = splitarray[i];
	               
	                if(value.trim()!=""){
	               // ps.setString(srl,value.trim());
	                
	                if(i ==1 ) { 
         			   
         			   String update_pan ="";
         			   full_pan = value.trim();
         			   
         			   if(value.length() <= 16 && value !=null && value.trim()!="" && value.length()>0 ) {
         				   System.out.println(value);
         				    update_pan =  value.substring(0, 6) +"XXXXXX"+ value.substring(value.length()-4);
         				   
         			   }else if (value.length() >= 16 && value !=null && value.trim()!="" && value.length()>0) {
         				   
         				    update_pan =  value.substring(0, 6) +"XXXXXXXXX"+ value.substring(value.length()-4);
         				   
         			   } else {
         				   
         				   update_pan =null;
         			   }
         			   
         			   ps.setString(srl,update_pan);
         			   
         		   }else {
         		   
         			   ps.setString(srl,value.trim());
         		   }
	                
	                }else{
	                	
	                	ps.setString(srl,null);
	                }
	             

	                srl = srl+1;


	            }
	            
	           // logger.info(thisLine +"-"+insrt);
	           
	            ps.setString(28, "NA");
	            ps.setString(29, setupBean.getCreatedBy());
	            ps.setString(30, null);
	            ps.setString(31, null);
	            ps.setString(32, "1");
	            ps.setString(33, setupBean.getFileDate());
	           
	           // ps.execute();
	            // logger.info(thisLine);
	            // logger.info("SRL" + srl);
	           // ps.setInt(1, srl);
	            //ps.setString(2, thisLine);
	            //ps.setString(3, "1");
	            //insrt = ps.executeUpdate();
	            
	            ps.addBatch();
	            flag++;
				
				if(flag == 20000)
				{
					flag = 1;
				
					ps.executeBatch();
					logger.info("Executed batch is "+batch);
					batch++;
				}
	        }
	        ps.executeBatch();
	        transactionManager.commit(status);
	        insrt=1;
	        logger.info("Process ended"+ new Date().getTime());
	        br.close();
	        ps.close();
	        
	        if(insrt>0){
	        	
	        	logger.info(insrt);
	        	result= true;
	        	
	        }else {
	        	logger.info(insrt);
	        	result= false;
	        }
	        
	        logger.info("***** ReadSwitchFile.uploadSwitchData End ****");
	        
	        	return result;
	        
	      //  conn.commit();
	        } catch(Exception ex) {

	        	
	        	logger.error(" error in ReadSwitchFile.uploadSwitchData", new Exception("ReadSwitchFile.uploadSwitchData",ex));
	                transactionManager.rollback(status);
	                return false;
	            }
		
		

	}
	
	*/
	
	public HashMap<String, Object> uploadSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {
		logger.info("***** ReadSwitchFile.uploadSwitchData Start ****");
		/*setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);*/
		String thisLine = null;
		int lineNumber = 0,partialPanRecords = 0;
		HashMap<String, Object> output = new HashMap<String, Object>();
		try {
			
			 boolean result = false;     
	        logger.info("Database connection established");
	        String newTargetFile = "ISG_03-May-2017.txt";//"SWT_29032017.txt";
	        String bnaId = "";
	        //String seefilename = "E:\\LED\\DCRS\\IDBI_SWITCH_05012017\\" + newTargetFile;
	       // logger.info("PROCESSING FILE " + seefilename);
	        InputStream fis = null;
	        
	    	BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	    	/*System.out.println("file is "+file.getOriginalFilename());
	    	File convFile = new File(file.getOriginalFilename());*/
	    	//file.transferTo(convFile);
	    	//LineIterator itr = FileUtils.lineIterator(convFile, "UTF-8");
	    
	    	System.out.println("deleting all data from temp");
	    	String deleteQuery = "DELETE FROM SWITCH_RAWDATA_TEMP";
	    	PreparedStatement ps = con.prepareStatement(deleteQuery);
	        //ps.setString(1, setupBean.getFileDate());
	       
	        ps.execute();
	        logger.info("deleted data from temp table");
	        
	         String insert = "INSERT INTO SWITCH_RAWDATA_TEMP (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,FPAN,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FILEDATE) "
	        		+ "values (?,?,?,?,?"
	        		+ "		  ,?,?,?,?,?,"
	        		+ "		  ?,?,?,?,?,"
	        		+ "		  ?,?,?,?,?,"
	        		+ "		  ?,?,?,?,?,"
	        		+ "		  ?,?,?,?, sysdate,?,?"
	        		+ "      ,?,?, to_date(?,'dd/mm/yyyy'))";
	        logger.info("insert=="+insert);
	        logger.info("Process started"+ new Date().getTime());
	        
	        ps = con.prepareStatement(insert);
	        int insrt=0,batch=0;	
	        int flag=0;
	        String full_pan ="";
	        long start = System.currentTimeMillis();
	        OUTER: while ((thisLine = br.readLine()) != null) {
	        	int srl = 1;
	        	String partialPan = null;
	        	lineNumber++;
	        	if(!thisLine.contains("rows selected.") && thisLine.contains("|"))//Added by INT8624 for handling dummy data from getting inserted in table
	        	{
	        		ps.setString(27, null);
	        		ps.setString(28, null);

	        		String []splitarray= null;

	        		splitarray = thisLine.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

	        		for(int i=0;i<splitarray.length;i++){

	        			String value = splitarray[i];

	        			if(value.trim()!=""){
	        				// ps.setString(srl,value.trim());

	        				if(i ==1 ) { 

	        					String update_pan ="";
	        					full_pan = value.trim();

	        					if(value.length() <= 16 && value !=null && value.trim()!="" && value.length()>0 ) {
	        						if(full_pan.length() < 6)
	        						{
	        							System.out.println("pan Number is not proper "+value);
	        							partialPan = full_pan;
	        						}
	        						else
	        						{
	        							update_pan =  value.substring(0, 6) +"XXXXXX"+ value.substring(value.length()-4);
	        						}

	        					}else if (value.length() >= 16 && value !=null && value.trim()!="" && value.length()>0) {

	        						update_pan =  value.substring(0, 6) +"XXXXXXXXX"+ value.substring(value.length()-4);

	        					} else {

	        						update_pan =null;
	        					}

	        					ps.setString(srl,update_pan);

	        				}
	        				else if(i == 9)
	        				{
	        					if(partialPan!=null)
	        					{
	        						if(value.trim().equalsIgnoreCase("92"))
	        						{
	        							logger.info("partial pan with 92 at lineNumber "+lineNumber);
	        							System.out.println("partial pan with 92 at lineNumber "+lineNumber);
	        							partialPanRecords++;
	        							continue OUTER;
	        						}
	        						else
	        						{

	        							System.out.println("Pan number is not proper");
	        							throw new Exception();

	        						}
	        					}
	        					else
	        					{
	        						ps.setString(srl,value.trim());
	        					}
	        				}
	        				else {

	        					ps.setString(srl,value.trim());
	        				}

	        			}else{

	        				ps.setString(srl,null);
	        			}
	        			srl++;
	        		}

	        		// logger.info(thisLine +"-"+insrt);
	        		ps.setString(29, full_pan);
	        		ps.setString(30, setupBean.getCreatedBy());
	        		ps.setString(31, null);
	        		ps.setString(32, null);
	        		ps.setString(33, "1");
	        		ps.setString(34, setupBean.getFileDate());

	        		ps.addBatch();
	        		flag++;

	        		if(flag == 20000)
	        		{
	        			flag = 1;

	        			ps.executeBatch();
	        			logger.info("Executed batch is "+batch);
	        			batch++;
	        		}
	        	}
	        }
	        ps.executeBatch();
	       
	        long end = System.currentTimeMillis();
	        logger.info("Time taken to insert into raw table is "+(end-start));
	        
	        //inserting in raw table
	        start = System.currentTimeMillis();
	        insert = "INSERT INTO SWITCH_RAWDATA select * from SWITCH_RAWDATA_TEMP WHERE FILEDATE = to_date(?,'dd/mm/yyyy')";
	        ps = con.prepareStatement(insert);
	        ps.setString(1, setupBean.getFileDate());	        
	        ps.execute();
	         end = System.currentTimeMillis();
	        logger.info("Time taken to insert into raw table is "+(end-start));
	        
	        logger.info("deleting switch temp table data");
	        //String deleteQuery = "DELETE FROM SWITCH_RAWDATA_TEMP WHERE FILEDATE = ?";
	        ps = con.prepareStatement(deleteQuery);
	       // ps.setString(1, setupBean.getFileDate());
	        
	        ps.execute();
	        
	        insrt=1;
	        logger.info("Process ended"+ new Date().getTime());
	        br.close();
	        ps.close();
	        
	        if(insrt>0){
	        	
	        	logger.info(insrt);
	        	result= true;
	        	
	        }else {
	        	logger.info(insrt);
	        	result= false;
	        }
	        
	        logger.info("***** ReadSwitchFile.uploadSwitchData End ****");
	        
	        output.put("result", true);
        	output.put("msg", "Partial Pan Record count "+partialPanRecords);
	        	return output;
	        	
	        	//return result;
	        
	      //  conn.commit();
	        } catch(Exception ex) {

	        		logger.error(" error in ReadSwitchFile.uploadSwitchData", new Exception("ReadSwitchFile.uploadSwitchData",ex));
	               /* transactionManager.rollback(status);*/
	        	logger.info("Issue on line number "+lineNumber);
	        	logger.info("Exception on "+thisLine);
	        	output.put("result", false);
	        	output.put("msg", "Issue at Line Number "+lineNumber);
	                return output;  
	            }
		
		

	}
	
	
	
	
	public void Read_SwitchData(String filename) {

		String[] filenameSplit = filename.split("_");
		String fileDate = null;
		DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
		Date filedt;

		try {
			filedt = format.parse(filenameSplit[1]);
			logger.info(filedt);
			format = new SimpleDateFormat("dd/MM/yyyy");
			fileDate = format.format(filedt);

			CompareSetupBean setupBean = new CompareSetupBean();
			setupBean.setCategory("ONUS");
			setupBean.setFileDate(fileDate);
			setupBean.setStFileName("SWITCH");
			setupBean.setInFileId(1);
			//AutoFilterNKnockoff filterationService = new AutoFilterNKnockoff();
			if (chkFlag("UPLOAD_FLAG", setupBean).equalsIgnoreCase("N")) {

				
				OracleConn con = new OracleConn();
				uploadData(con.getconn(), filename, fileDate);
				updatefile(setupBean);
			} else {

				logger.info("File Already Uploaded");
			}

		} catch (Exception e) {

			logger.info("Erro Occured");
			e.printStackTrace();
		}

	}

	public boolean updatefile(CompareSetupBean setupBean) {
	
		try{
		OracleConn conn = new OracleConn();
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag) "
				+ "values ("+setupBean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"'"
						+ ",'Y','N','N','N','N')";
		
		
			con = conn.getconn();
			st = con.createStatement();
			
			st.executeUpdate(query);
			
			
			return true; 
		}catch(Exception ex){
			
	
			logger.info(ex);
			ex.printStackTrace();
			return false;
		}finally{
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	public String chkFlag(String flag, CompareSetupBean setupBean) {

		try {

			ResultSet rs = null;
			String flg = "";
			OracleConn conn = new OracleConn();

			String query = "SELECT "
					+ flag
					+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
					+ setupBean.getFileDate()
					+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
					+ setupBean.getCategory() + "' AND FileId = "
					+ setupBean.getInFileId() + " ";

			query = " SELECT CASE WHEN exists (" + query + ") then (" + query
					+ ") else 'N' end as FLAG from dual";

			con = conn.getconn();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {

				flg = rs.getString(1);
			}

			return flg;

		} catch (Exception ex) {

			ex.printStackTrace();
			return null;

		} finally {

			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	public boolean updateFlag(String flag, CompareSetupBean setupBean) {
		
		try{
		
		OracleConn conn = new OracleConn();
		String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+" "; 
		
		
		con = conn.getconn();
		st = con.createStatement();
		int rowupdate =  st.executeUpdate(query);
		
		if(rowupdate>0) {
			
			
			return true;
			
		}else{
			
			return false;
		}
		}catch(Exception ex){
			
			ex.printStackTrace();
			return false;
		}finally {
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	public boolean uploadData(Connection con, String filename, String fileDate) {

		try {

			boolean result = false;

			logger.info("Database connection established");

			InputStream fis = null;
			try {

				//fis = new FileInputStream("D:\\Switch\\" + filename);
				fis = new FileInputStream(filename);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(ReadSwitchFile.class.getName()).log(
						Level.SEVERE, null, ex);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String thisLine = null;
			// "+sourceBean.getTableName()+"
			// MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,NEXT_TRAN_DATE,DCRS_TRAN_NO
			String insert = "INSERT INTO SWITCH_DATA (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FileDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
			logger.info("Process started" + new Date().getTime());
			PreparedStatement ps = con.prepareStatement(insert);
			int insrt = 0, batch = 0;
			int flag = 0;
			logger.info(br.readLine());
			String full_pan ="";
			while ((thisLine = br.readLine()) != null) {
				int srl = 1;

				String[] splitarray = null;

				splitarray = thisLine.split(Pattern.quote("|"));// ftpBean.getDataSeparator()

				for (int i = 0; i < splitarray.length; i++) {

					String value = splitarray[i];

					if (value.trim() != "") {

			               // ps.setString(srl,value.trim());
			                
			                if(i ==1 ) { 
		         			   
		         			   String update_pan ="";
		         			   full_pan = value.trim();
		         			   
		         			   if(value.length() <= 16 && value !=null && value.trim()!="" && value.length()>0 ) {
		         				  // System.out.println(value);
		         				    update_pan =  value.substring(0, 6) +"XXXXXX"+ value.substring(value.length()-4);
		         				   
		         			   }else if (value.length() >= 16 && value !=null && value.trim()!="" && value.length()>0) {
		         				   
		         				    update_pan =  value.substring(0, 6) +"XXXXXXXXX"+ value.substring(value.length()-4);
		         				   
		         			   } else {
		         				   
		         				   update_pan =null;
		         			   }
		         			   
		         			   ps.setString(srl,update_pan);
		         			   
		         		   }else {
		         		   
		         			   ps.setString(srl,value.trim());
		         		   }
			                
			                
					} else {

						ps.setString(srl, null);
					}

					srl = srl + 1;

				}

				// logger.info(thisLine +"-"+insrt);
				ps.setString(28, "NA");
				ps.setString(29, null);
				ps.setString(30, null);
				ps.setString(31, null);
				ps.setInt(32, 1);
				ps.setString(33, fileDate);

				// ps.execute();
				// logger.info(thisLine);
				// logger.info("SRL" + srl);
				// ps.setInt(1, srl);
				// ps.setString(2, thisLine);
				// ps.setString(3, "1");
				// insrt = ps.executeUpdate();

				ps.addBatch();
				flag++;

				if (flag == 20000) {
					flag = 1;

					ps.executeBatch();
					logger.info("Executed batch is " + batch);
					batch++;
				}
			}
			ps.executeBatch();
			/* transactionManager.commit(status); */
			insrt = 1;
			logger.info("Process ended" + new Date().getTime());
			br.close();
			ps.close();

			if (insrt > 0) {

				logger.info(insrt);
				result = true;

			} else {
				logger.info(insrt);
				result = false;
			}
			return result;

			// conn.commit();
		} catch (Exception ex) {

			ex.printStackTrace();
			logger.info("Exception " + ex);
			/* transactionManager.rollback(status); */
			return false;
		} finally {
			if (con != null) {

				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}	

	public static void main (String []args ){
          
		
		ReadSwitchFile switchFile = new ReadSwitchFile();
		
		if(0<args.length) {
				
				String filename = args[0];
				File file= new File(filename);
				
			}
			
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter a file name: ");
			System.out.flush();
			String filename = scanner.nextLine();
			File file = new File(filename);
			
			System.out.println(file.getName());
			
			if(file.renameTo(new File("D:\\Switch\\" + file.getName()))) {
				
				System.out.println("File Moved Successfully");
				
				switchFile.Read_SwitchData(filename);
				
			}else {
				
				System.out.println("Error Occured while moving file");
			}
			
			
			
			
	    }

}
