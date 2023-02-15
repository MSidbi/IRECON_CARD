package com.recon.loyality;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.recon.util.OracleConn;

public class Loyalty {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String path = "D:/loyalty.txt";
		BufferedWriter output = null;
		OracleConn conn;
		int row_lenth_1 = 168;
		int row_lenth_2 = 45;
		int row_lenth_3 = 21;
		int count_1 = 0;
		int count_2 = 0;
		int count_3 = 0;
		String julian_date = null;
		int result_count = 0;
		String result_count_val = null;
		String sun_val = null;
		int main_count = 0;
		ArrayList<String> arr_test = new ArrayList<String>();
		ArrayList<String> arr_test2 = new ArrayList<String>();
		ArrayList<SettlementTypeBean> arr = new ArrayList<SettlementTypeBean>();
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Start Date in mm/dd/yyyy format :: ");
		String dt_va = scan.next();
		System.out.println("Enter End Date in mm/dd/yyyy format :: ");
		String dt_va2 = scan.next();
		System.out.println("Date :: " + dt_va);
		String julian_date1 = null;
		String local_date=null;
		int startInd=1;
		int endInd=10000;
		int count_v=0;
		try {
			conn = new OracleConn();
			Connection con = conn.getconn();
			  SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");  
			     Date date = new Date();  
			     System.out.println(formatter.format(date));  


			File file = new File(path);
			output = new BufferedWriter(new FileWriter(file));

			String get_count="SELECT count(*) as count"
 + " FROM SETTLEMENT_RUPAY_SWITCH os1 WHERE os1.DCRS_REMARKS ='RUPAY_DOM-MATCHED-2' and os1.merchant_type " 
 + "not in('6011','6012') and os1.FILEDATE between to_date('"+dt_va+"','MM/DD/YYYY')  and  TO_date('"+dt_va2+"','MM/DD/YYYY')";
			PreparedStatement st1 = con.prepareStatement(get_count);
			ResultSet rs1 = st1.executeQuery();

			while (rs1.next()) {
			count_v=rs1.getInt("count");
			}
			for(startInd=1; startInd<=count_v;startInd++)
			{
			/*String get_bankrepo = "select * from(SELECT os1.PAN,os1.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT*100 as AMOUNT,os1.TERMID,os1.MERCHANT_TYPE, "
 + "TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT*100||substr(os1.issuer,7,7) as AUTHNUM ,os1.TERMLOC,substr(os1.issuer,7,7) as ISSUER, rownum rn "
 + "FROM SETTLEMENT_RUPAY_SWITCH os1 WHERE os1.DCRS_REMARKS ='RUPAY_DOM-MATCHED-2' and os1.merchant_type "
 + "not in('6011','6012') and os1.FILEDATE between to_date('"+dt_va+"','MM/DD/YYYY')  and  TO_date('"+dt_va2+"','MM/DD/YYYY')) where rn between '"+startInd+"' and '"+endInd+"'";*/
				
				String get_bankrepo = "select * from(SELECT os1.PAN,os1.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT*100 as AMOUNT,os1.TERMID,os1.MERCHANT_TYPE, "
						 + " TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) as ISSUER, rownum rn "
						+ " FROM SETTLEMENT_VISA_SWITCH os1 where os1.DCRS_REMARKS='VISA_ISS-MATCHED-2' and os1.FILEDATE between to_date('21/01/2018','DD/MM/YYYY')  and  TO_date('22/01/2018','DD/MM/YYYY') "
						+ " UNION ALL "
						+ " SELECT os2.PAN,os2.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os2.AMOUNT*100 as AMOUNT,os2.TERMID,os2.MERCHANT_TYPE, " 
						 + " TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os2.PAN||os2.AMOUNT*100||ltrim(substr(os2.issuer,7,7),0) as AUTHNUM ,os2.TERMLOC,ltrim(substr(os2.issuer,7,7),0) as ISSUER, rownum rn "
						+ "FROM SETTLEMENT_RUPAY_SWITCH os2 where os2.DCRS_REMARKS='RUPAY_DOM-MATCHED-2' and os2.FILEDATE between to_date('21/01/2018','DD/MM/YYYY')  and  TO_date('22/01/2018','DD/MM/YYYY'))";

			PreparedStatement st = con.prepareStatement(get_bankrepo);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				SettlementTypeBean sb = new SettlementTypeBean();
				sb.setPan(rs.getString("PAN"));
				sb.setcONTRA_ACCOUNT(rs.getString("CONTRA_ACCOUNT"));
				sb.setLocal_date(rs.getString("LOCAL_DATE"));
				//sb.setLocal_time(rs.getString("LOCAL_TIME"));
				sb.setAmount(rs.getString("AMOUNT"));
				sb.settERMID(rs.getString("TERMID"));
				sb.setMercahnt_type(rs.getString("MERCHANT_TYPE"));
				
				sb.setAuthnum(rs.getString("AUTHNUM"));
				sb.setTermloc(rs.getString("TERMLOC"));
				sb.setIssuer(rs.getString("ISSUER"));
								
				//sb.settRAN_ID(rs.getString("TRANS_ID"));
				/*arr_test.add(rs.getString("PAN"));
				arr_test.add(rs.getString("TERMID"));
				arr_test.add(rs.getString("TRACE"));*/
				arr.add(sb);
				
				

			}
			FileWriter writer = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(writer);
            int total=0;
			int counter_value = 0;
			long random_number = Utility.generateRandom2();
			System.out.println(random_number);
			bw.append("FH"+formatter.format(date)+random_number);
			bw.append("\n");
			System.out.println(formatter.format(date)+random_number);
			int count_val=0;
			for(int j =0 ;j < arr.size(); j++)
			{
				//bw=new BufferedWriter(writer);
				count_val++;
				//System.out.println((arr.get(i)));
				SettlementTypeBean generateTTUMBeanObj  = new SettlementTypeBean();
				generateTTUMBeanObj = arr.get(j);
              counter_value++;
				
				bw.append(generateTTUMBeanObj.getPan());
				bw.append("|");
				bw.append(generateTTUMBeanObj.getcONTRA_ACCOUNT());
				bw.append("|");
				bw.append("D");
				bw.append("|");
				bw.append(generateTTUMBeanObj.getLocal_date());
				bw.append("|");
				bw.append("000000");
				bw.append("|");
				bw.append(generateTTUMBeanObj.getAmount());
				bw.append("|");
				bw.append(generateTTUMBeanObj.gettERMID());
				bw.append("|");
				bw.append(generateTTUMBeanObj.getMercahnt_type());
				bw.append("|");
				/*bw.append(generateTTUMBeanObj.gettERMID());
				bw.append("|");*/
				bw.append(generateTTUMBeanObj.getTermloc());
				bw.append("|");
				bw.append(generateTTUMBeanObj.getAuthnum());
				bw.append("|");
				
				bw.append(generateTTUMBeanObj.getIssuer());
				
				bw.append("\n");
				System.out.println(count_val);
				/*if(arr_test.get(i).equals("1679731"))
				{
					System.out.println("Inside end");
				}
				bw.append("|");
				if (counter_value == 3) {
					bw.append("\n");
					counter_value = 0;
					total++;
					
				}*/
			}
			
			/*for (String str : arr_test) {
				counter_value++;
				
				bw.append(str);
				if(str.equals("6521"))
				{
					System.out.println("Inside end");
				}
				bw.append("|");
				if (counter_value == 3) {
					bw.append("\n");
					counter_value = 0;
					total++;
					
				}
			}*/
			//bw.append("\n");
			int count_main=0;
			/*count_main=count_v-endInd;
			System.out.println("Sub count"+count_main);
			count_v=count_main;*/
			startInd=endInd;
			endInd=endInd+10000;
			
			String getrw=Integer.toString(counter_value);
			String row=Utility.auto_append3(getrw);
			bw.append("FT"+row);
            System.out.println("FT");
            bw.flush();
            bw.close();
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
