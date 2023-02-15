package com.recon.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.recon.model.KnockOffBean;

@Component
public class GeneralUtil {

	/** User  Message Constants. */
	public static final String ERROR_MSG = "error_msg"; 
	public static final String SUCCESS_MSG = "success_msg"; 

	public static final String SHORT_DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat shortFormat = new SimpleDateFormat(SHORT_DATE_FORMAT);
	Date date;
	
	/**Exception Message Constants. */
	public static final String NO_DATA_FOUND = "No Data Found";
	public static final String NO_SEARCH_CRITERIA_FOUND = "No Search Criteria Found..!";
	
	/**Jtable Response Constants*/
	public static final String ERROR = "ERROR";
	public static final String OK = "OK";
	
	
	public static String GET_TRN_ID = "SELECT TRNID FROM MAIN_PROCESS_STATUS WHERE FILEID=? AND ENTRY_DT = TO_DATE(?,'MM/DD/YYYY')";
	//public static String GET_STATUS = "SELECT * FROM MAIN_FILESOURCE WHERE FILEID = ? ";
	public static final String CHECK_IP = "SELECT COUNT(*) FROM LOGIN_USER WHERE IP_ADDRESS = ?";
	public static final String GET_FILEID = "SELECT MAX(FILEID) FROM MAIN_FILESOURCE";
	public static final String GET_ID = "SELECT MAX(ID) FROM MAIN_COMPARE_MASTER";
	public static final String ADD_COMPARE_DETAILS = "INSERT INTO MAIN_COMPARE_DETAIL(ID, FILE_ID, FILE_HEADER, SEARCH_PATTERN, PADDING, START_CHARPOSITION, END_CHARPOSITION,CONDITION, ENTRY_BY, ENTRY_DT)" +
			" VALUES(?,?,?,?,?,?,?,?,?,?)";
	
	public static final String GET_TABLE_NAME = "SELECT TABLENAME FROM MAIN_FILESOURCE WHERE FILEID = ?";
	public static final String GET_FILE_ID = "select fileid from main_filesource where upper(filename)=? and file_category = ? and file_subcategory = ?";
	public static final String GET_COMPARE_ID = "SELECT ID FROM MAIN_COMPARE_MASTER WHERE FILE_ID = ? AND CATEGORY = ?";
	public static final String GET_SEARCH_PARAMS = "SELECT FILE_HEADER, SEARCH_PATTERN, PADDING, START_CHARPOSITION, END_CHARPOSITION,CONDITION FROM MAIN_COMPARE_DETAIL WHERE ID = ? AND FILE_ID = ? ORDER BY FILE_HEADER";
	public static final String GET_SEG_TRAN_ID = "SELECT MAX(SEG_TRAN_ID) AS SEG_TRAN_ID FROM MAIN_SEGREGATE_TRANSACTION";
	public static final String ADD_SEG_RECORD =  "INSERT INTO MAIN_SEGREGATE_TRANSACTION(SEG_TRAN_ID, ID, TRAN_DATE, ENTRY_DATE, ENTRY_BY,STATUS) VALUES(?,?,?,?,?,'N') ";
	public static final String UPDATE_SEG_RECORD = "UPDATE MAIN_SEGREGATE_TRANSACTION SET STATUS = 'Y' WHERE SEG_TRAN_ID = ?";
	public static final String GET_FILE_HEADERS = "SELECT COLUMNHEADER FROM MAIN_FILEHEADERS WHERE FILEID = ?";
	public static String ADD_RECORDS = ""; 
	
	public static final String GET_REVERSAL_DETAILS = "SELECT HEADER, VALUE FROM MAIN_REVERSAL_DETAIL WHERE REVERSAL_ID = ? AND FILE_ID = ? ORDER BY HEADER";
	public static final String GET_REVERSAL_PARAMS = "SELECT HEADER, VALUE FROM MAIN_REVERSAL_PARAMETERS WHERE REVERSAL_ID = ? AND FILE_ID = ? ORDER BY HEADER";
	public static final String GET_REVERSAL_ID = "SELECT distinct REVERSAL_ID FROM MAIN_REVERSAL_DETAIL WHERE FILE_ID = ? AND CATEGORY = ? ORDER BY REVERSAL_ID DESC";
	public static final String GET_KNOCKOFF_CRITERIA = "SELECT HEADER,PADDING, START_CHARPOSITION, CHAR_SIZE, HEADER_VALUE, CONDITION FROM MAIN_KNOCKOFF_CRITERIA WHERE REVERSAL_ID = ? AND FILE_ID = ? ORDER BY HEADER";

