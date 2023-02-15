package com.recon.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.recon.model.NFSSettlementBean;
import com.recon.service.EODReportService;

public class EODReportServiceImpl  extends JdbcDaoSupport implements EODReportService {

	public HashMap<String, Object>  checkEODReportProcess(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			String checkEODProcess = "select count(*) from eod_report where filedate = str_to_date(?,'%Y/%m/%d')";
			int recordCount = getJdbcTemplate().queryForObject(checkEODProcess, new Object[] {beanObj.getDatepicker()}, Integer.class);
			
			if(recordCount >0)
			{
				output.put("result", true);
				output.put("msg", "Report is already processed");
			}
			else
			{
				output.put("result", false);
				output.put("msg", "EOD Report is not processed for selected date");
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception while checking Report Process "+ex );
			output.put("result", false);
			output.put("msg", "Exception while validating eod Report");
		}
		return output;
	}
	
	public HashMap<String, Object>  checkCBSFileUpload(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			String checkEODProcess = "select count(*) from cbs_rupay_rawdata where filedate = str_to_date(?,'%Y/%m/%d')";
			int recordCount = getJdbcTemplate().queryForObject(checkEODProcess, new Object[] {beanObj.getDatepicker()}, Integer.class);
			
			if(recordCount >0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "CBS File is not Uploaded for selected date");
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception while checking cbs data "+ex);
			output.put("result", false);
			output.put("msg", "Exception while validating eod Report");
		}
		return output;
	
	}
	
	public HashMap<String, Object>  runEODReport(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			String executeEOD = "insert into eod_report(mnemonic, network, from_gl, d_c1, to_new_gl_code, d_c, amount, narration, filedate, createdby)"
					+"select t1.sys_ref,\r\n"
					+ " case when dcrs_remarks = '900000' and sys_ref = '2752'\r\n"
					+ " then\r\n"
					+ "    'VISA (POS)'\r\n"
					+ " ELSE \r\n"
					+ "    case when dcrs_Remarks = '400000' and sys_Ref = '2752'\r\n"
					+ "    then\r\n"
					+ "        'RUPAY'\r\n"
					+ " else\r\n"
					+ "    case when dcrs_Remarks = '900000' and sys_Ref = '2316'"
					+ "    then\r\n"
					+ "         'VISA (ATM)'\r\n"
					+ " else\r\n"
					+ "    case when dcrs_remarks = '700000' and sys_ref = '2318'"
					+ "    then\r\n"
					+ "        'NFS'\r\n"
					+ " else\r\n"
					+ "    case when dcrs_remarks = '800000' and sys_ref = '2301' "
					+ "    then\r\n"
					+ "        'CASHNET'\r\n"
					+ " END end end end end as network ,  "
					+ " t3.acquirer_gl, 'D',  t3.issuer_gl, 'C',"
					+ "round(sum(amount),2), concat('Transfer of transactions Dt.',date_format(str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d'),'%d-%M-%Y'))"
					+ " ,str_to_Date('"+beanObj.getDatepicker()+"','%Y/%m/%d') , '"+beanObj.getCreatedBy()+"' "
					+ " from cbs_rupay_rawdata t1, dhana_gl_master t3 where filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
					+ "and e='200' and t1.dcrs_remarks = t3.id and t1.sys_ref = t3.mnemonic\r\n"
					+ "AND respcode = '00' and dcrs_remarks in ('400000','900000','700000','800000') and amount > 0 "
					+ "and not exists(select 1 from cbs_rupay_rawdata t2 where t2.filedate = t1.filedate and t2.E = '420' and t2.respcode = '00' "
					+ "and t1.remarks = t2.remarks and t1.amount = t2.amount and t1.ref_no = t2.ref_no "
					+ "and t1.tran_id = t2.tran_id and t1.sys_ref = t2.sys_ref)"
					+ "group by sys_ref,dcrs_remarks,t3.acquirer_gl, t3.issuer_gl";
			
			logger.info("Query is "+executeEOD);
			
			getJdbcTemplate().execute(executeEOD);
			
			output.put("result", true);
			
		}
		catch(Exception e)
		{
			logger.info("Exception while executing EOD Report "+e);
			output.put("result", false);
			output.put("msg", "Exception while executing EOD Report");
		}
		return output;
	}
	
	@Override
	public List<Object> getEODReport(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getData = "";
			List<String> Column_list  = new ArrayList<String>();
			Column_list = getColumnList("eod_report");

			getData = "SELECT * FROM eod_report WHERE filedate = str_to_date(?,'%Y/%m/%d')";
			
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
	
	public ArrayList<String> getColumnList(String tableName) {

		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
		
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
	
/********************** ONE WAY RECON REPORT *******************************/	
	@Override
	public HashMap<String, Object>  checkAllReconProcess(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			String checkReconProcess = "select count(1) from main_file_upload_dtls "
					+ "where filedate = str_to_date(?,'%Y/%m/%d') and comapre_flag = 'Y' "
					+" and fileid in(select fileid from main_filesource where file_category in ('ONUS','VISA','NFS','CASHNET'))"; 
					
			int recordCount = getJdbcTemplate().queryForObject(checkReconProcess, new Object[] {beanObj.getDatepicker()}, Integer.class);
			
			checkReconProcess = "select COUNT(1) from main_filesource where file_category in ('ONUS','VISA','NFS','CASHNET')";
			
			int total_count = getJdbcTemplate().queryForObject(checkReconProcess, new Object[]{},Integer.class);
			
			if(recordCount == total_count)
			{
				output.put("result", true);
				output.put("msg", "Report is already processed");
			}
			else
			{
				output.put("result", false);
				output.put("msg", "All network recon are not processed");
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception while checking Recon Process "+ex );
			output.put("result", false);
			output.put("msg", "Exception while validating Recon Process");
		}
		return output;
	}
	
	
	@Override
	public List<Object> getOneWayReconReport(NFSSettlementBean beanObj)
	{
		logger.info("Inside getOneWayReconReport");
		List<Object> headers = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		try
		{
			//ISSUER DATA
			List<Object> Iss_Data = getIssuerData(beanObj);
			logger.info("got issuer data "+Iss_Data.size());
			
			//ACQUIRER DATA
			List<Object> Acq_Data = getAcquirerData(beanObj);
			logger.info("got acquirer data "+Acq_Data.size());
			//ONUS DATA
			List<Object> Onus_Data = getOnusData(beanObj);
			logger.info("got acquirer data "+Onus_Data.size());
			
			//POS DATA
			List<Object> Pos_Data = getPosData(beanObj);
			logger.info("got acquirer data "+Pos_Data.size());
			
			data.add(Iss_Data);
			data.add(Acq_Data);
			data.add(Pos_Data);
			data.add(Onus_Data);
		//	map_Data.put("ACQURIER", Acq_Data);
			
			logger.info("map size is "+data.size());
			
			
		}
		catch(Exception e)
		{
			logger.info("Exception in getting data "+e);
			return null;
		}
		return data;
	}
	
	public List<Object> getIssuerData(NFSSettlementBean beanObj)
	{
		logger.info("Inside getIssuerData");
		List<Object> Iss_header = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		List<Object> Iss_Data = new ArrayList<>();
		try
		{
			Iss_header.add(getDhanaColumnList("settlement_nfs_iss_switch"));
			Iss_header.add(getDhanaColumnList("settlement_nfs_iss_cbs"));
					
			logger.info("Before getting data");
		/*************** data ***********/
			// switch data
			//headers
			final List<String> columns = getDhanaColumnList("settlement_nfs_iss_switch");
			
			
			String cols = "";
			for(String col:columns)
			{
				if(col.equalsIgnoreCase("acctnum"))
				{
					cols = cols+","+"concat(concat(substring(substring(acctnum,-15),1,6),'XXXXX'),SUBSTR(acctnum,-4)) as acctnum";
				}
				else
				cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			String getData = "select "+cols+" from settlement_cashnet_iss_switch where dcrs_remarks = 'CASHNET_ISS-UNRECON-1'"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and respcode = '00' \n"
					+" union all "
					+"select "+cols+" from settlement_nfs_iss_switch where dcrs_remarks = 'NFS-ISS-UNRECON-1'"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and respcode = '00' \n"
					+" union all "
					+" select "+cols+" from settlement_visa_iss_switch t1 where dcrs_remarks = 'VISA_ISS-UNRECON-1' and transaction_type = '04' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and respcode = '00'";
			
			logger.info("getData query is "+getData);
			
			List<Object> switch_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			//cbs data
			//headers
			final List<String> cbs_columns = getDhanaColumnList("settlement_nfs_iss_cbs");
			cols = "";
			for(String col: cbs_columns)
			{
				if(col.equalsIgnoreCase("contra_account"))
				{
					cols = cols+","+"concat(concat(substring(contra_account,1,6),'XXXX'),substring(contra_account,-4) ) as  contra_account";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			getData = "select "+cols+" from settlement_visa_iss_cbs where dcrs_remarks like '%VISA_ISS-UNRECON-1%' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d')  and sys_ref = '2316' \n"
					+" union all \n"
					+"select "+cols+" from settlement_cashnet_iss_cbs where dcrs_remarks like 'CASHNET_ISS-UNRECON-1' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" union all "
					+"select "+cols+" from settlement_nfs_iss_cbs where dcrs_remarks like '%NFS-ISS-UNRECON-1%' "
					+"  and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n";
			
			logger.info("Cbs query is "+getData);
			
			List<Object> cbs_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							for(String column : cbs_columns)
							{
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(switch_Data);
			data.add(cbs_Data);
			
			
			Iss_Data.add("ISSUER");
			Iss_Data.add(Iss_header);
			Iss_Data.add(data);
			
		}
		catch(Exception e)
		{
			logger.info("Exception in getIssuerData "+e);
			return null;
		}
		return Iss_Data;
	}
	
	public List<Object> getAcquirerData(NFSSettlementBean beanObj)
	{
		logger.info("Inside getAcquirerData");
		List<Object> Acq_header = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		List<Object> Acq_Data = new ArrayList<>();
		try
		{
			Acq_header.add(getDhanaColumnList("settlement_nfs_acq_switch"));
			Acq_header.add(getDhanaColumnList("settlement_nfs_acq_cbs"));
					
			logger.info("Before getting data");
		/*************** data ***********/
			// switch data
			//headers
			final List<String> columns = getDhanaColumnList("settlement_nfs_acq_switch");
			
			
			String cols = "";
			for(String col:columns)
			{
				if(col.equalsIgnoreCase("acctnum"))
				{
					cols = cols+","+"concat(concat(substring(substring(acctnum,-15),1,6),'XXXXX'),SUBSTR(acctnum,-4)) as acctnum";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			/*String getData = "select "+cols+" from settlement_cashnet_acq_switch where (dcrs_remarks = 'CASHNET_ACQ-UNRECON-1' or dcrs_remarks like '%CASHNET_ACQ-UNRECON-2 (%')"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d')"
							+ " and respcode = '00' and amount >0 \n"
					+" union all \n"
					+"select "+cols+" from settlement_nfs_acq_switch where dcrs_remarks = 'NFS-ACQ-UNRECON-1' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
							+ "and respcode = '00' and amount >0 \n"
					+" union all \n"
					+"select "+cols+" from settlement_visa_acq_switch where dcrs_remarks = 'VISA_ACQ-UNRECON-1' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
					+"and respcode = '00' and amount >0";*/
			
			String getData = "select "+cols+" from settlement_cashnet_acq_switch where (dcrs_remarks = 'CASHNET_ACQ-UNRECON-1' or dcrs_remarks like '%CASHNET_ACQ-UNRECON-2 (%')"
					+" and local_date = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d')"
							+ " and respcode = '00' and amount >0 \n"
					+" union all \n"
					+"select "+cols+" from settlement_nfs_acq_switch where dcrs_remarks = 'NFS-ACQ-UNRECON-1' "
					+" and local_date = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
							+ "and respcode = '00' and amount >0 \n"
					+" union all \n"
					+"select "+cols+" from settlement_visa_acq_switch where dcrs_remarks = 'VISA_ACQ-UNRECON-1' "
					+" and local_date = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') "
					+"and respcode = '00' and amount >0";
			
			logger.info("getData query is "+getData);
			
			List<Object> switch_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			//cbs data
			//headers
			final List<String> cbs_columns = getDhanaColumnList("settlement_nfs_acq_cbs");
			cols = "";
			for(String col: cbs_columns)
			{
				if(col.equalsIgnoreCase("contra_account"))
				{
					cols = cols+","+"concat(concat(substring(contra_account,1,6),'XXXX'),substring(contra_account,-4) ) as  contra_account";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			/*+"select "+cols+" from settlement_cashnet_acq_cbs where dcrs_remarks like 'CASHNET_ACQ-UNRECON-2(' "
			+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
			+" union all \n"*/
			getData = "select "+cols+" from settlement_cashnet_acq_cbs where dcrs_remarks = 'CASHNET_ACQ-UNRECON-1' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"					
					+" union all \n"
					+"select "+cols+" from settlement_nfs_acq_cbs where dcrs_remarks = 'NFS-ACQ-UNRECON-1'"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" union all \n"
					+"select "+cols+" from settlement_visa_acq_cbs where dcrs_remarks = 'VISA_ACQ-UNRECON-1'"
					+"  and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n";
			
			logger.info("Cbs query is "+getData);
			
			List<Object> cbs_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							for(String column : cbs_columns)
							{
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(switch_Data);
			data.add(cbs_Data);
			
			Acq_Data.add("ACQUIRER");
			Acq_Data.add(Acq_header);
			Acq_Data.add(data);
		}
		catch(Exception e)
		{
			logger.info("Excception in getAcquirerData "+e);
			return null;
		}
		return Acq_Data;
	}
	
	public List<Object> getPosData(NFSSettlementBean beanObj)
	{
		logger.info("Inside getPosData");
		List<Object> pos_header = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		List<Object> pos_Data = new ArrayList<>();
		try
		{
			pos_header.add(getDhanaColumnList("settlement_visa_iss_switch"));
			pos_header.add(getDhanaColumnList("settlement_visa_iss_cbs"));
					
			logger.info("Before getting data");
		/*************** data ***********/
			// switch data
			//headers
			final List<String> columns = getDhanaColumnList("settlement_visa_iss_switch");
			
			
			String cols = "";
			for(String col:columns)
			{
				if(col.equalsIgnoreCase("acctnum"))
				{
					cols = cols+","+"concat(concat(substring(substring(acctnum,-15),1,6),'XXXXX'),SUBSTR(acctnum,-4)) as acctnum";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			String getData = "select "+cols+" from settlement_visa_iss_switch t1 where dcrs_remarks = 'VISA_ISS-UNRECON-1' \n"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" and transaction_type = '25' \n"
					+" and not exists (select 1 from settlement_visa_iss_cbs t2 where \n"
					+" t2.filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and t2.dcrs_remarks = 'VISA_ISS-KNOCKOFF' and \n"
					+" t1.pan = t2.remarks and t1.amount = t2.amount and t1.issuer = t2.ref_no and t1.termid= t2.tran_id) \n"
					+" union all \n"
					+ "select "+cols+" from settlement_nfs_pos_switch where dcrs_remarks = 'NFS_POS-UNMATCH' \n"
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" and respcode = '00'";
					
			logger.info("getData query is "+getData);
			
			List<Object> switch_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			//cbs data
			//headers
			final List<String> cbs_columns = getDhanaColumnList("settlement_visa_iss_cbs");
			cols = "";
			for(String col: cbs_columns)
			{
				if(col.equalsIgnoreCase("contra_account"))
				{
					cols = cols+","+"concat(concat(substring(contra_account,1,6),'XXXX'),substring(contra_account,-4) ) as  contra_account";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			getData = "select "+cols+" from settlement_visa_iss_cbs where dcrs_remarks like '%VISA_ISS-UNRECON-1%' " 
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" union all \n"
					+"select "+cols+" from settlement_nfs_pos_cbs where dcrs_remarks = 'NFS_POS-UNMATCH' "
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n";
				
			logger.info("Cbs query is "+getData);
			
			List<Object> cbs_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							for(String column : cbs_columns)
							{
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(switch_Data);
			data.add(cbs_Data);
			
			pos_Data.add("POS");
			pos_Data.add(pos_header);
			pos_Data.add(data);
		}
		catch(Exception e)
		{
			logger.info("Excception in getAcquirerData "+e);
			return null;
		}
		return pos_Data;
	}
	
	public List<Object> getOnusData(NFSSettlementBean beanObj)
	{
		logger.info("Inside getOnusData");
		List<Object> onus_header = new ArrayList<>();
		List<Object> data = new ArrayList<>();
		List<Object> onus_Data = new ArrayList<>();
		try
		{
			onus_header.add(getDhanaColumnList("settlement_onus_switch"));
			onus_header.add(getDhanaColumnList("settlement_onus_cbs"));
					
			logger.info("Before getting data");
		/*************** data ***********/
			// switch data
			//headers
			final List<String> columns = getDhanaColumnList("settlement_onus_switch");
			
			
			String cols = "";
			for(String col:columns)
			{
				if(col.equalsIgnoreCase("acctnum"))
				{
					cols = cols+","+"concat(concat(substring(substring(acctnum,-15),1,6),'XXXXX'),SUBSTR(acctnum,-4)) as acctnum";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			String getData = "select "+cols+" from settlement_onus_switch  where dcrs_remarks = 'ONUS-UNMATCHED' \n" 
					+" and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" and amount >0 \n";
				
					
			logger.info("getData query is "+getData);
			
			List<Object> switch_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			//cbs data
			//headers
			final List<String> cbs_columns = getDhanaColumnList("settlement_onus_cbs");
			cols = "";
			for(String col: cbs_columns)
			{
				if(col.equalsIgnoreCase("contra_account"))
				{
					cols = cols+","+"concat(concat(substring(contra_account,1,6),'XXXX'),substring(contra_account,-4) ) as  contra_account";
				}
				else
					cols = cols+","+col;
			}
			cols = cols.replaceFirst(",", "");
			
			logger.info("cols are "+cols);
			
			getData = "select * from settlement_onus_cbs  where  "
					+"  filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" and amount >0 and e != '420' and contra_account = '00999100780028'";
			
			getData += " union select * from settlement_onus_cbs  a where  "
					+"  filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') \n"
					+" and dcrs_remarks = 'ONUS-UNMATCHED' and amount >0 and e != '420' and respcode = '00' and  exists(select 1 from switch_rawdata b"
					+" where a.ref_no =b.issuer and b.respcode in ('06') and a.filedate = b.filedate)";
			logger.info("Cbs query is "+getData);
			
			List<Object> cbs_Data= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							for(String column : cbs_columns)
							{
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(switch_Data);
			data.add(cbs_Data);
			
			onus_Data.add("ONUS");
			onus_Data.add(onus_header);
			onus_Data.add(data);
		}
		catch(Exception e)
		{
			logger.info("Excception in getAcquirerData "+e);
			return null;
		}
		return onus_Data;
	}

	public ArrayList<String> getDhanaColumnList(String tableName) {
		
		
		String query = "select column_name from information_schema.columns where table_schema = database() and table_name = '"+tableName.toLowerCase()+"' "
				+"and column_name not in('id','createdby','createddate','dcrs_tran_no','next_tran_date','part_id','foracid','balance','pstd_user_id','particularals2','org_acct',"
				+"'tran_type','seg_tran_id','man_contra_account','balance','visa_rrn','bank_name','npci_rrn','fpan','arn','cbs_amount','contra_account',"
				+ "'dcrs_seq_no','destination_amount','destination_curr_code','diff_amount','relax_param','settlement_flag','source_amount','source_curr_code',"
				+ "'sur_amount','switch_trace','mcc','acceptorname','acquirer','amount_equiv','authnum','ch_amount','merchant_type','msgtype','new_amount','pcode','refnum','revcode','settlement_date','txndest','txnsrc')";
		
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
	public List<String> getEODVoucher(NFSSettlementBean beanObj)
	{
		List<String> Data = new ArrayList<String>();
		try
		{
			String getData = "";

			getData = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual \n"
					+" union all \n"
					+"select concat('203',from_gl,'       0099901005',"
					+"date_format(sysdate(),'%Y%m%d'), 'D',  date_format(sysdate(),'%Y%m%d'),'00101',"
					+"lpad(replace(convert(sum(amount),decimal(12,2)),'.',''),14,'0') ,"
					+"lpad(replace(convert(sum(amount),decimal(12,2)),'.',''),14,'0'),"
					+"'000000010000000000000',"
					+"lpad(' ',11,' '),'Eod Trf of txns dt. '," 
					+"concat(date_format(filedate,'%d'),'-',upper(left(date_format(filedate,'%M'),3)),'-',"
					+"date_format(filedate,'%y')),"
					+"lpad(' ',472,' ') ,'300000000000N0000000', from_gl  ) as a "
					+" from eod_report where filedate = ? and from_gl != to_new_gl_code "
					+" group by from_gl \n"
					+" union all \n"
					+" select concat('203',to_new_gl_code,'       0099901005',"
					+" date_format(sysdate(),'%Y%m%d'), 'C',  date_format(sysdate(),'%Y%m%d'),'00101',"
					+" lpad(replace(convert(amount,decimal(12,2)),'.',''),14,'0') ,"
					+" lpad(replace(convert(amount,decimal(12,2)),'.',''),14,'0') ,"
					+" '000000010000000000000',"
					+" lpad(' ',11,' '),'Eod Trf of txns dt. '," 
					+" concat(date_format(filedate,'%d'),'-',upper(left(date_format(filedate,'%M'),3)),'-',"
					+" date_format(filedate,'%y')),"
					+" lpad(' ',472,' ') ,'300000000000N0000000', to_new_gl_code  ) as a"
					+" from eod_report where filedate = '"+beanObj.getDatepicker()+"' and from_gl != to_new_gl_code \n"
					+" union all \n"					
 					+"select concat('30000000',lpad((count(d_c1)-1),2,'0'),lpad(round((sum(amount))*100),15,'0'),"
 					+"'0000000',lpad((count(d_c1)),2,'0'),lpad(round((sum(amount))*100),15,'0')) as a"
 					+" from eod_report where filedate = '"+beanObj.getDatepicker()+"' and from_gl != to_new_gl_code";
			
			logger.info("getData is "+getData);
			

			Data= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<String>>(){
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
	
}
