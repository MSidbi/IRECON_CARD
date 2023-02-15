package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.ADD_COMPARE_DETAILS;
import static com.recon.util.GeneralUtil.GET_FILEID;
import static com.recon.util.GeneralUtil.GET_ID;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.recon.dao.ConfigurationDao;
import com.recon.model.ConfigurationBean;


@Component
public class ConfigurationDaoImpl extends JdbcDaoSupport implements ConfigurationDao{

	/**
	 * Strng constants
	 */
	public final String FILE_ID = "file_id";
	public final String CATEGORY = "category";
	public final String FILE_NAME = "file_name";
	public final String ENTRY_BY = "Entry_By";
	public final String ID = "i_id";
	public final String TABLE_NAME = "table_name";
	private static final String O_ERROR_CODE="o_error_code";
	private static final String O_ERROR_MESSAGE="o_error_message";
    private PlatformTransactionManager transactionManager;


	private int id;
	
	
	
	   @SuppressWarnings("resource")
	public void setTransactionManager() {
	    
		   try{
		  
			  
		   ApplicationContext context= new ClassPathXmlApplicationContext();
		   context = new ClassPathXmlApplicationContext("/resources/bean.xml");
		 
		   System.out.println("in settransactionManager");
		   transactionManager = (PlatformTransactionManager) context.getBean("transactionManager"); 
		   System.out.println(" settransactionManager completed");
		   ((ClassPathXmlApplicationContext) context).close();
		   }catch (Exception ex) {
			   
			   ex.printStackTrace();
		   }
		   
		   
	   }

	
	
