package com.recon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.UploadTTUMBean;

public class ReadTextFile {

	/**
	 * @param args
	 * @throws IOException
	 */
	public  static  boolean read_fail(UploadTTUMBean uploadBean,
			MultipartFile file) {
		// TODO Auto-generated method stub
		String path = ""; // ADD YOUR PATH HERE
		String fileName = "D:/Card_to_Card/New folder/EP100A.txt";
		String testWord = "RECORD"; // CHANGE THIS IF YOU WANT
		int tLen = testWord.length();
		int wordCntr = 0;
		// String file = path + fileName;
		boolean check;
		boolean first = true;
		boolean second = true;
		boolean third = true;
		boolean fourth = true;
		String part_tran = null;
		Statement st;
		
		
		java.util.Date varDate1=null;
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		try {
		     varDate1=dateFormat1.parse(uploadBean.getStDate());
		    dateFormat1=new SimpleDateFormat("MM/dd/yyyy");
		    System.out.println("Date :"+dateFormat1.format(varDate1));
		    uploadBean.setStDate(dateFormat1.format(varDate1));
		}catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}
		
		
		
		java.util.Date varDate=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
		     varDate=dateFormat.parse(uploadBean.getStDate());
		    dateFormat=new SimpleDateFormat("ddMMyy");
		    System.out.println("Date :"+dateFormat.format(varDate));
		}catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}
		ArrayList<String> arr = new ArrayList<String>();
		try {
			OracleConn conn = new OracleConn();
			Connection con = conn.getconn();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					file.getInputStream()));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// check to see whether testWord occurs at least once in the
				// line of text
				check = strLine.toLowerCase().contains(testWord.toLowerCase());
				if (check) {
					// get the line, and parse its words into a String array
					String[] lineWords = strLine.split("\\s+");

					while ((lineWords[1].equals(testWord))) {
						if (first) {
							strLine = br.readLine();
							first = false;
						} else if (second) {
							strLine = br.readLine();
							second = false;
						} else if (third) {
							strLine = br.readLine();
							third = false;
						} else {
							strLine = br.readLine();
							String split_line[] = strLine.split("\\s+");
							System.out.println(split_line[1].substring(4, 20));
							String get_arn = split_line[2].substring(1, 23);
							System.out.println(split_line[2].substring(1, 23));
							// arr.add(split_line[1].substring(4,20));

							String get_column_vale = "SELECT COUNT (*) as count"
									+ " FROM user_tab_cols "
									+ " WHERE column_name = 'TTUM_STATUS' AND table_name = UPPER('SETTLEMENT_CARDTOCARD_BANKREPO')";

							con = conn.getconn();
							st = con.createStatement();
							ResultSet rs = st.executeQuery(get_column_vale);
							if (rs.next()) {
								String count = rs.getString("count");
								if (count.equals("0")) {
									String update_query = "alter table SETTLEMENT_CARDTOCARD_BANKREPO add TTUM_STATUS varchar2(100)";
									con = conn.getconn();
									st = con.createStatement();
									st.executeUpdate(update_query);
								}
							}

							String update_ttum_rec = "update SETTLEMENT_CARDTOCARD_BANKREPO set TTUM_STATUS='Failed TTUM' where VISA_CARD_NO='"
									+ split_line[1].substring(4, 20)
									+ "' and TO_CHAR (filedate, 'mm/dd/yyyy') = "
									+ "TO_CHAR (TO_DATE ('"
									+ uploadBean.getStDate()
									+ "', 'MM/DD/YYYY'), 'MM/DD/YYYY')";
							con = conn.getconn();
							st = con.createStatement();
							st.executeUpdate(update_ttum_rec);
							wordCntr++;
							first = true;
							second = true;
							third = true;
							break;
						}
					}
				}
			}
			String get_ttum_rec = "select * from SETTLEMENT_CARDTOCARD_BANKREPO where TTUM_STATUS='Failed TTUM' and TO_CHAR (filedate, 'mm/dd/yyyy') = "
					+ "TO_CHAR (TO_DATE ('"
					+ uploadBean.getStDate()
					+ "', 'MM/DD/YYYY'), 'MM/DD/YYYY')";
			con = conn.getconn();
			st = con.createStatement();
			ResultSet rs1 = st.executeQuery(get_ttum_rec);
			String get_accnu = null;
			String amount = null;
			while (rs1.next()) {

				for (int i = 1; i <= 2; i++) {
					if (i == 1) {
						part_tran = "D";
						get_accnu = "99937200010067";
						amount = rs1.getString("amount");
					} else if (i == 2) {
						part_tran = "C";
						get_accnu = rs1.getString("DEBIT_ACC");
						// get_accnu=rs1.getString("VISA_CARD_NO");
						amount = rs1.getString("amount");
					}
					String insert_val = "INSERT INTO CARD_to_CARD_ttum2(ACCOUNT_NUMBER,CURRENCY_CODE,SERVICE_OUTLET,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULARS,ref_num,REF_CURR_CODE,REF_TRAN_AMOUNT "
							+ ",DCRS_REMARKS) VALUES('"
							+ get_accnu
							+ "','INR','999','"
							+ part_tran
							+ "','"
							+ amount
							+ "','REV/C2C-REJECTED"
							+ uploadBean.getStDate()
							+ "','Invalid Card','INR','" + amount + "','C2C'||"+dateFormat.format(varDate)+"||ttum_seq.nextval)";
					System.out.println(insert_val);
					con = conn.getconn();
					st = con.createStatement();
					st.executeUpdate(insert_val);
				}

			}

			System.out.println("total is: " + wordCntr);
			// Close the input stream
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
