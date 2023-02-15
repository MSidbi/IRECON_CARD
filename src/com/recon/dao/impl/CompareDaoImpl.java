package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.ADD_RECORDS;
import static com.recon.util.GeneralUtil.CREATE_RECON_TABLE;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_COMPARE_CONDITION;
import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_CRITERIA;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_PARAMS;
import static com.recon.util.GeneralUtil.GET_MATCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_RECON_CONDITION;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_TTUM_PARAMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.CompareDao;
import com.recon.dao.impl.ReconProcessDaoImpl.IssClassificaton;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.util.demo;


@Component
public class CompareDaoImpl extends JdbcDaoSupport implements CompareDao{

	public static String GET_DATA = "";
	public static String PART2_QUERY = "";
	private String JOIN1_QUERY = "";
	private String JOIN2_QUERY = "";
//	private String UPDATE1_QUERY = "";
//	private String UPDATE2_QUERY = "";
	private String INSERT1_QUERY ="";
	private String INSERT2_QUERY = "";
	private String KNOCKOFF1_COMPARE = "";
	private String KNOCKOFF2_COMPARE = "";
	public int SEG_TRAN_ID = 0;
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";
	
/*@Override
public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id)
{
	try
	{
		int loop = 0;
		while(loop < tables.size())
		{
			int file_id = 0;
			List<Integer> reversalid = new ArrayList<>();

			if(loop == 0)
			{
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { tables.get(loop),compareBeanObj.getStCategory(),
					compareBeanObj.getStSubCategory()},Integer.class);
			reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), compareBeanObj.getStCategory()},new ReversalId());
			}
			else
			{
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { tables.get(loop),compareBeanObj.getStCategory(),
						compareBeanObj.getStTable2_SubCategory()},Integer.class);			
				reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), compareBeanObj.getStCategory()
							+"_"+compareBeanObj.getStSubCategory()},new ReversalId());
			}
			logger.info("file Id is "+file_id);
			//1. GET CONDITION FOR ORIGINAL TRANSACTION FROM MAIN_MATCHING_CONDITION
			String compare_cond = getCompareCondition(compareBeanObj,tables.get(loop),inrec_set_id);
			logger.info("compare condition is "+compare_cond);

			//GET COLUMNS OF TEMP TABLE
			List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS, new Object[] {"TEMP_"+compareBeanObj.getStMergeCategory()
					+"_"+tables.get(loop)},new ColumnMapper());
			String Selec_cols = "";
			int count = 0;
			for(String headers : TABLE_COLS)
			{
				if(count == 0)
				{
					Selec_cols = headers;
					count++;
				}
				else
				{
					Selec_cols = Selec_cols +","+headers;
				}
			}
			for(int reverse_id :reversalid)
			{

				//2. GET KNOCKOFF CONDITION FROM MAIN_KNOCKOFF_CRITERIA
				List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reverse_id , file_id}, new KnockOffCriteriaMaster());
				String knockoff_condition = getCondition(knockoff_Criteria);
				logger.info("knockoff_ condition is "+knockoff_condition);

				String MOVE_QUERY = "INSERT INTO SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+"(DCRS_REMARKS,"+Selec_cols+") "
								+"SELECT '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inrec_set_id+"',"+Selec_cols
								+" FROM TEMP_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+
								" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM TEMP_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+
								" OS2 WHERE "+knockoff_condition +") AND "+compare_cond+" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'";

				String DELETE_RECORDS = "DELETE FROM TEMP_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
								+"TEMP_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+" OS2 WHERE "+knockoff_condition+") AND "
								+compare_cond+" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'";

				logger.info("MOVE_QUERY IS "+MOVE_QUERY);
				logger.info("DELET_RECORDS "+DELETE_RECORDS);

				getJdbcTemplate().execute(MOVE_QUERY);
				getJdbcTemplate().execute(DELETE_RECORDS);


				String MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
						" OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON'" +
						" WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED' " +
						"AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
						" OS2 WHERE OS2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED' AND "
						+knockoff_condition +") AND "+compare_cond
						+" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'";

				String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
						+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED OS2 WHERE "+knockoff_condition+") AND "
						+compare_cond+" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'";

				logger.info("update duplicate query is "+MOVE_QUERY);

				logger.info("delete query is "+DELETE_RECORDS);
				getJdbcTemplate().execute(MOVE_QUERY);
				getJdbcTemplate().execute(DELETE_RECORDS);


			}
			loop++;
		}
	}
	catch(Exception e)
	{
		logger.info("Exception in removeDuplicate() "+e);
	}
}*/
	
//CHANGES MADE BY INT5779 FOR ADDING PROCEDURE FOR REMOVING DUPLICATES
@Override
public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception 
{
	logger.info("***** CompareDaoImpl.removeDuplicates Start ****");
	try
	{
		int loop = 0;
		while(loop < tables.size())
		{
			logger.info("START time FOR Removing Duplicates "+new java.sql.Timestamp(new java.util.Date().getTime()));
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { tables.get(loop),compareBeanObj.getStCategory()
						,compareBeanObj.getStSubCategory() },Integer.class);
			Map<String,Object> inParams = new HashMap<>();
			inParams.put("I_FILE_NAME", tables.get(loop));
			inParams.put("I_FILE_ID", file_id);
			inParams.put("I_FILE_DATE", compareBeanObj.getStFile_date());
			inParams.put("I_CATEGORY", compareBeanObj.getStCategory());
			inParams.put("I_SUBCATEGORY", compareBeanObj.getStSubCategory());
			inParams.put("I_MERGER_CATEGORY", compareBeanObj.getStMergeCategory());
			

			RemoveDuplicate execInsert = new RemoveDuplicate(getJdbcTemplate());
			Map<String, Object> outParams = execInsert.execute(inParams);
			logger.info("END time FOR Removinf Duplicates "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			if (outParams.get("ERROR_MESSAGE") != null) {

				
			}
			loop++;
		}	
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareDaoImpl.removeDuplicates");
		 logger.error(" error in CompareDaoImpl.removeDuplicates", new Exception("CompareDaoImpl.removeDuplicates",e));
		 throw e;
	}
	logger.info("***** CompareDaoImpl.removeDuplicates End ****");
	
}	

private class RemoveDuplicate extends StoredProcedure{
	private static final String insert_proc = "REMOVE_DUPLICATE";
	public RemoveDuplicate(JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate,insert_proc);
		setFunction(false);
		declareParameter(new SqlParameter("I_FILE_NAME",Types.VARCHAR));
		declareParameter(new SqlParameter("I_FILE_ID",Types.VARCHAR));
		declareParameter(new SqlParameter("I_FILE_DATE",Types.VARCHAR));
		declareParameter(new SqlParameter("I_CATEGORY",Types.VARCHAR));
		declareParameter(new SqlParameter("I_SUBCATEGORY",Types.VARCHAR));
		declareParameter(new SqlParameter("I_MERGER_CATEGORY",Types.VARCHAR));
		declareParameter(new SqlOutParameter(O_ERROR_CODE,Types.INTEGER));
		declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
		compile();
	}
	
}

private class ReversalId implements RowMapper<Integer>
{
	@Override
	public Integer mapRow(ResultSet rset, int row)throws SQLException
	{
		int i = Integer.parseInt(rset.getString("REVERSAL_ID"));
		return i;
	}
	
}	
	public String getStatus(String stCategory, String stFileName,String stProcess) throws Exception
	{
		logger.info("***** CompareDaoImpl.getStatus Start ****");
		try
		{
			String GET_STATUS = "SELECT "+stProcess+" FROM MAIN_FILESOURCE WHERE FILEID = ?";
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFileName,stCategory},Integer.class);
			logger.info("GET_STATUS=="+GET_STATUS);
			logger.info("file_id=="+file_id);
			String chFilter_Status = getJdbcTemplate().queryForObject(GET_STATUS, new Object[] {file_id}, String.class);
			logger.info("chFilter_Status=="+chFilter_Status);
			logger.info("***** CompareDaoImpl.getStatus End ****");
			return chFilter_Status;
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.getStatus");
			logger.error(" error in   CompareDaoImpl.getStatus ", new Exception("  CompareDaoImpl.getStatus ",e));
			return "N";
			
		}
		
	}
	
	@Override
	public List<Integer> getRec_set_id(String stCategory)throws Exception
	{
		logger.info("***** CompareDaoImpl.getRec_set_id Start ****");
		try
		{
			String GET_RECON_ID = "SELECT REC_SET_ID FROM MAIN_RECON_SEQUENCE WHERE RECON_CATEGORY = ? ORDER BY REC_SET_ID" ;
			List<Integer> rec_set_id = getJdbcTemplate().query(GET_RECON_ID, new Object[] {stCategory}, new recSetIDMapper());
			logger.info("");
			
			logger.info("***** CompareDaoImpl.getRec_set_id End ****");
			
			return rec_set_id;
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.getRec_set_id");
			logger.error(" error in   CompareDaoImpl.getRec_set_id ", new Exception("  CompareDaoImpl.getRec_set_id ",e));
			 
			return null;
			
		}
		
	}
	
	private static class recSetIDMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			
			int rec_id_list = (rs.getInt("REC_SET_ID"));
			
			return rec_id_list;
			
			

		}
	}
	
