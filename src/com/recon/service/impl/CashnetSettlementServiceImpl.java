package com.recon.service.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.control.NFSSettlementTTUMController;
import com.recon.model.NFSSettlementBean;
import com.recon.service.CashnetSettlementService;
import com.recon.util.ReadCASHNETAdjustmentFile;
import com.recon.util.ReadNFSAdjustmentFile;

public class CashnetSettlementServiceImpl extends JdbcDaoSupport implements CashnetSettlementService {
	
	private static final Logger logger = Logger.getLogger(CashnetSettlementServiceImpl.class);
	
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public HashMap<String, Object> validationForMonthlyInterchange(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> validate = new HashMap<String, Object>();
		int file_id = 0;
		//1. CHECK WHETHER INTERCHANGE IS ALREADY PROCESSED
		String getDataCount = "SELECT COUNT(*) FROM CASHNET_INTERCHANGE1 WHERE FILEDATE = TO_CHAR(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
		
		int dataCount = getJdbcTemplate().queryForObject(getDataCount, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(dataCount == 0)
		{
			//GETTING NUMBER OF DAYS 
			try
			{
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getCategory(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
				System.out.println("File id is "+file_id);
			}
			catch(Exception e)
			{
				validate.put("result", true);
				validate.put("msg", "File is not Configured!!!");
				return validate;
			}
			//temporary this code
			String getDaysCount = "select COUNT(*) from main_file_upload_dtls where to_char(filedate,'MM/RRRR' ) = ? AND CATEGORY = ? AND FILEID = ? and comapre_flag = 'Y'";
			int daysCount = getJdbcTemplate().queryForObject(getDaysCount, new Object [] {beanObj.getDatepicker(),beanObj.getCategory(),file_id},Integer.class);
			logger.info("Days Count is "+daysCount);
			//after march take end of month date and check the count in raw table
			
			if(daysCount == 0)
			{
				validate.put("result", false);
				validate.put("msg", "Recon is not processed for all days!");
			}
			else
			{
				//2. CHECK WHETHER RAW FILES ARE UPLOADED
				String getRawTable = "select TABLENAME  from main_filesource WHERE FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILENAME = ?";
				String rawTable= getJdbcTemplate().queryForObject(getRawTable, new Object [] {beanObj.getCategory(),beanObj.getStSubCategory(),beanObj.getCategory()},String.class);

				String getRawData = "select count(filedate) from (SELECT distinct filedate FROM "+rawTable+" where TO_CHAR(FILEDATE,'MM/RRRR') = ?)";
				int rawDates = getJdbcTemplate().queryForObject(getRawData, new Object[] {beanObj.getDatepicker()}, Integer.class);
				logger.info("raw Dates count "+rawDates);

				if(daysCount == rawDates)
				{
					logger.info("days Count "+daysCount);
					validate.put("result", true);
				}
				else
				{
					validate.put("result", false);
					validate.put("msg", "All Raw Files are not uploaded !!");
				}
			}
		}
		else
		{
			validate.put("result", false);
			validate.put("msg", "Interchange is already processed. Please download the reports");
			
		}
		return validate;
		
	}
	@Override
	public boolean runCashnetInterchange(NFSSettlementBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
				CashnetSettlementProc rollBackexe = new CashnetSettlementProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("FILEDT1", beanObj.getToDate());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		return true;
	
	}

	private class CashnetSettlementProc extends StoredProcedure{
		private static final String insert_proc = "CASHNET_INTERCHANGE";
		public CashnetSettlementProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("FILEDT1",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCAT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}	
	@Override
	public HashMap<String, Object> checkInterchangeProcess(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> validate = new HashMap<String, Object>();
		String checkData = "SELECT COUNT(*) FROM CASHNET_INTERCHANGE1 WHERE FILEDATE = TO_CHAR(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
		int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(dataCount > 0 )
		{
			validate.put("result", true);
			
		}
		else 
		{
			validate.put("result", false);
			validate.put("msg", "Settlement is not processed for selected Month !");
		}
		return validate;
	}
	@Override
	public List<Object> getInterchangeData(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getInterchange1 = null;
			String getInterchange2 = null;
			
				getInterchange1 = "SELECT * FROM CASHNET_interchange1 where filedate = to_char(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
				getInterchange2 ="SELECT * FROM CASHNET_interchange2 where TO_CHAR(filedate,'MONRRRR') = to_char(TO_DATE(?,'MM/YYYY'),'MONRRRR') ORDER BY FILEDATE";
			
				
			final List<String> Column_list  = getColumnList("CASHNET_interchange1");
			data.add(Column_list);


			List<Object> monthlyData= getJdbcTemplate().query(getInterchange1, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> table_data = new HashMap<String, String>();
						for(String column : Column_list)
						{
							table_data.put(column, rs.getString(column));
						}
						beanObj.add(table_data);
					}
					return beanObj;
				}
			});
			data.add(monthlyData);

			final List<String> Column_list2  = getColumnList("CASHNET_interchange2");
			 monthlyData= getJdbcTemplate().query(getInterchange2, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> table_data = new HashMap<String, String>();
						for(String column : Column_list2)
						{
							table_data.put(column, rs.getString(column));
						}
						beanObj.add(table_data);
					}
					return beanObj;
				}
			});
			data.add(Column_list2);
			data.add(monthlyData);
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getInterchangeData "+e);
			return null;

		}

	}
	public ArrayList<String> getColumnList(String tableName) {

		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = Database() AND TABLE_NAME = '"+tableName.toLowerCase()+"'";
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
	/******************* CODING FOR DAILY INTERCHNAGE ******************************/	
	@Override
	public HashMap<String, Object> validationForDailyInterchange(NFSSettlementBean beanObj)
	{

		HashMap<String,Object> validate = new HashMap<String, Object>();
		int file_id = 0;
		//1. CHECK WHETHER INTERCHANGE IS ALREADY PROCESSED
		String getDataCount = "SELECT COUNT(*) FROM CASHNET_DAILY_INTERCHANGE1 WHERE FILEDATE = ?";

		int dataCount = getJdbcTemplate().queryForObject(getDataCount, new Object[] {beanObj.getDatepicker()},Integer.class);

		if(dataCount == 0)
		{
			//GETTING NUMBER OF DAYS 
			try
			{
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getCategory(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
				System.out.println("File id is "+file_id);
			}
			catch(Exception e)
			{
				validate.put("result", false);
				validate.put("msg", "File is not Configured!!!");
				return validate;
			}
			//2. CHECK WHETHER RAW FILES ARE UPLOADED
			String getRawTable = "select TABLENAME  from main_filesource WHERE FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILENAME = ?";
			String rawTable= getJdbcTemplate().queryForObject(getRawTable, new Object [] {beanObj.getCategory(),beanObj.getStSubCategory(),beanObj.getCategory()},String.class);

			String getRawData = "select count(*) from "+rawTable+" where filedate = ?";
			int rawDates = getJdbcTemplate().queryForObject(getRawData, new Object[] {beanObj.getDatepicker()}, Integer.class);
			logger.info("raw Dates count "+rawDates);

			if(rawDates == 0)
			{
				logger.info("days Count "+rawDates);
				validate.put("result", false);
				validate.put("msg", "All Raw Files are not uploaded !!");
			}
			else
			{
				validate.put("result", true);
			}
		}
		else
		{
			validate.put("result", false);
			validate.put("msg", "Interchange has been already processed!");

		}
		return validate;
	}
	

@Override
	public boolean runCashnetDailyInterchange(NFSSettlementBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			CashnetDailySettlementProc rollBackexe = new CashnetDailySettlementProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		return true;
	
	}

	private class CashnetDailySettlementProc extends StoredProcedure{
		private static final String insert_proc = "CASHNET_DAILY_INCOME";
		public CashnetDailySettlementProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCAT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}	
	@Override
	public List<Object> getDailyInterchangeData(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getInterchange1 = null;
			String getInterchange2 = null;
			
				getInterchange1 = "SELECT * FROM CASHNET_DAILY_interchange1 where filedate = ?";
				getInterchange2 ="SELECT * FROM CASHNET_DAILY_interchange2 where filedate = ? ORDER BY FILEDATE";
			
				
			final List<String> Column_list  = getColumnList("CASHNET_Daily_interchange1");
			data.add(Column_list);


			List<Object> monthlyData= getJdbcTemplate().query(getInterchange1, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> table_data = new HashMap<String, String>();
						for(String column : Column_list)
						{
							table_data.put(column, rs.getString(column));
						}
						beanObj.add(table_data);
					}
					return beanObj;
				}
			});
			data.add(monthlyData);

			final List<String> Column_list2  = getColumnList("CASHNET_Daily_interchange2");
			 monthlyData= getJdbcTemplate().query(getInterchange2, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					while (rs.next()) {
						Map<String, String> table_data = new HashMap<String, String>();
						for(String column : Column_list2)
						{
							table_data.put(column, rs.getString(column));
						}
						beanObj.add(table_data);
					}
					return beanObj;
				}
			});
			data.add(Column_list2);
			data.add(monthlyData);
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getInterchangeData "+e);
			return null;

		}

	}
	@Override
	public HashMap<String, Object> checkDailyInterchangeProcess(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> validate = new HashMap<String, Object>();
		String checkData = "SELECT COUNT(*) FROM CASHNET_DAILY_INTERCHANGE1 WHERE FILEDATE = ?";
		int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(dataCount > 0 )
		{
			validate.put("result", true);
			
		}
		else 
		{
			validate.put("result", false);
			validate.put("msg", "Settlement is not processed for selected Date !");
		}
		return validate;
	}
