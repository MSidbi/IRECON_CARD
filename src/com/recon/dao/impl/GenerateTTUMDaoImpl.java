package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_PARAMS;
import static com.recon.util.GeneralUtil.GET_MATCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_TTUM_COLUMNS;
import static com.recon.util.GeneralUtil.GET_TTUM_HEADERS;
import static com.recon.util.GeneralUtil.GET_TTUM_PARAMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;






import com.lowagie.text.pdf.codec.postscript.ParseException;
import com.recon.dao.GenerateTTUMDao;
import com.recon.dao.impl.ReconProcessDaoImpl.IssClassificaton;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.KnockOffBean;
import com.recon.model.Mastercbs_respbean;
import com.recon.util.demo;

@Component
public class GenerateTTUMDaoImpl extends JdbcDaoSupport implements GenerateTTUMDao {

	//DBConnection dbconn = new DBConnection();

	//REFINED METHOD
@Override	
public List<List<GenerateTTUMBean>> generateTTUM(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUM Start ****");
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		ExcelHeaders.add("ACCOUNT_NUMBER");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("SERVICE_OUTLET");
		ExcelHeaders.add("PART_TRAN_TYPE");
		ExcelHeaders.add("TRANSACTION_AMOUNT");
		ExcelHeaders.add("TRANSACTION_PARTICULARS");
		ExcelHeaders.add("REFERENCE_NUMBER");
		ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
		ExcelHeaders.add("REFERENCE_TRANSACTION_AMOUNT");
		ExcelHeaders.add("REMARKS");
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		Excel_header.add(generatettumBeanObj);
		
		for(int i = 0 ; i<ExcelHeaders.size();i++)
		{
			table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
			insert_cols = insert_cols+","+ExcelHeaders.get(i);
		}
		String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
		logger.info("check table "+CHECK_TABLE);
		int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+"("+table_cols+")";
			logger.info("CREATE QUERY IS "+query);
			getJdbcTemplate().execute(query);			
		}
		
		/*String CHECK_ACC = "SELECT COUNT(*) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
							" WHERE SUBSTR(CONTRA_ACCOUNT,4,6) = '505000'";
		int count = getJdbcTemplate().queryForObject(CHECK_ACC, new Object[]{},Integer.class);
		String GET_DATA = "";*/
		/*if(count>0)
		{*/			
			String GET_DATA ="SELECT (SUBSTR(ACCOUNT_NUMBER,1,NVL(INSTR(ACCOUNT_NUMBER,'505000'),0)-1))||37000010085 AS DRACC,CONTRA_ACCOUNT," +
					"REPLACE(TRAN_AMT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUEDATE,'DD/MM/YY'),'DDMMYY') AS VALUE_DATE, SUBSTR(TRAN_PARTICULAR,1,8) AS ATM_ID, " +
				"TO_CHAR(TO_DATE(SUBSTR(TRAN_PARTICULAR,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NUM,1,10)) AS REF_NO," +
				" TRAN_RMKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
				" WHERE (DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(8)%') AND "+
				"TO_DATE(VALUEDATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()
				+"','DD-MM-YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
		/*}
		else 
		{			
			GET_DATA ="SELECT SUBSTR(CONTRA_ACCOUNT,1,4)||37000010085 AS DRACC,CONTRA_ACCOUNT,REPLACE(AMOUNT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE, SUBSTR(PARTICULARALS,1,8) AS ATM_ID, " +
					"TO_CHAR(TO_DATE(SUBSTR(PARTICULARALS,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NO,1,10)) AS REF_NO," +
					" REMARKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
					" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";			
		}*/
		logger.info("GET_DATA=="+GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
			//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
			generateBean.setStDebitAcc(rset.getString("DRACC"));
			generateBean.setStAmount(rset.getString("AMOUNT"));
			String stTran_Particular = "REV"+"-"+rset.getString("ATM_ID")+"-"+rset.getString("TRANDATE")+"-ONS-"+rset.getString("REF_NO");
			generateBean.setStTran_particulars(stTran_Particular);
			generateBean.setStCard_Number(rset.getString("TRAN_RMKS"));
			generateBean.setStDate(rset.getString("VALUE_DATE"));
			String remark = getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[] {},String.class);
			generateBean.setStRemark(remark);
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		//inserting data IN TTUM TABLE
		for(GenerateTTUMBean beanObj : TTUM_Data)
		{
			//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
									generatettumBeanObj.getStMerger_Category()+"_UNRECON-"+generatettumBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
			getJdbcTemplate().execute(INSERT_DATA);
			
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStMerger_Category()+"_UNRECON-"+generatettumBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
				logger.info("INSERT_DATA=="+INSERT_DATA);
		}
		
		
		
		String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
				" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
				+" WHERE (DCRS_REMARKS LIKE '%-UNRECON (%')  AND "+
				"TO_DATE(VALUEDATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
				" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
		
		logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
		
