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

public class ReadNCMCDSCRFile {
	
	private static final Logger logger = Logger.getLogger(ReadNCMCDSCRFile.class);
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
          boolean Idbi_Block = false;
          int read_line = 0;
          boolean reading_line = false;
          boolean total_encounter =false, stop_reading = false,last_line=false;
          int final_line = 0;
          
          
          String sql = "INSERT INTO NCMC_DSCR_RAWDATA(BANK_NAME,SETT_BIN, ISS_BIN, INWARD_OUTWARD, TXN_COUNT, TXN_CCY, TXN_AMT_DR, TXN_AMT_CR, SETT_CURR, SET_AMT_DR, SET_AMT_CR, INT_FEE_DR, INT_FEE_CR, MEM_INC_FEE_DR, MEM_INC_FEE_CR, CUS_COMPEN_DR, CUS_COMPEN_CR, OTH_FEE_AMT_DR, OTH_FEE_AMT_CR, OTH_FEE_GST_DR, OTH_FEE_GST_CR, FINAL_SUM_CR, FINAL_SUM_DR, FINAL_NET, FILEDATE, CREATEDBY, CYCLE) "+
        		  "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MON/YYYY'),?,?)";
          
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
  				//	book = new HSSFWorkbook(file.getInputStream()) ;
  				//	wb = new HSSFWorkbook(file.getInputStream());
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
                        	if(read_line == 1)
                        		read_line++;
                        	reading_line = false;
                               int cellCount = 1;
                               if(!stop_reading)
                               {
                            	   for (Cell c : r) {
                            		   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                            		   {
                            		   case Cell.CELL_TYPE_STRING:
                            			   if(c.getStringCellValue().equalsIgnoreCase("UCO BANK AS ISS QSPARC"))
                            			   {
                            				   bank_name = c.getStringCellValue();
                            				   Idbi_Block = true;
                            				   read_line++;
                            				   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            				   cellCount++;
                            			   }
                            			   else if(total_encounter && final_line > 0)
                            			   {
                            				   logger.info("in 1st Total Encounter loop");
                            				   if(final_line == 4 && cellCount == 1)
                            				   {
                            					   ps.setString(cellCount++, bank_name);
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            					   stop_reading = true;
                            					   last_line = true;
                            				   }
                            				   else if(final_line == 4 && cellCount >1)
                            				   {
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            					   break;
                            				   }
                            				   else
                            				   {
                            					   final_line ++;
                            					   continue OUTER;
                            				   }
                            			   }
                            			   else if(Idbi_Block)
                            			   {
                            				   if(read_line == 1)
                            				   {
                            					   if(cellCount == 2)
                            						   sett_bin = c.getStringCellValue();
                            					   else
                            					   {
                            						   continue OUTER;
                            					   }

                            					   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            					   cellCount++;

                            				   }
                            				   else if(c.getStringCellValue().equalsIgnoreCase("Total"))
                            				   {
                            					   if(cellCount == 1)
                            					   {
                            						   reading_line = true;
                            						   if(total_encounter)
                            						   {
                            							   final_line++;
                            							   continue OUTER;

                            						   }
                            						   else 
                            						   {
                            							   ps.setString(cellCount++, bank_name);
                            							   ps.setString(cellCount++, sett_bin);
                            							   ps.setString(cellCount++, acq_iss_bin);
                            							   ps.setString(cellCount, c.getStringCellValue());
                            							   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            							   cellCount++;
                            							   total_encounter = true;
                            						   }
                            					   }
                            					   /*  else if(stop_reading)
                                    			  {
                                    				  ps.setString(cellCount++, c.getStringCellValue());
                                    				  logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                                    			  }*/
                            				   }
                            				   else if(total_encounter)
                            				   {
                            					   logger.info("in Total Encounter loop");
                            					   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            				   }
                            				   else
                            				   {
                            					   continue OUTER;
                            				   }

                            			   }
                            			   else if(reading_line)
                            			   {
                            				   ps.setString(cellCount, c.getStringCellValue());
                            				   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            				   cellCount++;
                            			   }
                            			   /*else
                            			   {
                            				   continue OUTER;
                            			   }*/


                            			   break;
                            		   case Cell.CELL_TYPE_NUMERIC:
                            			   if(Idbi_Block)
                            			   {
                            				   boolean negativeSign = false;
                            				   String digit = c.getNumericCellValue() +"";
                            				   if(digit.contains("-"))
                            					{
                            					   		negativeSign = true;
                            					   		digit = digit.replace("-", ""); //added on 10mar
                            					}
                            				   if(digit.contains("E"))
                            				   {
                            					   digit = c.getNumericCellValue() +"";
                            					   if(digit.contains("-"))
                               						{
                               					   		negativeSign = true;
                               					   		digit = digit.replace("-", ""); //added on 10mar
                               						}
                            					   
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
                            					   
                            					   if(negativeSign)
                            					   {
                            						   digit = "-"+digit;
                            					   }

                            				   }

                            				   if(read_line == 1)
                            				   {
                            					   if(cellCount == 3)
                            						   acq_iss_bin = digit; 
                            					   else
                            					   {
                            						   continue OUTER;
                            					   }
                            					   cellCount++;
                            				   }
                            				   else if(reading_line || last_line)
                            				   {
                            					   System.out.println("Cell count is "+cellCount+" and Data is "+digit);
                            					   ps.setString(cellCount, digit);
                            					   cellCount++;

                            				   }
                            				   else if(total_encounter)
                            				   {
                            					   System.out.println("Cell count is "+cellCount+" and acq_iss_bin is "+digit);
                            					   acq_iss_bin = digit;
                            					   total_encounter = false;
                            				   }
                            				   // logger.info("cell count is "+cellCount+" value "+ digit);


                            			   }
                            			   break;


                            		   }



                            	   }
                            	   if(reading_line || last_line)
                            	   {
                            		   System.out.println("Before inserting data");
                            		   logger.info("Cell count is "+cellCount);
                            		   ps.setString(cellCount++, beanObj.getFileDate());
                            		   ps.setString(cellCount++, beanObj.getCreatedBy());
                            		   ps.setString(cellCount++, beanObj.getCycle());
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
          boolean Idbi_Block = false;
          int read_line = 0;
          boolean reading_line = false;
          boolean total_encounter =false, stop_reading = false,last_line=false;
          int final_line = 0;
          
          
          String sql = "INSERT INTO RUPAY_PBGB_DSCR_RAWDATA(BANK_NAME,SETT_BIN, ISS_BIN, INWARD_OUTWARD, TXN_COUNT, TXN_CCY, TXN_AMT_DR, TXN_AMT_CR, SETT_CURR, SET_AMT_DR, SET_AMT_CR, INT_FEE_DR, INT_FEE_CR, MEM_INC_FEE_DR, MEM_INC_FEE_CR, CUS_COMPEN_DR, CUS_COMPEN_CR, OTH_FEE_AMT_DR, OTH_FEE_AMT_CR, OTH_FEE_GST_DR, OTH_FEE_GST_CR, FINAL_SUM_CR, FINAL_SUM_DR, FINAL_NET, FILEDATE, CREATEDBY, CYCLE) "+
        		  "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,TO_DATE(?,'DD/MON/YYYY'),?,?)";
          
           try {
                  long start = System.currentTimeMillis();
                  System.out.println("start");
                  con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
  				//	book = new HSSFWorkbook(file.getInputStream()) ;
  				//	wb = new HSSFWorkbook(file.getInputStream());
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
                        	if(read_line == 1)
                        		read_line++;
                        	reading_line = false;
                               int cellCount = 1;
                               if(!stop_reading)
                               {
                            	   for (Cell c : r) {
                            		   switch(formulaEvaluate.evaluateInCell(c).getCellType())
                            		   {
                            		   case Cell.CELL_TYPE_STRING:
                            			   if(c.getStringCellValue().equalsIgnoreCase("PASCHIM BANGA GRAMIN BANK AS ISSUER"))
                            			   {
                            				   bank_name = c.getStringCellValue();
                            				   Idbi_Block = true;
                            				   read_line++;
                            				   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            				   cellCount++;
                            			   }
                            			   else if(total_encounter && final_line > 0)
                            			   {
                            				   logger.info("in 1st Total Encounter loop");
                            				   if(final_line == 4 && cellCount == 1)
                            				   {
                            					   ps.setString(cellCount++, bank_name);
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            					   stop_reading = true;
                            					   last_line = true;
                            				   }
                            				   else if(final_line == 4 && cellCount >1)
                            				   {
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            					   break;
                            				   }
                            				   else
                            				   {
                            					   final_line ++;
                            					   continue OUTER;
                            				   }
                            			   }
                            			   else if(Idbi_Block)
                            			   {
                            				   if(read_line == 1)
                            				   {
                            					   if(cellCount == 2)
                            						   sett_bin = c.getStringCellValue();
                            					   else
                            					   {
                            						   continue OUTER;
                            					   }

                            					   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            					   cellCount++;

                            				   }
                            				   else if(c.getStringCellValue().equalsIgnoreCase("Total"))
                            				   {
                            					   if(cellCount == 1)
                            					   {
                            						   reading_line = true;
                            						   if(total_encounter)
                            						   {
                            							   final_line++;
                            							   continue OUTER;

                            						   }
                            						   else 
                            						   {
                            							   ps.setString(cellCount++, bank_name);
                            							   ps.setString(cellCount++, sett_bin);
                            							   ps.setString(cellCount++, acq_iss_bin);
                            							   ps.setString(cellCount, c.getStringCellValue());
                            							   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            							   cellCount++;
                            							   total_encounter = true;
                            						   }
                            					   }
                            					   /*  else if(stop_reading)
                                    			  {
                                    				  ps.setString(cellCount++, c.getStringCellValue());
                                    				  logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                                    			  }*/
                            				   }
                            				   else if(total_encounter)
                            				   {
                            					   logger.info("in Total Encounter loop");
                            					   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            					   ps.setString(cellCount++, c.getStringCellValue());
                            				   }
                            				   else
                            				   {
                            					   continue OUTER;
                            				   }

                            			   }
                            			   else if(reading_line)
                            			   {
                            				   ps.setString(cellCount, c.getStringCellValue());
                            				   logger.info("cell count is "+cellCount+" value "+ c.getStringCellValue());
                            				   cellCount++;
                            			   }
                            			   else if(cellCount >= 3)
                            			   {
                            				   continue OUTER;
                            			   }


                            			   break;
                            		   case Cell.CELL_TYPE_NUMERIC:
                            			   if(Idbi_Block)
                            			   {
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

                            				   if(read_line == 1)
                            				   {
                            					   if(cellCount == 3)
                            						   acq_iss_bin = digit; 
                            					   else
                            					   {
                            						   continue OUTER;
                            					   }
                            					   cellCount++;
                            				   }
                            				   else if(reading_line || last_line)
                            				   {
                            					   System.out.println("Cell count is "+cellCount+" and Data is "+digit);
                            					   ps.setString(cellCount, digit);
                            					   cellCount++;

                            				   }
                            				   else if(total_encounter)
                            				   {
                            					   System.out.println("Cell count is "+cellCount+" and acq_iss_bin is "+digit);
                            					   acq_iss_bin = digit;
                            					   total_encounter = false;
                            				   }
                            				   // logger.info("cell count is "+cellCount+" value "+ digit);


                            			   }
                            			   break;


                            		   }



                            	   }
                            	   if(reading_line || last_line)
                            	   {
                            		   System.out.println("Before inserting data");
                            		   logger.info("Cell count is "+cellCount);
                            		   ps.setString(cellCount++, beanObj.getFileDate());
                            		   ps.setString(cellCount++, beanObj.getCreatedBy());
                            		   ps.setString(cellCount++, beanObj.getCycle());
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
