package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;


public class ReadNUploadCBSFiles {
	
	String partid;
	private int Part_id;
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public boolean uploadCBSData(String fileName,String filepath) {


		String[] filenameSplit = fileName.split("_");
		
		try{
		boolean uploaded = false;
		System.out.println(fileName);
		if(filenameSplit[0].contains("CBS702"))//ISSUER
		{
			System.out.println("Entered CBS File is Issuer");
			ReadNUploadCBSIssuer readIssuer = new ReadNUploadCBSIssuer();
			uploaded = readIssuer.uploadCBSData(fileName, filepath);
		}
		else if(filenameSplit[0].contains("CBS703"))//ACQUIRER
		{
			System.out.println("Entered CBS File is Acquirer");
			ReadNUploadCBSAcquirer readacquirer = new ReadNUploadCBSAcquirer();
			uploaded = readacquirer.uploadCBSData(fileName, filepath);
			
		}
		else if(filenameSplit[0].equalsIgnoreCase("CBSC43") || filenameSplit[0].equalsIgnoreCase("CBS43"))//ONUS
		{
			System.out.println("Entered CBS File is ONUS");
			ReadNUploadCBSOnus readOnus = new ReadNUploadCBSOnus();
			uploaded = readOnus.Read_CBSData(fileName, filepath);
			
		}
		else
		{
			System.out.println("Entered File is Wrong");
			return false;
		}
		return true;

		} catch (Exception e) {

			System.out.println("Error Occured");
			e.printStackTrace();
			
			return false;
		}
	
		
	}
	
	
	
	
	
	
	
	public static void main(String []args) {
	    	

         
			
	    	ReadNUploadCBSFiles readcbs = new ReadNUploadCBSFiles();
			
			
				Scanner scanner = new Scanner(System.in);
				System.out.print("Enter file path: ");
				System.out.flush();
				String filename = scanner.nextLine();
				File file = new File(filename);
				//Subcategory IS NOT NEEDED IN CASE OF  AS SINGLE FILE IS USED FOR BOTH
				/*System.out.println("Enter Sub Category ");
				System.out.flush();
				String stSubCategory = scanner.nextLine();*/
				
			//	System.out.println(file.getName());
				
				
				/*File f = new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS");
				if(!(f.exists())) {
					
					if(f.mkdir()) {
						
						System.out.println("directory created");
					}
				}*/
				
				
				/*if(file.renameTo(new File("\\\\10.143.11.50\\led\\DCRS\\AMEXCBS\\" + file.getName()))) {
					
					System.out.println("File Moved Successfully");
					
					readcbs.uploadCBSData(file.getName());
					
					System.out.println("Process Completed");
					
				}else {
					
					System.out.println("Error Occured while moving file");
				}*/
				
				
				if(readcbs.uploadCBSData(file.getName(),file.getPath()))
				{
					System.out.println("File uploaded successfully");
				}
				else
					System.out.println("File uploading failed");
				
				
				
		    
	    }







