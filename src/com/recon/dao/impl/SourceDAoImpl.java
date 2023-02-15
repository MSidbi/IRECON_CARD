package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.ADD_COMPARE_DETAILS;
import static com.recon.util.GeneralUtil.GET_FILEID;
import static com.recon.util.GeneralUtil.GET_ID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.recon.dao.ISourceDao;
import com.recon.dao.impl.ReconProcessDaoImpl.IssClassificaton;
import com.recon.loyality.SettlementTypeBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.ManualFileBean;
import com.recon.model.Settle_final_report_condn_bean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.util.GenerateSettleTTUMBean;
import com.recon.util.OracleConn;
import com.recon.util.Utility;
import com.recon.util.demo;

@Component
public class SourceDAoImpl extends JdbcDaoSupport implements ISourceDao {
	
	public final String FILE_ID = "file_id";
	public final String CATEGORY = "category";
	public final String FILE_NAME = "file_name";
	public final String ENTRY_BY = "Entry_By";
	public final String ID = "i_id";
	public final String TABLE_NAME = "table_name";
	private static final String O_ERROR_CODE="o_error_code";
	private static final String O_ERROR_MESSAGE="o_error_message";
    private PlatformTransactionManager transactionManager;
    
    String MAIN_KNOCKOFF_CRITERIA= "Insert into MAIN_KNOCKOFF_CRITERIA (REVERSAL_ID,FILE_ID,HEADER,ENTRY_BY,ENTRY_DATE,PADDING,START_CHARPOSITION,CHAR_SIZE,HEADER_VALUE,CONDITION)"
			+ "values (?,?,?,?,sysdate,?,?,?,?,?)";
	
	String MAIN_REVERSAL_DETAIL= "Insert into MAIN_REVERSAL_DETAIL (REVERSAL_ID,FILE_ID,CATEGORY,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)"
			+ "values (?,?,?,?,?,?,sysdate)";
	
	String MAIN_REVERSAL_PARAMETERS="Insert into MAIN_REVERSAL_PARAMETERS (REVERSAL_ID,FILE_ID,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)"
			+ "values (?,?,?,?,?,sysdate)";


	private int id;
	
	@SuppressWarnings("resource")
	public void setTransactionManager() throws Exception {
		logger.info("***** SourceDAoImpl.setTransactionManager Start ****");
		   try{
		  
			  
		   ApplicationContext context= new ClassPathXmlApplicationContext();
		   context = new ClassPathXmlApplicationContext("/resources/bean.xml");
		 
		   logger.info("in settransactionManager");
		   transactionManager = (PlatformTransactionManager) context.getBean("transactionManager"); 
		   logger.info(" settransactionManager completed");
		   logger.info("***** SourceDAoImpl.setTransactionManager End ****");
		   ((ClassPathXmlApplicationContext) context).close();
		   }catch (Exception ex) {
			   demo.logSQLException(ex, "SourceDAoImpl.setTransactionManager");
				 logger.error(" error in SourceDAoImpl.setTransactionManager", new Exception("SourceDAoImpl.setTransactionManager",ex));
				 throw ex;
		   }
		   
		   
	   }


	@Override
	public List<ConfigurationBean> getFileDetails() throws Exception {

		 logger.info("***** SourceDAoImpl.getFileDetails Start ****");
		List<ConfigurationBean> filelist = null;
		
		try {
			
			String query="SELECT filesrc.Fileid as inFileId ,filesrc.file_category as stCategory,filesrc.file_subcategory as stSubCategory, filesrc.FileName as stFileName ,filesrc.dataseparator ,filesrc.rddatafrm ,filesrc.charpatt,"
					+ " filesrc.Activeflag as activeFlag  "
					+ " FROM Main_FILESOURCE filesrc ";
									/*+ " WHERE filesrc.ActiveFlag='A' ";*/
				
			logger.info("query" +query);
			
		/*	rs = oracleConn.executeQuery(query);*/
			
			/*while(rs.next()){
				
				FTPBean ftpBean = new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileName(rs.getString("FileName"));
				
				ftpFileList.add(ftpBean);
				
				
			}*/
			
			filelist = getJdbcTemplate().query(query,new BeanPropertyRowMapper(ConfigurationBean.class));
			 logger.info("***** SourceDAoImpl.getFileDetails End ****");
		}catch (Exception ex) {
			
			demo.logSQLException(ex, "SourceDAoImpl.getFileDetails");
			 logger.error(" error in SourceDAoImpl.getFileDetails", new Exception("SourceDAoImpl.getFileDetails",ex));
			 throw ex;
		}
		
		
		
		return filelist;
	
	}

	@Override
	public boolean updateFileDetails(ConfigurationBean ftpBean) throws Exception {
		 logger.info("***** SourceDAoImpl.updateFileDetails Start ****");
		try {
			boolean result = false;
			int count =0;
			String query="";
			
			logger.info("logger.info(ftpBean)"+ftpBean);
			
			if(ftpBean.getInFileId()!=0) {
				
				logger.info("into the if condition");
				
				logger.info("flag"+ftpBean.getActiveFlag());
				count = getJdbcTemplate().update(
			                "UPDATE Main_fileSource "
			                + "	set FileName = ? ,dataseparator =? , ActiveFlag=? ,rddatafrm=?,charpatt=?  "
			                + " WHERE fileid = ?", 
			                ftpBean.getStFileName(),ftpBean.getDataSeparator(), ftpBean.getActiveFlag() ,ftpBean.getRdDataFrm(),ftpBean.getCharpatt(),ftpBean.getInFileId());
				logger.info("count"+count);
				
				
				if(count>0) {
					
					logger.info("Data updated successfully");
					result=true;
				}else{
					
					logger.info("Data not updated.");
					result = false;
				}
				
			}
			
			 logger.info("***** SourceDAoImpl.updateFileDetails End ****");
			return result;
		}catch(Exception ex) {
			
			demo.logSQLException(ex, "SourceDAoImpl.updateFileDetails");
			 logger.error(" error in SourceDAoImpl.updateFileDetails", new Exception("SourceDAoImpl.updateFileDetails",ex));
			 //throw ex;
			return false;
		}
		
		
	}

	@Override
	public boolean addFileSource(ConfigurationBean configBean) throws Exception {

		 logger.info("***** SourceDAoImpl.addFileSource Start ****");
		boolean result= false;
		String sql="INSERT into MAIN_FILESOURCE (FILEID,FILENAME,TABLENAME,ACTIVEFLAG,DATASEPARATOR,RDDATAFRM,CHARPATT,FILE_CATEGORY,FILE_SUBCATEGORY,FILTERATION,KNOCKOFF) values(?,?,?,?,?,?,?,?,?,?,?)";
		
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		String categ=configBean.getStCategory();
		logger.info(categ);
		String headers=null;
		try {
			
			String tablename="";
			
			if(configBean.getPrev_table()!=null && configBean.getPrev_tblFlag().equals("Y")) {
				
				tablename = configBean.getPrev_table();
				
			}else{
				
				tablename =  configBean.getStFileName()+"_"+configBean.getStCategory()+"_RAWDATA";
				logger.info(tablename);
			}
			
			logger.info(sql+configBean.getInFileId()+configBean.getStFileName()+configBean.getStFileName()+configBean.getActiveFlag()+configBean.getDataSeparator());
			int value = getJdbcTemplate().update(sql,new Object[]{configBean.getInFileId(),configBean.getStFileName(),tablename
					,configBean.getActiveFlag(),configBean.getDataSeparator(),configBean.getRdDataFrm(),configBean.getCharpatt(),configBean.getStCategory(),configBean.getStSubCategory(),
					configBean.getClassify_flag(),configBean.getKnock_offFlag()});
			
			if(value>0 ){
				
				 if( configBean.getPrev_tblFlag().equals("N")) {
				
				//  Creating a user table.
				
				String query="create table "+configBean.getStFileName()+"_"+configBean.getStCategory()+"_RAWDATA"+" (";
				String parameter="";
				String params[] = configBean.getStHeader().split(",");
				
				for(int i=0;i<params.length;i++){
					
						parameter=parameter+params[i].toUpperCase()+" varchar2(500),";
					
				
				}
				
			
				parameter= parameter+"PART_ID varchar2(2),";
				parameter= parameter+"DCRS_TRAN_NO number,";
				parameter= parameter+"NEXT_TRAN_DATE date,";
				parameter = parameter + " CreatedDate date Default sysdate,";
				parameter = parameter + " CreatedBy varchar2(500),";
				parameter = parameter + " FILEDATE date Default null";
				query=query+parameter+")";
				
				logger.info(query);
				
				getJdbcTemplate().execute(query);
				
				// Data Insert into  MAIN_FILEHEADERS
				
				String hdrquery = "INSERT into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader) values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1),?,?)";
				String header = configBean.getStHeader();
				value = getJdbcTemplate().update(hdrquery, new Object[]{configBean.getInFileId(),header.replace(" ", "_")});
				if(value>0){
					logger.info("Headers data inserted");
					transactionManager.commit(status); 
					result= true;
				} else {
					logger.info("Headers data not inserted");
					transactionManager.rollback(status);
					result = false;
				}
				
				 } else {
					 
					 
						 headers = getJdbcTemplate().queryForObject("(SELECT COLUMNHEADER from main_fileheaders where fileid = (select * from(select fileid from main_filesource where upper(tablename) ='"+configBean.getPrev_table().toUpperCase()+"' order by fileid ) where rownum =1))", String.class);
					 
					 logger.info(configBean.getPrev_table().toUpperCase());
					 String query = "insert into MAIN_FILEHEADERS (HEADERID , FILEID,Columnheader)"
					 		+ " values(((SELECT MAX(HEADERID) FROM  MAIN_FILEHEADERS)+1),"+configBean.getInFileId()+",'"+headers+"')";
					 
					 
					 int count = getJdbcTemplate().update(query);
					 
					 if(count>0) {
						 
						 logger.info("Headers data inserted");
							transactionManager.commit(status);
						 result = true;
						 
					 }else {
						 
						 logger.info("Headers data not inserted");
						 transactionManager.rollback(status);
						 result= false;
					 }
					 
				 }
				 logger.info("***** SourceDAoImpl.addFileSource End ****");
				
			}else {
				
				result=false;
			}
			
			
		}catch(Exception ex) {
			
			demo.logSQLException(ex, "SourceDAoImpl.addFileSource");
			 logger.error(" error in SourceDAoImpl.addFileSource", new Exception("SourceDAoImpl.addFileSource",ex));
			transactionManager.rollback(status);
			result=false;
			
		}
		
		
		return result;
	
	}