@Override
public List<String> getTableName(int inRec_Set_Id,String stCategory)throws Exception
{
	logger.info("***** CompareDaoImpl.getTableName Start ****");
		try
		{
			String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND RECON_CATEGORY = '"+stCategory+"'";
			
			List<CompareBean> comparebeanObj = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id}, new GetTableDetails());
					
			List<String> table_list = new ArrayList<>();
			CompareBean compareObj = comparebeanObj.get(0);
			//table_list.add(compareObj.getStCategory()+"-"+compareObj.getStTable1());
			//table_list.add(compareObj.getStCategory()+"-"+compareObj.getStTable2());
			
			table_list.add(compareObj.getStTable1());
			table_list.add(compareObj.getStTable2());
			
			logger.info("***** CompareDaoImpl.getTableName End ****");
			return table_list;
	
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.getTableName");
			logger.error(" error in   CompareDaoImpl.getTableName ", new Exception("  CompareDaoImpl.getTableName ",e));
			return null;
		}
	}
	
	private static class GetTableDetails implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean comparebeanObj = new CompareBean();
			
			comparebeanObj.setStTable1(rs.getString("FILE1"));
			comparebeanObj.setStTable2(rs.getString("FILE2"));
			comparebeanObj.setStCategory(rs.getString("CATEGORY"));
			comparebeanObj.setStMatched_file1(rs.getString("FILE1_MATCHED"));
			comparebeanObj.setStMatched_file2(rs.getString("FILE2_MATCHED"));
			return comparebeanObj;
			
			

		}
	}
	
	@Override
	public int moveData(List<String> tables,CompareBean compareBean,int inRec_Set_id) throws Exception
	{
		logger.info("***** CompareDaoImpl.moveData Start ****");
		logger.info("FILEDATE IN moveData is "+compareBean.getStFile_date());
	//	String tableCols = "MATCHING_FLAG CHAR(1 BYTE), SEG_TRAN_ID NUMBER , KNOCKOFF_FLAG CHAR(1 BYTE)";
		String tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE";
		Connection conn;
		PreparedStatement pstmt ;
		ResultSet rset;
		//String columns="SEG_TRAN_ID,KNOCKOFF_FLAG";
		String columns;
		//String condition = "";
		String temp_param = "";
		int tableExist = 0;
		String CHECK_TABLE ="";
		String stMatch_param = "";
		
		try
		{
			
			conn = getConnection();
		//*********1. CREATE TEMP TABLE
				//get table cols
			int loop = 0;
			while(loop < tables.size())
			{
				//condition = "";
				stMatch_param = "";
				temp_param = "";
				columns ="SEG_TRAN_ID";
				tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE ";
			//	String table_name = tables.get(loop);
			//	logger.info("TABLE NAME IS "+table_name);
				//String a[] = table_name.split("_");
				//String table = a[1]+"_DATA";
				String stFile_Name = tables.get(loop).toUpperCase();
			//logger.info("get cols query is "+GET_COLS );
			
			
			///1. GET THE HEADERS FROM DB
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_Name ,compareBean.getStCategory() , compareBean.getStSubCategory() },Integer.class);
			logger.info("file Id is "+file_id);
			
			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
			columns = columns+","+stFile_headers;
			logger.info("stfile headers are "+stFile_headers);
			
			if(stFile_Name.equals("CBS"))
			{
				stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
				columns = columns +",MAN_CONTRA_ACCOUNT";
			}
			
			String[] col_names = stFile_headers.trim().split(",");
			for(int i=0 ;i <col_names.length; i++)
			{
				tableCols = tableCols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
			}
			
			
			//logger.info("table columns are "+tableCols);
			//logger.info("columns are "+columns);
			pstmt = null;
			rset = null;
			
			//CHECKING WHETHER TABLE IS ALREADY PRESENT
			CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+compareBean.getStMergeCategory()+"_"+stFile_Name+"'";
			logger.info("CHECK_TABLE== "+CHECK_TABLE);
			tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			logger.info("table exists value is "+tableExist);
			if(tableExist == 0)
			{
				//create temp table
				String query = "CREATE TABLE TEMP_"+compareBean.getStMergeCategory()+"_"+stFile_Name+"("+tableCols+")";
				logger.info("CREATE QUERY IS "+query);
				getJdbcTemplate().execute(query);
				
			}
			
				//CHECKING WHETHER MATCHED RECORDS TABLE IS PRESENT
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+compareBean.getStMergeCategory()+"_"+stFile_Name+"_MATCHED'";
				logger.info("CHECK_TABLE== "+CHECK_TABLE);
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				logger.info("table exists value is "+tableExist);
				if(tableExist == 0)
				{
					//create temp table
					String query = "CREATE TABLE "+compareBean.getStMergeCategory()+"_"+stFile_Name+"_MATCHED "+"("+tableCols+")";
					logger.info("CREATE QUERY IS "+query);
					/*pstmt = conn.prepareStatement(query);
					pstmt.execute();

					pstmt = null;
*/
					getJdbcTemplate().execute(query);
				}

			
			
//--------------------------------------------MOVING DATA TO TEMP TABLE--------------------------------------------------------------------------------------
				//**********2. GET THE DATA FROM ORIGINAL TABLE
				//get the condition for getting original transactions from table

				//GET_DATA = "SELECT * FROM "+table_name + " WHERE KNOCKOFF_FLAG = 'N'";
				//GET_DATA = "SELECT * FROM "+compareBean.getSt + " OS1 WHERE ";
				//CREATEDDATE,CREATEDBY,FILEDATE,
				GET_DATA = "SELECT SYSDATE,'"+compareBean.getStEntryBy()+"',TO_DATE('"+compareBean.getStFile_date()+"','DD/MM/YYYY'),"+columns//entry by is going null
							+" FROM "+compareBean.getStMergeCategory()+"_"+stFile_Name + " OS1 WHERE ";


				//GET PARAMETERS FOR CONSIDERING THE RECORDS FOR COMPARE
				stMatch_param = getReconParameters(compareBean,stFile_Name,inRec_Set_id);
				//logger.info("stMatch_param== "+stMatch_param);
				
				if(!stMatch_param.equals(""))
				{
					//GET_DATA = GET_DATA +" AND "+ stMatch_param;
					//GET_DATA = GET_DATA + stMatch_param + "  AND NOT EXISTS ( ";
					GET_DATA = GET_DATA + stMatch_param + " AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE) " +
							"AND OS1.FILEDATE = '"+compareBean.getStFile_date()+"' AND NOT EXISTS ( ";
				}


				//logger.info("PART1 QUERY ::::::::::::::::::::::::: "+GET_DATA);
				
				int reversal_id;
				
				if(!compareBean.getStSubCategory().equals("-"))
				{
					//get part 2 of query
					reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), compareBean.getStCategory()+"_"+compareBean.getStSubCategory()},Integer.class);
					logger.info("reversal_id== "+reversal_id);
				}
				else
				{

					//get part 2 of query
					reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), compareBean.getStCategory()},Integer.class);
					logger.info("reversal_id== "+reversal_id);

				}
				List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
				logger.info("knockoff_Criteria== "+knockoff_Criteria);
				
				String part2_condition = getCondition(knockoff_Criteria);

				//logger.info("PART 2 CONDITION IS "+part2_condition);

				PART2_QUERY = "SELECT * FROM "+compareBean.getStMergeCategory()+"_"+stFile_Name+"_KNOCKOFF OS2 ";
				if(!part2_condition.equals(""))
				{
					PART2_QUERY = PART2_QUERY + " WHERE ( " + part2_condition + ")";
				}

				//logger.info("part2 query is "+PART2_QUERY);

				//GET_DATA = GET_DATA + PART2_QUERY + " )";
				GET_DATA = GET_DATA + PART2_QUERY + " AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE) " +
						"AND OS2.FILEDATE = '"+compareBean.getStFile_date()+"' )";

				logger.info("Final query is "+GET_DATA);

			
				//conn = getConnection();
				pstmt = conn.prepareStatement(GET_DATA);
				rset = pstmt.executeQuery();
				//logger.info("GOT THE UNKNOCKED DATA");
				//. MOVE THE DATA IN TEMP TABLE
				String[] cols = columns.split(",");
				ADD_RECORDS = "INSERT INTO TEMP_"+compareBean.getStMergeCategory()+"_"+stFile_Name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") "+GET_DATA;
				
				
				
				/*ADD_RECORDS = "INSERT INTO TEMP_"+compareBean.getStMergeCategory()+"_"+stFile_Name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
						"TO_DATE('"+compareBean.getStFile_date()+"','DD/MM/YYYY')";*/

				/*for(int i = 1 ; i<= cols.length; i++)
				{
					ADD_RECORDS = ADD_RECORDS + ",?";
				}

				ADD_RECORDS = ADD_RECORDS+")";*/
				logger.info("QUERY FOR ADDING RECORDS IN TEMP TABLE "+ADD_RECORDS);
				logger.info("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(ADD_RECORDS);
				//insertBatch(ADD_RECORDS,rset, cols);
				logger.info("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
				//-----------------------------NOW TRUNCATE ONUS AND KNOCKOFF TABLES
				/*String TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
				 */
			
			loop++;
			}
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.moveData");
			 logger.error(" error in   CompareDaoImpl.moveData ", new Exception("  CompareDaoImpl.moveData ",e));
			 throw e;
		}
		
		logger.info("***** CompareDaoImpl.moveData End ****");
		
		return 1;
		
		
	}
	
	private static class KnockOffCriteriaMaster implements RowMapper<KnockOffBean> {

		@Override
		public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			/*while(rs.next())
		{*/
			//logger.info("header is "+rs.getString("HEADER"));
			KnockOffBean knockOffBean = new KnockOffBean();

			knockOffBean.setStReversal_header(rs.getString("HEADER"));
			knockOffBean.setStReversal_padding(rs.getString("PADDING"));
			knockOffBean.setStReversal_charpos(rs.getString("START_CHARPOSITION"));
			knockOffBean.setStReversal_charsize(rs.getString("CHAR_SIZE"));
			knockOffBean.setStReversal_value(rs.getString("HEADER_VALUE"));
			knockOffBean.setStReversal_condition(rs.getString("CONDITION"));
			//knockOffBean.setStReversal_value(rs.getString("VALUE"));
			//filterBean.setStSearch_padding(rs.getString("PADDING"));
			//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
			//filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
			//filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));

			//search_params.add(filterBean);
			//	}
			return knockOffBean;


		}
	}
	
	public void updateMatchedRecords(List<String> Table_list,CompareBean CompareBeanObj,int inRec_Set_id) throws Exception
	{
		logger.info("***** CompareDaoImpl.updateMatchedRecords Start ****");
		//CompareBean compareBean = new CompareBean();
		//SELECT * FROM ONUS_SWITCH OS INNER JOIN ONUS_CBS OC  ON(OS.PAN = OC.REMARKS);
		String table1_condition = "";
		String table2_condition = "";
		ResultSet rset1=null;
		ResultSet rset2=null;
		ResultSet knock1_rset = null;
		ResultSet knock2_rset = null;
		Connection conn=null;
		PreparedStatement ps1 =null ;
		PreparedStatement ps2 = null;
		PreparedStatement knock1_ps = null;
		PreparedStatement knock2_ps = null;
		String table1_cols = "";
		String table2_cols = "";
		String[] table1_column;
		String[] table2_column;
		String SETTLEMENT_INSERT1 = "";
		String SETTLEMENT_INSERT2 = "";
		List<CompareBean> match_Headers1 = new ArrayList<>();
		List<CompareBean> match_Headers2 = new ArrayList<>();
		
		try
		{
			
			
			//String file1_name = "", file2_name = "";
			String condition = "";	
			//String table1_alias = "t1", table2_alias = "t2";
		//	String table1_name = Table_list.get(0);
			//logger.info("TABLE 1 NAME IS "+table1_name);
		//	String a[] = table1_name.split("_");
		//	file1_name = a[1];
			
			
		/*	String table2_name = Table_list.get(1);
			//logger.info("TABLE 2 NAME IS "+table2_name);
			a = table2_name.split("_");
			file2_name = a[1];*/
			
			String stFile1_Name = Table_list.get(0);
			String stFile2_Name = Table_list.get(1);
			
			
			//logger.info("file 1 name is "+file1_name);
			//logger.info("file 2 name is "+file2_name);
			
		//*******1. MATCH THE RECORDS AND TAKE THOSE IN RESULTSET
			//GET MATCHING PARAMETERS FROM DB
			
			
			
			logger.info("COMPARE STARTS HERE *************************************************");
			
			int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name, CompareBeanObj.getStCategory() , CompareBeanObj.getStSubCategory() },Integer.class);
			int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, CompareBeanObj.getStCategory() , CompareBeanObj.getStSubCategory() },Integer.class);
			logger.info("table1_file_id=="+table1_file_id);
			logger.info("table2_file_id=="+table2_file_id);
		//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster());
			if(CompareBeanObj.getStSubCategory().equals("-"))
			{
				match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,CompareBeanObj.getStCategory(),inRec_Set_id},new MatchParameterMaster1());
				match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,CompareBeanObj.getStCategory(),inRec_Set_id},new MatchParameterMaster2());
			
				logger.info("match_Headers1=="+match_Headers1);
				logger.info("match_Headers2=="+match_Headers2);
			}
			
		
			//prepare compare condition
			/*for(int i = 0; i<match_Headers1.size() ; i++)
			{
				//CHECKING PADDING FOR TABLE 1
				if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							table1_condition = "SUBSTR( TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'999999999.99')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name 
									+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
							logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
								
							}
							
							
						}
					}
					else
					{
						table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")";
	
					}	
				}
				else
				{	
					if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+CompareBeanObj.getStMergeCategory()+
									"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
								
							}
							
						}
						
					}
					else
					{
						table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();
	
					}	
				}
				
				//CHECKING PADDING FOR TABLE 2
				logger.info("i value is "+i);
				logger.info("match headers length is "+match_Headers2.size());
				logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());
				if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							table2_condition = " SUBSTR( TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM "
									+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								//ABC
								table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
								
							}
							else
							{
								table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
								
							}
							
						}
						
					}
					else
					{
						table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";
	
					}			
					
				}
				else
				{
					logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+CompareBeanObj.getStMergeCategory()+
									"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
								
							}
							else
							{
								table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
								
							}
							
						}
					}
					else
					{
						table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();
	
					}		
				
				}
				
				// PREPARING ACTUAL CONDITION OF BOTH TABLES
				if(i==(match_Headers1.size()-1))
				{
					
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
					condition = condition + table1_condition + " = "+table2_condition;
					
				}
				else
				{
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
					condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
				
				}
				
				
			}*/
			for(int i = 0; i<match_Headers1.size() ; i++)
			{
				//CHECKING PADDING FOR TABLE 1
				if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',','')"+","
									+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+	match_Headers1.get(i).getStMatchTable1_charSize()+"))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							/*table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";*/
							table1_condition = "TO_CHAR(TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" '),'DD/MM/YYYY')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name 
									+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
							logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
								
							}
							else
							{
								/*table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";*/
								table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")";
								
							}
							
							
						}
					}
					else
					{
						table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")";
	
					}	
				}
				else
				{	
					if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
							table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',',''))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							//table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
							table1_condition = " TO_CHAR(TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"'),'DD/MM/YYYY')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+CompareBeanObj.getStMergeCategory()+
									"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
								
							}
							
						}
						
					}
					else
					{
						table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();
	
					}	
				}
				
				//CHECKING PADDING FOR TABLE 2
				/*logger.info("i value is "+i);
				logger.info("match headers length is "+match_Headers2.size());
				logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
				if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',','')"+","
											+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+ match_Headers2.get(i).getStMatchTable2_charSize()+"))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							/*table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";*/
							table2_condition = "TO_CHAR(TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM "
									+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								//ABC
								table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
								
							}
							else
							{
								/*table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";*/
								
								table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")";
								
							}
							
						}
						
					}
					else
					{
						table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";
	
					}			
					
				}
				else
				{
					logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
							table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',',''))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							//table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
							table2_condition = " TO_CHAR(TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+CompareBeanObj.getStMergeCategory()+
									"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
								
							}
							else
							{
								table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
								
							}
							
						}
					}
					else
					{
						table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();
	
					}		
				
				}
				
				// PREPARING ACTUAL CONDITION OF BOTH TABLES
				if(i==(match_Headers1.size()-1))
				{
					
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
					condition = condition + table1_condition + " = "+table2_condition;
					
				}
				else
				{
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
					condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
				
				}
				
				
			}
			
			logger.info("condition=="+condition);
			
		//********* GETTING RECORDS IN RSET
			List<String> file1_selCols = getJdbcTemplate().query(GET_COLS,new Object[]{"TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name}, new ColumnMapper());
			String table1_selheaders = "";
			String join1_headers = "";
			int count = 0;
			for(String cols : file1_selCols)
			{
				if(count == 0)
				{
					count++;
					join1_headers = "t1."+cols;
					table1_selheaders = cols;
				}
				else
				{
					join1_headers = join1_headers+",t1."+cols;
					table1_selheaders = table1_selheaders+","+cols;
					
				}
			}
			
			List<String> file2_selheader = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name}, new ColumnMapper());
			String table2_selheader = "";
			String join2_headers = "";
			count = 0;
			for(String cols : file2_selheader)
			{
				if(count == 0)
				{
					count++;
					join2_headers = "t2."+cols;
					table2_selheader = cols;
				}
				else
				{
					join2_headers = join2_headers + ",t2."+cols;
					table2_selheader = table2_selheader +","+cols;
				}
			}
			
		//	JOIN1_QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
