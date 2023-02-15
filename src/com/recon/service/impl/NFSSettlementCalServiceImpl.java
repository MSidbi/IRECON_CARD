package com.recon.service.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

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
import com.recon.model.NFSSettlementBean;
import com.recon.service.NFSSettlementCalService;
import com.recon.service.NFSSettlementService;

public class NFSSettlementCalServiceImpl extends JdbcDaoSupport implements NFSSettlementCalService{

	private static final String O_ERROR_MESSAGE = "o_error_message";

	@Override
	public boolean runNFSMonthlyProc(NFSSettlementBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			if(beanObj.getFileName().contains("NFS"))
			{
				NFSSettlementProc rollBackexe = new NFSSettlementProc(getJdbcTemplate());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("FILEDT1", beanObj.getToDate());
				inParams.put("SUBCAT", beanObj.getStSubCategory());
				outParams = rollBackexe.execute(inParams);
			}
			else
			{
				DFSSettlementProc rollBackexe = new DFSSettlementProc(getJdbcTemplate());
				inParams.put("FILENAME", beanObj.getFileName());
				inParams.put("FILEDT", beanObj.getDatepicker());
				inParams.put("FILEDT1", beanObj.getToDate());
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
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
				System.out.println("File id is "+file_id);

				String ProcessFlag = "UPDATE MAIN_MONTHLY_NTSL_UPLOAD SET PROCESS_FLAG = 'Y' WHERE FILEID = "+file_id+" AND FILEDATE = '"+beanObj.getDatepicker()+"'";
				getJdbcTemplate().execute(ProcessFlag);

			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		return true;


	}
	private class NFSSettlementProc extends StoredProcedure{
		private static final String insert_proc = "NFS_INTERCHANGE";
		public NFSSettlementProc(JdbcTemplate jdbcTemplate)
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
	private class DFSSettlementProc extends StoredProcedure{
		private static final String insert_proc = "DFS_INTERCHANGE";
		public DFSSettlementProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("FILEDT1",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCAT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	@Override
	public List<Object> getInterchangeData(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getInterchange1 = null;
			String getInterchange2 = null;
			if(beanObj.getFileName().contains("NFS"))
			{
				getInterchange1 = "SELECT * FROM nfs_interchange1 where filedate = to_char(TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY'),'MONRRRR')";
				getInterchange2 ="SELECT * FROM nfs_interchange2 where filedate between '"+beanObj.getDatepicker()+"' AND '"+beanObj.getToDate()+"'  ORDER BY FILEDATE ASC";
			}
			else if(beanObj.getFileName().contains("DFS"))
			{
				 getInterchange1 = "SELECT * FROM Dfs_interchange1 where filedate = to_char(TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY'),'MONRRRR')";
				 getInterchange2 ="SELECT * FROM Dfs_interchange2 WHERE FILEDATE BETWEEN '"+beanObj.getDatepicker()+"' AND '"+beanObj.getToDate()+"' ORDER BY FILEDATE ASC";
			}
			else if(beanObj.getFileName().contains("JCB-UPI"))
			{
				 getInterchange1 = "SELECT * FROM JCB_interchange1 where filedate = to_char(TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY'),'MONRRRR')";
				 getInterchange2 ="SELECT * FROM JCB_interchange2 where filedate between '"+beanObj.getDatepicker()+"' and '"+beanObj.getToDate()+"' ORDER BY FILEDATE ASC";
			}
				
			final List<String> Column_list  = getColumnList("nfs_interchange1");
			data.add(Column_list);


			List<Object> monthlyData= getJdbcTemplate().query(getInterchange1, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					//NFSInterchangeMonthly monthlyObj = new NFSInterchangeMonthly();
					
					while (rs.next()) {
						Map<String, String> table_data = new HashMap<String, String>();
						/*monthlyObj.setFiledate(rs.getString("FILEDATE"));
						monthlyObj.setCard_acc_term_id(rs.getString("CARD_ACC_TERMINAL_ID"));
						monthlyObj.setSol(rs.getString("SOL"));
						monthlyObj.setAadhar_App(rs.getString("ADDHARAPP"));
						monthlyObj.setAcquirer_bi_appr(rs.getString("ACQUIRERBIAPPR"));
						monthlyObj.setAcquirer_mob_appr(rs.getString("ACQUIRERMOBILEAPPR"));
						monthlyObj.setAcquirer_ms_appr(rs.getString("ACQUIRERMSAPPR"));
						monthlyObj.setAcquirer_pc_appr(rs.getString("ACQUIRERPCAPPR"));
						monthlyObj.setAcquirer_wd_appr(rs.getString("ACQUIRERWDLAPPR"));
						beanObj.add(monthlyObj);*/
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

			final List<String> Column_list2  = getColumnList("nfs_interchange2");
			/*List<NFSInterchangeMonthly> monthlyData2= getJdbcTemplate().query(getInterchange2, new Object[] {}, new ResultSetExtractor<List<NFSInterchangeMonthly>>(){
				public List<NFSInterchangeMonthly> extractData(ResultSet rs)throws SQLException {
					List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
					NFSInterchangeMonthly monthlyObj = new NFSInterchangeMonthly();
					while (rs.next()) {
						monthlyObj.setFiledate(rs.getString("FILEDATE"));
						monthlyObj.setSum_chrg(rs.getDouble("SUM_CHRG"));
						beanObj.add(monthlyObj);
					}
					return beanObj;
				}
			});*/
			 monthlyData= getJdbcTemplate().query(getInterchange2, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanObj = new ArrayList<Object>();
					//NFSInterchangeMonthly monthlyObj = new NFSInterchangeMonthly();
					
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
	
	//CODE FOR RUNNING DAILY NFS SETTLEMENT PROC
	@Override
	public boolean runNFSDailyProc(NFSSettlementBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			NFSSettlementDailyProc rollBackexe = new NFSSettlementDailyProc(getJdbcTemplate());
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
				//UPDATE PROCESSED FLAG
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-" },Integer.class);
				System.out.println("File id is "+file_id);

				String ProcessFlag = "update main_settlement_file_upload set settlement_flag = 'Y' where fileid = "+file_id+" and cycle = '"+beanObj.getCycle()+"' and filedate = '"+beanObj.getDatepicker()+"'"; 
				getJdbcTemplate().execute(ProcessFlag);

			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
		return true;


	}	
	private class NFSSettlementDailyProc extends StoredProcedure{
		private static final String insert_proc = "nfs_daily_settlement";
		public NFSSettlementDailyProc(JdbcTemplate jdbcTemplate)
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
	public boolean runPBGBDailyProc(NFSSettlementBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			
			PBGBSettlementDailyProc rollBackexe = new PBGBSettlementDailyProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			inParams.put("ENTERED_CYCLE", beanObj.getCycle());
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
	private class PBGBSettlementDailyProc extends StoredProcedure{
		private static final String insert_proc = "PBGB_DAILY_SETTLEMENT";
		public PBGBSettlementDailyProc(JdbcTemplate jdbcTemplate)
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
	public List<Object> getDailySettlementReport(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getInterchange1 = "";
			List<String> Column_list  = new ArrayList<String>();
			Column_list = getDailyColumnList("nfs_settlement_report");
			if(beanObj.getFileName().contains("NFS"))
			{
				getInterchange1 = "select * from nfs_settlement_report where cycle = ? and filedate = ? order by sr_no";
				//Column_list.add("DESCRIPTION");
			}
			else if(beanObj.getFileName().contains("DFS"))
			{
				Column_list = getDailyColumnList("dfs_settlement_report");
				getInterchange1 = "SELECT * FROM Dfs_settlement_report WHERE CYCLE = ? AND FILEDATE = ? ORDER BY SR_NO";
				//Column_list.add("International_Transactions_Pulse");
				beanObj.setCycle(1);
			}
			else if(beanObj.getFileName().contains("JCB"))
			{
				Column_list = getDailyColumnList("jcb_settlement_report");
				getInterchange1 = "SELECT * FROM JCB_settlement_report WHERE CYCLE = ? AND FILEDATE = ? ORDER BY SR_NO";
				//Column_list.add("International_Transactions_JCB/UPI");
				beanObj.setCycle(1);
			}
			else
			{
				Column_list = getDailyColumnList("pbgb_settlement_report");
				getInterchange1 = "SELECT * FROM pbgb_settlement_report WHERE CYCLE = ? AND FILEDATE = ? ORDER BY SR_NO";
			}
			
			final List<String> Col  = Column_list; 
			
			/*Column_list.add("NO_OF_TXNS");
			Column_list.add("DEBIT");
			Column_list.add("CREDIT");*/
			data.add(Column_list);
		final List<String> columns  = Column_list;
		System.out.println("column value is "+columns.get(1));

			List<Object> DailyData= getJdbcTemplate().query(getInterchange1, new Object[] {beanObj.getCycle(),beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					//List<NFSInterchangeMonthly> beanObj = new ArrayList<NFSInterchangeMonthly>();
				//	List<NFSDailySettlementBean> beanList = new ArrayList<NFSDailySettlementBean>();
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						//NFSDailySettlementBean DailyObj = new NFSDailySettlementBean();
						Map<String, String> data = new HashMap<String, String>();
						//DailyObj.setSrl_no(rs.getInt("SR_NO"));
							logger.info("Column is "+columns.get(1));
							/*data.put(columns.get(0), rs.getString("DESCRIPTION"));
							data.put("CREDIT", rs.getString("CREDIT"));
							data.put("DEBIT", rs.getString("DEBIT"));
							data.put("NO_OF_TXNS", rs.getString("NO_OF_TXNS"));*/
							for(String column : columns)
							{
								data.put(column, rs.getString(column));
							}
							//DailyObj.setData(data);
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
public HashMap<String, Object> skipSettlement(NFSSettlementBean beanObj)
{
	HashMap<String, Object> mapObj = new HashMap<String, Object>();
	int file_id = 0;
	try
	{
		try
		{
		file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		System.out.println("File id is "+file_id);
		}
		catch(Exception e)
		{
			mapObj.put("result", false);
			mapObj.put("msg", "File is not Configured!");
			return mapObj;
		}
		//GET FILE COUNT FROM SOURCE TABLE FOR CYCLE
		int getFileCount = getJdbcTemplate().queryForObject("SELECT FILE_COUNT FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[]{file_id}, Integer.class);
		//CHECK WHTHER FIRST TIME PROCESS
		//String count = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?";
		int totalCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?", new Object[] {file_id},Integer.class);
		if(totalCount >0)
		{
			//SETTLEMENT IS ALREADY PROCESSED
			//String settAlreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE PROCESS_FLAG = 'Y' AND FILEID = ? AND FILEDATE =? AND CYCLE = ?";
			int settAlreadyProc = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE =? AND CYCLE = ?", new Object[] {file_id,beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
			logger.info("proc count "+settAlreadyProc);
			if(settAlreadyProc == 0)
			{
				//IF NTSL IS UPLOADED FOR SELECTED DATE
				int ntslUploadCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND CYCLE = ? AND FILEDATE =?",new Object[] {file_id,beanObj.getCycle(),beanObj.getDatepicker()},Integer.class);
				if(ntslUploadCount == 0)
				{
					 //temp commented by INT8624
					/*String prevdayProcess = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY')-1";
					int checkCount = getJdbcTemplate().queryForObject(prevdayProcess, new Object[] {file_id},Integer.class);
					if(checkCount >0 && checkCount == getFileCount)*/
					{
						String insertData = "INSERT INTO MAIN_SETTLEMENT_FILE_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,SETTLEMENT_FLAG,INTERCHANGE_FLAG,SETT_VOUCHER,TTUM_FLAG,FILE_COUNT) " + 
								"VALUES('"+file_id+"',TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY'),'"+beanObj.getCreatedBy()+"',sysdate,'"+beanObj.getCategory()+"','Y','"+beanObj.getStSubCategory()+"','"+beanObj.getCycle()
								+ "','Y','Y','Y','Y','1')";
						getJdbcTemplate().execute(insertData);
						mapObj.put("result", true);
					}
					/*else
					{
						mapObj.put("result", false);
						mapObj.put("msg", "Previous Day Settlement is not Done");

					}*/   //temp commented by INT8624
				}
				else
				{

					mapObj.put("result", false);
					mapObj.put("msg", "NTSL is uploaded for selected date and cycle. Please process Settlement");
				
				}
			}
			else
			{
				mapObj.put("result", false);
				mapObj.put("msg", "Settlement is already procesed for selected date and cycle");
			}
		}
		else
		{
			String insertData = "INSERT INTO MAIN_SETTLEMENT_FILE_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,SETTLEMENT_FLAG,INTERCHANE_FLAG,FILE_COUNT) " + 
					"VALUES('"+file_id+"',TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY'),'"+beanObj.getCreatedBy()+"',sysdate,'"+beanObj.getCategory()+"','Y','N','"+beanObj.getStSubCategory()+"','"+beanObj.getCycle()
					+ "','Y','Y','1')";
			getJdbcTemplate().execute(insertData);
			mapObj.put("result", true);
		}
	}
	catch(Exception e)
	{
		logger.info("exception in skipSettlement "+e);
		mapObj.put("result", false);
		mapObj.put("msg", "Exception occurred!!");
		return mapObj;
	}
	
	return mapObj;
}

@Override
public boolean runDFSJCBDailyProc(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		
		DFSSettlementDailyProc rollBackexe = new DFSSettlementDailyProc(getJdbcTemplate());
		inParams.put("FILEDT", beanObj.getDatepicker());
		inParams.put("USER_ID", beanObj.getCreatedBy()); 
		inParams.put("ENTERED_CYCLE", 1);
		inParams.put("FILENAME", beanObj.getFileName());
		outParams = rollBackexe.execute(inParams);
		if(outParams !=null && outParams.get("msg") != null)
		{
			logger.info("OUT PARAM IS "+outParams.get("msg"));
			return false;
		}
		else
		{
			//UPDATE PROCESSED FLAG
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-"},Integer.class);
			System.out.println("File id is "+file_id);

			String ProcessFlag = "UPDATE MAIN_SETTLEMENT_FILE_UPLOAD SET SETTLEMENT_FLAG = 'Y' WHERE FILEID = "+file_id+" AND CYCLE = '1' AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY')"; 
			getJdbcTemplate().execute(ProcessFlag);

		}

	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		return false;
	}
	return true;


}	
private class DFSSettlementDailyProc extends StoredProcedure{
	private static final String insert_proc = "DFS_JCB_Daily_SETTLEMENT";
	public DFSSettlementDailyProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
		declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
		declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}
//METHODS FOR GETTING DAILY INTERCHANGE REPORT
@Override
public boolean runDailyInterchangeProc(NFSSettlementBean beanObj)
{
	Map<String,Object> inParams = new HashMap<>();
	Map<String, Object> outParams = new HashMap<String, Object>();
	try {
		if(beanObj.getFileName().contains("NFS"))
		{
			NFSInterchangeDailyProc rollBackexe = new NFSInterchangeDailyProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
		}
		else
		{
			DFSJCBInterchangeDailyProc rollBackexe = new DFSJCBInterchangeDailyProc(getJdbcTemplate());
			inParams.put("FILENAME", beanObj.getFileName());
			inParams.put("FILEDT", beanObj.getDatepicker());
			outParams = rollBackexe.execute(inParams);
		}
		
		
		if(outParams !=null && outParams.get("msg") != null)
		{
			logger.info("OUT PARAM IS "+outParams.get("msg"));
			return false;
		}
		else
		{
			//UPDATE PROCESSED FLAG
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			System.out.println("File id is "+file_id);

			String ProcessFlag = "UPDATE MAIN_SETTLEMENT_FILE_UPLOAD SET INTERCHANGE_FLAG = 'Y' WHERE FILEID = "+file_id+" AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY')"; 
			getJdbcTemplate().execute(ProcessFlag);

		}

	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		return false;
	}
	return true;


}
private class NFSInterchangeDailyProc extends StoredProcedure{
	private static final String insert_proc = "NFS_DAILY_INCOME";
	public NFSInterchangeDailyProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}

}
private class DFSJCBInterchangeDailyProc extends StoredProcedure{
	private static final String insert_proc = "DFS_JCB_DAILY_INCOME";
	public DFSJCBInterchangeDailyProc(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
		declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
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
		if(beanObj.getFileName().contains("NFS"))
		{
			getInterchange1 = "SELECT * FROM nfs_daily_interchange1 WHERE FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
			getInterchange2 ="SELECT * FROM nfs_daily_interchange2 where  FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
		}
		else if(beanObj.getFileName().contains("DFS"))
		{
			 getInterchange1 = "SELECT * FROM Dfs_daily_interchange1  WHERE FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
			 getInterchange2 ="SELECT * FROM Dfs_daily_interchange2 where FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
		}
		else if(beanObj.getFileName().contains("JCB-UPI"))
		{
			 getInterchange1 = "SELECT * FROM JCB_daily_interchange1 where filedate = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
			 getInterchange2 ="SELECT * FROM JCB_daily_interchange2 WHERE FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY')";
		}
			
		final List<String> Column_list  = getColumnList("nfs_daily_interchange1");
		data.add(Column_list);


		List<Object> monthlyData= getJdbcTemplate().query(getInterchange1, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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

		final List<String> Column_list2  = getColumnList("nfs_daily_interchange2");
		
		 monthlyData= getJdbcTemplate().query(getInterchange2, new Object[] {}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanObj = new ArrayList<Object>();
				//NFSInterchangeMonthly monthlyObj = new NFSInterchangeMonthly();
				
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
		System.out.println("Exception in getDailyInterchangeData "+e);
		return null;

	}

}


}