/*************************** coding for cashnet TTUM ***********************/	
	@Override
	public HashMap<String, Object> validateForInterchangeTTUM(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> validate = new HashMap<String, Object>();
		String getDataCount = null;
		String checkTTUMProc = null;
		if(beanObj.getTimePeriod().equalsIgnoreCase("MONTHLY"))
		{
			getDataCount = "SELECT COUNT(*) FROM CASHNET_INTERCHANGE1 WHERE FILEDATE = TO_CHAR(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
		}
		else
		{
			getDataCount = "SELECT COUNT(*) FROM CASHNET_DAILY_INTERCHANGE1 WHERE FILEDATE = ?";
		}

		int dataCount = getJdbcTemplate().queryForObject(getDataCount, new Object[] {beanObj.getDatepicker()},Integer.class);
		if(dataCount == 0)
		{
			validate.put("result", false);
			validate.put("msg", "Please process Interchange First !");
		}
		else
		{
			// CHECK WHETHER TTUM IS ALREADY GENERATED
			if(beanObj.getTimePeriod().equalsIgnoreCase("MONTHLY"))
			{
				checkTTUMProc = "select count(*) from CASHNET_SETT_MONTHLY_TTUM where FILEDATE = TO_CHAR(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
			}
			else
			{
				checkTTUMProc = "select count(*) from CASHNET_SETT_DAILY_TTUM where FILEDATE = TO_DATE(?,'DD-MON-YYYY')";
			}
			int ttumCount = getJdbcTemplate().queryForObject(checkTTUMProc, new Object[] {beanObj.getDatepicker()},Integer.class);
			if(ttumCount >0)
			{
				validate.put("result", false);
				validate.put("msg", "TTUM is already processed. Please download the reports!");
			}
			else
			{
				validate.put("result", true);
			}
		}
		return validate;

	}
	@Override
	public boolean runInterchangeTTUM(NFSSettlementBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
				CashnetSettlementTTUMProc rollBackexe = new CashnetSettlementTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", "01/"+beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD", beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);

				if(outParams !=null && outParams.get("msg") != null)
				{
					return false;
				}
			
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		return true;
	
	}

	private class CashnetSettlementTTUMProc extends StoredProcedure{
		private static final String insert_proc = "CASHNET_SETTLEMENT_TTUM";
		public CashnetSettlementTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("TIMEPERIOD",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCAT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}	
	
	@Override
	public HashMap<String, Object> checkTTUMProcess(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> validate = new HashMap<String, Object>();
		String checkData = null;
		if(beanObj.getTimePeriod().equalsIgnoreCase("MONTHLY"))
		{
			checkData = "SELECT COUNT(*) FROM CASHNET_SETT_MONTHLY_TTUM WHERE FILEDATE = TO_CHAR(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
		}
		else
		{
			checkData = "select count(*) from CASHNET_SETT_DAILY_TTUM where FILEDATE = TO_DATE(?,'DD-MON-YYYY')";
		}
		int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(dataCount > 0 )
		{
			validate.put("result", true);
			
		}
		else 
		{
			validate.put("result", false);
			validate.put("msg", "TTUM is not processed! \n Please process TTUM First");
		}
		return validate;
	}
	@Override
	public List<Object> getTTUMData(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getData = null;
			//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
			final List<String> cols  = getTTUMColumnList("CASHNET_SETT_MONTHLY_TTUM");
			List<String> Column_list = new ArrayList<String>();
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
			Column_list.add("REFERENCE NUMBER");

			//final List<String> cols = Column_list;
			if(beanObj.getTimePeriod().equalsIgnoreCase("MONTHLY"))
			{
				getData = "SELECT * FROM CASHNET_SETT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'MM/YYYY'),'MONRRRR')";
			}
			else
			{
				getData = "SELECT * FROM CASHNET_SETT_DAILY_TTUM WHERE FILEDATE = TO_DATE(?,'DD-MON-YYYY')";
			}
				
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
				//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> table_Data = new HashMap<String, String>();
						for(String column: cols)
						{
							table_Data.put(column, rs.getString(column.replace(" ", "_")));
						}
						table_Data.put("CURRENCY CODE OF ACCOUNT NUMBER", "INR");
						table_Data.put("SERVICE OUTLET", "999");
						table_Data.put("ACCOUNT REPORT CODE", "");
						table_Data.put("REFERENCE CURRENCY CODE", "INR");
						table_Data.put("RATE CODE", "");
						beanList.add(table_Data);
					}
					return beanList;
				}
			});
			
			
			data.add(Column_list);
			data.add(DailyData);

			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getInterchangeData "+e);
			return null;

		}

	}
	
	public ArrayList<String> getTTUMColumnList(String tableName) {

		String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','CREATEDDATE','CREATEDBY','CYCLE')";
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
	
public HashMap<String, Object> uploadAdjustmentFile(NFSSettlementBean beanObj,MultipartFile file)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	
	try
	{
		ReadCASHNETAdjustmentFile cashnetAdjUpload = new ReadCASHNETAdjustmentFile(); 
		output = cashnetAdjUpload.fileupload(beanObj,file, getConnection());
		//output = cashnetAdjUpload.fileExcelupload(beanObj,file, getConnection());
		System.out.println("result is "+output);
		return output;
	}
	catch(Exception e)
	{
		logger.info("eXCEPTION IN adjustment file upload "+e);
		output.put("result", false);
		output.put("msg", "Exception Occurred");
		return output;
	}
}
public HashMap<String, Object> CheckAdjustmentFileUpload(NFSSettlementBean beanObj)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	
	try
	{
		String checkUpload = "select count(*) from cashnet_chargeback where filedate = str_to_date(?,'%Y/%m/%d') "
						+ "	and filename = ?";// and sub_category = ?";
		int uploadedCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {beanObj.getDatepicker(), beanObj.getFileName()
							}, Integer.class);
		
		if(uploadedCount > 0)
		{
			output.put("result", false);
			output.put("msg", "File is already uploaded");
		}
		else
		{
			output.put("result", true);
		}
		
	}
	catch(Exception e)
	{
		logger.info("Exception in CheckAdjustmentFileUpload "+e);
		output.put("result", false);
		output.put("msg", "Exception while validating");
		
	}
	return output;
}

