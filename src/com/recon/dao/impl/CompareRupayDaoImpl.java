package com.recon.dao.impl;


import static com.recon.util.GeneralUtil.ADD_RECORDS;
import static com.recon.util.GeneralUtil.CREATE_RECON_TABLE;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_COLS_COUNT;
import static com.recon.util.GeneralUtil.GET_COMPARE_CONDITION;
import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_CRITERIA;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_PARAMS;
import static com.recon.util.GeneralUtil.GET_MATCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_MATCH_RELAX_PARAMS;
import static com.recon.util.GeneralUtil.GET_RECON_CONDITION;
import static com.recon.util.GeneralUtil.GET_REVERSAL_DETAILS;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_TABLE_NAME;
import static com.recon.util.GeneralUtil.GET_TTUM_PARAMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;



import com.recon.dao.CompareRupayDao;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.util.demo;

@Component
public class CompareRupayDaoImpl extends JdbcDaoSupport implements CompareRupayDao{

	public static String GET_DATA = "";
	public static String PART2_QUERY = "";
	private static final Logger logger = Logger.getLogger(CompareRupayDaoImpl.class);
	private String JOIN1_QUERY = "";
	private String JOIN2_QUERY = "";
//	private String UPDATE1_QUERY = "";
//	private String UPDATE2_QUERY = "";
	private String INSERT1_QUERY ="";
	private String INSERT2_QUERY = "";
	/*private String KNOCKOFF1_COMPARE = "";
	private String KNOCKOFF2_COMPARE = "";*/
	public int SEG_TRAN_ID = 0;
	
