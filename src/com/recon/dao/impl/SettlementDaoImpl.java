package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_FILE_HEADERS;
import static com.recon.util.GeneralUtil.GET_FILE_ID;
import static com.recon.util.GeneralUtil.GET_KNOCKOFF_CRITERIA;
import static com.recon.util.GeneralUtil.GET_REVERSAL_ID;
import static com.recon.util.GeneralUtil.GET_SETTLEMENT_PARAM;
import static com.recon.util.GeneralUtil.GET_SETTLEMENT_ID;
import static com.recon.util.GeneralUtil.GET_TABLE_NAME;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.getKnockOffCondition;
import static com.recon.util.GeneralUtil.insertBatch;
import static com.recon.util.GeneralUtil.deleteBatch;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.SettlementDao;
import com.recon.model.CashnetSettlementFile;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;
import com.recon.model.SessionModel;
import com.recon.model.SettlementBean;


@Component
public class SettlementDaoImpl extends JdbcDaoSupport implements SettlementDao {


	
@Override	
public void manualReconToSettlement(ManualFileBean manualFileBeanObj)throws Exception
{
	
	//String[] a = table_Name.split("_");
	String table_cols = "CREATEDDATE DATE,CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE, DCR_TRAN_ID NUMBER, DCRS_REMARKS VARCHAR(100 BYTE)";
	String CREATE_QUERY = "";
	String CHECK_TABLE = "";
	String temp_param = "";
	String condition = "";
	PreparedStatement pstmt = null;
	Connection conn = null;
	ResultSet rset = null;
	
	
	try
	{
		
		
		//check if table exist if not then create it 
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {manualFileBeanObj.getStManualFile(),manualFileBeanObj.getStCategory(),manualFileBeanObj.getStSubCategory() },Integer.class);
		System.out.println("File id is "+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		//String Update_cols = stFile_headers;
		stFile_headers = "MAN_CONTRA_ACCOUNT,"+stFile_headers;
		
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+"'";
		int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		
		System.out.println("CHECK TABLE QUERY IS "+CHECK_TABLE);
		//get connection
		conn = getConnection();
		if(tableExist == 0)
		{
			String[] cols = stFile_headers.split(",");
			for(int i = 0 ; i < cols.length ; i++)
			{
				table_cols = table_cols + "," + cols[i] + " VARCHAR(100 BYTE)";
			}

			CREATE_QUERY = "CREATE TABLE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" ("+table_cols+")";
			System.out.println("CREATE TABLE QUERY IS "+CREATE_QUERY);
			pstmt = conn.prepareStatement(CREATE_QUERY);
			rset = pstmt.executeQuery();
			
			pstmt = null;
			rset = null;

		}
		else if(tableExist == 1 && manualFileBeanObj.getStManualFile().equalsIgnoreCase("CBS"))
		{
			boolean CheckForCol = false;
			//now check for the field MAN_CONTRA_ACCOUNT IF NOT PRESENT THEN ALTER TABLE
			List<String> Columns = getJdbcTemplate().query(GET_COLS, new Object[]{"SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_CBS"}, new ColumnsMapper());
			
			for(int i = 0 ;i<Columns.size(); i++)
			{
				if(Columns.get(i).equalsIgnoreCase("MAN_CONTRA_ACCOUNT"))
				{
					CheckForCol = true;
				}
			}
			
			if(!CheckForCol)
			{
				try{
					String ALTER_QUERY = "ALTER TABLE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" ADD MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
					getJdbcTemplate().execute(ALTER_QUERY);
				}
				catch(UncategorizedSQLException sqlException)
				{
					System.out.println("SQLEXCEPTION IS "+sqlException);
				}
			}
		}
		
		
//------------------------------------------ FILTER MANUAL RECON RECORDS USING CRITEIRA PROVIDED IN DOC----------------------------------------------------------
	    
	    
	    List<SettlementBean> settlement_id = getJdbcTemplate().query(GET_SETTLEMENT_ID , new Object[] {file_id}, new SettlementId());
	    
	 for(int id = 0 ; id < settlement_id.size() ; id++)
	 {
		 condition = "";
		 System.out.println("SETTLEMENT ID IS "+settlement_id.get(id).getInSettlement_id());
		 List<FilterationBean> search_params = getJdbcTemplate().query(GET_SETTLEMENT_PARAM, new Object[] {file_id,settlement_id.get(id).getInSettlement_id()}, new SearchParameterMaster());
	    
	//	System.out.println("got the search params"+search_params.size());
		for(int i = 0; i<search_params.size();i++)
		{
			FilterationBean filterBeanObj = new FilterationBean();
			filterBeanObj = search_params.get(i);
			temp_param = filterBeanObj.getStSearch_header().trim();
			//changes made by INT5779 for issue in ONUS as proxy transactions were received while testing
			if(filterBeanObj.getStSearch_pattern().equalsIgnoreCase("NULL"))
			{
				if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("="))
					condition = condition +" ("+ filterBeanObj.getStSearch_header()+" IS NULL "; 
				else
					condition = condition + " ("+ filterBeanObj.getStSearch_header()+" IS NOT NULL ";
			}
			else
			{
				if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
				{


					if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
					{
						condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
					}
					else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
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
					else
					{
						if(filterBeanObj.getStSearch_Datatype()!=null)
						{
							if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
							{
								condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
										filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+filterBeanObj.getStSearch_pattern().trim();
							}
						}
						else
						{
							condition = condition + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
						}

					}


				}
				else
				{
					if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
					{
						condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
					}
					else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
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
					else
					{
						if(filterBeanObj.getStSearch_Datatype()!=null)
						{
							if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
							{	
								condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+
										filterBeanObj.getStSearch_pattern().trim();
							}
						}
						else
						{
							condition = condition + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
									filterBeanObj.getStSearch_pattern().trim()+"'";
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
							if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
										search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
										"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
							}
							else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
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
							else
							{
								if(filterBeanObj.getStSearch_Datatype() != null)
								{
									if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
									{		
										condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
												search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
												search_params.get(j).getStSearch_pattern().trim();
									}
								}
								else
								{
									condition = condition + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
											search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
											"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
								}

							}
						}
						else
						{
							if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
							{
								condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
										search_params.get(j).getStSearch_Condition().trim()+" "+
										"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";

							}
							else if((filterBeanObj.getStSearch_Condition().trim()).equals("!="))
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
							else
							{
								System.out.println("check the datatype "+filterBeanObj.getStSearch_Datatype());
								if(filterBeanObj.getStSearch_Datatype() != null)
								{
									if(filterBeanObj.getStSearch_Datatype().equals("NUMBER"))
									{		
										condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
												search_params.get(j).getStSearch_Condition().trim()+search_params.get(j).getStSearch_pattern().trim();
									}
								}
								else
								{
									condition = condition + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
											search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
								}

							}
						}


						i = j;

					}

				}
			}
			//System.out.println("i value is "+i);
			if(i != (search_params.size())-1)
			{
				condition = condition +" ) AND ";
			}
			else
				condition = condition +")";
			
		//	System.out.println("condition is "+condition);
		}
		
		//System.out.println("Condition "+condition);
		String RECON_CATEGORIZATION = "";
		String DELETE_QUERY = "";
		String stTableName = "";
		
		if(manualFileBeanObj.getStCategory().equals("ONUS") || manualFileBeanObj.getStCategory().equals("AMEX"))
		{
			stTableName = manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
		}
		else
		{
			stTableName = manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStManualFile();
		}
		
		RECON_CATEGORIZATION = "SELECT * FROM "+stTableName+ " OS1 " ;
		DELETE_QUERY = "DELETE FROM "+stTableName + " OS1 ";
	
		if(!condition.equals(""))
		{
			RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
					" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'" ;
					
			
			DELETE_QUERY = DELETE_QUERY +" WHERE "+ condition + " AND TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
					" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'" ;
					
			
		}
		else
		{
			RECON_CATEGORIZATION = RECON_CATEGORIZATION +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
					" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'";
			DELETE_QUERY = DELETE_QUERY +" WHERE TO_CHAR(CREATEDDATE ,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
					" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'";
		}
		
		//String CHECK_SETTLEMENT = "SELECT * FROM SETTLEMENT_"+a[2] +" OS2 ";
		//NO NEED OF KNOCKOFF CONDITION AS WE ARE DELETING THE RECORDS THE SAME TIME
		/*int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), a[0]},Integer.class);
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		*/
		//String knockoffCond = getKnockOffCondition(knockoff_Criteria);
		
		//System.out.println("check here"+knockoffCond);
		
		/*if(!knockoffCond.equals(""))
		{
			CHECK_SETTLEMENT = CHECK_SETTLEMENT + " WHERE "+knockoffCond+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE)";
		}*/
		
		//RECON_CATEGORIZATION = RECON_CATEGORIZATION + "("+ CHECK_SETTLEMENT +")";
		//DELETE_QUERY = DELETE_QUERY + "("+CHECK_SETTLEMENT + ")";
		
		System.out.println("recon categorization QUERY IS "+RECON_CATEGORIZATION);
		
		pstmt = conn.prepareStatement(RECON_CATEGORIZATION);
		ResultSet CIA_GL_RECORDS = pstmt.executeQuery();
		
		
		
		String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile() + " (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+stFile_headers+")" +
							" VALUES (SYSDATE, 'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),'"+manualFileBeanObj.getStMergerCategory()+"-CIA-GL'";
		String[] tab_cols = stFile_headers.split(",");
		for(int count = 0 ; count <tab_cols.length ; count++)
		{
			INSERT_RECORDS = INSERT_RECORDS + ",?";
		}
		
