package com.recon.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.SystemOutLogger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;
import com.recon.service.RupayAdjustntFileUpService;


public class RupayAdjustntFileUpServiceImpl extends JdbcDaoSupport implements RupayAdjustntFileUpService{

	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	
	public HashMap<String, Object> validateAdjustmentUpload(String fileDate, String cycle, String network, String subcategory, boolean presentmentFile)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		try
		{
			String tableName = "";
			if(network.equalsIgnoreCase("RUPAY"))
			{
				if(subcategory.equalsIgnoreCase("DOMESTIC"))
				{
					tableName = "rupay_network_adjustment";
				}
				else
				{
					if(!presentmentFile)
						tableName = "RUPAY_INTERNATIONAL_ADJUSTMENT";
					else
						tableName = "RUPAY_INTERNATIONAL_PRESENTMENT";
				}
				
			}
			else
			{
				tableName = "RUPAY_NCMC_NETWORK_ADJUSTMENT";
			}
			
			String checkUpload = "select count(*) from "+tableName.toLowerCase()+" where filedate = str_to_date(?,'%Y/%m/%d') and cycle = ?";
			int uploadCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {fileDate, cycle}, Integer.class);
			
			if(uploadCount == 0)
			{
				output.put("result", true);
			}
			else
			{
				output.put("result", false);
				output.put("msg", "File is already uploaded");
			}
			
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception Occurred While checking");
			System.out.println("Exception is "+e);
		}
		return output;
	}
	
	@Override
	public HashMap<String, Object> rupayAdjustmentFileUpload(String fileDate,String createdBy,String cycle, String network ,MultipartFile file, String subcategory) {

		HashMap<String, Object> output = new HashMap<String, Object>();
		int totalCount = 0;
		String res="";
		String row="";
		String line = "";
		try {
		BufferedReader csvReader1 = new BufferedReader(new InputStreamReader(file.getInputStream()));
		Connection con=getConnection();
		String tableName = "";
		
		if(network.equalsIgnoreCase("RUPAY"))
		{
			if(subcategory.equalsIgnoreCase("DOMESTIC"))
				tableName = "rupay_network_adjustment";
			else
				tableName = "RUPAY_INTERNATIONAL_ADJUSTMENT";
		}
		else
		{
			tableName = "RUPAY_NCMC_NETWORK_ADJUSTMENT";
		}
		
		String sql="INSERT INTO "+tableName.toLowerCase()+" (report_date,dispute_raise_date,dispute_raised_settl_date,case_number,function_code,function_code_description,primary_account_number,processing_code,"
				+ "transaction_date,transaction_amount,txn_currency_code,settlement_amount,settlement_ccy_code,txn_settlement_date,amounts_additional,control_number,dispute_originator_pid,"
				+ "dispute_destination_pid,acquire_ref_data,approval_code,originator_point,pos_entry_mode,pos_condition_code,acquirer_instituteid_code,acquirer_name_country,issuer_insti_id_code,"
				+ "issuer_name_country,card_type,card_brand,card_acceptor_terminalid,card_acceptor_name,card_accept_location_add,card_accept_country_code,card_accept_buss_code,"
				+ "dispute_reason_code,dispute_reason_cd_desc,dispute_amt,full_partial_indicator,dispute_member_msg_text,dispute_document_indicator,document_attached_date,mti,"
				+ "incentive_amount,tier_cd_nonfullfill,tier_cd_fulfill,deadline_date,days_to_act,direction_iw_ow,last_adj_stage, last_adj_date, filedate, createdby, cycle) "
				+ "VALUES(?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, str_TO_DATE(?,'%Y/%m/%d') , ?, ?)";
		
		if(subcategory.equalsIgnoreCase("INTERNATIONAL"))
		{
			sql="INSERT INTO "+tableName+" (REPORT_DATE,DISPUTE_RAISE_DATE,DISPUTE_RAISED_SETTL_DATE,CASE_NUMBER,Scheme_Name,Transaction_Flag,"
					+ "FUNCTION_CODE,FUNCTION_CODE_DESCRIPTION,PRIMARY_ACCOUNT_NUMBER,PROCESSING_CODE,"
					+ "TRANSACTION_DATE,TRANSACTION_AMOUNT,TXN_CURRENCY_CODE,SETTLEMENT_AMOUNT,SETTLEMENT_CCY_CODE,Conversion_Rate,TXN_SETTLEMENT_DATE,AMOUNTS_ADDITIONAL,CONTROL_NUMBER,DISPUTE_ORIGINATOR_PID,"
					+ "DISPUTE_DESTINATION_PID,ACQUIRE_REF_DATA,APPROVAL_CODE,ORIGINATOR_POINT,POS_ENTRY_MODE,POS_CONDITION_CODE,ACQUIRER_INSTITUTEID_CODE,ACQUIRER_NAME_COUNTRY,ISSUER_INSTI_ID_CODE,"
					+ "ISSUER_NAME_COUNTRY,CARD_TYPE,CARD_BRAND,CARD_ACCEPTOR_TERMINALID,CARD_ACCEPTOR_NAME,CARD_ACCEPT_LOCATION_ADD,CARD_ACCEPT_COUNTRY_CODE,CARD_ACCEPT_BUSS_CODE,"
					+ "DISPUTE_REASON_CODE,DISPUTE_REASON_CD_DESC,DISPUTE_AMT,FULL_PARTIAL_INDICATOR,DISPUTE_MEMBER_MSG_TEXT,DISPUTE_DOCUMENT_INDICATOR,DOCUMENT_ATTACHED_DATE,MTI,"
					+ "INCENTIVE_AMOUNT,TIER_CD_NONFULLFILL,TIER_CD_FULFILL,DEADLINE_DATE,DAYS_TO_ACT,DIRECTION_IW_OW, FILEDATE, CREATEDBY, CYCLE) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,"
					+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, str_TO_DATE(?,'%Y/%m/%d') , ?, ?)";
		}
		
		//,DISPUTE_RAISE_DATE,DISPUTE_RAISED_SETTL_DATE,CASE_NUMBER,FUNCTION_CODE,FUNCTION_CODE_DESCRIPTION,PRIMARY_ACCOUNT_NUMBER,PROCESSING_CODE
		//,TO_DATE(?,'DD-MMM-YYYY'),TO_DATE(?,'DD-MMM-YYYY'),?,?,?,?,?
		PreparedStatement ps = con.prepareStatement(sql);
		int count=1;
		int Number = 0;
		con.setAutoCommit(false);
		while ((row = csvReader1.readLine()) != null) {
			int sr_no = 1;
			Number++;
			totalCount++;
			if(row.contains("---END OF REPORT---") || row.contains("---End of Report---")) {
				break;
			}
			if(count==1) {
				 count++;
				continue;
			}
			
			if(subcategory.equalsIgnoreCase("INTERNATIONAL"))
			{
				line = row.replaceAll(",", "|");
			}
			else
			{
				line = row.replaceAll("\",\"", "|");
				line = line.replace("\",", "|");
				line = line.replace(",\"", "|");
				line = line.replace("\"","");
				//System.out.println(line);
			}
		    //String[] data = row.split(",");
			String[] data = line.split("\\|");
		    
		    
		    for(int i = 0 ; i < data.length; i++)
		    {
		    	ps.setString(sr_no, data[i].replaceAll("^\"|\"$", "").replaceAll("-", "").trim());
		    	sr_no++;
		    }
		    		ps.setString(sr_no++, fileDate);
		    		ps.setString(sr_no++, createdBy);
		    		ps.setString(sr_no++, cycle);
	  
					ps.addBatch();
					
					if(Number == 1000)
					{
						System.out.print("Executed batch");
							ps.executeBatch();
					}
        	  
		}
		ps.executeBatch();
		
		con.commit();
   	 	res="success";
		csvReader1.close();
		
		output.put("result", true);
		output.put("count", totalCount);
		
		}catch (Exception e) {
			res="fail";
			output.put("result", false);
			output.put("count", totalCount);
			System.out.println("issue at Line number "+totalCount);
			System.out.println("Line issue "+line);
			System.out.println("Exception in reading adjustment is "+e);
			e.printStackTrace();
		}
		return output;
	}
	
	@Override
	public HashMap<String, Object> rupayIntPresentFileUpload(String fileDate,String createdBy,String cycle, String network ,MultipartFile file, String subcategory) {

		HashMap<String, Object> output = new HashMap<String, Object>();
		int totalCount = 0;
		String res="";
		String row="";
		String line = "";
		try {
		BufferedReader csvReader1 = new BufferedReader(new InputStreamReader(file.getInputStream()));
		Connection con=getConnection();
		String tableName = "";
		
		if(network.equalsIgnoreCase("RUPAY"))
		{
			if(subcategory.equalsIgnoreCase("INTERNATIONAL"))				
				tableName = "RUPAY_INTERNATIONAL_PRESENTMENT";
		}
		
			String sql="insert into "+tableName+
					"(Report_Date, Presentment_Raise_Date, Presentment_Settlement_Date, Case_Number, Function_Code, Scheme_Name, Transaction_Flag, Primary_Account_Number, Date_Local_Transaction, Transaction_Settlement_Date, Acquirer_Reference_Data, Processing_Code, Currency_Code_txn, ECommerce_Indicator, Amount_Transaction, Amount_Additional, Currency_Code, Settlement_Amount, Settlement_Amount_Additional, Settlement_Amount_Presentment, Approval_Code, Originator_Point, POS_Entry_Mode, POS_Condition_Code, Acquirer_Institution_ID_code, Transaction_Originator_Institution_ID_code, Acquirer_Name_Country, Issuer_Institution_ID_code, Transaction_Destination_code, Issuer_Name_Country, Card_Acceptor_Terminal_ID, Card_Acceptor_Name, Card_Acceptor_Location, Card_Acceptor_Country_Code, Card_Acceptor_Business_Code, Card_Acceptor_ID_Code, Card_Acceptor_State_Name, Card_Acceptor_City, Days_Aged, MTI, filedate, createdby, cycle) "
					+"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_dATE(?,'DD/MM/YYYY'), ?, ?)";
		
		
		//,DISPUTE_RAISE_DATE,DISPUTE_RAISED_SETTL_DATE,CASE_NUMBER,FUNCTION_CODE,FUNCTION_CODE_DESCRIPTION,PRIMARY_ACCOUNT_NUMBER,PROCESSING_CODE
		//,TO_DATE(?,'DD-MMM-YYYY'),TO_DATE(?,'DD-MMM-YYYY'),?,?,?,?,?
		PreparedStatement ps = con.prepareStatement(sql);
		int count=1;
		int Number = 0;
		con.setAutoCommit(false);
		while ((row = csvReader1.readLine()) != null) {
			int sr_no = 1;
			Number++;
			totalCount++;
			if(row.contains("---END OF REPORT---") || row.contains("---End of Report---")) {
				break;
			}
			if(count==1) {
				 count++;
				continue;
			}
			
			if(subcategory.equalsIgnoreCase("INTERNATIONAL"))
			{
				line = row.replaceAll(",", "|");
			}
			else
			{
				line = row.replaceAll("\",\"", "|");
				line = line.replace("\",", "|");
				line = line.replace("\"","");
				//System.out.println(line);
			}
		    //String[] data = row.split(",");
			String[] data = line.split("\\|");
		    
		    
		    for(int i = 0 ; i < data.length; i++)
		    {
		    	ps.setString(sr_no, data[i].replaceAll("^\"|\"$", "").replaceAll("-", ""));
		    	sr_no++;
		    }
		    
		    		ps.setString(sr_no++, fileDate);
		    		ps.setString(sr_no++, createdBy);
		    		ps.setString(sr_no++, cycle);
	  
					ps.addBatch();
					
					if(Number == 1000)
					{
						System.out.print("Executed batch");
							ps.executeBatch();
					}
        	  
		}
		ps.executeBatch();
		
		con.commit();
   	 	res="success";
		csvReader1.close();
		
		output.put("result", true);
		output.put("count", totalCount);
		
		}catch (Exception e) {
			res="fail";
			output.put("result", false);
			output.put("count", totalCount);
			System.out.println("issue at Line number "+totalCount);
			System.out.println("Line issue "+line);
			System.out.println("Exception in reading adjustment is "+e);
			e.printStackTrace();
		}
		return output;
	}
		

