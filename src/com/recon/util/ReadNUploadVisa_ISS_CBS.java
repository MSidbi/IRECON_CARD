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
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.recon.model.CompareSetupBean;



/**
 *
 * @author int6261
 */
public class ReadNUploadVisa_ISS_CBS  {
	
	Connection con;
	
	Statement st;
	int part_id;
	String man_flag="N",upload_flag="Y";
	
	public boolean uploadCBSData(String fileName,String filePath) {


		String[] filenameSplit = fileName.split("_");
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date filedt = null;
		String fileDate="";
		String flag="";

		try{


			System.out.println(fileName);


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
			setupBean.setCategory("VISA");
			setupBean.setFileDate(fileDate);
			setupBean.setStFileName("CBS");
			//ADDED BY INT5779 FOR DYNAMIC FILEID
			/*String GET_FILEID = "SELECT FILEID FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = 'RUPAY' AND FILE_SUBCATEGORY = '"+stSubCategory+"' AND FILENAME = 'CBS'";
		OracleConn ora = new OracleConn();
		Connection conn = ora.getconn();
		Statement stm = conn.createStatement();
		ResultSet rset = stm.executeQuery(GET_FILEID);
		int inFileId = 0;
		while(rset.next())
		{
			inFileId = Integer.parseInt(rset.getString("FILEID"));
		}*/

			setupBean.setInFileId(21);//4 IS FOR RUPAY DOM FILE

			if(chkFlag(flag, setupBean).equalsIgnoreCase("N")){


				OracleConn con = new OracleConn();
				if(uploadData(con.getconn(),fileName,fileDate,filePath)){

					if(getFileCount(setupBean)>0) {

						updateFlag(flag, setupBean);
					}else {

						updatefile(setupBean);
					}

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
		
		String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'CBS' and FILE_CATEGORY='VISA'   ";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(switchList);
		
		
		while(rs.next())
		{
		
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
				+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+rs.getString("file_subcategory")+"'"
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
	
		public boolean updateFlag(String flag, CompareSetupBean setupBean) {
			
			try{
			
				
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'CBS' and FILE_CATEGORY='VISA'  ";
			
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
					+ " AND CATEGORY = 'VISA' AND FileId = "+rs.getString("FILEID")+" "; 
			}
			else
			{

				query="Update MAIN_FILE_UPLOAD_DTLS set MANUPLOAD_FLAG ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
					+ " AND CATEGORY = 'VISA' AND FileId = "+rs.getString("FILEID")+" "; 
			
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


	public boolean uploadData(Connection con,String filename,String filedate,String filePath ) {
		
		int flag=1,batch=0;
				
				InputStream fis = null;
				boolean readdata = false;
				try {
					
					//\\10.144.143.191\led\DCRS\Raw_Files_29042017\CBS_Files_29042017
					//\\10.144.143.191\led\DCRS\Raw_Files_30042017\CBS_Files_30042017
				//	fis = new FileInputStream("D:\\SUSHANT\\ATM-RECON\\"+ newTargetFile);
					//fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\RUPAYCBS\\"+ filename);
					fis = new FileInputStream(filePath);
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
					
					 String insert = "INSERT INTO CBS_VISA_RAWDATA "
					 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,ORG_ACCT,Part_id,FILEDATE)"
					 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
				        
				        PreparedStatement ps = con.prepareStatement(insert);
				        
				        int insrt=0;
		
					while ((thisLine = br.readLine()) != null) {
						
						 String []splitarray= null;
		
						if (thisLine.contains("------")) {
		
							readdata = true;
		
						}
						if (!(thisLine.contains("-----")) && readdata) {
		
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
					          
					            
					            ps.setInt(17, part_id);
					            ps.setString(18, filedate);
					            
					            
					            
					          //  System.out.println(insert);
					            
					            ps.addBatch();
					            flag++;
								
								if(flag == 500)
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
	
	

    
    public static void main(String []args) {
    	

          
		
		ReadNUploadVisa_ISS_CBS readcbs = new ReadNUploadVisa_ISS_CBS();
		
		
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter file path: ");
			System.out.flush();
			String filename = scanner.nextLine();
			File file = new File(filename);
			//Subcategory IS NOT NEEDED IN CASE OF RUPAY AS SINGLE FILE IS USED FOR BOTH
			/*System.out.println("Enter Sub Category ");
			System.out.flush();
			String stSubCategory = scanner.nextLine();*/
			
			System.out.println(file.getName());
			
			
			/*File f = new File("\\\\10.144.143.191\\led\\DCRS\\RUPAYCBS");
			if(!(f.exists())) {
				
				if(f.mkdir()) {
					
					System.out.println("directory created");
				}
			}
			*/
			
			/*if(file.renameTo(new File("\\\\10.144.143.191\\led\\DCRS\\RUPAYCBS\\" + file.getName()))) 
			{*/
				
			//	System.out.println("File Moved Successfully");
				
				readcbs.uploadCBSData(file.getName(),file.getPath());
				
				System.out.println("Process Completed");
				
			/*}else {
				
				System.out.println("Error Occured while moving file");
			}*/
			
			
			
			
	    
    }
    
    

}