	/**
	 * @param dataSource
	 * the dataSource to set
	 *//*
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}*/

	
	@Override
	public int getFileId(ConfigurationBean configBean)throws Exception
	{
		try {
			int i =  getJdbcTemplate().queryForObject(GET_FILEID, new Object[] {  },Integer.class);			
			return i;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	@Override
	public void addConfigParams(ConfigurationBean configBean)throws Exception
	{
		//GET ID FROM MASTER TABLE AND THEN INCREMENT IT
		id =  (getJdbcTemplate().queryForObject(GET_ID, new Object[] {  },Integer.class))+1;		
		
		Map<String, Object> inParams = new HashMap<String, Object>();
		
		inParams.put(FILE_ID , configBean.getInFileId());
		inParams.put(FILE_NAME, configBean.getStFileName());
		inParams.put(CATEGORY, configBean.getStCategory());
		inParams.put(ID, id);
		inParams.put(TABLE_NAME, (configBean.getStFileName()+"_DATA"));
		inParams.put(ENTRY_BY, configBean.getStEntry_By());
		
		addCompareConfigParams addcompareparams = new addCompareConfigParams(getJdbcTemplate());
		Map<String, Object> outParams = addcompareparams.execute(inParams);

		if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
			throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
		}
		
		//ADDING VALUES IN MAIN_COMPARE_DETAILS TABLE
		List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
		comp_dtl_list = configBean.getComp_dtl_list();
		
		insertBatch(comp_dtl_list,configBean);
	}
	
	//
	public void insertBatch(final List<ConfigurationBean> comp_dtl_list,final ConfigurationBean configurationBean){
			
		
	
			/**Passing Sql query. */
			getJdbcTemplate().batchUpdate(ADD_COMPARE_DETAILS, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int j) throws SQLException {
					
					ConfigurationBean configbean = comp_dtl_list.get(j);
			
					ps.setInt(1, id );
					ps.setInt(2, configurationBean.getInFileId());
					ps.setString(3, configbean.getStHeader());
					ps.setString(4, configbean.getStSearch_Pattern());
					ps.setString(5, configbean.getStPadding());
					//ps.setInt(6, configbean.getInChar_Position());
					ps.setInt(6, configbean.getInStart_Char_Position());
					ps.setInt(7, configbean.getInEnd_char_position());
					ps.setString(8, configurationBean.getStEntry_By());
					ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
						
				}

				@Override
				public int getBatchSize() {
					return comp_dtl_list.size();
				}
			});

		
	}
	
	

	
	
	
	private class addCompareConfigParams extends StoredProcedure{
		private static final String add_config_params = "ADD_COMPARE_DATA";
		
		public addCompareConfigParams(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate , add_config_params);
			setFunction(false);
			declareParameter(new SqlParameter(FILE_ID , Types.INTEGER));
			declareParameter(new SqlParameter(FILE_NAME , Types.VARCHAR));
			declareParameter(new SqlParameter(CATEGORY , Types.VARCHAR));
			declareParameter(new SqlParameter(ID , Types.INTEGER));
			declareParameter(new SqlParameter(TABLE_NAME , Types.VARCHAR));
			declareParameter(new SqlParameter(ENTRY_BY , Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
			
		}
	}




	@Override
	public boolean addFileSource(ConfigurationBean configBean) throws Exception {
		
		boolean result= false;
		String sql="INSERT into MAIN_FILESOURCE (FILEID,FILENAME,TABLENAME,ACTIVEFLAG,DATASEPARATOR) values(?,?,?,?,?)";
		
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		
		try {
			
			System.out.println(sql+configBean.getInFileId()+configBean.getStFileName()+configBean.getStFileName()+configBean.getActiveFlag()+configBean.getDataSeparator());
			int value = getJdbcTemplate().update(sql,new Object[]{configBean.getInFileId(),configBean.getStFileName(),configBean.getStFileName(),configBean.getActiveFlag(),configBean.getDataSeparator()});
			
			if(value>0){
				
				//  Creating a user table.
				
				String query="create table "+configBean.getStFileName()+" (";
				String parameter="";
				String params[] = configBean.getStHeader().split(",");
				
				for(int i=0;i<params.length;i++){
					/*if(i!=params.length-1) {*/
					
						parameter=parameter+params[i].toUpperCase()+" varchar2(500),";
					
				/*	}else{*/
						
					//	parameter=parameter+params[i]+" varchar2(500)";
					/*}*/
				}
				
			
				parameter = parameter + " CreatedDate date Default sysdate,";
				parameter = parameter + " CreatedBy varchar2(500)";
				query=query+parameter+")";
				
				System.out.println(query);
				
				getJdbcTemplate().execute(query);
				
				// Data Insert into  MAIN_FILEHEADERS
				
				String hdrquery = "INSERT into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader) values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1),?,?)";
				value = getJdbcTemplate().update(hdrquery, new Object[]{configBean.getInFileId(),configBean.getStHeader()});
				if(value>0){
					System.out.println("Headers data inserted");
					result= true;
				} else {
					System.out.println("Headers data not inserted");
					result = false;
				}
				
				/*String processTrnQuery = "INSERT into Main_Process_Status (TRNID , FILEID, FILTERSTATUS, COMPARESTATUS, RECONSTATUS ,FILECATEGORY) values(((SELECT MAX(TRNID) FROM  Main_Process_Status)+1),?,'N','N','N','N')";
				value = getJdbcTemplate().update(processTrnQuery, new Object[]{configBean.getInFileId()});
				if(value>0){
					System.out.println("Inserted into Main_Process_Status");
					result= true;
				} else {
					System.out.println("Main_Process_Status data not inserted");
					result = false;
				}*/
				
				// Data Insert into  MAIN_FTPDetails
				
				String ftpquery = "INSERT into MAIN_FTPDetails (FTPDETAILID , FILEID,FILELOCATION,FILEPATH,FTPUSERNAME,FTPPASSWORD,FTPPORT) "
						+ " values(((SELECT MAX(FTPDETAILID) FROM  MAIN_FTPDetails)+1),?,?,?,?,?,?)";
				value = getJdbcTemplate().update(ftpquery, new Object[]{configBean.getInFileId(),configBean.getFileLocation(),configBean.getFilePath(),configBean.getFtpUser(),configBean.getFtpPwd(),configBean.getFtpPort()});
				if(value>0){
					System.out.println("FTP data inserted");
					transactionManager.commit(status);

					result= true;
				} else {
					System.out.println("FTP  data not inserted");
					transactionManager.rollback(status);
					result = false;
				}
				
				
			}else {
				
				result=false;
			}
			
			
		}catch(Exception ex) 
		{
			System.out.println("Error Occurred.....");
			ex.printStackTrace();
			transactionManager.rollback(status);
			result=false;
			
		}
		
		
		return result;
	}

	@Override
	public boolean chkTblExistOrNot(ConfigurationBean configBean)
			throws Exception {
	
		try {
			
			//check Filename exist or not
			
			String filesql="SELECT count(*) FROM Main_FileSource WHERE upper (Filename) ='"+configBean.getStFileName().toUpperCase()+"' ";
			
			int filerowNum=0;
			
			System.out.println(filesql);
			
			
			filerowNum =  getJdbcTemplate().queryForObject(filesql, Integer.class);
			System.out.println(filesql);
			
			
			
			
			
			//check table exist or not
			String sql="SELECT count (*) FROM tab WHERE tname  = '"+configBean.getStFileName().toUpperCase()+"'";
			int rowNum=0;
			
			
			rowNum =  getJdbcTemplate().queryForObject(sql, Integer.class);			
			System.out.println(sql);
			
			if(rowNum > 0 || filerowNum >0){
				
				return false;
			}else{
				
				return true;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println(e.toString());
			return false;
		}
	}

}
