package com.recon.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.recon.model.FisdomFileUploadBean;
import com.recon.model.TTUMBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.FisdomReconService;


public class FisdomReconServiceImpl extends JdbcDaoSupport implements FisdomReconService {
	
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public HashMap<String, Object> checkFileUploaded(String fileDate)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		String err_msg = "";
		try
		{
			String getSwitchCount = "select CASE WHEN count(*) > 0 THEN 1 ELSE 0 END AS DATA FROM SWITCH_FISDOM_RAWDATA WHERE FILEDATE = '"+fileDate+"'";
			String getGLCount = "select CASE WHEN count(*) > 0 THEN 1 ELSE 0 END AS DATA FROM GL_FISDOM_RAWDATA WHERE FILEDATE = '"+fileDate+"'";
			//String getCBSCount = "select CASE WHEN count(*) > 1 THEN 1 ELSE 0 END AS DATA FROM CBS_FISDOM_RAWDATA WHERE FILEDATE = '"+fileDate+"'";
			
			int checkSwitchCount = getJdbcTemplate().queryForObject(getSwitchCount, new Object[] {},Integer.class);
			int checkGLCount = getJdbcTemplate().queryForObject(getGLCount, new Object[] {},Integer.class);
		//	int checkCBSCount = getJdbcTemplate().queryForObject(getCBSCount, new Object[] {},Integer.class);
			
			if(checkSwitchCount >0 && checkGLCount > 0 )//&& checkCBSCount > 0)
			{
				output.put("result", true);
				
			}
			else
			{
				if(checkSwitchCount == 0)
				{
					err_msg = err_msg+"Switch File is not uploaded";
				}
				if(checkGLCount == 0)
				{
					err_msg = err_msg+ "GL File is not uploaded";
				}
				/*
				 * if(checkCBSCount == 0) { err_msg = err_msg+ "CBS File is not Uploaded"; }
				 */
				output.put("result", false);
				output.put("msg", err_msg);
			}
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception occured while validating file uploaded");
		}
		return output;
		
	}
	
	public HashMap<String, Object> checkpreviousReconProcess(String fileDate)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			int getOldCount = getJdbcTemplate().queryForObject("select count(*) from main_file_upload_dtls where filedate < ? and category = 'FISDOM' "
					+"and comapre_flag = 'Y' AND MANUALCOMPARE_FLAG = 'Y'",
					new Object[] {fileDate}, Integer.class);
			
			if(getOldCount > 0)
			{
				String checkPrevDayRecon = "select count(*) from main_file_upload_dtls where filedate = to_date(?,'dd/mm/yyyy')-1 and category = 'FISDOM' "+
							"and comapre_flag = 'Y' AND MANUALCOMPARE_FLAG = 'Y'";
				
				int firstRecon = getJdbcTemplate().queryForObject(checkPrevDayRecon, new Object[] {fileDate}, Integer.class);
				
				if(firstRecon < 3)
				{
					output.put("result", false);
					output.put("msg", "Previous Day recon is not processed");
				}
				else
				{
					output.put("result", true);
				}
				
			}
			else
			{
				output.put("result"	, true);
				
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception Occurred while checking previous day recon");
		}
	
		return output;
	}
	
	public boolean runFisdomRecon(String fileDate, String entryBy)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		try
		{
			
			FisdomReconProc rollBackexe = new FisdomReconProc(getJdbcTemplate());
			inParams.put("I_FILEDATE", fileDate);
			inParams.put("I_ENTRYBY", entryBy);
			outParams = rollBackexe.execute(inParams);
			
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in runFisdomRecon "+e);
			return false;
		}
		
		return true;
	}
	
	private class FisdomReconProc extends StoredProcedure{
		private static final String insert_proc = "RECON_FISDOM_PROC";
		public FisdomReconProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRYBY",Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	public HashMap<String, Object> reconAlreadyProcessed(String fileDate)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			int compare_count = getJdbcTemplate().queryForObject(
					"select COUNT(COMAPRE_FLAG) from main_file_upload_dtls where filedate = ? AND CATEGORY = 'FISDOM' AND COMAPRE_FLAG = 'Y'", new Object[] {fileDate}, Integer.class);
			
			if(compare_count == 3)
			{
				output.put("result", false);
				output.put("msg", "recon is already processed");
			}
			else if(compare_count < 3 && compare_count > 0)
			{
				output.put("result", false);
				output.put("msg", "recon is partially processed for selected date");
			}
			else if(compare_count == 0)
			{
				output.put("result", true);
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception Occured while checking recon is processed");
			
			logger.info("Exception in reconAlreadyProcessed "+e);
		}
		return output;
	}
	
	public boolean runFisdomTTUM(FisdomFileUploadBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		try
		{
			FisdomTTUMProc rollBackexe = new FisdomTTUMProc(getJdbcTemplate());
			inParams.put("I_FILEDATE", beanObj.getFileDate());
			inParams.put("I_ENTRYBY", beanObj.getCreatedBy());
			inParams.put("I_TYPEOFTTUM", beanObj.getTypeOfTTUM());
			outParams = rollBackexe.execute(inParams);
			
			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in runFisdomTTUM"+e );
			return false;
		}
		
		return true;
	
	}
	
	private class FisdomTTUMProc extends StoredProcedure{
		private static final String insert_proc = "GENERATE_FISDOM_TTUM";
		public FisdomTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRYBY",Types.VARCHAR));
			declareParameter(new SqlParameter("I_TYPEOFTTUM",Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MSG",Types.VARCHAR));
			compile();
		}

	}
	
	public boolean validateTTUMProcessed(FisdomFileUploadBean beanObj)
	{
		
		try
		{
			String checkProcess = "select count(*) from settlement_fisdom_switch where filedate = ? AND UPPER(TRANSACTION_STATUS)= ? "
					+ "AND DCRS_REMARKS = 'GENERATED-TTUM-GL-SWITCH-MATCHED'";
			
			int getCount = getJdbcTemplate().queryForObject(checkProcess, new Object[] {beanObj.getFileDate(), beanObj.getTypeOfTTUM()}, Integer.class);
			
			if(getCount > 0)
				return true;
			else
				return false;
			
		}
		catch(Exception e)
		{
			logger.info("exception in validateTTUMProcessed "+e);
			return false;
		}
	}
	
	public boolean checkAndMakeDirectory(FisdomFileUploadBean beanObj)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			java.util.Date date = sdf.parse(beanObj.getFileDate());

			sdf = new SimpleDateFormat("dd-MM-yyyy");

			String stnewDate = sdf.format(date);
			
			//1. Delete folder
			logger.info("Path is "+beanObj.getStPath()+File.separator+beanObj.getCategory());
			File checkFile = new File(beanObj.getStPath()+File.separator+beanObj.getCategory());
			if(checkFile.exists())
				FileUtils.forceDelete(new File(beanObj.getStPath()+File.separator+beanObj.getCategory()));
			
			//2. check whether category folder is there or not
			File directory = new File(beanObj.getStPath()+File.separator+beanObj.getCategory());
			if(!directory.exists())
			{
				directory.mkdir();
			}
			directory = new File(beanObj.getStPath()+File.separator+beanObj.getCategory()+File.separator+stnewDate);
			
			if(!directory.exists())
			{
				directory.mkdir();
			}
			
			beanObj.setStPath(beanObj.getStPath()+File.separator+beanObj.getCategory()+File.separator+stnewDate);
			
			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in checkAndMakeDirectory "+e);
			return false;
		}
	}
	
	public String createTTUMFile(FisdomFileUploadBean beanObj)
	{
		StringBuffer lineData;
		String fileName = "TTUM";
		String tableName = "";
		try
		{
			
			String getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,'INR' AS CURRENCY_CODE,PART_TRAN_TYPE,"
					+ "LPAD(nvl(TRANSACTION_AMOUNT,0),17,' ') as TRANSACTION_AMOUNT,"
					+ "rpad(TRANSACTION_PARTICULAR,29,' ') as TRANSACTION_PARTICULARS"
					+ ",to_char(to_date(filedate,'DD-Mon-YYYY'),'DD-MM-YYYY') AS FILEDATE"
					+ " from FISDOM_TTUM"
					+ " WHERE FILEDATE = ? AND TYPEOFTTUM = ?";

			logger.info("Query for getting ttum data is ");
			logger.info(getData);
					List<TTUMBean> ttumBeanObjLst = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate(),beanObj.getTypeOfTTUM()},new ResultSetExtractor() {
						public Object extractData(ResultSet rs) throws SQLException {
							List<TTUMBean> beanlst = new ArrayList<TTUMBean>();
							
							while (rs.next()) {
								TTUMBean ttumBeans = new TTUMBean();
								 ttumBeans.setAcc_number(rs.getString("ACCOUNT_NUMBER"));
								 ttumBeans.setCurrency_Code(rs.getString("CURRENCY_CODE"));
								 ttumBeans.setPart_tran_type(rs.getString("PART_TRAN_TYPE"));
								 ttumBeans.setTrans_amount(rs.getString("TRANSACTION_AMOUNT"));
								 ttumBeans.setTrans_particular(rs.getString("TRANSACTION_PARTICULARS"));
								// ttumBeans.setRemarks(rs.getString("REMARKS"));
								 ttumBeans.setFileDate(rs.getString("FILEDATE"));
								 beanlst.add(ttumBeans);
								 ttumBeans = null;
							}
							return beanlst;
						};
					});	
					
			logger.info("List Size is "+ttumBeanObjLst.size());		
			
			fileName = "FISDOM_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
			
			File file = new File(beanObj.getStPath()+File.separator+fileName);
			if(file.exists())
			{
				FileUtils.forceDelete(file);
			}
				file.createNewFile();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(beanObj.getStPath()+File.separator+fileName, true));
			int startLine = 0;
			//LOGIC TO CREATE TTUM DATA
			for(TTUMBean beanObjData : ttumBeanObjLst)
			{
				if(startLine > 0)
				{
					out.write("\n");
				}
				startLine++;
				lineData = new StringBuffer();
				lineData.append(beanObjData.getAcc_number()+"  "+"INR1870"+"    "+beanObjData.getPart_tran_type());
				lineData.append(beanObjData.getTrans_amount()+beanObjData.getTrans_particular());
				lineData.append("                                                                                                        ");
				lineData.append(beanObjData.getFileDate());
				//logger.info(lineData.toString());
				out.write(lineData.toString());	
					
			}
			
			out.flush();
			out.close();
			
			
		}
		catch(Exception e)
		{
			logger.info("Exception in RupayTTUMServiceImpl "+e);
		}
		
		return (beanObj.getStPath()+File.separator+fileName);
	}
	
	public boolean checkRecordsPresent(FisdomFileUploadBean beanObj)
	{
		
		try
		{
			String checkRecords = "SELECT COUNT(*) FROM SETTLEMENT_FISDOM_SWITCH WHERE FILEDATE = ? AND UPPER(TRANSACTION_STATUS) = ? "
					+" AND DCRS_REMARKS = 'GL-SWITCH-MATCHED'";
			int checkCount = getJdbcTemplate().queryForObject(checkRecords, new Object[] {beanObj.getFileDate(), beanObj.getTypeOfTTUM()}, Integer.class);
			
			if(checkCount == 0)
			{
				return false;
			}
			else 
				return true;
			
		}
		catch(Exception e)
		{
		
			logger.info("xception in checkRecordsPresent" +e);
			return false;
		}
	}
	
	public boolean checkTTUMAlreadyProcessed(FisdomFileUploadBean beanObj)
	{
		
		try
		{
			String checkRecords = "SELECT COUNT(*) FROM FISDOM_TTUM WHERE FILEDATE = ? AND TYPEOFTTUM = ? ";
					
			int checkCount = getJdbcTemplate().queryForObject(checkRecords, new Object[] {beanObj.getFileDate(), beanObj.getTypeOfTTUM()}, Integer.class);
			
			if(checkCount == 0)
			{
				return false;
			}
			else 
				return true;
			
		}
		catch(Exception e)
		{
		
			logger.info("xception in checkRecordsPresent" +e);
			return false;
		}
	}
}