	@Override
	public List<List<String>> getTableName(int inRec_Set_Id,String stCategory)throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getTableName Start ****");
		try
		{
			if(stCategory.equals("POS"))
			{
				
				stCategory=stCategory+"_ONUS";
				
			}
			
			String[] stTable_datas = stCategory.split("_");
			String GET_TABLES = "SELECT FILE1,FILE2,FILE1_MATCHED,FILE2_MATCHED," +
						"regexp_substr(FILE1_CATEGORY,'[^_]+',"+(stTable_datas[0].length()+1)+") AS FILE1_CATEGORY,"+
						"regexp_substr(FILE2_CATEGORY,'[^_]+',"+(stTable_datas[0].length()+1)+") AS FILE2_CATEGORY"+
							" FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND RECON_CATEGORY = '"+stCategory+"'";
			
			List<CompareBean> comparebeanObj = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id}, new GetTableDetails());
					
			List<String> table_list = new ArrayList<>();
			CompareBean compareObj = comparebeanObj.get(0);
			table_list.add(compareObj.getStTable1());
			table_list.add(compareObj.getStTable2());
			List<String> Files_subcategories = new ArrayList<>();
			Files_subcategories.add(compareObj.getStTable1_SubCategory());
			Files_subcategories.add(compareObj.getStTable2_SubCategory());
			
			List<List<String>> Recon_files_data = new ArrayList<>();
			
			Recon_files_data.add(table_list);
			if(compareObj.getStTable1_SubCategory()!=null && compareObj.getStTable2_SubCategory()!=null)
			{
			Recon_files_data.add(Files_subcategories);
			}
			
			logger.info("***** CompareRupayDaoImpl.getTableName End ****");
			return Recon_files_data;
	
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getTableName");
			logger.error(" error in CompareRupayDaoImpl.getTableName", new Exception("CompareRupayDaoImpl.getTableName",e));
			
			return null;
		}
	}
	private static class GetTableDetails implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean comparebeanObj = new CompareBean();
			
			comparebeanObj.setStTable1(rs.getString("FILE1"));
			comparebeanObj.setStTable2(rs.getString("FILE2"));
			comparebeanObj.setStTable1_SubCategory(rs.getString("FILE1_CATEGORY"));
			comparebeanObj.setStTable2_SubCategory(rs.getString("FILE2_CATEGORY"));			
			comparebeanObj.setStMatched_file1(rs.getString("FILE1_MATCHED"));
			comparebeanObj.setStMatched_file2(rs.getString("FILE2_MATCHED"));
			return comparebeanObj;
			
			

		}
	}
	
	private static class ReversalParameterMaster implements RowMapper<KnockOffBean> {
		@Override
		public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
 
			KnockOffBean knockOffBean = new KnockOffBean();
			knockOffBean.setStReversal_header(rs.getString("HEADER"));
			knockOffBean.setStReversal_value(rs.getString("VALUE"));
 
			return knockOffBean;
			
			
		}
}
	@Override
	public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.removeDuplicates Start ****");
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
							compareBeanObj.getStTable1_SubCategory()},Integer.class);
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), compareBeanObj.getStCategory()+"_"+
							compareBeanObj.getStTable1_SubCategory()},new ReversalId());
	 
				}
				else
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { tables.get(loop),compareBeanObj.getStCategory(),
							compareBeanObj.getStTable2_SubCategory()},Integer.class);			
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), compareBeanObj.getStCategory()
							+"_"+compareBeanObj.getStTable2_SubCategory()},new ReversalId());
 
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

				//CHANGES MADE BY INT5779 AS PER MAIL RECEIVED ON 19TH MARCH 2018
				if(compareBeanObj.getStCategory().equalsIgnoreCase("RUPAY") && inrec_set_id == 1)
				{
					if(tables.get(loop).equalsIgnoreCase("CBS"))
					{
						//CONDITION 1
						String MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
								" OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inrec_set_id
								+"' WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id
								+"' AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
								" OS2 WHERE OS2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"' AND "

								+"OS1.REMARKS = OS2.REMARKS AND OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND NVL(OS1.SYS_REF,'NE') = NVL(OS2.SYS_REF, 'NE') " +
								" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()

								//+"OS1.REMARKS = OS2.REMARKS AND OS1.PARTICULARALS = OS2.PARTICULARALS AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF" +
								//ADDED BY INT5779 --- CHANGING CONDITION AS PER SAMEER'S MAIL ON 20TH MARCH 2018 
								+"OS1.REMARKS = OS2.REMARKS AND OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF" +
								" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()

								+"' ) AND "+compare_cond
								+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

						String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id
								+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
								+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" OS2 WHERE OS1.REMARKS = OS2.REMARKS " +

								"AND OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND NVL(OS1.SYS_REF, 'NE') = NVL(OS2.SYS_REF,'NE') "
								+" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
								+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"

								//"AND OS1.PARTICULARALS = OS2.PARTICULARALS AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF "
								//ADDED BY INT5779 --- CHANGING CONDITION AS PER SAMEER'S MAIL ON 20TH MARCH 2018 
								+"AND OS1.AMOUNT = OS2.AMOUNT AND OS1.REF_NO = OS2.REF_NO AND OS1.SYS_REF = OS2.SYS_REF "
								+" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
								+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";


						
			/*			String DELETE_RECORDS =	"delete from  "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" org where exists ( "+
							"	Select 1 from  ( "+ 
							"	SELECT remarks, amount, ref_no ,sys_ref, ROW_NUMBER() OVER (PARTITION BY remarks, amount, ref_no ,sys_ref " + 
							" ORDER BY (  0)) RN "+ 
								 " FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" os1 where os1.filedate = '"+compareBeanObj.getStFile_date()+"' "+
							" ) dups where  dups.RN > 1 and dups.remarks = org.remarks and dups.amount = org.amount and dups.ref_no = org.ref_no and dups.sys_ref = org.sys_ref " + 
								"   ) and org.filedate = '"+compareBeanObj.getStFile_date()+"' AND "+ compare_cond ;*/
						
						
						
						logger.info("update duplicate query1 is "+MOVE_QUERY);
			
						getJdbcTemplate().execute(MOVE_QUERY);
						
						
						logger.info("delete query1 is "+DELETE_RECORDS);
						getJdbcTemplate().execute(DELETE_RECORDS);

						//CONDITION 2
						MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
								" OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inrec_set_id
								+"' WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id
								+"' AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
								" OS2 WHERE OS2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"' AND "
								+"OS1.REMARKS = OS2.REMARKS AND OS1.AMOUNT = OS2.AMOUNT AND SUBSTR(OS1.REF_NO,1,16) = SUBSTR(OS2.REF_NO,1,16) " +
								" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()
								+"' ) AND "+compare_cond
								+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

						DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id
								+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
								+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" OS2 WHERE OS1.REMARKS = OS2.REMARKS " +
								"AND OS1.AMOUNT = OS2.AMOUNT AND SUBSTR(OS1.REF_NO,1,16) = SUBSTR(OS2.REF_NO,1,16) "
 				+" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
								+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"
/*
						DELETE_RECORDS = " Select * from   "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" org where exists ( " + 
 " Select 1 from  ( " + 
" SELECT remarks, REF_NO, AMOUNT ,FILEDATE, ROW_NUMBER() OVER (PARTITION BY remarks, REF_NO, AMOUNT ,FILEDATE " +
                                       " ORDER BY (  0)) RN "+ 
  " FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" os1 where os1.filedate = '"+compareBeanObj.getStFile_date()+"' " +
"   ) dups where  dups.RN > 1 and  dups.FILEDATE = '"+compareBeanObj.getStFile_date()+"'  and dups.REMARKS = org.REMARKS and SUBSTR(dups.REF_NO,1,16) = SUBSTR(org.REF_NO,1,16) and dups.AMOUNT = org.AMOUNT "+
"    ) and org.filedate = '"+compareBeanObj.getStFile_date()+"' and "+ compare_cond  ;*/
   
   
						

								+" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
								+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";


						logger.info("update duplicate query2 is "+MOVE_QUERY);

						
						getJdbcTemplate().execute(MOVE_QUERY);
						
						logger.info("delete query2 is "+DELETE_RECORDS);
						getJdbcTemplate().execute(DELETE_RECORDS);

					}
					else if(tables.get(loop).equalsIgnoreCase("SWITCH"))
					{
						//RELAXING AUTHNUM
						String MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_SWITCH OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()
								+"-UNRECON-"+inrec_set_id+"' WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"'"
								+" AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_SWITCH OS2 WHERE OS2.DCRS_REMARKS = " 
								+"'"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"' AND "
								+" (SUBSTR(OS1.TRACE,2,6) = SUBSTR(OS2.TRACE,2,6) AND OS1.PAN = OS2.PAN AND OS1.AMOUNT = OS2.AMOUNT) " +
								" AND OS2.FILEDATE = '"
								+compareBeanObj.getStFile_date()+"')"
								+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

						//DELETING DUPLICATES IN CASE OF SWITCH

						String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inrec_set_id+" OS1 "
								+" WHERE ROWID != (SELECT MAX(ROWID) FROM "+compareBeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inrec_set_id+" OS2 WHERE "
								+" (SUBSTR(OS1.TRACE,2,6) = SUBSTR(OS2.TRACE,2,6) AND OS1.PAN = OS2.PAN AND OS1.AMOUNT = OS2.AMOUNT )" +
								" AND OS2.FILEDATE = '"
								+compareBeanObj.getStFile_date()+"' )"

								+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";
						

							//	+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

						logger.info("update duplicate query is "+MOVE_QUERY);
					

						getJdbcTemplate().execute(MOVE_QUERY);
						
						logger.info("delete query is "+DELETE_RECORDS);
						getJdbcTemplate().execute(DELETE_RECORDS);

					}
				}
				else
				{
					for(int reverse_id :reversalid)
					{

						try
						{
							//2. GET KNOCKOFF CONDITION FROM MAIN_KNOCKOFF_CRITERIA
							List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reverse_id , file_id}, new KnockOffCriteriaMaster());
							String knockoff_condition = getCondition(knockoff_Criteria);
							logger.info("knockoff_ condition is "+knockoff_condition);

							if(tables.get(loop).equalsIgnoreCase("CBS"))
							{
								String MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
										" OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inrec_set_id
										+"' WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id
										+"' AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
										" OS2 WHERE OS2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"' AND os1.remarks = os2.remarks and  "
										+knockoff_condition +" AND OS1.REMARKS = OS2.REMARKS AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()
										+"' ) AND "+compare_cond
										+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

							/*	String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id
										+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
										+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" OS2 WHERE "+knockoff_condition

										+" AND OS1.REMARKS = OS2.REMARKS AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
										+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";*/
								
					String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id
							+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
							+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" OS2 WHERE "+knockoff_condition
							+" AND OS1.REMARKS = OS2.REMARKS AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
							+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"

										+" AND OS1.REMARKS = OS2.REMARKS AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"' ) AND "
										+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";


								logger.info("update duplicate query is "+MOVE_QUERY);
								logger.info("delete query is "+DELETE_RECORDS);
								getJdbcTemplate().execute(MOVE_QUERY);
								getJdbcTemplate().execute(DELETE_RECORDS);
							}
							else
							{

								String MOVE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
										" OS1 SET OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inrec_set_id
										+"' WHERE OS1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id
										+"' AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+tables.get(loop)+
										" OS2 WHERE OS2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-MATCHED-"+inrec_set_id+"' AND os1.pan = os2.pan and "
										+knockoff_condition +" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"') AND "+compare_cond
										+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

								String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id
										+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "
										+compareBeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inrec_set_id+" OS2 WHERE "+knockoff_condition
										+" AND OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"') AND "
										+compare_cond+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'";

								logger.info("update duplicate query is "+MOVE_QUERY);
							
								getJdbcTemplate().execute(MOVE_QUERY);
								
								
								logger.info("delete query is "+DELETE_RECORDS);
								getJdbcTemplate().execute(DELETE_RECORDS);

							}
						}
						catch(Exception e)
						{
							demo.logSQLException(e, "CompareRupayDaoImpl.removeDuplicates");
							logger.error(" error in CompareRupayDaoImpl.removeDuplicates", new Exception("CompareRupayDaoImpl.removeDuplicates",e));
							throw e;
						}	

					}
				}
				loop++;
			}
			logger.info("***** CompareRupayDaoImpl.removeDuplicates End ****");
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.removeDuplicates");
			logger.error(" error in CompareRupayDaoImpl.removeDuplicates", new Exception("CompareRupayDaoImpl.removeDuplicates",e));
			throw e;
		}
	}
	

	public void removeDuplicatesFromCycle2(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.removeDuplicatesFromCycle2 Start ****");
		try
		{
			int loop = 0;
			while(loop < tables.size())
			{
				if(tables.get(loop).equalsIgnoreCase("SWITCH"))
				{
					String UPDATE_DUPLICATE = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_SWITCH OS1 SET DCRS_REMARKS = 'VISA_ISS-UNRECON-2' WHERE ROWID != ("
								+"SELECT MAX(ROWID) FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_SWITCH OS2 WHERE "
								+"(OS1.PAN = OS2.PAN AND OS1.AUTHNUM = OS2.AUTHNUM) AND OS2.FILEDATE='"+compareBeanObj.getStFile_date()
								+"' AND OS2.DCRS_REMARKS = 'VISA_ISS-MATCHED-2')"
								+" AND OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"' AND OS1.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'";
					
					String DELETE_RECORDS = "DELETE FROM "+compareBeanObj.getStMergeCategory()+"_SWITCH_MATCHED2 OS1 WHERE OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"' AND "
								+" ROWID != (SELECT MAX(ROWID) FROM "+compareBeanObj.getStMergeCategory()+"_SWITCH_MATCHED2 OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()
								+"' AND (OS1.PAN = OS2.PAN  AND OS1.AUTHNUM = OS2.AUTHNUM)) ";
					
					logger.info("UPDATE DUPLICATE QUERY IS "+UPDATE_DUPLICATE);
					
					logger.info("DELETE RECORDS QUERY IS "+DELETE_RECORDS);
					try
					{
					getJdbcTemplate().execute(UPDATE_DUPLICATE);
					getJdbcTemplate().execute(DELETE_RECORDS);
					}
					catch(Exception e)
					{
						demo.logSQLException(e, "CompareRupayDaoImpl.removeDuplicatesFromCycle2");
						 logger.error(" error in   CompareRupayDaoImpl.removeDuplicatesFromCycle2 ", new Exception(" CompareRupayDaoImpl.removeDuplicatesFromCycle2 ",e));
						 throw e;
					}
					
				}
				else if(tables.get(loop).equalsIgnoreCase("VISA"))
				{
					//DELETE THE RECORD IF CARD NO, AUTHCODE ,ARN AND DCRS_SEQ_NO ARE SAME
					String DELETE_DUPLICATE = "DELETE FROM VISA_ISS_VISA_MATCHED2 OS1 WHERE OS1.FILEDATE= '"+compareBeanObj.getStFile_date()+"'" +
									" AND ROWID != (SELECT MAX(ROWID) FROM VISA_ISS_VISA_MATCHED2 OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"'" +
									" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE AND OS1.DCRS_SEQ_NO = OS2.DCRS_SEQ_NO "+
									" AND OS1.ARN = OS2.ARN))";
					logger.info("DELETE DUPLICATE QUERY IS "+DELETE_DUPLICATE);
					getJdbcTemplate().execute(DELETE_DUPLICATE);
					
					DELETE_DUPLICATE = "DELETE FROM SETTLEMENT_VISA_VISA OS1 WHERE OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'" +
							" AND OS1.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'"+
							" AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_VISA_VISA OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"'" +
							" AND OS2.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'"+
							" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE AND OS1.DCRS_SEQ_NO = OS2.DCRS_SEQ_NO "+
							" AND OS1.ARN = OS2.ARN AND OS1.DCRS_REMARKS = OS2.DCRS_REMARKS))";
					
					logger.info("DELETE DUPLICATE QUERY IS "+DELETE_DUPLICATE);
					getJdbcTemplate().execute(DELETE_DUPLICATE);
										
					//MOVE THE RECORDS TO UNRECON ON BASIS OF CARD NO AND AUTHCODE
					String UPDATE_DUPLICATE = "UPDATE SETTLEMENT_VISA_VISA OS1 SET DCRS_REMARKS = 'VISA_ISS-UNRECON-2' WHERE OS1.FILEDATE = '"
									+compareBeanObj.getStFile_date()+"'" +
									" AND OS1.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'"+
									" AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_VISA_VISA OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()
									+"' AND OS2.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'" +
									" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE AND OS1.DCRS_REMARKS = OS2.DCRS_REMARKS))";
					
					/*DELETE_DUPLICATE= "DELETE FROM SETTLEMENT_VISA_VISA OS1 WHERE TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"
							+compareBeanObj.getStFile_date()+"'" +
							" AND OS1.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'"+
							" AND ROWID != (SELECT MAX(ROWID) FROM SETTLEMENT_VISA_VISA OS2 WHERE TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()
							+"' AND OS2.DCRS_REMARKS = 'VISA_ISS-MATCHED-2'" +
							" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE AND OS1.DCRS_REMARKS = OS2.DCRS_REMARKS))";*/
					logger.info("UPDATE DUPLICATE QUERY IS "+UPDATE_DUPLICATE);
					getJdbcTemplate().execute(UPDATE_DUPLICATE);
					
					DELETE_DUPLICATE = "DELETE FROM VISA_ISS_VISA_MATCHED2 OS1 WHERE OS1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'" +
							" AND ROWID != (SELECT MAX(ROWID) FROM VISA_ISS_VISA_MATCHED2 OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()+"'" +
							" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE ))";
					logger.info("DELETE DUPLICATE QUERY IS "+DELETE_DUPLICATE);
					getJdbcTemplate().execute(DELETE_DUPLICATE);
					
					/*DELETE_DUPLICATE = "DELETE FROM TEMP_VISA_ISS_VISA OS1 WHERE TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'" +
							" AND ROWID != (SELECT MAX(ROWID) FROM TEMP_VISA_ISS_VISA OS2 WHERE TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'" +
							" AND (OS1.CARD_NUMBER = OS2.CARD_NUMBER AND OS1.AUTHORIZATION_CODE = OS2.AUTHORIZATION_CODE ))";
					logger.info("DELETE DUPLICATE QUERY IS "+DELETE_DUPLICATE);
					getJdbcTemplate().execute(DELETE_DUPLICATE);*/
				}
				loop++;
			}
			logger.info("***** CompareRupayDaoImpl.removeDuplicatesFromCycle2 End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.removeDuplicatesFromCycle2");
			 logger.error(" error in   CompareRupayDaoImpl.removeDuplicatesFromCycle2 ", new Exception(" CompareRupayDaoImpl.removeDuplicatesFromCycle2 ",e));
			 throw e;
		}
	}
	@Override
	public List<Integer> getRec_set_id(String stCategory)throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getRec_set_id Start ****");
		try
		{
			String GET_RECON_ID = "SELECT REC_SET_ID FROM MAIN_RECON_SEQUENCE WHERE RECON_CATEGORY = ? ORDER BY REC_SET_ID" ;
			List<Integer> rec_set_id = getJdbcTemplate().query(GET_RECON_ID, new Object[] {stCategory}, new recSetIDMapper());
			logger.info("GET_RECON_ID=="+GET_RECON_ID);
			
			logger.info("***** CompareRupayDaoImpl.getRec_set_id End ****");
			
			return rec_set_id;
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getRec_set_id");
			 logger.error(" error in   CompareRupayDaoImpl.getRec_set_id ", new Exception(" CompareRupayDaoImpl.getRec_set_id ",e));
			 
			return null;
			
		}
		
	}
	
	
	/*@Override
	public List<Integer> getRec_domesticset_id(String stCategory)throws Exception
	{
		String GET_RECON_ID= "";
		try
		{
			if(stCategory.contains("RUPAY"))
			{
				GET_RECON_ID = "SELECT REC_SET_ID FROM MAIN_RECON_SEQUENCE WHERE (CATEGORY = ? OR CATEGORY = 'RUPAY_SURCHARGE')ORDER BY REC_SET_ID" ;
			}
			else if(stCategory.contains("VISA"))
			{
				GET_RECON_ID = "SELECT REC_SET_ID FROM MAIN_RECON_SEQUENCE WHERE (CATEGORY = ? OR CATEGORY = 'VISA_SURCHARGE')ORDER BY REC_SET_ID" ;	
			}
			List<Integer> rec_set_id = getJdbcTemplate().query(GET_RECON_ID, new Object[] {stCategory}, new recSetIDMapper());			
			return rec_set_id;
		}
		catch(Exception e)
		{
			logger.info("get Rec Set Id "+e);
			return null;
			
		}
		
	}*/
	
	
	private static class recSetIDMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			
			int rec_id_list = (rs.getInt("REC_SET_ID"));
			
			return rec_id_list;
			
			

		}
	}
	
	
	public String getStatus(CompareBean comparebeanObj,String stFile_name,String stProcess) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getStatus Start ****");
		int file_id = 0;
		try
		{
			String GET_STATUS = "SELECT "+stProcess+" FROM MAIN_FILESOURCE WHERE FILEID = ?";
			logger.info("GET_STATUS is "+GET_STATUS);
			if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				if(comparebeanObj.getStCategory().equals("RUPAY"))
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name, comparebeanObj.getStCategory(),"DOMESTIC"},Integer.class);
				}
				else if(comparebeanObj.getStCategory().equals("VISA"))
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name, comparebeanObj.getStCategory(),"ISSUER"},Integer.class);
				}
			}
			else
			{
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name, comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory()},Integer.class);
			}
			logger.info("file_id is "+file_id);
			String chFilter_Status = getJdbcTemplate().queryForObject(GET_STATUS, new Object[] {file_id}, String.class);
			logger.info("chFilter_Status is "+chFilter_Status);
			logger.info("***** CompareRupayDaoImpl.getStatus End ****");
			
			return chFilter_Status;
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getStatus");
			 logger.error(" error in CompareRupayDaoImpl.getStatus", new Exception(" CompareRupayDaoImpl.getStatus ",e));
			return "N";
			
		}
		
	}
	
	public int getRelax_paramCount(CompareBean comparebeanObj,int inrec_set_id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getRelax_paramCount Start ****");
		try
		{
			String GET_COUNT = "SELECT COUNT(*) FROM MAIN_MATCHING_CRITERIA WHERE RELAX_PARAM = 'Y' AND CATEGORY = ? AND REC_SET_ID = ?";
			int relax_count  = getJdbcTemplate().queryForObject(GET_COUNT, new Object[] {(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inrec_set_id}, Integer.class);
			logger.info("GET_COUNT=="+GET_COUNT);
			logger.info("relax_count=="+relax_count);
			logger.info("***** CompareRupayDaoImpl.getRelax_paramCount End ****");
			return relax_count;
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getRelax_paramCount");
			 logger.error(" error in CompareRupayDaoImpl.getRelax_paramCount", new Exception(" CompareRupayDaoImpl.getRelax_paramCount ",e));
			return 0;
			
		}
		
	}
	
	public List<String> getMatchedFlag(CompareBean comparebeanObj,int inRec_Set_Id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getMatchedFlag Start ****");
		List<String> matched_flags = new ArrayList<>();
		List<CompareBean> comparebean;
		try
		{
			//String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND RECON_CATEGORY = ?";
			String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND (FILE1_CATEGORY = ? OR FILE2_CATEGORY = ?)";
			if(!comparebeanObj.getStSubCategory().equals("-"))
			{
				logger.info("category is "+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory());
				logger.info("rec set id is "+inRec_Set_Id);
				
				comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())}, new GetTableDetails());
			}
			else
				comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,comparebeanObj.getStCategory()}, new GetTableDetails());
			
			
			CompareBean compareBean = comparebean.get(0);
			matched_flags.add(compareBean.getStMatched_file1());
			matched_flags.add(compareBean.getStMatched_file2());
			
			logger.info("***** CompareRupayDaoImpl.getMatchedFlag End ****");
			
			return matched_flags;
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getMatchedFlag");
			 logger.error(" error in CompareRupayDaoImpl.getMatchedFlag", new Exception(" CompareRupayDaoImpl.getMatchedFlag ",e));
			return null;
			
		}
		
	}
	
	
	@Override
	public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.moveData Start ****");
		String tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE";
		//Connection conn;
		//PreparedStatement pstmt ;
		//ResultSet rset;
		String columns;
		
		//String condition = "";
		int tableExist = 0;
		String CHECK_TABLE ="";
		String stMatch_param = "";
		int file_id = 0 ;
		try
		{
		//	List<String> tables = getTableName(inRec_Set_id);
			
		
			//conn = dbconn.getCon();
		//*********1. CREATE TEMP TABLE
				//get table cols
			
			//get MATCHED FLAGS FOR THE TABLES
			List<String> matched_flags = getMatchedFlag(comparebeanObj, inRec_set_Id);
			String stMatched_flag;
			int loop = 0;
			while(loop < tables.size())
			{
				//condition = "";
				stMatch_param = "";
				stMatched_flag = matched_flags.get(loop);

				columns ="SEG_TRAN_ID";
				tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE ";
				String stFile_name = tables.get(loop);
				logger.info("File NAME IS "+stFile_name);
				 
				 
				if(loop == 0)
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name,comparebeanObj.getStCategory(),comparebeanObj.getStTable1_SubCategory()},Integer.class);
					logger.info("file Id is "+file_id);
				}
				else if(loop == 1)
				{

					///1. GET THE HEADERS FROM DB
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("file Id is "+file_id);

				}
				String stRaw_TableName = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);

				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				columns = columns+","+stFile_headers;
				columns = stFile_headers;
				//changes to be done here for knockoff flag and filteration flag off
				if(stFile_name.equals("CBS"))
				{
					stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
					columns = columns +",MAN_CONTRA_ACCOUNT";
				}

				String[] col_names = stFile_headers.trim().split(",");
				for(int i=0 ;i <col_names.length; i++)
				{
 					tableCols = tableCols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
				}


				//CHECKING WHETHER TABLE IS ALREADY PRESENT
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				//logger.info("table exists value is "+tableExist);
				if(tableExist == 0)
				{
					//create temp table
					String query = "CREATE TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"("+tableCols+")";
					logger.info("CREATE QUERY IS "+query);
					getJdbcTemplate().execute(query);
				}

				//CHECKING WHETHER MATCHED RECORDS TABLE IS PRESENT
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+(comparebeanObj.getStMergeCategory()+"_"+stFile_name).toUpperCase()+"_MATCHED"+inRec_set_Id+"'";
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				//logger.info("table exists value is "+tableExist);
				if(tableExist == 0)
				{
					//create temp table
					String query = "CREATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+inRec_set_Id+" ("+tableCols+", RELAX_PARAM VARCHAR2 (100 BYTE))";
					logger.info("CREATE QUERY IS "+query);
					getJdbcTemplate().execute(query);
				}
				
				// WE HAVE TO ADD BELOW COLUMNS AS THEY ARE NEEDED FURTHER
				
				if(stFile_name.equals("SWITCH"))
				{
					
					CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
					tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					
					logger.info("CHECK_TABLE IS "+CHECK_TABLE);
					logger.info("tableExist IS "+tableExist);

					if(tableExist == 1)
					{
						int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"MAN_CONTRA_ACCOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"CONTRA_ACCOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD CONTRA_ACCOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"CBS_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD CBS_AMOUNT VARCHAR (100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"DIFF_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD DIFF_AMOUNT VARCHAR (100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
					}
					
					if(comparebeanObj.getStCategory().equalsIgnoreCase("VISA") && comparebeanObj.getStSubCategory().equalsIgnoreCase("SURCHARGE") && inRec_set_Id == 3)
					{
						int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","DIFF_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","DCRS_SEQ_NO"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD DCRS_SEQ_NO VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","ARN"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD ARN VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","SOURCE_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH  ADD SOURCE_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","SOURCE_CURR_CODE"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD SOURCE_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","DESTINATION_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD DESTINATION_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","DESTINATION_CURR_CODE"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD DESTINATION_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH","SETTLEMENT_FLAG"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_SWITCH ADD SETTLEMENT_FLAG VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						//ALTERING SWITCH VISA MATCHED TABLE
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DIFF_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DCRS_SEQ_NO"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DCRS_SEQ_NO VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"ARN"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD ARN VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SOURCE_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SOURCE_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SOURCE_CURR_CODE"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SOURCE_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DESTINATION_AMOUNT"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DESTINATION_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DESTINATION_CURR_CODE"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DESTINATION_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SETTLEMENT_FLAG"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SETTLEMENT_FLAG VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
					}
				}

				//-------------------------------------------------- CHECK IF FILE MATCHED FLAG IS Y
				if(stMatched_flag.equals("Y"))
				{
					CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
					tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					logger.info("CHECK_TABLE IS "+CHECK_TABLE);
					logger.info("tableExist IS "+tableExist);
					String select_cols = "";
					if(tableExist == 1)
					{
						int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"RELAX_PARAM"}, Integer.class);
						logger.info("Column_count IS "+Column_count);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD RELAX_PARAM VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY IS "+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
					}


					/*if(stFile_name.equals("SWITCH"))
//					{CBS_NFS_ACQ_RAWDATA
						CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
						tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);

						if(tableExist == 1)
						{
							int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"MAN_CONTRA_ACCOUNT"}, Integer.class);

							if(Column_count == 0)
							{
								String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";

								getJdbcTemplate().execute(ALTER_QUERY);
							}
							Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"CONTRA_ACCOUNT"}, Integer.class);

							if(Column_count == 0)
							{
								String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD CONTRA_ACCOUNT VARCHAR(100 BYTE)";
								getJdbcTemplate().execute(ALTER_QUERY);
							}
							Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"CBS_AMOUNT"}, Integer.class);

							if(Column_count == 0)
							{
								String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD CBS_AMOUNT VARCHAR (100 BYTE)";
								getJdbcTemplate().execute(ALTER_QUERY);
							}
							Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase(),"DIFF_AMOUNT"}, Integer.class);

							if(Column_count == 0)
							{
								String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD DIFF_AMOUNT VARCHAR (100 BYTE)";
								getJdbcTemplate().execute(ALTER_QUERY);
							}
						}
					}*/

					
					if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						if(comparebeanObj.getStCategory().equals("RUPAY"))
						{
							
							//List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStCategory()+"_DOM_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)},new ColumnMapper());
							List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
							logger.info("TABLE_COLS IS "+TABLE_COLS);
							int count = 0;
							for(String cols : TABLE_COLS)
							{
								if(count == 0)
									{
									select_cols = cols;
										count++;
									}
								else
									select_cols = select_cols+","+cols;
							}

							GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStCategory()+"_DOM"+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date();

					//		columns = "MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,RELAX_PARAM,CBS_AMOUNT,DIFF_AMOUNT,"+columns;
							GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStCategory()+"_DOM"+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()

									+"' AND RELAX_PARAM = 'Y'";
						}
						else if(comparebeanObj.getStCategory().equals("VISA"))
						{
							List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
							
							int count = 0;
							for(String cols : TABLE_COLS)
							{
								if(count == 0)
									{
									select_cols = cols;
										count++;
									}
								else
									select_cols = select_cols+","+cols;
							}
					//		columns = "MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,RELAX_PARAM,CBS_AMOUNT,DIFF_AMOUNT,"+columns;
							GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStCategory()+"_ISS_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
									+"' AND RELAX_PARAM = 'Y'";
							
						}
					}
					else
					{
						List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
						
						int count = 0;
						for(String cols : TABLE_COLS)
						{
							if(count == 0)
								{
								select_cols = cols;
									count++;
								}
							else
								select_cols = select_cols+","+cols;
						}

						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
								+"' "; 

					//	columns = "MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,RELAX_PARAM,CBS_AMOUNT,DIFF_AMOUNT,"+columns;
						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
								+"' ";//AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";

					}
				 
					ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" ("+select_cols+") "+GET_DATA;
					logger.info("IN FLAG N ADD_RECORDS IS "+ADD_RECORDS);
					logger.info("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
					getJdbcTemplate().execute(ADD_RECORDS);
					logger.info("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));

				}
				else
				{
					//--------------------------------------------MOVING DATA TO TEMP TABLE--------------------------------------------------------------------------------------

					//CHECK IF FILTERATION AND KNOCKOFF IS DONE FOR THAT FILE IF NOT THEN MOVE THE DATA DIRECTLY FROM RAW TABLE
					String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
					String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");

					if(stknockoff_Status.equals("N") && stFilter_Status.equals("N"))
					{
						String settlement_col = "CREATEDDATE, CREATEDBY,FILEDATE,DCRS_REMARKS";
						//CHECK SETTLEMENT TABLE IS PRESENT OR NOT
						String CHECK_SETTLEMENT = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+"'";
						logger.info("CHECK_SETTLEMENT IS "+CHECK_SETTLEMENT);
						int tableSettlementExist = getJdbcTemplate().queryForObject(CHECK_SETTLEMENT, new Object[] { },Integer.class);
						logger.info("tableSettlementExist IS "+tableSettlementExist);
						settlement_col = settlement_col+","+stFile_headers;

						String[] settlement_cols =stFile_headers.split(",");
						//String[] columns = stFile_headers.split(",");

						if(tableSettlementExist == 0)
						{
							String settle_col = "";
							if(stFile_name.equals("CBS"))
							{
								settle_col = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE)," +
										"FILEDATE DATE,DCRS_REMARKS VARCHAR(100 BYTE),SEG_TRAN_ID NUMBER, MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";
							}
							else
							{
								settle_col = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE,DCRS_REMARKS VARCHAR(100 BYTE),SEG_TRAN_ID NUMBER";
							}
							for(int m = 0 ;m <settlement_cols.length; m++)
							{
								settle_col = settle_col +","+ settlement_cols[m]+" VARCHAR (100 BYTE) ";
							}



							//create MAN RAW table
							String Create_query = "CREATE TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" ("+settle_col+")";
							logger.info("Create_query IS "+Create_query);
							PreparedStatement pstable = getConnection().prepareStatement(Create_query);
							pstable.execute();

							pstable = null;


						}
						//List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {stRaw_TableName},new ColumnMapper());
						List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
						String select_cols = "";
						int count = 0;
						for(String cols : TABLE_COLS)
						{
							if(count == 0)
								{
								select_cols = cols;
									count++;
								}
							else
								select_cols = select_cols+","+cols;
						}
						select_cols = select_cols.replace("SEG_TRAN_ID,", "");
						select_cols = select_cols.replace(",SEG_TRAN_ID","" );
						select_cols = select_cols.replace("CREATEDDATE,", "");
						select_cols = select_cols.replace(",CREATEDDATE", "");
						select_cols = select_cols.replace(",CREATEDBY", "");
						select_cols = select_cols.replace("CREATEDBY,", "");
						if(inRec_set_Id == 2 && stFile_name.equalsIgnoreCase("RUPAY")){
							select_cols = select_cols.replace("RELAX_PARAM,", "");
							}
						if(comparebeanObj.getStSubCategory().equals("DOMESTIC"))
						{
							
							//GET DATA FROM RAW TABLE
							GET_DATA = "SELECT SYSDATE,'"+comparebeanObj.getStEntryBy()+"',"+select_cols+" FROM "+stRaw_TableName+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
									+"' AND FLAG = 'D'";
									//+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND FLAG = 'D'";
						}
						else if(comparebeanObj.getStSubCategory().equals("INTERNATIONAL"))
						{

						//GET DATA FROM RAW TABLE
						GET_DATA = "SELECT SYSDATE,'"+comparebeanObj.getStEntryBy()+"',"+select_cols+" FROM "+stRaw_TableName+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
									+"' AND FLAG = 'I'";
								//+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND FLAG= 'I'";
						}
						else
						{
							//GET DATE FROM RAW TABLE FOR VISA
							GET_DATA = "SELECT SYSDATE,'"+comparebeanObj.getStEntryBy()+"',"+select_cols+" FROM "+stRaw_TableName+" WHERE FILEDATE = '"+comparebeanObj.getStFile_date()
									+"'";
						}
											

						logger.info("KNOCKOFF IS NOT DONE FOR THE FILE "+stFile_name);
						logger.info("NOW GET_DATA QUERY IS "+GET_DATA);

						/*ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
								"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";*/
						ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,"+select_cols+") "+GET_DATA;
						logger.info("IN FLAG N ADD_RECORDS IS "+ADD_RECORDS);
						logger.info("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
						//insertBatch(ADD_RECORDS,rset, cols);
						getJdbcTemplate().execute(ADD_RECORDS);
						logger.info("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));

					}
					else if(stFilter_Status.equals("Y") && stknockoff_Status.equals("N"))//CHANGES FOR VISA AS VISA NEEDS FILTERATION BUT NO KNOCKOFF
					{
						List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
						String select_cols = "";
						int count = 0;
						for(String cols : TABLE_COLS)
						{
							if(count == 0)
								{
								select_cols = cols;
									count++;
								}
							else
								select_cols = select_cols+","+cols;
						}
						
						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" WHERE FILEDATE = '"
									+comparebeanObj.getStFile_date()+"'";
						logger.info("GET_DATA QUERY IS "+GET_DATA);
						
						/*ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'"+
										comparebeanObj.getStEntryBy()+"',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";*/
						ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" ("+select_cols+") "+GET_DATA;
						logger.info("insert query is "+ADD_RECORDS);
						logger.info("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
						//insertBatch(ADD_RECORDS,rset, cols);
						getJdbcTemplate().execute(ADD_RECORDS);
						logger.info("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
						
					}
					else
					{
						//**********2. GET THE DATA FROM ORIGINAL TABLE
						//get the condition for getting original transactions from table

						//GET_DATA = "SELECT * FROM "+table_name + " WHERE KNOCKOFF_FLAG = 'N'";
						
						List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+stFile_name},new ColumnMapper());
						String select_cols = "";
						int count = 0;
						for(String cols : TABLE_COLS)
						{
							if(count == 0)
								{
								select_cols = cols;
									count++;
								}
							else
								select_cols = select_cols+","+cols;
						}
						
						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name + " OS1 WHERE OS1.FILEDATE = '"+comparebeanObj.getStFile_date()+"'";
						logger.info("GET_DATA QUERY IS "+GET_DATA);



						//GET PARAMETERS FOR CONSIDERING THE RECORDS FOR COMPARE
						stMatch_param = getReconParameters(comparebeanObj,stFile_name,inRec_set_Id);

						if(!stMatch_param.equals(""))
						{
							//GET_DATA = GET_DATA +" AND "+ stMatch_param;
							//GET_DATA = GET_DATA + stMatch_param + "  AND NOT EXISTS ( ";
							/*GET_DATA = GET_DATA + stMatch_param //+ " AND TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
									+" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND NOT EXISTS ( ";*/
							GET_DATA = GET_DATA + " AND "+ stMatch_param; //+ " AND TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
									//+" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' ";
						}


 
							ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" ("+select_cols+") "+GET_DATA;
							logger.info("ADD_RECORDS=="+ADD_RECORDS);
							logger.info("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
							getJdbcTemplate().execute(ADD_RECORDS);
							logger.info("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
						//}

					}

				}

				logger.info("MoveData() completed for the file!!!!!!!!!!!!!!!!!!!!!!!! "+stFile_name);
				loop++;
			}
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.moveData");
			 logger.error(" error in   CompareRupayDaoImpl.moveData ", new Exception("  CompareRupayDaoImpl.moveData ",e));
			 throw e;
		}
		logger.info("***** CompareRupayDaoImpl.moveData End ****");
		return 1;
		
		
	}
	public String getLoop2Condition(List<KnockOffBean> knockoff_Criteria,String alias)
	{
		
		String select_parameters = "", condition = "", update_condition="" ;
		List<KnockOffBean> Update_Headers = new ArrayList<>();
		for(int i = 0 ;i<knockoff_Criteria.size();i++)
		{
			if(i == (knockoff_Criteria.size()-1))
			{
				if(knockoff_Criteria.get(i).getStReversal_value()!=null)
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value();
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
						/*update_condition = update_condition +"SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") "+ knockoff_Criteria.get(i).getStReversal_condition()+
								knockoff_Criteria.get(i).getStReversal_value();*/
						select_parameters = select_parameters+ " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
					}
					else
					{
						condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
									+" "+knockoff_Criteria.get(i).getStReversal_value();
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";*/
						select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header();
					}
				}
				else
				{
					Update_Headers.add(knockoff_Criteria.get(i));
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" SUBSTR( "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
						select_parameters = select_parameters + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();*/
					}
					else
					{
						condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() +" "+alias+"."+knockoff_Criteria.get(i).getStReversal_header();
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
						select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header();*/
					}
				
					
				}
			}
			else
			{
				if(knockoff_Criteria.get(i).getStReversal_value()!=null)
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") = ?"+" AND ";*/
						/*select_parameters = select_parameters + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
						+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";*/
					}
					else
					{
						condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
									+" "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?"+" AND ";*/
						//select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
				}
				else
				{
					Update_Headers.add(knockoff_Criteria.get(i));
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" SUBSTR( "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AND ";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") = ? AND ";
						select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";*/
					}
					else
					{
						condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
								+" "+alias+"."+knockoff_Criteria.get(i).getStReversal_header()+" AND ";
				
					}
				
					
				}
				
			}
			
		}
		return condition;
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
	private class ColumnMapper implements RowMapper<String>
	{
		@Override
		public String mapRow(ResultSet rset, int row)throws SQLException
		{
			return rset.getString("COLUMN_NAME");
		}
	}
	
	private static class MatchCriteriaMaster implements RowMapper<KnockOffBean> {

		@Override
		public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			/*while(rs.next())
		{*/
			//logger.info("header is "+rs.getString("HEADER"));
			KnockOffBean knockOffBean = new KnockOffBean();

			knockOffBean.setStReversal_header(rs.getString("MATCH_HEADER"));
			knockOffBean.setStReversal_padding(rs.getString("PADDING"));
			knockOffBean.setStReversal_charpos(rs.getString("START_CHARPOS"));
			knockOffBean.setStReversal_charsize(rs.getString("CHAR_SIZE"));
			knockOffBean.setStReversal_value(null);
			knockOffBean.setStReversal_condition("=");
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
	
	
	public void updateMatchedRecordsForRupay(List<String> Table_list,CompareBean comparebeanObj,int inRec_set_Id) throws Exception
	{
		
		logger.info("***** CompareRupayDaoImpl.updateMatchedRecordsForRupay Start ****");
		//CompareBean compareBean = new CompareBean();
		//SELECT * FROM ONUS_SWITCH OS INNER JOIN ONUS_CBS OC  ON(OS.PAN = OC.REMARKS);
		String table1_condition = "";
		String table2_condition = "";
		ResultSet rset1=null;
		ResultSet rset2=null;
		Connection conn=null;
		PreparedStatement ps1 =null ;
		PreparedStatement ps2 = null;
		String table1_cols = "";
		String table2_cols = "";
		String[] table1_column;
		String[] table2_column;
		String SETTLEMENT_INSERT1 = "";
		String SETTLEMENT_INSERT2 = "";
		List<CompareBean> match_Headers1 = new ArrayList<>();
		List<CompareBean> match_Headers2 = new ArrayList<>();
		int table1_file_id , table2_file_id;
		List<CompareBean> relax_match_Headers1= new ArrayList<>();;
		List<CompareBean> relax_match_Headers2 =  new ArrayList<>();;
		try
		{
			String file1_name = "", file2_name = "";
			String condition = "";	
			//String table1_alias = "t1", table2_alias = "t2";
			file1_name = Table_list.get(0);
			//logger.info("TABLE 1 NAME IS "+table1_name);
			//String a[] = table1_name.split("-");
			//file1_name = a[1];
			
			
			//String table2_name = Table_list.get(1);
			//logger.info("TABLE 2 NAME IS "+table2_name);
			//a = table2_name.split("-");
			file2_name = Table_list.get(1);
			
			//List<String> stMatched_flags = getMatchedFlag(comparebeanObj, inRec_set_Id);
			
			/*String stMatched_flag1 = stMatched_flags.get(0);
			String stMatched_flag2 = stMatched_flags.get(1);*/
			
			logger.info("COMPARE STARTS HERE *************************************************");
			
			
/*			if(file1_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file1_name, comparebeanObj.getStCategory(),"DOMESTIC" },Integer.class);
			}
			else
			{*/
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file1_name, comparebeanObj.getStCategory(),comparebeanObj.getStTable1_SubCategory() },Integer.class);
			//}
			
			
			/*if(file2_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file2_name, comparebeanObj.getStCategory(),"DOMESTIC"},Integer.class);
			}
			else
			{*/
				table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file2_name, comparebeanObj.getStCategory(),comparebeanObj.getStTable2_SubCategory()},Integer.class);
			//}
			
			int relax_count = getRelax_paramCount(comparebeanObj, inRec_set_Id);
			logger.info("relax count is "+relax_count);
			int loop = 1;
			if(relax_count > 0)
			{
				loop++;
				
				/*//alter matched table
				String ALTER_TABLE = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED_"+inRec_set_Id+ " ADD RELAX_PARAM VARCHAR2 (100 BYTE)";
				getJdbcTemplate().execute(ALTER_TABLE);*/
				
			}
 			String stRelax_Param = "";
			while(loop != 0)
			{
				table1_condition = "";
				table2_condition = "";
				condition = "";

				if((loop == 1 && relax_count <= 0) || (loop == 2 && relax_count > 0))
				{
					stRelax_Param = "N";
					/*if(file1_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{					
						match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_DOMESTIC"),inRec_set_Id},new MatchParameterMaster1());
					}
					else
					{*/
						//match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory()),inRec_set_Id},new MatchParameterMaster1());
					match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id},new MatchParameterMaster1());
					//}
					
					/*if(file2_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_DOMESTIC"),inRec_set_Id},new MatchParameterMaster2());
					}
					else*/
					{
						//match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable2_SubCategory()),inRec_set_Id},new MatchParameterMaster2());
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id},new MatchParameterMaster2());
					}
					

				}
				else // changes for relax param concept
				{
					stRelax_Param = "Y";
					
						match_Headers1 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"N"},new MatchParameterMaster1());
					
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"N"},new MatchParameterMaster2());

						//if relax parameter is on then take difference for that header i.e amount
						relax_match_Headers1 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"Y"},new MatchParameterMaster1());
						
						relax_match_Headers2 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"Y"},new MatchParameterMaster2());


				}
						
				
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
										match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name 
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
									/*table1_condition = "REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':')";*/
									table1_condition = "LPAD(REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':'),6,'0')";
									
								}
								else
								{
									table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
									/*table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")";*/ //CHANGED FOR VISA ISSUER
									
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
								String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+comparebeanObj.getStMergeCategory()+
										"_"+file1_name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
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
								table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
							}
							else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
								match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM "
										+comparebeanObj.getStMergeCategory()+"_"+file2_name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
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
									/*table2_condition = "REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':')";*/
									
									table2_condition = "LPAD(REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':'),6,'0')";
									
								}
								else
								{
									table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
									//CHANGED DUE TO VISA ISSUER
									
									/*table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+")";*/
									
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
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+comparebeanObj.getStMergeCategory()+
										"_"+file2_name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
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
				String Table1_Selheaders = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);
				String stTable2_Selheaders = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
				
				if(stRelax_Param.equals("Y"))
				{
					String Relax_Param1 = "", Relax_Param2 = "";
					
					for(int i = 0 ;i<relax_match_Headers1.size();i++)
					{
						if(relax_match_Headers1.get(i).getStMatchTable1_header().contains("AMOUNT"))
						Relax_Param1 = relax_match_Headers1.get(i).getStMatchTable1_header();
						
						logger.info("relax param 1 "+Relax_Param1);
						
						if(relax_match_Headers2.get(i).getStMatchTable2_header().contains("AMOUNT"))
						Relax_Param2 = relax_match_Headers2.get(i).getStMatchTable2_header();
						logger.info("rELAX PARAM 2 "+Relax_Param2);

					}
					

					List<String> Columns1 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+file1_name}, new ColumnsMapper());
					List<String> Columns2 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+file2_name}, new ColumnsMapper());
					String COLS1 = "",COLS2="";
									
					for(int i = 0 ;i<Columns1.size();i++)
					{
						
						if(i == Columns1.size()-1)
						{							
							COLS1 = COLS1+"t1."+Columns1.get(i);
						}
						else
						{
							COLS1 = COLS1+"t1."+Columns1.get(i)+",";
						}
							
					}
					for(int i = 0 ;i<Columns2.size();i++)
					{
						if(i == Columns2.size()-1)
						{
							COLS2 = COLS2+"t2."+Columns2.get(i);
						}
						else
						{
							COLS2 = COLS2+"t2."+Columns2.get(i)+",";
						}
							
					}
						
					//diff amount should be CBS AMT - NTW AMT
					COLS1 = COLS1.replace("t1.DIFF_AMOUNT,","");	
					COLS1 = COLS1.replace(",t1.DIFF_AMOUNT","");
					COLS1 = COLS1.replace(",t1.RELAX_PARAM","");
					COLS1 = COLS1.replace("t1.RELAX_PARAM,","");
					COLS1 = COLS1.replace(",t1.FILEDATE", "");
					COLS1 = COLS1.replace("t1.FILEDATE,", "");
					Columns1.remove("DIFF_AMOUNT");
					Columns1.remove("RELAX_PARAM");
					Columns1.remove("FILEDATE");
					Table1_Selheaders = "RELAX_PARAM,FILEDATE";
					stTable2_Selheaders = "RELAX_PARAM,FILEDATE";
					if(file1_name.equals("SWITCH") && inRec_set_Id == 2)//CHANGES MADE BY INT5779 FOR VISA ISSUER AS ON 19TH MARCH
					{
						Table1_Selheaders = Table1_Selheaders+",DIFF_AMOUNT";
					}
					else if(file2_name.equals("SWITCH") && inRec_set_Id == 2)
						stTable2_Selheaders = stTable2_Selheaders+",DIFF_AMOUNT";
					
					for(String header : Columns1)
					{
						Table1_Selheaders = Table1_Selheaders+","+header;
					}
					
					COLS2 = COLS2.replace(",t2.DIFF_AMOUNT", "");
					COLS2 = COLS2.replace("t2.DIFF_AMOUNT,", "");
					COLS2 = COLS2.replace(",t2.RELAX_PARAM", "");
					COLS2 = COLS2.replace("t2.RELAX_PARAM,", "");
					Columns2.remove("DIFF_AMOUNT");
					Columns2.remove("RELAX_PARAM");
					Columns2.remove("FILEDATE");					
					COLS2 = COLS2.replace(",t2.FILEDATE", "");
					COLS2 = COLS2.replace("t2.FILEDATE,", "");
					
					for(String header : Columns2)
					{
						stTable2_Selheaders = stTable2_Selheaders+","+header;
					}
					
					
					if(file1_name.equals("SWITCH") && inRec_set_Id == 2)
					{
						
						JOIN1_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),NVL(TO_NUMBER(t2."+Relax_Param2+",'999999999.999')-TO_NUMBER(t1."+Relax_Param1
								+",'999999999.999'),'0') DIFF_AMOUNT, "+COLS1+" FROM TEMP_"
								+comparebeanObj.getStMergeCategory()+"_"+file1_name 
								+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}
					else
					{
						
						JOIN1_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+COLS1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name 
							+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
							+"_"+file2_name + " t2 ON( "+condition 
							+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "//AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
					/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					}
					/*else if(file2_name.equals("SWITCH"))
					{
						
						
						JOIN1_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t2.CBS_AMOUNT,'999999999.999') - TO_NUMBER(t1."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS2+" FROM TEMP_"
								+comparebeanObj.getStMergeCategory()+"_"+file1_name 
								+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					/*else
					{
						
						JOIN1_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1."+Relax_Param1+",'999999999.999') - TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name 
							+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
							+"_"+file2_name + " t2 ON( "+condition 
							+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "//AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					
					
					/*if(file1_name.equals("SWITCH"))
					{						
						JOIN2_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1.CBS_AMOUNT"+",'999999999.999')- TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS1+" FROM TEMP_"
								+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					
					if(file2_name.equals("SWITCH") && inRec_set_Id == 2)
					{

						JOIN2_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),NVL(TO_NUMBER(t1."+Relax_Param1+",'999999999.999')-TO_NUMBER(t2."+Relax_Param2+",'999999999.999'),'0') DIFF_AMOUNT, "+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					
					}
					else
					{

						JOIN2_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'), "+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					
					}
					/*else
					{

						JOIN2_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1."+Relax_Param1+",'999999999.999')- TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					
					}*/
					
					//GET KNOCKOFF CRITERIA FOR NOT EXISTS QUERY PART
					if(getStatus(comparebeanObj, file1_name, "KNOCKOFF").equals("Y"))
					{
						int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table1_file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
						List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());


						String part2_condition = getRecordsFromRelaxRecords(knockoff_Criteria);
						
						JOIN1_QUERY = JOIN1_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id
										+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
						
						JOIN2_QUERY = JOIN2_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id
								+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
				
					}
					else if(getStatus(comparebeanObj, file2_name, "KNOCKOFF").equals("Y"))
					{
						int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table2_file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
						List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , table2_file_id}, new KnockOffCriteriaMaster());


						String part2_condition = getRecordsFromRelaxRecords(knockoff_Criteria);
						
						JOIN1_QUERY = JOIN1_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id
								+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
				
						JOIN2_QUERY = JOIN2_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id
						+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
						
					}
					
				
					
				}
				else
				{
					
						String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);
						String cols1 = "t1."+stTable1_headers.replace(",", ",t1." );
						logger.info(cols1);
						String getcols1 = "";
						if(file1_name.equals("CBS"))
						{
							String temp_headers ="RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
							//getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							//getcols1 = "t1.CREATEDDATE,t2.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							getcols1 = "t1.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							Table1_Selheaders = temp_headers + Table1_Selheaders;
						}
						else if(file1_name.equals("SWITCH"))
						{
							String temp_headers = "";
							if((file2_name.equals("CBS") && (comparebeanObj.getStSubCategory().equals("DOMESTIC")|| comparebeanObj.getStSubCategory().equals("INTERNATIONAL")))
									||(file2_name.equals("CBS") && (comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("ACQUIRER"))))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							else //if(file2_name.equals("CBS") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							/*else
							getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.DIFF_AMOUNT,"+cols1;*/

						}
						else
						{
							if(getStatus(comparebeanObj,file1_name,"FILTERATION").equals("Y"))
							{
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,SEG_TRAN_ID,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;
							}
							else
							{
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;


							}

						}


						String getcols2 = "";
						String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
						String cols2 = "t2."+stTable2_headers.replace(",", ",t2." );
						logger.info(cols2);
						if(file2_name.equals("CBS"))
						{
							String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							stTable2_Selheaders = temp_headers+stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH") && file1_name.equals("CBS")
								&& (comparebeanObj.getStSubCategory().equals("DOMESTIC")||comparebeanObj.getStSubCategory().equals("INTERNATIONAL")
										||comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("ACQUIRER")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_header = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_header +stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH")
								&& (file1_name.equals("RUPAY")||comparebeanObj.getStSubCategory().equals("SURCHARGE")|| file1_name.equals("VISA")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID ,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_headers + stTable2_Selheaders;
						}
						else
						{	
							if(getStatus(comparebeanObj,file2_name,"FILTERATION").equals("Y"))
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;
							}
							else
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;


							}
						}

						//********* GETTING RECORDS IN RSET

						//	JOIN1_QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						//			JOIN2_QUERY = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN TEMP_"+table1_name + " t1 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						JOIN1_QUERY = "SELECT DISTINCT 'N',"+getcols1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name + " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')"; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						JOIN2_QUERY = "SELECT DISTINCT 'N',"+getcols2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name + " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" // AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" ; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						//GET WHERE CONDITION FROM MAIN_matching_CONDITION TABLE
					
				}
				
				String compare_cond1 = getCompareCondition(comparebeanObj,file1_name,inRec_set_Id);
				String compare_cond2 = getCompareCondition(comparebeanObj,file2_name,inRec_set_Id);
				//String compare_cond = compare_cond1 +" AND " + compare_cond2;
				if(!compare_cond1.equals(""))
				{
					JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond1;
					JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond1;
				}
				if(!compare_cond2.equals(""))
				{

					JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond2;
					JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond2;
				
					
				}

				logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

				//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
				logger.info("COMPARE QUERY IS****************************************");
				logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
				logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
				logger.info("JOIN1 QUERY IS "+JOIN1_QUERY); 
				
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

				// GET THE DIFFERENCE IN TWO FIELDS
				/*String Relax_Param1 = "", Relax_Param2 = "";
				String DIFFERENCE_QUERY = "";
				PreparedStatement amt_stmt = null;
				ResultSet amt_set = null;*/
				/*if(stRelax_Param.equals("Y"))
				{
					for(int i = 0 ;i<relax_match_Headers1.size();i++)
					{
						Relax_Param1 = relax_match_Headers1.get(i).getStMatchTable1_header();
						logger.info("relax param 1 "+Relax_Param1);
						Relax_Param2 = relax_match_Headers2.get(i).getStMatchTable2_header();
						logger.info("rELAX PARAM 2 "+Relax_Param2);

					}
					
					
					DIFFERENCE_QUERY = "SELECT NVL((t1."+Relax_Param1+"- t2."+Relax_Param2+"),'0') CBS_AMOUNT "+" FROM TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+file1_name + " t1 INNER JOIN TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable2_SubCategory().substring(0, 3)+"_"+file2_name + " t2 ON( "+condition 
							+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
					String DIFFERENCE_QUERY = "SELECT (t1."+Relax_Param1+"-t2."+Relax_Param2+") CBS_AMOUNT FROM "+"TEMP_"
							+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory().substring(0, 3)+"_"+file1_name+" t1 , TEMP_"+comparebeanObj.getStCategory()
							+"_"+comparebeanObj.getStSubCategory().substring(0, 3)+"_"+file2_name+" t2 ";

					logger.info("DIFFERENCE QUERY IS " +DIFFERENCE_QUERY);
					amt_stmt = conn.prepareStatement(DIFFERENCE_QUERY);
					amt_set = amt_stmt.executeQuery();
				}*/
				// ALTERING SETTLEMENT TABLE..... ADDING RELAX PARAMTERE FIELD
				int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name.toUpperCase(),"RELAX_PARAM"}, Integer.class);
				
				if(Column_count == 0)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+" ADD RELAX_PARAM VARCHAR(100 BYTE)";
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name.toUpperCase(),"RELAX_PARAM"}, Integer.class);
				
				if(Column_count == 0)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" ADD RELAX_PARAM VARCHAR(100 BYTE)";
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				
				
				//---------------- MOVING THE OBTAINED RESULT INTO NEW TABLE-----------------------
				//********************************** CHECK IF MATCHED FLAG IS Y OR NOT
				/*if(stMatched_flag1.equals("Y"))
				{

					//1. CREATE INSERT QUERY
					INSERT1_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" (CREATEDDATE,CREATEDBY,FILEDATE,RELAX_PARAM,";
					SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,RELAX_PARAM,";

					//get columns
					String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);	
					//String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
					if(file1_name.equals("CBS"))
					{
						table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable1_headers;
					}
					else if(file1_name.equals("SWITCH"))
					{
						table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,"+stTable1_headers;
					}
					else
					{
						table1_cols = "SEG_TRAN_ID,"+stTable1_headers;
					}



					INSERT1_QUERY = INSERT1_QUERY + table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+stRelax_Param+"',?";
					SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 +table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()
							+"','DD/MM/YYYY')"+",'"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id+"','"+stRelax_Param+"',?";

					table1_column = table1_cols.split(",");

					for(int i=0;i<(table1_column.length-1);i++)
					{
						INSERT1_QUERY = INSERT1_QUERY + ",?";
						SETTLEMENT_INSERT1 =SETTLEMENT_INSERT1 + ",?";
					}
					INSERT1_QUERY = INSERT1_QUERY +" )";
					SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 + " )";


					logger.info("INSERT1 QUERY IS "+INSERT1_QUERY);
					logger.info("SETTLEMENT INSERT1 QUERY IS "+SETTLEMENT_INSERT1);

				}
				else
				{*/
					//1. CREATE INSERT QUERY
					//INSERT1_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" (CREATEDDATE,CREATEDBY,FILEDATE,RELAX_PARAM,";
				INSERT1_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" ("+Table1_Selheaders+")";
				INSERT2_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" ("+stTable2_Selheaders+")";
				
				
				
				String INSERT_DATA = INSERT1_QUERY + JOIN1_QUERY;
				
							
				logger.info("INSERT DATA QUERY IS "+INSERT_DATA);
				
				logger.info("start time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(INSERT_DATA);
				logger.info("end time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				
				
				INSERT_DATA = INSERT2_QUERY + JOIN2_QUERY;
				logger.info("INSERT DATA QUERY IS "+INSERT_DATA);
				
				logger.info("start time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(INSERT_DATA);
				logger.info("END time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				//GET DATA FROM MATCHED TABLE AND THEN INSERT IT INTO SETTLEMENT TABLE
				
				List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id},new ColumnMapper());
				String select_cols = "";
				int count = 0;
				for(String cols : TABLE_COLS)
				{
					if(count == 0)
						{
							select_cols = cols;
							count++;
						}
					else
						select_cols = select_cols+","+cols;
				}

				String GET_MATCHED = "SELECT '"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id
								+"',"+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" WHERE RELAX_PARAM = '"+stRelax_Param+"'";
				
				SETTLEMENT_INSERT1 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+" (DCRS_REMARKS,"+select_cols+") "
									+GET_MATCHED;
				
				logger.info("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT1);
				logger.info("start time FOR INSERTING MATCHED DATA IN SETTLEMENT1  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(SETTLEMENT_INSERT1);
				logger.info("END time FOR INSERTING MATCHED DATA IN SETTLEMENT1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id},new ColumnMapper());
				select_cols = "";
				count = 0;
				for(String cols : TABLE_COLS)
				{
					if(count == 0)
						{
							select_cols = cols;
							count++;
						}
					else
						select_cols = select_cols+","+cols;
				}
				
				GET_MATCHED = "SELECT '"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id
						+"',"+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" WHERE RELAX_PARAM = '"+stRelax_Param+"'";
				
				SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" (DCRS_REMARKS,"+select_cols+") "
							+GET_MATCHED;
				
				logger.info("start time FOR INSERTING MATCHED DATA IN SETTLEMENT2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(SETTLEMENT_INSERT2);
				logger.info("END time FOR INSERTING MATCHED DATA IN SETTLEMENT2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
//---------------------------------------------------------------------------REMOVING DATA FROM TEMP for CYCLE 1 ONLY AS IT WAS CAUSING ISSUE IN DUPLICATES-------------------------------------------------------------------------------------------------------------
				if(inRec_set_Id == 1)
				{
					String DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";
					
					String checkCondition = "select count(*) from  TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					int reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table1_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					String knock_condition = getCondition(knockoff_Criteria);
					
					checkCondition = checkCondition  + knock_condition+"))";
					int dataCount1 = getJdbcTemplate().queryForObject(checkCondition, Integer.class); 
					logger.info("dataCount1=="+dataCount1);

					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";
					
					if(dataCount1>0){
					getJdbcTemplate().execute(DELETE_FROM_TEMP);
					dataCount1 = 0;
					}
					DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";
					
					String checkCondition1 = "select count(*) from  TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table2_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table2_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					knock_condition = getCondition(knockoff_Criteria);

					checkCondition1 = checkCondition1  + knock_condition+"))";
					int dataCount2 = getJdbcTemplate().queryForObject(checkCondition1, Integer.class); 
					logger.info("dataCount2=="+dataCount2);
					
					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";

					logger.info("DELETE_FROM_TEMP=="+DELETE_FROM_TEMP);
					if(dataCount2>0){
					getJdbcTemplate().execute(DELETE_FROM_TEMP);
					dataCount2=0;
					}

				}
				
				
				
				
				
					//SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,RELAX_PARAM,";
				//SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,RELAX_PARAM,";
					
					//INSERT1_QUERY = ""
					

					//get columns
				/*	String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);	
					//String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
					if(file1_name.equals("CBS"))
					{
						table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable1_headers;
					}
					else if(file1_name.equals("SWITCH"))
					{
						table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,"+stTable1_headers;
					}
					else
					{
						table1_cols = "SEG_TRAN_ID,"+stTable1_headers;
					}



					INSERT1_QUERY = INSERT1_QUERY + table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+stRelax_Param+"',?";
					SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 +table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()
							+"','DD/MM/YYYY')"+",'"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id+"','"+stRelax_Param+"',?";

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
				/*}

				//-----------------------------------INSERT IN TABLE 2	
				if(stMatched_flag2.equals("Y"))
				{

					INSERT2_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" (CREATEDDATE,CREATEDBY,FILEDATE,RELAX_PARAM,";
					SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,RELAX_PARAM,";

					String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);

					if(file2_name.equals("CBS"))
					{
						table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable2_headers;
					}
					else if(file2_name.equals("SWITCH"))
					{
						table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,"+stTable2_headers;
					}
					else
					{	
						table2_cols = "SEG_TRAN_ID,"+stTable2_headers;
					}
					INSERT2_QUERY = INSERT2_QUERY + table2_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+stRelax_Param+"',?";
					SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + table2_cols+ ") VALUES(SYSDATE,'INT5779',TO_DATE('"
							+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id+"','"+stRelax_Param+"',?";
					table2_column = table2_cols.split(",");
					for(int i=0;i<(table2_column.length-1);i++)
					{
						INSERT2_QUERY = INSERT2_QUERY + ",?";
						SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + ",?";
					}
					INSERT2_QUERY = INSERT2_QUERY + ")";
					SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 +" )";

				}
				else
				{*/
					//INSERT2_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" (CREATEDDATE,CREATEDBY,FILEDATE,RELAX_PARAM,";
					
					
					/*SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,RELAX_PARAM,";

					String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);

					if(file2_name.equals("CBS"))
					{
						table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable2_headers;
					}
					else if(file2_name.equals("SWITCH"))
					{
						table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,"+stTable2_headers;
					}
					else
					{	
						table2_cols = "SEG_TRAN_ID,"+stTable2_headers;
					}
					INSERT2_QUERY = INSERT2_QUERY + table2_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+stRelax_Param+"',?";
					SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + table2_cols+ ") VALUES(SYSDATE,'INT5779',TO_DATE('"
							+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')"+",'"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id+"','"+stRelax_Param+"',?";
					table2_column = table2_cols.split(",");
					for(int i=0;i<(table2_column.length-1);i++)
					{
						INSERT2_QUERY = INSERT2_QUERY + ",?";
						SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + ",?";
					}
					INSERT2_QUERY = INSERT2_QUERY + ")";
					SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 +" )";*/
				//}
				/*logger.info("INSERT2 QUERY IS "+INSERT2_QUERY);
				logger.info("SETTLEMENT INSERT2 QUERY "+SETTLEMENT_INSERT2);
				logger.info("");
				
				if(stRelax_Param.equals("N"))
				{
					logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
					//insertBatch(INSERT1_QUERY, rset1, table1_column);
					logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+file1_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("");

					logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
					//insertBatch(INSERT2_QUERY, rset2, table2_column);
					logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+file2_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("");

					//inserting in settlement table
					logger.info("start time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
					//insertBatch(SETTLEMENT_INSERT1, settlement_set1, table1_column);
					logger.info("end time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("");

					logger.info("start time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
					//insertBatch(SETTLEMENT_INSERT2, settlement_set2, table2_column);
					logger.info("end time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("");*/
			/*	}
				else
					if(stRelax_Param.equals("Y"))
					{
						logger.info("RELAX PARAMTER IS "+stRelax_Param);
						logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
						insertRelaxBatch(INSERT1_QUERY, rset1, amt_set, table1_column);
						logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+file1_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
						logger.info("");

						logger.info("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
						insertRelaxBatch(INSERT2_QUERY, rset2, amt_set ,table2_column);
						logger.info("eND TIME FOR COMPLETING INSERTION IN RECON_"+file2_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
						logger.info("");

						//inserting in settlement table
						logger.info("start time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
						insertRelaxBatch(SETTLEMENT_INSERT1, settlement_set1, amt_set , table1_column);
						logger.info("end time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
						logger.info("");

						logger.info("start time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
						insertRelaxBatch(SETTLEMENT_INSERT2, settlement_set2, amt_set, table2_column);
						logger.info("end time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
						logger.info("");
					}*/
				loop--;

			}
			logger.info("***** CompareRupayDaoImpl.updateMatchedRecordsForRupay End ****");
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.updateMatchedRecordsForRupay");
			 logger.error(" error in  CompareRupayDaoImpl.updateMatchedRecordsForRupay", new Exception(" CompareRupayDaoImpl.updateMatchedRecordsForRupay",e));
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
				demo.logSQLException(e, "CompareRupayDaoImpl.updateMatchedRecordsForRupay");
				 logger.error(" error in  CompareRupayDaoImpl.updateMatchedRecordsForRupay", new Exception(" CompareRupayDaoImpl.updateMatchedRecordsForRupay",e));
				 throw e;
			}
		}
		
		
	}
	
	//ADDED BY INT5779 ON 27TH MARCH AS VISA MATCHING NEEDS TO BE CHANGED A LOT AND RUPAY SHOULD NOT GET AFFECTED
	public void updateMatchedRecordsForVisa(List<String> Table_list,CompareBean comparebeanObj,int inRec_set_Id)throws Exception
	{
		
		logger.info("***** CompareRupayDaoImpl.updateMatchedRecordsForVisa Start ****");
		//CompareBean compareBean = new CompareBean();
		//SELECT * FROM ONUS_SWITCH OS INNER JOIN ONUS_CBS OC  ON(OS.PAN = OC.REMARKS);
		String table1_condition = "";
		String table2_condition = "";
		ResultSet rset1=null;
		ResultSet rset2=null;
		Connection conn=null;
		PreparedStatement ps1 =null ;
		PreparedStatement ps2 = null;
		String table1_cols = "";
		String table2_cols = "";
		String[] table1_column;
		String[] table2_column;
		String SETTLEMENT_INSERT1 = "";
		String SETTLEMENT_INSERT2 = "";
		List<CompareBean> match_Headers1 = new ArrayList<>();
		List<CompareBean> match_Headers2 = new ArrayList<>();
		int table1_file_id , table2_file_id;
		List<CompareBean> relax_match_Headers1= new ArrayList<>();;
		List<CompareBean> relax_match_Headers2 =  new ArrayList<>();;
		try
		{
			String file1_name = "", file2_name = "";
			String condition = "";	
			//String table1_alias = "t1", table2_alias = "t2";
			file1_name = Table_list.get(0);
			//logger.info("TABLE 1 NAME IS "+table1_name);
			//String a[] = table1_name.split("-");
			//file1_name = a[1];
			
			
			//String table2_name = Table_list.get(1);
			//logger.info("TABLE 2 NAME IS "+table2_name);
			//a = table2_name.split("-");
			file2_name = Table_list.get(1);
			
			//List<String> stMatched_flags = getMatchedFlag(comparebeanObj, inRec_set_Id);
			
			/*String stMatched_flag1 = stMatched_flags.get(0);
			String stMatched_flag2 = stMatched_flags.get(1);*/
			
			logger.info("COMPARE STARTS HERE *************************************************");
			
			
/*			if(file1_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file1_name, comparebeanObj.getStCategory(),"DOMESTIC" },Integer.class);
			}
			else
			{*/
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file1_name, comparebeanObj.getStCategory(),comparebeanObj.getStTable1_SubCategory() },Integer.class);
			//}
			
			
			/*if(file2_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file2_name, comparebeanObj.getStCategory(),"DOMESTIC"},Integer.class);
			}
			else
			{*/
				table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file2_name, comparebeanObj.getStCategory(),comparebeanObj.getStTable2_SubCategory()},Integer.class);
			//}
			
			int relax_count = getRelax_paramCount(comparebeanObj, inRec_set_Id);
			logger.info("relax count is "+relax_count);
			int loop = 1;
			if(relax_count > 0)
			{
				loop++;
				
				/*//alter matched table
				String ALTER_TABLE = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED_"+inRec_set_Id+ " ADD RELAX_PARAM VARCHAR2 (100 BYTE)";
				getJdbcTemplate().execute(ALTER_TABLE);*/
				
			}
 			String stRelax_Param = "";
			while(loop != 0)
			{
				table1_condition = "";
				table2_condition = "";
				condition = "";

				if((loop == 1 && relax_count <= 0) || (loop == 2 && relax_count > 0))
				{
					stRelax_Param = "N";
					/*if(file1_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{					
						match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_DOMESTIC"),inRec_set_Id},new MatchParameterMaster1());
					}
					else
					{*/
						//match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory()),inRec_set_Id},new MatchParameterMaster1());
					match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id},new MatchParameterMaster1());
					//}
					
					/*if(file2_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_DOMESTIC"),inRec_set_Id},new MatchParameterMaster2());
					}
					else*/
					{
						//match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable2_SubCategory()),inRec_set_Id},new MatchParameterMaster2());
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id},new MatchParameterMaster2());
					}
					

				}
				else // changes for relax param concept
				{
					stRelax_Param = "Y";
					
						match_Headers1 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"N"},new MatchParameterMaster1());
					
						match_Headers2 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"N"},new MatchParameterMaster2());

						//if relax parameter is on then take difference for that header i.e amount
						relax_match_Headers1 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table1_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"Y"},new MatchParameterMaster1());
						
						relax_match_Headers2 = getJdbcTemplate().query(GET_MATCH_RELAX_PARAMS , new Object[]{table2_file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),inRec_set_Id,"Y"},new MatchParameterMaster2());


				}
						
				
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
										match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name 
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
									/*table1_condition = "REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':')";*/
									table1_condition = "LPAD(REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':'),6,'0')";
									
								}
								else
								{
									table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";
									/*table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")";*/ //CHANGED FOR VISA ISSUER
									
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
								String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+comparebeanObj.getStMergeCategory()+
										"_"+file1_name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
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
					
					logger.info("table1_condition=="+table1_condition);
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
								table2_condition = " TO_NUMBER(replace(replace(SUBSTR(REPLACE(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",',','')"+","
												+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+ match_Headers2.get(i).getStMatchTable2_charSize()+"),'/',''),'-','') )";
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
										+comparebeanObj.getStMergeCategory()+"_"+file2_name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
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
									/*table2_condition = "REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':')";*/
									
									table2_condition = "LPAD(REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':'),6,'0')";
									
								}
								else
								{
									table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";
									//CHANGED DUE TO VISA ISSUER
									
									/*table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+")";*/
									
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
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+comparebeanObj.getStMergeCategory()+
										"_"+file2_name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
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
					
					logger.info("table2_condition=="+table2_condition);
					
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
				String Table1_Selheaders = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);
				String stTable2_Selheaders = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
				logger.info("Table1_Selheaders=="+Table1_Selheaders);
				logger.info("stTable2_Selheaders=="+stTable2_Selheaders);
				if(stRelax_Param.equals("Y"))
				{
					if(inRec_set_Id == 2)
					{
						//ALTERING MATCHED AND SETTLEMENT TABLES FOR CYCLE 2
						int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DIFF_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DCRS_SEQ_NO"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DCRS_SEQ_NO VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"ARN"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD ARN VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SOURCE_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SOURCE_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SOURCE_CURR_CODE"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SOURCE_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DESTINATION_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DESTINATION_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"DESTINATION_CURR_CODE"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD DESTINATION_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id,"SETTLEMENT_FLAG"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_Id+" ADD SETTLEMENT_FLAG VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						
						//ALTERING SETTLEMENT TABLE
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","DIFF_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","DCRS_SEQ_NO"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD DCRS_SEQ_NO VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","ARN"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD ARN VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","SOURCE_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD SOURCE_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","SOURCE_CURR_CODE"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD SOURCE_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","DESTINATION_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD DESTINATION_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","DESTINATION_CURR_CODE"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD DESTINATION_CURR_CODE VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH","SETTLEMENT_FLAG"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD SETTLEMENT_FLAG VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
					}
					String Relax_Param1 = "", Relax_Param2 = "";
					
					for(int i = 0 ;i<relax_match_Headers1.size();i++)
					{
						if(relax_match_Headers1.get(i).getStMatchTable1_header().contains("AMOUNT"))
						Relax_Param1 = relax_match_Headers1.get(i).getStMatchTable1_header();
						
						logger.info("relax param 1 "+Relax_Param1);
						
						if(relax_match_Headers2.get(i).getStMatchTable2_header().contains("AMOUNT"))
						Relax_Param2 = relax_match_Headers2.get(i).getStMatchTable2_header();
						logger.info("rELAX PARAM 2 "+Relax_Param2);

					}
					

					List<String> Columns1 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+file1_name}, new ColumnsMapper());
					List<String> Columns2 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+file2_name}, new ColumnsMapper());
					String COLS1 = "",COLS2="";
									
					for(int i = 0 ;i<Columns1.size();i++)
					{
						
						if(i == Columns1.size()-1)
						{							
							COLS1 = COLS1+"t1."+Columns1.get(i);
						}
						else
						{
							COLS1 = COLS1+"t1."+Columns1.get(i)+",";
						}
							
					}
					for(int i = 0 ;i<Columns2.size();i++)
					{
						if(i == Columns2.size()-1)
						{
							COLS2 = COLS2+"t2."+Columns2.get(i);
						}
						else
						{
							COLS2 = COLS2+"t2."+Columns2.get(i)+",";
						}
							
					}
						
					//diff amount should be CBS AMT - NTW AMT
					COLS1 = COLS1.replace("t1.DIFF_AMOUNT,","");					
					COLS1 = COLS1.replace(",t1.RELAX_PARAM","");
					COLS1 = COLS1.replace("t1.RELAX_PARAM,","");
					COLS1 = COLS1.replace(",t1.FILEDATE", "");
					COLS1 = COLS1.replace("t1.FILEDATE,", "");
					Columns1.remove("DIFF_AMOUNT");
					Columns1.remove("RELAX_PARAM");
					Columns1.remove("FILEDATE");
					Table1_Selheaders = "RELAX_PARAM,FILEDATE";
					stTable2_Selheaders = "RELAX_PARAM,FILEDATE";
					if(file1_name.equals("SWITCH") && inRec_set_Id == 2)//CHANGES MADE BY INT5779 FOR VISA ISSUER AS ON 19TH MARCH
					{
						/*Table1_Selheaders = Table1_Selheaders+",DIFF_AMOUNT";*/
						Table1_Selheaders = Table1_Selheaders+",DIFF_AMOUNT,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG";
					}
					else if(file2_name.equals("SWITCH") && inRec_set_Id == 2)
						//stTable2_Selheaders = stTable2_Selheaders+",DIFF_AMOUNT";
						stTable2_Selheaders = stTable2_Selheaders+",DIFF_AMOUNT,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG";
					
					for(String header : Columns1)
					{
						Table1_Selheaders = Table1_Selheaders+","+header;
					}
					
					COLS2 = COLS2.replace(",t2.DIFF_AMOUNT", "");
					COLS2 = COLS2.replace("t2.DIFF_AMOUNT,", "");
					COLS2 = COLS2.replace(",t2.RELAX_PARAM", "");
					COLS2 = COLS2.replace("t2.RELAX_PARAM,", "");
					Columns2.remove("DIFF_AMOUNT");
					Columns2.remove("RELAX_PARAM");
					Columns2.remove("FILEDATE");					
					COLS2 = COLS2.replace(",t2.FILEDATE", "");
					COLS2 = COLS2.replace("t2.FILEDATE,", "");
					
					for(String header : Columns2)
					{
						stTable2_Selheaders = stTable2_Selheaders+","+header;
					}
					
					
					if(file1_name.equals("SWITCH") && inRec_set_Id == 2)
					{
						
						JOIN1_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),NVL(TO_NUMBER(t2."+Relax_Param2+",'999999999.999')-TO_NUMBER(t1."+Relax_Param1
								+",'999999999.999'),'0') DIFF_AMOUNT,T2.DCRS_SEQ_NO,T2.ARN,T2.SOURCE_AMOUNT,T2.SOURCE_CURR_CODE,T2.DESTINATION_AMOUNT,T2.DESTINATION_CURR_CODE,T2.SETTLEMENT_FLAG, "
								+COLS1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name 
								+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}
					else
					{
						
						JOIN1_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+COLS1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name 
							+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
							+"_"+file2_name + " t2 ON( "+condition 
							+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "//AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
					/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					}
					
					
					/*else if(file2_name.equals("SWITCH"))
					{
						
						
						JOIN1_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t2.CBS_AMOUNT,'999999999.999') - TO_NUMBER(t1."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS2+" FROM TEMP_"
								+comparebeanObj.getStMergeCategory()+"_"+file1_name 
								+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					/*else
					{
						
						JOIN1_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1."+Relax_Param1+",'999999999.999') - TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name 
							+ " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
							+"_"+file2_name + " t2 ON( "+condition 
							+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') "//AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					
					
					/*if(file1_name.equals("SWITCH"))
					{						
						JOIN2_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1.CBS_AMOUNT"+",'999999999.999')- TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS1+" FROM TEMP_"
								+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}*/
					
					if(file2_name.equals("SWITCH") && inRec_set_Id == 2)
					{

						JOIN2_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),NVL(TO_NUMBER(t1."+Relax_Param1+",'999999999.999')-TO_NUMBER(t2."
								+Relax_Param2+",'999999999.999'),'0') DIFF_AMOUNT,T1.DCRS_SEQ_NO,T1.ARN,T1.SOURCE_AMOUNT,T1.SOURCE_CURR_CODE,T1.DESTINATION_AMOUNT,T1.DESTINATION_CURR_CODE," +
										"T1.SETTLEMENT_FLAG, "
								+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					
					}
					else
					{

						JOIN2_QUERY = "SELECT DISTINCT 'Y',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'), "+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
					
					}
					/*else
					{

						JOIN2_QUERY = "SELECT 'Y',NVL((TO_NUMBER(t1."+Relax_Param1+",'999999999.999')- TO_NUMBER(t2."+Relax_Param2+",'999999999.999')),'0') DIFF_AMOUNT, "+COLS2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name
								+ " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";// AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					
					}*/
					logger.info("JOIN2_QUERY=="+JOIN2_QUERY);
					
					//GET KNOCKOFF CRITERIA FOR NOT EXISTS QUERY PART
					if(getStatus(comparebeanObj, file1_name, "KNOCKOFF").equals("Y"))
					{
						int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table1_file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
						List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());

						logger.info("reversal_id=="+reversal_id);
						logger.info("knockoff_Criteria=="+knockoff_Criteria);
						
						
						String part2_condition = getRecordsFromRelaxRecords(knockoff_Criteria);
						
						
						JOIN1_QUERY = JOIN1_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id
										+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
						
						JOIN2_QUERY = JOIN2_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id
								+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
				
					}
					else if(getStatus(comparebeanObj, file2_name, "KNOCKOFF").equals("Y"))
					{
						int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table2_file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
						List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , table2_file_id}, new KnockOffCriteriaMaster());
						logger.info("reversal_id=="+reversal_id);
						logger.info("knockoff_Criteria=="+knockoff_Criteria);
						

						String part2_condition = getRecordsFromRelaxRecords(knockoff_Criteria);
						
						JOIN1_QUERY = JOIN1_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id
								+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
				
						JOIN2_QUERY = JOIN2_QUERY + " AND NOT EXISTS (SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id
						+" T3 WHERE (RELAX_PARAM = 'N' AND  "+part2_condition+")) ";
						
					}
					
					logger.info("JOIN1_QUERY=="+JOIN1_QUERY);
					logger.info("JOIN2_QUERY=="+JOIN2_QUERY);
					
				}
				else
				{
					//CHANGES MADE BY INT5779 ON 27TH MARCH 2018 AS NEW COLUMN IS TO BE ADDED IN CBS SURCHARGE MATCHED TABLE
					if(inRec_set_Id == 3)
					{
						//1.ALTER MATCHED TABLE AND SETTLEMENT TABLE IF COLUMN IS NOT PRESENT
						int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{comparebeanObj.getStMergeCategory()+"_CBS_MATCHED"+inRec_set_Id,"DIFF_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_CBS_MATCHED"+inRec_set_Id+" ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_CBS","DIFF_AMOUNT"}, Integer.class);
						if(Column_count == 0)
						{
							String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_CBS ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							logger.info("ALTER_QUERY=="+ALTER_QUERY);
							getJdbcTemplate().execute(ALTER_QUERY);
						}
						//ENDS HERE
						
						String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);
						String cols1 = "t1."+stTable1_headers.replace(",", ",t1." );
						logger.info(cols1);
						String getcols1 = "";
						if(file1_name.equals("CBS"))
						{
							String temp_headers ="RELAX_PARAM,DIFF_AMOUNT,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG" +
									",CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
						
							//getcols1 = "t1.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							getcols1 = "t2.DIFF_AMOUNT,t2.DCRS_SEQ_NO,t2.ARN,t2.SOURCE_AMOUNT,t2.SOURCE_CURR_CODE,t2.DESTINATION_AMOUNT,t2.DESTINATION_CURR_CODE,t2.SETTLEMENT_FLAG," +
									"t1.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							Table1_Selheaders = temp_headers + Table1_Selheaders;
						}
						else if(file1_name.equals("SWITCH"))
						{
							String temp_headers = "";
							if(file2_name.equals("CBS") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.DCRS_SEQ_NO,t1.ARN,t1.SOURCE_AMOUNT,t1.SOURCE_CURR_CODE,t1.DESTINATION_AMOUNT,t1.DESTINATION_CURR_CODE,t1.SETTLEMENT_FLAG," +
										"t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT" +
												",t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG," +
										"CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							else //if(file2_name.equals("CBS") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							/*else
							getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.DIFF_AMOUNT,"+cols1;*/

						}
						else
						{
							if(getStatus(comparebeanObj,file1_name,"FILTERATION").equals("Y"))
							{
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,SEG_TRAN_ID,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;
							}
							else
							{
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;


							}

						}


						String getcols2 = "";
						String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
						String cols2 = "t2."+stTable2_headers.replace(",", ",t2." );
						logger.info(cols2);
						if(file2_name.equals("CBS"))
						{
							String temp_headers = "RELAX_PARAM,DIFF_AMOUNT,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							getcols2 = "t1.DIFF_AMOUNT,t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							stTable2_Selheaders = temp_headers+stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH") && file1_name.equals("CBS")
								&& (comparebeanObj.getStSubCategory().equals("DOMESTIC")||comparebeanObj.getStSubCategory().equals("INTERNATIONAL")
										||comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("ACQUIRER")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.DIFF_AMOUNT,t2.DCRS_SEQ_NO,t2.ARN,t2.SOURCE_AMOUNT,t2.SOURCE_CURR_CODE,t2.DESTINATION_AMOUNT,t2.DESTINATION_CURR_CODE,t2.SETTLEMENT_FLAG,"+ 
									"t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_header = "RELAX_PARAM,DIFF_AMOUNT,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG"+ 
									"RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_header +stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH")
								&& (file1_name.equals("RUPAY")||comparebeanObj.getStSubCategory().equals("SURCHARGE")|| file1_name.equals("VISA")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID ,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_headers + stTable2_Selheaders;
						}
						else
						{	
							if(getStatus(comparebeanObj,file2_name,"FILTERATION").equals("Y"))
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;
							}
							else
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;


							}
						}

						//********* GETTING RECORDS IN RSET

						//	JOIN1_QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						//			JOIN2_QUERY = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN TEMP_"+table1_name + " t1 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						JOIN1_QUERY = "SELECT DISTINCT 'N',"+getcols1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name + " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')"; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						JOIN2_QUERY = "SELECT DISTINCT 'N',"+getcols2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name + " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" // AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" ; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						//GET WHERE CONDITION FROM MAIN_matching_CONDITION TABLE
					
						
					}
					else
					{

						String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);
						String cols1 = "t1."+stTable1_headers.replace(",", ",t1." );
						logger.info(cols1);
						String getcols1 = "";
						if(file1_name.equals("CBS"))
						{
							String temp_headers ="RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
							//getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							//getcols1 = "t1.CREATEDDATE,t2.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							getcols1 = "t1.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,"+cols1;
							Table1_Selheaders = temp_headers + Table1_Selheaders;
						}
						else if(file1_name.equals("SWITCH"))
						{
							String temp_headers = "";
							if((file2_name.equals("CBS") && (comparebeanObj.getStSubCategory().equals("DOMESTIC")|| comparebeanObj.getStSubCategory().equals("INTERNATIONAL")))
									||(file2_name.equals("CBS") && (comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("ACQUIRER"))))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.AMOUNT AS CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							else //if(file2_name.equals("CBS") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
							{
								//getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.CBS_AMOUNT,t1.DIFF_AMOUNT,"+cols1;
								temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
								Table1_Selheaders = temp_headers+Table1_Selheaders;
							}
							/*else
							getcols1 = "t1.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.DIFF_AMOUNT,"+cols1;*/

						}
						else
						{
							if(getStatus(comparebeanObj,file1_name,"FILTERATION").equals("Y"))
							{
								//getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,t1.SEG_TRAN_ID,"+cols1;
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t1.SEG_TRAN_ID,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,SEG_TRAN_ID,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;
							}
							else
							{
								getcols1 = "t1.CREATEDDATE,t1.CREATEDBY,t1.FILEDATE,"+cols1;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,";
								Table1_Selheaders= temp_headers+Table1_Selheaders;


							}

						}


						String getcols2 = "";
						String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
						String cols2 = "t2."+stTable2_headers.replace(",", ",t2." );
						logger.info(cols2);
						if(file2_name.equals("CBS"))
						{
							String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,";
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,"+cols2;
							stTable2_Selheaders = temp_headers+stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH") && file1_name.equals("CBS")
								&& (comparebeanObj.getStSubCategory().equals("DOMESTIC")||comparebeanObj.getStSubCategory().equals("INTERNATIONAL")
										||comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("ACQUIRER")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t1.MAN_CONTRA_ACCOUNT,t1.CONTRA_ACCOUNT,t1.AMOUNT AS CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_header = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_header +stTable2_Selheaders;
						}
						else if(file2_name.equals("SWITCH")
								&& (file1_name.equals("RUPAY")||comparebeanObj.getStSubCategory().equals("SURCHARGE")|| file1_name.equals("VISA")))
						{
							//getcols2 = "t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,t2.MAN_CONTRA_ACCOUNT,t2.CONTRA_ACCOUNT,t2.CBS_AMOUNT,t2.DIFF_AMOUNT,"+cols2;
							String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID ,MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,";
							stTable2_Selheaders = temp_headers + stTable2_Selheaders;
						}
						else
						{	
							if(getStatus(comparebeanObj,file2_name,"FILTERATION").equals("Y"))
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,t2.SEG_TRAN_ID,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),t2.SEG_TRAN_ID,"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;
							}
							else
							{
								//getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,t2.FILEDATE,"+cols2;
								getcols2 = "t2.CREATEDDATE,t2.CREATEDBY,TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+cols2;
								String temp_headers = "RELAX_PARAM,CREATEDDATE,CREATEDBY,FILEDATE,";
								stTable2_Selheaders = temp_headers+stTable2_Selheaders;


							}
						}

						//********* GETTING RECORDS IN RSET

						//	JOIN1_QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						//			JOIN2_QUERY = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN TEMP_"+table1_name + " t1 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
						JOIN1_QUERY = "SELECT DISTINCT 'N',"+getcols1+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name + " t1 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file2_name + " t2 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"// AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')"; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						JOIN2_QUERY = "SELECT DISTINCT 'N',"+getcols2+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name + " t2 INNER JOIN TEMP_"+comparebeanObj.getStMergeCategory()
								+"_"+file1_name + " t1 ON( "+condition 
								+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" // AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
								+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" ; // AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
						/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
							+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
						//GET WHERE CONDITION FROM MAIN_matching_CONDITION TABLE
					}
				}
				
				String compare_cond1 = getCompareCondition(comparebeanObj,file1_name,inRec_set_Id);
				String compare_cond2 = getCompareCondition(comparebeanObj,file2_name,inRec_set_Id);
				//String compare_cond = compare_cond1 +" AND " + compare_cond2;
				if(!compare_cond1.equals(""))
				{
					JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond1;
					JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond1;
				}
				if(!compare_cond2.equals(""))
				{

					JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond2;
					JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond2;
				
					
				}

				logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

				//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
				logger.info("COMPARE QUERY IS****************************************");
				logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
				logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);
				//logger.info("JOIN1 QUERY IS "+JOIN1_QUERY); 
				
				// ALTERING SETTLEMENT TABLE..... ADDING RELAX PARAMTERE FIELD
				int Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name.toUpperCase(),"RELAX_PARAM"}, Integer.class);
				
				if(Column_count == 0)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+" ADD RELAX_PARAM VARCHAR(100 BYTE)";
					logger.info("ALTER_QUERY=="+ALTER_QUERY);
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				Column_count = getJdbcTemplate().queryForObject(GET_COLS_COUNT, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name.toUpperCase(),"RELAX_PARAM"}, Integer.class);
				
				if(Column_count == 0)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" ADD RELAX_PARAM VARCHAR(100 BYTE)";
					logger.info("ALTER_QUERY=="+ALTER_QUERY);
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				
				INSERT1_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" ("+Table1_Selheaders+")";
				INSERT2_QUERY = "INSERT INTO "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" ("+stTable2_Selheaders+")";
				
				
				
				String INSERT_DATA = INSERT1_QUERY + JOIN1_QUERY;
				
							
				logger.info("INSERT DATA QUERY IS "+INSERT_DATA);
				
				logger.info("start time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(INSERT_DATA);
				logger.info("end time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				
				
				INSERT_DATA = INSERT2_QUERY + JOIN2_QUERY;
				logger.info("INSERT DATA QUERY IS "+INSERT_DATA);
				
				logger.info("start time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(INSERT_DATA);
				logger.info("END time FOR INSERTING MATCHED DATA 1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				//GET DATA FROM MATCHED TABLE AND THEN INSERT IT INTO SETTLEMENT TABLE
				
				List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id},new ColumnMapper());
				String select_cols = "";
				int count = 0;
				for(String cols : TABLE_COLS)
				{
					if(count == 0)
						{
							select_cols = cols;
							count++;
						}
					else
						select_cols = select_cols+","+cols;
				}

				String GET_MATCHED = "SELECT '"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id
								+"',"+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" WHERE RELAX_PARAM = '"+stRelax_Param+"'";
				
				SETTLEMENT_INSERT1 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file1_name+" (DCRS_REMARKS,"+select_cols+") "
									+GET_MATCHED;
				
				logger.info("GET_MATCHED QUERY IS "+GET_MATCHED);
				logger.info("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT1);
				logger.info("start time FOR INSERTING MATCHED DATA IN SETTLEMENT1  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(SETTLEMENT_INSERT1);
				logger.info("END time FOR INSERTING MATCHED DATA IN SETTLEMENT1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id},new ColumnMapper());
				select_cols = "";
				count = 0;
				for(String cols : TABLE_COLS)
				{
					if(count == 0)
						{
							select_cols = cols;
							count++;
						}
					else
						select_cols = select_cols+","+cols;
				}
				
				GET_MATCHED = "SELECT '"+comparebeanObj.getStMergeCategory()+"-MATCHED-"+inRec_set_Id
						+"',"+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" WHERE RELAX_PARAM = '"+stRelax_Param+"'";
				
				SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+file2_name+" (DCRS_REMARKS,"+select_cols+") "
							+GET_MATCHED;
				
				logger.info("GET_MATCHED QUERY IS "+GET_MATCHED);
				logger.info("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT2);
				
				logger.info("start time FOR INSERTING MATCHED DATA IN SETTLEMENT2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				getJdbcTemplate().execute(SETTLEMENT_INSERT2);
				logger.info("END time FOR INSERTING MATCHED DATA IN SETTLEMENT2  "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
//---------------------------------------------------------------------------REMOVING DATA FROM TEMP for CYCLE 1 ONLY AS IT WAS CAUSING ISSUE IN DUPLICATES-------------------------------------------------------------------------------------------------------------
				if(inRec_set_Id == 1)
				{
					String DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					int reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table1_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					String knock_condition = getCondition(knockoff_Criteria);

					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";
					getJdbcTemplate().execute(DELETE_FROM_TEMP);

					DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table2_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table2_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					knock_condition = getCondition(knockoff_Criteria);
					
					logger.info("knock_condition "+knock_condition);

					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";
					
					logger.info("DELETE_FROM_TEMPis "+DELETE_FROM_TEMP);

					getJdbcTemplate().execute(DELETE_FROM_TEMP);

				}
				else if(comparebeanObj.getStCategory().equalsIgnoreCase("VISA") && inRec_set_Id == 2)
				{

					String DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file1_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()
							+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file1_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					int reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table1_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					String knock_condition = getCondition(knockoff_Criteria);

					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";
					getJdbcTemplate().execute(DELETE_FROM_TEMP);

					DELETE_FROM_TEMP = "DELETE FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+file2_name+" OS1 WHERE  FILEDATE = '"+comparebeanObj.getStFile_date()+"' AND EXISTS("+
							" SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+file2_name+"_MATCHED"+inRec_set_Id+" OS2 WHERE FILEDATE = '"
							+comparebeanObj.getStFile_date()+"' AND (";

					reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table2_file_id), comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()},Integer.class);
					logger.info("reversal id is "+reversal_id);

					knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table2_file_id}, new KnockOffCriteriaMaster());
					logger.info("knockoff criteria "+knockoff_Criteria.size());

					knock_condition = getCondition(knockoff_Criteria);
					logger.info("knock_condition "+knock_condition);

					DELETE_FROM_TEMP = DELETE_FROM_TEMP + knock_condition+"))";
					logger.info("DELETE_FROM_TEMPis "+DELETE_FROM_TEMP);
					
					getJdbcTemplate().execute(DELETE_FROM_TEMP);

				
					
				}
			
				loop--;

			}
			logger.info("***** CompareRupayDaoImpl.updateMatchedRecordsForVisa End ****");
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.updateMatchedRecordsForVisa");
			 logger.error(" error in CompareRupayDaoImpl.updateMatchedRecordsForVisa", new Exception(" CompareRupayDaoImpl.updateMatchedRecordsForVisa",e));
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
				demo.logSQLException(e, "CompareRupayDaoImpl.updateMatchedRecordsForVisa");
				logger.error(" error in CompareRupayDaoImpl.updateMatchedRecordsForVisa", new Exception(" CompareRupayDaoImpl.updateMatchedRecordsForVisa",e));
				throw new Exception("ERROR WHILE CLOSING DB CONNECTION ");
			}
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
	
	
	/*public void insertBatch(final String QUERY, final ResultSet rset,final String[] columns){
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
				 if(value == columns.length)
					 value = 3;
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
					//logger.info("column value is "+rset.getString(columns[i-1].trim()));
					if(columns[i-1].trim().equals("CBS_AMOUNT"))
					{
						ps.setString((i), rset.getString("AMOUNT"));
					}
					else
					{	
						ps.setString((i), rset.getString(columns[i-1].trim()));
					}
					
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 100)
				{
					//logger.info("******** FLAG IS "+flag);
					flag = 1;
				
					ps.executeBatch();
					//logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			logger.info("completed insertion");
			
			
		} catch (DataAccessException | SQLException e) {
		
			logger.info("INSERT BATCH EXCEPTION "+e);
		}
	}
	
	public void insertRelaxBatch(final String QUERY, final ResultSet rset,final ResultSet amt_rset,final String[] columns){
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
				 if(value == columns.length)
					 value = 3;
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
					//logger.info("column value is "+rset.getString(columns[i-1].trim()));
					
					if(columns[i-1].trim().equals("CBS_AMOUNT"))
					{
						ps.setString((i), rset.getString("DIFF_AMOUNT"));
					}
					else
					{	
						ps.setString((i), rset.getString(columns[i-1].trim()));
					}
					
				//	value++;
				//	logger.info("i is "+i);
				}
				ps.addBatch();
				
				if(flag == 100)
				{
					//logger.info("******** FLAG IS "+flag);
					flag = 1;
				
					ps.executeBatch();
					//logger.info("Executed batch is "+batch);
					batch++;
				}
			}
			ps.executeBatch();
			logger.info("completed insertion");
			
			
		} catch (DataAccessException | SQLException e) {
		
			logger.info("INSERT BATCH EXCEPTION "+e);
		}
	}*/
	
	/*public void updateBatch(final String QUERY, final ResultSet rset,final String[] columns){
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
				 if(value == columns.length)
					 value = 3;
				
				for(int i = 1 ; i <= columns.length; i++)
				{
					//logger.info("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
					//logger.info("column value is "+rset.getString(columns[i-1].trim()));
					if(rset.getString(columns[i-1].trim()) == null)	
					{	
						ps.setString((i), "!null!");
						//logger.info(i+" = "+"1");
					
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
					//logger.info("Executed batch is "+batch);
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
	*/
	
	
	public void alterMatchedandSettlementTables(CompareBean comparebeanObj,int inRec_set_id)throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.alterMatchedandSettlementTables Start ****");
		
		try
		{
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id+"'";
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			
			logger.info("CHECK TABLE QUERY IS "+CHECK_TABLE);
			logger.info("tableExist QUERY IS "+tableExist);
			if(tableExist == 1)
			{
				boolean CheckForCol1 = false;
				boolean CheckForCol2 = false;
				boolean CheckForCol3 = false;
				boolean CheckForCol4 = false;
				//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
				List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id}, new ColumnsMapper());
				
				
				for(int i = 0 ;i<Columns.size(); i++)
				{
					if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
					{
						CheckForCol1 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("CONTRA_ACCOUNT"))
					{
						CheckForCol2 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("CBS_AMOUNT"))
					{
						CheckForCol3 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("DIFF_AMOUNT"))
					{
						CheckForCol4 = true;
					}
					
				}
				
				if(!CheckForCol1)
				{
					String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id+" ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol2)
				{
					String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id+" ADD CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol3)
				{
					String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id+" ADD CBS_AMOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol4)
				{
					String ALTER_QUERY = "ALTER TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED"+inRec_set_id+" ADD DIFF_AMOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
			}
			
			
			CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH'";
			tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			
			logger.info("CHECK SETTLEMENT TABLE QUERY IS "+CHECK_TABLE);
			logger.info("tableExist IS "+tableExist);
			if(tableExist == 1)
			{
				boolean CheckForCol1 = false;
				boolean CheckForCol2 = false;
				boolean CheckForCol3 = false;
				boolean CheckForCol4 = false;
				//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
				List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{"SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH"}, new ColumnsMapper());
				
				for(int i = 0 ;i<Columns.size(); i++)
				{
					if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
					{
						CheckForCol1 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("CONTRA_ACCOUNT"))
					{
						CheckForCol2 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("CBS_AMOUNT"))
					{
						CheckForCol3 = true;
					}
					else if(Columns.get(i).equalsIgnoreCase("DIFF_AMOUNT"))
					{
						CheckForCol4 = true;
					}
					
				}
				
				if(!CheckForCol1)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol2)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol3)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD CBS_AMOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				if(!CheckForCol4)
				{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+comparebeanObj.getStCategory()+"_SWITCH ADD DIFF_AMOUNT VARCHAR2(100 BYTE)";
					logger.info("ALTER_QUERY QUERY IS "+ALTER_QUERY );
					getJdbcTemplate().execute(ALTER_QUERY);
				}
			}
			
			
			logger.info("***** CompareRupayDaoImpl.alterMatchedandSettlementTables End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.alterMatchedandSettlementTables");
			 logger.error(" error in  CompareRupayDaoImpl.alterMatchedandSettlementTables", new Exception("  CompareRupayDaoImpl.alterMatchedandSettlementTables ",e));
			 throw e;
		}
	}
	
	private static class ColumnsMapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			String stColumns = rs.getString("COLUMN_NAME");
			
			return stColumns;
			
		}
	}

	public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.moveToRecon Start ****");
		int loop = 0,tableExist = 0;
		//String RECON_RECORDS_PART1 = "", recon_columns = "", RECON_INSERT = "", RECON_RECORDS_PART2 = "", RECON_RECORDS = "";
		String RECON_RECORDS_PART1 = "",  RECON_INSERT = "", RECON_RECORDS_PART2 = "", RECON_RECON_PART3 = "", RECON_RECORDS = "";
		String CHECK_TABLE = "";
		String stFile_name= "";
		PreparedStatement psrecon = null, settlement_pstmt= null;
		ResultSet rs = null,settlement_set = null;
		Connection con;
		String[] col_names={""};
		String SETTLEMENT_INSERT = "";
		try
		{
			logger.info("MOVETORECON STARTS HERE ---------------------------------------------------------------------------------------- ");
			//GET MATCHED FLAGS.....
			//List<String> stMatched_Flags = getMatchedFlag(comparebeanObj, inRec_Set_id);
			int file_id = 0;
			while(loop<tables.size())
			{			
				
				//String stMatched_Flag =stMatched_Flags.get(loop);
				
				stFile_name = tables.get(loop);
				
				/*if(stMatched_Flag.equals("Y"))
				{*/

					//String[] a = table_name.split("_");
					//1. *******CHECK FOR RECON TABLE
					CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+inRec_Set_id+"'";
					logger.info("check table "+CHECK_TABLE);
					tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					logger.info("tableExist "+tableExist);
					/*if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						if(comparebeanObj.getStCategory().equals("RUPAY"))
						{
							file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),"DOMESTIC" },Integer.class);
						}
						else if(comparebeanObj.getStCategory().equals("VISA"))
						{
							file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),"ISSUER" },Integer.class);
						}
					}
					else
					{
						file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
					}*/
					if(loop == 0)
					{
						file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),comparebeanObj.getStTable1_SubCategory()},Integer.class);
					}
					else if(loop == 1)
					{
						file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),comparebeanObj.getStTable2_SubCategory() },Integer.class);
					}
						
					String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
					
					if(stFile_name.equals("CBS"))
					{
						stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
					}
					else if(stFile_name.equals("SWITCH"))
					{
						if(comparebeanObj.getStCategory().equalsIgnoreCase("VISA") && inRec_Set_id == 3)
						{
							stFile_headers = stFile_headers +", MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,DCRS_SEQ_NO,ARN,SOURCE_AMOUNT,SOURCE_CURR_CODE,DESTINATION_AMOUNT,DESTINATION_CURR_CODE,SETTLEMENT_FLAG";
						}
						else
							stFile_headers = stFile_headers +", MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT";
					}
						

					if(tableExist == 0)
					{
						String tab_cols;
						//recon_columns = "CREATEDDATE, CREATEDBY,SEG_TRAN_ID,"+stFile_headers;
						tab_cols = "SEG_TRAN_ID NUMBER, CREATEDDATE DATE, CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE";

						col_names = stFile_headers.trim().split(",");
						for(int i=0 ;i <col_names.length; i++)
						{
							tab_cols = tab_cols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
						}
						CREATE_RECON_TABLE = "CREATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id +"( "+tab_cols +" )";
						logger.info("CREATE_RECON_TABLE "+CREATE_RECON_TABLE);
						getJdbcTemplate().execute(CREATE_RECON_TABLE);
						/*CREATE_RECON_TABLE = "CREATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id+" AS SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"
											+stFile_name+"_MATCHED"+inRec_Set_id;
						getJdbcTemplate().execute(CREATE_RECON_TABLE);
*/
					}
					
				//	recon_columns = "CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
					String col_headers = "SEG_TRAN_ID,"+stFile_headers;
					col_names = col_headers.split(",");
					
					// CHECK IF FILTERATION AND KNOCKOFF FLAG IS ON
					String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
					String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");
					logger.info("stFilter_Status "+stFilter_Status);
					logger.info("stknockoff_Status "+stknockoff_Status);
					
						//2. *************** GET RECORDS FROM TEMP TABLE
						//RECON_RECORDS = "SELECT * FROM TEMP_"+table_name+" WHERE MATCHING_FLAG = 'N'";
					List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS, new Object[] {"RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id},new ColumnMapper());
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
						RECON_RECORDS_PART1 = "SELECT "+Selec_cols+" FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" OS1 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
								//"AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'
								" AND NOT EXISTS";

						RECON_RECORDS_PART2 = "( SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+inRec_Set_id+" OS2 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
								//"AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' 
								" AND ";
						
						
					if(stFilter_Status.equals("Y") && stknockoff_Status.equals("Y"))
					{
						int reversal_id = 0;
						/*if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
						{
							if(comparebeanObj.getStCategory().equals("RUPAY"))
							{
								reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_DOMESTIC")},Integer.class);
							}
							else if(comparebeanObj.getStCategory().equals("VISA"))
							{
								reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_ISSUER")},Integer.class);
							}
						}
						else
						{
							//reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
							reversal_id = getJdbcTemplate().queryForObject("SELECT MIN(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?"
													, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
						}*/
						if(loop == 0)
						{
								reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable1_SubCategory())},Integer.class);
							
						}
						else
						{
							//reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
							/*reversal_id = getJdbcTemplate().queryForObject("SELECT MIN(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?"
													, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable2_SubCategory())},Integer.class);*/
							reversal_id = getJdbcTemplate().queryForObject("SELECT MAX(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?"
									, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStTable2_SubCategory())},Integer.class);
						}
						
						logger.info("REV ID IS "+reversal_id);
						List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());


						String compare_cond = getCondition(knockoff_Criteria);
						logger.info("COMPARE CONDITION IS "+compare_cond);

						RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
					}
					else
					{
											
						//GET MATCHING CRITERIA'S AS KNOCKOFF CRITERIA IS NOT AVAILABLE
						List<KnockOffBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())
															,inRec_Set_id},new MatchCriteriaMaster());
						
						String compare_cond = getCondition(match_Headers1);
						
						logger.info("compare condition is "+compare_cond);
						
						if(stFile_name.equals("RUPAY"))
							RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " AND OS1.TXNFUNCTION_CODE = OS2.TXNFUNCTION_CODE )";
						else
							RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
						
					}
					logger.info("RECON_RECORDS IS "+RECON_RECORDS);
					con = getConnection();
					psrecon = con.prepareStatement(RECON_RECORDS);
					rs = psrecon.executeQuery();

					settlement_pstmt = con.prepareStatement(RECON_RECORDS);
					settlement_set = settlement_pstmt.executeQuery();

					//3. *************** INSERT RECORDS IN RECON TABLE
					RECON_INSERT = "INSERT INTO RECON_"+comparebeanObj.getStMergeCategory()+"_"
							+stFile_name+inRec_Set_id+"( "+Selec_cols +" ) ";
							//VALUES (SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
					
					String RECON_DATA = RECON_INSERT+ RECON_RECORDS;
					logger.info("RECON_DATA QUERY IS "+RECON_DATA);
					
					
					SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" ( DCRS_REMARKS,"+Selec_cols+") "+
								"SELECT '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"',"+Selec_cols+" FROM RECON_"
									+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id;
					
					
					//ADDED ON 18TH AUG 2018 BY INT6345
					//START
					
					/*String get_datacount="SELECT count(*) FROM RECON_"
									+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id;
					
					int count_val = getJdbcTemplate().queryForObject(get_datacount, Integer.class);*/
					// END
					
					logger.info("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);
					
					logger.info("start time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
					
					
					getJdbcTemplate().execute(RECON_DATA);
					
					//insertBatch(RECON_INSERT, rs, col_names);
					logger.info("End time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
					logger.info("start time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
					//insertBatch(SETTLEMENT_INSERT, settlement_set, col_names);
					
					getJdbcTemplate().execute(SETTLEMENT_INSERT);
					
					logger.info("End time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
					
					
					
						
				rs = null;
				psrecon = null;
				
	
				String DELETE_TEMP = "TRUNCATE TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name;
				try
				{
				logger.info("DELETE_TEMP=="+DELETE_TEMP);
				getJdbcTemplate().execute(DELETE_TEMP);
				}
				catch(Exception e)
				{
					logger.info("Error while truncating table ");
					
				}
				if(comparebeanObj.getStSubCategory().equals("SURCHARGE"))
				{
					DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inRec_Set_id;
					logger.info("DELETE_TEMP=="+DELETE_TEMP);
					getJdbcTemplate().execute(DELETE_TEMP);
					
					DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
					logger.info("DELETE_TEMP=="+DELETE_TEMP);
					getJdbcTemplate().execute(DELETE_TEMP);
					
					if(tables.get(loop).equals("CBS"))
					{
						DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);
						
						DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);
					}
				}
				else
				{
					if(stFilter_Status.equals("Y"))
					{
						DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);
					}
					
					if(stknockoff_Status.equals("Y"))
					{
						DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);
					}
					
					/*DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inRec_Set_id;
					getJdbcTemplate().execute(DELETE_TEMP);*/
					
					DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
					logger.info("DELETE_TEMP=="+DELETE_TEMP);
					getJdbcTemplate().execute(DELETE_TEMP);
					
				}
				
				
		
				
				loop++;
			
			}
			logger.info("***** CompareRupayDaoImpl.moveToRecon End ****");
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.moveToRecon");
			 logger.error(" error in CompareRupayDaoImpl.moveToRecon", new Exception(" CompareRupayDaoImpl.moveToRecon",e));
			 throw e;
			
		}
		
	}
	
	//ADDED BY INT8624 FOR TRUNACTING TEMP TABLES on 17/NOV/2020
		@Override
		public void truncateTempTables(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)
		{
			try {
				int loop = 0;
				while(loop<tables.size()) {
					
					String stFile_name = tables.get(loop);

					String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
					String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");
					logger.info("stFilter_Status "+stFilter_Status);
					logger.info("stknockoff_Status "+stknockoff_Status);

					String DELETE_TEMP = "TRUNCATE TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name;
					logger.info("DELETE_TEMP=="+DELETE_TEMP);
					getJdbcTemplate().execute(DELETE_TEMP);
					if(comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inRec_Set_id;
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);

						DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);

						if(tables.get(loop).equals("CBS"))
						{
							DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
							logger.info("DELETE_TEMP=="+DELETE_TEMP);
							getJdbcTemplate().execute(DELETE_TEMP);

							DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
							logger.info("DELETE_TEMP=="+DELETE_TEMP);
							getJdbcTemplate().execute(DELETE_TEMP);
						}
					}
					else
					{
						if(stFilter_Status.equals("Y"))
						{
							DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
							logger.info("DELETE_TEMP=="+DELETE_TEMP);
							getJdbcTemplate().execute(DELETE_TEMP);
						}

						if(stknockoff_Status.equals("Y"))
						{
							DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
							logger.info("DELETE_TEMP=="+DELETE_TEMP);
							getJdbcTemplate().execute(DELETE_TEMP);
						}



						DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
						logger.info("DELETE_TEMP=="+DELETE_TEMP);
						getJdbcTemplate().execute(DELETE_TEMP);

					}
					loop++;
				}
			}
			catch(Exception e)
			{
				logger.info("Exception in truncateTempTables "+e);
			}
		}
		
	
