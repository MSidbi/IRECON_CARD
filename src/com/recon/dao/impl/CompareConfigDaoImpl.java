package com.recon.dao.impl;

import java.beans.Statement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.Logger;
import com.recon.dao.ICompareConfigDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileColumnDtls;
import com.recon.model.FileSourceBean;
import com.recon.model.ManualCompareBean;
import com.recon.model.ManualFileColumnDtls;
import com.recon.model.Pos_Bean;
import com.recon.model.ReadVisaFile;
import com.recon.util.CardTocard_cbs;
import com.recon.util.OracleConn;
import com.recon.util.Pos_Reading;
import com.recon.util.ReadCBSFile;
import com.recon.util.ReadCardToCardCBS;
import com.recon.util.ReadCashNetFile;
import com.recon.util.ReadDHANASwitchFile;
import com.recon.util.ReadNUploadCBSFiles;
import com.recon.util.ReadNUploadOnusPosFile;
import com.recon.util.ReadNfsRawData;
import com.recon.util.ReadRupay;
import com.recon.util.ReadRupay88File;
import com.recon.util.ReadSwitchFile;
import com.recon.util.ReadUCOSwitchFile;
import com.recon.util.Read_ATM_File;
import com.recon.util.Utility;
import com.recon.util.demo;

import javafx.beans.property.adapter.JavaBeanObjectProperty;
@Component
public class CompareConfigDaoImpl extends JdbcDaoSupport implements ICompareConfigDao {
	
	private static final String O_ERROR_MESSAGE = "o_error_message";
	int reconcount=0,manreconcount=0,manmatchcount=0;
	private PlatformTransactionManager transactionManager;
	private Integer uploadcount;
	private Integer filecount;
	String upload_flag="Y";
	//int count=0;
	String man_flag="N";
	
	String value=null;
	java.sql.Statement  st;
	Connection con;
	
	
	@SuppressWarnings("resource")
	public void setTransactionManager() {
	    
		   try{
		  
			   logger.info("***** CompareConfigDaoImpl.setTransactionManager Start ****");
		   ApplicationContext context= new ClassPathXmlApplicationContext();
		   context = new ClassPathXmlApplicationContext("/resources/bean.xml");
		 
		   logger.info("in settransactionManager");
		   transactionManager = (PlatformTransactionManager) context.getBean("transactionManager"); 
		   logger.info(" settransactionManager completed");
		   logger.info("***** CompareConfigDaoImpl.setTransactionManager End ****");
		   ((ClassPathXmlApplicationContext) context).close();
		   }catch (Exception ex) {
			   
			   logger.error(" error in CompareConfigDaoImpl.setTransactionManager", new Exception("CompareConfigDaoImpl.setTransactionManager",ex));
		   }
		   
		   
	   }
	
	@Override
	public List<CompareSetupBean> getFileDetails() {

		logger.info("***** CompareConfigDaoImpl.getFileDetails Start ****");
		
		List<CompareSetupBean> filelist = null;
		
		try {
			
			String query="SELECT filesrc.Fileid as inFileId , filesrc.FileName as stFileName ,filesrc.dataseparator ,filesrc.rddatafrm ,filesrc.charpatt,"
					+ " filesrc.Activeflag as activeFlag  "
					+ " FROM Main_FILESOURCE filesrc "
					+ " WHERE activeFlag='A' ";
									/*+ " WHERE filesrc.ActiveFlag='A' ";*/
				
			logger.info("query==" +query);

			
			filelist = getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
			
			logger.info("***** CompareConfigDaoImpl.getFileDetails End ****");
			
		}catch (Exception ex) {
			
			logger.error(" error in CompareConfigDaoImpl.getFileDetails", new Exception("CompareConfigDaoImpl.getFileDetails",ex));
			throw ex;
		}
		
		
		
		return filelist;
	
	}

	@Override
	public boolean saveCompareDetails(CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.saveCompareDetails Start ****");
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		logger.info("status=="+status);
		List<FileColumnDtls> columnDtls = new ArrayList<FileColumnDtls>();
		
		columnDtls = setupBean.getColumnDtls();
		
		try {
			
			String category =  setupBean.getStSubCategory().equals("")?setupBean.getCategory():setupBean.getCategory()+"_"+setupBean.getStSubCategory();
			logger.info("category=="+category);
			boolean result = true;
			reconcount = getJdbcTemplate().queryForObject("SELECT CASE WHEN  (SELECT MAX(rec_Set_ID) FROM MAIN_RECON_SEQUENCE where  category ='"+category+"') is null then 0 else (SELECT MAX(rec_Set_ID) FROM MAIN_RECON_SEQUENCE where  category ='"+category+"') end as FLAG from dual", Integer.class);
			reconcount=reconcount+1;
			logger.info("reconcount=="+reconcount);
			result = insertMain_ReconSetupDetails(setupBean);
			
			if(result){
				
				result =insertReconParam(setupBean,columnDtls);
			}
				
			logger.info("***** CompareConfigDaoImpl.saveCompareDetails End ****");
			
			return result;
			
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.saveCompareDetails");
			logger.error(" error in CompareConfigDaoImpl.saveCompareDetails", new Exception("CompareConfigDaoImpl.saveCompareDetails",ex));
			return false;
		}
		
		
		
	}

	private boolean insertReconParam(CompareSetupBean setupBean,
			List<FileColumnDtls> columnDtls)  {
		 logger.info("***** CompareConfigDaoImpl.insertReconParam Start ****");
		int count=0;
		
		List<CompareSetupBean> setup_dtl_list =setupBean.getSetup_dtl_list();
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		logger.info("status=="+status);
		try{
		//ADDED BY INT 5779 FOR ADDING CATEGORY AND SUBCATEGORY	
		String category =  setupBean.getStSubCategory().equals("")?setupBean.getCategory():setupBean.getCategory()+"_"+setupBean.getStSubCategory();
		logger.info("category=="+category);
		for(final CompareSetupBean compareSetupBean :setup_dtl_list) {
			
			getJdbcTemplate().update("Insert into MAIN_RECON_PARAM (CATEGORY,TABLE_HEADER,PADDING,START_CHARPOS,CONDITION,CHARSIZE,ENTRY_BY,ENTRY_DATE,TABLE_FILE_ID,PATTERN,REC_SET_ID)"
					+ "values ('"+category+"','"+compareSetupBean.getTable_header()+"','"+compareSetupBean.getStPadding()+"','"+compareSetupBean.getStart_charpos()+"',"
							+ "'"+compareSetupBean.getCondition()+"',"+compareSetupBean.getCharsize()+",'"+setupBean.getEntryBy()+"',sysdate,"+setupBean.getCompareFile1()+",'"+compareSetupBean.getSrch_Pattern()+"',"+reconcount+")");
		
			
			if(compareSetupBean.getMatchCondn().equals("Y")){
				
				getJdbcTemplate().update("Insert into MAIN_MATCHING_CONDITION (CATEGORY,TABLE_HEADER,PADDING,START_CHARPOS,CONDITION,CHARSIZE,ENTRY_BY,ENTRY_DATE,TABLE_FILE_ID,PATTERN,REC_SET_ID)"
						+ "values ('"+category+"','"+compareSetupBean.getTable_header()+"','"+compareSetupBean.getStPadding()+"','"+compareSetupBean.getStart_charpos()+"',"
								+ "'"+compareSetupBean.getCondition()+"',"+compareSetupBean.getCharsize()+",'"+setupBean.getEntryBy()+"',sysdate,"+setupBean.getCompareFile1()+",'"+compareSetupBean.getSrch_Pattern()+"',"+reconcount+")");
			
			}
			
		}
		
		for(final FileColumnDtls fileColumnDtls:columnDtls ){
			
			count= count+1;
			
			if(setupBean.getCompareFile1()>0){
				
				getJdbcTemplate().update("Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID,RELAX_PARAM)"
						+ "values ('"+category+"','"+fileColumnDtls.getFileColumn1()+"','"+fileColumnDtls.getStPadding()+"','"+fileColumnDtls.getInStart_Char_Position()+"',"
								+ ""+fileColumnDtls.getInEnd_char_position()+",'"+setupBean.getEntryBy()+"',sysdate,"+setupBean.getCompareFile1()+",'"+fileColumnDtls.getDataType()+"',"
										+ "'"+fileColumnDtls.getDatpattern()+"',"+count+","+reconcount+","+setupBean.getStRelaxParam1()+")");
			
			
			}if(setupBean.getCompareFile2()>0){
				
			
				
				getJdbcTemplate().update("Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID,RELAX_PARAM)"
						+ "values ('"+category+"','"+fileColumnDtls.getFileColumn2()+"','"+fileColumnDtls.getStPadding2()+"','"+fileColumnDtls.getInStart_Char_Position2()+"',"
								+ ""+fileColumnDtls.getInEnd_char_position2()+",'"+setupBean.getEntryBy()+"',sysdate,"+setupBean.getCompareFile2()+",'"+fileColumnDtls.getDataType2()+"',"
										+ "'"+fileColumnDtls.getDatpattern2()+"',"+count+","+reconcount+","+setupBean.getStRelaxParam2()+")");
				
				
			
			} if(setupBean.getCompareFile3()>0){
				
				
				getJdbcTemplate().update("Insert into MAIN_MATCHING_CRITERIA (CATEGORY,MATCH_HEADER,PADDING,START_CHARPOS,CHAR_SIZE,ENTRY_BY,ENTRY_DATE,FILE_ID,DATATYPE,DATA_PATTERN,MATCH_ID,REC_SET_ID)"
						+ "values ('"+category+"','"+fileColumnDtls.getFileColumn3()+"','"+fileColumnDtls.getStPadding3()+"','"+fileColumnDtls.getInStart_Char_Position3()+"',"
								+ ""+fileColumnDtls.getInEnd_char_position3()+",'"+setupBean.getEntryBy()+"',sysdate,"+setupBean.getCompareFile3()+",'"+fileColumnDtls.getDataType3()+"',"
										+ "'"+fileColumnDtls.getDatpattern3()+"',"+count+","+reconcount+",)");
				
				
			}
			
			
				
			}
		
		transactionManager.commit(status);		
		
		 logger.info("***** CompareConfigDaoImpl.insertReconParam Start ****");
		 
		return true;
		
		}catch (Exception e) {
			
			transactionManager.rollback(status);
			logger.error(" error in CompareConfigDaoImpl.insertReconParam", new Exception("CompareConfigDaoImpl.insertReconParam",e));
				return false;
			}
		
		}
		
	@Override
	public boolean chkMain_ReconSetupDetails (CompareSetupBean setupBean) {
		try{
			logger.info("***** CompareConfigDaoImpl.chkMain_ReconSetupDetails Start ****");
			String category = setupBean.getStSubCategory().equals("")?setupBean.getCategory():setupBean.getCategory()+"_"+setupBean.getStSubCategory();
			logger.info("category=="+category);
			int rowcount=0;
			String query = "SELECT count(*) from MAIN_RECON_SEQUENCE"
					+ " WHERE (file1 ='"+setupBean.getCompreFileName1().toUpperCase()+"' or file1 ='"+setupBean.getCompreFileName2().toUpperCase()+"' )"
					+ "  AND (file2 = '"+setupBean.getCompreFileName1().toUpperCase()+"' or file2='"+setupBean.getCompreFileName2().toUpperCase()+"') "
					+ " AND category='"+category+"' "; 
			
			logger.info(query);
			rowcount = getJdbcTemplate().queryForObject(query,Integer.class );
			logger.info(rowcount);
			
			logger.info("***** CompareConfigDaoImpl.chkMain_ReconSetupDetails End ****");
			
			if(rowcount>0){
			
				return false;
				
			}else{
				
				return true;
			}
		}catch (Exception ex) {
			
			logger.error(" error in CompareConfigDaoImpl.chkMain_ReconSetupDetails", new Exception("CompareConfigDaoImpl.chkMain_ReconSetupDetails",ex));
			return false;
		}
		
		
		
		
	}
	
	public boolean insertMain_ReconSetupDetails(CompareSetupBean setupBean) {
		
		logger.info("***** CompareConfigDaoImpl.insertMain_ReconSetupDetails Start ****");
		String category = setupBean.getStSubCategory().equals("")?setupBean.getCategory():setupBean.getCategory()+"_"+setupBean.getStSubCategory();
		logger.info("category=="+category);
		String query="Insert into MAIN_RECON_SEQUENCE(rec_Set_ID,CATEGORY,FILE1,FILE2,FILE1_MATCHED,FILE2_MATCHED,entry_dt,entry_by) values("+reconcount+",'"+category+"','"+setupBean.getCompreFileName1().toUpperCase()+"','"+setupBean.getCompreFileName2().toUpperCase()+"','"+setupBean.getFile1match()+"','"+setupBean.getFile2match()+"',sysdate,'"+setupBean.getEntryBy()+"') ";
		logger.info("query=="+query);
		int count = getJdbcTemplate().update(query);
		logger.info("count=="+count);
		
		logger.info("***** CompareConfigDaoImpl.insertMain_ReconSetupDetails End ****");
		
		if(count>0) {
			
			return true;
		}else {
			
			return false;
		}
		
	
	}

