package com.recon.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.recon.model.VisaUploadBean;

public class ReadVisaJVFIle {
	
	public boolean readVisaJVFile(VisaUploadBean beanObj, MultipartFile file, Connection conn) {
		
		int rowCount = 0, sr_no = 1;
		List<String> columnName = new ArrayList<>();
		List<List<String>> Data = new ArrayList<List<String>>();
		//List<String> fileData = new ArrayList<>();
		
			String INSERT_QUERY = "INSERT INTO visa_settlement_report(FILEDATE";
			String INSERT_QUERY2 = "VALUES (TO_DATE(?,'DD/MM/YYYY')";
		try
		{
			Workbook wb = new HSSFWorkbook(file.getInputStream());
			Sheet sheet = wb.getSheetAt(0);
			FormulaEvaluator formulaEvaluate = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
			
			for (Row r : sheet) {
				 int cellCount = 1;
				 rowCount++;
				 List<String> fileData = new ArrayList<>(); 
				 
				for (Cell c : r) {
					switch(formulaEvaluate.evaluateInCell(c).getCellType())
					{
						case Cell.CELL_TYPE_STRING:
									if(rowCount == 1)
									{
										columnName.add(c.getStringCellValue());
									}
									else
									{
										fileData.add(c.getStringCellValue());
									}
										System.out.println("cell count in string is "+cellCount+" value "+ c.getStringCellValue());
										cellCount++;
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
										 if(rowCount > 1)
										 {
											 if(digit.contains(".0"))
												 fileData.add(digit.replace(".0", ""));
											 else
												 fileData.add(digit);
										 }
										 System.out.println("Cell count is "+cellCount+" value "+digit.replace(".0", ""));
									 cellCount++;
                                   	  break;
						default:
								System.out.println("Here it is "+cellCount +" value is "+c.getStringCellValue());
								cellCount++;
								break;
					}
				}
				if(rowCount > 1)
					Data.add(fileData);
			}
				
			System.out.println("column name is "+columnName);
			
			for(String col : columnName)
			{
				INSERT_QUERY = INSERT_QUERY +", "+col.replace(" ", "_");
				INSERT_QUERY2 = INSERT_QUERY2+" , ?";
			}
			INSERT_QUERY = INSERT_QUERY+" ) ";
			INSERT_QUERY2 = INSERT_QUERY2 + " ) ";
			
			System.out.println("INSERT QUERY IS "+INSERT_QUERY);
			System.out.println("INSERT QUERY 2 IS "+INSERT_QUERY2);
			
			INSERT_QUERY = INSERT_QUERY + INSERT_QUERY2;
			System.out.println("INSERT_QUERY "+INSERT_QUERY);
			
			PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
			ps.setString(1, beanObj.getFileDate());
			
			for(List<String> fileDatas : Data)
			{
				sr_no = 1;
				for(String col : fileDatas)
				{
					System.out.println(sr_no);
					ps.setString(++sr_no, col);
				}
				ps.execute();
				System.out.println("Inserted");
			}
			
			
			
			System.out.println("Completed inserting ");
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Exception while reading JV File "+e);
			return false;
		}
	}

}
