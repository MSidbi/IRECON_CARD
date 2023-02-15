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

import com.recon.control.NFSSettlementController;
import com.recon.model.NFSSettlementBean;
import com.recon.service.NFSSettlementTTUMService;

public class NFSSettlementTTUMServiceImpl extends JdbcDaoSupport implements NFSSettlementTTUMService {
	
	private static final Logger logger = Logger.getLogger(NFSSettlementTTUMServiceImpl.class);
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
@Override
public HashMap<String, Object> validateMonthlySettlement(NFSSettlementBean beanObj)
{
	HashMap<String, Object>  mapObj = new HashMap<String, Object>();
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory()},Integer.class);
		
		String checkProc = "SELECT COUNT(*) FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE process_flag = 'Y' and FILEID = ? AND FILEDATE = ?";
		int checkCount = getJdbcTemplate().queryForObject(checkProc, new Object[] {file_id, beanObj.getDatepicker()},Integer.class);
		if(checkCount >0)
		{
			mapObj.put("result", true);
		}
		else
		{
			mapObj.put("result", false);
			mapObj.put("msg", "Settlement is not processed for selected Month");
		}
	}
	catch(Exception e)
	{
		logger.info("Exception in validateMonthlySettlement "+e);
		mapObj.put("result", false);
		mapObj.put("msg", "Exception Occurred");
	}
	return mapObj;
}

