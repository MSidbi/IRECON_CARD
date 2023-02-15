package com.recon.service.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ddf.EscherDggRecord.FileIdCluster;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.AddNewSolBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.NFSSettlementService;
import com.recon.util.ReadDFSJCBMonthlyNTSLFile;
import com.recon.util.ReadDFSRawFile;
import com.recon.util.ReadDFSandJCBNTSLFile;
import com.recon.util.ReadNFSMonthlyNTSLFile;
import com.recon.util.ReadNFSNTSLFile;
import com.recon.util.ReadNTSLFile;
import com.recon.util.ReadNfsRawData;

public class NFSSettlementServiceImpl extends JdbcDaoSupport implements NFSSettlementService{
	
	
	
	private int procCount;

	@Override
	public HashMap<String, Object> validatePrevFileUpload(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> validate = new HashMap<String, Object>();
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
				validate.put("result", true);
				validate.put("msg", "File is not Configured!!!");
				return validate;
			}
			String checkTotalCount = "select count(*) from main_settlement_file_upload where category = ? and file_subcategory = ? and fileid = ?";
			int totalCount = getJdbcTemplate().queryForObject(checkTotalCount, new Object[] {beanObj.getCategory(),beanObj.getStSubCategory(),file_id},Integer.class);
			if(totalCount >0)
			{
				if(beanObj.getCycle() != 0 && beanObj.getFileName().contains("NTSL"))
				{

					//check for selected filedate
					String checkForSameDate = "select count(*) from main_settlement_file_upload where fileid = ? and filedate =  str_to_date(?,'%Y/%m/%d') and cycle = ?";
					int dataCount = getJdbcTemplate().queryForObject(checkForSameDate,new Object[] {file_id,beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
					if(dataCount >0)
					{
						validate.put("result", true);
						validate.put("msg", "File is already uploaded!!!");
					}
					else
					{
						validate.put("result", false);
					}
				
				}
				else
				{
					//check for selected filedate
					String checkForSameDate = "select count(*) from main_settlement_file_upload where fileid = ? and filedate =  str_to_date(?,'%Y/%m/%d')";
					int dataCount = getJdbcTemplate().queryForObject(checkForSameDate,new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
					if(dataCount >0)
					{
						validate.put("result", true);
						validate.put("msg", "File is already uploaded!!!");
					}
					else
					{
						validate.put("result", false);
					}
				}
				//COMMENTED PART IS VALIDATION FOR PREVIOUS DAY FILE UPLOAD
				/*else
				{
					String checkPrevFile = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE =  TO_DATE(?,'DD/MON/YYYY')-1";
					int Uploadcount = getJdbcTemplate().queryForObject(checkPrevFile, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
					System.out.println("Upload Count is "+Uploadcount);

					if(Uploadcount<=0)
					{
						validate.put("result", true);
						validate.put("msg", "Previos date file is not uploaded");
					}
					else
						validate.put("result", false);
				}*/
			}
			else
			{
				System.out.println("Its first time upload");
				validate.put("result", false);
			}

		}
		catch(Exception e)
		{
			System.out.println("Exception in NFSSettlementServiceImpl: validatePrevFileUpload "+e);
			validate.put("result", true);
			validate.put("msg", "Exception Occured!!");
		}
		return validate;
	}
	
	@Override 
	public HashMap<String, Object> uploadDFSRawData(NFSSettlementBean beanObj,MultipartFile file)
	{
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		try
		{
			ReadDFSRawFile nfsRawData = new ReadDFSRawFile(); 
			mapObj =nfsRawData.readData(beanObj, getConnection(), file);
			System.out.println("result is "+mapObj);
			boolean result = (boolean)mapObj.get("result");
			int count = (Integer)mapObj.get("count");
			if(result)
			{
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
				System.out.println("File id is "+file_id);
				String insertData = "insert into main_settlement_file_upload(fileid, filedate, uploadby, uploaddate, category, upload_flag, file_subcategory,cycle,settlement_flag,interchange_flag,file_count) " + 
						"VALUES('"+file_id+"',str_TO_DATE('"+beanObj.getDatepicker()+"','%Y/%m/%d'),'"+beanObj.getCreatedBy()+"',sysdate(),'"+beanObj.getCategory()+"','Y','"+beanObj.getStSubCategory()+"',"
								+ "'1','N','N','1')";
				getJdbcTemplate().execute(insertData);
				mapObj.put("entry", true);
			}
			
			return mapObj;
		}
		catch(Exception e)
		{
			System.out.println("Exception is "+e);
			mapObj.put("result", false);
			mapObj.put("count", 0);
			return mapObj;
		}
		
	}
	
	@Override
	public HashMap<String, Object> uploadNTSLFile(NFSSettlementBean beanObj,MultipartFile file)
	{
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		
		try
		{
		if(beanObj.getFileName().contains("NFS"))
		{
			//ReadNTSLFile readObj = new ReadNTSLFile();
			ReadNFSNTSLFile readObj = new ReadNFSNTSLFile();
			mapObj = readObj.fileupload(beanObj, file, getConnection());
		}
		else
		{
			ReadDFSandJCBNTSLFile readObj = new ReadDFSandJCBNTSLFile();
			if(beanObj.getFileName().contains("DFS"))
			{
				mapObj = readObj.DFSfileupload(beanObj, file, getConnection());
			}
			else
			{
				mapObj = readObj.JCBfileupload(beanObj, file, getConnection());
			}
		}
		
		System.out.println("result is "+mapObj);
		boolean result = (boolean)mapObj.get("result");
		int count = (Integer)mapObj.get("count");
		if(result)
		{
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			System.out.println("File id is "+file_id);
			String insertData = "insert into main_settlement_file_upload(fileid, filedate, uploadby, category, upload_flag, file_subcategory,cycle,settlement_flag,interchange_flag,file_count) " + 
					"VALUES('"+file_id+"',str_TO_DATE('"+beanObj.getDatepicker()+"','%Y/%m/%d'),'"+beanObj.getCreatedBy()+"','"+beanObj.getCategory()+"','Y','"+beanObj.getStSubCategory()+"','"+beanObj.getCycle()+"',"
							+ "'N','N','1')";
			getJdbcTemplate().execute(insertData);
			mapObj.put("entry", true);
		}
		
		return mapObj;
	
		}
		catch(Exception e)
		{
			logger.info("Exception in uploadNTSLFile " +e);
			mapObj.put("result", false);
			mapObj.put("count", 0);
			return mapObj;
		}
	}

	
	@Override
	public HashMap<String,Object> ValidateDailySettProcess(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> result = new HashMap<String, Object>();
		int rawCount = 0;
		List<String> subcategories = new ArrayList<String>();
		subcategories.add("ISSUER");
		subcategories.add("ACQUIRER");
		int uploadCount = 0;
		int file_id = 0;
		int adjustmentFileCount = 0;
		try
		{
			try
			{
				//CHECK WHETHER FILE IS UPLOADED FOR SELECTED DATE
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			}
			catch(Exception e)
			{
				result.put("result",false);
				result.put("msg", "File is not Configured!");
				return result;
							
			}
				String checkFileUploaded = "select count(1) from main_settlement_file_upload where fileid = ? and filedate = ? and cycle = ?";
				uploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
				System.out.println("Already Process count "+uploadCount);

				//LOGIC TO CHECK WHETHER ADJUSTMENT FILE IS UPLOADED OR NOT
				if(beanObj.getFileName().equalsIgnoreCase("NTSL-NFS"))
				{
					try {
						int adjfile_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),"NFS_ADJUSTMENT","-" },Integer.class);
						adjustmentFileCount = getJdbcTemplate().queryForObject("select count(*) from main_settlement_file_upload where fileid = ? and filedate = ? and cycle = '1'", 
								new Object[] {adjfile_id,beanObj.getDatepicker()}, Integer.class);
						logger.info("Adjustment file count is "+adjustmentFileCount);
					}
					catch(Exception e)
					{
						result.put("result",false);
						result.put("msg", "Adjustment File is not Configured!");
						return result;
					}
				}
				else
				{
					adjustmentFileCount = 1;
				}
						