/*public void TTUMRecords(List<String> Table_list,CompareBean comparebeanObj)throws Exception
{
	logger.info("TTUM************************************************************************************************************************");
	try
	{
		String stTable1_Name = Table_list.get(0);
		String stTable2_Name = Table_list.get(1);
		//String[] stTable1 = stTable1_Name.split("_");
	//	String[] stTable2 = stTable2_Name.split("_");
	//	String stCategory = stTable1[0] ;
		String stFile1_Name = Table_list.get(0);
		String stFile2_Name = Table_list.get(1);
		String table1_condition = "";
		String table2_condition= "";
		String condition = "";
		logger.info("TTUM STARTS HERE *************************************************");
		
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
		
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,comparebeanObj.getStMergeCategory()},new MatchParameterMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,comparebeanObj.getStMergeCategory()},new MatchParameterMaster2());
		
	
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
			/*logger.info("i value is "+i);
			logger.info("match headers length is "+match_Headers2.size());
			logger.info("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
			/*if(match_Headers2.get(i).getStMatchTable2_Padding().equals("Y"))
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
		
		if(stFile1_Name.equals("CBS"))
		{
			JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile1_Name 
					+ " t1 INNER JOIN "+comparebeanObj.getStMergeCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
					+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
					+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY')='"+comparebeanObj.getStFile_date()
					+"' AND REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON'";
		}
		else if(stFile2_Name.equals("CBS"))
		{
			JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+stFile2_Name + " t2 INNER JOIN "+comparebeanObj.getStMergeCategory()+"_"+stFile1_Name + " t1 ON( "+condition + " ) " +
					"WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'"
					+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
					+"' AND REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON'";
		}
		//GET TTUM CONDITION
		/*String cond1 = getTTUMCondition(table1_file_id);
		String cond2 = getTTUMCondition(table2_file_id);*/
	
	//	logger.info("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

		//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
	//	logger.info("COMPARE QUERY IS****************************************");
		
		
		/*if(!cond1.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond1;
			//JOIN2_QUERY = JOIN2_QUERY + " AND "+cond1;
		}
		if(!cond2.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+cond2;
			//JOIN2_QUERY = JOIN2_QUERY + " AND "+cond2;
		}
		logger.info("JOIN1 QUERY IS "+JOIN1_QUERY);
		logger.info("JOIN2_QUERY IS "+JOIN2_QUERY);*/
		
		//get failed records for table 1
		
	//	getFailedRecords(JOIN1_QUERY, table2_file_id , comparebeanObj.getStMergeCategory(), stFile2_Name,stFile1_Name,table1_file_id);
		//getFailedRecords(JOIN2_QUERY, table1_file_id , comparebeanObj.getStMergeCategory(), stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
		
		//NOW TRUNCATE ALL TABLES----------------------------
	/*	logger.info("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
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
		/*String UPDATE_OLD_TTUM_RECORDS = "UPDATE SETTLEMENT_CBS SET CREATEDDATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
		
		getJdbcTemplate().execute(UPDATE_OLD_TTUM_RECORDS);
		
		logger.info("updated old ttum records");*/
		
		
		
	/*}
	catch(Exception e)
	{
		logger.info("TTum "+e);
	}
	finally
	{
		
	}
	
}

public void getFailedRecords(String QUERY, int file_id,String stCategory,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id)throws Exception
{
	logger.info("------------------------------------------------------------getfailedrecords -------------------------------");
	PreparedStatement pstmt = null;
	Connection conn = null;
	ResultSet rset = null;
	String update_condition = "";

	
	try
	{
		conn = getConnection();
		pstmt = conn.prepareStatement(QUERY);
		rset = pstmt.executeQuery();
	
		/*int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), stCategory},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());
*/
		//CREATE CONDITION USING KNOCKOFF CRITERIA 
	/*	while(rset.next())
		{
			logger.info("WHILE STARTS");
			update_condition = "";
			
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+stUpdate_FileName+" SET REMARKS = "+stCategory+"'-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND REMARKS = "+stCategory+"'-UNRECON' ";
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), stCategory},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				for(int i = 0; i<knockoff_Criteria1.size() ; i++)
				{
					if(i == (knockoff_Criteria1.size()-1))
					{
						if(knockoff_Criteria1.get(i).getStReversal_padding().equals("Y"))
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
						if(knockoff_Criteria1.get(i).getStReversal_padding().equals("Y"))
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
				
				
			/*}
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
			}*/
			
			
	/*	}
		logger.info("while completed");
		
		
	}
	catch(Exception e)
	{
		logger.info("Exception in getFailedRecords "+e);
	}
	
}*/

	public String getTTUMCondition(int inFile_Id) throws Exception
	{
		logger.info("***** CompareRupayDaoImpl.getTTUMCondition Start ****");
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
							condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
						}
						else
						{
							condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)),"+filterBeanObj.getStsearch_Startcharpos()+","+
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
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
						}
						else
						{
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
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
			
			logger.info("***** CompareRupayDaoImpl.getTTUMCondition End ****");
			
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.getTTUMCondition");
			 logger.error(" error in CompareRupayDaoImpl.getTTUMCondition", new Exception(" CompareRupayDaoImpl.getTTUMCondition",e));
			 throw e;
			
		}
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
	logger.info("***** CompareRupayDaoImpl.getReversalTTUMcondition Start ****");
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
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)),"+filterBeanObj.getStsearch_Startcharpos()+","+
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
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",1)) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
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
		
		
		
		logger.info("***** CompareRupayDaoImpl.getReversalTTUMcondition End ****");
		
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.getReversalTTUMcondition");
		 logger.error(" error in CompareRupayDaoImpl.getReversalTTUMcondition", new Exception(" CompareRupayDaoImpl.getReversalTTUMcondition",e));
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
			
		//	String a[] = sttable_Name.split("-"); 
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
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{
					if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
					{
						stcompare_con = stcompare_con + "(SUBSTR(TRIM( OS1."+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
					else if(filterBeanObj.getStSearch_Condition().trim().equals("!="))
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
					if(filterBeanObj.getStSearch_Condition().equals("="))
					{
						stcompare_con = stcompare_con + "(TRIM( OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+filterBeanObj.getStSearch_pattern().trim()+"'";
					}
					else if(filterBeanObj.getStSearch_Condition().equals("!="))
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
							if((compare_cond.get(j).getStSearch_Condition().trim()).equals("="))
							{
								stcompare_con = stcompare_con + " OR SUBSTR(TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") , "+compare_cond.get(j).getStsearch_Startcharpos()+","+
										compare_cond.get(j).getStsearch_Endcharpos()+") "+compare_cond.get(j).getStSearch_Condition().trim()+"'"+ compare_cond.get(j).getStSearch_pattern().trim()+"'";
							}
							else if(compare_cond.get(j).getStSearch_Condition().trim().equals("!="))
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
								if(j==(compare_cond.size()-1))
								{
									stcompare_con = stcompare_con + " , '" +compare_cond.get(j).getStSearch_pattern().trim()+"')";
								}
								else
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
			
			
	}
	catch(Exception e)
	{
		logger.info("eXCEPTION IS "+e);
	}
	return stcompare_con ;	
}*/


