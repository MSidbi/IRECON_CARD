package com.recon.service.impl;

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
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.CashnetGLStatementService;

public class CashnetGLStatementServiceImpl extends JdbcDaoSupport implements CashnetGLStatementService {
	
	public HashMap<String, Object> checkGLStatementProcess(UnMatchedTTUMBean beanObj)
	{
		HashMap<String, Object>  output = new HashMap<>();
		
		try
		{
			
			String table = "";
			
			if(beanObj.getCategory().equals("VISA")){
				if(beanObj.getStSubCategory().equals("ISSUER")){
					if(!beanObj.getAtmPos().equals("-")){
						table = "gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0,3).toLowerCase()+"_"+beanObj.getAtmPos();
					}else{
						table = "gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0,3).toLowerCase();	
					}
				}else{
					table = "gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0,3).toLowerCase();
				}
			}else{
				table = "gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0,3).toLowerCase();	
			}
			/*String sql = "select count(1) from gl_cashnet_"+beanObj.getStSubCategory().substring(0,3).toLowerCase()
					+" where filedate = '"+beanObj.getFileDate()+"'";*/
			
			String sql = "select count(1) from "+table
					+" where filedate = '"+beanObj.getFileDate()+"'";
			
			int count = getJdbcTemplate().queryForObject(sql, new Object[]{},Integer.class);
			
			if(count == 0)
			{
				output.put("result", true);
				output.put("msg", "Gl Statement is not processed");
			}
			else
			{
				output.put("result", false);
				output.put("msg", "GL Statement is already processed");
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception occured while validating");
			logger.info("Exception in checkGLStatementProcess "+e);
			
		}
		return output;
		
	}
	
	public HashMap<String, Object> checkCashnetProcessing(UnMatchedTTUMBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<>();
		
		try
		{
			String sql = "select count(1) from eod_report where filedate = '"+beanObj.getFileDate()+"'";
			
			int count = getJdbcTemplate().queryForObject(sql, new Object[] {},Integer.class);
			
			if(count > 0 )
			{
//				sql = "select count(1) from cashnet_settlement_ttum where filedate = '"+beanObj.getFileDate()+"'";
				
				String table = "";
				
				if(beanObj.getCategory().equals("VISA")){
					table = beanObj.getCategory().toLowerCase()+"_settlement_report ";
				}else{
	             table = beanObj.getCategory().toLowerCase()+"_settlement_ttum ";
				}
				
				sql = "select count(1) from "+table+" where filedate = '"+beanObj.getFileDate()+"'";
				
				count = getJdbcTemplate().queryForObject(sql, new Object[] {},Integer.class);
				
				if(count > 0)
				{
//					sql = "select count(1) from main_file_upload_dtls "
//							+ "where filedate = '"+beanObj.getFileDate()+"' and category = 'CASHNET'"
//							+" and file_subcategory = '"+beanObj.getStSubCategory()+"' and comapre_flag = 'Y'";
					
					sql = "select count(1) from main_file_upload_dtls "
							+ "where filedate = '"+beanObj.getFileDate()+"' and category = '"+beanObj.getCategory().toUpperCase()+"'"
							+" and file_subcategory = '"+beanObj.getStSubCategory()+"' and comapre_flag = 'Y'";
					count = getJdbcTemplate().queryForObject(sql, new Object[] {},Integer.class);
					
					if(count >= 3)
					{
						output.put("result", true);
					}
					else
					{
						output.put("result", false);
						output.put("msg", "Recon is not processed");
					}
					
				}
				else
				{
					output.put("result", false);
					output.put("msg", "Settlement TTUM is not processed");
					
				}
				
				
			}
			else
			{
				output.put("result", false);
				output.put("msg", "EOD report is not processed");
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Issue occurred while checking process");
			
		}
		return output;
	}

	public boolean runCashnetStatement(UnMatchedTTUMBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try
		{
			if(beanObj.getStSubCategory().equalsIgnoreCase("ISSUER"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("CASHNET")){
					CashnetIssGLProc rollBackexe = new CashnetIssGLProc(getJdbcTemplate());
					inParams.put("filedt", beanObj.getFileDate());
					inParams.put("userid", beanObj.getCreatedBy()); 
					outParams = rollBackexe.execute(inParams);
				}else if(beanObj.getCategory().equalsIgnoreCase("NFS")){
					NfsIssGLProc rollBackexe = new NfsIssGLProc(getJdbcTemplate());
					inParams.put("filedt", beanObj.getFileDate());
					inParams.put("userid", beanObj.getCreatedBy()); 
					outParams = rollBackexe.execute(inParams);
				}else if(beanObj.getCategory().equalsIgnoreCase("VISA")){
					
					/*if(beanObj.getAtmPos().equalsIgnoreCase("pos")){
						VisaIssGLPosProc rollBackexe = new VisaIssGLPosProc(getJdbcTemplate());
						inParams.put("filedt", beanObj.getFileDate());
						inParams.put("userid", beanObj.getCreatedBy());
						inParams.put("closingBal", beanObj.getClosingBal()); 
						outParams = rollBackexe.execute(inParams);
					}else if(beanObj.getAtmPos().equalsIgnoreCase("atm")){
						VisaIssGLAtmProc rollBackexe = new VisaIssGLAtmProc(getJdbcTemplate());
						inParams.put("filedt", beanObj.getFileDate());
						inParams.put("userid", beanObj.getCreatedBy());
						inParams.put("closingBal", beanObj.getClosingBal()); 
						outParams = rollBackexe.execute(inParams);
					}*/
					
				}

			}
			else
			{
				if(beanObj.getCategory().equalsIgnoreCase("CASHNET")){
					CashnetAcqGLProc rollBackexe = new CashnetAcqGLProc(getJdbcTemplate());
					inParams.put("filedt", beanObj.getFileDate());
					inParams.put("userid", beanObj.getCreatedBy());
					inParams.put("closingBal", beanObj.getClosingBal()); 
					outParams = rollBackexe.execute(inParams);
				}else if(beanObj.getCategory().equalsIgnoreCase("NFS")){
					NfsAcqGLProc rollBackexe = new NfsAcqGLProc(getJdbcTemplate());	
					inParams.put("filedt", beanObj.getFileDate());
					inParams.put("userid", beanObj.getCreatedBy());
					inParams.put("closingBal", beanObj.getClosingBal()); 
					outParams = rollBackexe.execute(inParams);
				}else if(beanObj.getCategory().equalsIgnoreCase("VISA")){
					
//					if(beanObj.getAtmPos().equalsIgnoreCase("pos")){
//						
//					}else if(beanObj.getAtmPos().equalsIgnoreCase("atm")){
//						
//					}
					
					VisaAcqGLProc rollBackexe = new VisaAcqGLProc(getJdbcTemplate());	
					inParams.put("filedt", beanObj.getFileDate());
					inParams.put("userid", beanObj.getCreatedBy());
					inParams.put("closingBal", beanObj.getClosingBal()); 
					outParams = rollBackexe.execute(inParams);
					
				}


			}

			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in runTTUMProcess "+e);
			return false;
		}
	}
	
	private class CashnetIssGLProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_cashnet_iss";
		public CashnetIssGLProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			compile();
		}

	}
	
