package com.recon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.UploadTTUMBean;

public class Read_successfull_tran {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public  static  boolean read_success(UploadTTUMBean uploadBean,
			MultipartFile file) {
    	
		String path = ""; //ADD YOUR PATH HERE
        String fileName = "D:/Card_to_Card/New folder/EP100A.txt";
        String testWord = "FINANCIAL TRANSACTIONS"; //CHANGE THIS IF YOU WANT
        int tLen = testWord.length();
        int wordCntr = 0;
       // String file = path + fileName;
        boolean check;
        boolean first = true;
        boolean second = true;
        boolean third = true;
        boolean fourth = true;
        Statement st;
       
        java.util.Date varDate1=null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		try {
		     varDate1=dateFormat1.parse(uploadBean.getStDate());
		    dateFormat1=new SimpleDateFormat("MM/dd/yyyy");
		    System.out.println("Date :"+dateFormat1.format(varDate1));
		    uploadBean.setStDate(dateFormat1.format(varDate1));
		}catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}
		
		
		
		java.util.Date varDate=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
		     varDate=dateFormat.parse(uploadBean.getStDate());
		    dateFormat=new SimpleDateFormat("ddMMyy");
		    System.out.println("Date :"+dateFormat.format(varDate));
		}catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}
        ArrayList<String> arr=new ArrayList<String>();
        try{
        	OracleConn conn=new OracleConn();
    		Connection con=conn.getconn();
           // FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String strLine;        
            //Read File Line By Line
            while((strLine = br.readLine()) != null){                
                //check to see whether testWord occurs at least once in the line of text
                check = strLine.toLowerCase().contains(testWord.toLowerCase());
                if(check){                    
                    //get the line, and parse its words into a String array
                    String[] lineWords = strLine.split("\\s+");                    
                         
                    
                    while((lineWords[1].equalsIgnoreCase("FINANCIAL"))){  
		            	if (first) {
		            		strLine=br.readLine();
			                  first = false;
			                }
		            	else if (second) {
		            		strLine=br.readLine();
		            		second = false;
			                }
		            	/*else if (third) {
		            		strLine=br.readLine();
		            		third = false;
			                }*/
		            	else {
		            		strLine=br.readLine();
		            	String split_line[]=strLine.split("\\s+");
//		            	/System.out.println(split_line[1].substring(4,20));
		            String part_tran="";
		            String Acc_num="";
		           // String Acc_num2="";
		            	for(int i=1;i<=2;i++)
		            	{
		            		if(i==1)
		            		{
		            			part_tran="D";
		            			Acc_num="99937200010067";
		            		}
		            		else if(i==2)
		            		{
		            			part_tran="C";
		            			Acc_num="99937200010068";
		            		}
		            	String update_query="INSERT INTO CARD_to_CARD_ttum1(ACCOUNT_NUMBER,CURRENCY_CODE,SERVICE_OUTLET,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,ref_num,REF_CURR_CODE,REF_TRAN_AMOUNT   "     
+ ",DCRS_REMARKS) VALUES('"+Acc_num+"','INR','999','"+part_tran+"','"+split_line[6]+"','CARD TO CARD SETT DT"+uploadBean.getStDate()+"','"+split_line[8]+"','INR','"+split_line[6]+"','C2C'||"+dateFormat.format(varDate)+"||ttum_seq.nextval)";
		            	System.out.println(update_query);
		            	con=conn.getconn();
		            	st = con.createStatement();
		    			st.executeUpdate(update_query);
		            	}
		            	wordCntr++;
		            	first=true;
		            	second=true;
		            	third=true;
		            	break;
		            	}
                    }}
                    }                    
                
                    
            System.out.println("total is: " + wordCntr);
        //Close the input stream
        br.close();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
		return true;
	}}
