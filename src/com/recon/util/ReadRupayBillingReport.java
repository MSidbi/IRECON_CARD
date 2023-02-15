package com.recon.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.recon.control.ManualKnockoffController;
import com.recon.model.RupayUploadBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

public class ReadRupayBillingReport {
	
	private static final Logger logger = Logger.getLogger(ReadRupayBillingReport.class);
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	
    public HashMap<String, Object> fileupload(RupayUploadBean beanObj,MultipartFile file,Connection con) throws SQLException {
    	String bank_name = null, sett_bin = null,acq_iss_bin = null, in_out=null, status = null, tran_cycle = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<String, Object>();
          Workbook wb = null;
          Sheet sheet = null;
          FormulaEvaluator formulaEvaluate = null;
          int extn = file.getOriginalFilename().indexOf(".");
          logger.info("extension is "+extn);
          String CR_DR = null;
          boolean Idbi_Block = false;
          int lineNumber = 0, read_line = 0;
          boolean reading_line = false;
          boolean total_encounter =false, stop_reading = false,last_line=false;
          int final_line = 0;
          
          String sql = "insert into rupay_billing_rawdata(bank_name, cr_dr, sett_bin,  txn_count, fee_amount, fee_amount_gst, fee, createdby, filedate, cycle) "+
        		  "VALUES(?,?,?,?,?,?,?,?,str_TO_DATE(?,'%Y/%m/%d'),?)";
        
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
                		wb = new XSSFWorkbook(file.getInputStream());
                		sheet = wb.getSheetAt(0);
      					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
  				
  				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
  					 
  					// book = new XSSFWorkbook(file.getInputStream());
  					wb = new XSSFWorkbook(file.getInputStream());
  					sheet = wb.getSheetAt(0);
  					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
  				}
                 
 OUTER:   for (Row r : sheet) {
                        totalcount++;
                        if (r.getRowNum() > 0) {
                        	lineNumber++;
                        	reading_line = false;
                               int cellCount = 1;
                               if(!stop_reading)
                               {
                            	   for (Cell c : r) {
                            		   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                            		   {
                            		   case Cell.CELL_TYPE_STRING:
                            			  if(!c.getStringCellValue().trim().equalsIgnoreCase(""))
                            			  {
                            				  if(c.getStringCellValue().contains("DHANLAXMI BANK"))
                            				  {
                            					  logger.info("cell count is "+cellCount+" Bank Name is  "+ c.getStringCellValue());
                            					  Idbi_Block = true;
                            					  bank_name = c.getStringCellValue();
                            					  cellCount++;
                            					 // continue OUTER;
                            				  }
                            				  else if(Idbi_Block)
                            				  {
                            					  if(c.getStringCellValue().equalsIgnoreCase("TOTAL") || total_encounter)
                            					  {
                            						  if(!total_encounter && final_line == 0)
                            						  {
                            							  total_encounter  = true;
                            							  final_line++;
                            							  continue OUTER;
                            						  }
                            						  else if(final_line == 3)
                            						  {
                            							  logger.info("Final  line starts");
                            							  stop_reading = true;
                            							  ps.setString(cellCount++, bank_name);
                            							  ps.setString(cellCount++, CR_DR);
                            							  ps.setString(cellCount++, c.getStringCellValue());
                            						  }
                            						  else
                            						  {
                            							  final_line++;
                            							  continue OUTER;
                            						  }

                            					  }
                            					  else if(lineNumber == 1 && (c.getStringCellValue().equalsIgnoreCase("CR") || c.getStringCellValue().equalsIgnoreCase("DR")))
                                				  {
                                					  CR_DR = c.getStringCellValue();
                                					  continue OUTER;
                                				  }
                            					  else if(lineNumber > 1)
                            					  {
                            						  continue OUTER;
                            					  }
                            				  }


                            				  break;
                            			  }
                            			  break;
                            		   case Cell.CELL_TYPE_NUMERIC:
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
                            				   
                            				   if(total_encounter)
                            				   {
                            					   logger.info("cell count is "+cellCount+" Value is  "+ digit);
                            					   ps.setString(cellCount++, digit);
                            				   }
                            				   else if(lineNumber > 1)
                            				   {
                            					   continue OUTER;
                            				   }
                            			   break;


                            		   }



                            	   }
                            	   if(total_encounter)
                            	   {
                            		   System.out.println("Before inserting data");
                            		   logger.info("Cell count is "+cellCount);
                            		   ps.setString(cellCount++, beanObj.getCreatedBy());
                            		   ps.setString(cellCount++, beanObj.getFileDate());
                            		   ps.setString(cellCount, beanObj.getCycle());
                            		   ps.execute();
                            	   }
                            	   logger.info("cellcount is "+cellCount);
                               }
                               
                        }
                  }
  			//	ps.executeBatch();
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
    
    
    public HashMap<String, Object> PBGBfileupload(RupayUploadBean beanObj,MultipartFile file,Connection con) throws SQLException {
    	String bank_name = null, sett_bin = null,acq_iss_bin = null, in_out=null, status = null, tran_cycle = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<String, Object>();
          Workbook wb = null;
          Sheet sheet = null;
          FormulaEvaluator formulaEvaluate = null;
          int extn = file.getOriginalFilename().indexOf(".");
          logger.info("extension is "+extn);
          String CR_DR = null;
          boolean Idbi_Block = false;
          int lineNumber = 0, read_line = 0;
          boolean reading_line = false;
          boolean total_encounter =false, stop_reading = false,last_line=false;
          int final_line = 0;
          
          String sql = "INSERT INTO RUPAY_PBGB_BILLING_RAWDATA(BANK_NAME, CR_DR, SETT_BIN,  txn_count, FEE_AMOUNT, FEE_AMOUNT_GST, FEE, CREATEDBY, FILEDATE, CYCLE) "+
        		  "VALUES(?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MON/YYYY'),?)";
        
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
                		wb = new XSSFWorkbook(file.getInputStream());
                		sheet = wb.getSheetAt(0);
      					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
  				
  				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
  					 
  					// book = new XSSFWorkbook(file.getInputStream());
  					wb = new XSSFWorkbook(file.getInputStream());
  					sheet = wb.getSheetAt(0);
  					formulaEvaluate = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
  				}
                 
