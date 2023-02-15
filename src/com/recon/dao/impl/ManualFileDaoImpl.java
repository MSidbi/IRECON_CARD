package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.ADD_RECORDS;
import static com.recon.util.GeneralUtil.CREATE_RECON_TABLE;
import static com.recon.util.GeneralUtil.GET_COLS;
import static com.recon.util.GeneralUtil.GET_COMPARE_CONDITION;
import static com.recon.util.GeneralUtil.GET_COMPARE_ID;
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
import static com.recon.util.GeneralUtil.GET_SEARCH_PARAMS;
import static com.recon.util.GeneralUtil.GET_TABLE_NAME;
import static com.recon.util.GeneralUtil.GET_TTUM_PARAMS;
import static com.recon.util.GeneralUtil.GET_UPDATE_COLS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.ManualFileDao;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;

@Component
public class ManualFileDaoImpl extends JdbcDaoSupport implements ManualFileDao{

	//DBConnection dbconn = new DBConnection();
	
public void compareManualFile(ManualFileBean manualFileBeanObj)
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
*/		//stFile1_Name = manualFileBeanObj.getStFileSelected();
		stFile1_Name = manualFileBeanObj.getStFileName();
		stManFile_Name = manualFileBeanObj.getStManualFile();
		String GET_FROM_RAW = "";
		String table1_condition = "";
		String table2_condition = "";
		String condition = "";
		String REMAINING_RECORDS = "";
		String columns = "CREATEDDATE, CREATEDBY,FILEDATE";
		String tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE), FILEDATE DATE, MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
		String INSERT_QUERY = "";
		String temp_param = "";
		String task = "";
		
		String CHECK_TABLE = "";
		int tableExist = 0;
		
	//	int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {manualFileBeanObj.getStFileSelected(),manualFileBeanObj.getStCategory(),manualFileBeanObj.getStSubCategory()},Integer.class);
		
		int table1_file_id = Integer.parseInt(manualFileBeanObj.getStFileSelected());
		
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {manualFileBeanObj.getStManualFile(),manualFileBeanObj.getStCategory(),manualFileBeanObj.getStSubCategory() },Integer.class);
		
		/*System.out.println("table 1 file id "+table1_file_id);
		System.out.println("table 2 file id "+table2_file_id);
		System.out.println("category is "+a[1]);
		*/
		String stRawTable1_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table1_file_id}, String.class).trim();
		String stRawTable2_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table2_file_id}, String.class).trim();
