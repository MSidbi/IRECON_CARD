package com.recon.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.recon.model.SettlementTypeBean;

public class CTF_convertor {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String path = "D:/Bankaway.ctf";
		BufferedWriter output = null;
		OracleConn conn;
		int row_lenth_1 = 168;
		int row_lenth_2 = 45;
		int row_lenth_3 = 21;
		int count_1=0;
		int count_2=0;
		int count_3=0;
		String julian_date=null;
		int result_count=0;
		String result_count_val=null;
		String sun_val=null;
		int main_count=0;
		ArrayList<String> arr_test = new ArrayList<String>();
		ArrayList<String> arr_test2 = new ArrayList<String>();
		ArrayList<SettlementTypeBean> arr = new ArrayList<SettlementTypeBean>();
		Scanner scan=new Scanner(System.in);
		System.out.println("Enter Date in mm/dd/yyyy format :: ");
		String dt_va=scan.next();
		System.out.println("Date :: "+dt_va);
		String julian_date1=null;
		try {
			conn = new OracleConn();
			Connection con = conn.getconn();
			
			File file = new File(path);
			output = new BufferedWriter(new FileWriter(file));

			String get_bankrepo = "select * from SETTLEMENT_CARDTOCARD_BANKREPO where DCRS_REMARKS='CARDTOCARD_BANKWAY_MATCED' and TO_CHAR (filedate, 'mm/dd/yyyy') = "
					+ " TO_CHAR (TO_DATE ('"+dt_va+"', 'MM/DD/YYYY'), 'MM/DD/YYYY')";

			PreparedStatement st = con.prepareStatement(get_bankrepo);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				SettlementTypeBean sb = new SettlementTypeBean();
				String visa_card_no = rs.getString("VISA_CARD_NO");
				String mobile_no = rs.getString("MOBILE_NO");
				String amount1 = rs.getString("AMOUNT");
				String amount = amount1.replaceAll("[,.]", "");
				String sol_id = rs.getString("SOL_ID");
				String debit_acc = rs.getString("DEBIT_ACC");
				String acc_name = rs.getString("ACC_NAME");
				String payment_id = rs.getString("PAYMENT_ID");
				String channel = rs.getString("CHANNEL");
				String date_time = rs.getString("DATE_TIME");
				String file_date = rs.getString("FILEDATE");
				
				long random_number = Utility.generateRandom();
				DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
				Date dateinput = inputFormat.parse(rs.getString("FILEDATE"));
				SimpleDateFormat sdfoffsite1 = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss.S");
				DateFormat outputFormat_new = new SimpleDateFormat("ddMMyyyy");
	    		String outputString_new = outputFormat_new.format(dateinput);
				java.util.Date datecisb;

				datecisb = sdfoffsite1.parse(file_date);

				sdfoffsite1 = new SimpleDateFormat("MM/dd/yyyy");

				System.out.println(sdfoffsite1.format(datecisb));

				String dataenew = sdfoffsite1.format(datecisb);
				
				int julian_dt = Utility.convertToJulian(outputString_new);
				int julian_dt1 = Utility.convertToJulian2(outputString_new);
				 julian_date = String.valueOf(julian_dt);
				 julian_date1 = String.valueOf(julian_dt1);
				String split_dt[] = dataenew.split("/");
				System.out.println(split_dt[0] + split_dt[1]);
				String amount_val=Utility.appnd_zeros(amount);
				System.out.println("12 digit Amount"+ amount_val);
				String get_acc_name=Utility.appnd_space(acc_name);
				
				arr_test.add("06");// 1-2
				arr_test.add("20");// 3-4
				arr_test.add(visa_card_no);// 5-20
				arr_test.add("000");// 21-23
				arr_test.add("   ");// 24-26
				//arr_test.add("7421426" + julian_date + random_number);// 27-49
			    arr_test.add(Utility.get_mod("7421426" + julian_date + random_number));
				arr_test.add("00000000");// 50-57
				arr_test.add(split_dt[0] + split_dt[1]);// 58-61
				arr_test.add("000000000000");// 62-73
				arr_test.add("   ");// 74-76
				arr_test.add(amount_val);// 77-88
				arr_test.add("356");// 89-91
				arr_test.add(get_acc_name);// 92-116
				arr_test.add("VISAMONEYTXFR");// 117-129
				arr_test.add("IN ");// 130-132
				arr_test.add("6051");// 133-136
				arr_test.add("00000");// 137-141
				arr_test.add("   ");// 142-144
				arr_test.add(" ");// 145
				arr_test.add(" ");// 146
				arr_test.add("1");//147
				arr_test.add("00");// 148-149
				arr_test.add("9");// 150
				arr_test.add(" ");// 151
				arr_test.add("      ");// 152-157
				arr_test.add(" ");// 158
				arr_test.add(" ");// 159
				arr_test.add(" ");// 160
				arr_test.add(" ");// 161
				arr_test.add("0");// 162-163
				arr_test.add(julian_date1);// 164-167
				arr_test.add("0");// 168
				
				//Newly added on 02/12/2018
				
				arr_test.add("06");// 1-2
				arr_test.add("21");// 3-4
				String sub_str= visa_card_no.substring(0, 6);
				String concat=sub_str+"4214260000";
				arr_test.add(concat);// 5-20
				arr_test.add("00");// 21-23
				arr_test.add(" ");// 23-24
				arr_test.add("PYMNT ID NO-");// 25-35
				arr_test.add(" ");// 36-37
				arr_test.add(payment_id);// 58-61
				arr_test.add("                                                          ");// 46-103
				arr_test.add("000000000000");// 104-115
				arr_test.add(" ");// 116-117
				arr_test.add("             ");// 117-129
				arr_test.add("0");// 130-131
				arr_test.add("                           ");// 131-157
				arr_test.add("000000000");// 158-166
				arr_test.add("  ");// 167-168
				arr_test.add("06");// 1-2
				arr_test.add("23");// 3-4
				arr_test.add("            ");
				arr_test.add("CRCP");
			
				
				
			}
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			//arr_test2=arr_test.iterator().next();
			String bind_rec="";
			//arr_test.indexOf(i);
			String bind_2="";
			String bind_rec1="";
			