				if(uploadCount > 0 && adjustmentFileCount >0)
				{
					//NOW CHECK WHETHER FILE IS ALREADY PROCESSED			
					System.out.println("File id is "+file_id);
					String alreadyProc = "select count(1) from main_settlement_file_upload where settlement_flag = 'y' and fileid = ? and filedate = ? and cycle = ?";
					int procCount = getJdbcTemplate().queryForObject(alreadyProc, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
					System.out.println("Already Process count "+alreadyProc);

					if(procCount == 0 )
					{
						//CHECK WHETHER THIS IS FIRST TIME OF PROCESSING 
						String checkFirstTime = "select count(1) from main_settlement_file_upload where fileid = ? and filedate < '"+beanObj.getDatepicker()+"'";
						int firstCount = getJdbcTemplate().queryForObject(checkFirstTime, new Object[] {file_id},Integer.class);
						if(firstCount >0)
						{		
							int getFileCount = getJdbcTemplate().queryForObject("select file_count from main_filesource where fileid = ?", new Object[]{file_id}, Integer.class);
							//CHECK WHETHER PREVIOUS DAY INTERCHANGE IS PROCESSED FOR BOTH CYCLES
							String prevdayProcess = "select count(1) from main_settlement_file_upload where settlement_flag = 'Y' and fileid = ? "
									+ "and filedate = str_to_date('"+beanObj.getDatepicker()+"','%Y/%m/%d')- interval '1' day ";
							int checkCount = getJdbcTemplate().queryForObject(prevdayProcess, new Object[] {file_id},Integer.class);
							if(checkCount > 0 && checkCount == getFileCount)
							{
								
								boolean rawfileUploaded = true;
								for(String subcategory : subcategories)
								{
									if(beanObj.getFileName().equalsIgnoreCase("NTSL-NFS"))
									{
										//We need to check entry for both ISSUER and ACQUIRER raw data

										file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "NFS","NFS",subcategory },Integer.class);
										System.out.println("File id is "+file_id);

										//check raw file for nfs is uploaded
										String checkRawData = "select count(1) from main_file_upload_dtls where fileid = ? and filedate =?";
										rawCount = getJdbcTemplate().queryForObject(checkRawData, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
										System.out.println("rawCount is "+rawCount);
									}
									else
									{

										file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName().split("-")[1],beanObj.getCategory(),subcategory },Integer.class);
										System.out.println("File id is "+file_id);

										//check raw file for DFS AND JCB is uploaded
										String checkRawData = "select count(1) from main_settlement_file_upload where fileid = ? and filedate = ?";
										rawCount = getJdbcTemplate().queryForObject(checkRawData, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
										System.out.println("rawCount is "+rawCount);

									}
									if(rawCount == 0)
									{
										rawfileUploaded = false;
									}
								}
								if(rawfileUploaded)
								{
									//CHECK NTSL IS UPLOADED FOR SELECTED DATE
									file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
									System.out.println("File id is "+file_id);

									String checkNTSLEntries = "select count(1) from main_settlement_file_upload where FILEID = ? AND filedate = ? AND CYCLE = ?";
									int entryCount = getJdbcTemplate().queryForObject(checkNTSLEntries, new Object[] {file_id,beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
									System.out.println("Entry count is "+entryCount);

									if(entryCount == 0)
									{
										System.out.println("NTSL File is not uploaded for selected date");
										result.put("result", false);
										result.put("msg", "Please Upload NTSL Files of selected date");
									}
									else
									{
										result.put("result", true);
									}
								}
								else
								{
									result.put("result",false);
									result.put("msg", "Issuer and Acquirer Raw Files are not uploaded");
								}
							}
							else
							{
								result.put("result",false);
								result.put("msg", "Previous day settlement is not processed");
							}
						}
						else
						{
							result.put("result", true);
						}
					}
					else
					{
						result.put("result",false);
						result.put("msg", "File is already Processed");
					}
				}
				else
				{
					if(uploadCount == 0)
					{
						result.put("result", false);
						result.put("msg", "Upload NTSL File for selected Date");
					}
					else
					{

						result.put("result", false);
						result.put("msg", "Upload Adjustment File for selected Date");
					
					}
				}
			
				
		}
		catch(Exception e)
		{
			System.out.println("Exception in ValidateDailySettProcess ");
			result.put("result", false);
			result.put("msg", "Exception Occurred");
		}
		return result;
	}
	
	
	

	@Override
	public HashMap<String,Object> checkNFSMonthlyProcess(NFSSettlementBean beanObj)
	{
		HashMap<String,Object> result = new HashMap<String, Object>();
		int entryCount =0, totalDays = 0,cycleCount = 0;
		int file_id = 0;
		String checkFlag = null;
		try
		{

			totalDays = Integer.parseInt(beanObj.getToDate().substring(0, 2));

			System.out.println("diff is "+totalDays);
			try
			{
				//1. CHECK WHETHER SETTLEMENT IS ALREADY PROCESSED FOR SELECTED MONTH
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),"MONTHLY_SETTLEMENT",beanObj.getStSubCategory()},Integer.class);
			}
			catch(Exception e)
			{
				result.put("result", false);
				result.put("msg", "File is not configured");
				return result;
			}
			try
			{
				checkFlag = getJdbcTemplate().queryForObject("SELECT PROCESS_FLAG FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE FILEID = ? AND FILEDATE = TO_DATE(?,'DD/MM/YYYY')", new Object[] {file_id, beanObj.getDatepicker()},String.class);
				logger.info("Flag is "+checkFlag);
			}
			catch(Exception e)
			{
				result.put("result", false);
				result.put("msg", "NTSL File is not uploaded for selected month");
				return result;
			}
			if(checkFlag != null)
			{
				if(checkFlag.equalsIgnoreCase("Y"))
				{
					result.put("result", false);
					result.put("msg", "Settlement is already processed for selected month");
					
				}
				else
				{
					result.put("result", true);
					if(!beanObj.getFileName().contains("NFS"))
					{
						
							//CHECK NTSL IS UPPLOADED and processed FOR SELECTED MONTH
							file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-"},Integer.class);
							System.out.println("File id is "+file_id);

							try
							{
								String checkNTSLEntries1Cycle = "select count(*) from main_settlement_file_upload where FILEID = ? AND filedate between TO_DATE(?,'DD/MM/YYYY') AND TO_DATE(?,'DD/MM/YYYY')";
								entryCount = getJdbcTemplate().queryForObject(checkNTSLEntries1Cycle, new Object[] {file_id,beanObj.getDatepicker(),beanObj.getToDate()},Integer.class);
								System.out.println("Entry count is "+entryCount);
								String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
								cycleCount = getJdbcTemplate().queryForObject(getCycleCount, new Object[] {file_id},Integer.class);
								logger.info("Cycle count is "+cycleCount);
							}
							catch(Exception e)
							{
								logger.info("Exception while validating cycle 1 entires "+e);
								result.put("result", false);
								result.put("msg", "Exception while validating NTSL entires ");
								return result;
							}
							if((entryCount/cycleCount) != totalDays)
							{
								System.out.println("NTSL are not uploaded for all days of selected month");
								result.put("result", false);
								result.put("msg", "Please process all Settlement First");
							}
							else
							{
								result.put("result", true);
							}
					}
				}
				return result;
			}
			else
			{
				result.put("result", false);
				result.put("msg", "NTSL File is not uploaded for selected month");
				return result;
			}

			//TEMPORARY COMMENTING BELOW CODE AS WE HAVE TO SKIP DAILY SETTLEMENT FOR NOW
			/*	if(checkCount == 0)
			{
				//CHECK NTSL FOR CYCLE 1 IS UPPLOADED FOR SELECTED MONTH
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-"},Integer.class);
				System.out.println("File id is "+file_id);

				try
				{
					String checkNTSLEntries1Cycle = "select count(*) from main_settlement_file_upload where process_flag = 'Y' and FILEID = ? AND filedate between TO_DATE(?,'DD/MM/YYYY') AND TO_DATE(?,'YYYY/MM/DD') AND CYCLE = '1'";
					entryCount = getJdbcTemplate().queryForObject(checkNTSLEntries1Cycle, new Object[] {file_id,beanObj.getDatepicker(),beanObj.getToDate()},Integer.class);
					System.out.println("Entry count is "+entryCount);
				}
				catch(Exception e)
				{
					logger.info("Exception while validating cycle 1 entires "+e);
					result.put("result", false);
					result.put("msg", "Please process settlement for Cycle 1 for all days of selected month");
					return result;
				}
				if(entryCount != totalDays)
				{
					System.out.println("NFS Settlement for Cycle 1 is not processed for all days of selected month");
					result.put("result", false);
					result.put("msg", "Please process all NFS cycle 1 Settlement First");
				}
				else
				{
					try
					{
						//NOW CHECK WHETHER CYCLE 2 IS PROCESSED
						String checkNTSLEntries2Cycle = "select count(*) from main_settlement_file_upload where process_flag = 'Y' and FILEID = ? AND filedate between TO_DATE(?,'DD/MM/YYYY') AND TO_DATE(?,'YYYY/MM/DD') AND CYCLE = '2'";
						entryCount = getJdbcTemplate().queryForObject(checkNTSLEntries2Cycle, new Object[] {file_id,beanObj.getDatepicker(),beanObj.getToDate()},Integer.class);
						System.out.println("Entry count is "+entryCount);
					}
					catch(Exception e)
					{
						logger.info("Exception while validating cycle 2 entires "+e);
						result.put("result", false);
						result.put("msg", "Please process settlement for Cycle 2 for all days of selected month");
						return result;											
					}
					if(entryCount != totalDays)
					{
						System.out.println("NFS Settlement for Cycle 2 is not processed for selected month");
						result.put("result", false);
						result.put("msg", "Please process all NFS cycle 2 Settlement First");
					}
					else
					{
						result.put("result", true);
					}
				}
			}
			else
			{
				result.put("result", false);
				result.put("msg", "Settlement is already processed for selected month");
			}*/
			//VALIDATION FOR DFS AND JCB
			

		}
		catch(Exception e)
		{
			System.out.println("Exception in checkNFSMonthlyProcess "+e);
			result.put("result", false);
			result.put("msg", "Exception Occurred");
		}
		return result;
	}
	
	@Override
	public HashMap<String, Object> uploadMonthlyNTSLFile(NFSSettlementBean beanObj,MultipartFile file)
	{
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		
		try
		{
		if(beanObj.getFileName().contains("NFS"))
		{
			ReadNFSMonthlyNTSLFile readObj = new ReadNFSMonthlyNTSLFile();
			mapObj = readObj.fileupload(beanObj, file, getConnection());
		}
		else if(beanObj.getFileName().contains("DFS"))
		{
			ReadDFSJCBMonthlyNTSLFile readObj = new ReadDFSJCBMonthlyNTSLFile();
			mapObj = readObj.DFSfileupload(beanObj, file, getConnection());
		}
		else
		{
			ReadDFSJCBMonthlyNTSLFile readObj = new ReadDFSJCBMonthlyNTSLFile();
			mapObj = readObj.JCBfileupload(beanObj, file, getConnection());
		}
		System.out.println("result is "+mapObj);
		boolean result = (boolean)mapObj.get("result");
		int count = (Integer)mapObj.get("count");
		if(result)
		{
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			System.out.println("File id is "+file_id);
			String insertData = "INSERT INTO MAIN_MONTHLY_NTSL_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,PROCESS_FLAG,TTUM_FLAG) " + 
					"VALUES('"+file_id+"',TO_DATE('"+beanObj.getDatepicker()+"','MM/YYYY'),'"+beanObj.getCreatedBy()+"',sysdate,'"+beanObj.getCategory()+"','Y','"+beanObj.getStSubCategory()+"','N','N')";
			getJdbcTemplate().execute(insertData);
			mapObj.put("entry", true);
		}
		
		return mapObj;
	
		}
		catch(Exception e)
		{
			logger.info("Exception in uploadNTSLFile " +e);
			mapObj.put("result", false);
			mapObj.put("count", 0);
			return mapObj;
		}
	}