	public HashMap<String, Object> uploadCBSData(CompareSetupBean setupBean,
			Connection connection, MultipartFile file, FileSourceBean sourceBean) {


		HashMap<String, Object> output = new HashMap<String, Object>();
		//commented for UCO by INT8624
		/*if(setupBean.getFileType().equals("MAN")){
					
					//flag="MANUPLOAD_FLAG"; 
					Part_id=2;
					//man_flag="Y";
					
				}else{
					
					//flag="UPLOAD_FLAG";
					//upload_flag="Y";
					Part_id=1;
				}*/
		
		try{
		boolean uploaded = false;
		
		//modified by INT8624 FOR UCO
		System.out.println("Entered CBS File IS "+file.getOriginalFilename());
		
		if(file.getOriginalFilename().contains("Atm")|| file.getOriginalFilename().contains("Pos"))
			return uploadISSData(setupBean, connection, file, sourceBean);
		else
			return uploadGLReonReport(setupBean, connection, file, sourceBean);
		
		

		} catch (Exception e) {

			
			System.out.println("Error Occured");
			e.printStackTrace();
			output.put("result", false);
			output.put("msg", "Exception Occured");
			return output;
		}
	
		
	}


	
public boolean uploadONUSData(CompareSetupBean setupBean,
		Connection con, MultipartFile file, FileSourceBean sourceBean) {

		
		int flag=1,batch=0;
		
		InputStream fis = null;
		boolean readdata = false;
		

		
		String thisLine = null;  
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
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
					
					 ps.setString(15,null);
					 ps.setString(16,null);
					
					  splitarray = thisLine.split(Pattern.quote("|"));//Pattern.quote(ftpBean.getDataSeparator())

			            for(int i=0;i<splitarray.length;i++){

			                String value = splitarray[i];
			                if( !value.trim().equalsIgnoreCase("") ){
			                	
			                //2 valuedate
			                // 4 tran_particular	
			                	
			                	/*System.out.println(splitarray[4]);
			                	if(i==2) {
			                		value = value +" "+ splitarray[4].substring(19,27);
			                		ps.setString(srl,value.trim());
			                		
			                	} else {
			                		
			                		ps.setString(srl,value.trim());
			                	}*/
			                	
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
			            ps.setString(19, setupBean.getFileDate());
			            
			            
			            
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
		}
	}
		
		
public boolean uploadACQData(CompareSetupBean setupBean,
		Connection con, MultipartFile file, FileSourceBean sourceBean) {
	
	int flag=1,batch=0;
			
			InputStream fis = null;
			boolean readdata = false;
			
			
			String thisLine = null;  
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				System.out.println("Reading data "+new Date().toString());
				
				 String insert = "INSERT INTO CBS_AMEX_RAWDATA "
				 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,pstd_user_id ,ENTRY_DATE,VFD_DATE,PARTICULARALS2,Part_id,FILEDATE)"
				 		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'dd/mm/yyyy')))";
			        
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
				          
				            
				            ps.setInt(16, Part_id);
				            ps.setString(17, setupBean.getFileDate());
				            
				            
				            
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
	
public HashMap<String, Object> uploadISSData(CompareSetupBean setupBean,
		Connection con, MultipartFile file, FileSourceBean sourceBean) {

	HashMap<String, Object> output = new HashMap<String, Object>();	
	String stLine = null;
	int lineNumber = 0,sr_no = 1, batchNumber = 0, batchSize = 0;
	boolean batchExecuted = false;
	
	String InsertQuery = "insert into cbs_dhana_rawdata_temp(systrnno, msg_type, accno, proc_code, amount, traceno, valuedate, settledate, issid, acqid, respcode, terminalid, cod_txn_ccy, cod_lcy, identifier, location, rrno, blank_field, authorizationcode, card_number, createdby, filedate) "+
			"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'))";
	
	String delQuery = "delete from cbs_dhana_rawdata_temp";
	try
	{
		PreparedStatement delpst = con.prepareStatement(delQuery.toLowerCase());
		delpst.execute();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
		PreparedStatement ps = con.prepareStatement(InsertQuery);
		
		while((stLine = br.readLine()) != null)
		{
			lineNumber++;
			batchExecuted = false;
			sr_no = 1 ;
			
			String[] splitData = stLine.split("\\|");
			
			for(int i = 1; i <= splitData.length ; i++)
			{
					//System.out.println("i "+i+" data "+splitData[i-1]);
					ps.setString(sr_no++, splitData[i-1].trim());
			}
			
			//System.out.println(sr_no);
			ps.setString(sr_no++, setupBean.getCreatedBy());
			ps.setString(sr_no++, setupBean.getFileDate());
			
			ps.addBatch();
			batchSize++;
			
			if(batchSize == 10000)
			{
				batchNumber++;
				System.out.println("Batch Executed is "+batchNumber);
				ps.executeBatch();
				batchSize = 0;
				batchExecuted = true;
			}
			
		}
		
		if(!batchExecuted)
		{
			batchNumber++;
			System.out.println("Batch Executed is "+batchNumber);
			ps.executeBatch();
		}
		
		br.close();
		ps.close();
        System.out.println("Reading data "+new Date().toString());
        
        output.put("result", true);
        output.put("msg", "Records Count is "+lineNumber);
        
        return output;
        
		
	}
	catch(Exception e)
	{
		
		try
		{
		con.rollback();
		}
		catch(Exception ex)
		{
			//logger.info("Exception in uploadISSData "+e);
			System.out.println("Exception in uploadISSData "+e);
			output.put("result", false);
			output.put("msg", "Issue at Line Number "+lineNumber);
            return output;  
		}
		System.out.println("Issue at line "+stLine);
		System.out.println("Exception in uploadISSData "+e);
		//return false;
		output.put("result", false);
		output.put("msg", "Exception Occured");
		return output;
	}

}

