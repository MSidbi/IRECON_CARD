package com.recon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;

public class CardTocard_cbs {
public boolean uploadDatac2c(CompareSetupBean setupBean, Connection conn,
		MultipartFile file, FileSourceBean sourceBean ) throws IOException {
		
		int flag=1,batch=0;
				
				InputStream fis = null;
				boolean readdata = false;
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				String thisLine = null;  
				try {
					
					System.out.println("Reading data "+new Date().toString());
					
					 String insert = "INSERT INTO CARD_TO_CARD_CBS_RAWDATA "
					 		+ "(FORACID,TRAN_DATE,E,AMOUNT,BALANCE,TRAN_ID,VALUE_DATE,REMARKS,REF_NO,PARTICULARALS,CONTRA_ACCOUNT,FILEDATE,PART_ID,CREATEDDATE,CREATEDBY)"
					 		+ " values (?,?,?,?,?,?,?,?,?,?,?"
					 		+ ",to_date(?,'dd/mm/yyyy'),?,sysdate,'AUTOMATION')";
				        
				        PreparedStatement ps = conn.prepareStatement(insert);
				        
				        int insrt=0;
		
					while ((thisLine = br.readLine()) != null) {
						
						 String []splitarray= null;
		
						if (thisLine.contains("------")) {
		
							readdata = true;
		
						} if(thisLine.contains("End Of the Statement Of Account Report")) {
							
							readdata =false;
							
							break;
							
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
					          
					            ps.setString(12, setupBean.getFileDate());
					            System.out.println("Date:-->>"+ setupBean.getFileDate());
					            ps.setInt(13, 1);
					           
					           
					            
					            
					            
					            
					            
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

}
