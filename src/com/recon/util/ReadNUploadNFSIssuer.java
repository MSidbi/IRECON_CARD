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

import com.recon.model.CompareSetupBean;

public class ReadNUploadNFSIssuer {
	
	Connection con;
	int fileid=0;
	
	ResultSet rs;
	Statement st;
	int upload_count=0;
	int file_count=0;
	String flag1="UPLOAD_FLAG";
	
	
	
	
	public boolean upload_data(String filename,String filedate) {
	
	
		DateFormat format = new SimpleDateFormat("ddMMyy");
		Date filedt;
		try {
			
			
			try{
				
				String getFile_count = "Select FILE_COUNT from Main_fileSource where FILE_CATEGORY ='NFS' and filename='NFS'  and FILE_SUBCATEGORY = 'ISSUER' ";
			
				OracleConn conn = new OracleConn();
				con = conn.getconn();
				st = con.createStatement();
				System.out.println("SELECT CASE WHEN exists ("+ getFile_count+") then ("+getFile_count+") else 0 end as FLAG from dual  ");
				rs = st.executeQuery( "SELECT CASE WHEN exists ("+ getFile_count+") then ("+getFile_count+") else 0 end as FLAG from dual  ");
				while(rs.next()) {
					
					file_count = rs.getInt(1);
				}
				
				
				}catch(Exception ex) {
					
					ex.printStackTrace();
					System.out.println(ex.toString());
					
				} finally {
					
					rs.close();
					con.close();
					st.close();
				}
		
			
			
			String  getupld_count = "Select FILE_COUNT from main_file_upload_dtls where CATEGORY ='NFS'  and FILE_SUBCATEGORY = 'ISSUER' and FILEID =13 and  filedate = to_date('"+filedate+"','dd/mm/yyyy')  ";
			
			//get upload Count
			
			try{
			OracleConn conn = new OracleConn();
			con = conn.getconn();
			st = con.createStatement();
			rs = st.executeQuery(getupld_count);
			while(rs.next()) {
				
				upload_count = rs.getInt(1);
			}
			
			
			}catch(Exception ex) {
				
				ex.printStackTrace();
				System.out.println(ex.toString());
				
			} finally {
				
				rs.close();
				con.close();
				st.close();
			}
			
			
		
			if(file_count > upload_count) {
			
			String [] filenameSplit = filename.split("\\."); 
			System.out.println(filename.substring(filenameSplit[0].length()-6));
			
			/*filedt = format.parse(filename.substring(filenameSplit[0].length()-6));
			System.out.println(filedt);
			format = new SimpleDateFormat("dd/MM/yyyy");
			fileDate = format.format(filedt);
			System.out.println(fileDate);*/

			CompareSetupBean setupBean = new CompareSetupBean();
			setupBean.setCategory("NFS");
			setupBean.setStSubCategory("ISSUER");
			setupBean.setFileDate(filedate);
			
			setupBean.setStFileName("NFS");
			
			System.out.println(filename);
			
			
				OracleConn conn = new OracleConn();

				con = conn.getconn();

				setupBean = getFileDetails();
				setupBean.setCategory("NFS");
				setupBean.setStSubCategory("ISSUER");
				setupBean.setFileDate(filedate);
				setupBean.setStFileName("NFS");

				String headers = getFileHeaders();

				String query = "insert into nfs_nfs_iss_rawdata ("
						+ headers
						+ " ,PART_ID,DCRS_TRAN_NO ,CreatedDate , CreatedBy , FILEDATE ) values "
						+ "(?,?,?,?,?" + ",?,?,?,?,?" + ",?,?,?,?,?"
						+ ",?,?,?,?,?" + ",?,?,?,?,?" + ",?,?,?,?,?"
						+ ",?,?,?,?,?," + "?,?,?,?,?," + "sysdate,?,to_date('"
						+ filedate + "','dd/mm/yyyy')) ";

				// "CBSC43_ATM_AO_03-05-2017.LST";
				// /"CBS43_ATM_MAN_01-03-2017.LST";//"CBSC43_ATM_AO_01-03-2017.txt ";//"CBS703_ACQO_01032017.txt";//
				// "CBS_03032017.txt"; CBSC43_ATM_AO_01-03-2017
				String bnaId = "";

				int flag = 1, batch = 0;

				InputStream fis = null;
				boolean readdata = false;
				try {

					
					fis = new FileInputStream("\\\\10.144.136.101\\Shareon101\\DCRS\\NFS\\ISSUER\\"
									+ filename + "");
				} catch (FileNotFoundException ex) {

					System.out.println("Exception" + ex);
					
					return false;
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));
				String thisLine = null;
				try {

					System.out.println("Reading data " + new Date().toString());
				
					PreparedStatement ps = con.prepareStatement(query);

					int insrt = 0;

					while ((thisLine = br.readLine()) != null) {
						
						
						ps.setString(1, thisLine.substring(0, 3));
						ps.setString(2, thisLine.substring(3, 5));
						ps.setString(3, thisLine.substring(5, 7));
						ps.setString(4, thisLine.substring(7, 9));
						ps.setString(5, thisLine.substring(9, 21));
						ps.setString(6, thisLine.substring(21, 23));
						ps.setString(7, thisLine.substring(23, 42));
						ps.setString(8, thisLine.substring(42, 43));
						ps.setString(9, thisLine.substring(43, 49));
						ps.setString(10, thisLine.substring(49, 61));
						ps.setString(11, thisLine.substring(61, 67));
						ps.setString(12, thisLine.substring(67, 73));
						ps.setString(13, thisLine.substring(73, 77));
						ps.setString(14, thisLine.substring(77, 83));
						ps.setString(15, thisLine.substring(83, 98));
						ps.setString(16, thisLine.substring(98, 106));
						ps.setString(17, thisLine.substring(106, 146));
						ps.setString(18, thisLine.substring(146, 157));
						ps.setString(19, thisLine.substring(157, 160));
						ps.setString(20, thisLine.substring(160, 179));
						ps.setString(21, thisLine.substring(179, 189));
						ps.setString(22, thisLine.substring(189, 208));
						ps.setString(23, thisLine.substring(208, 218));
						ps.setString(24, thisLine.substring(218, 221));
						ps.setString(25, thisLine.substring(221, 234).replaceAll("^0*","0") + "."
								+ thisLine.substring(234, 236));
						ps.setString(26, thisLine.substring(236, 249).replaceAll("^0*","0")+ "."
								+ thisLine.substring(249, 251));
						ps.setString(27, thisLine.substring(251, 266));
						ps.setString(28, thisLine.substring(266, 269));
						ps.setString(29, thisLine.substring(269, 284));
						ps.setString(30, thisLine.substring(284, 299));
						ps.setString(31, thisLine.substring(299, 314));
						ps.setString(32, thisLine.substring(314, 317));
						ps.setString(33, thisLine.substring(317, 332));
						ps.setString(34, thisLine.substring(332, 347));
						ps.setString(35, thisLine.substring(347, 362));
						ps.setString(36, thisLine.substring(362, 377));
						ps.setString(37, thisLine.substring(377, 392));
						ps.setString(38, thisLine.substring(392, 407));

						ps.setInt(39, 1);
						ps.setString(40, null);
						ps.setString(41, "AUTOMATION");

						ps.addBatch();
						flag++;

						if (flag == 20000) {
							flag = 1;

							ps.executeBatch();
							System.out.println("Executed batch is " + batch);
							batch++;
						}

						// insrt = ps.executeUpdate();

					}

					ps.executeBatch();
					br.close();
					ps.close();
					System.out.println("Reading data " + new Date().toString());
					
					if(getFileCount(setupBean)>0) {
						
						return  updateFlag(flag1, setupBean);
					
					}else {
					
						return  updatefile(setupBean);
					}

				} catch (Exception e) {
					System.out.println(thisLine);
					
					e.printStackTrace();
					return false;
				}
				
			}
		}catch(Exception ex) {
			
			
			ex.printStackTrace();
			return false;
		}
		return false;
	
	}
	
	
