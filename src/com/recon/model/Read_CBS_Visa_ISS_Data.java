package com.recon.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.regex.Pattern;

import com.recon.model.FileSourceBean;
import com.recon.util.OracleConn;



/**
 *
 * @author int6261
 */
public class Read_CBS_Visa_ISS_Data {
	
	public boolean uploadCBSData(FileSourceBean ftpBean,Connection con) {

		//String newTargetFile = "CBS703_ACQO_05-09-2017.txt";
		String newTargetFile = "CBS702_ISS0_28-09-2017.TXT";
				
		String bnaId = "";
		
		int flag=1,batch=0;
		
		InputStream fis = null;
		boolean readdata = false;
		try {
			
			//\\10.144.143.191\led\DCRS\Raw_Files_29042017\CBS_Files_29042017
			//\\10.144.143.191\led\DCRS\Raw_Files_30042017\CBS_Files_30042017
		//	fis = new FileInputStream("D:\\SUSHANT\\ATM-RECON\\"+ newTargetFile);
			
		//	fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\ATM-CIA\\isg_switch\\Recon_05-09-2017\\Recon_05-09-2017\\"+ newTargetFile);
			fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\VISA CARD\\Recon_28-09-2017\\Recon_28-09-2017\\"+ newTargetFile);
		} catch (FileNotFoundException ex) {

			System.out.println("Exception" + ex);
			// Logger.getLogger(ReadFileData.class.getName()).log(Level.SEVERE,
			// null, ex);
			return false;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String thisLine = null;  
		try {
			
			System.out.println("Reading data "+new Date().toString());
			
			 /*String insert = "INSERT INTO CBS_AMEX_RAWDATA "
			 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,Part_id,FILEDATE)"
			 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";*/
			
			String insert = "INSERT INTO CBS_VISA_RAWDATA "
			 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,Part_id,FILEDATE)"
			 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
		        
		        PreparedStatement ps = con.prepareStatement(insert);
		        
		        int insrt=0;

			while ((thisLine = br.readLine()) != null) {
				
				 String []splitarray= null;

				if (thisLine.contains("------")) {

					readdata = true;

				}
				if (!(thisLine.contains("-----")) && readdata) {

					 int srl = 1;
					 System.out.println(thisLine);
					
					  splitarray = thisLine.split(Pattern.quote("|"));//Pattern.quote(ftpBean.getDataSeparator())

			            for(int i=0;i<splitarray.length;i++){

			                String value = splitarray[i];
			                if( !value.equalsIgnoreCase("") ){
			                	
			                
 			                ps.setString(srl,value.trim());

			                ++srl;
			                }else{
			                	
			                	ps.setString(srl,null);
			                	// System.out.println(srl+"null");
			                	 ++srl;
			                }

			                
			            }
			            /**** comment 15 and 16 for online file****/
			          
			            
			            ps.setInt(17, 1);
			            ps.setString(18, "28/09/2017");
			            
			            
			            
			          //  System.out.println(insert);
			            
			            ps.addBatch();
			            flag++;
						
						if(flag == 20000)
						{
							flag = 1;
						
							ps.executeBatch();
							System.out.println("Executed batch is "+batch);
							batch++;
						}
						
			            
			            //insrt = ps.executeUpdate();

				}
				
				
				
			}
			ps.executeBatch();
			 br.close();
		        ps.close();
		        System.out.println("Reading data "+new Date().toString());
			return true;

		} catch (Exception ex) {
			
			ex.printStackTrace();

			System.out.println("Exception" + ex);
			return false;
		}
		

	}

    
    public static void main(String []args) {
    	
    	try{
    	OracleConn oracon = new OracleConn();
    	
    	
    	Read_CBS_Visa_ISS_Data cbsFile = new Read_CBS_Visa_ISS_Data();
    	
    	
    	cbsFile.uploadCBSData(null,oracon.getconn());
    	
    	}catch(Exception ex) {
    		
    		ex.printStackTrace();
    		System.out.println(ex.toString());
    		
    	}
    	
    }
    
    

}