		INSERT_RECORDS = INSERT_RECORDS + ")";
		
		System.out.println("INSERT QUERY IS "+INSERT_RECORDS);
		
		insertBatch(INSERT_RECORDS, CIA_GL_RECORDS, tab_cols, conn);
		
		//DELETE THE RECORDS FROM ONUS TABLE AFTER INSERTION IN SETTLEMENT TABLE
		System.out.println("DELETING RECON CATEGORIZED RECORDS------------------------------------------------------");
		System.out.println("DELETE QUERY IS "+DELETE_QUERY);
		getJdbcTemplate().execute(DELETE_QUERY);
		System.out.println("COMPLETED DELETION--------------------------------------------------------------------");
		
		
		
		
		/*String DELETE_QUERY = "DELETE FROM "+table_Name +" WHERE ";
		
		for(int count = 0 ; count <tab_cols.length ; count++)
		{
			if(count == (tab_cols.length-1))
			{
				DELETE_QUERY = DELETE_QUERY + "NVL("+tab_cols[count] + ",'!null!') = ? ";
			}
			else
				DELETE_QUERY = DELETE_QUERY + "NVL("+tab_cols[count] + ",'!null!') = ? AND ";
		}
		System.out.println("DELETE QUERY IS "+DELETE_QUERY);
		
		PreparedStatement delete_statement = conn.prepareStatement(RECON_CATEGORIZATION);
		ResultSet delete_set = delete_statement.executeQuery();
		
		deleteBatch(DELETE_QUERY, delete_set, tab_cols, conn);
		*/
		
	 }
	 
	 System.out.println("COMPLETED CIA GL--------------------------------------------------------------------------------------------------------------------------------------------------");
	 //insert remaining records from recon table in ONLINE ONUS table
	/* String REMAINING_RECON_RECORDS = "SELECT * FROM "+table_Name +" OS1 WHERE NOT EXISTS " +
	 								" ( SELECT * FROM SETTLEMENT_"+a[3]+" OS2 WHERE ";*/
	 
		/*int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), a[1]},Integer.class);
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		
		String knockoffCond = getKnockOffCondition(knockoff_Criteria);
		
		REMAINING_RECON_RECORDS = REMAINING_RECON_RECORDS + knockoffCond + ")";
*/
	 
	 //INSERTING REMAINING RECORDS IN CATEGORY TABLE IN CASE OF ONUS AND AMEX
	 String stTableName = "";
	 
	 if (manualFileBeanObj.getStCategory().equals("ONUS") || manualFileBeanObj.getStCategory().equals("AMEX"))
	 {

		 stTableName = manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
		 
		 String REMAINING_RECON_RECORDS = "SELECT * FROM "+stTableName+" WHERE TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
				 " AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'";
		 System.out.println("REMANING RECON RECORDS "+REMAINING_RECON_RECORDS);

		 PreparedStatement remainingStatement = conn.prepareStatement(REMAINING_RECON_RECORDS);
		 ResultSet remaining_set = remainingStatement.executeQuery();


		 String INSERT_REMAINING_RECORDS = "INSERT INTO "+manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStManualFile()+" (CREATEDDATE, CREATEDBY,FILEDATE,"+stFile_headers
				 +") VALUES(SYSDATE, 'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY')";
		 String[] tab_cols = stFile_headers.split(",");
		 for(int count = 0 ; count < tab_cols.length; count++)
		 {
			 INSERT_REMAINING_RECORDS = INSERT_REMAINING_RECORDS + ",?";
		 }
		 INSERT_REMAINING_RECORDS = INSERT_REMAINING_RECORDS + ")";

		 System.out.println("INSERT REMAINING RECORDS "+INSERT_REMAINING_RECORDS);

		 insertBatch(INSERT_REMAINING_RECORDS, remaining_set, tab_cols, conn);

		 System.out.println("COMPLETED INSERTING IN ONLINE ONUS TABLE!!!!!!");
	 }
	 
	 // TRUNCATE MANUAL ONUS TABLE
	// String TRUNCATE_QUERY = "TRUNCATE TABLE "+ manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile();
	// getJdbcTemplate().execute(TRUNCATE_QUERY);
	 
	// System.out.println("TRUNCATED TABLE "+manualFileBeanObj.getStCategory()+"_MANUAL_"+manualFileBeanObj.getStManualFile());
	 
	 
