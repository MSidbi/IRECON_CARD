package com.recon.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.recon.util.OracleConn;

public class ReadUCOCBSData {

	public static void main(String[] a)
	{
		String stLine = null;
		int lineNumber = 0,sr_no = 1, batchNumber = 0, batchSize = 0;
		boolean batchExecuted = false;
		
		String InsertQuery = "INSERT INTO CBS_UCO_RAWDATA_temp(SOLID, TRAN_AMOUNT, PART_TRAN_TYPE, CARD_NUMBER, RRN, ACC_NUMBER, SYSDT, SYS_TIME, CMD, TRAN_TYPE, DEVICE_ID, TRAN_ID, TRAN_DATE, FEE, SRV_TAX, SRV_CHRG, VISAIND, CREATEDBY, FILEDATE) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_DATE(?,'DD/MM/YYYY'))";
		
		try
		{
			File file = new File("D:\\bhagyashree\\ATMH_01022022.TXT");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			OracleConn oracObj = new OracleConn();
			Connection conn = oracObj.getconn();
			PreparedStatement ps = conn.prepareStatement(InsertQuery);
			
			conn.setAutoCommit(false);
			
			while((stLine = br.readLine()) != null)
			{
				lineNumber++;
				batchExecuted = false;
				sr_no = 1 ;
				
				String[] splitData = stLine.split("\\|");
				
				for(int i = 1; i <= splitData.length ; i++)
				{
					//System.out.println("i "+splitData[i-1]);
					//if(splitData[i-1].trim().equalsIgnoreCase(""))
					{
						if(i != 14)
						{
							//System.out.println("i "+i+" data "+splitData[i-1]);
							ps.setString(sr_no++, splitData[i-1]);
						}
					}
					
				}
				
				//System.out.println(sr_no);
				ps.setString(sr_no++, "INT");
				ps.setString(sr_no++, "06/02/2021");
				
				ps.addBatch();
				batchSize++;
				
				if(batchSize == 10000)
				{
					batchNumber++;
					System.out.println("Batch Executed is "+batchNumber);
					ps.executeBatch();
					batchSize = 0;
					batchExecuted = true;
				}
				
			}
			
			if(!batchExecuted)
			{
				batchNumber++;
				System.out.println("Batch Executed is "+batchNumber);
				ps.executeBatch();
			}
			conn.commit();
			br.close();
			ps.close();
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in ReadUCOSwitchData "+e);
		}
	}
}
