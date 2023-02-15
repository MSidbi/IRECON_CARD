package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ReadSwitchFileVer2{
	/*public static void main(String a[])throws Exception
	{
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		int count = 0 ,batchSize = 20000,batchcount = 0;
		int flag= 0;
		Scanner linesc;
		//temporary using jdbc connection afterwards use quartz job
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.143.136.88:1621:DCRS","RECON_BETA","reconbeta");
		System.out.println("con is "+con);
		String insert = "INSERT INTO SWITCH_RAWDATA_TEMP "
				+ "(MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,FPAN,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FILEDATE) "
				+ "values (?,?,?,?,?"
				+ "		  ,?,?,?,?,?,"
				+ "		  ?,?,?,?,?,"
				+ "		  ?,?,?,?,?,"
				+ "		  ?,?,?,?,?,"
				+ "		  ?,?,?,?, sysdate,?,?"
				+ "      ,?,?, to_date(?,'dd/mm/yyyy'))";
		PreparedStatement ps = con.prepareStatement(insert);
		String full_pan= null;
		try {

			long start = System.currentTimeMillis();
			System.out.println("Started reading file "+start);
			while(itr.hasNext())
			{	
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
				ps.setString(28, null);

				int srl = 1;
				String line = itr.next();

				String []splitarray= null;
				
				splitarray = line.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

				for(int i=0;i<splitarray.length;i++){

					String value = splitarray[i];

					if(value.trim()!=""){
						// ps.setString(srl,value.trim());

						if(i ==1 ) { 

							String update_pan ="";
							full_pan = value.trim();

							if(value.length() <= 16 && value !=null && value.trim()!="" && value.length()>0 ) {

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

				ps.setString(29, "AAAAAAAAAA");
				ps.setString(30, "JOB");
				ps.setString(31, null);
				ps.setString(32, null);
				ps.setString(33, "1");
				ps.setString(34, "03/oct/2020");

				ps.addBatch();

				 ps.addBatch();
		           flag++;
					
					if(flag == 20000)
					{
						flag = 1;
					
						ps.executeBatch();
						//logger.info("Executed batch is "+batch);
						System.out.println("Executed batch is "+batchcount);
						batchcount++;
					}

			}
			ps.executeBatch();
			ps.close();
			long end = System.currentTimeMillis();
			System.out.println("Time taken by scanner is "+(end-start)+" ms");

		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
		System.out.println("completed reading");
		
		
		//StringUtlisTest();
		//customeTest();
		//tokenTest();
		//ScannerTest();
		//SplitTest();
		
		
	}*/
	public static void main(String a[])throws Exception
	{
		System.out.println("Started reading");
		 boolean result = false;
		
		 Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.143.136.88:1621:DCRS","RECON_BETA","reconbeta");
			File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
			 LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
            String insert = "INSERT INTO SWITCH_RAWDATA_TEMP (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,FPAN,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FILEDATE) "
       		+ "values (?,?,?,?,?"
       		+ "		  ,?,?,?,?,?,"
       		+ "		  ?,?,?,?,?,"
       		+ "		  ?,?,?,?,?,"
       		+ "		  ?,?,?,?,?,"
       		+ "		  ?,?,?,?, sysdate,?,?"
       		+ "      ,?,?, to_date(?,'dd/mm/yyyy'))";
       PreparedStatement ps = con.prepareStatement(insert);
       int insrt=0,batch=0;	
       int flag=0;
       String full_pan ="";
       while (itr.hasNext()) {
    	   
           int srl = 1;
           String thisLine = itr.next();
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
           ps.setString(28, null);

           String []splitarray= null;

           splitarray = thisLine.split(Pattern.quote("|"));//ftpBean.getDataSeparator()

           /*      for(int i=0;i<splitarray.length;i++){

              String value = splitarray[i];
              
               if(value.trim()!=""){
              // ps.setString(srl,value.trim());
               
               if(i ==1 ) { 
    			   
    			   String update_pan ="";
    			   full_pan = value.trim();
    			   
    			   if(value.length() <= 16 && value !=null && value.trim()!="" && value.length()>0 ) {
    				 
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


           }*/
           
          // logger.info(thisLine +"-"+insrt);
          ps.setString(29, full_pan);
           ps.setString(30, "JOB");
           ps.setString(31, null);
           ps.setString(32, null);
           ps.setString(33, "1");
           ps.setString(34, "03/NOV/2020");
                     
           ps.addBatch();
           flag++;
			
			if(flag == 20000)
			{
				flag = 1;
			
				ps.executeBatch();
				//logger.info("Executed batch is "+batch);
				System.out.println("Executed batch is "+batch);
				batch++;
			}
       }
       ps.executeBatch();
     
       insrt=1;
      
       ps.close();
       con.close();
       if(insrt>0){
       	
       	//logger.info(insrt);
       	result= true;
       	
       }else {
      
       	result= false;
       }
      
       System.out.println("Reading completed");
       }
	
	public static void tokenTest()throws Exception
	{
		System.out.println("Inside TokenTest");
		long start = System.currentTimeMillis();
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		try {
		while(itr.hasNext())
		{
			String line = itr.next();
			//tokenizer
			StringTokenizer tokens = new StringTokenizer(line, "|");
			while(tokens.hasMoreTokens())
			{
				tokens.nextToken().trim();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time taken by tokenizer is "+(end-start)+" ms");
		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
	}
	
	public static void ScannerTest()throws Exception
	{
		System.out.println("Inside ScannerTest");
		long start = System.currentTimeMillis();
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		try {
		while(itr.hasNext())
		{
			String line = itr.next();
			//scanner test
			Scanner linesc = new Scanner(line).useDelimiter("\\|");
			
			while (linesc.hasNext()) {
				//System.out.println(linesc.next());
				linesc.next().trim();
				
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time taken by scanner is "+(end-start)+" ms");
		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
	}
	
	public static void SplitTest()throws Exception
	{
		System.out.println("Inside splitTest");
		long start = System.currentTimeMillis();
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		try {
		while(itr.hasNext())
		{
			String line = itr.next();
			//scanner test
			String[] a = line.split(Pattern.quote("|"));
			for(int i = 0; i< a.length ; i++)
			{
				a[i].trim();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time taken by split is "+(end-start)+" ms");
		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
	}
	
	public static void StringUtlisTest()throws Exception
	{
		System.out.println("Inside StringUtlisTest");
		long start = System.currentTimeMillis();
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		try {
		while(itr.hasNext())
		{
			String line = itr.next();
			//scanner test
			String[] a = StringUtils.splitByWholeSeparator(line, "|");
			for(int i = 0; i<a.length ; i++)
			{
				a[i].trim();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time taken by StringUtlisTest is "+(end-start)+" ms");
		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
	}
	
	public static void customeTest()throws Exception
	{
		System.out.println("Inside customeTest");
		long start = System.currentTimeMillis();
		File file = new File("\\\\\\\\10.144.74.121\\\\Raw Files\\\\OCT 2020\\\\311020\\\\ISG_31-OCT-2020.txt");
		LineIterator itr = FileUtils.lineIterator(file, "UTF-8");
		try {
		
		while(itr.hasNext())
		{
			String line = itr.next();
			splitStringChList(line);
		}
		long end = System.currentTimeMillis();
		System.out.println("Time taken by customeTest is "+(end-start)+" ms");
		}
		finally
		{
			LineIterator.closeQuietly(itr);
		}
	}
	public static ArrayList<String> splitStringChList(String line)
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<String> words = new ArrayList<String>();
		words.ensureCapacity(line.length()/5);
		char[] strArray = line.toCharArray();
		int i= 0 ;
		for(char c : strArray) {
			if(c == '|') {
				words.add(sb.toString().trim());
				sb.delete(0, sb.length());
			}
			else
			{
				sb.append(c);
				
			}
		}
		return words;
	}

}