		getJdbcTemplate().execute(UPDATE_RECORDS);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUM End ****");
		
	}
	catch(Exception e)
	{
		 demo.logSQLException(e, "GenerateTTUMDaoImpl.compareData");
		 logger.error(" error in GenerateTTUMDaoImpl.compareData", new Exception("GenerateTTUMDaoImpl.compareData",e));
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
	
// added on 27/03/2018 by int6345

public List<List<GenerateTTUMBean>> generateTTUMForMastercard_C_Repo(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_C_Repo Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		ExcelHeaders.add("CREATEDDATE");
		ExcelHeaders.add("CREATEDBY");
		ExcelHeaders.add("FILEDATE");
		ExcelHeaders.add("SEG_TRAN_ID");
		ExcelHeaders.add("MSGTYPE");
		ExcelHeaders.add("PAN");
		ExcelHeaders.add("TERMID");
		ExcelHeaders.add("LOCAL_DATE");
		ExcelHeaders.add("LOCAL_TIME");
		ExcelHeaders.add("PCODE");
		ExcelHeaders.add("TRACE");
		ExcelHeaders.add("AMOUNT");
		ExcelHeaders.add("ACCEPTORNAME");
		ExcelHeaders.add("RESPCODE");
		ExcelHeaders.add("TERMLOC");
		ExcelHeaders.add("NEW_AMOUNT");
		ExcelHeaders.add("TXNSRC");
		ExcelHeaders.add("TXNDEST");
		ExcelHeaders.add("REVCODE");
		ExcelHeaders.add("AMOUNT_EQUIV");
		ExcelHeaders.add("CH_AMOUNT");
		ExcelHeaders.add("SETTLEMENT_DATE");
		ExcelHeaders.add("ISS_CURRENCY_CODE");
		ExcelHeaders.add("ACQ_CURRENCY_CODE");
		ExcelHeaders.add("MERCHANT_TYPE");
		ExcelHeaders.add("AUTHNUM");
		ExcelHeaders.add("ACCTNUM");
		ExcelHeaders.add("TRANS_ID");
		ExcelHeaders.add("ACQUIRER");
		ExcelHeaders.add("PAN2");
		ExcelHeaders.add("ISSUER");
		ExcelHeaders.add("REFNUM");
		ExcelHeaders.add("CBS_AMOUNT");
		ExcelHeaders.add("CBS_CONTRA");
		
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		
		
		//String GET_DATA ="select * from "+generatettumBeanObj.getM_surch()+" where to_char(filedate, 'dd/mm/yyyy') =TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'DD/MM/YYYY')";
		String GET_DATA="select * from C1_MASTERCARD_ISSUER_UNMD UNION ALL SELECT * "
                + "FROM C2_MASTERCARD_ISSUER_UNMD UNION ALL SELECT * "
                + "FROM C3_MASTERCARD_ISSUER_UNMD where (TRUNC (FILEDATE)) = (TRUNC (to_date('"+generatettumBeanObj.getStDate()+"' ,'mm/dd/yyyy')-30))";
		
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setCreatedt(dt);
			generateBean.setCreatedby(rset.getString("CREATEDBY"));
			generateBean.setFiledate(rset.getString("FILEDATE"));
			generateBean.setMsgtype(rset.getString("MSGTYPE"));
			generateBean.setSeg_tran_id(rset.getString("SEG_TRAN_ID"));
			generateBean.setPan(rset.getString("PAN"));
			generateBean.setTermid(rset.getString("TERMID"));
			generateBean.setLocal_date(rset.getString("LOCAL_DATE"));
			
			generateBean.setLocal_time(rset.getString("LOCAL_TIME"));
			generateBean.setPcode(rset.getString("PCODE"));
			generateBean.setTrace(rset.getString("TRACE"));
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setAcceptorname(rset.getString("ACCEPTORNAME"));
			generateBean.setRespcode(rset.getString("RESPCODE"));
			generateBean.setAmount_equiv(rset.getString("AMOUNT_EQUIV"));
			generateBean.setCh_amount(rset.getString("CH_AMOUNT"));
			generateBean.setSettlement_date(rset.getString("SETTLEMENT_DATE"));
			generateBean.setIss_currency_code(rset.getString("ISS_CURRENCY_CODE"));
			generateBean.setAcq_currency_code(rset.getString("ACQ_CURRENCY_CODE"));
			generateBean.setMerchant_type(rset.getString("MERCHANT_TYPE"));
			generateBean.setAuthnum(rset.getString("AUTHNUM"));
			generateBean.setAcctnum(rset.getString("ACCTNUM"));
			generateBean.setTrans_id(rset.getString("TRANS_ID"));
			generateBean.setAcquirer(rset.getString("ACQUIRER"));
			generateBean.setPan2(rset.getString("PAN2"));
			generateBean.setIssuer(rset.getString("ISSUER"));
			generateBean.setRefnum(rset.getString("REFNUM"));
			generateBean.setCbs_amount(rset.getString("CBS_AMOUNT"));
			generateBean.setCbs_contra(rset.getString("CBS_CONTRA"));
			
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_C_Repo End ****");
		
	}
	catch(Exception e)
	{
		 demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_C_Repo");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_C_Repo", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_C_Repo",e));
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


public List<List<GenerateTTUMBean>> generateTTUMForCARDTOCARD(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForCARDTOCARD Start ****");
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	List<String> ExcelHeaders1 = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		ExcelHeaders1.add("ACCOUNT_NUMBER");
		ExcelHeaders1.add("CURRENCY_CODE");
		ExcelHeaders1.add("SERVICE_OUTLET");
		ExcelHeaders1.add("PART_TRAN_TYPE");
		ExcelHeaders1.add("TRANSACTION_AMOUNT");
		ExcelHeaders1.add("TRANSACTION_PARTICULARS");
		ExcelHeaders1.add("REF_CURR_CODE");
		ExcelHeaders1.add("REF_TRAN_AMOUNT");
		ExcelHeaders1.add("DCRS_REMARKS");
		ExcelHeaders1.add("REF_NUM");
		
		ExcelHeaders.add("ACCOUNT NUMBER");
		ExcelHeaders.add("CURRENCY CODE");
		ExcelHeaders.add("SERVICE OUTLET");
		ExcelHeaders.add("PART TRAN TYPE");
		ExcelHeaders.add("TRANSACTION AMOUNT");
		ExcelHeaders.add("TRANSACTION PARTICULARS");
		ExcelHeaders.add("REFERENCE CURRENCY CODE");
		ExcelHeaders.add("REFERENCE AMOUNT");
		ExcelHeaders.add("REMARKS");
		ExcelHeaders.add("REFERENCE NUMBER");
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		
		 SimpleDateFormat sdfoffsite1=new SimpleDateFormat("dd/MM/yyyy");

         java.util.Date datecisb=sdfoffsite1.parse(generatettumBeanObj.getStDate());

        sdfoffsite1=new SimpleDateFormat("MM/dd/yyyy");
        String GET_DATA=null;
         logger.info(sdfoffsite1.format(datecisb));
         if(generatettumBeanObj.getStFile_Name().equalsIgnoreCase("SUCCESS"))
         {
        	 generatettumBeanObj.setInRec_Set_Id(1);
        	  GET_DATA="select * from CARD_TO_CARD_TTUM"+generatettumBeanObj.getInRec_Set_Id()+" where substr(TRANSACTION_PARTICULARS,21,18)='"+generatettumBeanObj.getStDate()+"'";
         }
         if(generatettumBeanObj.getStFile_Name().equalsIgnoreCase("FAIL"))
         {
        	 generatettumBeanObj.setInRec_Set_Id(2);
        	  GET_DATA="select * from CARD_TO_CARD_TTUM"+generatettumBeanObj.getInRec_Set_Id()+" where substr(TRANSACTION_PARTICULARS,17,18)='"+generatettumBeanObj.getStDate()+"'";
         }
		//String GET_DATA ="select * from "+generatettumBeanObj.getM_surch()+" where to_char(filedate, 'dd/mm/yyyy') =TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'DD/MM/YYYY')";
		
		
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			//String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setAccount_number(rset.getString("ACCOUNT_NUMBER"));
			generateBean.setCurrency_code(rset.getString("CURRENCY_CODE"));
			generateBean.setService_outlet(rset.getString("SERVICE_OUTLET"));
			generateBean.setPart_tran_type(rset.getString("PART_TRAN_TYPE"));
			generateBean.setTransaction_amount(rset.getString("TRANSACTION_AMOUNT"));
			generateBean.setTransaction_particulars(rset.getString("TRANSACTION_PARTICULARS"));
			generateBean.setRef_curr_code(rset.getString("REF_CURR_CODE"));
			generateBean.setRef_tran_amount(rset.getString("REF_TRAN_AMOUNT"));
			generateBean.setDcrs_remarks(rset.getString("DCRS_REMARKS"));
			generateBean.setRef_num(rset.getString("REF_NUM"));
			
			
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForCARDTOCARD End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForCARDTOCARD");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForCARDTOCARD", new Exception("GenerateTTUMDaoImpl.generateTTUMForCARDTOCARD",e));
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

public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Switch(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Switch Start ****");
	String GET_DATA=null;
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		ExcelHeaders.add("CREATEDDATE");
		ExcelHeaders.add("CREATEDBY");
		ExcelHeaders.add("FILEDATE");
		ExcelHeaders.add("SEG_TRAN_ID");
		ExcelHeaders.add("MSGTYPE");
		ExcelHeaders.add("PAN");
		ExcelHeaders.add("TERMID");
		ExcelHeaders.add("LOCAL_DATE");
		ExcelHeaders.add("LOCAL_TIME");
		ExcelHeaders.add("PCODE");
		ExcelHeaders.add("TRACE");
		ExcelHeaders.add("AMOUNT");
		ExcelHeaders.add("ACCEPTORNAME");
		ExcelHeaders.add("RESPCODE");
		ExcelHeaders.add("TERMLOC");
		ExcelHeaders.add("NEW_AMOUNT");
		ExcelHeaders.add("TXNSRC");
		ExcelHeaders.add("TXNDEST");
		ExcelHeaders.add("REVCODE");
		ExcelHeaders.add("AMOUNT_EQUIV");
		ExcelHeaders.add("CH_AMOUNT");
		ExcelHeaders.add("SETTLEMENT_DATE");
		ExcelHeaders.add("ISS_CURRENCY_CODE");
		ExcelHeaders.add("ACQ_CURRENCY_CODE");
		ExcelHeaders.add("MERCHANT_TYPE");
		ExcelHeaders.add("AUTHNUM");
		ExcelHeaders.add("ACCTNUM");
		ExcelHeaders.add("TRANS_ID");
		ExcelHeaders.add("ACQUIRER");
		ExcelHeaders.add("PAN2");
		ExcelHeaders.add("ISSUER");
		ExcelHeaders.add("REFNUM");
		
		
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		String table_name="SW"+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_TTUM";
		if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
		{
		if(generatettumBeanObj.getInRec_Set_Id() == 1)
		{
			if(generatettumBeanObj.getStSelectedFile().equals("SWITCH"))
			{
				GET_DATA="select * from "+table_name+" where (TRUNC (FILEDATE)) between (TRUNC (to_date('"+generatettumBeanObj.getStStart_Date()+"' ,'dd/mm/yyyy')-4)) and (to_date('"+generatettumBeanObj.getStStart_Date()+"' ,'dd/mm/yyyy'))";
			}
		}
		else if(generatettumBeanObj.getInRec_Set_Id() == 2)
			{
			if(generatettumBeanObj.getStSelectedFile().equals("SWITCH"))
			{
				
			}
			}
		else if(generatettumBeanObj.getInRec_Set_Id() == 3)
		{
		if(generatettumBeanObj.getStSelectedFile().equals("SWITCH"))
		{
			
		}
		}
		
		}
		else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
		{
			if(generatettumBeanObj.getInRec_Set_Id() == 1)
			{
				if(generatettumBeanObj.getStSelectedFile().equals("SWITCH"))
				{
					GET_DATA="select * from "+table_name+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
					
				}
			}
			else if(generatettumBeanObj.getInRec_Set_Id() == 2)
				{
				String c_table="C"+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
				if(generatettumBeanObj.getStSelectedFile().equals("SWITCH"))
				{
					GET_DATA="select * from "+c_table+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
				}
				if(generatettumBeanObj.getStSelectedFile().equals("DCC"))
				{
					table_name="C_DCC"+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
					GET_DATA="select * from "+table_name+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
					
				}
				else if(generatettumBeanObj.getStSelectedFile().equals("ATM"))
				{
					table_name="C"+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
					String table_name1="C1"+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
					GET_DATA="select * from (select * from "+table_name+" union all select * from "+table_name1+") where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
					
				}
				}
		}
		
		/*if(generatettumBeanObj.getM_surch2().equalsIgnoreCase("SW_MASTERCARD_ACQUIRER_TTUM"))
		{
			GET_DATA="select * from "+generatettumBeanObj.getM_surch2()+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		}
		//String GET_DATA ="select * from "+generatettumBeanObj.getM_surch()+" where to_char(filedate, 'dd/mm/yyyy') =TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'DD/MM/YYYY')";
		
		else if(generatettumBeanObj.getM_surch4().equalsIgnoreCase("C_MASTERCARD_ACQUIRER_UNMT"))
		{
			GET_DATA="select * from "+generatettumBeanObj.getM_surch4()+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		}
		else if(generatettumBeanObj.getM_surch4().equalsIgnoreCase("C_DCC_MASTERCARD_ACQUIRER_UNMT"))
		{
			GET_DATA="select * from "+generatettumBeanObj.getM_surch4()+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		}
		else{
		GET_DATA="select * from "+generatettumBeanObj.getM_surch4()+" where (TRUNC (FILEDATE)) = (TRUNC (to_date('"+generatettumBeanObj.getStDate()+"' ,'dd/mm/yyyy')-4))";
		}*/
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setCreatedt(dt);
			generateBean.setCreatedby(rset.getString("CREATEDBY"));
			generateBean.setFiledate(rset.getString("FILEDATE"));
			generateBean.setMsgtype(rset.getString("MSGTYPE"));
			generateBean.setSeg_tran_id(rset.getString("SEG_TRAN_ID"));
			generateBean.setPan(rset.getString("PAN"));
			generateBean.setTermid(rset.getString("TERMID"));
			generateBean.setLocal_date(rset.getString("LOCAL_DATE"));
			
			generateBean.setLocal_time(rset.getString("LOCAL_TIME"));
			generateBean.setPcode(rset.getString("PCODE"));
			generateBean.setTrace(rset.getString("TRACE"));
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setAcceptorname(rset.getString("ACCEPTORNAME"));
			generateBean.setRespcode(rset.getString("RESPCODE"));
			generateBean.setAmount_equiv(rset.getString("AMOUNT_EQUIV"));
			generateBean.setCh_amount(rset.getString("CH_AMOUNT"));
			generateBean.setSettlement_date(rset.getString("SETTLEMENT_DATE"));
			generateBean.setIss_currency_code(rset.getString("ISS_CURRENCY_CODE"));
			generateBean.setAcq_currency_code(rset.getString("ACQ_CURRENCY_CODE"));
			generateBean.setMerchant_type(rset.getString("MERCHANT_TYPE"));
			generateBean.setAuthnum(rset.getString("AUTHNUM"));
			generateBean.setAcctnum(rset.getString("ACCTNUM"));
			generateBean.setTrans_id(rset.getString("TRANS_ID"));
			generateBean.setAcquirer(rset.getString("ACQUIRER"));
			generateBean.setPan2(rset.getString("PAN2"));
			generateBean.setIssuer(rset.getString("ISSUER"));
			generateBean.setRefnum(rset.getString("REFNUM"));
			
			
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Switch End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_Switch");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_Switch", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_Switch",e));
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

public List<List<Mastercbs_respbean>> generateTTUMForMastercard_Iss_cbs(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	
	String respcode[]=generatettumBeanObj.getRespcode().split(",");
	for(int i=0;i<respcode.length;i++)
	{
	generatettumBeanObj.setRespcode(respcode[i]);	
	Callproc(generatettumBeanObj);
	}
		
	
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Iss_cbs Start ****");
	String GET_DATA="";
	List<List<Mastercbs_respbean>> Data = new ArrayList<>();
	List<Mastercbs_respbean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<Mastercbs_respbean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try
	{
		
		ExcelHeaders.add("ACCOUNT_NUMBER");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("SERVICE_OUTLET");
		ExcelHeaders.add("PART_TRAN_TYPE");
		ExcelHeaders.add("TRANSACTION_AMOUNT");
		ExcelHeaders.add("TRANSACTION_PARTICULARS");
		ExcelHeaders.add("REFERENCE_NUMBER");
		ExcelHeaders.add("REF_CURR_CODE");
		ExcelHeaders.add("REF_TRAN_AMOUNT");
		ExcelHeaders.add("REMARKS");
		ExcelHeaders.add("REPORT_CODE");

		
		
		
		
        generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		//Excel_header.add(/);
		
		String ttum_table="TTUM_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSelectedFile();
		logger.info(ttum_table);
		//String GET_DATA ="select * from "+generatettumBeanObj.getM_surch()+" where to_char(filedate, 'dd/mm/yyyy') =TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'DD/MM/YYYY')";
			/*GET_DATA="SELECT os1.* FROM "+settlmt_table+" os1 INNER JOIN mastercard_iss_switch os2 "
        + "ON TRIM (os1.remarks) = TRIM (os2.pan) " 
        + "AND LPAD (os2.local_time, 6, '0') = SUBSTR (os1.ref_no, -6, 6)" 
        + "AND SUBSTR (os2.TRACE, 2, 6) = SUBSTR (os1.ref_no, 2, 6) " 
        + "AND TRIM (TO_NUMBER (REPLACE (os2.amount_equiv, ','','))) =TRIM (TO_NUMBER (REPLACE (os1.amount, ',', ''))) " 
        + "AND os1.e = 'C' " 
        + "AND os2.msgtype = '130' " 
        + "AND os2.respcode = '08' " 
        + "where TRUNC (os1.FILEDATE) between (TRUNC (to_date('"+generatettumBeanObj.getStStart_Date()+"' ,'dd/mm/yyyy')-4)) and (to_date('"+generatettumBeanObj.getStStart_Date()+"' ,'dd/mm/yyyy'))" +
        		" and os1.DCRS_REMARKS like '%"+generatettumBeanObj.getStCategory()+"_"+"ISS_"+generatettumBeanObj.getStSelectedFile()+"_UNMATCHED"+"%'";*/
		
		
			GET_DATA="SELECT * FROM MASTERCARD_CBS_RES_TTUM";
			
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			Mastercbs_respbean cbsbean=new Mastercbs_respbean();
			cbsbean.setAccount_number(rset.getString("ACCOUNT_NUMBER"));
			cbsbean.setCurrency_code(rset.getString("CURRENCY_CODE"));
			cbsbean.setService_outlet(rset.getString("SERVICE_OUTLET"));
			cbsbean.setPart_tran_type(rset.getString("PART_TRAN_TYPE"));
			cbsbean.setTransaction_amount(rset.getString("TRANSACTION_AMOUNT"));
			cbsbean.setTransaction_particulars(rset.getString("TRANSACTION_PARTICULARS"));
			cbsbean.setReference_number(rset.getString("REFERENCE_NUMBER"));
			cbsbean.setRef_curr_code(rset.getString("REF_CURR_CODE"));
			cbsbean.setRef_tran_amount(rset.getString("REF_TRAN_AMOUNT"));
			cbsbean.setRemarks(rset.getString("REMARKS"));
			cbsbean.setReport_code(rset.getString("REPORT_CODE"));
			
			
			
			
			
			
			TTUM_Data.add(cbsbean);
		}
		String query="insert into MASTERCARD_CBS_RES_TTUM_move  select * from MASTERCARD_CBS_RES_TTUM";
		getJdbcTemplate().execute(query);
		String query2="truncate table MASTERCARD_CBS_RES_TTUM";
		getJdbcTemplate().execute(query2);
		  
	
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Iss_cbs End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_Iss_cbs");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_Iss_cbs", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_Iss_cbs",e));
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


public List<List<GenerateTTUMBean>> generateTTUMForMastercard(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try
	{
		
		ExcelHeaders.add("CREATEDDATE");
		ExcelHeaders.add("CREATEDBY");
		ExcelHeaders.add("FILEDATE");
		ExcelHeaders.add("SEG_TRAN_ID");
		ExcelHeaders.add("MSGTYPE");
		ExcelHeaders.add("PAN");
		ExcelHeaders.add("TERMID");
		ExcelHeaders.add("LOCAL_DATE");
		ExcelHeaders.add("LOCAL_TIME");
		ExcelHeaders.add("PCODE");
		ExcelHeaders.add("TRACE");
		ExcelHeaders.add("AMOUNT");
		ExcelHeaders.add("ACCEPTORNAME");
		ExcelHeaders.add("RESPCODE");
		ExcelHeaders.add("TERMLOC");
		ExcelHeaders.add("NEW_AMOUNT");
		ExcelHeaders.add("TXNSRC");
		ExcelHeaders.add("TXNDEST");
		ExcelHeaders.add("REVCODE");
		ExcelHeaders.add("AMOUNT_EQUIV");
		ExcelHeaders.add("CH_AMOUNT");
		ExcelHeaders.add("SETTLEMENT_DATE");
		ExcelHeaders.add("ISS_CURRENCY_CODE");
		ExcelHeaders.add("ACQ_CURRENCY_CODE");
		ExcelHeaders.add("MERCHANT_TYPE");
		ExcelHeaders.add("AUTHNUM");
		ExcelHeaders.add("ACCTNUM");
		ExcelHeaders.add("TRANS_ID");
		ExcelHeaders.add("ACQUIRER");
		ExcelHeaders.add("PAN2");
		ExcelHeaders.add("ISSUER");
		ExcelHeaders.add("REFNUM");
		ExcelHeaders.add("CBS_AMOUNT");
		ExcelHeaders.add("CBS_CONTRA");
		ExcelHeaders.add("SETTLEMENT_AMOUNT");
		ExcelHeaders.add("SETTLEMENT_CURR_C");
		ExcelHeaders.add("CURRENCY_AMOUNT");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("VARIATION");
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		
		String cate=generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory();
		String GET_DATA =" select * from E_"+cate+"_TTUM_1 union all select * from E_"+cate+"_TTUM_2 "
         + "union all select * from E_"+cate+"_TTUM_3 union all select * from E_"+cate+"_TTUM_4 union all select * from E_"+cate+"_TTUM_5 "
         + "union all select * from E_"+cate+"_TTUM_6 union all select * from E_"+cate+"_TTUM_7 WHERE TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setCreatedt(dt);
			generateBean.setCreatedby(rset.getString("CREATEDBY"));
			generateBean.setFiledate(rset.getString("FILEDATE"));
			generateBean.setFiledate(rset.getString("SEG_TRAN_ID"));
			generateBean.setMsgtype(rset.getString("MSGTYPE"));
			generateBean.setPan(rset.getString("PAN"));
			generateBean.setTermid(rset.getString("TERMID"));
			generateBean.setLocal_date(rset.getString("LOCAL_DATE"));
			
			generateBean.setLocal_time(rset.getString("LOCAL_TIME"));
			generateBean.setPcode(rset.getString("PCODE"));
			generateBean.setTrace(rset.getString("TRACE"));
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setAcceptorname(rset.getString("ACCEPTORNAME"));
			generateBean.setRespcode(rset.getString("RESPCODE"));
			generateBean.setAmount_equiv(rset.getString("AMOUNT_EQUIV"));
			generateBean.setCh_amount(rset.getString("CH_AMOUNT"));
			generateBean.setSettlement_date(rset.getString("SETTLEMENT_DATE"));
			generateBean.setIss_currency_code(rset.getString("ISS_CURRENCY_CODE"));
			generateBean.setAcq_currency_code(rset.getString("ACQ_CURRENCY_CODE"));
			generateBean.setMerchant_type(rset.getString("MERCHANT_TYPE"));
			generateBean.setAuthnum(rset.getString("AUTHNUM"));
			generateBean.setAcctnum(rset.getString("ACCTNUM"));
			generateBean.setTrans_id(rset.getString("TRANS_ID"));
			generateBean.setAcquirer(rset.getString("ACQUIRER"));
			generateBean.setPan2(rset.getString("PAN2"));
			generateBean.setIssuer(rset.getString("ISSUER"));
			generateBean.setRefnum(rset.getString("REFNUM"));
			generateBean.setCbs_amount(rset.getString("CBS_AMOUNT"));
			generateBean.setCbs_contra(rset.getString("CBS_CONTRA"));
			generateBean.setSettlement_amount(rset.getString("SETTLEMENT_AMOUNT"));
			generateBean.setSettlement_curr_c(rset.getString("SETTLEMENT_CURR_CODE"));
			generateBean.setCurrency_amount(rset.getString("CURRENCY_AMOUNT"));
			generateBean.setCurrency_code(rset.getString("CURRENCY_CODE"));
			generateBean.setVariation(rset.getString("VARIATION"));
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard",e));
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

public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Issuer(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Issuer Start ****");
	
	String GET_DATA=null;
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try
	{
		
		ExcelHeaders.add("MSGTYPE");
		ExcelHeaders.add("PAN");
		ExcelHeaders.add("PROCESSING_CODE");
		ExcelHeaders.add("AMOUNT");
		ExcelHeaders.add("AMOUNT_RECON");
		ExcelHeaders.add("CONV_RATE_RECON");
		ExcelHeaders.add("DATE_VAL");
		ExcelHeaders.add("EXPIRE_DATE");
		ExcelHeaders.add("DATA_CODE");
		ExcelHeaders.add("CARD_SEQ_NUM");
		ExcelHeaders.add("FUNCATION_CODE");
		ExcelHeaders.add("MSG_RES_CODE");
		ExcelHeaders.add("CARD_ACC_CODE");
		ExcelHeaders.add("AMOUNT_ORG");
		ExcelHeaders.add("AQUIERER_REF_NO");
		ExcelHeaders.add("FI_ID_CODE");
		ExcelHeaders.add("RETRV_REF_NO");
		ExcelHeaders.add("APPROVAL_CODE");
		ExcelHeaders.add("SERVICE_CODE");
		ExcelHeaders.add("CARD_ACC_TERM_ID");
		ExcelHeaders.add("CARD_ACC_ID_CODE");
		ExcelHeaders.add("ADDITIONAL_DATA");
		ExcelHeaders.add("CURRENCY_CODE_TRAN");
		ExcelHeaders.add("CURRENCY_CODE_RECON");
		ExcelHeaders.add("TRAN_LIFECYCLE_ID");
		ExcelHeaders.add("MSG_NUM");
		ExcelHeaders.add("DATE_ACTION");
		ExcelHeaders.add("TRAN_DEST_ID_CODE");
		ExcelHeaders.add("TRAN_ORG_ID_CODE");
		ExcelHeaders.add("CARD_ISS_REF_DATA");
		ExcelHeaders.add("RECV_INST_IDCODE");
		ExcelHeaders.add("TERMINAL_TYPE");
		ExcelHeaders.add("ELEC_COM_INDIC");
		ExcelHeaders.add("PROCESSING_MODE");
		ExcelHeaders.add("CURRENCY_EXPONENT");
		ExcelHeaders.add("BUSINESS_ACT");
		ExcelHeaders.add("SETTLEMENT_IND");
		ExcelHeaders.add("CARD_ACCP_NAME_LOC");
		ExcelHeaders.add("HEADER_TYPE");
		ExcelHeaders.add("FILE_NAME");
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		String cate_sub=generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory();
		String pos="POS_"+cate_sub+"_UNMD";
		String pos1="POS1_"+cate_sub+"_UNMD";
		/*if(split_msurch[1].equals("1"))
		{
			GET_DATA ="select * from "+pos+" UNION ALL SELECT * FROM "+pos1+" where msgtype='1240' and funcation_code='200' and "
                        + " ltrim(replace(SUBSTR (PROCESSING_CODE, 0, LENGTH(PROCESSING_CODE)- 4),''),'0') is null and "
                        + " to_char(to_date(date_val,'yymmdd'),'mmddyy')=to_char(to_date('"+generatettumBeanObj.getStDate()+"','dd/mm/yyyy'),'mmddyy')";
		}
		else if(split_msurch[1].equals("2"))
		{*/
			GET_DATA ="select * from (select * from "+pos+" UNION ALL SELECT * FROM "+pos1+") where msgtype='1240' and funcation_code='200' and "
                    + " ltrim(replace(SUBSTR (PROCESSING_CODE, 0, LENGTH(PROCESSING_CODE)- 4),''),'0')=20 or ltrim(replace(SUBSTR (PROCESSING_CODE, 0, LENGTH(PROCESSING_CODE)- 4),''),'0') is null and "
                    + " to_char(to_date(date_val,'yymmdd'),'mmddyy')=to_char(to_date('"+generatettumBeanObj.getStStart_Date()+"','dd/mm/yyyy'),'mmddyy')";
		//}
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("MSGTYPE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setMsgtype(dt);
			generateBean.setPan(rset.getString("PAN"));
			generateBean.setProcessing_code(rset.getString("PROCESSING_CODE"));
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setAmount_recon(rset.getString("AMOUNT_RECON"));
			generateBean.setConv_rate_recon(rset.getString("CONV_RATE_RECON"));
			generateBean.setDate_val(rset.getString("DATE_VAL"));
			
			generateBean.setExpire_date(rset.getString("EXPIRE_DATE"));
			generateBean.setData_code(rset.getString("DATA_CODE"));
			generateBean.setCard_seq_num(rset.getString("CARD_SEQ_NUM"));
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setFuncation_code(rset.getString("FUNCATION_CODE"));
			generateBean.setMsg_res_code(rset.getString("MSG_RES_CODE"));
			generateBean.setCard_acc_code(rset.getString("CARD_ACC_CODE"));
			generateBean.setAmount_org(rset.getString("AMOUNT_ORG"));
			generateBean.setAquierer_ref_no(rset.getString("AQUIERER_REF_NO"));
			generateBean.setFi_id_code(rset.getString("FI_ID_CODE"));
			generateBean.setRetrv_ref_no(rset.getString("RETRV_REF_NO"));
			generateBean.setApproval_code(rset.getString("APPROVAL_CODE"));
			generateBean.setService_code(rset.getString("SERVICE_CODE"));
			generateBean.setCard_acc_term_id(rset.getString("CARD_ACC_TERM_ID"));
			generateBean.setCard_acc_id_code(rset.getString("CARD_ACC_ID_CODE"));
			generateBean.setAdditional_data(rset.getString("ADDITIONAL_DATA"));
			generateBean.setCurrency_code_tran(rset.getString("CURRENCY_CODE_TRAN"));
			generateBean.setCurrency_code_recon(rset.getString("CURRENCY_CODE_RECON"));
			generateBean.setTran_lifecycle_id(rset.getString("TRAN_LIFECYCLE_ID"));
			generateBean.setMsg_num(rset.getString("MSG_NUM"));
			generateBean.setDate_action(rset.getString("DATE_ACTION"));
			generateBean.setTran_dest_id_code(rset.getString("TRAN_DEST_ID_CODE"));
			generateBean.setTran_org_id_code(rset.getString("TRAN_ORG_ID_CODE"));
			generateBean.setCard_iss_ref_data(rset.getString("CARD_ISS_REF_DATA"));
			generateBean.setRecv_inst_idcode(rset.getString("RECV_INST_IDCODE"));
			generateBean.setTerminal_type(rset.getString("TERMINAL_TYPE"));
			generateBean.setElec_com_indic(rset.getString("ELEC_COM_INDIC"));
			generateBean.setProcessing_mode(rset.getString("PROCESSING_MODE"));
			generateBean.setBusiness_act(rset.getString("BUSINESS_ACT"));
			generateBean.setSettlement_ind(rset.getString("SETTLEMENT_IND"));
			generateBean.setCard_accp_name_loc(rset.getString("CARD_ACCP_NAME_LOC"));
			generateBean.setHeader_type(rset.getString("HEADER_TYPE"));
			generateBean.setStFile_Name(rset.getString("FILE_NAME"));
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Issuer End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_Issuer");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_Issuer", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_Issuer",e));
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
public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Acq_cbs(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Acq_cbs Start ****");
	
	String GET_DATA="";
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try
	{
		
		ExcelHeaders.add("CREATEDDATE");
		ExcelHeaders.add("CREATEDBY");
		ExcelHeaders.add("FILEDATE");
		ExcelHeaders.add("SEG_TRAN_ID");
		ExcelHeaders.add("FORACID");
		ExcelHeaders.add("TRAN_DATE");
		ExcelHeaders.add("E");
		ExcelHeaders.add("AMOUNT");
		ExcelHeaders.add("BALANCE");
		ExcelHeaders.add("TRAN_ID");
		ExcelHeaders.add("VALUE_DATE");
		ExcelHeaders.add("REMARKS");
		ExcelHeaders.add("REF_NO");
		ExcelHeaders.add("PARTICULARALS");
		ExcelHeaders.add("CONTRA_ACCOUNT");
		ExcelHeaders.add("PSTD_USER_ID");
		ExcelHeaders.add("ENTRY_DATE");
		ExcelHeaders.add("VFD_DATE");
		ExcelHeaders.add("PARTICULARALS2");
		ExcelHeaders.add("MAN_CONTRA_ACCOUNT");
		
		
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		String table_name=generatettumBeanObj.getStSelectedFile()+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_TTUM";
		
			GET_DATA="select * from "+table_name+" where TO_CHAR (filedate, 'mm/dd/yyyy') = "
                    + "TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStDate()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setCreatedt(dt);
			generateBean.setCreatedby(rset.getString("CREATEDBY"));
			generateBean.setFiledate(rset.getString("FILEDATE"));
			generateBean.setSeg_tran_id(rset.getString("SEG_TRAN_ID"));
			generateBean.setForacid(rset.getString("FORACID"));
			generateBean.setTran_date(rset.getString("TRAN_DATE"));
			generateBean.setE(rset.getString("E"));
			
			generateBean.setAmount(rset.getString("AMOUNT"));
			generateBean.setBalance(rset.getString("BALANCE"));
			generateBean.setTran_id(rset.getString("TRAN_ID"));
			generateBean.setValue_date(rset.getString("VALUE_DATE"));
			generateBean.setRemarks(rset.getString("REMARKS"));
			generateBean.setRef_no(rset.getString("REF_NO"));
			generateBean.setParticularals(rset.getString("PARTICULARALS"));
			generateBean.setContra_account(rset.getString("CONTRA_ACCOUNT"));
			generateBean.setPstd_user_id(rset.getString("PSTD_USER_ID"));
			generateBean.setEntry_date(rset.getString("ENTRY_DATE"));
			generateBean.setVfd_date(rset.getString("VFD_DATE"));
			generateBean.setParticularals2(rset.getString("PARTICULARALS2"));
			generateBean.setMan_contra_account(rset.getString("MAN_CONTRA_ACCOUNT"));
			
			
			
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_Acq_cbs End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_Acq_cbs");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_Acq_cbs", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_Acq_cbs",e));
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

public List<List<GenerateTTUMBean>> generateTTUMForMastercard_ATM_DCC(GenerateTTUMBean generatettumBeanObj) throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_ATM_DCC Start ****");
	String GET_DATA=null;
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try
	{
		
		ExcelHeaders.add("CREATEDDATE");
		ExcelHeaders.add("CREATEDBY");
		ExcelHeaders.add("FILEDATE");
		ExcelHeaders.add("SEG_TRAN_ID");
		ExcelHeaders.add("MSGTYPE");
		ExcelHeaders.add("SWICTH_SERIAL_NUM");
		ExcelHeaders.add("PROCESSOR_A_I");
		ExcelHeaders.add("PROCESSOR_ID");
		ExcelHeaders.add("TRAN_DATE");
		ExcelHeaders.add("TRAN_TIME");
		ExcelHeaders.add("PAN_LENGTH");
		ExcelHeaders.add("PAN_NUM");
		ExcelHeaders.add("PROCCESSING_CODE");
		ExcelHeaders.add("TRACE_NUM");
		ExcelHeaders.add("MERCAHNT_TYPE");
		ExcelHeaders.add("POS_ENTRY");
		ExcelHeaders.add("REF_NO");
		ExcelHeaders.add("AQUIRER_I_ID");
		ExcelHeaders.add("TERMINAL_ID");
		ExcelHeaders.add("RESPCODE");
		ExcelHeaders.add("BRAND");
		ExcelHeaders.add("ADVAICE_REG_CODE");
		ExcelHeaders.add("INTRA_CURR_AGGRMT_CODE");
		ExcelHeaders.add("AUTH_ID");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("IMPLIED_DEC_TRAN");
		ExcelHeaders.add("COMPLTD_AMNT_TRAN");
		ExcelHeaders.add("COMPLTD_AMNT_TRAN_D_C");
		ExcelHeaders.add("CASH_BACK_AMNT_L");
		ExcelHeaders.add("CASH_BACK_AMNT_D_C_C");
		ExcelHeaders.add("ACCESS_FEE_L");
		ExcelHeaders.add("ACCESS_FEE_L_D_C");
		ExcelHeaders.add("CURRENCY_SETTLMENT");
		ExcelHeaders.add("IMPLIED_DEC_SETTLMENT");
		ExcelHeaders.add("CONVERSION_RATE");
		ExcelHeaders.add("COMPLTD_AMT_SETTMNT");
		ExcelHeaders.add("COMPLTD_AMNT_D_C");
		ExcelHeaders.add("INTER_CHANGE_FEE");
		ExcelHeaders.add("INTER_CHANGE_FEE_D_C");
		ExcelHeaders.add("SERVICE_LEV_IND");
		ExcelHeaders.add("RESP_CODE1");
		ExcelHeaders.add("FILER");
		ExcelHeaders.add("POSITIVE_ID_IND");
		ExcelHeaders.add("ATM_SURCHARGE_FREE");
		ExcelHeaders.add("CROSS_BORD_IND");
		ExcelHeaders.add("CROSS_BORD_CURRENCY_IN");
		ExcelHeaders.add("VISA_IAS");
		ExcelHeaders.add("REQ_AMNT_TRAN");
		ExcelHeaders.add("FILER1");
		ExcelHeaders.add("TRACE_NUM_ADJ");
		ExcelHeaders.add("FILER2");
		ExcelHeaders.add("TYPE");
		ExcelHeaders.add("FILEDATE_1");
		ExcelHeaders.add("PART_ID");
		
		
		
		
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		
		Excel_header.add(generatettumBeanObj);
		String table_name=generatettumBeanObj.getStSelectedFile()+"_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
		String table_name1=generatettumBeanObj.getStSelectedFile()+"1_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory()+"_UNMT";
		
		if(generatettumBeanObj.getStSelectedFile().equals("ATM"))
		{
		
			GET_DATA="select * from (select * from "+table_name+" union all select * from "+table_name1+")where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		
		}
		else if(generatettumBeanObj.getStSelectedFile().equals("DCC")){
			GET_DATA="select * from "+table_name+" where TO_CHAR (filedate, 'mm/dd/yyyy') = TO_CHAR (TO_DATE ('"+generatettumBeanObj.getStStart_Date()+"', 'DD/MM/YYYY'), 'MM/DD/YYYY')";
		}
		logger.info(GET_DATA);
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			String dt=rset.getString("CREATEDDATE");
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setCreatedt(dt);
			generateBean.setCreatedby(rset.getString("CREATEDBY"));
			generateBean.setFiledate(rset.getString("FILEDATE"));
			generateBean.setMsgtype(rset.getString("MSGTYPE"));
			generateBean.setSeg_tran_id(rset.getString("SEG_TRAN_ID"));
			generateBean.setSwicth_serial_num(rset.getString("SWICTH_SERIAL_NUM"));
			generateBean.setProcessor_id(rset.getString("PROCESSOR_ID"));
			generateBean.setTran_date(rset.getString("TRAN_DATE"));
			
			generateBean.setTran_time(rset.getString("TRAN_TIME"));
			generateBean.setPan_length(rset.getString("PAN_LENGTH"));
			generateBean.setPan_num(rset.getString("PAN_NUM"));
			generateBean.setProcessing_code(rset.getString("PROCCESSING_CODE"));
			generateBean.setTrace_num(rset.getString("TRACE_NUM"));
			generateBean.setMercahnt_type(rset.getString("MERCAHNT_TYPE"));
			generateBean.setPos_entry(rset.getString("POS_ENTRY"));
			generateBean.setRef_no(rset.getString("REF_NO"));
			generateBean.setAquirer_i_id(rset.getString("AQUIRER_I_ID"));
			generateBean.setTerminal_id(rset.getString("TERMINAL_ID"));
			generateBean.setRespcode(rset.getString("RESPCODE"));
			generateBean.setBrand(rset.getString("BRAND"));
			generateBean.setAdvaice_reg_code(rset.getString("ADVAICE_REG_CODE"));
			generateBean.setIntra_curr_aggrmt_code(rset.getString("INTRA_CURR_AGGRMT_CODE"));
			generateBean.setAuth_id(rset.getString("AUTH_ID"));
			generateBean.setCurrency_code(rset.getString("CURRENCY_CODE"));
			generateBean.setImplied_dec_tran(rset.getString("IMPLIED_DEC_TRAN"));
			generateBean.setCompltd_amnt_tran(rset.getString("COMPLTD_AMNT_TRAN"));
			generateBean.setCompltd_amnt_tran_d_c(rset.getString("COMPLTD_AMNT_TRAN_D_C"));
			
			generateBean.setCash_back_amnt_l(rset.getString("CASH_BACK_AMNT_L"));
			generateBean.setCash_back_amnt_d_c_c(rset.getString("CASH_BACK_AMNT_D_C_C"));
			generateBean.setAccess_fee_l(rset.getString("ACCESS_FEE_L"));
			generateBean.setAccess_fee_l_d_c(rset.getString("ACCESS_FEE_L_D_C"));
			generateBean.setCurrency_settlment(rset.getString("CURRENCY_SETTLMENT"));
			generateBean.setImplied_dec_settlment(rset.getString("IMPLIED_DEC_SETTLMENT"));
			generateBean.setConversion_rate(rset.getString("CONVERSION_RATE"));
			generateBean.setCompltd_amt_settmnt(rset.getString("COMPLTD_AMT_SETTMNT"));
			generateBean.setCompltd_amnt_d_c(rset.getString("COMPLTD_AMNT_D_C"));
			generateBean.setInter_change_fee(rset.getString("INTER_CHANGE_FEE"));
			generateBean.setInter_change_fee_d_c(rset.getString("INTER_CHANGE_FEE_D_C"));
			generateBean.setService_lev_ind(rset.getString("SERVICE_LEV_IND"));
			generateBean.setResp_code1(rset.getString("RESP_CODE1"));
			generateBean.setFiler(rset.getString("FILER"));
			generateBean.setPositive_id_ind(rset.getString("POSITIVE_ID_IND"));
			generateBean.setAtm_surcharge_free(rset.getString("ATM_SURCHARGE_FREE"));
			generateBean.setCross_bord_ind(rset.getString("CROSS_BORD_IND"));
			generateBean.setCross_bord_currency_in(rset.getString("CROSS_BORD_CURRENCY_IND"));
			generateBean.setVisa_ias(rset.getString("VISA_IAS"));
			generateBean.setReq_amnt_tran(rset.getString("REQ_AMNT_TRAN"));
			generateBean.setFiler1(rset.getString("FILER1"));
			generateBean.setTrace_num_adj(rset.getString("TRACE_NUM_ADJ"));
			generateBean.setFiler2(rset.getString("FILER2"));
			generateBean.setType(rset.getString("TYPE"));
			generateBean.setFiledate_1(rset.getString("FILEDATE_1"));
			generateBean.setPart_id(rset.getString("PART_ID"));
			
			
			
			
			
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForMastercard_ATM_DCC End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForMastercard_ATM_DCC");
		 logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForMastercard_ATM_DCC", new Exception("GenerateTTUMDaoImpl.generateTTUMForMastercard_ATM_DCC",e));
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

// END int6345

/*@Override	
public List<GenerateTTUMBean> generateTTUM(GenerateTTUMBean generatettumBeanObj)throws Exception
{

	int inloop_count = 0;
	List<String> Final_Headers = new ArrayList<>();
	List<GenerateTTUMBean> bean_list = new ArrayList<>();
	GenerateTTUMBean generateBeanObj = new GenerateTTUMBean();
	List<String> stAction = new ArrayList<>();
	stAction.add("D");
	stAction.add("C");
	int intemp;
	try
	{
		while(inloop_count != table_list.size())
		{
			String stSelect_param = "", stInsert_Cols = "CREATEDDATE, CREATEDBY";
			String table_cols = "CREATEDDATE DATE, CREATEDBY VARCHAR (100 BYTE)";
			//String stTable_Name = table_list.get(inloop_count);
			//String stCategory = stTable_Name.split("-")[0];
			//String stFile_Name = stTable_Name.split("-")[1];
			//String stAction = "C";
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			//generateBean.setStCategory(stCategory);
			generateBean.setStCategory(generatettumBeanObj.getStMerger_Category());
			generateBean.setStStart_Date(generatettumBeanObj.getStStart_Date());
			generateBean.setStEnd_Date(generatettumBeanObj.getStEnd_Date());
			String sttemp_header = "";
			Connection conn;
			
			
			
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {generatettumBeanObj.getStFile_Name(),generatettumBeanObj.getStMerger_Category(),generatettumBeanObj.getStSubCategory() },Integer.class);
			logger.info("file id is "+file_id);
	//1. GET EXCEL HEADERS
			List<String> ExcelHeaders = getJdbcTemplate().query(GET_TTUM_HEADERS,new Object[] {file_id, generatettumBeanObj.getStMerger_Category() }, new ExcelHeaderMapper());
			
			for(int i = 0; i < ExcelHeaders.size(); i++)
			{
			//	logger.info("HEADER "+i+ExcelHeaders.get(i));
				Final_Headers.add(ExcelHeaders.get(i));
				stInsert_Cols = stInsert_Cols + " , "+ExcelHeaders.get(i).replace(" ", "_");
					table_cols = table_cols +" , "+ ExcelHeaders.get(i).replace(" ", "_") +" VARCHAR(100 BYTE) ";
				
			}
			generateBeanObj.setStExcelHeader(Final_Headers);
			
	
	//2. GET COLUMNS AND HEADERS FROM MAIN_TTUM_COLUMNS TABLE
			intemp = 0; 
			while(intemp < stAction.size())
			{
				logger.info("WHILE STARTS HERE-------------------------------------------------------------------------------");
				stSelect_param = "";
				List<GenerateTTUMBean> Excel_Columns = getJdbcTemplate().query(GET_TTUM_COLUMNS,new Object[] {file_id, stAction.get(intemp) }, new TTUMValuesMapper());
		
			for(int i = 0 ; i < Excel_Columns.size() ; i++)
			{
				GenerateTTUMBean generateTTUMBeanObj = Excel_Columns.get(i);
				sttemp_header = generateTTUMBeanObj.getStExcel_Header();
				
				if(generateTTUMBeanObj.getStFile_header()!=null)
				{
					if(generateTTUMBeanObj.getStPadding().equals("Y"))
					{
						if(generateTTUMBeanObj.getStRemove_char()!=null)
						{
							if(generateTTUMBeanObj.getStHeader_Value()!= null)
							{
								stSelect_param = stSelect_param + "REPLACE( SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
											+","+generateTTUMBeanObj.getStChar_Size()+" ), '"+generateTTUMBeanObj.getStRemove_char()+"', '')"
											+ " || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
							}
							else
							{
								stSelect_param = stSelect_param + "REPLACE( SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
										+","+generateTTUMBeanObj.getStChar_Size()+" ), '"+generateTTUMBeanObj.getStRemove_char()+"', '')";
							}
						}
						else
						{
							if(generateTTUMBeanObj.getStHeader_Value() != null)
							{
								stSelect_param = stSelect_param + "SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
								+","+generateTTUMBeanObj.getStChar_Size()+" ) || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
							}
							else
							{
								stSelect_param = stSelect_param + "SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
										+","+generateTTUMBeanObj.getStChar_Size()+" ) ";
							}
							
						}
					}
					else
					{
						if(generateTTUMBeanObj.getStRemove_char()!= null)
						{
							if(generateTTUMBeanObj.getStHeader_Value() != null)
							{
								stSelect_param = stSelect_param +" REPLACE( "+ generateTTUMBeanObj.getStFile_header()+" , '"+generateTTUMBeanObj.getStRemove_char()+
											"' , '' ) || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
							}
							else

								stSelect_param = stSelect_param +" REPLACE( "+ generateTTUMBeanObj.getStFile_header()+" , '"+generateTTUMBeanObj.getStRemove_char()+
											"' , '' )";
							
						}
						else
						{
							if(generateTTUMBeanObj.getStHeader_Value() != null)
							{
								stSelect_param = stSelect_param +generateTTUMBeanObj.getStFile_header()+" || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
							}
							else
								stSelect_param = stSelect_param +generateTTUMBeanObj.getStFile_header();
						}
					}
					
				}
				else
				{
					if(generateTTUMBeanObj.getStHeader_Value().contains("/"))
					{
						stSelect_param = stSelect_param +"'"+ generateTTUMBeanObj.getStHeader_Value()+"' ";
					}
					else
					//stSelect_param = stSelect_param + " '"+ generateTTUMBeanObj.getStHeader_Value()+"' AS "+ generateTTUMBeanObj.getStExcel_Header().replace(" ", "_");
						stSelect_param = stSelect_param + " '"+ generateTTUMBeanObj.getStHeader_Value()+"'";
				}
				
				
				for(int j = (i+1) ; j < Excel_Columns.size() ; j++)
				{
					if(Excel_Columns.get(j).getStExcel_Header().equals(sttemp_header))
					{
						
						if(Excel_Columns.get(j).getStFile_header()!=null
							&& (generateTTUMBeanObj.getStFile_header() == null || 
							generateTTUMBeanObj.getStFile_header().equals(Excel_Columns.get(j).getStFile_header())) )
						{
							if(Excel_Columns.get(j).getStPadding().equals("Y"))
							{
								if(Excel_Columns.get(j).getStRemove_char()!=null)
								{
									if(Excel_Columns.get(j).getStHeader_Value() != null)
									{	
										stSelect_param = stSelect_param + "|| REPLACE(SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","
											+Excel_Columns.get(j).getStStart_charpos()
											+","+Excel_Columns.get(j).getStChar_Size()+" ) , '"+Excel_Columns.get(j).getStRemove_char()+"' , '') || '"+
											Excel_Columns.get(j).getStHeader_Value()+"'";
									}
									else
										stSelect_param = stSelect_param + "|| REPLACE(SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","
												+Excel_Columns.get(j).getStStart_charpos()
										+","+Excel_Columns.get(j).getStChar_Size()+" ) , '"+Excel_Columns.get(j).getStRemove_char()+"' , '')";
								}
								else
								{
									if(Excel_Columns.get(j).getStHeader_Value() != null)
									{
										stSelect_param = stSelect_param + "|| SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","+Excel_Columns.get(j).getStStart_charpos()
														+","+Excel_Columns.get(j).getStChar_Size()+" ) || '"+Excel_Columns.get(j).getStHeader_Value()+"'";
									}
									else
									{
										stSelect_param = stSelect_param + "|| SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","+Excel_Columns.get(j).getStStart_charpos()
												+","+Excel_Columns.get(j).getStChar_Size()+" ) ";
									}
								}
								
							}
							else
							{
								if(Excel_Columns.get(j).getStRemove_char()!=null)
								{
									if(Excel_Columns.get(j).getStHeader_Value() !=null)
									{
										stSelect_param = stSelect_param +" || REPLACE( "+ Excel_Columns.get(j).getStFile_header()+" , '"+Excel_Columns.get(j).getStRemove_char()
													+"' , '') || '"+Excel_Columns.get(j).getStHeader_Value()+"'";
									}
									else
									{
										stSelect_param = stSelect_param +" || REPLACE( "+ Excel_Columns.get(j).getStFile_header()+" , '"+Excel_Columns.get(j).getStRemove_char()
												+"' , '')";
									}
								}
								else
									stSelect_param = stSelect_param +" || "+ Excel_Columns.get(j).getStFile_header();

							}
							
						}
						else if(Excel_Columns.get(j).getStFile_header()==null)
						{
							if(Excel_Columns.get(j).getStHeader_Value().contains("/"))
							{
								stSelect_param = stSelect_param + " || '"+ Excel_Columns.get(j).getStHeader_Value()+"' ";
							}
							else
								stSelect_param = stSelect_param + " || '"+ Excel_Columns.get(j).getStHeader_Value()+"'";
							
							
						}
						i = j;
					}
					
				}
				if(i != (Excel_Columns.size()-1))
				{
					//stSelect_param = stSelect_param + " AS "+count+",";
					stSelect_param = stSelect_param + " AS "+Excel_Columns.get(i).getStExcel_Header().replace(" ", "_")+",";
				}
				else
				{
					stSelect_param = stSelect_param + " AS "+Excel_Columns.get(i).getStExcel_Header().replace(" ", "_");
				}
				
			}
			
			if(!stSelect_param.equals("")){
				
			//String remark = getJdbcTemplate().queryForObject("select 'CRM'||ttum_seq.nextval from dual", new Object[] {},String.class);
			
			stSelect_param = stSelect_param + ", '"+remark+"' AS REMARKS ";
			String GET_TTUM_DATA = "";
			String GET_MAN_TTUM = "";
			if(stAction.get(intemp).equals("C"))
			{	
				GET_TTUM_DATA = "SELECT "+stSelect_param+" FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()
						+ " WHERE (DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(8)%')" +
						" AND CONTRA_ACCOUNT NOT LIKE '%78000010021%' AND " +
						"TO_CHAR(TO_DATE(VALUEDATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
				GET_MAN_TTUM = "SELECT "+stSelect_param.replaceAll("CONTRA_ACCOUNT","MAN_CONTRA_ACCOUNT")+" FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()
						+"_"+generatettumBeanObj.getStFile_Name()
						+ " WHERE (DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(08)%' ) AND CONTRA_ACCOUNT LIKE '%78000010021%' AND "+
						"TO_CHAR(TO_DATE(VALUEDATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
								
			}
			else
			{
				GET_TTUM_DATA = "SELECT "+stSelect_param+" FROM SETTLEMENT_"+stFile_Name + " WHERE REMARKS LIKE '%"+stCategory+"-GENERATE-TTUM%'" +
						" AND CONTRA_ACCOUNT NOT LIKE '%78000010021%'" ;
				GET_MAN_TTUM = "SELECT "+stSelect_param.replaceAll("CONTRA_ACCOUNT","MAN_CONTRA_ACCOUNT")+" FROM SETTLEMENT_"+stFile_Name 
						+ " WHERE REMARKS LIKE '%"+stCategory+"-GENERATE-TTUM%' AND CONTRA_ACCOUNT LIKE '%78000010021%'" ;
				
			}
			
			if(stAction.get(intemp).equals("D"))
			{	
				String GET_FROM_DATE = "SELECT MIN(TO_CHAR(FILEDATE,'DD')) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+
								generatettumBeanObj.getStFile_Name()+" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' ";
				logger.info("FROM DATE QUERY IS "+GET_FROM_DATE);
				
				String stFrom_Date = getJdbcTemplate().queryForObject(GET_FROM_DATE, new Object[] {},String.class);
				if(stFrom_Date != null)
				generateBean.setStStart_Date(stFrom_Date);
				
				
				
				logger.info("from date is "+stFrom_Date);
				String GET_TO_DATE = "SELECT MAX(TO_CHAR(FILEDATE,'DDMM')) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"
							+generatettumBeanObj.getStFile_Name()+" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' ";
				logger.info("GET_TO_DATE IS "+GET_TO_DATE);
				String stTo_Date = generateBean.getStEnd_Date();
				stTo_Date=getJdbcTemplate().queryForObject(GET_TO_DATE, new Object[] {}, String.class);
				logger.info("TO DATE IS "+stTo_Date);
				
				if(stTo_Date!=null)
						generateBean.setStEnd_Date(stTo_Date);
			}
			
			logger.info("GET_TTUM_DATA "+GET_TTUM_DATA);
			logger.info("GET MAN TTUM "+GET_MAN_TTUM);
			conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(GET_TTUM_DATA);
			ResultSet rset = pstmt.executeQuery();
			
			
			//ADD THE DATA IN THE LIST
			//List<String> stTTum_data = new ArrayList<>();
			List<List<String>> stFinal_Data = new ArrayList<>();
			List<String> stRemarks_List = new ArrayList<>();
			if(stAction.get(intemp).equals("C")){
				stRemarks_List = generateBeanObj.getStRemarks();
			}
			int remark_count = 0;
			while(rset.next())
			{
				List<String> stTTum_data = new ArrayList<>(); 
				
				for(int i = 1 ; i< Final_Headers.size() ; i++)
				{
					stTTum_data.add(rset.getString(i));

					//logger.info("value is "+rset.getString(i));
										
					if(stAction.get(intemp).equals("D") && i==(Final_Headers.size()-1))
					{
						String remark = getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[] {},String.class);
						
						stTTum_data.add(remark);
						stRemarks_List.add(remark);
					}
					else if(stAction.get(intemp).equals("C") && i==(Final_Headers.size()-1))
					{
						stTTum_data.add(stRemarks_List.get(remark_count));
						logger.info("check the remark field"+stRemarks_List.get(remark_count));
						remark_count++;
					}
					
					
				}
				stFinal_Data.add(stTTum_data);
				
			}
			
			//entries with proxy accounts in contra account column
			
				Connection con = getConnection();
				PreparedStatement man_pstmt = con.prepareStatement(GET_MAN_TTUM);
				ResultSet man_rset = man_pstmt.executeQuery();
				while(man_rset.next())
				{
					List<String> stTTum_data = new ArrayList<>(); 

					for(int i = 1 ; i< Final_Headers.size() ; i++)
					{
						if(Final_Headers.get(i-1).equals("ACCOUNT NUMBER"))
						{
							String acc_no = man_rset.getString(i).replaceAll(" ", "").replaceAll("^0*","");
							stTTum_data.add(acc_no);
						}
						else
							stTTum_data.add(man_rset.getString(i));
						//logger.info("ttum data "+stTTum_data.get(0));
						//logger.info("value is "+rset.getString(i));

						if(stAction.get(intemp).equals("D") && i==(Final_Headers.size()-1))
						{
							String remark = getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[] {},String.class);

							stTTum_data.add(remark);
							stRemarks_List.add(remark);
						}
						else if(stAction.get(intemp).equals("C") && i==(Final_Headers.size()-1))
						{
							stTTum_data.add(stRemarks_List.get(remark_count));
							remark_count++;
						}


					}
					stFinal_Data.add(stTTum_data);

				}
			
			
			if(stAction.get(intemp).equals("C"))
			{
				generateBeanObj.setStTTUM_Records(stFinal_Data);
				generateBeanObj.setStRemarks(stRemarks_List);
			}
			else
			{
				generateBeanObj.setStTTUM_DRecords(stFinal_Data);
				generateBeanObj.setStRemarks(stRemarks_List);
			}
			
			//CREATE TABLE FOR TTUM ENTRIES
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			logger.info("exists ?"+tableExist);
			logger.info("tablecols "+table_cols);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+"("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				PreparedStatement create_pstmt = conn.prepareStatement(query);
				create_pstmt.execute();
				
				pstmt = null;
				
			}
			//GENERATE REMARKS
			//int remark = getJdbcTemplate().queryForObject("select 'CRM'||ttum_seq.nextval from dual",Integer.class);
			PreparedStatement inserted_pstmt;
			for(int j = 0; j<stFinal_Data.size(); j++)
			{
				List<String> StInsert_Data = stFinal_Data.get(j);
				String TTUM_INSERT = "INSERT INTO TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+" ("+stInsert_Cols+") VALUES(SYSDATE,'INT5779'";

				for(int i =0 ;i< StInsert_Data.size() ;i++)
				{
					TTUM_INSERT = TTUM_INSERT + ",'"+StInsert_Data.get(i)+"'";
				}
				TTUM_INSERT = TTUM_INSERT + ")";
				logger.info("Insert query is "+TTUM_INSERT);
				inserted_pstmt = conn.prepareStatement(TTUM_INSERT);
				inserted_pstmt.executeQuery();
				
				
			}
			
			
			String TTUM_DATA = "SELECT CREATEDDATE, CREATEDBY, "+stSelect_param+" FROM SETTLEMENT_"+stFile_Name + " WHERE REMARKS = '"+stCategory+"-GENERATE-TTUM'" ;
			logger.info("TTUM_DATA "+TTUM_DATA);
			
			String TTUM_INSERT = "INSERT INTO TTUM_"+stFile_Name+" ("+stInsert_Cols+") "+TTUM_DATA ;
			
			logger.info("TTUM_INSERT "+TTUM_INSERT);
			
			PreparedStatement inserted_pstmt = conn.prepareStatement(TTUM_INSERT);
		//	ResultSet insert_rset = inserted_pstmt.executeQuery();
			
						
			
		
			}
			
			intemp++;
			}
			bean_list.add(generateBeanObj);
			bean_list.add(generateBean);
			inloop_count++;	
		}
			
			String UPDATE_TTUM = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()
					+" SET 	DCRS_REMARKS = REPLACE(DCRS_REMARKS , 'UNRECON' , 'GENERATED-TTUM' ) WHERE DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(8)%'"+
					" AND TO_CHAR(TO_DATE(VALUEDATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
					" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";;
			getJdbcTemplate().update(UPDATE_TTUM);
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		
	}
	
	return bean_list;
	
}*/

/*private void getTTUM_Data(String stTable_Name, int file_id,String stAction)
{

	String stSelect_param = "";
	String sttemp_header = "";
	Connection conn;
	String stFile_Name = stTable_Name.split("_")[1];
	String stCategory = stTable_Name.split("_")[0];
	List<GenerateTTUMBean> Excel_Columns = getJdbcTemplate().query(GET_TTUM_COLUMNS,new Object[] {file_id, stAction}, new TTUMValuesMapper());


	logger.info("GOT THE LIST");

	for(int i = 0 ; i < Excel_Columns.size() ; i++)
	{
		GenerateTTUMBean generateTTUMBeanObj = Excel_Columns.get(i);
		sttemp_header = generateTTUMBeanObj.getStExcel_Header();

		if(generateTTUMBeanObj.getStFile_header()!=null)
		{
			if(generateTTUMBeanObj.getStPadding().equals("Y"))
			{
				if(generateTTUMBeanObj.getStRemove_char()!=null)
				{
					if(generateTTUMBeanObj.getStHeader_Value()!= null)
					{
						stSelect_param = stSelect_param + "REPLACE( SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
								+","+generateTTUMBeanObj.getStChar_Size()+" ), '"+generateTTUMBeanObj.getStRemove_char()+"', '')"
								+ " || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
					}
					else
					{
						stSelect_param = stSelect_param + "REPLACE( SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
								+","+generateTTUMBeanObj.getStChar_Size()+" ), '"+generateTTUMBeanObj.getStRemove_char()+"', '')";
					}
				}
				else
				{
					if(generateTTUMBeanObj.getStHeader_Value() != null)
					{
						stSelect_param = stSelect_param + "SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
								+","+generateTTUMBeanObj.getStChar_Size()+" ) || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
					}
					else
					{
						stSelect_param = stSelect_param + "SUBSTR( "+generateTTUMBeanObj.getStFile_header()+","+generateTTUMBeanObj.getStStart_charpos()
								+","+generateTTUMBeanObj.getStChar_Size()+" ) ";
					}

				}
			}
			else
			{
				if(generateTTUMBeanObj.getStRemove_char()!= null)
				{
					if(generateTTUMBeanObj.getStHeader_Value() != null)
					{
						stSelect_param = stSelect_param +" REPLACE( "+ generateTTUMBeanObj.getStFile_header()+" , '"+generateTTUMBeanObj.getStRemove_char()+
								"' , '' ) || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
					}
					else

						stSelect_param = stSelect_param +" REPLACE( "+ generateTTUMBeanObj.getStFile_header()+" , '"+generateTTUMBeanObj.getStRemove_char()+
						"' , '' )";

				}
				else
				{
					if(generateTTUMBeanObj.getStHeader_Value() != null)
					{
						stSelect_param = stSelect_param +generateTTUMBeanObj.getStFile_header()+" || '"+generateTTUMBeanObj.getStHeader_Value()+"'";
					}
					else
						stSelect_param = stSelect_param +generateTTUMBeanObj.getStFile_header();
				}
			}

		}
		else
		{
			if(generateTTUMBeanObj.getStHeader_Value().contains("/"))
			{
				stSelect_param = stSelect_param +"'"+ generateTTUMBeanObj.getStHeader_Value()+"' ";
			}
			else
				//stSelect_param = stSelect_param + " '"+ generateTTUMBeanObj.getStHeader_Value()+"' AS "+ generateTTUMBeanObj.getStExcel_Header().replace(" ", "_");
				stSelect_param = stSelect_param + " '"+ generateTTUMBeanObj.getStHeader_Value()+"'";
		}


		for(int j = (i+1) ; j < Excel_Columns.size() ; j++)
		{
			if(Excel_Columns.get(j).getStExcel_Header().equals(sttemp_header))
			{

				if(Excel_Columns.get(j).getStFile_header()!=null)
				{
					if(Excel_Columns.get(j).getStPadding().equals("Y"))
					{
						if(Excel_Columns.get(j).getStRemove_char()!=null)
						{
							if(Excel_Columns.get(j).getStHeader_Value() != null)
							{	
								stSelect_param = stSelect_param + "|| REPLACE(SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","
										+Excel_Columns.get(j).getStStart_charpos()
										+","+Excel_Columns.get(j).getStChar_Size()+" ) , '"+Excel_Columns.get(j).getStRemove_char()+"' , '') || '"+
										Excel_Columns.get(j).getStHeader_Value()+"'";
							}
							else
								stSelect_param = stSelect_param + "|| REPLACE(SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","
										+Excel_Columns.get(j).getStStart_charpos()
										+","+Excel_Columns.get(j).getStChar_Size()+" ) , '"+Excel_Columns.get(j).getStRemove_char()+"' , '')";
						}
						else
						{
							if(Excel_Columns.get(j).getStHeader_Value() != null)
							{
								stSelect_param = stSelect_param + "|| SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","+Excel_Columns.get(j).getStStart_charpos()
										+","+Excel_Columns.get(j).getStChar_Size()+" ) || '"+Excel_Columns.get(j).getStHeader_Value()+"'";
							}
							else
							{
								stSelect_param = stSelect_param + "|| SUBSTR( "+Excel_Columns.get(j).getStFile_header()+","+Excel_Columns.get(j).getStStart_charpos()
										+","+Excel_Columns.get(j).getStChar_Size()+" ) ";
							}
						}

					}
					else
					{
						if(Excel_Columns.get(j).getStRemove_char()!=null)
						{
							if(Excel_Columns.get(j).getStHeader_Value() !=null)
							{
								stSelect_param = stSelect_param +" || REPLACE( "+ Excel_Columns.get(j).getStFile_header()+" , '"+Excel_Columns.get(j).getStRemove_char()
										+"' , '') || '"+Excel_Columns.get(j).getStHeader_Value()+"'";
							}
							else
							{
								stSelect_param = stSelect_param +" || REPLACE( "+ Excel_Columns.get(j).getStFile_header()+" , '"+Excel_Columns.get(j).getStRemove_char()
										+"' , '')";
							}
						}
						else
							stSelect_param = stSelect_param +" || "+ Excel_Columns.get(j).getStFile_header();

					}
				}
				else
				{
					if(Excel_Columns.get(j).getStHeader_Value().contains("/"))
					{
						stSelect_param = stSelect_param + " || '"+ Excel_Columns.get(j).getStHeader_Value()+"' ";
					}
					else
						stSelect_param = stSelect_param + " || '"+ Excel_Columns.get(j).getStHeader_Value()+"'";
				}
				i = j;
			}

		}
		if(i != (Excel_Columns.size()-1))
		{
			//stSelect_param = stSelect_param + " AS "+count+",";
			stSelect_param = stSelect_param + " AS "+Excel_Columns.get(i).getStExcel_Header().replace(" ", "_")+",";
		}
		else
		{
			stSelect_param = stSelect_param + " AS "+Excel_Columns.get(i).getStExcel_Header().replace(" ", "_");
		}

	}
	logger.info("select_cond "+stSelect_param);
	if(!stSelect_param.equals("")){
		String GET_TTUM_DATA = "SELECT "+stSelect_param+" FROM SETTLEMENT_"+stFile_Name + " WHERE REMARKS = '"+stCategory+"-GENERATE-TTUM'" ;
		logger.info("GET_TTUM_DATA QUERY IS "+GET_TTUM_DATA);
		conn = dbconn.getCon();
		PreparedStatement pstmt = conn.prepareStatement(GET_TTUM_DATA);
		ResultSet rset = pstmt.executeQuery();

		//ADD THE DATA IN THE LIST
		//List<String> stTTum_data = new ArrayList<>();
		List<List<String>> stFinal_Data = new ArrayList<>();
		while(rset.next())
		{
			List<String> stTTum_data = new ArrayList<>(); 
			for(int i = 1 ; i< Final_Headers.size() ; i++)
			{
				stTTum_data.add(rset.getString(i));
				//logger.info("value is "+rset.getString(i));
			}
			stFinal_Data.add(stTTum_data);

		}
		generateBeanObj.setStTTUM_Records(stFinal_Data);


	}
	bean_list.add(generateBeanObj);


}
*/
private static class ExcelHeaderMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		return rs.getString("FILE_HEADER");

	}
}

private static class TTUMValuesMapper implements RowMapper<GenerateTTUMBean> {

	@Override
	public GenerateTTUMBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		GenerateTTUMBean generateTTUMObj = new GenerateTTUMBean();
		
		generateTTUMObj.setStFile_header(rs.getString("FILE_HEADER"));
		generateTTUMObj.setStExcel_Header(rs.getString("COLUMN_NAME"));
		generateTTUMObj.setStPadding(rs.getString("PADDING"));
		generateTTUMObj.setStChar_Size(rs.getString("CHAR_SIZE"));
		generateTTUMObj.setStStart_charpos(rs.getString("START_CHARPOS"));
		generateTTUMObj.setStHeader_Value(rs.getString("HEADER_VALUE"));
		generateTTUMObj.setStRemove_char(rs.getString("REMOVE_CHAR"));
	
		
		return generateTTUMObj;
		
		
		

	}
}

public void addTTUMEntry(String stTable_name , GenerateTTUMBean generateTTUMBeanObj) throws Exception
{
	try
	{
		//1. CREATE TABLE FOR TTUM ENTRIES WITH THE COLUMNS AS EXCEL COLS
		String stCategory = stTable_name.split("_")[0];
		String stFile_Name = stTable_name.split("_")[1];
		
		
		
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.addTTUMEntry");
		logger.info("Exception in addTTUMEntry "+e);
	}
	
}



public void TTUMRecords(List<String> Table_list,GenerateTTUMBean generatettumBeanObj)throws Exception
{
	 logger.info("***** GenerateTTUMDaoImpl.TTUMRecords Start ****");
	String JOIN1_QUERY = "";
	
	try
	{
		//String stTable1_Name = Table_list.get(0);
		//String stTable2_Name = Table_list.get(1);
		/*String[] stTable1 = stTable1_Name.split("_");
		String[] stTable2 = stTable2_Name.split("_");*/
		String stCategory = generatettumBeanObj.getStMerger_Category();
		String stFile1_Name = Table_list.get(0);
		String stFile2_Name = Table_list.get(1);
		String table1_condition = "";
		String table2_condition= "";
		String condition = "";
		logger.info("TTUM STARTS HERE *************************************************");
		logger.info(generatettumBeanObj.getStSubCategory());
		logger.info(generatettumBeanObj.getStMerger_Category());
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , stCategory ,generatettumBeanObj.getStSubCategory()},Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, stCategory,generatettumBeanObj.getStSubCategory() },Integer.class);
		//generatettumBeanObj.getInRec_Set_Id()
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,stCategory,generatettumBeanObj.getInRec_Set_Id()},new MatchParameterMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,stCategory,generatettumBeanObj.getInRec_Set_Id()},new MatchParameterMaster2());
		
	
		//prepare compare condition
		/*for(int i = 0; i<match_Headers1.size() ; i++)
		{
			//CHECKING PADDING FOR TABLE 1
			if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
			{
				if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						table1_condition = "SUBSTR( TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'999999999.99')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()new()+"_"+stFile1_Name+
								" WHERE SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = dbconn.getCon();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
							
						}
						else
						{
							table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
							
						}
						
						
					}
				}
				else
				{
					table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
							match_Headers1.get(i).getStMatchTable1_charSize()+")";

				}	
			}
			else
			{	
				if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"
									+generatettumBeanObj.getStMerger_Category()new()+"_"+stFile1_Name
								+" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = dbconn.getCon();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
							
						}
						else
						{
							table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
							
						}
						
					}
					
				}
				else
				{
					table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();

				}	
			}
			
			//CHECKING PADDING FOR TABLE 2
			logger.info("i value is "+i);
			logger.info("match headers length is "+match_Headers2.size());
			logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());
			if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
			{
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						table2_condition = " SUBSTR( TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()
							+" ) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()new()+"_"+stFile2_Name
						+" WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL" +
								" AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = dbconn.getCon();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							//ABC
							table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
							
						}
						else
						{
							table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
							
						}
						
					}
					
				}
				else
				{
					table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
							 match_Headers2.get(i).getStMatchTable2_charSize()+")";

				}			
				
			}
			else
			{
				logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()
								+" FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()new()+"_"+stFile2_Name
								+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = dbconn.getCon();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
							
						}
						else
						{
							table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
							
						}
						
					}
				}
				else
				{
					table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();

				}		
			
			}
			
			// PREPARING ACTUAL CONDITION OF BOTH TABLES
			if(i==(match_Headers1.size()-1))
			{
				
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
				condition = condition + table1_condition + " = "+table2_condition;
				
			}
			else
			{
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
				condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
			
			}
			
			
		}*/
		
		for(int i = 0; i<match_Headers1.size() ; i++)
		{
			//CHECKING PADDING FOR TABLE 1
			if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
			{
				if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',','')"+","
								+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+	match_Headers1.get(i).getStMatchTable1_charSize()+"))";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name 
								+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
						logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
							
						}
						else
						{
							/*table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";*/
							table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")";
							
						}
						
						
					}
				}
				else
				{
					table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
							match_Headers1.get(i).getStMatchTable1_charSize()+")";

				}	
			}
			else
			{	
				if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
						table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',',''))";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"
								+generatettumBeanObj.getStMerger_Category()+
								"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
							
						}
						else
						{
							table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
							
						}
						
					}
					
				}
				else
				{
					table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();

				}	
			}
			
		logger.info("table1_condition=="+table1_condition);
			
			//CHECKING PADDING FOR TABLE 2
			/*logger.info("i value is "+i);
			logger.info("match headers length is "+match_Headers2.size());
			logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
			if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
			{
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',','')"+","
										+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+ match_Headers2.get(i).getStMatchTable2_charSize()+"))";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"
								+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							//ABC
							table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
							
						}
						else
						{
							/*table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";*/
							
							table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")";
							
						}
						
					}
					
				}
				else
				{
					table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
							 match_Headers2.get(i).getStMatchTable2_charSize()+")";

				}			
				
			}
			else
			{
				logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
						table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',',''))";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"
								+generatettumBeanObj.getStMerger_Category()+
								"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
							
						}
						else
						{
							table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
							
						}
						
					}
				}
				else
				{
					table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();

				}		
			
			}
			
			logger.info("table2_condition=="+table2_condition);
			
			// PREPARING ACTUAL CONDITION OF BOTH TABLES
			if(i==(match_Headers1.size()-1))
			{
				
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
				condition = condition + table1_condition + " = "+table2_condition;
				logger.info("condition=="+condition);
				
			}
			else
			{
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
				condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
				logger.info(condition);
			
			}
			
			
		}
		
		
		
		logger.info("FINALLY CONDITION IS "+condition);
		
			
	/*	JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE T1.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()
				+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
				" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";*/
		
		//CHANGES MADE FOR VALUE_DATE
		if(generatettumBeanObj.getStCategory().equals("AMEX"))
		JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE TO_CHAR(TO_DATE(T1.VALUE_DATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
						" AND T1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS OS1 WHERE OS1.DCRS_REMARKS = '"+generatettumBeanObj.getStCategory()+"-UNRECON')"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
				" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";
		else
			JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
			+ " ) WHERE TO_CHAR(TO_DATE(T1.VALUEDATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
					" AND T1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS OS1 WHERE OS1.DCRS_REMARKS = '"+generatettumBeanObj.getStCategory()+"-UNRECON')"
			//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
			+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
			" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";
		
		//GET TTUM CONDITION
		String cond1 = getTTUMCondition(table1_file_id);
		String cond2 = getTTUMCondition(table2_file_id);
	
		logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

		//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
		logger.info("COMPARE QUERY IS****************************************");
		
		
		if(!cond1.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond1;
		//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond1;
		}
		if(!cond2.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond2;
		//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond2;
		}
		logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
		//logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
		
		//get failed records for table 1
		
		//getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
		getFailedRecords(JOIN1_QUERY, table2_file_id , stFile2_Name,stFile1_Name,table1_file_id,generatettumBeanObj);
		//getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
		
		//NOW TRUNCATE ALL TABLES----------------------------
		/*logger.info("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
		String TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_MATCHED";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		// table 2 truncate query
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_MATCHED";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);*/
		
		// ALREADY UPDATED OLD TTUM RECORDS IN MOVETORECON METHOD
		/*String UPDATE_OLD_TTUM_RECORDS = "UPDATE SETTLEMENT_CBS SET CREATEDDATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
		
		getJdbcTemplate().execute(UPDATE_OLD_TTUM_RECORDS);
		
		logger.info("updated old ttum records");*/
		
		logger.info("***** GenerateTTUMDaoImpl.TTUMRecords End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.TTUMRecords");
		 logger.error(" error in GenerateTTUMDaoImpl.TTUMRecords", new Exception("GenerateTTUMDaoImpl.TTUMRecords",e));
		 throw e;
	}
	finally
	{
		
	}
	
}