 OUTER:   for (Row r : sheet) {
                        totalcount++;
                        if (r.getRowNum() > 0) {
                        	lineNumber++;
                        	reading_line = false;
                               int cellCount = 1;
                               if(!stop_reading)
                               {
                            	   for (Cell c : r) {
                            		   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                            		   {
                            		   case Cell.CELL_TYPE_STRING:
                            			  if(!c.getStringCellValue().trim().equalsIgnoreCase(""))
                            			  {
                            				  if(c.getStringCellValue().contains("PASCHIM BANGA GRAMIN BANK AS ISSUER"))
                            				  {
                            					  logger.info("cell count is "+cellCount+" Bank Name is  "+ c.getStringCellValue());
                            					  Idbi_Block = true;
                            					  bank_name = c.getStringCellValue();
                            					  cellCount++;
                            					 // continue OUTER;
                            				  }
                            				  else if(Idbi_Block)
                            				  {
                            					  if(c.getStringCellValue().equalsIgnoreCase("TOTAL") || total_encounter)
                            					  {
                            						  if(!total_encounter && final_line == 0)
                            						  {
                            							  total_encounter  = true;
                            							  final_line++;
                            							  continue OUTER;
                            						  }
                            						  else if(final_line == 3)
                            						  {
                            							  logger.info("Final  line starts");
                            							  stop_reading = true;
                            							  ps.setString(cellCount++, bank_name);
                            							  ps.setString(cellCount++, CR_DR);
                            							  ps.setString(cellCount++, c.getStringCellValue());
                            						  }
                            						  else
                            						  {
                            							  final_line++;
                            							  continue OUTER;
                            						  }

                            					  }
                            					  else if(lineNumber == 1 && (c.getStringCellValue().equalsIgnoreCase("CR") || c.getStringCellValue().equalsIgnoreCase("DR")))
                                				  {
                                					  CR_DR = c.getStringCellValue();
                                					  continue OUTER;
                                				  }
                            					  else if(lineNumber > 1)
                            					  {
                            						  continue OUTER;
                            					  }
                            				  }


                            				  break;
                            			  }
                            			  break;
                            		   case Cell.CELL_TYPE_NUMERIC:
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
                            				   
                            				   if(total_encounter)
                            				   {
                            					   logger.info("cell count is "+cellCount+" Value is  "+ digit);
                            					   ps.setString(cellCount++, digit);
                            				   }
                            				   else if(lineNumber > 1)
                            				   {
                            					   continue OUTER;
                            				   }
                            			   break;


                            		   }



                            	   }
                            	   if(total_encounter)
                            	   {
                            		   System.out.println("Before inserting data");
                            		   logger.info("Cell count is "+cellCount);
                            		   ps.setString(cellCount++, beanObj.getCreatedBy());
                            		   ps.setString(cellCount++, beanObj.getFileDate());
                            		   ps.setString(cellCount, beanObj.getCycle());
                            		   ps.execute();
                            	   }
                            	   logger.info("cellcount is "+cellCount);
                               }
                               
                        }
                  }
  			//	ps.executeBatch();
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