//1.............................. CREATE THE CONDITION FOR MAN RECORDS AND FETCH THEM FROM RAW ...................................................................................................
		List<Integer> id_list = getJdbcTemplate().query(GET_MAN_ID , new Object[] {table2_file_id}, new ManualId());
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
		conn = getConnection();
		for(int i = 0; i<id_list.size() ; i++)
		{
			
			condition = "";
		//	System.out.println("man id is "+id_list.get(i));
			List<ManualFileBean> manual_filter1 = getJdbcTemplate().query(GET_MANUAL_PARAM, new Object[] {table2_file_id, id_list.get(i)}, new ManualParameterMaster());
			
			//GET CONDITION FOR SPLITING THE MANUAL RECORDS FOR FILE ID 2
			for(int j = 0; j<manual_filter1.size();j++){
				ManualFileBean manualFileBeanObj1 = new ManualFileBean();
				manualFileBeanObj1 = manual_filter1.get(j);
				//temp_param = filterBeanObj.getStSearch_header().trim();
				temp_param = manualFileBeanObj1.getStFile_header();
				if((manualFileBeanObj1.getStPadding().trim()).equals("Y"))
				{
					if((manualFileBeanObj1.getStCondition().trim()).equals("="))
					{
						condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj1.getStFile_header()+"),"+manualFileBeanObj1.getStStart_charpos()+","+
								manualFileBeanObj1.getStChar_size()+") "+manualFileBeanObj1.getStCondition().trim()+"'"+manualFileBeanObj1.getStSearch_Pattern().trim()+"' ";
					}
					else if((manualFileBeanObj1.getStCondition().trim()).equalsIgnoreCase("like"))
					{
						condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj1.getStFile_header()+"),"+manualFileBeanObj1.getStStart_charpos()+","+
								manualFileBeanObj1.getStChar_size()+") "+manualFileBeanObj1.getStCondition().trim()+
								"'%"+manualFileBeanObj1.getStSearch_Pattern().trim()+"%' ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(t2."+manualFileBeanObj1.getStFile_header()+"),"+manualFileBeanObj1.getStStart_charpos()+","+
								manualFileBeanObj1.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj1.getStSearch_Pattern().trim()+"' ";					
					}
				}
				else
				{
					if(manualFileBeanObj1.getStCondition().equals("="))
					{
						condition = condition + "(TRIM(t2."+manualFileBeanObj1.getStFile_header()+") "+manualFileBeanObj1.getStCondition().trim()+" '"+
								manualFileBeanObj1.getStSearch_Pattern().trim()+"'";
					}
					else if(manualFileBeanObj1.getStCondition().equalsIgnoreCase("like"))
					{
						condition = condition + "(TRIM(t2."+manualFileBeanObj1.getStFile_header()+") "+manualFileBeanObj1.getStCondition().trim()+" "+
									"'%"+manualFileBeanObj1.getStSearch_Pattern().trim()+"%'";
					}
					else
					{
						condition = condition + "(TRIM(t2."+manualFileBeanObj1.getStFile_header()+") "+" NOT IN ('"+manualFileBeanObj1.getStSearch_Pattern().trim()+"' ";
					}
					
				}
				
				for(int k= (j+1); k <manual_filter1.size(); k++)
				{
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
					if(temp_param.equals(manual_filter1.get(k).getStFile_header()))
					{
							
						if(manual_filter1.get(k).getStPadding().trim().equals("Y"))
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
				ManualFileBean manualFileBeanObj2 = new ManualFileBean();
				manualFileBeanObj2 = manual_filter2.get(j);
				//temp_param = filterBeanObj.getStSearch_header().trim();
				temp_param = manualFileBeanObj2.getStFile_header();
				if((manualFileBeanObj2.getStPadding().trim()).equals("Y"))
				{
					if((manualFileBeanObj2.getStCondition().trim()).equals("="))
					{
						condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj2.getStFile_header()+"),"+manualFileBeanObj2.getStStart_charpos()+","+
								manualFileBeanObj2.getStChar_size()+") "+manualFileBeanObj2.getStCondition().trim()+"'"+manualFileBeanObj2.getStSearch_Pattern().trim()+"' ";
					}
					else if((manualFileBeanObj2.getStCondition().trim()).equalsIgnoreCase("like"))
					{
						condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj2.getStFile_header()+"),"+manualFileBeanObj2.getStStart_charpos()+","+
								manualFileBeanObj2.getStChar_size()+") "+manualFileBeanObj2.getStCondition().trim()+
								"'%"+manualFileBeanObj2.getStSearch_Pattern().trim()+"%' ";
					}
					else
					{
						condition = condition + "(SUBSTR(TRIM(t1."+manualFileBeanObj2.getStFile_header()+"),"+manualFileBeanObj2.getStStart_charpos()+","+
								manualFileBeanObj2.getStChar_size()+") "+"NOT IN ('"+manualFileBeanObj2.getStSearch_Pattern().trim()+"' ";					
					}
				}
				else
				{
					if(manualFileBeanObj2.getStCondition().equals("="))
					{
						condition = condition + "(TRIM(t1."+manualFileBeanObj2.getStFile_header()+") "+manualFileBeanObj2.getStCondition().trim()+" '"+
								manualFileBeanObj2.getStSearch_Pattern().trim()+"'";
					}
					else if(manualFileBeanObj2.getStCondition().equalsIgnoreCase("like"))
					{
						condition = condition + "(TRIM(t1."+manualFileBeanObj2.getStFile_header()+") "+manualFileBeanObj2.getStCondition().trim()+" "+
									"'%"+manualFileBeanObj2.getStSearch_Pattern().trim()+"%'";
					}
					else
					{
						condition = condition + "(TRIM(t1."+manualFileBeanObj2.getStFile_header()+") "+" NOT IN ('"+manualFileBeanObj2.getStSearch_Pattern().trim()+"' ";
					}
					
				}
				
				for(int k = (j+1); k <manual_filter2.size();k++)
				{
					if((temp_param.equals(manual_filter2.get(k).getStFile_header()))
						&& manualFileBeanObj2.getStCondition().equals(manual_filter2.get(k).getStCondition()))
					{
						if(manual_filter2.get(k).getStPadding().trim().equals("Y"))
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
			
			
			
			if(!condition.equals(""))
			{
				condition = condition + " t2.PART_ID = 2 AND ";
			}
			else
			{
				condition = " t2.PART_ID = 2 AND ";
			}
			
			
			//System.out.println("Condition is "+condition);
			
			//PREPARE COMPARE CONDITION
			List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table1_file_id},new MatchParameterMaster1());
			List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MAN_MATCHING , new Object[]{id_list.get(i),table2_file_id},new MatchParameterMaster2());
			
			//prepare compare condition
		/*	for(int l = 0; l<match_Headers1.size() ; l++)
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
									match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM "+stRawTable1_name;
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
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM "+stRawTable1_name;
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
									" FROM "+stRawTable2_name+" WHERE PART_ID = 2";
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
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM "+stRawTable2_name+
												   " WHERE PART_ID = 2";
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
				if(match_Headers1.get(l).getStMatchTable1_Padding().trim().equals("Y"))
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
									match_Headers1.get(l).getStMatchTable1_charSize()+" ) FROM "+manualFileBeanObj.getStMergerCategory()+"_"+stFile1_Name 
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
							String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(l).getStMatchTable1_header().trim()+" FROM "+manualFileBeanObj.getStMergerCategory()+
									"_"+stFile1_Name +" WHERE "+match_Headers1.get(l).getStMatchTable1_header().trim()+" IS NOT NULL";
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
				if(match_Headers2.get(l).getStMatchTable2_Padding().trim().equals("Y"))
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
							match_Headers2.get(l).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(l).getStMatchTable2_charSize()+" ) FROM "
									+manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStFileSelected() + " WHERE SUBSTR( "+match_Headers2.get(l).getStMatchTable2_header().trim()+","+
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
							String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(l).getStMatchTable2_header().trim()+" FROM "
									+manualFileBeanObj.getStMergerCategory()+
									"_"+manualFileBeanObj.getStManualFile()+" WHERE "+match_Headers2.get(l).getStMatchTable2_header().trim()+" IS NOT NULL";
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
			
			if(!condition.equals(""))
			{
				/*GET_FROM_RAW = "SELECT * FROM "+stManFile_Name+"_DATA t2 , "+stFile1_Name+"_DATA t1 WHERE "+condition
							   +" AND (TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') OR TO_CHAR(T2.NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))" +
							   " AND T1.MSGTYPE = '210'" ;*/
				
				GET_FROM_RAW = "SELECT * FROM "+stRawTable2_name+" t2 , "+stRawTable1_name+" t1 WHERE "+condition
						   +" AND TO_CHAR(t2.FILEDATE ,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date() +"'"+
						   " AND T1.MSGTYPE = '210'";
							//" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
				/*GET_FROM_RAW = "SELECT * FROM "+stManFile_Name+"_DATA t2 , "+stFile1_Name+"_DATA t1 WHERE "+condition
						   +" AND (TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017' OR TO_CHAR(T2.NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')) AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = '29/04/2017'";*/
				//			" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
			}
			else
			{
				GET_FROM_RAW = "SELECT * FROM "+stRawTable2_name+" t2 , "+stRawTable1_name+" t1 WHERE" 
						    	+" TO_CHAR(t2.FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'"
						    	+" AND T1.MSGTYPE = '210'" ;
						    	//" AND TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
			}
			
			System.out.println("GETTING RECORDS QUERY "+GET_FROM_RAW);
			
			
			
			pstmt = conn.prepareStatement(GET_FROM_RAW);
			MatchedRecords = pstmt.executeQuery();	
			
	//----------------------------- INSERT THE OBTAINED RECORDS WITH CONTRA_ACCOUNT INT MANUAL TABLE--------------------------------------------------------------
			//1. create manual table
			
			
			columns = columns+","+stFile_headers;
			//System.out.println("stfile headers are "+stFile_headers);
			
			
			String[] col_names = stFile_headers.trim().split(",");
			
			for(int m = 0 ;m <col_names.length; m++)
			{
				tableCols = tableCols +","+ col_names[m]+" VARCHAR (100 BYTE) ";
			}
			
		//	System.out.println("TABLE COLS ARE "+tableCols);
			
			CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'MANUAL_"+stRawTable2_name+"'";
			
		//	System.out.println("CHECK TABLE QUERY "+CHECK_TABLE);
			
			tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//	System.out.println("table exists is "+tableExist);
			if(tableExist == 0)
			{
				//create MAN RAW table
				String query = "CREATE TABLE MANUAL_"+stRawTable2_name+" ("+tableCols+")";
				System.out.println("CREATE QUERY IS "+query);
				PreparedStatement pstable = conn.prepareStatement(query);
				pstable.execute();
				
				pstable = null;
				
			}
			
			//2. Insert the matched records in the table with man contra account
			
			INSERT_QUERY = "INSERT INTO MANUAL_"+stRawTable2_name+" (MAN_CONTRA_ACCOUNT,"+columns + ") VALUES (?,SYSDATE,'"+manualFileBeanObj.getStEntryBy()
					+"',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY')";
			System.out.println("INSERT QUERY : "+INSERT_QUERY);
			for(int loop = 0 ; loop < col_names.length; loop++)
			{
				INSERT_QUERY = INSERT_QUERY + ",?";
				
			}
			
			INSERT_QUERY = INSERT_QUERY + ")";
			
	//		System.out.println("INSERT QUERY IS "+INSERT_QUERY);
			
			task = "MATCH";
			insertBatch(INSERT_QUERY, MatchedRecords, col_names, task);
			System.out.println("COMPLETED inserting in manual raw table---------------------------------------------------------------------------------");
			
			pstmt = null;
			MatchedRecords = null;
			columns = "CREATEDDATE, CREATEDBY, FILEDATE";
			tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE, MAN_CONTRA_ACCOUNT VARCHAR2(100 BYTE)";
		}
		
//******************************************* INSERT REMAINING RECORDS IN MANUAL TABLE
		System.out.println("--------------------------------------------INSERTING REMAINING RECORDS---------------------------------------------------------------");
		//GET REMAINING RECORDS FROM RAW TABLE AND INSERT IT INTO NEW RAW TABLE
				
		REMAINING_RECORDS = "SELECT * FROM "+stRawTable2_name+" WHERE PART_ID = 2 " +
							//"AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') 
				"AND  TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"'"
							+" AND NVL(CONTRA_ACCOUNT,'!NULL!') NOT IN ('99978000010021')";
		
		System.out.println("Query to fins remaining records is : "+REMAINING_RECORDS);
		columns = columns + ","+stFile_headers;
		String[] col_names = stFile_headers.trim().split(",");
		
		pstmt = conn.prepareStatement(REMAINING_RECORDS);
		ResultSet remain_records = pstmt.executeQuery();
		task = "UNMATCHED";
		
		INSERT_QUERY = "INSERT INTO MANUAL_"+stRawTable2_name+" ("+columns + ") VALUES (SYSDATE,'"+manualFileBeanObj.getStEntryBy()+"',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY')";
		
		for(int loop = 0 ; loop < col_names.length; loop++)
		{
			INSERT_QUERY = INSERT_QUERY + ",?";
			
		}
		
		INSERT_QUERY = INSERT_QUERY + ")";
		
		System.out.println("QUERY FOR INSERTING REMAINING RECORDS "+INSERT_QUERY);
		
		insertBatch(INSERT_QUERY, remain_records, col_names, task);

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

public void updateActionTakenTTUMRecord(ManualFileBean manualFileBeanObj)throws Exception
{
	int tableExist  = 0;
	Connection conn ;
	try
	{
		//String a[] = stTable_Name.split("_"); 
		//1.Get File ID from db
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { manualFileBeanObj.getStManualFile(),manualFileBeanObj.getStCategory(),manualFileBeanObj.getStSubCategory() },Integer.class);
		System.out.println("file id is "+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		System.out.println("file headers are "+stFile_headers);
		
	/*	String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'MANUAL_ACTION_"+a[1].toUpperCase()+"'";
		//System.out.println("check table "+CHECK_TABLE);
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		
		
		if(tableExist == 0)
		{
			//String columns = "CREATEDDATE, CREATEDBY, MAN_CONTRA_ACCOUNT"+stFile_headers;
			String tab_cols = "CREATEDDATE DATE, CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE,MAN_CONTRA_ACCOUNT VARCHAR(100 BYTE)";
			
			String[] col_names = stFile_headers.trim().split(",");
			for(int i=0 ;i <col_names.length; i++)
			{
				tab_cols = tab_cols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
			}
			
			
			
			CREATE_RECON_TABLE = "CREATE TABLE MANUAL_ACTION_"+a[1].toUpperCase() +"( "+tab_cols +" )";
			getJdbcTemplate().execute(CREATE_RECON_TABLE);
			System.out.println("create table query "+CREATE_RECON_TABLE);
		}*/
		
		List<String> TABLE_COLS = getJdbcTemplate().query(GET_COLS,new Object[] {manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStManualFile()},new ColumnMapper());
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
		
		String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TTUM_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		
		if(tableExist == 1)
		{
			String ACTION_RECORDS = "";
			/*if(manualFileBeanObj.getStCategory().equals("ONUS"))
			{
				ACTION_RECORDS = "SELECT * FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" T1, TTUM_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile().toUpperCase()+" T2 WHERE T1.TRAN_PARTICULAR = T2.TRANSACTION_PARTICULARS" +
						" AND T1.TRAN_RMKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				System.out.println("ACTION RECORDS "+ACTION_RECORDS);
			}
			else
			{
				ACTION_RECORDS = "SELECT * FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" T1, TTUM_"
						+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile().toUpperCase()+" T2 WHERE T1.PARTICULARALS = T2.TRANSACTION_PARTICULARS" +
						" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				
						
			}
			*/
			
			//2. update these records in SETTLEMENT TABLE AS ACTION TAKEN ENTRIES
			/*String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" SET DCRS_REMARKS = '"
					+manualFileBeanObj.getStMergerCategory()+"-ACTION-TAKEN' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' AND" +
						" (DCRS_REMARKS LIKE '%"+manualFileBeanObj.getStCategory()+"-UNRECON%'  " +
								"OR DCRS_REMARKS = '%"+manualFileBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' )";*/
			
			// WHERE DCRS_REMARKS LIKE '%GENERATE-TTUM%' ";
			
			//CHANGES MADE BY INT5779 ON 29 JAN 2018
			
/*			SELECT * FROM SETTLEMENT_AMEX_CBS WHERE REMARKS = '376535555331009' AND  AMOUNT = '10,000.00' AND DCRS_REMARKS LIKE '%AMEX-UNRECON%' AND
					 TO_CHAR(FILEDATE,'DD/MM/YYYY') = '13/01/2018' AND SUBSTR(PARTICULARALS,1,8) = 'ID007501' AND TO_NUMBER(SUBSTR(REF_NO,1,10)) = '195790';
					 
*/
			if(manualFileBeanObj.getStCategory().equals("AMEX"))
			{
							
				//inserting the classfied manual records in settlement table 
				/*String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
						+") VALUES (SYSDATE,'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),'MAN-MATCHED'";
				String[] col_names = stFile_headers.split(",");

				for(int i=0;i<col_names.length;i++)
				{
					INSERT_RECORDS = INSERT_RECORDS+",?";
				}

				INSERT_RECORDS = INSERT_RECORDS + ")";
				
				System.out.println("INSERT QUERY IS "+INSERT_RECORDS);

				insertBatch(INSERT_RECORDS, manual_rset, col_names, "");
*/				

				ACTION_RECORDS = "SELECT 'MAN-MATCHED',"+select_cols+" FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" t1 " +
						"WHERE EXISTS ( SELECT * FROM TTUM_AMEX_CBS t2 WHERE "+
								" TRIM(SUBSTR(T1.PARTICULARALS,0,20)) = TRIM(REPLACE(SUBSTR(T2.TRANSACTION_PARTICULARS,0,20),'-',' '))" +
								" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D')";
				System.out.println("ACTION RECORDS "+ACTION_RECORDS);
				String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+"(DCRS_REMARKS,"+select_cols+") "
							+ACTION_RECORDS;
				
				System.out.println("ACTION_RECORDS QUERY IS "+ACTION_RECORDS);
				
				getJdbcTemplate().execute(INSERT_RECORDS);
				
				String DELETE_QUERY = "DELETE FROM "+manualFileBeanObj.getStMergerCategory()+"_"+manualFileBeanObj.getStManualFile()+" t1 " +
						"WHERE EXISTS ( SELECT * FROM TTUM_AMEX_CBS t2 WHERE "+
						" TRIM(SUBSTR(T1.PARTICULARALS,0,20)) = TRIM(REPLACE(SUBSTR(T2.TRANSACTION_PARTICULARS,0,20),'-',' '))" +
						" AND T1.REMARKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D')";
				
				System.out.println("DELETE QUERY IS "+DELETE_QUERY);
				
				getJdbcTemplate().execute(DELETE_QUERY);
				
				//UPDATE_RECORDS = UPDATE_RECORDS +" WHERE REMARKS = '' AND AMOUNT = ? AND SUBSTR(PARTICULARALS,1,8) = ? AND TO_NUMBER(SUBSTR(REF_NO,1,10)) = ? ";
				String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" t1 SET DCRS_REMARKS = '"
						+manualFileBeanObj.getStMergerCategory()+"-ACTION-TAKEN' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' AND" +
							" (DCRS_REMARKS LIKE '%"+manualFileBeanObj.getStCategory()+"-UNRECON%'  " +
									"OR DCRS_REMARKS = '%"+manualFileBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' ) AND EXISTS (SELECT * FROM SETTLEMENT_"+
										manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()
										+" t2 , TTUM_AMEX_CBS T3 WHERE T2.DCRS_REMARKS = 'MAN-MATCHED' AND " +
										" (TRIM(REPLACE(SUBSTR(T3.TRANSACTION_PARTICULARS,0,20),'-',' ')) = TRIM(SUBSTR(T2.PARTICULARALS,0,20)) AND T2.REMARKS = T3.REMARKS AND T3.PART_TRAN_TYPE = 'D') " +
										" AND TO_NUMBER(T1.AMOUNT,'999999999.99') = TO_NUMBER(T2.AMOUNT,'999999999.99')"+  
										" AND SUBSTR(T1.PARTICULARALS,1,8) = SUBSTR(PARTICULARALS,5,8) " +
										//" AND TO_NUMBER(SUBSTR(T1.REF_NO,1,10)) = TO_NUMBER(SUBSTR(T2.REF_NO,1,10)))";
										" AND T1.REMARKS = T2.REF_NO AND TO_NUMBER(SUBSTR(T1.REF_NO,1,10)) = TO_NUMBER(SUBSTR(T3.TRANSACTION_PARTICULARS,-6,6)) " +
										"AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') ='"+manualFileBeanObj.getStFile_date()+"')";
				
				getJdbcTemplate().execute(UPDATE_RECORDS);
				
				System.out.println("UPDATE ACTION TAKEN QUERY "+UPDATE_RECORDS);
			}
			else		
			{
				String UPDATE_RECORDS = "UPDATE SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" SET DCRS_REMARKS = '"
						+manualFileBeanObj.getStMergerCategory()+"-ACTION-TAKEN' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' AND" +
							" (DCRS_REMARKS LIKE '%"+manualFileBeanObj.getStCategory()+"-UNRECON%'  " +
									"OR DCRS_REMARKS = '%"+manualFileBeanObj.getStCategory()+"-UNRECON-GENERATED-TTUM%' )";
				ACTION_RECORDS = "SELECT * FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" T1, TTUM_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile().toUpperCase()+" T2 WHERE T1.TRAN_PARTICULAR = T2.TRANSACTION_PARTICULARS" +
						" AND T1.TRAN_RMKS = T2.REMARKS AND T2.PART_TRAN_TYPE = 'D'";
				System.out.println("ACTION RECORDS "+ACTION_RECORDS);

				conn = getConnection();
				PreparedStatement action_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet action_rset = action_statement.executeQuery();
				
				//select COLUMNS FROM TABLE FOR WHERE CONDITION
				//		List<Manual> Update_cols_value = getJdbcTemplate().query(GET_UPDATE_COLS,new Object[]{},new ColumnsValueMapper());
				List<ManualFileBean> Update_cols = getJdbcTemplate().query(GET_UPDATE_COLS,new Object[]{},new ColumnsMapper());

				String[] stUpdate_Cols = new String[Update_cols.size()];
				String[] stDelete_cols = new String[Update_cols.size()];
				for(int i = 0 ;i <Update_cols.size() ; i++)
				{
					UPDATE_RECORDS = UPDATE_RECORDS + " AND "+Update_cols.get(i).getStUpdate_Column() +" = ? ";

				}
				for(int i=0;i<Update_cols.size() ; i++)
				{
					stUpdate_Cols[i] = Update_cols.get(i).getStUpdateCol_Value();
					//stUpdate_Cols[i] = Update_cols.get(i).getStUpdate_Column();
					stDelete_cols[i] = Update_cols.get(i).getStUpdate_Column();
				}
				System.out.println("UPDATE_RECORDS QUERY "+UPDATE_RECORDS);
				updateBatch(UPDATE_RECORDS, action_rset, stUpdate_Cols);

				//INSERT IN SETTLEMENT TABLE AND DELETE FROM ONUS_MANUAL_CBS TABLE
				PreparedStatement manual_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet manual_rset = manual_statement.executeQuery();

				PreparedStatement delete_statement = conn.prepareStatement(ACTION_RECORDS);
				ResultSet delete_rset = delete_statement.executeQuery();



				if(manualFileBeanObj.getStManualFile().equals("CBS"))
				{
					stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
				}

				String INSERT_RECORDS = "INSERT INTO SETTLEMENT_"+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers
						+") VALUES (SYSDATE,'"+manualFileBeanObj.getStEntryBy()+"',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),'MAN-MATCHED'";
				String[] col_names = stFile_headers.split(",");

				for(int i=0;i<col_names.length;i++)
				{
					INSERT_RECORDS = INSERT_RECORDS+",?";
				}

				INSERT_RECORDS = INSERT_RECORDS + ")";

				String DELETE_RECORDS = "DELETE FROM "+manualFileBeanObj.getStCategory()+"_"+manualFileBeanObj.getStManualFile()+" WHERE ";

				for(int i = 0 ;i <Update_cols.size() ; i++)
				{
					if(i == Update_cols.size()-1)
						DELETE_RECORDS = DELETE_RECORDS +Update_cols.get(i).getStUpdate_Column() +" = ? ";
					else
						DELETE_RECORDS = DELETE_RECORDS + Update_cols.get(i).getStUpdate_Column() +" = ? AND ";

				}

				System.out.println("DELETE QUERY IS "+DELETE_RECORDS);
				System.out.println("INSERT QUERY IS "+INSERT_RECORDS);

				insertBatch(INSERT_RECORDS, manual_rset, col_names, "");
				System.out.println("insert task completed------------------------------------------------------------------------------");
				insertBatch(DELETE_RECORDS, delete_rset, stDelete_cols, "");
				System.out.println("COMPLETED ACTION ON TTUM ENTRIES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			}
		}
	}
	catch(Exception e)
	{
		System.out.println("Exception in updateActionTakenRecord "+e);
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

private static class ColumnsMapper implements RowMapper<ManualFileBean> {

	@Override
	public ManualFileBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		ManualFileBean manFileBeanObj = new ManualFileBean();
		
		manFileBeanObj.setStUpdate_Column(rs.getString("FILE_HEADER"));
		manFileBeanObj.setStUpdateCol_Value(rs.getString("COLUMN_NAME").replace(" ", "_"));
		//String stColumns = rs.getString("FILE_HEADER").replace(" ", "_");
		return manFileBeanObj;
		//return stColumns;
		
	}
}

public int moveData(List<String> tables,ManualFileBean manualFileBeanObj,int inRec_Set_Id)throws Exception
{
	
//	String tableCols = "MATCHING_FLAG CHAR(1 BYTE), SEG_TRAN_ID NUMBER , KNOCKOFF_FLAG CHAR(1 BYTE)";
	String tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE,FILEDATE DATE";
	Connection conn;
	PreparedStatement pstmt ;
	ResultSet rset;
	//String columns="SEG_TRAN_ID,KNOCKOFF_FLAG";
	String columns;
	//String condition = "";
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
			columns ="SEG_TRAN_ID";
			tableCols = "SEG_TRAN_ID NUMBER, CREATEDBY VARCHAR2(100 BYTE), CREATEDDATE DATE,FILEDATE DATE ";
		//	String table_name = tables.get(loop);
		//	System.out.println("TABLE NAME IS "+table_name);
		//	String a[] = table_name.split("_");
			//String table = a[1]+"_DATA";
		
		//System.out.println("get cols query is "+GET_COLS );
		
		
		///1. GET THE HEADERS FROM DB
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {tables.get(loop) , manualFileBeanObj.getStCategory(), manualFileBeanObj.getStSubCategory() },Integer.class);
		//System.out.println("file Id is "+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		columns = columns+","+stFile_headers;
		//System.out.println("stfile headers are "+stFile_headers);
		
		if(tables.get(loop).equals("CBS"))
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
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'TEMP_"+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop).toUpperCase()+"'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE TEMP_"+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop).toUpperCase()+"("+tableCols+")";
			//System.out.println("CREATE QUERY IS "+query);
			pstmt = conn.prepareStatement(query);
			pstmt.execute();

			pstmt = null;
			
		}
		
	//CHECKING WHETHER MATCHED RECORDS TABLE IS PRESENT
		CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop).toUpperCase()+"_MATCHED'";
		tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
		//System.out.println("table exists value is "+tableExist);
		if(tableExist == 0)
		{
			//create temp table
			String query = "CREATE TABLE "+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop)+"_MATCHED "+"("+tableCols+")";
			//System.out.println("CREATE QUERY IS "+query);
			pstmt = conn.prepareStatement(query);
			pstmt.execute();

			pstmt = null;
			
		}
		

//--------------------------------------------MOVING TABLE TO TEMP TABLE--------------------------------------------------------------------------------------			
	//**********2. GET THE DATA FROM ORIGINAL TABLE
			//get the condition for getting original transactions from table
	
		//GET_DATA = "SELECT * FROM "+table_name + " WHERE KNOCKOFF_FLAG = 'N'";
	//	String GET_DATA = "SELECT * FROM "+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop) + " OS1 WHERE ";
		String GET_DATA = "SELECT SYSDATE,'"+manualFileBeanObj.getStEntryBy()+"',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),"+columns//entry by is going null
				+" FROM "+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop) + " OS1 WHERE ";
		
		
		
		//GET PARAMETERS FOR CONSIDERING THE RECORDS FOR COMPARE
		stMatch_param = getReconParameters(tables.get(loop),manualFileBeanObj,inRec_Set_Id);
		
		if(!stMatch_param.equals(""))
		{
			//GET_DATA = GET_DATA +" AND "+ stMatch_param;
			//GET_DATA = GET_DATA + stMatch_param + "  AND NOT EXISTS ( ";
			GET_DATA = GET_DATA + stMatch_param + " AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE) AND" +
					" TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' AND NOT EXISTS ( ";
		}
		
		
		//System.out.println("PART1 QUERY ::::::::::::::::::::::::: "+GET_DATA);
		
		//get part 2 of query
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), manualFileBeanObj.getStCategory()},Integer.class);
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		
		
		String part2_condition = getCondition(knockoff_Criteria);
		
		//System.out.println("PART 2 CONDITION IS "+part2_condition);
		
		String PART2_QUERY = "SELECT * FROM "+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop)+"_KNOCKOFF OS2 ";
		if(!part2_condition.equals(""))
		{
			PART2_QUERY = PART2_QUERY + " WHERE ( " + part2_condition + ")";
		}
		
		//System.out.println("part2 query is "+PART2_QUERY);
		
		//GET_DATA = GET_DATA + PART2_QUERY + " )";
		GET_DATA = GET_DATA + PART2_QUERY + " AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE)" +
				" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualFileBeanObj.getStFile_date()+"' )";
		
		System.out.println("Final query is "+GET_DATA);
		
		
		//conn = getConnection();
		pstmt = conn.prepareStatement(GET_DATA);
		rset = pstmt.executeQuery();
		//System.out.println("GOT THE UNKNOCKED DATA");
		//. MOVE THE DATA IN TEMP TABLE
		String[] cols = columns.split(",");
		ADD_RECORDS = "INSERT INTO TEMP_"+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop)+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") "+GET_DATA;
		
		System.out.println("QUERY FOR ADDING RECORDS IN TEMP TABLE "+ADD_RECORDS);
		System.out.println("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
		getJdbcTemplate().execute(ADD_RECORDS);
		//insertBatch(ADD_RECORDS,rset, cols);
		System.out.println("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
		/*ADD_RECORDS = "INSERT INTO TEMP_"+manualFileBeanObj.getStMergerCategory()+"_"+tables.get(loop)+" (CREATEDDATE,CREATEDBY,FILEDATE,"+columns+") VALUES(sysdate,'INT5779',TO_DATE('"+manualFileBeanObj.getStFile_date()+"','DD/MM/YYYY'),";

		for(int i = 1 ; i<= cols.length; i++)
		{
			if(i == cols.length)
			{
				ADD_RECORDS = ADD_RECORDS + "?";
				
			}
			else
			{
				ADD_RECORDS = ADD_RECORDS + "?,";
				
			}
		}
		ADD_RECORDS = ADD_RECORDS+")";
		System.out.println("QUERY FOR ADDING RECORDS IN TEMP TABLE "+ADD_RECORDS);
		System.out.println("start time FOR INSERTING IN TEMP TABLE TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));
		insertBatch(ADD_RECORDS, rset, cols, "");
		System.out.println("End time FOR INSERTING IN TEMP TABLE "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
//-----------------------------NOW TRUNCATE ONUS AND KNOCKOFF TABLES
		/*String TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name;
		getJdbcTemplate().execute(TRUNCATE_QUERY);
		TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name+"_KNOCKOFF";
		getJdbcTemplate().execute(TRUNCATE_QUERY);*/
		
		
		loop++;
		}
		
	}
	catch(Exception e)
	{
		System.out.println("Exception is "+e);
	}
	
	return 1;
	
	
}