@Override
public HashMap<String, Object> checkMonthlyNTSLUploaded(NFSSettlementBean beanObj)
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
			mapObj.put("msg", "File is not configured!");
			return mapObj;
		}
		String checkEntry = "SELECT COUNT(*) FROM MAIN_MONTHLY_NTSL_UPLOAD WHERE FILEID = ? AND FILEDATE= TO_DATE(?,'MM/YYYY')";
		int count = getJdbcTemplate().queryForObject(checkEntry, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
		logger.info("Count is "+count);
		
		if(count > 0)
		{
			mapObj.put("result", false);
			mapObj.put("msg", "File is already uploaded");
		}
		else
			mapObj.put("result", true);
		
	}
	catch(Exception e)
	{
		logger.info("Exception in checkMonthlyNTSL "+e);
		mapObj.put("result", false);
		mapObj.put("msg", "File is already uploaded");
	}
	return mapObj;
}

//VALIDATING DAILY INTERCHANGE	
@Override
public HashMap<String,Object> ValidateDailyInterchangeProcess(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	int rawCount = 0;
	List<String> subcategories = new ArrayList<String>();
	subcategories.add("ISSUER");
	subcategories.add("ACQUIRER");
	int uploadCount = 0;
	int file_id = 0;
	try
	{
		try
		{
			//CHECK WHETHER FILE IS UPLOADED FOR SELECTED DATE
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		}
		catch(Exception e)
		{
			result.put("result",false);
			result.put("msg", "File is not Configured!");
			return result;
						
		}
		//1. CHECK WHETHER NTSL FILES ARE UPLOADED FOR SELECTED DATE
			String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
			uploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {file_id , beanObj.getDatepicker()},Integer.class);
			System.out.println("Upload count"+uploadCount);
			//GETTING CYCLE COUNT
			String getCycleCount = "select FILE_COUNT from main_filesource where FILEID = ?";
			int cycleCount = getJdbcTemplate().queryForObject(getCycleCount, new Object[] {file_id},Integer.class);
			logger.info("Cycle count is "+cycleCount);

			if(uploadCount > 0 && uploadCount == cycleCount)
			{
				//2. NOW CHECK WHETHER FILE IS ALREADY PROCESSED			
				System.out.println("File id is "+file_id);
				String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE INTERCHANGE_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ?";
				int procCount = getJdbcTemplate().queryForObject(alreadyProc, new Object[] {file_id , beanObj.getDatepicker()},Integer.class);
				System.out.println("Already Process count "+alreadyProc);

				if(procCount == 0 )
				{
					//3. CHECK WHETHER THIS IS FIRST TIME OF PROCESSING 
					String checkFirstTime = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE < '"+beanObj.getDatepicker()+"'";
					int firstCount = getJdbcTemplate().queryForObject(checkFirstTime, new Object[] {file_id},Integer.class);
					if(firstCount >0)
					{		
						//int getFileCount = getJdbcTemplate().queryForObject("SELECT FILE_COUNT FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[]{file_id}, Integer.class);
						//4. CHECK WHETHER PREVIOUS DAY INTERCHANGE IS PROCESSED FOR BOTH CYCLES
						String prevdayProcess = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE INTERCHANGE_FLAG = 'Y' AND FILEID = ? AND FILEDATE = TO_DATE('"+beanObj.getDatepicker()+"','DD/MON/YYYY')-1";
						int checkCount = getJdbcTemplate().queryForObject(prevdayProcess, new Object[] {file_id},Integer.class);
						if(checkCount > 0 && checkCount == cycleCount)
						{
							//NOW CHECK WHETHER PREVIOUS DAY DAILY TTUM IS GENERATED
							int ttumCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE TTUM_FLAG = 'Y' AND FILEID = ? AND FILEDATE = TO_DATE(?,'DD/MM/YYYY')-1", new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
							logger.info("Previous day ttum count is "+ttumCount);
							if(ttumCount >0 && ttumCount == cycleCount)
							{
								boolean rawfileUploaded = true;
								for(String subcategory : subcategories)
								{
									if(beanObj.getFileName().equalsIgnoreCase("NTSL-NFS"))
									{
										//We need to check entry for both ISSUER and ACQUIRER raw data

										file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { "NFS","NFS",subcategory },Integer.class);
										System.out.println("File id is "+file_id);

										//check raw file for nfs is uploaded
										String checkRawData = "SELECT count(*) from MAIN_FILE_UPLOAD_DTLS WHERE FILEID = ? AND FILEDATE =?";
										rawCount = getJdbcTemplate().queryForObject(checkRawData, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
										System.out.println("rawCount is "+rawCount);
									}
									else
									{

										file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName().split("-")[1],beanObj.getCategory(),subcategory },Integer.class);
										System.out.println("File id is "+file_id);

										//check raw file for DFS AND JCB is uploaded
										String checkRawData = "SELECT count(*) from MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
										rawCount = getJdbcTemplate().queryForObject(checkRawData, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
										System.out.println("rawCount is "+rawCount);

									}
								}
								if(rawCount == 0)
								{
									result.put("result",false);
									result.put("msg", "Issuer and Acquirer Raw Files are not uploaded");
								}
								else
								{
									result.put("result", true);
								}
							}
							else
							{
								//ttum not generated for prev day
								result.put("result",false);
								result.put("msg", "Previous day Interchange TTUM is not processed");
							}
						}
						else
						{
							result.put("result",false);
							result.put("msg", "Previous day Interchange is not processed");
						}
					}
					else
					{
						result.put("result", true);
					}
				}
				else
				{
					result.put("result",false);
					result.put("msg", "File is already Processed");
				}
			}
			else
			{
				result.put("result", false);
				result.put("msg", "Upload all NTSL for selected Date");
			}
		
			
	}
	catch(Exception e)
	{
		System.out.println("Exception in ValidateDailyInterchangeProcess ");
		result.put("result", false);
		result.put("msg", "Exception Occurred");
	}
	return result;
}

