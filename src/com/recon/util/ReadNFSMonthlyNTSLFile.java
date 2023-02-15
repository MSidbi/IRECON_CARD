package com.recon.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import com.recon.control.ManualKnockoffController;
import com.recon.model.NFSSettlementBean;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

public class ReadNFSMonthlyNTSLFile {
	
	private static final Logger logger = Logger.getLogger(ReadNFSMonthlyNTSLFile.class);
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	
    public HashMap<String, Object> fileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
           int response = 0;String tableName = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<String, Object>();
          String getTableName = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILENAME = ? AND FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?";
          PreparedStatement pstmt = con.prepareStatement(getTableName);
          pstmt.setString(1, beanObj.getFileName());
          pstmt.setString(2, beanObj.getCategory());
          pstmt.setString(3, beanObj.getStSubCategory());
          ResultSet rs = pstmt.executeQuery();
          String description = null;
          double no_of_txns = 0,debit= 0, credit = 0;
          int count = 0;
          
          while(rs.next())
          {
        	  tableName = (String)rs.getString("TABLENAME");
          }
          
          /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                        + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
           String sql = "INSERT INTO "+tableName+"(DATES,INTERCHANGE_FEE_TXN,CGST_TXN,SGST_TXN,IGST_TXN,INTERCHANGE_FEE_DISP,CGST_DISP,SGST_DISP,IGST_DISP,INTERCHANGE_FEE_REV,CGST_REV,SGST_REV,IGST_REV,CREATEDBY,CREATEDDATE,SETTLEMENT_MONTH) "
           		+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?)";
           
          // Connection con = getConnection();
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  InputStream inputStream = file.getInputStream();
                  HSSFWorkbook wb = new HSSFWorkbook(inputStream);
                  HSSFSheet sheet = wb.getSheetAt(0);
			
                  
                  FormulaEvaluator formulaEvaluate = wb.getCreationHelper().createFormulaEvaluator();
 OUTER:   for (Row r : sheet) {
                        totalcount++;
                        if (r.getRowNum() > 16) {
                        	 count++;
                               int cellCount = 1;
                               for (Cell c : r) {
                                      switch(formulaEvaluate.evaluateInCell(c).getCellType())
                                      {
                                    	  case Cell.CELL_TYPE_STRING:
                                    		  if(c.getStringCellValue().equalsIgnoreCase("NFS is a multilateral ATM network of member banks"))
                                    		  {
                                    			  totalcount--;
                                    			  break OUTER;                                    			  
                                    		  }
                                    		  ps.setString(cellCount, c.getStringCellValue());
                                    		  cellCount++;
                                    		  break;
                                    	  case Cell.CELL_TYPE_NUMERIC:
                                    		  if(cellCount == 1)
                                    		  {
                                    			  logger.info("It is date value");
                                    			  String getdate ="select TO_CHAR(TO_DATE('"+beanObj.getDatepicker()+"','MM/YYYY')+"+(count-1)+",'DD/MM/YYYY') as dates from dual";
                                    			  logger.info("getdate query is "+getdate);
                                    			  PreparedStatement pst = con.prepareStatement(getdate);
                                    			  ResultSet rs1 = pst.executeQuery();
                                    			  while(rs1.next())
                                    			  {
                                    				  logger.info("date is "+rs1.getString("dates"));
                                    				  ps.setString(cellCount, rs1.getString("dates"));
                                    				  
                                    			  }
                                    			  
                                    		  }
                                    		  else
                                    		  {
                                    			  ps.setString(cellCount, c.getNumericCellValue()+"");
                                    		  }
                                    		  cellCount++;
                                    		  break;
                                    		  
                                      }
                                    	  
                                    	  //ps.setString(cellCount, c.getStringCellValue());
                                      
                          
                               }
                               ps.setString(cellCount++, beanObj.getCreatedBy());
                               logger.info("cellcount is "+cellCount);
                               ps.setString(cellCount, beanObj.getDatepicker()); // ONLY MONTH AND YEAR TO BE ENTERED
                               ps.addBatch();
                               
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
           } catch (Exception e) {
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