	@Override
	public int getFileId(ConfigurationBean configBean) {
		try {
			int i =  getJdbcTemplate().queryForObject(GET_FILEID, new Object[] {  },Integer.class);			
			return i;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public boolean chkTblExistOrNot(ConfigurationBean configBean) throws Exception {
		 logger.info("***** SourceDAoImpl.chkTblExistOrNot Start ****");
		try {
			
			//check Filename exist or not
			
			String filesql="SELECT count(*) FROM Main_FileSource WHERE upper (Filename) ='"+configBean.getStFileName().toUpperCase()+"'"
					+ " AND upper(FILE_CATEGORY) ='"+configBean.getStCategory().toUpperCase()+"' AND upper(FILE_SUBCATEGORY)='"+configBean.getStSubCategory()+"' ";
			
			int filerowNum=0;
			
			logger.info(filesql);
			
			
			filerowNum =  getJdbcTemplate().queryForObject(filesql, Integer.class);
			logger.info(filesql);
			
			
			
			
			
			//check table exist or not
			
			int rowNum=0;
			
			if(configBean.getPrev_tblFlag().equals("N")) {
				
				String sql="SELECT count (*) FROM tab WHERE tname  = '"+configBean.getStFileName().toUpperCase()+"_"+configBean.getStCategory()+"_RAWDATA'";
				rowNum =  getJdbcTemplate().queryForObject(sql, Integer.class);			
				logger.info(sql);
			}
			logger.info("***** SourceDAoImpl.chkTblExistOrNot End ****");
			if(rowNum > 0 && filerowNum >0){
				
				return false;
			}else{
				
				return true;
			}
			
		} catch (Exception e) {
			
			demo.logSQLException(e, "SourceDAoImpl.chkTblExistOrNot");
			 logger.error(" error in SourceDAoImpl.chkTblExistOrNot", new Exception("SourceDAoImpl.chkTblExistOrNot",e));
			return false;
		}
	}

	@Override
	public boolean addConfigParams(ConfigurationBean configBean) throws Exception  {
		logger.info("***** SourceDAoImpl.addConfigParams Start ****");
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		//GET ID FROM MASTER TABLE AND THEN INCREMENT IT
		try{
				id =  (getJdbcTemplate().queryForObject(GET_ID, new Object[] {  },Integer.class))+1;		
				
				Map<String, Object> inParams = new HashMap<String, Object>();
				
				//CHANGES MADE BY INT5779 FOR INSERTING PROPER CATEGORY IN DB
				//String category = configBean.getStSubCategory() != "" ? configBean.getStSubCategory()+"_"+configBean.getStCategory() : configBean.getStCategory();
				String category = configBean.getStSubCategory() != "" ? configBean.getStCategory()+"_"+configBean.getStSubCategory() : configBean.getStCategory();
				logger.info("CATEGORY IS "+configBean.getStCategory());
				logger.info("subcategory is "+configBean.getStSubCategory());
				
				inParams.put(FILE_ID , configBean.getInFileId());
				inParams.put(FILE_NAME, configBean.getStFileName());
				inParams.put(CATEGORY,category );
				inParams.put(ID, id);
				inParams.put(TABLE_NAME, (configBean.getStFileName()));
				inParams.put(ENTRY_BY, configBean.getStEntry_By());
				
				logger.info(configBean.getInFileId());
				logger.info(configBean.getStFileName());
				logger.info(category);
				logger.info(id);
				logger.info(configBean.getStFileName());
				logger.info(configBean.getStEntry_By());
				
				addCompareConfigParams addcompareparams = new addCompareConfigParams(getJdbcTemplate());
				Map<String, Object> outParams = addcompareparams.execute(inParams);

				if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				
					logger.info(outParams.get(O_ERROR_MESSAGE));
					return false;
				}
				
				//ADDING VALUES IN MAIN_COMPARE_DETAILS TABLE
				List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
				comp_dtl_list = configBean.getComp_dtl_list();
				
				List<ConfigurationBean> clasify_dtl_list = new ArrayList<ConfigurationBean>();
				clasify_dtl_list = configBean.getClasify_dtl_list();
				
				
				insertBatch(clasify_dtl_list,configBean);
				
				if(configBean.getKnock_offFlag().equals("Y")) {
					
					insertKnockOffBatch(comp_dtl_list, configBean);
					
				}
				
				logger.info("***** SourceDAoImpl.addConfigParams End ****");
				
				return true;
			
				
				
		}catch(Exception ex) {
			
			demo.logSQLException(ex, "SourceDAoImpl.addConfigParams");
			 logger.error(" error in SourceDAoImpl.addConfigParams", new Exception("SourceDAoImpl.addConfigParams",ex));
			
			return false;
			
		}
		
	}

	public void insertBatch(final List<ConfigurationBean> comp_dtl_list,
			final ConfigurationBean configurationBean) {
		logger.info("***** SourceDAoImpl.insertBatch Start ****");
		/** Passing Sql query. */
		getJdbcTemplate().batchUpdate(ADD_COMPARE_DETAILS,
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int j)
							throws SQLException {

						ConfigurationBean configbean = comp_dtl_list.get(j);

						
						if(configbean.getStHeader()!=null) {
						ps.setInt(1, id);
						ps.setInt(2, configurationBean.getInFileId());
						logger.info(configurationBean.getInFileId());
						ps.setString(3, configbean.getStHeader());
						ps.setString(4, configbean.getStSearch_Pattern());
						ps.setString(5, configbean.getStPadding());
						logger.info("getStPadding"+configbean.getStPadding());
						// ps.setInt(6, configbean.getInChar_Position());
						ps.setInt(6, configbean.getInStart_Char_Position());
						ps.setInt(7, configbean.getInEnd_char_position());
						ps.setString(8, configbean.getCondition());
						ps.setString(9,configbean.getStEntry_By() );
						ps.setTimestamp(10,new java.sql.Timestamp(
								new java.util.Date().getTime()));
						logger.info(id);
						logger.info(configurationBean.getInFileId());
						logger.info(configbean.getStHeader());
						logger.info( configbean.getStSearch_Pattern());
						logger.info(configbean.getStPadding());
						logger.info(configbean.getInStart_Char_Position());
						logger.info(configbean.getInEnd_char_position());
						logger.info(configurationBean.getStEntry_By());
						logger.info(new java.sql.Timestamp(
								new java.util.Date().getTime()));
						logger.info(configbean.getCondition());
						
						}

					}

					@Override
					public int getBatchSize() {
						return comp_dtl_list.size();
					}
				});
		logger.info("***** SourceDAoImpl.insertBatch End ****");
	}
	

	public void insertKnockOffBatch(final List<ConfigurationBean> comp_dtl_list,
			final ConfigurationBean configurationBean) throws Exception {
		
		logger.info("***** SourceDAoImpl.insertKnockOffBatch Start ****");
		String query="SELECT CASE WHEN  (SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL) is null then 0 else (SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL) end as FLAG from dual";
				//"SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL";
		
		id =  (getJdbcTemplate().queryForObject(query, new Object[] {  },Integer.class))+1;	
		logger.info("id"+id);

		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		try {
			
			insertMAIN_REVERSAL_DETAIL(comp_dtl_list, configurationBean);
			insertMAIN_REVERSAL_PARAMETERS(comp_dtl_list, configurationBean);
			insertMAIN_KNOCKOFF_CRITERIA(comp_dtl_list, configurationBean);
			transactionManager.commit(status);
			}catch(Exception ex) {
				
				transactionManager.rollback(status);
				demo.logSQLException(ex, "SourceDAoImpl.insertKnockOffBatch");
				 logger.error(" error in SourceDAoImpl.insertKnockOffBatch", new Exception("SourceDAoImpl.insertKnockOffBatch",ex));
				
			}
		
		logger.info("***** SourceDAoImpl.insertKnockOffBatch End ****");
	}
	
	
	
	public void insertMAIN_REVERSAL_DETAIL(final List<ConfigurationBean> comp_dtl_list,
			final ConfigurationBean configurationBean){
		logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_DETAIL Start ****");
		//ConfigurationBean configbean = null;
		for(final ConfigurationBean configbean:comp_dtl_list ){
		
			logger.info("forconfigbean.getKnockoff_col()"+configbean.getKnockoff_col());
			if(configbean.getKnockoff_OrgVal()!=null) {
				
			//	String category = configurationBean.getStSubCategory() != "" ? configurationBean.getStSubCategory()+"_"+configurationBean.getStCategory() : configurationBean.getStCategory();
				String category = configurationBean.getStSubCategory() != "" ? configurationBean.getStCategory()+"_"+configurationBean.getStSubCategory() : configurationBean.getStCategory();
				
				logger.info(category);
				getJdbcTemplate().update("Insert into MAIN_REVERSAL_DETAIL (REVERSAL_ID,FILE_ID,CATEGORY,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)"
						+ "values ("+id+","+configurationBean.getInFileId()+",'"+category+"','"+configbean.getKnockoff_col()+"', '"+configbean.getKnockoff_OrgVal()+"','"+configurationBean.getStEntry_By()+"',sysdate)");
				
			}
		
		}
		logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_DETAIL End ****");
	}
	

	public void insertMAIN_REVERSAL_PARAMETERS(final List<ConfigurationBean> comp_dtl_list,
			final ConfigurationBean configurationBean){
		
		logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_PARAMETERS Start ****");
		for(final ConfigurationBean configbean:comp_dtl_list ){
		
			if(configbean.getKnockoff_comprVal()!=null) {
				
				getJdbcTemplate().update("Insert into MAIN_REVERSAL_PARAMETERS (REVERSAL_ID,FILE_ID,HEADER,VALUE,ENTRY_BY,ENTRY_DATE)values ("+id+", "+configurationBean.getInFileId()+",'"+configbean.getKnockoff_col()+"','"+configbean.getKnockoff_comprVal()+"','"+configurationBean.getStEntry_By()+"',sysdate )");
				
				
			}
		
		}
		
		logger.info("***** SourceDAoImpl.insertMAIN_REVERSAL_PARAMETERS End ****");
		
	}

	

	public void insertMAIN_KNOCKOFF_CRITERIA(final List<ConfigurationBean> comp_dtl_list,
			final ConfigurationBean configurationBean){
		
		logger.info("***** SourceDAoImpl.insertMAIN_KNOCKOFF_CRITERIA Start ****");
		for(final ConfigurationBean configbean:comp_dtl_list ){
		
			if(configbean.getKnockoff_header()!=null) {
				
			
		
			/*getJdbcTemplate().update("Insert into MAIN_KNOCKOFF_CRITERIA (REVERSAL_ID,FILE_ID,HEADER,ENTRY_BY,ENTRY_DATE,PADDING,START_CHARPOSITION,CHAR_SIZE,HEADER_VALUE,CONDITION)"
					+ "values ("+id+", "+configurationBean.getInFileId()+",'"+configbean.getKnockoff_header()+"','"+configurationBean.getStEntry_By()+"',sysdate,'"+configbean.getKnockoff_stPadding()+"','"+configbean.getKnockoffEnd_char_pos()+"','"+configbean.getKnockoffStart_Char_Pos()+"','"+configbean.getKnockoffSrch_Pattern()+"','"+configbean.getKnockoff_condition()+"')");
		*/
				//modified BY INT5779 FOR IMPROPER END AND START CHAR POS
			getJdbcTemplate().update("Insert into MAIN_KNOCKOFF_CRITERIA (REVERSAL_ID,FILE_ID,HEADER,ENTRY_BY,ENTRY_DATE,PADDING," +
					"START_CHARPOSITION,CHAR_SIZE,HEADER_VALUE,CONDITION)"
						+ "values ("+id+", "+configurationBean.getInFileId()+",'"+configbean.getKnockoff_header()+"','"
					+configurationBean.getStEntry_By()+"',sysdate,'"+configbean.getKnockoff_stPadding()+"','"
						+configbean.getKnockoffStart_Char_Pos()+"','"+configbean.getKnockoffEnd_char_pos()+"','"
					+configbean.getKnockoffSrch_Pattern()+"','"+configbean.getKnockoff_condition()+"')");
			
				
			}
		}
		
		logger.info("***** SourceDAoImpl.insertMAIN_KNOCKOFF_CRITERIA End ****");
		
	}
	
	private class addCompareConfigParams extends StoredProcedure {
		private static final String add_config_params = "ADD_COMPARE_DATA";
		

		public addCompareConfigParams(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, add_config_params);
			setFunction(false);
			declareParameter(new SqlParameter(FILE_ID, Types.INTEGER));
			declareParameter(new SqlParameter(FILE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(CATEGORY, Types.VARCHAR));
			declareParameter(new SqlParameter(ID, Types.INTEGER));
			declareParameter(new SqlParameter(TABLE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(ENTRY_BY, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE,
					Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE,
					Types.VARCHAR));
			compile();

		}
	}




	@Override
	public String getHeaderList(int fileId) {
		logger.info("***** SourceDAoImpl.getHeaderList Start ****");
		String sql="SELECT columnheader FROM main_fileheaders WHERE fileid  = "+fileId+"";
		String hedrlist = null;		
		logger.info("sql"+sql);
		hedrlist = getJdbcTemplate().queryForObject(sql,String.class);
		
		logger.info(hedrlist);
		logger.info("***** SourceDAoImpl.getHeaderList End ****");
		return hedrlist;
	}


	@Override
	public ArrayList<ConfigurationBean> getCompareDetails(int fileId,
			String category,String subcat) throws Exception {
		logger.info("***** SourceDAoImpl.getCompareDetails Start ****");
		subcat = subcat== null ? "" : subcat; 
		ArrayList<ConfigurationBean> configurationBeans= null;
		try{
			String stcategory = !(subcat.equals("-"))?category+"_"+subcat: category;
		String sql = "SELECT mcompdtl.File_header as stHeader ,mcompdtl.SEARCH_PATTERN as stSearch_Pattern , "
				+ " mcompdtl.PADDING as stPadding ,mcompdtl.START_CHARPOSITION as inStart_Char_Position,"
				+ " mcompdtl.END_CHARPOSITION as inEnd_char_position,  mcompm.category as stCategory ,mcompdtl.CONDITION as condition "
				+ " FROM Main_Compare_detail mcompdtl INNER JOIN main_Compare_master mcompm"
				+ " ON mcompdtl.id = mcompm.ID"
				+ " WHERE mcompdtl.FILE_ID="+fileId+" and mcompm.category = '"+stcategory+"' ";
		
		logger.info(sql);
		
		logger.info("***** SourceDAoImpl.getCompareDetails End ****");
		 configurationBeans = (ArrayList<ConfigurationBean>) getJdbcTemplate().query(sql,new BeanPropertyRowMapper(ConfigurationBean.class));
		}catch(Exception ex){
			demo.logSQLException(ex, "SourceDAoImpl.getCompareDetails");
			 logger.error(" error in SourceDAoImpl.getCompareDetails", new Exception("SourceDAoImpl.getCompareDetails",ex));
			 throw ex;
		}
		return configurationBeans;
	}


	@Override
	public List<ConfigurationBean> getknockoffDetails(int fileId,
			String category,String  subcat) throws Exception {
		logger.info("***** SourceDAoImpl.getknockoffDetails Start ****");
		ArrayList<ConfigurationBean> configurationBeans= null;
		try{
			
			String query = "SELECT distinct mrevparam.Header as Knockoff_col,mrevdtl.VALUE as Knockoff_comprVal ,mrevparam.VALUE as Knockoff_OrgVal"
					+ " FROM MAIN_REVERSAL_DETAIL mrevdtl "
					+ " INNER JOIN main_reversal_parameters mrevparam"
					+ " ON mrevdtl.REVERSAL_ID= mrevparam.REVERSAL_ID"
					+ " WHERE mrevparam.file_id="+fileId+" AND mrevdtl.CATEGORY ='"+category+"'";
			logger.info(query);
			
			 configurationBeans = (ArrayList<ConfigurationBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(ConfigurationBean.class));
			 logger.info("***** SourceDAoImpl.getknockoffDetails End ****");
		}catch(Exception ex) {
			demo.logSQLException(ex, "SourceDAoImpl.getknockoffDetails");
			 logger.error(" error in SourceDAoImpl.getknockoffDetails", new Exception("SourceDAoImpl.getknockoffDetails",ex));
			 throw ex;
		}
		return configurationBeans;
	}


	@Override
	public List<ConfigurationBean> getknockoffcrt(int fileId, String category,String subcat) throws Exception {
		ArrayList<ConfigurationBean> configurationBeans= null;
		logger.info("***** SourceDAoImpl.getknockoffcrt Start ****");
		subcat = subcat== null ? "" : subcat; 
		try{
			
			String stCategory = !(subcat.equals("-"))?category+"_"+subcat: category;
			String query = "SELECT distinct knckcrt.HEADER as knockoff_header,knckcrt.PADDING as knockoff_stPadding,knckcrt.START_CHARPOSITION as knockoffStart_Char_Pos,"
					+ "		knckcrt.CHAR_SIZE as knockoffEnd_char_pos,knckcrt.HEADER_VALUE as knockoffSrch_Pattern ,knckcrt.CONDITION as knockoff_condition "
					+ "					FROM Main_Knockoff_criteria knckcrt "
					+ "		INNER JOIN MAIN_REVERSAL_DETAIL mrevdtl "
					+ "		ON knckcrt.REVERSAL_ID = mrevdtl.REVERSAL_ID "
					+ "		WHERE knckcrt.FILE_ID="+fileId+" and mrevdtl.CATEGORY='"+stCategory+"'";
			logger.info(query);
			
			 configurationBeans = (ArrayList<ConfigurationBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(ConfigurationBean.class));
			 logger.info("***** SourceDAoImpl.getknockoffcrt End ****"); 
		}catch(Exception ex) {
			demo.logSQLException(ex, "SourceDAoImpl.getknockoffcrt");
			 logger.error(" error in SourceDAoImpl.getknockoffcrt", new Exception("SourceDAoImpl.getknockoffcrt",ex));
			 throw ex;
		}
		return configurationBeans;
	}


	@Override
	public List<CompareSetupBean> getFileList(String category, String subcat) throws Exception {
		logger.info("***** SourceDAoImpl.getFileList Start ****");
		subcat = subcat== null ? "" : subcat; 
		try{
			
			/*String stCategory = !(subcat.equals("-")) ? category + "_" + subcat
					: category;*/

			List<CompareSetupBean> beans = null;
			String query = "Select fileid as inFileId, filename as stFileName "
					+ " FROM MAIN_FILESOURCE  where FILE_CATEGORY='" + category+ "' ";

			if (subcat != null && subcat != "-") {

				query = query + " AND FILE_SUBCATEGORY='" + subcat + "'";
			}

			logger.info(query);
			beans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class));
			logger.info("***** SourceDAoImpl.getFileList End ****");
			return beans;
		}catch(Exception ex) {
			demo.logSQLException(ex, "SourceDAoImpl.getFileList");
			 logger.error(" error in SourceDAoImpl.getFileList", new Exception("SourceDAoImpl.getFileList",ex));
			return null;
		}
		
	}

	

	
	@Override
	public List<String> gettable_list() {
		
		String query= "Select distinct TABLENAME from main_filesource ";
		
		//getJdbcTemplate().queryForList(query, String.class);
		
		
		
		return getJdbcTemplate().queryForList(query, String.class);
	}


	@Override
	public List<CompareSetupBean> getFileTypeList(String category,String subcat, String table) throws Exception {
		// TODO Auto-generated method stub
		logger.info("***** SourceDAoImpl.getFileTypeList Start ****");
			try{
				
				/*String stCategory = !(subcat.equals("-")) ? category + "_" + subcat
						: category;*/
				String query = "";
				
				List<CompareSetupBean> beans = null;
				/*if(!subcat.equals("-"))
				{
					query = "select distinct dcrs_remarks as remarks from "+table;
							//+" WHERE DCRS_REMARKS LIKE '%"+category+"_"+subcat.substring(0, 3)+"%'";
				}
				else*/
				if(!subcat.equals("-") && (!category.equals("NFS")) )
				{
					/*query = "select distinct dcrs_remarks as remarks from "+table
							+" WHERE DCRS_REMARKS LIKE '%"+category+"_"+subcat.substring(0, 3)+"%'";*/
					query = "select distinct dcrs_remarks as remarks from "+table+" WHERE DCRS_REMARKS NOT LIKE '%(%'";

				}
				else 
				{
					//query = "select distinct dcrs_remarks as remarks from "+table;
					query ="select   distinct dcrs_remarks as remarks   from( "
							+ "select case substr(dcrs_remarks,1,20) when   'MATCHED_UNSUCCESSFUL' then 'MATCHED_UNSUCCESSFUL'"
							+ "else dcrs_remarks  end as dcrs_remarks    from  "+table+" "
							+ ")";
					/*query = "select distinct translate(REGEXP_REPLACE (DCRS_REMARKS, '\\d', ''),'()',' ') as remarks " +
							"from "+table+" WHERE DCRS_REMARKS LIKE '%"+category+"_"+subcat.substring(0, 3)+"%' ";*/
					
				}
				
					//query = "select distinct dcrs_remarks as remarks from "+table+" WHERE DCRS_REMARKS NOT LIKE '%(%'";
					
					

				

				logger.info(query);
				beans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class));
				
				logger.info("***** SourceDAoImpl.getFileTypeList End ****");
				return beans;
			}catch(Exception ex) {
				
				demo.logSQLException(ex, "SourceDAoImpl.getFileTypeList");
				 logger.error(" error in SourceDAoImpl.getFileTypeList", new Exception("SourceDAoImpl.getFileTypeList",ex));
				return null;
			}
			
		}
	