public String getReconParameters(CompareBean comparebeanObj,String stfile_name,int inRec_set_Id) throws Exception
{
	logger.info("***** CompareRupayDaoImpl.getReconParameters Start ****");
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
						//ADDED BY INT5779 ON 21ST MARCH FOR VISA ISSUER AS AMOUNT IN DECIMALS WAS NOT GETTING POPULATED IN COMPARE MENU
						if(filterBeanObj.getStSearch_header().contains("AMOUNT"))
						{
							stcompare_con = stcompare_con + "(TRIM(OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()
									+filterBeanObj.getStSearch_pattern();
						}
						else
						{
							stcompare_con = stcompare_con + "(TRIM(OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()
										+" '"+filterBeanObj.getStSearch_pattern()+"'";
						}
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
								//ADDED BY INT5779 ON 21ST MARCH FOR VISA ISSUER AS AMOUNT IN DECIMALS WAS NOT GETTING POPULATED IN COMPARE MENU
								if(compare_cond.get(j).getStSearch_header().contains("AMOUNT"))
								{
									stcompare_con = stcompare_con + " OR TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") "+
											compare_cond.get(j).getStSearch_Condition().trim()+compare_cond.get(j).getStSearch_pattern().trim();
								}
								else
								{
								stcompare_con = stcompare_con + " OR TRIM( OS1." + compare_cond.get(j).getStSearch_header()+") "+
										compare_cond.get(j).getStSearch_Condition().trim()+" '"+compare_cond.get(j).getStSearch_pattern().trim()+"'";
								}
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
					if(compare_cond.get(i).getStSearch_Condition().equals("!="))
					{
						stcompare_con = stcompare_con + ") )";
					}
					else
					stcompare_con = stcompare_con +")";
				}
				
			//	logger.info("condition is "+condition);
			
			}

			logger.info("compare condition is "+stcompare_con);
			
			logger.info("***** CompareRupayDaoImpl.getReconParameters End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.getReconParameters");
		 logger.error(" error in CompareRupayDaoImpl.getReconParameters", new Exception(" CompareRupayDaoImpl.getReconParameters",e));
		 throw e;
	}
	return stcompare_con ;	
}

