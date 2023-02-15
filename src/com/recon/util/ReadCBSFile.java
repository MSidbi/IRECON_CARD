/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;



/**
 *
 * @author int6261
 */
public class ReadCBSFile {
	
	Logger logger =Logger.getLogger(ReadCBSFile.class);
	
	public boolean uploadCBSData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean) {
		logger.info("***** ReadCBSFile.uploadCBSData Start ****");
		String newTargetFile = "CBS43_ATM_MAN_03-05-2017.LST";
				//"CBSC43_ATM_AO_03-05-2017.LST";
				///"CBS43_ATM_MAN_01-03-2017.LST";//"CBSC43_ATM_AO_01-03-2017.txt ";//"CBS703_ACQO_01032017.txt";// "CBS_03032017.txt"; CBSC43_ATM_AO_01-03-2017
		String bnaId = "";
		
		int flag=1,batch=0;
		
		InputStream fis = null;
		boolean readdata = false;
		

		//BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String thisLine = null;  
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			logger.info("Reading data "+new Date().toString());
			//ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT
			 String insert = "INSERT INTO "+sourceBean.getTableName()+" "
			 		+ "("+sourceBean.getTblHeader()+",CREATEDDATE,CREATEDBY,PART_ID,NEXT_TRAN_DATE,DCRS_TRAN_NO,FILEDATE)"
			 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'))";
		     logger.info("insert=="+insert);   
		        PreparedStatement ps = con.prepareStatement(insert);
		        
		        int insrt=0;

			while ((thisLine = br.readLine()) != null) {
								 String []splitarray= null;

				if (thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) {

					readdata = true;

				}	
				if (!(thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) && readdata) {

					 int srl = 1;
					
					
					  splitarray = thisLine.split(Pattern.quote("|"));//Pattern.quote(ftpBean.getDataSeparator())

			            for(int i=0;i<splitarray.length;i++){

			                String value = splitarray[i];
			                if( !value.trim().equalsIgnoreCase("") ){
			                	
			                
 			                ps.setString(srl,value.trim());
 			             //  logger.info(srl+value.trim());
			                
			               

			                ++srl;
			                }else{
			                	
			                	ps.setString(srl,null);
			                	// logger.info(srl+"null");
			                	 ++srl;
			                }

			                
			            }
			            /**** comment 15 and 16 for online file****/
			           /* ps.setString(15, null);
			            ps.setString(16, null);*/
			            ps.setString(17, null);
			            ps.setString(18, setupBean.getCreatedBy());
			            if(setupBean.getFileType().equalsIgnoreCase("ONLINE")){
				            
			            	ps.setInt(19, 1);
			            }else {
			            	
			            	ps.setInt(19, 2);
			            }
			            
			            ps.setString(20, null);
			            ps.setString(21, null);
			           
			          //  logger.info(insert);
			            
			            ps.addBatch();
			            flag++;
						
						if(flag == 20000)
						{
							flag = 1;
						
							ps.executeBatch();
							logger.info("Executed batch is "+batch);
							batch++;
						}
						
			            
			            //insrt = ps.executeUpdate();

				}
				
				logger.info("***** ReadCBSFile.uploadCBSData End ****");
				
			}
			ps.executeBatch();
			 br.close();
		        ps.close();
		        logger.info("Reading data "+new Date().toString());
			return true;

		} catch (Exception ex) {
			
			logger.error(" error in ReadCBSFile.uploadCBSData", new Exception("ReadCBSFile.uploadCBSData",ex));
			return false;
		}
		

	}

    
    public static void main(String []args) {
    	
    	try{
    	OracleConn oracon = new OracleConn();
    	
    	
    	ReadCBSFile cbsFile = new ReadCBSFile();
    	
    	
    	cbsFile.uploadCBSData(null,oracon.getconn(),null,null);
    	
    	}catch(Exception ex) {
    		
    		ex.printStackTrace();
    		System.out.println(ex.toString());
    		
    	}
    	
    }
    
    

}