	public static final String GET_COLS = "SELECT COLUMN_NAME FROM USER_TAB_COLS WHERE TABLE_NAME=UPPER(?) and COLUMN_NAME not like ('%$%') ";
	public static final String GET_COLS_COUNT = "SELECT COUNT(*) FROM USER_TAB_COLS WHERE TABLE_NAME=UPPER(?) AND COLUMN_NAME = ?";
	//public static final String GET_MATCH_PARAMS = "SELECT MATCH_HEADER, PADDING, START_CHARPOSITION, END_CHARPOSITION FROM MAIN_MATCHING_CRITERIA WHERE FILE_ID= ? AND CATEGORY = ?";
	/*public static final String GET_MATCH_PARAMS = "SELECT TABLE1_HEADER, TABLE2_HEADER, TABLE1_PADDING,TABLE2_PADDING, TABLE1_START_CHARPOS, TABLE2_START_CHARPOS, TABLE1_CHARSIZE,TABLE2_CHARSIZE, DATATYPE, TABLE1_PATTERN, TABLE2_PATTERN " +
			" FROM MAIN_MATCHING_CRITERIA " +
			" WHERE (TABLE1_FILE_ID= ? AND TABLE2_FILE_ID= ?) OR (TABLE1_FILE_ID = ? AND TABLE2_FILE_ID= ?) " +
			" AND CATEGORY = ?";*/
	public static final String GET_MATCH_PARAMS = "SELECT MATCH_HEADER, PADDING, START_CHARPOS , CHAR_SIZE, DATATYPE, DATA_PATTERN FROM MAIN_MATCHING_CRITERIA" +
								" WHERE FILE_ID = ?  AND CATEGORY = ? AND REC_SET_ID = ?  ORDER BY MATCH_ID   ";
	public static final String GET_MATCH_RELAX_PARAMS = "SELECT MATCH_HEADER, PADDING, START_CHARPOS , CHAR_SIZE, DATATYPE, DATA_PATTERN FROM MAIN_MATCHING_CRITERIA" +
			" WHERE FILE_ID = ?  AND CATEGORY = ? AND REC_SET_ID = ? AND RELAX_PARAM = ? ORDER BY MATCH_ID   ";

	public static final String GET_TTUM_PARAMS = "SELECT FILE_HEADER,VALUE,PADDING,START_POS,CHAR_SIZE,CONDITION FROM MAIN_TTUM_PARAMETERS WHERE FILE_ID = ?";
	
	
	//RECON QUERIES
	public static String CREATE_RECON_TABLE = "";
	public static final String GET_COMPARE_CONDITION = "SELECT TABLE_HEADER, PADDING, START_CHARPOS, CONDITION, CHARSIZE, PATTERN " +
													"FROM MAIN_MATCHING_CONDITION WHERE TABLE_FILE_ID = ? AND REC_SET_ID = ?";
	public static final String GET_RECON_CONDITION = "SELECT TABLE_HEADER, PADDING, START_CHARPOS, CONDITION, CHARSIZE, PATTERN" +
													" FROM MAIN_RECON_PARAM WHERE TABLE_FILE_ID = ? AND REC_SET_ID = ? ORDER BY TABLE_HEADER";
	
	//MANUAL QUERIES
	public static final String GET_MANUAL_PARAM = "SELECT MAN_ID, FILE_HEADER, SEARCH_PATTERN, PADDING , START_CHARPOSITION, CHARSIZE, CONDITION FROM MAIN_MANUAL_MASTER " +
												"WHERE REF_FILE_ID = ? AND MAN_ID = ? ORDER BY MAN_ID";
	public static final String GET_MAN_ID = "SELECT DISTINCT MAN_ID FROM MAIN_MANUAL_MASTER WHERE REF_FILE_ID = ?";
	
	public static final String GET_MAN_MATCHING = "SELECT COMP_ID, FILE_HEADER, PADDING, START_CHARPOSITION, CHARSIZE, DATATYPE, DATA_PATTERN " +
													"FROM MAIN_MANUAL_CONDITION WHERE MAN_ID = ? AND REF_FILE_ID = ? ORDER BY COMP_ID";
	
	//SETTLEMENT QUERIES
	public static final String GET_SETTLEMENT_PARAM = "SELECT TABLE_HEADER, CONDITION, PATTERN, PADDING, START_CHARPOS, CHARSIZE, DATATYPE FROM MAIN_SETTLEMENT_CONDITION WHERE REF_FILE_ID = ? AND ID = ? ORDER BY TABLE_HEADER";
	public static final String GET_SETTLEMENT_ID = "SELECT DISTINCT ID FROM MAIN_SETTLEMENT_CONDITION WHERE REF_FILE_ID = ?";
	public static final String GET_KNOCKOFF_PARAMS = "SELECT HEADER,PADDING, START_CHARPOSITION, CHAR_SIZE, HEADER_VALUE, CONDITION FROM MAIN_KNOCKOFF_CRITERIA WHERE REVERSAL_ID = ? AND FILE_ID = ? AND HEADER_VALUE IS NULL ORDER BY HEADER";
	
	public static final String GET_TTUM_HEADERS = "SELECT FILE_HEADER FROM MAIN_TTUM_HEADERS WHERE FILE_ID = ? AND CATEGORY = ? ORDER BY SEQ";
	public static final String GET_TTUM_COLUMNS = "SELECT COLUMN_NAME, FILE_HEADER, PADDING,START_CHARPOS, CHAR_SIZE, HEADER_VALUE, REMOVE_CHAR FROM MAIN_TTUM_COLUMNS WHERE FILE_ID = ? AND ACTION = ? ORDER BY TTUM_ID";
	
