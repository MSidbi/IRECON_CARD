package com.recon.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.ReconStatusDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ReconStatusBean;

@Component 
public class ReconStatusDaoImpl extends JdbcDaoSupport implements ReconStatusDao {


@Override
public List<String> getAllCategories()
{
	
	String GET_CATEGORIES = "SELECT DISTINCT FILE_CATEGORY FROM MAIN_FILESOURCE ORDER BY FILE_CATEGORY";
	List<String>  categ = new ArrayList<>();
	try
	{
		categ = getJdbcTemplate().query(GET_CATEGORIES, new Object[]{}, new RowMapper<String>(){

			public String mapRow(ResultSet rs,int rowcount)throws SQLException
			{
				return rs.getString("FILE_CATEGORY");
			}
		});
		return categ;
	}
	catch(Exception e)
	{
		return categ;
	}
	
}


@Override
public List<CompareSetupBean> getlastUploadDetails(ReconStatusBean reconbean) 
{

	ArrayList<CompareSetupBean> setupBeans =null;
	try{
	
		String query= "SELECT filesrc.fileName as stFileName,to_char(filedate,'dd/mm/yy') as filedate,filter_Flag ,	knockoff_Flag , comapre_Flag , manualcompare_Flag , upload_Flag ,manupload_flag,"
				+ " updlodby as createdBy,category,filesrc.file_subcategory as stSubCategory,to_char(uploaddate,'dd/mm/yy') as entry_date "
				+ "	FROM MAIN_FILE_UPLOAD_DTLS  uplddtls "
				+ " INNER JOIN  MAIN_FILESOURCE filesrc ON  filesrc.fileId = uplddtls.fileid WHERE uplddtls.CATEGORY = '"+reconbean.getStCategory()+"' " +
				"AND uplddtls.FILE_SUBCATEGORY = '"+reconbean.getStSubCategory()+"'" +
				"AND TO_DATE(TO_CHAR(FILEDATE,'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN TO_DATE((select max(filedate)-8 from MAIN_FILE_UPLOAD_DTLS where CATEGORY = 'ONUS' AND FILE_SUBCATEGORY = '-' ) ,'DD/MM/rrrr')"
				+ "  AND TO_DATE((select max(filedate) from MAIN_FILE_UPLOAD_DTLS  where CATEGORY = 'ONUS' AND FILE_SUBCATEGORY = '-') ,'DD/MM/rrrr') "+
					" ORDER BY TO_DATE(FILEDATE,'DD/MM/YYYY') desc";
		
		/*String query= "SELECT filesrc.fileName as stFileName,to_char(filedate,'dd/mm/yy') as filedate,filter_Flag ,	knockoff_Flag , comapre_Flag , manualcompare_Flag , upload_Flag ,manupload_flag,"
				+ " updlodby as createdBy,category,filesrc.file_subcategory as stSubCategory,to_char(uploaddate,'dd/mm/yy') as entry_date "
				+ "	FROM MAIN_FILE_UPLOAD_DTLS  uplddtls "
				+ " INNER JOIN  MAIN_FILESOURCE filesrc ON  filesrc.fileId = uplddtls.fileid ORDER BY TO_DATE(FILEDATE,'DD/MM/YYYY'),CATEGORY,stSubCategory";*/
		
		
		System.out.println(query);
		
		setupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class)) ;
	
		
	}catch(Exception ex) {
		
		ex.printStackTrace();
		
	}
	
	
	
	return setupBeans;
}


public List<CompareSetupBean> getReconStatusReport(ReconStatusBean reconbean) 
{

	ArrayList<CompareSetupBean> setupBeans =null;
	try{
	
		String query= "SELECT filesrc.fileName as stFileName,to_char(filedate,'dd/mm/yy') as filedate,filter_Flag ,	knockoff_Flag , comapre_Flag , manualcompare_Flag , upload_Flag ,manupload_flag,"
				+ " updlodby as createdBy,category,filesrc.file_subcategory as stSubCategory,to_char(uploaddate,'dd/mm/yy') as entry_date "
				+ "	FROM MAIN_FILE_UPLOAD_DTLS  uplddtls "
				+ " INNER JOIN  MAIN_FILESOURCE filesrc ON  filesrc.fileId = uplddtls.fileid WHERE uplddtls.CATEGORY = '"+reconbean.getStCategory()+"' " +
				"AND uplddtls.FILE_SUBCATEGORY = '"+reconbean.getStSubCategory()+"'" +
				" ORDER BY TO_DATE(FILEDATE,'DD/MM/YYYY')";	
		
		System.out.println(query);
		
		setupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class)) ;
	
		
	}catch(Exception ex) {
		
		ex.printStackTrace();
		
	}
	
	
	
	return setupBeans;
}
	
}