private static class MatchParameterMaster1 implements RowMapper<CompareBean> {

	@Override
	public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CompareBean compareBeanObj = new CompareBean();
		
		
		/*compareBeanObj.setStMatchTable1_header(rs.getString("TABLE1_HEADER"));
		compareBeanObj.setStMatchTable2_header(rs.getString("TABLE2_HEADER"));
		compareBeanObj.setStMatchTable1_Padding(rs.getString("TABLE1_PADDING"));
		compareBeanObj.setStMatchTable2_Padding(rs.getString("TABLE2_PADDING"));
		compareBeanObj.setStMatchTable1_startcharpos(rs.getString("TABLE1_START_CHARPOS"));
		compareBeanObj.setStMatchTable2_startcharpos(rs.getString("TABLE2_START_CHARPOS"));
		compareBeanObj.setStMatchTable1_charSize(rs.getString("TABLE1_CHARSIZE"));
		compareBeanObj.setStMatchTable2_charSize(rs.getString("TABLE2_CHARSIZE"));
		compareBeanObj.setStMatch_Datatype(rs.getString("DATATYPE"));
		compareBeanObj.setStMatchTable1_DatePattern(rs.getString("TABLE1_PATTERN"));
		compareBeanObj.setStMatchTable2_DatePattern(rs.getString("TABLE2_PATTERN"));
		*/
		compareBeanObj.setStMatchTable1_header(rs.getString("MATCH_HEADER"));
		compareBeanObj.setStMatchTable1_Padding(rs.getString("PADDING"));
		compareBeanObj.setStMatchTable1_startcharpos(rs.getString("START_CHARPOS"));
		compareBeanObj.setStMatchTable1_charSize(rs.getString("CHAR_SIZE"));
		compareBeanObj.setStMatchTable1_DatePattern(rs.getString("DATA_PATTERN"));
		compareBeanObj.setStMatchTable1_Datatype(rs.getString("DATATYPE"));
	
		
		return compareBeanObj;
		
		
	}
}