public void updateMatchedRecords(List<String> Table_list,ManualFileBean manualBeanObj,int inRec_Set_Id) throws Exception
{

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
	try
	{
		String file1_name = "", file2_name = "";
		String condition = "";	
		//String table1_alias = "t1", table2_alias = "t2";
		//String table1_name = Table_list.get(0);
		//System.out.println("TABLE 1 NAME IS "+table1_name);
		//String a[] = table1_name.split("_");
		file1_name = Table_list.get(0);;
		
		
		/*String table2_name = Table_list.get(1);
		//System.out.println("TABLE 2 NAME IS "+table2_name);
		a = table2_name.split("_");*/
		file2_name = Table_list.get(1);
		
		//System.out.println("file 1 name is "+file1_name);
		//System.out.println("file 2 name is "+file2_name);
		
	//*******1. MATCH THE RECORDS AND TAKE THOSE IN RESULTSET
		//GET MATCHING PARAMETERS FROM DB
		
		System.out.println("COMPARE STARTS HERE *************************************************");
		
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file1_name,manualBeanObj.getStCategory(),manualBeanObj.getStSubCategory() },Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { file2_name,manualBeanObj.getStCategory(),manualBeanObj.getStSubCategory() },Integer.class);
		
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,manualBeanObj.getStCategory(),inRec_Set_Id},new MatchParamMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,manualBeanObj.getStCategory(),inRec_Set_Id},new MatchParamMaster2());
		
	
	/*	//prepare compare condition
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
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file1_name;
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
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file1_name;
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
			System.out.println("i value is "+i);
			System.out.println("match headers length is "+match_Headers2.size());
			System.out.println("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());
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
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file2_name;
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
				System.out.println("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
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
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file2_name;
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
		
		//System.out.println("FINALLY CONDITION IS "+condition);
		
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
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM "+manualBeanObj.getStMergerCategory()+"_"+file1_name 
								+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
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
						String CHECK_FORMAT = "SELECT DISTINCT "+match_Headers1.get(i).getStMatchTable1_header().trim()+" FROM "+manualBeanObj.getStMergerCategory()+
								"_"+file1_name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
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
			/*System.out.println("i value is "+i);
			System.out.println("match headers length is "+match_Headers2.size());
			System.out.println("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
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
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"')";		*/
						table2_condition = " TO_CHAR(TO_DATE( SUBSTR(  t2."+match_Headers2.get(i).getStMatchTable2_header().trim()+","+match_Headers2.get(i).getStMatchTable2_startcharpos()+","+
								 match_Headers2.get(i).getStMatchTable2_charSize()+")"+",'"+match_Headers2.get(i).getStMatchTable2_DatePattern()+"'),'DD/MM/YYYY')";		
					}
					else if(match_Headers2.get(i).getStMatchTable2_Datatype().equals("TIME"))
					{
						//check whether the column consists of :
						String CHECK_FORMAT = "SELECT DISTINCT SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) FROM "
								+manualBeanObj.getStMergerCategory()+"_"+file2_name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
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
				System.out.println("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
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
						String CHECK_FORMAT = "SELECT DISTINCT  "+match_Headers2.get(i).getStMatchTable2_header().trim()+" FROM "+manualBeanObj.getStMergerCategory()+
								"_"+file2_name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
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
		
	/*	String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);	
		String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);*/
		
		
		List<String> file1_selCols = getJdbcTemplate().query(GET_COLS,new Object[]{"TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file1_name}, new ColumnMapper());
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
		
		List<String> file2_selheader = getJdbcTemplate().query(GET_COLS,new Object[] {"TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file2_name}, new ColumnMapper());
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
	//********* GETTING RECORDS IN RSET
		
	//	JOIN1_QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
//		JOIN2_QUERY = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN TEMP_"+table1_name + " t1 ON( "+condition + " ) WHERE T1.MATCHING_FLAG = 'N' AND T2.MATCHING_FLAG = 'N'";
		/*String JOIN1_QUERY = "SELECT * FROM TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file1_name + " t1 INNER JOIN TEMP_"+manualBeanObj.getStCategory()+"_"+file2_name 
				        + " t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
				        		" AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'"
						+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
						" AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";
		KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
						+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";
		String JOIN2_QUERY = "SELECT * FROM TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file2_name + " t2 INNER JOIN TEMP_"+manualBeanObj.getStCategory()+"_"+file1_name + " t1 ON( "+condition 
						+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
								" AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'"
						+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
						" AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";*/
		/*KNOCKOFF2_COMPARE = "SELECT * FROM TEMP_"+table2_name + " t2 INNER JOIN "+table1_name + "_KNOCKOFF t1 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
						+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
		//GET WHERE CONDITION FROM MAIN_matching_CONDITION TABLE
//CREATEDDATE,CREATEDBY,FILEDATE,
		String JOIN1_QUERY = "SELECT DISTINCT "+join1_headers+" FROM TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file1_name + " t1 INNER JOIN TEMP_"+manualBeanObj.getStCategory()+"_"+file2_name 
				+ " t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
				" AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'"
				+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY') " +
				" AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";
		/*	KNOCKOFF1_COMPARE = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN "+table2_name + "_KNOCKOFF t2 ON( "+condition + " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')"
				+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')";*/
		String JOIN2_QUERY = "SELECT DISTINCT "+join2_headers+" FROM TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file2_name + " t2 INNER JOIN TEMP_"+manualBeanObj.getStCategory()+"_"+file1_name + " t1 ON( "+condition 
				+ " ) WHERE TO_CHAR(T1.CREATEDDATE,'DD/MM/YYYY') = "+"TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
				" AND TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'"
				+" AND TO_CHAR(T2.CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')" +
				" AND TO_CHAR(T2.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";

		String compare_cond1 = getCompareCondition(file1_name,manualBeanObj,inRec_Set_Id);
		String compare_cond2 = getCompareCondition(file2_name,manualBeanObj,inRec_Set_Id);
		String compare_cond = compare_cond1 +" AND " + compare_cond2;
		if(!compare_cond.equals(""))
		{
			JOIN1_QUERY = JOIN1_QUERY + " AND "+ compare_cond;
			JOIN2_QUERY = JOIN2_QUERY + " AND "+ compare_cond;
		}
		
		System.out.println("----------------------------------------------------------------------------------- DONE ---------------------------------------------");
		
		//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
		System.out.println("COMPARE QUERY IS****************************************");
		System.out.println("JOIN1 QUERY IS "+JOIN1_QUERY);
		System.out.println("JOIN2_QUERY IS "+JOIN2_QUERY);
		
		
/*******************************************inserting MATCHED REPORTS*************************************************************/		
		String INSERT1_QUERY = "INSERT INTO "+manualBeanObj.getStMergerCategory()+"_"+file1_name+"_MATCHED ("+table1_selheaders+") "+JOIN1_QUERY;
		String INSERT2_QUERY = "INSERT INTO "+manualBeanObj.getStMergerCategory()+"_"+file2_name+"_MATCHED ("+table2_selheader+") "+JOIN2_QUERY;
		
		getJdbcTemplate().execute(INSERT1_QUERY);
		getJdbcTemplate().execute(INSERT2_QUERY);
		
		SETTLEMENT_INSERT1 = "INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file1_name+" (DCRS_REMARKS,"+table1_selheaders+") "+
							"SELECT '"+manualBeanObj.getStMergerCategory()+"-MATCHED',"+table1_selheaders+" FROM "+manualBeanObj.getStMergerCategory()+"_"+file1_name+"_MATCHED " +
								" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";
		
		
		
		SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file2_name+" (DCRS_REMARKS,"+table2_selheader+") "+
							" SELECT '"+manualBeanObj.getStMergerCategory()+"-MATCHED',"+table2_selheader+" FROM "+manualBeanObj.getStMergerCategory()+"_"+file2_name+"_MATCHED "+
							" WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'";
				
		getJdbcTemplate().execute(SETTLEMENT_INSERT1);
		getJdbcTemplate().execute(SETTLEMENT_INSERT2);
		
				
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

		
//----------------------- REMOVING BATCH INSERT ------------------------------------------------------------------------------------		
		
		
		
//---------------- MOVING THE OBTAINED RESULT INTO NEW TABLE-----------------------
		//1. CREATE INSERT QUERY
		/*String INSERT1_QUERY = "INSERT INTO "+manualBeanObj.getStMergerCategory()+"_"+file1_name+"_MATCHED (CREATEDDATE,CREATEDBY,FILEDATE,";
		SETTLEMENT_INSERT1="INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file1_name+"(CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
		
		    //get columns
		String stTable1_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table1_file_id}, String.class);	
		String stTable2_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{table2_file_id}, String.class);
		if(file1_name.equals("CBS"))
		{
			table1_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable1_headers;
		}
		else
			table1_cols = "SEG_TRAN_ID,"+stTable1_headers;
		INSERT1_QUERY = INSERT1_QUERY + table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),?";
		SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 +table1_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),'"+manualBeanObj.getStMergerCategory()+"-MATCHED',?";
		
		table1_column = table1_cols.split(",");
		
		for(int i=0;i<(table1_column.length-1);i++)
		{
			INSERT1_QUERY = INSERT1_QUERY + ",?";
			SETTLEMENT_INSERT1 =SETTLEMENT_INSERT1 + ",?";
		}
		INSERT1_QUERY = INSERT1_QUERY +" )";
		SETTLEMENT_INSERT1 = SETTLEMENT_INSERT1 + " )";
		
		
		System.out.println("INSERT1 QUERY IS "+INSERT1_QUERY);
		System.out.println("SETTLEMENT INSERT1 QUERY IS "+SETTLEMENT_INSERT1);
		
		String INSERT2_QUERY = "INSERT INTO "+manualBeanObj.getStMergerCategory()+"_"+file2_name+"_MATCHED (CREATEDDATE,CREATEDBY,FILEDATE,";
		SETTLEMENT_INSERT2 = "INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file2_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,";
		if(file2_name.equals("CBS"))
		{
			table2_cols = "SEG_TRAN_ID,MAN_CONTRA_ACCOUNT,"+stTable2_headers;
		}
		else
		{	
			table2_cols = "SEG_TRAN_ID,"+stTable2_headers;
		}
		INSERT2_QUERY = INSERT2_QUERY + table2_cols+") VALUES(SYSDATE,'INT5779',TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),?";
		SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + table2_cols+ ") VALUES(SYSDATE,'INT5779',TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),'"+
						manualBeanObj.getStMergerCategory()+"-MATCHED',?";
		table2_column = table2_cols.split(",");
		for(int i=0;i<(table2_column.length-1);i++)
		{
			INSERT2_QUERY = INSERT2_QUERY + ",?";
			SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 + ",?";
		}
		INSERT2_QUERY = INSERT2_QUERY + ")";
		SETTLEMENT_INSERT2 = SETTLEMENT_INSERT2 +" )";
		
		System.out.println("INSERT2 QUERY IS "+INSERT2_QUERY);
		System.out.println("SETTLEMENT INSERT2 QUERY "+SETTLEMENT_INSERT2);
		
		
		//INSERT MATCHED RECORDS IN MATCHED TABLE
		System.out.println("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
		//insertBatch(INSERT1_QUERY, rset1, table1_column);
		insertBatch(INSERT1_QUERY, rset1, table1_column, "");
		System.out.println("eND TIME FOR COMPLETING INSERTION IN RECON_"+manualBeanObj.getStMergerCategory()+"_"+file1_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
		System.out.println("start time FOR INSERTING MATCHED DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
		//insertBatch(INSERT2_QUERY, rset2, table2_column);
		insertBatch(INSERT2_QUERY, rset2, table2_column, "");
		System.out.println("eND TIME FOR COMPLETING INSERTION IN RECON_"+manualBeanObj.getStMergerCategory()+"_"+file2_name+" : "+new java.sql.Timestamp(new java.util.Date().getTime()));
		
		//inserting in settlement table
		System.out.println("start time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
		insertBatch(SETTLEMENT_INSERT1, settlement_set1, table1_column, "");
		//insertBatch(SETTLEMENT_INSERT1, settlement_set1, table1_column);
		System.out.println("end time FOR INSERTING MATCHED DATA in settlement table1 "+new java.sql.Timestamp(new java.util.Date().getTime()));
		System.out.println("start time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));
	//	insertBatch(SETTLEMENT_INSERT2, settlement_set2, table2_column);
		insertBatch(SETTLEMENT_INSERT2, settlement_set2, table2_column, "");
		System.out.println("end time FOR INSERTING MATCHED DATA in settlement table2 "+new java.sql.Timestamp(new java.util.Date().getTime()));*/
		
		
		
//-------------------- NOW TRUNCATE TEMP AND MATCHED TABLES
	/*	String TRUNCATE_TABLE1 = "TRUNCATE TABLE "+table1_name;
		getJdbcTemplate().execute(TRUNCATE_TABLE1);
		
		String TRUNCATE_TABLE2 = "TRUNCATE TABLE "+table2_name;
		getJdbcTemplate().execute(TRUNCATE_TABLE2);*/
//-------------------------------DONE----------------------------------------------------			
		
	
	}
	catch(Exception e)
	{
		System.out.println("exception issss "+e);
		
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
			throw new Exception("ERROR WHILE CLOSING DB CONNECTION ");
		}
	}
	
	

}
public void moveToRecon(List<String> tables,ManualFileBean manualBeanObj)throws Exception
{
	//System.out.println("MOVE TO RECON");
	int loop = 0,tableExist = 0;
	String RECON_RECORDS_PART1 = "", recon_columns = "", RECON_INSERT = "", RECON_RECORDS_PART2 = "", RECON_RECORDS = "";
	String CHECK_TABLE = "";
	String file_name= "";
	PreparedStatement psrecon = null;
	ResultSet rs = null;
	Connection con;
	String[] col_names={""};
	String SETTLEMENT_INSERT = "";
	try
	{
		//System.out.println("recon_columns are "+recon_columns);
		System.out.println("MOVE TO RECON ENTERED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		while(loop<tables.size())
		{
			
			file_name = tables.get(loop);
			//String[] a = table_name.split("_");
	//1. *******CHECK FOR RECON TABLE
			CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = 'RECON_"+manualBeanObj.getStMergerCategory()+"_"+file_name.toUpperCase()+"'";
			//System.out.println("check table "+CHECK_TABLE);
			tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			//System.out.println("table exists value is "+tableExist);
			//if table is not present then create it
			
			//GET_TEMP_COLS = "SELECT COLUMN_NAME,DATA_TYPE FROM user_tab_cols WHERE table_name=UPPER('TEMP_"+table_name+"')";
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {file_name,manualBeanObj.getStCategory(),manualBeanObj.getStSubCategory() },Integer.class);
			//System.out.println("file Id is "+file_id);
			
			String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
			//List<CompareBean> sttemp_cols = getJdbcTemplate().query(GET_TEMP_COLS, new Object[]{}, new TableColumnsMapper());
			
			if(file_name.equals("CBS"))
			{
				stFile_headers = stFile_headers+",MAN_CONTRA_ACCOUNT";
			}
			
			if(tableExist == 0)
			{
				String tab_cols;
					recon_columns = "CREATEDDATE, CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
					tab_cols = "SEG_TRAN_ID NUMBER, CREATEDDATE DATE, CREATEDBY VARCHAR(100 BYTE),FILEDATE DATE";
				
				col_names = stFile_headers.trim().split(",");
				for(int i=0 ;i <col_names.length; i++)
				{
					tab_cols = tab_cols +","+ col_names[i]+" VARCHAR (100 BYTE) ";
				}
				
				
				
				CREATE_RECON_TABLE = "CREATE TABLE RECON_"+manualBeanObj.getStMergerCategory()+"_"+file_name +"( "+tab_cols +" )";
				getJdbcTemplate().execute(CREATE_RECON_TABLE);
				
			}
			recon_columns = "CREATEDDATE,CREATEDBY,FILEDATE,SEG_TRAN_ID,"+stFile_headers;
			String col_headers = "SEG_TRAN_ID,"+stFile_headers;
			col_names = col_headers.split(",");
			
//2. *************** GET RECORDS FROM TEMP TABLE
			List<String> selHeaders = getJdbcTemplate().query(GET_COLS,new Object[]{"TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file_name}, new ColumnMapper());
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
			
			//RECON_RECORDS = "SELECT * FROM TEMP_"+table_name+" WHERE MATCHING_FLAG = 'N'";
			RECON_RECORDS_PART1 = "SELECT "+stSelect_Headers+" FROM TEMP_"+manualBeanObj.getStMergerCategory()+"_"+file_name+" OS1 WHERE trunc(OS1.CREATEDDATE) = trunc(SYSDATE) " +
					"AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"' AND NOT EXISTS";
			
			RECON_RECORDS_PART2 = "( SELECT * FROM "+manualBeanObj.getStMergerCategory()+"_"+file_name+"_MATCHED OS2 WHERE trunc(OS2.CREATEDDATE) = trunc(SYSDATE)" +
					" AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"' AND ";
	
			System.out.println("file id is "+file_id);
		//	System.out.println("a[0] = "+a[0]  );
			 
			int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), manualBeanObj.getStCategory()},Integer.class);
			System.out.println("REV ID IS "+reversal_id);
			List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_CRITERIA, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
			
			
			String compare_cond = getCondition(knockoff_Criteria);
			System.out.println("COMPARE CONDITION IS "+compare_cond);
			
			RECON_RECORDS = RECON_RECORDS_PART1 + RECON_RECORDS_PART2 + compare_cond + " )";
			
			System.out.println("RECON_RECORDS IS "+RECON_RECORDS);
			con = getConnection();
			psrecon = con.prepareStatement(RECON_RECORDS);
			rs = psrecon.executeQuery();
			
			PreparedStatement settlement_pstmt = con.prepareStatement(RECON_RECORDS);
			ResultSet settlement_set = settlement_pstmt.executeQuery();
			
//DELETE RECON RECORDS OF PREVIOUS DAY FROM SETTLEMENT TABLE
			String DELETE_PREV_RECORDS = "DELETE FROM SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file_name
						+" WHERE DCRS_REMARKS = '"+manualBeanObj.getStMergerCategory()+"-UNRECON'" +
										//" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
			" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" +manualBeanObj.getStFile_date()+"'";
			//"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+stFile_date+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
			
			System.out.println("DELETE PREV RECORDS FROM SETTLEMENT "+DELETE_PREV_RECORDS);
			
			getJdbcTemplate().execute(DELETE_PREV_RECORDS);
			
			System.out.println("DELETED RECORDS OF PREV DAY");
			
			
			
			RECON_INSERT = "INSERT INTO RECON_"+manualBeanObj.getStMergerCategory()+"_"+file_name+"( "+stSelect_Headers 
					+" ) "+RECON_RECORDS;
			
			System.out.println("RECON INSERT QUERY IS "+RECON_INSERT);
					//SELECT SYSDATE,'"+manualBeanObj.getStEntryBy()+"',TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1";
			getJdbcTemplate().execute(RECON_INSERT);
			
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file_name+" ( DCRS_REMARKS,"+stSelect_Headers+") "+
								"SELECT '"+manualBeanObj.getStMergerCategory()+"-UNRECON',"+stSelect_Headers+" FROM RECON_"+manualBeanObj.getStMergerCategory()+"_"+file_name;
			
			System.out.println("settlement INSERT QUERY IS "+SETTLEMENT_INSERT);
			getJdbcTemplate().execute(SETTLEMENT_INSERT);
			
			
			
			
			
//3. *************** INSERT RECORDS IN RECON TABLE
		/*	RECON_INSERT = "INSERT INTO RECON_"+manualBeanObj.getStMergerCategory()+"_"+file_name+"( "+recon_columns 
					+" ) VALUES (SYSDATE,'"+manualBeanObj.getStEntryBy()+"',TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1";
			SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+manualBeanObj.getStCategory()+"_"+file_name+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+col_headers
					+") VALUES(SYSDATE,'"+manualBeanObj.getStEntryBy()+"',TO_DATE('"+manualBeanObj.getStFile_date()+"','DD/MM/YYYY'),'"+manualBeanObj.getStMergerCategory()+"-UNRECON'";
		
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
			
			
			System.out.println("INSERT CONDITION IS "+RECON_INSERT);
			System.out.println("start time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
	
			insertBatch(RECON_INSERT, rs, col_names, "");
			System.out.println("End time FOR INSERTING RECON DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			System.out.println("start time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(SETTLEMENT_INSERT, settlement_set, col_names, "");

			System.out.println("End time FOR INSERTING RECON DATA IN SETTLEMENT TABLE"+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			recon_columns = ""; 
					
			rs = null;
			psrecon = null;
*/
			
			loop++;
			
			//TRUNCATE TABLES
			/*String TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name+"_KNOCKOFF";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+table_name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+table_name+"_MATCHED";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+table_name;
			getJdbcTemplate().execute(TRUNCATE_QUERY);*/
			/*if(a[1].equals("CBS"))
			{
			TRUNCATE_QUERY = "TRUNCATE TABLE "+a[0]+"_MANUAL_"+a[1];
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+"MANUAL_"+a[1]+"_DATA";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			System.out.println("--------------------------------- TRUNCATED TABLE-----------------------------------------------------------------");
			}*/
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

private static class MatchParamMaster1 implements RowMapper<CompareBean> {

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

private static class MatchParamMaster2 implements RowMapper<CompareBean> {

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



/*public String getReconParameters(String stFileName,ManualFileBean manFileBeanObj,int inRec_Set_Id)
{
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
			//String a[] = sttable_Name.split("_"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFileName , manFileBeanObj.getStCategory(),manFileBeanObj.getStSubCategory() },Integer.class);
			System.out.println("file id is "+file_id);

			//2. Get Parameters from db
			
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id,inRec_Set_Id}, new CompareConditionMaster());
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
		System.out.println("eXCEPTION IS "+e);
	}
	return stcompare_con ;	
}*/

//public String getReconParameters(CompareBean comparebeanObj,String stfile_name,int inRec_set_Id)
public String getReconParameters(String stFileName,ManualFileBean manFileBeanObj,int inRec_Set_Id)
//String stFileName,ManualFileBean manFileBeanObj,int inRec_Set_Id
{
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
		//	String a[] = sttable_Name.split("-"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFileName,manFileBeanObj.getStCategory(),manFileBeanObj.getStSubCategory() },Integer.class);
			System.out.println("file id is "+file_id);

			//2. Get Parameters from db
			
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id,inRec_Set_Id}, new CompareConditionMaster());
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
						stcompare_con = stcompare_con + "(TRIM(OS1."+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()
										+" '"+filterBeanObj.getStSearch_pattern()+"'";
					}
					
				}
				
				for(int j= (i+1); j <compare_cond.size(); j++)
				{
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
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
				//System.out.println("i value is "+i);
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
				
			//	System.out.println("condition is "+condition);
			
			}

			System.out.println("compare condition is "+stcompare_con);
			
			
	}
	catch(Exception e)
	{
		System.out.println("eXCEPTION IS "+e);
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


public String getCompareCondition(String stFile_Name,ManualFileBean manFileBeanObj,int inRec_Set_Id)
{
	String temp_param = "";
	String stcompare_con = "";
	//String COMPARE_CONDITION = "";
	try
	{
			
			//String a[] = sttable_Name.split("_"); 
			//1.Get File ID from db
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {stFile_Name,manFileBeanObj.getStCategory(),manFileBeanObj.getStSubCategory() },Integer.class);
			System.out.println("file id is "+file_id);

			//2. Get Parameters from db
			
		//	List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_RECON_CONDITION, new Object[]{file_id}, new CompareConditionMaster());
			List<FilterationBean> compare_cond = getJdbcTemplate().query(GET_COMPARE_CONDITION, new Object[]{file_id,inRec_Set_Id}, new CompareConditionMaster());


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
					//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
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
		System.out.println("eXCEPTION IS "+e);
	}
	return stcompare_con ;	
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
				System.out.println("column name is "+columns[i-1].trim()+"column value is "+rset.getString(columns[i-1].trim()));
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


public void getReconRecords(KnockOffBean knockOffBean)
{
	try
	{
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { knockOffBean.getStFile_Name(),knockOffBean.getStCategory(),knockOffBean.getStSubCategory() },Integer.class);

		String stFile_headers = "SEG_TRAN_ID,"+getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class).trim();
		
		if(knockOffBean.getStFile_Name().equals("CBS"))
		{
			stFile_headers = "MAN_CONTRA_ACCOUNT," + stFile_headers;
		}

		// GET RECON RECORDS OF PREVIOUS DAY FROM SETTLEMENT TABLE AND INSERT IN ONLINE ONUS TABLE
		String GET_RECON_RECORDS = "SELECT * FROM SETTLEMENT_"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" WHERE DCRS_REMARKS = '"+knockOffBean.getStMergeCategory()+"-UNRECON' " +
			//	" AND TO_CHAR(CREATEDDATE,'DD/MM/YYYY')=TO_CHAR(SYSDATE-1,'DD/MM/YYYY')";
			" AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '" +knockOffBean.getStFile_date()+"'";
			//"TO_CHAR(TO_DATE(TO_CHAR(TRUNC(TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY'),'DD'),'DD/MM/YYYY'),'DD/MM/YYYY')-1,'DD/MM/YYYY')";
		System.out.println("GET RECON RECORDS QUERY "+GET_RECON_RECORDS);
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(GET_RECON_RECORDS);
		ResultSet rset = pstmt.executeQuery();

		String INSERT_QUERY = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()
				+" (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+") VALUES (SYSDATE,'"+knockOffBean.getStEntry_by()+"',TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY')";
		String[] col_names = stFile_headers.split(",");

		for(int i=0;i<col_names.length;i++)
		{
			INSERT_QUERY = INSERT_QUERY + ",?";
		}
		INSERT_QUERY = INSERT_QUERY + ")";
		System.out.println("INSERT RECON RECORDS QUERY "+INSERT_QUERY);

		System.out.println("START TIME FOR INSERTING RECON RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
		insertBatch(INSERT_QUERY, rset, col_names, "");
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


/***
 * KNOCKOFF LOGIC
 * @param stTable_Name
 * @return
 */
public void KnockOffRecords(KnockOffBean knockOffBean)throws Exception
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
				int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { knockOffBean.getStFile_Name(),knockOffBean.getStCategory(),knockOffBean.getStSubCategory() },Integer.class);
			//	System.out.println("file Id is "+file_id);
				
				// Get Id from compare Master as it is unique in all compare tables
				int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), knockOffBean.getStCategory()},Integer.class);
			//	System.out.println("REVERSAL id is "+reversal_id);

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
	/*	System.out.println("check udate headers ");
		for(int k=0;k<Update_Headers.size();k++)
		{
			System.out.println("header "+k+" : "+Update_Headers.get(k).getStReversal_header());
		}*/
	//	System.out.println("SELECT PARAMETERS ARE "+select_parameters);
		
		condition = getCondition(knockoff_Criteria);
		
		
		//GET_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 WHERE KNOCKOFF_FLAG = 'N'";
		GET1_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS1 ";
		GET2_DUPLICATES1 = "SELECT * FROM "+knockOffBean.getStCategory() +"_"+ knockOffBean.getStFile_Name()+" OS2 ";
		//System.out.println("GET RECORDS QUERY IS "+GET_DUPLICATES1);
		System.out.println("duplicate condition1 : "+duplicate1_condition);
		
		if(!duplicate1_condition.equals(""))
		{
			GET1_DUPLICATES1 = GET1_DUPLICATES1 + " WHERE "+duplicate1_condition;
		}
		
		System.out.println("DUPLICATE 1 : "+GET1_DUPLICATES1);
		//create duplicate2 query
				GET1_DUPLICATES2 = "SELECT * FROM "+ knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" OS2 ";
				GET2_DUPLICATES2 = "SELECT * FROM "+ knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+" OS1 ";
				
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
			PART1_DUPLICATES = GET1_DUPLICATES1 + " AND (trunc(OS1.CREATEDDATE) = trunc(SYSDATE)" +
					" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+knockOffBean.getStFile_date()+"') AND EXISTS ( "
					+GET1_DUPLICATES2+" AND trunc(OS2.CREATEDDATE) = trunc(SYSDATE)" +
							" AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+knockOffBean.getStFile_date()+"')";
			String PART2_DUPLICATES = GET2_DUPLICATES1 + " AND (trunc(OS2.CREATEDDATE) = trunc(SYSDATE)" +
					" AND TO_CHAR(OS2.FILEDATE,'DD/MM/YYYY') = '"+knockOffBean.getStFile_date()+"') AND EXISTS ( "
					+GET2_DUPLICATES2+" AND trunc(OS1.CREATEDDATE) = trunc(SYSDATE)" +
							" AND TO_CHAR(OS1.FILEDATE,'DD/MM/YYYY') = '"+knockOffBean.getStFile_date()+"')";
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
			
			/******INSERT THESE RECORDS IN NEW KNOCKOFF TABLE
			 * 
			 */
	//1. create new knockoff table if not created

			
				String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
				//String stFile_headers = getJdbcTemplate().queryForObject(GET_COLS,new Object[]{file_id}, String.class);
				columns = "SEG_TRAN_ID"+","+stFile_headers;
				//System.out.println("stfile headers are "+stFile_headers);
				if(knockOffBean.getStFile_Name().equals("CBS"))
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
				String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF'";
				tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
				//System.out.println("table exists value is "+tableExist);
				if(tableExist == 0)
				{
					//create temp table
					query = "CREATE TABLE "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF ("+tableCols+")";
					//System.out.println("CREATE QUERY IS "+query);
					PreparedStatement pstmt1 = conn.prepareStatement(query);
					pstmt1.execute();
					System.out.println("Table is Created");
					pstmt1 = null;
					
				}

			//CREATING INSERT QUERY FOR KNOCKOFF TRANSACTIONS
				/*INSERT_RECORDS = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF (CREATEDDATE,CREATEDBY,"+columns+") VALUES(" +
								  new java.sql.Timestamp(new java.util.Date().getTime())+","+knockOffBean.getStEntry_by()+",?";
				*/
				col_names = columns.split(",");
				String INSERT_RECORDS = "INSERT INTO "+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()+"_KNOCKOFF" +
						" (CREATEDDATE , CREATEDBY ,FILEDATE, "+columns+") VALUES(sysdate,'"+knockOffBean.getStEntry_by()+"',TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY')";
				String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+knockOffBean.getStCategory()+"_"+knockOffBean.getStFile_Name()
						+" (CREATEDDATE, CREATEDBY,FILEDATE, DCRS_REMARKS, "+columns 
						+ " ) VALUES (SYSDATE, '"+knockOffBean.getStEntry_by()+"' , TO_DATE('"+knockOffBean.getStFile_date()+"','DD/MM/YYYY'), 'MAN-KNOCKOFF'";
		
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
				insertBatch(INSERT_RECORDS, knockoff_records1, col_names, "");
				insertBatch(SETTLEMENT_INSERT, settlement_records1, col_names, "");
				//updateBatch(SETTLEMENT_INSERT, settlement_records1, columns);
				insertBatch(INSERT_RECORDS, knockoff_records2, col_names, "");
				insertBatch(SETTLEMENT_INSERT, settlement_records2, col_names, "");
				//updateBatch(INSERT_RECORDS, knockoff_records2, columns);
				//updateBatch(SETTLEMENT_INSERT, settlement_records2, columns);
				System.out.println("END TIME FOR INSERTING KNOCKOFF RECORDS "+new java.sql.Timestamp(new java.util.Date().getTime()));
				
				System.out.println("KNOCKOFF COMPLETED");
			
/***************************** REMOVING UPDATION AS IT IS TIME CONSUMING TASK			
		
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
		 */
	}
	catch(Exception e)
	{
		System.out.println("EXCEPTION IS "+e);
	}
}


