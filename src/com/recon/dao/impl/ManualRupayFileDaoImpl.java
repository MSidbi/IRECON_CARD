package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.ADD_RECORDS;
import static com.recon.util.GeneralUtil.CREATE_RECON_TABLE;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_COLS_COUNT;
import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_CRITERIA;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_PARAMS;
import static com.recon.util.GeneralUtil.GET_MANUAL_PARAM;
import static com.recon.util.GeneralUtil.GET_MAN_ID;
import static com.recon.util.GeneralUtil.GET_MAN_MATCHING;
import static com.recon.util.GeneralUtil.GET_MATCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_RECON_CONDITION;
import static com.recon.util.GeneralUtil.GET_REVERSAL_DETAILS;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_REVERSAL_PARAMS;
import static com.recon.util.GeneralUtil.GET_TABLE_NAME;
import static com.recon.util.GeneralUtil.GET_TTUM_PARAMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.ManualRupayFileDao;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;



@Component
public class ManualRupayFileDaoImpl extends JdbcDaoSupport implements ManualRupayFileDao{



	public int FilterRecords(FilterationBean filterbean)throws Exception
	{
		String tableCols = "MAN_CONTRA_ACCOUNT VARCHAR (100BYTE),CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE, SEG_TRAN_ID NUMBER";
		
		//String col = "CREATEDDATE, CREATEDBY,SEG_TRAN_ID";
		String settlement_col = "CREATEDDATE, CREATEDBY,FILEDATE,SEG_TRAN_ID,DCRS_REMARKS";
		String SETTLEMENT_INSERT = "";
		try
		{



			List<FilterationBean> search_params = new ArrayList<>();
			search_params = filterbean.getSearch_params();
			String query = "", condition = "";
			String Table_name = "";

			System.out.println("file name is "+filterbean.getStFile_Name());
			//1. GET THE HEADERS FROM DB
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory() },Integer.class);
			//System.out.println("file Id is "+file_id);

			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);

			//ADDING CODE FOR GETTING RAW TABLE NAME FROM DB
			Table_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);

			//CHECK WHETHER TABLE IS PRESENT ----- IF NOT THEN CREATE IT
			String CHECK_CATEGORY_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'MANUAL_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"'";

			int tableExist = getJdbcTemplate().queryForObject(CHECK_CATEGORY_TABLE, new Object[] { },Integer.class);


			String[] columns = stFile_headers.split(",");

			if(tableExist == 0)
			{
				for(int m = 0 ;m <columns.length; m++)
				{
					tableCols = tableCols +","+ columns[m]+" VARCHAR (100 BYTE) ";
				}

				//create MAN RAW table
				String Create_query = "CREATE TABLE MANUAL_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" ("+tableCols+")";
				System.out.println("CREATE QUERY IS "+Create_query);
				PreparedStatement pstable = getConnection().prepareStatement(Create_query);
				pstable.execute();

				pstable = null;

			}
			//CHECK FOR SETTLEMENT TABLE
			//String CHECK_SETTLEMENT = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+filterbean.getStFile_Name()+"'";
			String CHECK_SETTLEMENT = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+"'";

			int tableSettlementExist = getJdbcTemplate().queryForObject(CHECK_SETTLEMENT, new Object[] { },Integer.class);
			settlement_col = settlement_col+","+stFile_headers;

			String[] settlement_cols =stFile_headers.split(",");
			//String[] columns = stFile_headers.split(",");

			if(tableSettlementExist == 0)
			{
				String settle_col = "";
				if(filterbean.getStFile_Name().equals("CBS"))
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
				String Create_query = "CREATE TABLE SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+settle_col+")";
				PreparedStatement pstable = getConnection().prepareStatement(Create_query);
				pstable.execute();

				pstable = null;


			}

			//PREPARING PROPER CONDITION
			String temp_param;

			//changes MADE FOR NOT = CONDITION 
			for(int i = 0; i<search_params.size();i++){
				FilterationBean filterBeanObj = new FilterationBean();
				filterBeanObj = search_params.get(i);
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
						if(i == (search_params.size()-1))
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
						if(i == (search_params.size()-1))
						{
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
						}
						else
						{
							condition = condition + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
						}
					}

				}

				for(int j= (i+1); j <search_params.size(); j++)
				{
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(search_params.get(j).getStSearch_header()))
					{

						if(search_params.get(j).getStSearch_padding().equals("Y"))
						{
							if((search_params.get(j).getStSearch_Condition().trim()).equals("="))
							{
								condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
										search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
										"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
							}
							else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
										search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
										"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
							}
							else
							{
								if(j==(search_params.size()-1))
								{	
									/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
									condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									condition = condition + ", '"+search_params.get(j).getStSearch_pattern().trim()+"' ";
								}

							}
						}
						else
						{
							if((search_params.get(j).getStSearch_Condition().trim()).equals("="))
							{
								condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
										search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
							}
							else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
										search_params.get(j).getStSearch_Condition().trim()+" "+
										"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";

							}
							else
							{
								if(j==(search_params.size()-1))
								{
									condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"')";
								}
								else
								{
									condition = condition + " , '" +search_params.get(j).getStSearch_pattern().trim()+"' ";
								}

							}
						}



						i = j;
					}

				}
				//System.out.println("i value is "+i);
				if(i != (search_params.size())-1)
				{
					if(search_params.get(i).getStSearch_Condition().equals("!="))
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

				//	System.out.println("condition is "+condition);
			}


			//query = "SELECT * FROM "+Table_name;
			//(CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers+")
			query = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),'"+filterbean.getInseg_tran_id()+"',"
					+stFile_headers+" FROM "+Table_name;
			//String GET_MAN_RECORDS ="SELECT * FROM "+Table_name+" WHERE (PART_ID = 2 AND TO_CHAR(NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))" ;
			//CREATEDDATE, CREATEDBY,FILEDATE,SEG_TRAN_ID,DCRS_REMARKS
			/*String SETTLEMENT_QUERY = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),'"
					+filterbean.getInseg_tran_id()+"','MAN-"+filterbean.getStMerger_Category()+"',"+stFile_headers+" FROM "+Table_name;*/

			if(!condition.equals(""))
			{
				query = query + " WHERE "+condition +" AND PART_ID = 2 AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"'";
				//"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" ;

				/*	SETTLEMENT_QUERY = SETTLEMENT_QUERY + " WHERE "+condition +" AND PART_ID = 2 AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+
					"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" ;		*/

				//query = query + " WHERE "+condition;

			}
			else
			{
				query = query + " WHERE PART_ID = 2 AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"'";
				//+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";

				/*SETTLEMENT_QUERY = SETTLEMENT_QUERY + " WHERE PART_ID = 2 AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()
					+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/

			}
			System.out.println("get raw data query is  "+query);
			//	System.out.println("get raw data FOR SETTLEMENT TABLE query is  "+SETTLEMENT_QUERY);

			//2.. get data from raw table


			/*conn = null;
			pstmt = null;*/


			//3. Insert the data in batch obtained from RAW table into category_filename Table
			/*ADD_RECORDS = "INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers+") " +
					"VALUES(sysdate,'"+filterbean.getStEntry_by()+
						"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),?";
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+settlement_col + ") VALUES(SYSDATE,'"+filterbean.getStEntry_by()
								+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),?,'MAN-"+filterbean.getStMerger_Category()+"'";
			for(int i = 1 ; i<= columns.length; i++)
			{

					ADD_RECORDS = ADD_RECORDS + ",?";
					SETTLEMENT_INSERT = SETTLEMENT_INSERT + ",?";

			}
			ADD_RECORDS = ADD_RECORDS+")";
			SETTLEMENT_INSERT = SETTLEMENT_INSERT + ")";
			 */

			ADD_RECORDS = "INSERT INTO MANUAL_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers+") "+
					query;

			/*SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+settlement_col + ") "+
								SETTLEMENT_QUERY;*/


			System.out.println("ADD RECORDS QUERY  is "+ADD_RECORDS);
			System.out.println("SETTLEMENT INSERT QUERY "+SETTLEMENT_INSERT);

			System.out.println("start time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			getJdbcTemplate().execute(ADD_RECORDS);
			//	getJdbcTemplate().execute(SETTLEMENT_INSERT);
			//inserting filtered table in category table and then in settlement table
			//insertBatch(ADD_RECORDS,filtered_records,columns,filterbean.getInseg_tran_id());
			//insertBatch(SETTLEMENT_INSERT, settlement_resultset, settlement_cols, filterbean.getInseg_tran_id());
			System.out.println("End time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));

			if(filterbean.getStFile_Name().equals("CBS"))
			{
				//CategorizeProxyAccount(filterbean);


			}
			return 1;

		}
		catch(Exception e)
		{
			//throw new Exception(e);
			System.out.println("EXCEPTION IN FILTERATION "+e);
			return 0;
		}
		
		
	}

	@Override
	public void compareManualFile(List<String> tables,CompareSetupBean setupBean)
	{
		String stFile1_Name , stManFile_Name;
		PreparedStatement pstmt = null;
		Connection conn;
		ResultSet MatchedRecords;
		//CompareBean compareBean = new CompareBean();
		try
		{
			
			/*String[] a = tables.get(0).split("_");
			String[] b  =tables.get(1).split("_");//MANUAL FILE
*/			/*stFile1_Name = a[1];
			stManFile_Name = b[1];*/
			stFile1_Name = tables.get(0);
			stManFile_Name = tables.get(1);
			String GET_FROM_RAW = "";
			String table1_condition = "";
			String table2_condition = "";
			String condition = "";
			String REMAINING_RECORDS = "";
		//	String columns = "CREATEDDATE, CREATEDBY,FILEDATE";
			String columns = "";
			String tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE), FILEDATE DATE, MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
			String INSERT_QUERY = "";
			String temp_param = "";
			String task = "";
			
			String CHECK_TABLE = "";
			int tableExist = 0;
			int table1_file_id = 0;
			if(stFile1_Name.equals("SWITCH") && setupBean.getStSubCategory().equals("SURCHARGE"))
			{
				if(setupBean.getCategory().equals("RUPAY"))
				{
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name, setupBean.getCategory(),"DOMESTIC"},Integer.class);
				}
				else if(setupBean.getCategory().equals("VISA"))
				{
					table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name, setupBean.getCategory(),"ISSUER" },Integer.class);
				}
			}
			else
			{
				table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name, setupBean.getCategory(),setupBean.getStSubCategory() },Integer.class);				
			}
			int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stManFile_Name, setupBean.getCategory(),setupBean.getStSubCategory() },Integer.class);
			
			/*System.out.println("table 1 file id "+table1_file_id);
			System.out.println("table 2 file id "+table2_file_id);
			System.out.println("category is "+a[1]);
			*/
	//1.............................. CREATE THE CONDITION FOR MAN RECORDS AND FETCH THEM FROM RAW ...................................................................................................
			List<Integer> id_list = getJdbcTemplate().query(GET_MAN_ID , new Object[] {table2_file_id}, new ManualId());
			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
			columns = stFile_headers;
			conn = getConnection();
			for(int i = 0; i<id_list.size() ; i++)
			{
				
				condition = "";
			//	System.out.println("man id is "+id_list.get(i));
				List<ManualFileBean> manual_filter1 = getJdbcTemplate().query(GET_MANUAL_PARAM, new Object[] {table2_file_id, id_list.get(i)}, new ManualParameterMaster());
				
				//GET CONDITION FOR SPLITING THE MANUAL RECORDS FOR FILE ID 2
				for(int j = 0; j<manual_filter1.size();j++){
					ManualFileBean manualFileBeanObj = new ManualFileBean();
					manualFileBeanObj = manual_filter1.get(j);
					//temp_param = filterBeanObj.getStSearch_header().trim();
					temp_param = manualFileBeanObj.getStFile_header();
					if((manualFileBeanObj.getStPadding().trim()).equals("Y"))
					{
						if((manualFileBeanObj.getStCondition().trim()).equals("="))
						{
							condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+"'"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						else if((manualFileBeanObj.getStCondition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+
									"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%' ";
						}
						else
						{
							condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";					
						}
					}
					else
					{
						if(manualFileBeanObj.getStCondition().equals("="))
						{
							condition = condition + "(TRIM(t2."+manualFileBeanObj.getStFile_header()+") "+manualFileBeanObj.getStCondition().trim()+" '"+
									manualFileBeanObj.getStSearch_Pattern().trim()+"'";
						}
						else if(manualFileBeanObj.getStCondition().equalsIgnoreCase("like"))
						{
							condition = condition + "(TRIM(t2."+manualFileBeanObj.getStFile_header()+") "+manualFileBeanObj.getStCondition().trim()+" "+
										"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%'";
						}
						else
						{
							condition = condition + "(TRIM(t2."+manualFileBeanObj.getStFile_header()+") "+" NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						
					}
					
					for(int k= (j+1); k <manual_filter1.size(); k++)
					{
						//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
						if(temp_param.equals(manual_filter1.get(k).getStFile_header()))
						{
								
							if(manual_filter1.get(k).getStPadding().equals("Y"))
							{
								if((manual_filter1.get(k).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR SUBSTR(TRIM(t2." + manual_filter1.get(k).getStFile_header()+") , "+manual_filter1.get(k).getStStart_charpos()+","+
											manual_filter1.get(k).getStChar_size()+") "+manual_filter1.get(k).getStCondition().trim()+ 
										"'"+manual_filter1.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter1.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR SUBSTR(TRIM(t2." + manual_filter1.get(k).getStFile_header()+") , "+manual_filter1.get(k).getStStart_charpos()+","+
											manual_filter1.get(k).getStChar_size()+") "+manual_filter1.get(k).getStCondition().trim()+
											"'%"+manual_filter1.get(k).getStSearch_Pattern().trim()+"%'";
								}
								else
								{
									if(j==(manual_filter1.size()-1))
									{	
										/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
											search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
										condition = condition + ", '"+manual_filter1.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + ", '"+manual_filter1.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							else
							{
								if((manual_filter1.get(j).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR TRIM(t2." + manual_filter1.get(k).getStFile_header()+") "+
											manual_filter1.get(k).getStCondition().trim()+" '"+manual_filter1.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter1.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR TRIM(t2." + manual_filter1.get(k).getStFile_header()+") "+
											manual_filter1.get(k).getStCondition().trim()+" "+
											"'%"+manual_filter1.get(k).getStSearch_Pattern().trim()+"%'";
									
								}
								else
								{
									if(j==(manual_filter1.size()-1))
									{
										condition = condition + " , '" +manual_filter1.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + " , '" +manual_filter1.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							
							
								j = k;
						}
						
					}
					//System.out.println("i value is "+i);
					/*if(j != (manual_filter1.size())-1)
					{
						condition = condition +" ) AND ";
					}
					else
						condition = condition +")";*/
					
					if((manual_filter1.get(j).getStCondition().trim()).equals("!="))
					{
						condition = condition + " ) ) AND  ";
					}
					else
						condition = condition + " ) AND ";
					
				//	System.out.println("condition is "+condition);
				}
				
				//CONDITION FOR FILE ID 1
				List<ManualFileBean> manual_filter2 = getJdbcTemplate().query(GET_MANUAL_PARAM, new Object[] {table1_file_id, id_list.get(i)}, new ManualParameterMaster());
				String condition1 = "";
				for(int j = 0; j<manual_filter2.size();)
				{
					ManualFileBean manualFileBeanObj = new ManualFileBean();
					manualFileBeanObj = manual_filter2.get(j);
					//temp_param = filterBeanObj.getStSearch_header().trim();
					temp_param = manualFileBeanObj.getStFile_header();
					if((manualFileBeanObj.getStPadding().trim()).equals("Y"))
					{
						if((manualFileBeanObj.getStCondition().trim()).equals("="))
						{
							condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+"'"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						else if((manualFileBeanObj.getStCondition().trim()).equalsIgnoreCase("like"))
						{
							condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+manualFileBeanObj.getStCondition().trim()+
									"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%' ";
						}
						else
						{
							condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj.getStFile_header()+"),"+manualFileBeanObj.getStStart_charpos()+","+
									manualFileBeanObj.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";					
						}
					}
					else
					{
						if(manualFileBeanObj.getStCondition().equals("="))
						{
							condition = condition + "(TRIM(t1."+manualFileBeanObj.getStFile_header()+") "+manualFileBeanObj.getStCondition().trim()+" '"+
									manualFileBeanObj.getStSearch_Pattern().trim()+"'";
						}
						else if(manualFileBeanObj.getStCondition().equalsIgnoreCase("like"))
						{
							condition = condition + "(TRIM(t1."+manualFileBeanObj.getStFile_header()+") "+manualFileBeanObj.getStCondition().trim()+" "+
										"'%"+manualFileBeanObj.getStSearch_Pattern().trim()+"%'";
						}
						else
						{
							condition = condition + "(TRIM(t1."+manualFileBeanObj.getStFile_header()+") "+" NOT IN ('"+manualFileBeanObj.getStSearch_Pattern().trim()+"' ";
						}
						
					}
					
					for(int k = (j+1); k <manual_filter2.size();k++)
					{
						if((temp_param.equals(manual_filter2.get(k).getStFile_header()))
							&& manualFileBeanObj.getStCondition().equals(manual_filter2.get(k).getStCondition()))
						{
							if(manual_filter2.get(k).getStPadding().equals("Y"))
							{
								if((manual_filter2.get(k).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR SUBSTR(TRIM(t1." + manual_filter2.get(k).getStFile_header()+") , "+manual_filter2.get(k).getStStart_charpos()+","+
											manual_filter2.get(k).getStChar_size()+") "+manual_filter2.get(k).getStCondition().trim()+ 
										"'"+manual_filter2.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter2.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR SUBSTR(TRIM(t1." + manual_filter2.get(k).getStFile_header()+") , "+manual_filter2.get(k).getStStart_charpos()+","+
											manual_filter2.get(k).getStChar_size()+") "+manual_filter2.get(k).getStCondition().trim()+
											"'%"+manual_filter2.get(k).getStSearch_Pattern().trim()+"%'";
								}
								else
								{
									if(j==(manual_filter2.size()-1))
									{	
									
										condition = condition + ", '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + ", '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							else
							{
								if((manual_filter2.get(j).getStCondition().trim()).equals("="))
								{
									condition = condition + " OR TRIM(t1." + manual_filter2.get(k).getStFile_header()+") "+
											manual_filter2.get(k).getStCondition().trim()+" '"+manual_filter2.get(k).getStSearch_Pattern().trim()+"'";
								}
								else if((manual_filter2.get(k).getStCondition().trim()).equalsIgnoreCase("like"))
								{
									condition = condition + " OR TRIM(t1." + manual_filter2.get(k).getStFile_header()+") "+
											manual_filter2.get(k).getStCondition().trim()+" "+
											"'%"+manual_filter2.get(k).getStSearch_Pattern().trim()+"%'";
									
								}
								else
								{
									if(j==(manual_filter2.size()-1))
									{
										condition = condition + " , '" +manual_filter2.get(k).getStSearch_Pattern().trim()+"')";
									}
									else
									{
										condition = condition + " , '" +manual_filter2.get(k).getStSearch_Pattern().trim()+"' ";
									}
									
								}
							}
							manual_filter2.remove(k);
							k = k-1;
							
						}
					}
					
					if((manual_filter2.get(j).getStCondition().trim()).equals("!="))
					{
						condition = condition + " ) ) AND  ";
					}
					else
						condition = condition + " ) AND ";
					
				//	System.out.println("condition is "+condition);
					if(!condition1.equals(""))
					{
							condition = condition + condition1 + " ) AND";
					}
					condition1 = "";
					manual_filter2.remove(j);
				}
				
				
				
				if(condition.equals(""))
				{
					condition = condition + " AND ";
				}
				
				
				
				//System.out.println("Condition is "+condition);
				
				//PREPARE COMPARE CONDITION
				List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table1_file_id},new MatchParameterMaster1());
				List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table2_file_id},new MatchParameterMaster2());
				
				String stTable1_raw_table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table1_file_id}, String.class);
				String stTable2_raw_table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table2_file_id}, String.class);
				
				
				//prepare compare condition
				/*for(int l = 0; l<match_Headers1.size() ; l++)
				{
					//CHECKING PADDING FOR TABLE 1
					if(match_Headers1.get(l).getStMatchTable1_Padding().equals("Y"))
					{
						if(match_Headers1.get(l).getStMatchTable1_Datatype() != null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								table1_condition = "SUBSTR( TO_NUMBER( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",'999999999.99')"+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
												match_Headers1.get(l).getStMatchTable1_charSize()+")";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(l).getStMatchTable1_DatePattern()+" ')";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM "+stFile1_Name+"_DATA";
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM "+stTable1_raw_table;
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
									table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")"+" , ':')";
									
								}
								else
								{
									table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")"+","+6+",'0')";
									
								}
								
								
							}
						}
						else
						{
							table1_condition = "SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(l).getStMatchTable1_charSize()+")";
		
						}	
					}
					else
					{	
						if(match_Headers1.get(l).getStMatchTable1_Datatype()!=null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								table1_condition = " TO_NUMBER( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",'9999999999.99')";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = " TO_DATE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",'"+match_Headers1.get(l).getStMatchTable1_DatePattern()+"')";						
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								//String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM "+stFile1_Name+"_DATA";
								String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM "+stTable1_raw_table;
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
									table1_condition = "REPLACE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+" , ':')";
									
								}
								else
								{
									table1_condition = " LPAD( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+6+",'0')";
									
								}
								
							}
							
						}
						else
						{
							table1_condition = " t1."+match_Headers1.get(l).getStMatchTable1_header().trim();
		
						}	
					}
					
					//CHECKING PADDING FOR TABLE 2
					if(match_Headers2.get(l).getStMatchTable2_Padding().equals("Y"))
					{
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								table2_condition = " SUBSTR( TO_NUMBER( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",'9999999999.99')"+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(l).getStMatchTable2_charSize()+")";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(l).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
								match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" )" +
										" FROM "+stManFile_Name+"_DATA WHERE PART_ID = 2";
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
										match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" )" +
												" FROM "+stTable2_raw_table+" WHERE PART_ID = 2";
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
									table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")"+" , ':')";
									
								}
								else
								{
									table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")"+","+6+", '0')";
									
								}
								
							}
							
						}
						else
						{
							table2_condition = " SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(l).getStMatchTable2_charSize()+")";
		
						}			
						
					}
					else
					{
						//System.out.println("datatype is "+match_Headers2.get(l).getStMatchTable2_Datatype());
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								table2_condition = " TO_NUMBER( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",'9999999999.99')";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_DATE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM "+stManFile_Name+"_DATA" +
													   " WHERE PART_ID = 2";
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM "
													   +stTable2_raw_table+ " WHERE PART_ID = 2";
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
									table2_condition = "REPLACE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+" , ':')";
									
								}
								else
								{
									table2_condition = "LPAD( t2."+match_Headers2.get(l).getStMatchTable2_header()+","+6+",'0')";
									
								}
								
							}
						}
						else
						{
							table2_condition = " t2."+match_Headers2.get(l).getStMatchTable2_header();
		
						}		
					
					}
					
					// PREPARING ACTUAL CONDITION OF BOTH TABLES
					if(l==(match_Headers1.size()-1))
					{
						
						//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header();
						condition = condition +"("+ table1_condition + " = "+table2_condition+")";
						
					}
					else
					{
						//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
						condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
					
					}
					
					
				}*/
				
			//	System.out.println("FINALLY CONDITION IS "+condition);
				
				
				for(int l = 0; l<match_Headers1.size() ; l++)
				{
					//CHECKING PADDING FOR TABLE 1
					if(match_Headers1.get(l).getStMatchTable1_Padding().equals("Y"))
					{
						if(match_Headers1.get(l).getStMatchTable1_Datatype() != null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								table1_condition = "TO_NUMBER(SUBSTR(REPLACE(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",',','')"+","
										+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+	match_Headers1.get(l).getStMatchTable1_charSize()+"))";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = "TO_DATE(SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+")"+", ' "+match_Headers1.get(l).getStMatchTable1_DatePattern()+" ')";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+setupBean.getCategory()+"_"+stFile1_Name 
										+" WHERE  SUBSTR( "+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
										match_Headers1.get(l).getStMatchTable1_charSize()+" ) IS NOT NULL";
								System.out.println("CHECK FORMAT IS "+CHECK_FORMAT);
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
									table1_condition = "REPLACE( SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")"+" , ':')";
									
								}
								else
								{
									/*table1_condition = " LPAD( SUBSTR( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(i).getStMatchTable1_charSize()+")"+","+6+",'0')";*/
									table1_condition = " SUBSTR( LPAD(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",6,'0')"+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
											match_Headers1.get(l).getStMatchTable1_charSize()+")";
									
								}
								
								
							}
						}
						else
						{
							table1_condition = "SUBSTR( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+match_Headers1.get(l).getStMatchTable1_startcharpos()+","+
									match_Headers1.get(l).getStMatchTable1_charSize()+")";
		
						}	
					}
					else
					{	
						if(match_Headers1.get(l).getStMatchTable1_Datatype()!=null)
						{
							if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("NUMBER"))
							{
								//table1_condition = " TO_NUMBER( t1."+match_Headers1.get(i).getStMatchTable1_header().trim()+",'9999999999.99')";
								table1_condition = " TO_NUMBER( REPLACE(t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",',',''))";
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("DATE"))
							{
								table1_condition = " TO_DATE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+",'"+match_Headers1.get(l).getStMatchTable1_DatePattern()+"')";						
							}
							else if(match_Headers1.get(l).getStMatchTable1_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"
										+setupBean.getCategory()+"_"+stFile1_Name 
										+" WHERE "+match_Headers1.get(l).getStMatchTable1_header().trim()+" IS NOT NULL";
								System.out.println("CHECK_ FORMAT IS "+CHECK_FORMAT);
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
									table1_condition = "REPLACE( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+" , ':')";
									
								}
								else
								{
									table1_condition = " LPAD( t1."+match_Headers1.get(l).getStMatchTable1_header().trim()+","+6+",'0')";
									
								}
								
							}
							
						}
						else
						{
							table1_condition = " t1."+match_Headers1.get(l).getStMatchTable1_header().trim();
		
						}	
					}
					
					//CHECKING PADDING FOR TABLE 2
					/*System.out.println("i value is "+i);
					System.out.println("match headers length is "+match_Headers2.size());
					System.out.println("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
					if(match_Headers2.get(l).getStMatchTable2_Padding().equals("Y"))
					{
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								table2_condition = " TO_NUMBER(SUBSTR(REPLACE(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",',','')"+","
												+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+ match_Headers2.get(l).getStMatchTable2_charSize()+"))";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_DATE( SUBSTR(  t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
										 match_Headers2.get(l).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
								match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"
										+setupBean.getCategory()+"_"+stManFile_Name + " WHERE SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
								match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" ) IS NOT NULL";
								System.out.println("CHECK_ FORMAT IS "+CHECK_FORMAT);
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
									table2_condition = "REPLACE( SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")"+" , ':')";
									
								}
								else
								{
									/*table2_condition = " LPAD( SUBSTR( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(i).getStMatchTable2_charSize()+")"+","+6+", '0')";*/
									
									table2_condition = " SUBSTR( LPAD(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",6,'0')"+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
											 match_Headers2.get(l).getStMatchTable2_charSize()+")";
									
								}
								
							}
							
						}
						else
						{
							table2_condition = " SUBSTR( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+","+match_Headers2.get(l).getStMatchTable2_startcharpos()+","+
									 match_Headers2.get(l).getStMatchTable2_charSize()+")";
		
						}			
						
					}
					else
					{
						System.out.println("datatype is "+match_Headers2.get(l).getStMatchTable2_Datatype());
						if(match_Headers2.get(l).getStMatchTable2_Datatype()!=null)
						{
							if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("NUMBER"))
							{
								//table2_condition = " TO_NUMBER( t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+",'9999999999.99')";
								table2_condition = " TO_NUMBER( REPLACE(t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",',',''))";
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("DATE"))
							{
								table2_condition = " TO_DATE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+",'"+match_Headers2.get(l).getStMatchTable2_DatePattern()+"')";							
							}
							else if(match_Headers2.get(l).getStMatchTable2_Datatype().equals("TIME"))
							{
								//check whether the column consists of :
								String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"
										+setupBean.getCategory()+"_"+stManFile_Name
										+" WHERE "+match_Headers2.get(l).getStMatchTable2_header().trim()+" IS NOT NULL";
								System.out.println("CHECK_ FORMAT IS "+CHECK_FORMAT);
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
									table2_condition = "REPLACE( t2."+match_Headers2.get(l).getStMatchTable2_header().trim()+" , ':')";
									
								}
								else
								{
									table2_condition = "LPAD( t2."+match_Headers2.get(l).getStMatchTable2_header()+","+6+",'0')";
									
								}
								
							}
						}
						else
						{
							table2_condition = " t2."+match_Headers2.get(l).getStMatchTable2_header();
		
						}		
					
					}
					
					// PREPARING ACTUAL CONDITION OF BOTH TABLES
					
					if(l==(match_Headers1.size()-1))
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
				
				System.out.println("manual condition is "+condition);
				
				String[] col_names = stFile_headers.trim().split(",");
				
				String COLS1 = "";
				
				for(int j=0;j<col_names.length;j++)
				{
					if(j == col_names.length-1)
					{
						COLS1 = COLS1+"t2."+col_names[j];
					}
					else
					{
						COLS1 = COLS1+"t2."+col_names[j]+",";
					}
				}
				
				
				
				
				
				
				if(!condition.equals(""))
				{
					/*GET_FROM_RAW = "SELECT * FROM "+stManFile_Name+"_DATA t2 , "+stFile1_Name+"_DATA t1 WHERE "+condition
								   +" AND (TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') OR TO_CHAR(T2.NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))" +
								   " AND T1.MSGTYPE = '210'" ;*/
					//CREATEDDATE, CREATEDBY,FILEDATE
					GET_FROM_RAW = "SELECT t1.ACCTNUM,SYSDATE,'"+setupBean.getEntry_by()+"',TO_DATE('"+setupBean.getFileDate()+"','DD/MM/YYYY'),"+COLS1
									+" FROM MANUAL_"+setupBean.getStMerger_Category()+"_"+stManFile_Name+" t2 , "+stTable1_raw_table +" t1 WHERE "+condition
							  // +" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							   +" AND TO_CHAR(t2.FILEDATE ,'DD/MM/YYYY') = '"
							   +setupBean.getFileDate() +"'";
							   //" AND T1.MSGTYPE = '210'";
								//" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
					/*GET_FROM_RAW = "SELECT * FROM "+stManFile_Name+"_DATA t2 , "+stFile1_Name+"_DATA t1 WHERE "+condition
							   +" AND (TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017' OR TO_CHAR(T2.NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')) AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017'";*/
					//			" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				}
				else
				{
					GET_FROM_RAW = "SELECT CONTRA_ACCOUNT,SYSDATE,'"+setupBean.getEntry_by()+"',TO_DATE('"+setupBean.getFileDate()+",'DD/MM/YYYY'),"+columns
									+" FROM MANUAL_"+setupBean.getStMerger_Category()+"_"+stManFile_Name+" t2 , "+stTable1_raw_table+" t1 WHERE" 
							    	//+" TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							    	+" AND TO_CHAR(t2.FILEDATE,'DD/MM/YYYY') = '"
							    	+setupBean.getFileDate()+"'";
							    	//+" AND T1.MSGTYPE = '210'" ;
							    	//" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
				}
				
				System.out.println("GETTING RECORDS QUERY "+GET_FROM_RAW);
				
				
				
				pstmt = conn.prepareStatement(GET_FROM_RAW);
				MatchedRecords = pstmt.executeQuery();	
				
		//----------------------------- INSERT THE OBTAINED RECORDS WITH CONTRA_ACCOUNT INT MANUAL TABLE--------------------------------------------------------------
				//1. create manual table
				
				
				//columns = columns+","+stFile_headers;
				columns = stFile_headers;
				//System.out.println("stfile headers are "+stFile_headers);
				
				
				
				
				for(int m = 0 ;m <col_names.length; m++)
				{
					tableCols = tableCols +","+ col_names[m]+" VARCHAR (100 BYTE) ";
				}
				
			//	System.out.println("TABLE COLS ARE "+tableCols);
				
				//CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'MANUAL_"+stManFile_Name.toUpperCase()+"_DATA'";
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+setupBean.getStMerger_Category()+"_"+stManFile_Name.toUpperCase()+"'";
				
				System.out.println("CHECK TABLE QUERY "+CHECK_TABLE);
				
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			//	System.out.println("table exists is "+tableExist);
				if(tableExist == 0)
				{
					//create MAN RAW table
					String query = "CREATE TABLE "+setupBean.getStMerger_Category()+"_"+stManFile_Name+" ("+tableCols+")";
					System.out.println("CREATE QUERY IS "+query);
					PreparedStatement pstable = conn.prepareStatement(query);
					pstable.execute();
					
					pstable = null;
					
				}
				
				//2. Insert the matched records in the table with man contra account
				
				//INSERT_QUERY = "INSERT INTO "+filterBean.getStMerger_Category()+"_"+stManFile_Name+" (MAN_CONTRA_ACCOUNT,"+columns + ") VALUES (?,SYSDATE,'INT5779',TO_DATE('"+filterBean.getStFile_date()+"','DD/MM/YYYY')";
				INSERT_QUERY = "INSERT INTO "+setupBean.getStMerger_Category()+"_"+stManFile_Name+" (MAN_CONTRA_ACCOUNT,CREATEDDATE, CREATEDBY,FILEDATE,"+columns + ") "
								+ GET_FROM_RAW;
			
				System.out.println("INSERT QUERY : "+INSERT_QUERY);
				/*for(int loop = 0 ; loop < col_names.length; loop++)
				{
					INSERT_QUERY = INSERT_QUERY + ",?";
					
				}
				
				INSERT_QUERY = INSERT_QUERY + ")";*/
				
		//		System.out.println("INSERT QUERY IS "+INSERT_QUERY);
				
				//task = "MATCH";
				//insertBatch(INSERT_QUERY, MatchedRecords, col_names, task);
				getJdbcTemplate().execute(INSERT_QUERY);
				System.out.println("COMPLETED inserting in manual raw table---------------------------------------------------------------------------------");
				
				pstmt = null;
				MatchedRecords = null;
				columns = "";
				tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE, MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
			}
			
	//******************************************* INSERT REMAINING RECORDS IN MANUAL TABLE
			System.out.println("--------------------------------------------INSERTING REMAINING RECORDS---------------------------------------------------------------");
			//GET REMAINING RECORDS FROM MANUAL RAW TABLE AND INSERT IT INTO NEW RAW TABLE
			//CREATEDDATE, CREATEDBY,FILEDATE		
			columns = stFile_headers;
			
			//get KNOCKOFF CRITERIA FROM DB
			//int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (table2_file_id), setupBean.getCategory()+"_"+setupBean.getStSubCategory()},Integer.class);
			List<Integer> reversal_id = getJdbcTemplate().query(GET_REVERSAL_ID, new Object[] { (table2_file_id), setupBean.getCategory()+"_"+setupBean.getStSubCategory()},new ReversalId());
			
			for(Integer rever_id : reversal_id)
			{
				List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { rever_id , table2_file_id}, new KnockOffCriteriaMaster());

				String knockoff_cond = getCondition(knockoff_Criteria);

				REMAINING_RECORDS = "SELECT MAN_CONTRA_ACCOUNT,SYSDATE,'"+setupBean.getEntry_by()+"',TO_DATE('"+setupBean.getFileDate()+"','DD/MM/YYYY'),"+columns+" FROM MANUAL_"
						+setupBean.getStMerger_Category()+"_"+stManFile_Name+" OS1 WHERE " +
						//"TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						"TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'"
						+" AND NOT EXISTS(SELECT * FROM "+setupBean.getStMerger_Category()+"_"+stManFile_Name
						+" OS2 WHERE "+knockoff_cond
						//+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE) " 
						+
						"AND  TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"')";
				//+" AND NVL(CONTRA_ACCOUNT,'!NULL!') NOT IN ('99978000010021')";

				System.out.println("Query to fins remaining records is : "+REMAINING_RECORDS);
				//	columns = columns + ","+stFile_headers;
				columns = stFile_headers;
				String[] col_names = stFile_headers.trim().split(",");

				pstmt = conn.prepareStatement(REMAINING_RECORDS);
				ResultSet remain_records = pstmt.executeQuery();
				task = "UNMATCHED";

				INSERT_QUERY = "INSERT INTO "+setupBean.getStMerger_Category()+"_"+stManFile_Name+" (MAN_CONTRA_ACCOUNT,CREATEDDATE, CREATEDBY,FILEDATE,"+columns + ") "
						+REMAINING_RECORDS;



				/*for(int loop = 0 ; loop < col_names.length; loop++)
			{
				INSERT_QUERY = INSERT_QUERY + ",?";

			}

			INSERT_QUERY = INSERT_QUERY + ")";*/

				System.out.println("QUERY FOR INSERTING REMAINING RECORDS "+INSERT_QUERY);


				getJdbcTemplate().execute(INSERT_QUERY);
				
				//DELETE THESE RECORDS FROM MAN TABLE
				String DELETE_QUERY = "DELETE FROM MANUAL_"+setupBean.getStMerger_Category()+"_"+stManFile_Name+" OS1 WHERE " +
									"TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"' AND EXISTS(SELECT * FROM "+setupBean.getStMerger_Category()+"_"+stManFile_Name
									+" OS2 WHERE "+knockoff_cond+" AND  TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"')";
				
				System.out.println("DELETE QUERY IS "+DELETE_QUERY);
				getJdbcTemplate().execute(DELETE_QUERY);

			}
			
			
			//INSERT MANUAL RECORDS IN SETTLEMENT TABLE
			//CREATEDDATE,CREATEDBY,FILEDATE,
			String GET_CATEGORIZED_DATA = "SELECT SYSDATE,'"+setupBean.getEntry_by()+"',TO_DATE('"+setupBean.getFileDate()+"','DD/MM/YYYY'),'MAN-"+setupBean.getStMerger_Category()+"',"+stFile_headers
						+" FROM "+setupBean.getStMerger_Category()+"_"+stManFile_Name
								+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+setupBean.getFileDate()+"'";
								//+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
			System.out.println("GET_CATEGORIZED_DATA "+GET_CATEGORIZED_DATA);
			
			String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+setupBean.getCategory()+"_"+stManFile_Name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+
									stFile_headers+") "+GET_CATEGORIZED_DATA;
			
			System.out.println("SETTLEMENT INSERT :::: "+SETTLEMENT_INSERT);
			
			
			getJdbcTemplate().execute(SETTLEMENT_INSERT);
			
			
			

			System.out.println("iNSERTED REMAINING RECORDS IN MANUAL RAW TABLE ");
			
	//************************FILTERATION OF RAW DATA***************************************************************************************************************
			
			//if Filteration is completed then perform rest operations
	//************************ KNOCKOFF OF ONUS DATA****************************************************************************************************************		
			/*if(task_completed == 1)
			{
			
				int knockedoff = KnockOffRecords(tables.get(1));
				if(knockedoff == 1)
				{
					//--------------------------------------------------COMPARE LOGIC-------------------------------------------------------------------------------
					List<String> table_list = new ArrayList<>();
					//table_list.add("ONUS_SWITCH");
					table_list.add("ONUS_MANUAL_CBS");
						//CompareRecords(table_list);
				}
				
			}*/
		
			
		}
		catch(Exception e)
		{
			System.out.println("eXCEPTION IS "+e);
			
		}
		
		
	}
	

	private static class KnockOffCriteriaMaster implements RowMapper<KnockOffBean> {

		@Override
		public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			/*while(rs.next())
		{*/
			//System.out.println("header is "+rs.getString("HEADER"));
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
	private static class ManualId implements RowMapper<Integer> {

		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			int man_id = rs.getInt("MAN_ID");
			
			return man_id;
		}
	}
	private static class ManualParameterMaster implements RowMapper<ManualFileBean> {

		@Override
		public ManualFileBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ManualFileBean manualFileBeanObj = new ManualFileBean();
			
			manualFileBeanObj.setStFile_header(rs.getString("FILE_HEADER"));
			manualFileBeanObj.setStPadding(rs.getString("PADDING"));
			manualFileBeanObj.setStStart_charpos(rs.getString("START_CHARPOSITION"));
			manualFileBeanObj.setStChar_size(rs.getString("CHARSIZE"));
			manualFileBeanObj.setStCondition(rs.getString("CONDITION"));
			manualFileBeanObj.setStSearch_Pattern(rs.getString("SEARCH_PATTERN"));
			
			return manualFileBeanObj;
		}
	}
	
	private static class MatchParameterMaster1 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			compareBeanObj.setStMatchTable1_header(rs.getString("FILE_HEADER"));
			compareBeanObj.setStMatchTable1_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable1_startcharpos(rs.getString("START_CHARPOSITION"));
			compareBeanObj.setStMatchTable1_charSize(rs.getString("CHARSIZE"));
			compareBeanObj.setStMatchTable1_DatePattern(rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable1_Datatype(rs.getString("DATATYPE"));
		
			
			return compareBeanObj;
			
			
		}
	}

	private static class MatchParameterMaster2 implements RowMapper<CompareBean> {

		@Override
		public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			CompareBean compareBeanObj = new CompareBean();
			compareBeanObj.setStMatchTable2_header(rs.getString("FILE_HEADER"));
			compareBeanObj.setStMatchTable2_Padding(rs.getString("PADDING"));
			compareBeanObj.setStMatchTable2_startcharpos(rs.getString("START_CHARPOSITION"));
			compareBeanObj.setStMatchTable2_charSize(rs.getString("CHARSIZE"));
			compareBeanObj.setStMatchTable2_DatePattern(rs.getString("DATA_PATTERN"));
			compareBeanObj.setStMatchTable2_Datatype(rs.getString("DATATYPE"));
		
			
			return compareBeanObj;
			
			
		}
	}
	
	public String getCondition(List<KnockOffBean> knockoff_Criteria)
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
						condition = condition + " NVL(SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!')"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value();
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
						/*update_condition = update_condition +"SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") "+ knockoff_Criteria.get(i).getStReversal_condition()+
								knockoff_Criteria.get(i).getStReversal_value();*/
						select_parameters = select_parameters+ " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
					}
					else
					{
						condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') "+knockoff_Criteria.get(i).getStReversal_condition()
									+" "+knockoff_Criteria.get(i).getStReversal_value();
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+ " "+knockoff_Criteria.get(i).getStReversal_value();*/
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";*/
						select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header();
					}
				}
				else
				{
					Update_Headers.add(knockoff_Criteria.get(i));
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " NVL(SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!')"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" NVL(SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!')";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";*/
						select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
					}
					else
					{
						condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" NVL(OS2."+knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!')";
						update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?";
						select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header();
					}
				
					
				}
			}
			else
			{
				if(knockoff_Criteria.get(i).getStReversal_value()!=null)
				{
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " NVL(SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!')"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") = ?"+" AND ";*/
						select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
						+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
					else
					{
						condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') "+knockoff_Criteria.get(i).getStReversal_condition()
									+" "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
						/*update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ?"+" AND ";*/
						select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
				}
				else
				{
					Update_Headers.add(knockoff_Criteria.get(i));
					if(knockoff_Criteria.get(i).getStReversal_padding().equals("Y"))
					{
						condition = condition + " NVL(SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!')"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" NVL(SUBSTR( OS2."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+"),'!NULL!') AND ";
						update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") = ? AND ";
						select_parameters = select_parameters + " SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
					else
					{
						condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') "+knockoff_Criteria.get(i).getStReversal_condition()
								+" NVL(OS2."+knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') AND ";
						update_condition = update_condition +knockoff_Criteria.get(i).getStReversal_header()+" = ? AND ";
						select_parameters = select_parameters + "OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
				
					
				}
				
			}
			
		}
		return condition;
	}
	//ADDING NEW CONDITION FOR CHECKING NOT NULL AS PER SAMEER
	public String checkNULLcondition(List<KnockOffBean> knockoff_Criteria)
	{
		String condition = "";
		try
		{
			
			int i = 0;
			for(KnockOffBean beanObj : knockoff_Criteria)
			{
				if(i == 0)
				{
					condition = " OS1."+beanObj.getStReversal_header()+" IS NOT NULL AND OS2."+beanObj.getStReversal_header()+" IS NOT NULL ";
				}
				else
					condition = condition + " AND OS1."+beanObj.getStReversal_header()+" IS NOT NULL AND OS2."+beanObj.getStReversal_header()+" IS NOT NULL ";
				
				i++;
			}
			System.out.println("IS NOT NULL CONDITION IS "+condition);
		}
		catch(Exception e)
		{
			System.out.println("Exception in checkNULLcondition "+e);
		}
		return condition;
	}


	

public void getReconRecords(FilterationBean filterbean)
	{
		String stFile_headers = "";
		String GET_RECON_RECORDS = "";
		try
		{
			
			
			//GET RECON RECORDS
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(), filterbean.getStCategory(),filterbean.getStSubCategory() },Integer.class);

			/*if(!filterbean.getStCategory().equals("RUPAY"))
			{
				stFile_headers = "SEG_TRAN_ID,"+getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class).trim();
			}
			
			else
			*/{
				stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class).trim();
			}
			
			if(filterbean.getStFile_Name().equals("CBS"))
			{
				stFile_headers = "MAN_CONTRA_ACCOUNT," + stFile_headers;
			}

			
			//ADD THE CATEGORIZED RECORDS IN SETTLEMENT TABLE-------------------- IT IS DONE IN COMPAREMANUAL METHOD
		/*	//CREATEDDATE,CREATEDBY,FILEDATE,
			String GET_CATEGORIZED_DATA = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),'MAN-"+filterbean.getStMerger_Category()+"',"+stFile_headers
						+" FROM "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()
								+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()
								+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
			System.out.println("GET_CATEGORIZED_DATA "+GET_CATEGORIZED_DATA);
			
			String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+
									stFile_headers+") "+GET_CATEGORIZED_DATA;
			
			System.out.println("SETTLEMENT INSERT :::: "+SETTLEMENT_INSERT);
			
			
			getJdbcTemplate().execute(SETTLEMENT_INSERT);
			*/
			
			// GET RECON RECORDS OF PREVIOUS DAY FROM SETTLEMENT TABLE AND INSERT IN ONLINE ONUS TABLE
			//CREATEDDATE,CREATEDBY,FILEDATE,
			
			if(filterbean.getStSubCategory().equals("SURCHARGE"))
			{
				GET_RECON_RECORDS = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),"+stFile_headers
						+" FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" WHERE DCRS_REMARKS = '"+filterbean.getStMerger_Category()+"-UNRECON-"+3+"' " +
						//	" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
						" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" +filterbean.getStFile_date()+"'";

				
			}
			else
			{
				GET_RECON_RECORDS = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),"+stFile_headers
										+" FROM SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
										+" WHERE DCRS_REMARKS = '"+filterbean.getStMerger_Category()+"-UNRECON-"+1+"' " +
										//	" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
										" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" +filterbean.getStFile_date()+"'";
			}
			
				//"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			System.out.println("GET RECON RECORDS QUERY "+GET_RECON_RECORDS);
			//Connection conn = dbconn.getCon();
			//PreparedStatement pstmt = conn.prepareStatement(GET_RECON_RECORDS);
			//ResultSet rset = pstmt.executeQuery();

			String INSERT_QUERY = "INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()
					+" (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+") "+
						GET_RECON_RECORDS;//VALUES (SYSDATE,'INT5779',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')";
			
			System.out.println("INSERT RECON RECORDS QUERY "+INSERT_QUERY);

			System.out.println("START TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
			//insertBatch(INSERT_QUERY, rset, col_names, "");
			getJdbcTemplate().execute(INSERT_QUERY);
			System.out.println("END TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));

			
			
			
			
			// GET MANUAL RECON RECORDS FROM MANUAL ONUS TABLE AND INSERT IT IN ONLINE ONUS TABLE
			/*if(knockOffBean.getStFile_Name().equals("CBS"))
			{
				String GET_MAN_RECON = "SELECT * FROM "+knockOffBean.getStCategory()+"_MANUAL_"+knockOffBean.getStFile_Name()
						+" WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				System.out.println("GET MAN RECORDS QUERY "+GET_MAN_RECON);
				PreparedStatement man_pstmt = conn.prepareStatement(GET_MAN_RECON);
				ResultSet man_rset = man_pstmt.executeQuery();

				INSERT_QUERY = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,"+stFile_headers
						+") VALUES(SYSDATE,'INT5779'";

				for(int i=0;i<col_names.length;i++)
				{
					INSERT_QUERY = INSERT_QUERY + ",?";
				}
				INSERT_QUERY = INSERT_QUERY+")";
				System.out.println("INSERT QUERY FOR MAN RECON RECORDS "+INSERT_QUERY);

				System.out.println("START TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				insertBatch(INSERT_QUERY, man_rset, col_names, "");
				System.out.println("END TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
			}
			else
			{

				String GET_MAN_RECON = "SELECT * FROM "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()
										+" WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				System.out.println("GET MAN RECORDS QUERY "+GET_MAN_RECON);
				PreparedStatement man_pstmt = conn.prepareStatement(GET_MAN_RECON);
				ResultSet man_rset = man_pstmt.executeQuery();
				
				INSERT_QUERY = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" (CREATEDDATE,CREATEDBY,"+stFile_headers
								+") VALUES(SYSDATE,'INT5779'";
				
				for(int i=0;i<col_names.length;i++)
				{
					INSERT_QUERY = INSERT_QUERY + ",?";
				}
				INSERT_QUERY = INSERT_QUERY+")";
				System.out.println("INSERT QUERY FOR MAN RECON RECORDS "+INSERT_QUERY);
				
				System.out.println("START TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				insertBatch(INSERT_QUERY, man_rset, col_names, "");
				System.out.println("END TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
			}*/
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in getReconRecords "+e);
		}
	}