			String bind_3="";
			int counter_value=0;
			for (String str : arr_test) {
				counter_value++;
				bw.append(str);
				//bind_rec=str;
				//System.out.println("Index value ::"+arr_test.indexOf(str));
				//bw.append("\n");
				
				count_1=count_1+1;
				System.out.println("Count ::"+count_1);
				if(str.equals(julian_date1))
				{
					bind_rec="";
					bind_rec=bind_rec+str;
					//System.out.println("Bind 1 :: "+bind_rec);
				}
				if(!bind_rec.equals(""))
				{  
					count_3=count_3+1;
					if(count_3==2)
					{
					bind_2=bind_rec+"0";
					//System.out.println("Bind 2 :: "+bind_2);
					}
				}
				if(str.equals("000000000"))
				{
					bind_rec1="";
					bind_rec1=bind_rec1+str;
					System.out.println("Bind 1 :: "+bind_rec1);
				}
				
				if(!bind_rec1.equals(""))
				{  
					count_3=count_3+1;
					if(count_3==2)
					{
					bind_3=bind_rec1+"  ";
					//System.out.println("Bind 2 :: "+bind_3);
					}
				}
				//bind_rec=bind_rec+str;
				if (bind_2.equals(julian_date1+"0")) {
					bw.append("\n");
					main_count=main_count+1;
					bind_2="";
					count_3=0;
					bind_rec="";
					//bw.newLine();
					/*count_2=count_2+1;
					if(count_2==1)
					{
						bw.append("TEST");
					}*/
//					/bw.newLine();
					count_1=0;
				}
				else if(bind_3.equals("000000000  "))
				{
					bw.append("\n");
					main_count=main_count+1;
					bind_3="";
					count_3=0;
					bind_rec1="";
					bind_2="";
					count_3=0;
					bind_rec="";
					//bw.newLine();
					/*count_2=count_2+1;
					if(count_2==1)
					{
						bw.append("TEST");
					}*/
//					/bw.newLine();
					count_1=0;
				}
				else if(str.equals("CRCP"))
				{
					System.out.println("Counter value-->>"+counter_value);
					System.out.println("Array value-->>"+arr_test.size());
					if(counter_value!=arr_test.size())
					{
					bw.append("\n");
					}
					else{
						 //bw.append(String.format("%s%n", counter_value));
						 System.out.println("Inside else");


					}
					main_count=main_count+1;
					
					bind_3="";
					count_3=0;
					bind_rec1="";
					bind_2="";
					count_3=0;
					bind_rec="";
					//bw.newLine();
					/*count_2=count_2+1;
					if(count_2==1)
					{
						bw.append("TEST");
					}*/
//					/bw.newLine();
					count_1=0;
				}
			}
			/*main_count=main_count+1;
			
			System.out.println("Main Count"+ main_count);
			String count_val=Integer.toString(main_count);
			String main_cnt=Utility.auto_append(count_val);
			int count_6=Integer.parseInt(main_cnt);
			int main_count2=count_6+1;
			String count_val2=Integer.toString(main_count2);
			String main_cnt2=Utility.auto_append(count_val2);
			int count_7=Integer.parseInt(main_cnt2);*/
			
			String get_rec_count="select count(*) as count,sum(to_number(replace((replace(os1.amount,',')),'.'))) as total from SETTLEMENT_CARDTOCARD_BANKREPO os1";
			PreparedStatement pst=con.prepareStatement(get_rec_count);
			ResultSet rst=pst.executeQuery();
			while(rst.next())
			{
				/*//result_count=rst.getInt("count");
				System.out.println("Count"+result_count);
				String val=Integer.toString(result_count);
				result_count_val=Utility.auto_append(val);*/
				sun_val=rst.getString("total");
				System.out.println("Sum"+sun_val);
				
				
			}
			/*int get_row1=result_count+1;
			String val2=Integer.toString(get_row1);
			String row_count=Utility.auto_append3(val2);
			int get_count=Integer.parseInt(row_count);
			int get_row2=get_row1+1;
			String get_rw=Integer.toString(get_row2);
			String row_count2=Utility.auto_append3(get_rw);
			String get_total=Utility.auto_append2(sun_val);*/
			
