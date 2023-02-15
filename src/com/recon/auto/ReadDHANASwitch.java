package com.recon.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.util.SystemOutLogger;

import com.recon.util.OracleConn;

public class ReadDHANASwitch {

	//public static void main(String[] a)
	public static void main(String[] a)
	{
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readDHANASwitch();
		int start_pos = 0;
		int lineNumber = 0;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		
		
		String InsertQuery = "INSERT INTO SWITCH_DHANA_RAWDATA_TEMP(ACQUIRERID, ISSUERID, TRAN_TYPE, FROM_ACCOUNT_TYPE, TO_ACCOUNT_TYPE, RRN, RESPONSE_CODE, PAN, APPROVAL_NO, TRACE, CALENDER_YEAR, TRAN_DATE, TRAN_TIME, MCC, CARD_ACCEPTOR_ID, CARD_ACCEPTOR_TERM_ID, CARD_ACCEPTOR_TERM_LOC, AQUIRER_ID, ACCOUNT_NUM, TRAN_CURRENCY, ISSUER_CURRENCY, TRAN_AMOUNT, ACTUAL_TRAN_AMOUNT, BLL_CRR, CH_AMOUNT, SETTLEMENT_DATE, RESPCODE, REVCODE, CREATEDBY, FILEDATE) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";
		
		
		
		try
		{
			System.out.println("Enter file date ");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			String filedate = reader.readLine();
			
			if(!filedate.contains("/"))
			{
				System.out.println("Enter proper date ");
				System.exit(1);
			}
			/*System.out.println("Enter File Path ");
			
			String filePath = reader.readLine();*/
			
			OracleConn oracObj = new OracleConn();
			Connection conn = oracObj.getconn();
			PreparedStatement ps = conn.prepareStatement(InsertQuery);
			
			File file = new File("D:\\BHAGYASHREE\\DHANALAXMI DATA\\DHANALAXMI\\SWITCH\\dlb310122.txt");
			//File file = new File(filePath);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);			
			//conn.setAutoCommit(false);
			
			System.out.println("File Reading Starts");
			while((stLine = br.readLine()) != null)
			{
				sr_no = 1;
				batchExecuted = false;
				start_pos = 0;
				
					lineNumber++;
					for(int i = 0; i < elements.size(); i++)
					{
						String[] data = elements.get(i).split("\\|");
						if(data.length > 2)
						{
							
							{
								start_pos = Integer.parseInt(data[1]);
								//System.out.println("1 sr no is "+(sr_no)+data[0]+" "+stLine.substring((start_pos-1),(Integer.parseInt(data[2]))));


								//System.out.println("1 sr no is "+sr_no);
								ps.setString(sr_no++, stLine.substring((start_pos-1),(Integer.parseInt(data[2]))).trim());

								start_pos = start_pos+Integer.parseInt(data[2]);
							}
							
						}
						else
						{
								//System.out.println("2 sr no is  "+sr_no+data[0]+" "+stLine.substring(start_pos, (start_pos+Integer.parseInt(data[1]))));

								//System.out.println("2 sr no is "+sr_no);
						//		ps.setString(sr_no++, stLine.substring(start_pos,(start_pos+Integer.parseInt(data[1]))).trim());

								start_pos  = start_pos + Integer.parseInt(data[1]);
						}
					}
					
					ps.setString(sr_no++, "AUTOJOB");
					ps.setString(sr_no, filedate);
					
					ps.execute();
					//ps.addBatch();
					batchNumber++;
					
					if(batchNumber == 20000)
					{
						executedBatch++;
						System.out.println("Batch Executed is "+executedBatch);
					//	ps.executeBatch();
						batchNumber = 0;
						batchExecuted = true;
					}
			}
			
			if(!batchExecuted)
			{
				executedBatch++;
				System.out.println("Batch Executed of switch ATM file is "+executedBatch);
				//ps.executeBatch();
			}
			
			
			System.out.println("File Reading Completed !!!");
			
			br.close();
			//ps.close();
			
			//entry in tracking table
		}
		catch(Exception e)
		{
			System.out.println("Exception at line "+lineNumber);
			System.out.println("Exception at line "+stLine);
			System.out.println("Exception in ReadDHANASwitchData "+e);
		}
	
	}
}
