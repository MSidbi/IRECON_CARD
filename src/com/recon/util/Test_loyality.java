package com.recon.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.recon.loyality.SettlementTypeBean;

public class Test_loyality {


    public static void main(final String[] args) throws IOException {
        final String exten = ".txt";
        final String path = "D:/loyalty.txt";
        final BufferedWriter output = null;
        final int row_lenth_1 = 168;
        final int row_lenth_2 = 45;
        final int row_lenth_3 = 21;
        final int count_1 = 0;
        final int count_2 = 0;
        final int count_3 = 0;
        final String julian_date = null;
        final int result_count = 0;
        final String result_count_val = null;
        final String sun_val = null;
        final int main_count = 0;
        final ArrayList<String> arr_test = new ArrayList<String>();
        final ArrayList<String> arr_test2 = new ArrayList<String>();
        final ArrayList<SettlementTypeBean> arr = new ArrayList<SettlementTypeBean>();
        final Scanner scan = new Scanner(System.in);
        System.out.println("Enter Start Date in mm/dd/yyyy format :: ");
        final String dt_va = scan.next();
        System.out.println("Enter End Date in mm/dd/yyyy format :: ");
        final String dt_va2 = scan.next();
        final String timeStamp = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        System.out.println("Process Started...please wait a moment " + timeStamp);
        final String julian_date2 = null;
        final String local_date = null;
        final int startInd = 1;
        final int endInd = 50000;
        int count_v = 0;
        final int counter_value = 0;
        int count_val = 0;
        BufferedWriter bw = null;
        SettlementTypeBean sb = null;
        final FileWriter writer = null;
        int while_count = 0;
        try {
            final OracleConn conn = new OracleConn();
            final Connection con = conn.getconn();
            final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
            final Date date = new Date();
            System.out.println(formatter.format(date));
            final String get_count = "select count(*) as count from(SELECT distinct os1.PAN,os1.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE,  TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) as ISSUER  FROM BK_SETTLEMENT_VISA_SWITCH os1 where os1.DCRS_REMARKS='VISA_ISS-MATCHED-2' and os1.ISS_CURRENCY_CODE='356' and os1.FILEDATE between to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY') " + " UNION ALL " + " SELECT os2.PAN,os2.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os2.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os2.TERMID,os2.MERCHANT_TYPE, " + " TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os2.PAN||os2.AMOUNT_EQUIV*100||ltrim(substr(os2.issuer,7,7),0) as AUTHNUM ,os2.TERMLOC,ltrim(substr(os2.issuer,7,7),0) as ISSUER " + "FROM SETTLEMENT_RUPAY_SWITCH_BK os2 where os2.DCRS_REMARKS in ('RUPAY_DOM-MATCHED-2','RUPAY_INT-MATCHED-2') and os2.ISS_CURRENCY_CODE='356'  and os2.FILEDATE between to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY') UNION ALL SELECT distinct os1.PAN, " + " os2.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE, " + "TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) " + "as ISSUER FROM SETTLEMENT_POS_C os1 inner join SETTLEMENT_POS_CBS OS2 ON TO_NUMBER(TRIM (REPLACE (OS1.AMOUNT, ',','')))=TO_NUMBER(TRIM (REPLACE (rtrim ( rtrim ( to_char(OS2.AMOUNT), 0 ), '.' ), ',',''))) AND " + "LPAD(SUBSTR(OS1.TRACE,2,6),6,'0')=LPAD(SUBSTR(OS2.REF_NO,2,6),6,'0') AND TRIM(OS1.PAN)=TRIM(OS2.REMARKS) AND OS2.DCRS_REMARKS='POS_ONU_CBS_MATCHED'" + "  WHERE OS1.DCRS_REMARKS='POS_ONU_C_MATCHED' AND " + " OS1.FILEDATE BETWEEN to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY') UNION ALL SELECT distinct os1.PAN, os2.CONTRA_ACCOUNT,'D'," + " TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE, " + " TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) " + "as ISSUER FROM SETTLEMENT_MASTERCARD_SWITCH os1 inner join SETTLEMENT_MASTERCARD_CBS OS2 ON to_number(OS1.AMOUNT_EQUIV)=to_number(replace(OS2.AMOUNT,',','')) AND" + " LPAD(SUBSTR(OS1.TRACE,2,6),6,'0')=LPAD(SUBSTR(OS2.REF_NO,2,6),6,'0') AND TRIM(OS1.PAN)=TRIM(OS2.REMARKS) AND OS2.DCRS_REMARKS='MASTERCARD_ISS_CBS_MATCHED' WHERE" + " OS1.DCRS_REMARKS IN ('MASTERCARD_ISS_C1_MATCHED','MASTERCARD_ISS_C2_MATCHED','MASTERCARD_ISS_C3_MATCHED') AND OS1.FILEDATE BETWEEN to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY')) ";
            System.out.println(get_count);
            final PreparedStatement st1 = con.prepareStatement(get_count);
            final ResultSet rs1 = st1.executeQuery();
            while (rs1.next()) {
                count_v = rs1.getInt("count");
                System.out.println("Total Record::-" + count_v);
            }
            final File file = new File(path);
            bw = new BufferedWriter(new FileWriter(file, true));
            final long random_number = Utility.generateRandom2();
            System.out.println(random_number);
            bw.append((CharSequence)("FH" + formatter.format(date) + random_number));
            bw.append((CharSequence)"\n");
            int rs_count = 0;
            final String get_bankrepo = "SELECT distinct * FROM (select distinct OS3.* ,ROWNUM R from(SELECT distinct os1.PAN,os1.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' , os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE,  TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) as ISSUER  FROM BK_SETTLEMENT_VISA_SWITCH os1 where os1.DCRS_REMARKS='VISA_ISS-MATCHED-2' and os1.ISS_CURRENCY_CODE='356'  and os1.FILEDATE between to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY')  UNION ALL  SELECT distinct os2.PAN, " + "os2.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os2.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os2.TERMID,os2.MERCHANT_TYPE, " + " TO_CHAR(TO_DATE(os2.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os2.PAN||os2.AMOUNT_EQUIV*100||ltrim(substr(os2.issuer,7,7),0) as AUTHNUM ,os2.TERMLOC,ltrim(substr(os2.issuer,7,7),0) " + " as ISSUER FROM SETTLEMENT_RUPAY_SWITCH_BK os2 where os2.DCRS_REMARKS in ('RUPAY_DOM-MATCHED-2','RUPAY_INT-MATCHED-2') and os2.ISS_CURRENCY_CODE='356'  and os2.FILEDATE between to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY') UNION ALL SELECT distinct os1.PAN, " + " os2.CONTRA_ACCOUNT,'D',TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE, " + " TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) " + " as ISSUER FROM SETTLEMENT_POS_SWITCH os1 inner join SETTLEMENT_POS_CBS OS2 ON TO_NUMBER(TRIM (REPLACE (OS1.AMOUNT, ',','')))=TO_NUMBER(TRIM (REPLACE (rtrim ( rtrim ( to_char(OS2.AMOUNT), 0 ), '.' ), ',',''))) AND " + " LPAD(SUBSTR(OS1.TRACE,2,6),6,'0')=LPAD(SUBSTR(OS2.REF_NO,2,6),6,'0') AND TRIM(OS1.PAN)=TRIM(OS2.REMARKS) AND OS2.DCRS_REMARKS='POS_ONU_CBS_MATCHED'" + "  WHERE OS1.DCRS_REMARKS='POS_ONU_SWITCH_MATCHED' AND " + " OS1.FILEDATE BETWEEN to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY') UNION ALL SELECT distinct os1.PAN, os2.CONTRA_ACCOUNT,'D'," + " TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd') as LOCAL_DATE ,'0000000' ,os1.AMOUNT_EQUIV*100 as AMOUNT_EQUIV,os1.TERMID,os1.MERCHANT_TYPE, " + "TO_CHAR(TO_DATE(os1.LOCAL_DATE ,'mm/dd/yyyy'),'yyyymmdd')||os1.PAN||os1.AMOUNT_EQUIV*100||ltrim(substr(os1.issuer,7,7),0) as AUTHNUM ,os1.TERMLOC,ltrim(substr(os1.issuer,7,7),0) " + " as ISSUER FROM SETTLEMENT_MASTERCARD_SWITCH os1 inner join SETTLEMENT_MASTERCARD_CBS OS2 ON to_number(OS1.AMOUNT_EQUIV)=to_number(replace(OS2.AMOUNT,',','')) AND" + " LPAD(SUBSTR(OS1.TRACE,2,6),6,'0')=LPAD(SUBSTR(OS2.REF_NO,2,6),6,'0') AND TRIM(OS1.PAN)=TRIM(OS2.REMARKS) AND OS2.DCRS_REMARKS='MASTERCARD_ISS_CBS_MATCHED' WHERE" + " OS1.DCRS_REMARKS IN ('MASTERCARD_ISS_C1_MATCHED','MASTERCARD_ISS_C2_MATCHED','MASTERCARD_ISS_C3_MATCHED') AND OS1.FILEDATE BETWEEN to_date('" + dt_va + "','MM/DD/YYYY')  and  TO_date('" + dt_va2 + "','MM/DD/YYYY')) " + "  OS3 ) ";
            System.out.println("Query -->>" + get_bankrepo);
            final PreparedStatement st2 = con.prepareStatement(get_bankrepo);
            final ResultSet rs2 = st2.executeQuery();
            while (rs2.next()) {
                sb = new SettlementTypeBean();
                sb.setPan(rs2.getString("PAN"));
                sb.setcONTRA_ACCOUNT(rs2.getString("CONTRA_ACCOUNT"));
                sb.setLocal_date(rs2.getString("LOCAL_DATE"));
                sb.setAmount(rs2.getString("AMOUNT_EQUIV"));
                sb.settERMID(rs2.getString("TERMID"));
                sb.setMercahnt_type(rs2.getString("MERCHANT_TYPE"));
                sb.setAuthnum(rs2.getString("AUTHNUM"));
                sb.setTermloc(rs2.getString("TERMLOC"));
                sb.setIssuer(rs2.getString("ISSUER"));
                arr.add(sb);
                ++while_count;
                rs_count = rs2.getRow();
                if (arr.size() >= 100000) {
                    count_val = get_process(arr, startInd, endInd, while_count, bw, count_val, rs_count);
                }
            }
            count_val = get_process(arr, startInd, endInd, while_count, bw, count_val, rs_count);
            final int total = 0;
            System.out.println(String.valueOf(formatter.format(date)) + random_number);
            System.out.println("Array size-->" + arr.size());
            final String getrw = Integer.toString(count_val);
            final String row = Utility.auto_append3(getrw);
            bw.append((CharSequence)("FT" + row));
            bw.close();
            System.out.println("FT");
            arr.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            System.out.println("Process Completed..");
            bw.close();
        }
        System.out.println("Process Completed..");
        bw.close();
    }
    
    public static int get_process(final ArrayList<SettlementTypeBean> arr, final int startInd, final int endInd, final int while_count, final BufferedWriter bw, int count_val, final int rs_count) {
        try {
            for (int j = 0; j < arr.size(); ++j) {
                ++count_val;
                SettlementTypeBean generateTTUMBeanObj = new SettlementTypeBean();
                generateTTUMBeanObj = arr.get(j);
                if (generateTTUMBeanObj.getAuthnum().equals("20180407472258990121620168000418540")) {
                    System.out.println("Inside duplicate-->>" + generateTTUMBeanObj.getAuthnum());
                }
                bw.append((CharSequence)generateTTUMBeanObj.getPan());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getcONTRA_ACCOUNT());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)"D");
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getLocal_date());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)"000000");
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getAmount());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.gettERMID());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getMercahnt_type());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getTermloc());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getAuthnum());
                bw.append((CharSequence)"|");
                bw.append((CharSequence)generateTTUMBeanObj.getIssuer());
                bw.append((CharSequence)"\n");
            }
            final int count_main = 0;
            arr.clear();
            System.out.println("While count -->" + while_count);
            System.out.println("Rs count -->" + rs_count);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return count_val;
    }
}
