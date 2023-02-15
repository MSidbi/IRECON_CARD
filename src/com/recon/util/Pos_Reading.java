package com.recon.util;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.service.ReadImpData;
import com.recon.service.To_List;
import com.recon.service.impl.ReadImpDataImpl;
import com.recon.service.impl.To_List_Impl;




/**
 *
 * @author int6261
 */
public class Pos_Reading  {
	int count=0;
	Connection con;
	
	Statement st;
	int part_id;
	String man_flag="N",upload_flag="Y";
	String value=null;
	private static final Logger logger = Logger.getLogger(Pos_Reading.class);
	public boolean uploadPOSData(MultipartFile file,String fileName, CompareSetupBean setupBean2) throws ParseException {


		String[] filenameSplit = fileName.split("_");
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String filedt = null;
		String fileDate="";
		String flag="";
		String new_date="";
		/*SimpleDateFormat originalFormatter = new SimpleDateFormat ("yyyyMMdd");
		 SimpleDateFormat newFormatter = new SimpleDateFormat ("MM/dd/yyyy");

		 // parsing date string using original format
		  ParsePosition pos = new ParsePosition(0);
		  Date dateFromString = originalFormatter.parse(setupBean2.getFileDate());
		  System.out.println(dateFromString);
		  SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");    
		  try {
			Date d =newDateFormat.parse(newDateFormat.format(setupBean2.getFileDate()));
			new_date=newDateFormat.format(setupBean2.getFileDate());
			System.out.println(new_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}*/
		
		new_date=Utility.dateConveter_mmddyy(setupBean2.getFileDate());
		try{
		
			
		System.out.println(fileName);
		
		
		String split_dt=filenameSplit[1].split("\\.")[0];
			
			filedt = new_date;
			flag="UPLOAD_FLAG";
			upload_flag="Y";
			part_id=1;
		
		
		/*System.out.println(filedt);
		format = new SimpleDateFormat("dd/MM/yyyy");
		fileDate = format.format(filedt);*/
		
		
		CompareSetupBean setupBean = new CompareSetupBean();
		setupBean.setCategory("MASTERCARD");
		setupBean.setFileDate(filedt);
		setupBean.setStFileName("POS");
		setupBean.setInFileId(35);
	
		//if(chkFlag(flag, setupBean).equalsIgnoreCase("N")){
			
			
			OracleConn conn = new OracleConn();
			if(uploadData(file,conn.getconn(),fileName,new_date)){
			
				if(getFileCount(setupBean)>0) {
					
					updateFlag(flag, setupBean);
				}else {
				
					updatefile(setupBean);
				}
			
			}
		/*}else {
			
			System.out.println("File Already Uploaded");
		}*/
		
		return true;

		} catch (Exception e) {

			System.out.println("Erro Occured");
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
	
		
	}
	
	
	public boolean updatefile(CompareSetupBean setupBean) {
		
		try{
			int rowupdate=0;
			int rowupdate1=0;
		OracleConn conn = new OracleConn();
		
		String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'POS' and FILE_CATEGORY='MASTERCARD'   ";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(switchList);
		
		
		while(rs.next())
		{
		
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
				+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+rs.getString("file_subcategory")+"'"
						+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";
		
		
			con = conn.getconn();
			st = con.createStatement();
			st.executeUpdate(query);
			
			count=count+1;
			String insert_count="update Main_File_Upload_Dtls set file_count='"+count+"'  WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'MM/dd/yyyy') "
				+ " AND CATEGORY = 'MASTERCARD' AND FileId = '"+setupBean.getInFileId()+"'  ";
			con = conn.getconn();
			st = con.createStatement();
			st.executeUpdate(insert_count);
			
     String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'MM/dd/yyyy') "
				+ " AND CATEGORY = 'MASTERCARD' AND FileId = '"+setupBean.getInFileId()+"'   ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs1 = st.executeQuery(count);
			
			
			while(rs1.next())
			{
				value=rs1.getString("file_count");
				if(value.equals("6"))
				{
					String query1="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'mm/dd/yyyy') "
							+ " AND CATEGORY = 'MASTERCARD' AND FileId = "+rs.getString("FILEID")+" "; 
					
					
					con = conn.getconn();
					st = con.createStatement();
					 rowupdate =  st.executeUpdate(query1);
				}
				else{
					String query2="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'mm/dd/yyyy') "
							+ " AND CATEGORY = 'MASTERCARD' AND FileId = "+rs.getString("FILEID")+" "; 
					
					
					con = conn.getconn();
					st = con.createStatement();
					 rowupdate1 =  st.executeUpdate(query2);
				}
			}
			
		}
			
			
			return true; 
		}catch(Exception ex){
			
	
			System.out.println(ex);
			ex.printStackTrace();
			logger.error(ex.getMessage());
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
	
		public boolean updateFlag(String flag, CompareSetupBean setupBean) {
			
			try{
			
				
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'POS' and FILE_CATEGORY='MASTERCARD'  ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			
			int rowupdate=0;
			int rowupdate1=0;
			while(rs.next())
			{
			int val=0;
			String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'mm/dd/yyyy') "
					+ " AND CATEGORY = 'MASTERCARD' AND FileId = "+rs.getString("FILEID")+" "; 
			
			
			con = conn.getconn();
			st = con.createStatement();
			 rowupdate =  st.executeUpdate(query);
			 String count1 = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'MM/dd/yyyy') "
						+ " AND CATEGORY = 'MASTERCARD' AND FileId = '"+setupBean.getInFileId()+"'   ";
					
					con = conn.getconn();
					st = con.createStatement();
					
					ResultSet rs12 = st.executeQuery(count1);
					
					
					while(rs12.next())
					{
						val=rs12.getInt("file_count");
						val=val+1;
					}
				String insert_count="update Main_File_Upload_Dtls set file_count='"+val+"'  WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'MM/dd/yyyy') "
					+ " AND CATEGORY = 'MASTERCARD' AND FileId = '"+setupBean.getInFileId()+"'  ";
				con = conn.getconn();
				st = con.createStatement();
				st.executeUpdate(insert_count);
				
	     String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'MM/dd/yyyy') "
					+ " AND CATEGORY = 'MASTERCARD' AND FileId = '"+setupBean.getInFileId()+"'   ";
				
				con = conn.getconn();
				st = con.createStatement();
				
				ResultSet rs1 = st.executeQuery(count);
				
				
				while(rs1.next())
				{
					value=rs1.getString("file_count");
					if(value.equals("6"))
					{
						String query1="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'mm/dd/yyyy') "
								+ " AND CATEGORY = 'MASTERCARD' AND FileId = "+rs.getString("FILEID")+" "; 
						
						
						con = conn.getconn();
						st = con.createStatement();
						 rowupdate =  st.executeUpdate(query1);
					}
					else{
						String query2="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'mm/dd/yyyy') "
								+ " AND CATEGORY = 'MASTERCARD' AND FileId = "+rs.getString("FILEID")+" "; 
						
						
						con = conn.getconn();
						st = con.createStatement();
						 rowupdate1 =  st.executeUpdate(query2);
					}
				}
			
			}
			
			if(rowupdate>0) {
				
				
				return true;
				
			}else{
				
				return false;
			}
			}catch(Exception ex){
				
				ex.printStackTrace();
				logger.error(ex.getMessage());
				return false;
			}finally {
				
				try {
					st.close();
					con.close();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
	
		}

	
	
	private int getFileCount(CompareSetupBean setupBean) {
		
		try {
				int count = 0;
				OracleConn conn = new OracleConn();
				String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','mm/dd/yyyy'),'mm/dd/yyyy') "
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
					logger.error(e.getMessage());
				}
	
			}
			
	}


