package com.recon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;

public class ReadNfsRawData {

	private static final Logger logger = Logger.getLogger(ReadNfsRawData.class);
	
	public boolean readData(CompareSetupBean setupBean, Connection con,
			MultipartFile file, FileSourceBean sourceBean) {
	
		logger.info("***** ReadNfsRawData.readData Start ****");

		try{
			
			
			
		boolean uploaded = false;
		logger.info(setupBean.getStSubCategory());
		if(setupBean.getStSubCategory().equalsIgnoreCase("ISSUER"))//ISSUER
		{
			logger.info("Entered CBS File is Issuer");
			
			uploaded = uploadIssuerData(setupBean, con, file, sourceBean);
		}
		else if(setupBean.getStSubCategory().equalsIgnoreCase("ACQUIRER"))//ACQUIRER
		{
			logger.info("Entered CBS File is Acquirer");
		
			uploaded =uploadAcquirerData(setupBean, con, file, sourceBean);
			
		}
	
		else
		{
			logger.info("Entered File is Wrong");
			return false;
		}
		logger.info("***** ReadNfsRawData.readData End ****");
		
		return true;

		} catch (Exception e) {

			logger.error(" error in ReadNfsRawData.readData", new Exception("ReadNfsRawData.readData",e));
			//e.printStackTrace();
			
			return false;
		}
	
	}
	
	
public boolean uploadIssuerData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean ) {
	logger.info("***** ReadNfsRawData.uploadIssuerData Start ****");
		int flag=1,batch=0,recordcount=0;
		String cycle = "";
		//ADDED BY INT8624 FOR GETTING CYCLE FROM FILE NAME
				String fileName = file.getOriginalFilename();
				logger.info("FileName is "+fileName);
				String[] fileNames = fileName.split("_");
				if(fileNames.length >0)
					cycle = fileNames[1].substring(0,1);
				
				logger.info("Cycle is: "+cycle);
				
		String query = "insert /*+APPEND*/into nfs_nfs_iss_rawdata ("
				+ sourceBean.getTblHeader()
				+ " ,part_id,dcrs_tran_no ,createddate , createdby , filedate,fpan,cycle ) values "
				+ "(?,?,?,?,?" + ",?,?,?,?,?" + ",?,?,?,?,?"
				+ ",?,?,?,?,?" + ",?,?,?,?,?" + ",?,?,?,?,?"
				+ ",?,?,?,?,?," + "?,?,?,?,?," + "sysdate(),?, str_to_date('"
				+ setupBean.getFileDate() + "','%Y/%m/%d'),?,?) ";
		
		logger.info("query=="+query);
				
		try {
				
				boolean readdata = false;
				
		
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				String thisLine = null;  
				try {

					logger.info("Reading data " + new Date().toString());
				
					PreparedStatement ps = con.prepareStatement(query);

					int insrt = 0;

					while ((thisLine = br.readLine()) != null) {
						
						
						ps.setString(1, thisLine.substring(0, 3));
						ps.setString(2, thisLine.substring(3, 5));
						ps.setString(3, thisLine.substring(5, 7));
						ps.setString(4, thisLine.substring(7, 9));
						ps.setString(5, thisLine.substring(9, 21));
						ps.setString(6, thisLine.substring(21, 23));
						
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
						
						ps.setString(7, Update_Pan);
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
						/*ps.setString(25, thisLine.substring(221, 234).replaceAll("^0*","0") + "."
								+ thisLine.substring(234, 236));*/
						//Amount modified for mysql
						ps.setString(25, thisLine.substring(221, 234).replaceAll("^0+(?!$)", ""));
						ps.setString(26, thisLine.substring(236, 249).replaceAll("^0*","0")+ "."
								+ thisLine.substring(249, 251));
						ps.setString(27, thisLine.substring(251, 266));
						ps.setString(28, thisLine.substring(266, 269));
						/*ps.setString(29, thisLine.substring(269, 282).replaceAll("^0*","0")+ "."
								+ thisLine.substring(282, 284));*/
						
						ps.setString(29, thisLine.substring(269, 282).replaceAll("^0+(?!$)", ""));
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
						//Added by INT8624 FOR ENCRYPTING PAN
					/*	String encyp_pan = null;
						PreparedStatement ency_pst = con.prepareStatement("select ibkl_encrypt_decrypt.ibkl_set_encrypt_val('"+thisLine.substring(23, 42)+"') ENC from dual");
						ResultSet rs = ency_pst.executeQuery();
						while(rs.next())
						{
							encyp_pan = rs.getString("ENC");
						}
						ency_pst.close();
						rs.close(); 
						
						//ps.setString(42, thisLine.substring(23, 42));
						ps.setString(42, encyp_pan);*/
						ps.setString(42, thisLine.substring(23, 42));
						ps.setString(43, cycle);
						
						ps.addBatch();
						flag++;

						if (flag == 20000) {
							flag = 1;

							ps.executeBatch();
							logger.info("Executed batch is " + batch);
							recordcount++;
							batch++;
						}

						// insrt = ps.executeUpdate();

					}

					ps.executeBatch();
					
					/*** ENCRYPTING PAN IN RAWDATA ****/
					logger.info("Updation Starting ");
					String updateQuery ="update nfs_nfs_iss_rawdata set fpan = aes_encrypt(rtrim(ltrim(fpan)),'key_dbank') "
								+ "where cycle = '"+cycle+"' and filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')";
					
					PreparedStatement pstmt = con.prepareStatement(updateQuery);
					pstmt.execute();
					
					logger.info("Updation done");
					
					/** Done **/
					
					recordcount++;
					br.close();
					ps.close();
					logger.info("Reading data " + new Date().toString());
					
					logger.info("***** ReadNfsRawData.uploadIssuerData End ****");
					
					if(recordcount>0) {
						
								
						return  true;
					} else {
						
						return false;
					} 
						

				}catch(Exception ex){
					
					logger.error(" error in ReadNfsRawData.uploadIssuerData", new Exception("ReadNfsRawData.uploadIssuerData",ex));
					 return false;
					
				}
		}catch(Exception ex) {
			
			logger.error(" error in ReadNfsRawData.uploadIssuerData", new Exception("ReadNfsRawData.uploadIssuerData",ex));
			return false;
		}
		
	}
    
    

