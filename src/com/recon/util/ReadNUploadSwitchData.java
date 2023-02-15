package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.recon.model.CompareSetupBean;

public class ReadNUploadSwitchData {
	/**
	 *
	 * @author int6261
	 */
		 Connection con;
		 Statement st;
		
		String FILEDATE = ""; 
		
		
		public boolean Read_SwitchData(String filename,String filepath) {

			String[] filenameSplit = filename.split("_");
			String fileDate = null;
			DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
			Date filedt;

			try {
				filedt = format.parse(filenameSplit[1]);
				System.out.println(filedt);
				format = new SimpleDateFormat("dd/MM/yyyy");
				fileDate = format.format(filedt);
				FILEDATE = fileDate;
				
				CompareSetupBean setupBean = new CompareSetupBean();
				//setupBean.setCategory("RUPAY");
				setupBean.setCategory("RUPAY");
				setupBean.setFileDate(fileDate);
				setupBean.setStFileName("SWITCH");
				//setupBean.setInFileId(1);
				setupBean.setInFileId(3);
				
				if (chkFlag("UPLOAD_FLAG", setupBean).equalsIgnoreCase("N")) {

					
					OracleConn con = new OracleConn();
					if(uploadData(con.getconn(), filepath, fileDate)){
						updatefile(setupBean);
					}
				} else {

					System.out.println("File Already Uploaded");
					return false;
				}
				return true;
			} catch (Exception e) {

				System.out.println("Error Occured");
				e.printStackTrace();
				return false;
			}

		}
		
		public boolean Read_mainSwitchData(String filename,String filepath) {

			String[] filenameSplit = filename.split("_");
			String fileDate = null;
			DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
			Date filedt;

			try {
				filedt = format.parse(filenameSplit[1]);
				System.out.println(filedt);
				format = new SimpleDateFormat("dd/MM/yyyy");
				fileDate = format.format(filedt);
				FILEDATE = fileDate;
				
				CompareSetupBean setupBean = new CompareSetupBean();
				//setupBean.setCategory("RUPAY");
				setupBean.setCategory("RUPAY");
				setupBean.setFileDate(fileDate);
				setupBean.setStFileName("SWITCH");
				//setupBean.setInFileId(1);
				setupBean.setInFileId(3);
				
				

					
					OracleConn con = new OracleConn();
					uploadData(con.getconn(), filepath, fileDate);
						
					
				
				return true;
			} catch (Exception e) {

				System.out.println("Error Occured");
				e.printStackTrace();
				return false;
			}

		}

