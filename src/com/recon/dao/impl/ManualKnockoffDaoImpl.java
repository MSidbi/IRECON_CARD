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
import com.recon.model.FileSourceBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualKnockoffBean;

public class ManualKnockoffDaoImpl extends JdbcDaoSupport implements ManualKnockoffDao{
	
	private static final Logger logger = Logger.getLogger(ManualKnockoffDaoImpl.class);
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	public String ManualRollBack(String category, String subCategory, String fileDate)
	{
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		try
		{
			logger.info("START time FOR Rolling back "+new java.sql.Timestamp(new java.util.Date().getTime()));
			Map<String,Object> inParams = new HashMap<>();
			inParams.put("i_filedate", fileDate);
			if(category.equalsIgnoreCase("ONUS"))
			{
				inParams.put("i_filedate", fileDate);
				OnusRollBack rollBackexe = new OnusRollBack(getJdbcTemplate());
				outParams = rollBackexe.execute(inParams);
			}
			else if (category.equalsIgnoreCase("CASHNET"))
			{
				inParams.put("i_filedate", fileDate);
				if(subCategory.equalsIgnoreCase("ISSUER"))
				{
					CashnetIssRollBack rollBackexe = new CashnetIssRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
				else
				{
					CashnetAcqRollBack rollBackexe = new CashnetAcqRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
			}
			else if(category.equalsIgnoreCase("MASTERCARD"))
			{
				if(subCategory.equalsIgnoreCase("ISSUER"))
				{
					MasterCardIssRollBack rollBackexe = new MasterCardIssRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
				else
				{
					MasterCardAcqRollBack rollBackexe = new MasterCardAcqRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}

			}
			else if(category.equalsIgnoreCase("RUPAY"))
			{
				
				inParams.put("i_filedate", fileDate);
				if(subCategory.equalsIgnoreCase("DOMESTIC"))
				{
					RupayDomRollBack  rollBackexe = new RupayDomRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
				else if(subCategory.equalsIgnoreCase("INTERNATIONAL"))
				{
					RupayIntRollBack  rollBackexe = new RupayIntRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
			}
			else if(category.equalsIgnoreCase("VISA"))
			{
				inParams.put("i_filedate", fileDate);
				if(subCategory.equalsIgnoreCase("ISSUER"))
				{
					VisaIssRollBack rollBackexe = new VisaIssRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
				else
				{
					VisaAcqRollBack rollBackexe = new VisaAcqRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
			}
			else if(category.equalsIgnoreCase("NFS"))
			{
				inParams.put("i_filedate", fileDate);
				if(subCategory.equalsIgnoreCase("ISSUER"))
				{
					NfsIssRollBack rollBackexe = new NfsIssRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);
				}
				else
				{
					NfsAcqRollBack rollBackexe = new NfsAcqRollBack(getJdbcTemplate());
					outParams = rollBackexe.execute(inParams);

				}
			}
			else if(category.equalsIgnoreCase("POS"))//POS_ONUS CHECK CATEG AND SUBCA
			{
				inParams.put("I_FILEDATE", fileDate);
				PosOnusRollBack rollBackexe = new PosOnusRollBack(getJdbcTemplate());
				outParams = rollBackexe.execute(inParams);
			}
			else if(category.equalsIgnoreCase("FISDOM"))
			{
				FisdomRollBack rollBackexe = new FisdomRollBack(getJdbcTemplate());
				outParams = rollBackexe.execute(inParams);
			}
			else if(category.equalsIgnoreCase("CARDTOCARD"))
			{
				inParams.put("i_filedate", fileDate);
				CardtoCardRollBack rollBackexe = new CardtoCardRollBack(getJdbcTemplate());
				outParams = rollBackexe.execute(inParams);
			}
			logger.info("END time FOR Rolling back "+new java.sql.Timestamp(new java.util.Date().getTime()));

			if (outParams !=null && outParams.get("msg") != null) {

				return "RollBack Failed";
			}

		}
		catch(Exception e)
		{
			logger.info("Exception occurred while rolling back "+e);
			return "Exception Occured while Rolling Back!!";
		}
		return "RollBack Completed.";
	}
	
	private class FisdomRollBack extends StoredProcedure{
		private static final String insert_proc = "FISDOM_ROLLBACK";
		public FisdomRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	private class CardtoCardRollBack extends StoredProcedure{
		private static final String insert_proc = "cardtocard_iss_ROLLBACK";
		public CardtoCardRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	private class OnusRollBack extends StoredProcedure{
		private static final String insert_proc = "onus_rollback";
		public OnusRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	private class CashnetIssRollBack extends StoredProcedure{
		private static final String insert_proc = "CASHNET_ISS_ROLLBACK";
		public CashnetIssRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class CashnetAcqRollBack extends StoredProcedure{
		private static final String insert_proc = "CASHNET_ACQ_ROLLBACK";
		public CashnetAcqRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class MasterCardIssRollBack extends StoredProcedure{
		private static final String insert_proc = "MASTERCARD_ISS_ROLLBACK";
		public MasterCardIssRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class MasterCardAcqRollBack extends StoredProcedure{
		private static final String insert_proc = "MASTERCARD_ACQ_ROLLBACK";
		public MasterCardAcqRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class RupayDomRollBack extends StoredProcedure{
		private static final String insert_proc = "RUPAY_DOM_ROLLBACK";
		public RupayDomRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class RupayIntRollBack extends StoredProcedure{
		private static final String insert_proc = "RUPAY_INT_ROLLBACK";
		public RupayIntRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class VisaIssRollBack extends StoredProcedure{
		private static final String insert_proc = "VISA_ISS_ROLLBACK";
		public VisaIssRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class VisaAcqRollBack extends StoredProcedure{
		private static final String insert_proc = "VISA_ACQ_ROLLBACK";
		public VisaAcqRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class NfsIssRollBack extends StoredProcedure{
		private static final String insert_proc = "NFS_ISS_ROLLBACK";
		public NfsIssRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class NfsAcqRollBack extends StoredProcedure{
		private static final String insert_proc = "NFS_ACQ_ROLLBACK";
		public NfsAcqRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("i_filedate",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}
	
	private class PosOnusRollBack extends StoredProcedure{
		private static final String insert_proc = "POS_ONUS_ROLLBACK";
		public PosOnusRollBack(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("I_FILEDATE",Types.VARCHAR));
			//declareParameter(new SqlParameter("I_FILEDATE1",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
	}

	//VALIDATION OF RECON DATE
	
	@Override
	public Map<String, Object> validateRollBackDate(String category, String subcategory , String filedate)
	{

		Map<String, Object> output = new HashMap();
		boolean rollbackCanBeDone = false;
		String checkReconProcess = "select count(*) from main_file_upload_dtls where category = ? and file_subcategory = ?"
				+ " and filedate = str_TO_DATE(?,'%Y/%m/%d') "+
				"AND (filter_flag = 'Y' OR knockoff_flag = 'Y' OR comapre_flag = 'Y' OR manualcompare_flag = 'Y')";
		
		logger.info("checkReconProcess "+checkReconProcess);
		
		int reconcount = getJdbcTemplate().queryForObject(checkReconProcess, new Object[] {category ,subcategory,filedate},Integer.class);
				
		System.out.println("rollback can be done ? "+reconcount);
		if(reconcount > 0)
		{
			//CHECK WHETHER RECON FOR FURTHER DATE HAS BEEN PROCESSED
			String checkFutureRecon = "select count(*) from main_file_upload_dtls where "
					+ " category = ? and file_subcategory = ? and filedate > str_to_date(?,'%y/%m/%d')"
					+" AND (filter_flag = 'Y' OR knockoff_flag = 'Y' OR comapre_flag = 'Y' OR manualcompare_flag = 'Y')";
			logger.info("Query is "+checkFutureRecon);
			int futureReconCount = getJdbcTemplate().queryForObject(checkFutureRecon, new Object[] {category,subcategory,filedate},
					Integer.class);
			
			if(futureReconCount>0)
			{
				//return false;
				output.put("result", false);
				output.put("msg", "Recon for future date is processed \n"+ "Perform RollBack for future date first");
				return output;
			}
			else
			{
				output.put("result", true);
				//return true;
				return output;
			}
		}
		else
		{
			//return false;
			output.put("result", false);
			output.put("msg", "Recon is not processed for selected date");
			return output;
		}
		
		
	
	}
	
	
	
	//ADDED BY INT8624 FOR GETTING FILES
	@Override
	public List<FileSourceBean> getFiles(String stcategory,String subcategory)
	{
		String GET_FILENAME = "select fileid as fileid , filename as filename from main_filesource filesrc  where file_category = ? and activeflag='A'"
				+ " AND file_subcategory = ?";
		/*Map<String, String> files = getJdbcTemplate().query(GET_FILENAME, new Object[] {},new ResultSetExtractor() {
			 public Object extractData(ResultSet rs) throws SQLException {
				 Map map = new HashMap();
				 while (rs.next()) {
				 String col1 = rs.getString("FILEID");
				 String col2 = rs.getString("FILENAME");
				 map.put(col1, col2);
				 }
				 //}
				 return map;
				 };
				 });*/
		
		List<FileSourceBean>  ftpFileList = getJdbcTemplate().query(GET_FILENAME,new Object[] {stcategory,subcategory},new BeanPropertyRowMapper(FileSourceBean.class));
		
		System.out.println("map is "+ftpFileList);
		return ftpFileList;
	}
	
	//VALIDATION OF RECON FOR MASTERCARD
	
		@Override
		public Map<String, Object> validateMastercardAcqRollBack(String category, String subcategory , String filedate)
		{

			boolean rollbackCanBeDone = false;
			Map<String, Object> output = new HashMap();
			String checkReconProcess = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ?"
					+ " AND FILEDATE = TO_DATE(?,'DD/MON/YYYY') "+
					"AND (FILTER_FLAG = 'Y' OR KNOCKOFF_FLAG = 'Y' OR COMAPRE_FLAG = 'Y' ) AND FILEID != '41'";
			
			int reconcount = getJdbcTemplate().queryForObject(checkReconProcess, new Object[] {category ,subcategory,filedate},Integer.class);
					
			System.out.println("rollback can be done ? "+reconcount);
			if(reconcount > 0)
			{
				//CHECK WHETHER RECON FOR FURTHER DATE HAS BEEN PROCESSED
				String checkFutureRecon = "SELECT count(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE "
						+ " CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEDATE > TO_DATE(?,'DD/MON/YYYY')"
						+" AND (FILTER_FLAG = 'Y' OR KNOCKOFF_FLAG = 'Y' OR COMAPRE_FLAG = 'Y' ) AND FILEID != '41'";
				logger.info("Query is "+checkFutureRecon);
				int futureReconCount = getJdbcTemplate().queryForObject(checkFutureRecon, new Object[] {category,subcategory,filedate},
						Integer.class);
				
				if(futureReconCount>0)
				{
					//return false;
					output.put("result", false);
					output.put("msg", "Recon for future date is processed \n"+ "Perform RollBack for future date first");
					return output;
				}
				else
				{
					output.put("result", true);
					//return true;
					return output;
				
				}
			}
			else
			{	
				//return false;
				output.put("result", false);
				output.put("msg", "Recon is not processed for selected date");
				return output;
			
			}
			
			
		
		}
	
		@Override
		public boolean knockoffAllData(ManualKnockoffBean beanObj, MultipartFile file)
		{
			String thisLine = null;
			int lineNumber = 0,batchCount= 0,i=1 ,colIndex = 0,MandateColIndex = 0;
			String mergerCategory = beanObj.getCategory();
			String tableColumns = "",insertCols = "",colValues= "",updateQuery  = "",updateCond = "";
			PreparedStatement pst =null;
			List<Integer> notToConsider = new ArrayList<Integer>();
			try 
			{
				if(!beanObj.getStSubCategory().equals("-"))
				{
					mergerCategory = mergerCategory +"_"+beanObj.getStSubCategory().substring(0,3);
					logger.info("merger Category is "+mergerCategory);
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
				String fileHeaderColumns = br.readLine();
				logger.info("headerColumns value is "+fileHeaderColumns);
				
				//1. CREATE TABLE IF DOESN EXISTS USING HEADER COLUMNS
				String[] headerColumns = fileHeaderColumns.split(Pattern.quote("|"));
				for(String tableCol : headerColumns)
				{
					if(!tableCol.trim().equalsIgnoreCase("DCRS_REMARKS") && !tableCol.trim().equalsIgnoreCase("CREATEDDATE"))
					{
						MandateColIndex++;
						tableColumns = tableColumns +tableCol.trim()+" VARCHAR (500 BYTE) ,";
						insertCols = insertCols + tableCol.trim()+",";
						colValues = colValues+"?,";
						updateCond = updateCond + " TRIM(NVL(OS1."+tableCol.trim()+",'0')) = TRIM(NVL(OS2."+tableCol.trim()+",'0')) AND ";
					}
					else
					{
						notToConsider.add(colIndex);
					}
					colIndex++;
				}
				
				if(!tableColumns.trim().equalsIgnoreCase(""))
					tableColumns = tableColumns+" DCRS_REMARKS VARCHAR(100 BYTE),CREATEDDATE DATE";
				
				if(!insertCols.trim().equalsIgnoreCase(""))
					insertCols = insertCols+" DCRS_REMARKS,CREATEDDATE";
				
				if(!colValues.equalsIgnoreCase(""))
					colValues = colValues +"?, to_date(?,'dd/mm/yyyy')";
				
				if(!updateCond.equalsIgnoreCase(""))
					updateCond = updateCond +" OS2.DCRS_REMARKS = '"+beanObj.getNewRemarks()+"'";
				
				logger.info("tableColumns are "+tableColumns);
				logger.info("insert columns are "+insertCols);
				logger.info("update condition is "+updateCond);
				
				
				String tableName = beanObj.getStSelectedFile()+"_KNOCKOFF_"+mergerCategory;
				logger.info("Table name is "+tableName);
				
				if(tableName.length()>31)
				{
					tableName = beanObj.getStSelectedFile()+"_KNOCKOFF_"+mergerCategory.substring(3);
					logger.info("NOW TABLE NAME IS "+tableName);
				}
				
				
				if(getJdbcTemplate().queryForObject("SELECT count (*) FROM tab WHERE tname  = '"+tableName+"'", new Object[] {},Integer.class) == 0)
				{
					getJdbcTemplate().execute("create table "+tableName + "("+tableColumns+")");
					logger.info("Table created");
				}
				else
				{
					logger.info("Dropping the table ");
					getJdbcTemplate().execute("DROP TABLE "+tableName);
					logger.info(tableName+" Table dropped");
					getJdbcTemplate().execute("create table "+tableName + "("+tableColumns+")");
					logger.info(tableName+" table created now");
				}
				//TABLE CREATION COMPLETED
				//2. INSERTING FILE DATA IN TABLE
				String insertData = "INSERT INTO "+tableName+" ("+insertCols+") VALUES("+colValues+")";
				logger.info("Insert query is "+insertData);
				pst = getConnection().prepareStatement(insertData);
				
				while ((thisLine = br.readLine()) != null) {
					lineNumber++;
					batchCount++;
					String[] line = thisLine.split(Pattern.quote("|"));
					for(i =1 ;i<=line.length ; i++)
					{
						if(!notToConsider.contains(i-1))
						{
							String value = line[i-1].trim();
							if(value != "")
							{
								pst.setString(i	, value);
							}
							else
							{
								pst.setString(i, null);
							}
						}
					}
					pst.setString(MandateColIndex+1, beanObj.getNewRemarks());
					
					pst.setString(MandateColIndex+2,beanObj.getDatepicker());
					pst.addBatch();
					if(batchCount ==500)
					{
						batchCount = 0;
						pst.executeBatch();
						logger.info("Executed batch ");
					}
				}
				pst.executeBatch();
				logger.info("Completed insertion in table");
				
				
			//2. UPDATE SETTLEMENT TABLE network wise
				if(beanObj.getCategory().equalsIgnoreCase("POS"))
				{
					logger.info("Category is POS");
					String settlement_tableName = "";
					String deleteTableName = "";
					
						if(beanObj.getOldRemarks().contains("POS_ONU_C_"))
						{
							settlement_tableName = "SETTLEMENT_POS_C";
							deleteTableName = "C1_POS_ONUS_UNMAHD";
						}
						else
						{
							settlement_tableName = "SETTLEMENT_"+beanObj.getCategory()+"_"+beanObj.getStSelectedFile();
							deleteTableName = "SW_POS_ONUS_UNMHD";
						}
					
					
						updateQuery = "UPDATE "+settlement_tableName+" OS1 SET DCRS_REMARKS = '"+beanObj.getNewRemarks()+"'  WHERE OS1.DCRS_REMARKS = '"+beanObj.getOldRemarks()+"' AND "
								+" FILEDATE = '"+beanObj.getDatepicker()+"' AND "+
								" EXISTS( SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";
						
						logger.info("update query is "+updateQuery);
						logger.info("Update Execution starts");
						getJdbcTemplate().execute(updateQuery);
						logger.info("update Execution Completed");
						
						if(beanObj.getStSelectedFile().equalsIgnoreCase("CBS"))
						{
							String deleteQuery = "DELETE FROM CBS_POS_ONUS_UNMHD OS1 WHERE FILEDATE = '"+beanObj.getDatepicker()+"' AND EXISTS "
									+"(SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";
							logger.info("delete query is "+deleteQuery);
							getJdbcTemplate().execute(deleteQuery);
						}
						else if(beanObj.getStSelectedFile().equalsIgnoreCase("POS"))
						{

							String deleteQuery = "DELETE FROM POS_POS_ONUS_UNMD OS1 WHERE FILEDATE = '"+beanObj.getDatepicker()+"' AND EXISTS "
									+"(SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";	
							logger.info("delete query is "+deleteQuery);
							getJdbcTemplate().execute(deleteQuery);
						}
						else if(beanObj.getStSelectedFile().equalsIgnoreCase("SWITCH"))
						{
							String deleteQuery = "DELETE FROM "+deleteTableName+" OS1 WHERE FILEDATE ='"+beanObj.getDatepicker()+"' AND EXISTS "
									+"(SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";
							logger.info("delete query is "+deleteQuery);
							getJdbcTemplate().execute(deleteQuery);
						}
					
				}
				else if(beanObj.getCategory().equalsIgnoreCase("NFS") || beanObj.getCategory().equalsIgnoreCase("CASHNET"))
				{
					logger.info("settlement table of nfs is SETTLEMENT_"+mergerCategory+"_"+beanObj.getStSelectedFile());
					updateQuery = "UPDATE SETTLEMENT_"+mergerCategory+"_"+beanObj.getStSelectedFile()+" OS1 SET DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' WHERE DCRS_REMARKS = '"+beanObj.getOldRemarks()+"' AND "
							+" FILEDATE = '"+beanObj.getDatepicker()+"' AND "
							+ " EXISTS(SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";
					
					logger.info("update query is "+updateQuery);
					logger.info("Update Execution starts");
					getJdbcTemplate().execute(updateQuery);
					logger.info("update Execution Completed");
				}
				else if(beanObj.getCategory().equalsIgnoreCase("RUPAY") || beanObj.getCategory().equalsIgnoreCase("VISA") || beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
				{
					updateQuery = "UPDATE SETTLEMENT_"+beanObj.getCategory()+"_"+beanObj.getStSubCategory().substring(0, 3)+"_"+beanObj.getStSelectedFile()+" OS1 SET DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' WHERE DCRS_REMARKS = '"+beanObj.getOldRemarks()+"' AND "
							+ " FILEDATE ='"+beanObj.getDatepicker()+"' AND "
							+ " EXISTS(SELECT 1 FROM "+tableName+" OS2 WHERE "+updateCond+" )";
					
					logger.info("update query is "+updateQuery);
					logger.info("Update Execution starts");
					getJdbcTemplate().execute(updateQuery);
					logger.info("update Execution Completed");
				}
				
				logger.info("COMPLETED UPDATION");
				
				//3. DROPPING TEMP TABLE
				logger.info("Dropping table starts");
				getJdbcTemplate().execute("DROP TABLE "+tableName);
				logger.info("Dropping table Completed");
				
				if(beanObj.getCategory().equalsIgnoreCase("MASTERCARD") && beanObj.getStSelectedFile().equalsIgnoreCase("POS"))
				{
					String deleteQuery = "delete settlement_mastercard_pos  WHERE FILEDATE = '"+beanObj.getDatepicker()+"' and HEADER_TYPE in ('ADMINISTRATIVE','FEE_COLLECTION')";
					logger.info("delete query is "+deleteQuery);
					getJdbcTemplate().execute(deleteQuery);
				}
				
				
			}
			catch(Exception e)
			{
				logger.info("Exception in knockoffSwitchData "+e);
				return false;

			}
			finally
			{
				if(pst!= null)
				{
					pst = null;
				}
			}
			return true;
		}
		
		@Override
		public int getUpdatedRecordCount(ManualKnockoffBean beanObj)
		{
			String getCountQuery = "";
			String mergerCategory = beanObj.getCategory();
			int count = 0;
			try
			{
				if(!beanObj.getStSubCategory().equalsIgnoreCase("-"))
				{
					mergerCategory = mergerCategory+"_"+beanObj.getStSubCategory().substring(0, 3);
				}
				if(beanObj.getCategory().equalsIgnoreCase("NFS") || beanObj.getCategory().equalsIgnoreCase("CASHNET"))
				{
					logger.info("settlement table of nfs is SETTLEMENT_"+mergerCategory+"_"+beanObj.getStSelectedFile());
					getCountQuery = "SELECT COUNT(*) FROM SETTLEMENT_"+mergerCategory+"_"+beanObj.getStSelectedFile()+" WHERE DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' AND FILEDATE ='"+beanObj.getDatepicker()+"'";
					
				}
				else if(beanObj.getCategory().equalsIgnoreCase("RUPAY") || beanObj.getCategory().equalsIgnoreCase("VISA") || beanObj.getCategory().equalsIgnoreCase("MASTERCARD"))
				{
					getCountQuery = "SELECT COUNT(*) FROM SETTLEMENT_"+mergerCategory+"_"+beanObj.getStSelectedFile()+" WHERE DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' AND FILEDATE ='"+beanObj.getDatepicker()+"'";
					
				}
				else if(beanObj.getCategory().equalsIgnoreCase("POS"))
				{
					if(beanObj.getOldRemarks().contains("POS_ONU_C_"))
					{
						getCountQuery = "SELECT COUNT(*) FROM SETTLEMENT_POS_C WHERE DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' AND FILEDATE = '"+beanObj.getDatepicker()+"'";
					}
					else
						getCountQuery = "SELECT COUNT(*) FROM SETTLEMENT_"+beanObj.getCategory()+"_"+beanObj.getStSelectedFile()+" WHERE DCRS_REMARKS = '"+beanObj.getNewRemarks()+"' AND FILEDATE = '"+beanObj.getDatepicker()+"'";
				}
				logger.info("Count query is "+getCountQuery);
				count = getJdbcTemplate().queryForObject(getCountQuery,Integer.class);
				logger.info("Count is "+count);
			}
			catch(Exception e)
			{
				logger.info("Exception occured in getUpdatedRecordCount "+e);
				return 0;
			}
			return count;
		}
		
		//CHECKING WHETHER RECON IS PROCESSED FOR THE DATE SELECTED FOR MANUAL RECON
		public boolean checkreconStatus(String category, String subCategory, String fileDate)
		{
			try
			{
				String checkReconProcess = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS T1, MAIN_FILESOURCE T2 WHERE T1.CATEGORY = ? " + 
						"  AND T1.FILE_SUBCATEGORY = ? AND " + 
						"T1.FILEID IN (SELECT T2.FILEID FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = ? AND FILE_SUBCATEGORY = ?) " + 
						"AND T1.FILTER_FLAG = T2.FILTERATION AND T1.KNOCKOFF_FLAG = T2.KNOCKOFF AND " + 
						"T2.FILE_CATEGORY = ? AND T2.FILE_SUBCATEGORY = ? AND T1.FILEDATE = ? "+ 
						"AND COMAPRE_FLAG = 'Y' AND MANUALCOMPARE_FLAG = 'Y'";
				
				logger.info("Query is "+checkReconProcess);
				
				int count = getJdbcTemplate().queryForObject(checkReconProcess,new Object[] {category,subCategory, category, subCategory,category,subCategory,fileDate}, Integer.class);
				
				logger.info("count is "+count);
				
				if(count == 0)
				{
					return false;
				}
				
				
			}
			catch(Exception e)
			{
				logger.info("Exception in checkreconStatus "+e);
				return false;
			}
			return true;
		}
	}