	public static final String GET_UPDATE_COLS = "SELECT * FROM MAIN_TTUM_COLUMNS WHERE PADDING = 'N' AND ACTION = 'D'";
	
	public static final String EXIST_CBS_TTUM = "select count(*) from SETTLEMENT_RUPAY_CBS WHERE DCRS_REMARKS like '%RUPAY_DOM-UNRECON-GENERATE-TTUM-1%' AND FILEDATE = ? "
								+ "AND remarks = ? AND substr(ref_no,2,6) = substr(?,2,6) and TO_CHAR(to_date(tran_date ,'dd-mm-yyyy'),'dd-mm-yyyy') =  TO_CHAR(to_date(?,'mm/dd/yyyy'),'dd-mm-yyyy') "
								+ "AND trunc(TO_NUMBER(REPLACE(AMOUNT,',',''))) = trunc(TO_NUMBER(REPLACE(?,',','')))";
	
	public static final String EXIST_SWITCH_TTUM = "select count(*) from SETTLEMENT_RUPAY_SWITCH WHERE DCRS_REMARKS like '%RUPAY_DOM-UNRECON-GENERATE-TTUM-2%' AND FILEDATE = ? "
			+ "AND pan = ? AND trace = ? and local_date  = ? "
			+ "AND trunc(TO_NUMBER(REPLACE(amount_equiv,',',''))) = trunc(TO_NUMBER(REPLACE(?,',','')))";
	
	
	//METHOD FOR GETTING CONDITIONS IN QUERY
	public static String getKnockOffCondition(List<KnockOffBean> knockoff_Criteria)
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
						condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
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
						condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
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
						update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") = ?";
						select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header();
					}
					else
					{
						condition = condition +"NVL(OS1."+ knockoff_Criteria.get(i).getStReversal_header()+",'!NULL!') "+knockoff_Criteria.get(i).getStReversal_condition() 
								+" NVL(OS2."+knockoff_Criteria.get(i).getStReversal_header()
								+",'!NULL!')";
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
						condition = condition + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()+")"
								+" "+knockoff_Criteria.get(i).getStReversal_condition() + " "+knockoff_Criteria.get(i).getStReversal_value()+" AND ";
						/*update_condition = update_condition +" SUBSTR("+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
								+","+knockoff_Criteria.get(i).getStReversal_charsize()
								+") = ?"+" AND ";*/
						select_parameters = select_parameters + " SUBSTR( OS1."+knockoff_Criteria.get(i).getStReversal_header()+","+knockoff_Criteria.get(i).getStReversal_charpos()
						+","+knockoff_Criteria.get(i).getStReversal_charsize()+") AS "+knockoff_Criteria.get(i).getStReversal_header()+" , ";
					}
					else
					{
						condition = condition +"OS1."+ knockoff_Criteria.get(i).getStReversal_header()+" "+knockoff_Criteria.get(i).getStReversal_condition()
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
	
	public static void insertBatch(final String QUERY, final ResultSet rset,final String[] columns,Connection conn){
		PreparedStatement ps;
		Connection con;
		//System.out.println("query is 123 "+QUERY);
		//System.out.println("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = conn;
			
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
						ps.setString((i), rset.getString(columns[i-1].trim()));
					
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
			// TODO Auto-generated catch block
			System.out.println("INSERT BATCH EXCEPTION "+e);
		}
	}

	public static void deleteBatch(final String QUERY, final ResultSet rset,final String[] columns,Connection conn){
		PreparedStatement ps;
		Connection con;
		//System.out.println("query is 123 "+QUERY);
		//System.out.println("coulmn size is "+columns.length);
//		trns_srl = Integer.parseInt(new SimpleDateFormat("dd").format(cur_dt)) * Integer.parseInt(new SimpleDateFormat("MM").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("yyyy").format(cur_dt)) + Integer.parseInt(new SimpleDateFormat("ss").format(cur_dt));
		
		//for(int i=0; i<loadBeanList.size(); i += batchSize)
		int flag = 1;
		int batch = 1;
		
		try {
			
			con = conn;
			
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
			System.out.println("completed deletion");
			
			
		} catch (DataAccessException | SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("INSERT BATCH EXCEPTION "+e);
		}
	}
	public boolean compareDate(String datePrev, String dateNext){
		try{
			
			if(checkValidShortDate(datePrev) != true){
				throw new Exception("Invalid Previous Date");
			}
			if(checkValidShortDate(dateNext) != true){
				throw new Exception("Invalid Next Date");
			}
			
			shortFormat.setLenient(false);
			Date dateP = shortFormat.parse(datePrev);
			Date dateN = shortFormat.parse(dateNext);
			
			if(dateP.after(dateN) || dateP.equals(dateN)){
				throw new Exception("Future or Same Date");
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}	
	public boolean checkValidShortDate(String field){
		try{
			field = field.trim();
			if(field.equals("")){
				throw new Exception("Blank Field.");
			}
			shortFormat.setLenient(false);
			date = (Date) shortFormat.parse(field);
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