//------------------------------------------AS ON 22 JUNE 2017 NO NEED TO UPDATE RAW TABLE-----------------------------------------	 
//**********************************************************update raw table***************************	 
/*	PreparedStatement psrecon = conn.prepareStatement(REMAINING_RECON_RECORDS);
		ResultSet rs = psrecon.executeQuery();
		//get Raw table name
		String stRaw_Table = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
		String UPDATE_QUERY ="UPDATE "+stRaw_Table.toUpperCase()+" SET NEXT_TRAN_DATE = TO_CHAR(SYSDATE,'DD/MON/YYYY') WHERE ( ";
		//System.out.println("cols lenght is "+col_names.length);
		System.out.println("update columns are "+Update_cols);
		String[] col_names = Update_cols.split(",");
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
		deleteBatch(UPDATE_QUERY, rs, col_names,conn);//IT IS UPDATE QUERY FOR RAW TABLE
		//updateBatch(UPDATE_QUERY, rs, tab_cols);
		System.out.println("End time FOR UPDATING RAW TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
	 
	 
	 
	}
	catch(Exception e)
	{
		System.out.println("Exception is "+e);
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


public void updateBatch(final String QUERY, final ResultSet rset,final String[] columns){
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
				if(rset.getString(columns[i-1].trim()) == null)	
				{	
					ps.setString((i), "!null!");
					//System.out.println(i+" = "+"'!null!'");
				
				}
				else
				{
					ps.setString((i), rset.getString(columns[i-1].trim()));
					//System.out.println(i+" = "+rset.getString(columns[i-1].trim()));
				}
				
			//	value++;
			//	System.out.println("i is "+i);
			}
			ps.addBatch();
			
			if(flag == 100)
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

private static class ColumnsMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		String stColumns = rs.getString("COLUMN_NAME");
		
		return stColumns;
		
	}
}
private static class SettlementId implements RowMapper<SettlementBean> {

	@Override
	public SettlementBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		SettlementBean settlementBean = new SettlementBean();
		settlementBean.setInSettlement_id(rs.getInt("ID"));
		return settlementBean;
	}
}


private static class SearchParameterMaster implements RowMapper<FilterationBean> {

	@Override
	public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		//System.out.println("row num is "+rowNum);
	//	System.out.println("header is "+rs.getString("FILE_HEADER"));
		/*while(rs.next())
		{*/
			//System.out.println("header is "+rs.getString("TABLE_HEADER"));
		FilterationBean filterBean = new FilterationBean();
		
		filterBean.setStSearch_header(rs.getString("TABLE_HEADER"));
		filterBean.setStSearch_pattern(rs.getString("PATTERN"));
		filterBean.setStSearch_padding(rs.getString("PADDING"));
		//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
		filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOS"));
		filterBean.setStsearch_Endcharpos(rs.getString("CHARSIZE"));
		filterBean.setStSearch_Condition(rs.getString("CONDITION"));
		filterBean.setStSearch_Datatype(rs.getString("DATATYPE"));
		
		//search_params.add(filterBean);
	//	}
		return filterBean;
		
		
	}
}


