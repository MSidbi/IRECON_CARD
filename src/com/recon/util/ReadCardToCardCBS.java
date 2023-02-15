package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;

public class ReadCardToCardCBS {

	
	public boolean uploadDatacardtocardbank(CompareSetupBean Bean, Connection conn,
			MultipartFile file, FileSourceBean sourceBean) throws ClassNotFoundException, SQLException {

		try {

			boolean result = false;

			String path = ""; //ADD YOUR PATH HERE
	      
	     
	        int count = 0;
	        String msgtype=null;
	        String sub_str_frec=null;
	        //String file = "D:\\Master_Card\\MasterCard20170905\\";
	        boolean check = false;
	        String query=null;
	        
	       

	       // OracleConn conn=new OracleConn();
		//	Connection con2=conn.getconn();
			//int count=0;
			 PreparedStatement ps=null;
	        
	        try{
	            //FileInputStream fstream = new FileInputStream(file);
	        	BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	            //BufferedReader br = new BufferedReader(new InputStreamReader(file_path));
	            String strLine;        
	            //Read File Line By Line
	           // String get_data=strLine = br.readLine();
	            
	            query="insert into CARD_2_RAW_DATA(VISA_CARD_NO ,MOBILE_NO ,AMOUNT ,SOL_ID ,DEBIT_ACC ,ACC_NAME ,PAYMENT_ID ,CHANNEL ,DATE_TIME,FILEDATE)" +
	            		" values(?,?,?,?,?,?,?,?,?,(to_date(?,'mm/dd/yyyy')))";
	           
	           // strLine = br.readLine();
	            boolean first = true;
	            boolean second = true;
	            boolean third = true;
	            boolean fourth = true;
	          
	           
	            

	            while((strLine = br.readLine())  != null){  
	            	if (first) {
		                  first = false;
		                }
	            	else if (second) {
	            		second = false;
		                }
	            	else if (third) {
	            		third = false;
		                }
	            	else if (fourth) {
	            		fourth = false;
		                }
	            	else{
	            	sub_str_frec=strLine.substring(0,4).trim();
	            	                  
	                    //get the line, and parse its words into a String array
	                    String[] lineWords = strLine.split("\\s+");                    
	                    msgtype=sub_str_frec;
	                    String visa_card_no=(strLine.substring(5, 24)).trim();
	                    String mobile_no=(strLine.substring(25, 36)).trim();
	                    String amount=(strLine.substring(38, 48)).trim();
	                    String sol_id=(strLine.substring(49, 56)).trim();
	                    String debit_card_acc=(strLine.substring(56, 75)).trim();
	                    String acc_name=(strLine.substring(81, 106)).trim();
	                    String payment_id=(strLine.substring(106, 117)).trim();
	                    String channel=(strLine.substring(118, 128)).trim();
	                    String date_val=(strLine.substring(128, 148)).trim();
	                    
	                   
	                   
	                    ps=conn.prepareStatement(query);
	                    ps.setString(1, visa_card_no);
	                    ps.setString(2, mobile_no);
	                    ps.setString(3, amount);
	                    ps.setString(4, sol_id);
	                    ps.setString(5, debit_card_acc);
	                    ps.setString(6, acc_name);
	                    ps.setString(7, payment_id);
	                    ps.setString(8, channel);
	                    ps.setString(9, date_val);
	                  //  ps.setString(10, fileDate);
	                    ps.setString(10, Bean.getFileDate());
	                    //ps.setString(11, new Date());
	                    
	                    count=count+1;
	                    System.out.println("total is: " + count);
	                    
	                    int result1=ps.executeUpdate();
	                    ps.close();
	                                    
	                  
	           
	            }}
	            
	        //Close the input stream
	            br.close();
	            ps.close();
	        }catch(IndexOutOfBoundsException in)
	        {
	        	in.printStackTrace();
	        	System.out.println("inside arrayindex");
	        	return true;
	        }
	        
	        catch(Exception e){
	        	System.out.println("total is: " + count +"-"+msgtype +"-"+sub_str_frec);
	            e.printStackTrace();
	            System.exit(0);
	            
	        }
	        return true;
		}finally {
			if (conn != null) {

				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}

		}
		

	}	
}