//			JOIN2_QUERY = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN TEMP_"+table1_name + " t1 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
			JOIN1_QUERY = "SELECT DISTINCT "+join1_headers+" FROM TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " t1 INNER JOIN TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " t2 ON( "+condition 
						+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND T1.FILEDATE = '"+CompareBeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND T2.FILEDATE = '"+CompareBeanObj.getStFile_date()+"'";
		/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
			JOIN2_QUERY = "SELECT DISTINCT "+join2_headers+" FROM TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " t2 INNER JOIN TEMP_"+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " t1 ON( "+condition 
						+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND T1.FILEDATE = '"+CompareBeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND T2.FILEDATE = '"+CompareBeanObj.getStFile_date()+"'";
			/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
			//GET WHERE CONDITION FROM MAIN_matching_CONDITION TABLE
			
			String compare_cond1 = getCompareCondition(CompareBeanObj,stFile1_Name,inRec_Set_id);
			String compare_cond2 = getCompareCondition(CompareBeanObj,stFile2_Name,inRec_Set_id);
			String compare_cond = compare_cond1 +" AND " + compare_cond2;
			if(!compare_cond.equals(""))
			{
				JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond;
				JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond;
			}
			
			logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");
			
			//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
			logger.info("COMPARE QUERY IS****************************************");
			logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
			logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
			
			logger.info("Query for comparing with knockoff records is "+KNOCKOFF1_COMPARE);
			logger.info("QUERY FOR COMPARING WITH KNOCKOFF RECORDS IS "+KNOCKOFF2_COMPARE);
				
			/*conn = getConnection();
			ps1 = conn.prepareStatement(JOIN1_QUERY);
			rset1 = ps1.executeQuery();//got matched records
			ps2 = conn.prepareStatement(JOIN2_QUERY);
			rset2 = ps2.executeQuery();
			
			//SETTLEMENT RECORDS
			PreparedStatement settlement_pstmt1 = conn.prepareStatement(JOIN1_QUERY);
			ResultSet settlement_set1 = settlement_pstmt1.executeQuery();
			PreparedStatement settlement_pstmt2 = conn.prepareStatement(JOIN2_QUERY);
			ResultSet settlement_set2 = settlement_pstmt2.executeQuery();*/
			
			
			//match temp with knockedoff records and get the resultset
			/*knock1_ps = conn.prepareStatement(KNOCKOFF1_COMPARE);
			knock1_rset = knock1_ps.executeQuery();
			knock2_ps = conn.prepareStatement(KNOCKOFF2_COMPARE);
			knock2_rset = knock2_ps.executeQuery();*/
			
			
	//---------------- MOVING THE OBTAINED RESULT INTO NEW TABLE-----------------------
			//1. CREATE INSERT QUERY
			
			INSERT1_QUERY = "INSERT INTO "+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name+"_MATCHED ("+table1_selheaders+") "
							+JOIN1_QUERY;
			//SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile1_Name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
			logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			getJdbcTemplate().execute(INSERT1_QUERY);
			logger.info("END time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			INSERT2_QUERY = "INSERT INTO "+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name+"_MATCHED ("+table2_selheader+") "
							+JOIN2_QUERY;
			
			logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			getJdbcTemplate().execute(INSERT2_QUERY);
			logger.info("END time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			//SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile2_Name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
			
			
			logger.info("INSERT2 QUERY IS "+INSERT2_QUERY);
			logger.info("SETTLEMENT INSERT2 QUERY "+SETTLEMENT_INSERT2);
			
			
			logger.info("INSERT1 QUERY IS "+INSERT1_QUERY);
			logger.info("SETTLEMENT INSERT1 QUERY IS "+SETTLEMENT_INSERT1);
			
			
			SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile1_Name+" (DCRS_REMARKS,"+table1_selheaders+") "
								+"SELECT '"+CompareBeanObj.getStMergeCategory()+"-MATCHED',"+table1_selheaders+" FROM "+CompareBeanObj.getStMergeCategory()+"_"
								+stFile1_Name+"_MATCHED";
			
			logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			logger.info("SETTLEMENT_INSERT1=="+SETTLEMENT_INSERT1);
			getJdbcTemplate().execute(SETTLEMENT_INSERT1);
			logger.info("END time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile2_Name+" (DCRS_REMARKS,"+table2_selheader+") "
								+" SELECT '"+CompareBeanObj.getStMergeCategory()+"-MATCHED',"+table2_selheader+" FROM "+CompareBeanObj.getStMergeCategory()+"_"
								+stFile2_Name+"_MATCHED";
			
			logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			logger.info("SETTLEMENT_INSERT2=="+SETTLEMENT_INSERT2);
			getJdbcTemplate().execute(SETTLEMENT_INSERT2);
			logger.info("END time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			/*INSERT1_QUERY = "INSERT INTO "+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name+"_MATCHED (CREATEDDATE,CREATEDBY,FILEDATE,";
			SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile1_Name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
			
			
			
			    //get columns
			String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);	
			String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
			if(stFile1_Name.equals("CBS"))
			{
				table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable1_headers;
			}
			else
				table1_cols = "SEG_TRAN_ID,"+stTable1_headers;
			INSERT1_QUERY = INSERT1_QUERY + table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+CompareBeanObj.getStFile_date()+"','DD/MM/YYYY')"+",?";
			SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 +table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+CompareBeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"
						+CompareBeanObj.getStMergeCategory()+"-MATCHED',?";
			
			table1_column = table1_cols.split(",");
			
			for(int i=0;i<(table1_column.length-1);i++)
			{
				INSERT1_QUERY = INSERT1_QUERY + ",?";
				SETTLEMENT_INSERT1 =SETTLEMENT_INSERT1 + ",?";
			}
			INSERT1_QUERY = INSERT1_QUERY +" )";
			SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 + " )";
			
			
			logger.info("INSERT1 QUERY IS "+INSERT1_QUERY);
			logger.info("SETTLEMENT INSERT1 QUERY IS "+SETTLEMENT_INSERT1);*/
			
			/*INSERT2_QUERY = "INSERT INTO "+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name+"_MATCHED (CREATEDDATE,CREATEDBY,FILEDATE,";
			SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+CompareBeanObj.getStCategory()+"_"+stFile2_Name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
			if(stFile2_Name.equals("CBS"))
			{
				table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable2_headers;
			}
			else
			{	
				table2_cols = "SEG_TRAN_ID,"+stTable2_headers;
			}
			INSERT2_QUERY = INSERT2_QUERY + table2_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+CompareBeanObj.getStFile_date()+"','DD/MM/YYYY')"+",?";
			SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + table2_cols+ ") VALUES(SYSDATE,'INT5779',TO_DATE('"+CompareBeanObj.getStFile_date()+"','DD/MM/YYYY')"
					+",'"+CompareBeanObj.getStMergeCategory()+"-MATCHED',?";
			table2_column = table2_cols.split(",");
			for(int i=0;i<(table2_column.length-1);i++)
			{
				INSERT2_QUERY = INSERT2_QUERY + ",?";
				SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + ",?";
			}
			INSERT2_QUERY = INSERT2_QUERY + ")";
			SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 +" )";
			
			logger.info("INSERT2 QUERY IS "+INSERT2_QUERY);
			logger.info("SETTLEMENT INSERT2 QUERY "+SETTLEMENT_INSERT2);*/
			
			/*logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(INSERT1_QUERY, rset1, table1_column);
			logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+CompareBeanObj.getStMergeCategory()+"_"+stFile1_Name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
			logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(INSERT2_QUERY, rset2, table2_column);
			logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+CompareBeanObj.getStMergeCategory()+"_"+stFile2_Name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
			//inserting in settlement table
			logger.info("start time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(SETTLEMENT_INSERT1, settlement_set1, table1_column);
			logger.info("end time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
			logger.info("start time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(SETTLEMENT_INSERT2, settlement_set2, table2_column);
			logger.info("end time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
			
			
			
//-------------------- NOW TRUNCATE TEMP AND MATCHED TABLES
			/*String TRUNCATE_TABLE1 = "TRUNCATE TABLE "+table1_name;
			getJdbcTemplate().execute(TRUNCATE_TABLE1);
			
			String TRUNCATE_TABLE2 = "TRUNCATE TABLE "+table2_name;
			getJdbcTemplate().execute(TRUNCATE_TABLE2);*/
//-------------------------------DONE----------------------------------------------------			
			
			//inserting compare result of knockoff and temp tables in matched table
			/*logger.info("start time FOR INSERTING KNOCKOFF MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(INSERT1_QUERY, knock1_rset, table1_column);
			logger.info("eND TIME FOR COMPLETING INSERTING KNOCKOFF MATCHED DATA"+table1_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
			logger.info("start time FOR INSERTING KNOCKOFF MATCHED DATA IN TABLE 2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(INSERT2_QUERY, knock2_rset, table2_column);
			logger.info("eND TIME FOR COMPLETING INSERTING KNOCKOFF MATCHED DATA IN "+table1_name+"_MATCHED : "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			*/
			//PREPARING UPDATE QUERY for TABLE 1
			/*String update1_condition = "";
			String update2_condition = "";
			UPDATE1_QUERY = "UPDATE TEMP_"+table1_name + " SET MATCHING_FLAG = 'Y' WHERE ";
			UPDATE2_QUERY = "UPDATE TEMP_"+table2_name + " SET MATCHING_FLAG = 'Y' WHERE ";
			for(int i =0 ;i<match_Headers.size(); i++)
			{
				if(i == (match_Headers.size()-1))
				{
					update1_condition = update1_condition + match_Headers.get(i).getStMatchTable1_header()+"= ? ";
					update2_condition = update2_condition + match_Headers.get(i).getStMatchTable2_header()+"= ? ";
					table1_cols = table1_cols + match_Headers.get(i).getStMatchTable1_header();
					table2_cols = table2_cols + match_Headers.get(i).getStMatchTable2_header();
					
				}
				else
				{	
					update1_condition = update1_condition + match_Headers.get(i).getStMatchTable1_header()+" = ? AND ";
					update2_condition = update2_condition + match_Headers.get(i).getStMatchTable2_header()+" = ? AND ";
					table1_cols = table1_cols + match_Headers.get(i).getStMatchTable1_header() +",";
					table2_cols = table2_cols + match_Headers.get(i).getStMatchTable2_header() + ",";
				}
			}
			

			
			//update table1 
			String[] columns = table1_cols.split(",");
			UPDATE1_QUERY = UPDATE1_QUERY + update1_condition;
			logger.info("TABLE 1 UPDATE query is "+UPDATE1_QUERY);
			logger.info("start time FOR UPDATING MATCHING RECORDS IN TABE1  "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(UPDATE1_QUERY,rset1, columns);
			logger.info("End time FOR UPDATING MATCHING RECORDS IN TABLE1  "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			//update table2 
			columns = table2_cols.split(",");
			UPDATE2_QUERY = UPDATE2_QUERY + update2_condition;
			logger.info("TABLE 2 update query is "+UPDATE2_QUERY);
			logger.info("start time FOR UPDATING MATCHING RECORDS IN TABE2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(UPDATE2_QUERY,rset2, columns);
			logger.info("End time FOR UPDATING MATCHING RECORDS IN TABLE2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			*/
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.updateMatchedRecords");
			logger.error(" error in CompareDaoImpl.updateMatchedRecords", new Exception("CompareDaoImpl.updateMatchedRecords",e));
			throw e;
			
		}
		finally
		{
			try{
				if(conn!=null)
				{
					ps1.close();
					ps2.close();
					rset1.close();
					conn.close();
					rset2.close();
				}
			}
			catch(Exception e)
			{
				demo.logSQLException(e, "CompareDaoImpl.updateMatchedRecords");
				 logger.error(" error in CompareDaoImpl.updateMatchedRecords", new Exception("CompareDaoImpl.updateMatchedRecords",e));
				 throw e;
			}
		}
		logger.info("***** CompareDaoImpl.updateMatchedRecords End ****");
		
		
	}
	
	private class ColumnMapper implements RowMapper<String>
	{
		@Override
		public String mapRow(ResultSet rs, int row)throws SQLException
		{
			return rs.getString("COLUMN_NAME");
			
		}
		
	}
	
	private static class OriginalParameterMaster implements RowMapper<KnockOffBean> {

		@Override
		public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			/*while(rs.next())
			{*/
				//logger.info("header is "+rs.getString("HEADER"));
				KnockOffBean knockOffBean = new KnockOffBean();
			
			knockOffBean.setStReversal_header(rs.getString("HEADER"));
			knockOffBean.setStReversal_value(rs.getString("VALUE"));
			//filterBean.setStSearch_padding(rs.getString("PADDING"));
			//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
			//filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
			//filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));
			
			//search_params.add(filterBean);
//			}
			return knockOffBean;
			
			
		}
		}
	
	
	
	private static class MatchParameterMaster1 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			
			
			/*compareBeanObj.setStMatchTable1_header(rs.getString("TABLE1_HEADER"));
			compareBeanObj.setStMatchTable2_header(rs.getString("TABLE2_HEADER"));
			compareBeanObj.setStMatchTable1_Padding(rs.getString("TABLE1_PADDING"));
			compareBeanObj.setStMatchTable2_Padding(rs.getString("TABLE2_PADDING"));
			compareBeanObj.setStMatchTable1_startcharpos(rs.getString("TABLE1_START_CHARPOS"));
			compareBeanObj.setStMatchTable2_startcharpos(rs.getString("TABLE2_START_CHARPOS"));
			compareBeanObj.setStMatchTable1_charSize(rs.getString("TABLE1_CHARSIZE"));
			compareBeanObj.setStMatchTable2_charSize(rs.getString("TABLE2_CHARSIZE"));
			compareBeanObj.setStMatch_Datatype(rs.getString("DATATYPE"));
			compareBeanObj.setStMatchTable1_DatePattern(rs.getString("TABLE1_PATTERN"));
			compareBeanObj.setStMatchTable2_DatePattern(rs.getString("TABLE2_PATTERN"));
			*/
			compareBeanObj.setStMatchTable1_header(rs.getString("MATCH_HEADER"));
			compareBeanObj.setStMatchTable1_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable1_startcharpos(rs.getString("START_CHARPOS"));
			compareBeanObj.setStMatchTable1_charSize(rs.getString("CHAR_SIZE"));
			compareBeanObj.setStMatchTable1_DatePattern(rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable1_Datatype(rs.getString("DATATYPE"));
		
			
			return compareBeanObj;
			
			
		}
	}
	
	private static class MatchParameterMaster2 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			
			
			/*compareBeanObj.setStMatchTable1_header(rs.getString("TABLE1_HEADER"));
			compareBeanObj.setStMatchTable2_header(rs.getString("TABLE2_HEADER"));
			compareBeanObj.setStMatchTable1_Padding(rs.getString("TABLE1_PADDING"));
			compareBeanObj.setStMatchTable2_Padding(rs.getString("TABLE2_PADDING"));
			compareBeanObj.setStMatchTable1_startcharpos(rs.getString("TABLE1_START_CHARPOS"));
			compareBeanObj.setStMatchTable2_startcharpos(rs.getString("TABLE2_START_CHARPOS"));
			compareBeanObj.setStMatchTable1_charSize(rs.getString("TABLE1_CHARSIZE"));
			compareBeanObj.setStMatchTable2_charSize(rs.getString("TABLE2_CHARSIZE"));
			compareBeanObj.setStMatch_Datatype(rs.getString("DATATYPE"));
			compareBeanObj.setStMatchTable1_DatePattern(rs.getString("TABLE1_PATTERN"));
			compareBeanObj.setStMatchTable2_DatePattern(rs.getString("TABLE2_PATTERN"));
			*/

			compareBeanObj.setStMatchTable2_header(rs.getString("MATCH_HEADER"));
			compareBeanObj.setStMatchTable2_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable2_startcharpos(rs.getString("START_CHARPOS"));
			compareBeanObj.setStMatchTable2_charSize(rs.getString("CHAR_SIZE"));
			compareBeanObj.setStMatchTable2_DatePattern(rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable2_Datatype(rs.getString("DATATYPE"));
		
			
			return compareBeanObj;
			
			
		}
	}
	
	
	public void insertBatch(final String QUERY, final ResultSet rset,final String[] columns){
		PreparedStatement ps;
		Connection con;
		//logger.info("query is 123 "+QUERY);
		//logger.info("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = getConnection();
			
			ps = con.prepareStatement(QUERY);
			
			//logger.info("value is "+rset.getString("MSGTYPE"));
			//int batch = 1;
			//int value = 3;
			while(rset.next())
			{
				flag++;
				//ps.setInt(1, seg_tran_id);
				 /*if(value == columns.length)
					 value = 3;*/
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
					//logger.info("column value is "+rset.getString(columns[i-1].trim()));
						ps.setString((i), rset.getString(columns[i-1].trim()));
					
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 500)
				{
					//logger.info("******** FLAG IS "+flag);
					flag = 1;
				
					ps.executeBatch();
					logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			logger.info("completed insertion");
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			logger.info("INSERT BATCH EXCEPTION "+e);
		}
	}
	
	public void updateBatch(final String QUERY, final ResultSet rset,final String[] columns){
		PreparedStatement ps;
		Connection con;
		//logger.info("query is 123 "+QUERY);
		//logger.info("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = getConnection();
			
			ps = con.prepareStatement(QUERY);
			
			//logger.info("value is "+rset.getString("MSGTYPE"));
			//int batch = 1;
			//int value = 3;
			while(rset.next())
			{
				//logger.info("loop excecuted"+flag);
				flag++;
				//ps.setInt(1, seg_tran_id);
				 /*if(value == columns.length)
					 value = 3;*/
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
					//logger.info("column value is "+rset.getString(columns[i-1].trim()));
					if(rset.getString(columns[i-1].trim()) == null)	
					{	
						ps.setString((i), "!null!");
						//logger.info(i+" = "+"'!null!'");
					
					}
					else
					{
						ps.setString((i), rset.getString(columns[i-1].trim()));
						//logger.info(i+" = "+rset.getString(columns[i-1].trim()));
					}
					
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 20)
				{
					logger.info("******** FLAG IS "+flag);
					flag = 1;
				
					ps.executeBatch();
					logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			logger.info("completed UPDATION");
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			logger.info("INSERT BATCH EXCEPTION "+e);
		}
	}

	public void moveToRecon(List<String> tables,CompareBean comparebeanObj)throws Exception
	{
		logger.info("***** CompareDaoImpl.moveToRecon Start ****");
		int loop = 0,tableExist = 0;
		String RECON_RECORDS_PART1 = "", recon_columns = "", RECON_INSERT = "", RECON_RECORDS_PART2 = "", RECON_RECORDS = "";
		String CHECK_TABLE = "", GET_TEMP_COLS = "";
		//String table_name= "";
		String stFile_Name = "";
		PreparedStatement psrecon = null;
		ResultSet rs = null;
		Connection con = null;
		String matching_cond= "";
		String[] col_names={""};
		String SETTLEMENT_INSERT = "";
		try
		{
			logger.info("MOVETORECON STARTS HERE ---------------------------------------------------------------------------------------- ");
			while(loop<tables.size())
			{
				stFile_Name = tables.get(loop).toUpperCase();
			//	table_name = tables.get(loop);
			//	String[] a = table_name.split("_");
		//1. *******CHECK FOR RECON TABLE
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_Name+"'";
				logger.info("check table "+CHECK_TABLE);
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				logger.info("table exists value is "+tableExist);
				//if table is not present then create it
				
				//GET_TEMP_COLS = "SELECT COLUMN_NAME,DATA_TYPE FROM user_tab_cols WHERE table_name=UPPER('TEMP_"+table_name+"')";
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_Name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory()},Integer.class);
				logger.info("file Id is "+file_id);
				
				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				//List<CompareBean> sttemp_cols = getJdbcTemplate().query(GET_TEMP_COLS, new Object[]{}, new TableColumnsMapper());
				logger.info("stFile_headers =="+stFile_headers );
				if(stFile_Name.equals("CBS"))
				{
					stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
				}
				
				if(tableExist == 0)
				{
					String tab_cols;
						recon_columns = "CREATEDDATE, CREATEDBY,SEG_TRAN_ID,"+stFile_headers;
						tab_cols = "SEG_TRAN_ID NUMBER, CREATEDDATE DATE, CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE";
					
					col_names = stFile_headers.trim().split(",");
					for(int i=0 ;i <col_names.length; i++)
					{
						tab_cols = tab_cols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
					}
					
					
					
					CREATE_RECON_TABLE = "CREATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_Name +"( "+tab_cols +" )";
					getJdbcTemplate().execute(CREATE_RECON_TABLE);
					
				}
				recon_columns = "CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
				String col_headers = "SEG_TRAN_ID,"+stFile_headers;
				col_names = col_headers.split(",");
				
	//2. *************** GET RECORDS FROM TEMP TABLE
				//RECON_RECORDS = "SELECT * FROM TEMP_"+table_name+" WHERE MATCHING_FLAG = 'N'";
				
				List<String> selHeaders = getJdbcTemplate().query(GET_COLS,new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_Name}, new ColumnMapper());
				String stSelect_Headers = "";
				int count = 0;
				for(String header : selHeaders)
				{
					if(count == 0)
					{
						stSelect_Headers = header;
						count++;
					}
					else
						stSelect_Headers = stSelect_Headers+","+header;
				}
				
				RECON_RECORDS_PART1 = "SELECT "+stSelect_Headers+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_Name+" OS1 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
						"AND OS1.FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND NOT EXISTS";
				
				RECON_RECORDS_PART2 = "( SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_Name+"_MATCHED OS2 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
						"AND OS2.FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND ";
		
			//	logger.info("file id is "+file_id);
			//	logger.info("a[0] = "+a[0]  );
				int reversal_id;
			
				if(!comparebeanObj.getStSubCategory().equals("-"))
				{
					reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					//logger.info("REV ID IS "+reversal_id);
				}
				else
				{
					reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), comparebeanObj.getStCategory()},Integer.class);
				}
				logger.info("REV ID IS "+reversal_id);
				List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
				logger.info("knockoff_Criteria IS "+knockoff_Criteria);
				
				String compare_cond = getCondition(knockoff_Criteria);
				//logger.info("COMPARE CONDITION IS "+compare_cond);
				
				RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
				
				logger.info("RECON_RECORDS IS "+RECON_RECORDS);
				con = getConnection();
				psrecon = con.prepareStatement(RECON_RECORDS);
				rs = psrecon.executeQuery();
				
				PreparedStatement settlement_pstmt = con.prepareStatement(RECON_RECORDS);
				ResultSet settlement_set = settlement_pstmt.executeQuery();
				
				
				
	//3. *************** INSERT RECORDS IN RECON TABLE
				RECON_INSERT = "INSERT INTO RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_Name+"( "+stSelect_Headers +" ) "
								+RECON_RECORDS;
				logger.info("RECON_INSERT IS "+RECON_INSERT);
				getJdbcTemplate().execute(RECON_INSERT);
						
				SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_Name+" (DCRS_REMARKS,"+stSelect_Headers+") "+
									"SELECT '"+comparebeanObj.getStMergeCategory()+"-UNRECON',"+stSelect_Headers+" FROM RECON_"+comparebeanObj.getStMergeCategory()
									+"_"+stFile_Name;
				logger.info("SETTLEMENT_INSERT IS "+SETTLEMENT_INSERT);
				getJdbcTemplate().execute(SETTLEMENT_INSERT);
				loop++;
			
			}
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.moveToRecon");
			logger.error(" error in  CompareDaoImpl.moveToRecon", new Exception(" CompareDaoImpl.moveToRecon",e));
			//con.rollback();
			throw e;
			
		}
		finally
		{
			if(rs!=null){
				rs.close();
			}
			if(psrecon !=null){
			psrecon.close();
			}
			if(con!=null){
				con.close();
			}
		
		}
		logger.info("***** CompareDaoImpl.moveToRecon End ****");
		
	}
	