@Override	
public List<String> getSubcategories(String category) throws Exception
{
	logger.info("***** SourceDAoImpl.getSubcategories Start ****");
	List<String> SubCategories = new ArrayList<>();
	try
	{
		String GET_SUBCATE = "select distinct file_subcategory from main_filesource where file_category = ? and file_subcategory not like '%SUR%' ";
		SubCategories = getJdbcTemplate().query(GET_SUBCATE, new Object[] {category},new RowMapper()
		{
			@Override
			public String mapRow(ResultSet rs, int rownum) throws SQLException 
			{	
					String subcategory = rs.getString("FILE_SUBCATEGORY");
					return subcategory;
				
			}
		}
				);
		logger.info("***** SourceDAoImpl.getSubcategories End ****");
		return SubCategories;
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "SourceDAoImpl.getSubcategories");
		 logger.error(" error in SourceDAoImpl.getSubcategories", new Exception("SourceDAoImpl.getSubcategories",e));
		return null;
	}
		
}


@Override
public boolean generateCTF(SettlementBean settlementBean) throws IOException {
	// TODO Auto-generated method stub

	String path = "D:/Bankaway.ctf";
	BufferedWriter output = null;
	OracleConn conn;
	int row_lenth_1 = 168;
	int row_lenth_2 = 45;
	int row_lenth_3 = 21;
	int count_1=0;
	int count_2=0;
	int count_3=0;
	String julian_date=null;
	int result_count=0;
	String result_count_val=null;
	String sun_val=null;
	int main_count=0;
	ArrayList<String> arr_test = new ArrayList<String>();
	ArrayList<String> arr_test2 = new ArrayList<String>();
	ArrayList<SettlementTypeBean> arr = new ArrayList<SettlementTypeBean>();
	/*Scanner scan=new Scanner(System.in);
	System.out.println("Enter Date in mm/dd/yyyy format :: ");
	String dt_va=scan.next();
	System.out.println("Date :: "+dt_va);*/
	String julian_date1=null;
	try {
		conn = new OracleConn();
		Connection con = conn.getconn();
		
		File file = new File(path);
		output = new BufferedWriter(new FileWriter(file));

		String get_bankrepo = "select * from SETTLEMENT_CARDTOCARD_BANKREPO where DCRS_REMARKS='CARDTOCARD_BANKWAY_MATCED' and TO_CHAR (filedate, 'mm/dd/yyyy') = "
				+ " TO_CHAR (TO_DATE ('"+settlementBean.getDatepicker()+"', 'MM/DD/YYYY'), 'MM/DD/YYYY')";

		PreparedStatement st = con.prepareStatement(get_bankrepo);
		ResultSet rs = st.executeQuery();

		while (rs.next()) {

			SettlementTypeBean sb = new SettlementTypeBean();
			String visa_card_no = rs.getString("VISA_CARD_NO");
			String mobile_no = rs.getString("MOBILE_NO");
			String amount1 = rs.getString("AMOUNT");
			String amount = amount1.replaceAll("[,.]", "");
			String sol_id = rs.getString("SOL_ID");
			String debit_acc = rs.getString("DEBIT_ACC");
			String acc_name = rs.getString("ACC_NAME");
			String payment_id = rs.getString("PAYMENT_ID");
			String channel = rs.getString("CHANNEL");
			String date_time = rs.getString("DATE_TIME");
			String file_date = rs.getString("FILEDATE");
			
			long random_number = Utility.generateRandom();
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
			Date dateinput = inputFormat.parse(rs.getString("FILEDATE"));
			SimpleDateFormat sdfoffsite1 = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.S");
			DateFormat outputFormat_new = new SimpleDateFormat("ddMMyyyy");
    		String outputString_new = outputFormat_new.format(dateinput);
			java.util.Date datecisb;

			datecisb = sdfoffsite1.parse(file_date);

			sdfoffsite1 = new SimpleDateFormat("MM/dd/yyyy");

			System.out.println(sdfoffsite1.format(datecisb));

			String dataenew = sdfoffsite1.format(datecisb);
			
			int julian_dt = Utility.convertToJulian(outputString_new);
			int julian_dt1 = Utility.convertToJulian2(outputString_new);
			 julian_date = String.valueOf(julian_dt);
			 julian_date1 = String.valueOf(julian_dt1);
			String split_dt[] = dataenew.split("/");
			System.out.println(split_dt[0] + split_dt[1]);
			String amount_val=Utility.appnd_zeros(amount);
			System.out.println("12 digit Amount"+ amount_val);
			String get_acc_name=Utility.appnd_space(acc_name);
			
			arr_test.add("06");// 1-2
			arr_test.add("20");// 3-4
			arr_test.add(visa_card_no);// 5-20
			arr_test.add("000");// 21-23
			arr_test.add("   ");// 24-26
			//arr_test.add("7421426" + julian_date + random_number);// 27-49
		    arr_test.add(Utility.get_mod("7421426"+"000"+ julian_date + random_number));
			arr_test.add("00000000");// 50-57
			arr_test.add(split_dt[0] + split_dt[1]);// 58-61
			arr_test.add("000000000000");// 62-73
			arr_test.add("   ");// 74-76
			arr_test.add(amount_val);// 77-88
			arr_test.add("356");// 89-91
			arr_test.add(get_acc_name);// 92-116
			arr_test.add("VISAMONEYTXFR");// 117-129
			arr_test.add("IN ");// 130-132
			arr_test.add("6051");// 133-136
			arr_test.add("00000");// 137-141
			arr_test.add("   ");// 142-144
			arr_test.add(" ");// 145
			arr_test.add(" ");// 146
			arr_test.add("1");//147
			arr_test.add("00");// 148-149
			arr_test.add("9");// 150
			arr_test.add(" ");// 151
			arr_test.add("      ");// 152-157
			arr_test.add(" ");// 158
			arr_test.add(" ");// 159
			arr_test.add(" ");// 160
			arr_test.add(" ");// 161
			arr_test.add("0");// 162-163
			arr_test.add(julian_date1);// 164-167
			arr_test.add("0");// 168
			
			//Newly added on 02/12/2018
			
			arr_test.add("06");// 1-2
			arr_test.add("21");// 3-4
			String sub_str= visa_card_no.substring(0, 6);
			String concat=sub_str+"4214260000";
			arr_test.add(concat);// 5-20
			arr_test.add("00");// 21-23
			arr_test.add(" ");// 23-24
			arr_test.add("PYMNT ID NO-");// 25-35
			arr_test.add(" ");// 36-37
			arr_test.add(payment_id);// 58-61
			arr_test.add("                                                          ");// 46-103
			arr_test.add("000000000000");// 104-115
			arr_test.add(" ");// 116-117
			arr_test.add("             ");// 117-129
			arr_test.add("0");// 130-131
			arr_test.add("                           ");// 131-157
			arr_test.add("000000000");// 158-166
			arr_test.add("  ");// 167-168
			arr_test.add("06");// 1-2
			arr_test.add("23");// 3-4
			arr_test.add("            ");
			arr_test.add("CRCP");
		
			
			
		}
		FileWriter writer = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(writer);
		//arr_test2=arr_test.iterator().next();
		String bind_rec="";
		//arr_test.indexOf(i);
		String bind_2="";
		String bind_rec1="";
		
		String bind_3="";
		int counter_value=0;
		for (String str : arr_test) {
			counter_value++;
			bw.append(str);
			//bind_rec=str;
			//System.out.println("Index value ::"+arr_test.indexOf(str));
			//bw.append("\n");
			
			count_1=count_1+1;
			System.out.println("Count ::"+count_1);
			if(str.equals(julian_date1))
			{
				bind_rec="";
				bind_rec=bind_rec+str;
				//System.out.println("Bind 1 :: "+bind_rec);
			}
			if(!bind_rec.equals(""))
			{  
				count_3=count_3+1;
				if(count_3==2)
				{
				bind_2=bind_rec+"0";
				//System.out.println("Bind 2 :: "+bind_2);
				}
			}
			if(str.equals("000000000"))
			{
				bind_rec1="";
				bind_rec1=bind_rec1+str;
				System.out.println("Bind 1 :: "+bind_rec1);
			}
			
			if(!bind_rec1.equals(""))
			{  
				count_3=count_3+1;
				if(count_3==2)
				{
				bind_3=bind_rec1+"  ";
				//System.out.println("Bind 2 :: "+bind_3);
				}
			}
			//bind_rec=bind_rec+str;
			if (bind_2.equals(julian_date1+"0")) {
				bw.append("\n");
				main_count=main_count+1;
				bind_2="";
				count_3=0;
				bind_rec="";
				//bw.newLine();
				/*count_2=count_2+1;
				if(count_2==1)
				{
					bw.append("TEST");
				}*/
//				/bw.newLine();
				count_1=0;
			}
			else if(bind_3.equals("000000000  "))
			{
				bw.append("\n");
				main_count=main_count+1;
				bind_3="";
				count_3=0;
				bind_rec1="";
				bind_2="";
				count_3=0;
				bind_rec="";
				//bw.newLine();
				/*count_2=count_2+1;
				if(count_2==1)
				{
					bw.append("TEST");
				}*/
//				/bw.newLine();
				count_1=0;
			}
			else if(str.equals("CRCP"))
			{
				System.out.println("Counter value-->>"+counter_value);
				System.out.println("Array value-->>"+arr_test.size());
				if(counter_value!=arr_test.size())
				{
				bw.append("\n");
				}
				else{
					 //bw.append(String.format("%s%n", counter_value));
					 System.out.println("Inside else");


				}
				main_count=main_count+1;
				
				bind_3="";
				count_3=0;
				bind_rec1="";
				bind_2="";
				count_3=0;
				bind_rec="";
				//bw.newLine();
				/*count_2=count_2+1;
				if(count_2==1)
				{
					bw.append("TEST");
				}*/
//				/bw.newLine();
				count_1=0;
			}
		}
		/*main_count=main_count+1;
		
		System.out.println("Main Count"+ main_count);
		String count_val=Integer.toString(main_count);
		String main_cnt=Utility.auto_append(count_val);
		int count_6=Integer.parseInt(main_cnt);
		int main_count2=count_6+1;
		String count_val2=Integer.toString(main_count2);
		String main_cnt2=Utility.auto_append(count_val2);
		int count_7=Integer.parseInt(main_cnt2);*/
		
		String get_rec_count="select count(*) as count,sum(to_number(replace((replace(os1.amount,',')),'.'))) as total from SETTLEMENT_CARDTOCARD_BANKREPO os1";
		PreparedStatement pst=con.prepareStatement(get_rec_count);
		ResultSet rst=pst.executeQuery();
		while(rst.next())
		{
			/*//result_count=rst.getInt("count");
			System.out.println("Count"+result_count);
			String val=Integer.toString(result_count);
			result_count_val=Utility.auto_append(val);*/
			sun_val=rst.getString("total");
			System.out.println("Sum"+sun_val);
			
			
		}
		/*int get_row1=result_count+1;
		String val2=Integer.toString(get_row1);
		String row_count=Utility.auto_append3(val2);
		int get_count=Integer.parseInt(row_count);
		int get_row2=get_row1+1;
		String get_rw=Integer.toString(get_row2);
		String row_count2=Utility.auto_append3(get_rw);
		String get_total=Utility.auto_append2(sun_val);*/
		
		/*bw.append("9100426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt+"000000"+"00000001"+row_count+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
		bw.append("\n");
		bw.append("9200426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt2+"000000"+"00000000"+row_count2+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");*/
		
		bw.close();
		writer.close();
		System.out.println("CTF file created");
	

	// Reading file and getting no. of files to be generated
	//String inputfile = "D:/ctf_test.txt"; // Source File Name.
	double nol = 990.0; // No. of lines to be split and saved in each
							// output file.
	File file1 = new File(path);
	Scanner scanner = new Scanner(file1);
	int count = 0;
	int count_lines=0;
	while (scanner.hasNextLine()) {
		scanner.nextLine();
		/*if(count_lines==3)
		{
		count++;
		count_lines=0;
		continue;
		}
		count_lines++;*/
		count++;
	}
	System.out.println("Lines in the file: " + count); // Displays no.
														// of lines in
														// the input
														// file.

	double temp = (count / nol);
	int temp1 = (int) temp;
	int nof = 0;
	if (temp1 == temp) {
		nof = temp1;
	} else {
		nof = temp1 + 1;
	}
	System.out.println("No. of files to be generated :" + nof); // Displays
																// no.
																// of
																// files
																// to be
																// generated.

	// ---------------------------------------------------------------------------------------------------------

	// Actual splitting of file into smaller files
    int new_count=0;
    int total_sum=0;
    int total_rows=0;
	FileInputStream fstream = new FileInputStream(path);
	DataInputStream in = new DataInputStream(fstream);

	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String strLine;

	for (int j = 1; j <= nof; j++) {
		total_rows=0;
		total_sum=0;
		new_count=0;
		result_count=0;
		FileWriter fstream1 = new FileWriter("D:/" + j
				+ ".ctf"); // Destination File Location
		BufferedWriter out = new BufferedWriter(fstream1);
		for (int i = 1; i <= nol; i++) {
			
			strLine = br.readLine();
			if (strLine != null) {
				total_rows++;
				out.write(strLine);
				if (i != nol) {
					out.newLine();
					if(new_count==3)
					{
						result_count++;
						new_count=0;
					}
					try{
					new_count++;
					//result_count++;
					String get_amount=strLine.substring(77, 88);
					int get_tot=Integer.parseInt(get_amount);
					total_sum+=get_tot;
					}catch(Exception e)
					{
						e.printStackTrace();
						continue;
					}
				}
				
			}else{
				result_count++;
				
				break;
			}
			
		}
		if(j!=nof)
		{
			result_count++;
			out.newLine();
			System.out.println("Inside no of lines"+nof);
		}
		
		System.out.println("Total Sum::"+total_sum);
		total_rows=total_rows+1;
		
		System.out.println("Main Count"+ total_rows);
		String count_val=Integer.toString(total_rows);
		String main_cnt=Utility.auto_append(count_val);
		int count_6=Integer.parseInt(main_cnt);
		int main_count2=count_6+1;
		String count_val2=Integer.toString(main_count2);
		String main_cnt2=Utility.auto_append(count_val2);
		System.out.println("Total count ::"+result_count);
		String val=Integer.toString(result_count);
		result_count_val=Utility.auto_append(val);
		int get_row1=result_count+1;
		String val2=Integer.toString(get_row1);
		String row_count=Utility.auto_append3(val2);
		int get_count=Integer.parseInt(row_count);
		int get_row2=get_row1+1;
		String get_rw=Integer.toString(get_row2);
		String row_count2=Utility.auto_append3(get_rw);
		String tot_val=Integer.toString(total_sum);
		String get_total=Utility.auto_append2(tot_val);
       // out.newLine();
		out.append("9100426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt+"000000"+"00000001"+row_count+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
		out.newLine();
		out.append("9200426365"+julian_date1+"000000000000000"+result_count_val+"000000"+main_cnt2+"000000"+"00000000"+row_count2+"000000000000000000"+get_total+"000000000000000"+"00000000000000"+"0000000000000000"+"0000000");
		out.close();
	}
    
	in.close();

}
	



	/*
	 * PrintWriter pw = new PrintWriter(file); for(int
	 * index=0;index<arr.size();index++) {
	 * System.out.println("Output ::"+arr.get(index).getVisa_card_no() );
	 * pw.println(arr.get(index).getVisa_card_no().toString()); }
	 */

	/*
	 * String arr1=arr.toString(); for (int k = 0; k < arr.size(); k++){
	 * output.writ(arr.get(k)); }int count=1; for(String str: arr_test) {
	 * if(count==2) { output.write("//n"); count=1; } output.write(str);
	 * System.out.println("CTF file created"); count=count+1; } }
	 */

	catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		return false;
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		return false;
	} catch (IOException e) {
		e.printStackTrace();
		System.out.println("Failed to CTF file created");
	}
	output.close();
	return true;


}



