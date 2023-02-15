package com.recon.util;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.recon.model.CompareSetupBean;



/**
 *
 * @author int6261
 */
public class ReadNUploadCBSAcquirer  {
	
	Connection con;
	
	Statement st;
	int part_id;
	String man_flag="N",upload_flag="N";
	
	public boolean uploadCBSData(String fileName,String filepath) {


		String[] filenameSplit = fileName.split("_");
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date filedt = null;
		String fileDate="";
		String flag="";

		try{
		
			
		//System.out.println(fileName);
		
		
		if(filenameSplit[1].equals("MAN")){
			filedt = format.parse(filenameSplit[2].replace("I", ""));
			
			flag="MANUPLOAD_FLAG"; 
			part_id=2;
			man_flag="Y";
			
		}else{
			
			filedt = format.parse(filenameSplit[2]);
			flag="UPLOAD_FLAG";
			upload_flag="Y";
			part_id=1;
		}
		
		System.out.println(filedt);
		format = new SimpleDateFormat("dd/MM/yyyy");
		fileDate = format.format(filedt);
		
		
		CompareSetupBean setupBean = new CompareSetupBean();
		setupBean.setCategory("AMEX");
		setupBean.setFileDate(fileDate);
		setupBean.setStFileName("CBS");
		//ADDED BY INT5779 FOR DYNAMIC FILEID
		/*String GET_FILEID = "SELECT FILEID FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = '' AND FILE_SUBCATEGORY = '"+stSubCategory+"' AND FILENAME = 'CBS'";
		OracleConn ora = new OracleConn();
		Connection conn = ora.getconn();
		Statement stm = conn.createStatement();
		ResultSet rset = stm.executeQuery(GET_FILEID);
		int inFileId = 0;
		while(rset.next())
		{
			inFileId = Integer.parseInt(rset.getString("FILEID"));
		}*/
		
		setupBean.setInFileId(15);//4 IS FOR  DOM FILE
	
		if(chkFlag(flag, setupBean).equalsIgnoreCase("N")){
			
			
			OracleConn con = new OracleConn();
			//insert or update table before uploading the data this will give error if someone else is trying to upload the file at same time
			if(getFileCount(setupBean)>0) {
				
				updateFlag(flag, setupBean);
			}else {
			
				updatefile(setupBean);
			}
			if(uploadData(con.getconn(),fileName,fileDate,filepath)){
				System.out.println("File Uploading completed");
			
				/*if(getFileCount(setupBean)>0) {
					
					updateFlag(flag, setupBean);
				}else {
				
					updatefile(setupBean);
				}*/
			
			}
		}else {
			
			System.out.println("File Already Uploaded");
		}
		
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
		
		//String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'CBS' and FILE_CATEGORY='AMEX'";
		String FILE_LIST = "SELECT FILEID,FILE_CATEGORY,FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE TABLENAME = (SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILEID= '15')";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(FILE_LIST);
		
		List<String> Fileids = new ArrayList<String>();
		Map<String, String> file_data = new HashMap<String,String>();
		
		
		while(rs.next())
		{
			Fileids.add(rs.getString("FILEID"));
			file_data.put(rs.getString("FILEID") ,rs.getString("FILE_CATEGORY")+"_"+rs.getString("FILE_SUBCATEGORY"));
		
		}
		//ENTRY FOR EACH FILEID
		for(String fileid :Fileids)
		{
			String[] file_details = file_data.get(fileid).split("_");
			String category = file_details[0];
			String subcateg = file_details[1];
		/*String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
				+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+rs.getString("file_subcategory")+"'"
						+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";*/
		
			String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+fileid+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+category+"','"+subcateg+"'"
							+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";
		
			con = conn.getconn();
			st = con.createStatement();
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
	
	/*	public boolean updateFlag(String flag, CompareSetupBean setupBean) {
			
			try{
			
				
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'CBS' and FILE_CATEGORY='AMEX'  ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			String query = "";
			int rowupdate=0;
			while(rs.next())
			{
			if(man_flag.equals("N"))
			{
				query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
					+ " AND CATEGORY = 'AMEX' AND FileId = "+rs.getString("FILEID")+" "; 
			}
			else
			{

				query="Update MAIN_FILE_UPLOAD_DTLS set MANUPLOAD_FLAG ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
					+ " AND CATEGORY = 'AMEX' AND FileId = "+rs.getString("FILEID")+" "; 
			
			}
			
			con = conn.getconn();
			st = con.createStatement();
			 rowupdate =  st.executeUpdate(query);
			
			}
			
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
*/
	public boolean updateFlag(String flag, CompareSetupBean setupBean) {
		
		try{
		
			
		OracleConn conn = new OracleConn();
		
		//String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'CBS' and FILE_CATEGORY='AMEX'  ";
		String FILE_LIST = "SELECT FILEID,FILE_CATEGORY,FILE_SUBCATEGORY FROM MAIN_FILESOURCE WHERE TABLENAME = (SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILEID= '15')";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(FILE_LIST);
		String query = "";
		int rowupdate=0;
		
		List<String> Fileids = new ArrayList<String>();
		Map<String, String> file_data = new HashMap<String,String>();
		
		
		while(rs.next())
		{
			Fileids.add(rs.getString("FILEID"));
			file_data.put(rs.getString("FILEID") ,rs.getString("FILE_CATEGORY")+"_"+rs.getString("FILE_SUBCATEGORY"));
		
		}
		
		for(String fileid : Fileids)
		{
			String[] file_details = file_data.get(fileid).split("_");
			String category = file_details[0];
			//String subcateg = file_details[1];
			
			if(man_flag.equals("N"))
			{
				query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND CATEGORY = '"+category+"' AND FileId = '"+fileid+"' "; 
			}
			else
			{

				query="Update MAIN_FILE_UPLOAD_DTLS set MANUPLOAD_FLAG ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND CATEGORY = '"+category+"' AND FileId = '"+fileid+"' "; 

			}

			con = conn.getconn();
			st = con.createStatement();
			rowupdate =  st.executeUpdate(query);

		}
		
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

	
	
	private int getFileCount(CompareSetupBean setupBean) {
		
		try {
				int count = 0;
				OracleConn conn = new OracleConn();
				String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND CATEGORY = '"
						+ setupBean.getCategory()
						+ "' AND FileId = "
						+ setupBean.getInFileId() + "";
	
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


	public boolean uploadData(Connection con,String filename,String filedate,String filepath ) {
		
		int flag=1,batch=0;
				
				InputStream fis = null;
				boolean readdata = false;
				try {
					
					//\\10.144.143.191\led\DCRS\Raw_Files_29042017\CBS_Files_29042017
					//\\10.144.143.191\led\DCRS\Raw_Files_30042017\CBS_Files_30042017
				//	fis = new FileInputStream("D:\\SUSHANT\\ATM-RECON\\"+ newTargetFile);
				//	fis = new FileInputStream("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS\\"+ filename);
					fis = new FileInputStream(filepath);
				} catch (FileNotFoundException ex) {
		
					System.out.println("Exception" + ex);
					// Logger.getLogger(ReadFileData.class.getName()).log(Level.SEVERE,
					// null, ex);
					return false;
				}
		
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String thisLine = null;  
				try {
					
					System.out.println("Reading data "+new Date().toString());
					
					 String insert = "INSERT INTO CBS_AMEX_RAWDATA "
					 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,Part_id,FILEDATE,CREATEDDATE)"
					 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";
				        
				        PreparedStatement ps = con.prepareStatement(insert);
				        
				        int insrt=0;
		
					while ((thisLine = br.readLine()) != null) {
						
						 String []splitarray= null;
		
						if (thisLine.contains("--------------------------")) {
		
							readdata = true;
		
						}
						if (!(thisLine.contains("---------------------")) && readdata) {
		
							 int srl = 1;
							// System.out.println(thisLine);
							
							  splitarray = thisLine.split(Pattern.quote("|"));//Pattern.quote(ftpBean.getDataSeparator())
		
					            for(int i=0;i<splitarray.length;i++){
		
					                String value = splitarray[i];
					                if( !value.equalsIgnoreCase("") ){
					                	
					                
		 			                ps.setString(srl,value.trim());
		 
					                ++srl;
					                }else{
					                	
					                	ps.setString(srl,null);
					                	// System.out.println(srl+"null");
					                	 ++srl;
					                }
		
					                
					            }
					            /**** comment 15 and 16 for online file****/
					          
					            System.out.println( srl);
					            
					            ps.setInt(16, part_id);
					            ps.setString(17, filedate);
					            
					            
					            
					          //  System.out.println(insert);
					            
					            ps.addBatch();
					            flag++;
								
								if(flag == 10000)
								{
									flag = 1;
								
									ps.executeBatch();
									System.out.println("Executed batch is "+batch);
									batch++;
								}
								
					            
					            //insrt = ps.executeUpdate();
		
						}
						
						
					}
					ps.executeBatch();
					 br.close();
				        ps.close();
				        System.out.println("Reading data "+new Date().toString());
				        return true;
				        
				}catch(Exception ex){
					
					System.out.println("error occurred");
					ex.printStackTrace();
					 return false;
					
				}
		
	}


	
	
	public String chkFlag(String flag, CompareSetupBean setupBean) {

		try {

			ResultSet rs = null;
			String flg = "";
			OracleConn conn = new OracleConn();

			String query = "SELECT "+ flag
					+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
					+ setupBean.getFileDate()
					+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
					+ setupBean.getCategory() + "' AND FileId = "
					+ setupBean.getInFileId() + " ";

			query = " SELECT CASE WHEN exists (" + query + ") then (" + query
					+ ") else 'N' end as FLAG from dual";

			System.out.println(query);
			con = conn.getconn();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {

				flg = rs.getString(1);
			}
			
			//IF AMEX FILE IS ALREADY UPLOADED THEN CHECK FOR OTHER NETWORK WHETHER THE ENTRY IS THERE
			/***
			 * IF NOT THEN INSERT IT
			 */
			if(flg.equalsIgnoreCase("Y"))
			{
				String GET_FILES = "SELECT FILEID,FILE_SUBCATEGORY,FILE_CATEGORY FROM MAIN_FILESOURCE WHERE TABLENAME = (SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILEID = '15') AND FILEID <> '15'";
				PreparedStatement pstmt = con.prepareStatement(GET_FILES);
				ResultSet rset = pstmt.executeQuery();
				List<String> file_ids = new ArrayList<String>();
				HashMap<String, String> file_data = new HashMap<String,String>();
				while(rset.next())
				{
					file_data.put(rset.getString("FILEID"),rset.getString("FILE_CATEGORY")+"_"+rset.getString("FILE_SUBCATEGORY"))	;
					file_ids.add(rset.getString("FILEID"));
				}
				
				for(String fileid : file_ids)
				{
					String file_detail[] = file_data.get(fileid).split("_");
					String category = file_detail[0];
					String subcate = file_detail[1];
							
					//check count from upload dtls table
					String ENTRY_COUNT ="SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
							+ setupBean.getFileDate()+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
							+ category+ "' AND FileId = '"+ fileid + "'";
					PreparedStatement ps = con.prepareStatement(ENTRY_COUNT);
					ResultSet rs1 = ps.executeQuery();
					int entry_count = 0;
					if(rs1.next())
					{
						entry_count = Integer.parseInt(rs1.getString(1));
					}
					
					if(entry_count == 0)
					{
						//INSERT THE RECORD
						String insert_query ="insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) "
								+ "values ("+fileid+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+category+"','"+subcate+"'"
										+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"','0')";
						
						
							st = con.createStatement();
							st.executeUpdate(insert_query);
					}
					else
					{
						//UPDATE THAT RECORD
						String UPDATE_RECORD ="UPDATE MAIN_FILE_UPLOAD_DTLS SET "+flag +"= 'Y' WHERE FILEID = '"+fileid+"' AND to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
							+ setupBean.getFileDate()+ "','dd/mm/yyyy'),'dd/mm/yyyy')  ";
						st = con.createStatement();
						st.executeUpdate(UPDATE_RECORD);
						
					}
					/*String CHECK_UPLOAD = "SELECT "+ flag+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
							+ setupBean.getFileDate()
							+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
							+ setupBean.getCategory() + "' AND FileId = "
							+ fileid + " ";
					CHECK_UPLOAD = " SELECT CASE WHEN exists (" + CHECK_UPLOAD + ") then (" + CHECK_UPLOAD
							+ ") else 'N' end as FLAG from dual";
					PreparedStatement pst = con.prepareStatement(CHECK_UPLOAD);
					ResultSet rset1 = pst.executeQuery();
					if(rset1.next())
					{
						if(rset1.getString(1).equalsIgnoreCase("N"))
						{
							if(man_flag.equals("Y"))
							{
								//update manual flag
							}
							else
							{
							String insert_query ="insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
									+ "values ("+fileid+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+file_data.get(fileid)+"'"
											+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";
							
							
								con = conn.getconn();
								st = con.createStatement();
								st.executeUpdate(query);
							}
						}
					}*/

					
				}
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
	
	
public static void main(String []args) {
    	

          
		
    	ReadNUploadCBSAcquirer readcbs = new ReadNUploadCBSAcquirer();
		
		
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter file path: ");
			System.out.flush();
			String filename = scanner.nextLine();
			File file = new File(filename);
			//Subcategory IS NOT NEEDED IN CASE OF  AS SINGLE FILE IS USED FOR BOTH
			System.out.println("Enter File Date ('DD/MM/YYYY') ");
			System.out.flush();
			String filedate = scanner.nextLine();
			
			System.out.println(file.getName());
			OracleConn con = null;
			try {
				con = new OracleConn();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			readcbs.uploadData(con.getconn(),filename,filedate,file.getPath());
}
    
    /*public static void main(String []args) {
    	

          
		
    	ReadNUploadCBSAcquirer readcbs = new ReadNUploadCBSAcquirer();
		
		
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter file path: ");
			System.out.flush();
			String filename = scanner.nextLine();
			File file = new File(filename);
			//Subcategory IS NOT NEEDED IN CASE OF  AS SINGLE FILE IS USED FOR BOTH
			System.out.println("Enter Sub Category ");
			System.out.flush();
			String stSubCategory = scanner.nextLine();
			
			System.out.println(file.getName());
			
			
			File f = new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS");
			if(!(f.exists())) {
				
				if(f.mkdir()) {
					
					System.out.println("directory created");
				}
			}
			
			
			if(file.renameTo(new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS\\" + file.getName()))) {
				
				System.out.println("File Moved Successfully");
				
				readcbs.uploadCBSData(file.getName());
				
				System.out.println("Process Completed");
				
			}else {
				
				System.out.println("Error Occured while moving file");
			}
			
			
			if(readcbs.uploadCBSData(file.getName(),file.getPath()))
			{
				System.out.println("File uploaded successfully");
			}
			else
				System.out.println("File uploading failed");
			
			
			
	    
    }
    
    */

}