private static class MatchParameterMaster2 implements RowMapper<CompareBean> {

	@Override
	public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CompareBean compareBeanObj = new CompareBean();
		
		
		/*compareBeanObj.setStMatchTable1_header(rs.getString("TABLE1_HEADER"));
		compareBeanObj.setStMatchTable2_header(rs.getString("TABLE2_HEADER"));
		compareBeanObj.setStMatchTable1_Padding(rs.getString("TABLE1_PADDING"));
		compareBeanObj.setStMatchTable2_Padding(rs.getString("TABLE2_PADDING"));
		compareBeanObj.setStMatchTable1_startcharpos(rs.getString("TABLE1_START_CHARPOS"));
		compareBeanObj.setStMatchTable2_startcharpos(rs.getString("TABLE2_START_CHARPOS"));
		compareBeanObj.setStMatchTable1_charSize(rs.getString("TABLE1_CHARSIZE"));
		compareBeanObj.setStMatchTable2_charSize(rs.getString("TABLE2_CHARSIZE"));
		compareBeanObj.setStMatch_Datatype(rs.getString("DATATYPE"));
		compareBeanObj.setStMatchTable1_DatePattern(rs.getString("TABLE1_PATTERN"));
		compareBeanObj.setStMatchTable2_DatePattern(rs.getString("TABLE2_PATTERN"));
		*/

		compareBeanObj.setStMatchTable2_header(rs.getString("MATCH_HEADER"));
		compareBeanObj.setStMatchTable2_Padding(rs.getString("PADDING"));
		compareBeanObj.setStMatchTable2_startcharpos(rs.getString("START_CHARPOS"));
		compareBeanObj.setStMatchTable2_charSize(rs.getString("CHAR_SIZE"));
		compareBeanObj.setStMatchTable2_DatePattern(rs.getString("DATA_PATTERN"));
		compareBeanObj.setStMatchTable2_Datatype(rs.getString("DATATYPE"));
	
		
		return compareBeanObj;
		
		
	}
}



public String getTTUMCondition(int inFile_Id) throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.getTTUMCondition Start ****");
	String temp_param = "";
	String condition = "";
	try
	{
		List<FilterationBean> ttum_details = getJdbcTemplate().query(GET_TTUM_PARAMS, new Object[] {inFile_Id}, new TTUMParameterMaster());
		
		for(int i = 0; i<ttum_details.size();i++){
			FilterationBean filterBeanObj = new FilterationBean();
			filterBeanObj = ttum_details.get(i);
			temp_param = filterBeanObj.getStSearch_header().trim();
			if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
			{
				if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
				{
					condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
						filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
				}
				else if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
				{
					condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
							"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
				}
				else
				{
					if(i == (ttum_details.size()-1))
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
			}
			else
			{
				if(filterBeanObj.getStSearch_Condition().equals("="))
				{
					condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
								filterBeanObj.getStSearch_pattern().trim()+"'";
				}
				else if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
				{
					condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
				}
				else
				{
					if(i == (ttum_details.size()-1))
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
				
			}
			
			for(int j= (i+1); j <ttum_details.size(); j++)
			{
				//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
				if(temp_param.equals(ttum_details.get(j).getStSearch_header()))
				{
						
					if(ttum_details.get(j).getStSearch_padding().equals("Y"))
					{
						if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
						{
							condition = condition + " OR SUBSTR(TRIM(" + ttum_details.get(j).getStSearch_header()+") , "+ttum_details.get(j).getStsearch_Startcharpos()+","+
									ttum_details.get(j).getStsearch_Endcharpos()+") "+ttum_details.get(j).getStSearch_Condition().trim()+ 
								"'"+ttum_details.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((ttum_details.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + " OR SUBSTR(TRIM(" + ttum_details.get(j).getStSearch_header()+") , "+ttum_details.get(j).getStsearch_Startcharpos()+","+
									ttum_details.get(j).getStsearch_Endcharpos()+") "+ttum_details.get(j).getStSearch_Condition().trim()+
									"'%"+ttum_details.get(j).getStSearch_pattern().trim()+"%'";
						}
						else
						{
							if(j==(ttum_details.size()-1))
							{	
								/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
								condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					else
					{
						if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
						{
							condition = condition + " OR TRIM(" + ttum_details.get(j).getStSearch_header()+") "+
									ttum_details.get(j).getStSearch_Condition().trim()+" '"+ttum_details.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((ttum_details.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + " OR TRIM(" + ttum_details.get(j).getStSearch_header()+") "+
									ttum_details.get(j).getStSearch_Condition().trim()+" "+
									"'%"+ttum_details.get(j).getStSearch_pattern().trim()+"%'";
							
						}
						else
						{
							if(j==(ttum_details.size()-1))
							{
								condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					
				
					
						i = j;
				}
				
			}
			//logger.info("i value is "+i);
			if(i != (ttum_details.size())-1)
			{
				if(ttum_details.get(i).getStSearch_Condition().equals("!="))
				{
					condition = condition + " ) ) AND ";
				}
				else
					condition = condition +" ) AND ";
			}
			else
			{
				condition = condition +")";
			}
			
		//	logger.info("condition is "+condition);
		}
		
		logger.info("condition is "+condition);
		
		logger.info("***** GenerateTTUMDaoImpl.getTTUMCondition End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.getTTUMCondition");
		logger.error(" error in GenerateTTUMDaoImpl.getTTUMCondition", new Exception("GenerateTTUMDaoImpl.getTTUMCondition",e));
		 throw e;
		
	}
	return condition;
}

private static class TTUMParameterMaster implements RowMapper<FilterationBean> {

	@Override
	public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		FilterationBean filterationObjBean = new FilterationBean();
		
		filterationObjBean.setStSearch_header(rs.getString("FILE_HEADER"));
		filterationObjBean.setStSearch_pattern(rs.getString("VALUE"));
		filterationObjBean.setStSearch_padding(rs.getString("PADDING"));
		filterationObjBean.setStsearch_Startcharpos(rs.getString("START_POS"));
		filterationObjBean.setStsearch_Endcharpos(rs.getString("CHAR_SIZE"));
		filterationObjBean.setStSearch_Condition(rs.getString("CONDITION"));
		
		return filterationObjBean;
	/*	KnockOffBean knockOffBean = new KnockOffBean();
		
		knockOffBean.setStReversal_header(rs.getString("HEADER"));
		knockOffBean.setStReversal_value(rs.getString("VALUE"));
		return knockOffBean;*/
		
		
	}
	}



public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.getFailedRecords Start ****");
	PreparedStatement pstmt = null;
	Connection conn = null;
	ResultSet rset = null;
	String reversal_condition = "";
	String update_condition = "";
	String stFinal_cond = "";
	
	try
	{
		conn = getConnection();
		pstmt = conn.prepareStatement(QUERY);
		rset = pstmt.executeQuery();
	
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), generatettumBeanObj.getStMerger_Category()},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());

		//CREATE CONDITION USING KNOCKOFF CRITERIA 
		while(rset.next())
		{
			logger.info("WHILE STARTS");
			update_condition = "";
			reversal_condition = "";
			stFinal_cond = "";
		
				//code for inserting in settlement table
				/*String UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
						+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()
							+"','DD/MM/YYYY') AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' ";*/
			String UPDATE_QUERY = "";
			if(generatettumBeanObj.getStCategory().equals("AMEX"))
				UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
					+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
						")' WHERE TO_CHAR(TO_DATE(VALUE_DATE,'DD-MM-YYYY'),'DD/MM/YYYY') BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()
						+"' AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' AND "+
						"FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_AMEX_CBS OS1 WHERE OS1.DCRS_REMARKS = 'AMEX-UNRECON')";
			else
				UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
				+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
					")' WHERE TO_CHAR(TO_DATE(VALUEDATE,'DD-MM-YYYY'),'DD/MM/YYYY') BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()
					+"' AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' AND "+
					"FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_AMEX_CBS OS1 WHERE OS1.DCRS_REMARKS = 'AMEX-UNRECON')";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), generatettumBeanObj.getStMerger_Category()},Integer.class);
				logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				logger.info("KNOCKOFF CRITERIA SIZE "+knockoff_Criteria1.size());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
								
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
										+"'";
								
							}
					}
					else
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
							update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
							
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
										+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
								
							}
					
					}
					
				}
				
				logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
				pstmt1.executeUpdate();
				
				pstmt1 = null;
			
		}
		logger.info("while completed");
		
		logger.info("***** GenerateTTUMDaoImpl.getFailedRecords End ****");
		
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.getFailedRecords");
		logger.error(" error in GenerateTTUMDaoImpl.getFailedRecords", new Exception("GenerateTTUMDaoImpl.getFailedRecords",e));
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
	
}


private static class KnockOffCriteriaMaster implements RowMapper<KnockOffBean> {

	@Override
	public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		/*while(rs.next())
	{*/
		//logger.info("header is "+rs.getString("HEADER"));
		KnockOffBean knockOffBean = new KnockOffBean();

		knockOffBean.setStReversal_header(rs.getString("HEADER"));
		knockOffBean.setStReversal_padding(rs.getString("PADDING"));
		knockOffBean.setStReversal_charpos(rs.getString("START_CHARPOSITION"));
		knockOffBean.setStReversal_charsize(rs.getString("CHAR_SIZE"));
		knockOffBean.setStReversal_value(rs.getString("HEADER_VALUE"));
		knockOffBean.setStReversal_condition(rs.getString("CONDITION"));
		//knockOffBean.setStReversal_value(rs.getString("VALUE"));
		//filterBean.setStSearch_padding(rs.getString("PADDING"));
		//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
		//filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
		//filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));

		//search_params.add(filterBean);
		//	}
		return knockOffBean;


	}
}

//TTUM FOR AMEX
public List<List<GenerateTTUMBean>> generateTTUMForAMEX(GenerateTTUMBean generatettumBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateTTUMForAMEX Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";	
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		ExcelHeaders.add("ACCOUNT_NUMBER");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("SERVICE_OUTLET");
		ExcelHeaders.add("PART_TRAN_TYPE");
		ExcelHeaders.add("TRANSACTION_AMOUNT");
		ExcelHeaders.add("TRANSACTION_PARTICULARS");
		ExcelHeaders.add("REFERENCE_NUMBER");
		ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
		ExcelHeaders.add("REFERENCE_TRANSACTION_AMOUNT");
		ExcelHeaders.add("REMARKS");
		
		
		generatettumBeanObj.setStExcelHeader(ExcelHeaders);
		Excel_header.add(generatettumBeanObj);
		
		for(int i = 0 ; i<ExcelHeaders.size();i++)
		{
			table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
			insert_cols = insert_cols+","+ExcelHeaders.get(i);
		}
		String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
		logger.info("check table "+CHECK_TABLE);
		int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+"("+table_cols+")";
			logger.info("CREATE QUERY IS "+query);
			getJdbcTemplate().execute(query);			
		}
		
		/*String CHECK_ACC = "SELECT COUNT(*) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
							" WHERE SUBSTR(CONTRA_ACCOUNT,4,6) = '505000'";
		int count = getJdbcTemplate().queryForObject(CHECK_ACC, new Object[]{},Integer.class);
		String GET_DATA = "";*/
		/*if(count>0)
		{*/			
		
		
		String GET_DATA ="SELECT (SUBSTR(CONTRA_ACCOUNT,1,NVL(INSTR(CONTRA_ACCOUNT,'505000'),0)-1))||37000010085 AS DRACC,FORACID," +
					"REPLACE(AMOUNT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE, SUBSTR(PARTICULARALS,1,8) AS ATM_ID, " +
				"TO_CHAR(TO_DATE(SUBSTR(PARTICULARALS,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NO,1,10)) AS REF_NO," +
				" REMARKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
				" WHERE (DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(8)%') AND "+
				"TO_DATE(VALUE_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND " +
				"TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
		/*}
		else 
		{			
			GET_DATA ="SELECT SUBSTR(CONTRA_ACCOUNT,1,4)||37000010085 AS DRACC,CONTRA_ACCOUNT,REPLACE(AMOUNT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE, SUBSTR(PARTICULARALS,1,8) AS ATM_ID, " +
					"TO_CHAR(TO_DATE(SUBSTR(PARTICULARALS,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NO,1,10)) AS REF_NO," +
					" REMARKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
					" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";			
		}*/
		
		logger.info("GET_DATA=="+GET_DATA);
		
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		
		while (rset.next()) {
			
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setStCreditAcc(rset.getString("FORACID"));
			//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
			generateBean.setStDebitAcc(rset.getString("DRACC"));
			generateBean.setStAmount(rset.getString("AMOUNT"));
			String stTran_Particular = "REV"+"-"+rset.getString("ATM_ID")+"-"+rset.getString("TRANDATE")+"-AMEX-"+rset.getString("REF_NO");
			generateBean.setStTran_particulars(stTran_Particular);
			generateBean.setStCard_Number(rset.getString("REMARKS"));
			generateBean.setStDate(rset.getString("VALUE_DATE"));
			String remark = getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[] {},String.class);
			generateBean.setStRemark(remark);
			
			TTUM_Data.add(generateBean);
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		//inserting data IN TTUM TABLE
		for(GenerateTTUMBean beanObj : TTUM_Data)
		{
			//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
									generatettumBeanObj.getStMerger_Category()+"_UNRECON-"+generatettumBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
			getJdbcTemplate().execute(INSERT_DATA);
			
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStMerger_Category()+"_UNRECON-"+generatettumBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
				logger.info("INSERT_DATA=="+INSERT_DATA);
									
		}
		
		String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
				" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
				+" WHERE (DCRS_REMARKS LIKE '%(103)%' OR DCRS_REMARKS LIKE '%(8)%') AND "+
				"TO_CHAR(TO_DATE(VALUE_DATE,'DD-MM-YYYY'),'DD/MM/YYYY')  BETWEEN '"+generatettumBeanObj.getStStart_Date()+"' AND '"+generatettumBeanObj.getStEnd_Date()+"'" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_CBS)";
		
		logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
		
		getJdbcTemplate().execute(UPDATE_RECORDS);
		
		logger.info("***** GenerateTTUMDaoImpl.generateTTUMForAMEX End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateTTUMForAMEX");
		logger.error(" error in GenerateTTUMDaoImpl.generateTTUMForAMEX", new Exception("GenerateTTUMDaoImpl.generateTTUMForAMEX",e));
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


public void getSwitchTTUMData(GenerateTTUMBean generatettumBeanObj) throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.getSwitchTTUMData Start ****");
	try
	{
		String GET_DATA = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
					" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON-GENERATE-TTUM' WHERE DCRS_REMARKS = '"+
						generatettumBeanObj.getStMerger_Category()+"-UNRECON"+"'";
		
		logger.info("GET_DATA QUERY IS "+GET_DATA);
		getJdbcTemplate().execute(GET_DATA);
		
		logger.info("***** GenerateTTUMDaoImpl.getSwitchTTUMData End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.getSwitchTTUMData");
		logger.error(" error in GenerateTTUMDaoImpl.getSwitchTTUMData", new Exception("GenerateTTUMDaoImpl.getSwitchTTUMData",e));
		 throw e;
	}
}

public void generateSwitchTTUM(GenerateTTUMBean generateTTUMBeanObj)throws Exception
{
	logger.info("***** GenerateTTUMDaoImpl.generateSwitchTTUM Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		ExcelHeaders.add("ACCOUNT_NUMBER");
		ExcelHeaders.add("CURRENCY_CODE");
		ExcelHeaders.add("SERVICE_OUTLET");
		ExcelHeaders.add("PART_TRAN_TYPE");
		ExcelHeaders.add("TRANSACTION_AMOUNT");
		ExcelHeaders.add("TRANSACTION_PARTICULARS");
		ExcelHeaders.add("REFERENCE_NUMBER");
		ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
		ExcelHeaders.add("REFERENCE_TRANSACTION_AMOUNT");
		ExcelHeaders.add("REMARKS");
		
		
		generateTTUMBeanObj.setStExcelHeader(ExcelHeaders);
		Excel_header.add(generateTTUMBeanObj);
		
		for(int i = 0 ; i<ExcelHeaders.size();i++)
		{
			table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
			insert_cols = insert_cols+","+ExcelHeaders.get(i);
		}
		String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generateTTUMBeanObj.getStMerger_Category()+"_"+generateTTUMBeanObj.getStFile_Name().toUpperCase()+"'";
		logger.info("check table "+CHECK_TABLE);
		int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TTUM_"+generateTTUMBeanObj.getStMerger_Category()+"_"+generateTTUMBeanObj.getStFile_Name()+"("+table_cols+")";
			logger.info("CREATE QUERY IS "+query);
			getJdbcTemplate().execute(query);			
		}
		
		/*String GET_TTUM_DATA = "SELECT * FROM SETTLEMENT_"+generateTTUMBeanObj.getStMerger_Category()+"_"+generateTTUMBeanObj.getStFile_Name()
					+" WHERE DCRS_REMARKS = '"+generateTTUMBeanObj.getStMerger_Category()+"-UNRECON-GENERATE-TTUM";*/
		//CHECK FOR 0 IN ATM ID AS PER SAMEER SIR'S MAIL
		//String CHECK_ZERO = "SELECT COUNT(*) FROM"
		
		String GET_TTUM_DATA = "SELECT SUBSTR(TERMID,3,4) || 78000010085 AS SOL,TERMID,ACCTNUM,AMOUNT,PAN,TO_CHAR(TO_DATE(LOCAL_DATE,'MM/DD/YYYY'),'DDMMYY') AS LOCAL_DATE,SUBSTR(TRACE,-6,6) AS TRACE ," +
				" ACCEPTORNAME " +" FROM SETTLEMENT_"+generateTTUMBeanObj.getStCategory()+"_"+generateTTUMBeanObj.getStFile_Name() 
				+ " WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";
		conn = getConnection();
		pstmt = conn.prepareStatement(GET_TTUM_DATA);
		rset = pstmt.executeQuery();
		
		while(rset.next())
		{
			GenerateTTUMBean generateBean = new GenerateTTUMBean();
			generateBean.setStDebitAcc(rset.getString("ACCTNUM").replaceAll(" ", "").replaceAll("^0*",""));
			//generateTTUMBeanObj.setStCreditAcc("99937200010660");//CHANGE AS PER SAMEER SIR MAIL RECIEVED ON 12 JAN 18
			generateTTUMBeanObj.setStCreditAcc(rset.getString("SOL"));
			generateBean.setStAmount(rset.getString("AMOUNT"));
			//String stTran_particulars = "REV/"+generateTTUMBean.getStCategory()+"/"+rset.getString("LOCAL_DATE")+"/"+rset.getString("TRACE");
			
			String stTran_particulars = "REV-"+rset.getString("TERMID")+"-"+rset.getString("LOCAL_DATE")+"-AMEX-"+rset.getString("TRACE");
			generateBean.setStDate(rset.getString("LOCAL_DATE"));
			
		//	generateTTUMBean.setStDate(rset.getString("LOCAL_DATE"));
			generateBean.setStTran_particulars(stTran_particulars);
			//generateTTUMBeanObj.setStRRNo(rset.getString("ISSUER"));
			generateBean.setStCard_Number(rset.getString("PAN"));
			String remark = getJdbcTemplate().queryForObject("select 'DCRM'||ttum_seq.nextval from dual", new Object[] {},String.class);
			generateBean.setStRemark(remark);
			TTUM_Data.add(generateBean);
		
		}
		Data.add(Excel_header);	
		Data.add(TTUM_Data);
		
		//inserting data IN TTUM TABLE
		for(GenerateTTUMBean beanObj : TTUM_Data)
		{
			//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
			String INSERT_DATA = "INSERT INTO TTUM_"+generateTTUMBeanObj.getStMerger_Category()+"_"+generateTTUMBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
					generateTTUMBeanObj.getStMerger_Category()+"_UNRECON-"+generateTTUMBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generateTTUMBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
			getJdbcTemplate().execute(INSERT_DATA);
			
			INSERT_DATA = "INSERT INTO TTUM_"+generateTTUMBeanObj.getStMerger_Category()+"_"+generateTTUMBeanObj.getStFile_Name()+" ("+insert_cols+") VALUES('"+
					generateTTUMBeanObj.getStMerger_Category()+"_UNRECON-"+generateTTUMBeanObj.getInRec_Set_Id()+"-TTUM',SYSDATE,'"+generateTTUMBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
									
		}
		
		logger.info("***** GenerateTTUMDaoImpl.generateSwitchTTUM End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateSwitchTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.generateSwitchTTUM", new Exception("GenerateTTUMDaoImpl.generateSwitchTTUM",e));
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
}

@Override
public List<List<GenerateTTUMBean>> generatecashnetTTUM(GenerateTTUMBean generatettumBeanObj) throws Exception

{
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{


		
		
		try
		{
			
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			/*String CHECK_ACC = "SELECT COUNT(*) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
								" WHERE SUBSTR(CONTRA_ACCOUNT,4,6) = '505000'";
			int count = getJdbcTemplate().queryForObject(CHECK_ACC, new Object[]{},Integer.class);
			String GET_DATA = "";*/
			/*if(count>0)
			{*/		
			if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER")) {
				
				return  generateCashnetACQTTUM(generatettumBeanObj);
				
			}if(generatettumBeanObj.getStSubCategory().equals("ISSUER")) {
				
				return  generateCashnetIssTTUM(generatettumBeanObj);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
			logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
			 throw e;
		}
		
		

		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
		// throw e;
	}
	finally{
		if(rset!=null){
			rset.close();
		}
		if(pstmt!=null){
			pstmt.close();
		}
		if (conn!=null){
			conn.close();
		}
	}
	return Data;
}


