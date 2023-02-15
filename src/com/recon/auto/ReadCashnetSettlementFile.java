package com.recon.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.recon.dao.SettlementDao;
import com.recon.model.CashnetSettlementFile;

public class ReadCashnetSettlementFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String stLine = null;
		CashnetSettlementFile settlementFile = new CashnetSettlementFile();
		
		String dob = "06/12/22";
		SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
	      //Parsing the given String to Date object
	      Date date;
		try {
			date = formatter.parse(dob);
			System.out.println("Date object value: "+date);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	      

		
		SettlementDao dao = null;

		try{
		File file = new File("E:\\CARDS\\Cashnet Debit Credit\\NEWAQDLB101122_1C (1).txt");
		//File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);			
		//conn.setAutoCommit(false);
		
		System.out.println("File Reading Starts");
		
		int count = 0;
		
		while((stLine = br.readLine()) != null)
		{
			
			 
			if(!stLine.contains("DLB The Dhanalakshmi Bank") && !stLine.contains("356 Indian Rupee (India)") && !stLine.contains("CARD NUMBER") && !stLine.contains("------") && !stLine.contains("DATE") && !stLine.contains("Run Time")){
				count++;
				//System.out.println(stLine);
				
				if(stLine.contains("SUMMARY TOTALS")){
					break;
				}
				
				if(count == 1){
					settlementFile.setCardNumber(stLine.substring(0,22).trim());
					settlementFile.setTranDesc(stLine.substring(22,34).trim());
					settlementFile.setTranAmount1(stLine.substring(40,48).trim());
					settlementFile.setTranAmount2(stLine.substring(71,79).trim());
				}else if(count == 2){
					String date1 = stLine.substring(2,10).trim();
					String date2 = date1.substring(6,8)+"/"+date1.substring(3,5)+"/"+date1.substring(0,2);
					System.out.println(date2);
					settlementFile.setDate(date2);
					settlementFile.setTime(stLine.substring(12,18).trim());
					settlementFile.setSer(stLine.substring(22,34).trim());
					settlementFile.setTrace(stLine.substring(35,47).trim());
					settlementFile.setRRN(stLine.substring(48,56).trim());
					settlementFile.setAddress(stLine.substring(57,80).trim());
					settlementFile.setCity(stLine.substring(80,93).trim());
					settlementFile.setVnd(stLine.substring(93,97).trim());
					
//					int a = dao.saveDataToCashnetSettlement(settlementFile);
					count = 0;
					settlementFile = new CashnetSettlementFile();
				}
			}
		}
		
		}catch(Exception e){
			System.out.println("Exception in ReadCashnetSettlementFile: "+e);
		}
	}

}
