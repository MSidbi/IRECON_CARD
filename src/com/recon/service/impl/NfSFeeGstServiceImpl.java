package com.recon.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.metadata.DerbyTableMetaDataProvider;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;


import com.recon.model.NFSFeeGstBean;
import com.recon.model.NFSSuspectTxnBean;
import com.recon.service.NfSFeeGstService;
import com.recon.util.demo;

public class NfSFeeGstServiceImpl extends JdbcDaoSupport implements NfSFeeGstService {

	@Override
	public String nfsFeeGstReportDownload(NFSFeeGstBean beanObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getnfsFeeGstData(NFSFeeGstBean beanObj, String reportType) {
		List<Object> data = new ArrayList<Object>();
		try {
			String getData1 = null;
			if(reportType.equalsIgnoreCase("REPORT1")) {
				getData1="select * from NFS_FEE_GST_REPORT1";
			}else {
				getData1="select * from NFS_FEE_GST_REPORT2";
			}
			
			List<Object> datalist=getJdbcTemplate().query(getData1,new ResultSetExtractor<List<Object>>(){

				@Override
				public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<Object> beanList = new ArrayList<Object>();
					while(rs.next()) {
						Map<String,String> tableData=new HashMap<String, String>();
						tableData.put("TXNS", rs.getString("TXNS"));
						tableData.put("CREDIT_AMT", rs.getString("CREDIT_AMT"));
						tableData.put("DEBIT_AMT", rs.getString("DEBIT_AMT"));
						tableData.put("DESCRIPTION", rs.getString("DESCRIPTION"));
						tableData.put("FILEDATE",rs.getString("FILEDATE"));
						tableData.put("DATE1", rs.getString("DATE1"));
						tableData.put("DATE2", rs.getString("DATE2"));
						
						beanList.add(tableData);
					}
					return beanList;
				}
				
			});	
			System.out.println("datalist   size  "+datalist.size());
			List<String> Column_list = new ArrayList<String>();
			Column_list.add("TXNS");
			Column_list.add("CREDIT_AMT");	
			Column_list.add("DEBIT_AMT");
			Column_list.add("DESCRIPTION");	
			Column_list.add("FILEDATE");	
			Column_list.add("DATE1");	
			Column_list.add("DATE2");	
			data.add(Column_list);
			data.add(datalist);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	
	public void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,HttpServletResponse response )
	{

		StringBuffer lineData;
		List<String> files = new ArrayList<>();
		FileInputStream fis;
		try
		{
			logger.info("Filename is "+FileName);
			List<Object> TTUMData = (List<Object>) ExcelData.get(1);
			List<String> Excel_Headers = (List<String>) ExcelData.get(0);
			
			List<Object> Data;
			/*File file = new File(stPath+File.separator+FileName);
			if(file.exists())
			{
				FileUtils.forceDelete(file);
			}
			file.createNewFile();*/

						
			OutputStream fileOut = new FileOutputStream(stPath+File.separator+FileName);   
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Report");   
			HSSFRow header = sheet.createRow(0);
			for(int i =0 ;i < Excel_Headers.size(); i++)
			{
				header.createCell(i).setCellValue(Excel_Headers.get(i));
			}
			
			for(int record_count = 0 ; record_count < TTUMData.size() ; record_count++)
			{
			
				HSSFRow rowEntry;

				
					rowEntry = sheet.createRow(record_count+1);
					Map<String, String> map_data =  (Map<String, String>) TTUMData.get(record_count);
					if(map_data.size()>0)
					{

						for(int m= 0 ;m < Excel_Headers.size() ; m++)
						{


							rowEntry.createCell(m).setCellValue(map_data.get(Excel_Headers.get(m)));
						}
					}

				

			}
			
			workbook.write(fileOut);
			fileOut.close();
			
	    	File file = new File(stPath);
	    	String[] filelist = file.list();
	    	
	    	for(String Names : filelist )
	    	{	
	    		logger.info("name is "+Names);
	    		files.add(stPath+File.separator+Names);
	    	}
	    	FileOutputStream fos = new FileOutputStream(stPath+File.separator+ "Fee_Gst_Report.zip");
			ZipOutputStream   zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			
			try {
				for (String filespath : files) {
					File input = new File(filespath);
					fis = new FileInputStream(input);
					ZipEntry ze = new ZipEntry(input.getName()); //
					System.out.println("Zipping the file: " + input.getName());
					if(!(input.getName().equalsIgnoreCase("Fee_Gst_Report"))) {
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					zipOut.flush();
					fis.close();
					}
				}
				zipOut.close(); //
				System.out.println("Done... Zipped the files...");
			} catch (Exception fe) {
				System.out.println("Exception in zipping is " + fe);
			}

		}
		catch(Exception e)
		{
			logger.info("Exception in generateTTUMFile "+e );

		}


	}

	@Override
	public String processData(NFSFeeGstBean beanObj) {
		try {
			Map<String, Object> inParams = new HashMap<String, Object>();
            inParams.put("DATE1", beanObj.getFromDate());
			inParams.put("DATE2",beanObj.getToDate());
			inParams.put("NETWORK", beanObj.getNetwork());
			
			FeeGstProcc feeGstProcc = new FeeGstProcc(getJdbcTemplate());
			Map<String, Object> outParams = feeGstProcc.execute(inParams);
			return "success";
           } catch (Exception e) {
        	e.printStackTrace();
        	return "fail";
		}
		
	}
	

	class FeeGstProcc extends StoredProcedure {
		
		private static final String procName = "NFS_FEE_GST_REPORT";

		FeeGstProcc(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("DATE1", Types.VARCHAR));
			declareParameter(new SqlParameter("DATE2",
					Types.VARCHAR));
			declareParameter(new SqlParameter("NETWORK",
					Types.VARCHAR));
			
			compile();
		}
	}