public HashMap<String, Object> uploadGLReonReport(CompareSetupBean setupBean,
		Connection con, MultipartFile file, FileSourceBean sourceBean) {

	HashMap<String, Object> output = new HashMap<String, Object>();	
	String stLine = null;
	int lineNumber = 0,sr_no = 1, batchNumber = 0, batchSize = 0, index = 1;
	boolean batchExecuted = false;
	int executedBatch = 0;
	String improperData = null;
	
	String InsertQuery = "INSERT INTO cbs_dhana_recon_gl_rawdata(user_branch_code, gl_code, drcr_ind, lcy_amount, value_date, related_reference, related_account, book_date, txn_ref_no, txn_desc, cod_user_id, account_branch_code, filedate, createddate, createdby) "+
			"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'), sysdate(), ?)";
	
	//String delQuery = "delete from cbs_dhana_rawdata_temp";
	try
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
		PreparedStatement ps = con.prepareStatement(InsertQuery);
		
		while((stLine = br.readLine()) != null)
		{
			lineNumber++;
			batchExecuted = false;
			sr_no = 1 ;
			
			if(lineNumber >= 2)
			{
				String[] datas = stLine.split(",");

				/*if(datas.length == 13)
				{
					improperData =null;
					index =1;
					for(String data: datas)
					{
						if(index == 10 || index == 11)
						{
							if(index == 10)
							{
								improperData = data;
								index++;
								continue;
							}
							else if(index == 11 && improperData != null)
							{
								//System.out.println("data "+data.trim());
								ps.setString(sr_no++, improperData+" "+data.trim());
								index++;
							}
							else
							{
								//System.out.println("data "+data.trim());
								ps.setString(sr_no++, data.trim());
								index++;
							}
						}
						else
						{
							//System.out.println("data "+data.trim());
							ps.setString(sr_no++, data.trim());
							index++;
						}
						
					}
				}*/
				if(datas.length > 12)
				{
					improperData ="";
					index =1;
					for(String data: datas)
					{
						if(index > 9)
						{
							if(index == datas.length || index == (datas.length-1))
							{
								if(index == (datas.length-1))
								{
									System.out.println("improper data "+improperData);
									ps.setString(sr_no++, improperData);
									ps.setString(sr_no++, data.trim());
									System.out.println("data "+data.trim());
									index++;
								}
								else
								{
									System.out.println("data "+data.trim());
									ps.setString(sr_no++, data.trim());
									index++;
								}
							}
							else 
							{
								improperData = improperData + data.trim();
								index++;
								continue;
							}
						}
						else
						{
							System.out.println("data "+data.trim());
							ps.setString(sr_no++, data.trim());
							index++;
						}
						
					}
				}
				else
				{
					for(String data: datas)
					{
						//System.out.println("data "+data.trim());
						ps.setString(sr_no++, data.trim());
					}
				}

				ps.setString(sr_no++, setupBean.getFileDate());
				ps.setString(sr_no++, setupBean.getCreatedBy());
				
				ps.addBatch();
				batchNumber++;

				if(batchNumber == 5000)
				{
					executedBatch++;
					System.out.println("Batch Executed is "+executedBatch);
					ps.executeBatch();
					batchNumber = 0;
					batchExecuted = true;
				}
			}
			
		}
		
		if(!batchExecuted)
		{
			batchNumber++;
			System.out.println("Batch Executed is "+batchNumber);
			ps.executeBatch();
		}
		
		br.close();
		ps.close();
        System.out.println("Reading data "+new Date().toString());
        
        output.put("result", true);
        output.put("msg", "Records Count is "+lineNumber);
        
        return output;
        
		
	}
	catch(Exception e)
	{
		
		try
		{
		con.rollback();
		}
		catch(Exception ex)
		{
			//logger.info("Exception in uploadISSData "+e);
			System.out.println("Exception in uploadGLReonReport "+e);
			output.put("result", false);
			output.put("msg", "Issue at Line Number "+lineNumber);
            return output;  
		}
		
		System.out.println("Issue at line "+stLine);
		System.out.println("Exception in uploadGLReonReport "+e);
		//return false;
		output.put("result", false);
		output.put("msg", "Exception Occured");
		return output;
	}

}

}