/*public void TTUMRecords(List<String> Table_list,String stFile_date)throws Exception
{
	logger.info("TTUM************************************************************************************************************************");
	try
	{
		String stTable1_Name = Table_list.get(0);
		String stTable2_Name = Table_list.get(1);
		String[] stTable1 = stTable1_Name.split("_");
		String[] stTable2 = stTable2_Name.split("_");
		String stCategory = stTable1[0] ;
		String stFile1_Name = stTable1[1];
		String stFile2_Name = stTable2[1];
		String table1_condition = "";
		String table2_condition= "";
		String condition = "";
		logger.info("TTUM STARTS HERE *************************************************");
		
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , stCategory },Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, stCategory },Integer.class);
		
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,stCategory},new MatchParameterMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,stCategory},new MatchParameterMaster2());
		
	
		//prepare compare condition
		for(int i = 0; i<match_Headers1.size() ; i++)
		{
			//CHECKING PADDING FOR TABLE 1
			if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
			{
				if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						table1_condition = "SUBSTR( TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'999999999.99')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+stFile1_Name+
								" WHERE SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
							
						}
						else
						{
							table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
							
						}
						
						
					}
				}
				else
				{
					table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
							match_Headers1.get(i).getStMatchTable1_charSize()+")";

				}	
			}
			else
			{	
				if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
					{
						table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
					{
						table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"+stFile1_Name
								+" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
							
						}
						else
						{
							table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
							
						}
						
					}
					
				}
				else
				{
					table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();

				}	
			}
			
			//CHECKING PADDING FOR TABLE 2
			logger.info("i value is "+i);
			logger.info("match headers length is "+match_Headers2.size());
			logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());
			if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
			{
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						table2_condition = " SUBSTR( TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"+stFile2_Name
						+" WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL" +
								" AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							//ABC
							table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
							
						}
						else
						{
							table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
							
						}
						
					}
					
				}
				else
				{
					table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
							 match_Headers2.get(i).getStMatchTable2_charSize()+")";

				}			
				
			}
			else
			{
				logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
				if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
					{
						table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
					{
						table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"+stFile2_Name
								+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL AND SEG_TRAN_ID IS NOT NULL";
						logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
						boolean is_colon = false;
						Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
						ResultSet rs = ps.executeQuery();
						if(rs.next())
						{
							if(rs.getString(1).contains(":"))
							{
								is_colon = true;
							}
								
						}
						
						if(is_colon)
						{
							table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
							
						}
						else
						{
							table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
							
						}
						
					}
				}
				else
				{
					table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();

				}		
			
			}
			
			// PREPARING ACTUAL CONDITION OF BOTH TABLES
			if(i==(match_Headers1.size()-1))
			{
				
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
				condition = condition + table1_condition + " = "+table2_condition;
				
			}
			else
			{
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
				condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
			
			}
			
			
		}
		
		logger.info("FINALLY CONDITION IS "+condition);
		
		
		JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+stFile1_Name + " t1 INNER JOIN "+stCategory +"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+stFile_date+"'"
				+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY')='"+stFile_date
				+"' AND REMARKS = '"+stCategory+"-UNRECON'";
		
		JOIN2_QUERY = "SELECT * FROM SETTLEMENT_"+stFile2_Name + " t2 INNER JOIN "+stCategory+"_"+stFile1_Name + " t1 ON( "+condition + " ) " +
				"WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+stFile_date+"'"
				+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+stFile_date
				+"' AND REMARKS = '"+stCategory+"-UNRECON'";
		
		//GET TTUM CONDITION
		String cond1 = getTTUMCondition(table1_file_id);
		String cond2 = getTTUMCondition(table2_file_id);
	
		logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

		//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
		logger.info("COMPARE QUERY IS****************************************");
		
		
		if(!cond1.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond1;
			JOIN2_QUERY = JOIN2_QUERY + " AND "+cond1;
		}
		if(!cond2.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond2;
			JOIN2_QUERY = JOIN2_QUERY + " AND "+cond2;
		}
		logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
		logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
		
		//get failed records for table 1
		
		getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
		getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
		
		//NOW TRUNCATE ALL TABLES----------------------------
		logger.info("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
		String TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_MATCHED";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable1_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		// table 2 truncate query
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_MATCHED";
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable2_Name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		
		// ALREADY UPDATED OLD TTUM RECORDS IN MOVETORECON METHOD
		String UPDATE_OLD_TTUM_RECORDS = "UPDATE SETTLEMENT_CBS SET CREATEDDATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
		
		getJdbcTemplate().execute(UPDATE_OLD_TTUM_RECORDS);
		
		logger.info("updated old ttum records");
		
		
		
	}
	catch(Exception e)
	{
		logger.info("TTum "+e);
	}
	finally
	{
		
	}
	
}*/
	