@Override
public boolean runNFSMonthlyTTUM(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		//FIRST CHECK WHETHER TTUM IS ALREADY GENERATED
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory()},Integer.class);
		int checkTTUMFlag = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE FILEID = ? AND FILEDATE = ? AND TTUM_FLAG = 'Y'", new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
		logger.info("TTUM Flag is "+checkTTUMFlag);
		if(checkTTUMFlag == 0)
		{
			if(beanObj.getFileName().contains("NFS"))
			{
				NFSSettMonthlyTTUMProc rollBackexe = new NFSSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			}
			else if(beanObj.getFileName().contains("DFS"))
			{

				DFSSettMonthlyTTUMProc rollBackexe = new DFSSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			}
			else if(beanObj.getFileName().contains("JCB"))
			{

				JCBSettMonthlyTTUMProc rollBackexe = new JCBSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			}
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
			else
			{
				//UPDATE PROCESSED FLAG
				
				getJdbcTemplate().execute("UPDATE MAIN_MONTHLY_NTSL_UPLOAD SET TTUM_FLAG = 'Y' WHERE FILEID = "+file_id+" AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY') ");
//				getJdbcTemplate().execute(ProcessFlag);
				return true;

			}
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
	//return true;


}
private class NFSSettMonthlyTTUMProc extends StoredProcedure{
	private static final String insert_proc = "NFS_SETTLEMENT_TTUM";
	public NFSSettMonthlyTTUMProc(JdbcTemplate jdbcTemplate)
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
private class DFSSettMonthlyTTUMProc extends StoredProcedure{
	private static final String insert_proc = "DFS_SETTLEMENT_TTUM";
	public DFSSettMonthlyTTUMProc(JdbcTemplate jdbcTemplate)
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
private class JCBSettMonthlyTTUMProc extends StoredProcedure{
	private static final String insert_proc = "JCB_SETTLEMENT_TTUM";
	public JCBSettMonthlyTTUMProc(JdbcTemplate jdbcTemplate)
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
public List<Object> getMonthlyTTUMData(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = null;
		//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
		/*final List<String> cols  = getColumnList("NFS_SETTLEMENT_MONTHLY_TTUM");
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
		Column_list.add("REFERENCE NUMBER");*/

		//final List<String> cols = Column_list;
		
		if(beanObj.getFileName().contains("NFS"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'DD/MON/YYYY'),'MONRRRR')";
		}
		else if(beanObj.getFileName().contains("DFS"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE, "
					+"LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM DFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'DD/MON/YYYY'),'MONRRRR')";
		}
		else if(beanObj.getFileName().contains("JCB"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE, "
					+"LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM JCB_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'DD/MON/YYYY'),'MONRRRR')";
		}
		
		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
			//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
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
		
		
		return DailyData;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}
public ArrayList<String> getColumnList(String tableName) {

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
//CODE FOR DAILY INTERCHANGE TTUM
@Override
public HashMap<String, Object> validateDailyInterchange(NFSSettlementBean beanObj)
{
	HashMap<String, Object>  mapObj = new HashMap<String, Object>();
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory()},Integer.class);
		
		String checkProc = "select count(*) from MAIN_SETTLEMENT_FILE_UPLOAD where fileid = ? and filedate = ? and interchange_flag = 'Y'";
		int checkCount = getJdbcTemplate().queryForObject(checkProc, new Object[] {file_id, beanObj.getDatepicker()},Integer.class);
		//GETTING CYCLE COUNT
		String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
		int cycleCount = getJdbcTemplate().queryForObject(getCycleCount, new Object[] {file_id},Integer.class);
		logger.info("Cycle count is "+cycleCount);
		
		if(checkCount >0 && checkCount == cycleCount)
		{
			mapObj.put("result", true);
		}
		else
		{
			mapObj.put("result", false);
			mapObj.put("msg", "Interchange Report is not processed for selected Date");
		}
	}
	catch(Exception e)
	{
		logger.info("Exception in validateDailyInterchange "+e);
		mapObj.put("result", false);
		mapObj.put("msg", "Exception Occurred");
	}
	return mapObj;
}
@Override
public boolean checkDailyInterchangeTTUMProcess(NFSSettlementBean beanObj)
{
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory()},Integer.class);
		String checkCount = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ? AND TTUM_FLAG = 'Y'";
		int getCount = getJdbcTemplate().queryForObject(checkCount, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
		if(getCount >0)
		{
				return true;
		}
		else
		{
			return false;
		}
	}
	catch(Exception e)
	{
		logger.info("Exception occured in checkDailyInterchangeTTUMProcess "+e);
		return false;
	}
}
@Override
public boolean runDailyInterchangeTTUM(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		//FIRST CHECK WHETHER TTUM IS ALREADY GENERATED
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory()},Integer.class);
		int checkTTUMFlag = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ? AND TTUM_FLAG = 'Y'", new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
		logger.info("TTUM Flag is "+checkTTUMFlag);
		if(checkTTUMFlag == 0)
		{
			if(beanObj.getFileName().contains("NFS"))
			{
				NFSSettMonthlyTTUMProc rollBackexe = new NFSSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			}
			else if(beanObj.getFileName().contains("DFS"))
			{

				DFSSettMonthlyTTUMProc rollBackexe = new DFSSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			}
			else if(beanObj.getFileName().contains("JCB"))
			{

				JCBSettMonthlyTTUMProc rollBackexe = new JCBSettMonthlyTTUMProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("USER_ID", beanObj.getCreatedBy());
				inParams.put("TIMEPERIOD",beanObj.getTimePeriod());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			
			}
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
			else
			{
				//UPDATE PROCESS FLAG
				
				getJdbcTemplate().execute("UPDATE MAIN_SETTLEMENT_FILE_UPLOAD SET TTUM_FLAG = 'Y' WHERE FILEID = "+file_id+" AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY') ");
//				getJdbcTemplate().execute(ProcessFlag);
				return true;

			}
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
	//return true;


}
@Override
public List<Object> getDailyInterchangeTTUMData(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = null;
		//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
		/*final List<String> cols  = getColumnList("NFS_SETTLEMENT_MONTHLY_TTUM");
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
		Column_list.add("REFERENCE NUMBER");*/

		//final List<String> cols = Column_list;
		
		if(beanObj.getFileName().contains("NFS"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+ "LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+ "rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE "
					+ " FROM NFS_SETTLEMENT_DAILY_TTUM WHERE FILEDATE =TO_DATE(?,'DD/MON/YYYY')";
		}
		else if(beanObj.getFileName().contains("DFS"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+ "LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+ "rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE "
					+ " FROM DFS_SETTLEMENT_DAILY_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";
		}
		else if(beanObj.getFileName().contains("JCB"))
		{
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+ "LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+ "rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+ ",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE "
					+ " FROM JCB_SETTLEMENT_DAILY_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY')";
		}
		
		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
			//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
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
		
		return DailyData;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getDailyInterchangeTTUMData "+e);
		return null;

	}

}
@Override
public boolean runSettlementVoucher(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	String query = null;
	try {
		
		//1. execute adjustment procedure first.
		NFSAdjTTUM adjProc = new NFSAdjTTUM(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
		outParams = adjProc.execute(inParams);
		
		if(outParams.get("msg") == null)
		{
			//2. update chargeback records recmrks columns
			query = "select count(1) from nfs_adjustment_ttum where filedate = str_to_date(?,'%Y/%m/%d') and adjtype = 'Chargeback Raise'";
			
			int cbkCount = getJdbcTemplate().queryForObject(query, new Object[]{beanObj.getDatepicker()},Integer.class);
			
			
				query = "select transaction_particular from nfs_adjustment_ttum where filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
							+ "and adjtype = 'Chargeback Raise'";
				List<String> tran_partis = getJdbcTemplate().query(query, new ResultSetExtractor<List<String>>(){
					public List<String> extractData(ResultSet rs)throws SQLException {
						List<String> data = new ArrayList<>();
						
						while(rs.next())
						{
							data.add(rs.getString("transaction_particular"));
						}
						return data;
					}}
					);
				
				logger.info("Before updating ");
				
		   for(int i = 1; i<= cbkCount; i++)
			{
			   query = "update nfs_adjustment_ttum set remarks = concat(remarks,'"+i+"')"
			   		+ " where filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and adjtype = 'Chargeback Raise' "
					   +"and transaction_particular = '"+tran_partis.get(i-1)+"'";
			   logger.info("Update query is "+query);
			   getJdbcTemplate().execute(query);
			   
			}
			
		   logger.info("Before calling voucher proc");

			//3. execute settlement ttum procedure
			NFSSettVoucherProc rollBackexe = new NFSSettVoucherProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			outParams = rollBackexe.execute(inParams);


			if(outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			logger.info("Proc execution completed");
		}
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		return false;
	}
	return true;


}
private class NFSSettVoucherProc extends StoredProcedure{
	private static final String insert_proc = "nfs_settlement_ttum_process";
	public NFSSettVoucherProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

private class NFSAdjTTUM extends StoredProcedure{
	private static final String insert_proc = "nfs_dhana_adj_ttum";
	public NFSAdjTTUM(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

private class DFSSettVoucherProc extends StoredProcedure{
	private static final String insert_proc = "DFS_JCB_DAILY_SETTLEMENT_VOUCHER";
	public DFSSettVoucherProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}
public ArrayList<String> getDailyColumnList(String tableName) {

	//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','CREATEDDATE','CREATEDBY','CYCLE','UPDATEDDATE','UPDATEDBY')";
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

@Override
public List<Object> getSettlementVoucher(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = "select * from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
		List<String> Column_list  = new ArrayList<String>();
		Column_list = getDailyColumnList("nfs_settlement_ttum");
		
		final List<String> Col  = Column_list; 
		data.add(Column_list);
	final List<String> columns  = Column_list;
	System.out.println("column value is "+columns.get(1));

		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> data = new HashMap<String, String>();
						logger.info("Column is "+columns.get(1));
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

/************************ CODING FOR ADJUSTMENT TTUM *********************************/
@Override
public boolean runAdjTTUM(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams1 = new HashMap<String, Object>();
	Map<String, Object> outParams2 = new HashMap<String, Object>();
	try {
		
		/*AdjDetailedTTUMProc rollBackexe = new AdjDetailedTTUMProc(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
		//inParams.put("ENTERED_CYCLE", beanObj.getCycle()); 
		inParams.put("SUBCATE", beanObj.getStSubCategory());
		outParams1 = rollBackexe.execute(inParams);*/
				
		//run adj ttum
		AdjTTUMProc exe = new AdjTTUMProc(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
		inParams.put("ADJTYPE", beanObj.getAdjType()); 
		inParams.put("SUBCATE", beanObj.getStSubCategory());
		outParams2 = exe.execute(inParams);
		if(outParams2 !=null && outParams2.get("msg") != null)
		{
			logger.info("OUT PARAM IS "+outParams2.get("msg"));
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
private class AdjDetailedTTUMProc extends StoredProcedure{
	private static final String insert_proc = "nfs_ADJ_DETAILED_TTUM";
	public AdjDetailedTTUMProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		//declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
		declareParameter(new SqlParameter("SUBCATE",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

// ADJ ttum detailed
private class AdjTTUMProc extends StoredProcedure{
	private static final String insert_proc = "nfs_ADJ_TTUM";
	public AdjTTUMProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		//declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
		declareParameter(new SqlParameter("ADJTYPE",Types.VARCHAR));
		declareParameter(new SqlParameter("SUBCATE",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

/*@Override
public List<Object> getAdjTTUM(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData1 = null,getData2 = null;
		//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";

		//final List<String> cols = Column_list;
		
		if(beanObj.getFileName().contains("NFS"))
		{
			getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? AND UPPER(ADJTYPE) IN ('CHARGEBACK RAISE', 'PRE-ARBITRATION RAISE','NRP DECISION LOGGING IN FAVOUR OF ISSUER','ARBITRATION ACCEPTANCE')";
			
			getData2 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? AND UPPER(ADJTYPE) IN ('RE-PRESENTMENT RAISE','PRE-ARBITRATION DECLINED','GOOD FAITH CHARGEBACK DEEMED ACCEPTANCE','GOODFAITH CHARGEBACK ACCEPTANCE')";
			
		}
		
		List<Object> DailyData= getJdbcTemplate().query(getData1, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
			//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					logger.info("Inside rset");
					
					Map<String, String> table_Data = new HashMap<String, String>();
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
		
		data.add(DailyData);
		
		//ADDING REPORT 2 DATA
		
		List<Object> DailyData2= getJdbcTemplate().query(getData2, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
			//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
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
		
		data.add(DailyData2);
		
		return data;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}*/

@Override
public List<Object> getAdjTTUM(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData1 = null;//,getData2 = null;
		List<Object> DailyData = new ArrayList<Object>();
		
		if(beanObj.getFileName().contains("NFS"))
		{
			
			if(beanObj.getAdjType().equals("PENALTY") || beanObj.getAdjType().equals("FEE"))
			{
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
						+ "LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND upper(adjtype) like '%"+beanObj.getAdjType()+"%'";
				
				DailyData= getJdbcTemplate().query(getData1, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
					public List<Object> extractData(ResultSet rs)throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						
						while (rs.next()) {
							logger.info("Inside rset");
							
							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			}
			else
			{
				getData1 = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+"TRANSACTION_AMOUNT as TRANSACTION_AMOUNT,"
						+"TRANSACTION_PARTICULAR as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS"
						+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND (ADJTYPE) = ? AND adjtype not like '%Penalty%'";

				DailyData= getJdbcTemplate().query(getData1, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory(),beanObj.getAdjType()}, new ResultSetExtractor<List<Object>>(){
					public List<Object> extractData(ResultSet rs)throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			}
			/*getData2 = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"TRANSACTION_AMOUNT as TRANSACTION_AMOUNT,"
					+"TRANSACTION_PARTICULAR as TRANSACTION_PARTICULAR,"
					+ "REFERENCE_NUMBER AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
					+ " FROM NFS_ADJUSTMENT_TTUM_TEMP WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
					+ "AND UPPER(ADJTYPE) IN ('CHARGEBACK ACCEPTANCE', 'CREDIT ADJUSTMENT',"
					+ "'GOOD FAITH CHARGEBACK DEEMED ACCEPTANCE')";*/
		}

		
		
		data.add(DailyData);
		
		//ADDING REPORT 2 DATA
		
		/*List<Object> DailyData2= getJdbcTemplate().query(getData2, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
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
					table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		data.add(DailyData2);*/
		
		return data;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}

@Override
public List<Object> getPBGBAdjTTUM(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData1 = null;//,getData2 = null;
		List<Object> DailyData = new ArrayList<Object>();
		
		if(beanObj.getFileName().contains("NFS"))
		{
			if(beanObj.getAdjType().equals("PENALTY") || beanObj.getAdjType().equals("FEE"))
			{
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
						+ "LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND upper(adjtype) like '%"+beanObj.getAdjType()+"%' AND BANK = 'PBGB'";
				
				DailyData= getJdbcTemplate().query(getData1, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
					public List<Object> extractData(ResultSet rs)throws SQLException {
						List<Object> beanList = new ArrayList<Object>();
						
						while (rs.next()) {
							logger.info("Inside rset");
							
							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			}
			else
			{
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
						+"LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
						+ " FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
						+ "AND (ADJTYPE) = ? AND adjtype not like '%Penalty%' AND (BANK = 'PBGB')";

				DailyData= getJdbcTemplate().query(getData1, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory(),beanObj.getAdjType()}, new ResultSetExtractor<List<Object>>(){
					public List<Object> extractData(ResultSet rs)throws SQLException {
						List<Object> beanList = new ArrayList<Object>();

						while (rs.next()) {
							logger.info("Inside rset");

							Map<String, String> table_Data = new HashMap<String, String>();
							table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
							table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
							table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
							table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
							table_Data.put("REMARKS", rs.getString("REMARKS"));
							table_Data.put("FILEDATE", rs.getString("FILEDATE"));
							table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

							beanList.add(table_Data);
						}
						return beanList;
					}
				});
			}
			/*getData2 = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"TRANSACTION_AMOUNT as TRANSACTION_AMOUNT,"
					+"TRANSACTION_PARTICULAR as TRANSACTION_PARTICULAR,"
					+ "REFERENCE_NUMBER AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
					+ " FROM NFS_ADJUSTMENT_TTUM_TEMP WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = ? "
					+ "AND UPPER(ADJTYPE) IN ('CHARGEBACK ACCEPTANCE', 'CREDIT ADJUSTMENT',"
					+ "'GOOD FAITH CHARGEBACK DEEMED ACCEPTANCE')";*/
		}

		
		
		data.add(DailyData);
		
		//ADDING REPORT 2 DATA
		
		/*List<Object> DailyData2= getJdbcTemplate().query(getData2, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()}, new ResultSetExtractor<List<Object>>(){
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
					table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		data.add(DailyData2);*/
		
		return data;

	}
	catch(Exception e)
	{
		System.out.println("Exception in getInterchangeData "+e);
		return null;

	}

}
/****************** CODING FOR CO OPERATIVE BANK TTUM ******************/

@Override
public boolean runCooperativeTTUM(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		
		CooperativeTTUMProc rollBackexe = new CooperativeTTUMProc(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
		inParams.put("ENTERED_CYCLE", beanObj.getCycle());
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
private class CooperativeTTUMProc extends StoredProcedure{
	private static final String insert_proc = "NFS_COOPERATIVE_TTUM";
	public CooperativeTTUMProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}

@Override
public List<Object> getCooperativeTTUM(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = null;
		//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
		final List<String> cols  = getColumnList("NFS_COOPERATIVE_BANK_TTUM");
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
		
		if(beanObj.getFileName().contains("NFS"))
		{
			getData = "SELECT * FROM NFS_COOPERATIVE_BANK_TTUM WHERE FILEDATE = ? AND CYCLE = ?";
		}
		/*else if(beanObj.getFileName().contains("DFS"))
		{
			getData = "SELECT * FROM DFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'DD/MON/YYYY'),'MONRRRR')";
		}
		else if(beanObj.getFileName().contains("JCB"))
		{
			getData = "SELECT * FROM JCB_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = to_char(TO_DATE(?,'DD/MON/YYYY'),'MONRRRR')";
		}*/
		
		List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker(),beanObj.getCycle()}, new ResultSetExtractor<List<Object>>(){
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
		logger.info("Exception in getCooperativeTTUM "+e);
		return null;

	}

}
/**************** VALIDATING LATE REVERSAL FILE ***********************/

@Override
public String ValidateLateReversalFile(NFSSettlementBean beanObj)
{
	int prevCount = 0;
	try
	{
		//CHECK WHETHER TTUM IS LAREADY PROCESSED
		int processCount = getJdbcTemplate().queryForObject("select count(*) from NFS_LATE_REVERSAL_TTUM where filedate = ?", new Object[] {beanObj.getDatepicker()},Integer.class);
		
		if(processCount == 0)
		{
			//CHECK FILE UPLOAD
			int dataCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM NFS_REV_ACQ_REPORT WHERE FILEDATE = ?", new Object[] {beanObj.getDatepicker()},Integer.class);
			logger.info("dataCount is "+dataCount);

			if(dataCount >0)
			{
				//if it is first time 
				int firsttime = getJdbcTemplate().queryForObject("select count(*) FROM NFS_LATE_REVERSAL_TTUM", Integer.class);
				if(firsttime > 0)
				{
					//CHECK PREVIOUS DATE TTUM IS GENERATED
					prevCount = getJdbcTemplate().queryForObject("select count(*) from NFS_LATE_REVERSAL_TTUM where filedate = TO_DATE(?,'DD/MM/YYYY')-1",
							new Object[] {beanObj.getDatepicker()},Integer.class);
				}
				else
					prevCount = 1;

				if(prevCount >0)
				{
					//CHECK SETTLEMENT REPORT AND VOUCHER IS  GENERATED FOR SELECTED DATE
					/*int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "NTSL-NFS",beanObj.getCategory()+"_SETTLEMENT","-" },Integer.class);
					System.out.println("File id is "+file_id);

					int settlementCount = getJdbcTemplate().queryForObject("select count(*) from main_settlement_file_upload where fileid = ? and filedate = ? and settlement_flag = 'Y' and SETT_VOUCHER='Y'"
							, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);

					if(settlementCount < 2)
					{
						return "Please process settlement and generate Voucher for both cycles";
					}
					else*/ //this check is not needed
					{
						return "success";
					}

				}
				else
				{
					return "Previous date ttum is not generated";
				}
			}
			else
			{
				return "File is not uploaded for Selected date!";
			}
		}
		else
		{
			return "TTUM is already processed \n Please download report";
		}
	}
	catch(Exception e)
	{
		logger.info("Exception in ValidateLateReversalFile" + e);
		return "Exception Occurred!";
	}
}

@Override
public List<Object> getLateReversalTTUMData(NFSSettlementBean beanObj)
{
	List<Object> data = new ArrayList<Object>();
	try
	{
		String getData = null;
		//String getInterchange1 = "SELECT * FROM NFS_SETTLEMENT_MONTHLY_TTUM WHERE FILEDATE = ?";
		/*final List<String> cols  = getColumnList("NFS_LATE_REVERSAL_TTUM");
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
		Column_list.add("REFERENCE NUMBER"); */

		//final List<String> cols = Column_list;
		
			getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+"LPAD(TRANSACTION_AMOUNT,17,0) as TRANSACTION_AMOUNT,"
					+"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
					+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE"
					+ " FROM NFS_LATE_REVERSAL_TTUM WHERE FILEDATE = ? ";

		
		List<Object> TTUMData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
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

					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		return TTUMData;

	}
	catch(Exception e)
	{
		logger.info("Exception in getCooperativeTTUM "+e);
		return null;

	}
} 

@Override
public String checkReversalTTUMProcess(NFSSettlementBean beanObj)throws Exception
{
	int getTTUMCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM NFS_LATE_REVERSAL_TTUM WHERE FILEDATE = ?", new Object[] {beanObj.getDatepicker()},Integer.class);
	if(getTTUMCount >0)
		return "success";
	else
		return "Please process TTUM first";
				 
}

@Override
public boolean runLateReversalTTUM(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		
		LateReversalTTUM rollBackexe = new LateReversalTTUM(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
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
private class LateReversalTTUM extends StoredProcedure{
	private static final String insert_proc = "LATE_REVERSAL_TTUM";
	public LateReversalTTUM(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}


}
