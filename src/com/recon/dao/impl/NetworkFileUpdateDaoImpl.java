package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.NetworkFileUpdateDao;
import com.recon.model.NetworkFileUpdateBean;

@Component
public class NetworkFileUpdateDaoImpl extends JdbcDaoSupport implements NetworkFileUpdateDao{


@Override
public boolean checkUploadFlag(NetworkFileUpdateBean networkBean)
{
	try
	{
		String CHECK_ENTRY="";
		int count=0;
		//int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { networkBean.getStFileName(),networkBean.getCategory(),networkBean.getStsubCategory() },Integer.class);
		if(networkBean.getStsubCategory().equals("-"))
		{
			CHECK_ENTRY= "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = ?  AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = ?";
			count = getJdbcTemplate().queryForObject(CHECK_ENTRY, new Object[] {networkBean.getCategory(),networkBean.getDatepicker()}, Integer.class);
		System.out.println(count);
		}
		else{
		CHECK_ENTRY= "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ? AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = ?";
		count = getJdbcTemplate().queryForObject(CHECK_ENTRY, new Object[] {networkBean.getCategory(),networkBean.getStsubCategory(),networkBean.getStFileName()
				,networkBean.getDatepicker()}, Integer.class);
		}
		 
		
		if(count == 0 )
		{
			return true;
		}
		else
			return false;
	}
	catch(Exception e)
	{
		return false;
		
	}
}

@Override
public void EntryForFile(NetworkFileUpdateBean networkBean)throws Exception
{
	
	//int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { networkBean.getStFileName(),networkBean.getCategory(),networkBean.getStsubCategory() },Integer.class);
	String INSERT_QUERY =null;
	if(networkBean.getStsubCategory().equals("-"))
	{
		for(int i=1;i<=2;i++)
		{
			if(i==1)
			{
		
		INSERT_QUERY = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILTER_FLAG,KNOCKOFF_FLAG,COMAPRE_FLAG,MANUALCOMPARE_FLAG,UPLOAD_FLAG,MANUPLOAD_FLAG,FILE_SUBCATEGORY,FILE_COUNT) "+
				" VALUES ('43',TO_DATE('"+networkBean.getDatepicker()+"','DD/MM/YYYY'),'"+networkBean.getStEntryBy()
				+"',SYSDATE,'"+networkBean.getCategory()+"','N','N','Y','Y','Y','Y','"+networkBean.getStsubCategory()+"','1')";
			
		System.out.println("INSERT QUERY IS "+INSERT_QUERY);
		getJdbcTemplate().execute(INSERT_QUERY);
			}
			else if(i==2)
			{
				INSERT_QUERY = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILTER_FLAG,KNOCKOFF_FLAG,COMAPRE_FLAG,MANUALCOMPARE_FLAG,UPLOAD_FLAG,MANUPLOAD_FLAG,FILE_SUBCATEGORY,FILE_COUNT) "+
						" VALUES ('42',TO_DATE('"+networkBean.getDatepicker()+"','DD/MM/YYYY'),'"+networkBean.getStEntryBy()
						+"',SYSDATE,'"+networkBean.getCategory()+"','Y','N','Y','Y','Y','Y','"+networkBean.getStsubCategory()+"','1')";
			
				System.out.println("INSERT QUERY IS "+INSERT_QUERY);
				getJdbcTemplate().execute(INSERT_QUERY);
			}
	}}
	else if(networkBean.getCategory().equals("MASTERCARD")){
		
		INSERT_QUERY = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILTER_FLAG,KNOCKOFF_FLAG,COMAPRE_FLAG,MANUALCOMPARE_FLAG,UPLOAD_FLAG,MANUPLOAD_FLAG,FILE_SUBCATEGORY,FILE_COUNT) "+
				" VALUES ('35',TO_DATE('"+networkBean.getDatepicker()+"','DD/MM/YYYY'),'"+networkBean.getStEntryBy()
				+"',SYSDATE,'"+networkBean.getCategory()+"','N','N','N','N','Y','N','"+networkBean.getStsubCategory()+"','6')";

