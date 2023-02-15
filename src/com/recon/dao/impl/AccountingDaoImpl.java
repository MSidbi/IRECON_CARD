package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_SEARCH_PARAMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.AccountingDao;
import com.recon.model.AccountingConfigBean;
import com.recon.model.FilterationBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.util.demo;

@Component
public class AccountingDaoImpl extends JdbcDaoSupport implements AccountingDao{

	
	
	public void addAccountingData(AccountingConfigBean accountingBean)
	{
		
	}
	
	@Override
	public List<List<GenerateTTUMBean>> getReportERecords(GenerateTTUMBean generatettumBean,int inRec_Set_Id)throws Exception
	{
		
		logger.info("***** AccountingDaoImpl.getReportERecords Start ****");
		
		List<List<GenerateTTUMBean>> Data = new ArrayList<>();
		List<GenerateTTUMBean> ttum_data = new ArrayList<>();
		List<GenerateTTUMBean> TTUM_C_Data = new ArrayList<>();
		List<GenerateTTUMBean> TTUM_D_Data = new ArrayList<>();
		List<GenerateTTUMBean> Excel_headers = new ArrayList<>();
		List<String> Headers = new ArrayList<>();
		List<String> Headers1 = new ArrayList<>();
		String[] out_par=null;
		double total_amount=0;
        String amount=null;
		String stTran_particulars="";
		String total2=null;
		String remark="";
		String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE), RECORDS_DATE DATE";
		String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try
		{
			java.util.Date varDate=null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			try {
			     varDate=dateFormat.parse(generatettumBean.getStFile_Date());
			    dateFormat=new SimpleDateFormat("ddMMyy");
			    logger.info("Date :"+dateFormat.format(varDate));
			}catch (Exception e) {
				logger.error(" error in AccountingDaoImpl.getReportERecords", new Exception("AccountingDaoImpl.getReportERecords",e));
				 throw e;
			}
			
			Headers.add("ACCOUNT NUMBER");
			Headers.add("CURRENCY CODE");
			Headers.add("SERVICE OUTLET");
			Headers.add("PART TRAN TYPE");
			Headers.add("TRANSACTION AMOUNT");
			Headers.add("TRANSACTION PARTICULARS");
			Headers.add("REFERENCE CURRENCY CODE");
			Headers.add("REFERENCE AMOUNT");
			Headers.add("REMARKS");
			Headers.add("REFERENCE NUMBER");
			Headers.add("ACCOUNT REPORT CODE");
			
			Headers1.add("ACCOUNT_NUMBER");
			Headers1.add("CURRENCY_CODE");
			Headers1.add("SERVICE_OUTLET");
			Headers1.add("PART_TRAN_TYPE");
			Headers1.add("TRANSACTION_AMOUNT");
			Headers1.add("TRANSACTION_PARTICULARS");
			Headers1.add("REFERENCE_CURRENCY_CODE");
			Headers1.add("REFERENCE_AMOUNT");
			Headers1.add("REMARKS");
			Headers1.add("REFERENCE_NUMBER");
			Headers1.add("ACCOUNT_REPORT_CODE");
			
			generatettumBean.setStExcelHeader(Headers);
			
			Excel_headers.add(generatettumBean);
			
			
			String datediff =" Select to_date(MAX(FILEDATE),'DD/MM/RRRR')  - to_date('"+generatettumBean.getStDate()+"', 'DD/MM/RRRR' )  from SETTLEMENT_RUPAY_SWITCH where dcrs_remarks like '%"+generatettumBean.getStSubCategory().substring(0, 3)+"%' " ;
			int dateDiff = 	getJdbcTemplate().queryForObject(datediff, Integer.class);
			
			String GET_DATA ="";
			if(dateDiff > 3){
				  GET_DATA = "SELECT AMOUNT,CONTRA_ACCOUNT,NVL(DIFF_AMOUNT,'0') AS DIFF_AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE," +
						"SUBSTR(TRACE,-6,6) AS TRACE,ACCEPTORNAME,MERCHANT_TYPE " +
						" FROM SETTLEMENT_RUPAY_SWITCH_BK WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+generatettumBean.getStDate()+"'";
				
			}else{
				  GET_DATA = "SELECT AMOUNT,CONTRA_ACCOUNT,NVL(DIFF_AMOUNT,'0') AS DIFF_AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE," +
						"SUBSTR(TRACE,-6,6) AS TRACE,ACCEPTORNAME,MERCHANT_TYPE " +
						" FROM SETTLEMENT_RUPAY_SWITCH WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+generatettumBean.getStDate()+"'";
				
			}
			
		logger.info("GET DATA "+GET_DATA);
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			String local_date = "";
			boolean flag = false;			
			
			while(rset.next()) {
				flag = true;
				GenerateTTUMBean generateTTUMBeanObj = new GenerateTTUMBean();
				//CHECK WHETHER THE TRANSACTION IS PETROL 
				String stMerchant_Code = rset.getString("MERCHANT_TYPE");
				if(stMerchant_Code.equals("5541") || stMerchant_Code.equals("5542") 
				    || stMerchant_Code.equals("5983") ||stMerchant_Code.equals("5172") )
				{
					//CHECK WHETHER THE CARD IS PLATINUM
					String CHECK_CARD_TYPE = "SELECT COUNT(*) FROM MAIN_CARD_DETAILS WHERE VALUE = SUBSTR('"+rset.getString("PAN")+"',1,6)";
					int card_count = getJdbcTemplate().queryForObject(CHECK_CARD_TYPE,new Object[] {},Integer.class);
					if(card_count >0)
					{
						//CHECK THE AMOUNT DR IN WAVIER ACCOUNT FOR THAT CUST
						String CHECK_WAVIER_DR = "SELECT NVL(SUM(AMOUNT_DR),0) FROM MAIN_TRACK_TXN WHERE CARD_NUM = '"+rset.getString("PAN")+"' AND amount_dr<>'null'";
						 
						int total_DRAmt = getJdbcTemplate().queryForObject(CHECK_WAVIER_DR,new Object[] {},Integer.class);
											
						//CHECK THE AMOUNT IS BETWEEN THE PROVIDED RANGE
						String CHECK_AMT = "SELECT COUNT(*) FROM MAIN_ACCOUNTING_TABLE WHERE CATEGORY = '"+generatettumBean.getStCategory()+"_"+generatettumBean.getStSubCategory()
								+"' AND MIN_AMT <'"+rset.getString("AMOUNT")+"' AND MAX_AMT > '"+rset.getString("AMOUNT")+"' AND MAX_AMT > '"+total_DRAmt+"' " +
								"AND (MAX_AMT - "+total_DRAmt+") >= '"+rset.getString("AMOUNT")+"'"; // last condition is to check whether balance capping is greater than amount to be debited
						int amt_count = getJdbcTemplate().queryForObject(CHECK_AMT, new Object[] {},Integer.class);

						if(amt_count > 0) 
						{
							//ADD AN ENTRY IN TRACK TABLE
							String INSERT_QUERY = "INSERT INTO MAIN_TRACK_TXN (CARD_NUM , ACCOUNT_NUM , AMOUNT_DR, CREATEDDATE,CREATEDBY,CATEGORY,FILEDATE)" +
									" VALUES('"+rset.getString("PAN")+"','99987750010146','"+rset.getString("DIFF_AMOUNT")
									+"',SYSDATE,'"+generatettumBean.getStEntry_by()+"','"+generatettumBean.getStCategory()+"_"+generatettumBean.getStSubCategory()+"'," +
											"TO_DATE('"+generatettumBean.getStDate()+"','DD/MM/YYYY'))";
							
							logger.info("INSERT_QUERY "+INSERT_QUERY);
							
							getJdbcTemplate().execute(INSERT_QUERY);
							
							//DEBIT FROM WAIVER ACCOUNT
							generateTTUMBeanObj.setStDebitAcc("99987750010146");
						}
						else
						{
							//DEBIT FROM CUST ACC
							//out_par=rset.getString("ACCTNUM").split("\\s+");
							generateTTUMBeanObj.setStDebitAcc(rset.getString("CONTRA_ACCOUNT"));
						}
					}
					else
					{
						//DEBIT FROM CUST ACC
						//out_par=rset.getString("ACCTNUM").split("\\s+");
						generateTTUMBeanObj.setStDebitAcc(rset.getString("CONTRA_ACCOUNT"));
					}
				}
				else
				{
					//DEBIT FROM CUST ACC
					//out_par=rset.getString("ACCTNUM").split("\\s+");
					generateTTUMBeanObj.setStDebitAcc(rset.getString("CONTRA_ACCOUNT"));
				}
				
				//RUPAY GL ACC
				amount=rset.getString("DIFF_AMOUNT");
				total_amount+=Double.valueOf(amount);
				generateTTUMBeanObj.setStAmount(rset.getString("DIFF_AMOUNT"));
				stTran_particulars = "DR-RPAY-SURCHARGE-"+rset.getString("LOCAL_DATE")+"-"+rset.getString("TRACE");//+"-"+rset.getString("ACCEPTORNAME");;
				generateTTUMBeanObj.setStDate(rset.getString("LOCAL_DATE"));
				local_date = rset.getString("LOCAL_DATE");
				generateTTUMBeanObj.setStTran_particulars(stTran_particulars);
				generateTTUMBeanObj.setStCard_Number(rset.getString("PAN"));
				generateTTUMBeanObj.setAccount_repo("CHRG");
				remark = getJdbcTemplate().queryForObject("select 'RPYD'||'"+dateFormat.format(varDate)+"'||ttum_seq.nextval from dual", new Object[] {},String.class);
				generateTTUMBeanObj.setStRemark(remark);
				TTUM_D_Data.add(generateTTUMBeanObj);
				
			}
			if(flag)
			{
				GenerateTTUMBean generateTTUMBeanObj1 = new GenerateTTUMBean();
				generateTTUMBeanObj1.setStCreditAcc("99937200010660");
				generateTTUMBeanObj1.setStTran_particulars(stTran_particulars);
				generateTTUMBeanObj1.setStRemark(remark);
				total2 = String.valueOf(Double.parseDouble(new DecimalFormat("##.####").format(total_amount))); 
				generateTTUMBeanObj1.setStAmount(total2);
				generateTTUMBeanObj1.setStDate(local_date);
				generateTTUMBeanObj1.setStCard_Number("");
				generateTTUMBeanObj1.setAccount_repo("");
				TTUM_C_Data.add(generateTTUMBeanObj1);
			}
			Data.add(Excel_headers);
			Data.add(null);
			Data.add(2,TTUM_C_Data);
			Data.add(3,TTUM_D_Data);
			//update THE RECORDS AS GENERATED TTUM AND MAKE ENTRY OF THESE RECORDS IN TTUM TABLE
			for(int i = 0 ; i<Headers1.size();i++)
			{
				table_cols =table_cols+","+ Headers1.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+Headers1.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBean.getStMerger_Category()
					+"_"+generatettumBean.getStFile_Name()+"'";
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			
			if(tableExist == 0)
			{
				String CREATE_QUERY = "CREATE TABLE TTUM_"+generatettumBean.getStMerger_Category()
						+"_"+generatettumBean.getStFile_Name()+" ("+table_cols+")";
				
				getJdbcTemplate().execute(CREATE_QUERY);
			}
			
			List<String> QUERIES = new ArrayList<>();
			int count =0;
			for(int i = 0;i<TTUM_D_Data.size();i++)
			{
				count++;
				GenerateTTUMBean beanObj = new GenerateTTUMBean();
				beanObj = TTUM_D_Data.get(i);
				String INSERT_QUERY = "INSERT INTO TTUM_"+generatettumBean.getStMerger_Category()
							+"_"+generatettumBean.getStFile_Name() 
							+"("+insert_cols+") VALUES ('"+generatettumBean.getStMerger_Category()+
							"-UNRECON-TTUM-"+inRec_Set_Id
							+"',SYSDATE,'"+generatettumBean.getStEntry_by()+"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY')"+",'"+beanObj.getStDebitAcc()+"','INR','999','D','"+
							beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+
							beanObj.getStRemark()+"','"+beanObj.getAccount_repo()+"')";
				
				logger.info("INSERT_QUERY "+INSERT_QUERY);
				QUERIES.add(INSERT_QUERY);
							 
				 if(count == 200)
				 {
					 String[] insert = new String[count];
					 insert = QUERIES.toArray(insert);
					 getJdbcTemplate().batchUpdate(insert);
					 QUERIES.clear();
					 count = 0;
				 }
				 
				 
			}
			if(count > 0 )
			{

				 String[] insert = new String[count];
				 insert = QUERIES.toArray(insert);
				 getJdbcTemplate().batchUpdate(insert);
				 QUERIES.clear();
				 count = 1;
			 
			}
			
			/*for(int i = 0;i<TTUM_C_Data.size();i++)
			{*/
				
				
				if(TTUM_C_Data.size()>0)
				{
					GenerateTTUMBean beanObj = new GenerateTTUMBean();
					beanObj = TTUM_C_Data.get(0);
				

					String INSERT_QUERY = "INSERT INTO TTUM_"+generatettumBean.getStMerger_Category()
							+"_"+generatettumBean.getStFile_Name() 
							+"("+insert_cols+") VALUES ('"+generatettumBean.getStMerger_Category()
							+"-UNRECON-TTUM-"+inRec_Set_Id
							+"',SYSDATE,'"+generatettumBean.getStEntry_by()+"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+
							beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+
							beanObj.getStRemark()+"','"+beanObj.getAccount_repo()+"')";
					// QUERIES.add(INSERT_QUERY);
					
					logger.info("INSERT_QUERY "+INSERT_QUERY);
					getJdbcTemplate().execute(INSERT_QUERY);
				}
				 
			
				String UPDATE_QUERY = "";
				if(dateDiff > 3){
					 UPDATE_QUERY = "UPDATE SETTLEMENT_RUPAY_SWITCH_BK SET DCRS_REMARKS = '"+generatettumBean.getStMerger_Category()+"-UNRECON-GENERATED-TTUM-"
							+inRec_Set_Id+"' WHERE DCRS_REMARKS = '"
							+generatettumBean.getStMerger_Category()+"-UNRECON-GENERATE-TTUM-"+inRec_Set_Id +
									"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+generatettumBean.getStDate()+"'";
				}else{
					 UPDATE_QUERY = "UPDATE SETTLEMENT_RUPAY_SWITCH SET DCRS_REMARKS = '"+generatettumBean.getStMerger_Category()+"-UNRECON-GENERATED-TTUM-"
							+inRec_Set_Id+"' WHERE DCRS_REMARKS = '"
							+generatettumBean.getStMerger_Category()+"-UNRECON-GENERATE-TTUM-"+inRec_Set_Id +
									"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+generatettumBean.getStDate()+"'";
				}
			

			logger.info("update query is "+UPDATE_QUERY);
			
			getJdbcTemplate().execute(UPDATE_QUERY);
			
					
			logger.info("***** AccountingDaoImpl.getReportERecords End ****");
			
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "AccountingDaoImpl.getReportERecords");
			logger.error(" error in AccountingDaoImpl.getReportERecords", new Exception("AccountingDaoImpl.getReportERecords",e));
			 throw e;
		}
		finally{
			if(rset!=null){
				rset.close();
			}
			if(pstmt!=null){
				pstmt.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
		return Data;
		
	}

}