private static class ReversalParameterMaster implements RowMapper<KnockOffBean> {

@Override
public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	/*while(rs.next())
	{*/
	//	System.out.println("header is "+rs.getString("HEADER"));
		KnockOffBean knockOffBean = new KnockOffBean();
	
	knockOffBean.setStReversal_header(rs.getString("HEADER"));
	knockOffBean.setStReversal_value(rs.getString("VALUE"));
	//filterBean.setStSearch_padding(rs.getString("PADDING"));
	//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
	//filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
	//filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));
	
	//search_params.add(filterBean);
//	}
	return knockOffBean;
	
	
}
}
private class ReversalId implements RowMapper<Integer>{
	@Override
	public Integer mapRow(ResultSet rset , int row)throws SQLException
	{
		int id = Integer.parseInt(rset.getString("REVERSAL_ID"));
		return id;
	}
}

//KNOCKOFF 
/*public void KnockOffRecords(FilterationBean filterbean)throws Exception
{
	List<KnockOffBean> reversal_params = new ArrayList<>();
	//reversal_params = knockOffBean.getReversal_params();
	String query = "", condition = "";
			String duplicate1_condition = "";
	String temp_param;
	String columns = "";
	String tableCols = "SEG_TRAN_ID NUMBER,CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE";
	int tableExist = 0;
	String GET1_DUPLICATES1 = "", GET1_DUPLICATES2 ="", PART1_DUPLICATES = "";
	String GET2_DUPLICATES2 = "", GET2_DUPLICATES1 = "";
	Connection conn;
	PreparedStatement pstmt;
	//String cond1 ="";
	try
	{

		//1. GET AND UPDATE TRANSACTIONS TO BE CONSIDERED FOR KNOCKOFF COMPARISON
			// Get File id from db
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(), filterbean.getStSubCategory()},Integer.class);
			//	System.out.println("file Id is "+file_id);
				
				// Get Id from compare Master as it is unique in all compare tables
				//int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), filterbean.getStCategory()+"_"+filterbean.getStSubCategory()},Integer.class);
				List<Integer> reversal_id = getJdbcTemplate().query(GET_REVERSAL_ID, new Object[] { (file_id), filterbean.getStCategory()+"_"+filterbean.getStSubCategory()},new ReversalId());
			//	System.out.println("REVERSAL id is "+reversal_id);

				int loopcount = 0;
				String DELETE_FROM_CLASS1 = "";
				String DELETE_FROM_CLASS2 = "";
			//	for(int reversalid : reversal_id)
				
				// GET REVERSAL_DETAILS
				List<KnockOffBean> reversal_details = getJdbcTemplate().query(GET_REVERSAL_DETAILS, new Object[] { reversal_id , file_id}, new ReversalParameterMaster());
			//	System.out.println("SEARCH PARAMETERS ARE "+GET_REVERSAL_DETAILS);
			//	System.out.println("search list size is "+reversal_details.size());
		
				
				
				//PREPARING PROPER CONDITION for duplicate1 
			//String temp_param;
			//String cond1 ="";
			for(int i = 0; i<reversal_details.size();i++){
				KnockOffBean knockoffBeanObj = new KnockOffBean();
				knockoffBeanObj = reversal_details.get(i);
				temp_param = knockoffBeanObj.getStReversal_header();
				
				duplicate1_condition = duplicate1_condition + "("+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";
				
				for(int j= (i+1); j <reversal_details.size(); j++)
				{
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+reversal_details.get(j).getStReversal_header());
					if(temp_param.equals(reversal_details.get(j).getStReversal_header()))
					{
						duplicate1_condition = duplicate1_condition + " OR " + reversal_details.get(j).getStReversal_header()+" = '"+reversal_details.get(j).getStReversal_value()+"'";
						i = j;
					}
					
				}
				//System.out.println("i value is "+i);
				if(i != (reversal_details.size())-1)
				{
					duplicate1_condition = duplicate1_condition +" ) AND ";
				}
				else
					duplicate1_condition = duplicate1_condition +")";
				
				//System.out.println("condition is "+condition);
			}
			//System.out.println("condition is "+condition);
	
		
		//System.out.println("GET RECORDS QUERY IS "+GET_DUPLICATES1);
		
		
		
		
//********************ends here****************************		
		
		//2. GET THE CONDITION FROM REVERSAL_PARAMETER TABLE -- eg  records with msgtype 430 or 410 criteria
			// i.e with which header and value we have to compare
 
		//GET REVERSAL PARAMETER
		reversal_params = getJdbcTemplate().query(GET_REVERSAL_PARAMS, new Object[] { reversal_id , file_id}, new ReversalParameterMaster());
		
		
		String condition1 = "";
		for(int i = 0; i<reversal_params.size();i++){
			KnockOffBean knockoffBeanObj = new KnockOffBean();
			knockoffBeanObj = reversal_params.get(i);
			temp_param = knockoffBeanObj.getStReversal_header();			
			
			
			condition1 = condition1 + "(OS2."+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";

			for(int j= (i+1); j <reversal_params.size(); j++)
			{
				//System.out.println("CHECK THE VALUE IN J "+j+" value = "+reversal_params.get(j).getStReversal_header());
				if(temp_param.equals(reversal_params.get(j).getStReversal_header()))
				{
					condition1 = condition1 + " OR OS2." + reversal_params.get(j).getStReversal_header()+" = '"+reversal_params.get(j).getStReversal_value()+"'";
					i = j;
				}

			}
			//System.out.println("i value is "+i);
			if(i != (reversal_params.size())-1)
			{
				condition1 = condition1 +" ) AND ";
			}
			else
				condition1 = condition1 +")";

			//System.out.println("now the condition is "+condition1);
		}
		//System.out.println("condition is "+condition1);
				
		
		
		//System.out.println("1. new query is "+GET_DUPLICATES2);
		//condition = "";
		//3. GET PARAMETERS for appending in duplicate2 query for main comparison
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());

	
		//System.out.println("condition is "+condition);
		//System.out.println("update condition is "+update_condition);
		System.out.println("check udate headers ");
		for(int k=0;k<Update_Headers.size();k++)
		{
			System.out.println("header "+k+" : "+Update_Headers.get(k).getStReversal_header());
		}
	//	System.out.println("SELECT PARAMETERS ARE "+select_parameters);
		
		condition = getCondition(knockoff_Criteria);
		
		
		//GET_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 WHERE KNOCKOFF_FLAG = 'N'";
		GET1_DUPLICATES1 = "SELECT * FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS1 ";
		GET2_DUPLICATES1 = "SELECT * FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS2 ";
		//System.out.println("GET RECORDS QUERY IS "+GET_DUPLICATES1);
		System.out.println("duplicate condition1 : "+duplicate1_condition);
		
		if(!duplicate1_condition.equals(""))
		{
			GET1_DUPLICATES1 = GET1_DUPLICATES1 + " WHERE "+duplicate1_condition;
		}
		
		System.out.println("DUPLICATE 1 : "+GET1_DUPLICATES1);
		//create duplicate2 query
				GET1_DUPLICATES2 = "SELECT * FROM "+ filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" OS2 ";
				GET2_DUPLICATES2 = "SELECT * FROM "+ filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" OS1 ";
				
				// we have to update both 210 and 430 or 410 records at the same time
				  if(!condition1.equals(""))
				{
					  GET1_DUPLICATES2 = GET1_DUPLICATES2+" WHERE "+condition1; 
					  GET2_DUPLICATES1 = GET2_DUPLICATES1 + " WHERE "+condition1;
				}
				
		if(!duplicate1_condition.equals(""))
			{
						GET2_DUPLICATES2 = GET2_DUPLICATES2 +" WHERE "+ duplicate1_condition;
			}
			if(!condition.equals(""))
			{
				
				GET1_DUPLICATES2 = GET1_DUPLICATES2 + " AND ("+condition+")";
				GET2_DUPLICATES2 = GET2_DUPLICATES2 + " AND ("+condition +")";
				
			}
			
			
			//System.out.println("2. DUPLICATE 2 QUERY IS "+GET1_DUPLICATES2);
		
		// QUERY FOR GETTING DUPLICATES IN TABLE
			// we have to take 2 queries coz we have to insert both the records eg 210 and 410 or 430 from onus
			PART1_DUPLICATES = GET1_DUPLICATES1 + " AND (OS1.CREATEDDATE = trunc(SYSDATE)" +
					" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"') AND EXISTS ( "
					+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE)" +
							" AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"')";
			String PART2_DUPLICATES = GET2_DUPLICATES1 + " AND ( OS2.CREATEDDATE = trunc(SYSDATE)" +
					" AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"') AND EXISTS ( "
					+GET2_DUPLICATES2+" AND TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
							" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()+"')";
			System.out.println("MANUAL KNOCKED OFF QUERY 1 IS : "+PART1_DUPLICATES);
			System.out.println("MANUAL KNOCKED OFF QUERY 2 IS"+PART2_DUPLICATES);
			
			conn = getConnection();
			pstmt = conn.prepareStatement(PART1_DUPLICATES);
			ResultSet knockoff_records1 = pstmt.executeQuery();
			//settlement resultset
			PreparedStatement settlement_pstmt1 = conn.prepareStatement(PART1_DUPLICATES);
			ResultSet settlement_records1 =settlement_pstmt1.executeQuery();
			System.out.println("got the result set");
			
			PreparedStatement pstmt2 =conn.prepareStatement(PART2_DUPLICATES);
			ResultSet knockoff_records2 = pstmt2.executeQuery();
			//settlement resultset
			PreparedStatement settlement_pstmt2 = conn.prepareStatement(PART2_DUPLICATES);
			ResultSet settlement_records2 =settlement_pstmt2.executeQuery();
			System.out.println("got the second resultset");
			
			*//******INSERT THESE RECORDS IN NEW KNOCKOFF TABLE
			 * 
			 *//*
	//1. create new knockoff table if not created

			
				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				//String stFile_headers = getJdbcTemplate().queryForObject(GET_COLS,new Object[]{file_id}, String.class);
				columns = "SEG_TRAN_ID"+","+stFile_headers;
				//System.out.println("stfile headers are "+stFile_headers);
				if(filterbean.getStFile_Name().equals("CBS"))
				{
					stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
					columns = columns + ",MAN_CONTRA_ACCOUNT";
				}

				String[] col_names = stFile_headers.trim().split(",");

				for(int i=0 ;i <col_names.length; i++)
				{
					tableCols  = tableCols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
				}
				

				//System.out.println("table columns are "+tableCols);
				//System.out.println("columns are "+columns);


				//CECKING WHETHER TABLE IS ALREADY PRESENT
				String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF'";
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				//System.out.println("table exists value is "+tableExist);
				if(tableExist == 0)
				{
					//create temp table
					query = "CREATE TABLE "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF ("+tableCols+")";
					//System.out.println("CREATE QUERY IS "+query);
					PreparedStatement pstmt1 = conn.prepareStatement(query);
					pstmt1.execute();
					System.out.println("Table is Created");
					pstmt1 = null;
					
				}

			//CREATING INSERT QUERY FOR KNOCKOFF TRANSACTIONS
				INSERT_RECORDS = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF (CREATEDDATE,CREATEDBY,"+columns+") VALUES(" +
								  new java.sql.Timestamp(new java.util.Date().getTime())+","+knockOffBean.getStEntry_by()+",?";
				
				col_names = columns.split(",");
				String INSERT_RECORDS = "INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF" +
						" (CREATEDDATE , CREATEDBY ,FILEDATE, "+columns+") VALUES(sysdate,'INT5779',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY')";
				String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()
						+" (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+columns 
						+ " ) VALUES (SYSDATE, 'INT5779' , TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'), 'MAN-"+filterbean.getStMerger_Category()+"-KNOCKOFF'";
		
				System.out.println("insert query is  "+INSERT_RECORDS);
				for(int i = 0; i< col_names.length; i++ )
				{
					INSERT_RECORDS = INSERT_RECORDS + ",?";
					SETTLEMENT_INSERT = SETTLEMENT_INSERT + ",?";
				}
				INSERT_RECORDS = INSERT_RECORDS + ")";
				SETTLEMENT_INSERT = SETTLEMENT_INSERT +")";
				System.out.println("INSERT QUERY IS "+INSERT_RECORDS);
				System.out.println("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);
			
				System.out.println("START TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				updateBatch(INSERT_RECORDS, knockoff_records1, columns);
				updateBatch(SETTLEMENT_INSERT, settlement_records1, columns);
				updateBatch(INSERT_RECORDS, knockoff_records2, columns);
				updateBatch(SETTLEMENT_INSERT, settlement_records2, columns);
				
				System.out.println("END TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				System.out.println("KNOCKOFF COMPLETED");
			
*//***************************** REMOVING UPDATION AS IT IS TIME CONSUMING TASK			
		
		//UPDATING RECORDS OBTAINED AS DUPLICATE
			UPDATE_RECORDS = "UPDATE "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" SET KNOCKOFF_FLAG = 'Y' WHERE ";
			
			if(!update_condition.equals(""))
			{
				UPDATE_RECORDS = UPDATE_RECORDS + update_condition;
			}
			System.out.println("UPDATE KNOCK OFF DATA QUERY IS "+UPDATE_RECORDS);
			
		System.out.println("start time FOR KNOCKINGOFF DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
		//updateBatch(records, knockoff_Criteria);
		updateBatch(records, Update_Headers);
		System.out.println("End time FOR KNOCKING OFF DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
		//System.out.println("DONE");
		 * 
		 *//*
	}
	catch(Exception e)
	{
		System.out.println("EXCEPTION IS "+e);
	}
}*/
public void KnockOffRecords(FilterationBean filterbean)throws Exception
{
	List<KnockOffBean> reversal_params = new ArrayList<>();
	//reversal_params = knockOffBean.getReversal_params();
	String query = "", condition = "";
			
	String temp_param;
	String columns = "";
	String tableCols = "SEG_TRAN_ID NUMBER,CREATEDDATE DATE,FILEDATE DATE, CREATEDBY VARCHAR2(100 BYTE)";
	int tableExist = 0;
	List<KnockOffBean> insert_cols = new ArrayList<>();
	//int reversal_id;
	List<Integer> reversalid = new ArrayList<Integer>();
	String select_cols = "";
	String GET1_DUPLICATES1 = "", GET1_DUPLICATES2 ="", PART1_DUPLICATES = "",PART2_DUPLICATES = "";
	String GET2_DUPLICATES2 = "", GET2_DUPLICATES1 = "";
	try
	{
		
		
		

		//1. GET AND UPDATE TRANSACTIONS TO BE CONSIDERED FOR KNOCKOFF COMPARISON
			// Get File id from db
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory() },Integer.class);
				System.out.println("file Id is "+file_id);

				if(filterbean.getStSubCategory().equals("-"))
				{
					//FOR TAKING KNOCKOFF IN LOOPS
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), filterbean.getStCategory()},new ReversalId());
					// Get Id from compare Master as it is unique in all compare tables
				//	reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), knockOffBean.getStCategory()},Integer.class);
					//System.out.println("REVERSAL id is "+Reversal_id);
				}
				else
				{
					reversalid = getJdbcTemplate().query(GET_REVERSAL_ID,new Object[]{ (file_id), filterbean.getStCategory()+"_"+filterbean.getStSubCategory()},new ReversalId());
				//	reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (knockOffBean.getStCategory()+"_"+knockOffBean.getStSubCategory())},Integer.class);
				}
				
	//looping for MULTIPLE KNOCKOFF CONDITIONS
				int loopcount = 0;
				String DELETE_FROM_CLASS1 = "";
				String DELETE_FROM_CLASS2 = "";
				
				for(int Reversal_id : reversalid)
				{
					loopcount++;
					String duplicate1_condition = "";
					System.out.println("REVERSAL id is "+Reversal_id);
					// GET REVERSAL_DETAILS
					List<KnockOffBean> reversal_details = getJdbcTemplate().query(GET_REVERSAL_DETAILS, new Object[] { Reversal_id , file_id}, new ReversalParameterMaster());
					//	System.out.println("SEARCH PARAMETERS ARE "+GET_REVERSAL_DETAILS);
					//	System.out.println("search list size is "+reversal_details.size());



					//PREPARING PROPER CONDITION for duplicate1 
					//String temp_param;
					//String cond1 ="";
					for(int i = 0; i<reversal_details.size();i++){
						KnockOffBean knockoffBeanObj = new KnockOffBean();
						knockoffBeanObj = reversal_details.get(i);
						temp_param = knockoffBeanObj.getStReversal_header();

						duplicate1_condition = duplicate1_condition + "("+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";

						for(int j= (i+1); j <reversal_details.size(); j++)
						{
							//System.out.println("CHECK THE VALUE IN J "+j+" value = "+reversal_details.get(j).getStReversal_header());
							if(temp_param.equals(reversal_details.get(j).getStReversal_header()))
							{
								duplicate1_condition = duplicate1_condition + " OR " + reversal_details.get(j).getStReversal_header()+" = '"+reversal_details.get(j).getStReversal_value()+"'";
								i = j;
							}

						}
						//System.out.println("i value is "+i);
						if(i != (reversal_details.size())-1)
						{
							duplicate1_condition = duplicate1_condition +" ) AND ";
						}
						else
							duplicate1_condition = duplicate1_condition +")";

						//System.out.println("condition is "+condition);
					}
					//System.out.println("condition is "+condition);


					//System.out.println("GET RECORDS QUERY IS "+GET_DUPLICATES1);




					//********************ends here****************************		

					//2. GET THE CONDITION FROM REVERSAL_PARAMETER TABLE -- eg  records with msgtype 430 or 410 criteria
					// i.e with which header and value we have to compare

					//GET REVERSAL PARAMETER
					reversal_params = getJdbcTemplate().query(GET_REVERSAL_PARAMS, new Object[] { Reversal_id , file_id}, new ReversalParameterMaster());


					String condition1 = "";
					for(int i = 0; i<reversal_params.size();i++){
						KnockOffBean knockoffBeanObj = new KnockOffBean();
						knockoffBeanObj = reversal_params.get(i);
						temp_param = knockoffBeanObj.getStReversal_header();			


						condition1 = condition1 + "(OS2."+knockoffBeanObj.getStReversal_header()+" = '"+knockoffBeanObj.getStReversal_value()+"'";

						for(int j= (i+1); j <reversal_params.size(); j++)
						{
							//System.out.println("CHECK THE VALUE IN J "+j+" value = "+reversal_params.get(j).getStReversal_header());
							if(temp_param.equals(reversal_params.get(j).getStReversal_header()))
							{
								condition1 = condition1 + " OR OS2." + reversal_params.get(j).getStReversal_header()+" = '"+reversal_params.get(j).getStReversal_value()+"'";
								i = j;
							}

						}
						//System.out.println("i value is "+i);
						if(i != (reversal_params.size())-1)
						{
							condition1 = condition1 +" ) AND ";
						}
						else
							condition1 = condition1 +")";

						//System.out.println("now the condition is "+condition1);
					}
					//System.out.println("condition is "+condition1);



					//System.out.println("1. new query is "+GET_DUPLICATES2);
					//condition = "";
					//3. GET PARAMETERS for appending in duplicate2 query for main comparison
					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { Reversal_id , file_id}, new KnockOffCriteriaMaster());
					condition = getCondition(knockoff_Criteria);
					
					//ADDING NOT NULL CONDITION
					String notNullCondition = checkNULLcondition(knockoff_Criteria);
									
					List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()},new ColumnMapper());
					
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



					//GET_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 WHERE KNOCKOFF_FLAG = 'N'";
					GET1_DUPLICATES1 = "SELECT "+select_cols+" FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS1 ";
					String CHECK_COUNT = "SELECT COUNT(*) FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS1 ";
					GET2_DUPLICATES1 = "SELECT "+select_cols+" FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS2 ";
					
					DELETE_FROM_CLASS1 = "DELETE FROM "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" OS1 ";
					DELETE_FROM_CLASS2 = "DELETE FROM "+filterbean.getStMerger_Category() +"_"+ filterbean.getStFile_Name()+" OS2 ";
					//System.out.println("GET RECORDS QUERY IS "+GET_DUPLICATES1);
					System.out.println("duplicate condition1 : "+duplicate1_condition);

					if(!duplicate1_condition.equals(""))
					{
						GET1_DUPLICATES1 = GET1_DUPLICATES1 + " WHERE "+duplicate1_condition;
						DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+ " WHERE "+duplicate1_condition;
						CHECK_COUNT = CHECK_COUNT + " WHERE "+duplicate1_condition;
					}
					
							
					System.out.println("DUPLICATE 1 : "+GET1_DUPLICATES1);
					//create duplicate2 query
					GET1_DUPLICATES2 = "SELECT "+select_cols+" FROM "+ filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" OS2 ";
					GET2_DUPLICATES2 = "SELECT "+select_cols+" FROM "+ filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+" OS1 ";
					String DELETE_QUERY1 = "SELECT * FROM "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF OS2 ";
					String DELETE_QUERY2 = "SELECT * FROM "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF OS1 ";

					// we have to update both 210 and 430 or 410 records at the same time
					if(!condition1.equals(""))
					{
						GET1_DUPLICATES2 = GET1_DUPLICATES2+" WHERE "+condition1; 
						GET2_DUPLICATES1 = GET2_DUPLICATES1 + " WHERE "+condition1;
						DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2+" WHERE "+condition1;								
						DELETE_QUERY1 = DELETE_QUERY1+ " WHERE "+condition1;
					}

					if(!duplicate1_condition.equals(""))
					{
						GET2_DUPLICATES2 = GET2_DUPLICATES2 +" WHERE "+ duplicate1_condition;
						DELETE_QUERY2 = DELETE_QUERY2 + " WHERE "+duplicate1_condition;
					}
					if(!condition.equals(""))
					{

						/*GET1_DUPLICATES2 = GET1_DUPLICATES2 + " AND ("+condition+")";
						GET2_DUPLICATES2 = GET2_DUPLICATES2 + " AND ("+condition +")";
						DELETE_QUERY1 = DELETE_QUERY1 +" AND ("+condition+")";
						DELETE_QUERY2 = DELETE_QUERY2 +" AND ("+condition+")";
*/
						//CHANGES MADE BY INT5779 AS ON 09TH MARCH
						GET1_DUPLICATES2 = GET1_DUPLICATES2 + " AND ("+condition+" AND "+notNullCondition+" )";
						GET2_DUPLICATES2 = GET2_DUPLICATES2 + " AND ("+condition +" AND "+notNullCondition+" )";
						DELETE_QUERY1 = DELETE_QUERY1 +" AND ("+condition+" AND "+notNullCondition+" )";
						DELETE_QUERY2 = DELETE_QUERY2 +" AND ("+condition+" AND "+notNullCondition+" )";

					}


					//System.out.println("2. DUPLICATE 2 QUERY IS "+GET1_DUPLICATES2);

					// QUERY FOR GETTING DUPLICATES IN TABLE
					// we have to take 2 queries coz we have to insert both the records eg 210 and 410 or 430 from onus
					PART1_DUPLICATES = GET1_DUPLICATES1 + " AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					//added BY INT5779 ON 06TH MARCH 2018
					CHECK_COUNT = CHECK_COUNT + " AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					PART2_DUPLICATES = GET2_DUPLICATES1 + " AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+GET2_DUPLICATES2+" AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE))";
					/*DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+" AND (OS1.CREATEDDATE = trunc(SYSDATE)) AND EXISTS ( "+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2+ " AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "
										+GET2_DUPLICATES2+" AND TO_CHAR(OS1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))";*/
					
					DELETE_FROM_CLASS1 = DELETE_FROM_CLASS1+" AND  trunc(OS1.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+DELETE_QUERY1+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE))";
					DELETE_FROM_CLASS2 = DELETE_FROM_CLASS2 +" AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)) AND EXISTS ( "+DELETE_QUERY2+" AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE))";
					System.out.println("KNOCKED OFF QUERY IS : "+PART1_DUPLICATES);
					System.out.println("PART2 QUERY IS "+PART2_DUPLICATES);
					
					System.out.println("DELETE QUERY IS "+DELETE_FROM_CLASS1);
					System.out.println("DELETE QUERY IS "+DELETE_FROM_CLASS2);
					
					
					/**
					 * IF THE COUNT IS 0 THEN DO NOT FIRE DELETE QUERY 
					 */
					int check_count = getJdbcTemplate().queryForObject(CHECK_COUNT,new Object[]{},Integer.class);
					System.out.println("knockoff data count is "+check_count);
					
					//FOR 2ND LOOP APPLY NOT EXISTS IN THE QUERY 
					
					/*if(loopcount == 2)
					{
						String loop2Condi1 = getLoop2Condition(knockoff_Criteria,"OS1");
						String loop2Condi2 = getLoop2Condition(knockoff_Criteria, "OS2");
						  System.out.println("LOOP1 CONDITION IS "+loop2Condi1);
						  System.out.println("LOOP2 CONDITION IS "+loop2Condi2);
						
						String LOOP2_QUERY1 = " SELECT * FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF T1 WHERE "+
											duplicate1_condition+" AND "+ loop2Condi1;
						PART1_DUPLICATES = PART1_DUPLICATES+" AND NOT EXISTS ("+LOOP2_QUERY1+")";
						LOOP2_QUERY1 = " SELECT * FROM "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF T1 WHERE "+
								duplicate1_condition+" AND "+ loop2Condi2;
						
						PART2_DUPLICATES = PART2_DUPLICATES+" AND NOT EXISTS ("+LOOP2_QUERY1+")";
						System.out.println("INSIDE LOOP2 "+PART1_DUPLICATES);
						System.out.println("INSIDE LOOP 2 "+PART2_DUPLICATES);

					}*/
					
					
					

					/*pstmt = getConnection().prepareStatement(PART1_DUPLICATES);
					ResultSet knockoff_records1 = pstmt.executeQuery();
					//settlement resultset
					PreparedStatement settlement_pstmt1 = getConnection().prepareStatement(PART1_DUPLICATES);
					ResultSet settlement_records1 =settlement_pstmt1.executeQuery();
					System.out.println("got the result set");

					PreparedStatement pstmt2 =getConnection().prepareStatement(PART2_DUPLICATES);
					ResultSet knockoff_records2 = pstmt2.executeQuery();
					//settlement resultset
					PreparedStatement settlement_pstmt2 = getConnection().prepareStatement(PART2_DUPLICATES);
					ResultSet settlement_records2 =settlement_pstmt2.executeQuery();
					System.out.println("got the second resultset");*/

					/******INSERT THESE RECORDS IN NEW KNOCKOFF TABLE
					 * 
					 */
					//1. create new knockoff table if not created


					String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
					//String stFile_headers = getJdbcTemplate().queryForObject(GET_COLS,new Object[]{file_id}, String.class);
					columns = "SEG_TRAN_ID"+","+stFile_headers;
					//System.out.println("stfile headers are "+stFile_headers);
					if(filterbean.getStFile_Name().equals("CBS"))
					{
						stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
						columns = columns + ",MAN_CONTRA_ACCOUNT";
					}

					String[] col_names = stFile_headers.trim().split(",");

					for(int i=0 ;i <col_names.length; i++)
					{
						tableCols  = tableCols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
					}


					//System.out.println("table columns are "+tableCols);
					//System.out.println("columns are "+columns);


					//CECKING WHETHER TABLE IS ALREADY PRESENT
					String CHECK_TABLE = "SELECT count (*) FROM tab WHERE upper(tname)  = '"+filterbean.getStMerger_Category().toUpperCase()+"_"+filterbean.getStFile_Name().toUpperCase()+"_KNOCKOFF'";
					tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
					//System.out.println("table exists value is "+tableExist);
					if(tableExist == 0)
					{
						//create temp table
						query = "CREATE TABLE "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF ("+tableCols+")";
						//System.out.println("CREATE QUERY IS "+query);
						PreparedStatement pstmt1 = getConnection().prepareStatement(query);
						pstmt1.execute();
						System.out.println("Table is Created");
						pstmt1 = null;

					}

					//CREATING INSERT QUERY FOR KNOCKOFF TRANSACTIONS
					/*INSERT_RECORDS = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF (CREATEDDATE,CREATEDBY,"+columns+") VALUES(" +
								  new java.sql.Timestamp(new java.util.Date().getTime())+","+knockOffBean.getStEntry_by()+",?";
					 */
					/*INSERT_RECORDS = "INSERT INTO "+knockOffBean.getStMergeCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF (CREATEDDATE , CREATEDBY ,FILEDATE,"+columns+") " +
							"VALUES(sysdate,'"+knockOffBean.getStEntry_by()+"', TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY'),?";
					SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+columns + " ) " +
							"VALUES (SYSDATE, '"+knockOffBean.getStEntry_by()+"',TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY') , '"
							+knockOffBean.getStMergeCategory()+"-KNOCKOFF', ?";*/

					String INSERT_RECORDS = " INSERT INTO "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF ("+select_cols+") ";
					String INSERT_QUERY1 = INSERT_RECORDS+" "+PART1_DUPLICATES;
					String INSERT_QUERY2 = INSERT_RECORDS+" "+PART2_DUPLICATES;
							//"VALUES(sysdate,'"+knockOffBean.getStEntry_by()+"', TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY'),?";
					/*SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+columns + " ) " +
							"VALUES (SYSDATE, '"+knockOffBean.getStEntry_by()+"',TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY') , '"
							+knockOffBean.getStMergeCategory()+"-KNOCKOFF', ?";*/

					/*for(int i = 0; i< col_names.length; i++ )
					{
						INSERT_RECORDS = INSERT_RECORDS + ",?";
						SETTLEMENT_INSERT = SETTLEMENT_INSERT + ",?";
					}
					INSERT_RECORDS = INSERT_RECORDS + ")";
					SETTLEMENT_INSERT = SETTLEMENT_INSERT +")";*/
					//System.out.println("INSERT QUERY IS "+INSERT_RECORDS);
					System.out.println("INSERT QUERY 1 "+INSERT_QUERY1);
					System.out.println("INSERT QUERY 2 "+INSERT_QUERY2);
					
					

					System.out.println("START TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
					getJdbcTemplate().execute(INSERT_QUERY1);
					getJdbcTemplate().execute(INSERT_QUERY2);
					System.out.println("END TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));

					System.out.println("KNOCKOFF COMPLETED");
					
					if(count > 0)
					{
						//delete RECORDS entrIES FROM CLASSIFICATION TABLE
						getJdbcTemplate().execute(DELETE_FROM_CLASS1);
						getJdbcTemplate().execute(DELETE_FROM_CLASS2);
					}
				}
				
				String SETTLEMENT_DATA = "SELECT "+select_cols+",'"+filterbean.getStMerger_Category()+"-KNOCKOFF' FROM "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_KNOCKOFF" +
						" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY')='"+filterbean.getStFile_date()+"'";
				
				String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" ("+select_cols+",DCRS_REMARKS) " +SETTLEMENT_DATA ;
				
				System.out.println("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);
				getJdbcTemplate().execute(SETTLEMENT_INSERT);
			

	}
	catch(Exception e)
	{
		System.out.println("EXCEPTION IS "+e);
	}
}



