package com.recon.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.control.AdjustmentFileController;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RefundTTUMBean;
import com.recon.service.RefundTTUMService;

public class RefundTTUMServiceImpl extends JdbcDaoSupport implements RefundTTUMService {
	
	private static final Logger logger = Logger.getLogger(RefundTTUMServiceImpl.class);
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	
	@Override
	public HashMap<String,Object> validateRefundTTUM(RefundTTUMBean beanObj)
	{
		HashMap<String,Object> output = new HashMap<String,Object>();
		String fileName = "RUPAY";
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				fileName = "POS";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				fileName = "VISA";
			}
			// Raw File is Uploaded or not
			String rawTable = getJdbcTemplate().queryForObject("select DISTINCT TABLENAME from main_filesource where filename = ? AND FILE_CATEGORY = ?", 
							new Object[] {fileName,beanObj.getCategory()},String.class);
			logger.info("Raw Table name is "+rawTable);
			int rawCount = getJdbcTemplate().queryForObject("select count(*) from "+rawTable+" where filedate = ?" , new Object[] {beanObj.getFileDate()},Integer.class);
			logger.info("Data count in raw table is "+rawCount);
			
			if(rawCount == 0)
			{
				output.put("result", false);
				output.put("msg", "Raw File is not uploaded for selected date !");
			}
			else
			{
				//previous day refund ttum is processed or not
				output.put("result", true);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in validateRefundTTUM "+e);
			output.put("result", false);
			output.put("msg", "Exception in validateRefundTTUM!");
		}
		
		return output;
	}

	@Override
	public boolean runRefundTTUMMatching(RefundTTUMBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			RefundTTUMMatching rollBackexe = new RefundTTUMMatching(getJdbcTemplate());
			inParams.put("FILE_DATE", beanObj.getFileDate());
			inParams.put("CATEGORY", beanObj.getCategory());
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			else
			{
				return true;
			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}

	}
	private class RefundTTUMMatching extends StoredProcedure{
		private static final String insert_proc = "REFUND_TTUM_MATCHING";
		public RefundTTUMMatching(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILE_DATE",Types.VARCHAR));
			declareParameter(new SqlParameter("CATEGORY",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	@Override
	public HashMap<String,Object> ValidateRefundProcessing(RefundTTUMBean beanObj)
	{
		String matched_tableName = null,unmatched_table = null;
		HashMap<String,Object> output = new HashMap<String,Object>();
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				matched_tableName =  "RUPAY_TTUM_MATCHED";
				unmatched_table = "RUPAY_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				matched_tableName =  "MASTER_POS_TTUM_MATCHED";
				unmatched_table =  "MASTER_POS_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				matched_tableName =  "VISA_TTUM_MATCHED";
				unmatched_table =  "VISA_TTUM_UNMATCHED";
			}
			
			//CHECK PREVIOS DAY UNMATCHED DATA
			int count1 = getJdbcTemplate().queryForObject("select count(*) from "+unmatched_table+" where filedate = TO_DATE(?,'DD/MON/YYYY')-1 AND SYSTEM_REMARKS = 'UNMATCHED'", new Object[] {beanObj.getFileDate()},Integer.class);
			
			if(count1 == 0)
			{

				String query1 = "select count(*) from "+matched_tableName+" where filedate = to_date(?,'DD/MON/YYYY')";
				String query2 = "select count(*) from "+unmatched_table+" where filedate = to_date(?,'DD/MON/YYYY')";
				count1 = getJdbcTemplate().queryForObject(query1, new Object[] {beanObj.getFileDate()},Integer.class);

				int count2 = getJdbcTemplate().queryForObject(query2, new Object[] {beanObj.getFileDate()},Integer.class);

				if(count1 > 0 || count2 > 0)
				{
					output.put("result", false);
					output.put("msg", "Matching is already processed");
				}
				else
				{
					output.put("msg", "Matching is not processed");
					output.put("result", true);
				}
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Previous day unmatched data are still pending");
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in ValidateRefundProcessing " + e);
			output.put("result", false);
			output.put("msg", "Exception Occured while Validating");
		}
		
		return output;
		
	}
	
	public List<Object> getRefundTTUMProcessData(RefundTTUMBean beanObj)
	{
		String matched_tableName = null,unmatched_table = null;
		List<Object> data = new ArrayList<Object>();
		try
		{
			
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				matched_tableName =  "RUPAY_TTUM_MATCHED";
				unmatched_table = "RUPAY_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				matched_tableName =  "MASTER_POS_TTUM_MATCHED";
				unmatched_table =  "MASTER_POS_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				matched_tableName =  "VISA_TTUM_MATCHED";
				unmatched_table =  "VISA_TTUM_UNMATCHED";
			}
			
			String getData = null;
			//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
			final List<String> cols  = getColumnList(matched_tableName);
			
			
				getData = "SELECT * FROM "+matched_tableName+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";
			
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						for(String column: cols)
						{
							table_Data.put(column, rs.getString(column));
						}
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			
			data.add(cols);
			data.add(DailyData);

			//GETTING UNMATCHED DATA
			
			final List<String> columns  = getColumnList(unmatched_table);
			getData = "SELECT * FROM "+unmatched_table+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";
			
			DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						for(String column: columns)
						{
							//table_Data.put(column, rs.getString(column.replace(" ", "_")));
							table_Data.put(column, rs.getString(column));
						}
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			data.add(columns);
			data.add(DailyData);
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getRefundTTUMData "+e);
			return null;

		}
		
	}
	
	public ArrayList<String> getColumnList(String tableName) {

		//String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
		String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
		System.out.println(query);


		ArrayList<String> typeList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				return rs.getString(1);
			}
		});

		System.out.println(typeList);
		return typeList;

	}
	
