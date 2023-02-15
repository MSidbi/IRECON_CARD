package com.recon.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.recon.control.NFSSettlementController;
import com.recon.model.NFSSettlementBean;

public class ReadDFSandJCBNTSLFile_bk {

	private static final Logger logger = Logger.getLogger(ReadDFSandJCBNTSLFile_bk.class);
	
    public HashMap<String, Object> DFSfileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
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
          int srl_no = 1;
          
          while(rs.next())
          {
        	  tableName = (String)rs.getString("TABLENAME");
          }
          
          /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                        + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
           String sql = "INSERT INTO "+tableName+"(INTERNATIONAL_TRANS_PULSE,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
           String insertDollarVal = "INSERT INTO MAIN_DOLLAR_VALUE (NETWORK,DOLLAR_RATE,FILEDATE,CREATEDBY,CREATEDDATE) VALUES(?,?,TO_DATE(?,'DD/MM/YYYY'),?,SYSDATE)";
           
          // Connection con = getConnection();
           try {
        	   int extn = file.getOriginalFilename().indexOf(".");
        	   FormulaEvaluator formulaEvaluate = null;
        	   Workbook wb = null;
       			Sheet sheet = null;
        	   
        	   if (file.getOriginalFilename().substring(extn)
						.equalsIgnoreCase(".XLS")) {
			 
				//	book = new HSSFWorkbook(file.getInputStream()) ;
					wb = new HSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
				
				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
					 
					// book = new XSSFWorkbook(file.getInputStream());
					wb = new XSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
				}
        	   
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  InputStream inputStream = file.getInputStream();
                 /* HSSFWorkbook wb = new HSSFWorkbook(inputStream);
                  HSSFSheet sheet = wb.getSheetAt(0);
			*/
                  
                //  FormulaEvaluator formulaEvaluate = wb.getCreationHelper().createFormulaEvaluator();
 OUTER:   for (Row r : sheet) {
                        totalcount++;
                        if( r.getRowNum() == 2)
                        {
                        	//NOW IT WILL BE DOLLAR RECORD
                    		pstmt = con.prepareStatement(insertDollarVal);
                    		pstmt.setString(1, "DFS");
                        	int cellcount = 2;
                        	for (Cell c : r) {

                        		switch(formulaEvaluate.evaluateInCell(c).getCellType())
                        		{
                        		/*case Cell.CELL_TYPE_STRING:
                        			if(c.getStringCellValue().equalsIgnoreCase("Dispute Adjustments"))
                        			{
                        				totalcount--;
                        				break OUTER;                                    			  
                        			}
                        			System.out.println("Cell value in string case is "+c.getStringCellValue());
                        			ps.setString(cellcount, c.getStringCellValue());
                        			cellcount++;
                        			break;*/
                        		case Cell.CELL_TYPE_NUMERIC:
                        			String digit = c.getNumericCellValue() +"";
                        			if(digit.contains("E"))
                        			{
                        				digit = c.getNumericCellValue() +"";
                        				double d = Double.parseDouble(digit);
                        				System.out.println(digit);
                        				BigDecimal bd = new BigDecimal(d);
                        				System.out.println("Bigdecimal is "+bd);
                        				/* digit = bd.round(new MathContext(15)).toPlainString();
                               	   System.out.println(digit);
                        				 */
                        				String tryDigit = bd+"";
                        				int indexOfDot = tryDigit.indexOf(".");
                        				int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                        				System.out.println(secondDigit);
                        				if(secondDigit > 5)
                        				{
                        					digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                        				}
                        				else
                        				{
                        					BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                        					System.out.println("digit is " + db);
                        					digit = db.toPlainString();
                        				}

                        			}
                        			System.out.println("digit "+digit);
                        			pstmt.setString(cellcount, digit);
                        			cellcount++;
                        			break;
                        		}
                        		
                        	}
                        	pstmt.setString(cellcount, beanObj.getDatepicker());
                        	pstmt.setString(++cellcount, beanObj.getCreatedBy());
                        	pstmt.execute();
                        	con.commit();
                        }
                        if (r.getRowNum() > 4) {
                               int count = 0;
                               int cellCount = 1;
                               for (Cell c : r) {

                            	   count++;
                            	   boolean crossCheck = false;
                            	   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                            	   {
                            	   case Cell.CELL_TYPE_STRING:
                            		   crossCheck = true;
                            		   if(c.getStringCellValue().equalsIgnoreCase("Dispute Adjustments"))
                            		   {
                            			   totalcount--;
                            			   break OUTER;                                    			  
                            		   }
                            		   else if(cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                            		   {
                            			   continue OUTER;
                            		   }
                            		   System.out.println("Cell value in string case is "+c.getStringCellValue());
                            		   ps.setString(cellCount, c.getStringCellValue());
                            		   break;
                            	   case Cell.CELL_TYPE_NUMERIC:
                            		   //System.out.println("Numeric cell value is "+c.getNumericCellValue());
                            		   crossCheck = true;
                                       String digit = c.getNumericCellValue() +"";
                                       if(digit.contains("E"))
                                       {
                                    	   digit = c.getNumericCellValue() +"";
                                    	   double d = Double.parseDouble(digit);
                                    	   System.out.println(digit);
                                           BigDecimal bd = new BigDecimal(d);
                                           System.out.println("Bigdecimal is "+bd);
                                    	  /* digit = bd.round(new MathContext(15)).toPlainString();
                                    	   System.out.println(digit);
                                    	   */
                                           String tryDigit = bd+"";
                                           int indexOfDot = tryDigit.indexOf(".");
                                           int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                                           System.out.println(secondDigit);
                                           if(secondDigit > 5)
                                           {
                                        	   digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                                                                            }
                                           else
                                           {
                                        	   BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                                        	   System.out.println("digit is " + db);
                                        	   digit = db.toPlainString();
                                           }
                                       
                                       }
                                       System.out.println("digit "+digit);
                            		   ps.setString(cellCount, digit);
                            		   break;
                            	   default:
                            		  // System.out.println("Inside default Loop "+c.getStringCellValue());
                            		   crossCheck = true;
                            		   if(cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                            		   {
                            			   continue OUTER;
                            		   }
                            		   System.out.println("cell value is "+c.getStringCellValue());
                            		   ps.setString(cellCount, c.getStringCellValue());
                            		   break;

                            	   }

                            	   if(!crossCheck)
                            	   {
                            		   ps.setString(cellCount, c.getStringCellValue());
                            	   }
                            	   cellCount++;

                               }
                               ps.setInt(5, 1);
                               ps.setString(6, beanObj.getDatepicker());
                               ps.setString(7, beanObj.getCreatedBy());
                               ps.setInt(8, srl_no++);
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
       String description = null;
       double no_of_txns = 0,debit= 0, credit = 0;
       int srl_no = 1;
       
       while(rs.next())
       {
     	  tableName = (String)rs.getString("TABLENAME");
       }
       
       /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                     + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
        String sql = "INSERT INTO "+tableName+"(INTERNATIONAL_TRANSACTIONS,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
        String insertDollarVal = "INSERT INTO MAIN_DOLLAR_VALUE (NETWORK,DOLLAR_RATE,FILEDATE,CREATEDBY,CREATEDDATE) VALUES(?,?,TO_DATE(?,'DD/MM/YYYY'),?,SYSDATE)";
        
       // Connection con = getConnection();
        try {
     	   int extn = file.getOriginalFilename().indexOf(".");
     	   FormulaEvaluator formulaEvaluate = null;
     	   Workbook wb = null;
    			Sheet sheet = null;
     	   
     	   if (file.getOriginalFilename().substring(extn)
						.equalsIgnoreCase(".XLS")) {
			 
				//	book = new HSSFWorkbook(file.getInputStream()) ;
					wb = new HSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
				
				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
					 
					// book = new XSSFWorkbook(file.getInputStream());
					wb = new XSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
				}
     	   
               long start = System.currentTimeMillis();
               System.out.println("start");
               con.setAutoCommit(false);
               PreparedStatement ps = con.prepareStatement(sql);
               PreparedStatement dollar_pst = con.prepareStatement(insertDollarVal);
               @SuppressWarnings("unused")
			InputStream inputStream = file.getInputStream();
              /* HSSFWorkbook wb = new HSSFWorkbook(inputStream);
               HSSFSheet sheet = wb.getSheetAt(0);
			*/
               
             //  FormulaEvaluator formulaEvaluate = wb.getCreationHelper().createFormulaEvaluator();
               int dollar_row = 0;
OUTER:   for (Row r : sheet) {
                     totalcount++;
                    
                     boolean dollar_val = false;
                     if( r.getRowNum() < 5)
                     {
                    	 
                     	//NOW IT WILL BE DOLLAR RECORD
                 		/*pstmt = con.prepareStatement(insertDollarVal);
                 		if(r.getRowNum() == 2)
                 			pstmt.setString(1, "JCB");
                 		else
                 			pstmt.setString(1, "UPI");*/
                     	int cellcount = 1;
             	for (Cell c : r) {

                     		switch(formulaEvaluate.evaluateInCell(c).getCellType())
                     		{
                     		case Cell.CELL_TYPE_STRING:
                     			if(cellcount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                     			{
                     				continue OUTER;
                     			}
                     			else if(c.getStringCellValue().contains("Interchange Rate JCB Withdrawal"))
                     			{
                     				dollar_val = true;
                     				//pstmt = con.prepareStatement(insertDollarVal);
                     				dollar_pst.setString(1, "JCB"); 
                     				cellcount++;
                     			}
                     			else if(c.getStringCellValue().contains("Interchange Rate UPI Withdrawal"))
                     			{
                     				dollar_val = true;
                     				//pstmt = con.prepareStatement(insertDollarVal);
                     				dollar_pst.setString(1, "UPI");
                     				cellcount++;
                     			}
                     			
                     			System.out.println("Cell value in string case is "+c.getStringCellValue());
                     			break;
                     		case Cell.CELL_TYPE_NUMERIC:
                     			if(dollar_val)
                     			{
                     				String digit = c.getNumericCellValue() +"";
                     				if(digit.contains("E"))
                     				{
                     					digit = c.getNumericCellValue() +"";
                     					double d = Double.parseDouble(digit);
                     					System.out.println(digit);
                     					BigDecimal bd = new BigDecimal(d);
                     					System.out.println("Bigdecimal is "+bd);
                     					/* digit = bd.round(new MathContext(15)).toPlainString();
                            	   System.out.println(digit);
                     					 */
                     					String tryDigit = bd+"";
                     					int indexOfDot = tryDigit.indexOf(".");
                     					int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                     					System.out.println(secondDigit);
                     					if(secondDigit > 5)
                     					{
                     						digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                     					}
                     					else
                     					{
                     						BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                     						System.out.println("digit is " + db);
                     						digit = db.toPlainString();
                     					}

                     				}
                     				System.out.println("digit "+digit);
                     				dollar_pst.setString(cellcount, digit);
                     				cellcount++;
                     				break;
                     			}
                     		}
                     		
                     	}
                     	if(dollar_val)
                     	{
                     		dollar_row = r.getRowNum();
                     		dollar_pst.setString(cellcount, beanObj.getDatepicker());
                     		dollar_pst.setString(++cellcount, beanObj.getCreatedBy());
                     		dollar_pst.execute();
                     		con.commit();
                     	}
                     	//con.commit();
                     }
                    // if (r.getRowNum() > 6) {
                    // if(dollar_row >0 && r.getRowNum() > dollar_row+2) {
                     if( r.getRowNum() > 5 ) {
                            int count = 0;
                            int cellCount = 1;
                            for (Cell c : r) {

                         	   count++;
                         	   boolean crossCheck = false;
                         	   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                         	   {
                         	   case Cell.CELL_TYPE_STRING:
                         		   crossCheck = true;
                         		   if(c.getStringCellValue().equalsIgnoreCase("Dispute Adjustments"))
                         		   {
                         			   totalcount--;
                         			   break OUTER;                                    			  
                         		   }
                         		   else if(cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                         		   {
                         			   continue OUTER;
                         		   }
                         		   System.out.println("Cell value in string case is "+c.getStringCellValue());
                         		   ps.setString(cellCount, c.getStringCellValue());
                         		   break;
                         	   case Cell.CELL_TYPE_NUMERIC:
                         		   //System.out.println("Numeric cell value is "+c.getNumericCellValue());
                         		   crossCheck = true;
                                    String digit = c.getNumericCellValue() +"";
                                    if(digit.contains("E"))
                                    {
                                 	   digit = c.getNumericCellValue() +"";
                                 	   double d = Double.parseDouble(digit);
                                 	   System.out.println(digit);
                                        BigDecimal bd = new BigDecimal(d);
                                        System.out.println("Bigdecimal is "+bd);
                                 	  /* digit = bd.round(new MathContext(15)).toPlainString();
                                 	   System.out.println(digit);
                                 	   */
                                        String tryDigit = bd+"";
                                        int indexOfDot = tryDigit.indexOf(".");
                                        int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                                        System.out.println(secondDigit);
                                        if(secondDigit > 5)
                                        {
                                     	   digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                                                                         }
                                        else
                                        {
                                     	   BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                                     	   System.out.println("digit is " + db);
                                     	   digit = db.toPlainString();
                                        }
                                    
                                    }
                                    System.out.println("digit "+digit);
                         		   ps.setString(cellCount, digit);
                         		   break;
                         	   default:
                         		  // System.out.println("Inside default Loop "+c.getStringCellValue());
                         		   crossCheck = true;
                         		   if(cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                         		   {
                         			   continue OUTER;
                         		   }
                         		   System.out.println("cell value is "+c.getStringCellValue());
                         		   ps.setString(cellCount, c.getStringCellValue());
                         		   break;

                         	   }

                         	   if(!crossCheck)
                         	   {
                         		   ps.setString(cellCount, c.getStringCellValue());
                         	   }
                         	   cellCount++;

                            }
                            ps.setInt(5, 1);
                            ps.setString(6, beanObj.getDatepicker());
                            ps.setString(7, beanObj.getCreatedBy());
                            ps.setInt(8, srl_no++);
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