public String getCompareCondition(CompareBean comparebeanObj,String stFile_Name,int inRec_set_Id) throws Exception
{
	logger.info("***** CompareRupayDaoImpl.getCompareCondition Start ****");
	String temp_param = "";
	String stcompare_con = "";
	int file_id;
	//String COMPARE_CONDITION = "";
	try
	{
			
			//String a[] = sttable_Name.split("-"); 
			//1.Get File ID from db
			if(stFile_Name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				if(comparebeanObj.getStCategory().equals("RUPAY"))
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_Name , comparebeanObj.getStCategory() , "DOMESTIC"},Integer.class);
				}
				else
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_Name , comparebeanObj.getStCategory() , "ISSUER"},Integer.class);
				}
			}
			else
			{
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_Name , comparebeanObj.getStCategory() , comparebeanObj.getStSubCategory()},Integer.class);
			}
			
			logger.info("file id is "+file_id);

			//2. Get Parameters from db
			
		//	List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id}, new CompareConditionMaster());
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_COMPARE_CONDITION, new Object[]{file_id,inRec_set_Id}, new CompareConditionMaster());


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
					if(compare_cond.get(i).getStSearch_Condition().equalsIgnoreCase("!="))
					{
						stcompare_con = stcompare_con +" ) ) AND ";
					}
					else
					{
						stcompare_con = stcompare_con +" ) AND ";
					}
				}
				else
				{
					if(compare_cond.get(i).getStSearch_Condition().equalsIgnoreCase("!="))
					{
						stcompare_con = stcompare_con +") ) ";	
					}
					else
					{
						stcompare_con = stcompare_con +")";
					}
				}
				
			//	logger.info("condition is "+condition);
			
			}

			logger.info("compare condition is "+stcompare_con);
			
			logger.info("***** CompareRupayDaoImpl.getCompareCondition End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.getCompareCondition");
		 logger.error(" error in CompareRupayDaoImpl.getCompareCondition", new Exception(" CompareRupayDaoImpl.getCompareCondition",e));
		 throw e;
	}
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
	
