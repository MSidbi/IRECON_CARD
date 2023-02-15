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

public class ReadDhanaReconGL {

	//public static void main(String[] a)
	public static void main(String[] a)
	{
		String stLine = null;
		Switch_POS reading = new Switch_POS();
		List<String> elements = reading.readDHANASwitch();
		int start_pos = 0;
		int lineNumber = 0, index = 1;
		int sr_no = 1;
		int batchNumber = 0, executedBatch = 0;
		boolean batchExecuted = false;
		String improperData = null;
		
		
		String InsertQuery = "INSERT INTO cbs_dhana_recon_gl_rawdata(user_branch_code, gl_code, drcr_ind, lcy_amount, value_date, related_reference, related_account, book_date, txn_ref_no, txn_desc, cod_user_id, account_branch_code, filedate, createddate, createdby) "+
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'), sysdate(), ?)";
		
		
		
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
			
			File file = new File("E:\\share folder\\DEBIT CARD RECONCILATION\\24,25 GL Recon\\testing_gl_file.csv");
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
					improperData = "";
					if(lineNumber == 2)
					{
						String[] datas = stLine.split(",");

						System.out.println("data list size "+datas.length);	
						/*if(datas.length == 13)
						{
							improperData =null;
							index =1;
							for(String data: datas)
							{
								if(index == 10 || index == 11)
								{
									if(index == 10)
									{
										improperData = data;
										index++;
										continue;
									}
									else if(index == 11 && improperData != null)
									{
										System.out.println("data "+data.trim());
										ps.setString(sr_no++, improperData+" "+data.trim());
										index++;
									}
									else
									{
										System.out.println("data "+data.trim());
										ps.setString(sr_no++, data.trim());
										index++;
									}
								}
								else
								{
									System.out.println("data "+data.trim());
									ps.setString(sr_no++, data.trim());
									index++;
								}
								
							}
						}*/
						// new code
						if(datas.length > 12)
						{
							improperData =null;
							index =1;
							for(String data: datas)
							{
								if(index > 9)
								{
									if(index == datas.length || index == (datas.length-1))
									{
										if(index == (datas.length-1))
										{
											System.out.println("improper data "+improperData);
											ps.setString(sr_no++, improperData);
											ps.setString(sr_no++, data.trim());
											System.out.println("data "+data.trim());
											index++;
										}
										else
										{
											System.out.println("data "+data.trim());
											ps.setString(sr_no++, data.trim());
											index++;
										}
									}
									else 
									{
										improperData = improperData + data.trim();
										index++;
										continue;
									}
								}
								else
								{
									System.out.println("data "+data.trim());
									ps.setString(sr_no++, data.trim());
									index++;
								}
								
							}
						}
						else
						{
							for(String data: datas)
							{
								System.out.println("data "+data.trim());
								ps.setString(sr_no++, data.trim());
							}
						}

						ps.setString(sr_no++,filedate);
						ps.setString(sr_no++, "int");
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
			}
			
			if(!batchExecuted)
			{
				//executedBatch++;
				System.out.println("Batch Executed of GL Recon file is "+executedBatch);
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