public int FilterRecords(FilterationBean filterBean)throws Exception
{
	String columns = "CREATEDDATE, CREATEDBY,FILEDATE,MAN_CONTRA_ACCOUNT";
	String tableCols = "CREATEDDATE DATE, CREATEDBY VARCHAR2(100 BYTE),FILEDATE DATE";
	Connection conn = null;
	try
	{

		try
		{
			conn = getConnection();
		//FilterationBean filterbean = new FilterationBean();
		/*String a[] = stTable_Name.split("_");
		filterbean.setStFile_Name(a[1]);
		filterbean.setStCategory(a[0]);
		filterbean.setStEntry_by("INT5779");*/
		
		List<FilterationBean> search_params = new ArrayList<>();
		search_params = getSearchParams(filterBean);
		String query = "", condition = "";
		String Table_name = "";
		
		//1. GET THE HEADERS FROM DB
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBean.getStFile_Name(), filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
		//System.out.println("file Id is "+file_id);
		
		String stFile_headers = getJdbcTemplate().queryForObject(GET_FILE_HEADERS,new Object[]{file_id}, String.class);
		
		
		Table_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{file_id}, String.class);
		
		Table_name = "MANUAL_"+Table_name;
		
		//System.out.println("TABLE NAME IS "+Table_name);
		
		String temp_param = "";
		String condition1= "";
		for(int i = 0; i<search_params.size();i++){
			condition1 ="";
			FilterationBean filterBeanObj = new FilterationBean();
			filterBeanObj = search_params.get(i);
			temp_param = filterBeanObj.getStSearch_header().trim();
			
			if((filterBeanObj.getStSearch_padding().trim()).equals("Y"))
			{
				if((filterBeanObj.getStSearch_Condition().trim()).equals("="))
				{
					condition1 = condition1 + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
						filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+"'"+filterBeanObj.getStSearch_pattern().trim()+"' ";
				}
				else if((filterBeanObj.getStSearch_Condition().trim()).equalsIgnoreCase("like"))
				{
					condition1 = condition1 + "(SUBSTR(TRIM("+filterBeanObj.getStSearch_header()+"),"+filterBeanObj.getStsearch_Startcharpos()+","+
							filterBeanObj.getStsearch_Endcharpos()+") "+filterBeanObj.getStSearch_Condition().trim()+
							"'%"+filterBeanObj.getStSearch_pattern().trim()+"%' ";
				}
				else
				{
					if(i == (search_params.size()-1))
					{
						condition1 = condition1 + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
									filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition1 = condition1 + "(SUBSTR(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')),"+filterBeanObj.getStsearch_Startcharpos()+","+
								filterBeanObj.getStsearch_Endcharpos()+") "+"NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
					}
				}
			}
			else
			{
				if(filterBeanObj.getStSearch_Condition().equals("="))
				{
					condition1 = condition1 + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" '"+
								filterBeanObj.getStSearch_pattern().trim()+"'";
				}
				else if(filterBeanObj.getStSearch_Condition().equalsIgnoreCase("like"))
				{
					condition1 = condition1 + "(TRIM("+filterBeanObj.getStSearch_header()+") "+filterBeanObj.getStSearch_Condition().trim()+" "+
								"'%"+filterBeanObj.getStSearch_pattern().trim()+"%'";
				}
				else
				{
					if(i == (search_params.size()-1))
					{
						condition1 = condition1 + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"') ";
					}
					else
					{
						condition1 = condition1 + "(TRIM(NVL("+filterBeanObj.getStSearch_header()+",'!NULL!')) "+" NOT IN ('"+filterBeanObj.getStSearch_pattern().trim()+"' ";
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
							condition1 = condition1 + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
								search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+ 
								"'"+search_params.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition1 = condition1 + " OR SUBSTR(TRIM(" + search_params.get(j).getStSearch_header()+") , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition().trim()+
									"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
						}
						else
						{
							if(j==(search_params.size()-1))
							{	
								/*condition = condition + " OR SUBSTR(" + search_params.get(j).getStSearch_header()+" , "+search_params.get(j).getStsearch_Startcharpos()+","+
									search_params.get(j).getStsearch_Endcharpos()+") "+search_params.get(j).getStSearch_Condition()+ search_params.get(j).getStSearch_pattern();*/
								condition1 = condition1 + ", '"+search_params.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition1 = condition1 + ", '"+search_params.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					else
					{
						if((search_params.get(j).getStSearch_Condition().trim()).equals("="))
						{
							condition1 = condition1 + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
										search_params.get(j).getStSearch_Condition().trim()+" '"+search_params.get(j).getStSearch_pattern().trim()+"'";
						}
						else if((search_params.get(j).getStSearch_Condition().trim()).equalsIgnoreCase("like"))
						{
							condition1 = condition1 + " OR TRIM(" + search_params.get(j).getStSearch_header()+") "+
									search_params.get(j).getStSearch_Condition().trim()+" "+
									"'%"+search_params.get(j).getStSearch_pattern().trim()+"%'";
							
						}
						else
						{
							if(j==(search_params.size()-1))
							{
								condition1 = condition1 + " , '" +search_params.get(j).getStSearch_pattern().trim()+"')";
							}
							else
							{
								condition1 = condition1 + " , '" +search_params.get(j).getStSearch_pattern().trim()+"' ";
							}
							
						}
					}
					
				
					
						i = j;
				}
				
			}
			
			//replace contra_account column by man_contra_account IF ANY
			if(condition1.contains("CONTRA_ACCOUNT"))
			{
				System.out.println("CONTRA_ACCOUNT present in condition");
				condition1 = condition1.replaceAll("CONTRA_ACCOUNT", "MAN_CONTRA_ACCOUNT");
				System.out.println("NOW THE CONDITION IS "+condition1);
			}
			
			//System.out.println("i value is "+i);
			if(i != (search_params.size())-1)
			{
				if(search_params.get(i).getStSearch_Condition().equals("!="))
				{
					condition = condition + condition1  + " ) ) AND ";
				}
				else
					condition = condition + condition1 + " ) AND ";
			}
			else
			{
				condition = condition+ condition1 +")";
			}
			
			System.out.println("condition is "+condition);
		}
		
		
		
		query = "SELECT * FROM "+Table_name;
		//System.out.println("table name is "+query);
		
		if(!condition.equals(""))
		{
			query = query + " WHERE "+condition +" AND (TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')) " +
					"AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterBean.getStFile_date()+"'";
					//"' OR TO_CHAR(NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE , 'DD/MM/YYYY'))" ;
			//query = query + " WHERE "+condition;
			
		}
		else
			query = query + " WHERE (TO_CHAR(CREATEDDATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')) " +
					"AND TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+filterBean.getStFile_date()+"'";
				//	+"' OR TO_CHAR(NEXT_TRAN_DATE,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY'))" ;
		System.out.println("CATEGORIZATION QUERY IS "+query);
		
		//2.. get data from raw table
			Connection conn1 = getConnection();
			PreparedStatement pstmt1 = conn1.prepareStatement(query);
			ResultSet filtered_records = pstmt1.executeQuery();
			//System.out.println("got the resultset");
			
			PreparedStatement settlement_pstmt = conn1.prepareStatement(query);
			ResultSet settlement_rset = settlement_pstmt.executeQuery();

			//check whether table is present if not then create it
			
			stFile_headers = stFile_headers + ",MAN_CONTRA_ACCOUNT";
			columns = columns+","+stFile_headers;
			
		//	System.out.println("stfile headers are "+stFile_headers);
			
			
			String[] col_names = stFile_headers.trim().split(",");
			
			for(int m = 0 ;m <col_names.length; m++)
			{
				tableCols = tableCols +","+ col_names[m]+" VARCHAR (100 BYTE) ";
			}
			
			String CHECK_TABLE = "SELECT count (*) FROM tab WHERE tname  = '"+filterBean.getStCategory()
						+"_MANUAL_"+filterBean.getStFile_Name().toUpperCase()+"'";
			
		//	System.out.println("CHECK TABLE QUERY "+CHECK_TABLE);
			
			int tableExist = getJdbcTemplate().queryForObject(CHECK_TABLE, new Object[] { },Integer.class);
			System.out.println("table exists is "+tableExist);
			if(tableExist == 0)
			{
				//create MAN RAW table
				String create_query = "CREATE TABLE "+filterBean.getStCategory()+"_MANUAL_"+filterBean.getStFile_Name().toUpperCase()+" ("+tableCols+")";
				System.out.println("CREATE QUERY IS "+create_query);
				PreparedStatement pstable = conn.prepareStatement(create_query);
				pstable.execute();
				System.out.println("table created !!!!!!!!!!!!!!!!");
				pstable = null;
				
			}

		//3. Insert the data in batch obtained from RAW table into category_filename Table
			ADD_RECORDS = "INSERT INTO "+filterBean.getStCategory()+"_MANUAL_"+filterBean.getStFile_Name()
					+" (CREATEDDATE,CREATEDBY,FILEDATE,"+stFile_headers+") VALUES(sysdate,'"+filterBean.getStEntry_by()+"',TO_DATE('"+filterBean.getStFile_date()+"','DD/MM/YYYY')";
			String SETTLEMENT_INSERT = "INSERT INTO SETTLEMENT_"+filterBean.getStCategory()+"_"+filterBean.getStFile_Name()
					+" (CREATEDDATE,CREATEDBY,FILEDATE,DCRS_REMARKS,"+stFile_headers+") VALUES(sysdate,'"+filterBean.getStEntry_by()
					+"',TO_DATE('"+filterBean.getStFile_date()+"','DD/MM/YYYY'),'MAN-"+filterBean.getStMerger_Category()+"'";
			
			//String[] columns = stFile_headers.split(",");
			for(int i = 1 ; i<= col_names.length; i++)
			{
				
					ADD_RECORDS = ADD_RECORDS + ",?";
					SETTLEMENT_INSERT = SETTLEMENT_INSERT + ",?";
				
			}
			ADD_RECORDS = ADD_RECORDS+")";
			SETTLEMENT_INSERT = SETTLEMENT_INSERT+")";
			System.out.println("query is "+ADD_RECORDS);
			
			
			System.out.println("start time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(ADD_RECORDS, filtered_records, col_names, "");
			System.out.println("End time FOR INSERTING RAW DATA "+new java.sql.Timestamp(new java.util.Date().getTime()));
			
			System.out.println("INSERT IN SETTLEMENT TABLE QUERY "+SETTLEMENT_INSERT);
			
			System.out.println("start time FOR INSERTING ONUS DATA IN SETTLEMENT "+new java.sql.Timestamp(new java.util.Date().getTime()));
			insertBatch(SETTLEMENT_INSERT, settlement_rset, col_names, "");
			System.out.println("End time FOR INSERTING ONUS DATA IN SETTLEMENT "+new java.sql.Timestamp(new java.util.Date().getTime()));
		
		
		
		}
		catch(Exception e)
		{
			//throw new Exception(e);
			System.out.println("EXCEPTION IN FILTERATION "+e);
		}
		return 1;
		
	
		
		
	}
	catch(Exception e)
	{
		System.out.println("Exception in Filteeration "+e);
		return 0;
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

public List<FilterationBean> getSearchParams(FilterationBean filterBean)throws Exception
{
	//1. Get File id from db
	int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { filterBean.getStFile_Name(),filterBean.getStCategory(),filterBean.getStSubCategory() },Integer.class);
	//System.out.println("file Id is "+file_id);
	
	//2. Get Id from compare Master as it is unique in all compare tables
	int compare_id;
	if(!filterBean.getStSubCategory().equals("-"))
	{
		compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()+"_"+filterBean.getStSubCategory()},Integer.class);
	}
	else
	{
		compare_id = getJdbcTemplate().queryForObject(GET_COMPARE_ID, new Object[] { (file_id), filterBean.getStCategory()},Integer.class);
	}
	//System.out.println("Compare id is "+compare_id);
	
	//System.out.println("file id "+file_id+"compare id "+compare_id);
	
	//3. Get All search Parameters from compare_details table
	List<FilterationBean> search_params = getJdbcTemplate().query(GET_SEARCH_PARAMS, new Object[] { compare_id , file_id}, new SearchParameterMaster());
	//System.out.println("SEARCH PARAMETERS ARE "+GET_SEARCH_PARAMS);
	//System.out.println("search list size is "+search_params.size());
	
	return search_params;
	
}

private static class SearchParameterMaster implements RowMapper<FilterationBean> {

	@Override
	public FilterationBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		FilterationBean filterBean = new FilterationBean();
		
		filterBean.setStSearch_header(rs.getString("FILE_HEADER"));
		filterBean.setStSearch_pattern(rs.getString("SEARCH_PATTERN"));
		filterBean.setStSearch_padding(rs.getString("PADDING"));
		//filterBean.setStsearch_charpos(rs.getString("CHARPOSITION"));
		filterBean.setStsearch_Startcharpos(rs.getString("START_CHARPOSITION"));
		filterBean.setStsearch_Endcharpos(rs.getString("END_CHARPOSITION"));
		filterBean.setStSearch_Condition(rs.getString("CONDITION"));
		
	
		return filterBean;
		
		
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



public void insertBatch(final String QUERY, final ResultSet rset,final String[] columns, final String task){
	PreparedStatement ps;
	Connection con;
	
	//System.out.println("query is 123 "+QUERY);
	int flag = 1;
	int batch = 1;
	
	try {
		
		con = getConnection();
		
		ps = con.prepareStatement(QUERY);
		
		if(task.equals("MATCH"))
		{
			System.out.println("IN IF");
			while(rset.next())
			{
				//CHECK FOR DIFFERENT DATE ISSUE OR CAN SAY DUPLICATE RECORDS ISSUE
				String cbs_date = rset.getString("TRAN_PARTICULAR");
				String switch_date = rset.getString("LOCAL_DATE");
				
				DateFormat cbs_originalFormat = new SimpleDateFormat("dd-MM-yy");
				 DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
				 //Date date = originalFormat.parse("30-04-17");
				 Date date1 = cbs_originalFormat.parse(cbs_date.substring(9, 18).trim());
				 //System.out.println("cbs_date.substring(10, 18).trim() "+cbs_date.substring(10, 18).trim());
				 
				 
				 DateFormat switch_originalFormat = new SimpleDateFormat("MM/dd/yyyy");
				 Date date2 = switch_originalFormat.parse(switch_date);
				 
				 
				 String formattedDate1 = targetFormat.format(date1);
				 String formattedDate2 = targetFormat.format(date2);
				 
				 
				 
				if(formattedDate1.equals(formattedDate2))
				{
					

				flag++;
				ps.setString(1, rset.getString("ACCTNUM"));
				/*if(value == columns.length)
				 value = 3;*/

				for(int i = 1 ; i <= columns.length; i++)
				{
					/*System.out.println("column name is "+columns[i-1].trim());
					System.out.println("column value is "+rset.getString(columns[i-1].trim()));*/
					ps.setString((i+1), rset.getString(columns[i-1].trim()));


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
			}
			ps.executeBatch();
		}
		else
		{
			System.out.println("IN ELSE");
			
			while(rset.next())
			{

				flag++;

				for(int i = 1 ; i <= columns.length; i++)
				{
					ps.setString((i), rset.getString(columns[i-1].trim()));

					
					//	value++;
					//	System.out.println("i is "+i);
				}
				//System.out.println("hey");
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
		}
		//System.out.println("completed insertion");
		
		
	} catch (DataAccessException | SQLException | ParseException e) {
		
		e.printStackTrace();
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


public void clearTables(List<String> tables,ManualFileBean manualFileObj)throws Exception
{
	try
	{
		for(int i = 0 ;i<tables.size();i++)
		{
			int table_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] {tables.get(i),manualFileObj.getStCategory(),
								manualFileObj.getStSubCategory() },Integer.class);
			String stRawTable_name = getJdbcTemplate().queryForObject(GET_TABLE_NAME , new Object[]{table_file_id}, String.class);
			
			
			String TRUNCATE_QUERY = "TRUNCATE TABLE "+manualFileObj.getStMergerCategory()+"_"+tables.get(i);
			System.out.println("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+manualFileObj.getStMergerCategory()+"_"+tables.get(i)+"_KNOCKOFF";
			System.out.println("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE "+manualFileObj.getStMergerCategory()+"_"+tables.get(i)+"_MATCHED";
			System.out.println("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE TEMP_"+manualFileObj.getStMergerCategory()+"_"+tables.get(i);
			System.out.println("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			TRUNCATE_QUERY = "TRUNCATE TABLE RECON_"+manualFileObj.getStMergerCategory()+"_"+tables.get(i);
			System.out.println("TRUNCATE QUERY IS "+TRUNCATE_QUERY);
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			/*TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_CBS_"+manualFileObj.getStCategory()+"_RAWDATA";
			getJdbcTemplate().execute(TRUNCATE_QUERY);*/
			TRUNCATE_QUERY = "TRUNCATE TABLE "+manualFileObj.getStCategory()+"_MANUAL_CBS";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			
			if(tables.get(i).equals("CBS"))
			{
				TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+stRawTable_name;
				getJdbcTemplate().execute(TRUNCATE_QUERY);
			}
			/*TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_CBS_"+manualFileObj.getStCategory()+"_RAWDATA";
			getJdbcTemplate().execute(TRUNCATE_QUERY);*/
			
			
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Exception while clearing tables "+e);
	}
}

@Override
public void CleanTables(CompareBean comparebeanObj) {
	
	try
	{
		if(comparebeanObj.getStCategory().equals("RUPAY"))
		{
			if(comparebeanObj.getStSubCategory().equals("DOMESTIC") || comparebeanObj.getStSubCategory().equals("SURCHARGE"))
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
						
			TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_DOM_CBS";
			getJdbcTemplate().execute(TRUNCATE_QUERY);
			}
			else
			{

				String TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_CBS_MATCHED1";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_SWITCH_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE "+comparebeanObj.getStCategory()+"_INT_RUPAY_MATCHED2";
				getJdbcTemplate().execute(TRUNCATE_QUERY);
				
				TRUNCATE_QUERY = "TRUNCATE TABLE MANUAL_"+comparebeanObj.getStCategory()+"_DOM_CBS";
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


/*public void TTUMRecords(List<String> Table_list,CompareBean compareBeanObj,int rec_set_id)throws Exception*/
public void TTUMRecords(List<String> Table_list,ManualFileBean manualBeanObj,int rec_set_id)throws Exception
{
	System.out.println("TTUM************************************************************************************************************************");
	String JOIN1_QUERY = "";
	
	try
	{
		//manualBeanObj.setInrec_set_id(rec_set_id);
		//String stTable1_Name = Table_list.get(0);
		//String stTable2_Name = Table_list.get(1);
		/*String[] stTable1 = stTable1_Name.split("_");
		String[] stTable2 = stTable2_Name.split("_");*/
		String stCategory = manualBeanObj.getStMergerCategory();
		String stFile1_Name = "CBS";
		String stFile2_Name = "SWITCH";
		String table1_condition = "";
		String table2_condition= "";
		String condition = "";
		System.out.println("TTUM STARTS HERE *************************************************");
		System.out.println(manualBeanObj.getStSubCategory());
		System.out.println(manualBeanObj.getStMergerCategory());
		int table1_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile1_Name , stCategory ,manualBeanObj.getStSubCategory()},Integer.class);
		int table2_file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { stFile2_Name, stCategory,manualBeanObj.getStSubCategory() },Integer.class);
		//generatettumBeanObj.getInRec_Set_Id()
	//	List<CompareBean> match_Headers = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,table2_file_id,table2_file_id,table1_file_id,a[0]},new MatchParameterMaster()); 
		List<CompareBean> match_Headers1 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table1_file_id,stCategory,rec_set_id},new MatchParamMaster1());
		List<CompareBean> match_Headers2 = getJdbcTemplate().query(GET_MATCH_PARAMS , new Object[]{table2_file_id,stCategory,rec_set_id},new MatchParamMaster2());
				
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
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) FROM SETTLEMENT_"+manualBeanObj.getStMergerCategory()+"_"+stFile1_Name 
								+" WHERE  SUBSTR( "+match_Headers1.get(i).getStMatchTable1_header().trim()+","+match_Headers1.get(i).getStMatchTable1_startcharpos()+","+
								match_Headers1.get(i).getStMatchTable1_charSize()+" ) IS NOT NULL";
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
								+manualBeanObj.getStMergerCategory()+
								"_"+stFile1_Name +" WHERE "+match_Headers1.get(i).getStMatchTable1_header().trim()+" IS NOT NULL";
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
			/*System.out.println("i value is "+i);
			System.out.println("match headers length is "+match_Headers2.size());
			System.out.println("padding in match headers 2 is "+match_Headers2.get(i).getStMatchTable2_Padding());*/
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
								+manualBeanObj.getStMergerCategory()+"_"+stFile2_Name + " WHERE SUBSTR( "+match_Headers2.get(i).getStMatchTable2_header().trim()+","+
						match_Headers2.get(i).getStMatchTable2_startcharpos()+" , "+match_Headers2.get(i).getStMatchTable2_charSize()+" ) IS NOT NULL";
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
				System.out.println("datatype is "+match_Headers2.get(i).getStMatchTable2_Datatype());
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
								+manualBeanObj.getStMergerCategory()+
								"_"+stFile2_Name+" WHERE "+match_Headers2.get(i).getStMatchTable2_header().trim()+" IS NOT NULL";
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
				System.out.println(condition);
				
			}
			else
			{
				//condition = condition + "t1."+match_Headers.get(i).getStMatchTable1_header() + " = t2."+match_Headers.get(i).getStMatchTable2_header()+" AND ";
				condition = condition +" ("+ table1_condition +" = "+table2_condition +") AND ";
				System.out.println(condition);
			
			}
			
			
		}
		
		
		
		System.out.println("FINALLY CONDITION IS "+condition);
		
			
	/*	JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE T1.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()
				+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY')"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON'" +
				" AND t2.DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"'";*/
		
		//CHANGES MADE FOR VALUE_DATE
		JOIN1_QUERY = "SELECT * FROM SETTLEMENT_"+manualBeanObj.getStMergerCategory()+"_"+stFile1_Name + " t1 INNER JOIN SETTLEMENT_"
				+manualBeanObj.getStMergerCategory()+"_"+stFile2_Name+ " t2 ON( "+condition 
				+ " ) WHERE "+
						" TO_CHAR(T1.FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()+"'"
				//+" AND T2.FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"' ,'DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()+"','DD/MM/YYYY') "
				+" AND t1.DCRS_REMARKS = '"+manualBeanObj.getStMergerCategory()+"-UNRECON'" +
				" AND t2.DCRS_REMARKS = '"+manualBeanObj.getStMergerCategory()+"'";
		
		//GET TTUM CONDITION
		String cond1 = getTTUMCondition(table1_file_id);
		String cond2 = getTTUMCondition(table2_file_id);
	
		System.out.println("----------------------------------------------------------------------------------- DONE ---------------------------------------------");

		//QUERY = "SELECT * FROM TEMP_"+table1_name + " t1 INNER JOIN TEMP_"+table2_name + " t2 ON( "+condition + " ) WHERE T2.MATCHING_FLAG = 'N'";
		System.out.println("COMPARE QUERY IS****************************************");
		
		
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
		System.out.println("JOIN1 QUERY IS "+JOIN1_QUERY);
		//System.out.println("JOIN2_QUERY IS "+JOIN2_QUERY);
		
		//get failed records for table 1
		
		//getFailedRecords(JOIN1_QUERY, table2_file_id , stCategory, stFile2_Name,stFile1_Name,table1_file_id);
		getFailedRecords(JOIN1_QUERY, table2_file_id , stFile2_Name,stFile1_Name,table1_file_id,manualBeanObj);
		//getFailedRecords(JOIN2_QUERY, table1_file_id , stCategory, stFile1_Name,stFile2_Name,table2_file_id);//for join query 2 pass file id of table 2
		
		//NOW TRUNCATE ALL TABLES----------------------------
		/*System.out.println("--------------------------------- TRUNCATING ALL TABLES------------------------------------------------------");
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
		
		System.out.println("updated old ttum records");*/
		
		
		
	}
	catch(Exception e)
	{
		System.out.println("TTum "+e);
	}
	finally
	{
		
	}
	
}

//public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,CompareBean compareBeanObj)throws Exception
public void getFailedRecords(String QUERY, int file_id,String stFile_Name,String stUpdate_FileName,int inUpdate_File_Id,ManualFileBean manualBeanObj)throws Exception
{
	System.out.println("------------------------------------------------------------getfailedrecords -------------------------------");
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
	
		int reversal_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (file_id), manualBeanObj.getStMergerCategory()},Integer.class);
		System.out.println("reversal id is "+reversal_id);
		
		List<KnockOffBean> knockoff_Criteria = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { reversal_id , file_id}, new KnockOffCriteriaMaster());
		System.out.println("knockoff criteria "+knockoff_Criteria.size());
		List<String> update_queries = new ArrayList<>();
		//CREATE CONDITION USING KNOCKOFF CRITERIA 
		while(rset.next())
		{
			//System.out.println("WHILE STARTS");
			update_condition = "";
			reversal_condition = "";
			stFinal_cond = "";
		
				//code for inserting in settlement table
				/*String UPDATE_QUERY = "UPDATE SETTLEMENT_"+generatettumBeanObj.getStMerger_Category()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()
						+"-UNRECON-GENERATE-TTUM ("+rset.getString("RESPCODE")+
							")' WHERE FILEDATE BETWEEN TO_DATE('"+generatettumBeanObj.getStStart_Date()+"','DD/MM/YYYY') AND TO_DATE('"+generatettumBeanObj.getStEnd_Date()
							+"','DD/MM/YYYY') AND DCRS_REMARKS = '"+generatettumBeanObj.getStMerger_Category()+"-UNRECON' ";*/
			String UPDATE_QUERY  = "";
			
			
				UPDATE_QUERY = "UPDATE SETTLEMENT_"+manualBeanObj.getStMergerCategory()+"_"+stUpdate_FileName+" SET DCRS_REMARKS = '"
							+manualBeanObj.getStMergerCategory()+"-UNRECON ("+rset.getString("RESPCODE")+
						")' WHERE TO_CHAR(FILEDATE,'DD/MM/YYYY') = '"+manualBeanObj.getStFile_date()
						+"' AND DCRS_REMARKS = '"+manualBeanObj.getStMergerCategory()+"-UNRECON' ";
			
				
				int rev_id = getJdbcTemplate().queryForObject(GET_REVERSAL_ID, new Object[] { (inUpdate_File_Id), manualBeanObj.getStMergerCategory()},Integer.class);
			//	System.out.println("reversal id is "+reversal_id);
				
				List<KnockOffBean> knockoff_Criteria1 = getJdbcTemplate().query(GET_KNOCKOFF_PARAMS, new Object[] { rev_id , inUpdate_File_Id}, new KnockOffCriteriaMaster());
				
			//	System.out.println("KNOCKOFF CRITERIA SIZE "+knockoff_Criteria1.size());
				
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
				
				System.out.println("update condition is "+update_condition);
				if(!update_condition.equals(""))
				{
					UPDATE_QUERY = UPDATE_QUERY + " AND " + update_condition;
				}
				System.out.println("UPDATE QUERY IS "+UPDATE_QUERY);
				
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
		System.out.println("while completed");
		
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Exception in getFailedRecords "+e);
	}

	
}

public String getTTUMCondition(int inFile_Id)
{
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
				//System.out.println("CHECK THE VALUE IN J "+j+" value = "+search_params.get(j).getStSearch_header());
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
			//System.out.println("i value is "+i);
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
			
		//	System.out.println("condition is "+condition);
		}
		
		System.out.println("condition is "+condition);
		
		
		
	}
	catch(Exception e)
	{
		System.out.println("Exception is "+e);
		
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

}