public HashMap<String, Object> CheckSettlementProcess(NFSSettlementBean beanObj)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	
	try
	{
		String query = "select count(1) from cashnet_settlement_report where filedate = str_to_date(?,'%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'";
		if(beanObj.getCycle()== 0)
		{
			query = "select count(1) from cashnet_settlement_report where filedate = str_to_date(?,'%Y/%m/%d')";
		}
		
		int count = getJdbcTemplate().queryForObject(query, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(count == 0)
		{
			output.put("result", false);
			output.put("msg", "Settlement Report is not processed");
		}
		else
		{
			output.put("result", true);
			output.put("msg", "Settlement Report is already processed");
		}
	}
	catch(Exception e)
	{
		logger.info("Exception while validating settlement "+e);
		output.put("result", false);
		output.put("msg", "Exception while validating settlement");
	}
	return output;
}

@Override
public HashMap<String, Object> CheckAdjNRawFileUpload(NFSSettlementBean beanObj)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	
	try
	{
		/*String checkUpload = "select count(*) from cashnet_chargeback where filedate = str_to_date(?,'%Y/%m/%d') ";
		
		int uploadedCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {beanObj.getDatepicker()
							}, Integer.class);
		*/
		
		//if(uploadedCount > 0)
		{
			String checkUpload = "select coalesce(SUM(file_count),0) from main_file_upload_dtls where filedate = str_to_date(?,'%Y/%m/%d') "
						+ "and fileid in (select fileid from main_filesource where filename = 'CASHNET')";
			
			int uploadedCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {beanObj.getDatepicker()},Integer.class);
			
			if(uploadedCount> 0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Please Upload cashnet Raw data first");
			}
			
			
		}
		/*else
		{
			output.put("result", false);
			output.put("msg", "Adjustment File is not uploaded");
		}*/
		
	}
	catch(Exception e)
	{
		logger.info("Exception in CheckAdjNRawFileUpload "+e);
		output.put("result", false);
		output.put("msg", "Exception while validating");
		
	}
	return output;
}