/******************* ADJUSTMENT TTUM **************************/
	public HashMap<String, Object> validateAdjustmentTTUM(String fileDate, String adjType)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		int adjTTUMCount = 0;
		try
		{
			//1. Dispute file is uploaded or not
			String checkUpload = "select count(cycle) from(select distinct cycle from rupay_network_adjustment where filedate = str_to_Date(?, '%Y/%m/%d'))";
			int uploadedCount = getJdbcTemplate().queryForObject(checkUpload, new Object[] {fileDate}, Integer.class);
			
			String rawUpload = "SELECT COUNT(*) FROM RUPAY_RUPAY_RAWDATA WHERE FILEDATE = str_to_Date(?,'%Y/%m/%d')";
			int rawCount = getJdbcTemplate().queryForObject(rawUpload, new Object[] {fileDate}, Integer.class);
			
			if(uploadedCount > 0 && rawCount > 0)
			{
				//2. Adjustment TTUM is already processed
				if(!adjType.equalsIgnoreCase("FEE"))
				{
					String checKAdjTTUM = "select count(*) from rupay_adjustment_Ttum where filedate = str_to_Date(?,'%Y/%m/%d') and adjtype != 'FEE'";
					adjTTUMCount = getJdbcTemplate().queryForObject(checKAdjTTUM, new Object[] {fileDate}, Integer.class);
				}
				else
				{
					String checKAdjTTUM = "select count(*) from rupay_adjustment_Ttum where filedate = str_to_Date(?,'%Y/%m/%d') and adjtype = ?";
					adjTTUMCount = getJdbcTemplate().queryForObject(checKAdjTTUM, new Object[] {fileDate, adjType}, Integer.class);
				}
				
				if(adjTTUMCount > 0)
				{
					output.put("result", false);
					output.put("msg", "TTUM is already processed");
				}
				else
				{
					//check whether data is present for ttum generation
					String checkData = "select count(*) from rupay_network_adjustment where filedate = str_to_Date(?,'%Y/%m/%d') "
							+ " and function_code_description not like '%262-Refund%'"
							+ "    and DIRECTION_IW_OW is not null and upper(direction_iw_ow) = 'INWARD'"
							+ "    and function_code_description like '%Acceptance%'";
					
					int getData = getJdbcTemplate().queryForObject(checkData, new Object[] {fileDate}, Integer.class);
					
					if(getData > 0)
						output.put("result", true);
					else
					{
						output.put("result", false);
						output.put("msg", "Data is not present for TTUM generation");
					}
					
					
				}
				
			}
			else
			{
				if(rawCount == 0)
				{
					output.put("result", false);
					output.put("msg", "Raw files are not uploaded");
				}
				else
				{
					output.put("result", false);
					output.put("msg", "All disputes files are not uploaded");
				}
			}
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception Occurred While checking");
			System.out.println("Exception is "+e);
		}
		return output;
	}
	
	public HashMap<String, Object> validateAdjustmentTTUMProcess(String fileDate, String adjType)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		int adjTTUMCount = 0;
		try
		{
				//2. Adjustment TTUM is already processed
				if(!adjType.equalsIgnoreCase("FEE"))
				{
					String checKAdjTTUM = "select count(*) from rupay_adjustment_Ttum where filedate = str_to_Date(?,'%Y/%m/%d') and adjtype != 'FEE'";
					adjTTUMCount = getJdbcTemplate().queryForObject(checKAdjTTUM, new Object[] {fileDate}, Integer.class);
				}
				else
				{
					String checKAdjTTUM = "select count(*) from rupay_adjustment_Ttum where filedate = str_to_Date(?,'%Y/%m/%d') and adjtype = ?";
					adjTTUMCount = getJdbcTemplate().queryForObject(checKAdjTTUM, new Object[] {fileDate, adjType}, Integer.class);
				}
				
				if(adjTTUMCount > 0)
				{
					output.put("result", true);
					
				}
				else
				{
					output.put("result", false);
					output.put("msg", "TTUM is not processed");
					
				}
				
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception Occurred While checking");
			System.out.println("Exception is "+e);
		}
		return output;
	}
	
	@Override
	public boolean runAdjTTUM(String fileDate, String adjType, String createdBy)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams2 = new HashMap<String, Object>();
		try {
			
			//run adj ttum
			AdjTTUMProc exe = new AdjTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", fileDate);
			inParams.put("USER_ID", createdBy); 
			inParams.put("ADJTYPE", adjType); 
			inParams.put("SUBCATE", "DOMESTIC");
			outParams2 = exe.execute(inParams);
			if(outParams2 !=null && outParams2.get("msg") != null)
			{
				logger.info("OUT PARAM IS "+outParams2.get("msg"));
				return false;
			}
			else
			{
				return true;
			}

		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return false;
		}
	}
	
	// ADJ ttum detailed
	private class AdjTTUMProc extends StoredProcedure{
		private static final String insert_proc = "RUPAY_ADJ_TTUM";
		public AdjTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			//declareParameter(new SqlParameter("ENTERED_CYCLE",Types.INTEGER));
			declareParameter(new SqlParameter("ADJTYPE",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCATE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	@Override
	public List<Object> getAdjTTUM(String fileDate, String adjType)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getData1 = null;//,getData2 = null;
			List<Object> DailyData = new ArrayList<Object>();
			
				if(adjType.equals("FEE"))
				{
					getData1 = "SELECT RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
							+"LPAD(TRANSACTION_AMOUNT,17,' ') as TRANSACTION_AMOUNT,"
							+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,"
							+ "LPAD(NVL(REFERENCE_NUMBER,' '),16,' ') AS REMARKS"
							+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
							+ " FROM RUPAY_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = 'DOMESTIC' "
							+ "AND upper(adjtype) like '%"+adjType+"%'";
					
					DailyData= getJdbcTemplate().query(getData1, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
						public List<Object> extractData(ResultSet rs)throws SQLException {
							List<Object> beanList = new ArrayList<Object>();
							
							while (rs.next()) {
								logger.info("Inside rset");
								
								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
								table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
								table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
								table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

								beanList.add(table_Data);
							}
							return beanList;
						}
					});
				}
				else
				{
					getData1 = "SELECT ACCOUNT_NUMBER AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
							+"TRANSACTION_AMOUNT as TRANSACTION_AMOUNT,"
							+"TRANSACTION_PARTICULAR as TRANSACTION_PARTICULAR,REFERENCE_NUMBER AS REMARKS"
							+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE,ADJTYPE"
							+ " FROM RUPAY_ADJUSTMENT_TTUM WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') and SUBCATEGORY = 'DOMESTIC' "
							//+ "AND (ADJTYPE) = ? "
							+ "AND adjtype not like '%Penalty%'";

					DailyData= getJdbcTemplate().query(getData1, new Object[] {fileDate}, new ResultSetExtractor<List<Object>>(){
						public List<Object> extractData(ResultSet rs)throws SQLException {
							List<Object> beanList = new ArrayList<Object>();

							while (rs.next()) {
								logger.info("Inside rset");

								Map<String, String> table_Data = new HashMap<String, String>();
								table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
								table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
								table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
								table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
								table_Data.put("REMARKS", rs.getString("REMARKS"));
								table_Data.put("FILEDATE", rs.getString("FILEDATE"));
								table_Data.put("ADJTYPE", rs.getString("ADJTYPE"));

								beanList.add(table_Data);
							}
							return beanList;
						}
					});
				}
			
			
			data.add(DailyData);
			
			//ADDING REPORT 2 DATA
			
			
			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getInterchangeData "+e);
			return null;

		}

	}
	
}