	@Override
	public ArrayList<CompareSetupBean> getCompareFiles(String type,String subcat) throws Exception {
		logger.info("***** CompareConfigDaoImpl.getCompareFiles Start ****");
		/*ArrayList<CompareSetupBean> compareSetupBeans = null;
		
		subcat = subcat==null?"":subcat;
		
		try{
			//subcat
			
			String stCategory = subcat.equals("")?type+"_"+subcat : type;
			String query=" SELECT rec_set_id,recon_category as category,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE1 and FILE_CATEGORY='"+type+"' and FILE_SUBCATEGORY='-')) compreFileName1 ,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE2 and FILE_CATEGORY='"+type+"' and FILE_SUBCATEGORY='-') )compreFileName2 " 
					+ "  FROM MAIN_RECON_SEQUENCE mrecdet "
					+ " WHERE recon_category='"+type+"' ";
			
			query="SELECT rec_set_id,Rec_category as category,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.firstfile) compreFile1 ,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.secondfile) compreFile2 ,  "
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.thirdfile) compreFile3  "
					+ " FROM Main_ReconSetupDetails mrecdet"
					+ " WHERE Rec_category='"+type+"' and layer="+layer+"";
			
			logger.info(query);
			compareSetupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
			
		}catch(Exception ex){
			
			ex.printStackTrace();
		}
		
		return compareSetupBeans;*/
		ArrayList<CompareSetupBean> compareSetupBeans = null;
		String query=null;
		subcat = subcat==null?"":subcat;
		String file_cat=null;
		file_cat=type;
		if(type.equals("MASTERCARD") || type.equals("RUPAY") || type.equals("VISA") || type.equals("POS"))
		{
			file_cat=type;
			type=type+"_"+subcat;
			
		}
		
		try{
			//subcat
			
			String stCategory = subcat.equals("")?type+"_"+subcat : type;
			if(subcat.equalsIgnoreCase(""))
			{
			 query=" SELECT rec_set_id,RECON_CATEGORY as category,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE1 and FILE_CATEGORY='"+type+"' and FILE_SUBCATEGORY='-')) compreFileName1 ,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE2 and FILE_CATEGORY='"+type+"' and FILE_SUBCATEGORY='-') )compreFileName2 " 
					+ "  FROM MAIN_RECON_SEQUENCE mrecdet "
					+ " WHERE RECON_CATEGORY='"+type+"' ";
			
			}else{
			 query=" SELECT rec_set_id,RECON_CATEGORY as category,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE1 and FILE_CATEGORY='"+file_cat+"' and FILE_SUBCATEGORY='"+subcat+"')) compreFileName1 ,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = (select FILEID from main_filesource where upper(filename) = mrecdet.FILE2 and FILE_CATEGORY='"+file_cat+"' and FILE_SUBCATEGORY='"+subcat+"') )compreFileName2 " 
					+ "  FROM MAIN_RECON_SEQUENCE mrecdet "
					+ " WHERE RECON_CATEGORY='"+type+"' ";
			}
			/*query="SELECT rec_set_id,Rec_category as category,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.firstfile) compreFile1 ,"
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.secondfile) compreFile2 ,  "
					+ "(SELECT filename FROM main_filesource mfilsrc WHERE mfilsrc.fileid = mrecdet.thirdfile) compreFile3  "
					+ " FROM Main_ReconSetupDetails mrecdet"
					+ " WHERE Rec_category='"+type+"' and layer="+layer+"";
			*/
			logger.info("query=="+query);
			compareSetupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
			
			logger.info("***** CompareConfigDaoImpl.getCompareFiles End ****");
			
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.getCompareFiles");
			logger.error(" error in CompareConfigDaoImpl.getCompareFiles", new Exception("CompareConfigDaoImpl.getCompareFiles",ex));
			throw ex;
		}
		
		return compareSetupBeans;
	}

	@Override
	public ArrayList<CompareSetupBean> getmatchcrtlist(int rec_set_id,String Cate) throws Exception {
		ArrayList<CompareSetupBean> compareSetupBeans = null ;
		logger.info("***** CompareConfigDaoImpl.getmatchcrtlist Start ****");
		try{
			String query = " SELECT category,match_header as table_header,padding,start_charpos,char_size,"
					+ " (SELECT filename FROM main_filesource WHERE fileid = mmatchcrt.file_id)filename,datatype,match_id,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date"
					+ "  FROM main_matching_criteria mmatchcrt"
					+ "  WHERE mmatchcrt.REC_SET_ID="+rec_set_id+" and mmatchcrt.CATEGORY='"+Cate+"'"
					+ "  ORDER BY match_id  ";
			
			logger.info(query);
			compareSetupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
			
			logger.info("***** CompareConfigDaoImpl.getmatchcrtlist End ****");
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.getmatchcrtlist");	
			logger.error(" error in CompareConfigDaoImpl.getmatchcrtlist", new Exception("CompareConfigDaoImpl.getmatchcrtlist",ex));
			throw ex;
			}
			
		
		return compareSetupBeans;
	}

	@Override
	public ArrayList<CompareSetupBean> getmatchcondnlist(int rec_set_id,String Cate) throws Exception {
		ArrayList<CompareSetupBean> compareSetupBeans = null ;
		logger.info("***** CompareConfigDaoImpl.getmatchcondnlist Start ****");
		try{
			String query = " SELECT category,table_header,padding,start_charpos,condition,charsize,"
					+ " (SELECT filename FROM main_filesource WHERE fileid =mmatchcon.TABLE_FILE_ID)filename,pattern,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date"
					+ "   FROM main_matching_condition mmatchcon "
					+ "  WHERE mmatchcon.REC_SET_ID="+rec_set_id+" and mmatchcon.CATEGORY='"+Cate+"'";
			
			logger.info(query);
			compareSetupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
		
			logger.info("***** CompareConfigDaoImpl.getmatchcondnlist End ****");
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.getmatchcondnlist");		
			logger.error(" error in CompareConfigDaoImpl.getmatchcondnlist", new Exception("CompareConfigDaoImpl.getmatchcondnlist",ex));
			throw ex;
			}
			
		
		return compareSetupBeans;
	}