//---------------------- CODING FOR SETTLEMENT VOUCHER----------------------------
/*@Override
public HashMap<String,Object> ValidateForSettVoucher(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	int rawCount = 0;
	List<String> subcategories = new ArrayList<String>();
	subcategories.add("ISSUER");
	subcategories.add("ACQUIRER");
	int uploadCount = 0;
	int file_id = 0;
	try
	{
		try
		{
			//CHECK WHETHER FILE IS UPLOADED FOR SELECTED DATE
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		}
		catch(Exception e)
		{
			result.put("result",false);
			result.put("msg", "File is not Configured!");
			return result;
						
		}
		
		System.out.println("File id is "+file_id);
		// check whether NTSL IS UPLOADED
		String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?  AND FILEDATE = ? AND CYCLE = ?";
		uploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
		System.out.println("Upload count "+uploadCount);
		
		if(uploadCount > 0)
		{
			
			// CHECK WHETHER SETTLEMENT IS PROCESSED OR NOT
			String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
			int procCount = getJdbcTemplate().queryForObject(alreadyProc, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
			System.out.println("Already Process count "+alreadyProc);
			
			if(procCount > 0)
			{
				if(beanObj.getFileName().equalsIgnoreCase("NTSL-NFS"))
				{
					int adj_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),"NFS_ADJUSTMENT","-" },Integer.class);
					String checkAdjFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ?";
					uploadCount = getJdbcTemplate().queryForObject(checkAdjFileUploaded, new Object[] {adj_file_id , beanObj.getDatepicker()},Integer.class);
					System.out.println("Already Process count "+uploadCount);

					if(uploadCount == 0)
					{
						System.out.println("Error message ");
						result.put("result", false);
						result.put("msg", "Adjustment File is not uploaded for Selected Date!");
					}
					else
					{

						//CHECK WHETHER THERE IS DIFFERENCE IN NTSL AND SETTLEMENT REPORT

						String diffQuery = "select count(*) from  NFS_SETTLEMENT_REPORT_DIFF where (NO_OF_TXNS = 'Y' OR DEBIT = 'Y' OR CREDIT = 'Y') " + 
								"AND FILEDATE = ? AND RECTIFIED = 'N' AND CYCLE = ?";
						int diffCount = getJdbcTemplate().queryForObject(diffQuery, new Object[] {beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
						logger.info("diffCount is "+diffCount);
						if(diffCount > 0 )
						{
							result.put("result", false);
							result.put("msg", "Final Settlement Amount is mismatching !");
						}
						else
						{	
							result.put("result", true);
						}
					}
				}
				else
				{
					result.put("result", true);
				}
			}
			else
			{
				result.put("result", false);
				result.put("msg", "Please process Settlement first!");
				
			}
		}
		else
		{
			result.put("result", false);
			result.put("msg", "NTSL File is not uploaded for Selected Date!");
		}
		
			
	}
	catch(Exception e)
	{
		System.out.println("Exception in ValidateDailySettProcess "+e);
		result.put("result", false);
		result.put("msg", "Exception Occurred");
	}
	return result;
}*/