/*	public void TTUMRecords(List<String> Table_list,CompareBean compareBeanObj,int rec_set_id)throws Exception
	{
		logger.info("TTUM************************************************************************************************************************");
		String JOIN1_QUERY = "";
		
		try
		{
			compareBeanObj.setInrec_set_id(rec_set_id);
			//String stTable1_Name = Table_list.get(0);
			//String stTable2_Name = Table_list.get(1);
			String[] stTable1 = stTable1_Name.split("_");
			String[] stTable2 = stTable2_Name.split("_");
			String stCategory = compareBeanObj.getStMergeCategory();
			String stFile1_Name = "CBS";
			String stFile2_Name = "SWITCH";
			String table1_condition = "";
			String table2_condition= "";
			String condition = "";
			logger.info("TTUM STARTS HERE *************************************************");
			logger.info(compareBeanObj.getStSubCategory());
			logger.info(compareBeanObj.getStMergeCategory());
			int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , stCategory ,compareBeanObj.getStSubCategory()},Integer.class);
			int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, stCategory,compareBeanObj.getStSubCategory() },Integer.class);
			//generatettumBeanObj.getInRec_Set_Id()
		//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
			List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,stCategory,compareBeanObj.getInrec_set_id()},new MatchParameterMaster1());
			List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,stCategory,compareBeanObj.getInrec_set_id()},new MatchParameterMaster2());
					
			for(int i = 0; i<match_Headers1.size() ; i++)
			{
				//CHECKING PADDING FOR TABLE 1
				if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',','')"+","
									+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+	match_Headers1.get(i).getStMatchTable1_charSize()+"))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name 
									+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
							logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
								table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")";
								
							}
							
							
						}
					}
					else
					{
						table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")";

					}	
				}
				else
				{	
					if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
							table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',',''))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+
									"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
								
							}
							
						}
						
					}
					else
					{
						table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();

					}	
				}
				
				//CHECKING PADDING FOR TABLE 2
				logger.info("i value is "+i);
				logger.info("match headers length is "+match_Headers2.size());
				logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());
				if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',','')"+","
											+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+ match_Headers2.get(i).getStMatchTable2_charSize()+"))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								//ABC
								table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
								
							}
							else
							{
								table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
								
								table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")";
								
							}
							
						}
						
					}
					else
					{
						table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";

					}			
					
				}
				else
				{
					logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
							table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',',''))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+
									"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
								
							}
							else
							{
								table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
								
							}
							
						}
					}
					else
					{
						table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();

					}		
				
				}
				
				// PREPARING ACTUAL CONDITION OF BOTH TABLES
				if(i==(match_Headers1.size()-1))
				{
					
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
					condition = condition + table1_condition + " = "+table2_condition;
					logger.info(condition);
					
				}
				else
				{
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
					condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
					logger.info(condition);
				
				}
				
				
			}
			
			
			
			logger.info("FINALLY CONDITION IS "+condition);
			
				
			JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE T1.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()
					+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')"
					//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
					" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";
			
			//CHANGES MADE FOR VALUE_DATE
			JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"
					+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE "+
							" TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'"
					//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON'" +
					" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";
			
			//GET TTUM CONDITION
			String cond1 = getTTUMCondition(table1_file_id);
			String cond2 = getTTUMCondition(table2_file_id);
		
			logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

			//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
			logger.info("COMPARE QUERY IS****************************************");
			
			
			if(!cond1.equals(""))
			{
				JOIN1_QUERY = JOIN1_QUERY + " AND "+cond1;
			//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond1;
			}
			if(!cond2.equals(""))
			{
				JOIN1_QUERY = JOIN1_QUERY + " AND "+cond2;
			//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond2;
			}
			logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
			//logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
			
			//get failed records for table 1
			
			//getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
			getFailedRecords(JOIN1_QUERY, table2_file_id , stFile2_Name,stFile1_Name,table1_file_id,compareBeanObj);
			//getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
			
			//NOW TRUNCATE ALL TABLES----------------------------
			logger.info("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
			String TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_MATCHED";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			// table 2 truncate query
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_MATCHED";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			// ALREADY UPDATED OLD TTUM RECORDS IN MOVETORECON METHOD
			String UPDATE_OLD_TTUM_RECORDS = "UPDATE SETTLEMENT_CBS SET CREATEDDATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
			
			getJdbcTemplate().execute(UPDATE_OLD_TTUM_RECORDS);
			
			logger.info("updated old ttum records");
			
			
			
		}
		catch(Exception e)
		{
			logger.info("TTum "+e);
		}
		finally
		{
			
		}
		
	}

	public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,CompareBean compareBeanObj)throws Exception
	{
		logger.info("------------------------------------------------------------getfailedrecords -------------------------------");
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		String reversal_condition = "";
		String update_condition = "";
		String stFinal_cond = "";
		
		try
		{
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY);
			rset = pstmt.executeQuery();
		
			int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), compareBeanObj.getStMergeCategory()},Integer.class);
			logger.info("reversal id is "+reversal_id);
			
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
			logger.info("knockoff criteria "+knockoff_Criteria.size());

			//CREATE CONDITION USING KNOCKOFF CRITERIA 
			while(rset.next())
			{
				logger.info("WHILE STARTS");
				update_condition = "";
				reversal_condition = "";
				stFinal_cond = "";
			
					//code for inserting in settlement table
					String UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
							+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
								")' WHERE FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()
								+"','DD/MM/YYYY') AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' ";
				String UPDATE_QUERY  = "";
				
				
					UPDATE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"
								+compareBeanObj.getStMergeCategory()+"-UNRECON ("+rset.getString("RESPCODE")+
							")' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()
							+"' AND DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON' ";
				
					
					int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), compareBeanObj.getStMergeCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);
					
					List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
					
					logger.info("KNOCKOFF CRITERIA SIZE "+knockoff_Criteria1.size());
					
					for(int i = 0; i<knockoff_Criteria1.size() ; i++)
					{
						if(i == (knockoff_Criteria1.size()-1))
						{
							if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
								{
									update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
											+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
									
								}
								else
								{
									update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
											+"'";
									
								}
						}
						else
						{
							if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
								{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
											+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
								
								}
								else
								{
									update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
											+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
									
								}
						
						}
						
					}
					
					logger.info("update condition is "+update_condition);
					if(!update_condition.equals(""))
					{
						UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
					}
					logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
					
					PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
					pstmt1.executeUpdate();
					
					pstmt1 = null;
				
			}
			logger.info("while completed");
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception in getFailedRecords "+e);
		}

		
	}
	*/
	