public void moveUnFilteredData(FilterationBean filterbean)
{
	String CHECK_TABLE = "";
	int tableExist = 0;
	String tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE";
	try
	{
		System.out.println("Entry in moveUnFilteredData() ");
		
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {filterbean.getStFile_Name(),filterbean.getStCategory(),filterbean.getStSubCategory()},Integer.class);
		System.out.println("file Id is "+file_id);
		
		//String stRaw_TableName = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		

		
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name().toUpperCase()+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TEMP_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"("+tableCols+")";
			//System.out.println("CREATE QUERY IS "+query);
			//pstmt = conn.prepareStatement(query);
			getJdbcTemplate().execute(query);
		}

		//CHECKING WHETHER MATCHED RECORDS TABLE IS PRESENT
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+(filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()).toUpperCase()
					+"_MATCHED"+filterbean.getInRec_Set_Id()+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE "+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()+"_MATCHED"+filterbean.getInRec_Set_Id()+" ("+tableCols+", RELAX_PARAM VARCHAR2 (100 BYTE))";
			//System.out.println("CREATE QUERY IS "+query);
			getJdbcTemplate().execute(query);
		}
		
		/*String GET_DATA = "SELECT * FROM "+stRaw_TableName+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterbean.getStFile_date()
							+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
		
		System.out.println("GET DATA "+GET_DATA);*/
		
		String GET_RECON_DATA = "SELECT SYSDATE,'"+filterbean.getStEntry_by()+"',TO_DATE('"+filterbean.getStFile_date()+"','DD/MM/YYYY'),"+stFile_headers+" FROM SETTLEMENT_"
						+filterbean.getStCategory()+"_"+filterbean.getStFile_Name()+" WHERE DCRS_REMARKS = '"+
								filterbean.getStMerger_Category()+"-UNRECON-"+filterbean.getInRec_Set_Id() +
								"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY')='"+filterbean.getStFile_date()+"'";
		
		String INSERT_TEMP_DATA = "INSERT INTO TEMP_"+filterbean.getStMerger_Category()+"_"+filterbean.getStFile_Name()
						+" (CREATEDDATE, CREATEDBY, FILEDATE, "+stFile_headers+")"+GET_RECON_DATA;
		
		
		getJdbcTemplate().execute(INSERT_TEMP_DATA);
		System.out.println("INSERT_TEMP_DATA "+INSERT_TEMP_DATA);
		
		
		
		
	}
	catch(Exception e)
	{
		System.out.println("Exception in moveUnFilteredData "+e);
	}
	
}


