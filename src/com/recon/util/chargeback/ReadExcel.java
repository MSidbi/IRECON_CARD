package com.recon.util.chargeback;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ReadExcel {

	@SuppressWarnings("deprecation")
	public JSONArray read( Workbook book){
			
		JSONArray toTable = new JSONArray();
		try {
				Sheet sheet = book.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				JSONArray header = new JSONArray();
			
				while (rowIterator.hasNext()) {
					Row currentRow = rowIterator.next();
					Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = currentRow.cellIterator() ;
					while(cellIterator.hasNext()){
					Cell cell =	cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
						 
							header.add(cell.getStringCellValue().trim());
							break;
						}
					}
					break;
					}  
				
			 
				while (rowIterator.hasNext()) {
					Row currentRow = rowIterator.next();
					Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = currentRow.cellIterator() ;
					 JSONObject row = new JSONObject();
					while(cellIterator.hasNext()){
						Cell cell =	cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							row.put(header.get(cell.getColumnIndex()).toString(), cell.getStringCellValue().trim());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							Double e1Val = cell.getNumericCellValue();
							BigDecimal bd = new BigDecimal(e1Val.toString());
							long lonVal = bd.longValue();
							
							if((10*e1Val- 10*lonVal) == 0){
								row.put(header.get(cell.getColumnIndex()).toString(), lonVal);	
							}else{
								row.put(header.get(cell.getColumnIndex()).toString(), e1Val);
							}
							
							break;	
						case Cell.CELL_TYPE_BLANK:
							break;	
						default:
							break;
						}
					}
					toTable.add(row) ;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	 return toTable;
		}
	
	
	/*public static void main(String [] arg) throws IOException{
		String fileName ="D:\\check\\Sample Files\\Sample Files\\fsd.xlsx";
		Workbook workbook = null;
		 InputStream fis = new FileInputStream(fileName);
		if(fileName.toLowerCase().endsWith("xlsx")){
			workbook = new XSSFWorkbook(fis);
		}else if(fileName.toLowerCase().endsWith("xls")){
			workbook = new HSSFWorkbook(fis);
		}
		
	new ReadExcel().read(workbook);
	}*/
	
	
	
}