/*public String getCondition(List<KnockOffBean> knockoff_Criteria)
{
	
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
	return condition;
}*/

//HANDLING NULL VALUES
public String getCondition(List<KnockOffBean> knockoff_Criteria)
{
	
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
					condition = condition + "NVL( SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),1)"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" NVL(SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),1)";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
					select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",1) "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" NVL(OS2."+knockoff_Criteria.get(i).getStReversal_header()+",1)";
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
					condition = condition + " NVL(SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),1)"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" NVL(SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),1) AND ";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") = ? AND ";
					select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()
							+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
				else
				{
					condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",1) "+knockoff_Criteria.get(i).getStReversal_condition()
							+" NVL(OS2."+knockoff_Criteria.get(i).getStReversal_header()+",1) AND ";
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ? AND ";
					select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			
				
			}
			
		}
		
	}
	return condition;
}

public String getRecordsFromRelaxRecords(List<KnockOffBean> knockoff_Criteria)
{
	logger.info("***** CompareRupayDaoImpl.getRecordsFromRelaxRecords Start ****");
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
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( T3."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")";
					update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
					select_parameters = select_parameters + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
				}
				else
				{
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() +" T3."+knockoff_Criteria.get(i).getStReversal_header();
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
					select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header();
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
					condition = condition + " SUBSTR( T1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
							+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
							+" "+knockoff_Criteria.get(i).getStReversal_condition() 
							+" SUBSTR( T3."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
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
					condition = condition +"T1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
							+" T3."+knockoff_Criteria.get(i).getStReversal_header()+" AND ";
					update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ? AND ";
					select_parameters = select_parameters + "T1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
				}
			
				
			}
			
		}
		logger.info("condition=="+condition);
		logger.info("update_condition=="+update_condition);
		logger.info("select_parameters=="+select_parameters);
		
		logger.info("***** CompareRupayDaoImpl.getRecordsFromRelaxRecords End ****");
	}
	return condition;
}
public String getConditionForReversal(List<KnockOffBean> knockoff_Criteria)
{
	
	String  condition = "";
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




public void getReconRecords(List<String> tables,CompareBean compareBeanObj , int inRec_Set_id)throws Exception
{
	logger.info("***** CompareRupayDaoImpl.getReconRecords Start ****");
	String stFileName = "";
	String INSERT_QUERY = "";
	try
	{
		List<String> matched_flags = getMatchedFlag(compareBeanObj, inRec_Set_id);
		String stMatchedFalg;
		for(int i = 0;i<tables.size();i++)
		{
			stFileName = tables.get(i);
			stMatchedFalg = matched_flags.get(i);
			
		 		if(stMatchedFalg.equals("Y") && inRec_Set_id != 3)//AS PER NEW REQUIREMENT FROM SAMEER ON 07TH JAN 2018 DO NOT CARRY FORWARD UNMATCH TRANSACTIONS OF SWITCH UNRECON 3
			{
				List<String> Columns1 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+compareBeanObj.getStCategory()+"_"+compareBeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+stFileName}, new ColumnsMapper());
				String stCols = "";
				
				for(int j=0;j<Columns1.size();j++)
				{
					if(!Columns1.get(j).equals("CREATEDDATE") && !Columns1.get(j).equals("CREATEDBY") && !Columns1.get(j).equals("FILEDATE"))
					{
						if(j == 0)
							stCols = Columns1.get(j);
						else
							stCols = stCols+", "+Columns1.get(j);
							
					}
				}
				
							
			 		
				String GET_RECON_RECORDS = "SELECT SYSDATE,'"+compareBeanObj.getStEntryBy()+"',TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),"+stCols
						+" FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stFileName
						+" WHERE (DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"' OR DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-GENERATED-TTUM-"+
						inRec_Set_id+"' ) " +

						"AND FILEDATE = TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY')-1";

					//	"AND FILEDATE = TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY')-1";
						// "TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";

				
				
				INSERT_QUERY = "INSERT INTO TEMP_"+compareBeanObj.getStMergeCategory()+"_"+stFileName+
							" (CREATEDDATE, CREATEDBY,FILEDATE,"+stCols+") "+GET_RECON_RECORDS;
				
				logger.info("INSERT QUERY IN GETRECONRECORDS "+INSERT_QUERY);
				
				getJdbcTemplate().execute(INSERT_QUERY);
				
				
				
			//}
			//Changes by minakshi 20June18 
		}else if(stMatchedFalg.equals("N") && inRec_Set_id == 2 && stFileName.equalsIgnoreCase("RUPAY")){
			
			List<String> Columns1 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+compareBeanObj.getStCategory()+"_"+compareBeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+stFileName}, new ColumnsMapper());
			String stCols = "";
			
			for(int j=0;j<Columns1.size();j++)
			{
				if(!Columns1.get(j).equals("CREATEDDATE") && !Columns1.get(j).equals("CREATEDBY") && !Columns1.get(j).equals("FILEDATE"))
				{
					if(j == 0)
						stCols = Columns1.get(j);
					else
						stCols = stCols+", "+Columns1.get(j);
						
				}
			}
			
			String GET_RECON_RECORDS = "SELECT SYSDATE,'"+compareBeanObj.getStEntryBy()+"',TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),"+stCols
					+" FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stFileName
					+" WHERE (DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"' OR DCRS_REMARKS like '"+compareBeanObj.getStMergeCategory()+"-UNRECON-GENERATE%' ) " +
					 "AND FILEDATE = TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY')-1"
					// "TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')"
					+ " AND TXNFUNCTION_CODE = '200'";
			
			
			INSERT_QUERY = "INSERT INTO TEMP_"+compareBeanObj.getStMergeCategory()+"_"+stFileName+
						" (CREATEDDATE, CREATEDBY,FILEDATE,"+stCols+") "+GET_RECON_RECORDS;
			
			logger.info("INSERT QUERY IN GETRECONRECORDS "+INSERT_QUERY);
			
			getJdbcTemplate().execute(INSERT_QUERY);
			
			
		}else if(stMatchedFalg.equals("N") && inRec_Set_id == 2 && stFileName.equalsIgnoreCase("VISA")){
			
			List<String> Columns1 = getJdbcTemplate().query(GET_COLS, new Object[]{"TEMP_"+compareBeanObj.getStCategory()+"_"+compareBeanObj.getStTable1_SubCategory().substring(0, 3)+"_"+stFileName}, new ColumnsMapper());
			String stCols = "";
			
			for(int j=0;j<Columns1.size();j++)
			{
				if(!Columns1.get(j).equals("CREATEDDATE") && !Columns1.get(j).equals("CREATEDBY") && !Columns1.get(j).equals("FILEDATE"))
				{
					if(j == 0)
						stCols = Columns1.get(j);
					else
						stCols = stCols+", "+Columns1.get(j);
						
				}
			}
			
			String GET_RECON_RECORDS = "SELECT SYSDATE,'"+compareBeanObj.getStEntryBy()+"',TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),"+stCols
					+" FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stFileName
					+" WHERE (DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"' OR DCRS_REMARKS like '"+compareBeanObj.getStMergeCategory()+"-UNRECON-GENERATE%' ) " +
					 "AND FILEDATE = TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY')-1"
					// "TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+compareBeanObj.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')"
					+ " AND TC in('05','07')";
			
			
			INSERT_QUERY = "INSERT INTO TEMP_"+compareBeanObj.getStMergeCategory()+"_"+stFileName+
						" (CREATEDDATE, CREATEDBY,FILEDATE,"+stCols+") "+GET_RECON_RECORDS;
			
			logger.info("INSERT QUERY IN GETRECONRECORDS "+INSERT_QUERY);
			
			getJdbcTemplate().execute(INSERT_QUERY);
			
			
		}
			
		}
		logger.info("***** CompareRupayDaoImpl.getReconRecords End ****");
	}
	
	
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.getReconRecords");
		logger.error(" error in  CompareRupayDaoImpl.getReconRecords", new Exception(" CompareRupayDaoImpl.getReconRecords ",e));
		throw e;
	}
	
}

