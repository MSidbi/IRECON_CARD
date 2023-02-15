package com.recon.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.util.OracleConn;
import com.recon.util.ReadCashNetFile;


public class ReadVisaFile {

	private static final Logger logger = Logger.getLogger(ReadVisaFile.class);
	
	public static void readFile(String stFilePath,Connection conn)
	{
		logger.info("***** ReadVisaFile.readFile Start ****");
		
		String newTargetFile = "INCTF_VISA_280917.txt";
		ArrayList<String> Amount_translation = new ArrayList<>();
		//String newTargetFile = "123.txt";
		String stTable_Name = "";
		try
		{
			//CHECK WHETHER TABLE IS ALREADY PRESENT 
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'VISA_INPUT_FILE'";
			logger.info("CHECK_TABLE=="+CHECK_TABLE);
			PreparedStatement pstmt1 = conn.prepareStatement(CHECK_TABLE);
			ResultSet rset1 = pstmt1.executeQuery();
			int isPresent = 0;
			
			if(rset1.next())
			{
				//logger.info("table is present ? "+rset1.getInt(1));
				isPresent = rset1.getInt(1);
			}
			logger.info("isPresent=="+isPresent);
			if(isPresent == 0)
			{

				//IF NOT THEN CREATE IT
				//CREATE VISA INPUT TABLE
				String CREATE_QUERY = "CREATE TABLE VISA_INPUT_FILE (TC VARCHAR2(100 BYTE), TCR_CODE VARCHAR2(100 BYTE)," +
						" STRING VARCHAR2(100 BYTE), DCRS_SEQ_NO VARCHAR2(100 BYTE), FILEDATE DATE)";
				
				logger.info("CREATE_QUERY=="+CREATE_QUERY);
				PreparedStatement pstmt = conn.prepareStatement(CREATE_QUERY);
				pstmt.executeQuery();

			}
			
			//CREATE RAW TABLE FOR VISA
				//GET TABLE NAME FROM MAIN FILESOURCES
			String GET_RAW_TABLE = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = 'VISA' AND FILE_CATEGORY = 'VISA' AND FILE_SUBCATEGORY = 'ISSUER'";
			logger.info("GET_RAW_TABLE=="+GET_RAW_TABLE);
			pstmt1 = conn.prepareStatement(GET_RAW_TABLE);
			rset1 = pstmt1.executeQuery();
			
			if(rset1.next())
			{
				//logger.info("TABLE NAME IS "+rset1.getString("TABLENAME"));
				stTable_Name = rset1.getString("TABLENAME");
			}
			
			
			TCRFile tcrFileObj = new TCRFile();
			
			File file = new File(stFilePath+newTargetFile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String stline=br.readLine();
			
		//	String stline = "05004587772840129982000Z  74332747270726991827448000000000926000000300120356000000300120356MAKEMYTRIP INDIA PVT LTD NEW DELHI    IN 472200000     1008N1251101 4 0172710";
			
/*			Data_Elements.add("Account Number|5|20");
			Data_Elements.add("Transaction Code Qualifier|3");
*/			
			
			String DATA_INSERT = "INSERT INTO "+stTable_Name +" (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,AUTHORIZATION_CODE,DCRS_SEQ_NO," +
					"FILEDATE,PART_ID,FPAN) " +
			" VALUES(?,?,?,?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?,?)";
			logger.info("DATA_INSERT=="+DATA_INSERT);
			PreparedStatement read_stmt =conn.prepareStatement(DATA_INSERT);
			
			String INSERT_33 = "INSERT INTO "+stTable_Name + " (TC,TCR_CODE,CARD_NUMBER,SOURCE_AMOUNT,TRACE,REFERENCE_NUMBER,RESPONSE_CODE,DCRS_SEQ_NO,FILEDATE,PART_ID,FPAN)" +
					" VALUES(?,?,?,?,?,?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?,?)";
			logger.info("INSERT_33=="+INSERT_33);
			PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
			
				
			int count = 0;
			int batch = 1;
			float amt = 0.00f;
			String INSERT_QUERY = "INSERT INTO VISA_INPUT_FILE (TC, TCR_CODE, STRING,DCRS_SEQ_NO,FILEDATE) VALUES(?,?,?,?,str_TO_DATE(?,'%Y/%m/%d'))";
			String Seq_num = "select 'VISA'||visa_seq.nextval AS SEQ from dual";
			PreparedStatement pstmt2 = conn.prepareStatement(Seq_num);
			String seq = "";
			logger.info("INSERT_QUERY=="+INSERT_QUERY);
			logger.info("Seq_num=="+Seq_num);
			//GETTING ALL CHARACTER , DIGIT AND SIGN FROM TABLE
			String SIGN_QUERY = "SELECT * FROM VISA_CHAR_TRANSLATION";
			logger.info("SIGN_QUERY=="+SIGN_QUERY);
			PreparedStatement sign_pstmt = conn.prepareStatement(SIGN_QUERY);
			ResultSet sign_rset = sign_pstmt.executeQuery();
			
			while(sign_rset.next())
			{
				String Data = sign_rset.getString("CHARACTER")+"|"+sign_rset.getInt("LAST_DIGIT")+"|"+sign_rset.getString("SIGN");
			//	logger.info("check here "+Data);
				Amount_translation.add(Data);
				logger.info("Data=="+Data);
				
			}
			
			pstmt1 = conn.prepareStatement(INSERT_QUERY);
			while(stline != null)
			{
				
				//logger.info("count is "+count);
				String check_TCR = "";
				String ACC_NO = "";
				String SOURCE_AMT = "";
				String AUTH_CODE = "";
				String TRACE = "";
				String RESPONSE_CODE = "";
				String REFERENCE_NUMBER = "";
				String CARD_NUMBER = "";
				String FPAN = "";
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
					//logger.info("NOW STRING IS "+stLine);
					check_TCR = stLine.substring(3, 6);
					//logger.info("check TCR "+check_TCR);
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
								//logger.info("DE[0] "+DE[0]);
								//	logger.info("DE LENGTH IS "+DE.length);
								if(DE.length == 3)
								{
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());
									/*logger.info("start positon "+ststart_Pos);
							logger.info("End Position "+stEnd_pos);
							logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

									if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
									{
										ACC_NO = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
									}
									else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT")) {

										SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										amt = (Float.parseFloat(SOURCE_AMT)/100);
										//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
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
						//need to add else if for TRAN ID LINE INT8624 ON 28 MAY 2021
						
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
									DE[0].equalsIgnoreCase("Transaction Amount"))
							{
								if(DE.length == 3)
								{
									int ststart_Pos = Integer.parseInt(DE[1].trim());
									int stEnd_pos = Integer.parseInt(DE[2].trim());
									/*logger.info("start positon "+ststart_Pos);
							logger.info("End Position "+stEnd_pos);
							logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

									if(DE[0].equalsIgnoreCase("Trace Number"))
									{
										//TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										TRACE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
									}
									else if(DE[0].equalsIgnoreCase("Response Code")) 
									{
										RESPONSE_CODE = stLine.substring((ststart_Pos-1), stEnd_pos).trim();
										//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
									}
									else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
									{
										REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
									}
									else if(DE[0].equalsIgnoreCase("Card Number"))
									{
										String pan = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
										
										//String pan = thisLine.substring(23, 42).trim();
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										CARD_NUMBER = Update_Pan;
										FPAN = pan;
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
										//logger.info("amt is "+amt);
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
										//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
									}
									else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
									{
										REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
									}
									else if(DE[0].equalsIgnoreCase("Card Number"))
									{
										String pan = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
										
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										CARD_NUMBER = Update_Pan;
										FPAN = pan;
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
				/*logger.info("TRACE "+TRACE);
				logger.info("REF NO "+REFERENCE_NUMBER);
				logger.info("AMOUNT "+SOURCE_AMT);
				logger.info("CARD NUMB "+CARD_NUMBER);
				logger.info("RESP CODE "+RESPONSE_CODE);
				
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
					pstmt1.setString(5, "28/09/2017");

					pstmt1.addBatch();

					if(TCR_Code.equals("00"))
					{
						read_stmt.setString(1, TC);
						read_stmt.setString(2, TCR_Code);
						read_stmt.setString(3, ACC_NO);
						read_stmt.setString(4, amt+"");
						read_stmt.setString(5, AUTH_CODE);
						read_stmt.setString(6, seq+"");
						read_stmt.setString(7, "28/09/2017");
						read_stmt.setString(8, "1");
						read_stmt.setString(9, FPAN);

						read_stmt.addBatch();
					}

					if(count == 10000)
					{
						count = 1;
						read_stmt.executeBatch();
						pstmt1.executeBatch();
						logger.info("Exceuted batch "+batch);
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
					pstmt1.setString(5, "28/09/2017");

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
						pstmt33.setString(9, "28/09/2017");
						pstmt33.setString(10, "1");
						pstmt33.setString(11, FPAN);
						pstmt33.addBatch();
					}
					if(count == 10000)
					{
						count = 1;
						pstmt33.executeBatch();
						pstmt1.executeBatch();
						logger.info("Exceuted batch "+batch);
						batch++;

					}
					
					
					
				}
				
				stline = br.readLine();
			}
			read_stmt.executeBatch();
			pstmt1.executeBatch();
			pstmt33.executeBatch();
			
			logger.info("Completed Reading");
			
			logger.info("***** ReadVisaFile.readFile End ****");
			
		}
		catch(Exception e)
		{
			logger.error(" error in ReadVisaFile.readFile", new Exception("ReadVisaFile.readFile",e));
			
		}
	}
	

	
	
	

	public boolean readData(CompareSetupBean setupBean, Connection conn,
			MultipartFile file, FileSourceBean sourceBean) {logger.info("***** ReadVisaFile.readData Start ****");
			InputStream fis = null;
			String stTable_Name = "";
			ArrayList<String> Amount_translation = new ArrayList<String>();
			
			TCRFile tcrFileObj = new TCRFile();
			
		
			BufferedReader br = null ;
			String thisLine = null;  
			int lineNumber = 0;
			try
			{
				
				br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
				logger.info("Reading data "+new Date().toString());
				
				
				//CREATE RAW TABLE FOR VISA
					//GET TABLE NAME FROM MAIN FILESOURCES
				String GET_RAW_TABLE = "";
				if(setupBean.getStSubCategory().equals("-") && setupBean.getCategory().equals("WCC")){
					GET_RAW_TABLE = "select tablename from main_filesource where filename = 'VISA' and file_subcategory = '-'";
				}else{
					GET_RAW_TABLE = "select tablename from main_filesource where filename = 'visa' and file_category = 'VISA' AND file_subcategory = 'ISSUER'";
				}
				logger.info("GET_RAW_TABLE=="+GET_RAW_TABLE);
				PreparedStatement pstmt1 = conn.prepareStatement(GET_RAW_TABLE);
				ResultSet rset1 = pstmt1.executeQuery();
				
				if(rset1.next())
				{
					//logger.info("TABLE NAME IS "+rset1.getString("TABLENAME"));
					stTable_Name = rset1.getString("TABLENAME");
				}
				
				String stline=br.readLine();
				
				String DATA_INSERT_05 = "INSERT INTO "+stTable_Name.toLowerCase() +" (tc,tcr_code,dcrs_seq_no,filedate,part_id,card_number," +
						"floor_limit_indi,arn,acquirer_busi_id,purchase_date,destination_amount,destination_curr_code,source_amount,source_curr_code,merchant_name,merchant_city," +
						"merchant_country_code,merchant_category_code,merchant_zip_code,usage_code,reason_code,settlement_flag,auth_chara_ind,authorization_code," +
						"pos_terminal_capability,cardholder_id_method," +
						"collection_only_flag,pos_entry_mode,central_process_date,reimbursement_attr,fpan,tran_id) " +
				" VALUES(?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				logger.info("DATA_INSERT_05=="+DATA_INSERT_05);
				
				String DATA_INSERT_10 = "insert into "+stTable_Name.toLowerCase() +" (tc,tcr_code,dcrs_seq_no,filedate,part_id,destination_bin,source_bin,reason_code,country_code,event_date," +
						"card_number,destination_amount,destination_curr_code,source_amount,source_curr_code,message_text,settlement_flag,transac_identifier,central_process_date,reimbursement_attr,fpan) " +
				" VALUES(?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?," +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				logger.info("DATA_INSERT_10=="+DATA_INSERT_10);
				
				PreparedStatement read_stmt_05 =conn.prepareStatement(DATA_INSERT_05);
				
				PreparedStatement read_stmt_10 = conn.prepareStatement(DATA_INSERT_10);
				
				String INSERT_33 = "INSERT INTO "+stTable_Name.toLowerCase() + " (tc,tcr_code,card_number,source_amount,trace,reference_number,response_code,dcrs_seq_no,filedate,part_id,req_msgtype,fpan)" +
						" VALUES(?,?,?,?,?,?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?,?,?)";
				
				logger.info("INSERT_33=="+INSERT_33);
				PreparedStatement pstmt33 = conn.prepareStatement(INSERT_33);
				
				int count = 0;
				int batch = 1;
				float amt = 0.00f;
				String INSERT_QUERY="";
				if(setupBean.getStSubCategory().equals("-")){
					INSERT_QUERY = "insert into wcc_input_file (tc, tcr_code, string,dcrs_seq_no,filedate) values(?,?,?,?,str_to_date(?,'%Y/%m/%d'))";
				}else{
					INSERT_QUERY = "insert into visa_input_file (tc, tcr_code, string,dcrs_seq_no,filedate) values(?,?,?,?,str_to_date(?,'%Y/%m/%d'))";
				}
				logger.info("INSERT_QUERY=="+INSERT_QUERY);
				pstmt1 = conn.prepareStatement(INSERT_QUERY);
				
				/*String Seq_num = "select visa_seq.nextval AS SEQ from dual";
				PreparedStatement pstmt2 = conn.prepareStatement(Seq_num);
				String seq = "";*/
				
				//GETTING ALL CHARACTER , DIGIT AND SIGN FROM TABLE
				String SIGN_QUERY = "select * from visa_char_translation";
				PreparedStatement sign_pstmt = conn.prepareStatement(SIGN_QUERY);
				ResultSet sign_rset = sign_pstmt.executeQuery();
				
				while(sign_rset.next())
				{
					String Data = sign_rset.getString("character")+"|"+sign_rset.getInt("last_digit")+"|"+sign_rset.getString("sign");
					Amount_translation.add(Data);
				}
				List<String> stRawData = new ArrayList<String>(); 
					while(stline != null)
					{
						lineNumber++;
						String seq = "";
						stRawData.clear();
						boolean containsVISATable = false;
						//logger.info("count is "+count);
						String check_TCR = "";
						String ACC_NO = "";
						String SOURCE_AMT = "";
						String AUTH_CODE = "";
						String TRACE = "";
						String RESPONSE_CODE = "";
						String REFERENCE_NUMBER = "";
						String CARD_NUMBER = "";
						//ADDED BY INT8624 FOR ADDING FULL PAN
						String FPAN = "";
						String REQ_MSG_TYPE = "";
						String TRAN_ID = "";
						//String TRAN_AMOUNT = "";
						
						List<String> Data_Elements = new ArrayList<String>();
						String TC = stline.substring(0, 2);
						String TCR_Code = stline.substring(2,4);
						
						//generating SEQ number
						
						
						
						//INSERT THE DATA IN INPUT TABLE
						
						if(TC.equals("05") || TC.equals("06") || TC.equals("07")|| TC.equals("25") || TC.equals("27"))
						{
							if(TCR_Code.equals("00") || TCR_Code.equals("20"))
							{
								Data_Elements = tcrFileObj.TCR050Format();
							}
							//Added by int8624 on 28 May
							else if(TCR_Code.equalsIgnoreCase("05"))
							{
								Data_Elements = tcrFileObj.TCR0505Format();
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
							
							//ADDED BY INT5779 FOR VISA TABLE ISSUE
							
							//if(stLine.contains("VISA TABLE"))
							if(stLine.contains("VISAPLUS TBL"))
							{
								containsVISATable = true;
							}
							else{
								//logger.info("NOW STRING IS "+stLine);
								check_TCR = stLine.substring(3, 6);
								//logger.info("check TCR "+check_TCR);
								if(check_TCR.equals("200"))
								{
									Data_Elements = tcrFileObj.V22200();
								}
							}
						}
						if(!containsVISATable)
						{
							//READ CARD NUMBER , AMOUNT , AND AUTH CODE FROM INPUT STRING AND INSERT IT IN NEW RAW TABLE
							for(int i= 0 ;i<Data_Elements.size(); i++)
							{
								if(TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27"))
								{
									if(TCR_Code.equals("00") || TCR_Code.equals("20"))
									{

										String[] DE =  Data_Elements.get(i).split("\\|");
										String stData = "";

										//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
										/*if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
											DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
											DE[0].trim().equalsIgnoreCase("Authorization Code"))
									{*/
										//logger.info("DE[0] "+DE[0]);
										//	logger.info("DE LENGTH IS "+DE.length);
										if(DE.length == 3)
										{
											int ststart_Pos = Integer.parseInt(DE[1].trim());
											int stEnd_pos = Integer.parseInt(DE[2].trim());
											/*logger.info("start positon "+ststart_Pos);
									logger.info("End Position "+stEnd_pos);
									logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												//logger.info(DE[0]);
												stData = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												//logger.info(DE[0]);
												SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
												stRawData.add(stData);
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(DE[0].equalsIgnoreCase("Floor Limit Indicator") ||
														DE[0].equalsIgnoreCase("Acquirer Reference Number") ||
														DE[0].equalsIgnoreCase("Acquirer's Business ID") ||
														DE[0].equalsIgnoreCase("Purchase Date") ||
														//DE[0].equalsIgnoreCase("Destination Amount") || 
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Merchant Name") ||
														DE[0].equalsIgnoreCase("Merchant City") ||
														DE[0].equalsIgnoreCase("Merchant Country Code") ||
														DE[0].equalsIgnoreCase("Merchant Category Code") ||
														DE[0].equalsIgnoreCase("Merchant ZIP Code") ||
														DE[0].equalsIgnoreCase("Usage Code") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Authorization Characteristics Indicator") ||
														DE[0].equalsIgnoreCase("POS Terminal Capability") ||
														DE[0].equalsIgnoreCase("Cardholder ID Method") ||
														DE[0].equalsIgnoreCase("Collection-Only Flag") ||
														DE[0].equalsIgnoreCase("POS Entry Mode") ||
														DE[0].equalsIgnoreCase("Central Processing Date") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Transaction Identifier")||
														DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))


												{
													//logger.info(DE[0]);
													//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_Pos-1), stEnd_pos).trim();
													stRawData.add(stData);
													if(DE[0].equalsIgnoreCase("Collection-Only Flag"))
													{
														//logger.info("Collection-Only Flag "+stData);
													}
												}



										}
										else if(DE.length == 2)
										{
											int ststart_pos =  Integer.parseInt(DE[1].trim());
											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												//logger.info(DE[0]);
												stData = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												try
												{
													//	logger.info(DE[0]);
													SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
													stRawData.add(stData);
												}
												catch(Exception e)
												{
													logger.info("Exception in visa issuer on line "+stline);
												}
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(DE[0].equalsIgnoreCase("Floor Limit Indicator") ||
														DE[0].equalsIgnoreCase("Acquirer Reference Number") ||
														DE[0].equalsIgnoreCase("Acquirer's Business ID") ||
														DE[0].equalsIgnoreCase("Purchase Date") ||
														//DE[0].equalsIgnoreCase("Destination Amount") || 
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Merchant Name") ||
														DE[0].equalsIgnoreCase("Merchant City") ||
														DE[0].equalsIgnoreCase("Merchant Country Code") ||
														DE[0].equalsIgnoreCase("Merchant Category Code") ||
														DE[0].equalsIgnoreCase("Merchant ZIP Code") ||
														DE[0].equalsIgnoreCase("Usage Code") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Authorization Characteristics Indicator") ||
														DE[0].equalsIgnoreCase("POS Terminal Capability") ||
														DE[0].equalsIgnoreCase("Cardholder ID Method") ||
														DE[0].equalsIgnoreCase("Collection-Only Flag") ||
														DE[0].equalsIgnoreCase("POS Entry Mode") ||
														DE[0].equalsIgnoreCase("Central Processing Date") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Transaction Identifier")||
														DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))

												{
													//logger.info(DE[0]);
													//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_pos-1), (ststart_pos)).trim();
													stRawData.add(stData);
													if(DE[0].equalsIgnoreCase("Collection-Only Flag"))
													{
														//logger.info("Collection-Only Flag "+stData);
													}
												}

										}
										//}
										/*if(!stData.equals(""))	
									stRawData.add(stData);	*/
									}
									//ADD TRAN ID ON 28 MAY BY INT 8624
									else if(TCR_Code.equalsIgnoreCase("05"))
									{
										String[] DE =  Data_Elements.get(i).split("\\|");
										//TRAN_ID = stline.substring(4, 19);
										TRAN_ID = stline.substring(Integer.parseInt(DE[1]), Integer.parseInt(DE[2]));
										//logger.info("Tran_id "+TRAN_ID);
									}
								}
								else if(TC.equals("10")||TC.equals("20"))
								{

									if(TCR_Code.equals("00"))
									{

										String[] DE =  Data_Elements.get(i).split("\\|");
										String stData = "";

										//if(((DE[0].trim()).equals("Account Number")) || ((DE[0].trim()).equals("Source Amount"))|| ((DE[0].trim()).equals("Authorization Code")));
										/*if(DE[0].trim().equalsIgnoreCase("ACCOUNT NUMBER") ||
											DE[0].trim().equalsIgnoreCase("SOURCE AMOUNT") ||
											DE[0].trim().equalsIgnoreCase("Authorization Code"))
									{*/
										//logger.info("DE[0] "+DE[0]);
										//	logger.info("DE LENGTH IS "+DE.length);
										if(DE.length == 3)
										{
											int ststart_Pos = Integer.parseInt(DE[1].trim());
											int stEnd_pos = Integer.parseInt(DE[2].trim());
											/*logger.info("start positon "+ststart_Pos);
									logger.info("End Position "+stEnd_pos);
									logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												stData = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {

												SOURCE_AMT = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
												stRawData.add(stData);
												//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if( 	DE[0].equalsIgnoreCase("Destination BIN") ||
														DE[0].equalsIgnoreCase("Source BIN") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Country Code") ||
														DE[0].equalsIgnoreCase("Event Date (MMDD)") ||
														DE[0].equalsIgnoreCase("Account Number") ||
														//	DE[0].equalsIgnoreCase("Destination Amount") ||
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Amount")||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Transaction Identifier") ||
														DE[0].equalsIgnoreCase("Central Processing Date (YDDD)") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Message Text"))


												{
													//AUTH_CODE = stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_Pos-1), stEnd_pos).trim();
													stRawData.add(stData);
												}



										}
										else if(DE.length == 2)
										{
											int ststart_pos =  Integer.parseInt(DE[1].trim());
											if(DE[0].equalsIgnoreCase("ACCOUNT NUMBER"))
											{
												stData = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
												stRawData.add(stData);
											}
											else if(DE[0].equalsIgnoreCase("SOURCE AMOUNT") || DE[0].equalsIgnoreCase("DESTINATION AMOUNT")) {
												try
												{
													SOURCE_AMT = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = String.valueOf((Float.parseFloat(SOURCE_AMT)/100));
													stRawData.add(stData);
												}
												catch(Exception e)
												{
													logger.info("Exception in visa issuer on line "+stline);
												}
											}
											else //if(DE[0].equalsIgnoreCase("AUTHORIZATION CODE"))
												if(	DE[0].equalsIgnoreCase("Destination BIN") ||
														DE[0].equalsIgnoreCase("Source BIN") ||
														DE[0].equalsIgnoreCase("Reason Code") ||
														DE[0].equalsIgnoreCase("Country Code") ||
														DE[0].equalsIgnoreCase("Event Date (MMDD)") ||
														DE[0].equalsIgnoreCase("Account Number") ||
														//DE[0].equalsIgnoreCase("Destination Amount") ||
														DE[0].equalsIgnoreCase("Destination Currency Code") ||
														DE[0].equalsIgnoreCase("Source Amount")||
														DE[0].equalsIgnoreCase("Source Currency Code") ||
														DE[0].equalsIgnoreCase("Settlement Flag") ||
														DE[0].equalsIgnoreCase("Transaction Identifier") ||
														DE[0].equalsIgnoreCase("Central Processing Date (YDDD)") ||
														DE[0].equalsIgnoreCase("Reimbursement Attribute") ||
														DE[0].equalsIgnoreCase("Message Text"))

												{
													//AUTH_CODE = stline.substring((ststart_pos-1), (ststart_pos)).replaceAll("^0*","").trim();
													stData = stline.substring((ststart_pos-1), (ststart_pos)).trim();
													stRawData.add(stData);
												}

										}
										//}
										/*if(!stData.equals(""))	
									stRawData.add(stData);	*/
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
												/*logger.info("start positon "+ststart_Pos);
									logger.info("End Position "+stEnd_pos);
									logger.info("stHeader : "+DE[0]+" Value : "+stline.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*",""));*/

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
													//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
												}
												else if(DE[0].equalsIgnoreCase("Retrieval Reference Number"))
												{
													REFERENCE_NUMBER = stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
												}
												else if(DE[0].equalsIgnoreCase("Card Number"))
												{
													String pan= stLine.substring((ststart_Pos-1), stEnd_pos).replaceAll("^0*","").trim();
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
												}
												else if(DE[0].equalsIgnoreCase("Transaction Amount"))
												{
													try
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
														//logger.info("amt is "+amt);
													}
													catch(Exception e)
													{
														logger.info("Exception in TC 33 on line "+stline);
														logger.info("Exception is "+e);
													}
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
													//logger.info("AMT IS "+SOURCE_AMT+" check this "+amt);
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
													String pan = stLine.substring((ststart_Pos-1), ststart_Pos).replaceAll("^0*","").trim();
													
													String Update_Pan="";		
													if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
								         				  // System.out.println(pan);
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
								         				   
								         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
								         				   
								         			   } else {
								         				   
								         				   Update_Pan =null;
								         			   }
													
													CARD_NUMBER = Update_Pan;
													FPAN = pan;
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
						}
						/*logger.info("TRACE "+TRACE);
						logger.info("REF NO "+REFERENCE_NUMBER);
						logger.info("AMOUNT "+SOURCE_AMT);
						logger.info("CARD NUMB "+CARD_NUMBER);
						logger.info("RESP CODE "+RESPONSE_CODE);
						
						*/
						if(TC.equals("05")||TC.equals("06")||TC.equals("07")||TC.equals("25")||TC.equals("27"))
						{
							count++;
							if(TCR_Code.equals("00")||TCR_Code.equals("20"))
							{
									seq = "VISA" + lineNumber;
							}
							
							//Insertint in input table first
							pstmt1.setString(1, TC);
							pstmt1.setString(2, TCR_Code);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());

							pstmt1.addBatch();

							if(TCR_Code.equals("00")|| TCR_Code.equals("20"))
							{
								read_stmt_05.setString(1, TC);
								read_stmt_05.setString(2, TCR_Code);
								/*read_stmt.setString(3, ACC_NO);
								read_stmt.setString(4, amt+"");
								read_stmt.setString(5, AUTH_CODE);*/
								read_stmt_05.setString(3, seq+"");
								/*read_stmt.setString(7, "28/09/2017");*/
								read_stmt_05.setString(4, setupBean.getFileDate());
								read_stmt_05.setString(5, "1");
								int index = 6;
								for(int m = 0;m<stRawData.size();m++)
								{
									
									if(m == 0) { 
										
										String pan =stRawData.get(m);
										
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										FPAN = pan;
										read_stmt_05.setString(index, Update_Pan);
									}else {
									read_stmt_05.setString(index, stRawData.get(m));
									}
									
									index++;
								}
								
								read_stmt_05.setString(31, FPAN);
								//read_stmt_05.addBatch();  //commented by int8624 on 28 may
							}
							//ADD ELSE IF CONDITION AND ADD BATCH HERE IN8624 on 28 May
							else if(TCR_Code.equalsIgnoreCase("05")|| (TC.equals("06")&& TCR_Code.equalsIgnoreCase("25")))
							{
								read_stmt_05.setString(32, TRAN_ID);
								read_stmt_05.addBatch();
							}
						}
						else if(TC.equals("10")||TC.equals("20"))
						{

							count++;
							if(TCR_Code.equals("00"))
							{
								
								seq = "VISA" + lineNumber;
								
							}
							
							//Insertint in input table first
							pstmt1.setString(1, TC);
							pstmt1.setString(2, TCR_Code);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());

							pstmt1.addBatch();

							if(TCR_Code.equals("00"))
							{
								read_stmt_10.setString(1, TC);
								read_stmt_10.setString(2, TCR_Code);
								/*read_stmt.setString(3, ACC_NO);
								read_stmt.setString(4, amt+"");
								read_stmt.setString(5, AUTH_CODE);*/
								read_stmt_10.setString(3, seq+"");
								/*read_stmt.setString(7, "28/09/2017");*/
								read_stmt_10.setString(4, setupBean.getFileDate());
								read_stmt_10.setString(5, "1");
								int index = 6;
								for(int m = 0;m<stRawData.size();m++)
								{
									//read_stmt_10.setString(index, stRawData.get(m));
									
									if(m == 0) { 
										
										String pan =stRawData.get(m);
										
										String Update_Pan="";		
										if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
					         				  // System.out.println(pan);
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
					         				   
					         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
					         				   
					         			   } else {
					         				   
					         				   Update_Pan =null;
					         			   }
										
										
										read_stmt_10.setString(index, Update_Pan);
									}else {
										read_stmt_10.setString(index, stRawData.get(m));
									}
									
									index++;
								}
								System.out.println("index is "+index);
								read_stmt_10.setString(21, FPAN);
								read_stmt_10.addBatch();
							}

							/*if(count == 10000)
							{
								count = 1;
								read_stmt_10.executeBatch();
								pstmt1.executeBatch();
								logger.info("Exceuted batch "+batch);
								batch++;

							}*/
						
						}
						else if(TC.equals("33"))
						{
							count++;					
							//if(check_TCR.equals("200"))
							if(check_TCR.equals("200") || containsVISATable)
							{
								
								seq = "VISA" + lineNumber;
								
							}
							
							//Insertint in input table first
							pstmt1.setString(1, TC);
							pstmt1.setString(2, check_TCR);
							pstmt1.setString(3, stline);
							pstmt1.setString(4, seq+"");
							/*pstmt1.setString(5, "28/09/2017");*/
							pstmt1.setString(5, setupBean.getFileDate());
							

							pstmt1.addBatch();
							
							if(check_TCR.equals("200") && !containsVISATable)
							{

								pstmt33.setString(1, TC);
								pstmt33.setString(2, check_TCR);
								
								if(CARD_NUMBER != null && CARD_NUMBER.trim()!="") {
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
								
								if(FPAN.equalsIgnoreCase(""))
									FPAN = pan;
								
								pstmt33.setString(3, Update_Pan);
								} else  {
									
									pstmt33.setString(3, CARD_NUMBER);
									
								}
								pstmt33.setString(4, amt+"");
								pstmt33.setString(5, TRACE);
								pstmt33.setString(6, REFERENCE_NUMBER);
								pstmt33.setString(7, RESPONSE_CODE);
								pstmt33.setString(8, seq);
								//pstmt33.setString(9, "28/09/2017");
								pstmt33.setString(9, setupBean.getFileDate());
								pstmt33.setString(10, "1");
								pstmt33.setString(11, REQ_MSG_TYPE);
								pstmt33.setString(12, FPAN);

								pstmt33.addBatch();
							}
							/*if(count == 500)
							{
								count = 1;
								pstmt33.executeBatch();
								pstmt1.executeBatch();
								logger.info("Exceuted batch "+batch);
								batch++;

							}*/
							
							
							
						}
						if(count == 10000)
						{
							count = 1;
							//INSERTING ISSUER DATA
							pstmt1.executeBatch();
							read_stmt_05.executeBatch();
							read_stmt_10.executeBatch();
							//INSERTIN TC 33 DATA
							pstmt1.executeBatch();
							pstmt33.executeBatch();
							logger.info("EXECUTED BATCH "+batch);
							batch++;
							
							
							
						}
						
						stline = br.readLine();
					}
					
						
				
				read_stmt_05.executeBatch();
				read_stmt_10.executeBatch();
				pstmt1.executeBatch();
				pstmt33.executeBatch();
				
				logger.info("Completed Reading");
				
				/****** ENCRYPTING FPAN *****/
				try
				{
					String checkUploadCount = "select file_count as FILECOUNT from main_file_upload_dtls WHERE fileid IN (select fileid from main_filesource where filename = 'VISA' AND file_category = 'VISA' "+
										"AND file_subcategory = 'ISSUER') and filedate = str_to_date(?,'%Y/%m/%d')";
					PreparedStatement encryptps = conn.prepareStatement(checkUploadCount);
					encryptps.setString(1, setupBean.getFileDate());
					ResultSet rs = encryptps.executeQuery();
					int uploadCount = 0;
					if(rs.next())
					{
						uploadCount = rs.getInt("FILECOUNT");
					}
					else
					{
						uploadCount = 0;
					}
					logger.info("Upload Count is "+uploadCount);
					rs.close();
					encryptps.close();
					
					if(uploadCount == 1)
					{
						logger.info("Inside Updation ");
						/*String encryptPan = "update visa_visa_rawdata OS1 set FPAN = (select ibkl_encrypt_decrypt.ibkl_set_encrypt_val(FPAN) enc from VISA_VISA_RAWDATA OS2 "
								+ "WHERE OS1.DCRS_SEQ_NO = OS2.DCRS_SEQ_NO) WHERE FILEDATE = ?";*/
						String encryptPan = "update visa_visa_rawdata set fpan = aes_encrypt(rtrim(ltrim(fpan)),'key_dbank') "+
								"where filedate = str_TO_DATE(?,'%Y/%m/%d')";
						logger.info("Excryption query id "+encryptPan);
						encryptps = conn.prepareStatement(encryptPan);
						encryptps.setString(1, setupBean.getFileDate());
						logger.info("Updation "+encryptps.execute());
					}
					
				}
				catch(Exception e)
				{
					logger.info("Exception while updating rawdata "+e);
				}
				
				logger.info("***** ReadVisaFile.readData End ****");
				
			}
			catch(Exception e)
			{
				logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData",e));
				e.printStackTrace();
				return false;
			}
			finally
			{
				
				
				if(br != null)
				{
					try
					{
						br.close();
					}
					catch(Exception e)
					{
						logger.error(" error in ReadVisaFile.readData", new Exception("ReadVisaFile.readData",e));
					}
				}
			}
			return true;}
	
	

	
	
	
	public static void main(String args[])
	{
		try
		{
		OracleConn oracon = new OracleConn();
		readFile("\\D:\\",oracon.getconn());
		}
		catch(Exception e)
		{
			
		}
	}

}