public List<List<GenerateTTUMBean>> generateCashnetACQTTUM(GenerateTTUMBean generatettumBeanObj) {
	
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	ResultSet rset=null;
	
	PreparedStatement  pstmt= null;
	Connection conn= null;
	
	 ArrayList<String> tableheaders = new ArrayList<>();
		
		ExcelHeaders.add("ACCOUNT_NUMBER");
		ExcelHeaders.add("CURRENCY_CODE_OF_ACCOUNT_NUMBER");
		ExcelHeaders.add("SERVICE_OUTLET");
		ExcelHeaders.add("PART_TRAN_TYPE");
		ExcelHeaders.add("TRANSACTION_AMOUNT");
		ExcelHeaders.add("TRANSACTION_PARTICULAR");
		ExcelHeaders.add("REFERENCE_NUMBER");
		ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
		ExcelHeaders.add("REFERENCE_AMOUNT");
		ExcelHeaders.add("REMARKS");
		
		
		
		tableheaders.add("ACCOUNT_NUMBER");
		tableheaders.add("CURRENCY_CODE");
		tableheaders.add("SERVICE_OUTLET");
		tableheaders.add("PART_TRAN_TYPE");
		tableheaders.add("TRANSACTION_AMOUNT");
		tableheaders.add("TRANSACTION_PARTICULARS");
		tableheaders.add("REFERENCE_NUMBER");
		tableheaders.add("REFERENCE_CURRENCY_CODE");
		tableheaders.add("REFERENCE_AMOUNT");
		tableheaders.add("REMARKS");
	
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	generatettumBeanObj.setStExcelHeader(ExcelHeaders);
	Excel_header.add(generatettumBeanObj);
	
	for(int i = 0 ; i<tableheaders.size();i++)
	{
		table_cols =table_cols+","+ tableheaders.get(i)+" VARCHAR (100 BYTE)";
		insert_cols = insert_cols+","+tableheaders.get(i);
	}
	
	String GET_DATA="";
	String allRspCode = "";
	if(!generatettumBeanObj.getRespcode().equals("All")) {
		
		allRspCode =  "regexp_replace(t1.dcrs_remarks, '[^0-9]', '')= "+generatettumBeanObj.getRespcode()+" AND ";
	}else{
		
		allRspCode=" regexp_replace(t1.dcrs_remarks, '[^0-9]', '') <> 0  AND";
	}
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
		// pool account change from 999780000000019  to 99978000010204 as per mail from sanjaya on date 19/mar/2019  
		
		if(! generatettumBeanObj.getRespcode().equals("0") ) {
		 GET_DATA =" Select distinct substr(particularals,1,8) ATMID ,substr(PARTICULARALS,10,8) PARTICULARALS, DECODE (SUBSTR (t1.PARTICULARALS , 3,1 ),0, SUBSTR (t1.PARTICULARALS, 4, 3), SUBSTR (t1.PARTICULARALS, 3, 4))||'78000010085' CONTRA_ACCOUNT,'99978000010204 'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount,"
                 +" 'C/REV/' || substr(t1.PARTICULARALS,1,8) || '/'|| to_char(to_date(substr(particularals,10,8),'dd-mm-rr'),'ddmmyy') || '/'||  substr(t1.ref_no,5,6) || '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars, substr(t1.ref_no,5,6) ref_no,TRAN_DATE,"
               +"  remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,TRAN_DATE VALUE_DATE,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode"
                +"  from SETTLEMENT_cashnet_acq_CBS t1 "
                +" WHERE  regexp_replace(t1.dcrs_remarks, '[^0-9]', '')in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				//+" AND TO_DATE(TRAN_DATE,'DD-MM-YYYY') <= (SELECT MAX(FILEDATE)-1 FROM SETTLEMENT_cashnet_acq_CBS)"
				+ " AND  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_acq_CBS) "
			 	+" AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and t1.REMARKS=t3.remarks " //t3.REFERENCE_NUMBER
			 	+"  AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT "
                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
		} else if (generatettumBeanObj.getRespcode().equals("0")) {
			
			 GET_DATA =" Select distinct substr(particularals,1,8) ATMID, substr(PARTICULARALS,10,8) PARTICULARALS, decode(substr(t1.PARTICULARALS,3,1),0,substr(t1.PARTICULARALS,4,3),substr(t1.PARTICULARALS,3,4))||'37000010085' DRACC  ,'99978000010204' CONTRA_ACCOUNT  ,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount,"
					 +" 'C/REV/' || substr(t1.PARTICULARALS,1,8) || '/'|| to_char(to_date(substr(particularals,10,8),'dd-mm-rr'),'ddmmyy')  || '/'|| substr(t1.ref_no,5,6) || '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars,substr(t1.ref_no,5,6) ref_no,TRAN_DATE,"
	               +"  remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,TRAN_DATE VALUE_DATE,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode"
	                +"  from SETTLEMENT_cashnet_ACQ_CBS t1 "
	                +" WHERE   regexp_replace(t1.dcrs_remarks, '[^0-9]', '')in ( "+generatettumBeanObj.getRespcode()+") AND"
	                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
					+" TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
					//+" AND TO_DATE(TRAN_DATE,'DD-MM-YYYY') <= (SELECT MAX(FILEDATE)-1 FROM SETTLEMENT_cashnet_acq_CBS) "
					+ " AND  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_acq_CBS)"
				 	+" AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and trim(t1.REMARKS)=trim(t3.remarks) " //t3.REFERENCE_NUMBER
				 	+"  AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT "
	                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
			
		}
		
	} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
		
		 GET_DATA =" Select   distinct decode(substr(t1.termid,3,1),0,substr(t1.termid,4,3),substr(t1.termid,3,4))||'78000010085'CONTRA_ACCOUNT  ,'999780000000019' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT ,',','') TransactionAmount," // replace AMOUNT_EQUIV to AMOUNT on date 18aug2020
		 		+ " 'C/REV/'||t1.termid ||'/'||to_char(to_date(t1.LOCAL_DATE ,'mm/dd/yyyy'),'ddmmyy')||'/'|| substr(t1.TRACE,2,6 )||regexp_replace(t1.dcrs_remarks, '[^0-9]', '') as TRANSACTIONPARTICULARS,"
		 		+ " pan as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.LOCAL_DATE,'MM/DD/YYYY'),'dd/mm/yyyy') as VALUE_DATE ,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode ,substr(t1.TRACE,2,6 ) ref_no "
		 		+ "  from SETTLEMENT_cashnet_ACQ_switch t1 " 
		 		+" WHERE t1.LOCAL_DATE <> '00/00/0000' and   regexp_replace(t1.dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(LOCAL_DATE,'mm/dd/yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				//+" AND TO_DATE(LOCAL_DATE,'mm/dd/yyyy') <= (SELECT MAX(FILEDATE)-1 FROM SETTLEMENT_cashnet_ACQ_switch) "
				+ " AND  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_acq_switch) "
				+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan)=trim(t3.remarks) " //t3.REFERENCE_NUMBER
		 	+"  AND to_number(t1.AMOUNT_EQUIV) = to_number(t3.TRANSACTION_AMOUNT) "
            +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
		
		
	} else if(generatettumBeanObj.getStFile_Name().equals("CASHNET")) {
		
		 GET_DATA =" Select  distinct  decode(substr(t1.CARD_ACC_TERMINAL_ID,3,1),0,substr(t1.CARD_ACC_TERMINAL_ID,4,3),substr(t1.CARD_ACC_TERMINAL_ID,3,4))||'37000010085' DRACC  ,'999780000000019'CONTRA_ACCOUNT ,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(ltrim(t1.ACQ_SETTLE_AMNT,'0') ,',','') TransactionAmount,"
		 		+" 'C/REV/' || t1.CARD_ACC_TERMINAL_ID || '/'|| to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'ddmmyy')  || '/'||substr(TXN_SERIAL_NO,-6)|| '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars,"
		 		+ " pan_number as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'dd/mm/yyyy') as VALUE_DATE ,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode,substr(TXN_SERIAL_NO,-6) ref_no  "
		 		+ "  from SETTLEMENT_cashnet_acq_cashnet t1 " 
		 		/*"inner join   ( SELECT DISTINCT REMARKS,CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%' ) t2"  //settlement_cashnet_iss_cbs t2 on   "
		 		+ " on trim(t1.PAN)= (t2.REMARKS)  "*/
		 		+" WHERE    regexp_replace(t1.dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+") AND"
               + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(t1.TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				//+" AND TO_DATE(t1.TRANSACTION_DATE ,'yymmdd') <= (SELECT MAX(FILEDATE)-1 FROM SETTLEMENT_cashnet_acq_cashnet) "
				+ " and  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_acq_Cashnet) "
				+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan_number)=trim(t3.remarks) " //t3.REFERENCE_NUMBER
		 	+"  AND to_number(t1.ACQ_SETTLE_AMNT) = to_number(t3.TRANSACTION_AMOUNT) "
           +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
		
		
	}
	logger.info(GET_DATA);
	
	try{
	
	conn = getConnection();
	pstmt = conn.prepareStatement(GET_DATA);
	rset = pstmt.executeQuery();
	
	while (rset.next()) {
		
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		
		
		//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
		generateBean.setStDebitAcc(rset.getString("CONTRA_ACCOUNT"));
		generateBean.setStCreditAcc(rset.getString("DRACC"));
		generateBean.setStAmount(rset.getString("TransactionAmount"));
		String stTran_Particular = rset.getString("TRANSACTIONPARTICULARS");
		generateBean.setStTran_particulars(stTran_Particular);
		
		if(rset.getString("referencenumber").contains("/") && generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
			String query = "SELECT DISTINCT rEMARKS FROM debitcard_recon.cbs_AMEX_RAWDATA  WHERE substr(ref_no,5,6) = '"+rset.getString("ref_no")+"' AND "
					+ " substr(PARTICULARALS,10,8) ='"+rset.getString("PARTICULARALS")+"' and substr(particularals,1,8) = '"+rset.getString("ATMID")+"' AND E='D' ";
			System.out.println(query);
			String remarks = getJdbcTemplate().queryForObject(query, String.class);
			
			generateBean.setStCard_Number(remarks);
			
			
		}else {
			generateBean.setStCard_Number(rset.getString("referencenumber"));
		}
		generateBean.setStDate(rset.getString("VALUE_DATE"));
		generateBean.setRespcode(rset.getString("respcode"));
		generateBean.setRef_num(rset.getString("ref_no"));
		
		String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
		
		Date date= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String date2 =  sdf.format(date);
		remark= "CSNA"+date2+remark;
		generateBean.setStRemark(remark);
		
		TTUM_Data.add(generateBean);
	}
	//Data.add(Excel_header);	
	//Data.add(TTUM_Data);
	
	pstmt.close();
	
	//inserting data IN TTUM TABLE
	for(GenerateTTUMBean beanObj : TTUM_Data)
	{
		//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
		if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
			if(beanObj.getStDebitAcc().contains("78000010085") ) {
			
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
					
			getJdbcTemplate().execute(INSERT_DATA);
		
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+ beanObj.getStRemark()+"' ,'INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getRef_num()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
			} else if(beanObj.getStDebitAcc().contains("999780000000019") ) {
				
				String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
						+ "VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
										"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
										"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getRef_num()+"')";
			
				getJdbcTemplate().execute(INSERT_DATA);
			
				INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
						"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
						"','"+beanObj.getStCard_Number()+"' ,'INR','"+beanObj.getStAmount()+"','"+ beanObj.getStRemark()+"')";
					getJdbcTemplate().execute(INSERT_DATA);
				
				
				
				
				
			} else {
				
				String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
						+ "VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
										"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
										"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
						
				getJdbcTemplate().execute(INSERT_DATA);
			
				INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
						"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
						"','"+ beanObj.getStRemark()+"' ,'INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getRef_num()+"')";
					getJdbcTemplate().execute(INSERT_DATA);
					
			}
				
			
		} else if(generatettumBeanObj.getStFile_Name().equals("CASHNET")) {
				

				String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
						+ "VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
										"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
										"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getRef_num()+"')";
			
				
				getJdbcTemplate().execute(INSERT_DATA);
			
			
				
				INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
						"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
						"','"+beanObj.getStCard_Number()+"' ,'INR','"+beanObj.getStAmount()+"','"+ beanObj.getStRemark()+"')";
					getJdbcTemplate().execute(INSERT_DATA);
				
			
		} 
		
		else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
			
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
			
			logger.info(INSERT_DATA);
			
			getJdbcTemplate().execute(INSERT_DATA);
			
			
	
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getRef_num()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
				logger.info(INSERT_DATA);
			
			
		}
								
	}
	//SETTLEMENT_cashnet_iss_CBS
	String UPDATE_RECORDS="";
	
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
	UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
			" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
			+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+" ) and"
					+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
			" TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
			" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
			" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
	} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) { 
		
		UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
				" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
				+" WHERE LOCAL_DATE <> '00/00/0000' and regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
						+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
				" TO_DATE(LOCAL_DATE,'mm/dd/YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
				" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
	
		
		
	}else if(generatettumBeanObj.getStFile_Name().equals("CASHNET")) { 
		
		UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
				" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
				+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
						+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
				" TO_DATE(TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
				" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
	
		
		
	}
	 TTUM_Data.clear();
	 
	 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
	 
	getJdbcTemplate().execute(UPDATE_RECORDS);
	
	String query="";
	
	
	 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
			+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  "
					+ " and regexp_replace(dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+" )"  ;
	
	
	logger.info(query);
	 pstmt = conn.prepareStatement(query);
	 rset = pstmt.executeQuery();
	
	
	while (rset.next()) {
		
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
		generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
		String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
		generateBean.setStTran_particulars(stTran_Particular);
		generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
		generateBean.setStDate(rset.getString("RECORDS_DATE"));
		generateBean.setStRemark(rset.getString("REMARKS"));
		generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
		
		
		TTUM_Data.add(generateBean);
	}
	
	
	Data.add(Excel_header);	
	Data.add(TTUM_Data);
	
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM End ****");
	
	
	return Data;
	}catch (Exception ex) {
		
		ex.printStackTrace();
		return Data;
		
		
	}
	
	
}


public List<List<GenerateTTUMBean>> generateCashnetIssTTUM (GenerateTTUMBean generatettumBeanObj) {
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	ResultSet rset=null;
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	PreparedStatement  pstmt= null;
	 ExcelHeaders = new ArrayList<>();
	 ArrayList<String> tableheaders = new ArrayList<>();
	Connection conn= null;
	ExcelHeaders.add("ACCOUNT_NUMBER");
	ExcelHeaders.add("CURRENCY_CODE_OF_ACCOUNT_NUMBER");
	ExcelHeaders.add("SERVICE_OUTLET");
	ExcelHeaders.add("PART_TRAN_TYPE");
	ExcelHeaders.add("TRANSACTION_AMOUNT");
	ExcelHeaders.add("TRANSACTION_PARTICULAR");
	ExcelHeaders.add("REFERENCE_NUMBER");
	ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
	ExcelHeaders.add("REFERENCE_AMOUNT");
	ExcelHeaders.add("REMARKS");
	
	
	
	tableheaders.add("ACCOUNT_NUMBER");
	tableheaders.add("CURRENCY_CODE");
	tableheaders.add("SERVICE_OUTLET");
	tableheaders.add("PART_TRAN_TYPE");
	tableheaders.add("TRANSACTION_AMOUNT");
	tableheaders.add("TRANSACTION_PARTICULARS");
	tableheaders.add("REFERENCE_NUMBER");
	tableheaders.add("REFERENCE_CURRENCY_CODE");
	tableheaders.add("REFERENCE_AMOUNT");
	tableheaders.add("REMARKS");
	
	
	
	
	
	
	generatettumBeanObj.setStExcelHeader(ExcelHeaders);
	Excel_header.add(generatettumBeanObj);
	
	for(int i = 0 ; i<tableheaders.size();i++)
	{
		table_cols =table_cols+","+ tableheaders.get(i)+" VARCHAR (100 BYTE)";
		insert_cols = insert_cols+","+tableheaders.get(i);
	}
	
	
	
	
	
	 
	/* table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	 insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";*/
	
	String GET_DATA="";
	String allRspCode = "";
	if(!generatettumBeanObj.getRespcode().equals("All")) {
		
		allRspCode =  "regexp_replace(t1.dcrs_remarks, '[^0-9]', '')= "+generatettumBeanObj.getRespcode()+" AND ";
	}else{
		
		allRspCode=" regexp_replace(t1.dcrs_remarks, '[^0-9]', '') <> 0  AND";
	}
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
		
		 GET_DATA =" Select distinct t1.CONTRA_ACCOUNT ACCTNUM,'99937200010020'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount,"
                 +" 'C-REV/'||to_char(to_date(t1.TRAN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.REF_NO ,-6 )||'/'||substr(t1.REF_NO,2,6)||'/'||regexp_replace(t1.dcrs_remarks, '[^0-9]', '') as TRANSACTIONPARTICULARS,"
               +"  remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,VALUE_DATE,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode"
                +"  from SETTLEMENT_cashnet_iss_CBS t1 "
                +" WHERE E='C' and regexp_replace(t1.dcrs_remarks, '[^0-9]', '')in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(VALUE_DATE,'DD-MM-rrrr')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				+" AND t1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_iss_CBS) "
			 	+" AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and t1.REMARKS=t3.remarks " //t3.REFERENCE_NUMBER
			 	+"  AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT "
                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
			
		
	} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
		
		 GET_DATA =" Select  distinct nvl(t1.ACCTNUM,null) ACCTNUM ,'99937200010020'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT ,',','') TransactionAmount,"
		 		+ " 'C-REV/'||to_char(to_date(t1.LOCAL_DATE ,'mm/dd/yyyy'),'ddmmyy')||'/'||substr(t1.LOCAL_TIME,1,6 )||'/'||substr(t1.TRACE ,2,6)||'/'||regexp_replace(t1.dcrs_remarks, '[^0-9]', '') as TRANSACTIONPARTICULARS,"
		 		+ " a1.pan as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.LOCAL_DATE,'MM/DD/YYYY'),'dd/mm/yyyy') as VALUE_DATE ,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode "
		 		+ "  from SETTLEMENT_cashnet_iss_switch t1 "
		 		+ " inner join  switch_rawdata a1 "
		 		+ "                 on a1.TRACE = t1.TRACE and "
		 		+ "                   a1.AMOUNT = t1.AMOUNT and"
		 		+ " substr(a1.PAN,1,6) =substr( t1.PAN,1,6 ) and"
		 		+ " substr(a1.PAN,-4) =substr( t1.PAN,-4 ) and"
		 		+ "                   a1.ACQUIRER =t1.ACQUIRER" 
		 		/*"inner join   ( SELECT DISTINCT REMARKS,CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%' ) t2"  //settlement_cashnet_iss_cbs t2 on   "
		 		+ " on trim(t1.PAN)= (t2.REMARKS)  "*/
		 		+" WHERE t1.LOCAL_DATE <> '00/00/0000' and   regexp_replace(t1.dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(t1.LOCAL_DATE,'mm/dd/yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				+" AND t1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_cashnet_iss_switch) "
				+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan)=t3.remarks " //t3.REFERENCE_NUMBER
		 	+"  AND to_number(t1.AMOUNT_EQUIV) = to_number(t3.TRANSACTION_AMOUNT) "
            +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
		
		
	}
	logger.info(GET_DATA);
	
	try{
	
	conn = getConnection();
	pstmt = conn.prepareStatement(GET_DATA);
	rset = pstmt.executeQuery();
	
	while (rset.next()) {
		String stTran_Particular = rset.getString("TRANSACTIONPARTICULARS");
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		
		/*String Account_num = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
				//rset.getString("CONTRA_ACCOUNT");
		//String acctnum = rset.getString("ACCTNUM");
		String acctnum = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT ltrim(SUBSTR(ACCTNUM , INSTR(ACCTNUM, ' ') + 1),0) ACCTNUM FROM  BK_STL_CASHNET_ISS_SWITCH where ACCTNUM is not null AND pan='"+rset.getString("referencenumber") +"'),'NA') from dual",  String.class);
		*/
		String acctnum=null;
		
		try {
		 /*acctnum =  getJdbcTemplate().queryForObject("select nvl( (select  distinct pan from cmsidbi.card_acct@DCMSLINK  "
				+ " where  CAP_PAN_CODE IN cmsidbi.CX_DF_AH@DCMSLINK ('"+rset.getString("referencenumber") +"')),'00000' ) CAM_ACCT_NO from dual", String.class);*/
			
			 //acctnum =  getJdbcTemplate().queryForObject("select GET_ACCOUNT_NO('"+rset.getString("referencenumber").trim()+"') from dual", String.class);
			acctnum = getAccountNo(rset.getString("referencenumber").trim());
			
		//SUBSTR(t2.ACCTNUM , INSTR(t2.ACCTNUM, ' ') + 1) ACCTNUM 
	}catch (Exception ex) {
			
			
		}
		
	
			if(acctnum!=null && !(acctnum.equals("00000"))) {
				
				generateBean.setStCreditAcc(acctnum);
			
			
			} else {
					//acctnum =  getJdbcTemplate().queryForObject("select contra_account from settlement_cashnet_iss_cbs where ) ", requiredType)
							String[] tran_part = stTran_Particular.split("\\/");
							logger.info("tran_parti is "+tran_part);
							logger.info(tran_part[3]);
							/*String get_man_acc = "select distinct t1.CONTRA_ACCOUNT,t2.ACCTNUM from cbs_rupay_rawdata t1 inner join "
								      + "switch_rawdata t2 on t1.REMARKS = t2.PAN and t1.FILEDATE = t2.FILEDATE and trunc(TO_NUMBER(REPLACE(T1.AMOUNT,',',''))) = trunc(TO_NUMBER(t2.AMOUNT_EQUIV)) and substr(t1.REF_NO,2,6) "
								      + "=substr(t2.TRACE,2,6)     "
								      + "where t1.filedate   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')   and t2.ACCTNUM is not null    and t2.PAN = '"+rset.getString("referencenumber")+"' "
								      //CONDITIONS ADDED BY INT8624 AS INCORRECT ACC WHERE FETCHED
								      +" AND SUBSTR(T2.TRACE,2,6) = '"+tran_part[3]+"' AND T2.AMOUNT = '"+rset.getString("TransactionAmount")+"'";*/
							//QUERY MODIFIED BY INT8624 AS ACC NUMBERS WERE INCORRECT
							String get_man_acc ="select CONTRA_ACCOUNT from cbs_rupay_rawdata where "
								      +"substr(ref_no,2,6) = '"+tran_part[3]+"' and remarks = '"+rset.getString("referencenumber")+"' and TO_NUMBER(REPLACE(AMOUNT,',','')) = "+rset.getString("TransactionAmount")
								      +" AND filedate   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')";
													
							logger.info("get_man_acc ::> "+  get_man_acc  );		
													
																					
								PreparedStatement pstmt_con = conn.prepareStatement(get_man_acc);
								ResultSet rset_con = pstmt_con.executeQuery();
								
								if(rset_con != null)
								{
									while(rset_con.next())
									{
										if(rset_con.getString("CONTRA_ACCOUNT").contains("78000010021")) {

											generateBean.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*","")); 

										}
										else {

											generateBean.setStCreditAcc(rset_con.getString("CONTRA_ACCOUNT").replaceAll(" ", " ")); 
										}

										//System.out.println("::::::::::::"+card_num+":::::::::::::::::"+stAccNum+"::::::::::::"+rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*","")+"::::::::::::::::::::::::::::::::::");			
									}
								}
								else
								{
									generateBean.setStCreditAcc(rset.getString("ACCTNUM").replaceAll(" ", " ").replaceAll("^0*",""));
									logger.info("rset_con is null");
								}
				
						
				
					}
		
		//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
		generateBean.setStDebitAcc(rset.getString("DRACC"));
		generateBean.setStAmount(rset.getString("TransactionAmount"));
		
		generateBean.setStTran_particulars(stTran_Particular);
		generateBean.setStCard_Number(rset.getString("referencenumber"));
		
		generateBean.setStDate(rset.getString("VALUE_DATE"));
		generateBean.setRespcode(rset.getString("respcode"));
		
		String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
		
		Date date= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String date2 =  sdf.format(date);
		remark= "CSNI"+date2+remark;
		generateBean.setStRemark(remark);
		
		TTUM_Data.add(generateBean);
	}
	//Data.add(Excel_header);	
	//Data.add(TTUM_Data);
	
	pstmt.close();
	
	//inserting data IN TTUM TABLE
	for(GenerateTTUMBean beanObj : TTUM_Data)
	{
		//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
		if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		

			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-rrrr'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
		
			
			getJdbcTemplate().execute(INSERT_DATA);
		
		
			
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-rrrr'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
			
		} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
			
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
			
			logger.info(INSERT_DATA);
			
			getJdbcTemplate().execute(INSERT_DATA);
			
			
	
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
				logger.info(INSERT_DATA);
			
			
		}
								
	}
	//SETTLEMENT_cashnet_iss_CBS
	String UPDATE_RECORDS="";
	
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
	UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
			" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
			+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+" ) and"
					+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
			" TO_DATE(VALUE_DATE,'DD-MM-rrrr')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
			" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
			" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
	} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) { 
		
		UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
				" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
				+" WHERE LOCAL_DATE <> '00/00/0000' and regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
						+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
				" TO_DATE(LOCAL_DATE,'mm/dd/YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
				" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
				" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
	
		
		
	}
	 TTUM_Data.clear();
	 
	 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
	 
	getJdbcTemplate().execute(UPDATE_RECORDS);
	
	String query="";
	
	
	 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
			+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  "
					+ " and regexp_replace(dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+" )"  ;
	
	
	logger.info(query);
	 pstmt = conn.prepareStatement(query);
	 rset = pstmt.executeQuery();
	
	
	while (rset.next()) {
		
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
		generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
		String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
		generateBean.setStTran_particulars(stTran_Particular);
		generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
		generateBean.setStDate(rset.getString("RECORDS_DATE"));
		generateBean.setStRemark(rset.getString("REMARKS"));
		generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
		
		
		TTUM_Data.add(generateBean);
	}
	
	
	Data.add(Excel_header);	
	Data.add(TTUM_Data);
	
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM End ****");
	
	
	return Data;
	}catch (Exception ex) {
		
		ex.printStackTrace();
		return Data;
		
		
	}


}






@Override
public List<Integer> getRespCode(String category, String subcategory,String filename,
		String filedate) throws Exception {
	logger.info("***** GenerateTTUMDaoImpl.getRespCode Start ****");
	List<Integer> respcodes = new ArrayList<Integer>();
	
	try{
	if(category.equals("CASHNET") ) {
		
		String query ="";
		if(subcategory.equals("ISSUER"))
	
		{
		 query = "Select distinct regexp_replace(dcrs_remarks, '[^0-9]', '') respCode from settlement_"+category+"_"+subcategory.substring(0, 3)+"_"+filename 
				+ " where  dcrs_remarks like('%(%') and to_number(regexp_replace(dcrs_remarks, '[^0-9]', '')) > 0 ";
		
		logger.info(query);
		}
		
		else if(subcategory.equals("ACQUIRER")) {
			
			 query = "Select distinct regexp_replace(dcrs_remarks, '[^0-9]', '') respCode from settlement_"+category+"_"+subcategory.substring(0, 3)+"_"+filename 
					+ " where  dcrs_remarks like('%(%')";
			
		}
		
		
		respcodes = 	getJdbcTemplate().queryForList(query, Integer.class);
		
		logger.info("respcodes=="+respcodes);
		
		logger.info("***** GenerateTTUMDaoImpl.getRespCode End ****");
	
	} else {
		
		String query ="";
		if(category.equals("VISA"))
	
		{
		/* query = "Select distinct regexp_replace(dcrs_remarks, '[^0-9]', '') respCode from settlement_"+category+"_"+filename 
				+ " where  dcrs_remarks like('%(%') and to_number(regexp_replace(dcrs_remarks, '[^0-9]', '')) > 0 ";*/
			
			query =	"SELECT DISTINCT translate(regexp_substr(os1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') from settlement_VISA_CBS os1 "+
			 " where os1.DCRS_REMARKS like '%VISA_ISS-UNRECON-1%' "+
								 "and translate(regexp_substr(os1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0')!=0";
		
		logger.info(query);
		
		respcodes = 	getJdbcTemplate().queryForList(query, Integer.class);
		}
	}
	}catch(Exception e)
		{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.getRespCode");
			logger.error(" error in GenerateTTUMDaoImpl.getRespCode", new Exception("GenerateTTUMDaoImpl.getRespCode",e));
			 throw e;
		}
		
	
	return respcodes;
	
	
}