	public boolean uploadData(MultipartFile file,Connection con,String filename,String new_date) {
		Scanner sc;
		try{
		System.setProperty("file.encoding", "latin1");//  
		java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		StringBuilder sb = new StringBuilder();
		BufferedReader bfrd = new BufferedReader(new InputStreamReader(file.getInputStream()));
		//BufferedReader bfrd = new BufferedReader(new InputStreamReader(new FileInputStream(filename_new)));
		System.out.println("ENCODING" + System.getProperty("file.decoding"));
		sc = new Scanner(bfrd);
		while (sc.hasNextLine()) {
			sc.useDelimiter(",//s");//	sc.useDelimiter(",//s*");
			sb.append(sc.next());// Line
		}
		try {
			To_List to_arrayList = new To_List_Impl();
			ReadImpData rid = ReadImpDataImpl.getInstance();
			ReadImpDataImpl jcsv = (ReadImpDataImpl) ReadImpDataImpl.getInstance();
			rid._read1251(to_arrayList.to_block(sb.toString()),filename,new_date);
			//_to_csv_interface csv = _to_csv_.getInstance();
			//count=count+1;
			//csv.map_to_csv(jcsv.getJcsv());
			//bfrd.close();
		}catch(NullPointerException ne)
		{
			ne.printStackTrace();
			System.out.println("Inside null pointer exception");
			System.out.println("Complete");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(0);
		}}
		catch (IllegalArgumentException e) {

			// TODOAuto-generatedcatchblock

			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(0);

		}/*
		 * catch(IllegalAccessExceptione){
		 * 
		 * //TODOAuto-generatedcatchblock
		 * 
		 * e.printStackTrace();
		 * 
		 * }
		 */catch (FileNotFoundException e) {

			// TODOAuto-generatedcatchblock

			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(0);

		} catch (IOException e) {

			// TODOAuto-generatedcatchblock

			e.printStackTrace();
			logger.error(e.getMessage());
			System.exit(0);

		} catch (SecurityException e1) {

			// TODOAuto-generatedcatchblock

			e1.printStackTrace();
			logger.error(e1.getMessage());
			System.exit(0);

		} catch (NoSuchFieldException e1) {

			// TODOAuto-generatedcatchblock

			e1.printStackTrace();
			logger.error(e1.getMessage());
			System.exit(0);

		} catch (IllegalAccessException e1) {

			// TODOAuto-generatedcatchblock

			e1.printStackTrace();
			logger.error(e1.getMessage());
			System.exit(0);

		}
		return true;
	}


	
	
	public String chkFlag(String flag, CompareSetupBean setupBean) {

		try {

			ResultSet rs = null;
			String flg = "";
			OracleConn conn = new OracleConn();

			String query = "SELECT "
					+ flag
					+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"
					+ setupBean.getFileDate()
					+ "','mm/dd/yyyy'),'mm/dd/yyyy')  " + " AND CATEGORY = '"
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
	
	

    
    public  static  boolean read_method(CompareSetupBean setupBean, Connection conn,
			MultipartFile file) {
    	

          try{
		
		Pos_Reading readcbs = new Pos_Reading();
		
		
		String filename = file.getOriginalFilename();
			
			System.out.println(file.getName());
			
		

			readcbs.uploadPOSData(file, filename,setupBean);

			System.out.println("Process Completed");
			return true;
          }catch(Exception e){
        	  e.printStackTrace();
        	  return false;
        	  
          }
	

	}
			
			
			
			
	    
    
    
    

}