@Override
public HashMap<String,Object> ValidateForSettVoucher(NFSSettlementBean beanObj)
{
	HashMap<String, Object>  output = new HashMap<String, Object>();
	
	try
	{

		//check if ttum is already processed
		String checkProcess = "select count(*) from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
		
		int processCount = getJdbcTemplate().queryForObject(checkProcess, new Object[] {beanObj.getDatepicker()}, Integer.class);
		
		if(processCount == 0)
		{
			//check if Report is processed 
			checkProcess = "select count(cycle) from("
					+ "select distinct (cycle) from nfs_settlement_report where filedate = str_to_date(?,'%Y/%m/%d')) as a";
			
			processCount = getJdbcTemplate().queryForObject(checkProcess, new Object[] {beanObj.getDatepicker()},Integer.class);
			
			if(processCount == 4)
			{
				
				//check if rectification is done
				checkProcess = "select count(rectified) from nfs_settlement_report_diff where filedate = str_to_date(?,'%Y/%m/%d') and rectified = 'N'";
				
				processCount = getJdbcTemplate().queryForObject(checkProcess, new Object[] {beanObj.getDatepicker()},Integer.class);
				
				if(processCount == 0)
				{
					output.put("result", true);
				}
				else
				{
					output.put("result", false);
					output.put("msg", "Settlements are not rectified");
				}
			}
			else
			{
				output.put("result", false);
				output.put("msg", "All Settlements are not processed");
			}
		
		}
		else
		{
			output.put("result", false);
			output.put("msg", "Settlement Voucher is already processed");
		}
		
	}
	catch(Exception e)
	{
		logger.info("Exception while validating settlement Voucher "+e);
		output.put("result", false);
		output.put("msg", "Exception while validating settlement Voucher");
	}
	return output;
}

@Override
public boolean checkSettVoucherProcess(NFSSettlementBean beanObj)
{
	try
	{
		if(!beanObj.getFileName().contains("PBGB"))
		{
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-"},Integer.class);

			String checkProcessFlag = "select count(*) from main_settlement_file_upload where sett_voucher = 'Y' and fileid = ? and filedate = str_to_date(?,'%Y/%m/%d') and cycle = ?";
			int procCount = getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
			logger.info("Already Process count "+procCount);
			if(procCount >0)
			{
				//return true;
				//DELETE RECORDS
				if(beanObj.getFileName().equalsIgnoreCase("NTSL-NFS"))
				{
					String deleteQuery= "DELETE FROM NFS_SETTLEMENT_VOUCHER WHERE FILEDATE ='"+beanObj.getDatepicker()+"' AND CYCLE = '"+beanObj.getCycle()+"'";
					getJdbcTemplate().execute(deleteQuery);
				}
				else if(beanObj.getFileName().equalsIgnoreCase("NTSL-DFS"))
				{
					String deleteQuery= "DELETE FROM DFS_SETTLEMENT_VOUCHER WHERE FILEDATE ='"+beanObj.getDatepicker()+"' AND CYCLE = '"+beanObj.getCycle()+"'";
					getJdbcTemplate().execute(deleteQuery);
				}
				else
				{
					String deleteQuery= "DELETE FROM JCB_SETTLEMENT_VOUCHER WHERE FILEDATE ='"+beanObj.getDatepicker()+"' AND CYCLE = '"+beanObj.getCycle()+"'";
					getJdbcTemplate().execute(deleteQuery);
				}
			}

			return true;
		}
		else
		{
			String deleteQuery= "DELETE FROM PBGB_SETTLEMENT_VOUCHER WHERE FILEDATE ='"+beanObj.getDatepicker()+"'";
			getJdbcTemplate().execute(deleteQuery);
			
			return true;
		}
		
	}
	catch(Exception e)
	{
		logger.info("Exception occurred in checkSettVoucherProcess "+e);
		return false;
	}
}