@Override
public Settlement_FinalBean getReportDetails(SettlementBean settlementBean) {
	Settlement_FinalBean finalBean = new Settlement_FinalBean();
	
	
	try {
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");

		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();

		inParams.put("I_FILEDATE", settlementBean.getDatepicker());
		inParams.put("NET_SETTL_AMT", settlementBean.getNet_settl_amt());
		inParams.put("MAN_Iss_Repr_Dtls_Amt", settlementBean.getMan_Iss_represnment());
		inParams.put("MAN_Acq_Repr_Dtls_Amt", settlementBean.getMan_ACQ_represnment());
		inParams.put("User_ID", settlementBean.getUser_id());

		GenerateCashnetSettlement acqclassificaton = new GenerateCashnetSettlement(getJdbcTemplate()); //(getJdbcTemplate());
		Map<String, Object> outParams = acqclassificaton.execute(inParams);

		// logger.info("outParams msg1"+outParams.get("msg1"));
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");

		if (outParams.get("ERROR_MSG") != null) {
			
			System.out.println(outParams.get("ERROR_MSG"));

		//	return finalBean;
		} 

	} catch (Exception e) {
		//demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
		logger.error(" error in  ReconProcessDaoImpl.getReportDetails",
				new Exception(" ReconProcessDaoImpl.getReportDetails", e));
		//return finalBean;
	}
	
	
	finalBean =	 getJdbcTemplate().queryForObject("select * from Cashnet_settlement where filedate = ?", new Object[]{settlementBean.getDatepicker()}, new Settlement_FinalBeanRowMapper());
		
	finalBean.setFileDate(settlementBean.getDatepicker());
	//finalBean.set
			//("select * from Settlement_Final_Report_condn where filedate='"+settlementBean.getDatepicker()+"'", new Settlement_FinalBean());
					
				/*		//new Settlement_FinalBean(){

							@Override
							public Settlement_FinalBean mapRow(
									ResultSet rs, int arg1)
									throws SQLException {
								Settle_final_report_condn_bean condn_bean = new Settle_final_report_condn_bean();
								condn_bean.setAccount_type(rs.getString("account_type"));
								condn_bean.setReport_col(rs.getString("Report_col"));
								condn_bean.setNetworktype(rs.getString("Networktype"));
								condn_bean.setTTYPE(rs.getString("TTYPE"));
								condn_bean.setRE_Networktype(rs.getString("RE_Networktype"));
								condn_bean.setRE_TTYPE(rs.getString("RE_TTYPE"));
								
								
								return condn_bean;
							}
							
						});*/

	
/*	finalBean = setData(condn_beans,filedate);
	finalBean = setChargebackData(finalBean,filedate);*/
		
		
		
	
	
	
	
	
	return finalBean;
}


