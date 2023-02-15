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

public class ReadRupayIntDSCRFile {
	
	private static final Logger logger = Logger.getLogger(ReadRupayIntDSCRFile.class);
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
          int read_line = 0,total_count = 0;
          boolean reading_line = false;
          boolean total_encounter =false, stop_reading = false,last_line=false;
          int final_line = 0;
          int finalCell = 1;
          boolean total_Encounter = false;
          
          String sql = "INSERT INTO RUPAY_INT_DSCR_RAWDATA(PROD_NAME, SETT_BIN, ACQ_ISS_BIN, INWARD_OUTWARD, TXN_COUNT, TXN_AMT_DR, TXN_AMT_CR, BILL_AMT_DR, BILL_AMT_CR, SET_AMT_DR, SET_AMT_CR, INT_FEE_AMT_DR, INT_FEE_AMT_CR, MEM_INC_FEE_AMT_DR, MEM_INC_FEE_AMT_CR, OTH_FEE_AMT_DR, OTH_FEE_AMT_CR, OTH_FEE_GST_DR, OTH_FEE_GST_CR, FINAL_SUM_CR, FINAL_SUM_DR, FINAL_NET, FILEDATE, CREATEDBY, CYCLE) "+
        		  "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,TO_DATE(?,'DD/MON/YYYY'),?,?)";
          
           try {
        	   con.setAutoCommit(false);
                  long start = System.currentTimeMillis();
                  System.out.println("start");
               //   con.setAutoCommit(false);
                  PreparedStatement ps = con.prepareStatement(sql);
                  
                  if (file.getOriginalFilename().substring(extn)
  						.equalsIgnoreCase(".XLS")) {
  			 
  				//	book = new HSSFWorkbook(file.getInputStream()) ;
  					wb = new HSSFWorkbook(file.getInputStream());
                	//	wb = new XSSFWorkbook(file.getInputStream());
                		sheet = wb.getSheetAt(0);
      					formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
  				
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
                            			  if(cellCount == 1 && c.getStringCellValue().contains("Issuer"))
                            			  {
                            				  bank_name = c.getStringCellValue();
                            				  cellCount++;
                            				  continue OUTER;
                            			  }
                            			  else if(cellCount == 1 && c.getStringCellValue().contains("INWARD GST"))
                            			  {
                            				  total_encounter =true;
                            				  continue OUTER;
                            			  }
                            			 /* else if(cellCount == 1 && c.getStringCellValue().equalsIgnoreCase("TOTAL"))
                            			  {
                            				  stop_reading = true;
                            			  }*/
                            			  
                            			  if(total_encounter)
                            			  {
                            				  if(total_count == 3)
                            				  {
                            					  ps.setString(cellCount++, bank_name);
                            					//  ps.setString(cellCount++, c.getStringCellValue());
                            					  reading_line = true;
                            				  }
                            				  else
                            				  {
                            					  total_count++;
                            					  continue OUTER;
                            				  }
                            			  }
                            			  else
                            			  {
                            				  continue OUTER;
                            			  }
                            			   break;
                            		   case Cell.CELL_TYPE_NUMERIC:
                            			   if(total_encounter && total_count == 3)
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
                            				   ps.setString(cellCount++, digit);
                            			   }
                            			   else
                            			   {
                            				   continue OUTER;
                            			   }
                            			   break;

                            		   }
                            		   
                            			   

                            	   }
                            	   if(reading_line)
                            	   {
                            		   System.out.println("Before inserting data");
                            		   logger.info("Cell count is "+cellCount);
                            		   ps.setString(cellCount++, beanObj.getFileDate());
                            		   ps.setString(cellCount++, beanObj.getCreatedBy());
                            		   ps.setString(cellCount++, beanObj.getCycle());
                            		   ps.execute();
                            		   total_encounter = false;
                            		   reading_line = false;
                            		   total_count = 0;
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