@Override
public int saveDataToCashnetSettlement(CashnetSettlementFile settlementFile, String createdby, String date) {
	
	PreparedStatement pstmt = null;
	Connection conn = getConnection();
	int a = 0;
	
	try{
	String sqlQuery = "INSERT INTO cashnet_gl_rawdata (card_number, tran_desc, tran_amount1, tran_amount2, filedate, tran_time, rrn, trace, atm_id, address, city, vnd, created_date, created_by, cycle) VALUES ('"+
	settlementFile.getCardNumber()+"','"+ settlementFile.getTranDesc()+"','"+ settlementFile.getTranAmount1()+"','"+ settlementFile.getTranAmount2()+"','"+ settlementFile.getDate()+"','"+ settlementFile.getTime()+"','"+ 
			settlementFile.getSer()+"','"+ settlementFile.getTrace()+"','"+settlementFile.getRRN()+"','"+ settlementFile.getAddress()+"','"+ settlementFile.getCity()+"','"+ settlementFile.getVnd()+"','"+date+"','"+createdby+"','"+settlementFile.getCycle()+"')";

	//conn.prepareStatement(sqlQuery);
	pstmt = conn.prepareStatement(sqlQuery);
	 a  = pstmt.executeUpdate();
	
	}catch(Exception e){
		
		System.out.println("Exception occured while saving cashnet gl rawdata: "+e);
		logger.error("Exception occured while saving cashnet gl rawdata: "+e);
		try {
			conn.rollback();
			System.exit(0);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception occured while rollback gl rawdata: "+e1);
		}
	}
	return a;
}


@Override
public HashMap<String, Object> checkFileProcessedForCashnetSettlement(CompareSetupBean setupBean) {
	

	HashMap<String, Object>  output = new HashMap<>();
	
	try
	{
		
		
		
		String sql = "select count(1) from cashnet_gl_rawdata "
				+" where filedate = '"+setupBean.getFileDate()+"' and cycle = '"+setupBean.getCategory()+"'";
		
		int count = getJdbcTemplate().queryForObject(sql, new Object[]{},Integer.class);
		
		if(count == 0)
		{
			output.put("result", true);
			output.put("msg", "GL File is not processed");
		}
		else
		{
			output.put("result", false);
			output.put("msg", "GL Statement is already processed");
		}
		
	}
	catch(Exception e)
	{
		output.put("result", false);
		output.put("msg", "Exception occured while validating");
		logger.info("Exception in checkGLStatementProcess "+e);
		
	}
	return output;
	



}


}