private Settlement_FinalBean setChargebackData(Settlement_FinalBean finalBean,String filedate) {
	
	float iss_charge_dtls_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) - nvl(sum(to_number(ADJ_AMOUNT)),0)    from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy')='"+filedate+"' and   trim(upper(ADJ_TYPE)) in('CHARGEBACK')", Integer.class);
	
	float iss_charge_dtls_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy')='"+filedate+"' and     trim(upper(ADJ_TYPE)) in('CHARGEBACK') ", Integer.class);
	
  float acq_charge_dtls_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where   to_date(filedate,'dd/mon/yyyy')='"+filedate+"' ", Integer.class);
	
	float acq_charge_dtls_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where  to_date(filedate,'dd/mon/yyyy')='"+filedate+"'  ", Integer.class);
	
	/*select distinct sum(nvl(ADJCUSTPENALTY,0)) from CASHNET_CHARGEBACK_HDR

	select distinct sum(nvl(ADJCUSTPENALTY,0)) from CASHNET_CHARGEBACK_ACQUIER*/
	float  ISS_PENALTY = getJdbcTemplate().queryForObject("select sum(to_number(ADJCUSTPENALTY)) from CASHNET_CHARGEBACK where  to_date(filedate,'dd/mon/yyyy')='"+filedate+"'  ", Integer.class);
	
	float  ACQ_PENALTY = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where  to_date(filedate,'dd/mon/yyyy') ='"+filedate+"'  ", Integer.class);
	
	float credt_adj_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(TXN_AMOUNT)),0) - nvl(sum(to_number(ADJ_AMOUNT)),0)  from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and    ADJ_TYPE ='CREDIT' ", Integer.class);
	
	float  credt_adj_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and    ADJ_TYPE ='CREDIT'", Integer.class);
	
   float Debit_adj_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and    ADJ_TYPE ='DEBIT'", Integer.class);
	
	float  Debit_adj_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and    ADJ_TYPE ='DEBIT' ", Integer.class);
	
	float PRE_ARBITRATION_amt=0;
	try{
	 PRE_ARBITRATION_amt = getJdbcTemplate().queryForObject("select to_number(nvl(REPRSTMT_RECV_AMT,0)) - to_number(nvl(PRE_ARB_ADJ_RECV_AMT,0)) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"'  and ADJ_TYPE = 'PRE-ARBITRATION'  ", Integer.class);
	}catch (Exception ex) {
		
		
	}
	float  PRE_ARBITRATION_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"' and  ADJ_TYPE ='PRE-ARBITRATION'  ", Integer.class);
	
	float REPRESENTMENT_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"' and    ADJ_TYPE ='REPRESENTMENT'", Integer.class);
	
	float  REPRESENTMENT_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"' and   ADJ_TYPE ='REPRESENTMENT' ", Integer.class);
	
    float PRE_ARBITRATION_DEC_amt = getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"' and  ADJ_TYPE = 'PreArbitration Reject'", Integer.class);
	
	float  PRE_ARBITRATION_DEC_cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK where CATEGORY='CASHNET' and to_date(filedate,'dd/mon/yyyy') ='"+filedate+"' and  ADJ_TYPE = 'PreArbitration Reject'  ", Integer.class);
	
	float	Acq_Pre_arb_Details_Amt =  getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and   ADJ_TYPE = 'PRE-ARBITRATION'  ", Integer.class); ;
	float	Acq_Pre_arb_Details_Cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and     ADJ_TYPE = 'PRE-ARBITRATION' ", Integer.class);;
	
	
	float	Acq_represnment_Amt =  getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and   ADJ_TYPE = 'REPRESENTMENT'  ", Integer.class); ;
	float	Acq_represnment_Cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and     ADJ_TYPE = 'REPRESENTMENT' ", Integer.class);;
	
	//CHARGEBACK
	
	float	Acq_CHARGEBACK_Amt =  getJdbcTemplate().queryForObject("select nvl(sum(to_number(ADJ_AMOUNT)),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and   ADJ_TYPE = 'CHARGEBACK'  ", Integer.class); ;
	float	Acq_CHARGEBACK_Cnt = getJdbcTemplate().queryForObject("select nvl(count(*),0) from CASHNET_CHARGEBACK_ACQUIER where NETWORK='CASHNET' and to_date(ADJ_DATE,'dd/mm/yyyy') ='"+filedate+"' and     ADJ_TYPE = 'CHARGEBACK' ", Integer.class);;
	
	
	
	finalBean.setAcq_Repr_Dtls_Amt(String.valueOf(Acq_represnment_Amt));
	finalBean.setAcq_Repr_Dtls_Cnt(String.valueOf(Acq_represnment_Cnt));
	
	finalBean.setAcq_Charg_Dtls_Amt(String.valueOf(Acq_CHARGEBACK_Amt));
	finalBean.setAcq_Charg_Dtls_Cnt(String.valueOf(Acq_CHARGEBACK_Cnt));
	
	
	
	
	
	
	finalBean.setAcq_Pre_Arb_Dec_Dtls_Amt(String.valueOf(Acq_Pre_arb_Details_Amt));
	
	finalBean.setAcq_Pre_Arb_Dec_Dtls_Cnt(String.valueOf(Acq_Pre_arb_Details_Cnt));
	
	finalBean.setIss_Pre_Arb_Dec_Dtls_Amt(String.valueOf(PRE_ARBITRATION_DEC_amt));
	finalBean.setIss_Pre_Arb_Dec_Dtls_Cnt(String.valueOf(PRE_ARBITRATION_DEC_cnt));
	
	finalBean.setAcq_Charg_Dtls_Amt(String.valueOf(acq_charge_dtls_amt));
	
	finalBean.setAcq_Charg_Dtls_Cnt(String.valueOf(acq_charge_dtls_cnt));
	
	finalBean.setIss_Charge_Dtls_Cnt(String.valueOf(iss_charge_dtls_cnt));
	
	finalBean.setIss_Charge_Dtls_Amt(String.valueOf(iss_charge_dtls_amt));
		
	finalBean.setIss_Cre_Adj_Dtls_Amt(String.valueOf(credt_adj_amt));
	
	finalBean.setIss_Cre_Adj_Dtls_Cnt(String.valueOf(credt_adj_cnt));
	
	finalBean.setIss_Pre_arb_Dtls_Amt(String.valueOf(PRE_ARBITRATION_amt));
	
	finalBean.setIss_Pre_arb_Dtls_Cnt(String.valueOf(PRE_ARBITRATION_cnt));
	
	finalBean.setIss_Repr_Dtls_Amt(String.valueOf(REPRESENTMENT_amt));
	
	finalBean.setIss_Repr_Dtls_Cnt(String.valueOf(REPRESENTMENT_cnt));
	
   finalBean.setIss_Debit_Adj_Dtls_Amt((String.valueOf(Debit_adj_amt)));
	
	finalBean.setIss_Debit_Adj_Dtls_Cnt(String.valueOf(Debit_adj_cnt));
	
	finalBean.setIss_Repr_Dtls_Amt(String.valueOf(REPRESENTMENT_amt));
	
	finalBean.setIss_Repr_Dtls_Cnt(String.valueOf(REPRESENTMENT_cnt));
	
	finalBean.setIss_penalty(String.valueOf(ISS_PENALTY));
	
	finalBean.setAcq_penalty(String.valueOf(ACQ_PENALTY));
	
	return finalBean;
}