	private class NfsIssGLProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_nfs_iss";
		public NfsIssGLProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			compile();
		}

	}
	
	private class VisaIssGLPosProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_visa_iss_pos";
		public VisaIssGLPosProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			declareParameter(new SqlParameter("closingBal",Types.VARCHAR));
			compile();
		}

	}
	
	private class VisaIssGLAtmProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_visa_iss_atm";
		public VisaIssGLAtmProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			declareParameter(new SqlParameter("closingBal",Types.VARCHAR));
			compile();
		}

	}
	
	private class VisaAcqGLProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_visa_acq";
		public VisaAcqGLProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			declareParameter(new SqlParameter("closingBal",Types.VARCHAR));
			compile();
		}

	}
	
	private class CashnetAcqGLProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_cashnet_acq";
		public CashnetAcqGLProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			declareParameter(new SqlParameter("closingBal",Types.VARCHAR));
			compile();
		}

	}
	
	private class NfsAcqGLProc extends StoredProcedure{
		private static final String insert_proc = "process_gl_nfs_acq";
		public NfsAcqGLProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("filedt",Types.VARCHAR));
			declareParameter(new SqlParameter("userid",Types.VARCHAR));
			declareParameter(new SqlParameter("closingBal",Types.VARCHAR));
			compile();
		}

	}
	
	public List<Object> getCashnetGLStatement(UnMatchedTTUMBean beanObj)
	{

		List<Object> data = new ArrayList<Object>();
		try
		{
//			String tableName = "gl_cashnet_"+beanObj.getStSubCategory().substring(0, 3).toLowerCase();
			String tableName = "gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0, 3).toLowerCase();
			List<String> Column_list  = new ArrayList<String>();
			//Column_list = getDailyColumnList("gl_cashnet_"+beanObj.getStSubCategory().substring(0, 3).toLowerCase());
			Column_list.add("action");
			Column_list.add("description");
			Column_list.add("amount");
			Column_list.add("balance");
			
			data.add(Column_list);

			String getData = "select * from "+tableName+" where filedate = ? order by sr_no";
			
		final List<String> columns  = Column_list;
		System.out.println("column value is "+columns.get(1));

			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()}, new ResultSetExtractor<List<Object>>(){
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
	
	public Boolean CashnetStatementRollback(UnMatchedTTUMBean beanObj)
	{
		try
		{
//			String sql = "delete from gl_cashnet_"+beanObj.getStSubCategory().substring(0,3).toLowerCase()
//					+" where filedate = '"+beanObj.getFileDate()+"'";
			
			String sql = "delete from gl_"+beanObj.getCategory().toLowerCase()+"_"+beanObj.getStSubCategory().substring(0,3).toLowerCase()
					+" where filedate = '"+beanObj.getFileDate()+"'";
			
			getJdbcTemplate().execute(sql);
		}
		catch(Exception e)
		{
			logger.info("Exception in CashnetStatementRollback "+e);
			return false;
		}
		return true;
	}
	
	public ArrayList<String> getDailyColumnList(String tableName) {

		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','CREATEDDATE','CREATEDBY','CYCLE','UPDATEDDATE','UPDATEDBY')";
		
		String query = "select column_name from information_schema.columns where table_schema = database() and table_name = '"+tableName.toLowerCase()+"' "
				+"and column_name not in('id','createdby','createddate','dcrs_tran_no','next_tran_date','part_id','foracid','balance','pstd_user_id','particularals2','org_acct',"
				+"'tran_type','seg_tran_id','man_contra_account','balance','filedate','sr_no')";
		
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
}