public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id)
{
	System.out.println("Entry in moveData() ");
	String tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE";
	//Connection conn;
	PreparedStatement pstmt ;
	ResultSet rset;
	String columns;
	//String condition = "";
	int tableExist = 0;
	String CHECK_TABLE ="";
	String stMatch_param = "";
	int file_id = 0 ;
	String GET_DATA = "";
	String PART2_QUERY = "";
	boolean filtered_records = false;
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
			
			//columns ="SEG_TRAN_ID";
			tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE, FILEDATE DATE ";
			String stFile_name = tables.get(loop);
			System.out.println("File NAME IS "+stFile_name);
			//String a[] = table_name.split("-");
			//String table = a[1]+"_DATA";
		
		//System.out.println("get cols query is "+GET_COLS );
		
		
		if(comparebeanObj.getStSubCategory().equals("SURCHARGE") && stFile_name.equals("SWITCH"))
		{
			if(comparebeanObj.getStCategory().equals("RUPAY"))
			{
				///1. GET THE HEADERS FROM DB
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name,comparebeanObj.getStCategory(),"DOMESTIC"},Integer.class);
				System.out.println("file Id is "+file_id);
			}
			else if(comparebeanObj.getStCategory().equals("VISA"))
			{

				///1. GET THE HEADERS FROM DB
				file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name,comparebeanObj.getStCategory(),"ISSUER"},Integer.class);
				System.out.println("file Id is "+file_id);
			
			}
		}
		else
		{

			///1. GET THE HEADERS FROM DB
			file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory()},Integer.class);
			System.out.println("file Id is "+file_id);
		
		}
				
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		//columns = columns+","+stFile_headers;
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
		
		
		//System.out.println("table columns are "+tableCols);
		//System.out.println("columns are "+columns);
		pstmt = null;
		rset = null;
		
		//CHECKING WHETHER TABLE IS ALREADY PRESENT
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"("+tableCols+")";
			//System.out.println("CREATE QUERY IS "+query);
			//pstmt = conn.prepareStatement(query);
			getJdbcTemplate().execute(query);

			pstmt = null;

		}

		//CHECKING WHETHER MATCHED RECORDS TABLE IS PRESENT
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+(comparebeanObj.getStMergeCategory()+"_"+stFile_name).toUpperCase()+"_MATCHED"+inRec_set_Id+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+inRec_set_Id+" ("+tableCols+", RELAX_PARAM VARCHAR2 (100 BYTE))";
			//System.out.println("CREATE QUERY IS "+query);
			getJdbcTemplate().execute(query);
		}
		if(stFile_name.equals("SWITCH"))
		{
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
					String ALTER_QUERY = "ALTER TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+" ADD DIFF_AMOUNT VARCHAR(100 BYTE)";
							
					getJdbcTemplate().execute(ALTER_QUERY);
				}
			}
		}