			/*bw.append("9100426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt+"000000"+"00000001"+row_count+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
			bw.append("\n");
			bw.append("9200426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt2+"000000"+"00000000"+row_count2+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");*/
			
			bw.close();
			writer.close();
			System.out.println("CTF file created");
		

		// Reading file and getting no. of files to be generated
		//String inputfile = "D:/ctf_test.txt"; // Source File Name.
		double nol = 990.0; // No. of lines to be split and saved in each
								// output file.
		File file1 = new File(path);
		Scanner scanner = new Scanner(file1);
		int count = 0;
		int count_lines=0;
		while (scanner.hasNextLine()) {
			scanner.nextLine();
			/*if(count_lines==3)
			{
			count++;
			count_lines=0;
			continue;
			}
			count_lines++;*/
			count++;
		}
		System.out.println("Lines in the file: " + count); // Displays no.
															// of lines in
															// the input
															// file.

		double temp = (count / nol);
		int temp1 = (int) temp;
		int nof = 0;
		if (temp1 == temp) {
			nof = temp1;
		} else {
			nof = temp1 + 1;
		}
		System.out.println("No. of files to be generated :" + nof); // Displays
																	// no.
																	// of
																	// files
																	// to be
																	// generated.

		// ---------------------------------------------------------------------------------------------------------

		// Actual splitting of file into smaller files
        int new_count=0;
        int total_sum=0;
        int total_rows=0;
		FileInputStream fstream = new FileInputStream(path);
		DataInputStream in = new DataInputStream(fstream);

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		for (int j = 1; j <= nof; j++) {
			total_rows=0;
			total_sum=0;
			new_count=0;
			result_count=0;
			FileWriter fstream1 = new FileWriter("D:/" + j
					+ ".ctf"); // Destination File Location
			BufferedWriter out = new BufferedWriter(fstream1);
			for (int i = 1; i <= nol; i++) {
				
				strLine = br.readLine();
				if (strLine != null) {
					total_rows++;
					out.write(strLine);
					if (i != nol) {
						out.newLine();
						if(new_count==3)
						{
							result_count++;
							new_count=0;
						}
						try{
						new_count++;
						//result_count++;
						String get_amount=strLine.substring(77, 88);
						int get_tot=Integer.parseInt(get_amount);
						total_sum+=get_tot;
						}catch(Exception e)
						{
							e.printStackTrace();
							continue;
						}
					}
					
				}else{
					result_count++;
					
					break;
				}
				
			}
			if(j!=nof)
			{
				result_count++;
				out.newLine();
				System.out.println("Inside no of lines"+nof);
			}
			
			System.out.println("Total Sum::"+total_sum);
			total_rows=total_rows+1;
			
			System.out.println("Main Count"+ total_rows);
			String count_val=Integer.toString(total_rows);
			String main_cnt=Utility.auto_append(count_val);
			int count_6=Integer.parseInt(main_cnt);
			int main_count2=count_6+1;
			String count_val2=Integer.toString(main_count2);
			String main_cnt2=Utility.auto_append(count_val2);
			System.out.println("Total count ::"+result_count);
			String val=Integer.toString(result_count);
			result_count_val=Utility.auto_append(val);
			int get_row1=result_count+1;
			String val2=Integer.toString(get_row1);
			String row_count=Utility.auto_append3(val2);
			int get_count=Integer.parseInt(row_count);
			int get_row2=get_row1+1;
			String get_rw=Integer.toString(get_row2);
			String row_count2=Utility.auto_append3(get_rw);
			String tot_val=Integer.toString(total_sum);
			String get_total=Utility.auto_append2(tot_val);
           // out.newLine();
			out.append("9100426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt+"000000"+"00000001"+row_count+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
			out.newLine();
			out.append("9200426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt2+"000000"+"00000000"+row_count2+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
			out.close();
		}
        
		in.close();
	
	}
		
	

		/*
		 * PrintWriter pw = new PrintWriter(file); for(int
		 * index=0;index<arr.size();index++) {
		 * System.out.println("Output ::"+arr.get(index).getVisa_card_no() );
		 * pw.println(arr.get(index).getVisa_card_no().toString()); }
		 */

		/*
		 * String arr1=arr.toString(); for (int k = 0; k < arr.size(); k++){
		 * output.writ(arr.get(k)); }int count=1; for(String str: arr_test) {
		 * if(count==2) { output.write("//n"); count=1; } output.write(str);
		 * System.out.println("CTF file created"); count=count+1; } }
		 */

		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to CTF file created");
		} finally {
			if (output != null) {
				
				
				
				output.close();
			}
		}

	}

}
