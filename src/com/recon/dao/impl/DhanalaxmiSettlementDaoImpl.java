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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.DhanalaxmiDao;
import com.recon.dao.SettlementDao;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.SessionModel;
import com.recon.model.SettlementBean;


@Component
public class DhanalaxmiSettlementDaoImpl extends JdbcDaoSupport implements DhanalaxmiDao {

	@Override
	public HashMap<String, Object> checkFileProcess(NFSSettlementBean beanObj) {
		HashMap<String,Object> validate = new HashMap<String, Object>();
//		String checkData = "SELECT COUNT(*) FROM NFS_SETTLEMENT_VOUCHER WHERE FILEDATE = TO_CHAR(TO_DATE(?,'YYYY/MM/DD'),'MONRRRR')"; 
//		String checkData = "SELECT COUNT(*) FROM NFS_SETTLEMENT_VOUCHER WHERE FILEDATE = TO_DATE(?,'YYYY/MM/DD')";
		String checkNSVData = "select count(1) from nfs_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
		int dataCount = getJdbcTemplate().queryForObject(checkNSVData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		String checkVSTData = "select count(1) from visa_settlement_report where filedate = str_to_date(?,'%Y/%m/%d')";
		int VSTDataCount = getJdbcTemplate().queryForObject(checkVSTData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		String CSTData = "select count(1) from cashnet_settlement_ttum where filedate = str_to_date(?,'%Y/%m/%d')";
		int CSTDataCount = getJdbcTemplate().queryForObject(CSTData, new Object[] {beanObj.getDatepicker()},Integer.class);
		
		System.out.println(dataCount);
		if(dataCount > 0 && VSTDataCount >0 && CSTDataCount >0)
		{
			validate.put("result", true);
			//validate.put("msg", "Settlement validtion success !");
			
		}
		else 
		{
			if(dataCount == 0)
			{
				validate.put("result", false);
				validate.put("msg", "NFS Settlement is not processed for selected date !");
			}
			else if(VSTDataCount==0)
			{
				validate.put("result", false);
				validate.put("msg", "Visa Settlement is not processed for selected date !");
			}
			else if(CSTDataCount==0)
			{
				validate.put("result", false);
				validate.put("msg", "Cashnet Settlement is not processed for selected date !");
			}
		}
		
		
		return validate;
	}

	@Override
	public List<String> getSettlementVoucher(NFSSettlementBean beanObj) {
		List<String> output = new ArrayList<>();
		
		try
		{
			String credit_count = " select sum(action_count) from ( "
					+" select COUNT(1) as action_count from cashnet_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"'"
					+" and amount>0 and description not in('DIFFERENCE') and action = 'C' "
					+" group by action "
					+" union all \n"
					+" select COUNT(1) as action_count from visa_settlement_report where filedate = '"+beanObj.getDatepicker()+"' "
					+" and amount>0 and description not in('DIFFERENCE') and action = 'C' "
					+" group by action "
					+" union all \n"
					+" select count(1) as action_count from nfs_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"' "
					+" and amount>0 and description not in('DIFFERENCE') and action = 'C' "
					+" group by action) as a ";
			
			int creditCnt = getJdbcTemplate().queryForObject(credit_count, new Object[]{}, Integer.class);
			
			logger.info("Credit count is "+creditCnt);
			
			String query = "select concat('1',date_format(sysdate(),'%Y%m%d')) as a from dual \n " 
					+" union all \n"
					+" select concat(case when length(ifnull(acccount_number,'0')) > '10'\n"
					+" then "
					+"	'201' "
					   +" else "
						+"	'203' end "
					+" ,rpad(ifnull(acccount_number,'0'),'16',' '),'00', sol,'0', " 
					+" case when length(ifnull(acccount_number,'0') < 10) "
					+" then " 
					+"	'1005' "
					+" else "
					+"	case when action = 'C' "
					  +"  then '1408' "
					   +" else '1008' "
					 +"   end end "
					+"	,date_format(sysdate(),'%Y%m%d'), "
					+" action,date_format(sysdate(),'%Y%m%d'),'00101', "
					+" case when amount not like '%.%' then " 
					+"	lpad(concat(amount,'00'),'14','0') " 
					 +" else "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0') " 
					 +" end, "
					+" case when amount not like '%.%' then "
					+"	lpad(concat(amount,'00'),'14','0') " 
					+" else "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0') " 
					+" end, '000000010000000000000', "
					+" '           ', rpad(narration,120,' '),'1',lpad('',380,' '),'300000000000N', "
					+" lpad(acccount_number,16,'0') "
					+") as data from cashnet_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"' " 
					+" and amount >0 and description not in ('DIFFERENCE') \n\r"
					+" union ALL \n"
					+" select concat(case when length(ifnull(acccount_number,'0')) > '10' "
					+" then "
					+"	'201' "
					+"    else "
					+"		'203' end "
					+",rpad(ifnull(acccount_number,'0'),'16',' '),'00', sol,'0', "
					+" case when length(ifnull(acccount_number,'0') < 10) "
					+" then "
					+"	'1005' "
					+" else "
					+"	case when action = 'C' "
					+"    then '1408' "
					+"    else '1008' "
					+"    end end  "
					+"	,date_format(sysdate(),'%Y%m%d'),  "
					+" action,date_format(sysdate(),'%Y%m%d'),'00101',  "
					+" case when amount not like '%.%' then  "
					+"	lpad(concat(amount,'00'),'14','0')  "
					+" else  "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0')  "
					+" end, "
					+" case when amount not like '%.%' then  "
					+"	lpad(concat(amount,'00'),'14','0')  "
					+" else  "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0')  "
					+" end, '000000010000000000000',  "
					+" '           ', rpad(narration,120,' '),'1',lpad('',380,' '),'300000000000N',  "
					+" lpad(ifnull(acccount_number,'0'),16,'0')  "
					+" ) as data  "
					+" from visa_settlement_report where filedate = '"+beanObj.getDatepicker()+"'  "
					+" and amount > 0 and description not in ('DIFFERENCE')  "
					+" union ALL  \n"
					+" select concat( case when length(ifnull(acccount_number,'0')) >10  "
					+" then '201' else '203' end, rpad(ifnull(acccount_number,'0'),'16',' '),'00', lpad(ifnull(sol,0),3,'0'),'0',  "
					+" case when length(ifnull(acccount_number,'0') < 10)  "
					+" then  "
					+"	'1005'  "
					+"  else  "
					+"	case when action = 'C'  "
					+"    then '1408'  "
					+"    else '1008'  "
					+"    end end  "
					+"	,date_format(sysdate(),'%Y%m%d'),  "
					+" action,date_format(sysdate(),'%Y%m%d'),'00101',  "
					+" case when amount not like '%.%' then  "
					+"	lpad(concat(amount,'00'),'14','0')  "
					+"  else  "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0')  "
					+"  end,  "
					+"  case when amount not like '%.%' then  "
					+"	lpad(concat(amount,'00'),'14','0')  "
					+"  else  "
					+"	 lpad(replace(cast(amount as decimal(20,2)),'.',''),14,'0')  "
					+"  end, '000000010000000000000',  "
					+" '           ', rpad(narration,120,' '),'1',lpad('',380,' '),'300000000000N',  "
					+"lpad(ifnull(acccount_number,'0'),16,'0')  "
					+" ) as data  "
					+" from nfs_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"'  "
					+" and amount >0 and description not in ('DIFFERENCE')  "
					+"\n union all \n"
					+" select concat('3', lpad(sum(action_count),9,'0') ,  "
					+" case when sum(amt) not like '%.%' then "
					+" 	lpad(concat(sum(amt),'00'),'15','0') "
					+"  else "
					+" 	 lpad(replace(cast(sum(amt) as decimal(20,2)),'.',''),15,'0')  "
					+"  end, "
					+" lpad("+creditCnt+" , 9,'0'),"
					+"  case when sum(amt) not like '%.%' then  "
					+" 	lpad(concat(sum(amt),'00'),'15','0') "
					+"  else "
					+" 	 lpad(replace(cast(sum(amt) as decimal(20,2)),'.',''),15,'0') "
					+"  end "
					+" ) from ( "
					+" select COUNT(1) as action_count,round(sum(amount),2)as amt, action from cashnet_settlement_ttum "
					+ "where filedate = '"+beanObj.getDatepicker()+"' "
					+" and amount>0 and description not in('DIFFERENCE') and action = 'D' "
					+" group by action "
					+"\n union all \n"
					+" select COUNT(1)  as action_count,round(sum(amount),2)as amt, action from visa_settlement_report where filedate = '"+beanObj.getDatepicker()+"' "
					+" and amount>0 and description not in('DIFFERENCE') and action = 'D' "
					+" group by action "
					+" union all \n"
					+" select count(1) as action_count,round(sum(amount),2)as amt, action from nfs_settlement_ttum where filedate = '"+beanObj.getDatepicker()+"' "
					+" and amount>0 and description not in('DIFFERENCE') and action = 'D' "
					+" group by action) as a "
					+" group by action ";
					
			
			
			logger.info("getData is "+query);
			

			output= getJdbcTemplate().query(query, new Object[] {}, new ResultSetExtractor<List<String>>(){
				public List<String> extractData(ResultSet rs)throws SQLException {
					List<String> beanList = new ArrayList<String>();
					
					while (rs.next()) {
						
						beanList.add(rs.getString(1));
					}
					return beanList;
				}
			});
		}
		catch(Exception e)
		{
			logger.info("Exception in getting settlement Voucher "+e);
			return null;
		}
		return output;
		
	}
	

	public HashMap<String, Object> checkCbsGlUpload(NFSSettlementBean beanObj)
	{
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		try
		{
			String checkCbsUpload = "select count(1) from cbs_rupay_rawdata where filedate = str_to_date("
					+ "?,'%Y/%m/%d')";
			String checkGLUpload = "select count(1) from cbs_dhana_recon_gl_rawdata where filedate = str_to_date("
					+ "?,'%Y/%m/%d')";
			
			int cbsCount = getJdbcTemplate().queryForObject(checkCbsUpload, new Object[]{beanObj.getDatepicker()},Integer.class);
			
			int glCount = getJdbcTemplate().queryForObject(checkGLUpload, new Object[]{beanObj.getDatepicker()},Integer.class);
			
			
			if(cbsCount == 0)
			{
				output.put("result", false);
				output.put("msg", "Cbs File is not uploaded for selected date");
			}
			else
			{
				if(glCount == 0)
				{
					output.put("result", false);
					output.put("msg", "Gl Recon File is not uploaded for selected date");
				}
				else
				{
					output.put("result", true);
				}
			}
			
		}
		catch(Exception e)
		{
			output.put("result", false);
			output.put("msg", "Exception while checking cbs and gl uploaded files");
			logger.info("Exception while checking cbs and gl uploaded files "+e);
		}
		return output;
	}
	
	@Override
	public List<Object> getCbsGlExceptionReport(NFSSettlementBean beanObj)
	{
		List<Object> data = new ArrayList<Object>();
		try
		{
			String getData = "";
			List<String> Column_list  = new ArrayList<String>();
			Column_list = getColumnList("cbs_dhana_recon_gl_rawdata");

			getData = "select a1.* from cbs_dhana_recon_gl_rawdata a1  where cod_user_id = 'SYSTEM01' and "
					+ " not exists ( select * from cbs_rupay_rawdata a2 "
					+" where ( substring(a1.txn_ref_no,-6) = a2.trace or " 
					+" substring(a1.txn_ref_no,-6) = substring(a2.ref_no,-6)) and " 
					+" (substring(replace(a1.txn_desc,':',''),1,16) = cast(aes_decrypt(a2.fpan,'key_dbank')as char) "
					+" or substr(substring(txn_desc,-21),1,16) = cast(aes_decrypt(a2.fpan,'key_dbank')as char)) "
					+" and replace(a1.lcy_amount,'-','') = a2.amount "         
					+" and a1.filedate = a2.filedate "
					+" ) and filedate = str_to_date(?,'%Y/%m/%d')";
			
			data.add(Column_list);
		final List<String> columns  = Column_list;
		System.out.println("column value is "+columns.get(1));

			List<Object> DailyData= getJdbcTemplate().query(getData, new Object[] {beanObj.getDatepicker()}, new ResultSetExtractor<List<Object>>(){
				public List<Object> extractData(ResultSet rs)throws SQLException {
					List<Object> beanList = new ArrayList<Object>();
					
					while (rs.next()) {
						Map<String, String> data = new HashMap<String, String>();
							logger.info("Column is "+columns.get(1));
							for(String column : columns)
							{
								data.put(column, rs.getString(column));
							}
							beanList.add(data);
					}
					return beanList;
				}
			});
			data.add(DailyData);

			return data;

		}
		catch(Exception e)
		{
			System.out.println("Exception in getInterchangeData "+e);
			return null;

		}

	}
	
	public ArrayList<String> getColumnList(String tableName) {

		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
		
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = Database() AND TABLE_NAME = '"+tableName.toLowerCase()+"' ";
				//+"and column_name not in('filedate','settlement_date','createddate','createdby','file_type')";
		
		System.out.println(query);


		ArrayList<String> typeList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				return rs.getString(1);
			}
		});

		System.out.println(typeList);
		return typeList;

	}
}
