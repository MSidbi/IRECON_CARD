package com.recon.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.recon.control.FisdomAct4Controller;
import com.recon.model.Act4Bean;
import com.recon.model.FisdomACT4Detail;
import com.recon.model.TTUMBean;
import com.recon.model.VisaUploadBean;
import com.recon.service.FisdomAct4Service;


public class FisdomAct4ServiceImpl extends JdbcDaoSupport implements FisdomAct4Service  {
	
	private static final Logger logger = Logger.getLogger(FisdomAct4ServiceImpl.class);
	
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public  List<String>  getGlAccount(String category, String subcategory)
	{
		List<String> glAccounts = new ArrayList<String>();
		
		try
		{
			
			String getAccounts = "select Account_number||' ('||account_description||')' as GL_ACC from MAIN_ACT_GL_MASTER where category = ? and subcategory = ?";
			
			glAccounts = getJdbcTemplate().query(getAccounts, new Object[] {category, subcategory},new RowMapper()
			{
				@Override
				public String mapRow(ResultSet rs, int rownum) throws SQLException 
				{	
						String subcategory = rs.getString("GL_ACC");
						return subcategory;
					
				}
			});
			
			logger.info("Got account numbers");
		}
		catch(Exception e)
		{
			logger.info("Exception in getGlAccount "+e);
		}
		return glAccounts;
	}
	
	public HashMap<String,Object> checkAct4Process(Act4Bean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String checkAct4Process = "select count(*) from fisdom_act4_cr where filedate = ?";
			
			int getCrCount = getJdbcTemplate().queryForObject(checkAct4Process, new Object[] {beanObj.getDatepicker()},Integer.class);
			
			checkAct4Process = "select count(*) from fisdom_act4_Dr where filedate = ?";
				
			int getDrCount = getJdbcTemplate().queryForObject(checkAct4Process, new Object[] {beanObj.getDatepicker()},Integer.class);
			
			if(getDrCount >0 || getCrCount > 0)
			{
				output.put("result", true);
				output.put("msg", "Act4 is already processed!");
				
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Act4 is not processed!");
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in checling ACT4 process");
			output.put("result", false);
			output.put("msg", "Exception Occurred!");
		}
		return output;
	}
	