	@Override
	public String checkIfSuspectTxnProcess(String network, String date) {
		String query="select count(*) from  NFS_SUSPECTED_TRANSACTION where TO_CHAR(FILEDATE,'DD/MM/YYYY')='"+date+"'";
		try {
		int count=getJdbcTemplate().queryForObject(query, new Object[]{},Integer.class);
		if(count==0) {
			return "Suspected TRansaction Report is not Processed for selected date";
		}else {
			return "success";
		}
		}catch (Exception e) {
			e.printStackTrace();
			return "Exception";
		}
		
	}

	@Override
	public List<Object> getnfsSuspextTxnData(NFSSuspectTxnBean NFSSuspectTxnBean) {
		List<Object> data = new ArrayList<Object>();
		List<String> Column_list = new ArrayList<String>();
		try {
			Column_list = getColumnList("NFS_SUSPECTED_TRANSACTION");
			final List<String> cols = Column_list;
			
			
			String getData1 = "select * from  NFS_SUSPECTED_TRANSACTION where TO_CHAR(FILEDATE,'DD/MM/YYYY')='"+NFSSuspectTxnBean.getDate()+"'";
			
			List<Object> datalist=getJdbcTemplate().query(getData1,new ResultSetExtractor<List<Object>>(){

				@Override
				public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<Object> beanList = new ArrayList<Object>();
					while(rs.next()) {
						Map<String,String> tableData=new HashMap<String, String>();
						
						for(String column : cols)
						{
							tableData.put(column , rs.getString(column));
							/*tableData.put("CARD_NUMBER", rs.getString("CARD_NUMBER"));
						tableData.put("RRN", rs.getString("RRN"));
						tableData.put("TRAN_DATE", rs.getString("TRAN_DATE"));
						tableData.put("TRAN_TIME", rs.getString("TRAN_TIME"));
						tableData.put("TRAN_AMOUNT", rs.getString("TRAN_AMOUNT"));
						tableData.put("MSGTYPE",rs.getString("MSGTYPE"));
						tableData.put("TRAN_TYPE", rs.getString("TRAN_TYPE"));
						tableData.put("RESPCODE", rs.getString("RESPCODE"));
						tableData.put("REV_REASON", rs.getString("REV_REASON"));
						tableData.put("FROM_ACCOUNT", rs.getString("FROM_ACCOUNT"));
						tableData.put("TERMID", rs.getString("TERMID"));
						tableData.put("LOCATION", rs.getString("LOCATION"));
						tableData.put("CREATEDDATE", rs.getString("CREATEDDATE"));*/
						}
					    beanList.add(tableData);
					}
					return beanList;
				}
				
			});	
			System.out.println("datalist   size  "+datalist.size());
			/*List<String> Column_list = new ArrayList<String>();
			Column_list.add("CARD_NUMBER");
			Column_list.add("RRN");	
			Column_list.add("TRAN_DATE");
			Column_list.add("TRAN_TIME");
			Column_list.add("TRAN_AMOUNT");
			Column_list.add("MSGTYPE");	
			Column_list.add("TRAN_TYPE");	
			Column_list.add("RESPCODE");	
			Column_list.add("REV_REASON");	
			Column_list.add("FROM_ACCOUNT");	
			Column_list.add("TERMID");	
			Column_list.add("LOCATION");	
			Column_list.add("CREATEDDATE");	 */
			data.add(Column_list);
			data.add(datalist);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String processNFSSuspectTxn(String network, String date) {
		try {
			Map<String, Object> inParams = new HashMap<String, Object>();
            inParams.put("FILEDT",date);
			SuspectTxnProcc suspectTxnProcc = new SuspectTxnProcc(getJdbcTemplate());
			Map<String, Object> outParams = suspectTxnProcc.execute(inParams);
			return "Processing completed Succesfully !! \nPlease download Reports now.";
           } catch (Exception e) {
        	e.printStackTrace();
        	return "fail";
		}
		
	}
	
@Override
public HashMap<String, Object> validateNFSSettProcess(String network, String fromDate, String toDate)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	try
	{
		String checkProcess = "select count(cycle) from ("
				+ "select cycle from nfs_settlement_report where  filedate between ? and ?"
				+ "group by filedate,cycle)";
		int count = getJdbcTemplate().queryForObject(checkProcess, new Object[] {fromDate, toDate}, Integer.class);
		
		String getdaysCount = "select (to_date(?,'dd/mm/yyyy') - to_date(?,'dd/mm/yyyy'))+1 from dual";
		int daysCount = getJdbcTemplate().queryForObject(getdaysCount, new Object[] {fromDate, toDate}, Integer.class);
		
		if((daysCount*4) == count)
		{
			output.put("result", true);
		}
		else
		{
			output.put("result", false);
			output.put("msg", "Settlements are not processed for selected date!");
		}
		
	}
	catch(Exception e)
	{
		output.put("result", false);
		output.put("msg", "Exception Occurred while validating");
		logger.info("Exception in validateNFSSettProcess  "+e);
	}
	return output;
}

	
class SuspectTxnProcc extends StoredProcedure {
		
		private static final String procName = "NFS_SUSPECTED_TRANSACTION_PROCESS";

		SuspectTxnProcc(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("FILEDT",
					Types.VARCHAR));
			declareParameter(new SqlOutParameter("msg", Types.VARCHAR));
			compile();
		}
	}

public ArrayList<String> getColumnList(String tableName) {

	//String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
	String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%'";
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