@Override
public List<List<GenerateTTUMBean>> generateVISATTUM(
		GenerateTTUMBean generatettumBeanObj) throws Exception {

	logger.info("***** GenerateTTUMDaoImpl.generateVISATTUM Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
		return visaCBSTTUM(generatettumBeanObj);
		
	}
	
	
	else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")  ) {
		
		if( generatettumBeanObj.getInRec_Set_Id()== -1) {
		
			return visaSurchargeMatchedTTUM(generatettumBeanObj);
		
		} else if( generatettumBeanObj.getInRec_Set_Id()== 3) {
			
			return visaSurchargeUNMatchedTTUM(generatettumBeanObj);
			
		}
		
		
		
	}else {
		
			
			try
			{
				
				try
				{
					ExcelHeaders.add("ACCOUNT_NUMBER");
					ExcelHeaders.add("CURRENCY_CODE");
					ExcelHeaders.add("SERVICE_OUTLET");
					ExcelHeaders.add("PART_TRAN_TYPE");
					ExcelHeaders.add("TRANSACTION_AMOUNT");
					ExcelHeaders.add("TRANSACTION_PARTICULARS");
					ExcelHeaders.add("REFERENCE_NUMBER");
					ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
					ExcelHeaders.add("REFERENCE_AMOUNT");
					ExcelHeaders.add("REMARKS");
					ExcelHeaders.add("REPORT_CODE");
					
					
					
					generatettumBeanObj.setStExcelHeader(ExcelHeaders);
					Excel_header.add(generatettumBeanObj);
					
					for(int i = 0 ; i<ExcelHeaders.size();i++)
					{
						table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
						insert_cols = insert_cols+","+ExcelHeaders.get(i);
					}
					String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
					logger.info("check table "+CHECK_TABLE);
					int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					if(tableExist == 0)
					{
						//create temp table
						String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"("+table_cols+")";
						logger.info("CREATE QUERY IS "+query);
						getJdbcTemplate().execute(query);			
					}
					
					
					String GET_DATA="";
					if(generatettumBeanObj.getStFile_Name().equals("VISA")) {
						 GET_DATA =" Select distinct '78000010021' CONTRA_ACCOUNT ,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.DESTINATION_AMOUNT,',','') TransactionAmount,"
			                 +" 'LP/VISA/'||to_char(to_date(t1.PURCHASE_DATE,'MMDD'),'ddmm')||'/'||MERCHANT_NAME as TRANSACTIONPARTICULARS,"
			               +"  card_number as referencenumber ,'INR'REFERENCECURRENCYCODE, to_char(TO_DATE(PURCHASE_DATE,'MMDD'),'ddmmyy') as VALUE_DATE,MERCHANT_NAME,AUTHORIZATION_CODE,TC,to_char(t1.filedate,'ddmmyy') filedate"
			                +"  from SETTLEMENT_VISA_VISA t1 "
			               // + "  ( SELECT DISTINCT OS1.REMARKS,OS1.CONTRA_ACCOUNT FROM  CBS_rupay_RAWDATA OS1 where CONTRA_ACCOUNT is not null ) t2"
			               /* + " on t1.CARD_NUMBER = t2.remarks "*/
			                +" WHERE TC ='"+generatettumBeanObj.getVisaFunctionCode()+"' "
			                + " AND usage_code='1' "
			                + " AND t1.dcrs_remarks LIKE 'VISA_ISS-UNRECON-2%' AND "
			                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
							+" t1.filedate  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
							//+" AND t1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_VISA_VISA) "
						 	+" AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where " //ltrim(trim(ltrim(t2.remarks,'0')),'0' )=t3.ACCOUNT_NUMBER and 
						 			+ " t3.remarks = t1.card_number ||'/'||AUTHORIZATION_CODE " //t3.REFERENCE_NUMBER
						 	+"  AND replace(t1.DESTINATION_AMOUNT,',','') = t3.TRANSACTION_AMOUNT "
			                +"  AND  TO_DATE(to_char(RECORDS_DATE,'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  ) order by card_number asc ";
						
					}
					logger.info(GET_DATA);
					
				
					
					conn = getConnection();
					pstmt = conn.prepareStatement(GET_DATA);
					rset = pstmt.executeQuery();
					
					logger.info(rset.getRow());
					int count =0;
					
					while (rset.next()) {
						count++;
						System.out.println(count);
						GenerateTTUMBean generateBean = new GenerateTTUMBean();
						String CONTRA_ACCOUNT = rset.getString("CONTRA_ACCOUNT");
						if(CONTRA_ACCOUNT.equals("000000") || CONTRA_ACCOUNT.equals(null)) {
							
							generateBean.setStCreditAcc("NOT AVAILABLE");
							
						}else{
						generateBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
						
						}
						//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
						generateBean.setStDebitAcc(rset.getString("DRACC"));
						generateBean.setStAmount(rset.getString("TransactionAmount"));
						String stTran_Particular = rset.getString("TRANSACTIONPARTICULARS");
						generateBean.setStTran_particulars(stTran_Particular);
						generateBean.setStCard_Number(rset.getString("referencenumber"));
						generateBean.setMerchant_type(rset.getString("MERCHANT_NAME"));
		 				generateBean.setAuthnum(rset.getString("AUTHORIZATION_CODE"));
						generateBean.setTc(rset.getString("TC"));
						generateBean.setFiledate(rset.getString("FILEDATE"));
						
						generateBean.setStDate(rset.getString("VALUE_DATE"));
						
						String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
						
						Date date= new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
						String date2 =  sdf.format(date);
						remark= "VISI"+date2+remark;
						generateBean.setStRemark(remark);
						
						TTUM_Data.add(generateBean);
					}
					//Data.add(Excel_header);	
					//Data.add(TTUM_Data);
					
					pstmt.close();
					
					//inserting data IN TTUM TABLE
					ArrayList<String> Card_number = new ArrayList<String>();
					double totalamount=0.0;
					String stdate="";
					String filedate="";
					String stremark="";
					String merchant_type="";
					
					
					
					for(GenerateTTUMBean beanObj : TTUM_Data)
					{
						//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
						if(generatettumBeanObj.getStFile_Name().equals("VISA")) {
						
							String INSERT_DATA ="";
							if(generatettumBeanObj.getVisaFunctionCode().equals("05")|| generatettumBeanObj.getVisaFunctionCode().equals("07"))
						{
							 INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+  "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
									+ "VALUES('"+
									generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
													"',TO_DATE('"+beanObj.getFiledate()+"','DDMMYY'),'"+beanObj.getStDebitAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','LP/VISA/"+beanObj.getFiledate()+ "'"
															+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getMerchant_type().replace("'", "")+"',null)";
					
							 logger.info("INSERT_DATA=="+INSERT_DATA);
							 
							 getJdbcTemplate().execute(INSERT_DATA);
		
							INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
									generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getFiledate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars().replace("'", "")+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
							
							 logger.info("INSERT_DATA=="+INSERT_DATA);
							getJdbcTemplate().execute(INSERT_DATA);
							
							
							
						}
							
							else if(generatettumBeanObj.getVisaFunctionCode().equals("06")|| generatettumBeanObj.getVisaFunctionCode().equals("00"))
							{
								 
								
								stdate = beanObj.getStDate();
									filedate=beanObj.getFiledate();
									stremark = beanObj.getStRemark();
									merchant_type= beanObj.getMerchant_type();
										
										if(beanObj.getStCard_Number().equals(beanObj.getStCard_Number())) {
											
										
										INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
												generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getFiledate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','REF/VISA/"+beanObj.getStDate() +"/"+beanObj.getMerchant_type().replace("'", "")+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
										
										logger.info("INSERT_DATA=="+INSERT_DATA);	
										getJdbcTemplate().execute(INSERT_DATA);
										
										
										totalamount =totalamount + Double.parseDouble(beanObj.getStAmount());
										
										}
									
									
								
								//Card_number.add(beanObj.getStCard_Number());
								
								
								
								/*INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+  "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
										+ "VALUES('"+
										generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
														"',TO_DATE('"+beanObj.getFiledate()+"','DDMMYY'),'99934450010021','INR','999','D','"+totalamount+"','REF/VISA/"+beanObj.getStDate()+ "'"
																+ ",'"+beanObj.getStRemark()+"','INR','"+totalamount+"','"+beanObj.getMerchant_type().replace("'", "")+"/"+beanObj.getFiledate()+"',null)";
								
								 logger.info("INSERT_DATA=="+INSERT_DATA);
								 getJdbcTemplate().execute(INSERT_DATA);*/
								 
								
								
								
								
							}
							
							else if(generatettumBeanObj.getVisaFunctionCode().equals("25")|| generatettumBeanObj.getVisaFunctionCode().equals("26"))
							{
								 INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+  "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
											+ "VALUES('"+
											generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
															"',TO_DATE('"+beanObj.getFiledate()+"','DD-MM-YYYY'),'99937200010053','INR','999','D','"+beanObj.getStAmount()+"','REF/VISA/"+beanObj.getFiledate()+ "/"+beanObj.getMerchant_type().replace("'", "")+"'"
																	+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getMerchant_type().replace("'", "")+"',null)";
									
								 logger.info("INSERT_DATA=="+INSERT_DATA);
								 getJdbcTemplate().execute(INSERT_DATA);
		
									INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
											generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+beanObj.getFiledate()+"','DDMMYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','REF/VISA/"+beanObj.getFiledate()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"',"
													+ "'"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
									
									logger.info("INSERT_DATA=="+INSERT_DATA);
									getJdbcTemplate().execute(INSERT_DATA);
								
								
							}
						
							
						} 				
					}
					
					 if(generatettumBeanObj.getVisaFunctionCode().equals("06")|| generatettumBeanObj.getVisaFunctionCode().equals("00")) {
						
						 String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+  "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
									+ "VALUES('"+
									generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_UNRECON_"+generatettumBeanObj.getVisaFunctionCode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
													"',TO_DATE('"+filedate+"','DDMMYY'),'99934450010021','INR','999','D','"+totalamount+"','REF/VISA/"+stdate+ "'"
															+ ",'"+stremark+"','INR','"+totalamount+"','"+merchant_type.replace("'", "")+"/"+filedate+"',null)";
							
							 logger.info("INSERT_DATA=="+INSERT_DATA);
							 getJdbcTemplate().execute(INSERT_DATA);
						 
						
					}
					//SETTLEMENT_cashnet_iss_CBS
					String UPDATE_RECORDS="";
					
					if(generatettumBeanObj.getStFile_Name().equals("VISA")) {
					UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
							+" WHERE TC ='"+generatettumBeanObj.getVisaFunctionCode()+"' and dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
							"  FILEDATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') " ;
							//" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+")";
					
					}
					 TTUM_Data.clear();
					 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
					 
					getJdbcTemplate().execute(UPDATE_RECORDS);
					
					System.out.println("update account no");
					getupdatedAccountNo("");
					System.out.println("account no updated");
					
					String query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
							+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  "
									+ " and regexp_replace(dcrs_remarks, '[^0-9]', '') = "+generatettumBeanObj.getVisaFunctionCode()+""  ;
					
					logger.info(query);
					 pstmt = conn.prepareStatement(query);
					 rset = pstmt.executeQuery();
					
					
					while (rset.next()) {
						
						GenerateTTUMBean generateBean = new GenerateTTUMBean();
						generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
						generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
						String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
						generateBean.setStTran_particulars(stTran_Particular);
						generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
						generateBean.setStDate(rset.getString("RECORDS_DATE"));
						generateBean.setStRemark(rset.getString("REMARKS"));
						generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
						
						if(generateBean.getStCreditAcc().contains("78000010021")) {
							
							String rmrk[] = generateBean.getStRemark().split("/");
							/*String contraacc_query = "select distinct nvl(LTrim(t2.ACCTNUM,0),00000) "
									+ " from cbs_rupay_rawdata t1 inner join switch_rawdata t2 "
									+ "on t1.REMARKS = t2.PAN and t2.ACCTNUM is not null "
								      + "where t1.CONTRA_ACCOUNT = '"+generateBean.getStCreditAcc()+"' and t2.PAN = '"+rmrk[0]+"'"
								      		+ " and  t2.MSGTYPE in( '110')"
								      		+ " and to_number(t2.amount)>0 ";*/
						///changes done by sushant on date 11/jan/2019
							/*String contraacc_query ="select CAM_ACCT_NO  from cmsidbi.card_acct@DCMSLINK"
									+ "				 where  CAP_PAN_CODE IN cmsidbi.CX_DF_AH@DCMSLINK"
									+ "									 ('"+rmrk[0]+"')";*/
							
							/*String contraacc_query = "select nvl( (select  CAM_ACCT_NO from cmsidbi.card_acct@DCMSLINK  "
									+ " where  CAP_PAN_CODE IN cmsidbi.CX_DF_AH@DCMSLINK ('"+rmrk[0]+"')),'00000' ) CAM_ACCT_NO from dual";*/
							
							//String query2 ="select GET_ACCOUNT_NO('"+rmrk[0].trim()+"') from dual";
							//logger.info(query2);
							//String contraacc_query = getJdbcTemplate().queryForObject(query2, Long.class);
							
							//logger.info(contraacc_query);
							//Object contra_account = getJdbcTemplate().queryForObject(query2, Object.class);
							//Object contra_account = getJdbcTemplate().queryForInt(query2);
							String contra_account  = getAccountNo(rmrk[0].trim());
							if(contra_account.equals("00000")) {
								
								generateBean.setStCreditAcc("NOT AVAILABLE");
								
							} else {
							generateBean.setStCreditAcc(contra_account.toString());
						
							}
						}
						
						
						
						
						
						TTUM_Data.add(generateBean);
					}
					
					
					Data.add(Excel_header);	
					Data.add(TTUM_Data);
					
					logger.info("***** GenerateTTUMDaoImpl.generateVISATTUM End ****");
					
				}
				catch(Exception e)
				{
					demo.logSQLException(e, "GenerateTTUMDaoImpl.generateVISATTUM");
					logger.error(" error in GenerateTTUMDaoImpl.generateVISATTUM", new Exception("GenerateTTUMDaoImpl.generateVISATTUM",e));
					 throw e;
				}
				return Data;
		
		
				}
			catch(Exception e)
			{
				demo.logSQLException(e, "GenerateTTUMDaoImpl.generateVISATTUM");
				logger.error(" error in GenerateTTUMDaoImpl.generateVISATTUM", new Exception("GenerateTTUMDaoImpl.generateVISATTUM",e));
				// throw e;
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
		}
	return Data;


	

}


private List<List<GenerateTTUMBean>> visaSurchargeUNMatchedTTUM(
		GenerateTTUMBean generatettumBeanObj) throws Exception {

	logger.info("***** GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		try
		{
			ExcelHeaders.add("ACCOUNT_NUMBER");
			ExcelHeaders.add("CURRENCY_CODE");
			ExcelHeaders.add("SERVICE_OUTLET");
			ExcelHeaders.add("PART_TRAN_TYPE");
			ExcelHeaders.add("TRANSACTION_AMOUNT");
			ExcelHeaders.add("TRANSACTION_PARTICULARS");
			ExcelHeaders.add("REFERENCE_NUMBER");
			ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
			ExcelHeaders.add("REFERENCE_AMOUNT");
			ExcelHeaders.add("REMARKS");
			ExcelHeaders.add("REPORT_CODE");
			
			
			
			generatettumBeanObj.setStExcelHeader(ExcelHeaders);
			Excel_header.add(generatettumBeanObj);
			
			for(int i = 0 ; i<ExcelHeaders.size();i++)
			{
				table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+ExcelHeaders.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_VISA_UNMATCHED_SURCHARGE'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_VISA_UNMATCHED_SURCHARGE("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			
			String GET_DATA="";
			if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
				
				if(Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) >0) {
				 GET_DATA =" Select distinct nvl(trim(ltrim(t1.ACCTNUM,'0')),'NOT AVAILABLE') CONTRA_ACCOUNT ,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT_EQUIV ,',','') TransactionAmount,"
				 		+ "                   pan as referencenumber ,'INR'REFERENCECURRENCYCODE, to_char(TO_DATE(t1.LOCAL_DATE ,'mm/dd/yyyy'),'dd/mm/yyyy') as VALUE_DATE, t1.ACCEPTORNAME ,t1.AUTHNUM ,to_char(t1.filedate,'ddmmyy') filedate ,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,substr(t1.Pan,1,6) BIN,MERCHANT_TYPE ,ACQ_CURRENCY_CODE "
				 		+ "	                  from SETTLEMENT_VISA_SWITCH t1 "
				 		+ "	                 WHERE    t1.dcrs_remarks LIKE 'VISA_SUR-UNRECON-3' and    (t1.DCRS_REMARKS not LIKE '%TTUM%') "
				 		+"		and t1.FILEDATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+ " AND   to_number (DIFF_AMOUNT) > 0 AND "
					+ "not exists(select * from TTUM_VISA_UNMATCHED_SURCHARGE t3 where (trim(ltrim(t1.ACCTNUM,'0')))=t3.ACCOUNT_NUMBER and t1.pan=t3.remarks "
					+ "  AND replace(t1.AMOUNT_EQUIV ,',','') = t3.TRANSACTION_AMOUNT  "
					+ "  AND  TO_DATE(to_char(RECORDS_DATE,'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') ) ";
				 
				} else if  (Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) < 0) {
					
					 GET_DATA =" Select distinct nvl(trim(ltrim(t1.ACCTNUM,'0')),'NOT AVAILABLE') CONTRA_ACCOUNT ,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT_EQUIV ,',','') TransactionAmount,"
						 		+ "                   pan as referencenumber ,'INR'REFERENCECURRENCYCODE, to_char(TO_DATE(t1.LOCAL_DATE ,'mm/dd/yyyy'),'dd/mm/yyyy') as VALUE_DATE, t1.ACCEPTORNAME ,t1.AUTHNUM ,to_char(t1.filedate,'ddmmyy') filedate ,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,substr(t1.Pan,1,6) BIN,MERCHANT_TYPE ,ACQ_CURRENCY_CODE "
						 		+ "	                  from SETTLEMENT_VISA_SWITCH t1 "
						 		+ "	                 WHERE    t1.dcrs_remarks LIKE 'VISA_SUR-UNRECON-3' and    (t1.DCRS_REMARKS not LIKE '%TTUM%') "
						 		+"		and t1.FILEDATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
							+ " AND   to_number (DIFF_AMOUNT) < 0 AND "
							+ "not exists(select * from TTUM_VISA_UNMATCHED_SURCHARGE t3 where (trim(ltrim(t1.ACCTNUM,'0')))=t3.ACCOUNT_NUMBER and t1.pan=t3.remarks "
							+ "  AND replace(t1.AMOUNT_EQUIV ,',','') = t3.TRANSACTION_AMOUNT  "
							+ "  AND  TO_DATE(to_char(RECORDS_DATE,'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') ) ";
						 
				}
				 //t1.MERCHANT_TYPE in('5541', '5542', '5983','5172')  AND 
			}
			logger.info(GET_DATA);
			
		
			
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			
			logger.info(rset.getRow());
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				String CONTRA_ACCOUNT =  getJdbcTemplate().queryForObject("select nvl( (select  CAM_ACCT_NO from cmsidbi.card_acct@DCMSLINK  where  CAP_PAN_CODE IN cmsidbi.CX_DF_AH@DCMSLINK ('"+rset.getString("referencenumber")+"')"
						+ " ) ,'00000' ) CAM_ACCT_NO from dual" ,String.class);
						
						/*rset.getString("CONTRA_ACCOUNT");
				if(CONTRA_ACCOUNT.equals("") || CONTRA_ACCOUNT.equals(null)) {
					
					generateBean.setStCreditAcc("NOT AVAILABLE");
					
				}else{
				generateBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
				
				}*/
				//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
				generateBean.setStCreditAcc(CONTRA_ACCOUNT);
				generateBean.setStDebitAcc(rset.getString("DRACC"));
				generateBean.setStAmount(rset.getString("TransactionAmount"));
				
				generateBean.setStCard_Number(rset.getString("referencenumber"));
				System.out.println(rset.getString("MERCHANT_TYPE"));
				generateBean.setMerchant_type(rset.getString("MERCHANT_TYPE"));
				generateBean.setAcceptorname(rset.getString("ACCEPTORNAME"));
 				generateBean.setAuthnum(rset.getString("AUTHNUM"));
				
 				generateBean.setCbs_amount(rset.getString("CBS_AMOUNT"));
 				generateBean.setDIFF_AMOUNT(rset.getString("DIFF_AMOUNT"));
				generateBean.setFiledate(rset.getString("FILEDATE"));
				generateBean.setPan(rset.getString("BIN"));
				generateBean.setAcq_currency_code(rset.getString("ACQ_CURRENCY_CODE"));
				
				//logger.info(rset.getString("VALUE_DATE"));
				generateBean.setStDate(rset.getString("VALUE_DATE"));
				
				String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
				
				Date date= new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				String date2 =  sdf.format(date);
				remark= "VISI"+date2+remark;
				generateBean.setStRemark(remark);
				
				TTUM_Data.add(generateBean);
			}
			//Data.add(Excel_header);	
			//Data.add(TTUM_Data);
			
			pstmt.close();
			
			String panlist=null;
			String authnumList=null;
			float total_amount=0;
			String  recondate="",stdate="",acceptoname,ttum_remarks = null;
			
			/*INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") "
			+ "VALUES('"+
			generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
							"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
									+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getAcceptorname()+"',null)";
	 
	logger.info("INSERT_DATA=="+INSERT_DATA);
	getJdbcTemplate().execute(INSERT_DATA);*/
			
			
			
			//inserting data IN TTUM TABLE
			for(GenerateTTUMBean beanObj : TTUM_Data)
			{
				//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
				
						
					String INSERT_DATA ="";
					double cbs_amount = Double.parseDouble(beanObj.getCbs_amount().replace(",", ""));
					double diff_amount = Double.parseDouble(beanObj.getDIFF_AMOUNT().replace(",", ""));
					
					if(Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) >0)
					{
						recondate = beanObj.getFiledate();
						stdate=beanObj.getStDate();
						ttum_remarks = beanObj.getStRemark();
						
						if (beanObj.getMerchant_type().equals("5541") || beanObj.getMerchant_type().equals("5542") || beanObj.getMerchant_type().equals("5983") 
							|| beanObj.getMerchant_type().equals("5172") 	)  {
							
								if((beanObj.getPan().equals("432098")||beanObj.getPan().equals("458778")|| beanObj.getPan().equals("421491")||beanObj.getPan().equals("472258")))
								
								{ 
									if(Double.parseDouble(beanObj.getStAmount())>400 &&
											Double.parseDouble(beanObj.getStAmount())<4000	) {
										
										
									
										/*INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
											generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"/99987750010094','INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getFiledate()+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
					*/
										INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
												generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),'99987750010094','INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";


										//									Common credit lake.
										total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
									
									logger.info("INSERT_DATA=="+INSERT_DATA);
									getJdbcTemplate().execute(INSERT_DATA);
										
										
									}  else {
										
										
										INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
												generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),"+beanObj.getStCreditAcc()+",'INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate()+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
											
										logger.info("INSERT_DATA=="+INSERT_DATA);
										getJdbcTemplate().execute(INSERT_DATA);
										
										total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
											
									}
									
									
									 
									
								} else if((beanObj.getPan().equals("472259")||beanObj.getPan().equals("432099")|| beanObj.getPan().equals("458118"))) {
									
									if(Double.parseDouble(beanObj.getStAmount())>400 &&
											Double.parseDouble(beanObj.getStAmount())<4000	) {
										
										
									
										
									INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
											generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),'99987750010093','INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
										
									logger.info("INSERT_DATA=="+INSERT_DATA);
									getJdbcTemplate().execute(INSERT_DATA);
									
									total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
										
										/*INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") "
												+ "VALUES('"+
												generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
																"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
																		+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getAcceptorname()+"',null)";
										 
										logger.info("INSERT_DATA=="+INSERT_DATA);
										getJdbcTemplate().execute(INSERT_DATA);*/
									} else {
										
										
										INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
												generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),"+beanObj.getStCreditAcc()+",'INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/","")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
											
										logger.info("INSERT_DATA=="+INSERT_DATA);
										getJdbcTemplate().execute(INSERT_DATA);
										
										total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
											
									}
									
								} else if((beanObj.getPan().equals("400815"))) {
									
									if(Double.parseDouble(beanObj.getStAmount())>400 &&
											Double.parseDouble(beanObj.getStAmount())<4000	) {
										
										
									INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
											generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),'99987750010147','INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getAcceptorname()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
										
									logger.info("INSERT_DATA=="+INSERT_DATA);
									getJdbcTemplate().execute(INSERT_DATA);
										
									total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
										
										/*INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") "
												+ "VALUES('"+
												generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
																"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
																		+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getAcceptorname()+"',null)";
										 
										logger.info("INSERT_DATA=="+INSERT_DATA);
										getJdbcTemplate().execute(INSERT_DATA);*/
									} else {
										
										
										INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
												generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),"+beanObj.getStCreditAcc()+",'INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
											
										logger.info("INSERT_DATA=="+INSERT_DATA);
										getJdbcTemplate().execute(INSERT_DATA);
										
										total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
											
									}
									
									
									
								} else {
									
									
									INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
											generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),"+beanObj.getStCreditAcc()+",'INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
										
									logger.info("INSERT_DATA=="+INSERT_DATA);
									getJdbcTemplate().execute(INSERT_DATA);
									
									total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
										
								}
					} else {
						
						
						INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
								"',TO_DATE('"+beanObj.getFiledate()+"','DDMMRR'),"+beanObj.getStCreditAcc()+",'INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getStDate().replace("/", "")+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
							
						logger.info("INSERT_DATA=="+INSERT_DATA);
						getJdbcTemplate().execute(INSERT_DATA);
						
						total_amount = total_amount+Float.parseFloat(beanObj.getDIFF_AMOUNT());
							
					}
			
						
					}
					
					else if(Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) <0)
					{
						
						if(beanObj.getAcq_currency_code().equals("356"))
						{
							if(panlist==null && authnumList==null){
								
								panlist= beanObj.getStCard_Number();
								
								authnumList = beanObj.getAuthnum();
							}else{
							
								panlist =panlist +","+ beanObj.getStCard_Number();
								
								authnumList =authnumList+","+ beanObj.getAuthnum();
								
						
							}
							
							INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
									generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+generatettumBeanObj.getFiledate()+"','DD/MM/YYYY'),' 99937200010089','INR','999','D','"+beanObj.getStAmount()+"',NULL,"
											+ "'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
								
							logger.info("INSERT_DATA=="+INSERT_DATA);
							getJdbcTemplate().execute(INSERT_DATA);
						
					 INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") "
							+ "VALUES('"+
							generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+generatettumBeanObj.getFiledate()+"','DD/MM/YYYY'),'"+generatettumBeanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"',NULL"
													+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
					 
					 logger.info("INSERT_DATA=="+INSERT_DATA);
					 getJdbcTemplate().execute(INSERT_DATA);
						
						}
						
						
					}
				
				
					
				} 		
			
			if(Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) >0) {
			
				String INSERT_DATA = "INSERT INTO TTUM_VISA_UNMATCHED_SURCHARGE ("+insert_cols+") "
			+ "VALUES('"+
			generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
							"',TO_DATE('"+recondate+"','DDMMRR'),'99937200010089','INR','999','C','"+total_amount+"','DR VISA/T&S/"+stdate+ "'"
									+ ",'"+ttum_remarks+"','INR','"+total_amount+"','VISA-"+recondate+"',null)";
	 
				logger.info("INSERT_DATA=="+INSERT_DATA);
				getJdbcTemplate().execute(INSERT_DATA);
				
				
				String UPDATE_RECORDS ="";
				
					 UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'SUR-UNRECON','SUR-UNRECON-GENERATED-TTUM')"
							+" WHERE  dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and local_date<>'00/00/0000' and  to_number (DIFF_AMOUNT) > 0 " +
							" AND Filedate   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') ";
					
					
					 TTUM_Data.clear();
					 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
					getJdbcTemplate().execute(UPDATE_RECORDS);
	
			} else if(Integer.parseInt(generatettumBeanObj.getDIFF_AMOUNT()) < 0) { 
				
				
				String UPDATE_RECORDS ="";
				
				 UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
						" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'SUR-UNRECON','SUR-UNRECON-GENERATED-TTUM')"
						+" WHERE  dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and local_date<>'00/00/0000' and  to_number (DIFF_AMOUNT) < 0 " +
						" AND Filedate  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') ";
				
				
				 TTUM_Data.clear();
				 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
				getJdbcTemplate().execute(UPDATE_RECORDS);
				
			}
			
			//SETTLEMENT_cashnet_iss_CBS
			String UPDATE_RECORDS="";
			
			
			
			String query="select * from TTUM_VISA_UNMATCHED_SURCHARGE where "
					+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  "  ;
			
			logger.info(query);
			 pstmt = conn.prepareStatement(query);
			 rset = pstmt.executeQuery();
			
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
				generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
				String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
				generateBean.setStDate(rset.getString("RECORDS_DATE"));
				generateBean.setStRemark(rset.getString("REMARKS"));
				generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
				
				
				TTUM_Data.add(generateBean);
			}
			
			
			Data.add(Excel_header);	
			Data.add(TTUM_Data);
			
			logger.info("***** GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM End ****");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			demo.logSQLException(e, "GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM");
			logger.error(" error in GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM", new Exception("GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM",e));
			 throw e;
		}
		return Data;


		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM", new Exception("GenerateTTUMDaoImpl.visaSurchargeUNMatchedTTUM",e));
		// throw e;
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


