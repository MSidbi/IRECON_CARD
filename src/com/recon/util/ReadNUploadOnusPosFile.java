package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.TCRFile;

public class ReadNUploadOnusPosFile {
	
	Connection con;
	PreparedStatement pstmt ;
	
	
	public boolean validateandUploadFile(String filename,String filePath,String filedate)
	{
		CompareSetupBean setupBean = new CompareSetupBean();
		ReadNUploadOnusPosFile readFile = new ReadNUploadOnusPosFile();
		try
		{
			setupBean.setCategory("POS");
		//	setupBean.setFileDate(final_date);
			//as per sameer sir take file date from user
			setupBean.setFileDate(filedate);

			List<String> files_to_be_upload =  checkUpload(setupBean);
			if(files_to_be_upload.size() == 0)
				return false;
			else
			{
				readFile.readFile(filePath, filedate);
				readFile.updateRecords(files_to_be_upload, setupBean);
				
				
			}
			
				
		}
		catch(Exception e)
		{
			System.out.println("Exception in validateFile "+e);
			return false;
		}
		return true;
		
	}
	
	public void updateRecords(List<String> file_ids,CompareSetupBean setupBean)
	{
		Connection conn = null;
		try
		{
			OracleConn oracon = new OracleConn();
			conn = oracon.getconn();
			PreparedStatement pstm1;
			for(String fileId : file_ids)
			{
			
					String stSubCateg = "";
					int count = 0;
					String GET_SUBCATE = "SELECT FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE FILEID = '"+fileId+"'";
					PreparedStatement stm = conn.prepareStatement(GET_SUBCATE);
					ResultSet rset = stm.executeQuery();
					if(rset.next())
						stSubCateg = rset.getString("FILE_SUBCATEGORY");
	
					String CHECK_DATA = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEID = '"+fileId+"' AND CATEGORY = '"+setupBean.getCategory()+"' " +
										"AND FILE_SUBCATEGORY = '"+stSubCateg+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'";
					stm = conn.prepareStatement(CHECK_DATA);
					rset = stm.executeQuery();
					
					if(rset.next())
					{
						count = rset.getInt(1);
					}
					
					if(count == 0)
					{
						//String UPDATE_QUERY = "UPDATE MAIN_FILE_UPLOAD_DTLS SET UPLOAD_FLAG = 'Y' WHERE FILEID = '"+fileid+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+fileDate+"'";
						String INSERT_DATA = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) "
								+" VALUES('"+fileId+"',TO_DATE('"+setupBean.getFileDate()+"','DD/MM/YYYY'),'AUTOMATION',SYSDATE,'"+setupBean.getCategory()+"','"+stSubCateg+"'," +
								"'Y','N','N','N','N','N','0')";
						stm = conn.prepareStatement(INSERT_DATA);
						stm.execute();	
					}
					else
					{
						String GET_FILE_COUNT ="SELECT FILE_COUNT FROM MAIN_FILE_UPLOAD_DTLS WHERE FILEID = '"+fileId+"' AND " +
									"TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"' AND CATEGORY = '"+setupBean.getCategory()
									+"' AND FILE_SUBCATEGORY = '"+stSubCateg+"'";
						String FINAL_QUERY = "SELECT CASE WHEN EXISTS ("+GET_FILE_COUNT+") THEN ("+GET_FILE_COUNT+") ELSE 0 END AS FLAG FROM DUAL";
						pstm1 = conn.prepareStatement(FINAL_QUERY);
						ResultSet rs = pstm1.executeQuery();
						int filecount = 0;
						while(rs.next())
						{
							filecount = rs.getInt("FLAG");
						}
						
						String UPDATE_QUERY = "UPDATE MAIN_FILE_UPLOAD_DTLS SET UPLOAD_FLAG = 'Y',FILE_COUNT = '1' WHERE FILEID = '"+fileId+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'" +
								 " AND CATEGORY = '"+setupBean.getCategory()+"' AND FILE_SUBCATEGORY = '"+stSubCateg+"'";
						System.out.println("UPDATE QUERY "+UPDATE_QUERY);
						PreparedStatement stm1 = conn.prepareStatement(UPDATE_QUERY);
						stm1.executeUpdate();
					}
					
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception in updateRecords "+e);
		}
		finally
		{
			if(conn != null)
			{
				try
				{
					conn.close();
				}
				catch(SQLException e)
				{
					System.out.println("Error while closing connextion "+e);
				}
			}
		}
		
	}
	
	
	public void readFile(String filepath,String fileDate)
	{
		InputStream fis = null;
		String stTable_Name = "";
		ArrayList<String> Amount_translation = new ArrayList<>();
		Connection conn = null;
		TCRFile tcrFileObj = new TCRFile();
		
		try {
			
			
			fis = new FileInputStream(filepath);
		} 
		catch (FileNotFoundException ex) {
			System.out.println("Exception" + ex);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String thisLine = null;  
		
		try
		{
			OracleConn con = new OracleConn();
			 conn = con.getconn();
			System.out.println("Reading data "+new Date().toString());
			//CHECK WHETHER TABLE IS ALREADY PRESENT 
			//String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'POS_ONUS_INPUT_FILE'";
			PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
			ResultSet rset1 = pstmt1.executeQuery();
			int isPresent = 0;
			
			if(rset1.next())
			{
				//System.out.println("table is present ? "+rset1.getInt(1));
				isPresent = rset1.getInt(1);
			}
			
			if(isPresent == 0)
			{

				//IF NOT THEN CREATE IT
				//CREATE VISA INPUT TABLE
				String CREATE_QUERY = "CREATE TABLE POS_ONUS_INPUT_FILE (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE)," +
						" STRING VARCHAR2(1000 BYTE), DCRS_SEQ_NO VARCHAR2(100 BYTE), FILEDATE DATE)";
				PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);
				pstmt.executeQuery();

			}
			
			//CREATE RAW TABLE FOR VISA
				//GET TABLE NAME FROM MAIN FILESOURCES
			String GET_RAW_TABLE = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'POS' AND FILE_CATEGORY = 'POS' AND FILE_SUBCATEGORY = 'ONUS'";
			pstmt1 = conn.prepareStatement(GET_RAW_TABLE);
			rset1 = pstmt1.executeQuery();
			
			if(rset1.next())
			{
				//System.out.println("TABLE NAME IS "+rset1.getString("TABLENAME"));
				stTable_Name = rset1.getString("TABLENAME");
			}
			
			String stline=br.readLine();
			
			String DATA_INSERT = "INSERT INTO "+stTable_Name +" (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,AUTHORIZATION_CODE,DCRS_SEQ_NO," +
					"FILEDATE,PART_ID) " +
			" VALUES(?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?)";
			PreparedStatement read_stmt =conn.prepareStatement(DATA_INSERT);
			
			String INSERT_33 = "INSERT INTO "+stTable_Name + " (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,REQ_MSGTYPE)" +
					" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?)";
			PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
			
			int count = 0;
			int batch = 1;
			float amt = 0.00f;
			String INSERT_QUERY = "INSERT INTO POS_ONUS_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
			pstmt1 = conn.prepareStatement(INSERT_QUERY);
			
			String Seq_num = "select 'POS'||pos_seq.nextval AS SEQ from dual";
			PreparedStatement pstmt2 = conn.prepareStatement(Seq_num);
			String seq = "";
			
			//GETTING ALL CHARACTER , DIGIT AND SIGN FROM TABLE
			String SIGN_QUERY = "SELECT * FROM VISA_CHAR_TRANSLATION";
			PreparedStatement sign_pstmt = conn.prepareStatement(SIGN_QUERY);
			ResultSet sign_rset = sign_pstmt.executeQuery();
			
			while(sign_rset.next())
			{
				String Data = sign_rset.getString("CHARACTER")+"|"+sign_rset.getInt("LAST_DIGIT")+"|"+sign_rset.getString("SIGN");
			//	System.out.println("check here "+Data);
				Amount_translation.add(Data);
				while(stline != null)
				{
					
					//System.out.println("count is "+count);
					String check_TCR = "";
					String ACC_NO = "";
					String SOURCE_AMT = "";
					String AUTH_CODE = "";
					String TRACE = "";
					String RESPONSE_CODE = "";
					String REFERENCE_NUMBER = "";
					String CARD_NUMBER = "";
					String REQ_MSG_TYPE = "";
					//String TRAN_AMOUNT = "";
					
					List<String> Data_Elements = new ArrayList<>();
					String TC = stline.substring(0, 2);
					String TCR_Code = stline.substring(2,4);
					
					//generating SEQ number
					
					
					
					//INSERT THE DATA IN INPUT TABLE
					
					if(TC.equals("05") || TC.equals("06") || TC.equals("07")|| TC.equals("25") || TC.equals("27"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR050Format();
						}
					}
					else if(TC.equals("10"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR10FeeCollectionFormat();
						}
					}
					else if(TC.equals("20"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR20FundDisbursement();
						}
					}
					else if(TC.equals("33"))//VISA ACQUIRER TRANSACTIONS
					{
						//REMOVE THE HEADER FROM THIS STRING AND THEN STRING
						String stLine = stline.substring(34 , stline.length());
						//System.out.println("NOW STRING IS "+stLine);
						check_TCR = stLine.substring(3, 6);
						//System.out.println("check TCR "+check_TCR);
						if(check_TCR.equals("200"))
						{
							Data_Elements = tcrFileObj.V22200();
						}
						
					}
					
					//READ CARD NUMBER , AMOUNT , AND AUTH CODE FROM INPUT STRING AND INSERT IT IN NEW RAW TABLE
					for(int i= 0 ;i<Data_Elements.size(); i++)
					{
						if((TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27")||TC.equals("10")||TC.equals("20")))
						{
							if(TCR_Code.equals("00"))
							{
								String[] DE =  Data_Elements.get(i).split("\\|");

								//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
								if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
										DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
										DE[0].trim().equalsIgnoreCase("Authorization Code"))
								{
									//System.out.println("DE[0] "+DE[0]);
									//	System.out.println("DE LENGTH IS "+DE.length);
									if(DE.length == 3)
									{
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										int stEnd_pos = Integer.parseInt(DE[2].trim());
										/*System.out.println("start positon "+ststart_Pos);
								System.out.println("End Position "+stEnd_pos);
								System.out.println("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

										if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
										{
											ACC_NO = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT")) {

											SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											amt = (Float.parseFloat(SOURCE_AMT)/100);
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
										{
											//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).trim();
										}



									}
									else if(DE.length == 2)
									{
										int ststart_pos =  Integer.parseInt(DE[1].trim());
										if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
										{
											ACC_NO = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT")) {

											SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
											amt = (Float.parseFloat(SOURCE_AMT)/100);
										}
										else if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
										{
											//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
											AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).trim();
										}

									}
								}
							}
						}
						else if(TC.equals("33"))
						{
							String stLine = stline.substring(34 , stline.length());
							if(check_TCR.equals("200"))
							{
								//GET VARIABLES FOR DECIMAL AND SIGN
								String sign = "";
								String last_Digit = "";
								String[] DE =  Data_Elements.get(i).split("\\|");

								if(DE[0].equalsIgnoreCase("Trace Number")||
										DE[0].equalsIgnoreCase("Response Code")	||
										DE[0].equalsIgnoreCase("Retrieval Reference Number")||
										DE[0].equalsIgnoreCase("Card Number")||
										DE[0].equalsIgnoreCase("Transaction Amount")||
										DE[0].equalsIgnoreCase("Request Message Type"))
								{
									if(DE.length == 3)
									{
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										int stEnd_pos = Integer.parseInt(DE[2].trim());
										/*System.out.println("start positon "+ststart_Pos);
								System.out.println("End Position "+stEnd_pos);
								System.out.println("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

										if(DE[0].equalsIgnoreCase("Trace Number"))
										{
											//TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
										}
										else if(DE[0].equalsIgnoreCase("Request Message Type"))
										{
											REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Response Code")) 
										{
											RESPONSE_CODE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
										{
											REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Card Number"))
										{
											CARD_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Transaction Amount"))
										{
											SOURCE_AMT = stLine.substring((ststart_Pos-1), (stEnd_pos)).replaceAll("^0*","").trim();
											//check for the list of variables provided by visa for decimal position
											for(int j =0; j< Amount_translation.size() ; j++ )
											{
												String[] data =  Amount_translation.get(j).split("\\|");
												if(SOURCE_AMT.contains(data[0]))
												{
													if(data[2].equals("-"))
													{
														SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
													}
													else
													{
														SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
													}
												}
												
											}
											amt = (Float.parseFloat(SOURCE_AMT)/100);
											//System.out.println("amt is "+amt);
										}



									}
									else if(DE.length == 2)
									{
										int ststart_Pos =  Integer.parseInt(DE[1].trim());
										if(DE[0].equalsIgnoreCase("Trace Number"))
										{
											//TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
											TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
										}
										else if(DE[0].equalsIgnoreCase("Response Code")) {

											RESPONSE_CODE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("Request Message Type"))
										{
											REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
										{
											REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Card Number"))
										{
											CARD_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Transaction Amount"))
										{
											SOURCE_AMT = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
											
											for(int j =0; j< Amount_translation.size() ; j++ )
											{
												String[] data =  Amount_translation.get(j).split("\\|");
												if(SOURCE_AMT.contains(data[0]))
												{
													if(data[2].equals("-"))
													{
														SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
													}
													else
													{
														SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
													}
												}
												
											}
											
											amt = (Float.parseFloat(SOURCE_AMT)/100);
										}

									}
								}
							}
							
							
						}
				}
					/*System.out.println("TRACE "+TRACE);
					System.out.println("REF NO "+REFERENCE_NUMBER);
					System.out.println("AMOUNT "+SOURCE_AMT);
					System.out.println("CARD NUMB "+CARD_NUMBER);
					System.out.println("RESP CODE "+RESPONSE_CODE);
					
					*/
					if((TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27")||TC.equals("10")||TC.equals("20")))
					{
						count++;
						if(TCR_Code.equals("00"))
						{
							ResultSet rset2 =pstmt2.executeQuery();
							if(rset2.next())
							{
								seq = rset2.getString("SEQ");
							}
						}
						
						//Insertint in input table first
						pstmt1.setString(1, TC);
						pstmt1.setString(2, TCR_Code);
						pstmt1.setString(3, stline);
						pstmt1.setString(4, seq+"");
						/*pstmt1.setString(5, "28/09/2017");*/
						pstmt1.setString(5, fileDate);

						pstmt1.addBatch();

						if(TCR_Code.equals("00"))
						{
							read_stmt.setString(1, TC);
							read_stmt.setString(2, TCR_Code);
							read_stmt.setString(3, ACC_NO);
							read_stmt.setString(4, amt+"");
							read_stmt.setString(5, AUTH_CODE);
							read_stmt.setString(6, seq+"");
							/*read_stmt.setString(7, "28/09/2017");*/
							read_stmt.setString(7, fileDate);
							read_stmt.setString(8, "1");
							

							read_stmt.addBatch();
						}

						if(count == 500)
						{
							count = 1;
							read_stmt.executeBatch();
							pstmt1.executeBatch();
							System.out.println("Exceuted batch "+batch);
							batch++;

						}
					}
					else if(TC.equals("33"))
					{
						count++;					
						if(check_TCR.equals("200"))
						{
							ResultSet rset2 =pstmt2.executeQuery();
							if(rset2.next())
							{
								seq = rset2.getString("SEQ");
							}
						}
						
						//Insertint in input table first
						pstmt1.setString(1, TC);
						pstmt1.setString(2, check_TCR);
						pstmt1.setString(3, stline);
						pstmt1.setString(4, seq+"");
						/*pstmt1.setString(5, "28/09/2017");*/
						pstmt1.setString(5, fileDate);
						

						pstmt1.addBatch();
						
						if(check_TCR.equals("200"))
						{

							pstmt33.setString(1, TC);
							pstmt33.setString(2, check_TCR);
							pstmt33.setString(3, CARD_NUMBER);
							pstmt33.setString(4, amt+"");
							pstmt33.setString(5, TRACE);
							pstmt33.setString(6, REFERENCE_NUMBER);
							pstmt33.setString(7, RESPONSE_CODE);
							pstmt33.setString(8, seq);
							//pstmt33.setString(9, "28/09/2017");
							pstmt33.setString(9, fileDate);
							pstmt33.setString(10, "1");
							pstmt33.setString(11, REQ_MSG_TYPE);

							pstmt33.addBatch();
						}
						if(count == 500)
						{
							count = 1;
							pstmt33.executeBatch();
							pstmt1.executeBatch();
							System.out.println("Exceuted batch "+batch);
							batch++;

						}
						
						
						
					}
					
					stline = br.readLine();
				}
				
			}					
			
			read_stmt.executeBatch();
			pstmt1.executeBatch();
			pstmt33.executeBatch();
			
			System.out.println("Completed Reading");
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in readFile "+e);
		}
		finally
		{
			if(conn != null)
			{
				try
				{
					conn.close();
				}
				catch(SQLException e)
				{
					System.out.println("Error while closing connextion "+e);
				}
			}
		}

	}
	
	
	public List<String> checkUpload(CompareSetupBean setupBean)
	{
		List<String> File_to_be_uploaded = new ArrayList<>();
		try
		{
			String GET_FILEID = "SELECT FILEID,FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = '"+setupBean.getCategory()
							+"' AND FILENAME = '"+setupBean.getCategory()+"'";
			OracleConn Oraconn = new OracleConn();
			Connection conn = Oraconn.getconn(); 
			PreparedStatement stmt = conn.prepareStatement(GET_FILEID);
			ResultSet rset = stmt.executeQuery();
			List<String> fileid = new ArrayList<>();
			List<String> subcategory = new ArrayList<>();
			List<String> uploadStatus = new ArrayList<>();
			
			while(rset.next())
			{
				fileid.add(rset.getString("FILEID"));
				subcategory.add(rset.getString("FILE_SUBCATEGORY"));
			}
			
			for(String file_id : fileid)
			{
				String query = "SELECT UPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
						+ setupBean.getCategory() + "' AND FileId = "
						+ file_id + " ";

				query = " SELECT CASE WHEN exists (" + query + ") then (" + query
						+ ") else 'N' end as FLAG from dual";
				//System.out.println("checkupload query is "+query);
				
				stmt = conn.prepareStatement(query);
				rset = stmt.executeQuery();
				while(rset.next())
				{
					uploadStatus.add(rset.getString("FLAG"));
				}
			}
			
			if(uploadStatus.contains("N"))
			{
				for(int i = 0 ; i < uploadStatus.size(); i++)
				{
					if(uploadStatus.get(i).equals("Y"))
					{
						System.out.println("File is uploaded for pos "+subcategory.get(i));
						//check for file count in filesource and main_file_upload
						String CHECK_COUNT = "SELECT T1.UPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS T1, MAIN_FILESOURCE T2 " +
								"WHERE T1.FILEID = '"+fileid.get(i)+"' AND T2.FILEID = '"+fileid.get(i)
								+"' AND T1.FILE_COUNT = T2.FILE_COUNT AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'";
						String FINAL_COUNT = "SELECT CASE WHEN EXISTS ("+CHECK_COUNT+") THEN ("+CHECK_COUNT+") ELSE 'N' END AS FLAG FROM DUAL";
						PreparedStatement pstmt1 = conn.prepareStatement(FINAL_COUNT);
						ResultSet rst = pstmt1.executeQuery();
						String check_count = "N";
						while(rst.next())
						{
							check_count = rst.getString("UPLOAD_FLAG");
						}
						System.out.println("FILE IS UPLOADED BUT COUNT FLAG IS "+check_count);
						if(check_count.equalsIgnoreCase("N"))
						{
							File_to_be_uploaded.add(fileid.get(i));
						}
					}
					else if(uploadStatus.get(i).equals("N"))
					{
						
						System.out.println("FILE NOT UPLOADED FOR pos "+subcategory.get(i)+" FILE ID FOR SAME IS "+fileid.get(i));
						File_to_be_uploaded.add(fileid.get(i));
					}

				}
			}
			else
			{

				for(int i = 0 ; i < uploadStatus.size(); i++)
				{
						System.out.println("File is uploaded for pos "+subcategory.get(i));
						//check for file count in filesource and main_file_upload
						String CHECK_COUNT = "SELECT T1.UPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS T1, MAIN_FILESOURCE T2 " +
								"WHERE T1.FILEID = '"+fileid.get(i)+"' AND T2.FILEID = '"+fileid.get(i)
								+"' AND T1.FILE_COUNT = T2.FILE_COUNT AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'";
						String FINAL_COUNT = "SELECT CASE WHEN EXISTS ("+CHECK_COUNT+") THEN ("+CHECK_COUNT+") ELSE 'N' END AS FLAG FROM DUAL";
						PreparedStatement pstmt1 = conn.prepareStatement(FINAL_COUNT);
						ResultSet rst = pstmt1.executeQuery();
						String check_count = "N";
						while(rst.next())
						{
							check_count = rst.getString("FLAG");
						}
						System.out.println("FILE IS UPLOADED BUT COUNT FLAG IS "+check_count);
						if(check_count.equalsIgnoreCase("N"))
						{
							File_to_be_uploaded.add(fileid.get(i));
						}
					
				}
				if(File_to_be_uploaded.size() == 0)
						System.out.println("FILES ARE ALREADY UPLOADED ");
				return File_to_be_uploaded;
			}
			return File_to_be_uploaded;
						
		}
		catch(Exception e)
		{
			System.out.println("Exception in checkUpload "+e);
			return File_to_be_uploaded;
			
		}
	}
	
	public static void main(String args[])
	{
		ReadNUploadOnusPosFile readVisa = new ReadNUploadOnusPosFile();
		
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter file path: ");
		System.out.flush();
		String filename = scanner.nextLine();
		System.out.print("Enter a file date in (dd/MM/yyyy) format: ");
		String filedate = scanner.nextLine();
		
		
		File file = new File(filename);
		
		System.out.println(file.getName());
		boolean validate = readVisa.validateandUploadFile(file.getName(),filename,filedate);
		if(validate)
		{
			
			System.out.println("FILE READING SUCCESSFULLY COMPLETED ");
			/*if(file.renameTo(new File("\\\\10.144.143.191\\led\\DCRS\\RUPAYCBS\\" + file.getName())))
			{
				System.out.println("FILE MOVED SUCCESSFULLY");
			}*/
				
		}
	}

	public  static  boolean read_method_onp(CompareSetupBean setupBean, Connection conn,
			MultipartFile file) {
    	
		try{
		ReadNUploadOnusPosFile readVisa = new ReadNUploadOnusPosFile();
		
		String filename = file.getOriginalFilename();

		System.out.println(file.getName());
		
		boolean validate = readVisa.validateandUploadFile(file.getName(),file,setupBean.getFileDate());
		if(validate)
		{
			
			System.out.println("FILE READING SUCCESSFULLY COMPLETED ");
			/*if(file.renameTo(new File("\\\\10.144.143.191\\led\\DCRS\\RUPAYCBS\\" + file.getName())))
			{
				System.out.println("FILE MOVED SUCCESSFULLY");
			}*/
				
		}
		return true;
		
	}catch(Exception e)
	{
		e.printStackTrace();
		return false;
	}

}
	
	
	public boolean validateandUploadFile(String filename,MultipartFile filePath,String filedate)
	{
		CompareSetupBean setupBean = new CompareSetupBean();
		ReadNUploadOnusPosFile readFile = new ReadNUploadOnusPosFile();
		try
		{
			setupBean.setCategory("POS");
		//	setupBean.setFileDate(final_date);
			//as per sameer sir take file date from user
			setupBean.setFileDate(filedate);

			List<String> files_to_be_upload =  checkUpload(setupBean);
			if(files_to_be_upload.size() == 0)
				return false;
			else
			{
				readFile.readFile(filePath, filedate);
				readFile.updateRecords(files_to_be_upload, setupBean);
				
				
			}
			
				
		}
		catch(Exception e)
		{
			System.out.println("Exception in validateFile "+e);
			return false;
		}
		return true;
		
	}
	

	
	public void readFile(MultipartFile filepath,String fileDate) throws IOException
	{
		
		String getdate=null;
		InputStream fis = null;
		String stTable_Name = "";
		ArrayList<String> Amount_translation = new ArrayList<>();
		Connection conn = null;
		TCRFile tcrFileObj = new TCRFile();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(filepath.getInputStream()));
		//BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String thisLine = null;  
		
		try
		{
			OracleConn con = new OracleConn();
			 conn = con.getconn();
			System.out.println("Reading data "+new Date().toString());
			//CHECK WHETHER TABLE IS ALREADY PRESENT 
			//String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'POS_ONUS_INPUT_FILE'";
			PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
			ResultSet rset1 = pstmt1.executeQuery();
			int isPresent = 0;
			
			if(rset1.next())
			{
				//System.out.println("table is present ? "+rset1.getInt(1));
				isPresent = rset1.getInt(1);
			}
			
			if(isPresent == 0)
			{

				//IF NOT THEN CREATE IT
				//CREATE VISA INPUT TABLE
				String CREATE_QUERY = "CREATE TABLE POS_ONUS_INPUT_FILE (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE)," +
						" STRING VARCHAR2(1000 BYTE), DCRS_SEQ_NO VARCHAR2(100 BYTE), FILEDATE DATE)";
				PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);
				pstmt.executeQuery();

			}
			
			//CREATE RAW TABLE FOR VISA
				//GET TABLE NAME FROM MAIN FILESOURCES
			String GET_RAW_TABLE = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'POS' AND FILE_CATEGORY = 'POS' AND FILE_SUBCATEGORY = 'ONUS'";
			pstmt1 = conn.prepareStatement(GET_RAW_TABLE);
			rset1 = pstmt1.executeQuery();
			
			if(rset1.next())
			{
				//System.out.println("TABLE NAME IS "+rset1.getString("TABLENAME"));
				stTable_Name = rset1.getString("TABLENAME");
			}
			
			String stline=br.readLine();
			
			String DATA_INSERT = "INSERT INTO "+stTable_Name +" (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,AUTHORIZATION_CODE,DCRS_SEQ_NO,ARN," +
					"FILEDATE,PART_ID) " +
			" VALUES(?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?)";
			PreparedStatement read_stmt =conn.prepareStatement(DATA_INSERT);
			
			String INSERT_33 = "INSERT INTO "+stTable_Name + " (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,REQ_MSGTYPE)" +
					" VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MM/YYYY'),?,?)";
			PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
			
			int count = 0;
			int batch = 1;
			float amt = 0.00f;
			String INSERT_QUERY = "INSERT INTO POS_ONUS_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,TO_DATE(?,'DD/MM/YYYY'))";
			pstmt1 = conn.prepareStatement(INSERT_QUERY);
			
			String Seq_num = "select 'POS'||pos_seq.nextval AS SEQ from dual";
			PreparedStatement pstmt2 = conn.prepareStatement(Seq_num);
			String seq = "";
			
			//GETTING ALL CHARACTER , DIGIT AND SIGN FROM TABLE
			String SIGN_QUERY = "SELECT * FROM VISA_CHAR_TRANSLATION";
			PreparedStatement sign_pstmt = conn.prepareStatement(SIGN_QUERY);
			ResultSet sign_rset = sign_pstmt.executeQuery();
			
			while(sign_rset.next())
			{
				String Data = sign_rset.getString("CHARACTER")+"|"+sign_rset.getInt("LAST_DIGIT")+"|"+sign_rset.getString("SIGN");
			//	System.out.println("check here "+Data);
				Amount_translation.add(Data);
				while(stline != null)
				{
					 getdate=stline.substring(10,16);
					//System.out.println("count is "+count);
					String check_TCR = "";
					String ACC_NO = "";
					String SOURCE_AMT = "";
					String AUTH_CODE = "";
					String ARN = "";
					String TRACE = "";
					String RESPONSE_CODE = "";
					String REFERENCE_NUMBER = "";
					String CARD_NUMBER = "";
					String REQ_MSG_TYPE = "";
					//String TRAN_AMOUNT = "";
					
					List<String> Data_Elements = new ArrayList<>();
					String TC = stline.substring(0, 2);
					String TCR_Code = stline.substring(2,4);
					
					//generating SEQ number
					
					
					
					//INSERT THE DATA IN INPUT TABLE
					
					if(TC.equals("05") || TC.equals("06") || TC.equals("07")|| TC.equals("25") || TC.equals("27"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR050Format();
						}
					}
					else if(TC.equals("10"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR10FeeCollectionFormat();
						}
					}
					else if(TC.equals("20"))
					{
						if(TCR_Code.equals("00"))
						{
							Data_Elements = tcrFileObj.TCR20FundDisbursement();
						}
					}
					else if(TC.equals("33"))//VISA ACQUIRER TRANSACTIONS
					{
						//REMOVE THE HEADER FROM THIS STRING AND THEN STRING
						String stLine = stline.substring(34 , stline.length());
						//System.out.println("NOW STRING IS "+stLine);
						check_TCR = stLine.substring(3, 6);
						//System.out.println("check TCR "+check_TCR);
						if(check_TCR.equals("200"))
						{
							Data_Elements = tcrFileObj.V22200();
						}
						
					}
					
					//READ CARD NUMBER , AMOUNT , AND AUTH CODE FROM INPUT STRING AND INSERT IT IN NEW RAW TABLE
					for(int i= 0 ;i<Data_Elements.size(); i++)
					{
						if((TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27")||TC.equals("10")||TC.equals("20")))
						{
							if(TCR_Code.equals("00"))
							{
								String[] DE =  Data_Elements.get(i).split("\\|");

								//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
								if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
										DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
										DE[0].trim().equalsIgnoreCase("Authorization Code") ||
										DE[0].trim().equalsIgnoreCase("Acquirer Reference Number"))
								{
									//System.out.println("DE[0] "+DE[0]);
									//	System.out.println("DE LENGTH IS "+DE.length);
									if(DE.length == 3)
									{
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										int stEnd_pos = Integer.parseInt(DE[2].trim());
										/*System.out.println("start positon "+ststart_Pos);
								System.out.println("End Position "+stEnd_pos);
								System.out.println("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

										if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
										{
											ACC_NO = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT")) {

											SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											amt = (Float.parseFloat(SOURCE_AMT)/100);
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
										{
											//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).trim();
										}
										else if(DE[0].equalsIgnoreCase("ACQUIRER REFERENCE NUMBER"))
										{
											//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											ARN = stline.substring((ststart_Pos-1), stEnd_pos).trim();
										}


									}
									else if(DE.length == 2)
									{
										int ststart_pos =  Integer.parseInt(DE[1].trim());
										if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
										{
											ACC_NO = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT")) {

											SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
											amt = (Float.parseFloat(SOURCE_AMT)/100);
										}
										else if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
										{
											//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
											AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).trim();
										}

									}
								}
							}
						}
						else if(TC.equals("33"))
						{
							String stLine = stline.substring(34 , stline.length());
							if(check_TCR.equals("200"))
							{
								//GET VARIABLES FOR DECIMAL AND SIGN
								String sign = "";
								String last_Digit = "";
								String[] DE =  Data_Elements.get(i).split("\\|");

								if(DE[0].equalsIgnoreCase("Trace Number")||
										DE[0].equalsIgnoreCase("Response Code")	||
										DE[0].equalsIgnoreCase("Retrieval Reference Number")||
										DE[0].equalsIgnoreCase("Card Number")||
										DE[0].equalsIgnoreCase("Transaction Amount")||
										DE[0].equalsIgnoreCase("Request Message Type"))
								{
									if(DE.length == 3)
									{
										int ststart_Pos = Integer.parseInt(DE[1].trim());
										int stEnd_pos = Integer.parseInt(DE[2].trim());
										/*System.out.println("start positon "+ststart_Pos);
								System.out.println("End Position "+stEnd_pos);
								System.out.println("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

										if(DE[0].equalsIgnoreCase("Trace Number"))
										{
											//TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
										}
										else if(DE[0].equalsIgnoreCase("Request Message Type"))
										{
											REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Response Code")) 
										{
											RESPONSE_CODE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
										{
											REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Card Number"))
										{
											CARD_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
											
											 String pan = thisLine.substring(23, 42).trim();
												String Update_Pan="";		
												if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
							         				  // System.out.println(pan);
							         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
							         				   
							         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
							         				   
							         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
							         				   
							         			   } else {
							         				   
							         				   Update_Pan =null;
							         			   }
												
											
												CARD_NUMBER =Update_Pan;
											
										}
										else if(DE[0].equalsIgnoreCase("Transaction Amount"))
										{
											SOURCE_AMT = stLine.substring((ststart_Pos-1), (stEnd_pos)).replaceAll("^0*","").trim();
											//check for the list of variables provided by visa for decimal position
											for(int j =0; j< Amount_translation.size() ; j++ )
											{
												String[] data =  Amount_translation.get(j).split("\\|");
												if(SOURCE_AMT.contains(data[0]))
												{
													if(data[2].equals("-"))
													{
														SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
													}
													else
													{
														SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
													}
												}
												
											}
											amt = (Float.parseFloat(SOURCE_AMT)/100);
											//System.out.println("amt is "+amt);
										}



									}
									else if(DE.length == 2)
									{
										int ststart_Pos =  Integer.parseInt(DE[1].trim());
										if(DE[0].equalsIgnoreCase("Trace Number"))
										{
											//TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
											TRACE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
										}
										else if(DE[0].equalsIgnoreCase("Response Code")) {

											RESPONSE_CODE = stLine.substring((ststart_Pos-1), ststart_Pos).trim();
											//System.out.println("AMT IS "+SOURCE_AMT+" check this "+amt);
										}
										else if(DE[0].equalsIgnoreCase("Request Message Type"))
										{
											REQ_MSG_TYPE = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
										{
											REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Card Number"))
										{
											CARD_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										}
										else if(DE[0].equalsIgnoreCase("Transaction Amount"))
										{
											SOURCE_AMT = stLine.substring((ststart_Pos-1), (ststart_Pos)).replaceAll("^0*","").trim();
											
											for(int j =0; j< Amount_translation.size() ; j++ )
											{
												String[] data =  Amount_translation.get(j).split("\\|");
												if(SOURCE_AMT.contains(data[0]))
												{
													if(data[2].equals("-"))
													{
														SOURCE_AMT = data[2]+SOURCE_AMT.replace(data[0], data[1]);
													}
													else
													{
														SOURCE_AMT = SOURCE_AMT.replace(data[0], data[1]);
													}
												}
												
											}
											
											amt = (Float.parseFloat(SOURCE_AMT)/100);
										}

									}
								}
							}
							
							
						}
				}
					/*System.out.println("TRACE "+TRACE);
					System.out.println("REF NO "+REFERENCE_NUMBER);
					System.out.println("AMOUNT "+SOURCE_AMT);
					System.out.println("CARD NUMB "+CARD_NUMBER);
					System.out.println("RESP CODE "+RESPONSE_CODE);
					
					*/
					if((TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27")||TC.equals("10")||TC.equals("20")))
					{
						count++;
						if(TCR_Code.equals("00"))
						{
							ResultSet rset2 =pstmt2.executeQuery();
							if(rset2.next())
							{
								seq = rset2.getString("SEQ");
							}
						}
						
						//Insertint in input table first
						pstmt1.setString(1, TC);
						pstmt1.setString(2, TCR_Code);
						pstmt1.setString(3, stline);
						pstmt1.setString(4, seq+"");
						/*pstmt1.setString(5, "28/09/2017");*/
						pstmt1.setString(5, fileDate);

						pstmt1.addBatch();

						if(TCR_Code.equals("00"))
						{
							read_stmt.setString(1, TC);
							read_stmt.setString(2, TCR_Code);
							
							
							 String pan = ACC_NO;
								String Update_Pan="";		
								if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
			         				  // System.out.println(pan);
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
			         				   
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   } else {
			         				   
			         				   Update_Pan =null;
			         			   }
								read_stmt.setString(3,Update_Pan );
							read_stmt.setString(4, amt+"");
							read_stmt.setString(5, AUTH_CODE);
							read_stmt.setString(6, seq+"");
							read_stmt.setString(7, ARN);
							read_stmt.setString(8, fileDate);
							read_stmt.setString(9, "1");
							

							read_stmt.addBatch();
						}

						if(count == 500)
						{
							count = 1;
							read_stmt.executeBatch();
							pstmt1.executeBatch();
							System.out.println("Exceuted batch "+batch);
							batch++;

						}
					}
					else if(TC.equals("33"))
					{
						count++;					
						if(check_TCR.equals("200"))
						{
							ResultSet rset2 =pstmt2.executeQuery();
							if(rset2.next())
							{
								seq = rset2.getString("SEQ");
							}
						}
						
						//Insertint in input table first
						pstmt1.setString(1, TC);
						pstmt1.setString(2, check_TCR);
						pstmt1.setString(3, stline);
						pstmt1.setString(4, seq+"");
						/*pstmt1.setString(5, "28/09/2017");*/
						pstmt1.setString(5, fileDate);
						

						pstmt1.addBatch();
						
						if(check_TCR.equals("200"))
						{

							pstmt33.setString(1, TC);
							pstmt33.setString(2, check_TCR);
							
							 String pan = CARD_NUMBER;
								String Update_Pan="";		
								if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
			         				  // System.out.println(pan);
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
			         				   
			         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
			         				   
			         			   } else {
			         				   
			         				   Update_Pan =null;
			         			   }
								
							pstmt33.setString(3, Update_Pan);
							pstmt33.setString(4, amt+"");
							pstmt33.setString(5, TRACE);
							pstmt33.setString(6, REFERENCE_NUMBER);
							pstmt33.setString(7, RESPONSE_CODE);
							pstmt33.setString(8, seq);
							//pstmt33.setString(9, "28/09/2017");
							pstmt33.setString(9, fileDate);
							pstmt33.setString(10, "1");
							pstmt33.setString(11, REQ_MSG_TYPE);

							pstmt33.addBatch();
						}
						if(count == 500)
						{
							count = 1;
							pstmt33.executeBatch();
							pstmt1.executeBatch();
							System.out.println("Exceuted batch "+batch);
							batch++;

						}
						
						
						
					}
					
					stline = br.readLine();
				}
				
			}					
			
			read_stmt.executeBatch();
			pstmt1.executeBatch();
			pstmt33.executeBatch();
			
			System.out.println(getdate);
			DateFormat fmt1 = new SimpleDateFormat("yyDDD");
			java.util.Date date = fmt1.parse(getdate);
			DateFormat fmt2 = new SimpleDateFormat("MM/dd/yyyy");
			String output = fmt2.format(date);
			System.out.println("Date::" +output);
			String update_julian_date="update "+stTable_Name+" set PROCESSING_DATE='"+output+"' where to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+fileDate+"','dd/mm/yyyy'),'dd/mm/yyyy')";
			System.out.println(update_julian_date);
			Statement st = conn.createStatement();
			int rowupdate =  st.executeUpdate(update_julian_date);
			/*PreparedStatement pstm=conn.prepareStatement(update_julian_date);
			int exe=pstm.executeUpdate();*/
			conn.commit();
			if(rowupdate>0)
			{
				System.out.println("Julian date updated"+output);
				
			}
			System.out.println("Completed Reading");
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in readFile "+e);
		}
		finally
		{
			if(conn != null)
			{
				try
				{
					conn.close();
				}
				catch(SQLException e)
				{
					System.out.println("Error while closing connextion "+e);
				}
			}
		}

	}
	
}
