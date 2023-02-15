package com.recon.util;

import java.io.BufferedReader;
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
import java.util.regex.Pattern;

import com.recon.model.CompareSetupBean;

public class ReadNUploadCBSOnus {
	
	
	 Connection con;
	 Statement st;
	 int Part_id;
	 String man_flag="N", upload_flag="N";
	 
	
	
	
	
	public boolean Read_CBSData(String filename,String filepath) {

		String[] filenameSplit = filename.split("_");
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date filedt;
		String fileDate="";
		String flag="";

		try{
		
			
		//System.out.println(filename);
		
		filedt = format.parse(filenameSplit[3]);
		if(filenameSplit[2].equals("MAN")){
			
			flag="MANUPLOAD_FLAG"; 
			Part_id=2;
			man_flag="Y";
			
		}else{
			
			flag="UPLOAD_FLAG";
			upload_flag="Y";
			Part_id=1;
		}
		
		System.out.println(filedt);
		format = new SimpleDateFormat("dd/MM/yyyy");
		fileDate = format.format(filedt);
		
		
		CompareSetupBean setupBean = new CompareSetupBean();
		setupBean.setCategory("ONUS");
		setupBean.setFileDate(fileDate);
		setupBean.setStFileName("CBS");
		setupBean.setInFileId(2);
	
		if(chkFlag(flag, setupBean).equalsIgnoreCase("N")){
			
			
			OracleConn con = new OracleConn();
			if(getFileCount(setupBean)>0) {
				
				updateFlag(flag, setupBean);
			}else {
			
				updatefile(setupBean);
			}
			if(uploadData(con.getconn(),filename,fileDate,filepath)){
			
				System.out.println("File Uploading Completed");
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

			System.out.println("Erro Occured");
			e.printStackTrace();
			return false;
		}

	}

	public boolean updatefile(CompareSetupBean setupBean) {
	
		try{
		OracleConn conn = new OracleConn();
		
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
				+ "values ("+setupBean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','-'"
						+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";
		
		
			con = conn.getconn();
			st = con.createStatement();
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
				st.close();
				con.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	public int getFileCount(CompareSetupBean setupBean) {
		 
		try {
			int count =0;
		OracleConn conn = new OracleConn();
		String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+"";
		
		con =conn.getconn();
		
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
		
			count = rs.getInt(1);
			
		}
		
		return count;
		
		}catch(Exception ex) {
			
			ex.printStackTrace();
			
			return -1;
			
		}finally {
			
			try {
				st.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	public boolean uploadData(Connection con, String filename, String fileDate,String filepath) {

		
			int flag=1,batch=0;
			
			InputStream fis = null;
			boolean readdata = false;
			try {
				
			
				//fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\CBS\\"+ filename);
				fis = new FileInputStream(filepath);
			} catch (FileNotFoundException ex) {

				System.out.println("Exception" + ex);
				return false;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String thisLine = null;  
			try {
				
				System.out.println("Reading data "+new Date().toString());
				
				 String insert = "INSERT INTO CBS_RAWDATA "
				 		+ "(ACCOUNT_NUMBER,TRANDATE,VALUEDATE,TRAN_ID,TRAN_PARTICULAR,TRAN_RMKS,PART_TRAN_TYPE,TRAN_PARTICULAR1,TRAN_AMT,BALANCE,PSTD_USER_ID,CONTRA_ACCOUNT,ENTRY_DATE,VFD_DATE,REF_NUM,TRAN_PARTICULAR_2,ORG_ACCT,Part_id,FILEDATE,CREATEDDATE)"
				 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')),sysdate)";
			        
			        PreparedStatement ps = con.prepareStatement(insert);
			        
			        int insrt=0;

				while ((thisLine = br.readLine()) != null) {
					
					 String []splitarray= null;

					if (thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) {

						readdata = true;

					}
					if (!(thisLine.contains("ACCOUNT NUMBER|TRAN DATE|")) && readdata) {

						 int srl = 1;
						 ps.setString(15, null);
						 ps.setString(16, null);
						
						
						  splitarray = thisLine.split(Pattern.quote("|"));//Pattern.quote(ftpBean.getDataSeparator())

				            for(int i=0;i<splitarray.length;i++){

				                String value = splitarray[i];
				                if( !value.trim().equalsIgnoreCase("") ){
				                	
				                
	 			                ps.setString(srl,value.trim());

				                ++srl;
				                }else{
				                	
				                	ps.setString(srl,null);
				                	// System.out.println(srl+"null");
				                	 ++srl;
				                }

				                
				            }
				            /**** comment 15 and 16 for online file****/
				          
				            ps.setString(17, null);
				            ps.setInt(18, Part_id);
				            ps.setString(19, fileDate);
				            
				            
				            
				          //  System.out.println(insert);
				            
				            ps.addBatch();
				            flag++;
							
							if(flag == 20000)
							{
								flag = 1;
							
								ps.executeBatch();
								System.out.println("Executed batch is "+batch);
								batch++;
							}
						
					}
					
					
					
				}
				ps.executeBatch();
				 br.close();
			        ps.close();
			        System.out.println("Reading data "+new Date().toString());
				return true;

			} catch (Exception ex) {
				
				ex.printStackTrace();

				System.out.println("Exception" + ex);
				return false;
			}finally{
				
				if(con!=null){
					
					try {
						con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			

	

	}	

	/*public static void main (String []args ){
          
		
		ReadNUploadCBSOnus cbsFile = new ReadNUploadCBSOnus();
		
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
			
			
			File f = new File("\\\\10.144.143.191\\led\\DCRS\\CBS");
			if(!(f.exists())) {
				
				if(f.mkdir()) {
					
					System.out.println("directory created");
				}
			}
			
			
			//if(file.renameTo(new File("\\\\10.144.143.191\\led\\DCRS\\CBS\\" + file.getName()))) {
				
			//	System.out.println("File Moved Successfully");
			
				
				cbsFile.Read_CBSData(file.getName(),file.getPath());
				System.out.println("Process completed");
				
			}else {
				
				System.out.println("Error Occured while moving file");
			}
			
			
			
			
	    }*/
	
	

}