/*public void getFailedRecords(String QUERY, int file_id,String stCategory,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id)throws Exception
{
	logger.info("------------------------------------------------------------getfailedrecords -------------------------------");
	PreparedStatement pstmt = null;
	Connection conn = null;
	ResultSet rset = null;
	String reversal_condition = "";
	String update_condition = "";
	String stFinal_cond = "";
	
	try
	{
		conn = getConnection();
		pstmt = conn.prepareStatement(QUERY);
		rset = pstmt.executeQuery();
	
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), stCategory},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());

		//CREATE CONDITION USING KNOCKOFF CRITERIA 
		while(rset.next())
		{
			logger.info("WHILE STARTS");
			update_condition = "";
			reversal_condition = "";
			stFinal_cond = "";
			for(int i = 0; i<knockoff_Criteria.size() ; i++)
			{
				if(i == (knockoff_Criteria.size()-1))
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
						{
							reversal_condition = reversal_condition + " SUBSTR( "+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
									+" "+knockoff_Criteria.get(i).getStReversal_condition() 
									+" SUBSTR( '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"',"+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
							
						}
						else
						{
							reversal_condition = reversal_condition + knockoff_Criteria.get(i).getStReversal_header()
									+" "+knockoff_Criteria.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())
									+"'";
							
						}
					
						
					
				}
				else
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
						{
							reversal_condition = reversal_condition + " SUBSTR( "+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
									+" "+knockoff_Criteria.get(i).getStReversal_condition() 
									+" SUBSTR( '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"',"+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
						
						}
						else
						{
							reversal_condition = reversal_condition + knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
									+" '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"' AND ";
							
						}
				
				}
				
			}
			
		//	logger.info("reversal condition is "+reversal_condition);
			// made opposite of the condition of TTUM 
			String ttum_reverse_cond = getReversalTTUMcondition(file_id);
		
			if(!reversal_condition.equals(""))
			{
				stFinal_cond = stFinal_cond + reversal_condition;
			}
			
			if(!ttum_reverse_cond.equals(""))
			{
				if(!stFinal_cond.equalsIgnoreCase(""))
				{
					stFinal_cond = stFinal_cond + " AND "+ttum_reverse_cond;
				}
				else
					stFinal_cond = stFinal_cond + ttum_reverse_cond;
			}
			
			String REVERSAL_QUERY = "SELECT COUNT(*) FROM "+stCategory +"_"+ stFile_Name+" WHERE "+stFinal_cond;
			
			logger.info("reversal QUERY "+REVERSAL_QUERY);
			
			PreparedStatement pstmt1 = conn.prepareStatement(REVERSAL_QUERY);
			ResultSet result = pstmt1.executeQuery();
			logger.info(" query count is "+result.getInt(1));
			
			//int count = getJdbcTemplate().queryForObject(REVERSAL_QUERY, new Object[]{}, Integer.class);
		//	logger.info("query count is "+count);
			
			if(count==0)//if count is 0 then make remarks as GENERATE TTUM
			{
				//code for inserting in settlement table
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+stUpdate_FileName+" SET REMARKS = 'ONUS-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND REMARKS = 'ONUS-RECON' ";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), stCategory},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
								
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
										+"'";
								
							}
					}
					else
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
							update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
							
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
										+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
								
							}
					
					}
					
				}
				
				logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
				pstmt1.executeUpdate();
				
				pstmt1 = null;
				
				
			}
			else if(count==1)//if count is 0 then make remarks as GENERATE TTUM
			{
				//code for inserting in settlement table
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+stUpdate_FileName+" SET REMARKS = 'ONUS-AUTO-REVERSED' " +
							"WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND REMARKS = 'ONUS-RECON' ";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), stCategory},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
								
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
										+"'";
								
							}
					}
					else
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
							update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
							
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
										+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
								
							}
					
					}
					
				}
				
				logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
				pstmt1.executeUpdate();
				
				pstmt1 = null;
			}
			
			
		}
		logger.info("while completed");
		
		
	}
	catch(Exception e)
	{
		logger.info("Exception in getFailedRecords "+e);
	}
	
}*/

	public void TTUMRecords(List<String> Table_list,CompareBean compareBeanObj,int rec_set_id)throws Exception
	{
		logger.info("***** CompareDaoImpl.TTUMRecords Start ****");
		String JOIN1_QUERY = "";
		
		try
		{
			//manualBeanObj.setInrec_set_id(rec_set_id);
			//String stTable1_Name = Table_list.get(0);
			//String stTable2_Name = Table_list.get(1);
			/*String[] stTable1 = stTable1_Name.split("_");
			String[] stTable2 = stTable2_Name.split("_");*/
			String stCategory = compareBeanObj.getStMergeCategory();
			String stFile1_Name = "CBS";
			String stFile2_Name = "SWITCH";
			String table1_condition = "";
			String table2_condition= "";
			String condition = "";
			logger.info("TTUM STARTS HERE *************************************************");
			logger.info(compareBeanObj.getStSubCategory());
			logger.info(compareBeanObj.getStMergeCategory());
			int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , stCategory ,compareBeanObj.getStSubCategory()},Integer.class);
			int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, stCategory,compareBeanObj.getStSubCategory() },Integer.class);
			logger.info("table1_file_id=="+table1_file_id);
			logger.info("table2_file_id=="+table2_file_id);
			
			//generatettumBeanObj.getInRec_Set_Id()
		//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
			List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,stCategory,rec_set_id},new MatchParameterMaster1());
			List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,stCategory,rec_set_id},new MatchParameterMaster2());
			logger.info("match_Headers1=="+match_Headers1);
			logger.info("match_Headers2=="+match_Headers2);
			
			for(int i = 0; i<match_Headers1.size() ; i++)
			{
				//CHECKING PADDING FOR TABLE 1
				if(match_Headers1.get(i).getStMatchTable1_Padding().equals("Y"))
				{
					if(match_Headers1.get(i).getStMatchTable1_Datatype() != null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',','')"+","
									+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+	match_Headers1.get(i).getStMatchTable1_charSize()+"))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							/*table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";*/
							
							table1_condition = "TO_CHAR(TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" '),'DD/MM/YYYY')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name 
									+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
							logger.info("CHECK FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+" , ':')";
								
							}
							else
							{
								/*table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";*/
								table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(i).getStMatchTable1_charSize()+")";
								
							}
							
							
						}
					}
					else
					{
						table1_condition = "SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")";

					}	
				}
				else
				{	
					if(match_Headers1.get(i).getStMatchTable1_Datatype()!=null)
					{
						if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("NUMBER"))
						{
							//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
							table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",',',''))";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("DATE"))
						{
							//table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";
							table1_condition = " TO_CHAR(TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"'),'DD/MM/YYYY')";
						}
						else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+
									"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table1_condition = "REPLACE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+" , ':')";
								
							}
							else
							{
								table1_condition = " LPAD( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+6+",'0')";
								
							}
							
						}
						
					}
					else
					{
						table1_condition = " t1."+match_Headers1.get(i).getStMatchTable1_header().trim();

					}	
				}
				
				logger.info("table1_condition"+table1_condition);
				
				//CHECKING PADDING FOR TABLE 2
				/*logger.info("i value is "+i);
				logger.info("match headers length is "+match_Headers2.size());
				logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
				if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
				{
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',','')"+","
											+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+ match_Headers2.get(i).getStMatchTable2_charSize()+"))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							/*table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";	*/
							table2_condition = " TO_CHAR(TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";	
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
							match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								//ABC
								table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+" , ':')";
								
							}
							else
							{
								/*table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";*/
								
								table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")";
								
							}
							
						}
						
					}
					else
					{
						table2_condition = " SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")";

					}			
					
				}
				else
				{
					logger.info("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
					if(match_Headers2.get(i).getStMatchTable2_Datatype()!=null)
					{
						if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("NUMBER"))
						{
							//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
							table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',',''))";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("DATE"))
						{
							//table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
							table2_condition = " TO_CHAR(TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";
						}
						else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
						{
							//check whether the column consists of :
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"
									+compareBeanObj.getStMergeCategory()+
									"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
							logger.info("CHECK_ FORMAT IS "+CHECK_FORMAT);
							boolean is_colon = false;
							Connection con = getConnection();
							PreparedStatement ps = con.prepareStatement(CHECK_FORMAT);
							ResultSet rs = ps.executeQuery();
							if(rs.next())
							{
								if(rs.getString(1).contains(":"))
								{
									is_colon = true;
								}
									
							}
							
							if(is_colon)
							{
								table2_condition = "REPLACE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+" , ':')";
								
							}
							else
							{
								table2_condition = "LPAD( t2."+match_Headers2.get(i).getStMatchTable2_header()+","+6+",'0')";
								
							}
							
						}
					}
					else
					{
						table2_condition = " t2."+match_Headers2.get(i).getStMatchTable2_header();

					}		
				
				}
				logger.info("table2_condition"+table2_condition);
				
				// PREPARING ACTUAL CONDITION OF BOTH TABLES
				if(i==(match_Headers1.size()-1))
				{
					
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
					condition = condition + table1_condition + " = "+table2_condition;
					//logger.info("condition=="+condition);
					
				}
				else
				{
					//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
					condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
					//logger.info("condition=="+condition);
				
				}
				
				
			}
			
			
			
			logger.info("FINALLY CONDITION IS "+condition);
			
				
		/*	JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE T1.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()
					+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')"
					//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
					" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";*/
			
			//CHANGES MADE FOR VALUE_DATE
			
			//************************CHANGES MADE BY INT5779 ON 20 FEB AS BATCHING FOR UPDATE IS TAKING TIME
			List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS, new Object[] {"TEMP_"+compareBeanObj.getStMergeCategory()
					+"_CBS"},new ColumnMapper());
			String Selec_cols = "";
			String insert_cols = "";
			int count = 0;
			for(String headers : TABLE_COLS)
			{
				if(count == 0)
				{
					Selec_cols = "t1."+headers;
					insert_cols = headers;
					count++;
				}
				else
				{
					Selec_cols = Selec_cols +","+"t1."+headers;
					insert_cols = insert_cols+","+headers;
				}
			}
			
			//CREATE A temporary TABLE
			//commented by sushant 30/10/2018
			/*if(getJdbcTemplate().queryForObject("SELECT count (*) FROM tab WHERE tname  = '"+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA'", new Object[] {},Integer.class) == 0)
			{
				getJdbcTemplate().execute("CREATE TABLE "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA AS SELECT * FROM TEMP_"+compareBeanObj.getStMergeCategory()+"_CBS");
				getJdbcTemplate().execute("ALTER TABLE "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA ADD DCRS_REMARKS VARCHAR2 (100 BYTE)");
			}*/
			//ends here
			String INSERT_QUERY = "INSERT INTO "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA (DCRS_REMARKS,"+insert_cols+") ";			
			
			
			/*JOIN1_QUERY = "SELECT (T1.DCRS_REMARKS || ' ('||T2.RESPCODE||')'),"+Selec_cols+" FROM SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"
					+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE "+
							" TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'"
					//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON'" +
					" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";*/
			
			
			JOIN1_QUERY = "SELECT (T1.DCRS_REMARKS || ' ('||T2.RESPCODE||')'),"+Selec_cols+" FROM "
					+ " ( SELECT * FROM  SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " WHERE FILEDATE = '"+compareBeanObj.getStFile_date()+"' and DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON' "
							+ "and PART_TRAN_TYPE= 'C' ) "					
					+ " t1 INNER JOIN SETTLEMENT_"
					+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE "+
							" T1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"
					//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
					+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON'" +
					" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";
			
			if(compareBeanObj.getStMergeCategory().equals("AMEX")) {
				
				JOIN1_QUERY = "SELECT (T1.DCRS_REMARKS || ' ('||T2.RESPCODE||')'),"+Selec_cols+" FROM "
						+ " ( SELECT * FROM  SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name + " WHERE FILEDATE = '"+compareBeanObj.getStFile_date()+"' and DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON' "
								+ "and E= 'C' ) "					
						+ " t1 INNER JOIN SETTLEMENT_"
						+compareBeanObj.getStMergeCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
						+ " ) WHERE "+
								" T1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"
						//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
						+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON'" +
						" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";
				
				
				
				
			}
			
			//GET TTUM CONDITION
			String cond1 = getTTUMCondition(table1_file_id);
			String cond2 = getTTUMCondition(table2_file_id);
		
			logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

			//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
			logger.info("COMPARE QUERY IS****************************************");
			
			
			if(!cond1.equals(""))
			{
				JOIN1_QUERY = JOIN1_QUERY + " AND "+cond1;
			//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond1;
			}
			if(!cond2.equals(""))
			{
				JOIN1_QUERY = JOIN1_QUERY + " AND "+cond2;
			//	JOIN2_QUERY = JOIN2_QUERY + " AND "+cond2;
			}
			logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
			//logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
	
			INSERT_QUERY = INSERT_QUERY + JOIN1_QUERY;
			
			logger.info("INSERT QUERY "+INSERT_QUERY);
			
//			COMMENTED BY SUSHANT GAVAS 26/10/2018 FOR NOT WORKING
 		//	getJdbcTemplate().execute(INSERT_QUERY);
			
			getResponseCode(compareBeanObj.getStCategory(), compareBeanObj.getStSubCategory(), compareBeanObj.getStFile_date(), compareBeanObj.getStEntryBy());
			
			logger.info("Response code generated");
			
			
			int reversal_id = getJdbcTemplate().queryForObject("SELECT MIN(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table1_file_id), 
											compareBeanObj.getStCategory()},Integer.class);
			
			logger.info("reversal id is "+reversal_id);
			
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());
			logger.info("knockoff criteria "+knockoff_Criteria.size());

			String knock_condition = getCondition(knockoff_Criteria);
			
			String DELETE_QUERY = "DELETE FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_CBS OS1 WHERE OS1.DCRS_REMARKS = '"
					+compareBeanObj.getStMergeCategory()+"-UNRECON' AND OS1.FILEDATE = '"
					+compareBeanObj.getStFile_date()+"' " +
					"AND EXISTS(SELECT * FROM "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA OS2 WHERE OS2.FILEDATE = '"
					+compareBeanObj.getStFile_date()+"' AND "+knock_condition+")";

			logger.info("delete QEURY IS "+DELETE_QUERY);
			
			//getJdbcTemplate().execute(DELETE_QUERY);

			//NOW INSERT INTO SETTLEMENT TABLE 
			String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+compareBeanObj.getStCategory()+"_CBS (DCRS_REMARKS,"+insert_cols
					+") SELECT DCRS_REMARKS,"+insert_cols+" FROM "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA WHERE FILEDATE = '"
					+compareBeanObj.getStFile_date()+"'";
			logger.info("SETTLEMENT_INSERT QUERY IS "+INSERT_RECORDS);
			//getJdbcTemplate().execute(INSERT_RECORDS);


			//getJdbcTemplate().execute("DROP TABLE "+compareBeanObj.getStCategory()+"_UNRECON_RESPCODE_CBS_DATA");
			
			
			//get failed records for table 1
			
			//getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
			
			//******************************CHANGES MADE AS PER RUPAY ON 08TH MARCH 2018
		//	getFailedRecords(JOIN1_QUERY, table2_file_id , stFile2_Name,stFile1_Name,table1_file_id,compareBeanObj);
			//getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
			
			//NOW TRUNCATE ALL TABLES----------------------------
			/*logger.info("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
			String TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable1_Name+"_MATCHED";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable1_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			// table 2 truncate query
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+stTable2_Name+"_MATCHED";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+stTable2_Name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);*/
			
			// ALREADY UPDATED OLD TTUM RECORDS IN MOVETORECON METHOD
			/*String UPDATE_OLD_TTUM_RECORDS = "UPDATE SETTLEMENT_CBS SET CREATEDDATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
			
			getJdbcTemplate().execute(UPDATE_OLD_TTUM_RECORDS);
			
			logger.info("updated old ttum records");*/
			
			
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.TTUMRecords");
			logger.error(" error in  CompareDaoImpl.TTUMRecords", new Exception("  CompareDaoImpl.TTUMRecords",e));
			throw e;
		}
		finally
		{
			
		}
		logger.info("***** CompareDaoImpl.TTUMRecords End ****");
	}

	
	public boolean getResponseCode(String category,String subCat,String filedate,String entry_by) throws ParseException, Exception {
		try {
			logger.info("***** ReconProcessDaoImpl.ISSClassifydata Start ****");
			
			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("I_FILE_DATE",filedate);
			inParams.put("I_ENTRY_BY", entry_by);
			
			
			GetResponseCode acqclassificaton = new GetResponseCode(getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);

			//logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** ReconProcessDaoImpl.ISSClassifydata End ****");
			
			if (outParams.get("ERROR_MESSAGE") != null) {
			

						return false;
					}else {
						
						return true;
					}
				

			
			
		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
			logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata", new Exception(" ReconProcessDaoImpl.ISSClassifydata",e));
			return false;
		}
		
	}
	
	class GetResponseCode extends StoredProcedure {
		private static final String procName = "get_resp_code";

		GetResponseCode(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);
			
			
			declareParameter(new SqlParameter("I_FILE_DATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE", Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE", Types.VARCHAR));
			compile();
		}
	}
	
	
	//public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,CompareBean compareBeanObj)throws Exception
	public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,CompareBean compareBeanObj)throws Exception
	{
		logger.info("***** CompareDaoImpl.getFailedRecords Start ****");
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		String reversal_condition = "";
		String update_condition = "";
		String stFinal_cond = "";
		
		try
		{
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY);
			rset = pstmt.executeQuery();
		
			int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), compareBeanObj.getStMergeCategory()},Integer.class);
			logger.info("reversal id is "+reversal_id);
			
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
			logger.info("knockoff criteria "+knockoff_Criteria.size());
			List<String> update_queries = new ArrayList<>();
			//CREATE CONDITION USING KNOCKOFF CRITERIA 
			while(rset.next())
			{
				//logger.info("WHILE STARTS");
				update_condition = "";
				reversal_condition = "";
				stFinal_cond = "";
			
					//code for inserting in settlement table
					/*String UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
							+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
								")' WHERE FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()
								+"','DD/MM/YYYY') AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' ";*/
				String UPDATE_QUERY  = "";
				
				
					UPDATE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStMergeCategory()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"
								+compareBeanObj.getStMergeCategory()+"-UNRECON ("+rset.getString("RESPCODE")+
							")' WHERE FILEDATE = '"+compareBeanObj.getStFile_date()
							+"' AND DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON' ";
				
					
					int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), compareBeanObj.getStMergeCategory()},Integer.class);
				//	logger.info("reversal id is "+reversal_id);
					
					List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
					
				//	logger.info("KNOCKOFF CRITERIA SIZE "+knockoff_Criteria1.size());
					
					for(int i = 0; i<knockoff_Criteria1.size() ; i++)
					{
						if(i == (knockoff_Criteria1.size()-1))
						{
							if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
								{
									update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
											+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
									
								}
								else
								{
									update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
											+"'";
									
								}
						}
						else
						{
							if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
								{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
											+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
											+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
											+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
								
								}
								else
								{
									update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
											+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
									
								}
						
						}
						
					}
					
					logger.info("update condition is "+update_condition);
					if(!update_condition.equals(""))
					{
						UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
					}
					logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
					
					update_queries.add(UPDATE_QUERY);
					
					if(update_queries.size() == 10)
					{
						String[] update_batch = new String[update_queries.size()];
						update_batch = update_queries.toArray(update_batch);
						getJdbcTemplate().batchUpdate(update_batch);
					}
			}
			
			if(update_queries.size() > 0)
			{
				String[] update_batch = new String[update_queries.size()];
				update_batch = update_queries.toArray(update_batch);
				getJdbcTemplate().batchUpdate(update_batch);
			}
			logger.info("while completed");
			logger.info("***** CompareDaoImpl.getFailedRecords End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.getFailedRecords");
			logger.error(" error in  CompareDaoImpl.getFailedRecords", new Exception("  CompareDaoImpl.getFailedRecords",e));
			throw e;
		}

		
	}
	
	