/*************** CODING FOR VALIDATING ADJUSTMENT TTUM ********************/
@Override
public HashMap<String,Object> ValidateForAdjTTUM(NFSSettlementBean beanObj)
{

	HashMap<String,Object> result = new HashMap<String, Object>();
	int rawCount = 0;
	List<String> subcategories = new ArrayList<String>();
	subcategories.add("ISSUER");
	subcategories.add("ACQUIRER");
	int uploadCount = 0;
	int file_id = 0;
	try
	{
		try
		{
			//CHECK WHETHER FILE IS UPLOADED FOR SELECTED DATE
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-" },Integer.class);
		}
		catch(Exception e)
		{
			result.put("result",false);
			result.put("msg", "File is not Configured!");
			return result;
						
		}
		
		//2. CHECK WHETHER ADJUSTMENT FILE IS UPLOADED
		String checkAdjFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE = ? ";
		uploadCount = getJdbcTemplate().queryForObject(checkAdjFileUploaded, new Object[] {file_id , beanObj.getDatepicker()},Integer.class);
		System.out.println("Already Process count "+uploadCount);
		

		if(uploadCount > 0)
		{
				//CHECK WHETHER SETTLEMENT IS PROCESSED FOR SELECTED DATE
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getFileName().split("-")[1]+"_SETTLEMENT","-"},Integer.class);
				logger.info("File id is "+file_id);
				//String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
				/*String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ?";
				int procCount = getJdbcTemplate().queryForObject(alreadyProc, new Object[] {file_id , beanObj.getDatepicker()},Integer.class);
				System.out.println("Settlement Process count "+alreadyProc);
				if(procCount == 4)
				{*/
					//CHECK WHETHER ADJUSTMENT TTUM IS ALREADY PROCESSED
				String checkAdjProcess = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = ? AND SUBCATEGORY = ? and adjtype = ?";
				int processCount = 0;
				if(beanObj.getAdjType().equalsIgnoreCase("PENALTY") || beanObj.getAdjType().equalsIgnoreCase("FEE"))
				{
					checkAdjProcess = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = ? AND SUBCATEGORY = ? "
							+ "AND Upper(adjtype) like '%"+beanObj.getAdjType()+"%'";
					processCount = getJdbcTemplate().queryForObject(checkAdjProcess, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()},Integer.class);
				}
				else
				{
					processCount = getJdbcTemplate().queryForObject(checkAdjProcess, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory(),beanObj.getAdjType()},Integer.class);
				}
					
				
					if(processCount >0)
					{
						result.put("result", false);
						result.put("msg", "Adjustment TTUM is already processed.\n Please download TTUM");
					}
					else
					{	
						//CHECK WHETHER IS THERE ANY DATA PRESENT IN RAWDATA FOR GENERATING TTUM   ***  ADDED ON 24 MAR 2021
						/*String checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where filedate = ? AND ADJTYPE in ('Good faith chargeback Deemed Acceptance','Arbitration Acceptance',"
								+ "'Goodfaith chargeback Acceptance','Pre-Arbitration Acceptance','Credit Adjustment','Chargeback Acceptance') ";*/
						String checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where filedate = ? AND ADJTYPE in ('"+beanObj.getAdjType()+"') ";
							
						if(beanObj.getAdjType().equalsIgnoreCase("PENALTY") || beanObj.getAdjType().equalsIgnoreCase("FEE"))
						{
							if(beanObj.getAdjType().equalsIgnoreCase("PENALTY"))
							{
							checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where filedate = ? AND customerpenalty > 0";
							}
							if(beanObj.getAdjType().equalsIgnoreCase("FEE"))
							{
								checkDataPresent = "select count(*) FROM NFS_ADJUSTMENT_RAWDATA  where filedate = ? AND ADJTYPE in('Arbitration Acceptance','NRP decision logging in favour of Issuer ')";
										
							}
						}
								//+ "and cycle = '"+beanObj.getCycle()+"C'";
						if(beanObj.getStSubCategory().equalsIgnoreCase("ISSUER"))
						{
							checkDataPresent = checkDataPresent+" AND ACQ != 'DLB'";
						}
						else
						{
							checkDataPresent = checkDataPresent+" AND ACQ = 'DLB'";
						}
						int getcount = getJdbcTemplate().queryForObject(checkDataPresent, new Object[] {beanObj.getDatepicker()},Integer.class);
						if(getcount > 0 )
						{	
							result.put("result", true);
						}
						else
							
						{
							result.put("result", false);
							result.put("msg","Data is not present in rawdata for generating TTUM");
						}
					}
				/*}
				else
				{	
					result.put("result", false);
					result.put("msg","Settlement is not processed for selected date");
				}*/
		}
		else
		{
			result.put("result", false);
			result.put("msg", "Adjustment File is not uploaded for Selected Date!");
		}
		
			
	}
	catch(Exception e)
	{
		System.out.println("Exception in ValidateDailySettProcess "+e);
		result.put("result", false);
		result.put("msg", "Exception Occurred");
	}
	return result;
}