//-------------------------------------------------- CHECK IF FILE MATCHED FLAG IS Y
		if(stMatched_flag.equals("Y"))
		{
			//filtered_records= true;
			String select_cols = "";
			if(stFile_name.equals("SWITCH"))
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

				//move recon records in matched table......
				
				String GET_RECON_RECORDS = "SELECT "+select_cols+" FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') ='"+comparebeanObj.getStFile_date()
						+"' AND DCRS_REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_set_Id+"'";
				String ADD_IN_TEMP_TABLE = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+ " ("+select_cols+" )"+
						GET_RECON_RECORDS;
				System.out.println("ADD_IN_MATCH_TABLE "+ADD_IN_TEMP_TABLE);
				getJdbcTemplate().execute(ADD_IN_TEMP_TABLE);
				
				if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
				{
					if(comparebeanObj.getStCategory().equals("RUPAY"))
					{
						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStCategory()+"_DOM_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
								+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}
					else if(comparebeanObj.getStCategory().equals("VISA"))
					{
						GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStCategory()+"_ISS_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
								+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
					}
				}
				else
				{	
					GET_DATA = "SELECT "+select_cols+" FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
							+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				}

				/*if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
				{
					GET_DATA = "SELECT * FROM "+comparebeanObj.getStCategory()+"_DOM"+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
							+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				}
				else
				{	
					GET_DATA = "SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+(inRec_set_Id-1)+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
							+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
				}*/
				/*ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
						"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";*/
				
				ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" ("+select_cols+") "+GET_DATA;
				System.out.println("IN FLAG N ADD_RECORDS IS "+ADD_RECORDS);
				getJdbcTemplate().execute(ADD_RECORDS);

			}
			
		}
		else
		{
			//--------------------------------------------MOVING DATA TO TEMP TABLE--------------------------------------------------------------------------------------

			//CHECK IF FILTERATION AND KNOCKOFF IS DONE FOR THAT FILE IF NOT THEN MOVE THE DATA DIRECTLY FROM RAW TABLE
			String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
			String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");

			if(stFilter_Status.equals("N") && stknockoff_Status.equals("N"))
			{
				
				FilterationBean filterbean = new FilterationBean();
				filterbean.setStCategory(comparebeanObj.getStCategory());
				filterbean.setStSubCategory(comparebeanObj.getStSubCategory());
				filterbean.setStMerger_Category(comparebeanObj.getStMergeCategory());
				filterbean.setStFile_Name(stFile_name);
				filterbean.setInRec_Set_Id(inRec_set_Id);
				filterbean.setStEntry_by(comparebeanObj.getStEntryBy());
				filterbean.setStFile_date(comparebeanObj.getStFile_date());
				moveUnFilteredData(filterbean);
				
				/*
				String settlement_col = "CREATEDDATE, CREATEDBY,FILEDATE,DCRS_REMARKS";
				//CHECK SETTLEMENT TABLE IS PRESENT OR NOT
				String CHECK_SETTLEMENT = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+"'";
				
				int tableSettlementExist = getJdbcTemplate().queryForObject(CHECK_SETTLEMENT, new Object[] { },Integer.class);
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
					PreparedStatement pstable = getConnection().prepareStatement(Create_query);
					pstable.execute();
					
					pstable = null;
					
					
				}
				
				
				//GET DATA FROM RAW TABLE
				GET_DATA = "SELECT * FROM "+stRaw_TableName+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()
						+"' AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";

				System.out.println("FILTERATION AND KNOCKOFF IS NOT DONE FOR THE FILE "+stFile_name);
				System.out.println("NOW GET_DATA QUERY IS "+GET_DATA);

				ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
						"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
				System.out.println("IN FLAG N ADD_RECORDS IS "+ADD_RECORDS);
				
			*/}
			else
			{
				filtered_records= true;
				if(stFile_name.equals("CBS"))
				{
					//**********2. GET THE DATA FROM ORIGINAL TABLE
					//get the condition for getting original transactions from table

					//GET_DATA = "SELECT * FROM "+table_name + " WHERE KNOCKOFF_FLAG = 'N'";
					GET_DATA = "SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name + " OS1 WHERE ";
					System.out.println("GET_DATA QUERY IS "+GET_DATA);



					//GET PARAMETERS FOR CONSIDERING THE RECORDS FOR COMPARE
					stMatch_param = getReconParameters(comparebeanObj,stFile_name,inRec_set_Id);

					if(!stMatch_param.equals(""))
					{
						//GET_DATA = GET_DATA +" AND "+ stMatch_param;
						//GET_DATA = GET_DATA + stMatch_param + "  AND NOT EXISTS ( ";
						GET_DATA = GET_DATA + stMatch_param + " AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE) " +
								"AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' ";//+"' AND NOT EXISTS ( ";
					}


					//System.out.println("PART1 QUERY ::::::::::::::::::::::::: "+GET_DATA);

					//get part 2 of query
					/*int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());


					String part2_condition = getCondition(knockoff_Criteria);

					//System.out.println("PART 2 CONDITION IS "+part2_condition);

					PART2_QUERY = "SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_KNOCKOFF OS2 ";
					if(!part2_condition.equals(""))
					{
						PART2_QUERY = PART2_QUERY + " WHERE ( " + part2_condition + ")";
					}
*/
					//System.out.println("part2 query is "+PART2_QUERY);

					//GET_DATA = GET_DATA + PART2_QUERY + " )";
					/*GET_DATA = GET_DATA + " AND TO_CHAR(OS2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							"AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' )";*/

					System.out.println("GET_DATA QUERY IS "+GET_DATA);

					columns = "SEG_TRAN_ID,"+columns;
					ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
							"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
				}
				else
				{
					//GET RECON DATA FROM SETTLEMENT TABLE
					
					//columns = "SEG_TRAN_ID,CONTRA_ACCOUNT,MAN_CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT,"+columns;
				//	columns = "SEG_TRAN_ID,"+columns;
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
					/*GET_DATA = "SELECT SYSDATE,'"+comparebeanObj.getStEntryBy()+"',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+columns+" FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name
							+" WHERE DCRS_REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_set_Id+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
					*/
					GET_DATA = "SELECT "+select_cols+" FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name
									+" WHERE DCRS_REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_set_Id
									+"' AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"'";
					ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name
							+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'"+comparebeanObj.getStEntryBy()+"'," +
							"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
					
				}
			}
			
		}
		if(filtered_records)
		{
			//conn = dbconn.getCon();
			pstmt = getConnection().prepareStatement(GET_DATA);
			System.out.println("GET DATA QUERY IS "+GET_DATA);
			rset = pstmt.executeQuery();
			//System.out.println("GOT THE UNKNOCKED DATA");
			//. MOVE THE DATA IN TEMP TABLE
			String[] cols = columns.split(",");
			
			
			/*ADD_RECORDS = "INSERT INTO TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779'," +
					"TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";*/

			for(int i = 1 ; i<= cols.length; i++)
			{
				ADD_RECORDS = ADD_RECORDS + ",?";
			}

			ADD_RECORDS = ADD_RECORDS+")";
			System.out.println("QUERY FOR ADDING RECORDS IN TEMP TABLE "+ADD_RECORDS);
			System.out.println("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			insertBatch(ADD_RECORDS,rset, cols);
			System.out.println("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
		}
			//-----------------------------NOW TRUNCATE ONUS AND KNOCKOFF TABLES
			/*String TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);

			 */
		
		System.out.println("MoveData() completed for the file!!!!!!!!!!!!!!!!!!!!!!!! "+stFile_name);
		loop++;
		}
		
	}
	catch(Exception e)
	{
		System.out.println("Exception in movedata is "+e);
	}
	System.out.println("movedata Exit");
	return 1;
	
	
}