/*public void getFailedRecords(String QUERY, int file_id,String stCategory,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id)throws Exception
{
	logger.info("------------------------------------------------------------getfailedrecords -------------------------------");
	PreparedStatement pstmt = null;
	Connection conn = null;
	ResultSet rset = null;
	String reversal_condition = "";
	String update_condition = "";
	String stFinal_cond = "";
	
	try
	{
		conn = getConnection();
		pstmt = conn.prepareStatement(QUERY);
		rset = pstmt.executeQuery();
	
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), stCategory},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());

		//CREATE CONDITION USING KNOCKOFF CRITERIA 
		while(rset.next())
		{
			logger.info("WHILE STARTS");
			update_condition = "";
			reversal_condition = "";
			stFinal_cond = "";
			for(int i = 0; i<knockoff_Criteria.size() ; i++)
			{
				if(i == (knockoff_Criteria.size()-1))
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
						{
							reversal_condition = reversal_condition + " SUBSTR( "+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
									+" "+knockoff_Criteria.get(i).getStReversal_condition() 
									+" SUBSTR( '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"',"+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
							
						}
						else
						{
							reversal_condition = reversal_condition + knockoff_Criteria.get(i).getStReversal_header()
									+" "+knockoff_Criteria.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())
									+"'";
							
						}
					
						
					
				}
				else
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
						{
							reversal_condition = reversal_condition + " SUBSTR( "+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
									+" "+knockoff_Criteria.get(i).getStReversal_condition() 
									+" SUBSTR( '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"',"+knockoff_Criteria.get(i).getStReversal_charpos()
									+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
						
						}
						else
						{
							reversal_condition = reversal_condition + knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
									+" '"+rset.getString(knockoff_Criteria.get(i).getStReversal_header())+"' AND ";
							
						}
				
				}
				
			}
			
		//	logger.info("reversal condition is "+reversal_condition);
			// made opposite of the condition of TTUM 
			String ttum_reverse_cond = getReversalTTUMcondition(file_id);
		
			if(!reversal_condition.equals(""))
			{
				stFinal_cond = stFinal_cond + reversal_condition;
			}
			
			if(!ttum_reverse_cond.equals(""))
			{
				if(!stFinal_cond.equalsIgnoreCase(""))
				{
					stFinal_cond = stFinal_cond + " AND "+ttum_reverse_cond;
				}
				else
					stFinal_cond = stFinal_cond + ttum_reverse_cond;
			}
			
			String REVERSAL_QUERY = "SELECT COUNT(*) FROM "+stCategory +"_"+ stFile_Name+" WHERE "+stFinal_cond;
			
			logger.info("reversal QUERY "+REVERSAL_QUERY);
			
			PreparedStatement pstmt1 = conn.prepareStatement(REVERSAL_QUERY);
			ResultSet result = pstmt1.executeQuery();
			logger.info(" query count is "+result.getInt(1));
			
			//int count = getJdbcTemplate().queryForObject(REVERSAL_QUERY, new Object[]{}, Integer.class);
		//	logger.info("query count is "+count);
			
			if(count==0)//if count is 0 then make remarks as GENERATE TTUM
			{
				//code for inserting in settlement table
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+stUpdate_FileName+" SET REMARKS = 'ONUS-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND REMARKS = 'ONUS-RECON' ";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), stCategory},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
								
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
										+"'";
								
							}
					}
					else
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
							update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
							
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
										+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
								
							}
					
					}
					
				}
				
				logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
				pstmt1.executeUpdate();
				
				pstmt1 = null;
				
				
			}
			else if(count==1)//if count is 0 then make remarks as GENERATE TTUM
			{
				//code for inserting in settlement table
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+stUpdate_FileName+" SET REMARKS = 'ONUS-AUTO-REVERSED' " +
							"WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND REMARKS = 'ONUS-RECON' ";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), stCategory},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
								update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")";
								
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() +" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())
										+"'";
								
							}
					}
					else
					{
						if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
							{
							update_condition = update_condition + " SUBSTR( "+knockoff_Criteria1.get(i).getStReversal_header()+","+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+")"
										+" "+knockoff_Criteria1.get(i).getStReversal_condition() 
										+" SUBSTR( '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"',"+knockoff_Criteria1.get(i).getStReversal_charpos()
										+","+knockoff_Criteria1.get(i).getStReversal_charsize()+") AND ";
							
							}
							else
							{
								update_condition = update_condition + knockoff_Criteria1.get(i).getStReversal_header()+" "+knockoff_Criteria1.get(i).getStReversal_condition()
										+" '"+rset.getString(knockoff_Criteria1.get(i).getStReversal_header())+"' AND ";
								
							}
					
					}
					
				}
				
				logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				PreparedStatement pstmt1 = conn.prepareStatement(UPDATE_QUERY);
				pstmt1.executeUpdate();
				
				pstmt1 = null;
			}
			
			
		}
		logger.info("while completed");
		
		
	}
	catch(Exception e)
	{
		logger.info("Exception in getFailedRecords "+e);
	}
	
}*/

	public String getTTUMCondition(int inFile_Id) throws Exception
	{
		logger.info("***** CompareDaoImpl.getTTUMCondition Start ****");
		String temp_param = "";
		String condition = "";
		try
		{
			List<FilterationBean> ttum_details = getJdbcTemplate().query(GET_TTUM_PARAMS, new Object[] {inFile_Id}, new TTUMParameterMaster());
			
			for(int i = 0; i<ttum_details.size();i++){
				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = ttum_details.get(i);
				temp_param = filterBeanObj.getStSearch_header().trim();
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{
					if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
					{
						condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
					{
						condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
					}
					else
					{
						if(i == (ttum_details.size()-1))
						{
							condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
						}
						else
						{
							condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
						}
					}
				}
				else
				{
					if(filterBeanObj.getStSearch_Condition().equals("="))
					{
						condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
									filterBeanObj.getStSearch_pattern().trim()+"'";
					}
					else if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
					{
						condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
									"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
					}
					else
					{
						if(i == (ttum_details.size()-1))
						{
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
						}
						else
						{
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
						}
					}
					
				}
				
				for(int j= (i+1); j <ttum_details.size(); j++)
				{
					//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(ttum_details.get(j).getStSearch_header()))
					{
							
						if(ttum_details.get(j).getStSearch_padding().equals("Y"))
						{
							if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
							{
								condition = condition + " OR SUBSTR(TRIM(" + ttum_details.get(j).getStSearch_header()+") , "+ttum_details.get(j).getStsearch_Startcharpos()+","+
										ttum_details.get(j).getStsearch_Endcharpos()+") "+ttum_details.get(j).getStSearch_Condition().trim()+ 
									"'"+ttum_details.get(j).getStSearch_pattern().trim()+"'";
							}
							else if((ttum_details.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR SUBSTR(TRIM(" + ttum_details.get(j).getStSearch_header()+") , "+ttum_details.get(j).getStsearch_Startcharpos()+","+
										ttum_details.get(j).getStsearch_Endcharpos()+") "+ttum_details.get(j).getStSearch_Condition().trim()+
										"'%"+ttum_details.get(j).getStSearch_pattern().trim()+"%'";
							}
							else
							{
								if(j==(ttum_details.size()-1))
								{	
									/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
										search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
									condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						else
						{
							if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
							{
								condition = condition + " OR TRIM(" + ttum_details.get(j).getStSearch_header()+") "+
										ttum_details.get(j).getStSearch_Condition().trim()+" '"+ttum_details.get(j).getStSearch_pattern().trim()+"'";
							}
							else if((ttum_details.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR TRIM(" + ttum_details.get(j).getStSearch_header()+") "+
										ttum_details.get(j).getStSearch_Condition().trim()+" "+
										"'%"+ttum_details.get(j).getStSearch_pattern().trim()+"%'";
								
							}
							else
							{
								if(j==(ttum_details.size()-1))
								{
									condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						
					
						
							i = j;
					}
					
				}
				//logger.info("i value is "+i);
				if(i != (ttum_details.size())-1)
				{
					if(ttum_details.get(i).getStSearch_Condition().equals("!="))
					{
						condition = condition + " ) ) AND ";
					}
					else
						condition = condition +" ) AND ";
				}
				else
				{
					condition = condition +")";
				}
				
			//	logger.info("condition is "+condition);
			}
			
			logger.info("condition is "+condition);
			
			
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareDaoImpl.getTTUMCondition");
			logger.error(" error in  CompareDaoImpl.getTTUMCondition", new Exception("  CompareDaoImpl.getTTUMCondition",e));
			throw e;
			
		}
		logger.info("***** CompareDaoImpl.getTTUMCondition End ****");
		
		return condition;
	}
	
	private static class TTUMParameterMaster implements RowMapper<FilterationBean> {

		@Override
		public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
			FilterationBean filterationObjBean = new FilterationBean();
			
			filterationObjBean.setStSearch_header(rs.getString("FILE_HEADER"));
			filterationObjBean.setStSearch_pattern(rs.getString("VALUE"));
			filterationObjBean.setStSearch_padding(rs.getString("PADDING"));
			filterationObjBean.setStsearch_Startcharpos(rs.getString("START_POS"));
			filterationObjBean.setStsearch_Endcharpos(rs.getString("CHAR_SIZE"));
			filterationObjBean.setStSearch_Condition(rs.getString("CONDITION"));
			
			return filterationObjBean;
		/*	KnockOffBean knockOffBean = new KnockOffBean();
			
			knockOffBean.setStReversal_header(rs.getString("HEADER"));
			knockOffBean.setStReversal_value(rs.getString("VALUE"));
			return knockOffBean;*/
			
			
		}
		}
	
	
public String getReversalTTUMcondition(int inFile_Id) throws Exception
{
	logger.info("***** CompareDaoImpl.getReversalTTUMcondition Start ****");
	String temp_param = "";
	String condition = "";
	try
	{
		List<FilterationBean> ttum_details = getJdbcTemplate().query(GET_TTUM_PARAMS, new Object[] {inFile_Id}, new TTUMParameterMaster());
		
		for(int i = 0; i<ttum_details.size();i++){
			FilterationBean filterBeanObj = new FilterationBean();
			filterBeanObj = ttum_details.get(i);
			temp_param = filterBeanObj.getStSearch_header().trim();
			if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
			{
				if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
				{
					condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
						filterBeanObj.getStsearch_Endcharpos()+") = "+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
				}
				else if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
				{
					if(i == (ttum_details.size()-1))
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
			}
			else
			{
				if(filterBeanObj.getStSearch_Condition().equals("!="))
				{
					condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") = '"+
								filterBeanObj.getStSearch_pattern().trim()+"'";
				}
				else if(filterBeanObj.getStSearch_Condition().equals("="))
				{
					if(i == (ttum_details.size()-1))
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
				
			}
			
			for(int j= (i+1); j <ttum_details.size(); j++)
			{
				//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
				if(temp_param.equals(ttum_details.get(j).getStSearch_header()))
				{
						
					if(ttum_details.get(j).getStSearch_padding().equals("Y"))
					{
						if((ttum_details.get(j).getStSearch_Condition().trim()).equals("!="))
						{
							condition = condition + " OR SUBSTR(TRIM(" + ttum_details.get(j).getStSearch_header()+") , "+ttum_details.get(j).getStsearch_Startcharpos()+","+
									ttum_details.get(j).getStsearch_Endcharpos()+") = '"+ttum_details.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
						{
							if(j==(ttum_details.size()-1))
							{	
								/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
								condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + ", '"+ttum_details.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					else
					{
						if((ttum_details.get(j).getStSearch_Condition().trim()).equals("!="))
						{
							condition = condition + " OR TRIM(" + ttum_details.get(j).getStSearch_header()+") = '"
										+ttum_details.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((ttum_details.get(j).getStSearch_Condition().trim()).equals("="))
						{
							if(j==(ttum_details.size()-1))
							{
								condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition = condition + " , '" +ttum_details.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					
				
					
						i = j;
				}
				
			}
			//logger.info("i value is "+i);
			if(i != (ttum_details.size())-1)
			{
				if(ttum_details.get(i).getStSearch_Condition().equals("="))
				{
					condition = condition + " ) ) AND ";
				}
				else
					condition = condition +" ) AND ";
			}
			else
			{
				condition = condition +")";
			}
			
		//	logger.info("condition is "+condition);
		}
		
		
		logger.info("***** CompareDaoImpl.getReversalTTUMcondition End ****");
		
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareDaoImpl.getReversalTTUMcondition");
		logger.error(" error in  CompareDaoImpl.getReversalTTUMcondition", new Exception("  CompareDaoImpl.getReversalTTUMcondition",e));
		throw e;
		
	}
	return condition;

}

	
/*public String getReconParameters(CompareBean comparebeanObj,String stfile_name,int inRec_set_Id)
{
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
			//String a[] = sttable_Name.split("_"); 
			//1.Get File ID from db
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stfile_name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
			logger.info("file id is "+file_id);

			//2. Get Parameters from db
			
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id,inRec_set_Id}, new CompareConditionMaster());
		//	List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_COMPARE_CONDITION, new Object[]{file_id}, new CompareConditionMaster());


			//3. Form Proper condition
			
			for(int i = 0; i<compare_cond.size(); i++)
			{

				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = compare_cond.get(i);
				temp_param = filterBeanObj.getStSearch_header().trim();
				logger.info("HEADER IN GETRECON IS "+filterBeanObj.getStSearch_header().trim());
				logger.info("condition is "+filterBeanObj.getStSearch_Condition().trim());
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{
					if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";					
					}
				}
				else
				{
					if(filterBeanObj.getStSearch_Condition().equals("="))
					{
						stcompare_con = stcompare_con + "(TRIM( OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+filterBeanObj.getStSearch_pattern().trim()+"'";
					}
					else
					{
						stcompare_con = stcompare_con + "(TRIM( OS1."+filterBeanObj.getStSearch_header()+") "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					
				}
				
				for(int j= (i+1); j <compare_cond.size(); j++)
				{
					//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(compare_cond.get(j).getStSearch_header()))
					{
							
						if(compare_cond.get(j).getStSearch_padding().equals("Y"))
						{
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR SUBSTR(TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") , "+compare_cond.get(j).getStsearch_Startcharpos()+","+
										compare_cond.get(j).getStsearch_Endcharpos()+") "+compare_cond.get(j).getStSearch_Condition().trim()+"'"+ compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else
							{
								if(j==(compare_cond.size()-1))
								{	
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						else
						{
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") "+
										compare_cond.get(j).getStSearch_Condition().trim()+" '"+compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else
							{
								if(j==(compare_cond.size()-1))
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						
						
							i = j;
					}
					
				}
				//logger.info("i value is "+i);
				if(i != (compare_cond.size())-1)
				{
					//stcompare_con = stcompare_con +" ) AND ";
					if(compare_cond.get(i).getStSearch_Condition().equals("!="))
					{
						stcompare_con = stcompare_con + " ) ) AND ";
					}
					else
						stcompare_con = stcompare_con +" ) AND ";
				}
				else
					stcompare_con = stcompare_con +")";
				
			//	logger.info("condition is "+condition);
			
			}

			logger.info("compare condition is "+stcompare_con);
			
			
	}
	catch(Exception e)
	{
		logger.info("eXCEPTION IS "+e);
	}
	return stcompare_con ;	
}*/

public String getReconParameters(CompareBean comparebeanObj,String stfile_name,int inRec_set_Id) throws Exception
{
	logger.info("***** CompareDaoImpl.getReconParameters Start ****");
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
		//	String a[] = sttable_Name.split("-"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stfile_name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
			logger.info("file id is "+file_id);

			//2. Get Parameters from db
			
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id,inRec_set_Id}, new CompareConditionMaster());
		//	List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_COMPARE_CONDITION, new Object[]{file_id}, new CompareConditionMaster());
			logger.info("compare_cond is "+compare_cond);

			//3. Form Proper condition
			
			for(int i = 0; i<compare_cond.size(); i++)
			{

				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = compare_cond.get(i);
				temp_param = filterBeanObj.getStSearch_header().trim();
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{
					/*if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else*/ if(filterBeanObj.getStSearch_Condition().trim().equals("!="))
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";					
					}
					else
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM(OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim() 
								+" '"+filterBeanObj.getStSearch_pattern().trim()+"'";
					}
				}
				else
				{
					/*if(filterBeanObj.getStSearch_Condition().equals("="))
					{
						stcompare_con = stcompare_con + "(TRIM( OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+filterBeanObj.getStSearch_pattern().trim()+"'";
					}
					else*/ if(filterBeanObj.getStSearch_Condition().equals("!="))
					{
						stcompare_con = stcompare_con + "(TRIM( OS1."+filterBeanObj.getStSearch_header()+") "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else
					{
						stcompare_con = stcompare_con + "(TRIM(OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()
										+" '"+filterBeanObj.getStSearch_pattern()+"'";
					}
					
				}
				
				for(int j= (i+1); j <compare_cond.size(); j++)
				{
					//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(compare_cond.get(j).getStSearch_header()))
					{
							
						if(compare_cond.get(j).getStSearch_padding().equals("Y"))
						{
							/*if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR SUBSTR(TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") , "+compare_cond.get(j).getStsearch_Startcharpos()+","+
										compare_cond.get(j).getStsearch_Endcharpos()+") "+compare_cond.get(j).getStSearch_Condition().trim()+"'"+ compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else*/ if(compare_cond.get(j).getStSearch_Condition().trim().equals("!="))
							{
								/*if(j==(compare_cond.size()-1))
								{	
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else*/
								{
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
							else
							{
								stcompare_con = stcompare_con + " OR SUBSTR(TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") , "+compare_cond.get(j).getStsearch_Startcharpos()+","+
										compare_cond.get(j).getStsearch_Endcharpos()+") "+compare_cond.get(j).getStSearch_Condition().trim()+"'"+ compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
						}
						else
						{
							
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("!="))
							{
								/*if(j==(compare_cond.size()-1))
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else*/
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
							else
							{
								stcompare_con = stcompare_con + " OR TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") "+
										compare_cond.get(j).getStSearch_Condition().trim()+" '"+compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
						}
						
						
							i = j;
					}
					
				}
				//logger.info("i value is "+i);
				if(i != (compare_cond.size())-1)
				{
					if(compare_cond.get(i).getStSearch_Condition().equals("!="))
					{
						stcompare_con = stcompare_con + " ) ) AND ";
					}
					else
						stcompare_con = stcompare_con +" ) AND ";
				}
				else
				{
					stcompare_con = stcompare_con +")";
				}
				
			//	logger.info("condition is "+condition);
			
			}

			logger.info("compare condition is "+stcompare_con);
			
			logger.info("***** CompareDaoImpl.getReconParameters End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareDaoImpl.getReconParameters");
		 logger.error(" error in  CompareDaoImpl.getReconParameters", new Exception("CompareDaoImpl.getReconParameters",e));
		 throw e;
	}
	return stcompare_con ;	
}

public String getCompareCondition(CompareBean comparebeanObj,String sttable_Name,int inRec_Set_in) throws Exception
{
	logger.info("***** CompareDaoImpl.getCompareCondition Start ****");
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
			//String a[] = sttable_Name.split("_"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {sttable_Name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory()},Integer.class);
			logger.info("file id is "+file_id);

			//2. Get Parameters from db
			
		//	List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id}, new CompareConditionMaster());
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_COMPARE_CONDITION, new Object[]{file_id,inRec_Set_in}, new CompareConditionMaster());


			//3. Form Proper condition
			
			for(int i = 0; i<compare_cond.size(); i++)
			{

				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = compare_cond.get(i);
				temp_param = filterBeanObj.getStSearch_header().trim();
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{
					if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( "+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( "+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";					
					}
				}
				else
				{
					if(filterBeanObj.getStSearch_Condition().equals("="))
					{
						stcompare_con = stcompare_con + "(TRIM( "+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+filterBeanObj.getStSearch_pattern().trim()+"'";
					}
					else
					{
						stcompare_con = stcompare_con + "(TRIM( "+filterBeanObj.getStSearch_header()+") "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					
				}
				
				for(int j= (i+1); j <compare_cond.size(); j++)
				{
					//logger.info("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(compare_cond.get(j).getStSearch_header()))
					{
							
						if(compare_cond.get(j).getStSearch_padding().equals("Y"))
						{
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR SUBSTR(TRIM( " + compare_cond.get(j).getStSearch_header()+") , "+compare_cond.get(j).getStsearch_Startcharpos()+","+
										compare_cond.get(j).getStsearch_Endcharpos()+") "+compare_cond.get(j).getStSearch_Condition().trim()+"'"+ compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else
							{
								if(j==(compare_cond.size()-1))
								{	
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									stcompare_con = stcompare_con + ", '"+compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						else
						{
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR TRIM( " + compare_cond.get(j).getStSearch_header()+") "+
										compare_cond.get(j).getStSearch_Condition().trim()+" '"+compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else
							{
								if(j==(compare_cond.size()-1))
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"' ";
								}
								
							}
						}
						
						
							i = j;
					}
					
				}
				//logger.info("i value is "+i);
				if(i != (compare_cond.size())-1)
				{
					stcompare_con = stcompare_con +" ) AND ";
				}
				else
					stcompare_con = stcompare_con +")";
				
			//	logger.info("condition is "+condition);
			
			}

			logger.info("compare condition is "+stcompare_con);
			
			
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareDaoImpl.getCompareCondition");
		 logger.error(" error in  CompareDaoImpl.getCompareCondition", new Exception("CompareDaoImpl.getCompareCondition",e));
		 throw e;
	}
	logger.info("***** CompareDaoImpl.getCompareCondition End ****");
	return stcompare_con ;	
}
private static class CompareConditionMaster implements RowMapper<FilterationBean> {

	@Override
	public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		FilterationBean filterbeanObj = new FilterationBean();
		
		filterbeanObj.setStSearch_header(rs.getString("TABLE_HEADER"));
		filterbeanObj.setStSearch_padding(rs.getString("PADDING"));
		filterbeanObj.setStSearch_pattern(rs.getString("PATTERN"));
		filterbeanObj.setStsearch_Startcharpos(rs.getString("START_CHARPOS"));
		filterbeanObj.setStsearch_Endcharpos(rs.getString("CHARSIZE"));
		filterbeanObj.setStSearch_Condition(rs.getString("CONDITION"));
		
		
		return filterbeanObj;
		
		
	}
	}
	
public String getCondition(List<KnockOffBean> knockoff_Criteria)
{
	logger.info("***** CompareDaoImpl.getCondition Start ****");
	String select_parameters = "", condition = "", update_condition="" ;
	List<KnockOffBean> Update_Headers = new ArrayList<>();
	for(int i = 0 ;i<knockoff_Criteria.size();i++)
	{
		if(i == (knockoff_Criteria.size()-1))
		{
			if(knockoff_Criteria.get(i).getStReversal_value()==null)
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
					select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() +" OS2."+knockoff_Criteria.get(i).getStReversal_header();
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header();
				}
			
				
			}
		}
		else
		{
			
			if(knockoff_Criteria.get(i).getStReversal_value()==null)
			{
				Update_Headers.add(knockoff_Criteria.get(i));
				if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
				{
					condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ? AND ";
					select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
				else
				{
					condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
							+" OS2."+knockoff_Criteria.get(i).getStReversal_header()+" AND ";
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ? AND ";
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			
				
			}
			
		}
		
	}
	
	logger.info("condition=="+condition);
	logger.info("update_condition=="+update_condition);
	logger.info("select_parameters=="+select_parameters);
	
	logger.info("***** CompareDaoImpl.getCondition End ****");
	
	return condition;
}
public String getConditionForReversal(List<KnockOffBean> knockoff_Criteria)
{
	
	String select_parameters = "", condition = "", update_condition="" ;
	List<KnockOffBean> Update_Headers = new ArrayList<>();
	for(int i = 0 ;i<knockoff_Criteria.size();i++)
	{
		if(i == (knockoff_Criteria.size()-1))
		{
					condition = condition + knockoff_Criteria.get(i).getStReversal_header()+" = ?";
				
		}
		else
		{
			condition = condition + knockoff_Criteria.get(i).getStReversal_header() + " = ? AND ";
			
		}
		
	}
	return condition;
}

public void clearTables(List<String> tables,CompareBean compareBeanObj)throws Exception
{
	logger.info("***** CompareDaoImpl.clearTables Start ****");
	try
	{
		for(int i = 0 ;i<tables.size();i++)
		{
			String TRUNCATE_QUERY = "TRUNCATE TABLE "+compareBeanObj.getStMergeCategory()+"_"+tables.get(i);
			logger.info("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+compareBeanObj.getStMergeCategory()+"_"+tables.get(i)+"_KNOCKOFF";
			logger.info("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+compareBeanObj.getStMergeCategory()+"_"+tables.get(i)+"_MATCHED";
			logger.info("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(i);
			logger.info("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+compareBeanObj.getStMergeCategory()+"_"+tables.get(i);
			logger.info("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
		 
			
			
		}
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareDaoImpl.clearTables");
		logger.error(" error in  CompareDaoImpl.clearTables", new Exception(" CompareDaoImpl.clearTables",e));
		throw e;
	}
	
	logger.info("***** CompareDaoImpl.clearTables End ****");
}







}