@Override
public boolean checkAdjTTUMProcess(NFSSettlementBean beanObj)
{
	try
	{
		//int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-" },Integer.class);
		int procCount = 0;
		String checkProcessFlag = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') AND SUBCATEGORY = ? and ADJTYPE = ?";
		if(beanObj.getAdjType().equalsIgnoreCase("PENALTY") || beanObj.getAdjType().equalsIgnoreCase("FEE"))
		{
			checkProcessFlag = "SELECT COUNT(*) FROM NFS_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MON/YYYY') AND SUBCATEGORY = ? "+
						"AND upper(adjtype) like '%"+beanObj.getAdjType()+"%'";
			procCount = getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory()},Integer.class);
		}
		else
		{
			procCount = getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] {beanObj.getDatepicker(),beanObj.getStSubCategory(),beanObj.getAdjType()},Integer.class);
		}
		
		logger.info("Already Process count "+procCount);
		if(procCount >0)
		{
			return true;
		}
		else
			return false;
		
	}
	catch(Exception e)
	{
		logger.info("Exception occurred in checkSettVoucherProcess "+e);
		return false;
	}
}

/*************************** CODING FOR CO OPERATIVE BANK TTUM ********************/
//---------------------- CODING FOR SETTLEMENT VOUCHER----------------------------
@Override
public HashMap<String,Object> ValidateCooperativeBank(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	int rawCount = 0;
	List<String> subcategories = new ArrayList<String>();
	subcategories.add("ISSUER");
	subcategories.add("ACQUIRER");
	int uploadCount = 0;
	int file_id = 0;
	try
	{
		try
		{
			//CHECK WHETHER FILE IS UPLOADED FOR SELECTED DATE
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		}
		catch(Exception e)
		{
			result.put("result",false);
			result.put("msg", "File is not Configured!");
			return result;
						
		}
		
		System.out.println("File id is "+file_id);
		// check whether NTSL IS UPLOADED
		String checkFileUploaded = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ?  AND FILEDATE = ? AND CYCLE = ?";
		uploadCount = getJdbcTemplate().queryForObject(checkFileUploaded, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
		System.out.println("Upload count "+uploadCount);
		
		if(uploadCount > 0)
		{
			// CHECK WHETHER SETTLEMENT IS PROCESSED OR NOT
			String alreadyProc = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE SETTLEMENT_FLAG = 'Y' AND FILEID = ? AND FILEDATE = ? AND CYCLE = ?";
			int procCount = getJdbcTemplate().queryForObject(alreadyProc, new Object[] {file_id , beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
			System.out.println("Already Process count "+procCount);
			
			if(procCount == 0)
			{
				result.put("result", false);
				result.put("msg", "Please process Settlement first!");
				
			}
			else
			{
				result.put("result", true);
			}
		}
		else
		{
			result.put("result", false);
			result.put("msg", "NTSL File is not uploaded for Selected Date!");
		}
		
			
	}
	catch(Exception e)
	{
		System.out.println("Exception in ValidateDailySettProcess "+e);
		result.put("result", false);
		result.put("msg", "Exception Occurred while Validating");
	}
	return result;
}

@Override
public boolean checkCoopTTUMProcess(NFSSettlementBean beanObj)
{
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),"-" },Integer.class);
		
		String checkProcessFlag = "SELECT COUNT(*) FROM NFS_COOPERATIVE_BANK_TTUM WHERE CYCLE = ? AND FILEDATE = ?";
		int procCount = getJdbcTemplate().queryForObject(checkProcessFlag, new Object[] {beanObj.getCycle() , beanObj.getDatepicker()},Integer.class);
		logger.info("Already Process count "+procCount);
		if(procCount >0)
		{
			String deleteQuery= "DELETE FROM NFS_COOPERATIVE_BANK_TTUM WHERE FILEDATE ='"+beanObj.getDatepicker()+"' AND CYCLE = '"+beanObj.getCycle()+"'";
			getJdbcTemplate().execute(deleteQuery);
		}
		
		return true;
	}
	catch(Exception e)
	{
		logger.info("Exception occurred in checkSettVoucherProcess "+e);
		return false;
	}
}

/*************** CODE FOR CHECKING DIFFERENCE AMOUNT *****************/ 
@Override
public HashMap<String,Object> validateSettDifference(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	String updateAmount = null;
	try
	{
		//CHECK WHETHER THERE IS DIFFERENCE FOR SELECTED DATE
		String checkDiffTab = "select count(*) from nfs_settlement_report_diff where filedate = str_to_date(?,'%Y/%m/%d') and cycle = ? and rectified = 'N'";
		int getcount = getJdbcTemplate().queryForObject(checkDiffTab, new Object[] {beanObj.getDatepicker(),beanObj.getCycle()},Integer.class);
		if(getcount >0)
		{
				String getDiffCol = "select case when debit = 'Y' then 'debit' else 'credit' end as diff_col "
						+ "from nfs_settlement_report_diff where (no_of_txns = 'Y' OR debit = 'Y' OR credit = 'Y') and filedate = str_to_date(?,'%Y/%m/%d')"
						+ " and cycle = ?";
				String colName = getJdbcTemplate().queryForObject(getDiffCol, new Object[] {beanObj.getDatepicker(),beanObj.getCycle()}, String.class);
				logger.info("Diff Column name is "+colName);

				String getDiffAmt = "select round(t2."+colName.toLowerCase()+" - t1."+colName.toLowerCase()+",2) from nfs_settlement_report t1, ntsl_nfs_rawdata t2 where t1.filedate = t2.filedate and t1.cycle = t2.cycle "
						+ "and t1.description = t2.description and t1.filedate = str_to_date(?,'%Y/%m/%d') "+
						"and t1.cycle = ? and t1.description ='Final Settlement Amount' ";
				Double diffAmount = getJdbcTemplate().queryForObject(getDiffAmt, new Object[] {beanObj.getDatepicker(),beanObj.getCycle()}, Double.class);
				if(diffAmount > 0.99)
				{
					result.put("result", false);
					result.put("msg", "Difference amount is greater than 1");
					return result;
				}
				else if(diffAmount != Double.parseDouble(beanObj.getRectAmt()))
				{
					result.put("result", false);
					result.put("msg", "Difference amount and entered amount is different!");
					return result;
				}
				else
				{
					updateAmount = "update nfs_settlement_report set "+colName+" = "+colName+" +"+beanObj.getRectAmt()+" where filedate = '"+beanObj.getDatepicker()+"' AND CYCLE = '"+beanObj.getCycle()+"' and description ='Final Settlement Amount'";
					logger.info("UpdateAmount query is "+updateAmount);
					getJdbcTemplate().execute(updateAmount);

					String updateTable = "update nfs_settlement_report_diff set rectified = 'Y' where filedate = '"+beanObj.getDatepicker()+"' and cycle = '"+beanObj.getCycle()+"'";
					getJdbcTemplate().execute(updateTable);

					result.put("result", true);
					result.put("msg", "Amount is Rectified!");
					return result;
				}

		}
		else
		{
			result.put("result", false);
			result.put("msg", "Selected Date does not have difference!");
		}
		
		
	}
	catch(Exception e)
	{
		logger.info("Exception occurred in validateSettDifference "+e);
		result.put("result", false);
		result.put("msg", "Exception occured in Updating!");
		return result;
	}
	return result;
}

