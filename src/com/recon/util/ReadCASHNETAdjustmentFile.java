package com.recon.util;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.recon.control.ManualKnockoffController;
import com.recon.model.NFSSettlementBean;

import javafx.css.PseudoClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ReadCASHNETAdjustmentFile {
	
	private static final Logger logger = Logger.getLogger(ReadCASHNETAdjustmentFile.class);
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	
    public HashMap<String, Object> fileupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
           int response = 0;String tableName = null;
           int totalcount = 0;
           HashMap<String, Object> mapObj = new HashMap<String, Object>();
          logger.info("File Name is "+file.getOriginalFilename());
          String description = null;
          double no_of_txns = 0,debit= 0, credit = 0;
          int count = 0,  colCount = 1, skipCount = 0, bankCount = 0, sr_no = 1, PreArb_Count = 0, LastCol = 0;
          
          /* String sql = "insert  into BACIDSTAT_temp (SRNO,ACCOUNTNO,Bacid,TRAN_DATE, Particulars,TRAN_RMKS ,Debit,Credit, Closing_balance,IFSC,entry_by)"
                        + " values (?,?,?,?,?,?,?,?,?,?,6346)";*/
           String sql = "insert into cashnet_chargeback(adj_date, adj_time, adj_type, acq, isr, response, txn_date, txn_time, rrn, atm_id, card_no, chb_date, chb_ref, "
           		+ "  txn_amount, adj_amount, acq_fee, "
           		+ "iss_fee, iss_fee_sw, adj_fee, adj_ref, adj_proof, adjcustpenalty, emv_status , adj_reason_code, "
           		+ "category, filedate, filename) "
           		+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'), ?)";
           
           String insert_PreArb = "insert into cashnet_chargeback(adj_date,	adj_type, acq, response, txn_date, txn_time, rrn, atm_id, card_no, stl_amount,"
           		+ "fst_cbk_recv_amt, reprstmt_recv_amt, pre_arb_adj_recv_amt, reg_amount,"
           		+ "acq_fee, iss_fee_sw, en_fee, adj_ref, adj_fee, adj_proof, category, filedate, filename)"
           		+" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, str_to_date(?,'%Y/%m/%d'), ?)";
           
          
           try {
        	  PreparedStatement ps = con.prepareStatement(sql);
        	  
        	  PreparedStatement prearb_ps = con.prepareStatement(insert_PreArb);
        	  
        	   Path tempDir = Files.createTempDirectory(""); 
          	  File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
          	  file.transferTo(tempFile);
          	  String content = Jsoup.parse(tempFile,"UTF-8").toString(); 
  			  org.jsoup.nodes.Document html = Jsoup.parse(content);
  			  if (content != null) 
  			  { 
  				 
				  Elements contents = html.getElementsByTag("tbody");
				  System.out.println("********************** Reading tbody tags ****************");
				  
				  OUTER:  for(Element a : contents)
				  {
					  //code starts from here
					  Elements thContents = a.getElementsByTag("tr");
					  Elements tdContents = a.getElementsByTag("td");
					  INNER:  for(Element b : tdContents)
					  {
						  
						  if(b.text().equals("ArbitrationFee"))
						  {
							  if(bankCount == 0)
							  {
								  System.out.println(b.text());
								  bankCount++;
								  continue;
							  }
							  else
							  {
								  break OUTER;
							  }
						  }
						  else if(b.text().equals("ENFee"))
						  {
							  PreArb_Count++;
							  LastCol++;
							  continue;
						  }
						  else if(b.text().equalsIgnoreCase("Back   Print"))
						  {
							  break OUTER;
						  }
						  
						  if(PreArb_Count > 0 && LastCol < 4)
						  {
							  LastCol++;
							  continue;
						  }
						  else if(LastCol == 4)
						  {
							  if(colCount == 4 || colCount == 17)
							  {
								  colCount++;
								  continue;
							  }
							  else
							  {
								  System.out.println(colCount+" "+b.text());
								  prearb_ps.setString(sr_no++, b.text().replace("'", ""));
								  colCount++;
								  if(colCount == 23 || colCount > 22)
								  {
									  totalcount++;
									  System.out.println("Last Column ");
									  prearb_ps.setString(sr_no++, "CASHNET");
									//  prearb_ps.setString(sr_no++, beanObj.getStSubCategory());
									  prearb_ps.setString(sr_no++, beanObj.getDatepicker());
									  prearb_ps.setString(sr_no++, file.getOriginalFilename());
									  prearb_ps.execute();
									  colCount = 1;
									  sr_no = 1;
								  }
							  }
						  }
						  /****** Reading main fields****************/
						  
						  if(bankCount == 1)
						  {
							  if(colCount%25 == 0)
							  {
								  skipCount++;
								  colCount++;
							  }
							  else if(skipCount == 0)
							  {
								  System.out.println(sr_no+" "+b.text());
								  ps.setString(sr_no++, b.text().replace("'", ""));
								  colCount++;
							  }
							  else if(skipCount == 1 && b.text().equals("0"))
							  {
								  totalcount++;
								  ps.setString(sr_no++, "CASHNET");
								 // ps.setString(sr_no++, beanObj.getStSubCategory());
								  ps.setString(sr_no++, beanObj.getDatepicker());
								  ps.setString(sr_no++, file.getOriginalFilename());
								  ps.execute();
								  sr_no = 1;
								  colCount = 1;
								  skipCount = 0;
							  }
						  }
						  
					  }



				  }
				
				  // card encryption 
				  String updateQuery = "update cashnet_chargeback set card_no = cast(aes_encrypt(rtrim(ltrim(card_no)),'key_dbank')as char) "+
             			 "where filedate = '"+beanObj.getDatepicker()+"' and FILENAME like '%"+file.getOriginalFilename()+"%'";
             	 //" a.CYCLE = '"+cycle+"'";
             	 logger.info("Update Query "+updateQuery);
             	 PreparedStatement update_pst = con.prepareStatement(updateQuery);
             	 update_pst.execute();
				  
              long end = System.currentTimeMillis();
              mapObj.put("result", true);
              mapObj.put("count", totalcount);
              
			  
  			  }
        	   
           } catch (Exception e) {
                  e.printStackTrace();
                  mapObj.put("result", false);
                  mapObj.put("count", "Exception in file upload ");
                  try {
                        con.rollback();
                  } catch (SQLException ex) {
                        ex.printStackTrace();
                  }
           }
           return mapObj;
    }
    
    public HashMap<String, Object> fileExcelupload(NFSSettlementBean beanObj,MultipartFile file,Connection con) throws SQLException {
    	
    	HashMap<String, Object> mapObj = new HashMap<String, Object>();
    	int totalcount = 0, colCount = 1, skipCount = 0;
    	try
    	{
    		List<Integer> DateCol = new ArrayList<Integer>();
            DateCol.add(1);
            DateCol.add(7);
            DateCol.add(8);
            
    		System.out.println("start");
            con.setAutoCommit(false);
            InputStream inputStream = file.getInputStream();
           /* HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = wb.getSheetAt(0);*/
            FormulaEvaluator formulaEvaluate = null;
            Workbook wb = null;
   			Sheet sheet = null;
            int extn = file.getOriginalFilename().indexOf(".");
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
            
            OUTER:   for (Row r : sheet) {
                totalcount++;
                logger.info("r.getRowNum() "+r.getRowNum());
                if (r.getRowNum() > 2) {
                       int cellCount = 1;
                       colCount = 1;
                       for (Cell c : r) {
                    	   if(!(colCount > 25 && colCount <30))
							  {
                    		   	
								  switch(formulaEvaluate.evaluateInCell(c).getCellType())
								  {
								  case Cell.CELL_TYPE_STRING:
									  //removing ' from data and checkig adjustment settlement date
									  System.out.println("cellCount "+cellCount+" data "+c.getStringCellValue());
									  cellCount++;
									  break;
								  case Cell.CELL_TYPE_NUMERIC:
								  {
									  if(DateCol.contains(cellCount))
									  {
										  logger.info("Its Date");
										  String digit = c.getNumericCellValue() +"";
										  Date javaDate= DateUtil.getJavaDate(Double.parseDouble(digit));
										  System.out.println(javaDate);
										  System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(javaDate));
										  digit = new SimpleDateFormat("dd/MM/yyyy").format(javaDate)+"";
										  System.out.println("digit is"+digit);
										//  ps.setString(cellCount, digit);
										  //checking whether adjSettlementDate is same as selected filedate
										  if(cellCount == 33)
										  {
											  logger.info("Its AdSettlementDate Column "+digit.substring(0,2));
											  if(digit.substring(0,2) != beanObj.getDatepicker().substring(0, 2))
											  {
												  logger.info("Dates present in adjsettlement column is different");
												  mapObj.put("result", false);
												  mapObj.put("count", "AdjSettlementDate has different dates");
												  return mapObj;
											  }
										  }

									  }
									  else
									  {
										  String digit = c.getNumericCellValue() +"";
										  if(digit.contains("E"))
										  {
											  digit = c.getNumericCellValue() +"";
											  double d = Double.parseDouble(digit);
											  System.out.println(digit);
											  BigDecimal bd = new BigDecimal(d);
											  System.out.println("cellCount "+cellCount+"Bigdecimal is "+bd);
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
										  // ps.setString(cellCount, digit);
									  }
								  }
								  cellCount++;
								  break;
								  }
							  }
                            	  
                            	  //ps.setString(cellCount, c.getStringCellValue());
                              
                  
                       }
                       
                }
          }
    		
            mapObj.put("result", false);
    		mapObj.put("count", "Done");
    	}
    	catch(Exception e)
    	{
    		logger.info("Exception while uploading excel "+e);
    		mapObj.put("result", false);
    		mapObj.put("count", "Exception");
    	}
    	return mapObj;
    }



}