@Override
public boolean runCashnetSettlement(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
				CashnetSettProc rollBackexe = new CashnetSettProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("I_CYCLE",beanObj.getCycle());
				outParams = rollBackexe.execute(inParams);
				
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		return false;
	}
	return true;


}

private class CashnetSettProc extends StoredProcedure{
	private static final String insert_proc = "cashnet_settlement_process";
	public CashnetSettProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlParameter("I_CYCLE",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}
public HashMap<String, Object> CheckSettlementVoucher(NFSSettlementBean beanObj)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	try
	{
		String query = "select count(1) from cashnet_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
		
		int getCount = getJdbcTemplate().queryForObject(query, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(getCount > 0)
		{
			output.put("result", true);
			output.put("msg", "Settlement Voucher is already processed");
		}
		else
		{
			output.put("result", false);
			output.put("msg", "Settlement Voucher is not processed");
		}
	}
	catch(Exception e)
	{
		logger.info("Exception while validating "+e);
		output.put("result", false);
		output.put("msg", "Exception while validating ");
	}
	return output;
}

@Override
public boolean runCashnetSettlementVouch(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
				CashnetSettVoucherProc rollBackexe = new CashnetSettVoucherProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				outParams = rollBackexe.execute(inParams);
				
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		return false;
	}
	return true;


}

