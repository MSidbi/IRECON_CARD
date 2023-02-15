package com.recon.service.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import java.util.HashMap;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.web.multipart.MultipartFile;

import com.recon.control.AdjustmentFileController;
import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.util.ReadDFSRawFile;
import com.recon.util.ReadNFSAdjustmentFile;

public class AdjustmentFileServiceImpl extends JdbcDaoSupport implements AdjustmentFileService{

	private static final Logger logger = Logger.getLogger(AdjustmentFileServiceImpl.class);

@Override
public HashMap<String, Object> validateAdjustmentFileUpload(NFSSettlementBean beanObj)
{
	HashMap<String, Object> validate = new HashMap<String, Object>();
	try
	{
		//1. VALIDATE WHETHER FILE IS ALREADY UPLOADED
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		logger.info("File id is "+file_id);
		
		
		int filecount = getJdbcTemplate().queryForObject("select count(1) from main_settlement_file_upload where fileid = ? and filedate = str_to_date(?,'%Y/%m/%d')", new Object[] {file_id,beanObj.getDatepicker()},
						Integer.class);
		logger.info("Filecount is"+filecount);
		if(filecount > 0)
		{
			validate.put("result", false);
			validate.put("msg", "File is already uploaded!!!");
		}
		else
		{
			validate.put("result",true);
		}
	}
	catch(Exception e)
	{
		logger.info("Exception is "+e);
		validate.put("result", false);
		validate.put("msg", "Exception Occured!!");
	}
	return validate;
}
@Override
public HashMap<String, Object> uploadAdjustmentFile(NFSSettlementBean beanObj,MultipartFile file)
{
	HashMap<String, Object> mapObj = new HashMap<String, Object>();
	try
	{
		ReadNFSAdjustmentFile nfsAdjRawData = new ReadNFSAdjustmentFile(); 
		mapObj =nfsAdjRawData.fileupload(beanObj,file, getConnection());
		System.out.println("result is "+mapObj);
		boolean result = (boolean)mapObj.get("result");
		int count = (Integer)mapObj.get("count");
		if(result)
		{
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			System.out.println("File id is "+file_id);
			String insertData = "insert into main_settlement_file_upload(fileid, filedate, uploadby, uploaddate, category, upload_flag, file_subcategory,cycle,settlement_flag,interchange_flag,file_count) " + 
					"VALUES('"+file_id+"',str_TO_DATE('"+beanObj.getDatepicker()+"','%Y/%m/%d'),'"+beanObj.getCreatedBy()+"',sysdate(),'"+beanObj.getCategory()+"','Y','-',"
							+ "'1','N','N','1')";
			getJdbcTemplate().execute(insertData);
			mapObj.put("result", true);
		}
		else
		{
			mapObj.put("result", false);
			mapObj.put("count", count);			
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

public Boolean AdjustmentRollback(NFSSettlementBean beanObj)
{
	try
	{
			String query = "delete from nfs_adjustment_rawdata where filedate = str_to_date('"+
						beanObj.getDatepicker()+"','%Y/%m/%d')";
			
			logger.info("query is :"+query);
			getJdbcTemplate().execute(query);
			
			query = "delete from main_settlement_file_upload where filedate = str_to_date('"+
						beanObj.getDatepicker()+"','%Y/%m/%d') "
						+" and fileid = (select fileid from main_filesource where filename = 'NTSL-NFS' "
						+ "and file_category = 'NFS_ADJUSTMENT')";
			
			logger.info("query is :"+query);
			getJdbcTemplate().execute(query);
		
	}
	catch(Exception e)
	{
		logger.info("Exception in AdjustmentRollback "+e);
		return false;
		
	}
	return true;
}	

}
