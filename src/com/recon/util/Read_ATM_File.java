package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;

public class Read_ATM_File {
	/**
	 *
	 * @author int6261
	 */
		 Connection con;
		 Statement st;
		
		
		
		
		public void Read_SwitchData(MultipartFile file,String filename,int i) {

			String[] filenameSplit = filename.split("_");
			String fileDate = null;
			DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
			Date filedt;
			String new_date="";

			try {
				SimpleDateFormat originalFormatter = new SimpleDateFormat ("yyyyMMdd");
				 SimpleDateFormat newFormatter = new SimpleDateFormat ("dd/MM/yyyy");

				 // parsing date string using original format
				  ParsePosition pos = new ParsePosition(0);
				  Date dateFromString = originalFormatter.parse(filenameSplit[1], pos);
				  System.out.println(dateFromString);
				  SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");    
				  try {
					Date d =newDateFormat.parse(newDateFormat.format(dateFromString));
					new_date=newDateFormat.format(d);
					System.out.println(new_date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				CompareSetupBean setupBean = new CompareSetupBean();
				setupBean.setCategory("MASTERCARD");
				setupBean.setFileDate(new_date);
				if(i==0)
				{
				setupBean.setStFileName("ATM");
				setupBean.setInFileId(34);
				}
				
				else if(i==1){
					setupBean.setStFileName("DCC");
					setupBean.setInFileId(41);
				}
				/*else if(i==2){
					setupBean.setStFileName("ATM");
					setupBean.setInFileId(40);
				}*/
				
				if (chkFlag("UPLOAD_FLAG", setupBean).equalsIgnoreCase("N")) {

					if(i==0)
					{
					OracleConn con = new OracleConn();
					if(uploadData(con.getconn(), new_date,file)){
						updatefile(setupBean);
					}
				} 
					else if(i==1)
					{
						updatefile1(setupBean);
					}
					else if(i==2)
					{
						updatefile2(setupBean);
					}
				}
					
					else {

					System.out.println("File Already Uploaded");
				}

			} catch (Exception e) {

				System.out.println("Erro Occured");
				e.printStackTrace();
			}

		}

		public boolean updatefile(CompareSetupBean setupBean) {
		
			try{
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'ATM' and FILE_CATEGORY='MASTERCARD' ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			int count=0;
			String query="";
			
			while(rs.next())
			{
				if(count==0)
				{
			 query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'AUTOMATION',sysdate,'"+rs.getString("file_category")+"','"+rs.getString("file_subcategory")+"'" //
							+ ",'Y','N','N','N','N','N')";
			
				}else{
					
					 query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
							+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'AUTOMATION',sysdate,'"+rs.getString("file_category")+"','"+rs.getString("file_subcategory")+"'" //
									+ ",'Y','N','N','N','N','N')";
					
				}
			   count=count+1;
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

		public boolean updatefile1(CompareSetupBean setupBean) {
			
			try{
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'DCC' and FILE_CATEGORY='MASTERCARD' ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			int count=0;
			String query="";
			
			while(rs.next())
			{
				
			 query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'AUTOMATION',sysdate,'"+rs.getString("file_category")+"','"+rs.getString("file_subcategory")+"'" //
							+ ",'Y','Y','Y','N','N','N')";
			
			
			   count=count+1;
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

public boolean updatefile2(CompareSetupBean setupBean) {
			
			try{
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'DCC' and FILE_CATEGORY='MASTERCARD' ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			int count=0;
			String query="";
			
			while(rs.next())
			{
				
			 query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','MM/dd/yyyy'),'AUTOMATION',sysdate,'"+rs.getString("file_category")+"','"+rs.getString("file_subcategory")+"'" //
							+ ",'Y','N','N','N','N','N')";
			
			
			   count=count+1;
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
						+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','mm/dd/yyyy'),'mm/dd/yyyy')  " + " AND CATEGORY = '"
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
		
		
		public boolean uploadData(Connection con, String fileDate, MultipartFile file) throws ClassNotFoundException, SQLException {

			try {

				boolean result = false;

				String path = ""; //ADD YOUR PATH HERE
		        String fileName = "test2.txt";
		        String testWord = "FREC"; //CHANGE THIS IF YOU WANT
		        String testWord1 = "NREC";
		        String testWord2 = "EREC"; //added by INT8624 FOR READING REVERSAL TRANSACTIONS ON 10 JUNE 2021
		        int tLen = testWord.length();
		        int count = 0;
		        String msgtype=null;
		        String sub_str_frec=null;
		       // String file = "D:\\Master_Card\\MasterCard20170905\\";
		        boolean check = false;
		        String query=null;

		        OracleConn conn=new OracleConn();
				Connection con2=conn.getconn();
				//int count=0;
				 PreparedStatement ps=null;
		        
		        try{
		            //FileInputStream fstream = new FileInputStream(file);
		           /* BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_path)));*/
		        	BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
		            //BufferedReader br = new BufferedReader(new InputStreamReader(file_path));
		            String strLine;        
		            //Read File Line By Line
		           // String get_data=strLine = br.readLine();
		            
		            query="insert into MASTERCARD_ATM_RAWDATA(msgtype ,swicth_serial_num  ,processor_a_i ,processor_id ,tran_date ,tran_time ,pan_length ,pan_num ,proccessing_code,trace_num ,mercahnt_type "     
		         + ",pos_entry ,ref_no ,aquirer_i_id ,terminal_id ,respcode ,brand  ,advaice_reg_code ,intra_curr_aggrmt_code,auth_id ,currency_code , "    
		+ "implied_dec_tran  ,compltd_amnt_tran ,compltd_amnt_tran_d_c ,cash_back_amnt_l ,cash_back_amnt_d_c_c ,access_fee_l ,access_fee_l_d_c , "    
		+ "currency_settlment ,implied_dec_settlment ,conversion_rate ,compltd_amt_settmnt ,compltd_amnt_d_c  ,inter_change_fee  ,inter_change_fee_d_c "  
		+ ",service_lev_ind ,resp_code1 ,filer ,positive_id_ind ,atm_surcharge_free ,cross_bord_ind  ,cross_bord_currency_ind,visa_ias ,req_amnt_tran "        
		+ ",filer1  ,trace_num_adj ,filer2,type,part_id,filedate_1,filedate,FPAN ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'mm/dd/yyyy')),?)";
		           
		            while((strLine = br.readLine()) != null){  
		            	sub_str_frec=strLine.substring(0,4).trim();
		            	if(!(sub_str_frec.trim()).equals(testWord.trim())   && !(sub_str_frec.trim()).equals(testWord1.trim()) && !(sub_str_frec.trim()).equals(testWord2.trim()))
		                {
		                	//System.out.println(sub_str_frec);
		            		count=count+1;
		            		 System.out.println("total is: " + count);
		                }
		            	
		            	 else
		                 {
		            	if((sub_str_frec.trim()).equals(testWord.trim()))	
		            	{
		                check = strLine.toLowerCase().contains(testWord.toLowerCase());
		            	}
		            	else if((sub_str_frec.trim()).equals(testWord1.trim()))
		            	{
		            		check = strLine.toLowerCase().contains(testWord1.toLowerCase());
		            	}
		            	else if((sub_str_frec.trim()).equals(testWord2.trim()))
		            	{
		            		check = strLine.toLowerCase().contains(testWord2.toLowerCase());
		            	}
		                if(check){                    
		                    //get the line, and parse its words into a String array
		                    String[] lineWords = strLine.split("\\s+");                    
		                    msgtype=sub_str_frec;
		                    String swicth_serial_num=strLine.substring(4, 13);
		                    String processor_a_i=strLine.substring(13, 14);
		                    String processor_id=strLine.substring(14, 18);
		                    String tran_date=strLine.substring(18, 24);
		                    String tran_time=strLine.substring(24, 30);
		                    String pan_length=strLine.substring(30, 32);
		                    
		                    
		                    String pan = strLine.substring(32, 51).trim();
							String Update_Pan="";		
							if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
		         				  // System.out.println(pan);
		         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
		         				   
		         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
		         				   
		         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
		         				   
		         			   } else {
		         				   
		         				   Update_Pan =null;
		         			   }
							
							String pan_num=Update_Pan;
						
		                    String proccessing_code=strLine.substring(52, 57);
		                    String trace_num=strLine.substring(57, 63);
		                    String mercahnt_type=strLine.substring(63, 67);
		                    String pos_entry=strLine.substring(67, 70);
		                    String ref_no=strLine.substring(70, 82);
		                    String aquirer_i_id=strLine.substring(82, 92);
		                    String terminal_id=strLine.substring(92, 102);
		                    String respcode=strLine.substring(102, 104);
		                    String brand=strLine.substring(104, 107);
		                   
		                    String advaice_reg_code=strLine.substring(107, 114);
		                    String intra_curr_aggrmt_code=strLine.substring(114, 118);
		                    String auth_id=strLine.substring(118, 124);
		                    String currency_code=strLine.substring(124, 127);
		                    String implied_dec_tran=strLine.substring(127, 128);
		                    String compltd_amnt_tran=strLine.substring(128, 140);
		                    String compltd_amnt_tran_d_c=strLine.substring(140, 141);
		                    String cash_back_amnt_l=strLine.substring(141, 153);
		                    String cash_back_amnt_d_c_c=strLine.substring(153, 154);
		                    String access_fee_l=strLine.substring(154, 163);
		                    String access_fee_l_d_c=strLine.substring(163, 164);
		                    String currency_settlment=strLine.substring(164, 166);
		                    String implied_dec_settlment=strLine.substring(166, 167);
		                    String conversion_rate=strLine.substring(167, 175);
		                    String compltd_amt_settmnt=strLine.substring(175, 187);
		                    String compltd_amnt_d_c=strLine.substring(187, 188);
		                    String inter_change_fee=strLine.substring(188, 198);
		                    String inter_change_fee_d_c=strLine.substring(198, 199);
		                    String service_lev_ind=strLine.substring(199, 202);
		                    String resp_code1=strLine.substring(202, 204);
		                    String filer=strLine.substring(204, 214);
		                    String positive_id_ind=strLine.substring(214, 215);
		                    String atm_surcharge_free=strLine.substring(215, 216);
		                    String cross_bord_ind=strLine.substring(216, 217);
		                    String cross_bord_currency_ind=strLine.substring(217, 218);
		                    String visa_ias=strLine.substring(218, 219);
		                    String req_amnt_tran=strLine.substring(219, 231);
		                    String filer1=strLine.substring(231, 243);
		                    String trace_num_adj=strLine.substring(243, 249);
		                    String filer2=strLine.substring(249, 250);
		                    ps=con.prepareStatement(query);
		                    ps.setString(1, msgtype);
		                    ps.setString(2, swicth_serial_num);
		                    ps.setString(3, processor_a_i);
		                    ps.setString(4, processor_id);
		                    ps.setString(5, tran_date);
		                    ps.setString(6, tran_time);
		                    ps.setString(7, pan_length);
		                    ps.setString(8, pan_num);
		                    ps.setString(9, proccessing_code);
		                    ps.setString(10, trace_num);
		                    ps.setString(11, mercahnt_type);
		                    ps.setString(12, pos_entry);
		                    ps.setString(13, ref_no);
		                    ps.setString(14, aquirer_i_id);
		                    ps.setString(15, terminal_id);
		                    ps.setString(16, respcode);
		                    ps.setString(17, brand);
		                    ps.setString(18, advaice_reg_code);
		                    ps.setString(19, intra_curr_aggrmt_code);
		                    ps.setString(20, auth_id);
		                    ps.setString(21, currency_code);
		                    ps.setString(22, implied_dec_tran);
		                    ps.setString(23, compltd_amnt_tran);
		                    ps.setString(24, compltd_amnt_tran_d_c);
		                    ps.setString(25, cash_back_amnt_l);
		                    ps.setString(26, cash_back_amnt_d_c_c);
		                    ps.setString(27, access_fee_l);
		                    ps.setString(28, access_fee_l_d_c);
		                    ps.setString(29, currency_settlment);
		                    ps.setString(30, implied_dec_settlment);
		                    ps.setString(31, conversion_rate);
		                    ps.setString(32, compltd_amt_settmnt);
		                    ps.setString(33, compltd_amnt_d_c);
		                    ps.setString(34, inter_change_fee);
		                    ps.setString(35, inter_change_fee_d_c);
		                    ps.setString(36, service_lev_ind);
		                    ps.setString(37, resp_code1);
		                    ps.setString(38, filer);
		                    ps.setString(39, positive_id_ind);
		                    ps.setString(40, atm_surcharge_free);
		                    ps.setString(41, cross_bord_ind);
		                    ps.setString(42, cross_bord_currency_ind);
		                    ps.setString(43, visa_ias);
		                    ps.setString(44, req_amnt_tran);
		                    ps.setString(45, filer1);
		                    ps.setString(46, trace_num_adj);
		                    ps.setString(47, filer2);
		                    if(brand.equalsIgnoreCase("MC1") || brand.equalsIgnoreCase("CI1") || brand.equalsIgnoreCase("MS1") )
		                    {
		                    	ps.setString(48, "IN");
		                    }
		                    else
		                    {
		                    	ps.setString(48, "DO");
		                    }
		                    ps.setInt(49, 1);
		                    ps.setString(50, tran_date);
		                  //  String new_date;
							ps.setString(51, fileDate);
							ps.setString(52, strLine.substring(32, 51).trim()); // ADDED BY INT8624 FOR FULL PAN ON 10 JUNE
		                    count=count+1;
		                    System.out.println("total is: " + count);
		                    
		                    int result1=ps.executeUpdate();
		                    ps.close();
		                                    
		                }   }
		           
		            }
		            
		        //Close the input stream
		            br.close();
		            ps.close();
		        }catch(IndexOutOfBoundsException in)
		        {
		        	in.printStackTrace();
		        	System.out.println("inside arrayindex");
		        	
		        	return true;
		        }
		        
		        catch(Exception e){
		        	System.out.println("total is: " + count +"-"+msgtype +"-"+sub_str_frec);
		            e.printStackTrace();
		            System.exit(0);
		            
		        }
		        return true;
			}finally {
				if (con != null) {

					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.exit(0);
					}
				}

			}
			

		}	

		public  static  boolean read_method_atm(CompareSetupBean setupBean, Connection conn,
				MultipartFile file) {
	    	
	          
			//String one = args[0];
try{
		Read_ATM_File atmfile = new Read_ATM_File();

		String filename = file.getOriginalFilename();

		System.out.println(file.getName());

		for (int i = 0; i < 2; i++) {
			atmfile.Read_SwitchData(file, filename, i);
		}

		System.out.println("Process Completed");
			return true;

		} 
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
				}
}
				
				
				
		    

	