private List<List<GenerateTTUMBean>> visaSurchargeMatchedTTUM(
		GenerateTTUMBean generatettumBeanObj) throws Exception {
	
	logger.info("***** GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM Start ****");
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try
	{
		
		try
		{
			ExcelHeaders.add("ACCOUNT_NUMBER");
			ExcelHeaders.add("CURRENCY_CODE");
			ExcelHeaders.add("SERVICE_OUTLET");
			ExcelHeaders.add("PART_TRAN_TYPE");
			ExcelHeaders.add("TRANSACTION_AMOUNT");
			ExcelHeaders.add("TRANSACTION_PARTICULARS");
			ExcelHeaders.add("REFERENCE_NUMBER");
			ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
			ExcelHeaders.add("REFERENCE_AMOUNT");
			ExcelHeaders.add("REMARKS");
			ExcelHeaders.add("REPORT_CODE");
			
			
			
			generatettumBeanObj.setStExcelHeader(ExcelHeaders);
			Excel_header.add(generatettumBeanObj);
			
			for(int i = 0 ; i<ExcelHeaders.size();i++)
			{
				table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+ExcelHeaders.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_VISA_MATCHED_SURCHARGE'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_VISA_MATCHED_SURCHARGE("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			
			String GET_DATA="";
			if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
				 GET_DATA =" Select distinct nvl(trim(ltrim(t1.ACCTNUM,'0')),'NOT AVAILABLE') CONTRA_ACCOUNT ,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT_EQUIV ,',','') TransactionAmount,"
				 		+ "                   pan as referencenumber ,'INR'REFERENCECURRENCYCODE, to_char(TO_DATE(t1.LOCAL_DATE ,'mm/dd/yyyy'),'dd/mm/yyyy') as VALUE_DATE, t1.ACCEPTORNAME ,t1.AUTHNUM ,to_char(t1.filedate,'ddmmyy') filedate ,t1.CBS_AMOUNT,t1.DIFF_AMOUNT "
				 		+ "	                  from SETTLEMENT_VISA_SWITCH t1 "
				 		+ "	                 WHERE    t1.dcrs_remarks LIKE 'VISA_SUR-MATCHED-3' AND    (t1.DCRS_REMARKS not LIKE '%TTUM%') "
				 		/*+ "		 TO_DATE(t1.LOCAL_DATE ,'mm/dd/yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "*/
					+ " AND t1.FILEDATE = to_date('"+generatettumBeanObj.getStDate()+"','dd/mm/yyyy')"
					+ " AND  not exists(select * from TTUM_VISA_MATCHED_SURCHARGE t3 where (trim(ltrim(t1.ACCTNUM,'0')))=t3.ACCOUNT_NUMBER and t1.pan=t3.remarks "
					+ "  AND replace(t1.AMOUNT_EQUIV ,',','') = t3.TRANSACTION_AMOUNT "
					+ "  AND  TO_DATE(to_char(RECORDS_DATE,'dd/mm/yyyy'),'dd/mm/yyyy') = to_date('"+generatettumBeanObj.getStDate()+"','dd/mm/yyyy') )";

			}
			logger.info(GET_DATA);
			
		
			
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			
			logger.info(rset.getRow());
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				String CONTRA_ACCOUNT = rset.getString("CONTRA_ACCOUNT");
				if(CONTRA_ACCOUNT.equals("") || CONTRA_ACCOUNT.equals(null)) {
					
					generateBean.setStCreditAcc("NOT AVAILABLE");
					
				}else{
				generateBean.setStCreditAcc(rset.getString("CONTRA_ACCOUNT"));
				
				}
				
				generateBean.setStDebitAcc(rset.getString("DRACC"));
				generateBean.setStAmount(rset.getString("TransactionAmount"));
				
				generateBean.setStCard_Number(rset.getString("referencenumber"));
				generateBean.setMerchant_type(rset.getString("ACCEPTORNAME"));
 				generateBean.setAuthnum(rset.getString("AUTHNUM"));
				//generateBean.setTc(rset.getString("TC"));
 				generateBean.setCbs_amount(rset.getString("CBS_AMOUNT"));
 				generateBean.setDIFF_AMOUNT(rset.getString("DIFF_AMOUNT"));
				generateBean.setFiledate(rset.getString("FILEDATE"));
				
				generateBean.setStDate(rset.getString("VALUE_DATE"));
				
				String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
				
				Date date= new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				String date2 =  sdf.format(date);
				remark= "VISI"+date2+remark;
				generateBean.setStRemark(remark);
				
				TTUM_Data.add(generateBean);
			}
			//Data.add(Excel_header);	
			//Data.add(TTUM_Data);
			
			pstmt.close();
			
			//inserting data IN TTUM TABLE
			for(GenerateTTUMBean beanObj : TTUM_Data)
			{
				//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
				
				
					String INSERT_DATA ="";
					double cbs_amount = Double.parseDouble(beanObj.getCbs_amount().replace(",", ""));
					double diff_amount = Double.parseDouble(beanObj.getDIFF_AMOUNT().replace(",", ""));
					
					if(cbs_amount>diff_amount)
				{
						
						INSERT_DATA = "INSERT INTO TTUM_VISA_MATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
								"',TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY'),'999877500100083','INR','999','D','"+beanObj.getDIFF_AMOUNT()+"','DR VISA/T&S/"+beanObj.getFiledate()+"/"+beanObj.getMerchant_type()+"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
							
						logger.info("INSERT_DATA=="+INSERT_DATA);
						getJdbcTemplate().execute(INSERT_DATA);
						
						
					 INSERT_DATA = "INSERT INTO TTUM_VISA_MATCHED_SURCHARGE ("+insert_cols+") "
							+ "VALUES('"+
							generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
													+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getMerchant_type()+"',null)";
					 
					 logger.info("INSERT_DATA=="+INSERT_DATA);
					 getJdbcTemplate().execute(INSERT_DATA);

					 logger.info(
					 "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+  "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
								+ "VALUES('"+
								generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
														+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+generatettumBeanObj.getStDate()+"',null)");
						
					
					
					
				}
					
					if(cbs_amount<diff_amount)
					{
						
						double balance_amount = diff_amount - cbs_amount;

						INSERT_DATA = "INSERT INTO TTUM_VISA_MATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
								"',TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY'),'999877500100083','INR','999','D','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getValue_date()+"/"+beanObj.getMerchant_type()+"',"
										+ "'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
							
						logger.info("INSERT_DATA=="+INSERT_DATA);
						getJdbcTemplate().execute(INSERT_DATA);
						
							INSERT_DATA = "INSERT INTO TTUM_VISA_MATCHED_SURCHARGE ("+insert_cols+") VALUES('"+
									generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY'),'"+generatettumBeanObj.getStCreditAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getValue_date()+"/"+beanObj.getMerchant_type()+"',"
											+ "'"+beanObj.getStRemark()+"','INR','"+balance_amount+"','"+beanObj.getStCard_Number()+"/"+beanObj.getAuthnum()+"',null)";
								
							logger.info("INSERT_DATA=="+INSERT_DATA);
							getJdbcTemplate().execute(INSERT_DATA);
						
					 INSERT_DATA = "INSERT INTO TTUM_VISA_MATCHED_SURCHARGE ("+insert_cols+") "
							+ "VALUES('"+
							generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_SURCHARGE_TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
											"',TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY'),'99937200010089','INR','999','C','"+beanObj.getStAmount()+"','DR VISA/T&S/"+beanObj.getFiledate() + "'"
													+ ",'"+beanObj.getStRemark()+"','INR','"+beanObj.getDIFF_AMOUNT()+"','"+beanObj.getMerchant_type()+"',null)";
					 
					 logger.info("INSERT_DATA=="+INSERT_DATA);
					 getJdbcTemplate().execute(INSERT_DATA);
						
						
						
					}
				
				
					
				} 				
			
			//SETTLEMENT_cashnet_iss_CBS
			String UPDATE_RECORDS="";
			
			if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
			UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
					" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'SUR-MATCHED','SUR-MATCHED-GENERATED-TTUM')"
					+" WHERE  dcrs_remarks not like '%MATCHED-GENERATED-TTUM%'  and local_date<>'00/00/0000' " +
					" AND FILEDATE = TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY')";
			
			}
			 TTUM_Data.clear();
			 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
			getJdbcTemplate().execute(UPDATE_RECORDS);
			
			String query="select * from TTUM_VISA_MATCHED_SURCHARGE where "
					+ " RECORDS_DATE  =TO_DATE('"+generatettumBeanObj.getStDate()+"','DD/MM/YYYY')  "  ;
			
			logger.info(query);
			 pstmt = conn.prepareStatement(query);
			 rset = pstmt.executeQuery();
			
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
				generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
				String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
				generateBean.setStDate(rset.getString("RECORDS_DATE"));
				generateBean.setStRemark(rset.getString("REMARKS"));
				generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
				
				
				TTUM_Data.add(generateBean);
			}
			
			
			Data.add(Excel_header);	
			Data.add(TTUM_Data);
			
			logger.info("***** GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM");
			logger.error(" error in GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM", new Exception("GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM",e));
			 throw e;
		}
		return Data;


		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM", new Exception("GenerateTTUMDaoImpl.visaSurchargeMatchedTTUM",e));
		// throw e;
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


private List<List<GenerateTTUMBean>> visaCBSTTUM(
		GenerateTTUMBean generatettumBeanObj) throws Exception {
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	ResultSet rset=null;
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	PreparedStatement  pstmt= null;
	 ExcelHeaders = new ArrayList<>();
	Connection conn= null;
	ExcelHeaders.add("ACCOUNT_NUMBER");
	ExcelHeaders.add("CURRENCY_CODE");
	ExcelHeaders.add("SERVICE_OUTLET");
	ExcelHeaders.add("PART_TRAN_TYPE");
	ExcelHeaders.add("TRANSACTION_AMOUNT");
	ExcelHeaders.add("TRANSACTION_PARTICULARS");
	ExcelHeaders.add("REFERENCE_NUMBER");
	ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
	ExcelHeaders.add("REFERENCE_AMOUNT");
	ExcelHeaders.add("REMARKS");
	
	
	
	generatettumBeanObj.setStExcelHeader(ExcelHeaders);
	Excel_header.add(generatettumBeanObj);
	
	for(int i = 0 ; i<ExcelHeaders.size();i++)
	{
		table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
		insert_cols = insert_cols+","+ExcelHeaders.get(i);
	}
	
	
	
	
	
	 
	/* table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	 insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";*/
	
	String GET_DATA="";
	String allRspCode = "";
	if(!generatettumBeanObj.getRespcode().equals("All")) {
		
		allRspCode =  "regexp_replace(t1.dcrs_remarks, '[^0-9]', '')= "+generatettumBeanObj.getRespcode()+" AND ";
	}else{
		
		allRspCode=" regexp_replace(t1.dcrs_remarks, '[^0-9]', '') <> 0  AND";
	}
	
	if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		
		
		 GET_DATA =" Select distinct t1.CONTRA_ACCOUNT,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount,"
                 +" 'C-REV/'||to_char(to_date(t1.TRAN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'|| t1.particularals  as TRANSACTIONPARTICULARS,"
               +"  remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,VALUE_DATE,translate(regexp_substr(t1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') respcode,ref_no"
                +"  from SETTLEMENT_VISA_CBS t1 "
                +" WHERE E='C' and translate(regexp_substr(t1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(VALUE_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				+" AND t1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_VISA_CBS) "
			 	+" AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and t1.REMARKS= REGEXP_SUBSTR(t3.remarks, '[^/]+') " //t3.REFERENCE_NUMBER
			 	+"  AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT "
                +"  AND to_date(t3.RECORDS_DATE,'dd/mm/rrrr') BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/rrrr') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/rrrr')  )";
			
		
	} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
		
		 GET_DATA =" Select  distinct nvl(t1.ACCTNUM,null) ACCTNUM ,'99937200010089'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(t1.AMOUNT_EQUIV ,',','') TransactionAmount,"
		 		+ " 'C-REV/'||to_char(to_date(t1.LOCAL_DATE ,'mm/dd/yyyy'),'ddmmyy')||'/'||substr(t1.LOCAL_TIME,1,6 )||'/'||substr(t1.TRACE ,2,6)||'/'||regexp_replace(t1.dcrs_remarks, '[^0-9]', '') as TRANSACTIONPARTICULARS,"
		 		+ " pan as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.LOCAL_DATE,'MM/DD/YYYY'),'dd/mm/yyyy') as VALUE_DATE ,translate(regexp_substr(DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') respcode "
		 		+ "  from SETTLEMENT_VISA_switch t1 " 
		 		/*"inner join   ( SELECT DISTINCT REMARKS,CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%' ) t2"  //settlement_cashnet_iss_cbs t2 on   "
		 		+ " on trim(t1.PAN)= (t2.REMARKS)  "*/
		 		+" WHERE t1.LOCAL_DATE <> '00/00/0000' and   translate(regexp_substr(t1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') in ( "+generatettumBeanObj.getRespcode()+") AND"
                + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
				+" TO_DATE(LOCAL_DATE,'mm/dd/yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				+" AND t1.FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_VISA_switch) "
				+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan)=t3.remarks " //t3.REFERENCE_NUMBER
		 	+"  AND to_number(t1.AMOUNT_EQUIV) = to_number(t3.TRANSACTION_AMOUNT) "
            +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
		
		
	}
	logger.info(GET_DATA);
	
	try{
	
	conn = getConnection();
	pstmt = conn.prepareStatement(GET_DATA);
	rset = pstmt.executeQuery();
	
	while (rset.next()) {
		
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		
		
		
		String acctnum =  getJdbcTemplate().queryForObject("select nvl( (select  CAM_ACCT_NO from cmsidbi.card_acct@DCMSLINK  "
				+ " where  CAP_PAN_CODE IN cmsidbi.CX_DF_AH@DCMSLINK ('"+rset.getString("referencenumber") +"')),'00000' ) CAM_ACCT_NO from dual", String.class);
		
			if(acctnum!=null && !(acctnum.equals("00000"))) {
				
				generateBean.setStCreditAcc(acctnum);
			
			
			} else {
				generateBean.setStCreditAcc("NOT AVAILABLE"); 
				
					}
		generateBean.setStDebitAcc(rset.getString("DRACC"));
		generateBean.setStAmount(rset.getString("TransactionAmount"));
		String stTran_Particular = rset.getString("TRANSACTIONPARTICULARS");
		generateBean.setStTran_particulars(stTran_Particular);
		generateBean.setStCard_Number(rset.getString("referencenumber")+"/"+rset.getString("REF_NO").substring(2, 6));
		//System.out.println(rset.getString("referencenumber")+"/"+rset.getString("REF_NO").substring(2, 6));
		
		
		generateBean.setStDate(rset.getString("VALUE_DATE"));
		generateBean.setRespcode(rset.getString("respcode"));
		
		String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
		
		Date date= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String date2 =  sdf.format(date);
		remark= "VISI"+date2+remark;
		generateBean.setStRemark(remark);
		
		TTUM_Data.add(generateBean);
	}
	//Data.add(Excel_header);	
	//Data.add(TTUM_Data);
	
	pstmt.close();
	
	//inserting data IN TTUM TABLE
	for(GenerateTTUMBean beanObj : TTUM_Data)
	{
		//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
		if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
		

			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
		
			
			getJdbcTemplate().execute(INSERT_DATA);
		
		
			
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','DD-MM-YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
			
		} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) {
			
			String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
					+ "VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
			
			logger.info(INSERT_DATA);
			
			getJdbcTemplate().execute(INSERT_DATA);
			
			
	
			INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
					generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"_UNRECON-"+beanObj.getRespcode()+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
					"',TO_DATE('"+beanObj.getStDate()+"','dd/mm/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
					"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
				getJdbcTemplate().execute(INSERT_DATA);
				
				logger.info(INSERT_DATA);
			
			
		}
								
	}
	//SETTLEMENT_cashnet_iss_CBS
	String UPDATE_RECORDS="";
	
	

	UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+
			" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
			+" WHERE  translate(regexp_substr(DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') in( "+generatettumBeanObj.getRespcode()+" ) and"
					+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
			" TO_DATE(VALUE_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
			" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
			" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStFile_Name()+")";
	
	 TTUM_Data.clear();
	 
	 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
	 
	getJdbcTemplate().execute(UPDATE_RECORDS);
	
	String query="";
	
	
	 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
			+ " to_date(RECORDS_DATE,'dd/mm/rrrr')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  "
					+ " and (regexp_replace(dcrs_remarks, '[^0-9]', '')) in ( "+generatettumBeanObj.getRespcode()+" )"  ;
	
	
	logger.info(query);
	 pstmt = conn.prepareStatement(query);
	 rset = pstmt.executeQuery();
	
	
	while (rset.next()) {
		
		GenerateTTUMBean generateBean = new GenerateTTUMBean();
		generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
		generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
		String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
		generateBean.setStTran_particulars(stTran_Particular);
		generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
		generateBean.setStDate(rset.getString("RECORDS_DATE"));
		generateBean.setStRemark(rset.getString("REMARKS"));
		generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
		
		
		TTUM_Data.add(generateBean);
	}
	
	
	Data.add(Excel_header);	
	Data.add(TTUM_Data);
	
	logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM End ****");
	
	
	return Data;
	}catch (Exception ex) {
		
		ex.printStackTrace();
		return Data;
		
		
	}


}

/*@Override
public List<List<GenerateTTUMBean>> generateNFSTTUM(
		GenerateTTUMBean generatettumBeanObj) {
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generateNFSTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	
	try{
	
	try
	{


		
		
		try
		{
			ExcelHeaders.add("ACCOUNT_NUMBER");
			ExcelHeaders.add("CURRENCY_CODE");
			ExcelHeaders.add("SERVICE_OUTLET");
			ExcelHeaders.add("PART_TRAN_TYPE");
			ExcelHeaders.add("TRANSACTION_AMOUNT");
			ExcelHeaders.add("TRANSACTION_PARTICULARS");
			ExcelHeaders.add("REFERENCE_NUMBER");
			ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
			ExcelHeaders.add("REFERENCE_AMOUNT");
			ExcelHeaders.add("REMARKS");
			
			
			
			generatettumBeanObj.setStExcelHeader(ExcelHeaders);
			Excel_header.add(generatettumBeanObj);
			
			for(int i = 0 ; i<ExcelHeaders.size();i++)
			{
				table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+ExcelHeaders.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			String CHECK_ACC = "SELECT COUNT(*) FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
								" WHERE SUBSTR(CONTRA_ACCOUNT,4,6) = '505000'";
			int count = getJdbcTemplate().queryForObject(CHECK_ACC, new Object[]{},Integer.class);
			String GET_DATA = "";
			if(count>0)
			{		
			String GET_DATA="";

			
			if(generatettumBeanObj.getStFile_Name().equals("REV_REPORT")) {
				
				
				if(generatettumBeanObj.getInRec_Set_Id()== 8 ) {
				
					if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
						{
						GET_DATA =" Select distinct  '00000000000000' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E, replace(t1.REQUESTAMT,',','') TransactionAmount,"
				                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
				               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
				               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
				                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
				                +" WHERE ISS='IDB' and dcrs_remarks='UNMATCHED' and  to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  and "
				                + " FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
							 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
				                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							
						} else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
						{
							
							//REGEXP_REPLACE (substr(t1.ATMID,3,4), '0', '', 1, 1, 'i')||'37000010085'"
									
							GET_DATA =" Select distinct  decode(substr(t1.ATMID,3,1),0,substr(t1.ATMID,4,3),substr(t1.ATMID,3,4))||'37000010085' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E ,replace(t1.REQUESTAMT,',','') TransactionAmount,"
					                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
					               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
					               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
					                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
					                +" WHERE ACQ='IDB' and dcrs_remarks='UNMATCHED' and  to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  "
					                + " and FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
								 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
					                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							
								
						}
						
				}else if(generatettumBeanObj.getInRec_Set_Id()== 7 ) {
				
					
					if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
					{

						GET_DATA =" Select distinct '9997790010068' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E,replace(t1.REQUESTAMT,',','') TransactionAmount,"
		                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,"
		               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
		               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
		                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
		                +" WHERE ISS='IDB' and dcrs_remarks='MATCHED' and   to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') and  "
		                + "FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
					 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
		                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
						
					} 	else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
					{
												
						GET_DATA =" Select distinct '99937200010066' ACCTNUM ,'99937200010088' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E,replace(t1.REQUESTAMT,',','') TransactionAmount,"
				                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
				               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
				               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
				                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
				                +" WHERE ACQ='IDB' and dcrs_remarks='MATCHED' and to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				                + " and  FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
							 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
				                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							
					}	
				
				}
				
				}
			
//			to be continued...
			logger.info(GET_DATA);
			
			}
			else 
			{			
				GET_DATA ="SELECT SUBSTR(CONTRA_ACCOUNT,1,4)||37000010085 AS DRACC,CONTRA_ACCOUNT,REPLACE(AMOUNT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE, SUBSTR(PARTICULARALS,1,8) AS ATM_ID, " +
						"TO_CHAR(TO_DATE(SUBSTR(PARTICULARALS,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NO,1,10)) AS REF_NO," +
						" REMARKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
						" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";			
			}
			
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				
				//String Account_num = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
						//rset.getString("CONTRA_ACCOUNT");
				String acctnum = rset.getString("ACCTNUM");
				
				if(acctnum.equals("00000000000000")) {
					
					
					acctnum = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
				}
				
				
				
				if( Account_num!=null && !(Account_num.equals("NA")) )  {
					if( Account_num.contains("78000010021")&& acctnum!=null ){
					generateBean.setStCreditAcc(acctnum.replace("00 ", ""));
					
				}else {
					generateBean.setStCreditAcc(Account_num );
					}
				}
				else {
					if(acctnum!=null) {
						
						generateBean.setStCreditAcc(acctnum.replace("00 ", ""));
					
					
					} else {
						generateBean.setStCreditAcc(Account_num); 
						
							}
				
				generateBean.setStCreditAcc(acctnum);
				//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
				generateBean.setStDebitAcc(rset.getString("DRACC"));
				generateBean.setStAmount(rset.getString("TransactionAmount"));
				String stTran_Particular = "LTREV/"+rset.getString("ATMID")+"/"+rset.getString("tran_date")+"/"+rset.getString("rrn")   ; //rset.getString("TRANSACTIONPARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("referencenumber"));
				
				generateBean.setStDate(rset.getString("VALUE_DATE"));
				
				
				String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
				
				Date date= new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				String date2 =  sdf.format(date);
				remark= "REVR"+date2+remark;
				generateBean.setStRemark(remark);
				
				TTUM_Data.add(generateBean);
			}
			//Data.add(Excel_header);	
			//Data.add(TTUM_Data);
			
			pstmt.close();
			
			//inserting data IN TTUM TABLE
			for(GenerateTTUMBean beanObj : TTUM_Data)
			{
				//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE

				
		
				String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
						+ "VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
										"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
										"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
			
				
				getJdbcTemplate().execute(INSERT_DATA);
			
				
				INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
						"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
						"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
					getJdbcTemplate().execute(INSERT_DATA);
				
			
										
			}
			//SETTLEMENT_cashnet_iss_CBS
			String UPDATE_RECORDS="";
			
			
			if(generatettumBeanObj.getInRec_Set_Id()== 8) {
			UPDATE_RECORDS = "UPDATE SETTLEMENT_NFS_ACQ_REV_REPORT "+
					" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNMATCHED','UNMATCH-UNRECON-GENERATED-TTUM')"
					+" WHERE  DCRS_REMARKS = 'UNMATCHED' and"
							+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
					" TO_DATE(TRASN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
					" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
					" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
			} else if(generatettumBeanObj.getInRec_Set_Id()== 7) {
				
				UPDATE_RECORDS = "UPDATE SETTLEMENT_NFS_ACQ_REV_REPORT "+
						" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'MATCHED','MATCH-UNRECON-GENERATED-TTUM')"
						+" WHERE   DCRS_REMARKS = 'MATCHED' and "
								+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
						" TO_DATE(TRASN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
						" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				
				
			}
			 TTUM_Data.clear();
			 
			 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
			 
			getJdbcTemplate().execute(UPDATE_RECORDS);
			
			String query="";
			
			
			 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
					+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  ";
							  
			
			
			logger.info(query);
			 pstmt = conn.prepareStatement(query);
			 rset = pstmt.executeQuery();
			
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
				generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
				String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
				generateBean.setStDate(rset.getString("RECORDS_DATE"));
				generateBean.setStRemark(rset.getString("REMARKS"));
				generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
				
				
				TTUM_Data.add(generateBean);
			}
			
			
			Data.add(Excel_header);	
			Data.add(TTUM_Data);
			
			logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
			e.printStackTrace() ;
			logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
			 throw e;
		}
		return Data;
		

		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
		// throw e;
	}
	finally{
		if(rset!=null){
			rset.close();
		}
		if(pstmt!=null){
			pstmt.close();
		}
		if (conn!=null){
			conn.close();
		}
	}
	}catch(Exception e)
	{
		logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
		// throw e;
	}
	
	return Data;
}
*/


@Override
public List<List<GenerateTTUMBean>> generateNFSTTUM(
		GenerateTTUMBean generatettumBeanObj) {
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generateNFSTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	try{
	try
	{
		try
		{
			ExcelHeaders.add("ACCOUNT_NUMBER");
			ExcelHeaders.add("CURRENCY_CODE");
			ExcelHeaders.add("SERVICE_OUTLET");
			ExcelHeaders.add("PART_TRAN_TYPE");
			ExcelHeaders.add("TRANSACTION_AMOUNT");
			ExcelHeaders.add("TRANSACTION_PARTICULARS");
			ExcelHeaders.add("REFERENCE_NUMBER");
			ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
			ExcelHeaders.add("REFERENCE_AMOUNT");
			ExcelHeaders.add("REMARKS");

			generatettumBeanObj.setStExcelHeader(ExcelHeaders);
			Excel_header.add(generatettumBeanObj);
			
			for(int i = 0 ; i<ExcelHeaders.size();i++)
			{
				table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+ExcelHeaders.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			 
			String GET_DATA="";

				if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER")||generatettumBeanObj.getStSubCategory().equals("ISSUER")) {
				
				return  generateNfsAcqIssTTUM(generatettumBeanObj);
				
				} 
			if(generatettumBeanObj.getStFile_Name().equals("REV_REPORT")) {
				
				logger.info("inside REV_REPORT method to Select TTUM query ");
				if(generatettumBeanObj.getInRec_Set_Id()== 8 ) {
				
					if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
						{
						GET_DATA =" Select distinct  '00000000000000' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E, replace(t1.REQUESTAMT,',','') TransactionAmount,"
				                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
				               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
				               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
				                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
				                +" WHERE ISS='IDB' and dcrs_remarks='UNMATCHED' and  to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  and "
				                + " FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
							 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
				                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							
						} else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
						{
							
							//REGEXP_REPLACE (substr(t1.ATMID,3,4), '0', '', 1, 1, 'i')||'37000010085'"
									
							GET_DATA =" Select distinct  decode(substr(t1.ATMID,3,1),0,substr(t1.ATMID,4,3),substr(t1.ATMID,3,4))||'37000010085' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E ,replace(t1.REQUESTAMT,',','') TransactionAmount,"
					                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
					               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
					               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
					                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
					                +" WHERE ACQ='IDB' and dcrs_remarks='UNMATCHED' and  to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  "
					                + " and FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
								 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
					                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							 }
						
				}else if(generatettumBeanObj.getInRec_Set_Id()== 7 ) {
				
					
					if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
					{

						GET_DATA =" Select distinct '9997790010068' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E,replace(t1.REQUESTAMT,',','') TransactionAmount,"
		                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,"
		               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
		               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
		                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
		                +" WHERE ISS='IDB' and dcrs_remarks='MATCHED' and   to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') and  "
		                + "FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
					 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
		                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
						
					} 	else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
					{
												
						GET_DATA =" Select distinct '99937200010066' ACCTNUM ,'99937200010088' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E,replace(t1.REQUESTAMT,',','') TransactionAmount,"
				                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
				               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,"
				               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
				                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
				                +" WHERE ACQ='IDB' and dcrs_remarks='MATCHED' and to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
				                + " and  FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
							 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
				                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
							
					}	
				}
			}
			
//			to be continued...
			logger.info(GET_DATA);
			
			/*}
			else 
			{			
				GET_DATA ="SELECT SUBSTR(CONTRA_ACCOUNT,1,4)||37000010085 AS DRACC,CONTRA_ACCOUNT,REPLACE(AMOUNT,',','') AS AMOUNT,TO_CHAR(TO_DATE(VALUE_DATE,'DD/MM/YYYY'),'DDMMYY') AS VALUE_DATE, SUBSTR(PARTICULARALS,1,8) AS ATM_ID, " +
						"TO_CHAR(TO_DATE(SUBSTR(PARTICULARALS,10,8),'DD/MM/YYYY'),'DDMMYY') AS TRANDATE,TO_NUMBER(SUBSTR(REF_NO,1,10)) AS REF_NO," +
						" REMARKS FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+generatettumBeanObj.getStFile_Name()+
						" WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%'";			
			}*/
			
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				
				//String Account_num = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
						//rset.getString("CONTRA_ACCOUNT");
				String acctnum = rset.getString("ACCTNUM");
				
				if(acctnum.equals("00000000000000")) {
					
					
					acctnum = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_cashnet_iss_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
				}
				
				 
				
				generateBean.setStCreditAcc(acctnum);
				//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
				generateBean.setStDebitAcc(rset.getString("DRACC"));
				generateBean.setStAmount(rset.getString("TransactionAmount"));
				String stTran_Particular = "LTREV/"+rset.getString("ATMID")+"/"+rset.getString("tran_date")+"/"+rset.getString("rrn")   ; //rset.getString("TRANSACTIONPARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("referencenumber"));
				
				generateBean.setStDate(rset.getString("VALUE_DATE"));
				
				
				String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
				
				Date date= new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				String date2 =  sdf.format(date);
				remark= "REVR"+date2+remark;
				generateBean.setStRemark(remark);
				
				TTUM_Data.add(generateBean);
			}
			//Data.add(Excel_header);	
			//Data.add(TTUM_Data);
			
			pstmt.close();
			
			//inserting data IN TTUM TABLE
			for(GenerateTTUMBean beanObj : TTUM_Data)
			{
				//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE

				
		
				String INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
						+ "VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
										"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
										"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
			
				
				getJdbcTemplate().execute(INSERT_DATA);
			
				
				INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
						generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
						"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
						"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
					getJdbcTemplate().execute(INSERT_DATA);
				
			
										
			}
			//SETTLEMENT_cashnet_iss_CBS
			String UPDATE_RECORDS="";
			
			
			if(generatettumBeanObj.getInRec_Set_Id()== 8) {
			UPDATE_RECORDS = "UPDATE SETTLEMENT_NFS_ACQ_REV_REPORT "+
					" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNMATCHED','UNMATCH-UNRECON-GENERATED-TTUM')"
					+" WHERE  DCRS_REMARKS = 'UNMATCHED' and"
							+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
					" TO_DATE(TRASN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
					" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
					" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
			} else if(generatettumBeanObj.getInRec_Set_Id()== 7) {
				
				UPDATE_RECORDS = "UPDATE SETTLEMENT_NFS_ACQ_REV_REPORT "+
						" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'MATCHED','MATCH-UNRECON-GENERATED-TTUM')"
						+" WHERE   DCRS_REMARKS = 'MATCHED' and "
								+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
						" TO_DATE(TRASN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
						" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				
				
			}
			/* jit changes for update record start */
			if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
				UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
						" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
						+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+" ) and"
								+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
						" TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
						" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) { 
					
					UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
							+" WHERE LOCAL_DATE <> '00/00/0000' and regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
									+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
							" TO_DATE(LOCAL_DATE,'mm/dd/YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
							" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
							" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				
					
					
				}else if(generatettumBeanObj.getStFile_Name().equals("NFS")) { 
					
					UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
							+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
									+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
							" TO_DATE(TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
							" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
							" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
					
				}
			/* jit changes for update record end  */
			
			
			
			
			 TTUM_Data.clear();
			 
			 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
			 
			getJdbcTemplate().execute(UPDATE_RECORDS);
			
			String query="";
			
			
			 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
					+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  ";
							  
			
			
			logger.info(query);
			 pstmt = conn.prepareStatement(query);
			 rset = pstmt.executeQuery();
			
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
				generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
				String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
				generateBean.setStDate(rset.getString("RECORDS_DATE"));
				generateBean.setStRemark(rset.getString("REMARKS"));
				generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
				
				
				TTUM_Data.add(generateBean);
			}
			
			
			Data.add(Excel_header);	
			Data.add(TTUM_Data);
			
			logger.info("***** GenerateTTUMDaoImpl.generatecashnetTTUM End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
			e.printStackTrace() ;
			logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
			 throw e;
		}
		return Data;
		

		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generatecashnetTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
		// throw e;
	}
	finally{
		if(rset!=null){
			rset.close();
		}
		if(pstmt!=null){
			pstmt.close();
		}
		if (conn!=null){
			conn.close();
		}
	}
	}catch(Exception e)
	{
		logger.error(" error in GenerateTTUMDaoImpl.generatecashnetTTUM", new Exception("GenerateTTUMDaoImpl.generatecashnetTTUM",e));
		// throw e;
	}
	
	return Data;
}