/**
 * Refund ttum Generation Code	
 */
	@Override
	public HashMap<String,Object> validateRefundTTUMGeneration(RefundTTUMBean beanObj)
	{
		HashMap<String,Object> output = new HashMap<String,Object>();
		String table_name = null;
		try
		{
			//CHECK WHETHER DATA IS PRESENT FOR TTUM GENERATION

			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				table_name = "RUPAY_TTUM_MATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				table_name = "MASTER_POS_TTUM_MATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				table_name = "VISA_TTUM_MATCHED";
			}
			
			String checkData = "SELECT COUNT(*) FROM "+table_name+" WHERE FILEDATE= ? AND (SYSTEM_REMARKS = 'MOVED' OR SYSTEM_REMARKS = 'MATCHED')";
			
			int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[] {beanObj.getFileDate()},Integer.class);
			
			if(dataCount == 0)
			{

				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					table_name = "RUPAY_REFUND_TTUM";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
				{
					table_name = "MASTERCARD_POS_refund_ttum";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					table_name = "VISA_REFUND_TTUM";
				}
				//TTUM is already processed
				String checkQuery = "SELECT COUNT(*) FROM "+table_name+" where filedate = TO_DATE(?,'DD/MON/YYYY')";

				int procCount = getJdbcTemplate().queryForObject(checkQuery, new Object[] {beanObj.getFileDate()},Integer.class);

				if(procCount > 0)
				{
					output.put("result", true);
					output.put("msg", "TTUM is already processed. Please download the reports");
				}
				else
				{
					output.put("result", false);
				}
			}
			else
			{
				output.put("result", false);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in validateRefundTTUM "+e);
			output.put("result", true);
			output.put("msg", "Exception in validateRefundTTUM!");
		}
		
		return output;
	}	
	
	@Override
	public boolean runRefundTTUMGeneration(RefundTTUMBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			RefundTTUMGeneration ttumexe = new RefundTTUMGeneration(getJdbcTemplate());
			inParams.put("FILE_DATE", beanObj.getFileDate());
			inParams.put("CATEGORY", beanObj.getCategory());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams = ttumexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			else
			{
				return true;
			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		
	}
	
	private class RefundTTUMGeneration extends StoredProcedure{
		private static final String insert_proc = "REFUND_TTUM";
		public RefundTTUMGeneration(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILE_DATE",Types.VARCHAR));
			declareParameter(new SqlParameter("CATEGORY",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public List<Object> getRefundTTUMData(RefundTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		try
		{
			
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				ttum_tableName =  "rupay_refund_ttum";
			}
		   else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				ttum_tableName =  "visa_refund_ttum";
			}
			
			String getData = null;
			final List<String> cols  = getTTUMColumnList(ttum_tableName);
			/*SList<String> Column_list = new ArrayList<String>();
			Column_list.add("ACCOUNT NUMBER");
			Column_list.add("CURRENCY CODE OF ACCOUNT NUMBER");	
			Column_list.add("SERVICE OUTLET");
			Column_list.add("PART TRAN TYPE");	
			Column_list.add("TRANSACTION AMOUNT");	
			Column_list.add("TRANSACTION PARTICULAR");	
			Column_list.add("ACCOUNT REPORT CODE");	
			Column_list.add("REFERENCE AMOUNT");	
			Column_list.add("REFERENCE CURRENCY CODE");	
			Column_list.add("RATE CODE");	
			Column_list.add("REMARKS");	
			Column_list.add("REFERENCE NUMBER");*/
			
			
				/*getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";*/
			
				getData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";
			
				logger.info("Getdata query is "+getData);
				
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						/*for(String column: cols)
						{*/
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			
			//data.add(Column_list);
			//data.add(DailyData);

			
			return DailyData;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getRefundTTUMData "+e);
			return null;

		}
		
	}
	
	
	public List<Object> getVisaRefundTTUMData(RefundTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		try
		{
			
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				ttum_tableName =  "rupay_refund_ttum";
			}
		   else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				ttum_tableName =  "visa_refund_ttum";
			}
			
			String getDomData = null;
			String getIntData = null;
			final List<String> cols  = getTTUMColumnList(ttum_tableName);
			
			
				getDomData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') AND TRAN_TYPE = 'DOMESTIC'";
			
				logger.info("Getdata query is "+getDomData);
				
				getIntData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') AND TRAN_TYPE = 'INTERNATIONAL'";
			
				logger.info("Getdata query is "+getIntData);
			
				
			List<Object> DailyData1= getJdbcTemplate().query(getDomData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						/*for(String column: cols)
						{*/
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			List<Object> DailyData2 = getJdbcTemplate().query(getIntData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						/*for(String column: cols)
						{*/
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			
			//data.add(Column_list);
			//data.add(DailyData);
			data.add(cols);
			data.add(DailyData1);
			data.add(DailyData2);
			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getRefundTTUMData "+e);
			return null;

		}
		
	}
	
	
	public ArrayList<String> getTTUMColumnList(String tableName) {

		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('CREATEDDATE','CREATEDBY','CYCLE')";
		String query = "select column_name from information_schema.columns where table_schema = database() and table_name = '"+tableName.toLowerCase()+"' "
				+"and column_name not in('id','createdby','createddate','dcrs_tran_no','next_tran_date','part_id','foracid','balance','pstd_user_id','particularals2','org_acct',"
				+"'tran_type','seg_tran_id','man_contra_account','balance')";
		System.out.println(query);


		ArrayList<String> typeList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				return rs.getString(1);
			}
		});

		System.out.println(typeList);
		return typeList;

	}
	
	public HashMap<String,Object> validationForKnockoff(RefundTTUMBean beanObj)
	{
		String table_Name = null;
		HashMap<String,Object> output = new HashMap<String,Object>();
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				table_Name = "RUPAY_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				table_Name = "VISA_TTUM_UNMATCHED";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				table_Name = "MASTER_POS_TTUM_UNMATCHED";
			}
			
			String checkProcessing = "SELECT COUNT(*) FROM "+table_Name+" WHERE FILEDATE = ? AND SYSTEM_REMARKS = 'UNMATCHED'";
			
			int count = getJdbcTemplate().queryForObject(checkProcessing, new Object[] {beanObj.getFileDate()},Integer.class);
			
			if(count > 0)
			{
				output.put("result", true);
				
			}
			else
			{
				output.put("result", false);
				output.put("msg", "No data in refund Unmatch table");
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in validateRefundProcess "+e);
			output.put("result", false);
			output.put("msg", "No data in refund Unmatch table");
		}
		return output;
	}
	
	public HashMap<String, Object> moveUnmatchedData(MultipartFile file,RefundTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap();
		BufferedReader br =null;
		String query = "";
		String deleteQuery = "";
		ArrayList<String> columns = null;
		if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
		{
			query = "INSERT INTO MASTER_POS_TTUM_MATCHED (";
			deleteQuery = "DELETE FROM MASTER_POS_TTUM_UNMATCHED WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
			columns = getColumnList("MASTER_POS_TTUM_MATCHED");
		}
		else
		{
			query = "INSERT INTO "+beanObj.getCategory()+"_TTUM_MATCHED (";
			deleteQuery = "DELETE FROM "+beanObj.getCategory()+"_TTUM_UNMATCHED WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
			columns = getColumnList(beanObj.getCategory()+"_TTUM_MATCHED");
		}
		
		int count= 0;
		String sel_col = "";
		PreparedStatement ps = null;
		PreparedStatement dele_ps =null;
		String move_condition ="";
		String del_condition = "";
		try
		{
			
			for(String column : columns)
			{
				if(!column.equalsIgnoreCase("SYSTEM_REMARKS") && !column.equalsIgnoreCase("MATCH_CYCLE"))
				{
					if(count == 0)
					{
						query = query+column;
						sel_col = sel_col+column;
					}
					else
					{	
						query = query+ ","+column;
						sel_col = sel_col+","+column;
					}
					count++;
				}
			}
			if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				query = query +",SYSTEM_REMARKS ) SELECT "+sel_col+",'MOVED' FROM MASTER_POS_TTUM_UNMATCHED WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
			}
			else
			{	
				query = query +",SYSTEM_REMARKS ) SELECT "+sel_col+",'MOVED' FROM "+beanObj.getCategory()+"_TTUM_UNMATCHED WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
			}
			br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String stline=br.readLine();
			//GETTING COLUMNS
			String[] cols = stline.split(Pattern.quote("|"));
			count = 0;
			
			while((stline = br.readLine()) != null && !stline.equalsIgnoreCase(""))
			{
				move_condition = "";
				del_condition = "";
				count++;
				String values[] = stline.split(Pattern.quote("|"));
				for(int i = 0; i< cols.length ; i++)
				{
					if(values[i]  != null)
					{
						move_condition = move_condition + " AND "+cols[i].trim()+" = '"+values[i].trim()+"'";
						del_condition = del_condition+" AND "+cols[i].trim()+" = '"+values[i].trim()+"'";
					}
					else
					{
						move_condition = move_condition +" AND "+cols[i].trim()+ " IS NULL";
						del_condition = del_condition+" AND "+cols[i].trim()+" IS NULL";
					}
				}
				ps = getConnection().prepareStatement(query+move_condition);
				logger.info("Query is "+query+move_condition);
				ps.setString(1, beanObj.getFileDate());
				boolean flag = ps.execute();
				
				dele_ps = getConnection().prepareStatement(deleteQuery+del_condition);
				logger.info("delete query is "+deleteQuery+del_condition);
				dele_ps.setString(1, beanObj.getFileDate());
				dele_ps.execute();
				
				System.out.println("Query is "+query+move_condition);
			}
			
			logger.info("After Moving data");
			output.put("result", true);
			output.put("msg", count+" Records are Moved Successfully!");
		}
		catch(Exception e)
		{
			logger.info("Exception in moveUnmatchedData " +e);
			output.put("result", false);
			output.put("msg", "Exception while moving the records");
			
		}
		return output;
	}
	
	@Override
	public HashMap<String, Object> knockoffData(MultipartFile file, RefundTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap();
		String updateQuery = "";
		int updated_count = 0;
		if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
		{
			updateQuery = "UPDATE MASTER_POS_TTUM_UNMATCHED SET SYSTEM_REMARKS = ? WHERE SYSTEM_REMARKS = 'UNMATCHED' AND FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
		}
		else
		{
			updateQuery = "UPDATE "+beanObj.getCategory()+"_TTUM_UNMATCHED SET SYSTEM_REMARKS = ? WHERE SYSTEM_REMARKS = 'UNMATCHED' AND FILEDATE = TO_DATE(?,'DD/MON/YYYY') ";
		}
		BufferedReader br =null;
		String update_cond = "";
		PreparedStatement ps = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			String stline=br.readLine();
			String[] columns = stline.split(Pattern.quote("|"));
			
			while((stline = br.readLine()) != null)
			{
				if(!stline.equalsIgnoreCase(""))
				{
					update_cond  = "";
					String[] values = stline.split(Pattern.quote("|"));
					for(int i =0 ; i<columns.length ; i++)
					{

						update_cond = update_cond +" AND "+columns[i].trim()+" = '"+values[i].trim()+"'";
					}

					logger.info("Query is "+updateQuery+update_cond);
					ps = getConnection().prepareStatement(updateQuery+update_cond);
					ps.setString(1, beanObj.getNewRemarks());
					ps.setString(2, beanObj.getFileDate());
					ps.execute();
				}
			}
			
			logger.info("Updation completed");
			//GETTING COUNT
			if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				updated_count = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MASTER_POS_TTUM_UNMATCHED WHERE FILEDATE = ? AND SYSTEM_REMARKS = ?", 
						new Object[] {beanObj.getFileDate(),beanObj.getNewRemarks()},Integer.class);
			}
			else
			{
				updated_count = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM "+beanObj.getCategory()+"_TTUM_UNMATCHED WHERE FILEDATE = ? AND SYSTEM_REMARKS = ?", 
											new Object[] {beanObj.getFileDate(),beanObj.getNewRemarks()},Integer.class);
			}
			
			
			output.put("result", true);
			output.put("msg", "Total "+updated_count+" records are Knocked Off !");
		}
		catch(Exception e)
		{
			logger.info("Exception in knockoffData "+e);
			output.put("result", true);
			output.put("msg", "Issue while knocking Off Data !");
		}
		return output;
	}
	
	@Override
	public HashMap<String, Object> getRefundCountAmount(RefundTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String fileName = beanObj.getCategory();
			if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
			{
				fileName = "POS";
			}
			
			// Raw File is Uploaded or not
			String rawTable = getJdbcTemplate().queryForObject("select DISTINCT tablename from main_filesource where filename = ? AND file_category = ?", 
							new Object[] {fileName,beanObj.getCategory()},String.class);
			logger.info("Raw Table name is "+rawTable);
			String getData = "SELECT COUNT(1) FROM "+rawTable.toLowerCase()+" where filedate = ? and ";
			String getAmount = null;
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				getData = "select count(case_number) from(select distinct acquirer_reference_data, amount_transaction ,case_number from "+rawTable.toLowerCase()
					+" where filedate = ? and ";
				getData = getData+" txnfunction_code = '262') as a";
				getAmount = "select sum(amount_transaction) from(select distinct acquirer_reference_data, amount_transaction ,case_number from "
						+rawTable.toLowerCase()+" where filedate = ? and   txnfunction_code = '262') as a";
			}
			else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				getData = getData + " tc IN (06,26) ";
				getAmount = "select round(sum(destination_amount),2) from "+rawTable.toLowerCase()
					+" where filedate = ? and  tc IN (06,26) ";
			}
			
			logger.info("Count query is "+getData);
			int count = getJdbcTemplate().queryForObject(getData, new Object[] {beanObj.getFileDate()},Integer.class);
			
			logger.info("Amount query is "+getAmount);
			Double amount = getJdbcTemplate().queryForObject(getAmount, new Object[] {beanObj.getFileDate()},Double.class);
			
			logger.info("count = "+count);
			
			logger.info("Amount = "+amount);
			
			output.put("result", true);
			output.put("msg", count+"|"+amount);			
		}
		catch(Exception e)
		{
			logger.info("Exception while getting amount and count "+ e);
			output.put("result", false);
			output.put("msg", "Exception Occurred while getting refund count and amount");
			
		}
		return output;
	}
	
