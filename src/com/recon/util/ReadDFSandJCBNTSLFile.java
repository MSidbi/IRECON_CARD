package com.recon.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;
import com.recon.model.NFSSettlementBean;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadDFSandJCBNTSLFile {

	private static final Logger logger = Logger.getLogger(ReadDFSandJCBNTSLFile.class);
	
    public HashMap<String, Object> DFSfileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
    		String tableName = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<String, Object>();
          String getTableName = "select tablename from main_filesource where filename = ? and file_category = ? and file_subcategory = ?";
          PreparedStatement pstmt = con.prepareStatement(getTableName);
          pstmt.setString(1, beanObj.getFileName());
          pstmt.setString(2, beanObj.getCategory());
          pstmt.setString(3, beanObj.getStSubCategory());
          ResultSet rs = pstmt.executeQuery();
          int cellCount = 4,count = 1;;
          int srl_no = 1;
          long start = System.currentTimeMillis();
          while(rs.next())
          {
        	  tableName = (String)rs.getString("TABLENAME");
          }
          
          /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                        + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
           String sql = "INSERT INTO "+tableName.toLowerCase()+"(international_trans_pulse,no_of_txns,debit,credit,cycle,filedate,createdby,createddate,sr_no) VALUES(?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,sysdate(),?)";
           String insertDollarVal = "insert into main_dollar_value (network,dollar_rate,filedate,createdby,createddate) VALUES(?,?,str_to_date(?,'%Y/%m/%d'),?,sysdate())";
           PreparedStatement ps = con.prepareStatement(sql);
           pstmt = con.prepareStatement(insertDollarVal);
   		   pstmt.setString(1, "DFS");
   		   con.setAutoCommit(false);
          // Connection con = getConnection();
           try {
        	  Path tempDir = Files.createTempDirectory(""); 
        	  File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
        	  file.transferTo(tempFile);
        	  String content = Jsoup.parse(tempFile,"UTF-8").toString(); 
			  org.jsoup.nodes.Document html = Jsoup.parse(content);
			  if (content != null) 
			  { 
				  //GETTING DOLLAR VALUE FIRST
				  Elements contents = html.getElementsByTag("th");
				  logger.info("********************** Reading th tags ****************");
				  
				  for(Element a : contents)
				  {
					  
					  if(a.text().contains("Interchange Rate"))
					  {
						  //INSERT DOLLAR VALUE IN DOLLAR TABLE
						  System.out.println("Dollar Value is "+a.nextElementSibling().text());
						  pstmt.setString(2, a.nextElementSibling().text());
						  pstmt.setString(3, beanObj.getDatepicker());
						  pstmt.setString(4, beanObj.getCreatedBy());
                      	  pstmt.execute();
                      	  con.commit();
                      	  break;
					  }
				  }
				  
				  contents = html.getElementsByTag("td");
				  System.out.println("************************************* Reading td tags *****************************************************");
				  
				  count = 1;
				  for(Element a : contents)
				  {
					 if(count == 1 && a.text().equalsIgnoreCase("Dispute Adjustments"))
					 {
							  break;
					 }
					 else if(count == 1 && a.text().trim().equalsIgnoreCase(""))
					 {
						 continue;
					 }
					 else
					 {
							 if(count == 1)
								 totalcount++;

							 System.out.println(count +"is: "+a.text());
							 ps.setString(count, a.text());
							 count++;
					 }
					 if(count == cellCount+1)
					 {
						 ps.setInt(5, 1);
                         ps.setString(6, beanObj.getDatepicker());
                         ps.setString(7, beanObj.getCreatedBy());
                         ps.setInt(8, srl_no++);
                         ps.addBatch();
                         count = 1;
					 }
				  }
				  
				  ps.executeBatch();
                  con.commit();
                  con.close();
                  long end = System.currentTimeMillis();
                  System.out.println("start and end diff" + (start - end));
                  System.out.println(" table  insert");
                 // response = ADDBACIDSTAT();

                  System.out.println("Data Inserted");
                  mapObj.put("result", true);
                  mapObj.put("count", totalcount);
			  }
			  //delete the file from temp folder
			  FileUtils.forceDelete(tempFile);
				logger.info(tempFile.exists());
        	   
           } catch (Exception e) {
        	   logger.info("Exception in ReadDFSandJCBNTSL "+e);
                  e.printStackTrace();
                  mapObj.put("result", false);
                  mapObj.put("count", totalcount);
                  try {
                        con.rollback();
                  } catch (SQLException ex) {
                        ex.printStackTrace();
                  }
           }
           return mapObj;
    }

    public HashMap<String, Object> JCBfileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
        int response = 0;String tableName = null;
        int totalcount = 0;
        HashMap<String, Object> mapObj = new HashMap<String, Object>();
       String getTableName = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = ? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?";
       PreparedStatement pstmt = con.prepareStatement(getTableName);
       pstmt.setString(1, beanObj.getFileName());
       pstmt.setString(2, beanObj.getCategory());
       pstmt.setString(3, beanObj.getStSubCategory());
       ResultSet rs = pstmt.executeQuery();
       int srl_no = 1;
       int cellCount = 4,count = 1;
       long start = System.currentTimeMillis();
       while(rs.next())
       {
     	  tableName = (String)rs.getString("TABLENAME");
       }
       
       /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                     + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
        String sql = "INSERT INTO "+tableName+"(INTERNATIONAL_TRANSACTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
        String insertDollarVal = "INSERT INTO MAIN_DOLLAR_VALUE (NETWORK,DOLLAR_RATE,FILEDATE,CREATEDBY,CREATEDDATE) VALUES(?,?,TO_DATE(?,'DD/MM/YYYY'),?,SYSDATE)";
        PreparedStatement ps = con.prepareStatement(sql);
        pstmt = con.prepareStatement(insertDollarVal);
		   pstmt.setString(1, "DFS");
		   int dollarCount =2;
		   con.setAutoCommit(false);
       // Connection con = getConnection();
        try {

      	  Path tempDir = Files.createTempDirectory(""); 
      	  File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
      	  file.transferTo(tempFile);
      	  String content = Jsoup.parse(tempFile,"UTF-8").toString(); 
			  org.jsoup.nodes.Document html = Jsoup.parse(content);
			  if (content != null) 
			  { 
				  //GETTING DOLLAR VALUE FIRST
				  Elements contents = html.getElementsByTag("th");
				  logger.info("********************** Reading th tags ****************");
				  
				  for(Element a : contents)
				  {
					  
					  if(a.text().contains("Interchange Rate"))
					  {
						  //INSERT DOLLAR VALUE IN DOLLAR TABLE
						  System.out.println("Dollar Value is "+a.nextElementSibling().text());
						  if(a.text().contains("JCB"))
						  {
							  pstmt.setString(1, "JCB");
						  }
						  else
							  pstmt.setString(1, "UPI");
						  
						  pstmt.setString(2, a.nextElementSibling().text());
						  pstmt.setString(3, beanObj.getDatepicker());
						  pstmt.setString(4, beanObj.getCreatedBy());
                    	  pstmt.execute();
                    	  con.commit();
                    	  dollarCount--;
					  }
				  }
				  
				  contents = html.getElementsByTag("td");
				  System.out.println("************************************* Reading td tags *****************************************************");
				  
				  count = 1;
				  for(Element a : contents)
				  {
					 if(count == 1 && a.text().equalsIgnoreCase("Dispute Adjustments"))
					 {
							  break;
					 }
					 else if(count == 1 && a.text().trim().equalsIgnoreCase(""))
					 {
						 continue;
					 }
					 else
					 {
							 if(count == 1)
								 totalcount++;

							 System.out.println(count +"is: "+a.text());
							 ps.setString(count, a.text());
							 count++;
					 }
					 if(count == cellCount+1)
					 {
						 ps.setInt(5, 1);
                       ps.setString(6, beanObj.getDatepicker());
                       ps.setString(7, beanObj.getCreatedBy());
                       ps.setInt(8, srl_no++);
                       ps.addBatch();
                       count = 1;
					 }
				  }
				  
				  ps.executeBatch();
                con.commit();
                con.close();
                long end = System.currentTimeMillis();
                System.out.println("start and end diff" + (start - end));
                System.out.println(" table  insert");
               // response = ADDBACIDSTAT();

                System.out.println("Data Inserted");
                mapObj.put("result", true);
                mapObj.put("count", totalcount);
			  }
			  //delete the file from temp folder
			  FileUtils.forceDelete(tempFile);
				logger.info(tempFile.exists());
         
			  
        } catch (Exception e) {
     	   logger.info("Exception in ReadDFSandJCBNTSL "+e);
               e.printStackTrace();
               mapObj.put("result", false);
               mapObj.put("count", totalcount);
               try {
                     con.rollback();
               } catch (SQLException ex) {
                     ex.printStackTrace();
               }
        }
        return mapObj;
 }



}