public Settlement_FinalBean setData(ArrayList<Settle_final_report_condn_bean> condn_beans, String filedate ) {
	
	Settlement_FinalBean finalBean = new Settlement_FinalBean();
	
	try{
		String count_query ="";
		String Amount_query ="";
		String count ="";
		String Amount ="";
			for(Settle_final_report_condn_bean condn_bean :condn_beans) {
				count="";
				Amount="";
				
				if(condn_bean.getReport_col().contains("Iss")) {
				 count_query ="select Count(*) from cashnet_cashnet_iss_rawdata where filedate='"+filedate+"' "
						+ " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee "
						+ " WHERE networktype = '"+condn_bean.getNetworktype()+"' AND ttype = '"+condn_bean.getTTYPE()+"')"
						+ " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='"+condn_bean.getRE_Networktype()+"'  and ttype like '%"+condn_bean.getRE_TTYPE()+"')"
						+ " AND from_account_type in ('00','01','02') ";
				
				System.out.println("count_query"+count_query);
				
				 count = getJdbcTemplate().queryForObject( count_query ,String.class) ;
				
				
				 Amount_query = "select nvl(sum(to_number(TRANSACTION_AMOUNT)),0) from cashnet_cashnet_iss_rawdata where filedate='"+filedate+"' "
						+ " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee "
						+ " WHERE networktype = '"+condn_bean.getNetworktype()+"' AND ttype = '"+condn_bean.getTTYPE()+"')"
						+ " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='"+condn_bean.getRE_Networktype()+"'  and ttype like '%"+condn_bean.getRE_TTYPE()+"')"
						+ " AND from_account_type in ('00','01','02') ";
				System.out.println(Amount_query+"Amount_query"); 
				
				 Amount = getJdbcTemplate().queryForObject( Amount_query ,String.class) ;
				}
			 else {
				
				if(condn_bean.getReport_col().contains("PC_")) {
					
					count_query ="select Count(*) from cashnet_cashnet_ACQ_rawdata where filedate='"+filedate+"' "
							+ " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee "
							+ " WHERE networktype = '"+condn_bean.getNetworktype()+"' AND ttype = '"+condn_bean.getTTYPE()+"')"
							+ " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='"+condn_bean.getRE_Networktype()+"'  and ttype like '%"+condn_bean.getRE_TTYPE()+"') ";
					
					System.out.println("count_query"+count_query);
					
				}
					
				else {	count_query ="select Count(*) from cashnet_cashnet_ACQ_rawdata where filedate='"+filedate+"' "
						+ " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee "
						+ " WHERE networktype = '"+condn_bean.getNetworktype()+"' AND ttype = '"+condn_bean.getTTYPE()+"')"
						+ " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='"+condn_bean.getRE_Networktype()+"'  and ttype like '%"+condn_bean.getRE_TTYPE()+"')"
						+ " AND from_account_type in ("+condn_bean.getAccount_type()+") ";
				
				System.out.println("count_query"+count_query);
				}
				
				 count = getJdbcTemplate().queryForObject( count_query ,String.class) ;
				
				
				 Amount_query = "select nvl(sum(to_number(TXN_AMOUNT)),0) from cashnet_cashnet_ACQ_rawdata where filedate='"+filedate+"' "
						+ " and transaction_type IN ( SELECT ttypecode FROM atm_interchange_fee "
						+ " WHERE networktype = '"+condn_bean.getNetworktype()+"' AND ttype = '"+condn_bean.getTTYPE()+"')"
						+ " AND response_code in (SELECT ttypecode from ATM_INTERCHANGE_FEE where NETWORKTYPE ='"+condn_bean.getRE_Networktype()+"'  and ttype like '%"+condn_bean.getRE_TTYPE()+"')"
						+ " AND from_account_type in ("+condn_bean.getAccount_type()+") ";
				System.out.println(Amount_query+"Amount_query"); 
				
				 Amount = getJdbcTemplate().queryForObject( Amount_query ,String.class) ;
			 }
			
				
				switch (condn_bean.getReport_col()) {
				case "Iss_BI_App_Fee":
					finalBean.setIss_BI_App_Fee_Amt(String.valueOf(Integer.parseInt(count)*5));
					finalBean.setIss_BI_App_Fee_Cnt(count);
				break;
				case "Iss_BI_Dec_App_Fee":
					finalBean.setIss_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setIss_BI_Dec_App_Fee_Cnt(count);
				break;
				case "Iss_MS_App_Fee" :
					
					finalBean.setIss_MS_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setIss_MS_App_Fee_Cnt(count);
				break;
				
				case "Iss_MS_Dec_App_Fee" :
				
					finalBean.setIss_MS_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
				finalBean.setIss_MS_Dec_App_Fee_Cnt(count);
				
				break;
				case "Iss_PC_App_Fee" :
					
					finalBean.setIss_PC_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setIss_PC_App_Fee_Cnt(count);
					
				break;
				
				case "Iss_PC_Dec_App_Fee" :
					
					finalBean.setIss_PC_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setIss_PC_Dec_App_Fee_Cnt(count);
					
				break;
				
				case "Cash_Sha_Net_Iss_Sett" :
					
					finalBean.setCash_Sha_Net_Iss_Sett_Amt(Amount);
					finalBean.setCash_Sha_Net_Iss_Sett_Cnt(count);
					
				break;
				
				case "Iss_WDL_Dec_App_Fee" :
					///change
					
					
					int wdl_fee_amt = Integer.parseInt(count)*5;
					
					Integer wdl_cbk_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE),0) from  CASHNET_CHARGEBACK where to_date(FIlEdate,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
					
					/*Integer wdl_rep_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE),0) from  CASHNET_CHARGEBACK_HDR where to_date(REPRESENTMENT_DATE,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
					
					Integer wdl_prearb_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE),0) from  CASHNET_CHARGEBACK_HDR where to_date(PRE_ARBITRATION_DATE,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
					*/
					
					Integer wdl_chargeamout  = wdl_cbk_amt+wdl_fee_amt;
					
					finalBean.setIss_WDL_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(wdl_chargeamout))));
					System.out.println("Iss_WDL_Dec_App_Fee" + count);
					
					finalBean.setIss_WDL_Dec_App_Fee_Cnt(count);
					
				break;
				
				case "Acq_BI_App_Fee" :
					finalBean.setAcq_BI_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_BI_App_Fee_Cnt(count);
					
				break;
				
				case "Acq_BI_Dec_Fee" :
					finalBean.setAcq_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_BI_Dec_App_Fee_Cnt(count);
					
				break;
				
				case "Acq_MS_Appr_Fee" :
					finalBean.setAcq_MS_Appr_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_MS_Appr_Fee_Cnt(count);
					
				break;
				
				case "Acq_MS_Dec_App_Fee" :
					finalBean.setAcq_MS_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_MS_Dec_App_Fee_Cnt(count);
					
				break;
				
				case "Acq_PC_App_Fee" :
					finalBean.setAcq_PC_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_PC_App_Fee_Cnt(count);
					
				break;
					
				case "Acq_PC_Dec_App_Fee" :
					finalBean.setAcq_PC_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setAcq_PC_Dec_App_Fee_Cnt(count);
					
				break;
				
				case "CCard_Acq_WDL_App_Fee" :
					finalBean.setCCard_Acq_WDL_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*30))));
					finalBean.setCCard_Acq_WDL_App_Fee_Cnt(count);
					
				break;
				
				case "CC_Acq_WDL_Dec_App_Fee" :
					finalBean.setCC_Acq_WDL_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setCC_Acq_WDL_Dec_App_Fee_Cnt(count);
					
				break;
				case "Acq_WDL_Dec_App_Fee" :
					
					//Changes to be done
					
					Integer cbk_amount = getJdbcTemplate().queryForObject("select nvl(sum(ACQ_FEE),0) from CASHNET_CHARGEBACK_ACQUIER where to_date(FILEDATE,'dd/mon/yyyy') = '"+filedate+"' ", Integer.class);
					
					Integer total_amout =( Integer.parseInt(count)*5)+ (cbk_amount);
					
					finalBean.setAcq_WDL_Dec_App_Fee_Amt(String.valueOf(total_amout));
					finalBean.setAcq_WDL_Dec_App_Fee_Cnt(count);
					
				break;
				case "CCard_Acq_BI_Dec_App_Fee" :
					finalBean.setCCard_Acq_BI_Dec_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setCCard_Acq_BI_Dec_App_Fee_Cnt(count);
					
				break;
				case "CCard_Acq_BI_App_Fee" :
					System.out.println("CCard_Acq_BI_App_Fee" +String.valueOf(Integer.parseInt(count)*5) + count);
					
					
					finalBean.setCCard_Acq_BI_App_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Integer.parseInt(count)*5))));
					finalBean.setCCard_Acq_BI_App_Fee_Cnt(count);
					
				break;
				default:
					break;
				}
				
			}
			
			
			
			count_query =" select count(*) from cashnet_cashnet_acq_rawdata where filedate='"+filedate+"' and response_code in ('00','26') "+
					 "and to_number(ACQ_SETTLE_AMNT) >0 "+
					 "and  (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')";
			
			String Cash_Sha_Net_Acq_Sett_Cnt = getJdbcTemplate().queryForObject( count_query ,String.class) ;
			
			
			 Amount_query = "select sum(to_number(TXN_AMOUNT)) from cashnet_cashnet_acq_rawdata where filedate='"+filedate+"' and response_code in ('00','26') "+
					 "and to_number(ACQ_SETTLE_AMNT) >0 "+
					 "and  (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')";
			
			String Cash_Sha_Net_Acq_Sett_amt = getJdbcTemplate().queryForObject( Amount_query ,String.class) ;
			
			
			
	
			int Fee_amount  = Integer.parseInt(finalBean.getIss_BI_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_BI_Dec_App_Fee_Cnt()) +
					Integer.parseInt(finalBean.getIss_MS_App_Fee_Cnt())+Integer.parseInt(finalBean.getIss_MS_Dec_App_Fee_Cnt())  + Integer.parseInt(finalBean.getIss_PC_App_Fee_Cnt()) 
					+ Integer.parseInt(finalBean.getIss_PC_Dec_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_WDL_Dec_App_Fee_Cnt());
			
			System.out.println("Fee_amount"+Fee_amount);
			
			int Fee_cnt = Integer.parseInt(finalBean.getIss_BI_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_BI_Dec_App_Fee_Cnt()) +
					Integer.parseInt(finalBean.getIss_MS_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_PC_App_Fee_Cnt()) 
					+ Integer.parseInt(finalBean.getIss_PC_Dec_App_Fee_Cnt()) + Integer.parseInt(finalBean.getIss_WDL_Dec_App_Fee_Cnt());
			
			/*int Cash_Sha_net_Swt_Exp_Amt =  Integer.parseInt(finalBean.getCash_Sha_Net_Iss_Sett_Amt()) - Integer.parseInt(finalBean.getAcq_Pre_arb_Details_Amt()) - Integer.parseInt(finalBean.getIss_Pre_Arb_Dec_Dtls_Amt())+
											Integer.parseInt(finalBean.getAcq_Charg_Dtls_Amt())+ Integer.parseInt(finalBean.getIss_Repr_Dtls_Amt())		  ;
			*/
			int Cash_Sha_net_Swt_Exp_cnt = Integer.parseInt(finalBean.getCash_Sha_Net_Iss_Sett_Cnt()) - Integer.parseInt(finalBean.getAcq_Pre_arb_Details_Cnt()) - Integer.parseInt(finalBean.getIss_Pre_Arb_Dec_Dtls_Cnt())+
					Integer.parseInt(finalBean.getAcq_Charg_Dtls_Cnt())+ Integer.parseInt(finalBean.getIss_Repr_Dtls_Cnt())		  ;
			
			
			
			finalBean.setIss_WDL_Approved_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(Cash_Sha_net_Swt_Exp_cnt*15))));
			
			finalBean.setIss_WDL_Approved_Fee_Cnt(String.valueOf(Cash_Sha_net_Swt_Exp_cnt));
			
			System.out.println("Cash_Sha_net_Swt_Exp_cnt"+Cash_Sha_net_Swt_Exp_cnt);
			
			
			///Acquirer withdrawl fee start
			Integer Acq_total_All_cnt = getJdbcTemplate().queryForObject("select count(*) from  cashnet_cashnet_acq_rawdata where filedate='"+filedate+"'and (TRANSACTION_TYPE ='61' OR TRANSACTION_TYPE ='89' OR  TRANSACTION_TYPE ='29' OR TRANSACTION_TYPE ='04' OR TRANSACTION_TYPE ='88')" 
                    + "AND RESPONSE_CODE in('00')  AND TXN_AMOUNT>0 " ,Integer.class );

			/*Integer acq_cbkcnt = getJdbcTemplate().queryForObject("select count(*) from CASHNET_CHARGEBACK_ACQUIER where to_date(CHARGEBACK_DATE,'dd/mon/yyyy') ='"+filedate+"' ", Integer.class);
			Integer acq_repcnt = getJdbcTemplate().queryForObject("select count(*) from CASHNET_CHARGEBACK_ACQUIER where to_date(REPRESENTMENT_DATE,'dd/mon/yyyy') ='"+filedate+"' ", Integer.class);
			Integer acq_prearbcnt = getJdbcTemplate().queryForObject("select count(*) from CASHNET_CHARGEBACK_ACQUIER where to_date(PRE_ARBITRATION_DATE,'dd/mon/yyyy') ='"+filedate+"' ", Integer.class);
			Integer acq_prearbrepcnt = getJdbcTemplate().queryForObject("select count(*) from CASHNET_CHARGEBACK_ACQUIER where to_date(PRE_ARBITRATION_REJECT_DATE,'dd/mon/yyyy') ='"+filedate+"' ", Integer.class);
*/
			Integer acq_wdl_cnt = Acq_total_All_cnt- Integer.parseInt(finalBean.getCCard_Acq_WDL_App_Fee_Cnt());
			finalBean.setAcq_WDL_Appr_Fee_Cnt(String.valueOf(acq_wdl_cnt));
			finalBean.setAcq_WDL_Appr_Fee_Amt(String.valueOf(Float.parseFloat(String.valueOf(acq_wdl_cnt*15))));
			
			
			//Acquirer wiothdrawl end
			
			
			
			
			
			Integer 	cbk_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE_SW),0) from  CASHNET_CHARGEBACK where to_date(FILEDATE,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
			
		/*	Integer 	rep_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE_SW),0) from  CASHNET_CHARGEBACK where to_date(REPRESENTMENT_DATE,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
			
			Integer 	prearb_amt = getJdbcTemplate().queryForObject( "select nvl(sum(ISS_FEE_SW),0) from  CASHNET_CHARGEBACK where to_date(PRE_ARBITRATION_DATE,'dd/mon/yyyy')='"+filedate+"'" ,Integer.class) ;
			
			*/
			System.out.println(cbk_amt);
			
			/*System.out.println(rep_amt);
			
			System.out.println(prearb_amt);*/
			
			int chargeamout  = cbk_amt+Fee_amount+Cash_Sha_net_Swt_Exp_cnt;
			
			System.out.println(chargeamout);
			
		
			
			finalBean.setCash_Sha_net_Swt_Exp_Amt(String.valueOf(Float.parseFloat(String.valueOf(chargeamout))));
			
			finalBean.setCash_Sha_net_Swt_Exp_Cnt(String.valueOf(chargeamout));
			
			// Acquirer 
			
			finalBean.setCash_Sha_Net_Acq_Sett_Cnt(Cash_Sha_Net_Acq_Sett_Cnt);
			
			finalBean.setCash_Sha_Net_Acq_Sett_Amt(String.valueOf(Float.parseFloat(Cash_Sha_Net_Acq_Sett_amt)));
			
			Float cash_sha_net_acq_sett_cnt = Float.parseFloat(finalBean.getAcq_BI_App_Fee_Cnt()) +Float.parseFloat(finalBean.getAcq_BI_Dec_App_Fee_Cnt()) 
					+Float.parseFloat(finalBean.getAcq_MS_Appr_Fee_Cnt())+Float.parseFloat(finalBean.getAcq_MS_Dec_App_Fee_Cnt())+
					Float.parseFloat(finalBean.getAcq_PC_App_Fee_Cnt()) + Float.parseFloat(finalBean.getAcq_PC_Dec_App_Fee_Cnt())+
					+ Float.parseFloat(finalBean.getCCard_Acq_WDL_App_Fee_Cnt())+ Float.parseFloat(finalBean.getAcq_WDL_Dec_App_Fee_Cnt())+
					Float.parseFloat(finalBean.getCC_Acq_WDL_Dec_App_Fee_Cnt()) + Float.parseFloat(finalBean.getCCard_Acq_BI_Dec_App_Fee_Cnt())+
					Float.parseFloat(finalBean.getCCard_Acq_BI_App_Fee_Cnt()  );
			
			Float cash_sha_net_acq_sett_amt = Float.parseFloat(finalBean.getAcq_BI_App_Fee_Amt()) +Float.parseFloat(finalBean.getAcq_BI_Dec_App_Fee_Amt()) 
					+Float.parseFloat(finalBean.getAcq_MS_Appr_Fee_Amt())+Float.parseFloat(finalBean.getAcq_MS_Dec_App_Fee_Amt())+
					Float.parseFloat(finalBean.getAcq_PC_App_Fee_Amt()) + Float.parseFloat(finalBean.getAcq_PC_Dec_App_Fee_Amt())+
					+ Float.parseFloat(finalBean.getCCard_Acq_WDL_App_Fee_Amt())+ Float.parseFloat(finalBean.getAcq_WDL_Dec_App_Fee_Amt())+
					Float.parseFloat(finalBean.getCC_Acq_WDL_Dec_App_Fee_Amt()) + Float.parseFloat(finalBean.getCCard_Acq_BI_Dec_App_Fee_Amt())+
					Float.parseFloat(finalBean.getCCard_Acq_BI_App_Fee_Amt() );

	
			finalBean.setCash_Sha_Network_Serv_Tax_Cnt(String.valueOf(cash_sha_net_acq_sett_cnt+ Integer.parseInt(finalBean.getAcq_WDL_Appr_Fee_Cnt())));
			
			System.out.println();
			
			Float sha_amt = (float) ((cash_sha_net_acq_sett_amt + Float.parseFloat(finalBean.getAcq_WDL_Appr_Fee_Amt()))  * 0.18);
			finalBean.setCash_Sha_Network_Serv_Tax_Amt(String.valueOf(sha_amt));
	
			finalBean.setTDS_on_Iss_Swtch_Exp_Cnt(finalBean.getCash_Sha_net_Swt_Exp_Cnt());
			
			//double Exp_amt = Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt())*0.01;
			double Exp_amt = Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt())*0.015;
				
			System.out.println("TDS_amount"+Exp_amt);
			finalBean.setTDS_on_Iss_Swtch_Exp_Amt(String.format("%.2f", Exp_amt));
			
			finalBean.setCash_Sha_net_Swt_Exp_Amt(String.valueOf(Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt())*1));
			
			
			
			float Gst_amt = Float.parseFloat(finalBean.getIss_BI_App_Fee_Amt()) + Float.parseFloat(finalBean.getIss_BI_Dec_App_Fee_Amt()) +
					Float.parseFloat((finalBean.getIss_MS_App_Fee_Amt())) + Float.parseFloat(finalBean.getIss_MS_Dec_App_Fee_Amt())+Float.parseFloat(finalBean.getIss_PC_App_Fee_Amt())
					+ Float.parseFloat(finalBean.getIss_PC_Dec_App_Fee_Amt())+ Float.parseFloat(finalBean.getIss_WDL_Approved_Fee_Amt())+ Float.parseFloat(finalBean.getIss_WDL_Dec_App_Fee_Amt())
					+ Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Amt());
			
			System.out.println("Gst_amt"+Gst_amt);
			
			finalBean.setCash_ATM_inter_Paid_GST_Amt(String.valueOf(Float.parseFloat(String.valueOf(Gst_amt*0.18))));
			
			finalBean.setCash_ATM_inter_Paid_GST_Cnt(String.valueOf(Float.parseFloat(finalBean.getIss_BI_App_Fee_Cnt()) + Float.parseFloat(finalBean.getIss_BI_Dec_App_Fee_Cnt()) +
					Float.parseFloat((finalBean.getIss_MS_App_Fee_Cnt())) + Float.parseFloat(finalBean.getIss_MS_Dec_App_Fee_Cnt())+Float.parseFloat(finalBean.getIss_PC_App_Fee_Cnt())
					+ Float.parseFloat(finalBean.getIss_PC_Dec_App_Fee_Cnt())+ Float.parseFloat(finalBean.getIss_WDL_Approved_Fee_Cnt())+ Float.parseFloat(finalBean.getIss_WDL_Dec_App_Fee_Cnt())
					+ Float.parseFloat(finalBean.getCash_Sha_net_Swt_Exp_Cnt())));
			
			
	
	return finalBean;
	}catch(Exception ex) {
		
		ex.printStackTrace();
		return finalBean;
	}
	
	
	
	
	
}