public boolean uploadAcquirerData(CompareSetupBean setupBean,Connection con,MultipartFile file,FileSourceBean sourceBean ) {
	logger.info("***** ReadNfsRawData.uploadAcquirerData Start ****");
	int flag=1,batch=0;
	int getFileCount=0;
	String cycle = "";		
	try {
		//ADDED BY INT8624 FOR GETTING CYCLE FROM FILE NAME
		String fileName = file.getOriginalFilename();
		logger.info("FileName is "+fileName);
		String[] fileNames = fileName.split("_");
		if(fileNames.length >0)
			cycle = fileNames[1].substring(0,1);
		
		logger.info("Cycle is: "+cycle);
		String query="insert into nfs_nfs_acq_rawdata (participant_id,transaction_type,from_account_type,to_account_type,txn_serial_no,response_code,pan_number,member_number,approval_number,sys_trace_audit_no,transaction_date,transaction_time,merchant_category_cd,card_acc_settle_dt,card_acc_id,card_acc_terminal_id,card_acc_terminal_loc,acquirer_id	,acq_settle_date,txn_currency_code,txn_amount,actual_txn_amt,"
				+ "txn_activity_fee,acq_settle_currency_cd,acq_settle_amnt,acq_settle_fee,acq_settle_process_fee,txn_acq_conv_rate,part_id,createddate,createdby,filedate,cycle,fpan) values "
				+ "(?,?,?,?,?"
				+ ",?,?,?,?,?"
				+ ",?,?,?,?,?"
				+ ",?,?,?,?,?"
				+ ",?,?,?,?,?"
				+ ",?,?,?,?,"
				+ "sysdate(),?,str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d'),?,?) ";
			
		logger.info("query=="+query);
		
		boolean readdata = false;
			
	
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String thisLine = null;  
			try {

				logger.info("Reading data " + new Date().toString());
			
				PreparedStatement ps = con.prepareStatement(query);

				int insrt = 0;

				while ((thisLine = br.readLine()) != null) {
					 String []splitarray= null;
					 	
					 
					 

				            ps.setString(1, thisLine.substring(0,3));
				            ps.setString(2, thisLine.substring(3,5));
				            ps.setString(3, thisLine.substring(5,7));
				            ps.setString(4, thisLine.substring(7,9));
				            ps.setString(5, thisLine.substring(9,21));
				            ps.setString(6, thisLine.substring(21,23));
				            
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
							
							ps.setString(7, Update_Pan);
				            ps.setString(8, thisLine.substring(42,43));
				            ps.setString(9, thisLine.substring(43,49));
				            ps.setString(10, thisLine.substring(49,61));
				            ps.setString(11, thisLine.substring(61,67));
				            ps.setString(12, thisLine.substring(67,73));
				            ps.setString(13, thisLine.substring(73,77));
				            ps.setString(14, thisLine.substring(77,83));
				            ps.setString(15, thisLine.substring(83,98));
				            ps.setString(16, thisLine.substring(98,106));
				            ps.setString(17, thisLine.substring(106,146));
				            ps.setString(18, thisLine.substring(146,157));
				            ps.setString(19, thisLine.substring(157,163));
				            ps.setString(20, thisLine.substring(163,166));
				           /* ps.setString(21, thisLine.substring(166, 179).replaceAll("^0*","0") + "."
									+ thisLine.substring(179, 181));*/
				            ps.setString(21, thisLine.substring(166, 179).replaceAll("^0+(?!$)", ""));
				            ps.setString(22, thisLine.substring(181,196));
				            ps.setString(23, thisLine.substring(196,211));
				            ps.setString(24, thisLine.substring(211,214));
				           /* ps.setString(25, thisLine.substring(214, 227).replaceAll("^0*","0") + "."
									+ thisLine.substring(227, 229));*/
				            
				            //for mysql
				            ps.setString(25, thisLine.substring(214, 227).replaceAll("^0+(?!$)", ""));
				           
				            ps.setString(26, thisLine.substring(229,244));
				            ps.setString(27, thisLine.substring(244,259));
				            ps.setString(28, thisLine.substring(259,274));
				            
				           
				            ps.setInt(29, 1);
				           
				            ps.setString(30,"AUTOMATION");
				            ps.setString(31, cycle); //added by int8624
				         
				            //Added by INT8624 FOR ENCRYPTING PAN
						/*	String encyp_pan = null;
							PreparedStatement ency_pst = con.prepareStatement("select ibkl_encrypt_decrypt.ibkl_set_encrypt_val('"+thisLine.substring(23, 42)+"') ENC from dual");
							ResultSet rs = ency_pst.executeQuery();
							while(rs.next())
							{
								encyp_pan = rs.getString("ENC");
							}
							ency_pst.close();
							rs.close();
				            ps.setString(32, encyp_pan); //added by int8624 */
				            
				            ps.setString(32, thisLine.substring(23, 42)); //added by int8624 */
				            
				            
				            ps.addBatch();
				            flag++;
							
							if(flag == 5000)
							{
								flag = 1;
								
								ps.executeBatch();
								logger.info("Executed batch is "+batch);
								batch++;
								getFileCount++;
							}
							
				            
				            //insrt = ps.executeUpdate();

					}

				ps.executeBatch();
				
				/*** ENCRYPTING PAN IN RAWDATA ****/
				logger.info("Updation Starting ");
				String updateQuery ="update nfs_nfs_acq_rawdata set fpan = aes_encrypt(rtrim(ltrim(fpan)),'key_dbank') "
							+ "where cycle = '"+cycle+"' and filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')";
				
				PreparedStatement pstmt = con.prepareStatement(updateQuery);
				pstmt.execute();
				
				logger.info("Updation done");
				
				/** Done **/
				
				getFileCount++;
				br.close();
				ps.close();
				logger.info("Reading data " + new Date().toString());
				
				logger.info("***** ReadNfsRawData.uploadAcquirerData End ****");
				
				if(getFileCount >0) {
					
				
					return  true;
				} else {
					
					return false;
				}

			}catch(Exception ex){
				
				logger.error(" error in ReadNfsRawData.uploadAcquirerData", new Exception("ReadNfsRawData.uploadAcquirerData",ex));
				 return false;
				
			}
	}catch(Exception ex) {
		
		logger.error(" error in ReadNfsRawData.uploadAcquirerData", new Exception("ReadNfsRawData.uploadAcquirerData",ex));
		return false;
	}
	
}


}