private class CashnetSettVoucherProc extends StoredProcedure{
	private static final String insert_proc = "cashnet_settlement_ttum_process";
	public CashnetSettVoucherProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

@Override
public List<Object> getCashnetSettlementReport(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = "";
		List<String> Column_list  = new ArrayList<String>();
		Column_list = getColumnList("cashnet_settlement_report");

		getData = "select * from cashnet_settlement_report where cycle = ? and filedate = str_to_date(?,'%Y/%m/%d') order by sr_no";
		
		data.add(Column_list);
	final List<String> columns  = Column_list;
	System.out.println("column value is "+columns.get(1));

		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getCycle(),beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> data = new HashMap<String, String>();
						for(String column : columns)
						{
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
				}
				return beanList;
			}
		});
		data.add(DailyData);

		return data;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}

@Override
public List<Object> getCashnetSettVoucher(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = "";
		List<String> Column_list  = new ArrayList<String>();
		Column_list = getColumnList("cashnet_settlement_ttum");

		getData = "SELECT * FROM cashnet_settlement_ttum WHERE filedate = str_to_date(?,'%Y/%m/%d') ORDER BY sr_no";
		
		data.add(Column_list);
	final List<String> columns  = Column_list;
	System.out.println("column value is "+columns.get(1));

		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> data = new HashMap<String, String>();
						for(String column : columns)
						{
							data.put(column, rs.getString(column));
						}
						beanList.add(data);
				}
				return beanList;
			}
		});
		data.add(DailyData);

		return data;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getCashnetSettVoucher "+e);
		return null;

	}

}

public boolean CashnetSettlementRollback(NFSSettlementBean beanObj)
{
	String deleteQuery = null;
	try
	{
		if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
		{
			deleteQuery = "delete from cashnet_settlement_report where filedate = '"+beanObj.getDatepicker()+"' "
					+ "	and cycle = '"+beanObj.getCycle()+"'";
			getJdbcTemplate().execute(deleteQuery);
			return true;
			
		}
		else if(beanObj.getFileName().equalsIgnoreCase("VOUCHER"))
		{
			deleteQuery = "delete from cashnet_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"' ";
			getJdbcTemplate().execute(deleteQuery);
			return true;
		}
	}
	catch(Exception e)
	{
		logger.info("Exception while rollback "+e);
		return false;
		
	}
	return true;
	
}
}