class GenerateCashnetSettlement extends StoredProcedure {
	private static final String procName = "Generate_Cashnet_Settl_report";

	GenerateCashnetSettlement(JdbcTemplate JdbcTemplate) {
		super(JdbcTemplate, procName);
		setFunction(false);

		declareParameter(new SqlParameter("I_FILEDATE",
				Types.VARCHAR));
		declareParameter(new SqlParameter("NET_SETTL_AMT",
				Types.VARCHAR));
		declareParameter(new SqlParameter("MAN_Iss_Repr_Dtls_Amt", Types.VARCHAR));
		declareParameter(new SqlParameter("MAN_Acq_Repr_Dtls_Amt",
				Types.VARCHAR));
		declareParameter(new SqlParameter("User_ID", Types.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_MSG",
				Types.VARCHAR));
		/*declareParameter(new SqlOutParameter("ERROR_MESSAGE",
				OracleTypes.VARCHAR));*/
		compile();
	}
}



public class Settlement_FinalBeanRowMapper implements RowMapper<Settlement_FinalBean> {

    @Override
    public Settlement_FinalBean mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	Settlement_FinalBean finalbean = new Settlement_FinalBean();
    	finalbean.setAcq_BI_App_Fee_Cnt(rs.getString("ACQ_BI_APP_FEE_CNT")!= null ? rs.getString("ACQ_BI_APP_FEE_CNT"):"00" );
    	finalbean.setAcq_BI_App_Fee_Amt(rs.getString("ACQ_BI_APP_FEE_AMT") != null ? rs.getString("ACQ_BI_APP_FEE_AMT"):"00");
    	finalbean.setAcq_BI_Dec_App_Fee_Cnt(rs.getString("ACQ_BI_DEC_APP_FEE_CNT") != null ? rs.getString("ACQ_BI_DEC_APP_FEE_CNT"):"00");
    	finalbean.setAcq_BI_Dec_App_Fee_Amt(rs.getString("ACQ_BI_DEC_APP_FEE_AMT") != null ? rs.getString("ACQ_BI_DEC_APP_FEE_AMT"):"00");
    	finalbean.setAcq_MS_Appr_Fee_Amt(rs.getString("ACQ_MS_APPR_FEE_AMT") != null ? rs.getString("ACQ_MS_APPR_FEE_AMT"):"00");
    	finalbean.setAcq_MS_Appr_Fee_Cnt(rs.getString("ACQ_MS_APPR_FEE_CNT") != null ? rs.getString("ACQ_MS_APPR_FEE_CNT"):"00");
    	finalbean.setAcq_MS_Dec_App_Fee_Cnt(rs.getString("ACQ_MS_DEC_APP_FEE_CNT") != null ? rs.getString("ACQ_MS_DEC_APP_FEE_CNT"):"00");
    	finalbean.setAcq_MS_Dec_App_Fee_Amt(rs.getString("ACQ_MS_DEC_APP_FEE_AMT") != null ? rs.getString("ACQ_MS_DEC_APP_FEE_AMT"):"00");
    	finalbean.setAcq_PC_App_Fee_Cnt(rs.getString("ACQ_PC_APP_FEE_CNT") != null ? rs.getString("ACQ_PC_APP_FEE_CNT"):"00");
    	finalbean.setAcq_PC_App_Fee_Amt(rs.getString("ACQ_PC_APP_FEE_AMT") != null ? rs.getString("ACQ_PC_APP_FEE_AMT"):"00");
    	finalbean.setAcq_PC_Dec_App_Fee_Cnt(rs.getString("ACQ_PC_DEC_APP_FEE_CNT") != null ? rs.getString("ACQ_PC_DEC_APP_FEE_CNT"):"00");
    	finalbean.setAcq_PC_Dec_App_Fee_Amt(rs.getString("ACQ_PC_DEC_APP_FEE_AMT") != null ? rs.getString("ACQ_PC_DEC_APP_FEE_AMT"):"00");
    	finalbean.setCCard_Acq_WDL_App_Fee_Cnt(rs.getString("CCARD_ACQ_WDL_APP_FEE_CNT") != null ? rs.getString("CCARD_ACQ_WDL_APP_FEE_CNT"):"00");
    	finalbean.setCCard_Acq_WDL_App_Fee_Amt(rs.getString("CCARD_ACQ_WDL_APP_FEE_AMT") != null ? rs.getString("CCARD_ACQ_WDL_APP_FEE_AMT"):"00");
    	finalbean.setAcq_WDL_Dec_App_Fee_Amt(rs.getString("ACQ_WDL_DEC_APP_FEE_AMT") != null ? rs.getString("ACQ_WDL_DEC_APP_FEE_AMT"):"00");
    	finalbean.setAcq_WDL_Dec_App_Fee_Cnt(rs.getString("ACQ_WDL_DEC_APP_FEE_CNT") != null ? rs.getString("ACQ_WDL_DEC_APP_FEE_CNT"):"00");
    	finalbean.setCC_Acq_WDL_Dec_App_Fee_Amt(rs.getString("CC_ACQ_WDL_DEC_APP_FEE_AMT") != null ? rs.getString("CC_ACQ_WDL_DEC_APP_FEE_AMT"):"00");
    	finalbean.setCC_Acq_WDL_Dec_App_Fee_Cnt(rs.getString("CC_ACQ_WDL_DEC_APP_FEE_CNT") != null ? rs.getString("CC_ACQ_WDL_DEC_APP_FEE_CNT"):"00");
    	finalbean.setCCard_Acq_BI_Dec_App_Fee_Amt(rs.getString("CCARD_ACQ_BI_DEC_APP_FEE_AMT") != null ? rs.getString("CCARD_ACQ_BI_DEC_APP_FEE_AMT"):"00");
    	finalbean.setCCard_Acq_BI_Dec_App_Fee_Cnt(rs.getString("CCARD_ACQ_BI_DEC_APP_FEE_CNT") != null ? rs.getString("CCARD_ACQ_BI_DEC_APP_FEE_CNT"):"00");
    	finalbean.setCCard_Acq_BI_App_Fee_Amt(rs.getString("CCARD_ACQ_BI_APP_FEE_AMT") != null ? rs.getString("CCARD_ACQ_BI_APP_FEE_AMT"):"00");
    	finalbean.setCCard_Acq_BI_App_Fee_Cnt(rs.getString("CCARD_ACQ_BI_APP_FEE_CNT") != null ? rs.getString("CCARD_ACQ_BI_APP_FEE_CNT"):"00");
    	finalbean.setIss_BI_App_Fee_Amt(rs.getString("ISS_BI_APP_FEE_AMT") != null ? rs.getString("ISS_BI_APP_FEE_AMT"):"00");
    	finalbean.setIss_BI_App_Fee_Cnt(rs.getString("ISS_BI_APP_FEE_CNT") != null ? rs.getString("ISS_BI_APP_FEE_CNT"):"00");
    	finalbean.setIss_BI_Dec_App_Fee_Amt(rs.getString("ISS_BI_DEC_APP_FEE_AMT") != null ? rs.getString("ISS_BI_DEC_APP_FEE_AMT"):"00");
    	finalbean.setIss_BI_Dec_App_Fee_Cnt(rs.getString("ISS_BI_DEC_APP_FEE_CNT") != null ? rs.getString("ISS_BI_DEC_APP_FEE_CNT"):"00");
    	finalbean.setIss_MS_App_Fee_Cnt(rs.getString("ISS_MS_APP_FEE_CNT") != null ? rs.getString("ISS_MS_APP_FEE_CNT"):"00");
    	finalbean.setIss_MS_App_Fee_Amt(rs.getString("ISS_MS_APP_FEE_AMT") != null ? rs.getString("ISS_MS_APP_FEE_AMT"):"00");
    	finalbean.setIss_MS_Dec_App_Fee_Amt(rs.getString("ISS_MS_DEC_APP_FEE_AMT") != null ? rs.getString("ISS_MS_DEC_APP_FEE_AMT"):"00");
    	finalbean.setIss_MS_Dec_App_Fee_Cnt(rs.getString("ISS_MS_DEC_APP_FEE_CNT") != null ? rs.getString("ISS_MS_DEC_APP_FEE_CNT"):"00");
    	finalbean.setIss_PC_App_Fee_Amt(rs.getString("ISS_PC_APP_FEE_AMT") != null ? rs.getString("ISS_PC_APP_FEE_AMT"):"00");
    	finalbean.setIss_PC_App_Fee_Cnt(rs.getString("ISS_PC_APP_FEE_CNT") != null ? rs.getString("ISS_PC_APP_FEE_CNT"):"00");
    	finalbean.setIss_PC_Dec_App_Fee_Amt(rs.getString("ISS_PC_DEC_APP_FEE_AMT") != null ? rs.getString("ISS_PC_DEC_APP_FEE_AMT"):"00");
    	finalbean.setIss_PC_Dec_App_Fee_Cnt(rs.getString("ISS_PC_DEC_APP_FEE_CNT") != null ? rs.getString("ISS_PC_DEC_APP_FEE_CNT"):"00");
    	finalbean.setIss_WDL_Dec_App_Fee_Amt(rs.getString("ISS_WDL_DEC_APP_FEE_AMT") != null ? rs.getString("ISS_WDL_DEC_APP_FEE_AMT"):"00");
    	finalbean.setIss_WDL_Dec_App_Fee_Cnt(rs.getString("ISS_WDL_DEC_APP_FEE_CNT") != null ? rs.getString("ISS_WDL_DEC_APP_FEE_CNT"):"00");
    	finalbean.setAcq_Pre_arb_Details_Amt(rs.getString("ACQ_PRE_ARB_DETAILS_AMT") != null ? rs.getString("ACQ_PRE_ARB_DETAILS_AMT"):"00");
    	finalbean.setAcq_Pre_arb_Details_Cnt(rs.getString("ACQ_PRE_ARB_DETAILS_CNT") != null ? rs.getString("ACQ_PRE_ARB_DETAILS_CNT"):"00");
    	finalbean.setIss_Pre_Arb_Dec_Dtls_Amt(rs.getString("ISS_PRE_ARB_DEC_DTLS_AMT") != null ? rs.getString("ISS_PRE_ARB_DEC_DTLS_AMT"):"00");
    	finalbean.setIss_Pre_Arb_Dec_Dtls_Cnt(rs.getString("ISS_PRE_ARB_DEC_DTLS_CNT") != null ? rs.getString("ISS_PRE_ARB_DEC_DTLS_CNT"):"00");
    	finalbean.setAcq_Charg_Dtls_Amt(rs.getString("ACQ_CHARG_DTLS_AMT") != null ? rs.getString("ACQ_CHARG_DTLS_AMT"):"00");
    	finalbean.setAcq_Charg_Dtls_Cnt(rs.getString("ACQ_CHARG_DTLS_CNT") != null ? rs.getString("ACQ_CHARG_DTLS_CNT"):"00");
    	finalbean.setAcq_Cred_Adj_Dtls_Amt(rs.getString("ACQ_CRED_ADJ_DTLS_AMT") != null ? rs.getString("ACQ_CRED_ADJ_DTLS_AMT"):"00");
    	finalbean.setAcq_Cred_Adj_Dtls_Cnt(rs.getString("ACQ_CRED_ADJ_DTLS_CNT") != null ? rs.getString("ACQ_CRED_ADJ_DTLS_CNT"):"00");
    	finalbean.setAcq_Repr_Dtls_Amt(rs.getString("ACQ_REPR_DTLS_AMT") != null ? rs.getString("ACQ_REPR_DTLS_AMT"):"00");
    	finalbean.setAcq_Repr_Dtls_Cnt(rs.getString("ACQ_REPR_DTLS_CNT") != null ? rs.getString("ACQ_REPR_DTLS_CNT"):"00");
    	finalbean.setAcq_Pre_Arb_Dec_Dtls_Amt(rs.getString("ACQ_PRE_ARB_DEC_DTLS_AMT") != null ? rs.getString("ACQ_PRE_ARB_DEC_DTLS_AMT"):"00");
    	finalbean.setAcq_Pre_Arb_Dec_Dtls_Cnt(rs.getString("ACQ_PRE_ARB_DEC_DTLS_CNT") != null ? rs.getString("ACQ_PRE_ARB_DEC_DTLS_CNT"):"00");
    	finalbean.setIss_Cre_Adj_Dtls_Amt(rs.getString("ISS_CRE_ADJ_DTLS_AMT") != null ? rs.getString("ISS_CRE_ADJ_DTLS_AMT"):"00");
    	finalbean.setIss_Cre_Adj_Dtls_Cnt(rs.getString("ISS_CRE_ADJ_DTLS_CNT") != null ? rs.getString("ISS_CRE_ADJ_DTLS_CNT"):"00");
    	finalbean.setIss_Pre_arb_Dtls_Amt(rs.getString("ISS_PRE_ARB_DTLS_AMT") != null ? rs.getString("ISS_PRE_ARB_DTLS_AMT"):"00");
    	finalbean.setIss_Pre_arb_Dtls_Cnt(rs.getString("ISS_PRE_ARB_DTLS_CNT") != null ? rs.getString("ISS_PRE_ARB_DTLS_CNT"):"00");
    	finalbean.setIss_Charge_Dtls_Amt(rs.getString("ISS_CHARGE_DTLS_AMT") != null ? rs.getString("ISS_CHARGE_DTLS_AMT"):"00");
    	finalbean.setIss_Charge_Dtls_Cnt(rs.getString("ISS_CHARGE_DTLS_CNT") != null ? rs.getString("ISS_CHARGE_DTLS_CNT"):"00");
    	finalbean.setIss_Repr_Dtls_Amt(rs.getString("ISS_REPR_DTLS_AMT") != null ? rs.getString("ISS_REPR_DTLS_AMT"):"00");
    	finalbean.setIss_Repr_Dtls_Cnt(rs.getString("ISS_REPR_DTLS_CNT") != null ? rs.getString("ISS_REPR_DTLS_CNT"):"00");
    	finalbean.setCash_ATM_inter_Paid_GST_Amt(rs.getString("CASH_ATM_INTER_PAID_GST_AMT") != null ? rs.getString("CASH_ATM_INTER_PAID_GST_AMT"):"00");
    	finalbean.setCash_ATM_inter_Paid_GST_Cnt(rs.getString("CASH_ATM_INTER_PAID_GST_CNT") != null ? rs.getString("CASH_ATM_INTER_PAID_GST_CNT"):"00");
    	finalbean.setCash_Sha_Net_Acq_Sett_Amt(rs.getString("CASH_SHA_NET_ACQ_SETT_AMT") != null ? rs.getString("CASH_SHA_NET_ACQ_SETT_AMT"):"00");
    	finalbean.setCash_Sha_Net_Acq_Sett_Cnt(rs.getString("CASH_SHA_NET_ACQ_SETT_CNT") != null ? rs.getString("CASH_SHA_NET_ACQ_SETT_CNT"):"00");
    	finalbean.setTDS_on_Iss_Swtch_Exp_Amt(rs.getString("TDS_ON_ISS_SWTCH_EXP_AMT") != null ? rs.getString("TDS_ON_ISS_SWTCH_EXP_AMT"):"00");
    	finalbean.setTDS_on_Iss_Swtch_Exp_Cnt(rs.getString("TDS_ON_ISS_SWTCH_EXP_CNT") != null ? rs.getString("TDS_ON_ISS_SWTCH_EXP_CNT"):"00");
    	finalbean.setCash_Sha_Network_Serv_Tax_Amt(rs.getString("CASH_SHA_NETWORK_SERV_TAX_AMT") != null ? rs.getString("CASH_SHA_NETWORK_SERV_TAX_AMT"):"00");
    	finalbean.setCash_Sha_Network_Serv_Tax_Cnt(rs.getString("CASH_SHA_NETWORK_SERV_TAX_CNT") != null ? rs.getString("CASH_SHA_NETWORK_SERV_TAX_CNT"):"00");
    	finalbean.setAcq_WDL_Appr_Fee_Amt(rs.getString("ACQ_WDL_APPR_FEE_AMT") != null ? rs.getString("ACQ_WDL_APPR_FEE_AMT"):"00");
    	finalbean.setAcq_WDL_Appr_Fee_Cnt(rs.getString("ACQ_WDL_APPR_FEE_CNT") != null ? rs.getString("ACQ_WDL_APPR_FEE_CNT"):"00");
    	finalbean.setCash_Sha_net_Swt_Exp_Amt(rs.getString("CASH_SHA_NET_SWT_EXP_AMT") != null ? rs.getString("CASH_SHA_NET_SWT_EXP_AMT"):"00");
    	finalbean.setCash_Sha_net_Swt_Exp_Cnt(rs.getString("CASH_SHA_NET_SWT_EXP_CNT") != null ? rs.getString("CASH_SHA_NET_SWT_EXP_CNT"):"00");
    	finalbean.setIss_WDL_Approved_Fee_Amt(rs.getString("ISS_WDL_APPROVED_FEE_AMT") != null ? rs.getString("ISS_WDL_APPROVED_FEE_AMT"):"00");
    	finalbean.setIss_WDL_Approved_Fee_Cnt(rs.getString("ISS_WDL_APPROVED_FEE_CNT") != null ? rs.getString("ISS_WDL_APPROVED_FEE_CNT"):"00");
    	finalbean.setCash_Sha_Net_Iss_Sett_Amt(rs.getString("CASH_SHA_NET_ISS_SETT_AMT") != null ? rs.getString("CASH_SHA_NET_ISS_SETT_AMT"):"00");
    	finalbean.setCash_Sha_Net_Iss_Sett_Cnt(rs.getString("CASH_SHA_NET_ISS_SETT_CNT") != null ? rs.getString("CASH_SHA_NET_ISS_SETT_CNT"):"00");
    	finalbean.setNet_sett_AMOUNT_Amt(rs.getString("NET_SETT_AMOUNT_AMT") != null ? rs.getString("NET_SETT_AMOUNT_AMT"):"00");
    	finalbean.setNet_sett_AMOUNT_Cnt(rs.getString("NET_SETT_AMOUNT_CNT") != null ? rs.getString("NET_SETT_AMOUNT_CNT"):"00");
    	finalbean.setIss_Debit_Adj_Dtls_Amt(rs.getString("ISS_DEBIT_ADJ_DTLS_AMT") != null ? rs.getString("ISS_DEBIT_ADJ_DTLS_AMT"):"00");
    	finalbean.setIss_Debit_Adj_Dtls_Cnt(rs.getString("ISS_DEBIT_ADJ_DTLS_CNT") != null ? rs.getString("ISS_DEBIT_ADJ_DTLS_CNT"):"00");
    	finalbean.setAcq_Debit_Adj_Dtls_Amt(rs.getString("ACQ_DEBIT_ADJ_DTLS_AMT") != null ? rs.getString("ACQ_DEBIT_ADJ_DTLS_AMT"):"00");
    	finalbean.setAcq_Debit_Adj_Dtls_Cnt(rs.getString("ACQ_DEBIT_ADJ_DTLS_CNT") != null ? rs.getString("ACQ_DEBIT_ADJ_DTLS_CNT"):"00");
    	finalbean.setAcq_Pre_arb_Accpt_Cnt(rs.getString("ACQ_PRE_ARB_ACCPT_CNT") != null ? rs.getString("ACQ_PRE_ARB_ACCPT_CNT"):"00");
    	finalbean.setAcq_Pre_arb_Accpt_Amt(rs.getString("ACQ_PRE_ARB_ACCPT_AMT") != null ? rs.getString("ACQ_PRE_ARB_ACCPT_AMT"):"00");
    	finalbean.setIss_Pre_arb_Accpt_Amt(rs.getString("ISS_PRE_ARB_ACCPT_AMT") != null ? rs.getString("ISS_PRE_ARB_ACCPT_AMT"):"00");
    	finalbean.setIss_Pre_arb_Accpt_Cnt(rs.getString("ISS_PRE_ARB_ACCPT_CNT") != null ? rs.getString("ISS_PRE_ARB_ACCPT_CNT"):"00");
    	finalbean.setIss_penalty(rs.getString("ISS_PENALTY") != null ? rs.getString("ISS_PENALTY"):"00");
    	finalbean.setAcq_penalty(rs.getString("ACQ_PENALTY") != null ? rs.getString("ACQ_PENALTY"):"00");
    	//finalbean.setFileDate(rs.getString("FILEDATE") != null ? rs.getString(""):"00");
    	finalbean.setMan_Iss_represnment(rs.getString("MANUAL_ISS_REPR_DTLS_AMT") != null ? rs.getString("MANUAL_ISS_REPR_DTLS_AMT"):"00");
    	finalbean.setMan_ACQ_represnment(rs.getString("MANUAL_ACQ_REPR_DTLS_AMT") != null ? rs.getString("MANUAL_ACQ_REPR_DTLS_AMT"):"00");