	@Override
	public ArrayList<CompareSetupBean> getrecparamlist(int rec_set_id,String Cate) throws Exception {
		logger.info("***** CompareConfigDaoImpl.getrecparamlist Start ****");
		ArrayList<CompareSetupBean> compareSetupBeans = null ;
		try{
		String query = "SELECT category,table_header,padding,start_charpos,condition,charsize,"
				+ " (SELECT filename FROM main_filesource WHERE fileid =mrecpar.TABLE_FILE_ID)filename,pattern,entry_by,to_char(entry_date,'dd/MON/yyyy')entry_date "
				+ " FROM Main_recon_param mrecpar WHERE mrecpar.REC_SET_ID="+rec_set_id+" and  mrecpar.CATEGORY='"+Cate+"'";
		
		logger.info(query);
		compareSetupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(CompareSetupBean.class));
		
		logger.info("***** CompareConfigDaoImpl.getrecparamlist End ****");
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.getrecparamlist");		
			logger.error(" error in CompareConfigDaoImpl.getrecparamlist", new Exception("CompareConfigDaoImpl.getrecparamlist",ex));
			throw ex;
		}
		
		
		return compareSetupBeans;
	}

	@Override
	public ArrayList<CompareSetupBean> getFileList() {
		logger.info("***** CompareConfigDaoImpl.getFileList Start ****");
		List<CompareSetupBean> filelist = null;
		
		try {
			
			String query="SELECT distinct filesrc.FileName as stFileName "
					+ " FROM Main_FILESOURCE filesrc "
					+ " WHERE filesrc.ActiveFlag='A' ";
				
			logger.info("query" +query.toLowerCase());
			
		/*	rs = oracleConn.executeQuery(query);*/
			
			/*while(rs.next()){
				
				FTPBean ftpBean = new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileName(rs.getString("FileName"));
				
				ftpFileList.add(ftpBean);
				
				
			}*/
			
			filelist = getJdbcTemplate().query(query.toLowerCase(),new BeanPropertyRowMapper(CompareSetupBean.class));
			logger.info("***** CompareConfigDaoImpl.getFileList End ****");
			
		}catch (Exception ex) {
			
			logger.error(" error in CompareConfigDaoImpl.getFileList", new Exception("CompareConfigDaoImpl.getFileList",ex));
			throw ex;
		}
		
		
		
		return (ArrayList<CompareSetupBean>) filelist;
	}
	
	
	
	
	@Override
	public ArrayList<Pos_Bean> getFileNameList(String filedate) throws Exception {
		logger.info("***** CompareConfigDaoImpl.getFileList Start ****");
		List<Pos_Bean> filelist = null;
		
		try {
			String filedateval=Utility.dateConveter_ddmonyyyy(filedate);
			String query="select distinct os1.FILE_NAME from MASTERCARD_POS_RAWDATA os1 where os1.FILEDATE='"+filedateval+"' ";
				
			logger.info("query" +query);
			
		/*	rs = oracleConn.executeQuery(query);*/
			
			/*while(rs.next()){
				
				FTPBean ftpBean = new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileName(rs.getString("FileName"));
				
				ftpFileList.add(ftpBean);
				
				
			}*/
			
			filelist = getJdbcTemplate().query(query,new BeanPropertyRowMapper(Pos_Bean.class));
			logger.info("***** CompareConfigDaoImpl.getFileList End ****");
			
		}catch (Exception ex) {
			
			logger.error(" error in CompareConfigDaoImpl.getFileList", new Exception("CompareConfigDaoImpl.getFileList",ex));
			throw ex;
		}
		
		
		
		return (ArrayList<Pos_Bean>) filelist;
	}
	

	@Override
	public boolean chkFileupload(CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.chkFileupload Start ****");
		boolean result = false;
		
		try {
			
			int fileid=0;
		String query= "Select COUNT(Fileid) from Main_File_Upload_Dtls where Fileid="+setupBean.getInFileId()+" AND to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"' ";
		logger.info(query);
		fileid = getJdbcTemplate().queryForObject(query.toLowerCase(),Integer.class);
		
		logger.info("***** CompareConfigDaoImpl.chkFileupload End ****");
		if(fileid == 0){
			
			result = true;
		}else {
			
			result= false;
		}
		
		
		}catch(Exception ex) {
			demo.logSQLException(ex, "CompareConfigDaoImpl.chkFileupload");
			logger.error(" error in CompareConfigDaoImpl.chkFileupload", new Exception("CompareConfigDaoImpl.chkFileupload",ex));
			//throw ex;
			
			return false;
			
		}
		
		
		return result;
	}

	@Override
	public HashMap<String, Object> uploadFile(CompareSetupBean setupBean,MultipartFile file) throws Exception {

		logger.info("***** CompareConfigDaoImpl.uploadFile Start ****");
		boolean result = false;
		String tablename="";
		//ADDED BY INT8624 FIR SHOWING EXCEPTION TO USER
		HashMap<String, Object> output = new HashMap();
		boolean outputFlag = false;
		FileSourceBean sourceBean;
		String query = "select distinct tablename,columnheader as tblheader from main_filesource filesrc inner join main_fileheaders filehd on filehd.fileid= filesrc.fileid "
				+ " where (filesrc.filename='"+setupBean.getFilename()+"' )  ";
		
		if( setupBean.getFilename().equals("SWITCH") || setupBean.getFilename().equals("VISA")) { 
			
			if(setupBean.getFilename().equals("VISA") && (setupBean.getCategory())!="WCC") {
				
				query = query + " and filesrc.tableName='visa_visa_rawdata'";
				
			}
			else if(setupBean.getFilename().equals("VISA") || setupBean.getCategory().equals("WCC"))
			{
				
					query = query + " and filesrc.tableName='VISA_WCC_RAWDATA'";
				
			}
			else if(setupBean.getFilename().equals(""
					+ "")) {
				
				query = query + " and filesrc.tableName='switch_rawdata'";
			}
			
		}else {
			
			if(setupBean.getStSubCategory().equalsIgnoreCase("CARDTOCARD"))
			{
			query = query + " and filesrc.file_category ='"+setupBean.getStSubCategory()+"' ";
			}
			else if(!setupBean.getFilename().equals("CBS")){
				query = query + "and filesrc.file_subcategory ='"+setupBean.getStSubCategory()+"' ";
			}
			
			// int8624 changes needs to be done here for single table  -- UCO
						/*if(setupBean.getStSubCategory().equals("-") && setupBean.getFilename().equals("CBS")) {
				
				query = query + " and  filesrc.FILE_CATEGORY ='ONUS'";
			}
			*else if(setupBean.getStSubCategory().equals("ISSUER") && setupBean.getFilename().equals("CBS")) { 
				
				query = query + " and  filesrc.tableName='CBS_RUPAY_RAWDATA' and  filesrc.FILE_CATEGORY ='NFS'";
			}else if(setupBean.getStSubCategory().equals("ACQUIRER") && setupBean.getFilename().equals("CBS")) {
				
				query = query + " and  filesrc.tableName='CBS_AMEX_RAWDATA' and filesrc.FILE_CATEGORY ='NFS' ";
			}*/
			if(setupBean.getFilename().equals("CBS")) { 
				
				query = query + " and  filesrc.tableName='cbs_rupay_rawdata' and  filesrc.file_category ='NFS'";
			}
      /*  else if(setupBean.getStSubCategory().equals("-") && setupBean.getFilename().equals("CBS") && (setupBean.getCategory().equals("CARDTOCARD"))) {
				
				query = query + " and  filesrc.FILE_CATEGORY ='CARD_TO_CARD'";
			}*/
			
		} 
		
		
		logger.info("query=="+query);
		sourceBean = (FileSourceBean) getJdbcTemplate().queryForObject(query,new BeanPropertyRowMapper( FileSourceBean.class));
		
		
		
		try{
		if(setupBean.getFilename().equalsIgnoreCase("SWITCH")) {

			
			//ReadSwitchFile switchFile = new ReadSwitchFile();
			//ADDED BY INT8624 FIR SHOWING EXCEPTION TO USER
			/*output= switchFile.uploadSwitchData(setupBean, getConnection(),file,sourceBean);
			result = (boolean) output.get("result");*/
			// changes made by INT8624 for UCO
			ReadDHANASwitchFile switchFile = new ReadDHANASwitchFile();
			String[] fileNames = file.getOriginalFilename().split("\\_");
			String fileName = fileNames[0];
			int extn = file.getOriginalFilename().indexOf(".");
			if(!file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLS") && !file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX"))
			{	
				output = switchFile.uploadSwitchData(setupBean, getConnection(), file, sourceBean);

				result = (Boolean) output.get("result");
				//Mapping in main tables
				if(result)
				{
					// call proc for mapping in main cbs table
					Map<String,Object> inParams = new HashMap<>();
					Map<String, Object> outParams = new HashMap<String, Object>();

					SWITCHCBSFileMapping rollBackexe = new SWITCHCBSFileMapping(getJdbcTemplate());
					inParams.put("FILEDT", setupBean.getFileDate());
					inParams.put("FILENAME", "SWITCH");  
					outParams = rollBackexe.execute(inParams);
					if(outParams !=null && outParams.get("msg") != null)
					{
						logger.info("OUT PARAM IS "+outParams.get("msg"));
					}

				}
			}
			else
			{
				output = switchFile.uploadAGSSwitchData(setupBean, getConnection(), file, sourceBean);
				result = (boolean) output.get("result");
			}
		
		}else if (setupBean.getFilename().equalsIgnoreCase("CBS")) {
			
			/*if(setupBean.getStSubCategory().equalsIgnoreCase("CARDTOCARD"))
			{
				CardTocard_cbs  readcardtocardcbs = new CardTocard_cbs();
				result=readcardtocardcbs.uploadDatac2c(setupBean, getConnection(), file, sourceBean);
			}
			else
			{*/
			String fileName = file.getOriginalFilename();
				System.out.println("Reading of CBS Starts");
				ReadNUploadCBSFiles cbsFile = new ReadNUploadCBSFiles();
				String bankName = "UCO";
				
				
				 output =cbsFile.uploadCBSData(setupBean, getConnection(), file, sourceBean);
				
				result = (Boolean) output.get("result");
					
				if(file.getOriginalFilename().contains("Atm") || file.getOriginalFilename().contains("Pos"))
				{
					if(result)
					{
						// call proc for mapping in main cbs table
						Map<String,Object> inParams = new HashMap<>();
						Map<String, Object> outParams = new HashMap<String, Object>();

						SWITCHCBSFileMapping rollBackexe = new SWITCHCBSFileMapping(getJdbcTemplate());
						inParams.put("FILEDT", setupBean.getFileDate());
						inParams.put("FILENAME", "CBS"); 
						outParams = rollBackexe.execute(inParams);
						if(outParams !=null && outParams.get("msg") != null)
						{
							logger.info("OUT PARAM IS "+outParams.get("msg"));
						}

					}
				}
			//}
		}else if(setupBean.getFilename().equalsIgnoreCase("RUPAY")){
			String fileName = file.getOriginalFilename();
			
			if(fileName.substring(0,2).equalsIgnoreCase("88"))
			{
				ReadRupay88File readRupay = new ReadRupay88File();
				result=readRupay.readData(setupBean, getConnection(), file, sourceBean);
			}
			else
			{	
				ReadRupay  readRupay = new ReadRupay();
				output =readRupay.readData(setupBean, getConnection(), file, sourceBean);
				result = (Boolean)output.get("result");
			}
			
			
		}else if(setupBean.getFilename().equalsIgnoreCase("NFS")){
			
			ReadNfsRawData nfsRawData = new ReadNfsRawData(); 
			result=nfsRawData.readData(setupBean, getConnection(), file, sourceBean);
			
		}else if(setupBean.getFilename().equalsIgnoreCase("CASHNET")){
			
			ReadCashNetFile  readcashnet = new ReadCashNetFile();
			result=readcashnet.readData(setupBean, getConnection(), file, sourceBean);
			
		}else if(setupBean.getFilename().equalsIgnoreCase("VISA")){
			
			ReadVisaFile  readRupay = new ReadVisaFile();
			result=readRupay.readData(setupBean, getConnection(), file, sourceBean);
			
		}
		else if(setupBean.getFilename().equalsIgnoreCase("ATM")){
			
			Read_ATM_File  readmas_atm = new Read_ATM_File();
			result=readmas_atm.read_method_atm(setupBean, getConnection(), file);
			
		}
		
		if(setupBean.getCategory().equalsIgnoreCase("POS"))
		{
			if(setupBean.getFilename().equalsIgnoreCase("POS"))
			{
				ReadNUploadOnusPosFile  readonus_pos = new ReadNUploadOnusPosFile();
				result=readonus_pos.read_method_onp(setupBean, getConnection(), file);
			}
		}
		if(setupBean.getCategory().equalsIgnoreCase("MASTERCARD"))
		{
			if(setupBean.getFilename().equalsIgnoreCase("POS")){
				
				Pos_Reading  readmas_pos = new Pos_Reading();
				result=readmas_pos.read_method(setupBean, getConnection(), file);
				
			}
		}

else if (setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
			
	//String strDate="06/05/2018";
	
	 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
	 String original_date = setupBean.getFileDate();
 	try {
 		
 	    java.util.Date varDate=dateFormat.parse(setupBean.getFileDate());
 	    dateFormat=new SimpleDateFormat("mm/dd/yyyy");
 	    System.out.println("Date :"+dateFormat.format(varDate));
 	   setupBean.setFileDate(dateFormat.format(varDate));
 	}catch (Exception e) {
 	    // TODO: handle exception
 	    e.printStackTrace();
 	}
	
			ReadCardToCardCBS bankway = new ReadCardToCardCBS();
			result=bankway.uploadDatacardtocardbank(setupBean, getConnection(), file, sourceBean);
		   setupBean.setFileDate(original_date);
			
		}
/*else if(setupBean.getFilename().equalsIgnoreCase("CBS_C2C")){
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
	try {
	    java.util.Date varDate=dateFormat.parse(setupBean.getFileDate());
	    dateFormat=new SimpleDateFormat("mm/dd/yyyy");
	    System.out.println("Date :"+dateFormat.format(varDate));
	    setupBean.setFileDate(dateFormat.format(varDate));
	}catch (Exception e) {
	    // TODO: handle exception
	    e.printStackTrace();
	}
	CardTocard_cbs  readcardtocardcbs = new CardTocard_cbs();
	result=readcardtocardcbs.uploadDatac2c(setupBean, getConnection(), file, sourceBean);
	
}*/
		/*else if (setupBean.getFilename().equalsIgnoreCase("CBS")) {
			
			ReadCardToCardCBS cbsFile = new ReadCardToCardCBS();
			result=cbsFile.uploadDatacardtocardcbs(setupBean, getConnection(), file, sourceBean);
			
		}*/
		
		
		if(result) {

			if(setupBean.getFilename().equalsIgnoreCase("POS"))
			{
				//return true;
				outputFlag = true;
			}
			else if(setupBean.getFilename().equalsIgnoreCase("ATM"))
			{
				//return true;
				outputFlag = true;
			}
			else if(setupBean.getCategory().equalsIgnoreCase("POS"))
			{
				//return true;
				outputFlag = true;
			}
			else{
				//result= updatefile(setupBean);
				outputFlag= updatefile(setupBean);
				
			}
		}
		
		logger.info("***** CompareConfigDaoImpl.uploadFile End ****");
		output.put("result", outputFlag);
		return output;
		
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.uploadFile");
			logger.error(" error in CompareConfigDaoImpl.uploadFile", new Exception("CompareConfigDaoImpl.uploadFile",ex));
			ex.printStackTrace();
			
			output.put("result", false);
			output.put("msg", "Exception while uploading file");
			
			return output;
			
		}
		
	
		
	}
	
	// inserting into Main_File_Upload_Dtls
	/*public boolean updatefile(CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.updatefile Start ****");
		logger.info("File name is "+setupBean.getFilename()+" sub category is "+setupBean.getStSubCategory());
		int count = 0;
		String subcatquery=null;
		
		if(setupBean.getCategory().equals("WCC") && setupBean.getFilename().equals("VISA")){

		if(setupBean.getStSubCategory().equals("-") && setupBean.getFilename().equals("WCC-VISA")){

			subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%"+setupBean.getFilename()+"%' and FILE_SUBCATEGORY = '-' ";
			
		}else if(setupBean.getFilename().equals("VISA") && setupBean.getStSubCategory().equals("ISSUER"))
					{
			subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%"+setupBean.getFilename()+"%' and  FILE_SUBCATEGORY!='-'";
		

			subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%"+setupBean.getFilename()+"%' and FILE_SUBCATEGORY = '-' ";
			
		}else{
			
			subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%"+setupBean.getFilename()+"%' ";
		}

			
					}
		else{
			
			subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename LIKE '%"+setupBean.getFilename()+"%' ";
		}
		if( setupBean.getFilename().equals("SWITCH") || setupBean.getFilename().equals("VISA")) {

			if(setupBean.getFilename().equals("VISA"))
			{
				subcatquery=subcatquery + " and FILE_CATEGORY = 'VISA'"; // MODIFIED BY INT8624 ON 19 NOV
			}
			else
			{
				//subcatquery=subcatquery;
				subcatquery="Select fileid InFileId, FILE_CATEGORY Category, FILE_SUBCATEGORY StSubCategory from main_filesource where filename = '"+setupBean.getFilename()+"' ";
			
			}
			
		
		} else {
			
			if (setupBean.getFilename().equals("CBS")) {
				subcatquery = subcatquery + " and tablename = 'CBS_RUPAY_RAWDATA' ";
			}
			else {
				
				if (setupBean.getStSubCategory().equalsIgnoreCase("CARDTOCARD")) {
					subcatquery = subcatquery + " AND FILE_CATEGORY = '"
							+ setupBean.getStSubCategory() + "' ";
				} else {
					subcatquery = subcatquery + " AND FILE_SUBCATEGORY = '"
							+ setupBean.getStSubCategory() + "' ";
				}
			}

		}
		
		
		logger.info("subcatquery=="+subcatquery);
		ArrayList<CompareSetupBean> fileids = new ArrayList<>();
		 fileids =  (ArrayList<CompareSetupBean>) getJdbcTemplate().query(subcatquery, new BeanPropertyRowMapper(CompareSetupBean.class));
         logger.info("fileids are "+fileids);  
         
		for(CompareSetupBean bean :fileids ) {
		String query="";		
		if(setupBean.getFileType().equalsIgnoreCase("MANUAL")){
			
			if(chkUploadFlag("UPLOAD_FLAG", setupBean).equalsIgnoreCase("N")) {
				
			
			query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
					+ "values ("+bean.getInFileId() +",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
							+ ",'N','N','N','N','N','Y')";
			
			
			
			}else {
				
				 query="Update MAIN_FILE_UPLOAD_DTLS set MANUPLOAD_FLAG ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND CATEGORY = '"+bean.getCategory()+"' AND FileId = "+bean.getInFileId()+" "; 
			}
		} else {
			
			bean.setFilename(setupBean.getFilename());
			bean.setFileDate(setupBean.getFileDate());
			if(chkUploadFlag("MANUPLOAD_FLAG", bean).equalsIgnoreCase("N")) {
				
				if(setupBean.getCategory().equalsIgnoreCase("CARDTOCARD"))
				{
					if(setupBean.getFilename().equalsIgnoreCase("BANKREPO"))
					{
						query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
								+ "values ("+bean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
										+ ",'Y','N','N','N','N','Y')";
					}
					else{
						query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
								+ "values ("+bean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
										+ ",'Y','N','N','N','N','Y')";
					}
				}
				else if(setupBean.getStSubCategory().equals("CARDTOCARD"))
				{
					if(setupBean.getFilename().equalsIgnoreCase("BANKREPO"))
					{
						query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
								+ "values ("+bean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
										+ ",'Y','N','N','N','N','Y')";
					}
					else{
					 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
					 	try {
					 	    java.util.Date varDate=dateFormat.parse(setupBean.getFileDate());
					 	    dateFormat=new SimpleDateFormat("mm/dd/yyyy");
					 	    System.out.println("Date :"+dateFormat.format(varDate));
					 	   setupBean.setFileDate(dateFormat.format(varDate));
					 	
					
					query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
						+ "values ("+bean.getInFileId()+",to_date('"+dateFormat.format(varDate)+"','mm/dd/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
						+ ",'Y','N','N','N','N','Y')";
					 	}catch (Exception e) {
					 	    // TODO: handle exception
					 	    e.printStackTrace();
					 	}
				}}
				
				else if(setupBean.getFilename().equalsIgnoreCase("VISA") && setupBean.getCategory().equalsIgnoreCase("WCC"))
				{
					if(getFileCount(setupBean)>0) {
						
						updateFlag1(setupBean);
					}else {
					
						updateFlag2(setupBean);
					}
					return true;
				}
				else{
					
					query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
						+ "values ("+bean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
								+ ",'Y','N','N','N','N','N')";
					
					if (setupBean.getFilename().equals("CBS")&& setupBean.getStSubCategory().equals("-")) {
						
						query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
								+ "values ("+bean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'"+setupBean.getCreatedBy()+"',sysdate,'"+bean.getCategory()+"','"+bean.getStSubCategory()+"'"
										+ ",'Y','N','N','N','N','Y')";
						
					}
				
					
				}
				logger.info("query=="+query);
				
				String  getupld_count = "Select case when FILE_COUNT is null then 0 else file_count end as file_count from main_file_upload_dtls where CATEGORY ='"+bean.getCategory()+"'  and FILE_SUBCATEGORY = '"+bean.getStSubCategory()+"' and FILEID ="+bean.getInFileId()+" ";
						
				if(setupBean.getFilename().equalsIgnoreCase("BANKREPO")) {
					
					//getupld_count = 	getupld_count + " and  filedate = to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy')  ";
					getupld_count = 	getupld_count + " and  filedate = to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy')  ";
				
				} 
				else if(setupBean.getStSubCategory().equals("CARDTOCARD"))
				{
					getupld_count = 	getupld_count + " and  filedate = to_date('"+setupBean.getFileDate()+"','mm/dd/yyyy')  ";
				}
				
				else {
					
					getupld_count = 	getupld_count + " and  filedate = to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy')  ";
				}
				
				getupld_count = " SELECT CASE WHEN exists ("+ getupld_count+") then ("+getupld_count+") else 0 end as FLAG from dual";
				
				logger.info("getupld_count=="+getupld_count);
				
				uploadcount = getJdbcTemplate().queryForObject(getupld_count, Integer.class);
				
				logger.info("uploadcount=="+uploadcount);
				
				uploadcount = uploadcount+1;
				
						if(setupBean.getFilename().equals("NFS")||setupBean.getFilename().equals("RUPAY") ||   (setupBean.getFilename().equals("VISA")) ||
								setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CASHNET") )
						{
							if (uploadcount == 1) {
	
								query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) "
										+ "values ("
										+ bean.getInFileId()
										+ ",to_date('"
										+ setupBean.getFileDate()
										+ "','dd/mm/yyyy'),'"
										+ setupBean.getCreatedBy()
										+ "',sysdate,'"
										+ bean.getCategory()
										+ "','"
										+ bean.getStSubCategory()
										+ "'"
										+ ",'Y','N','N','N','N','N',"
										+ uploadcount
										+ ")";
	
							} else {
	
								query = "Update MAIN_FILE_UPLOAD_DTLS set FILE_COUNT ='"
										+ uploadcount
										+ "'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
										+ setupBean.getFileDate()
										+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
										+ " AND CATEGORY = '"
										+ bean.getCategory()
										+ "' AND FileId = "
										+ bean.getInFileId()
										+ " ";
	
							}
					}
						else if (setupBean.getFilename().equalsIgnoreCase("CBS"))
						{

							if (uploadcount == 1) {
	
								query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) "
										+ "values ("
										+ bean.getInFileId()
										+ ",to_date('"
										+ setupBean.getFileDate()
										+ "','dd/mm/yyyy'),'"
										+ setupBean.getCreatedBy()
										+ "',sysdate,'"
										+ bean.getCategory()
										+ "','"
										+ bean.getStSubCategory()
										+ "'"
										+ ",'Y','N','N','N','N','Y',"
										+ uploadcount
										+ ")";
	
							} else {
	
								query = "Update MAIN_FILE_UPLOAD_DTLS set FILE_COUNT ='"
										+ uploadcount
										+ "'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
										+ setupBean.getFileDate()
										+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
										+ " AND CATEGORY = '"
										+ bean.getCategory()
										+ "' AND FileId = "
										+ bean.getInFileId()
										+ " ";
	
							}
					
						}
						else	if (setupBean.getCategory().equals("CARDTOCARD") && setupBean.getFilename().equals("BANKREPO")) {
						
						if (uploadcount == 1) {
							
							query = " insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,FILE_COUNT) "
									+ "values ("
									+ bean.getInFileId()
									+ ",to_date('"
									+ setupBean.getFileDate()
									+ "','dd/mm/yyyy'),'"
									+ setupBean.getCreatedBy()
									+ "',sysdate,'"
									+ bean.getCategory()
									+ "','"
									+ bean.getStSubCategory()
									+ "'"
									+ ",'Y','N','N','N','N','N',"
									+ uploadcount
									+ ")";

						} else {

							query = "Update MAIN_FILE_UPLOAD_DTLS set FILE_COUNT ='"
									+ uploadcount
									+ "'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
									+ setupBean.getFileDate()
									+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
									+ " AND CATEGORY = '"
									+ bean.getCategory()
									+ "' AND FileId = "
									+ bean.getInFileId()
									+ " ";

						}
						
					}
						
				}else {
					
					 query="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
							+ " AND CATEGORY = '"+bean.getCategory()+"' AND FileId = "+bean.getInFileId()+" "; 
				}
			
			
		}
		
		logger.info("query=="+query);
		
		try{
			
			getJdbcTemplate().update(query);
			
			
			count++; 
			
			logger.info("***** CompareConfigDaoImpl.updatefile End ****");
			
		}catch(Exception ex){
			
			demo.logSQLException(ex, "CompareConfigDaoImpl.updatefile");
			logger.error(" error in CompareConfigDaoImpl.updatefile", new Exception("CompareConfigDaoImpl.updatefile",ex));
			return false;
		}
		
		}
		
		if(count>0) {
			
			return true;
		} else {
			
			return false;
		}
		
		
		


}*/
/************* code modified by int8624 for external banks *****/
public boolean updatefile(CompareSetupBean setupBean) throws Exception {
	try
	{
		
		
		String getUploadCount = "select count(*) from main_file_upload_dtls where filedate = ? and fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"' and "
				+ "file_subcategory = '"+setupBean.getStSubCategory()+"') ";
		
		if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
		{
			getUploadCount = "select count(*) from main_file_upload_dtls where filedate = ? and fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"')";
		}
		
		logger.info("getUploadCount is "+getUploadCount);
		
		int count = getJdbcTemplate().queryForObject(getUploadCount, new Object[] {setupBean.getFileDate()},Integer.class);
		
		logger.info("count is "+count);
		
		if(count == 0)
		{
			// insert into main file table
			if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
			{
				String query = "insert into main_file_upload_dtls (fileid,filedate,updlodby,uploaddate,category,file_subcategory,upload_flag,filter_flag,knockoff_flag,"
								+ "	comapre_flag,manualcompare_flag,manupload_flag,file_count) "
						+"select fileid , '"+setupBean.getFileDate()+"', '"+setupBean.getCreatedBy()+"', sysdate(), file_category, file_subcategory, 'Y','N','N','N','N','Y','1'"
								+ "from main_filesource where filename = '"+setupBean.getFilename()+"'";
				getJdbcTemplate().execute(query);
				
				return true;
			}
			else
			{
				String query = "insert into main_file_upload_dtls (fileid,filedate,updlodby,uploaddate,category,file_subcategory,upload_flag,filter_flag,knockoff_flag,"
						+ "	comapre_flag,manualcompare_flag,manupload_flag,file_count) "
				+"select fileid , '"+setupBean.getFileDate()+"', '"+setupBean.getCreatedBy()+"', sysdate(), file_category, file_subcategory, 'Y','N','N','N','N','Y','1'"
				+ "from main_filesource where filename = '"+setupBean.getFilename()+"' and file_category = '"+setupBean.getCategory()+"' and file_subcategory = '"+setupBean.getStSubCategory()+"'";
				
				logger.info("Insert query is "+query);
				getJdbcTemplate().execute(query);
				
				return true;
			}
					
		}
		else
		{
			/// check file count and then update
			String getfile_count = "select distinct file_count from main_file_upload_dtls where filedate = ? and "
					+ "fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"'"
							+ " and file_subcategory = '"+setupBean.getStSubCategory()+"') ";
			
			if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
			{
				getfile_count = "select distinct file_count from main_file_upload_dtls where filedate = ? and "
						+ "fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"')";
								
			}
			
			logger.info("getfile_count "+getfile_count);
			
			int fileCount1 = getJdbcTemplate().queryForObject(getfile_count, new Object[] {setupBean.getFileDate()},Integer.class);
			
			getfile_count = "select distinct file_count from main_filesource where filename = '"+setupBean.getFilename()+"' and file_subcategory = '"+setupBean.getStSubCategory()+"'";
			
			if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
			{
				getfile_count = "select distinct file_count from main_filesource where filename = '"+setupBean.getFilename()+"'";
			}
			int fileCount2 = getJdbcTemplate().queryForObject(getfile_count, new Object[] {},Integer.class);
			
			if(fileCount1 < fileCount2)
			{
				if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
				{
					//update
					String query = "update main_file_upload_dtls set file_count = file_count+1 where filedate = STR_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d') and fileid in "
							+"(select fileid from main_filesource where filename = '"+setupBean.getFilename()+"')";
					getJdbcTemplate().execute(query);
					return true;
						
				}
				else
				{
				//update
				String query = "update main_file_upload_dtls set file_count = file_count+1 where filedate = STR_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d') and fileid in "
						+"(select fileid from main_filesource where filename = '"+setupBean.getFilename()+"' AND file_subcategory = '"+setupBean.getStSubCategory()+"')";
				getJdbcTemplate().execute(query);
				return true;
				}
			}
			else
			{
				return false;
			}
			
		}
		
	}
	catch(Exception e)
	{
		logger.info("Exception while updating "+e);
		return false;
		
		
	}
	
	
	}
	
	private class SWITCHCBSFileMapping extends StoredProcedure{
		private static final String insert_proc = "switch_cbs_data_mapping";
		public SWITCHCBSFileMapping(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("FILENAME",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	@Override
	public String chkFlag(String flag, CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.chkFlag Start ****");
		
			String flg = "";
			
			try{
			
			/*String query="SELECT "+flag+" FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy')  "
					+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+"  "; */
				// changes by minakshi 10/08/2018 date format replace
				String query="SELECT "+flag+" FROM MAIN_FILE_UPLOAD_DTLS WHERE filedate = '"+setupBean.getFileDate()+"'  "
						+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+"  "; 
			
			if(setupBean.getStSubCategory()!=null && !(setupBean.getStSubCategory().equals(""))){
				
				query= query+ " AND FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' ";
				
			}
			
			//logger.info(query);
			
			query = " SELECT CASE WHEN exists ("+ query+") then ("+query+") else 'N' end as FLAG from dual"; 
			
			logger.info("query == "+query);
			
			
			
			flg = getJdbcTemplate().queryForObject(query, String.class);
			
			logger.info("***** CompareConfigDaoImpl.chkFlag End ****");
			}catch(Exception e){
				demo.logSQLException(e, "CompareConfigDaoImpl.chkFlag");
				 logger.error(" error in CompareConfigDaoImpl.chkFlag ", new Exception("CompareConfigDaoImpl.chkFlag ",e));
				 throw e;
			}
				return flg;
		
	}
	
	@Override
	public boolean updateFlag(String flag, CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.updateFlag Start ****");
		//MODIFIED BY INT5779
		/*String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FileId = "+setupBean.getInFileId()+" AND FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' "; 
		*/
		try{
		/*String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+" AND FileId = "+setupBean.getInFileId();*/
			// changes by minakshi 10/08/2018 date format replace
			String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y'  WHERE filedate = '"+setupBean.getFileDate()+"' "
					+" AND FileId = "+setupBean.getInFileId();
			
		logger.info("query=="+query);
		
		int rowupdate =  getJdbcTemplate().update(query);
		logger.info("rowupdate=="+rowupdate);
		logger.info("***** CompareConfigDaoImpl.updateFlag End ****");
	
		
		if(rowupdate>0) {
			
			
			return true;
			
		}else{
			
			return false;
		}
		
		}catch(Exception e){
			demo.logSQLException(e, "CompareConfigDaoImpl.updateFlag");
			logger.error(" error in CompareConfigDaoImpl.updateFlag", new Exception("CompareConfigDaoImpl.updateFlag",e));
			throw e;
		}
	
	}

	
	

	@SuppressWarnings("resource")
	@Override
	public boolean validateFile(CompareSetupBean setupBean,MultipartFile file) throws Exception {
		logger.info("***** CompareConfigDaoImpl.validateFile Start ****");
		File readFile;
		FileInputStream fis;

		try {
			readFile = setupBean.getDataFile();

			fis = new FileInputStream("\\\\10.144.143.191\\led\\DCRS\\test"+ File.separator +file+".txt");

			int count = 0;

			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String thisline;

			while ((thisline = br.readLine()) != null) {

				if (count == 0 && setupBean.getStFileName().equalsIgnoreCase("SWITCH")) {
					
					
					
					String[] splitarray = null;

					splitarray = thisline.split(Pattern.quote("|"));
					
				String seperator =	getJdbcTemplate().queryForObject("SELECT Dataseparator FROM main_filesource WHERE fileid="+setupBean.getInFileId()+" ", String.class);
				logger.info("seperator=="+seperator);
				
				return chkfiledetails(splitarray,seperator,setupBean);

				}if(count==1 && (setupBean.getStFileName().equalsIgnoreCase("CBS"))) {
					
					
					String[] splitarray = null;

					splitarray = thisline.split(Pattern.quote("|"));
					
				String seperator =	getJdbcTemplate().queryForObject("SELECT Dataseparator FROM main_filesource WHERE fileid="+setupBean.getInFileId()+" ", String.class);
				logger.info("seperator=="+seperator);
				
				return chkfiledetails(splitarray,seperator,setupBean);
					
					
				}if(count==1 && setupBean.getStFileName().equals("MANUALCBS")){
					
					String[] splitarray = null;

					splitarray = thisline.split(Pattern.quote("|"));
					
				String seperator =	getJdbcTemplate().queryForObject("SELECT Dataseparator FROM main_filesource WHERE fileid="+setupBean.getInFileId()+" ", String.class);
				logger.info("seperator=="+seperator);
				
				return chkfiledetails(splitarray,seperator,setupBean);
					
				}
				
				count=count+1;

			}
			logger.info("***** CompareConfigDaoImpl.validateFile End ****");
			
		} catch (Exception ex) {

			logger.error(" error in CompareConfigDaoImpl.validateFile", new Exception("CompareConfigDaoImpl.validateFile",ex));
			throw ex;
		}

		return false;
	}
	
	
	public boolean chkfiledetails(String[]splitArray,String seperator,CompareSetupBean setupBean){
		logger.info("***** CompareConfigDaoImpl.chkfiledetails Start ****");
		int acceptorName=0;
		boolean result= false;
		
		if (setupBean.getStFileName().equalsIgnoreCase("SWITCH")) {
			
			if(splitArray[0].trim().length()==3 ||splitArray[1].trim().length()==16){
				
				String sql ="SELECT count(*) FROM  SWITCH_RAWDATA "
						+ " WHERE amount ='"+splitArray[7]+"' AND respcode = '"+splitArray[9]+"' "
								+ " AND pan ='"+splitArray[1]+"'  AND trace ='"+splitArray[6]+"' "
								+ " AND authnum ='"+splitArray[21]+"'";
				logger.info("sql=="+sql);
				
				acceptorName = getJdbcTemplate().queryForObject(sql, Integer.class);
				logger.info("acceptorName=="+acceptorName);
				
				if(acceptorName==0){
					
					result= true;  
					
				}else{
					
					result = false;
				}
				
			} else {
				
				
				return false;
			}

		}if (setupBean.getStFileName().equalsIgnoreCase("CBS")) {
			
			int len=0;
			
			if(setupBean.getFileType().equalsIgnoreCase("MANUAL")){
				
				len = splitArray[3].length();
			}else {
				
				len= splitArray[3].trim().length();
			}
			
			if(len == 9){
				
				String sql ="SELECT count(*) FROM  CBS_RAWDATA "
						+ " WHERE TRAN_ID ='"+splitArray[3]+"'";
				
				logger.info(sql);
				
				 acceptorName = getJdbcTemplate().queryForObject(sql, Integer.class);
				 logger.info("acceptorName=="+acceptorName);
				if(acceptorName==0){
					
					result= true;  
					
				}else{
					
					result = false;
				}
				
			} else {
				
				
				return false;
			}

		} if(setupBean.getStFileName().equals("MANUALCBS")){
			

			
			if(splitArray[3].length()==9){
				
				String sql ="SELECT count(*) FROM  cbs_Data "
						+ " WHERE TRAN_ID ='"+splitArray[3]+"'";
				
				logger.info(sql);
				
				 acceptorName = getJdbcTemplate().queryForObject(sql, Integer.class);
				 logger.info("acceptorName=="+acceptorName);
				if(acceptorName==0){
					
					result= true;  
					
				}else{
					
					result = false;
				}
				
			} else {
				
				
				return false;
			}

		
		}
		logger.info("***** CompareConfigDaoImpl.chkfiledetails End ****");
		
		return result;
		
		
		
		
	}

	@Override
	public List<CompareSetupBean> getlastUploadDetails() throws Exception {
		logger.info("***** CompareConfigDaoImpl.getlastUploadDetails Start ****");
		ArrayList<CompareSetupBean> setupBeans =null;
		try{
		
			String query= "SELECT filesrc.fileName as stFileName,to_char(filedate,'dd/mm/yy') as filedate,filter_Flag ,	knockoff_Flag , comapre_Flag , manualcompare_Flag , upload_Flag ,manupload_flag,"
					+ " updlodby as createdBy,category,filesrc.file_subcategory as stSubCategory,to_char(uploaddate,'dd/mm/yy') as entry_date "
					+ "	FROM MAIN_FILE_UPLOAD_DTLS  uplddtls "
					+ " INNER JOIN  MAIN_FILESOURCE filesrc ON  filesrc.fileId = uplddtls.fileid ORDER BY TO_DATE(FILEDATE,'DD/MM/YYYY')";
			
			
			logger.info(query);
			
			setupBeans = (ArrayList<CompareSetupBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class)) ;
			logger.info("***** CompareConfigDaoImpl.getlastUploadDetails End ****");
			
		}catch(Exception ex) {
			demo.logSQLException(ex, "CompareConfigDaoImpl.getlastUploadDetails");
			logger.error(" error in CompareConfigDaoImpl.getlastUploadDetails ", new Exception("CompareConfigDaoImpl.getlastUploadDetails ",ex));
			throw ex;
		}
		
		
		
		return setupBeans;
	}
	
	public boolean CheckAlreadyProcessed(CompareSetupBean setupBean) throws Exception
	{
		logger.info("***** CompareConfigDaoImpl.CheckAlreadyProcessed Start ****");
		int file1=0,file2=0;
		try
		{
			//check for already processed
			String query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'Y' AND UPLOAD_FLAG = 'Y'  AND Fileid="+setupBean.getCompareFile1()+" AND to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"' ";
			logger.info(query);
			file1 = getJdbcTemplate().queryForObject(query, Integer.class);

			query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'Y' AND MANUPLOAD_FLAG= 'Y'  AND Fileid="+setupBean.getCompareFile2()+" AND to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"' ";
			logger.info(query);
			file2 = getJdbcTemplate().queryForObject(query, Integer.class);	
			
			logger.info("***** CompareConfigDaoImpl.CheckAlreadyProcessed End ****");
			
			if(file1 != 0|| file2 != 0)
			{
				return true;
			}
			else return false;
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareConfigDaoImpl.CheckAlreadyProcessed");
			logger.error(" error in CompareConfigDaoImpl.CheckAlreadyProcessed ", new Exception("CompareConfigDaoImpl.CheckAlreadyProcessed ",e));
		
			return false;
		}
	}

	@Override
	public boolean chkCompareFiles(CompareSetupBean setupBean) throws Exception {
		logger.info("***** CompareConfigDaoImpl.chkCompareFiles Start ****");
		boolean result= false;
		try{
			
			int file1=0,file2=0;
			boolean alreadyProcessed = false;
			if(setupBean.getCompareLvl().equals("2")){
				
				
					
				
			String query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'N' AND UPLOAD_FLAG = 'Y'  AND Fileid="+setupBean.getCompareFile1()+" AND to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"' ";
			logger.info(query);
			file1 = getJdbcTemplate().queryForObject(query, Integer.class);
			
			query = "SELECT count(*) FROM main_File_upload_dtls where MANUALCOMPARE_FLAG = 'N' AND MANUPLOAD_FLAG= 'Y'  AND Fileid="+setupBean.getCompareFile2()+" AND to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"' ";
			logger.info(query);
			file2 = getJdbcTemplate().queryForObject(query, Integer.class);
			
			
			logger.info("***** CompareConfigDaoImpl.chkCompareFiles End ****");
			
			if(file1==0||file2==0) {
				
				result=false;
			}else {
				
				result = true;
			}
				
			}
		
		}catch(Exception ex) {
			demo.logSQLException(ex, "CompareConfigDaoImpl.chkCompareFiles");
			logger.error(" error in CompareConfigDaoImpl.chkCompareFiles ", new Exception("CompareConfigDaoImpl.chkCompareFiles ",ex));
			throw ex;
		}
		
		return result;
	}

	@Override
	public String getTableName(int Fileid) throws Exception {
		logger.info("***** CompareConfigDaoImpl.getTableName Start ****");
		String tablename="";
		try{
			
			String query = "Select tablename from main_filesource where fileid="+Fileid+"";
			tablename =getJdbcTemplate().queryForObject(query, String.class);
			
			logger.info("query"+query);
			logger.info("tablename"+tablename);
			logger.info("***** CompareConfigDaoImpl.getTableName End ****");
			
			return tablename;
			
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.getTableName");
			logger.error(" error in CompareConfigDaoImpl.getTableName ", new Exception("CompareConfigDaoImpl.getTableName ",ex));
			return null;
		}
		
		
	}

	@Override
	public boolean validate_File(String Filedate,CompareSetupBean setupBean) throws Exception{
		logger.info("***** CompareConfigDoImpl.validate_File Start ****");
		PreparedStatement ps = null;
		ResultSet rs;
		int count=0;
		try{
			
			//CHECKING RECORDS FOR PREVIOUS DAY
			String prevData = "select count(*) from SWITCH_data where TO_CHAR(createddate,'DD/MM/YYYY') = TO_CHAR(sysdate-1,'DD/MM/YYYY')";
			logger.info("prevData=="+prevData);
			//CHECKING WHETHER ANY DATA IS PRESENT
			String tablename = getJdbcTemplate().queryForObject( "select tablename from MAIN_FILESOURCE where FILEID = "+setupBean.getInFileId(),String.class);
			logger.info("tablename=="+tablename);
			
			String chkData ="select count(*) from "+tablename+" where TO_CHAR(CREATEDDATE,'DD/MM/YYYY') < TO_CHAR(sysdate,'DD/MM/YYYY')";
			
			logger.info(chkData);
			
			int dataCount = getJdbcTemplate().queryForObject(chkData, Integer.class); 
			logger.info("dataCount=="+dataCount);
			
			if(dataCount>0){
			
			
				String query = "SELECT count (*) FROM MAIN_FILE_UPLOAD_DTLS WHERE (TRUNC (filedate)) = (TRUNC (TO_DATE ('"+Filedate+"', 'dd/mm/yyyy') - 1) ) "
						+ "	AND Fileid ="+setupBean.getInFileId()+" AND category='ONUS' "
						+ " AND FILTER_FLAG='Y' AND KNOCKOFF_FLAG='Y' AND COMAPRE_FLAG='Y' "
						+ " AND UPLOAD_FLAG='Y'  ";
				count = getJdbcTemplate().queryForObject(query, Integer.class); 
				
				logger.info("query=="+query);
				logger.info("count=="+count);
				
				logger.info("***** CompareConfigDoImpl.validate_File End ****");
				//rs=ps.executeQuery();
				if(count>0){
					
					return true;
				}else {
					return false;
				}
			}else{
				
				return true;
			}
			
		
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.validate_File");
			logger.error(" error in CompareConfigDoImpl.validate_File", new Exception("CompareConfigDoImpl.validate_File",ex));
			return false;
		}
		
	}

	@Override
	public boolean saveManCompareDetails(ManualCompareBean manualCompareBean ) throws Exception {
		logger.info("***** CompareConfigDaoImpl.saveManCompareDetails Start ****");
		
		setTransactionManager();
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		List<ManualFileColumnDtls> columnDtls = new ArrayList<ManualFileColumnDtls>();
		List<ManualCompareBean> comp_dtl_list =new ArrayList<ManualCompareBean>();
		
		columnDtls = manualCompareBean.getColumnDtls();
		comp_dtl_list =manualCompareBean.getComp_dtl_list();
		
		try {
			
			manmatchcount = getJdbcTemplate().queryForObject("select (count (MAN_ID))+1 from MAIN_MANUAL_MASTER where category='"+manualCompareBean.getCategory()+"'", Integer.class);
			logger.info("manmatchcount=="+manmatchcount);
			
			insertMAIN_MANUAL_MASTER(comp_dtl_list,manualCompareBean);
			insertMAIN_MANUAL_CONDITION(columnDtls,manualCompareBean);
			
			logger.info("***** CompareConfigDaoImpl.saveManCompareDetails End ****");
			return true;
			
		}catch(Exception ex){
			demo.logSQLException(ex, "CompareConfigDaoImpl.saveManCompareDetails");
			logger.error(" error in CompareConfigDoImpl.saveManCompareDetails", new Exception("CompareConfigDoImpl.saveManCompareDetails",ex));
			return false;
		}
		
		
		
	
	}

	private void insertMAIN_MANUAL_CONDITION(List<ManualFileColumnDtls> columnDtls,	ManualCompareBean manualCompareBean) {
		logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_CONDITION Start ****");
		int compid=1;

		for(ManualFileColumnDtls filedtls:columnDtls) {
			
	if(filedtls.getFileColumn1()!= null){
		
				if(!(filedtls.getFileColumn1().equals("0"))){
			
					getJdbcTemplate().update("Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)"
					+ "values ("+manmatchcount+",'"+manualCompareBean.getCategory()+"',"+manualCompareBean.getCategory()+","+compid+",'"+filedtls.getFileColumn1()+"',"
							+ "'"+filedtls.getStPadding()+"','"+filedtls.getInStart_Char_Position()+"',"+filedtls.getInEnd_char_position()+",'"+manualCompareBean.getEntryBy()+"',sysdate)");
		
					logger.info("Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)"
					+ "values ("+manmatchcount+",'"+manualCompareBean.getCategory()+"',"+manualCompareBean.getCategory()+","+compid+",'"+filedtls.getFileColumn1()+"',"
							+ "'"+filedtls.getStPadding()+"','"+filedtls.getInStart_Char_Position()+"',"+filedtls.getInEnd_char_position()+",'"+manualCompareBean.getEntryBy()+"',sysdate)");
			} if(!(filedtls.getFileColumn2().equals("0"))) {
				
				getJdbcTemplate().update("Insert into MAIN_MANUAL_CONDITION (Man_Id,Category,Ref_File_Id,Comp_Id,File_Header,Padding,Start_Charposition,Charsize,Entry_By,Entry_Dt)"
				+ "values ("+manmatchcount+",'"+manualCompareBean.getCategory()+"',"+manualCompareBean.getMan_File()+","+compid+",'"+filedtls.getFileColumn2()+"',"
						+ "'"+filedtls.getStPadding()+"','"+filedtls.getInStart_Char_Position2()+"',"+filedtls.getInEnd_char_position2()+",'"+manualCompareBean.getEntryBy()+"',sysdate)");
	
			}
			
				compid++;
			}
		}
		logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_CONDITION End ****");
		
	}

	private void insertMAIN_MANUAL_MASTER(List<ManualCompareBean> comp_dtl_list,ManualCompareBean manualCompareBean) {
	
		logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_MASTER Start ****");
	for(ManualCompareBean filedtls:comp_dtl_list) {
			
		if(filedtls.getRefFileId()!=null){
				if(!(filedtls.getRefFileId().equals("0"))){
			
					getJdbcTemplate().update("Insert into MAIN_MANUAL_MASTER (MAN_ID,CATEGORY,REF_FILE_ID,FILE_HEADER,SEARCH_PATTERN,PADDING,START_CHARPOSITION,CHARSIZE,CONDITION,ENTRY_BY,ENTRY_DT)"
					+ "values ("+manmatchcount+",'"+manualCompareBean.getCategory()+"',"+filedtls.getRefFileId()+",'"+filedtls.getRefFileHdr()+"','"+filedtls.getStSearch_Pattern()+"'"
							+ ",'"+filedtls.getStPadding()+"','"+filedtls.getStChar_Pos()+"',"+filedtls.getStChar_Size()+",'"+filedtls.getCondition()+"','"+manualCompareBean.getEntryBy()+"',sysdate)");
		
			} 
		
		}
	
		}
	logger.info("***** CompareConfigDaoImpl.insertMAIN_MANUAL_MASTER End ****");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CompareSetupBean> getFileList(String category) {
		
		List<CompareSetupBean> beans =null;
		String query="Select fileid as inFileId, filename as stFileName,REC_SET_ID "
				+ " FROM MAIN_FILESOURCE inner join MAIN_RECONSETUPDETAILS on REC_CATEGORY = FILE_CATEGORY where FILE_CATEGORY='"+category+"'";
		
		
		
		logger.info(query);
		beans = (ArrayList<CompareSetupBean>)getJdbcTemplate().query(query, new BeanPropertyRowMapper(CompareSetupBean.class));
		return beans;
	}

/*	@Override
	public String chkUploadFlag(String flag, CompareSetupBean setupBean) {
	
		logger.info("***** CompareConfigDaoImpl.chkUploadFlag Start ****");
			String flg = "";
			String query="";
			try{
			if(setupBean.getFilename().equals("NFS")||setupBean.getFilename().equals("RUPAY") ||(setupBean.getFilename().equals("VISA") && setupBean.getStSubCategory().equals("-"))
					|| setupBean.getFilename().equalsIgnoreCase("SWITCH") ||setupBean.getFilename().equalsIgnoreCase("CBS") 
					|| setupBean.getFilename().equalsIgnoreCase("CASHNET")) {
				

				if(setupBean.getCategory().equalsIgnoreCase("WCC") && setupBean.getFilename().equals("VISA"))
				{
					setupBean.setCategory("WCC");
					setupBean.setStSubCategory("-");
				}
				else if(setupBean.getFilename().equals("VISA") && !(setupBean.getCategory().equalsIgnoreCase("WCC"))){
					setupBean.setCategory("VISA");
					setupBean.setStSubCategory("ISSUER");
				}
				else if(setupBean.getFilename().equals("SWITCH") && setupBean.getStSubCategory().equals("-")){
					setupBean.setCategory("NFS");
					setupBean.setStSubCategory("ISSUER");
				}

	String  getupld_count = "Select case when FILE_COUNT is null then 0 else file_count end as file_count from main_file_upload_dtls where CATEGORY ='"+setupBean.getCategory()+"'  and FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' "
						+ "and FileId in(Select fileid from main_filesource where filename='"+setupBean.getFilename()+"' AND FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"') "
						+ "and  filedate = to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy')  ";				
				logger.info(getupld_count);
				
				getupld_count = " SELECT CASE WHEN exists ("+ getupld_count+") then ("+getupld_count+") else 0 end as FLAG from dual";
				logger.info(getupld_count);
				
				uploadcount = getJdbcTemplate().queryForObject(getupld_count, Integer.class);
				logger.info("uploadcount=="+uploadcount);
				
				String getFile_count = "Select FILE_COUNT from Main_fileSource where FILE_CATEGORY ='"+setupBean.getCategory()+"' and filename='"+setupBean.getFilename()+"'  and FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' ";
				if(setupBean.getFilename().equalsIgnoreCase("CBS"))
				{
					getFile_count = "Select DISTINCT FILE_COUNT from Main_fileSource where FILE_CATEGORY ='NFS' and filename='CBS'";
				}
				logger.info("getFile_count=="+getFile_count);
				
				filecount = getJdbcTemplate().queryForObject(getFile_count, Integer.class);
				logger.info("filecount=="+filecount);
				
				if(filecount==uploadcount) {
					
					return"Y";
				} else {
					
					return"N";
				}
			}
			

			if(setupBean.getCategory().equalsIgnoreCase("MASTERCARD") && setupBean.getFilename().equalsIgnoreCase("POS"))
			{
			 query="SELECT distinct 'N' as flag FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"'  "
					
					+ " AND FileId in(Select fileid from main_filesource where filename='"+setupBean.getFilename()+"' ";
			}
			else{
				
				 query="SELECT distinct "+flag+" FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = '"+setupBean.getFileDate()+"'  "
							
					+ " AND FileId in(Select fileid from main_filesource where filename='"+setupBean.getFilename()+"' ";
			}
			if(!setupBean.getStSubCategory().equals("-")) {
				if(setupBean.getFilename().equalsIgnoreCase("CBS"))
				{
					query=query+ " AND CATEGORY = '"+setupBean.getCategory()+"' AND FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' )";
				}
				else
				{
					query=query+ " AND FILE_SUBCATEGORY = '"+setupBean.getStSubCategory()+"' )"; 
				}
			} else {
				
				if(!setupBean.getCategory().isEmpty()) {
					
					query=query+ " ) AND CATEGORY = '"+setupBean.getCategory()+"' ";
				} else {
				query=query+" )";
				}
				
				
			}
			
			
			//logger.info(query);
			
			query = " SELECT CASE WHEN exists ("+ query+") then ("+query+") else 'N' end as FLAG from dual"; 			
			logger.info("query=="+query);					
			
			flg = getJdbcTemplate().queryForObject(query, String.class);
			logger.info("flg=="+flg);
			
			logger.info("***** CompareConfigDaoImpl.chkUploadFlag End ****");
			
				return flg;
				
			} catch (Exception e) {
				logger.error(" error in CompareConfigDaoImpl.chkUploadFlag", new Exception("CompareConfigDaoImpl.chkUploadFlag",e));
				e.printStackTrace();
				throw e;
			}
	}*/
	
/***************** code simplified by int8624*****/
public String chkUploadFlag(String flag, CompareSetupBean setupBean) {

	try
	{
		if(setupBean.getFilename().equalsIgnoreCase("SWITCH") || setupBean.getFilename().equalsIgnoreCase("CBS") || setupBean.getFilename().equalsIgnoreCase("VISA"))
		{
			String getMainFileCount = "select distinct file_count from main_filesource where filename = '"+setupBean.getFilename()+"'";

			int fileCount = getJdbcTemplate().queryForObject(getMainFileCount, new Object[] {},Integer.class);

			getMainFileCount = "select count(*) from main_file_upload_dtls where filedate = str_to_Date('"+setupBean.getFileDate()+
					"','%Y/%m/%d')  and fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"'"
					+") and file_count = '"+fileCount+"'";
			logger.info("getMainFileCount "+getMainFileCount);

			int uploadCount = getJdbcTemplate().queryForObject(getMainFileCount, new Object[] {},Integer.class);

			if(uploadCount == 0)
			{
				return "N";
			}
			else
				return "Y";
		}
		else
		{
			String getMainFileCount = "select file_count from main_filesource where filename = '"+setupBean.getFilename()
			+"' and file_category = '"+setupBean.getCategory()+"' and file_subcategory = '"+setupBean.getStSubCategory()+"'";

			int fileCount = getJdbcTemplate().queryForObject(getMainFileCount, new Object[] {},Integer.class);

			getMainFileCount = "select count(*) from main_file_upload_dtls where filedate = str_to_date('"+setupBean.getFileDate()+
					"','%Y/%m/%d')  and fileid in (select fileid from main_filesource where filename = '"+setupBean.getFilename()+"'"
					+" and file_category = '"+setupBean.getCategory()+"' and file_subcategory = '"+setupBean.getStSubCategory()+"'"
					+ ") and file_count = '"+fileCount+"'";
			
			logger.info("getMainFileCount "+getMainFileCount);

			int uploadCount = getJdbcTemplate().queryForObject(getMainFileCount, new Object[] {},Integer.class);

			if(uploadCount == 0)
			{
				return "N";
			}
			else
				return "Y";
		}

	}
	catch(Exception e)
	{
		logger.error(" error in CompareConfigDaoImpl.chkUploadFlag", new Exception("CompareConfigDaoImpl.chkUploadFlag",e));
		e.printStackTrace();
		throw e;
	}
	}

	@Override
	public int getrecordcount(CompareSetupBean setupBean) {		

		try
		{
		logger.info("***** CompareConfigDaoImpl.getrecordcount Start ****");
		FileSourceBean sourceBean;
		String query = "SELECT distinct lower(tableName) FROM main_filesource filesrc "
				+ " WHERE (filesrc.filename='"+setupBean.getFilename()+"') ";// or filesrc.FILENAME like '%SUR%'
		
		if( !setupBean.getFilename().equals("SWITCH")) {
			
				if(setupBean.getFilename().equals("VISA")) {
					query = query + " and filesrc.file_subcategory ='ISSUER' ";
				}
				else if(setupBean.getFilename().equalsIgnoreCase("CASHNET"))
				{
					if(setupBean.getStSubCategory().equalsIgnoreCase("ISSUER"))
					{
						query = query + " and filesrc.file_subcategory ='ISSUER' ";
					}
					else
					{
						query = query + " and filesrc.file_subcategory ='ACQUIRER' ";
					}
				}
				else if(!setupBean.getFilename().equalsIgnoreCase("CBS")){
					
					query = query + " and filesrc.file_subcategory ='"+setupBean.getStSubCategory()+"' ";
				}
			
			//modified for UCO
			/*if(setupBean.getStSubCategory().equals("-") && setupBean.getFilename().equals("CBS")) {
				
				query = query + " and  filesrc.FILE_CATEGORY ='ONUS'";
			}
			else if(setupBean.getStSubCategory().equals("ISSUER") && setupBean.getFilename().equals("CBS")) {
				
				query = query + " and  filesrc.tableName ='CBS_RUPAY_RAWDATA'";
				
			}else if(setupBean.getStSubCategory().equals("ACQUIRER") && setupBean.getFilename().equals("CBS")) {
				
				query = query + " and  filesrc.tableName ='CBS_AMEX_RAWDATA'";
			}*/
			if(setupBean.getFilename().equals("CBS"))
			{
				query = query;
			}
			
		} else {
			
			query = query + " and  filesrc.tablename ='SWITCH_RAWDATA'";
			
		}
		
		
		logger.info("query=="+query);
		String tablename = "";
		tablename = (getJdbcTemplate().queryForObject(query, String.class)).toLowerCase();
		logger.info("tablename=="+tablename);
		int count=0;
		if(setupBean.getFilename().equalsIgnoreCase("RUPAY"))
		{
			logger.info("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')"
					+ " AND flag = '"+setupBean.getStSubCategory().substring(0, 1)+"'");
			count= getJdbcTemplate().queryForObject("Select  count(*) from "+tablename+" where filedate = str_TO_DATE('"+setupBean.getFileDate()+"','%Y/%m/%d') AND FLAG = '"+setupBean.getStSubCategory().substring(0, 1)+"'",
					 Integer.class);
		}
		else if(setupBean.getFilename().equalsIgnoreCase("NFS"))
		{
			logger.info("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')");
			count= getJdbcTemplate().queryForObject("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')",
					 Integer.class);
		}
		else if(setupBean.getFilename().equalsIgnoreCase("VISA"))
		{
			logger.info("Select  count(*) from "+tablename+" where filedate = str_to_Date('"+setupBean.getFileDate()+"','%Y/%m/%d')");
			count= getJdbcTemplate().queryForObject("Select  count(*) from "+tablename+" where filedate = "
					+ "str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')",
					 Integer.class);
		}
		else if(setupBean.getFilename().equalsIgnoreCase("CASHNET"))
		{
			logger.info("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')");
			count= getJdbcTemplate().queryForObject("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')",
					 Integer.class);
		}
		else{
			logger.info("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')");
		count= getJdbcTemplate().queryForObject("Select  count(*) from "+tablename+" where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')",
				 Integer.class);
		}
		logger.info("count=="+count);
		
		logger.info("***** CompareConfigDaoImpl.getrecordcount End ****");
		
		return count;
		
		}
		catch(Exception e)
		{
			logger.info("Exception while getting count "+e);
			return 0;
		}
	
		
		
	}

	@Override
	public HashMap<String, Object> uploadREV_File(CompareSetupBean setupBean, MultipartFile file) {
		
		int extn = file.getOriginalFilename().indexOf(".");
		HashMap<String, Object> output = new HashMap<String, Object>();
		//Workbook book;
		PreparedStatement ps = null;
		 FormulaEvaluator objFormulaEvaluator = null;
		int lineNumber = 0,fileUploadedCount = 0;
		try {
			//OPCPackage pkg = OPCPackage.open(new File(file.getInputStream()));
			
			Workbook wb = null;
		//	Workbook wb_hssf = null;
			Sheet sheet = null;
			
			String cycle = "";
     		//ADDED BY INT8624 FOR GETTING CYCLE FROM FILE NAME
     		String fileName = file.getOriginalFilename();
     		logger.info("FileName is "+fileName);
     		String[] fileNames = fileName.split("_");
     		if(fileNames.length >0)
     			cycle = fileNames[1].substring(0,1);
     		
     		logger.info("Cycle is: "+cycle);
     		
     		
				if (file.getOriginalFilename().substring(extn)
						.equalsIgnoreCase(".XLS")) {
			 
				//	book = new HSSFWorkbook(file.getInputStream()) ;
					wb = new HSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					 objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
				
				} else if (file.getOriginalFilename().substring(extn).equalsIgnoreCase(".XLSX")){
					 
					// book = new XSSFWorkbook(file.getInputStream());
					wb = new XSSFWorkbook(file.getInputStream());
					sheet = wb.getSheetAt(0);
					 objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
				}
				  
				 /*POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
		         HSSFWorkbook wb = new HSSFWorkbook(fs);*/
				
				
				//VALIDATING ALREADY UPLOADED FILE
				
				//get filecount from Main_file_upload_dtls
				int uploadCount = getJdbcTemplate().queryForObject("select count(*) from main_file_upload_dtls where filedate = str_to_date('"+setupBean.getFileDate()
						+"','%Y/%m/%d') and fileid in (select fileid from main_filesource where filename = 'rev_report')", Integer.class);	
				
				if(uploadCount > 0)
				{
					fileUploadedCount = getJdbcTemplate().queryForObject("select file_count from main_file_upload_dtls where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d') "
							+ "			and fileid in (select fileid from main_filesource where filename = 'rev_report')", Integer.class);
				}
				int ActualFileCount = getJdbcTemplate().queryForObject("select file_count from main_filesource where filename = 'rev_report'", Integer.class);
				
				if(fileUploadedCount != ActualFileCount)
				{
					int dataCount = getJdbcTemplate().queryForObject("select count(*) from nfs_rev_acq_report where filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d') AND CYCLE = '"+cycle+"'",Integer.class); 
					String prevdate;

					boolean result= false;
					if (dataCount > 0) {

						result = true;

					}

					if(!result) {

						// HSSFSheet sheet = wb.getSheetAt(0);


						Row row = (Row)sheet.getRow(4);
						try
						{
						int noOfColumns = sheet.getRow(3).getLastCellNum();
						}
						catch(NullPointerException ne)
						{
							//ENTRY IN MAIN_FILE_UPLOAD_DTLS
							if(uploadCount > 0)
							{
								//update file count col of main_file_upload_dtls table
								String updateTable = "update main_file_upload_dtls set file_count = file_count+1 where fileid in (select fileid from main_filesource where filename = 'REV_REPORT')"
												+ " and filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')";
								getJdbcTemplate().execute(updateTable);
							}
							else
							{
									String insertData = "insert into main_file_upload_dtls (fileid,filedate,updlodby, category, filter_flag, knockoff_flag, comapre_flag, manualcompare_flag ,upload_flag,manupload_flag, file_subcategory, file_count) "
											+"values((select fileid from main_filesource where filename = 'REV_REPORT'), str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d'), '"+setupBean.getCreatedBy()+"', 'NFS', 'N','N','N','N','Y','Y', 'ACQUIRER' ,'1')";
									getJdbcTemplate().execute(insertData);
							}
							output.put("result", false);
							output.put("msg", "Uploaded File is Blank");
							return output;
						}
						for(int i =3;i<= sheet.getLastRowNum();i++) {

							row = (Row)sheet.getRow(i);
							String offsitebTotal = row.getCell(0).getStringCellValue();

							//	 System.out.println(row.getCell(2).getStringCellValue());

							System.out.println(offsitebTotal);

							final SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");






							String sql = "insert into nfs_rev_acq_report (transtype ,resp_code,cardno ,rrn ,stanno ,acq ,iss ,trasn_date,trans_time ,atmid ,settledate ,requestamt ,receivedamt ,status,dcrs_remarks,filedate,cycle,merchant_type,fpan)"
									+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y/%m/%d'),?,?,?)";
							ps= getConnection().prepareStatement(sql);

							// FormulaEvaluator objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) wb);

							DataFormatter objDefaultFormat = new DataFormatter();
							Cell txn_time = row.getCell(8);
							Cell atm_id = row.getCell(9);
							Cell settl_dt = row.getCell(10); 
							Cell cell7  = row.getCell(7);
							objFormulaEvaluator.evaluate(atm_id); // This will evaluate the cell, And any type of cell will return string value
							String atmid=objDefaultFormat.formatCellValue(atm_id,objFormulaEvaluator);
							objFormulaEvaluator.evaluate(txn_time);
							String txntime = objDefaultFormat.formatCellValue(txn_time,objFormulaEvaluator);
							objFormulaEvaluator.evaluate(settl_dt);
							String settldt = objDefaultFormat.formatCellValue(settl_dt,objFormulaEvaluator);
							objFormulaEvaluator.evaluate(cell7);
							String cell_7 = objDefaultFormat.formatCellValue(cell7,objFormulaEvaluator);





							ps.setString(1, row.getCell(0).getStringCellValue().replace("'", ""));
							ps.setString(2, row.getCell(1).getStringCellValue().replace("'", ""));
							//ps.setString(3, row.getCell(2).getStringCellValue().replace("`", ""));
							//masking pan number 
							String new_pan = row.getCell(2).getStringCellValue().replace("`", "").trim();
							new_pan =  new_pan.substring(0, 6)+"XXXXXX"+new_pan.substring(new_pan.length()-4);
							logger.info("New Pan is "+new_pan);
							ps.setString(3, new_pan);	

							//ends here

							ps.setString(4, row.getCell(3).getStringCellValue().replace("'", ""));

							BigDecimal bd= new BigDecimal(row.getCell(4).getNumericCellValue());


							ps.setLong(5, bd.longValue());
							ps.setString(6, row.getCell(5).getStringCellValue().replace("'", ""));
							ps.setString(7, row.getCell(6).getStringCellValue().replace("'", ""));
							ps.setString(8, (cell_7.replaceAll("\u00A0", "").replace("'", "")));
							ps.setString(9, setupBean.getFileDate());


							final Date dateObj = sdf.parse(txntime.replaceAll("\u00A0", ""));


							ps.setString(9, new SimpleDateFormat("Kmmss").format(dateObj) );
							ps.setString(10, (atmid.replaceAll("\u00A0", "")).replace("'", ""));
							ps.setString(11, (settldt.replaceAll("\u00A0", "")).replace("'", ""));
							ps.setString(12, String.valueOf(row.getCell(11).getNumericCellValue()).replace(".0", "") );
							
							logger.info("String.valueOf(row.getCell(12).getNumericCellValue()) "+String.valueOf(row.getCell(12).getNumericCellValue()) );
							
							logger.info("check this "+ String.valueOf(row.getCell(12).getNumericCellValue()).replace(".0", "") );
							
							ps.setString(13, String.valueOf(row.getCell(12).getNumericCellValue()).replace(".0", "") );
							ps.setString(14, row.getCell(13).getStringCellValue().replace("'", ""));
							ps.setString(15,"UNMATCHED" );
							ps.setString(16, setupBean.getFileDate());
							ps.setString(17, cycle);
							
							/*System.out.println(row.getCell(8).getDateCellValue().toString());
						 final Date dateObj = sdf.parse(row.getCell(8).getDateCellValue().toString());
						 System.out.println(new SimpleDateFormat("Kmmss").format(dateObj) );*/


							//GETTING MERCHANT_TYPE
							String pan = row.getCell(2).getStringCellValue().replace("`", "");
							System.out.println("Pan is "+pan);
							System.out.println(pan.substring(0, 6));
							System.out.println(pan.substring(pan.length()-4));
							String Merchant_type = "";

							try
							{
								String acqtype = row.getCell(5).getStringCellValue().replace("'", "");
								if(acqtype.equalsIgnoreCase("DLB"))
								{
									System.out.println("select merchant_category_cd from nfs_nfs_acq_rawdata where filedate = str_TO_DATE(?,'%Y/%m/%d') AND substr(pan_number,1,6) = '"+pan.substring(0, 6)+"' and substr(pan_number,-4) = '"+pan.substring(pan.length()-4)
									+"' AND txn_serial_no = '"+row.getCell(3).getStringCellValue().replace("'", "")+"'");
									String getData = "select merchant_category_cd from nfs_nfs_acq_rawdata where filedate = STR_TO_DATE(?,'%Y/%m/%d') AND substr(pan_number,1,6) = '"+pan.substring(0, 6)+"' and substr(pan_number,-4) = '"+pan.substring(pan.length()-4)
									+"' AND txn_serial_no = '"+row.getCell(3).getStringCellValue().replace("'", "")+"'";
									Merchant_type = getJdbcTemplate().queryForObject(getData, new Object[] {(cell_7.replaceAll("\u00A0", "").replace("'", ""))},String.class);
									System.out.println("Merchant_type is "+Merchant_type);
								}
								else
								{
									System.out.println("select merchant_category_cd from nfs_nfs_iss_rawdata where filedate = str_TO_DATE(?,'%Y/%m/%d') AND substr(pan_number,1,6) = '"+pan.substring(0, 6)+"' and substr(pan_number,-4) = '"+pan.substring(pan.length()-4)
									+"' AND txn_serial_no = '"+row.getCell(3).getStringCellValue().replace("'", "")+"'");
									String getData = "select merchant_category_cd from nfs_nfs_iss_rawdata where filedate = str_TO_DATE(?,'%Y/%m/%d') AND substr(pan_number,1,6) = '"+pan.substring(0, 6)+"' and substr(pan_number,-4) = '"+pan.substring(pan.length()-4)
									+"' AND txn_serial_no = '"+row.getCell(3).getStringCellValue().replace("'", "")+"'";
									Merchant_type = getJdbcTemplate().queryForObject(getData, new Object[] {(cell_7.replaceAll("\u00A0", "").replace("'", ""))},String.class);
									System.out.println("Merchant_type is "+Merchant_type);


								}
							}
							catch(Exception e)
							{
								logger.info("Exception while getting merchant_type "+e);
								System.out.println("Exception while getting merchant_type "+e);

							}
							System.out.println("Done getting merchant_type");
							ps.setString(18, Merchant_type);
							ps.setString(19, row.getCell(2).getStringCellValue().replace("`", "").trim());
							
							lineNumber++;
							ps.executeUpdate();

						}


						ps.close();
						
						try
						{
							//CHECK all 4 files are uploaded or not
							logger.info("Updation Starting cycle is "+cycle);
							String updateQuery ="UPDATE nfs_rev_acq_report SET fpan = aes_encrypt(rtrim(ltrim(fpan)),'key_dbank') "
										+ "WHERE cycle = '"+cycle+"' AND filedate = str_TO_DATE('"+setupBean.getFileDate()+"','%Y/%m/%d')";
							
							logger.info("updateQuery is "+updateQuery);
							
							PreparedStatement pstmt = getConnection().prepareStatement(updateQuery);
							pstmt.execute();
							
							logger.info("Updation done");
							
							/*logger.info("Before updating rev table ");
							//UPDATING ENCRYPTED PAN NUMBERS IN RAW TABLE
							String updateQuery = "update nfs_rev_acq_report A set fpan = (select cast(aes_encrypt(rtrim(ltrim(fpan)),'key_dbank')as char) enc from NFS_REV_ACQ_REPORT B"
									+" where a.RRN = B.RRN AND A.CARDNO = B.CARDNO AND A.STANNO = B.STANNO AND A.FILEDATE = B.FILEDATE AND A.CYCLE = B.CYCLE)"
									+" WHERE FILEDATE = STR_TO_DATE('"+setupBean.getFileDate()+"','%Y/%m/%d') AND CYCLE = '"+cycle+"'";
							logger.info("update query is "+updateQuery);
							PreparedStatement update_ps  = getConnection().prepareStatement(updateQuery);
							update_ps.execute();
							logger.info("updated encrypted pan");*/
						}
						catch(Exception exc)
						{
							logger.info("Exception while updating rev report table "+exc);
						}
						
						//ENTRY IN MAIN_FILE_UPLOAD_DTLS
						if(uploadCount > 0)
						{
							//update file count col of main_file_upload_dtls table
							String updateTable = "update main_file_upload_dtls set file_count = file_count+1 where fileid in (select fileid from main_filesource where filename = 'REV_REPORT')"
											+ " and filedate = str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d')";
							getJdbcTemplate().execute(updateTable);
						}
						else
						{
								String insertData = "insert into main_file_upload_dtls (fileid,filedate,updlodby, category, filter_flag, knockoff_flag, comapre_flag, manualcompare_flag ,upload_flag,manupload_flag, file_subcategory, file_count) "
										+"VALUES((select fileid from main_filesource where filename = 'REV_REPORT'), str_to_date('"+setupBean.getFileDate()+"','%Y/%m/%d'), '"+setupBean.getCreatedBy()+"', 'NFS', 'N','N','N','N','Y','Y', 'ACQUIRER' ,'1')";
								getJdbcTemplate().execute(insertData);
						}
						
						output.put("result", true);
						output.put("msg", " "+lineNumber);
						return output;

					} else {

						output.put("result", false);
						output.put("msg","Selected cycle file is already Uploaded");
						System.out.println("File already Uploaded");
						return output;
					}
				}
				else
				{
					output.put("result", false);
					output.put("msg","All Files are already uplaoded!");
					logger.info("All Files are uploaded");
					return output;
				}
		      
				
				
				
		}
		catch(NotOLE2FileException e)
		{
			logger.info("File is not excel "+e);
			output.put("result", false);
			output.put("msg","Uploaded file is not Excel");
			return output;
			
		}
		catch (Exception ex) {		
		
			logger.info(ex.toString());
			ex.printStackTrace();
			output.put("result", false);
			output.put("msg","Exception Occurred while uplaoding file");
			return output;
		}
		
		
	}

	@Override
	public int getREVrecordcount(CompareSetupBean setupBean) {
		
		int count=0;
		
		String query = "select count(*) from nfs_rev_acq_report where filedate=to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy')   " ;
		
		count = getJdbcTemplate().queryForObject(query, Integer.class);
		
		return count;
	}
 
	/* jit -avoid duplicate file name --  start */
	@Override
	public boolean chkBeforeUploadFile(String flag, CompareSetupBean setupBean) throws Exception {
		
		Map<String , Object> inParams = new HashMap<String , Object>();
		inParams.put("P_FILEID", setupBean.getFILEID() );
		inParams.put("P_SUB_FILEID", setupBean.getSUB_FILEID());
		inParams.put("PP_FILENAME", setupBean.getP_FILE_NAME());
		inParams.put("P_FILEDATE", setupBean.getFileDate());
		
		ChkBeforeClass chk = new ChkBeforeClass(getJdbcTemplate());
		Map<String , Object> outParams = chk.execute(inParams);
		 
		
		if(outParams.get("MSG") != null){
			throw new Exception(outParams.get("MSG").toString());
		}
		
		 
		return false;
	}
	
	private class ChkBeforeClass extends StoredProcedure{
		private static final String procName = "checkfileupload" ;
		ChkBeforeClass(JdbcTemplate  template  ){
			super(template, procName );
			declareParameter(new SqlParameter("P_FILEID", Types.INTEGER));
			declareParameter(new SqlParameter("P_SUB_FILEID", Types.INTEGER));
			declareParameter(new SqlParameter("PP_FILENAME", Types.VARCHAR));
			declareParameter(new SqlParameter("P_FILEDATE", Types.VARCHAR));
			declareParameter(new SqlOutParameter("MSG", Types.VARCHAR));
			compile();
		}
	}
	
	@Override
	public boolean insertUploadTRAN (CompareSetupBean setupBean) {
		String query="";
		
		String BATCHNO = "TO_CHAR(TO_DATE('"+setupBean.getFILEDATE() +"', 'dd/mm/rrrr'), 'ddmmrr') || "+setupBean.getFILEID()+"||"+setupBean.getSUB_FILEID()+" || "+setupBean.getP_FILE_NAME()+""   ;
		
		try{
			
			query = "INSERT INTO  FILE_UPLOAD_MASTER (ID, FILEID, SUB_FILEID ,P_FILE_NAME ) VALUES("+BATCHNO+" , "+setupBean.getFILEID()+","+setupBean.getSUB_FILEID()+"  ,"+setupBean.getP_FILE_NAME()+"  )" ;
					
			int count = getJdbcTemplate().update(query);
			
			if(count > 0)
				{
					query	=" INSERT INTO   FILE_UPLOAD_TRANS(ID,FILEDATE) VALUES ("+BATCHNO+", TO_DATE( '"+setupBean.getFILEDATE() +"' , 'dd/mm/rrrr') )";	
					
					 count = getJdbcTemplate().update(query);
				}
			
			
			if(count >0) {
				
				return true;
			} else {
				
				return false;
			}
			
		/*	inParams.put("P_FILEID", setupBean.getFILEID() );
			inParams.put("P_SUB_FILEID", setupBean.getSUB_FILEID());
			inParams.put("PP_FILENAME", setupBean.getP_FILE_NAME());
			inParams.put("P_FILEDATE", setupBean.getFileDate());*/
			
			
		}catch(Exception ex) {
			
			ex.printStackTrace();
			
			return false;
		}
		
		
		
		
		
	
		}
	

	@Override
	public boolean insertFileTranDate(CompareSetupBean setupBean) {
		String sql ="";
		
		try {
			sql = "INSERT FILE_UPLOAD_TRANS ( ID,FILEDATE)VALUES ( ?, ?)";
			System.out.println("sql::>>>>>>>>>>>>"+sql);
		  int  row  = getJdbcTemplate().update(sql, setupBean.getFILEID(),setupBean.getFILEDATE());
		 System.out.println(row + " row inserted.");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return false;
	}
	
	
	public boolean updateFlag2(CompareSetupBean setupBean) {
		
		try{
			int rowupdate=0;
			int rowupdate1=0;
			int count1=0;
		OracleConn conn = new OracleConn();
		
		String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'VISA' and FILE_CATEGORY='WCC'   ";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs = st.executeQuery(switchList);
		//int count1=0;
		
		while(rs.next())
		{
		
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG) "
				+ "values ("+rs.getString("FILEID")+",to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','"+rs.getString("file_subcategory")+"'"
						+ ",'"+upload_flag+"','N','N','N','N','"+man_flag+"')";
		
		
			con = conn.getconn();
			st = con.createStatement();
			st.executeUpdate(query);
			
			count1=count1+1;
			String insert_count="update Main_File_Upload_Dtls set file_count='"+count1+"'  WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'MM/dd/yyyy') "
				+ " AND CATEGORY = 'WCC' AND FileId = '58'";
			con = conn.getconn();
			st = con.createStatement();
			st.executeUpdate(insert_count);
			
     String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'MM/dd/yyyy') "
				+ " AND CATEGORY = 'WCC' AND FileId = '58'   ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs1 = st.executeQuery(count);
			
			
			while(rs1.next())
			{
				value=rs1.getString("file_count");
				if(value.equals("3"))
				{
					String query1="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'mm/dd/yyyy') "
							+ " AND CATEGORY = 'WCC' AND FileId ='58'"; 
					
					
					con = conn.getconn();
					st = con.createStatement();
					 rowupdate =  st.executeUpdate(query1);
				}
				else{
					String query2="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='N'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'mm/dd/yyyy') "
							+ " AND CATEGORY = 'WCC' AND FileId ='58' "; 
					
					
					con = conn.getconn();
					st = con.createStatement();
					 rowupdate1 =  st.executeUpdate(query2);
				}
			}
			
		}
			
			
			return true; 
		}catch(Exception ex){
			
	
			System.out.println(ex);
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return false;
		}finally{
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
private int getFileCount(CompareSetupBean setupBean) {
		
		try {
				int count = 0;
				OracleConn conn = new OracleConn();
				String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/MM/yyyy'),'mm/dd/yyyy') "
						+ " AND CATEGORY = '"
						+ setupBean.getCategory()
						+ "' AND FileId ='58'";
	
				con = conn.getconn();
	
				st = con.createStatement();
	
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
	
					count = rs.getInt(1);
	
				}
	
				return count;
	
			} catch (Exception ex) {
	
				ex.printStackTrace();
	
				return -1;
	
			} finally {
	
				try {
					st.close();
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e.getMessage());
				}
	
			}
			
	}
public boolean updateFlag1(CompareSetupBean setupBean) {
	
	try{
	
		
	OracleConn conn = new OracleConn();
	
	String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'VISA' and FILE_CATEGORY='WCC'  ";
	
	con = conn.getconn();
	st = con.createStatement();
	
	ResultSet rs = st.executeQuery(switchList);
	
	int rowupdate=0;
	int rowupdate1=0;
	while(rs.next())
	{
	int val=0;
	String query="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'mm/dd/yyyy') "
			+ " AND CATEGORY = 'WCC' AND FileId = '58' "; 
	
	
	con = conn.getconn();
	st = con.createStatement();
	 rowupdate =  st.executeUpdate(query);
	 String count1 = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'MM/dd/yyyy') "
				+ " AND CATEGORY = 'WCC' AND FileId = '58'   ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs12 = st.executeQuery(count1);
			
			
			while(rs12.next())
			{
				val=rs12.getInt("file_count");
				val=val+1;
			}
		String insert_count="update Main_File_Upload_Dtls set file_count='"+val+"'  WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'MM/dd/yyyy') "
			+ " AND CATEGORY = 'WCC' AND FileId = '58'  ";
		con = conn.getconn();
		st = con.createStatement();
		st.executeUpdate(insert_count);
		
 String count = "SELECT file_count from Main_File_Upload_Dtls WHERE to_char(filedate,'MM/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'MM/dd/yyyy') "
			+ " AND CATEGORY = 'WCC' AND FileId = '58'   ";
		
		con = conn.getconn();
		st = con.createStatement();
		
		ResultSet rs1 = st.executeQuery(count);
		
		
		while(rs1.next())
		{
			value=rs1.getString("file_count");
			if(value.equals("3"))
			{
				String query1="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG ='Y'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'mm/dd/yyyy') "
						+ " AND CATEGORY = 'WCC' AND FileId = '58' "; 
				
				
				con = conn.getconn();
				st = con.createStatement();
				 rowupdate =  st.executeUpdate(query1);
			}
			else{
				String query2="Update MAIN_FILE_UPLOAD_DTLS set UPLOAD_FLAG='N'  WHERE to_char(filedate,'mm/dd/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/MM/yyyy'),'mm/dd/yyyy') "
						+ " AND CATEGORY = 'WCC' AND FileId = '58' "; 
				
				
				con = conn.getconn();
				st = con.createStatement();
				 rowupdate1 =  st.executeUpdate(query2);
			}
		}
	
	}
	
	if(rowupdate>0) {
		
		
		return true;
		
	}else{
		
		return false;
	}
	}catch(Exception ex){
		
		ex.printStackTrace();
		logger.error(ex.getMessage());
		return false;
	}finally {
		
		try {
			st.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}

@Override
public HashMap<String , Object>  checkUploadedFileName(String category, String subCateg, String fileName)
{
	HashMap<String , Object>  output = new HashMap<String , Object>();
	
	try
	{
		if(category.equalsIgnoreCase("NFS"))
		{
			if(subCateg.equalsIgnoreCase("ISSUER"))
			{
				if(!fileName.contains("ISSR"))
				{
					output.put("result", false);
					output.put("msg", "File Selected for uploading is Incorrect");
				}
				else
				{
					output.put("result", true);
				}
			}
			else if(subCateg.equalsIgnoreCase("ACQUIRER"))
			{
					if(!fileName.contains("ACQR"))
					{
						output.put("result", false);
						output.put("msg", "File Selected for uploading is Incorrect");
					}
					else
					{
						output.put("result", true);
					}
			}
			else
			{
				output.put("result", true);
			}
		}
		else if(category.equalsIgnoreCase("CASHNET"))
		{
			if(subCateg.equalsIgnoreCase("ISSUER"))
			{
				if(!fileName.contains("S"))
				{
					output.put("result", false);
					output.put("msg", "File Selected for uploading is Incorrect");
				}
				else
				{
					output.put("result", true);
				}
			}
			else if(subCateg.equalsIgnoreCase("ACQUIRER"))
			{
				System.out.println("check this "+fileName.substring(3,4));
				if(!fileName.substring(3,4).contains("C"))
				{
					output.put("result", false);
					output.put("msg", "File Selected for uploading is Incorrect");
				}
				else
				{
					output.put("result", true);
				}
			}
			
		}
		else
		{
			output.put("result", true);
		}
	}
	catch(Exception e)
	{
		logger.info("Exception in checkUploadedFileName "+e);
		output.put("result", false);
		output.put("msg", "Exception Occurred in checkUploadedFileName");
	}
	return output;
}

public HashMap<String , Object>  checkUploadedCycle(String filedate, String subCateg, String fileName, String phyFileName)
{
	HashMap<String , Object> output = new HashMap<>();
	String checkUpload = null, cycle = "0";
	int uploadedcount = 0;
	try
	{
		if(fileName.equals("NFS"))
		{
			String[] fileNames = phyFileName.split("_");
			
			String fileDate = fileNames[0].substring(fileNames[0].length()-6, fileNames[0].length());
			String[] splitDate = filedate.split("/");
			String year = splitDate[0].substring(splitDate[0].length()-2,splitDate[0].length());
			String compareDate = splitDate[2]+splitDate[1]+year;
			
			if(fileDate.equals(compareDate)){
			if(subCateg.equalsIgnoreCase("ISSUER"))
			{
//				String[] fileNames = phyFileName.split("_");
				if(fileNames.length >0)
					cycle = fileNames[1].substring(0,1);

				logger.info("cycle is "+cycle);
				checkUpload= "select count(1) from nfs_nfs_iss_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and cycle = ?";
				uploadedcount = getJdbcTemplate().queryForObject(checkUpload, new Object[]{filedate, cycle}, Integer.class);
				
				if(uploadedcount == 0)
				{
					output.put("result", true);
					return output;
				}
				else
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
			}
			else if(subCateg.equalsIgnoreCase("ACQUIRER"))
			{

//				String[] fileNames = phyFileName.split("_");
				if(fileNames.length >0)
					cycle = fileNames[1].substring(0,1);

				
				
				logger.info("cycle is "+cycle);
				checkUpload= "select count(1) from nfs_nfs_acq_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and cycle = ?";
				uploadedcount = getJdbcTemplate().queryForObject(checkUpload, new Object[]{filedate, cycle}, Integer.class);
				
				if(uploadedcount == 0)
				{
					output.put("result", true);
					return output;
				}
				else
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
			
			}
			}else{
				output.put("result", false);
				output.put("msg", "File date don't compare with selected date.");
				return output;
			}
		}
		else if(fileName.equals("RUPAY"))
		{
			logger.info("file name is "+phyFileName);
			phyFileName = phyFileName.replace(".xml", "");
			phyFileName = phyFileName.replace("_xml", "");
			
			checkUpload = "select count(1) from rupay_rupay_rawdata where filedate = str_to_Date(?,'%Y/%m/%d') "
					+ "and unique_file_name like '%"+phyFileName+"%'";
			uploadedcount = getJdbcTemplate().queryForObject(checkUpload, new Object[]{filedate}, Integer.class);
			
			if(uploadedcount == 0)
			{
				output.put("result", true);
				return output;
			}
			else
			{
				output.put("result", false);
				output.put("msg", "File is already uploaded");
				return output;
			}
			
		}
		else if(fileName.equals("CASHNET"))
		{
			String[] fileNames = phyFileName.split("_");
			if(fileNames.length >0)
				cycle = fileNames[1].substring(0,1);
			
			String fileDate = fileNames[0].substring(fileNames[0].length()-6, fileNames[0].length());
			String[] splitDate = filedate.split("/");
			String year = splitDate[0].substring(splitDate[0].length()-2,splitDate[0].length());
			String compareDate = splitDate[2]+splitDate[1]+year;
			
			if(fileDate.equals(compareDate)){
				if(subCateg.equalsIgnoreCase("ISSUER"))
				{
					checkUpload = "select count(1) from cashnet_cashnet_iss_rawdata where "
							+ "filedate = str_to_Date(?,'%Y/%m/%d') "
							+ "and cycle = '"+cycle+"'";
					uploadedcount = getJdbcTemplate().queryForObject(checkUpload, new Object[]{filedate}, Integer.class);

					if(uploadedcount == 0)
					{
						output.put("result", true);
						return output;
					}
					else
					{
						output.put("result", false);
						output.put("msg", "File is already uploaded");
						return output;
					}
				}
				else
					if(subCateg.equalsIgnoreCase("ACQUIRER"))
					{
						checkUpload = "select count(1) from cashnet_cashnet_acq_rawdata where "
								+ "filedate = str_to_Date(?,'%Y/%m/%d') "
								+ "and cycle = '"+cycle+"'";
						uploadedcount = getJdbcTemplate().queryForObject(checkUpload, new Object[]{filedate}, Integer.class);

						if(uploadedcount == 0)
						{
							output.put("result", true);
							return output;
						}
						else
						{
							output.put("result", false);
							output.put("msg", "File is already uploaded");
							return output;
						}
					}
			}else{
					output.put("result", false);
					output.put("msg", "File date don't compare with selected date.");
					return output;
				}
		}
		else if(fileName.equals("SWITCH"))
		{
			if(phyFileName.contains("txt"))
			{
				String checkData = "select count(1) from switch_rawdata where filedate = str_to_date(?,'%Y/%m/%d')";
				int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[]{filedate},Integer.class);
				
				if(dataCount == 0)
				{
					output.put("result", true);
					return output;
				}
				else
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
			}
			else
			{
				String checkData = "select count(1) from switch_rawdata_ags where filedate = str_to_date(?,'%Y/%m/%d')";
				int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[]{filedate},Integer.class);
				
				if(dataCount == 0)
				{
					output.put("result", true);
					return output;
				}
				else
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
			
				
			}
		}
		else if(fileName.equals("CBS"))
		{
			if(phyFileName.contains("Atm_Pos"))
			{
				String checkData = "select count(1) from cbs_dhana_rawdata_temp where filedate = str_to_date(?,'%Y/%m/%d')";
				int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[]{filedate},Integer.class);
				
				if(dataCount >0)
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
				else
				{
					output.put("result", true);
					return output;
				}
			}
			else if(phyFileName.contains("GL_Recon"))
			{

				String checkData = "select count(1) from cbs_dhana_recon_gl_rawdata where filedate = str_to_date(?,'%Y/%m/%d')";
				int dataCount = getJdbcTemplate().queryForObject(checkData, new Object[]{filedate},Integer.class);
				
				if(dataCount >0)
				{
					output.put("result", false);
					output.put("msg", "File is already uploaded");
					return output;
				}
				else
				{
					output.put("result", true);
					return output;
				}
			
				
			}
			else
			{
				output.put("result", false);
				output.put("msg", "Invalid File Uploaded");
			}
		}
		else
		{
			output.put("result", true);
			return output;
		}
	}
	catch(Exception e)
	{
		logger.info("Exception ocurred while validating uploaded cycles ");
		output.put("result", false);
		output.put("msg", "Exception Occurred while validating uplaoded file");
	}
	return output;
}

}