/*public List<String> getMatchedFlag(CompareBean comparebeanObj,int inRec_Set_Id)
{
	List<String> matched_flags = new ArrayList<>();
	List<CompareBean> comparebean;
	try
	{
		String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND CATEGORY = ?";
		if(!comparebeanObj.getStSubCategory().equals("-"))
		{
			System.out.println("category is "+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory());
			System.out.println("rec set id is "+inRec_Set_Id);
			
			comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())}, new GetTableDetails());
		}
		else
			comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,comparebeanObj.getStCategory()}, new GetTableDetails());
		
		
		CompareBean compareBean = comparebean.get(0);
		matched_flags.add(compareBean.getStMatched_file1());
		matched_flags.add(compareBean.getStMatched_file2());
		return matched_flags;
	}
	catch(Exception e)
	{
		System.out.println("Exception in getstatus"+e);
		return null;
		
	}
	
}
*/

public List<String> getMatchedFlag(CompareBean comparebeanObj,int inRec_Set_Id)
{
	List<String> matched_flags = new ArrayList<>();
	List<CompareBean> comparebean;
	try
	{
		//String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND RECON_CATEGORY = ?";
		String GET_TABLES = "SELECT * FROM MAIN_RECON_SEQUENCE WHERE REC_SET_ID = ? AND (FILE1_CATEGORY = ? OR FILE2_CATEGORY = ?)";
		if(!comparebeanObj.getStSubCategory().equals("-"))
		{
			System.out.println("category is "+comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory());
			System.out.println("rec set id is "+inRec_Set_Id);
			
			comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory()),(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())}, new GetTableDetails());
		}
		else
			comparebean = getJdbcTemplate().query(GET_TABLES,new Object[]{inRec_Set_Id,comparebeanObj.getStCategory()}, new GetTableDetails());
		
		
		CompareBean compareBean = comparebean.get(0);
		matched_flags.add(compareBean.getStMatched_file1());
		matched_flags.add(compareBean.getStMatched_file2());
		return matched_flags;
	}
	catch(Exception e)
	{
		System.out.println("Exception in getstatus"+e);
		return null;
		
	}
	
}