public void CleanTables(CompareBean comparebeanObj)throws Exception
{
	logger.info("***** CompareRupayDaoImpl.CleanTables Start ****");
	try
	{
		if(comparebeanObj.getStCategory().equals("RUPAY"))
		{
			if(comparebeanObj.getStSubCategory().equals("SURCHARGE") || comparebeanObj.getStSubCategory().equals("DOMESTIC"))
			{
				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_CBS_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_RUPAY_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_SWITCH_MATCHED3";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_CBS_MATCHED3";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);		 
			}
			else if(comparebeanObj.getStSubCategory().equals("INTERNATIONAL"))
			{
				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_CBS";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_KNOCKOFF";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_CBS_KNOCKOFF";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_CBS_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_SWITCH_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_RUPAY_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
			}
		}
		else if(comparebeanObj.getStCategory().equals("VISA"))
		{
			if(comparebeanObj.getStSubCategory().equals("ISSUER") || comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{

				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_SWITCH_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_CBS_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_SWITCH_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_VISA_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_SWITCH_MATCHED3";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_CBS_MATCHED3";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);	
			}
			else
			{

				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ACQ_SWITCH_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ACQ_CBS_MATCHED1";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ACQ_SWITCH_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ACQ_VISA_MATCHED2";
				logger.info("TRUNCATE_QUERY=="+TRUNCATE_QUERY);
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
			}
	
		
			
		}
			
		logger.info("***** CompareRupayDaoImpl.CleanTables End ****");
	}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.CleanTables");
		 logger.error(" error in CompareRupayDaoImpl.CleanTables", new Exception(" CompareRupayDaoImpl.CleanTables",e));
		 throw e;
	}
	//logger.info("Clean Tables Exit");
}


