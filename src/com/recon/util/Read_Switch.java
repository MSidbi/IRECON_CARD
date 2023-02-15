/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.recon.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;


/**
 *
 * @author int6261
 */
public class Read_Switch extends JdbcDaoSupport {
	

	 PlatformTransactionManager transactionManager;
	
	public void setTransactionManager() {
	    
		   try{
		  
			  
		   ApplicationContext context= new ClassPathXmlApplicationContext();
		   context = new ClassPathXmlApplicationContext("/resources/bean.xml");
		 
		   System.out.println("in settransactionManager");
		    transactionManager = (PlatformTransactionManager) context.getBean("transactionManager"); 
		   System.out.println(" settransactionManager completed");
		   ((ClassPathXmlApplicationContext) context).close();
		   }catch (Exception ex) {
			   
			   ex.printStackTrace();
		   }
		   
		   
	   }
	public boolean uploadSwitchData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {
		 
		/*setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);*/
		try {
			
			 boolean result = false;
		  
	     
	        System.out.println("Database connection established");
	    //    String newTargetFile = "ISG_30-Aug-2017.txt";//"SWT_29032017.txt";
	        String newTargetFile = "ISG_05-Sep-2017.txt";//"SWT_29032017.txt";
	        String bnaId = "";
	        //String seefilename = "E:\\LED\\DCRS\\IDBI_SWITCH_05012017\\" + newTargetFile;
	       // System.out.println("PROCESSING FILE " + seefilename);
	        InputStream fis = null;
	        try {
	        	//\\10.144.143.191\led\DCRS\Raw_Files_29042017\Switch_File_29042017
	        	
	        	//fis = new FileInputStream("D:\\SUSHANT\\ATM-RECON\\" + newTargetFile);
	        	
	        	fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\ATM-CIA\\isg_switch\\" + newTargetFile);
	        	//fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\test\\"+file);
	        } catch (FileNotFoundException ex) {
	            Logger.getLogger(ReadSwitchFile.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	        String thisLine = null;
	        //"+sourceBean.getTableName()+"
	        //MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,NEXT_TRAN_DATE,DCRS_TRAN_NO
	        String insert = "INSERT INTO switch_RAWdata (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FileDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
	        System.out.println("Process started"+ new Date().getTime());
	        PreparedStatement ps = con.prepareStatement(insert);
	        int insrt=0,batch=0;
	        int flag=0;
	        while ((thisLine = br.readLine()) != null) {
	            int srl = 1;

	            String []splitarray= null;

	            splitarray = thisLine.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

	            for(int i=0;i<splitarray.length;i++){

	                String value = splitarray[i];
	               
	                if(value.trim()!=""){
	                ps.setString(srl,value.trim());
	                }else{
	                	
	                	ps.setString(srl,null);
	                }
	             

	                srl = srl+1;


	            }
	            
	           // System.out.println(thisLine +"-"+insrt);
	            ps.setString(28, "NA");
	            ps.setString(29,null);
	            ps.setString(30, null);
	            ps.setString(31, null);
	            ps.setInt(32, 1);
	            ps.setString(33, "05/09/2017");
	            
	           // ps.execute();
	            // System.out.println(thisLine);
	            // System.out.println("SRL" + srl);
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
					System.out.println("Executed batch is "+batch);
					batch++;
				}
	        }
	        ps.executeBatch();
	        /*transactionManager.commit(status);*/
	        insrt=1;
	        System.out.println("Process ended"+ new Date().getTime());
	        br.close();
	        ps.close();
	        
	        if(insrt>0){
	        	
	        	System.out.println(insrt);
	        	result= true;
	        	
	        }else {
	        	System.out.println(insrt);
	        	result= false;
	        }
	        	return result;
	        
	      //  conn.commit();
	        } catch(Exception ex) {

	        	ex.printStackTrace();
	                System.out.println("Exception "+ ex);
	              /*  transactionManager.rollback(status);*/
	                return false;
	            }
		
		

	}
	
	public boolean uploadData(Connection con,String filename,String fileDate) {
		 
		
		try {
			
			 boolean result = false;
		  
	     
	        System.out.println("Database connection established");
	
	       
	        InputStream fis = null;
	        try {
	        	
	        	
	        	fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\ATM-CIA\\isg_switch" + filename);
	        } catch (FileNotFoundException ex) {
	            Logger.getLogger(ReadSwitchFile.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	        String thisLine = null;
	        //"+sourceBean.getTableName()+"
	        //MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,NEXT_TRAN_DATE,DCRS_TRAN_NO
	        String insert = "INSERT INTO SWITCH_DATA (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FileDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
	        System.out.println("Process started"+ new Date().getTime());
	        PreparedStatement ps = con.prepareStatement(insert);
	        int insrt=0,batch=0;
	        int flag=0;
	        System.out.println( br.readLine());
	        while ((thisLine = br.readLine()) != null) {
	            int srl = 1;

	            String []splitarray= null;

	            splitarray = thisLine.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

	            for(int i=0;i<splitarray.length;i++){

	                String value = splitarray[i];
	               
	                if(value.trim()!=""){
	                ps.setString(srl,value.trim());
	                }else{
	                	
	                	ps.setString(srl,null);
	                }
	             

	                srl = srl+1;


	            }
	            
	           // System.out.println(thisLine +"-"+insrt);
	            ps.setString(28, "NA");
	            ps.setString(29,null);
	            ps.setString(30, null);
	            ps.setString(31, null);
	            ps.setInt(32, 1);
	            ps.setString(33, fileDate);
	            
	           // ps.execute();
	            // System.out.println(thisLine);
	            // System.out.println("SRL" + srl);
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
					System.out.println("Executed batch is "+batch);
					batch++;
				}
	        }
	        ps.executeBatch();
	        /*transactionManager.commit(status);*/
	        insrt=1;
	        System.out.println("Process ended"+ new Date().getTime());
	        br.close();
	        ps.close();
	        
	        if(insrt>0){
	        	
	        	System.out.println(insrt);
	        	result= true;
	        	
	        }else {
	        	System.out.println(insrt);
	        	result= false;
	        }
	        	return result;
	        
	      //  conn.commit();
	        } catch(Exception ex) {

	        	ex.printStackTrace();
	                System.out.println("Exception "+ ex);
	              /*  transactionManager.rollback(status);*/
	                return false;
	            }finally{
	            	if(con!=null){
	            
	            		try {
							con.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
	            	}
	            	
	            }
		
		

	}
	
	
	public static void main (String []args ){
          
		

		Read_Switch switchFile = new Read_Switch();
		FileSourceBean ftpBean = new FileSourceBean();
		
		OracleConn con;
		try {
			con = new OracleConn();
		
		Connection conn  =con.getconn();
		boolean result = switchFile.uploadSwitchData(null,conn,null,null);
		
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
    }

}