private static class GetTableDetails implements RowMapper<CompareBean> {

	@Override
	public CompareBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		CompareBean comparebeanObj = new CompareBean();
		
		comparebeanObj.setStTable1(rs.getString("FILE1"));
		comparebeanObj.setStTable2(rs.getString("FILE2"));
		//comparebeanObj.setStCategory(rs.getString("CATEGORY"));
		comparebeanObj.setStMatched_file1(rs.getString("FILE1_MATCHED"));
		comparebeanObj.setStMatched_file2(rs.getString("FILE2_MATCHED"));
		return comparebeanObj;
		
		

	}
}
public String getStatus(CompareBean comparebeanObj,String stFile_name,String stProcess)
{
	int file_id = 0;
	try
	{
		String GET_STATUS = "SELECT "+stProcess+" FROM MAIN_FILESOURCE WHERE FILEID = ?";
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
		
		String chFilter_Status = getJdbcTemplate().queryForObject(GET_STATUS, new Object[] {file_id}, String.class);
		return chFilter_Status;
		
	}
	catch(Exception e)
	{
		System.out.println("Exception in getstatus"+e);
		return "N";
		
	}
	
}

public String getReconParameters(CompareBean comparebeanObj,String stfile_name,int inRec_set_Id)
{
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
		//	String a[] = sttable_Name.split("-"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stfile_name,comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
			System.out.println("file id is "+file_id);

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
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
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
				//System.out.println("i value is "+i);
				if(i != (compare_cond.size())-1)
				{
					stcompare_con = stcompare_con +" ) AND ";
				}
				else
					stcompare_con = stcompare_con +")";
				
			//	System.out.println("condition is "+condition);
			
			}

			System.out.println("compare condition is "+stcompare_con);
			
			
	}
	catch(Exception e)
	{
		System.out.println("eXCEPTION in getReconParameters IS "+e);
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

public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)throws Exception
{
	//System.out.println("MOVE TO RECON");
	int loop = 0,tableExist = 0;
	String RECON_RECORDS_PART1 = "", recon_columns = "", RECON_INSERT = "", RECON_RECORDS_PART2 = "", RECON_RECORDS = "";
	String CHECK_TABLE = "";
	String stFile_name= "";
	PreparedStatement psrecon = null, settlement_pstmt= null;
	ResultSet rs = null,settlement_set = null;
	Connection con;
	String[] col_names={""};
	String SETTLEMENT_INSERT = "";
	try
	{
		System.out.println("MOVETORECON STARTS HERE ---------------------------------------------------------------------------------------- ");
		//GET MATCHED FLAGS.....
		List<String> stMatched_Flags = getMatchedFlag(comparebeanObj, inRec_Set_id);
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
				//System.out.println("check table "+CHECK_TABLE);
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				
				if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
				{
					if(comparebeanObj.getStCategory().equals("RUPAY")){
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
				}
				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				
				if(stFile_name.equals("CBS"))
				{
					stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
				}
				else if(stFile_name.equals("SWITCH"))
				{
					stFile_headers = stFile_headers +", MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT";
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
					CREATE_RECON_TABLE = "CREATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id +"( "+tab_cols +" )";
					getJdbcTemplate().execute(CREATE_RECON_TABLE);

				}
				recon_columns = "CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
				String col_headers = "SEG_TRAN_ID,"+stFile_headers;
				col_names = col_headers.split(",");
				
				// CHECK IF FILTERATION AND KNOCKOFF FLAG IS ON
				String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
				String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");
				
				
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
							"AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND NOT EXISTS";

					RECON_RECORDS_PART2 = "( SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+inRec_Set_id+" OS2 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							"AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND ";

				if(stFilter_Status.equals("Y") && stknockoff_Status.equals("Y"))
				{
					int reversal_id = 0;
					if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
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
						reversal_id = getJdbcTemplate().queryForObject("SELECT MIN(REVERSAL_ID) FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ?", new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
					}
					
					//System.out.println("REV ID IS "+reversal_id);
					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());


					String compare_cond = getCondition(knockoff_Criteria);
					//System.out.println("COMPARE CONDITION IS "+compare_cond);

					
					RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
				}
				else
				{
					//GET MATCHING CRITERIA'S AS KNOCKOFF CRITERIA IS NOT AVAILABLE
					List<KnockOffBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())
														,inRec_Set_id},new MatchCriteriaMaster());
					
					String compare_cond = getCondition(match_Headers1);
					
					System.out.println("compare condition is "+compare_cond);
					
					RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
					
				}
				System.out.println("RECON_RECORDS IS "+RECON_RECORDS);
				con = getConnection();
				psrecon = con.prepareStatement(RECON_RECORDS);
				rs = psrecon.executeQuery();

				settlement_pstmt = con.prepareStatement(RECON_RECORDS);
				settlement_set = settlement_pstmt.executeQuery();
				
				//DELETE UNRECON RECORDS FROM TABLE FOR AVOIDING DUPLICATE ENTRIES

				String DELETE_PREV_RECORDS = "DELETE FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name
						+" WHERE DCRS_REMARKS = '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"'" +
										//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
										" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" +comparebeanObj.getStFile_date()+"'";
					//"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			
				System.out.println("DELETE PREV RECORDS FROM SETTLEMENT "+DELETE_PREV_RECORDS);
			
				getJdbcTemplate().execute(DELETE_PREV_RECORDS);
			
				System.out.println("DELETED RECORDS OF PREV DAY");
			
				
				
				
				//3. *************** INSERT RECORDS IN RECON TABLE
				
				RECON_INSERT = "INSERT INTO RECON_"+comparebeanObj.getStMergeCategory()+"_"
						+stFile_name+inRec_Set_id+"( "+Selec_cols +" ) ";
						//VALUES (SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
				
				String RECON_DATA = RECON_INSERT+ RECON_RECORDS;
				System.out.println("RECON_DATA QUERY IS "+RECON_DATA);
				
				
				SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" ( DCRS_REMARKS,"+Selec_cols+") "+
							"SELECT '"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"',"+Selec_cols+" FROM RECON_"
								+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id;
				
				System.out.println("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);
				
				/*RECON_INSERT = "INSERT INTO RECON_"+comparebeanObj.getStMergeCategory()+"_"
						+stFile_name+inRec_Set_id+"( "+recon_columns +" ) VALUES (SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
				SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+col_headers+") VALUES(SYSDATE,'INT5779',TO_DATE('"
						+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+"'"+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"'";*/
/*
				for(int i = 0 ;i<col_names.length; i++)
				{

					RECON_INSERT = RECON_INSERT + ",?" ;					
					SETTLEMENT_INSERT = SETTLEMENT_INSERT +",?";
				}
				RECON_INSERT = RECON_INSERT +")";
				SETTLEMENT_INSERT = SETTLEMENT_INSERT +")";*/
				//System.out.println("now the columns are "+recon_columns);
				//	String[] recon_cols = recon_columns.split(",");
				System.out.println("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);

				//--------------TRUNCATE RECON TABLE
				String TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+inRec_Set_id;

				/*	PreparedStatement truncate_ps = null;
					truncate_ps = con.prepareStatement(TRUNCATE_QUERY);
					truncate_ps.executeQuery();*/
			/*}
			else
			{


				//String[] a = table_name.split("_");
				//1. *******CHECK FOR RECON TABLE
				CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name.toUpperCase()+"'";
				//System.out.println("check table "+CHECK_TABLE);
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				//System.out.println("table exists value is "+tableExist);
				//if table is not present then create it

				if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
				{
				//GET_TEMP_COLS = "SELECT COLUMN_NAME,DATA_TYPE FROM user_tab_cols WHERE table_name=UPPER('TEMP_"+table_name+"')";
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),"DOMESTIC" },Integer.class);
				}
				else
				{
					file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile_name , comparebeanObj.getStCategory(),comparebeanObj.getStSubCategory() },Integer.class);
				}
				//System.out.println("file Id is "+file_id);

				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				//List<CompareBean> sttemp_cols = getJdbcTemplate().query(GET_TEMP_COLS, new Object[]{}, new TableColumnsMapper());

				if(stFile_name.equals("CBS"))
				{
					stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
				}
				else if(stFile_name.equals("SWITCH"))
				{
					stFile_headers = stFile_headers +", MAN_CONTRA_ACCOUNT,CONTRA_ACCOUNT,CBS_AMOUNT,DIFF_AMOUNT";
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



					CREATE_RECON_TABLE = "CREATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name +"( "+tab_cols +" )";
					getJdbcTemplate().execute(CREATE_RECON_TABLE);

				}
				recon_columns = "CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
				String col_headers = "SEG_TRAN_ID,"+stFile_headers;
				col_names = col_headers.split(",");
				
				
				// CHECK IF FILTERATION AND KNOCKOFF FLAG IS ON
				String stFilter_Status = getStatus(comparebeanObj,stFile_name,"FILTERATION");
				String stknockoff_Status = getStatus(comparebeanObj,stFile_name,"KNOCKOFF");
				
				
					//2. *************** GET RECORDS FROM TEMP TABLE
					//RECON_RECORDS = "SELECT * FROM TEMP_"+table_name+" WHERE MATCHING_FLAG = 'N'";
					RECON_RECORDS_PART1 = "SELECT * FROM TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name+" OS1 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							"AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND NOT EXISTS";

					RECON_RECORDS_PART2 = "( SELECT * FROM "+comparebeanObj.getStMergeCategory()+"_"+stFile_name+"_MATCHED"+inRec_Set_id+" OS2 WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
							"AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND ";

					//	System.out.println("file id is "+file_id);
					//	System.out.println("a[0] = "+a[0]  );
				if(stFilter_Status.equals("Y") && stknockoff_Status.equals("Y"))
				{
					int reversal_id;
					if(stFile_name.equals("SWITCH") && comparebeanObj.getStSubCategory().equals("SURCHARGE"))
					{
						reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_DOMESTIC")},Integer.class);
					}
					else
					{
						reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), (comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())},Integer.class);
					}
					//System.out.println("REV ID IS "+reversal_id);
					List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());


					String compare_cond = getCondition(knockoff_Criteria);
					//System.out.println("COMPARE CONDITION IS "+compare_cond);

					RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
				}
				else
				{
					//GET MATCHING CRITERIA'S AS KNOCKOFF CRITERIA IS NOT AVAILABLE
					List<KnockOffBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{file_id,(comparebeanObj.getStCategory()+"_"+comparebeanObj.getStSubCategory())
														,inRec_Set_id},new MatchCriteriaMaster());
					
					String compare_cond = getCondition(match_Headers1);
					
					System.out.println("compare condition is "+compare_cond);
					
					RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
					
				}
				System.out.println("RECON_RECORDS IS "+RECON_RECORDS);
				con = getConnection();
				psrecon = con.prepareStatement(RECON_RECORDS);
				rs = psrecon.executeQuery();

				settlement_pstmt = con.prepareStatement(RECON_RECORDS);
				settlement_set = settlement_pstmt.executeQuery();

				

				//3. *************** INSERT RECORDS IN RECON TABLE
				RECON_INSERT = "INSERT INTO RECON_"+comparebeanObj.getStMergeCategory()+"_"
						+stFile_name+"( "+recon_columns +" ) VALUES (SYSDATE,'INT5779',TO_DATE('"+comparebeanObj.getStFile_date()+"','DD/MM/YYYY')";
				SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+col_headers+") VALUES(SYSDATE,'INT5779',TO_DATE('"
						+comparebeanObj.getStFile_date()+"','DD/MM/YYYY'),"+"'"+comparebeanObj.getStMergeCategory()+"-RECON-"+inRec_Set_id+"'";

				for(int i = 0 ;i<col_names.length; i++)
				{

					RECON_INSERT = RECON_INSERT + ",?" ;					
					SETTLEMENT_INSERT = SETTLEMENT_INSERT +",?";
				}
				RECON_INSERT = RECON_INSERT +")";
				SETTLEMENT_INSERT = SETTLEMENT_INSERT +")";
				//System.out.println("now the columns are "+recon_columns);
				//	String[] recon_cols = recon_columns.split(",");
				System.out.println("SETTLEMENT INSERT QUERY IS "+SETTLEMENT_INSERT);

				//--------------TRUNCATE RECON TABLE
				String TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name;
				getJdbcTemplate().execute(TRUNCATE_QUERY);
					PreparedStatement truncate_ps = null;
				truncate_ps = con.prepareStatement(TRUNCATE_QUERY);
				truncate_ps.executeQuery();
				
				
				
			}*/
			
			//DELETE RECON RECORDS FROM SETTLEMENT TABLE FOR AVOIDING DUPLICATE ENTRIES
			/*String DELETE_PREV_RECON_RECORDS = "DELETE FROM SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name
										+" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+comparebeanObj.getStFile_date()+"' AND DCRS_REMARKS = '"
										+comparebeanObj.getStMergeCategory()+"-UNRECON-"+inRec_Set_id+"'"; 
			getJdbcTemplate().execute(DELETE_PREV_RECON_RECORDS);*/
			
			
			System.out.println("INSERT CONDITION IS "+RECON_INSERT);
			System.out.println("start time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			getJdbcTemplate().execute(RECON_DATA);
			//insertBatch(RECON_INSERT, rs, col_names);
			System.out.println("End time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			System.out.println("start time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
			//insertBatch(SETTLEMENT_INSERT, settlement_set, col_names);
			getJdbcTemplate().execute(SETTLEMENT_INSERT);
			System.out.println("End time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			recon_columns = ""; 
					
			rs = null;
			psrecon = null;
			
			//UPDATE CREATEDDATE FOR TTUM GENERATED RECORDS AS SYSDATE
			String UPDATE_TTUM = "UPDATE SETTLEMENT_"+comparebeanObj.getStCategory()+"_"+stFile_name
						+" SET CREATEDDATE = TO_DATE(SYSDATE,'DD/MM/YY') WHERE DCRS_REMARKS LIKE '%ONUS-GENERATE-TTUM%'";
			
			getJdbcTemplate().execute(UPDATE_TTUM);
			System.out.println("DATE UPDATED FOR TTUM RECORDS");
			
			
//-----------------------------AS ON 22 JUNE 2017 DO NOT INSERT OR UPDATE THE RAW TABLE FOR RECON RECORDS.				
//-------------------------------------------------- UPDATE RAW TABLE FOR RECON RECORDS--------------------------------------------------------------------------
		/*	col_names = stFile_headers.split(",");
			psrecon = con.prepareStatement(RECON_RECORDS);
			rs = psrecon.executeQuery();
			String stRaw_Table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
			System.out.println("raw table is "+stRaw_Table);
			String INSERT_RAW = "INSERT INTO "+stRaw_Table.toUpperCase()+" (CREATEDDATE,CREATEDBY,NEXT_TRAN_DATE,"+stFile_headers+
								") VALUES(SYSDATE,'INT5779',SYSDATE+1";
			for(int i=0; i<col_names.length ; i++)
			{
				INSERT_RAW = INSERT_RAW + ",?";
			}
			INSERT_RAW = INSERT_RAW + ")";
			System.out.println("insert raw query is "+INSERT_RAW);
			System.out.println("start time FOR INSERTING IN RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(INSERT_RAW, rs, col_names);
			System.out.println("End time FOR INSERTING IN RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
//----------------------------COMMENTED AS UPDATE TAKES ALOT OF TIME				
			//get Raw table name
/*			String stRaw_Table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
			String UPDATE_QUERY ="UPDATE "+stRaw_Table.toUpperCase()+" SET NEXT_TRAN_DATE = TO_CHAR(SYSDATE+1,'DD/MON/YYYY') WHERE ( ";
			System.out.println("cols lenght is "+col_names.length);
			for(int i=0; i<col_names.length ; i++)
			{
				if(i==(col_names.length-1))
					UPDATE_QUERY = UPDATE_QUERY + "NVL("+col_names[i] + ",'!null!') = ?";
				else
					UPDATE_QUERY = UPDATE_QUERY + "NVL("+ col_names[i] + ",'!null!') = ? AND ";
			}
			
			UPDATE_QUERY = UPDATE_QUERY + ")";
			
			System.out.println("UPDATE QUERY "+UPDATE_QUERY);
			System.out.println("start time FOR UPDATING RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
			updateBatch(UPDATE_QUERY, rs, col_names);
			System.out.println("End time FOR UPDATING RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
			
			String DELETE_TEMP = "TRUNCATE TABLE TEMP_"+comparebeanObj.getStMergeCategory()+"_"+stFile_name;
			getJdbcTemplate().execute(DELETE_TEMP);
			
			if(comparebeanObj.getStSubCategory().equals("SURCHARGE"))
			{
				DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inRec_Set_id;
				getJdbcTemplate().execute(DELETE_TEMP);
				
				DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
				getJdbcTemplate().execute(DELETE_TEMP);
				
				if(tables.get(loop).equals("CBS"))
				{
					DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
					getJdbcTemplate().execute(DELETE_TEMP);
					
					DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
					getJdbcTemplate().execute(DELETE_TEMP);
				}
			}
			else
			{
				if(stFilter_Status.equals("Y"))
				{
					DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop);
					getJdbcTemplate().execute(DELETE_TEMP);
				}
				
				if(stknockoff_Status.equals("Y"))
				{
					DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_KNOCKOFF";
					getJdbcTemplate().execute(DELETE_TEMP);
				}
				
				/*DELETE_TEMP = "TRUNCATE TABLE "+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+"_MATCHED"+inRec_Set_id;
				getJdbcTemplate().execute(DELETE_TEMP);*/
				
				DELETE_TEMP = "TRUNCATE TABLE RECON_"+comparebeanObj.getStMergeCategory()+"_"+tables.get(loop)+inRec_Set_id;
				getJdbcTemplate().execute(DELETE_TEMP);
				
			}
			
			loop++;
		
		}
		
	}
	catch(Exception e)
	{
		System.out.println("EXCEPTION IN movetorecon() "+e);
		
	}
	finally
	{
		
	}
	
}

private static class MatchCriteriaMaster implements RowMapper<KnockOffBean> {

	@Override
	public KnockOffBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		/*while(rs.next())
	{*/
		//System.out.println("header is "+rs.getString("HEADER"));
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

public void updateBatch(final String QUERY , final ResultSet rset, String columns){
	PreparedStatement ps;
	//PreparedStatement ps1;
	Connection con;
	ResultSet rs ;
	int flag = 1;
	int batch = 1;
	int record_count = 0;
	
	/*System.out.println("QUERY PASSED IS "+QUERY);
	System.out.println("COLUMNS PASSES ARE "+columns);*/
	try {
		//ps1 = con.prepareStatement(UPDATE_RECORDS);
		ps = getConnection().prepareStatement(QUERY);
		String [] cols = columns.split(",");
		while(rset.next())
		{
			flag++;
			
			
			
			
				//System.out.println("hie");
				/*for(int j = 0; j<knockOff_criteria.size() ; j++)
				{
					System.out.println("HEADER IS "+knockOff_criteria.get(j).getStReversal_header());
					System.out.println("VALUE IS "+rset.getString(knockOff_criteria.get(j).getStReversal_header()));
//					System.out.println("rset.getString(knockOff_criteria.get(j).getStReversal_header()) "+rset.getString(knockOff_criteria.get(j).getStReversal_header()));
					ps.setString((j+1), rset.getString(knockOff_criteria.get(j).getStReversal_header()));
				}*/
				for(int i = 0; i<cols.length ; i++)
				{
					//System.out.println("column name is "+cols[i]);
					ps.setString((i+1), rset.getString(cols[i].trim()));
				}
			
				ps.addBatch();
				
				if(flag == 500)
				{
					flag = 1;
					//System.out.println("BATCH EXECUTION START TIME "+new java.sql.Timestamp(new java.util.Date().getTime()));
					ps.executeBatch();
					///System.out.println("BATCH EXECUTION END TIME "+new java.sql.Timestamp(new java.util.Date().getTime()));
					System.out.println("Executed batch is "+batch);
					batch++;
				}
			
		}
	//	System.out.println("while ended");
		ps.executeBatch();
	//	System.out.println("XECUTION IS COMPLETED");
		
		
	//updating all records	
	/*	ps = con.prepareStatement(UPDATE_RECORDS);
		
		//System.out.println("value is "+rset.getString("MSGTYPE"));
		//int batch = 1;
		while(rset.next())
		{
			flag++;
			//System.out.println("loop no "+flag);
			//UPDATE_RECORDS
			for(int j = 0; j<knockOff_criteria.size() ; j++)
			{
				ps.setString((j+1), rset.getString(knockOff_criteria.get(j).getStReversal_header()));
			}
			
			
			
			ps.addBatch();
			
			if(flag == 20000)
			{
				flag = 1;
			
				ps.executeBatch();
				System.out.println("Executed batch is "+batch);
				batch++;
			}
		}
		ps.executeBatch();*/
		//System.out.println("completed updation");
		
	} catch (DataAccessException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void insertBatch(final String QUERY, final ResultSet rset,final String[] columns){
	PreparedStatement ps;
	Connection con;
	//System.out.println("query is 123 "+QUERY);
	//System.out.println("coulmn size is "+columns.length);
//	trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
	
	//for(int i=0; i<loadBeanList.size(); i += batchSize)
	int flag = 1;
	int batch = 1;
	
	try {
		
		con = getConnection();
		
		ps = con.prepareStatement(QUERY);
		
		//System.out.println("value is "+rset.getString("MSGTYPE"));
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
				//System.out.println("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
				//System.out.println("column value is "+rset.getString(columns[i-1].trim()));
				if(columns[i-1].trim().equals("CBS_AMOUNT"))
				{
					ps.setString((i), rset.getString("AMOUNT"));
				}
				else
				{	
					ps.setString((i), rset.getString(columns[i-1].trim()));
				}
				
			//	value++;
			//	System.out.println("i is "+i);
			}
			ps.addBatch();
			
			if(flag == 500)
			{
				//System.out.println("******** FLAG IS "+flag);
				flag = 1;
			
				ps.executeBatch();
				System.out.println("Executed batch is "+batch);
				batch++;
			}
		}
		ps.executeBatch();
		System.out.println("completed insertion");
		
		
	} catch (DataAccessException | SQLException e) {
	
		System.out.println("INSERT BATCH EXCEPTION "+e);
	}
}
@Override
public void CleanTables(CompareBean comparebeanObj)
{
	try
	{
		if(comparebeanObj.getStCategory().equals("RUPAY"))
		{/*
			String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED1";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_CBS_MATCHED1";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED2";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_RUPAY_MATCHED2";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_SWITCH_MATCHED3";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_CBS_MATCHED3";
			getJdbcTemplate().execute(TRUNCATE_QUERY);		 
						
			TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_DOM_CBS";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_CBS";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
		*/

			if(comparebeanObj.getStSubCategory().equals("SURCHARGE") || comparebeanObj.getStSubCategory().equals("DOMESTIC"))
			{
				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_CBS_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_SWITCH_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_DOM_RUPAY_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_SWITCH_MATCHED3";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_CBS_MATCHED3";
				getJdbcTemplate().execute(TRUNCATE_QUERY);	
				
				TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_SUR_CBS";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_DOM_CBS";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
			}
			else if(comparebeanObj.getStSubCategory().equalsIgnoreCase("INTERNATIONAL"))
			{
				System.out.println("INSIDE MANUAL CLEAN TABLES INTERNATIONAL" +comparebeanObj.getStMergeCategory());
				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_CBS";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH_KNOCKOFF";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_CBS_KNOCKOFF";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_CBS_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);

				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_RUPAY_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_INT_CBS";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
			}
		
			
		}
		else if(comparebeanObj.getStCategory().equals("VISA"))
		{

			String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_SWITCH_MATCHED1";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_CBS_MATCHED1";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_SWITCH_MATCHED2";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_ISS_VISA_MATCHED2";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_SWITCH_MATCHED3";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_SUR_CBS_MATCHED3";
			getJdbcTemplate().execute(TRUNCATE_QUERY);		 
		
			
		}
			
	
	}
	catch(Exception e)
	{
		System.out.println("EXCEPTION IN CLEANTABLES "+e);
	}
}

@Override
public String chkFileUpload(String Category, CompareSetupBean setupBean, String filedate,String subCat) {
	
	
	String msg=null,flg;

	
		
	 System.out.println(setupBean.getInFileId());
	 
	 //modified by int5779
	/*String query="SELECT UPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') =  to_char(to_date('"+filedate+"','dd/mm/yyyy'),'dd/mm/yyyy') "
			+ " AND CATEGORY = '"+Category+"' AND FileId = "+setupBean.getInFileId()+" AND FILE_SUBCATEGORY='"+subCat+"' "; */
	 String query="SELECT MANUPLOAD_FLAG FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') =  to_char(to_date('"+filedate+"','dd/mm/yyyy'),'dd/mm/yyyy') "
				+"AND FileId = "+setupBean.getInFileId(); 
	
	query = " SELECT CASE WHEN exists ("+ query+") then ("+query+") else 'N' end as FLAG from dual"; 
	
	System.out.println(query);
	 
	flg = getJdbcTemplate().queryForObject(query, String.class);
	
		if(flg.equalsIgnoreCase("N")){
			
			msg = "Files are Not Uploaded.";
			return msg;
		}
	
	
	
	
	return msg;
}

private class ColumnMapper implements RowMapper<String>
{
	@Override
	public String mapRow(ResultSet rset, int row)throws SQLException
	{
		return rset.getString("COLUMN_NAME");
	}
}



}