/**** FULL REFUND TTUM ****/
	public HashMap<String, Object> checkFullTTUMProcess(RefundTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				
				String checkCount = "select count(1) from full_"+beanObj.getCategory().toLowerCase()+"_refund_ttum where filedate = str_to_date(?,'%Y/%m/%d')";

				int getCount = getJdbcTemplate().queryForObject(checkCount, new Object[] {beanObj.getFileDate()},Integer.class);

				if(getCount == 0)
				{
					//output.put("result", true);

					//1. compare and check dispute table and raw files
					String getdisputeFileCount = "select count(1) from rupay_network_adjustment where filedate = str_to_date(?,'%Y/%m/%d')";
					int dispCount = getJdbcTemplate().queryForObject(getdisputeFileCount, new Object[] {beanObj.getFileDate()},Integer.class);

					if(dispCount == 0)
					{
						output.put("result", false);
						output.put("msg", "No dispute files are uploaded");
					}
					else
					{
						// check disp and raw files
						String getDispCycleCount = "select count(cycle) from (select distinct cycle from rupay_network_adjustment where "+
								" filedate = str_to_date(?,'%Y/%m/%d') and function_code = '262') as a";

						String getRawCycleCount = "select count(cycle) from (select distinct substr(unique_file_name,3,1) as cycle "+
								"from rupay_rupay_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and unique_file_name not like '%722%'"
								+" and txnfunction_code = '262') as a";

						int dispCycleCount = getJdbcTemplate().queryForObject(getDispCycleCount, new Object[] {beanObj.getFileDate()}, Integer.class);
						int rawCycleCount = getJdbcTemplate().queryForObject(getRawCycleCount, new Object[] {beanObj.getFileDate()} ,Integer.class);

						if(dispCycleCount != rawCycleCount)
						{
							output.put("result", false);
							output.put("msg", "Mismatch in rupay raw and dispute cycles");
						}
						else
						{
							output.put("result"	, true);
						}
					}
				}
				else
				{
					output.put("result", false);
					output.put("msg", "TTUM is already processed");
				}
				
				
			}	
			else
			{
				String checkCount = "select count(1) from full_"+beanObj.getCategory().toLowerCase()+"_refund_ttum where filedate = str_to_date(?,'%Y/%m/%d')";

				int getCount = getJdbcTemplate().queryForObject(checkCount, new Object[] {beanObj.getFileDate()},Integer.class);

				if(getCount == 0)
				{
					output.put("result", true);
				}
				else
				{
					output.put("result", false);
					output.put("msg", "TTUM is already processed");
				}
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in checkFullTTUMProcess "+e);
			output.put("result", false);
			output.put("msg", "TTUM is already processed");
		}
		return output;
	}
	
	@Override
	public boolean runFullRefundTTUMGeneration(RefundTTUMBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			FullRefundTTUMGeneration ttumexe = new FullRefundTTUMGeneration(getJdbcTemplate());
			inParams.put("FILE_DATE", beanObj.getFileDate());
			inParams.put("CATEGORY", beanObj.getCategory());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			outParams = ttumexe.execute(inParams);
			if(outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			else
			{
				return true;
			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		
	}
	
	private class FullRefundTTUMGeneration extends StoredProcedure{
		private static final String insert_proc = "full_refund_ttum";
		public FullRefundTTUMGeneration(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILE_DATE",Types.VARCHAR));
			declareParameter(new SqlParameter("CATEGORY",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	/**** FULL REFUND TTUM ****/
	public HashMap<String, Object> validateFullTTUMProcess(RefundTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				
				String checkCount = "select count(1) from full_"+beanObj.getCategory().toLowerCase()+"_refund_ttum where filedate = str_to_date(?,'%Y/%m/%d')";

				int getCount = getJdbcTemplate().queryForObject(checkCount, new Object[] {beanObj.getFileDate()},Integer.class);

				if(getCount == 0)
				{
					output.put("result", true);
					
				}
				else
				{
					output.put("result", false);
					output.put("msg", "TTUM is already processed");
				}
				
				
			}	
			else
			{
				String checkCount = "select count(1) from full_"+beanObj.getCategory().toLowerCase()+"_refund_ttum where filedate = str_to_date(?,'%Y/%m/%d')";

				int getCount = getJdbcTemplate().queryForObject(checkCount, new Object[] {beanObj.getFileDate()},Integer.class);

				if(getCount == 0)
				{
					output.put("result", true);
				}
				else
				{
					output.put("result", false);
					output.put("msg", "TTUM is already processed");
				}
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in checkFullTTUMProcess "+e);
			output.put("result", false);
			output.put("msg", "TTUM is already processed");
		}
		return output;
	}
	
	public List<Object> getFullRefundTTUMData(RefundTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		try
		{
			
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				ttum_tableName =  "full_rupay_refund_ttum";
			}
			
			String getRupayData = null;
			String getNCMCData = null;
			final List<String> cols  = getTTUMColumnList(ttum_tableName);
			
			getRupayData = "select account_number as account_number,part_tran_type,"
					+ "transaction_amount as transaction_amount,"
					+ "transaction_particular as transaction_particular ,"
					+ "reference_number as remarks"
					+ ",date_format(str_to_date(sysdate(),'%d-%m-%Y'),'%d/%m/%Y') as filedate, cycle from "+ttum_tableName
					+" where filedate = str_to_date(?,'%Y/%m/%d') ";
			
				logger.info("Getdata query is "+getRupayData);
				
			List<Object> DailyData1= getJdbcTemplate().query(getRupayData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						table_Data.put("account_number", rs.getString("account_number"));
						table_Data.put("part_tran_type", rs.getString("part_tran_type"));
						table_Data.put("transaction_amount", rs.getString("transaction_amount"));
						table_Data.put("transaction_particular", rs.getString("transaction_particular"));
						table_Data.put("remarks", rs.getString("remarks"));
						table_Data.put("filedate", rs.getString("filedate"));
						table_Data.put("cycle", rs.getString("cycle"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			/*getNCMCData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+ "TRANSACTION_AMOUNT AS TRANSACTION_AMOUNT,"
					+ "TRANSACTION_PARTICULAR AS TRANSACTION_PARTICULAR ,"
					+ "REFERENCE_NUMBER AS REMARKS"
					+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE, CYCLE FROM "+ttum_tableName
					+" WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') AND tran_type= 'NCMC'";			
			
			
			List<Object> DailyData2= getJdbcTemplate().query(getNCMCData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("CYCLE", rs.getString("CYCLE"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});*/
			
			
			data.add(DailyData1);

			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getRefundTTUMData "+e);
			return null;

		}
		
	}
	
	public List<Object> getVisaFullRefundTTUMData(RefundTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		try
		{
			
			if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				ttum_tableName =  "full_rupay_refund_ttum";
			}
		   else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				ttum_tableName =  "full_visa_refund_ttum";
			}
			
			String getDomData = null;
			String getIntData = null;
			final List<String> cols  = getTTUMColumnList(ttum_tableName);
			
			getDomData = "select rpad(account_number,14,' ')  as account_number,part_tran_type,"
					+ "lpad(transaction_amount,17,' ') as transaction_amount,"
					+ "rpad(transaction_particular,30,' ') as transaction_particular ,"
					+ "lpad(ifnull(reference_number,' '),16,' ') as remarks"
					+ ", filedate from "+ttum_tableName
					+" where filedate = str_to_date(?,'%Y/%m/%d')";
			
				logger.info("Getdata query is "+getDomData);
				
			List<Object> DailyData1 = getJdbcTemplate().query(getDomData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						/*for(String column: cols)
						{*/
						table_Data.put("account_number", rs.getString("account_number"));
						table_Data.put("part_tran_type", rs.getString("part_tran_type"));
						table_Data.put("transaction_amount", rs.getString("transaction_amount"));
						table_Data.put("transaction_particular", rs.getString("transaction_particular"));
						table_Data.put("remarks", rs.getString("remarks"));
						table_Data.put("filedate", rs.getString("filedate"));
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			

			data.add(DailyData1);
			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getRefundTTUMData "+e);
			return null;

		}
		
	}
	
	public void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String TTUMName,HttpServletResponse response )
	{

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try
		{
			logger.info("Filename is "+FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			
			/*File file = new File(stPath+File.separator+FileName);
			if(file.exists())
			{
				FileUtils.forceDelete(file);
			}
			file.createNewFile();*/

						
			OutputStream fileOut = new FileOutputStream(stPath+File.separator+FileName);   
			
			HSSFWorkbook workbook = new HSSFWorkbook();
	        HSSFSheet sheet = workbook.createSheet("Report");   
			
	     // create header row
	    	HSSFRow header = sheet.createRow(0);
	    	
	    	for(int i =0 ;i < Excel_Headers.size(); i++)
	    	{
	    		header.createCell(i).setCellValue(Excel_Headers.get(i));
	    	}
	    	
	    	HSSFRow rowEntry;
	    	
	    	for(int i =0; i< TTUMData.size() ; i++)
	    	{
	    		rowEntry = sheet.createRow(i+1);
	    		Map<String, String> map_data =  (Map<String, String>) TTUMData.get(i);
	    		if(map_data.size()>0)
	    		{

	    			for(int m= 0 ;m < Excel_Headers.size() ; m++)
	    			{
	    				
	    						
	    					rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
	    			}
	    		}

	    	}
	    	
	    	workbook.write(fileOut);
	    	fileOut.close();
	    	
	    	File file = new File(stPath);
	    	String[] filelist = file.list();
	    	
	    	for(String Names : filelist )
	    	{	
	    		logger.info("name is "+Names);
	    		files.add(stPath+File.separator+Names);
	    	}
	    	FileOutputStream fos = new FileOutputStream(stPath+File.separator+ TTUMName+".zip");
			ZipOutputStream   zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
	           try
	           {
	        	   for(String filespath : files)
	        	   {
	        		   File input = new File(filespath);
	        		   fis = new FileInputStream(input);
	        		   ZipEntry ze = new ZipEntry(input.getName());
	        		  // System.out.println("Zipping the file: "+input.getName());
	        		   zipOut.putNextEntry(ze);
	        		   byte[] tmp = new byte[4*1024];
	        		   int size = 0;
	        		   while((size = fis.read(tmp)) != -1){
	        			   zipOut.write(tmp, 0, size);
	        		   }
	        		   zipOut.flush();
	        		   fis.close();
	        	   }
	        	   zipOut.close();
	        	 //  System.out.println("Done... Zipped the files...");
	           }
	           catch(Exception fe)
	           {
	        	   System.out.println("Exception in zipping is "+fe);
	           }
			/*int startLine = 0;
			for(int i =0 ;i<TTUMData.size(); i++)
			{
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);

				if(startLine > 0)
				{
					out.write("\n");
				}
				startLine++;
				lineData = new StringBuffer();
				lineData.append(table_Data.get("ACCOUNT_NUMBER")+"|"+table_Data.get("PART_TRAN_TYPE"));
				lineData.append("|"+table_Data.get("TRANSACTION_AMOUNT")+"|"+table_Data.get("TRANSACTION_PARTICULAR")+"|"+TTUMName);
				//logger.info(lineData.toString());
				out.write(lineData.toString());	

			}

			out.flush();
			out.close();*/
		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );

		}


	}
	
	
	public void generateRupayRefund(String stPath,List<Object> ExcelData,String zipName,HttpServletResponse response)
	{

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try
		{
			logger.info("IN RUPAY DOWNLOAD");
	    	File file = new File(stPath);
	    	String[] filelist = file.list();
	    	
	    	for(String Names : filelist )
	    	{	
	    		logger.info("in serviceImpl name is "+Names);
	    		files.add(stPath+File.separator+Names);
	    	}
	    	
	    	logger.info("Before zipping files ");
	    	
	    	FileOutputStream fos = new FileOutputStream(stPath+File.separator+ zipName+".zip");
			ZipOutputStream   zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
	           try
	           {
	        	   for(String filespath : files)
	        	   {
	        		   logger.info("filepath is "+filespath);
	        		   File input = new File(filespath);
	        		   fis = new FileInputStream(input);
	        		   ZipEntry ze = new ZipEntry(input.getName());
	        		   logger.info("Zipping the file: "+input.getName());
	        		   zipOut.putNextEntry(ze);
	        		   byte[] tmp = new byte[4*1024];
	        		   int size = 0;
	        		   while((size = fis.read(tmp)) != -1){
	        			   zipOut.write(tmp, 0, size);
	        		   }
	        		   zipOut.flush();
	        		   fis.close();
	        	   }
	        	   zipOut.close();
	        	   fos.close();
	        	   logger.info("Done... Zipped the files...");
	           }
	           catch(Exception fe)
	           {
	        	   System.out.println("Exception in zipping is "+fe);
	           }
	           logger.info("zipping completed ");
		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );

		}


	}
	
public boolean RefundRollBack(RefundTTUMBean beanObj)	
{
	String tableName = null;
	
	try
	{
		if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
		{
			tableName = "full_rupay_refund_ttum";
		}
		else
		{
			tableName = "full_visa_refund_ttum";
		}
		String delQuery = "delete from "+tableName+" where filedate = str_to_Date('"+beanObj.getFileDate()+"','%Y/%m/%d')";
		getJdbcTemplate().execute(delQuery);
	}
	catch(Exception e)
	{
		logger.info("Exception in RefundRollBack "+e);
		return false;
	}
	return true;
	
}

/*
@Override
public List<String> getRefundVoucher(RefundTTUMBean beanObj)
{
	List<String> Data = new ArrayList<String>();
	try
	{
		String getData = "";

		getData = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual "
				+" union all "
				+"select concat('201',rpad(account_number,16,' '),'0',substr(account_number,1,5),'1408',"
				+"		date_format(sysdate(),'%Y%m%d'),'C',date_format(sysdate(),'%Y%m%d'),'00101',"
				+"		case when transaction_amount not like '%.%' then "
				+" lpad(concat(transaction_amount,'00'),'14','0') "
				+" else "
				+" lpad(replace(cast(transaction_amount as decimal(20,2)),'.',''),14,'0') "
				+" end, case when transaction_amount not like '%.%'  "
				+" then  lpad(round(transaction_amount*100),14,'0') "  
				+"  else  lpad(round(transaction_amount*100),14,'0') "
				+ " end,'000000010000000000000',"
				+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',account_number)"
				+"		 from full_visa_refund_ttum where filedate = '"+beanObj.getFileDate()+"' and part_tran_type = 'C'"
				+"		 union all"
				+"		 select concat(rpad(account_number,19,' '),'009990','1005',"
				+"		date_format(sysdate(),'%Y%m%d'),'D',date_format(sysdate(),'%Y%m%d'),'00101',"
				+"		lpad(round(transaction_amount*100),14,'0'),lpad(round(transaction_amount*100),14,'0'),'000000010000000000000',"
				+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',lpad(substr(account_number,-9),15,0))"
				+"		 from full_visa_refund_ttum where filedate = '"+beanObj.getFileDate()+"' "
						+ "and part_tran_type = 'D'"
				+"		union all "
				+"		  select  concat('3000000001',lpad(round((sum(transaction_amount))*100),15,'0'),"
				+"		 lpad(count(1),9,'0'),lpad(round((sum(transaction_amount))*100),15,'0'))" 
				+"		 from full_visa_refund_ttum where filedate = '"+beanObj.getFileDate()+"'"
						+ " and part_tran_type = 'C'";
		
		logger.info("getData is "+getData);
		

		Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<String>>(){
			public List<String> extractData(ResultSet rs)throws SQLException {
				List<String> beanList = new ArrayList<String>();
				
				while (rs.next()) {
					
					beanList.add(rs.getString(1));
				}
				return beanList;
			}
		});
		
		return Data;
	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}	*/

@Override
public List<String> getRefundVoucher(RefundTTUMBean beanObj)
{
	List<String> Data = new ArrayList<String>();
	String ttum_tableName = "";
	
	String getData = "";
	try
	{
		if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
		{
			ttum_tableName =  "full_rupay_refund_ttum";
			getData = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual "
					+" union all "
					+"select concat('201',rpad(account_number,16,' '),'0',substr(account_number,1,5),'1408',"
					+"		date_format(sysdate(),'%Y%m%d'),'C',date_format(sysdate(),'%Y%m%d'),'00101',"
					+"		case when transaction_amount not like '%.%' then "
					+" lpad(concat(transaction_amount,'00'),'14','0') "
					+" else "
					+" lpad(replace(cast(transaction_amount as decimal(20,2)),'.',''),14,'0') "
					+" end, case when transaction_amount not like '%.%'  "
					+" then  lpad(round(transaction_amount*100),14,'0') "  
					+"  else  lpad(round(transaction_amount*100),14,'0') "
					+ " end,'000000010000000000000',"
					+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',account_number)"
					+"		 from "+ttum_tableName+
					" where filedate = '"+beanObj.getFileDate()+"' and part_tran_type = 'C'"
					+"		 union all"
					+"		 select concat(rpad(account_number,19,' '),'009990','1005',"
					+"		date_format(sysdate(),'%Y%m%d'),'D',date_format(sysdate(),'%Y%m%d'),'00101',"
					+"		lpad(round(transaction_amount*100),14,'0'),lpad(round(transaction_amount*100),14,'0'),'000000010000000000000',"
					+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',lpad(substr(account_number,-9),15,0))"
					+"		 from "+ttum_tableName+" where filedate = '"+beanObj.getFileDate()+"' "
							+ "and part_tran_type = 'D'"
					+"		union all "
					+"		  select  concat('3000000001',lpad(round((sum(transaction_amount))*100),15,'0'),"
					+"		 lpad(count(1),9,'0'),lpad(round((sum(transaction_amount))*100),15,'0'))" 
					+"		 from "+ttum_tableName+" where filedate = '"+beanObj.getFileDate()+"'"
							+ " and part_tran_type = 'C'";
		}
	   else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
		{
			ttum_tableName =  "full_visa_refund_ttum";
			getData = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual "
					+"\n union all \n"
					+"select concat(case when transaction_particular like '%Planet Tax%' "
					+" then concat(rpad('203100780011',19,' '),'009990','1005') else "
					+ " concat('201',rpad(account_number,16,' '),"
					+ "'0',substr(account_number,1,5),'1408') end,"
					+"		date_format(sysdate(),'%Y%m%d'),'C',date_format(sysdate(),'%Y%m%d'),'00101',"
					+"		case when transaction_amount not like '%.%' then "
					+" lpad(concat(transaction_amount,'00'),'14','0') "
					+" else "
					+" lpad(replace(cast(transaction_amount as decimal(20,2)),'.',''),14,'0') "
					+" end, case when transaction_amount not like '%.%'  "
					+" then  lpad(round(transaction_amount*100),14,'0') "  
					+"  else  lpad(round(transaction_amount*100),14,'0') "
					+ " end,'000000010000000000000',"
					+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',account_number)"
					+"		 from "+ttum_tableName+
					" where filedate = '"+beanObj.getFileDate()+"' and part_tran_type = 'C' "
					+"\n		 union all \n"
					+"		 select concat(rpad(account_number,19,' '),'009990','1005',"
					+"		date_format(sysdate(),'%Y%m%d'),'D',date_format(sysdate(),'%Y%m%d'),'00101',"
					+"		lpad(round(transaction_amount*100),14,'0'),lpad(round(transaction_amount*100),14,'0'),'000000010000000000000',"
					+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',lpad(substr(account_number,-9),15,0))"
					+"		 from "+ttum_tableName+" where filedate = '"+beanObj.getFileDate()+"' "
							+ "and part_tran_type = 'D'"
					+"	\n	union all \n "
					+"		  select  concat('3000000001',lpad(round((sum(transaction_amount))*100),15,'0'),"
					+"		 lpad(count(1),9,'0'),lpad(round((sum(transaction_amount))*100),15,'0'))" 
					+"		 from "+ttum_tableName+" where filedate = '"+beanObj.getFileDate()+"'"
							+ " and part_tran_type = 'C'";
		}
		
		logger.info("getData is "+getData);
		

		Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<String>>(){
			public List<String> extractData(ResultSet rs)throws SQLException {
				List<String> beanList = new ArrayList<String>();
				
				while (rs.next()) {
					
					beanList.add(rs.getString(1));
				}
				return beanList;
			}
		});
		
		return Data;
	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}	


public List<Object> getRefundVoucherForRupay(RefundTTUMBean beanObj)
{
	List<Object> Data = new ArrayList<Object>();
	try
	{
		String getData = "";

		getData = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual "
				+" union all "
				+"select concat('201',rpad(account_number,16,' '),'0',substr(account_number,1,5),'1408',"
				+"		date_format(sysdate(),'%Y%m%d'),'C',date_format(sysdate(),'%Y%m%d'),'00101',"
				+"		case when transaction_amount not like '%.%' then "
				+" lpad(concat(transaction_amount,'00'),'14','0') "
				+" else "
				+" lpad(replace(cast(transaction_amount as decimal(20,2)),'.',''),14,'0') "
				+" end, case when transaction_amount not like '%.%'  "
				+" then  lpad(round(transaction_amount*100),14,'0') "  
				+"  else  lpad(round(transaction_amount*100),14,'0') "
				+ " end,'000000010000000000000',"
				+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',account_number)"
				+"		 from full_rupay_refund_ttum where filedate = '"+beanObj.getFileDate()+"' and part_tran_type = 'C'"
				+"		 union all"
				+"		 select concat(rpad(account_number,19,' '),'009990','1005',"
				+"		date_format(sysdate(),'%Y%m%d'),'D',date_format(sysdate(),'%Y%m%d'),'00101',"
				+"		lpad(round(transaction_amount*100),14,'0'),lpad(round(transaction_amount*100),14,'0'),'000000010000000000000',"
				+"		'           ', rpad(transaction_particular,501,' '),'300000000000N0',lpad(substr(account_number,-9),15,0))"
				+"		 from full_rupay_refund_ttum where filedate = '"+beanObj.getFileDate()+"' "
						+ "and part_tran_type = 'D'"
				+"		union all "
				+"		  select  concat('3000000001',lpad(round((sum(transaction_amount))*100),15,'0'),"
				+"		 lpad(count(1),9,'0'),lpad(round((sum(transaction_amount))*100),15,'0'))" 
				+"		 from full_rupay_refund_ttum where filedate = '"+beanObj.getFileDate()+"'"
						+ " and part_tran_type = 'C'";
		
		logger.info("getData is "+getData);
		

		Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					
					beanList.add(rs.getString(1));
				}
				return beanList;
			}
		});
		
		return Data;
	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}

}