System.out.println("INSERT QUERY IS "+INSERT_QUERY);
getJdbcTemplate().execute(INSERT_QUERY);
	}
	else
	{
	INSERT_QUERY = "INSERT INTO MAIN_FILE_UPLOAD_DTLS (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILTER_FLAG,KNOCKOFF_FLAG,COMAPRE_FLAG,MANUALCOMPARE_FLAG,UPLOAD_FLAG,MANUPLOAD_FLAG,FILE_SUBCATEGORY,FILE_COUNT) "+
					" VALUES ('"+networkBean.getStFileName()+"',TO_DATE('"+networkBean.getDatepicker()+"','DD/MM/YYYY'),'"+networkBean.getStEntryBy()
					+"',SYSDATE,'"+networkBean.getCategory()+"','N','N','N','N','Y','N','"+networkBean.getStsubCategory()+"','1')";
	
	System.out.println("INSERT QUERY IS "+INSERT_QUERY);
	getJdbcTemplate().execute(INSERT_QUERY);
	}
	
			
	
}
@Override
public boolean checkProcessFlag(NetworkFileUpdateBean networkBean)
{
	
	String CHECK_FILE = "SELECT COUNT(*) FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ? AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = ?"+
					" AND FILTER_FLAG = 'N' AND KNOCKOFF_FLAG = 'N' AND COMAPRE_FLAG = 'N' AND MANUALCOMPARE_FLAG = 'N'";
	
	int count = getJdbcTemplate().queryForObject(CHECK_FILE,new Object[] {networkBean.getCategory(),networkBean.getStsubCategory(),networkBean.getStFileName(),networkBean.getDatepicker()},Integer.class);
	
	if(count == 0)
	{
		//THAT MEANS FILE IS EITHER ALREADY PROCESSED OR THERE IS NO ENTRY FOR IT
		return false;
	}
	else
	{
		return true;
	}
	
}

@Override
public void DeleteData(NetworkFileUpdateBean networkBean)
{
	/*String DELETE_ENTRY = "DELETE FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = ? AND FILE_SUBCATEGORY = ? AND FILEID = ? AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = ? "+
			 " AND FILTER_FLAG = 'N' AND KNOCKOFF_FLAG = 'N' AND COMAPRE_FLAG = 'N' AND MANUALCOMPARE_FLAG = 'N'";*/
	
	String DELETE_ENTRY = "DELETE FROM MAIN_FILE_UPLOAD_DTLS WHERE CATEGORY = '"+networkBean.getCategory()+"' AND FILE_SUBCATEGORY = '"+networkBean.getStsubCategory()
						+"' AND FILEID = '"+networkBean.getStFileName()+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+networkBean.getDatepicker()+"' "+
			 " AND FILTER_FLAG = 'N' AND KNOCKOFF_FLAG = 'N' AND COMAPRE_FLAG = 'N' AND MANUALCOMPARE_FLAG = 'N'";
	
	System.out.println("DELETE_ENTRY "+DELETE_ENTRY);
	
	int deleted = getJdbcTemplate().update(DELETE_ENTRY);
	
	//int deleted = getJdbcTemplate().update(DELETE_ENTRY,new Object[] {networkBean.getCategory(),networkBean.getStsubCategory(),networkBean.getStFileName(),networkBean.getDatepicker()},Integer.class);
	
	if(deleted == 1)
	{
		String GET_RAWTABLE = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILEID = ?";
		String sttablename = getJdbcTemplate().queryForObject(GET_RAWTABLE,new Object[] {networkBean.getStFileName()},String.class);
		String DELETE_DATA = "DELETE FROM "+sttablename+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = ?";
		getJdbcTemplate().update(DELETE_DATA,new Object[] {networkBean.getDatepicker()}); 
		
	}
}
}