private int getFileCount(CompareSetupBean setupBean) {
		
		try {
				int count = 0;
				OracleConn conn = new OracleConn();
				String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND upper(CATEGORY) = '"+setupBean.getCategory().toUpperCase()+"' AND FILE_SUBCATEGORY='ISSUER' AND FileId = "+setupBean.getInFileId() +" ";
	
				con = conn.getconn();
	
				st = con.createStatement();
	
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
	
					count = rs.getInt(1);
	
				}
	
				return count;
	
			} catch (Exception ex) {
	
				ex.printStackTrace();
	
				return -1;
	
			} finally {
	
				try {
					st.close();
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
			
	}
	
	public String chkFlag(String flag, CompareSetupBean setupBean) {

		Statement st = null;
		ResultSet rs = null;
		
		try {

			
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
				st.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	

	
	public boolean updateFlag(String flag, CompareSetupBean setupBean) {
		
		try{
		
			
		OracleConn conn = new OracleConn();
		
		
		
		String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y',FILE_COUNT = "+(upload_count+1)+"  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+ " AND CATEGORY = 'NFS' AND FileId = "+setupBean.getInFileId()+" "; 
		
		
		con = conn.getconn();
		st = con.createStatement();
		 int rowupdate = st.executeUpdate(query);
		
		
		
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
				st.close();
				con.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	
	
	public boolean updatefile(CompareSetupBean setupBean) {
		Statement st =null;
		
		try{
		OracleConn conn = new OracleConn();
		
		
		
		con = conn.getconn();
		st = con.createStatement();

		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,file_count) "
				+ "values ("+fileid+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+setupBean.getStSubCategory()+"'" //
						+ ",'Y','N','N','N','N','N',1)";
		
		
			con = conn.getconn();
			st = con.createStatement();
			
			System.out.println(query);
			st.executeUpdate(query);
			
		
			
			
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
	
	
	
	public CompareSetupBean getFileDetails() {
		Statement st = null;
		ResultSet rs= null;
		try {
		
		CompareSetupBean setupBean=null;
		String getfileid = "Select fileid,tablename from MAIN_FILESOURCE where filename =upper('NFS') and File_category= upper('NFS') and file_subcategory = upper('ISSUER')";
		
		 st = con.createStatement();
		 rs= st.executeQuery(getfileid);
		while(rs.next()) {
			
			setupBean = new CompareSetupBean();
			fileid =  rs.getInt(1);
			setupBean.setInFileId( rs.getInt(1));
			setupBean.setStTableName(rs.getString(2));
			
		}
		return setupBean;
		
		}catch (Exception ex) {
			
			ex.printStackTrace();
			return null;
		} finally {
			
			try{
				if(st!=null) {
					
					st.close();
					rs.close();
					
				}
			
			}catch(Exception ex) {
				ex.printStackTrace();
				}
			}
		}
		
		
	public String  getFileHeaders() {
		Statement st = null;
		ResultSet rs = null;
		String headers=null;
		
		try{
			
			String query = "Select columnheader from main_fileheaders where fileid = "+fileid+"";
			st= con.createStatement();
			rs = st.executeQuery(query);
			
			while (rs.next()) {
				
				headers=rs.getString(1);
			}
			
			return headers;
			
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
		} finally {
			
			try{
			if(st!=null && rs!= null)
			st.close();
			rs.close();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
		
		
		
		
		
		
		
	}
	
	public static void main(String[] args) {
		
		
		ReadNUploadNFSIssuer issuer = new ReadNUploadNFSIssuer();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a file path: ");
		System.out.flush();
		String filename = scanner.nextLine();
		System.out.print("Enter a file date(dd/mm/yyyy): ");
		System.out.flush();
		String filedate = scanner.nextLine();
		
		File file = new File(filename);
		
		
		System.out.println(file.getName());
		
		
		File f = new File("\\\\10.144.136.101\\Shareon101\\DCRS\\NFS\\ISSUER\\");
		if(!(f.exists())) {
			
			if(f.mkdir()) {
				
				System.out.println("directory created");
			}
		}
		
		try{
		if(file.renameTo(new File("\\\\10.144.136.101\\Shareon101\\DCRS\\NFS\\ISSUER\\" + file.getName()))) {
			
			System.out.println("File Moved Successfully");
			
			issuer.upload_data(file.getName(),filedate);
			
		}else {
			
			System.out.println("Error Occured while moving file");
		} }catch(Exception ex ) {
			
			
		}
	
    }
		
		
		
		
			
	

}