public boolean addCooperativeBank(String bankName, String accNumber)
{
	try
	{
		String insertData = "INSERT INTO main_cooperative_master (BANK_NAME,ACCOUNT_NUMB) VALUES('"+bankName+"','"+accNumber+"')";
		getJdbcTemplate().execute(insertData);
		
		return true;
	}
	catch(Exception e)
	{
		return false;
	}
}

public List<String> getNodalData(String state)
{
	List<String> data = new ArrayList<String>();
	try
	{

		String getNodalId = "select DISTINCT NODAL_SOL_ID from nodal_sol_master where state = ?";
		String nodalId = getJdbcTemplate().queryForObject(getNodalId, new Object[] {state},String.class);
		logger.info("Nodal Sol ID "+nodalId);
		data.add(nodalId);

		String getNodalPh = "select DISTINCT NODAL_PH from nodal_sol_master where state = ?";
		String nodalPh = getJdbcTemplate().queryForObject(getNodalPh, new Object[] {state},String.class);
		logger.info("Nodal Ph "+nodalPh);
		data.add(nodalPh);
	}
	catch(Exception e)
	{
		logger.info("Exception in getNodalData" +e);
	}
	return data;
}

public boolean SaveNodalDetails(AddNewSolBean beanObj)
{
	try
	{
		String InsertQuery = "INSERT INTO nodal_sol_master(SOL_ID,STATE,GSTIN,NODAL_PH,NODAL_ACCOUNT_NUMBER,INCOME_PH,INCOME_ACCOUNT_NUMBER,CREATEDBY,CREATEDDATE) "
				+ "VALUES('"+beanObj.getSolId()+"','"+beanObj.getState()+"','"+beanObj.getGstin()+"','"+beanObj.getNodalPh()+"','"+beanObj.getNodalAccNo()+"','"+beanObj.getIncomePH()+"','"+beanObj.getIncomeAccNo()+"','"+beanObj.getCreatedBy()+"',sysdate)";
		
		getJdbcTemplate().execute(InsertQuery);
	
		return true;
		
	}
	catch(Exception e)
	{
		logger.info("Exception in SaveNodalDetails "+e);
		return false;
	}
	
}
/******* VALIDATE WHETHER SETTLEMENT IS PROCESSED *********/
@Override
public HashMap<String,Object> CheckSettlementProcess(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	String checkSettProcess = "";
	String checkFlag = "";
	String tableName = "";
	try
	{
		
		if(beanObj.getFileName().contains("NFS"))
		{
			checkSettProcess = "select settlement_flag from main_settlement_file_upload where filedate = ? and cycle = ? and fileid ="
					+ "(select fileid from main_filesource where file_category = 'NFS_SETTLEMENT' AND filename = ?)";
			checkFlag = getJdbcTemplate().queryForObject(checkSettProcess, new Object[] {beanObj.getDatepicker(),beanObj.getCycle(),beanObj.getFileName()},String.class);
			if(checkFlag.equalsIgnoreCase("Y"))
			{
				result.put("result", true);
			}
			else
			{
				result.put("result", false);
				result.put("msg", "Settlement is not processed");
			}
		}
		else
		{
			 if (beanObj.getFileName().contains("PBGB"))
			 {
				 tableName = "PBGB_SETTLEMENT_REPORT";
			 }
			 else if(beanObj.getFileName().contains("DFS"))
			  {
				  tableName = "DFS_SETTLEMENT_REPORT";	
			  }
			  else if(beanObj.getFileName().contains("JCB")) 
			  {
				  tableName = "JCB_SETTLEMENT_REPORT";
			  }
			checkSettProcess = "SELECT COUNT(*) from "+tableName+" where filedate = to_date(?,'dd/mm/yyyy')";
			int settCount = getJdbcTemplate().queryForObject(checkSettProcess, new Object[] {beanObj.getDatepicker()}, Integer.class);
			
			if(settCount == 0)
			{
				result.put("result", false);
				result.put("msg", "Settlement is not processed");
			}
			else
			{
				result.put("result", true);
			}
		}
		
	}
	catch(Exception e) {
		logger.info("Exception in checking settlement process "+e);
		result.put("result", false);
		result.put("msg", "Exception while checking settlement process");
	}
	return result;
	
}

@Override
public HashMap<String,Object> ValidateOtherSettProcess(NFSSettlementBean beanObj)
{
	HashMap<String,Object> result = new HashMap<String, Object>();
	String tableName = "";
	String reportTable = "";
	
	try
	{
		if(beanObj.getFileName().contains("PBGB"))
		{
			tableName = "NFS_NFS_ACQ_RAWDATA";
			reportTable = "PBGB_SETTLEMENT_REPORT";
		}
		else if(beanObj.getFileName().contains("DFS"))
		{
			tableName = "nfs_dfs_acq_rawdata";
			reportTable = "DFS_SETTLEMENT_REPORT";
		}
		else
		{
			tableName = "nfs_jcb_acq_rawdata";
			reportTable = "JCB_SETTLEMENT_REPORT";
		}
		
		//1. raw data uploaded
		String rawQuery = "SELECT COUNT(*) FROM "+tableName+" where filedate = to_date(?,'dd/mm/yyyy')";
		int rawCount = getJdbcTemplate().queryForObject(rawQuery, new Object[] {beanObj.getDatepicker()}, Integer.class);
		
		if(rawCount == 0)
		{
			result.put("result", false);
			result.put("msg", "Raw Files are not uploaded");
		}
		else
		{
			String alreadyProcQuery = "SELECT COUNT(*) FROM "+reportTable+" where filedate = to_date(?,'dd/mm/yyyy')";
			int procCount = getJdbcTemplate().queryForObject(alreadyProcQuery, new Object[]{beanObj.getDatepicker()}, Integer.class);
			
			if(procCount > 0)
			{
				result.put("result", false);
				result.put("msg", "Settlement is already processed");
			}
			else
			{
				result.put("result", true);
			}
			
		}
		
		
		
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		result.put("result", false);
		result.put("msg", "Exception occurred while validating");
	}
	return result;
}

}
