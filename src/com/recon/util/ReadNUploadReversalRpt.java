package com.recon.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

public class ReadNUploadReversalRpt {
	
	Connection con;
	PreparedStatement ps;
	Statement st;

	
	public void  uploadData(String filename,String filedate) {
		
		ResultSet rs;
	
	try {
		
		File file = new File("\\\\10.144.136.101\\Shareon101\\DCRS\\ReversalFiles\\"+filename);
		FileInputStream fstream = new FileInputStream(file);
		
		 POIFSFileSystem fs = new POIFSFileSystem(fstream);
         HSSFWorkbook wb = new HSSFWorkbook(fs);
         HSSFSheet sheet = wb.getSheetAt(0);
         
         
         Row row = (Row)sheet.getRow(4);
         int noOfColumns = sheet.getRow(3).getLastCellNum();
         //String dateval=row.getCell(7).getStringCellValue();
         
         OracleConn conn = new OracleConn();
    	 con= conn.getconn();
    	 
    	 st=con.createStatement();
    	 rs = st.executeQuery("Select distinct filedate from SETTLEMENT_NFS_ISS_REV_REPORT where filedate = to_date('"+filedate+"','dd/mm/yyyy') "); 
    	 String prevdate;
    	
    	 boolean result= false;
    	 while (rs.next()) {
    		 
    		 result = true;
    		 
    	 }
         for(int i =3;i<= sheet.getLastRowNum();i++) {
        	 
        	 row = (Row)sheet.getRow(i);
        	 String offsitebTotal = row.getCell(0).getStringCellValue();
        	 
        	 System.out.println(row.getCell(2).getStringCellValue());
        	 
        	 System.out.println(offsitebTotal);
        	 
        	 final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
        	 
        	
        	
        	if(!result) {
        	 
        	String sql = "insert into SETTLEMENT_NFS_ISS_REV_REPORT (TransType ,Resp_Code,Cardno ,RRN ,StanNo ,ACQ ,ISS ,Trasn_Date,Trans_Time ,ATMId ,SettleDate ,RequestAmt ,ReceivedAmt ,Status,dcrs_remarks,Filedate)"
        			+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy') )";
        	 ps= con.prepareStatement(sql);
        	 
        	 FormulaEvaluator objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
        	 
        	 DataFormatter objDefaultFormat = new DataFormatter();
             Cell txn_time = row.getCell(8);
             Cell atm_id = row.getCell(9);
             Cell settl_dt = row.getCell(10); 
             Cell cell7  = row.getCell(7);
             objFormulaEvaluator.evaluate(atm_id); // This will evaluate the cell, And any type of cell will return string value
             String atmid=objDefaultFormat.formatCellValue(atm_id,objFormulaEvaluator);
             objFormulaEvaluator.evaluate(txn_time);
             String txntime = objDefaultFormat.formatCellValue(txn_time,objFormulaEvaluator);
             objFormulaEvaluator.evaluate(settl_dt);
             String settldt = objDefaultFormat.formatCellValue(settl_dt,objFormulaEvaluator);
             objFormulaEvaluator.evaluate(cell7);
             String cell_7 = objDefaultFormat.formatCellValue(cell7,objFormulaEvaluator);
            

            
            
        		
				ps.setString(1, row.getCell(0).getStringCellValue().replace("'", ""));
				ps.setString(2, row.getCell(1).getStringCellValue().replace("'", ""));
				ps.setString(3, row.getCell(2).getStringCellValue().replace("'", ""));
				ps.setString(4, row.getCell(3).getStringCellValue().replace("'", ""));
				
				BigDecimal bd= new BigDecimal(row.getCell(4).getNumericCellValue());
				
				
				ps.setLong(5, bd.longValue());
				ps.setString(6, row.getCell(5).getStringCellValue().replace("'", ""));
				ps.setString(7, row.getCell(6).getStringCellValue().replace("'", ""));
				ps.setString(8, (cell_7.replaceAll("\u00A0", "").replace("'", "")));
				ps.setString(9, filedate);
				
				
			       final Date dateObj = sdf.parse(txntime.replaceAll("\u00A0", ""));
			       
				
				ps.setString(9, new SimpleDateFormat("Kmmss").format(dateObj) );
				ps.setString(10, (atmid.replaceAll("\u00A0", "")).replace("'", ""));
				ps.setString(11, (settldt.replaceAll("\u00A0", "")).replace("'", ""));
				ps.setString(12, String.valueOf(row.getCell(11).getNumericCellValue()));
				ps.setString(13, String.valueOf(row.getCell(12).getNumericCellValue()));
				ps.setString(14, row.getCell(13).getStringCellValue().replace("'", ""));
				ps.setString(15,"UNMATCHED" );
				ps.setString(16, filedate);
				/*System.out.println(row.getCell(8).getDateCellValue().toString());
				 final Date dateObj = sdf.parse(row.getCell(8).getDateCellValue().toString());
				 System.out.println(new SimpleDateFormat("Kmmss").format(dateObj) );*/
			
				
				
				ps.executeUpdate();
				
        	}else {
        		
        		System.out.println("File already Uploaded");
        		
        	}
        	
        	
         }
         
         ps.close();
         
         
         
         String query= "UPDATE SETTLEMENT_NFS_ISS_REV_REPORT set Filedate= to_date('"+filedate+"','dd/mm/yyyy') where dcrs_remarks = 'UNMATCHED' ";
         
         ps = con.prepareStatement(query);
         ps.executeUpdate();
         
      		
		
	} catch (Exception e) {
		
		e.printStackTrace();
		// TODO: handle exception
	}
	}
	
	public static void main(String[] args) {
		
		
		ReadNUploadReversalRpt reversalFile = new ReadNUploadReversalRpt();
		
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter a file Path: ");
			System.out.flush();
			String filename = scanner.nextLine();
			File file = new File(filename);
			System.out.print("Enter a FileDate (dd/mm/yyyy):");
			String filedate=scanner.nextLine();
			
			
			System.out.println(file.getName());
			
			
			File f = new File("\\\\10.144.136.101\\Shareon101\\DCRS\\ReversalFiles");
			if(!(f.exists())) {
				
				if(f.mkdir()) {
					
					System.out.println("directory created");
				}
			}
			
			
			if(file.renameTo(new File("\\\\10.144.136.101\\Shareon101\\DCRS\\ReversalFiles\\" + file.getName()))) {
				
				System.out.println("File Moved Successfully");
				
				reversalFile.uploadData(file.getName(),filedate);
				
			}else {
				
				System.out.println("Error Occured while moving file");
			}
			
			
			
			
	    
	}
	
}