	public boolean runAct4Report(Act4Bean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			if(beanObj.getCategory().equalsIgnoreCase("Fisdom")) {
			Act4Report rollBackexe = new Act4Report(getJdbcTemplate());
			inParams.put("I_FILEDT", beanObj.getDatepicker());
			inParams.put("I_ENTRYBY", beanObj.getCreatedby()); 
			outParams = rollBackexe.execute(inParams);
			}else if(beanObj.getCategory().equalsIgnoreCase("Nfs")) {
				
			}
			
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
	
	private class Act4Report extends StoredProcedure{
		private static final String insert_proc = "FISDOM_ACT4";
		public Act4Report(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRYBY",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public List<FisdomACT4Detail> getACT4CreditData(Act4Bean beanObj)
	{
		/* FisdomACT4Detail fisdomAct4Bean = new FisdomACT4Detail(); */
		List<FisdomACT4Detail> fisdomAct4Crlst = new ArrayList<FisdomACT4Detail>();
		String tableName="";
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("Fisdom")) {
				tableName="FISDOM_ACT4_CR";
			}else if(beanObj.getCategory().equalsIgnoreCase("Nfs")) {
				tableName="FISDOM_ACT4_CR";
			}
			
			String getCreditData = "select * from "+tableName+" WHERE FILEDATE = ?";
			fisdomAct4Crlst = (List<FisdomACT4Detail>) getJdbcTemplate().
					query(getCreditData, new Object[] {beanObj.getDatepicker()},new ResultSetExtractor<List<FisdomACT4Detail>>(){
				public List<FisdomACT4Detail> extractData(ResultSet rs)throws SQLException {
					List<FisdomACT4Detail> fisdomAct4Crlst = new ArrayList<FisdomACT4Detail>();
					FisdomACT4Detail fisdomAct4Bean;
					
					while (rs.next()) {
						fisdomAct4Bean  = new FisdomACT4Detail();
						fisdomAct4Bean.setAmount(rs.getString("AMOUNT"));
						fisdomAct4Bean.setAnnexure(rs.getString("ANNEXURE"));
						fisdomAct4Bean.setCount(rs.getString("COUNT"));
						fisdomAct4Bean.setDate_of_item(rs.getString("DATE_OF_ITEM"));
						fisdomAct4Bean.setItems_credited(rs.getString("ITEMS_CREDITED"));
						fisdomAct4Crlst.add(fisdomAct4Bean);
					}
					return fisdomAct4Crlst;
				}
			});
		}
		catch(Exception e)
		{
			logger.info("Exception in getACT4CreditData is "+e);
		}
		return fisdomAct4Crlst;
	}
	
	public List<FisdomACT4Detail> getACT4DebitData(Act4Bean beanObj)
	{
		/* FisdomACT4Detail fisdomAct4Bean = new FisdomACT4Detail(); */
		String tableName="";
		
		List<FisdomACT4Detail> fisdomAct4Drlst = new ArrayList<FisdomACT4Detail>();
		try
		{

			if(beanObj.getCategory().equalsIgnoreCase("Fisdom")) {
				tableName="FISDOM_ACT4_DR";
			}else if(beanObj.getCategory().equalsIgnoreCase("Nfs")) {
				tableName="FISDOM_ACT4_DR";
			}
			String getCreditData = "select * from "+tableName+" WHERE FILEDATE = ?";
			fisdomAct4Drlst = (List<FisdomACT4Detail>) getJdbcTemplate().
					query(getCreditData, new Object[] {beanObj.getDatepicker()},new ResultSetExtractor<List<FisdomACT4Detail>>(){
				public List<FisdomACT4Detail> extractData(ResultSet rs)throws SQLException {
					List<FisdomACT4Detail> fisdomActDrlst = new ArrayList<FisdomACT4Detail>();
					FisdomACT4Detail fisdomAct4Bean;
					
					while (rs.next()) {
						fisdomAct4Bean  = new FisdomACT4Detail();
						fisdomAct4Bean.setAmount(rs.getString("AMOUNT"));
						fisdomAct4Bean.setAnnexure(rs.getString("ANNEXURE"));
						fisdomAct4Bean.setCount(rs.getString("COUNT"));
						fisdomAct4Bean.setDate_of_item(rs.getString("DATE_OF_ITEM"));
						fisdomAct4Bean.setItems_debited(rs.getString("ITEMS_DEBITED"));
						fisdomActDrlst.add(fisdomAct4Bean);
					}
					return fisdomActDrlst;
				}
			});
		}
		catch(Exception e)
		{
			logger.info("Exception in getACT4CreditData is "+e);
		}
		return fisdomAct4Drlst;
	}
	
	public String getTotal_credit(String filedt)
	{
		String total = "0";
		try
		{
			String getTotal = "select (t1.amount-t2.amount) as credit_amt from fisdom_act4_cr t1 , fisdom_act4_dr t2 where t1.filedate = ? "+
						"and t2.filedate = t1.FILEDATE and t1.ITEMS_CREDITED = 'TOTAL CREDIT' AND T2.ITEMS_DEBITED = 'TOTAL DEBIT'";
			
			total = getJdbcTemplate().queryForObject(getTotal, new Object[] {filedt},String.class);
					
		}
		catch(Exception e)
		{
			logger.info("Exception in getTotal_credit "+e);
		}
		return total;
	}
	
	//getting data from db
	public List<Object> getFisdomAct4Data(String description,String action, String fileDate)
	{
		List<Object> Alldata = new ArrayList<Object>();
		List<String> cols = new ArrayList<String>();
		List<Object> dataRecords = new ArrayList<Object>() ;
		try
		{
			cols = getColumnList("SETTLEMENT_FISDOM_GL");
			final List<String> col_list = cols;
			String getData = "";
			if(action.equalsIgnoreCase("DEBIT"))
			{
				getData = "select * from settlement_fisdom_gl t1 where filedate = ? "+
						"AND SUBSTR(PARTICULARS,1,10) IN (select distinct SUBSTR(particulars,1,10) from gl_fisdom_rawdata) "+
						"and  DCRS_REMARKS = 'GL-SWITCH-UNMATCHED' and substr(particulars,1,10) like '%"+description+"%'";

				dataRecords = getJdbcTemplate().query(getData, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
					public List<Object> extractData(ResultSet rs)throws SQLException {
						List<Object> beanObj = new ArrayList<Object>();
						while (rs.next()) {
							Map<String, String> table_data = new HashMap<String, String>();

							for(String column : col_list)
							{
								table_data.put(column, rs.getString(column));
							}
							beanObj.add(table_data);
						}
						return beanObj;
					}
				});
			}
			else
			{
				if(description.contains("MPAY/TRTR"))
				{
					getData = "select * from settlement_fisdom_gl t1 where filedate = ? "+
							"AND SUBSTR(PARTICULARS,1,10) IN (select distinct SUBSTR(particulars,1,10) from gl_fisdom_rawdata) "+
							"and  DCRS_REMARKS = 'GL-SWITCH-UNMATCHED'  and deposits is not null "+
							"and substr(particulars,1,10) like '%MPAY/TRTR/%'";
					
					dataRecords = getJdbcTemplate().query(getData, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
						public List<Object> extractData(ResultSet rs)throws SQLException {
							List<Object> beanObj = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> table_data = new HashMap<String, String>();

								for(String column : col_list)
								{
									//logger.info("column is "+column+" data is "+rs.getString(column));
									table_data.put(column, rs.getString(column));
								}
								beanObj.add(table_data);
							}
							return beanObj;
						}
					});
				}
				else if(description.equalsIgnoreCase("AGEING"))
				{
					getData = "select * from settlement_fisdom_gl T1 where "+
							"SUBSTR(PARTICULARS,1,10) IN (select distinct SUBSTR(particulars,1,10) from gl_fisdom_rawdata) "+
							"and  DCRS_REMARKS = 'GL-SWITCH-MATCHED' "+
							"and (substr(particulars,1,10) = 'MPAY/TRTR/' ) "+
							"AND EXISTS (SELECT 1 FROM SETTLEMENT_FISDOM_GL T2 WHERE T1.PARTICULARS = T2.PARTICULARS AND T1.DEPOSITS = T2.DEPOSITS "+
							"AND T1.BALANCE = T2.BALANCE AND T2.DCRS_REMARKS = 'GL-CBS-MATCHED') "+
							"AND EXISTS (SELECT 1 FROM SETTLEMENT_FISDOM_SWITCH T3 WHERE T3.bank_txn_id = substr(t1.particulars,11,12) and "+
							"t3.AMOUNT = to_number(replace(t1.deposits,',','')) and t3.dcrs_remarks like '%GENERATED-TTUM%')";
					
					dataRecords = getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
						public List<Object> extractData(ResultSet rs)throws SQLException {
							List<Object> beanObj = new ArrayList<Object>();
							while (rs.next()) {
								Map<String, String> table_data = new HashMap<String, String>();

								for(String column : col_list)
								{
									table_data.put(column, rs.getString(column));
								}
								beanObj.add(table_data);
							}
							return beanObj;
						}
					});
				}
			}
			Alldata.add(cols);
			Alldata.add(dataRecords);
			
		}
		catch(Exception e)
		{
			logger.info("Exception in "+e);
		}
		return Alldata;
	}
	
	public ArrayList<String> getColumnList(String tableName) {

		String query = "SELECT distinct column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' "
				+"and column_name not IN ('CREATEDDATE','FILEDATE')";
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
	
	public List<Object> getAct4Data(String category, String fileDate, String subcategory)
	{
		List<Object> allData = new ArrayList<Object>();
		List<Object> dr_data = new ArrayList<Object>();
		List<Object> cr_data = new ArrayList<Object>();
		HashMap<String, String> credit_map = new HashMap<String, String>();
		HashMap<String, String> debit_map = new HashMap<String, String>();
		HashMap<String , Object> both_map = new HashMap<String, Object>();
		List<String> dr_Columns = new ArrayList<String>();
		List<String> cr_Columns = new ArrayList<String>();
		List<Object> cols =  new ArrayList<Object>();
		String debitTable="";
		String creditTable="";
		try
		{
			
        if(category.equalsIgnoreCase("Fisdom")) {
        	debitTable="FISDOM_ACT4_DR";
        	creditTable="FISDOM_ACT4_CR";
        }else if(category.equalsIgnoreCase("Nfs")) {
        	debitTable="FISDOM_ACT4_DR";
        	creditTable="FISDOM_ACT4_CR";
        }			
			dr_Columns = getColumnList(debitTable);
			final List<String> dr_cols = dr_Columns;
			
			
			String getDebitData = "SELECT * FROM "+debitTable+" WHERE FILEDATE = ?";
			String getCreditData = "SELECT * FROM "+creditTable+" WHERE FILEDATE = ?";
			
			dr_data = getJdbcTemplate().query(getDebitData, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> data = new ArrayList<Object>();
					
					while (rs.next()) {
						HashMap<String, String> table_data = new HashMap<String, String>();
						for(String column : dr_cols)
						{
							table_data.put(column, rs.getString(column));
						}
						data.add(table_data);
					}
					return data;
				}
			});
			
			cr_Columns = getColumnList(creditTable);
			final List<String> cr_cols = cr_Columns;
			
			cr_data = getJdbcTemplate().query(getCreditData, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> data = new ArrayList<Object>();
					
					while (rs.next()) {
						HashMap<String, String> table_data = new HashMap<String, String>();
						for(String column : cr_cols)
						{
							table_data.put(column, rs.getString(column));
						}
						data.add(table_data);
					}
					return data;
				}
			});
			
			both_map.put("CREDIT", cr_data);
			both_map.put("DEBIT", dr_data);
			cols.add(dr_cols);
			cols.add(cr_cols);
			
			allData.add(cols);
			allData.add(both_map);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception in getAct4Data "+e);
			
		}
		return allData;
	}
	
	public List<Object> getAct4MatchedData(Act4Bean beanObj)
	{
		HashMap<String, String> Data = new HashMap<String, String>();
		List<Object> AllData = new ArrayList<Object>();
		List<Object> data_Lst = new ArrayList<Object>();
		try
		{
			List<String> columns = new ArrayList<String>();
			columns = getColumnList("SETTLEMENT_FISDOM_GL");
			final List<String> cr_cols = columns;
			
			String getMatchedData = "select * from settlement_fisdom_gl T1 where filedate = ? "
					+"AND SUBSTR(PARTICULARS,1,10) IN (select distinct SUBSTR(particulars,1,10) from gl_fisdom_rawdata) "
					+"and  DCRS_REMARKS = 'GL-SWITCH-MATCHED' and (substr(particulars,1,10) = 'MPAY/TRTR/' ) ";
			
			data_Lst = getJdbcTemplate().query(getMatchedData, new Object[] {beanObj.getDatepicker()}, 
					new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> AllData1 = new ArrayList<Object>();
					while (rs.next()) {
						HashMap<String, String> table_data = new HashMap<String, String>();
						for(String column : cr_cols)
						{
							table_data.put(column, rs.getString(column));
						}
						AllData1.add(table_data);
					}
					return AllData1;
				}
			});
			
			AllData.add(columns);
			AllData.add(data_Lst);
			
		}
		catch(Exception e)
		{
			logger.info("Exception in getAct4CreditDebitData "+e);
		}
		return AllData;
	}

	@Override
	public List<Object> getNfsAct4Data(String description, String action, String fileDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
