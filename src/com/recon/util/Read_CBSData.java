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
import java.util.regex.Pattern;

import com.recon.model.FileSourceBean;



/**
 *
 * @author int6261
 */
public class Read_CBSData {
	
	public boolean uploadCBSData(FileSourceBean ftpBean,Connection con) {

		String newTargetFile = "CBS43_ATM_MAN_26-06-2017.LST";
				//"CBSC43_ATM_AO_03-05-2017.LST";
				///"CBS43_ATM_MAN_01-03-2017.LST";//"CBSC43_ATM_AO_01-03-2017.txt ";//"CBS703_ACQO_01032017.txt";// "CBS_03032017.txt"; CBSC43_ATM_AO_01-03-2017
		String bnaId = "";
		
		int flag=1,batch=0;
		
		InputStream fis = null;
		boolean readdata = false;
		try {
			
			//\\10.144.143.191\led\DCRS\Raw_Files_29042017\CBS_Files_29042017
			//\\10.144.143.191\led\DCRS\Raw_Files_30042017\CBS_Files_30042017
		//	fis = new FileInputStream("D:\\SUSHANT\\ATM-RECON\\"+ newTargetFile);
			fis = new FileInputStream("C:\\Users\\Int6261\\Desktop\\CBS43_ATM_MAN_26-06-2017\\"+ newTargetFile);
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
			
			 String insert = "INSERT INTO CBS_data "
			 		+ "(ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT,Part_id,FILEDATE,CREATEDDATE)"
			 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";
		        
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
 			             //  System.out.println(srl+value.trim());
			                
			               

			                ++srl;
			                }else{
			                	
			                	ps.setString(srl,null);
			                	// System.out.println(srl+"null");
			                	 ++srl;
			                }

			                
			            }
			            /**** comment 15 and 16 for online file****/
			          
			            ps.setString(17, null);
			            ps.setInt(18, 2);
			            ps.setString(19, "26/06/2017");
			            
			            
			            
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
	
	
	public boolean uploaddata(Connection con,String filename){
		
		
		int flag=1,batch=0;
		
		InputStream fis = null;
		boolean readdata = false;
		try {
			
		
			fis = new FileInputStream("D:\\CBS\\"+ filename);
		} catch (FileNotFoundException ex) {

			System.out.println("Exception" + ex);
			return false;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String thisLine = null;  
		try {
			
			System.out.println("Reading data "+new Date().toString());
			
			 String insert = "INSERT INTO CBS_TESTDATA "
			 		+ "(ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT,Part_id,FILEDATE,CREATEDDATE)"
			 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";
		        
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
 			           
			                
			               

			                ++srl;
			                }else{
			                	
			                	ps.setString(srl,null);
			                	// System.out.println(srl+"null");
			                	 ++srl;
			                }

			                
			            }
			            /**** comment 15 and 16 for online file****/
			          
			            ps.setString(17, null);
			            ps.setInt(18, 1);
			            ps.setString(19, "26/06/2017");
			            
			            
			            
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
		}finally{
			
			if(con!=null){
				
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	
	}

    
    public static void main(String []args) {
    	
    	try{
    	OracleConn oracon = new OracleConn();
    	
    	
    	Read_CBSData cbsFile = new Read_CBSData();
    	
    	
    	cbsFile.uploadCBSData(null,oracon.getconn());
    	
    	}catch(Exception ex) {
    		
    		ex.printStackTrace();
    		System.out.println(ex.toString());
    		
    	}
    	
    }
    
    

}