		public boolean updatefile(CompareSetupBean setupBean) {
		
			try{
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'SWITCH'  ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			
			
			while(rs.next())
			{
			String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+rs.getString("file_category")+"','"+rs.getString("file_subcategory")+"'" //
							+ ",'Y','N','N','N','N','N')";
			
			
				con = conn.getconn();
				st = con.createStatement();
				
				System.out.println(query);
				st.executeUpdate(query);
				
			}
				
				
				return true; 
			}catch(Exception ex){
				
		
				System.out.println(ex);
				ex.printStackTrace();
				return false;
			}finally{
				
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}



		public String chkFlag(String flag, CompareSetupBean setupBean) {

			try {

				ResultSet rs = null;
				String flg = "";
				OracleConn conn = new OracleConn();

				String query = "SELECT "
						+ flag
						+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
						+ setupBean.getCategory() + "' AND FileId = "
						+ setupBean.getInFileId() + " ";

				query = " SELECT CASE WHEN exists (" + query + ") then (" + query
						+ ") else 'N' end as FLAG from dual";

				con = conn.getconn();
				st = con.createStatement();
				rs = st.executeQuery(query);
				while (rs.next()) {

					flg = rs.getString(1);
				}

				return flg;

			} catch (Exception ex) {

				ex.printStackTrace();
				return null;

			} finally {

				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		
		public boolean updateFlag(String flag, CompareSetupBean setupBean) {
			
			try{
			
			OracleConn conn = new OracleConn();
			String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
					+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+" "; 
			
			
			con = conn.getconn();
			st = con.createStatement();
			int rowupdate =  st.executeUpdate(query);
			
			if(rowupdate>0) {
				
				
				return true;
				
			}else{
				
				return false;
			}
			}catch(Exception ex){
				
				ex.printStackTrace();
				return false;
			}finally {
				
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		
		public boolean uploadData(Connection con, String filepath, String fileDate) {

			try {
				
				/*String DELETE_RAW = "DELETE FROM SWITCH_RAWDATA WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '22/01/2018'";
				System.out.println("DELETE RAW "+DELETE_RAW);
				PreparedStatement pstm = con.prepareStatement(DELETE_RAW);
				pstm.execute();*/
				
				//System.out.println("DELETED FROM SWITCH RAW");
				
				boolean result = false;

				System.out.println("Database connection established");

				InputStream fis = null;
				try {

				//	fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\Switch\\" + filename);
					fis = new FileInputStream(filepath);
				} catch (FileNotFoundException ex) {
					Logger.getLogger(ReadSwitchFile.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String thisLine = null;
				// "+sourceBean.getTableName()+"
				// MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,NEXT_TRAN_DATE,DCRS_TRAN_NO
				String insert = "INSERT INTO SWITCH_RAWDATA (MSGTYPE,PAN,TERMID,LOCAL_DATE,LOCAL_TIME,PCODE,TRACE,AMOUNT,ACCEPTORNAME,RESPCODE,TERMLOC,NEW_AMOUNT,TXNSRC,TXNDEST,REVCODE,AMOUNT_EQUIV,CH_AMOUNT,SETTLEMENT_DATE,ISS_CURRENCY_CODE,ACQ_CURRENCY_CODE,MERCHANT_TYPE,AUTHNUM,ACCTNUM,TRANS_ID,ACQUIRER,PAN2,ISSUER,REFNUM,CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,DCRS_TRAN_NO,PART_ID,FileDATE) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
				System.out.println("Process started" + new Date().getTime());
				System.out.println(insert);
				
				PreparedStatement ps = con.prepareStatement(insert);
				int insrt = 0, batch = 0;
				int flag = 0;
				//commented by int5779 as it is skipping first line of raw file
				//System.out.println(br.readLine());
				while ((thisLine = br.readLine()) != null) {
					int srl = 1;

					String[] splitarray = null;

					splitarray = thisLine.split(Pattern.quote("|"));// ftpBean.getDataSeparator()

					for (int i = 0; i < splitarray.length; i++) {

						String value = splitarray[i];

						if (value.trim() != "") {
							ps.setString(srl, value.trim());
						} else {

							ps.setString(srl, null);
						}

						srl = srl + 1;

					}

					// System.out.println(thisLine +"-"+insrt);
					ps.setString(28, "NA");
					ps.setString(29, null);
					ps.setString(30, null);
					ps.setString(31, null);
					ps.setInt(32, 1);
					ps.setString(33, fileDate);

					// ps.execute();
					// System.out.println(thisLine);
					// System.out.println("SRL" + srl);
					// ps.setInt(1, srl);
					// ps.setString(2, thisLine);
					// ps.setString(3, "1");
					// insrt = ps.executeUpdate();

					ps.addBatch();
					flag++;

					if (flag == 500) {
						flag = 1;

						ps.executeBatch();
						System.out.println("Executed batch is " + batch);
						batch++;
					}
				}
				ps.executeBatch();
				/* transactionManager.commit(status); */
				insrt = 1;
				System.out.println("Process ended" + new Date().getTime());
				br.close();
				ps.close();

				if (insrt > 0) {

					System.out.println(insrt);
					result = true;

				} else {
					System.out.println(insrt);
					result = false;
				}
				return result;

				// conn.commit();
			} catch (Exception ex) {

				ex.printStackTrace();
				System.out.println("Exception " + ex);
				/* transactionManager.rollback(status); */
				return false;
			} finally {
				if (con != null) {

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}

		}	

		
	/*	public boolean checkDuplicate()
		{
			ResultSet rs = null;
			int count = 0;
			try
			{
				OracleConn conn = new OracleConn();
				String GET_DUPLICATE = "SELECT COUNT(*) FROM SWITCH_RAWDATA T1 WHERE ROWID != (SELECT MAX(ROWID) FROM SWITCH_RAWDATA T2 WHERE T1.PAN = T2.PAN "+
										"AND SUBSTR(T1.TRACE,2,6) = SUBSTR(T2.TRACE,2,6) AND T1.AMOUNT = T2.AMOUNT "+ 
										"AND T1.ACCEPTORNAME = T2.ACCEPTORNAME AND T1.MSGTYPE = T2.MSGTYPE) AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+FILEDATE+"' " +
										"AND T1.PART_ID = '1' AND (T1.MSGTYPE = 110 OR T1.MSGTYPE = 210)";	
				System.out.println("GET_DUPLICATE QUERY IS "+GET_DUPLICATE);
				con = conn.getconn();
				st = con.createStatement();
				rs = st.executeQuery(GET_DUPLICATE);
				if(rs.next()) {
					count = rs.getInt(1);				
				}
				
				if(count >= 1)
				{
					String UPDATE_DUPLICATE = "UPDATE SWITCH_RAWDATA T1 SET T1.DCRS_REMARKS = 'DUPLICATE' WHERE ROWID != (SELECT MAX(ROWID) " +
							"FROM SWITCH_RAWDATA T2 WHERE T1.PAN = T2.PAN "+
										"AND SUBSTR(T1.TRACE,2,6) = SUBSTR(T2.TRACE,2,6) AND T1.AMOUNT = T2.AMOUNT "+ 
										"AND T1.ACCEPTORNAME = T2.ACCEPTORNAME AND T1.RESPCODE = T2.RESPCODE AND T1.MSGTYPE = T2.MSGTYPE) " +
										"AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+FILEDATE+"' " +
										"AND T1.PART_ID = '1' AND (T1.MSGTYPE = 110 OR T1.MSGTYPE = 210)";
					System.out.println("UPDATE DUPLICATE QUERY IS "+UPDATE_DUPLICATE);
					
					st = con.createStatement();
					st.executeUpdate(UPDATE_DUPLICATE);
					
					String UPDATE_ORIGINAL = "UPDATE SWITCH_RAWDATA T1 SET T1.DCRS_REMARKS = 'NOT DUPLICATE' WHERE ROWID = (SELECT MAX(ROWID) FROM SWITCH_RAWDATA T2 WHERE T1.PAN = T2.PAN "+
							"AND SUBSTR(T1.TRACE,2,6) = SUBSTR(T2.TRACE,2,6) AND T1.AMOUNT = T2.AMOUNT AND T1.RESPCODE = T2.RESPCODE "+ 
							"AND T1.ACCEPTORNAME = T2.ACCEPTORNAME AND T1.MSGTYPE = T2.MSGTYPE) AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '25/11/2017' " +
							"AND T1.PART_ID = '1' AND (T1.MSGTYPE = 110 OR T1.MSGTYPE = 210)";
					
					//System.out.println("UPDATE ORIGINAL QUERY IS "+UPDATE_ORIGINAL);
					st = con.createStatement();
					st.executeUpdate(UPDATE_ORIGINAL);
					
				}
				
			}
			catch(Exception e)
			{
				System.out.println("Exception in checkDuplicate "+e);
			}
			return true;
		}*/

		public static void main (String []args ){
	          
			
			ReadNUploadSwitchData switchFile = new ReadNUploadSwitchData();
			
			if(0<args.length) {
					
					String filename = args[0];
					File file= new File(filename);
					
				}
				
				Scanner scanner = new Scanner(System.in);
				System.out.print("Enter a file name: ");
				System.out.flush();
				String filename = scanner.nextLine();
				File file = new File(filename);
				
				
				System.out.println(file.getName());
				
				
				/*File f = new File("\\\\10.143.11.50\\led\\DCRS\\Switch");
				if(!(f.exists())) {
					
					if(f.mkdir()) {
						
						System.out.println("directory created");
					}
				}
				*/
				System.out.println("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
				boolean uploaded = switchFile.Read_mainSwitchData(file.getName(),file.getPath());
				
				//MOVING FILE AFTER READING FILE
				if(uploaded)
				{
					//switchFile.checkDuplicate();
					//if(file.renameTo(new File("\\\\10.143.11.50\\led\\DCRS\\Switch\\" + file.getName()))) 
					{

						System.out.println("File Uploaded Successfully");

						//switchFile.Read_SwitchData(file.getName());

					}/*else {

						System.out.println("Error Occured while moving file");
					}*/
					
					
					//MARKING DUPLICATE RECORDS IN THE RAW TABLE ITSELF
					//switchFile.checkDuplicate();

				}
				System.out.println("END time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				
				
		    }

	}



