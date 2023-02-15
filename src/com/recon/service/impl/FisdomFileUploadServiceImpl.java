package com.recon.service.impl;

import java.util.HashMap;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.Act4Bean;
import com.recon.model.FisdomFileUploadBean;
import com.recon.service.FisdomFileUploadService;
import com.recon.util.ReadFisdomCBSFile;
import com.recon.util.ReadFisdomGLFile;
import com.recon.util.ReadFisdomSwitchFile;
import com.recon.util.ReadNFSGLFile;
import com.recon.util.ReadRUPAYGLFile;

public class FisdomFileUploadServiceImpl extends JdbcDaoSupport implements FisdomFileUploadService  {

	
	public HashMap<String, Object> validateFileAlreadyUploaded(FisdomFileUploadBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		String VALIDATE_QUERY= "SELECT COUNT(*) FROM ";
		
		try
		{
			String GET_tableName = "SELECT tablename from main_filesource where filename = ?";
			String tableName = getJdbcTemplate().queryForObject(GET_tableName, new Object[] {"FISDOM_"+beanObj.getFileName()},String.class);
			
			VALIDATE_QUERY = VALIDATE_QUERY+tableName+" WHERE FILEDATE = ?";
			
			int getCount = getJdbcTemplate().queryForObject(VALIDATE_QUERY, new Object[] {beanObj.getFileDate()}, Integer.class);
			
			if(getCount > 0)
			{
				output.put("result", false);
				output.put("msg", "File is Already Uploaded");
			}
			else
				output.put("result", true);
			
		}
		catch(Exception e)
		{
			logger.info("Exception occured "+e);
			output.put("result", false);
			output.put("msg", "Exception Occured");
		}
		return output;
	}
	
	public boolean checkTrackingTable(FisdomFileUploadBean beanObj)
	{
		String CHECK_ENTRY = "SELECT count(*) from main_file_upload_dtls where filedate = ? and category = 'FISDOM' "
				+ " and fileid = (select fileid from main_filesource where filename = 'FISDOM_"+beanObj.getFileName()+"')";
		try
		{
			int entryCount = getJdbcTemplate().queryForObject(CHECK_ENTRY, new Object[] {beanObj.getFileDate()}, Integer.class);
			
			if(entryCount > 0)
			{
				return false;
			}
			else 
				return true;
		}
		catch(Exception e)
		{
			logger.info("Exception while checking tracking table "+e);
			return false;
		}
	}
	
	public HashMap<String , Object> readFiles(FisdomFileUploadBean beanObj, MultipartFile file)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			if(beanObj.getFileName().equalsIgnoreCase("SWITCH"))
			{
				ReadFisdomSwitchFile readFile = new ReadFisdomSwitchFile();
				output = readFile.uploadFisdomSwitchData(beanObj, getConnection(), file);
			}
			else if(beanObj.getFileName().equalsIgnoreCase("CBS"))
			{
				ReadFisdomCBSFile readFile =  new ReadFisdomCBSFile();
				output = readFile.uploadFisdomCBSData(beanObj, getConnection(), file);
			}
			else if(beanObj.getFileName().equalsIgnoreCase("GL"))
			{
				ReadFisdomGLFile readFile = new ReadFisdomGLFile();
				output = readFile.uploadFisdomCBSData(beanObj, getConnection(), file);
			}
			
			//INSERT IN UPLOAD DTLS TABLE
			if(output != null && (Boolean) output.get("result"))
			{
				String INSERT_QUERY = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,"
						+ "Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "+
						" VALUES((SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'FISDOM_"+beanObj.getFileName()+"'),"
								+ " TO_DATE('"+beanObj.getFileDate()+"','DD/MM/YYYY'), '"+beanObj.getCreatedBy()+"',"
								+ "sysdate,'FISDOM','-','Y','N','N','N','N','Y')";
				
				getJdbcTemplate().execute(INSERT_QUERY);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			output.put("result", false);
			output.put("msg","Exception Occured");
			
		}
		return output;
	}
	
	public boolean validateGLAlreadyUpload(Act4Bean beanObj)
	{
		String tableName = "";
		if(beanObj.getCategory().equalsIgnoreCase("NFS"))
		{
			tableName = "gl_nfs_rawdata";
		}
		else
		{
			tableName = "gl_rupay_rawdata";
		}
		String CHECK_ENTRY = "select count(*) from "+tableName+" where filedate = to_date(?,'DD/MM/YYYY') AND gl_account = ?";

				try
		{
			int entryCount = getJdbcTemplate().queryForObject(CHECK_ENTRY, new Object[] {beanObj.getDatepicker(),beanObj.getGlAccount().get(0).substring(0, beanObj.getGlAccount().get(0).indexOf("(")).trim()}, Integer.class);
			
			if(entryCount > 0)
			{
				return true;
			}
			else 
				return false;
		}
		catch(Exception e)
		{
			logger.info("Exception while checking tracking table "+e);
			return false;
		}
	}
	
	public HashMap<String , Object> readGLFiles(Act4Bean beanObj, MultipartFile file)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			if(beanObj.getCategory().equalsIgnoreCase("NFS"))
			{
				ReadNFSGLFile readFile = new ReadNFSGLFile();
				output = readFile.uploadNFSGLData(beanObj, getConnection(), file);
			}
			else if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				ReadRUPAYGLFile readFile = new ReadRUPAYGLFile();
				output = readFile.uploadRUPAYGLData(beanObj, getConnection(), file);
				
				if(output != null && (Boolean)output.get("result"))
				{
					String INSERT_QUERY = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,"
							+ "Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "+
							" VALUES((SELECT FILEID FROM MAIN_FILESOURCE WHERE FILENAME = 'GL' AND FILE_CATEGORY = 'RUPAY' AND FILE_SUBCATEGORY = 'INTERNATIONAL'),"
									+ " TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY'), '"+beanObj.getCreatedby()+"',"
									+ "sysdate,'FISDOM','-','Y','N','N','N','N','Y')";
					
					getJdbcTemplate().execute(INSERT_QUERY);
				}
			}
				
			
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			output.put("result", false);
			output.put("msg","Exception Occured");
			
		}
		return output;
	}
}
