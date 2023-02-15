package com.recon.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.recon.model.NFSSettlementBean;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

public class ReadNTSLFile {
	
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
          int srl_no = 1;
          
          while(rs.next())
          {
        	  tableName = (String)rs.getString("TABLENAME");
          }
          
          /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                        + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
           String sql = "INSERT INTO "+tableName+"(DESCRIPTION,NO_OF_TXNS,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
           
           String CoopBank_sql = "INSERT INTO COOP_NTSL_NFS_RAWDATA (BANK_NAME,DESCRIPTION,DEBIT,CREDIT,CYCLE,FILEDATE,CREATEDBY,CREATEDDATE,SR_NO) "
           		+ "VALUES(?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?)";
           
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
                  
                  PreparedStatement coop_ps = con.prepareStatement(CoopBank_sql);
                  InputStream inputStream = file.getInputStream();
                  boolean coopBankFlag = false;
                 /* HSSFWorkbook wb = new HSSFWorkbook(inputStream);
                  HSSFSheet sheet = wb.getSheetAt(0);
			*/
                  
                //  FormulaEvaluator formulaEvaluate = wb.getCreationHelper().createFormulaEvaluator();
                  OUTER:   for (Row r : sheet) {
                	  totalcount++;

                	  if (r.getRowNum() > 2) {

                		  int cellCount = 1;
                		  for (Cell c : r) {

                			  if(!coopBankFlag) {
                				  boolean crossCheck = false;
                				  switch(formulaEvaluate.evaluateInCell(c).getCellType())
                				  {
                				  case Cell.CELL_TYPE_STRING:
                					  crossCheck = true;
                					  //MODIFIED ON 09 FEB FOR READING ADJUSTMENT DATA
                					  System.out.println("cell value is "+c.getStringCellValue());
                					  if(c.getStringCellValue().contains("Daily Settlement Statement for"))
                					  {
                						  //totalcount--;
                						  //break OUTER;
                						  coopBankFlag = true;
                						  //int startIndex = "Daily Settlement Statement for ".length(); 
                						  int endIndex = c.getStringCellValue().indexOf("as on ");
                						  System.out.println("endIndex is "+endIndex);
                						  String bankName = c.getStringCellValue().toUpperCase().substring(0, endIndex);
                						  coop_ps.setString(1, bankName);
                						  continue OUTER;
                					  }
                					  else if((cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase("")) || (cellCount == 3 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                							  || cellCount == 4 && c.getStringCellValue().trim().equalsIgnoreCase(""))
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
                						  BigDecimal bd = new BigDecimal(d);
                						  /* digit = bd.round(new MathContext(15)).toPlainString();
                                    	   System.out.println(digit);
                						   */
                						  String tryDigit = bd+"";
                						  int indexOfDot = tryDigit.indexOf(".");
                						  if(indexOfDot > 0)
                						  {
                							  if(tryDigit.substring(indexOfDot+1).length() >1)
                							  {
                								  System.out.println("tryDigit.substring(indexOfDot) "+tryDigit.substring(indexOfDot+1));
                								  if(tryDigit.substring(indexOfDot+1).length() >2)
                								  {
                									  System.out.println("Inside third digit");
                									  int thirdDigit = Integer.parseInt(tryDigit.substring(indexOfDot+3, indexOfDot+4));
                									  if(thirdDigit >= 5)
                									  {
                										  int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+2, indexOfDot+3))+1;
                										  digit = tryDigit.substring(0,indexOfDot+2)+secondDigit;   
                										  System.out.println("Digit1 is "+digit);
                									  }
                									  else
                									  {
                										  BigDecimal db = bd.setScale(2, RoundingMode.HALF_UP);
                										  digit = db.toPlainString();
                										  System.out.println("Digit 2 is "+digit);
                									  }
                								  }
                								  else
                								  {

                									  System.out.println("Inside second digit");
                									  int second = Integer.parseInt(tryDigit.substring(indexOfDot+2, indexOfDot+3));
                									  if(second >= 5)
                									  {
                										  int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+2, indexOfDot+3))+1;
                										  digit = tryDigit.substring(0,indexOfDot+2)+secondDigit;   
                										  System.out.println("Digit1 is "+digit);
                									  }
                									  else
                									  {
                										  BigDecimal db = bd.setScale(2, RoundingMode.HALF_UP);
                										  digit = db.toPlainString();
                										  System.out.println("Digit 2 is "+digit);
                									  }


                								  }
                							  }
                							  else
                							  {
                								  digit = tryDigit;   
                								  System.out.println("Digit 3 is "+digit);
                							  }
                						  }
                					  }
                					  
                					  ps.setString(cellCount, digit);
                					  break;
                				  default:
                					  // System.out.println("Inside default Loop "+c.getStringCellValue());
                					  crossCheck = true;
                					  if(cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                					  {
                						  continue OUTER;
                					  }
                					  ps.setString(cellCount, c.getStringCellValue());
                					  break;

                				  }

                				  if(!crossCheck)
                				  {
                					  ps.setString(cellCount, c.getStringCellValue());
                				  }
                				  cellCount++;
                			  }
                			  else
                			  {
                				  //READING CO OPERATIVE BANK DATA
                				  switch(formulaEvaluate.evaluateInCell(c).getCellType())
                				  {
                				  case Cell.CELL_TYPE_STRING:
                					  //MODIFIED ON 09 FEB FOR READING ADJUSTMENT DATA
                					  if(c.getStringCellValue().contains("Daily Settlement Statement for"))
                					  {
                						  //totalcount--;
                						  //break OUTER;
                						  coopBankFlag = true;
                						  //int startIndex = "Daily Settlement Statement for ".length(); 
                						  int endIndex = c.getStringCellValue().indexOf("as on ");
                						  String bankName = c.getStringCellValue().toUpperCase().substring(0, endIndex);
                						  System.out.println("Bank Name is "+bankName);
                						  coop_ps.setString(1, bankName);
                						  continue OUTER;
                					  }
                					  else if((cellCount == 1 && !c.getStringCellValue().equalsIgnoreCase("Final Settlement Amount"))||
                							  (cellCount ==1 && c.getStringCellValue().trim().equalsIgnoreCase("")) || (cellCount == 3 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                							  || cellCount == 4 && c.getStringCellValue().trim().equalsIgnoreCase(""))
                					  {
                						  totalcount--;
                						  continue OUTER;
                					  }
                					  System.out.println("Cell value in string case is "+c.getStringCellValue());
                					  coop_ps.setString(cellCount+1, c.getStringCellValue());
                					  cellCount++;
                					  break;
                				  case Cell.CELL_TYPE_NUMERIC:
                					  //System.out.println("Numeric cell value is "+c.getNumericCellValue());
                					  String digit = c.getNumericCellValue() +"";
                					  if(digit.contains("E"))
                					  {
                						  digit = c.getNumericCellValue() +"";
                						  double d = Double.parseDouble(digit);
                						  BigDecimal bd = new BigDecimal(d);
                						  /* digit = bd.round(new MathContext(15)).toPlainString();
                                    	   System.out.println(digit);
                						   */
                						  String tryDigit = bd+"";
                						  int indexOfDot = tryDigit.indexOf(".");
                						  int secondDigit = Integer.parseInt(tryDigit.substring(indexOfDot+1, indexOfDot+2));
                						  if(secondDigit > 5)
                						  {
                							  digit = tryDigit.substring(0,indexOfDot+3);                                        	   
                						  }
                						  else
                						  {
                							  BigDecimal db = bd.setScale(1, RoundingMode.HALF_UP);
                							  digit = db.toPlainString();
                						  }

                					  }
                					  coop_ps.setString(cellCount+1, digit);
                					  cellCount++;
                					  break;
                				  }
                				  
                			  }
                		  }
                		  if(cellCount > 1)
                		  {
                			  if(!coopBankFlag )
                			  {
                				  ps.setInt(5, beanObj.getCycle());
                				  ps.setString(6, beanObj.getDatepicker());
                				  ps.setString(7, beanObj.getCreatedBy());
                				  ps.setInt(8, srl_no++);
                				  ps.addBatch();
                			  }
                			  else
                			  {
                				  coop_ps.setInt(5, beanObj.getCycle());
                				  coop_ps.setString(6, beanObj.getDatepicker());
                				  coop_ps.setString(7, beanObj.getCreatedBy());
                				  coop_ps.setInt(8, srl_no++);
                				  coop_ps.addBatch();
                			  }
                		  }
                	  }
                  }
                  if(coopBankFlag)
                	  coop_ps.executeBatch();
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