        return finalbean;

    }
}




@Override
public List<GenerateSettleTTUMBean> generateSettlTTUM(
		SettlementBean settlementBean) {
	
	List<GenerateSettleTTUMBean> beanlist = new ArrayList<GenerateSettleTTUMBean>();
	
		GenerateSettleTTUMBean bean = new GenerateSettleTTUMBean();
		String msg = null;
		try {
		
			msg  = settlementTTUM(settlementBean.getDatepicker(),settlementBean.getUser_id());
		
		} catch(Exception ex) {
			
			ex.printStackTrace();
		}
		
		
			if(msg != null) {
				
				beanlist = getTTUMdata(settlementBean);
				
				
			} else if ( msg.equals("TTUM Already Generated")) {
				
				beanlist = getTTUMdata(settlementBean);
			}
			
			return beanlist ;
			
	
}


private List<GenerateSettleTTUMBean> getTTUMdata(SettlementBean settlementBean) {
	
	List<GenerateSettleTTUMBean> beanlist = new ArrayList<GenerateSettleTTUMBean>();
	Connection conn=null;
	PreparedStatement pstmt= null;
	ResultSet rset =null; 
	
	try {
	
	String GET_DATA = " select ACCOUNT_NUMBER,CURRENCY_CODE_OF_ACC_NO,SERVICE_OUTLET,PART_TRAN_TYPE,TRANSACTION_AMOUNT,TRANSACTION_PARTICULAR," +
                               "  REMARKS,REFERENCE_CURRENCY_CODE,REFERENCE_AMOUNT,ACCOUNT_REPORT_CODE "+
                                "  from CASHNET_STTLEMENT_TTUM where filedate='"+settlementBean.getDatepicker()+"'";
	
	
	conn = getConnection();
	pstmt = conn.prepareStatement(GET_DATA);
	rset = pstmt.executeQuery();
	
	System.out.println(GET_DATA);
	
	while (rset.next()) {
		
		GenerateSettleTTUMBean bean = new GenerateSettleTTUMBean();
		
		bean.setACCOUNT_NUMBER(rset.getString("ACCOUNT_NUMBER") );
		bean.setCURRENCY_CODE_OF_ACC_NO(rset.getString("CURRENCY_CODE_OF_ACC_NO"));
		bean.setSERVICE_OUTLET(rset.getString("SERVICE_OUTLET"));
		bean.setPART_TRAN_TYPE(rset.getString("PART_TRAN_TYPE"));
		bean.setTRANSACTION_AMOUNT(rset.getString("TRANSACTION_AMOUNT")!= null ? rset.getString("TRANSACTION_AMOUNT") : "0" );
		bean.setTRANSACTION_PARTICULAR(rset.getString("TRANSACTION_PARTICULAR"));
		bean.setREMARKS(rset.getString("REMARKS"));
		bean.setREFERENCE_CURRENCY_CODE(rset.getString("REFERENCE_CURRENCY_CODE"));
		bean.setREFERENCE_AMOUNT(rset.getString("REFERENCE_AMOUNT")!= null ?rset.getString("REFERENCE_AMOUNT"):"0" );
		bean.setACCOUNT_REPORT_CODE(rset.getString("ACCOUNT_REPORT_CODE"));
		
		
		beanlist.add(bean);
	}
	}catch (Exception ex) {
		
		ex.printStackTrace();
	}
	
	
	return beanlist;
}


public String settlementTTUM(String filedate, String entry_by) throws ParseException, Exception {
	try {
		logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");

		String response = null;
		Map<String, Object> inParams = new HashMap<String, Object>();

		inParams.put("I_FIlEDATE", filedate);
		inParams.put("I_ENTRY_BY", entry_by);

		GenerateSettlTtum acqclassificaton = new GenerateSettlTtum(getJdbcTemplate());
		Map<String, Object> outParams = acqclassificaton.execute(inParams);

		// logger.info("outParams msg1"+outParams.get("msg1"));
		logger.info("***** ReconProcessDaoImpl.settlementTTUM End ****");
		
		
			return outParams.get("ERROR_MSG").toString();

		
	} catch (Exception e) {
		demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
		logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
				new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
		return "ERROR OCCURRED";
	}

}

class GenerateSettlTtum extends StoredProcedure {
	private static final String procName = "Generate_Cashnet_ttum";

	GenerateSettlTtum(JdbcTemplate JdbcTemplate) {
		super(JdbcTemplate, procName);
		setFunction(false);

		declareParameter(new SqlParameter("I_FIlEDATE",Types.VARCHAR));
		declareParameter(new SqlParameter("I_ENTRY_BY", Types.VARCHAR));
		declareParameter(new SqlOutParameter("ERROR_MSG",Types.VARCHAR));
		compile();
	}
}






	
	


}
	






