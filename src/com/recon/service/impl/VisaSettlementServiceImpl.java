package com.recon.service.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;
import com.recon.model.ReadVisaFile;
import com.recon.model.VisaUploadBean;
import com.recon.service.VisaSettlementService;
import com.recon.util.ReadVisaEPFiles;
import com.recon.util.ReadVisaJVFIle;

public class VisaSettlementServiceImpl extends JdbcDaoSupport implements VisaSettlementService{

	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public HashMap<String, Object> checkFileAlreadyUpload(String fileDate,String subcate)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String checkUpload = "SELECT COUNT(*) FROM visa_ep_rawdata  WHERE filedate = str_TO_DATE(?,'%Y/%m/%d') and subcategory = ?";
			int checkUploadCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {fileDate, subcate},Integer.class);
			
			if(checkUploadCount > 0)
			{
				output.put("result", false);
				output.put("msg", "File is already Uploaded !");
			}
			else
			{
				output.put("result", true);
			}
			
			return output;
		}
		catch(Exception e)
		{
			logger.info("Exception in VisaSettlementServiceImpl : checkFileAlreadyUpload "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred while validating File Upload !");
			return output;
		}
		
	}
	
	public boolean uploadFile(VisaUploadBean beanObj, MultipartFile file)
	{
		try
		{
			ReadVisaEPFiles readObj = new ReadVisaEPFiles();
			Connection conn = getConnection();
			readObj.fileupload(beanObj, file, conn);
			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in VisaSettlementServiceImpl : uploadFile "+e);
			return false;
		}
	}
	
	public boolean checkFileUpload(VisaUploadBean beanObj)
	{
		try
		{
			String checkFileUpload = "select count(*) from visa_ep_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and subcategory = 'DOMESTIC'";
			
			int domeUploadCount = getJdbcTemplate().queryForObject(checkFileUpload, new Object[] {beanObj.getFileDate()}, Integer.class);
			
			checkFileUpload = "select count(*) from visa_ep_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and subcategory = 'INTERNATIONAL'";
			
			int interUploadCount = getJdbcTemplate().queryForObject(checkFileUpload, new Object[] {beanObj.getFileDate()}, Integer.class);
			
			if(domeUploadCount > 0 && interUploadCount > 0)
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
			logger.info("Exception in check File Upload "+e);
			return false;
			
		}
	}
	
	public HashMap<String, Object> checkSettlementProcess(VisaUploadBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String checkSettlementProcess = "SELECT COUNT(1)  FROM visa_settlement_report WHERE filedate = str_to_date(?,'%Y/%m/%d')";
			int settlementCount = getJdbcTemplate().queryForObject(checkSettlementProcess, new Object[] {beanObj.getFileDate()}, Integer.class);
			
			if(settlementCount ==  0)
			{
				output.put("result"	, true);
			}
			else
			{
				output.put("result"	, false);
				output.put("msg", "Settlement is already processed for selected date");
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception while checking settlement Process"+e);

			output.put("result"	, false);
			output.put("msg", "Exception while validating settlement process");
		
		}
		return output;
	}
	
	public boolean runVisaSettlement(VisaUploadBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			VisaSettlementProc rollBackexe = new VisaSettlementProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
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
	
	private class VisaSettlementProc extends StoredProcedure{
		private static final String insert_proc = "visa_settlement_process";
		public VisaSettlementProc(JdbcTemplate jdbcTemplate)
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
	public List<Object> getSettlementData(VisaUploadBean beanObj)
	{

		List<Object> data = new ArrayList<Object>();
		final List<String> cols  = getColumnList("visa_settlement_report");
		
		String getData = "select * from  visa_settlement_report where filedate =  str_TO_DATE(?,'%Y/%m/%d') order by sr_no";
		
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
					for(String column: cols)
					{
						table_Data.put(column, rs.getString(column.replace(" ", "_")));
					}
					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		data.add(cols);
		data.add(settlementData);
		
		return data;
	
	} 
	
	public ArrayList<String> getColumnList(String tableName) {

		/*String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and owner = 'DEBITCARD_RECON'"
				+ " and column_name not like '%$%' and column_name not in('FILEDATE','SETTLEMENT_DATE','CREATEDDATE','CREATEDBY','FILE_TYPE') "
				+ " order by column_name";*/
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = Database() AND TABLE_NAME = '"+tableName.toLowerCase()+"' "
				+"and column_name not in('filedate','settlement_date','createddate','createdby','file_type')";
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
	public HashMap<String, Object> checkJVUploaded(VisaUploadBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String checkFileUploaded = "SELECT COUNT(*) FROM VISA_SETTLEMENT_REPORT WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY')";
			int getUploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {beanObj.getFileDate()},Integer.class);
			
			if(getUploadCount == 0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "JV File is already uploaded");
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in checkJVUploaded "+e);
			output.put("result", false);
			output.put("msg", "Excception in checkJVUploaded");
		}
		return output;
	}
	
	public boolean readJVFile(VisaUploadBean beanObj, MultipartFile file)
	{
		ReadVisaJVFIle readJV = new ReadVisaJVFIle();
		
		return readJV.readVisaJVFile(beanObj, file, getConnection());
		
	}
	
	/***** SETTLEMENT TTUM CODE ********/
	@Override
	public HashMap<String, Object> CheckTTUMProcessed(VisaUploadBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String checkFileUploaded = "select count(*) from visa_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
			int getUploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {beanObj.getFileDate()},Integer.class);
			
			if(getUploadCount == 0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "TTUM is already Processed");
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in CheckTTUMProcessed "+e);
			output.put("result", false);
			output.put("msg", "Exception in CheckTTUMProcessed");
		}
		return output;
	}
	
	public boolean runVisaSettlementTTUM(VisaUploadBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			VisaSettlementTTUMProc rollBackexe = new VisaSettlementTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}

		}
		catch(Exception e)
		{
			logger.info("Exception in runVisaSettlementTTUM is "+e);
			return false;
		}
		return true;
		
	}
	
	private class VisaSettlementTTUMProc extends StoredProcedure{
		private static final String insert_proc = "VISA_SETTLEMENT_TTUM_PROCESS";
		public VisaSettlementTTUMProc(JdbcTemplate jdbcTemplate)
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
	public List<Object> getSettlementTTUMData(VisaUploadBean beanObj)
	{

		/*List<Object> data = new ArrayList<Object>();
		final List<String> cols  = getColumnTTUMList("VISA_SETTLEMENT_TTUM");
		List<String> Column_list = new ArrayList<String>();
		Column_list.add("ACCOUNT NUMBER");
		Column_list.add("CURRENCY CODE OF ACCOUNT NUMBER");	
		Column_list.add("SERVICE OUTLET");
		Column_list.add("PART TRAN TYPE");	
		Column_list.add("TRANSACTION AMOUNT");	
		Column_list.add("TRANSACTION PARTICULAR");	
		Column_list.add("REFERENCE AMOUNT");	
		Column_list.add("REFERENCE CURRENCY CODE");	
		Column_list.add("RATE CODE");	
		Column_list.add("REMARKS");	
		Column_list.add("REFERENCE NUMBER");
		Column_list.add("ACCOUNT REPORT CODE");*/
		
		/*String getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
				+"LPAD(nvl(TRANSACTION_AMOUNT,0),17,0) as TRANSACTION_AMOUNT,"
				+"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REMARKS,' '),16,' ') AS REMARKS"
				+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE "
				+"from  VISA_SETTLEMENT_TTUM where filedate =  TO_DATE(?,'DD/MM/YYYY')";*/
		
		String getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
				+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
				+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
				+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
				+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE  "
				+"from  VISA_SETTLEMENT_TTUM where filedate =  TO_DATE(?,'DD/MM/YYYY')";
		
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
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
		
		
		return settlementData;
	
	} 
	
	public ArrayList<String> getColumnTTUMList(String tableName) {

		String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','SETTLEMENT_DATE','CREATEDDATE','CREATEDBY','FILE_TYPE','ISS_ACQ','SUBCATEGORY') "
				+ " order by column_name";
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

public boolean VisaSettRollback(VisaUploadBean beanObj)
{
	try
	{
		String deleteQuery = "delete from visa_settlement_report where filedate = str_to_date('"+beanObj.getFileDate()+"',"
				+ "'%Y/%m/%d')";
		getJdbcTemplate().execute(deleteQuery);
		
	}
	catch(Exception e)
	{
		logger.info("Exception in rollback "+e);
		return false;
	}
	return true;
}
}
