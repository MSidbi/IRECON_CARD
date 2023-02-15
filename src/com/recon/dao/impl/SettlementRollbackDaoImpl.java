package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.control.ReconProcess;
import com.recon.dao.CompareDao;
import com.recon.dao.ManualKnockoffDao;
import com.recon.dao.SettlementRollbackDao;
import com.recon.model.FileSourceBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualKnockoffBean;
import com.recon.model.NFSSettlementBean;

public class SettlementRollbackDaoImpl extends JdbcDaoSupport implements SettlementRollbackDao{
	
	private static final Logger logger = Logger.getLogger(SettlementRollbackDaoImpl.class);
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public Map<String, Object> NFSvalidateSettlementProcess(NFSSettlementBean beanObj)
	{
		Map<String, Object> output = new HashMap<>();
		int count = 0;
		String query = "";
		
		try
		{
			//1. first check whether voucher is processed if yes then throw error
			query = "select count(1) from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
			int vou_count = getJdbcTemplate().queryForObject(query, new Object[]{beanObj.getDatepicker()},Integer.class);
			
			if(vou_count>0)
			{
				output.put("result", false);
				output.put("msg", "Voucher is already processed. Please rollback voucher first.");
				
			}
			else
			{
				if(beanObj.getFileName().contains("NFS"))
				{
					query = "select count(1) from nfs_settlement_report where filedate = str_to_date(?,'%Y/%m/%d') and cycle = ?";

					count = getJdbcTemplate().queryForObject(query, new Object[]{beanObj.getDatepicker(), beanObj.getCycle()},
							Integer.class);
				}

				if(count >0)
				{
					output.put("result", true);
				}
				else
				{
					output.put("result", false);
					output.put("msg", "Settlement is not processed");
				}
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in validateSettlementProcess "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred while validating");
		}
		return output;
		
	}

	public Boolean NFSSettlementRollback(NFSSettlementBean beanObj)
	{
		Boolean flag = false;
		try
		{
				String query1 = "delete from nfs_settlement_report WHERE filedate = "
						+ "str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'";
				
				String query2 = "delete from nfs_settlement_report_diff WHERE filedate = "
						+ "str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'";
				
				String updateQuery = "update main_settlement_file_upload set settlement_flag = 'N' where filedate = "
						+ "str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'"
						+ " and fileid = (select fileid from main_filesource where filename = 'NTSL-NFS' and file_category = 'NFS_SETTLEMENT')";
				
				getJdbcTemplate().execute(query1);
				getJdbcTemplate().execute(query2);
				getJdbcTemplate().execute(updateQuery);
			
			/*else if(beanObj.getFileName().equalsIgnoreCase("VOUCHER"))
			{
				String query1 = "delete from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
				getJdbcTemplate().execute(query1);
			}*/
			
		}
		catch(Exception e)
		{
			logger.info("Exception in NFSSettlementRollback "+e);
			return false;
			
		}
		return true;
	}
	
// voucher rollbacck code	
	public Map<String, Object> NFSSettVoucherValidation(NFSSettlementBean beanObj)
	{
		Map<String, Object> output = new HashMap<>();
		int count = 0;
		String query = "";
		
		try
		{
			//1. first check whether voucher is processed if yes then throw error
			query = "select count(1) from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
			int vou_count = getJdbcTemplate().queryForObject(query, new Object[]{beanObj.getDatepicker()},Integer.class);
			
			if(vou_count>0)
			{
				output.put("result", true);
				
			}
			else
				{
					output.put("result", false);
					output.put("msg", "Settlement voucher is not processed");
				}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in NFSSettVoucherValidation "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred while validating");
		}
		return output;
		
	}
	
	public Boolean NFSSettVoucherRollback(NFSSettlementBean beanObj)
	{
		try
		{
				String query1 = "delete from nfs_settlement_ttum where filedate = str_to_date('"+
							beanObj.getDatepicker()+"','%Y/%m/%d')";
				getJdbcTemplate().execute(query1);
			
		}
		catch(Exception e)
		{
			logger.info("Exception in NFSSettVoucherRollback "+e);
			return false;
			
		}
		return true;
	}
	
/// NTSL rollback
		public Map<String, Object> NtslValidation(NFSSettlementBean beanObj)
		{
			Map<String, Object> output = new HashMap<>();
			int count = 0;
			String query = "";
			
			try
			{
				//1. first check whether voucher is processed if yes then throw error
				query = "select count(1) from ntsl_nfs_rawdata where filedate = str_to_date(?,'%Y/%m/%d') "
							+" and cycle = ?";
				int vou_count = getJdbcTemplate().queryForObject(query, new Object[]{beanObj.getDatepicker(),
						beanObj.getCycle()},Integer.class);
				
				if(vou_count>0)
				{
					output.put("result", true);
					
				}
				else
					{
						output.put("result", false);
						output.put("msg", "NTSL is not uploaded");
					}
				
			}
			catch(Exception e)
			{
				logger.info("Exception in NtslValidation "+e);
				output.put("result", false);
				output.put("msg", "Exception Occurred while validating ntsl");
			}
			return output;
			
		}
		
		public Boolean nfsNTSLRollback(NFSSettlementBean beanObj)
		{
			try
			{
					String query = "delete from ntsl_nfs_rawdata where filedate = str_to_date('"+
								beanObj.getDatepicker()+"','%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'";
					getJdbcTemplate().execute(query);
					
					query = "delete from main_settlement_file_upload where filedate = str_to_date('"+
								beanObj.getDatepicker()+"','%Y/%m/%d') and cycle = '"+beanObj.getCycle()+"'"
								+" and fileid = (select fileid from main_filesource where filename = 'NTSL-NFS' and file_category = 'NFS_SETTLEMENT')";
					getJdbcTemplate().execute(query);
				
			}
			catch(Exception e)
			{
				logger.info("Exception in NFSSettVoucherRollback "+e);
				return false;
				
			}
			return true;
		}		
}