//RESPONSE CODE

public void TTUMRecords(List<String> Table_list,CompareBean compareBeanObj,int rec_set_id)throws Exception
{
	logger.info("***** CompareRupayDaoImpl.TTUMRecords Start ****");
	String JOIN1_QUERY = "";
	
	try
	{
		compareBeanObj.setInrec_set_id(rec_set_id);
		//String stTable1_Name = Table_list.get(0);
		//String stTable2_Name = Table_list.get(1);
		/*String[] stTable1 = stTable1_Name.split("_");
		String[] stTable2 = stTable2_Name.split("_");*/
		//String stCategory = compareBeanObj.getStMergeCategory();
		String stFile1_Name = "CBS";
		String stFile2_Name = "SWITCH";
		String table1_condition = "";
		String table2_condition= "";
		String condition = "";
		logger.info("TTUM STARTS HERE *************************************************");
		logger.info(compareBeanObj.getStSubCategory());
		logger.info(compareBeanObj.getStMergeCategory());
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , compareBeanObj.getStCategory() ,compareBeanObj.getStSubCategory()},Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, compareBeanObj.getStCategory(),compareBeanObj.getStSubCategory() },Integer.class);
		//generatettumBeanObj.getInRec_Set_Id()
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		
		String GET_MATCHING_PARAMS = "SELECT MATCH_HEADER, PADDING, START_CHARPOS , CHAR_SIZE, DATATYPE, DATA_PATTERN FROM MAIN_MATCHING_CRITERIA" +
							" WHERE RELAX_PARAM = 'N' AND FILE_ID = ?  AND CATEGORY = ? AND REC_SET_ID = ?  ORDER BY MATCH_ID   ";
		logger.info("GET_MATCHING_PARAMS=="+GET_MATCHING_PARAMS);
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCHING_PARAMS , new Object[]{table1_file_id,compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory(),compareBeanObj.getInrec_set_id()},new MatchParameterMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCHING_PARAMS , new Object[]{table2_file_id,compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory(),compareBeanObj.getInrec_set_id()},new MatchParameterMaster2());
				
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
						table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(i).getStMatchTable1_DatePattern()+" ')";
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+compareBeanObj.getStMergeCategory()+"_"+stFile1_Name 
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
							/*table1_condition = "REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':')";*/
							table1_condition = "LPAD(REPLACE( TRIM(SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(i).getStMatchTable1_charSize()+"))"+" , ':'),6,'0')";
							
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
						table1_condition = " TO_DATE( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'"+match_Headers1.get(i).getStMatchTable1_DatePattern()+"')";						
					}
					else if(match_Headers1.get(i).getStMatchTable1_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+compareBeanObj.getStMergeCategory()+
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
			logger.info(" table1_condition IS "+table1_condition);
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
						table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM "
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
							/*table2_condition = "REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':')";*/
							table2_condition = "LPAD(REPLACE( TRIM(SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(i).getStMatchTable2_charSize()+"))"+" , ':'),6,'0')";
							
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
						table2_condition = " TO_DATE( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";							
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+compareBeanObj.getStMergeCategory()+
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
		
		logger.info(" table2_condition IS "+table2_condition);
		logger.info("FINALLY CONDITION IS "+condition);
		
		
	
		
		//CHANGES MADE BY INT5779 ON 20 FEB AS BATCHING FOR UPDATE IS TAKING TIME
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
		//MODIFIED BY INT8624 AS THIS TABLE WAS CAUSING ERROR IN RUPAY AND VISA SIMULTANEOUS PROCESSING
		String tableName = "RESPCODE_CBS_DATA_"+compareBeanObj.getStMergeCategory();
		logger.info("Inside ttum records of "+compareBeanObj.getStMergeCategory() +" table name is "+tableName); // UNRECON_RESPCODE_CBS_DATA
		if(getJdbcTemplate().queryForObject("SELECT count (*) FROM tab WHERE tname  = '"+tableName+"'", new Object[] {},Integer.class) == 0)
		{
			getJdbcTemplate().execute("CREATE TABLE "+tableName+" AS SELECT * FROM TEMP_"+compareBeanObj.getStMergeCategory()+"_CBS");
			getJdbcTemplate().execute("ALTER TABLE "+tableName+" ADD DCRS_REMARKS VARCHAR2 (100 BYTE)");
		}
		//ends here
		String INSERT_QUERY = "INSERT INTO "+tableName+" (DCRS_REMARKS,"+insert_cols+") ";
		
		//CHANGES MADE FOR VALUE_DATE
		JOIN1_QUERY = "SELECT DISTINCT (T1.DCRS_REMARKS || ' ('||T2.RESPCODE||')'),"+Selec_cols+" FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"
				+compareBeanObj.getStCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE "+
						" T1.FILEDATE = '"+compareBeanObj.getStFile_date()+"'"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-1'" +
				" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";
		
		//GET TTUM CONDITION
		String cond1 = getTTUMCondition(table1_file_id);
		String cond2 = getTTUMCondition(table2_file_id);
		
		logger.info("cond1 IS "+cond1);
		
		logger.info("cond2 IS "+cond2);
	
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
		
		//CHANGES MADE BY INT5779 ON 20 FEB AS BATCHING FOR UPDATE IS TAKING TIME
		
		
		INSERT_QUERY = INSERT_QUERY + JOIN1_QUERY;
		
		logger.info("INSERT QUERY "+INSERT_QUERY);
		
		getJdbcTemplate().execute(INSERT_QUERY);
		
		
		
		
		
		
		//DELETING ALREADY EXISTING RECORDS FROM SETTLEMENT TABLE
	//	int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table1_file_id), compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory()},Integer.class);
		int reversal_id = getJdbcTemplate().queryForObject("SELECT MIN(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (table1_file_id), compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory()},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , table1_file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());

		String knock_condition = getCondition(knockoff_Criteria);
		
		
		//ADDED BY INT5779 ON 08/03/2018
				//check for duplicate in this temporary table and delete one entry
		//CHANGES DONE AS PER SAMEER SIR FOR TRAN_ID IN RECON RECORDS DUPLICATE
				/*String REMOVE_DUPLICATES = "DELETE FROM UNRECON_RESPCODE_CBS_DATA OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM UNRECON_RESPCODE_CBS_DATA OS2 WHERE "+
								" OS2.DCRS_REMARKS LIKE '%"+compareBeanObj.getStMergeCategory()+"-UNRECON-1"+"%' AND ("+
								knock_condition+")) AND OS1.DCRS_REMARKS LIKE '%"+compareBeanObj.getStMergeCategory()+"-UNRECON-1"+"%'";*/
		try
		{
			String REMOVE_DUPLICATES = "DELETE FROM "+tableName+" OS1 WHERE ROWID != (SELECT MAX(ROWID) FROM "+tableName+" OS2 WHERE "+
				" OS2.DCRS_REMARKS LIKE '%"+compareBeanObj.getStMergeCategory()+"-UNRECON-1"+"%' AND ("+
				knock_condition+") AND OS1.TRAN_ID = OS2.TRAN_ID) AND OS1.DCRS_REMARKS LIKE '%"+compareBeanObj.getStMergeCategory()+"-UNRECON-1"+"%'";
				logger.info("REMOVE DUPLICATE QUERY IS "+REMOVE_DUPLICATES);
		
				getJdbcTemplate().execute(REMOVE_DUPLICATES);
		
		//ENDS HERE
		}
		catch(Exception e)
		{
			demo.logSQLException(e, "CompareRupayDaoImpl.TTUMRecords");
			logger.error(" error in CompareRupayDaoImpl.TTUMRecords", new Exception(" CompareRupayDaoImpl.TTUMRecords",e));
			throw e;
		}
				
		String DELETE_QUERY = "DELETE FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_CBS OS1 WHERE OS1.DCRS_REMARKS = '"
						+compareBeanObj.getStMergeCategory()+"-UNRECON-1' AND OS1.FILEDATE = '"
						+compareBeanObj.getStFile_date()+"' " +
						"AND EXISTS(SELECT * FROM "+tableName+" OS2 WHERE OS2.FILEDATE = '"+compareBeanObj.getStFile_date()
						+"' AND "+knock_condition+")";

		logger.info("delete QEURY IS "+DELETE_QUERY);
		getJdbcTemplate().execute(DELETE_QUERY);
		
		//NOW INSERT INTO SETTLEMENT TABLE 
		String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+compareBeanObj.getStCategory()+"_CBS (DCRS_REMARKS,"+insert_cols
						+") SELECT DCRS_REMARKS,"+insert_cols+" FROM "+tableName+" WHERE FILEDATE = '"+compareBeanObj.getStFile_date()+"'";
		logger.info("SETTLEMENT_INSERT QUERY IS "+INSERT_RECORDS);
		getJdbcTemplate().execute(INSERT_RECORDS);
		logger.info("drop table query is "+"DROP TABLE "+tableName);
		
		getJdbcTemplate().execute("DROP TABLE "+tableName);
		logger.info("dropped table ");
		
		
		
		/*JOIN1_QUERY = "SELECT "+Selec_cols+" FROM SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"
				+compareBeanObj.getStCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE "+
						" TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()+"'"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-1'" +
				" AND t2.DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"'";*/
		//get failed records for table 1
		
		//getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
	//	getFailedRecords(JOIN1_QUERY, table2_file_id , stFile2_Name,stFile1_Name,table1_file_id,compareBeanObj);
		//getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
		logger.info("***** CompareRupayDaoImpl.TTUMRecords End ****");
		
}
	catch(Exception e)
	{
		demo.logSQLException(e, "CompareRupayDaoImpl.TTUMRecords");
		logger.error(" error in  CompareRupayDaoImpl.TTUMRecords", new Exception(" CompareRupayDaoImpl.TTUMRecords",e));
		throw e;
	}
	finally
	{
		getConnection().close();
	}
	
}

/*public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,CompareBean compareBeanObj)throws Exception
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
	
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory()},Integer.class);
		logger.info("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		logger.info("knockoff criteria "+knockoff_Criteria.size());

		//CREATE CONDITION USING KNOCKOFF CRITERIA 
		List<String> update_queries = new ArrayList<>();
		int batch = 0;
		while(rset.next())
		{
			
			//logger.info("WHILE STARTS");
			update_condition = "";
			reversal_condition = "";
			stFinal_cond = "";
		
				//code for inserting in settlement table
				String UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
						+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()
							+"','DD/MM/YYYY') AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' ";
			String UPDATE_QUERY  = "";
			
			
				UPDATE_QUERY = "UPDATE SETTLEMENT_"+compareBeanObj.getStCategory()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"
							+compareBeanObj.getStMergeCategory()+"-UNRECON-1 ("+rset.getString("RESPCODE")+
						")' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+compareBeanObj.getStFile_date()
						+"' AND DCRS_REMARKS = '"+compareBeanObj.getStMergeCategory()+"-UNRECON-1' ";
			
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), compareBeanObj.getStCategory()+"_"+compareBeanObj.getStSubCategory()},Integer.class);
				//logger.info("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
				//logger.info("KNOCKOFF CRITERIA SIZE "+knockoff_Criteria1.size());
				
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
				
				//logger.info("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				//logger.info("UPDATE QUERY IS "+UPDATE_QUERY);
				
				update_queries.add(UPDATE_QUERY);
				
				if(update_queries.size() == 100)
				{
					batch++;
					logger.info("EXECUTED BATCH "+batch);
					String[] queries = new String[update_queries.size()];
					queries = update_queries.toArray(queries);
					getJdbcTemplate().batchUpdate(queries);
					update_queries.clear();
				}
			
		}
		if(update_queries.size() > 0)
		{
			String[] queries = new String[update_queries.size()];
			queries = update_queries.toArray(queries);
			getJdbcTemplate().batchUpdate(queries);
		}
		logger.info("while completed");
		
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
		logger.info("Exception in getFailedRecords "+e);
	}

	
}*/


}
