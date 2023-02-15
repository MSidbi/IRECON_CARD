package com.recon.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.recon.control.RupaySettlementController;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RefundTTUMBean;
import com.recon.model.TTUMBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.RupayTTUMService;


public class RupayTTUMServiceImpl extends JdbcDaoSupport implements RupayTTUMService {

	private static final Logger logger = Logger.getLogger(RupayTTUMServiceImpl.class);
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public boolean runTTUMProcess(UnMatchedTTUMBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try
		{
			UnmatchedTTUMProc rollBackexe = new UnmatchedTTUMProc(getJdbcTemplate());
			inParams.put("CATEGORY", beanObj.getCategory());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			inParams.put("FILENAME", beanObj.getFileName());
			inParams.put("TTUMTYPE", beanObj.getTypeOfTTUM());
			inParams.put("SUBCATEGORY", beanObj.getStSubCategory());
			inParams.put("LOCALDT", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			
			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in runTTUMProcess "+e);
			return false;
		}
		
	}
	
	private class UnmatchedTTUMProc extends StoredProcedure{
		private static final String insert_proc = "RUPAY_UNMATCH_TTUM_PROCESS";
		public UnmatchedTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("CATEGORY",Types.VARCHAR));
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
			declareParameter(new SqlParameter("TTUMTYPE",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCATEGORY",Types.VARCHAR));
			declareParameter(new SqlParameter("LOCALDT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public boolean runInternationalTTUMProcess(UnMatchedTTUMBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try
		{
			InternationalTTUMProc rollBackexe = new InternationalTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			inParams.put("TTUM_TYPE", beanObj.getTypeOfTTUM());
			inParams.put("LOCALDT", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			
			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in runTTUMProcess "+e);
			return false;
		}
		
	}
	
	private class InternationalTTUMProc extends StoredProcedure{
	//	private static final String insert_proc = "RUPAY_INT_MEMBERFEE";
		private static final String insert_proc = "RUPAY_INT_TTUM";
		public InternationalTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("TTUM_TYPE",Types.VARCHAR));
			declareParameter(new SqlParameter("LOCALDT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	public HashMap<String,Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj)
	{
		HashMap<String,Object>  output = new HashMap<String,Object>();
		String tableName = "";
		String fileDate = "";
		String sett_table = "";
		String fetch_condition = "";
		try
		{
			String query = "SELECT COUNT(*) FROM ";
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LP"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_VISA";
				}
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}			
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_SUR_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_SUR_CBS";
				}
				//fetch_condition = " WHERE FILEDATE = '"+beanObj.getFileDate()+"'";
				fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY')";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
			{

				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_DOM_CBS";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_CBS";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("NFS"))
				{
					tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
					sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
			
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV"))
			{
				if(beanObj.getStSubCategory().equalsIgnoreCase("ISSUER"))
				{
					tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_nfs";
					sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";

					fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY') ";//AND FILEDATE = (SELECT MAX(FILEDATE) FROM "+sett_table+")";
				}
				else
				{
					tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_nfs";
					sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
					
					fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY')";// AND FILEDATE = (SELECT MAX(FILEDATE) FROM "+sett_table+")";
					
				}
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_DOM_SWITCH";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_SWITCH";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				}
				else if (beanObj.getCategory().equalsIgnoreCase("NFS"))
				{
					tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_switch";
					sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_switch";
				}
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("REVERSAL"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_DOM_rev_RUPAY";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_rev_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_visa";
				}
				fetch_condition = " WHERE FILEDATE = TO_DATE('"+beanObj.getFileDate()+"','DD/MM/YYYY')";
			}
			/*if(beanObj.getFileDate() == null)
			{
				logger.info("Filedate is null ");
				fileDate = getJdbcTemplate().queryForObject("SELECT MAX(FILEDATE) FROM "+sett_table, new Object[] {},String.class);
			}
				*/
			
			query = query+tableName+fetch_condition;
			int checkCount = getJdbcTemplate().queryForObject(query, new String[] {},Integer.class);
			
			if(checkCount > 0)
			{
				output.put("result", true);
				output.put("msg", "TTUM is already Processed. Please download report");
			}
			else
			{
				output.put("result", false);
				output.put("msg", "TTUM is not processed");
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in checkTTUMProcessed "+e);
			output.put("result", false);
			output.put("msg", "Exception while validating");
		}
		return output;
		
	}
	
	public HashMap<String,Object> checkReconProcessed(UnMatchedTTUMBean beanObj)
	{
		HashMap<String,Object>  output = new HashMap<String,Object>();
		
		try
		{
			String checkCompareCount = "SELECT COUNT(*) FROM main_file_upload_dtls where filedate = ? AND CATEGORY = ? "
						+ "and FILE_SUBCATEGORY = ? and comapre_flag = 'Y'";
			int compareCount = getJdbcTemplate().queryForObject(checkCompareCount, new Object[] {beanObj.getFileDate(),beanObj.getCategory(),
						beanObj.getStSubCategory()},Integer.class);
			
			if(compareCount >= 3)
			{
				output.put("result", true);
				output.put("msg", "Recon is Processed");
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Recon is not processed!");
			}
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception occurred while checking recon processed!");
			
		}
		return output;
		
	}
	
	public String createTTUMFile(UnMatchedTTUMBean beanObj)
	{
		StringBuffer lineData;
		String fileName = "TTUM";
		String tableName = "";
		try
		{
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LP"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_DOM_RUPAY";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_VISA";
				}
			}
			
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_SUR_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_SUR_CBS";
				}
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
			{

				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					tableName = "TTUM_RUPAY_DOM_SWITCH";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					tableName = "TTUM_VISA_ISS_SWITCH";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("NFS"))
				{
					tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_cbs";
				}
			
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV"))
			{
				tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_nfs";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
			{
				tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_switch";
			}
				
			
			String getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,CURRENCY_CODE,PART_TRAN_TYPE,"
					+ "LPAD(nvl(TRANSACTION_AMOUNT,0),17,0) as TRANSACTION_AMOUNT,"
					+ "rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULARS,LPAD(NVL(REMARKS,' '),16,' ') AS REMARKS"
					+ ",to_char(filedate,'DD-MM-YYYY') AS FILEDATE"
					+ " from "+tableName
					+ " WHERE FILEDATE = ?";

			logger.info("Query for getting ttum data is ");
			logger.info(getData);
					List<TTUMBean> ttumBeanObjLst = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate()},new ResultSetExtractor() {
						public Object extractData(ResultSet rs) throws SQLException {
							List<TTUMBean> beanlst = new ArrayList<TTUMBean>();
							
							while (rs.next()) {
								TTUMBean ttumBeans = new TTUMBean();
								 ttumBeans.setAcc_number(rs.getString("ACCOUNT_NUMBER"));
								 ttumBeans.setCurrency_Code(rs.getString("CURRENCY_CODE"));
								 ttumBeans.setPart_tran_type(rs.getString("PART_TRAN_TYPE"));
								 ttumBeans.setTrans_amount(rs.getString("TRANSACTION_AMOUNT"));
								 ttumBeans.setTrans_particular(rs.getString("TRANSACTION_PARTICULARS"));
								 ttumBeans.setRemarks(rs.getString("REMARKS"));
								 ttumBeans.setFileDate(rs.getString("FILEDATE"));
								 beanlst.add(ttumBeans);
								 ttumBeans = null;
							}
							return beanlst;
						};
					});	
					
			logger.info("List Size is "+ttumBeanObjLst.size());		
			
			fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
			
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
				lineData.append(beanObjData.getAcc_number()+"  "+"INR0391"+"    "+beanObjData.getPart_tran_type());
				lineData.append(beanObjData.getTrans_amount()+beanObjData.getTrans_particular()+"         "+beanObjData.getRemarks());
				lineData.append("                                                                          ");
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
		
		return beanObj.getStPath();
	}
	
	public boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj)
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
	
	public List<Object> getTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String fetch_condition = "";
		String sett_table = "";
		String ttum_format = null; 
		try
		{
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LP"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_ISS_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_VISA";
				}
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}			
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_SUR_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_SUR_CBS";
				}
				//fetch_condition = " WHERE FILEDATE = '"+beanObj.getFileDate()+"'";
				fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY')";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
			{

				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_RUPAY_DOM_CBS";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_VISA_ISS_CBS";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("NFS"))
				{
						ttum_tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
						sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				}
			
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV"))
			{
				ttum_tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_nfs";
				sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_DOM_SWITCH";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_ISS_SWITCH";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				}
				else
				{
					ttum_tableName = "TTUM_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_switch";
					sett_table = "SETTLEMENT_NFS_"+beanObj.getStSubCategory().substring(0, 3)+"_switch";
				}
				
				fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("REVERSAL"))
			{
				if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_DOM_rev_RUPAY";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
				}
				else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
				{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_ISS_rev_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_visa";
				}
			}
			
			String getData = null;
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
			
			if(ttum_format!=null && ttum_format.equalsIgnoreCase("OLD"))
			{
				getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
			}
			else
			{
				getData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
			}
				logger.info("Getdata query is "+getData);
				
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
	
	@Override
	public List<Object> getVisaTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String dom_fetch_condition = "";
		String int_fetch_condition = "";
		String sett_table = "";
		String ttum_format = null; 
		try
		{
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LP"))
			{
				
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_ISS_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_VISA";
					dom_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
							"  and tran_type = 'DOMESTIC'";
			}			
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_SUR_CBS";
				//fetch_condition = " WHERE FILEDATE = '"+beanObj.getFileDate()+"'";
					dom_fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY') "+
								"  and tran_type = 'DOMESTIC'";
					int_fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY') "+
							"  and tran_type = 'INTERNATIONAL'";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
			{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_VISA_ISS_CBS";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				dom_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
					"  and tran_type = 'DOMESTIC'";
				
				int_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")"
						+ " and tran_type = 'INTERNATIONAL'";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
			{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_VISA_ISS_SWITCH";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				
				dom_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
								"  and tran_type = 'DOMESTIC'";
				
				int_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
						"  and tran_type = 'INTERNATIONAL'";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("REVERSAL"))
			{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_VISA_ISS_rev_VISA";
					sett_table = "SETTLEMENT_VISA_"+beanObj.getStSubCategory().substring(0, 3)+"_visa";
					
					dom_fetch_condition = " WHERE TRAN_TYPE = 'DOMESTIC' AND FILEDATE = '"+beanObj.getFileDate()+"'";
					int_fetch_condition = " WHERE TRAN_TYPE = 'INTERNATIONAL' AND FILEDATE = '"+beanObj.getFileDate()+"'";
			}
			
			String getdomData = null;
			String getintData = null;
			
			
			if(ttum_format!=null && ttum_format.equalsIgnoreCase("OLD"))
			{
				getdomData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+dom_fetch_condition;
				
				
				getintData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+int_fetch_condition;
			}
			else
			{
				getdomData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+dom_fetch_condition;
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
				
				getintData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+int_fetch_condition;
			}
				logger.info("Getdata query is "+getdomData);
				logger.info("Getdata query is "+getintData);
				
			List<Object> DailyDatadom = getJdbcTemplate().query(getdomData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			List<Object> DailyDataInt = getJdbcTemplate().query(getintData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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

			data.add(DailyDatadom);
			data.add(DailyDataInt);
			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
	
	@Override
	public List<Object> getRupayTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String rupay_fetch_condition = "";
		String ncmc_fetch_condition = "";
		String sett_table = "";
		String ttum_format = null; 
		try
		{
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LP"))
			{
				
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_DOM_RUPAY";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_RUPAY";
					rupay_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
							"  and tran_type = 'RUPAY'";
					ncmc_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
							"  and tran_type = 'NCMC'";
			}			
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_SUR_CBS";
				//fetch_condition = " WHERE FILEDATE = '"+beanObj.getFileDate()+"'";
					rupay_fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY') "+
								"  and tran_type = 'RUPAY'";
					
					ncmc_fetch_condition = " WHERE TRAN_DATE = TO_DATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY') "+
							"  and tran_type = 'NCMC'";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
			{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_RUPAY_DOM_CBS";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_CBS";
				rupay_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
					"  and tran_type = 'RUPAY'";
				
				ncmc_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")"
						+ " and tran_type = 'NCMC'";
			}
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
			{
					ttum_format = "DRM";
					ttum_tableName = "TTUM_RUPAY_DOM_SWITCH";
					sett_table = "SETTLEMENT_RUPAY_"+beanObj.getStSubCategory().substring(0, 3)+"_SWITCH";
				
				rupay_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
								"  and tran_type = 'RUPAY'";
				
				ncmc_fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'"+// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+") "+
						"  and tran_type = 'NCMC'";
			}
			
			String getdomData = null;
			String getintData = null;
			
			
			if(ttum_format!=null && ttum_format.equalsIgnoreCase("OLD"))
			{
				getdomData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+rupay_fetch_condition;
				
				
				getintData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+ncmc_fetch_condition;
			}
			else
			{
				getdomData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+rupay_fetch_condition;
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
				
				getintData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+ncmc_fetch_condition;
			}
				logger.info("Getdata query is "+getdomData);
				logger.info("Getdata query is "+getintData);
				
			List<Object> DailyDatarupay = getJdbcTemplate().query(getdomData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			List<Object> DailyDatancmc = getJdbcTemplate().query(getintData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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

			data.add(DailyDatarupay);
			data.add(DailyDatancmc);
			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
	
	
	@Override
	public List<Object> getNIHTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String fetch_condition = " WHERE TRAN_DATE = '"+beanObj.getLocalDate()+"'";// AND FILEDATE = (SELECT MAX(FILEDATE) FROM SETTLEMENT_NFS_ACQ_NFS)";
		String sett_table = "";
		try
		{
			String getData1 = null;
			String microAtmData =  "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
					+ "TRANSACTION_AMOUNT,"
					+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
					+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM TTUM_NFS_ACQ_nfs "
					+fetch_condition +" AND ttum_type = 'MICROATM'";
			
				getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
						+ ",to_char(TO_DATE(FILEDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM TTUM_NFS_ACQ_nfs "
						+fetch_condition +" AND ttum_type != 'MICROATM'";
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
			
				logger.info("Getdata query is "+getData1);
				logger.info("Getdata query is "+microAtmData);
				
			List<Object> DailyData1= getJdbcTemplate().query(microAtmData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			List<Object> DailyData2= getJdbcTemplate().query(getData1, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
			
			
			data.add(DailyData1);
			data.add(DailyData2);

			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
	
	
	public HashMap<String,Object> checkTranReconDate(UnMatchedTTUMBean beanObj)
	{
		HashMap<String,Object>  output = new HashMap<String,Object>();
		
		try
		{
			//tablename should be dynamic
			String tableName = "SETTLEMENT_"+beanObj.getCategory()+"_"+beanObj.getStSubCategory().substring(0,3)+"_";
			if(beanObj.getFileName() != null && !beanObj.getFileName().equalsIgnoreCase(""))
			{
				if(beanObj.getFileName().equalsIgnoreCase("NETWORK"))
					tableName = tableName+beanObj.getCategory();
				else
					tableName = tableName+beanObj.getFileName();
			}
			else if(beanObj.getCategory().equalsIgnoreCase("NFS"))
			{
				if(beanObj.getTypeOfTTUM().equalsIgnoreCase("LATEREV"))
				{
					tableName = tableName+"NFS";
				}
				else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED"))
				{
					tableName = tableName+"CBS";
				}
				else
				{
					tableName = tableName+"SWITCH";
				}
			}
			logger.info("Table name is "+tableName);
			String checckCount = "select COUNT(*) FROM "+tableName+" WHERE FILEDATE >= '"+beanObj.getLocalDate()+"'";
			
			int getCount = getJdbcTemplate().queryForObject(checckCount, new Object[] {}, Integer.class);
			
			if(getCount >0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Recon Process Date is Smaller than Local Date");
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in checking recon and local date "+e);
			
			output.put("result", false);
			output.put("msg", "Exception in checking recon and local date ");
		}
		return output;
	
	}
	
	public void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String TTUMName,HttpServletResponse response, boolean ZipFolder )
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
	    	FileOutputStream fos = new FileOutputStream(stPath+File.separator+ "EXCEL_TTUMS.zip");
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
		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );

		}


	}
	
	public void generateRupayExcelTTUM(String stPath,List<Object> ExcelData,String TTUMName,HttpServletResponse response )
	{

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try
		{
			
	    	File file = new File(stPath);
	    	String[] filelist = file.list();
	    	
	    	for(String Names : filelist )
	    	{	
	    		logger.info("name is "+Names);
	    		files.add(stPath+File.separator+Names);
	    	}
	    	FileOutputStream fos = new FileOutputStream(stPath+File.separator+ "EXCEL_TTUMS.zip");
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
		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );

		}


	}
	
	@Override
	public HashMap<String,Object> checkInternationalTTUMProcessed(UnMatchedTTUMBean beanObj)
	{
		HashMap<String,Object>  output = new HashMap<String,Object>();
		try
		{
			String checkTTUM = "select count(*) from ttum_rupay_int_rupay where filedate = to_date('"+beanObj.getFileDate()+"','dd/mm/yyyy')";
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE")) {
				checkTTUM = "select count(*) from ttum_rupay_int_cbs where tran_Date = to_date('"+beanObj.getLocalDate()+"','dd/mm/yyyy')";
			}
			
			int checkTTUMCount = getJdbcTemplate().queryForObject(checkTTUM, new Object[] {}, Integer.class);
			
			if(checkTTUMCount == 0)
			{
				output.put("result", false);
				output.put("msg", "TTUM is not processed ");
			}
			else
			{
				output.put("result", true);
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception occured in checkInternationalTTUMProcessed "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred in checkInternationalTTUMProcessed");
		}
		return output;
	}
	
	
	public List<Object> getInternationalTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String fetch_condition = "";
		String sett_table = "";
		String ttum_format = null; 
		try
		{
			
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("MEMBERFUND"))
			{
					ttum_format = "OLD";
					ttum_tableName = "TTUM_RUPAY_INT_RUPAY";

					fetch_condition = " WHERE FILEDATE = '"+beanObj.getFileDate()+"'";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
			}	
			else if(beanObj.getTypeOfTTUM().equalsIgnoreCase("SURCHARGE"))
			{

				ttum_format = "OLD";
				ttum_tableName = "TTUM_RUPAY_INT_CBS";

				fetch_condition = " WHERE TRAN_DATE = TO_dATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY')";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
		
			}
			
			
			String getData = null;
			if(ttum_format!=null && ttum_format.equalsIgnoreCase("OLD"))
			{
				getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS, SUBSTR(ACCOUNT_NUMBER,1,4) AS SOL"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
			}
			else
			{
				getData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
			}
				logger.info("Getdata query is "+getData);
				
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
							table_Data.put("SOL", rs.getString("SOL"));
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
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
	
	public void generateInternationalTTUMFile(String stPath, String FileName,List<Object> TTUMData )
	{
		StringBuffer lineData;
		try
		{
			
			File file = new File(stPath+File.separator+FileName);
			if(file.exists())
			{
				FileUtils.forceDelete(file);
			}
				file.createNewFile();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(stPath+File.separator+FileName, true));
			int startLine = 0;
			//LOGIC TO CREATE TTUM DATA
			for(int i =0 ;i<TTUMData.size(); i++)
			{
				Map<String, String> table_Data = (Map<String, String>) TTUMData.get(i);
				
				if(startLine > 0)
				{
					out.write("\n");
				}
				startLine++;
				lineData = new StringBuffer();
				lineData.append(table_Data.get("ACCOUNT_NUMBER")+"  "+"INR"+table_Data.get("SOL")+"    "+table_Data.get("PART_TRAN_TYPE"));
				lineData.append(table_Data.get("TRANSACTION_AMOUNT")+table_Data.get("TRANSACTION_PARTICULAR"));//+"         "+table_Data.get("REMARKS"));
				lineData.append("                                                                                                       ");
				lineData.append(table_Data.get("FILEDATE"));
				//logger.info(lineData.toString());
				out.write(lineData.toString());	
					
			}
			
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );
			
		}
		
	}
	
	@Override
	public HashMap<String,Object> checkNIHRecords(UnMatchedTTUMBean beanObj)
	{
		String checkNIHData = ""; 
		int getunreconCount = 0;
		HashMap<String,Object> output = new HashMap<String,Object>();
		try
		{
			if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON"))
			{	
				checkNIHData = "select count(*) from settlement_nfs_acq_NFS where filedate = (select max(filedate) from settlement_nfs_iss_cbs) "+
							"AND DCRS_REMARKS = 'NFS-ACQ-UNRECON-2' AND  TO_DATE(TRANSACTION_DATE,'YYMMDD') = ?";
			    getunreconCount = getJdbcTemplate().queryForObject(checkNIHData, new Object[] {beanObj.getLocalDate()}, Integer.class);
			}
			else
			{
				checkNIHData = "select count(*) from settlement_nfs_acq_NFS where filedate = (select max(filedate) from settlement_nfs_iss_cbs) "+
						"AND DCRS_REMARKS = 'NFS-ACQ-GENERATED-TTUM-2' AND  TO_DATE(TRANSACTION_DATE,'YYMMDD') = ?";
				getunreconCount = getJdbcTemplate().queryForObject(checkNIHData, new Object[] {beanObj.getLocalDate()}, Integer.class);
			}
			
			if(getunreconCount == 0)
			{
				output.put("result", false);
				output.put("msg", "No Records present");
			}
			else
			{
				output.put("result", true);
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception in checkTTUMProcessed "+e);
		}
		return output;
	}
		
	@Override
	public List<Object> getNIHReport(UnMatchedTTUMBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getInterchange1 = "";
			List<String> Column_list  = new ArrayList<String>();
			
			if(beanObj.getTypeOfTTUM().equals("UNRECON")) {
				
				getInterchange1 = "select DISTINCT ibkl_encrypt_decrypt.ibkl_get_encrypt_val(fPAN) as card, issuer, respcode, trace, termid, local_date, LOCAL_TIME, amount "
						+" from switch_rawdata T1 where issuer in ( "
						+"select TXN_SERIAL_NO from settlement_nfs_acq_nfs where filedate = (select max(filedate) from settlement_nfs_acq_NFS) and dcrs_remarks = 'NFS-ACQ-UNRECON-2' "
						+"AND TO_DATE(TRANSACTION_DATE,'YYMMDD') = ?) "
						+"and dcrs_remarks = 'ATM' AND AMOUNT > 0 AND MSGTYPE ='0210' AND SUBSTR(PAN,1,6) NOT IN (SELECT BIN FROM UCO_BIN_MASTER WHERE BANK = 'UCO')";
				
			}
			else
			{	
			getInterchange1 = "select DISTINCT ibkl_encrypt_decrypt.ibkl_get_encrypt_val(fPAN) as card, issuer, respcode, trace, termid, local_date, LOCAL_TIME, amount "
					+"from switch_rawdata T1 where issuer in ( "
					+"select reference_number from  ttum_nfs_acq_nfs where tran_date = to_date(?,'dd/mm/yyyy') and part_tran_type = 'C') "
					+"and dcrs_remarks = 'ATM' AND AMOUNT > 0 AND MSGTYPE ='0210' AND SUBSTR(PAN,1,6) NOT IN (SELECT BIN FROM UCO_BIN_MASTER WHERE BANK = 'UCO')";
				//Column_list.add("DESCRIPTION");
			}
			
			logger.info("Query is "+getInterchange1);
			
			Column_list.add("card");
			Column_list.add("issuer");
			Column_list.add("respcode");
			Column_list.add("trace");
			Column_list.add("termid");
			Column_list.add("local_date");
			Column_list.add("LOCAL_TIME");
			Column_list.add("amount");
			data.add(Column_list);
			
		final List<String> columns  = Column_list;
		//System.out.println("column value is "+columns.get(1));

			List<Object> DailyData= getJdbcTemplate().query(getInterchange1, new Object[] {beanObj.getLocalDate()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							//logger.info("Column is "+columns.get(1));
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
	
	public ArrayList<String> getDailyColumnList(String tableName) {

		String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','CREATEDDATE','CREATEDBY','CYCLE','UPDATEDDATE','UPDATEDBY')";
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
	public HashMap<String,Object> checkCardToCardTTUMProcessed(UnMatchedTTUMBean beanObj)
	{
		HashMap<String,Object>  output = new HashMap<String,Object>();
		try
		{
			String checkTTUM = "select count(*) from ttum_cardtocard_iss_cbs where tran_Date = to_date('"+beanObj.getLocalDate()+"','dd/mm/yyyy')";
			
			int checkTTUMCount = getJdbcTemplate().queryForObject(checkTTUM, new Object[] {}, Integer.class);
			
			if(checkTTUMCount == 0)
			{
				output.put("result", false);
				output.put("msg", "TTUM is not processed ");
			}
			else
			{
				output.put("result", true);
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception occured in checkCardToCardTTUMProcessed "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred in checkCardToCardTTUMProcessed");
		}
		return output;
	}
	
	public boolean runCardToCardTTUMProcess(UnMatchedTTUMBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		try
		{
			CardToCardTTUMProc rollBackexe = new CardToCardTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy()); 
			inParams.put("TTUM_TYPE", beanObj.getTypeOfTTUM());
			inParams.put("LOCALDT", beanObj.getLocalDate());
			outParams = rollBackexe.execute(inParams);
			if(outParams !=null && outParams.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams.get("msg"));
				return false;
			}
			
			return true;
		}
		catch(Exception e)
		{
			logger.info("Exception in runTTUMProcess "+e);
			return false;
		}
		
	}
	
	private class CardToCardTTUMProc extends StoredProcedure{
	//	private static final String insert_proc = "RUPAY_INT_MEMBERFEE";
		private static final String insert_proc = "CARDTOCARD_UNRECON_TTUM";
		public CardToCardTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("TTUM_TYPE",Types.VARCHAR));
			declareParameter(new SqlParameter("LOCALDT",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	@Override
	public List<Object> getCardToCardTTUMData(UnMatchedTTUMBean beanObj)
	{
		String ttum_tableName = null;
		List<Object> data = new ArrayList<Object>();
		String fetch_condition = "";
		String sett_table = "";
		String ttum_format = null; 
		try
		{
			

				ttum_format = "DRM";
				ttum_tableName = "TTUM_CARDTOCARD_ISS_CBS";

				fetch_condition = " WHERE TRAN_DATE = TO_dATE('"+beanObj.getLocalDate()+"','DD/MM/YYYY')";// AND FILEDATE = (SELECT TO_DATE(MAX(FILEDATE),'DD/MM/YY') FROM "+sett_table+")";
		
			
			
			String getData = null;
			if(ttum_format!=null && ttum_format.equalsIgnoreCase("OLD"))
			{
				getData = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
						+ "rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR "
						+ " ,LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS, SUBSTR(ACCOUNT_NUMBER,1,4) AS SOL"
						+ ",to_char(TO_DATE(SYSDATE,'DD-mm-YY'),'DD/MM/YYYY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
			}
			else
			{
				getData = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
						+ "TRANSACTION_AMOUNT,"
						+ "TRANSACTION_PARTICULAR ,NVL(REFERENCE_NUMBER,' ') AS REMARKS,'1735' AS SOL"
						+ ",TO_DATE(FILEDATE,'DD/MM/YY') AS FILEDATE FROM "+ttum_tableName
						+fetch_condition;
						//+" WHERE FILEDATE = '"+beanObj.getFileDate()+"' ";//TO_DATE(?,'DD/MON/YYYY')";
			}
				logger.info("Getdata query is "+getData);
				
			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {}, new ResultSetExtractor<List<Object>>(){
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
							table_Data.put("SOL", rs.getString("SOL"));
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
			System.out.println("Exception in getTTUMData "+e);
			return null;

		}
		
	}
}
