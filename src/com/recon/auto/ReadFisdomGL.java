package com.recon.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.recon.util.OracleConn;

public class ReadFisdomGL {
	
	public static void main(String[] a)
	{
		String gl_acc_no = "";
		String stLine = "";
		boolean readingBlock = false;
		int sr_no = 1, batchNumber = 0;
		/*String INSERT_QUERY = "INSERT INTO GL_FISDOM_RAWDATA(TXN_DATE, TXN_TYPE, PARTICULARS, CHQ_NO, WITHDRAWALS, DEPOSITS, BALANCE, CR_DR, FILEDATE, CREATEDBY)"
				+" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?)";*/
		String INSERT_QUERY = "INSERT INTO GL_FISDOM_RAWDATA(TXN_DATE, PARTICULARS, WITHDRAWALS, DEPOSITS, BALANCE, CR_DR, FILEDATE, CREATEDBY)"
				+" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?)";
		try
		{
			File file = new File("D:\\bhagyashree\\TESTING\\Fisdom\\18700210002193_07JAN  .rpt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			OracleConn oracObj = new OracleConn();
			Connection conn = oracObj.getconn();
			PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
			
			while((stLine = br.readLine()) != null)
			{
				sr_no = 1;
				if(!stLine.trim().equalsIgnoreCase(""))
				{
					if(stLine.trim().startsWith("STATEMENT OF ACCOUNT FOR THE PERIOD OF") ||
							stLine.trim().contains("Debit Amount"))
					{
						readingBlock = true;
						continue;
					}
					else if(stLine.trim().contains("Page Total Credit  :") || stLine.trim().contains("Limits(S.L.+TODs)") ||stLine.trim().contains("Page "))
					{
						readingBlock = false;
					}
					else if(stLine.trim().contains("B/F")|| stLine.trim().contains("Order by GL. Date."))
					{
						continue;
					}
					
					if(stLine.trim().startsWith("A/C NO:") && gl_acc_no.equalsIgnoreCase(""))
					{
						gl_acc_no = stLine.substring(44, 60);
						System.out.println("GL Account number "+gl_acc_no);
					}

					if(readingBlock)
					{
						if(stLine.trim().contains("------------------") || stLine.trim().startsWith("DATE     PARTICULARS")
								|| stLine.trim().startsWith("Order by GL. Date."))
						{
							continue;
						}
						else
						{
													
							/*ps.setString(sr_no++, stLine.substring(0, 12).trim());
							ps.setString(sr_no++, stLine.substring(13, 15).trim());
							ps.setString(sr_no++, stLine.substring(15, 48).trim());
							ps.setString(sr_no++, stLine.substring(50, 69).trim());
							ps.setString(sr_no++, stLine.substring(69, 86).trim());
							ps.setString(sr_no++, stLine.substring(86, 99).trim());
							ps.setString(sr_no++, stLine.substring(99, 115).trim());
							ps.setString(sr_no++, stLine.substring(115, stLine.length()).trim());*/
							System.out.println("line is "+stLine);
							System.out.println("Records is ");
							System.out.println(stLine.substring(0, 12).trim());
							System.out.println(stLine.substring(40, 90).trim());
							System.out.println(stLine.substring(91, 112).trim());
							System.out.println(stLine.substring(112, 134).trim());
							System.out.println(stLine.substring(135, 153).trim());
							System.out.println(stLine.substring(153, 156).trim());
							//System.out.println(stLine.substring(99, 115).trim());
							//System.out.println(stLine.substring(115, stLine.length()).trim());
							
							ps.setString(sr_no++, "17/DEC/2021");
							ps.setString(sr_no++, "INT8624");
							
							//ps.addBatch();
							batchNumber++;
							
							/*if(batchNumber == 500)
							{
								ps.executeBatch();
								System.out.println("Batch Executed");
							}*/
							
							
						}
					}
				}
			}
			//ps.executeBatch();
			System.out.println("File Reading Completed");
			
		}
		catch(Exception e)
		{
			System.out.println("Exception is "+e);
			
		}
	}

}