@Override
public String getLatestFileDate(GenerateTTUMBean generateTTUMBean) {

	logger.info("***** GenerateRupayTTUMDaoImpl.getLatestFileDate Start ****");
	String stFileDate = null;
	String GET_FILEDATE="";
	
	
	
		GET_FILEDATE ="SELECT TO_CHAR(MAX(FILEDATE),'DD/MM/YYYY') FROM SETTLEMENT_"+generateTTUMBean.getStCategory()+"_"+generateTTUMBean.getStSubCategory().substring(0, 3)+ "_"+generateTTUMBean.getStSelectedFile();
		
		stFileDate = getJdbcTemplate().queryForObject(GET_FILEDATE, new Object[]{}, String.class);
	
	logger.info("GET_FILEDATE=="+GET_FILEDATE);
	logger.info("stFileDate=="+stFileDate);
	
	logger.info("***** GenerateNFSTTUMDaoImpl.getLatestFileDate End ****");
	
	return stFileDate;
}

/*changes made by -jit */

public List<List<GenerateTTUMBean>> generateNfsAcqIssTTUM(GenerateTTUMBean generatettumBeanObj) {
	
	List<List<GenerateTTUMBean>> Data = new ArrayList<>();
	logger.info("***** GenerateTTUMDaoImpl.generateNfsAcqIssTTUM Start ****");
	List<GenerateTTUMBean> TTUM_Data = new ArrayList<>();
	List<String> ExcelHeaders = new ArrayList<>();
	List<GenerateTTUMBean> Excel_header = new ArrayList<>();
	String table_cols = "DCRS_REMARKS VARCHAR (100 BYTE), CREATEDDATE DATE , CREATEDBY VARCHAR (100 BYTE),RECORDS_DATE DATE";
	String insert_cols = "DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE";
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	String txntype = "";
	try{
	try
	{
		try
		{
			ExcelHeaders.add("ACCOUNT_NUMBER");
			ExcelHeaders.add("CURRENCY_CODE");
			ExcelHeaders.add("SERVICE_OUTLET");
			ExcelHeaders.add("PART_TRAN_TYPE");
			ExcelHeaders.add("TRANSACTION_AMOUNT");
			ExcelHeaders.add("TRANSACTION_PARTICULARS");
			ExcelHeaders.add("REFERENCE_NUMBER");
			ExcelHeaders.add("REFERENCE_CURRENCY_CODE");
			ExcelHeaders.add("REFERENCE_AMOUNT");
			ExcelHeaders.add("REMARKS");

			generatettumBeanObj.setStExcelHeader(ExcelHeaders);
			Excel_header.add(generatettumBeanObj);
			
			for(int i = 0 ; i<ExcelHeaders.size();i++)
			{
				table_cols =table_cols+","+ ExcelHeaders.get(i)+" VARCHAR (100 BYTE)";
				insert_cols = insert_cols+","+ExcelHeaders.get(i);
			}
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"'";
			logger.info("check table "+CHECK_TABLE);
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"("+table_cols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);			
			}
			
			 
			String GET_DATA="";

			 if(generatettumBeanObj.getStFile_Name().equals("NFS")){
			 
			 
			if(generatettumBeanObj.getStSubCategory().equals("ISSUER"))
			{

				GET_DATA ="Select distinct '9997790010068' ACCTNUM ,'9997800010194' DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,'D' E,replace(t1.REQUESTAMT,',','') TransactionAmount,"
                 +" 'LTREV/'||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||t1.ATMID ||'/' ||to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy')||'/'||substr(t1.RRN,-6) as TRANSACTIONPARTICULARS,"
               +" to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'ddmmyy') tran_date,t1.ATMID ,substr(t1.RRN,-6) rrn ,to_char(to_date(t1.TRASN_DATE,'dd-mm-yyyy'),'dd/mm/yyyy') value_date ,"
               + "  CARDNO as referencenumber ,'INR'REFERENCECURRENCYCODE "
                +"  from SETTLEMENT_NFS_ACQ_REV_REPORT t1 "
                +" WHERE ISS='IDB' and dcrs_remarks='MATCHED' and   to_date(TRASN_DATE,'dd-mm-yyyy')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') and  "
                + "FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_REV_REPORT) and not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  t1.CARDNO=t3.remarks " //t3.REFERENCE_NUMBER
			 	+"  AND replace(t1.REQUESTAMT,',','') = t3.TRANSACTION_AMOUNT "
                +"  AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
			 
			} 	else if(generatettumBeanObj.getStSubCategory().equals("ACQUIRER"))
			{
										
				 
				GET_DATA =" Select  distinct  decode(substr(t1.CARD_ACC_TERMINAL_ID,3,1),0,substr(t1.CARD_ACC_TERMINAL_ID,4,3),substr(t1.CARD_ACC_TERMINAL_ID,3,4))||'37000010085' DRACC  ,'99978000010194'ACCTNUM ,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(ltrim(t1.ACQ_SETTLE_AMNT,'0') ,',','') TransactionAmount,"
				 		+" 'NFS/' || t1.CARD_ACC_TERMINAL_ID || '/'|| to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'ddmmyy')  || '/'||substr(TXN_SERIAL_NO,-4)|| '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars,t1.card_acc_terminal_id as ATMID,t1.transaction_date as c_tran_date,"
				 		+ " pan_number as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'dd/mm/yyyy') as VALUE_DATE ,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode,substr(TXN_SERIAL_NO,-4) ref_no  "
				 		+ "  from SETTLEMENT_nfs_ACQ_NFS t1 " 
				 		+" WHERE    regexp_replace(t1.dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+") AND"
		               + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
						+" TO_DATE(t1.TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
						+" AND TO_DATE(t1.TRANSACTION_DATE ,'yymmdd') < (SELECT MAX(FILEDATE)-2 FROM SETTLEMENT_nfs_ACQ_NFS) "
						+ " and  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_nfs_ACQ_NFS) "
						+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan_number)=trim(t3.remarks) " //t3.REFERENCE_NUMBER
				 	+"  AND to_number(t1.ACQ_SETTLE_AMNT) = to_number(t3.TRANSACTION_AMOUNT) "
		           +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
				
				 txntype = "UP";
				
			}
			 logger.info("GET_DATA"+GET_DATA);
			} 
			 
			if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
			 logger.info("nfs(CBS) code start");
				if(! generatettumBeanObj.getRespcode().equals("0") ) {
				 
				 GET_DATA =" Select  distinct filedate,TRAN_DATE ,substr(particularals,1,8) ATMID ,substr(PARTICULARALS,10,8) PARTICULARALS, DECODE (SUBSTR (t1.PARTICULARALS , 3,1 ),0, SUBSTR (t1.PARTICULARALS, 4, 3), SUBSTR (t1.PARTICULARALS, 3, 4))||'78000010085' DRACC,'99978000010194'ACCTNUM ,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount ,"
						 +" 'NFS/' || substr(t1.PARTICULARALS,1,8) || '/'|| to_char(to_date(TRAN_DATE,'dd-mm-yyyy'),'ddmmyy')  || '/'||  substr(t1.ref_no,7,4) || '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars, substr(t1.ref_no,7,4) ref_no,TRAN_DATE ,"
						 +" remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,TRAN_DATE VALUE_DATE,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode "
                         +" ,TO_CHAR (TO_DATE (tran_date, 'dd-mm-yyyy'), 'ddmmyy')as c_tran_date  from SETTLEMENT_nfs_acq_CBS t1 WHERE  regexp_replace(t1.dcrs_remarks, '[^0-9]', '')in ( "+generatettumBeanObj.getRespcode()+") "
                         +" AND E='C' and  (t1.DCRS_REMARKS not LIKE '%TTUM%') "
                         +" AND   TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  " 
                         +" AND  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_nfs_acq_CBS)  "
                         +" AND    not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"  t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and t1.REMARKS=t3.remarks "  
                         +" AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT  "
                         +" AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
				 txntype = "DR";	
				} else if (generatettumBeanObj.getRespcode().equals("0")) {
					  GET_DATA =" Select  distinct filedate,TRAN_DATE ,substr(particularals,1,8) ATMID ,substr(PARTICULARALS,10,8) PARTICULARALS, DECODE (SUBSTR (t1.PARTICULARALS , 3,1 ),0, SUBSTR (t1.PARTICULARALS, 4, 3), SUBSTR (t1.PARTICULARALS, 3, 4))||'78000010085' CONTRA_ACCOUNT,'99978000010194'DRACC,'INR' as CurrencyCode ,'999' As ServiceOutlet,E,replace(t1.AMOUNT,',','') TransactionAmount,"
							 +"  'NFS/' || substr(t1.PARTICULARALS,1,8) || '/'|| to_char(to_date(TRAN_DATE,'dd-mm-yyyy'),'ddmmyy')  || '/'||  substr(t1.ref_no,7,4) || '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars, substr(t1.ref_no,7,4) ref_no,TRAN_DATE,"
							 +"remarks as referencenumber ,'INR'REFERENCECURRENCYCODE,TRAN_DATE VALUE_DATE,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode"
	                        +"  from SETTLEMENT_nfs_acq_CBS t1 WHERE  regexp_replace(t1.dcrs_remarks, '[^0-9]', '')in ( "+generatettumBeanObj.getRespcode()+")"
	                         +" AND E='C' and  (t1.DCRS_REMARKS not LIKE '%TTUM%') "
	                        +"  AND   TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  " 
	                        +"  AND  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_nfs_acq_CBS)  "
	                        +"  AND    not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+"  t3 where t1.CONTRA_ACCOUNT=t3.ACCOUNT_NUMBER and t1.REMARKS=t3.remarks "  
	                        +"   AND replace(t1.AMOUNT,',','') = t3.TRANSACTION_AMOUNT  "
	                         +" AND t3.RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
					
					  txntype = "CR";
					 
				}
				
			}
			
			/*	else if(generatettumBeanObj.getStFile_Name().equals("NFS")) {
			 logger.info("nfs code start");
				 
				GET_DATA =" Select  distinct  decode(substr(t1.CARD_ACC_TERMINAL_ID,3,1),0,substr(t1.CARD_ACC_TERMINAL_ID,4,3),substr(t1.CARD_ACC_TERMINAL_ID,3,4))||'37000010085' DRACC  ,'99978000010194'ACCTNUM ,'INR' as CurrencyCode ,'999' As ServiceOutlet,replace(ltrim(t1.ACQ_SETTLE_AMNT,'0') ,',','') TransactionAmount,"
				 		+" 'NFS/' || t1.CARD_ACC_TERMINAL_ID || '/'|| to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'ddmmyy')  || '/'||substr(TXN_SERIAL_NO,-6)|| '/'  || REGEXP_REPLACE (t1.dcrs_remarks, '[^0-9]', '') AS transactionparticulars,t1.card_acc_terminal_id as ATMID,t1.transaction_date as c_tran_date,"
				 		+ " pan_number as referencenumber ,'INR'REFERENCECURRENCYCODE,to_char(to_date(t1.TRANSACTION_DATE ,'yymmdd'),'dd/mm/yyyy') as VALUE_DATE ,regexp_replace(t1.dcrs_remarks, '[^0-9]', '') respcode,substr(TXN_SERIAL_NO,-6) ref_no  "
				 		+ "  from SETTLEMENT_nfs_ACQ_NFS t1 " 
				 		+" WHERE    regexp_replace(t1.dcrs_remarks, '[^0-9]', '') in ( "+generatettumBeanObj.getRespcode()+") AND"
		               + " (t1.DCRS_REMARKS not LIKE '%TTUM%') AND "
						+" TO_DATE(t1.TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY') "
						+" AND TO_DATE(t1.TRANSACTION_DATE ,'yymmdd') < (SELECT MAX(FILEDATE)-2 FROM SETTLEMENT_nfs_ACQ_NFS) "
						+ " and  filedate = (SELECT MAX(FILEDATE) FROM SETTLEMENT_nfs_ACQ_NFS) "
						+ " AND	not exists(select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" t3 where  trim(t1.pan_number)=trim(t3.remarks) " //t3.REFERENCE_NUMBER
				 	+"  AND to_number(t1.ACQ_SETTLE_AMNT) = to_number(t3.TRANSACTION_AMOUNT) "
		           +"  AND t3.RECORDS_DATE   BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date() +"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date() +"','DD/MM/YYYY')  )";
				
				 txntype = "UP";
			} */
		 
			  
//			to be continued...
			logger.info(GET_DATA);
			
			 
			conn = getConnection();
			pstmt = conn.prepareStatement(GET_DATA);
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				 String acctnum = rset.getString("ACCTNUM");
				
				if(acctnum.equals("00000000000000")) {
					
					
					acctnum = getJdbcTemplate().queryForObject("select nvl((SELECT DISTINCT CONTRA_ACCOUNT FROM  settlement_nfs_acq_cbs  where remarks='"+rset.getString("referencenumber") +"' and   contra_account is not null and CONTRA_ACCOUNT not like '%78000010021%'),'NA') from dual",  String.class);
				}
				
				 
				
				generateBean.setStCreditAcc(acctnum);
				
				//generateBean.setStDebitAcc(rset.getString("FORACID")); // AS PER SAMEER MAIL RECEIVED ON 12 JAN 2017 CHANGE THE DR ACC
				generateBean.setStDebitAcc(rset.getString("DRACC"));
				generateBean.setStAmount(rset.getString("TransactionAmount")); // transactionamount
				
				String stTran_Particular="NFS/"+rset.getString("ATMID")+"/"+rset.getString("c_tran_date")+"/"+rset.getString("ref_no")+"/"+txntype+rset.getString("respcode");
				logger.info("stTran_Particular"+stTran_Particular);
				//String stTran_Particular = "LTREV/"+rset.getString("ATMID")+"/"+rset.getString("tran_date")+"/"+rset.getString("rrn")   ; //rset.getString("TRANSACTIONPARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("referencenumber"));
				logger.info("referencenumber"+rset.getString("referencenumber"));
				generateBean.setStDate(rset.getString("VALUE_DATE"));
				
				
				String remark = getJdbcTemplate().queryForObject("select lpad(ttum_seq.nextval,6,0) from dual", new Object[] {},String.class);
				
				Date date= new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
				String date2 =  sdf.format(date);
				remark= "NFS"+date2+remark;
				generateBean.setStRemark(remark);
				
				TTUM_Data.add(generateBean);
			}
			 
			pstmt.close();
			
			//inserting data IN TTUM TABLE
			for(GenerateTTUMBean beanObj : TTUM_Data)
			{
				//DCRS_REMARKS, CREATEDDATE, CREATEDBY,RECORDS_DATE
				String INSERT_DATA="";
				if(generatettumBeanObj.getStFile_Name().equals("CBS")){
					
					if(beanObj.getStDebitAcc().contains("78000010085") ) {
						  INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
								+ "VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
												"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
												"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
						
						  getJdbcTemplate().execute(INSERT_DATA);
						
						INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
								"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
								"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
							
						getJdbcTemplate().execute(INSERT_DATA);
					}
			
					
					else if(beanObj.getStDebitAcc().contains("99978000010194") ){
						INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") VALUES('"+
								generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
								"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
								"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
							
						getJdbcTemplate().execute(INSERT_DATA);
							
							INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
									+ "VALUES('"+
									generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
													"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
													"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
							
						getJdbcTemplate().execute(INSERT_DATA);
							
							logger.info("INSERT_DATA::>>"+INSERT_DATA);
					}	
				}
				else if (generatettumBeanObj.getStFile_Name().equals("NFS")) {
						
						 INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
									+ "VALUES('"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
									"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStDebitAcc()+"','INR','999','D','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
									"','"+beanObj.getStRemark()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStCard_Number()+"')";
						 logger.info("INSERT_DATA::>"+INSERT_DATA+"beanObj.getStDebitAcc():>"+beanObj.getStDebitAcc());
						 getJdbcTemplate().execute(INSERT_DATA);
						
						 INSERT_DATA = "INSERT INTO TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" ("+insert_cols+") "
									+ "VALUES('"+
									generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+"-TTUM',SYSDATE,'"+generatettumBeanObj.getStEntry_by()+
													"',TO_DATE('"+beanObj.getStDate()+"','DD/MM/YYYY'),'"+beanObj.getStCreditAcc()+"','INR','999','C','"+beanObj.getStAmount()+"','"+beanObj.getStTran_particulars()+
													"','"+beanObj.getStCard_Number()+"','INR','"+beanObj.getStAmount()+"','"+beanObj.getStRemark()+"')";
							
						 logger.info("INSERT_DATA::>"+INSERT_DATA+"beanObj.getStCreditAcc():>"+beanObj.getStCreditAcc());
						getJdbcTemplate().execute(INSERT_DATA);
						
							
				}
				
				
				
				
					
										
			}
			//SETTLEMENT_cashnet_iss_CBS
			String UPDATE_RECORDS="";
			  
			/* jit changes for update record start */
			if(generatettumBeanObj.getStFile_Name().equals("CBS")) {
				UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
						" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
						+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+" ) and"
								+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
						" TO_DATE(TRAN_DATE,'DD-MM-YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
						" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
						" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				} else if(generatettumBeanObj.getStFile_Name().equals("SWITCH")) { 
					
					UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
							+" WHERE LOCAL_DATE <> '00/00/0000' and regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
									+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
							" TO_DATE(LOCAL_DATE,'mm/dd/YYYY')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
							" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
							" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
				
					
					
				}else if(generatettumBeanObj.getStFile_Name().equals("NFS")) { 
					
					UPDATE_RECORDS = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+
							" SET DCRS_REMARKS = REPLACE(DCRS_REMARKS,'UNRECON','UNRECON-GENERATED-TTUM')"
							+" WHERE  regexp_replace(dcrs_remarks, '[^0-9]', '') in( "+generatettumBeanObj.getRespcode()+") and"
									+ " dcrs_remarks not like '%UNRECON-GENERATED-TTUM%'  and "+
							" TO_DATE(TRANSACTION_DATE ,'yymmdd')  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD-MM-YYYY')" +
							" AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD-MM-YYYY')" +
							" AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_"+generatettumBeanObj.getStCategory()+"_"+generatettumBeanObj.getStSubCategory().substring(0,3)+"_"+generatettumBeanObj.getStFile_Name()+")";
					
				}
			/* jit changes for update record end  */
			
			
			
			
			 TTUM_Data.clear();
			 
			 logger.info("UPDATE_RECORDS=="+UPDATE_RECORDS);
			 
			getJdbcTemplate().execute(UPDATE_RECORDS);
			
			String query="";
			
			
			 query="select * from TTUM_"+generatettumBeanObj.getStCategory()+ "_"+generatettumBeanObj.getStSubCategory().substring(0, 3)+ "_"+generatettumBeanObj.getStFile_Name().toUpperCase()+" where "
					+ " RECORDS_DATE  BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')  ";
							  
			
			
			logger.info(query);
			 pstmt = conn.prepareStatement(query);
			 rset = pstmt.executeQuery();
			
			
			while (rset.next()) {
				
				GenerateTTUMBean generateBean = new GenerateTTUMBean();
				generateBean.setStCreditAcc(rset.getString("ACCOUNT_NUMBER"));
				generateBean.setStAmount(rset.getString("TRANSACTION_AMOUNT"));
				String stTran_Particular = rset.getString("TRANSACTION_PARTICULARS");
				generateBean.setStTran_particulars(stTran_Particular);
				generateBean.setStCard_Number(rset.getString("REFERENCE_NUMBER"));
				generateBean.setStDate(rset.getString("RECORDS_DATE"));
				generateBean.setStRemark(rset.getString("REMARKS"));
				generateBean.setStPart_Tran_Type(rset.getString("PART_TRAN_TYPE"));
				
				
				TTUM_Data.add(generateBean);
			}
			
			
			Data.add(Excel_header);	
			Data.add(TTUM_Data);
			
			logger.info("***** GenerateTTUMDaoImpl.generateNfsAcqIssTTUM End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "GenerateTTUMDaoImpl.generateNfsAcqIssTTUM");
			e.printStackTrace() ;
			logger.error(" error in GenerateTTUMDaoImpl.generateNfsAcqIssTTUM", new Exception("GenerateTTUMDaoImpl.generateNfsAcqIssTTUM",e));
			 throw e;
		}
		return Data;
		

		}
	catch(Exception e)
	{
		demo.logSQLException(e, "GenerateTTUMDaoImpl.generateNfsAcqIssTTUM");
		logger.error(" error in GenerateTTUMDaoImpl.generateNfsAcqIssTTUM", new Exception("GenerateTTUMDaoImpl.generateNfsAcqIssTTUM",e));
		// throw e;
	}
	finally{
		if(rset!=null){
			rset.close();
		}
		if(pstmt!=null){
			pstmt.close();
		}
		if (conn!=null){
			conn.close();
		}
	}
	}catch(Exception e)
	{
		logger.error(" error in GenerateTTUMDaoImpl.generateNfsAcqIssTTUM", new Exception("GenerateTTUMDaoImpl.generateNfsAcqIssTTUM",e));
		// throw e;
	}
	
	return Data;
}

/*changes made by -End  */



public void forPositiveDiffAmount () {}

	@Override
	public List<Integer> getRespcode(String category, String subcategory,
			String filename, String filedate) throws Exception {
		logger.info("***** GenerateTTUMDaoImpl.getRespCode Start ****");
		List<Integer> respcodes = new ArrayList<Integer>();

		try {

			String query = "SELECT DISTINCT translate(regexp_substr(os1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0') from SETTLEMENT_MASTERCARD_CBS os1 where os1.DCRS_REMARKS like '%MASTERCARD_ISS_CBS_UNMATCHED%' " +
					" and translate(regexp_substr(os1.DCRS_REMARKS  ,'[^ - ]+',1,2),'0#$&&!_()','0')!=0";

			respcodes = getJdbcTemplate().queryForList(query, Integer.class);

			logger.info("respcodes==" + respcodes);

			logger.info("***** GenerateTTUMDaoImpl.getRespCode End ****");

		} catch (Exception e) {
			demo.logSQLException(e, "GenerateTTUMDaoImpl.getRespCode");
			logger.error(" error in GenerateTTUMDaoImpl.getRespCode",
					new Exception("GenerateTTUMDaoImpl.getRespCode", e));
			throw e;
		}

		return respcodes;

	}

	
	public boolean Callproc(GenerateTTUMBean generatettumBeanObj) throws ParseException, Exception {
		try {
			logger.info("***** ReconProcessDaoImpl.Onus_Pos_Cycle1 Start ****");
			//boolean resp=false;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("category_name", generatettumBeanObj.getStCategory());
			inParams.put("date_val", generatettumBeanObj.getStStart_Date());
			inParams.put("date_val2", generatettumBeanObj.getEntry_date());
			inParams.put("REP_CODE", generatettumBeanObj.getRespcode());
			
			main_prc cbrmatching = new main_prc(getJdbcTemplate());
			Map<String, Object> outParams = cbrmatching.execute(inParams);
			
			logger.info("outParams Msg=="+outParams.get("msg1") );
			logger.info("***** ReconProcessDaoImpl.Onus_Pos_Cycle1 End ****");
			
			if (outParams.get("msg1") != null) {
				
				return true;
			}
			else{
				return false;
			}
			
		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.Onus_Pos_Cycle1");
			logger.error(" error in  ReconProcessDaoImpl.Onus_Pos_Cycle1", new Exception(" ReconProcessDaoImpl.Onus_Pos_Cycle1",e));
			return false;
		}
		
		
	}
	
	private class main_prc extends StoredProcedure {
		private static final String procName = "mastercard_cbs_ttum";

		main_prc(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			
			
			declareParameter(new SqlParameter("category_name",Types.VARCHAR));
			declareParameter(new SqlParameter("date_val",Types.VARCHAR));
			declareParameter(new SqlParameter("date_val2",Types.VARCHAR));
			declareParameter(new SqlParameter("REP_CODE",Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg1", Types.VARCHAR));
			
			compile();
		}
	}
	
	public String getAccountNo(String CardNumber) throws ParseException, Exception {
		
			String Account ="00000";
		try {
			logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");

			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("CardNumber", CardNumber);

			GETACCOUNTNO acqclassificaton = new GETACCOUNTNO(
					getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);

			// logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");

			if (outParams.get("account_number") != null) {

				return Account = (String) outParams.get("account_number") ;
			} else {

				return Account;
			}

		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
			logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
					new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
			return Account;
		}

	}
	
	class GETACCOUNTNO extends StoredProcedure {
		private static final String procName = "proc_GET_ACCOUNT_NO";

		GETACCOUNTNO(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("CardNumber",	Types.VARCHAR));
			declareParameter(new SqlOutParameter("account_number",Types.VARCHAR));
			compile();
		}
	}
	
	// update account number for visa 
	
	public void getupdatedAccountNo(String CardNumber) throws ParseException, Exception {
		
		String Account ="00000";
	try {
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");

		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();

		inParams.put("CardNumber", "");

		updateaccountno acqclassificaton = new updateaccountno(getJdbcTemplate());
		Map<String, Object> outParams = acqclassificaton.execute(inParams);

		// logger.info("outParams msg1"+outParams.get("msg1"));
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");

		

	} catch (Exception e) {
		demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
		logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
				new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
		
	}

}

class updateaccountno extends StoredProcedure {
	private static final String procName = "update_visa_account_number";

	updateaccountno(JdbcTemplate JdbcTemplate) {
		super(JdbcTemplate, procName);
		setFunction(false);

		declareParameter(new SqlParameter("CardNumber",	Types.VARCHAR));
		declareParameter(new SqlOutParameter("account_number",Types.VARCHAR));
		compile();
	}
}
	
	
	
	
	
	
	
	
}
