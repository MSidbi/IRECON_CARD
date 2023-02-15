package com.recon.dao.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;


import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.IGlBalanceDao;
import com.recon.dao.impl.ReconProcessDaoImpl.IssClassificaton;
import com.recon.model.SettlementBean;
import com.recon.util.GLBalanceBean;
import com.recon.util.OracleConn;
import com.recon.util.demo;

@Component
public class GlBalanceDaoImpl extends JdbcDaoSupport implements IGlBalanceDao  {

//	For Issuer
	@Override
	public String getGLBalance(float closingBal, float totalcashdisp,String filedate,String category,String subcategory,String[] arrsetdt, String[] arritem_settlamnt) {
		
		String msg="";
		
		String query = "Select distinct comapre_flag from main_file_upload_dtls where rownum = 1 and filedate='"+filedate+"' and "
				+ " fileid in(select fileid from main_filesource where file_category='"+category+"' and file_subcategory='"+subcategory+"')"
				+ " order by (case  when comapre_flag = 'N' then 1 when comapre_flag ='Y' then 2  end) ";
		
		
		String Flag = getJdbcTemplate().queryForObject(query, String.class) ;
		
		
		if(Flag.equalsIgnoreCase("Y")) {
			
			try {
				
				if(category.equals("CASHNET")) {
				
				if(subcategory.equals("ISSUER")) {
				
					
				
					if(glBalanceproc(category, subcategory, filedate, null,arrsetdt, arritem_settlamnt)) {
						
						for(int i = 0;i<arritem_settlamnt.length;i++) {
							
							getJdbcTemplate().execute("insert into Cashnet_ACQ_GL_BAL_DIS values ('"+arrsetdt[i]+"','"+arritem_settlamnt[i]+"','N')");
						
						}
						
						msg = "GL Balance Calculated.";
						
					}else {
						
						msg ="Error Occurred While calculating GL.";
					}
				} else if(subcategory.equals("ACQUIRER")) {
					
					
					
					if(glacqBalanceproc(category, subcategory, filedate, null, arrsetdt,  arritem_settlamnt)) {
						
						for(int i = 0;i<arritem_settlamnt.length;i++) {
							
							getJdbcTemplate().execute("insert into Cashnet_ACQ_GL_BAL_DIS values('"+arrsetdt[i]+"','"+arritem_settlamnt[i]+"','N')");
						
						}
						
						msg = "GL Balance Calculated.";
						
					}else {
						
						msg ="Error Occurred While calculating GL.";
					}
					
					
					}
				} else  if(category.equals("VISA")) {
					
					if(subcategory.equals("ISSUER")) {
					
						
					
						if(visaglacqBalanceproc(category, subcategory, filedate, null,arrsetdt, arritem_settlamnt)) {
							
							for(int i = 0;i<arritem_settlamnt.length;i++) {
								
								getJdbcTemplate().execute("insert into visa_ACQ_GL_BAL_DIS values ('"+arrsetdt[i]+"','"+arritem_settlamnt[i]+"','N')");
							
							}
							
							msg = "GL Balance Calculated.";
							
						}else {
							
							msg ="Error Occurred While calculating GL.";
						}
					} else if(subcategory.equals("ACQUIRER")) {
						
						
						
						if(visaglacqBalanceproc(category, subcategory, filedate, null, arrsetdt,  arritem_settlamnt)) {
							
							for(int i = 0;i<arritem_settlamnt.length;i++) {
								
								getJdbcTemplate().execute("insert into VISA_ACQ_GL_BAL_DIS values ('"+arrsetdt[i]+"','"+arritem_settlamnt[i]+"','N')");
							
							}
							
							msg = "GL Balance Calculated.";
							
						}else {
							
							msg ="Error Occurred While calculating GL.";
						}
						
						
						}
					}
				
				
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msg ="Error Occurred While calculating GL Balance.";
				e.printStackTrace();
			} 
			
			
		} else {
			
			msg = "Recon process not completed for selected date.";
			
		}
		
		
		
		return msg;
	}
	
	public boolean glBalanceproc(String category, String subCat,
			String filedate, String entry_by,String[] arrsetdt, String[] arritem_settlamnt) throws ParseException, Exception {
		try {
			logger.info("***** glBalance Start ****");

			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("I_CATEGORY", category);
			inParams.put("I_SUBCATEGORY", subCat);
			inParams.put("I_FILE_DATE", filedate);
			inParams.put("I_ENTRY_BY", entry_by);

			glBalance acqclassificaton = new glBalance(
					getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);

			// logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** glBalance End ****");

			if (outParams.get("ERROR_MESSAGE") != null) {

				return false;
			} else {

				return true;
			}

		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
			logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
					new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
			return false;
		}

	}

	class glBalance extends StoredProcedure {
		private static final String procName = "CASHNET_GL_BALANCING_Proc ";

		glBalance(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY", Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",
					Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",
					Types.VARCHAR));
			compile();
		}
	}

	@Override
	public void generate_Reports(SettlementBean settlementBeanObj,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,String difference) {

    	Connection con;
        Statement st;
        ResultSet rset = null;
        List<String> files = new ArrayList<>();
        //Row header = sheet.createRow(0);
       List<List<String>> Records = new ArrayList<>();
       //ArrayList<Excel_Bean> arr=new ArrayList<Excel_Bean>();
       OracleConn conn;
       try {
    	  
    	   
    	   con = getConnection();
    	   //1. 	GET FILES FROM main recon sequence table
    	  // String GET_FILES = "SELECT FILENAME FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = '"+settlementBeanObj.getCategory()+"' AND FILE_SUBCATEGORY = '"+settlementBeanObj.getStsubCategory()+"'";
    	   List<String> stTableNames = new ArrayList<String>();
    	   
    	   stTableNames.add("MAIN_GL_CASHNET_ISS_SWITCH");
    	   stTableNames.add("CUTOFF_GL_CASHNET_ISS_SWITCH");
    	   stTableNames.add("MAIN_GL_CASHNET_ISS_CBS");
    	   stTableNames.add("CUTOFF_GL_CASHNET_ISS_CBS");

    	   //CREATE FOLDERS
    	   
    	   SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");

           java.util.Date date=sdf.parse(settlementBeanObj.getDatepicker());
           
            sdf=new SimpleDateFormat("ddMMMyyyy");
            
           String convdate = sdf.format(date);
           

          
           
           String stfile_name = "GL_balance_Report"+convdate+".xlsx";
          
		   
		   OutputStream outStream = response.getOutputStream();

			XSSFWorkbook wb = new XSSFWorkbook();

			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb, 1000);
			
			
			SXSSFSheet mainsheet = (SXSSFSheet) workbook1.createSheet("GL-BALANCE REPORT");
			
			SXSSFRow headerrow = mainsheet.createRow(0);
			headerrow.createCell(0).setCellValue("Cashnet Issuer Transaction Reconciliation");
			headerrow.createCell(1).setCellValue("Date");
			headerrow.createCell(2).setCellValue(settlementBeanObj.getDatepicker());
			
			SXSSFRow datarow1 = mainsheet.createRow(1);
			datarow1.createCell(0).setCellValue("Closing Balance as per finacle");
			datarow1.createCell(1).setCellValue(closing_balance);
			
			SXSSFRow datarow2 = mainsheet.createRow(2);
			datarow2.createCell(0).setCellValue("Total Cash Dispensed as per Euronet Report");
			datarow2.createCell(1).setCellValue(cash_dispense);
			
			
			SXSSFRow datarow3 = mainsheet.createRow(3);
			datarow3.createCell(0).setCellValue("Difference (A)");
			datarow3.createCell(1).setCellValue(difference);
			
			SXSSFRow datarow4 = mainsheet.createRow(4);
			datarow4.createCell(0).setCellValue("Cut-off Transaction in Switch_Unrecon");
			datarow4.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ISS_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow5 = mainsheet.createRow(5);
			datarow5.createCell(0).setCellValue("Cut-off Transaction in CBS_Unrecon ");
			datarow5.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ISS_cbs", Integer.class));
			
			SXSSFRow datarow6 = mainsheet.createRow(6);
			datarow6.createCell(0).setCellValue("Reversal Transaction in Switch_Unrecon");
			datarow6.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ISS_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow7 = mainsheet.createRow(7);
			datarow7.createCell(0).setCellValue("Reversal Transaction in CBS_Unrecon ");
			datarow7.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ISS_cbs", Integer.class));
			

			
			
			
		
			
           int filecount = 0;
    	   for(String stTableName : stTableNames)
    	   {
    		   int datacount=1;
    		   SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet(stTableName.replace("MAIN_GL", "REVERSAL"));
    		   List<String> Column_list  = getColumnList(stTableName);
    			   int colcount = 0;
    			   String headercolumns="";
    			   SXSSFRow header = sheet.createRow(0);
    			   for(int i =0;i<Column_list.size() ;i++)
    			   {
    				  
    				   header.createCell(i).setCellValue(Column_list.get(i));
    				   
    			   }
    			  
    			   String GET_DATA = "Select * from "+stTableName;
    			    
    			   System.out.println(GET_DATA);
    			   PreparedStatement ps = con.prepareStatement(GET_DATA);
    			   rset = ps.executeQuery();
    			    
    			   try {
    				   
    				   int count = 0;
    				   while(rset.next()) {
    					   ++count;
    				   SXSSFRow data = sheet.createRow(datacount);
    				   int valuecnt=1;
    				   for(int i =0;i<Column_list.size() ;i++)
        			   {
        				  
    					   data.createCell(i).setCellValue(rset.getString(valuecnt));
        				   
    					   valuecnt++;
        			   }
    				   datacount++;
    				   } 
    				   
    				   if(count ==0){
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }
    				   /*else {
    					   
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }*/
    				
    					
     			   
    			   } catch(Exception e)
     			   {
    				   e.printStackTrace();
     				   System.out.println("eXCEPTION IS IN fileoutputstream "+e);
     			   }
    			   
    			   
    		   
    		  
    	   }
    	   
    	   response.setContentType("application/vnd.ms-excel");
   		response.setHeader("Content-disposition", "attachment; filename="+stfile_name);
    	   
   		workbook1.write(outStream);
        outStream.close();
		response.getOutputStream().flush();
		   
    	  
		
           }
           catch(Exception fe)
           {
        	   fe.printStackTrace();
        	   System.out.println("Exception in zipping is "+fe);
           }
		   
    	   
    	   
    	   
    	   
    	 //  zos.close();
      
    }

	
	
	
	public ArrayList<String> getColumnList(String tableName) {
		
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
	
	@Override
	public void generate_cashnet_acq_Reports(SettlementBean settlementBeanObj,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,String difference) {

    	Connection con;
        Statement st;
        ResultSet rset = null;
        List<String> files = new ArrayList<>();
        //Row header = sheet.createRow(0);
       List<List<String>> Records = new ArrayList<>();
       //ArrayList<Excel_Bean> arr=new ArrayList<Excel_Bean>();
       OracleConn conn;
       try {
    	  
    	   
    	   con = getConnection();
    	   //1. 	GET FILES FROM main recon sequence table
    	  // String GET_FILES = "SELECT FILENAME FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = '"+settlementBeanObj.getCategory()+"' AND FILE_SUBCATEGORY = '"+settlementBeanObj.getStsubCategory()+"'";
    	   List<String> stTableNames = new ArrayList<String>();
    	   
    	   stTableNames.add("MAIN_GL_CASHNET_ACQ_SWITCH");
    	   stTableNames.add("CUTOFF_GL_CASHNET_ACQ_SWITCH");
    	   stTableNames.add("MAIN_GL_CASHNET_ACQ_CBS");
    	   stTableNames.add("CUTOFF_GL_CASHNET_ACQ_CBS");

    	   //CREATE FOLDERS
    	   
    	   SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");

           java.util.Date date=sdf.parse(settlementBeanObj.getDatepicker());
           
            sdf=new SimpleDateFormat("ddMMMyyyy");
            
           String convdate = sdf.format(date);
           

          
           
           String stfile_name = "GL_balance_Report"+convdate+".xlsx";
          
		   
		   OutputStream outStream = response.getOutputStream();

			XSSFWorkbook wb = new XSSFWorkbook();

			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb, 1000);
			
			
			SXSSFSheet mainsheet = (SXSSFSheet) workbook1.createSheet("GL-BALANCE REPORT");
			
			SXSSFRow headerrow = mainsheet.createRow(0);
			headerrow.createCell(0).setCellValue("Cashnet Issuer Transaction Reconciliation");
			headerrow.createCell(1).setCellValue("Date");
			headerrow.createCell(2).setCellValue(settlementBeanObj.getDatepicker());
			
			SXSSFRow datarow1 = mainsheet.createRow(1);
			datarow1.createCell(0).setCellValue("Closing Balance as per finacle");
			datarow1.createCell(1).setCellValue(closing_balance);
			
			SXSSFRow datarow2 = mainsheet.createRow(2);
			datarow2.createCell(0).setCellValue("Total Cash Dispensed as per Euronet Report");
			datarow2.createCell(1).setCellValue(cash_dispense);
			
			
			SXSSFRow datarow3 = mainsheet.createRow(3);
			datarow3.createCell(0).setCellValue("Difference (A)");
			datarow3.createCell(1).setCellValue(difference);
			
			SXSSFRow datarow4 = mainsheet.createRow(4);
			datarow4.createCell(0).setCellValue("Cut-off Transaction in Switch_Unrecon");
			datarow4.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ACQ_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow5 = mainsheet.createRow(5);
			datarow5.createCell(0).setCellValue("Cut-off Transaction in CBS_Unrecon ");
			datarow5.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ACQ_cbs", Integer.class));
			
			SXSSFRow datarow6 = mainsheet.createRow(6);
			datarow6.createCell(0).setCellValue("Reversal Transaction in Switch_Unrecon");
			datarow6.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow7 = mainsheet.createRow(7);
			datarow7.createCell(0).setCellValue("EXCESS Transaction in CBS_Unrecon ");
			datarow7.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_cbs WHERE DCRS_REMARKS LIKE '%EXCESS-CBS%'", Integer.class));
			
			SXSSFRow datarow8 = mainsheet.createRow(8);
			datarow8.createCell(0).setCellValue("SHORT Transaction in CBS_Unrecon ");
			datarow8.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_cbs WHERE DCRS_REMARKS LIKE '%SHORT-CBS%'", Integer.class));
			
			SXSSFRow datarow9 = mainsheet.createRow(9);
			datarow9.createCell(0).setCellValue(" Network Unrecon (Excess) ");
			datarow9.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select nvl(sum(replace(TXN_AMOUNT,',')),0) from MAIN_GL_CASHNET_ACQ_CASHNET ", Integer.class));
			

			
			
			
		
			
           int filecount = 0;
    	   for(String stTableName : stTableNames)
    	   {
    		   int datacount=1;
    		   SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet(stTableName.replace("MAIN_GL", "REVERSAL"));
    		   List<String> Column_list  = getColumnList(stTableName);
    			   int colcount = 0;
    			   String headercolumns="";
    			   SXSSFRow header = sheet.createRow(0);
    			   for(int i =0;i<Column_list.size() ;i++)
    			   {
    				  
    				   header.createCell(i).setCellValue(Column_list.get(i));
    				   
    			   }
    			  
    			   String GET_DATA = "Select * from "+stTableName;
    			    
    			   System.out.println(GET_DATA);
    			   PreparedStatement ps = con.prepareStatement(GET_DATA);
    			   rset = ps.executeQuery();
    			    
    			   try {
    				   
    				   int count = 0;
    				   while(rset.next()) {
    					   ++count;
    				   SXSSFRow data = sheet.createRow(datacount);
    				   int valuecnt=1;
    				   for(int i =0;i<Column_list.size() ;i++)
        			   {
        				  
    					   data.createCell(i).setCellValue(rset.getString(valuecnt));
        				   
    					   valuecnt++;
        			   }
    				   datacount++;
    				   } 
    				   
    				   if(count ==0){
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }
    				   /*else {
    					   
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }*/
    				
    					
     			   
    			   } catch(Exception e)
     			   {
    				   e.printStackTrace();
     				   System.out.println("eXCEPTION IS IN fileoutputstream "+e);
     			   }
    			   
    			   
    		   
    		  
    	   }
    	   
    	   response.setContentType("application/vnd.ms-excel");
   		response.setHeader("Content-disposition", "attachment; filename="+stfile_name);
    	   
   		workbook1.write(outStream);
        outStream.close();
		response.getOutputStream().flush();
		   
    	  
		
           }
           catch(Exception fe)
           {
        	   fe.printStackTrace();
        	   System.out.println("Exception in zipping is "+fe);
           }
		   
    	   
    	   
    	   
    	   
    	 //  zos.close();
      
    }

	

	@Override
	public void DeleteFiles(String path) {
		// TODO Auto-generated method stub
		
	}
	
	
	public boolean glacqBalanceproc(String category, String subCat,
			String filedate, String entry_by,String[] arrsetdt, String[] arritem_settlamnt) throws ParseException, Exception {
		try {
			logger.info("***** glBalance Start ****");

			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("I_CATEGORY", category);
			inParams.put("I_SUBCATEGORY", subCat);
			inParams.put("I_FILE_DATE", filedate);
			inParams.put("I_ENTRY_BY", entry_by);

			glacqBalance acqclassificaton = new glacqBalance(
					getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);

			// logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** glBalance End ****");

			if (outParams.get("ERROR_MESSAGE") != null) {

				return false;
			} else {

				return true;
			}

		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
			logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
					new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
			return false;
		}

	}

	class glacqBalance extends StoredProcedure {
		private static final String procName = "CASHNET_ACQ_GL_BALANCING_Proc ";

		glacqBalance(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY", Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",
					Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",
					Types.VARCHAR));
			compile();
		}
	}

	@Override
	public String checkdispense(String filedate, String subcat,String [] arrsetdt,String category) {
		
		String query="";
		
		if(category.equals("CASHNET") ) {
			
			if(subcat.equals("ISSUER")) {
				
				if(arrsetdt.length > 0) {
					
					query = "select nvl(sum(transaction_amount),0) from cashnet_cashnet_iss_rawdata where response_code in('00','26','98') and"
							+ " filedate between '"+arrsetdt[0]+"' and '"+filedate+"' and"
							+ " transaction_type  in('04','61','89','29','88') and  to_number(ISSUER_1_SETTLEMENT_AMOUNT)> 0 "; 
					
				} else {
				query = "select nvl(sum(transaction_amount),0) from cashnet_cashnet_iss_rawdata where response_code in('00','26','98') and filedate='"+filedate+"' and"
						+ " transaction_type  in('04','61','89','29','88') and  to_number(ISSUER_1_SETTLEMENT_AMOUNT)> 0 "; 
				}
				
			} else if(subcat.equals("ACQUIRER")) {
				
				if(arrsetdt.length > 1) {
											
				} else {
				
					query = "select nvl(sum(transaction_amount),0) from cashnet_cashnet_acq_rawdata where response_code in('00','26') and filedate='"+filedate+"' "; 
				}
			}
			
		} else if(category.equals("VISA")) {
			
				if(subcat.equals("ACQUIRER")) {
				
				if(arrsetdt.length > 1) {
											
				} else {
				
					query = "select nvl(sum(to_number(source_amount)),0) from settlement_VISA_VISA where DCRS_REMARKS  in('VISA_ACQ-UNRECON-2','VISA_ACQ-MATCHED-2') and filedate='"+filedate+"' "; 
				}
			}
		}
		
		
		return getJdbcTemplate().queryForObject(query, String.class) ;
	}

	@Override
	public List<GLBalanceBean> prevdispense(String filedate, String subcat,String category) {
		// TODO Auto-generated method stub
		
		String query="";
		ArrayList<GLBalanceBean> balanceBeans = new ArrayList<GLBalanceBean>();
		
		query = getQuery(filedate, subcat, category);
		
		balanceBeans = (ArrayList<GLBalanceBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(GLBalanceBean.class)) ;
		
		return balanceBeans ;
	}
	
	
	public String getQuery (String filedate, String subcat,String category) {
		
		String query="";
		
		if(category.equals("CASHNET")) {
			
			if(subcat.equals("ISSUER")) {
				
				query = "select to_char(filedate,'dd/mon/rrrr') filedate,Amount from Cashnet_ISS_GL_BAL_DIS where  filedate <'"+filedate+"' "; 
				
			} else if(subcat.equals("ACQUIRER")) {
				
				query = "select to_char(filedate,'dd/mon/rrrr') filedate,Amount from Cashnet_ACQ_GL_BAL_DIS where  filedate <'"+filedate+"' ";
			}
		}  if(category.equals("VISA")) {
				
				if(subcat.equals("ISSUER")) {
					
					query = "select to_char(filedate,'dd/mon/rrrr') filedate,Amount from VISA_ISS_GL_BAL_DIS where  filedate <'"+filedate+"' "; 
					
				} else if(subcat.equals("ACQUIRER")) {
					
					query = "select to_char(filedate,'dd/mon/rrrr') filedate,Amount from VISA_ACQ_GL_BAL_DIS where  filedate <'"+filedate+"' ";
				}
			
		}
		
		
		
		return  query;
	}
	
	
	
	public boolean visaglacqBalanceproc(String category, String subCat,
			String filedate, String entry_by,String[] arrsetdt, String[] arritem_settlamnt) throws ParseException, Exception {
		try {
			logger.info("***** glBalance Start ****");

			String response = null;
			Map<String, Object> inParams = new HashMap<String, Object>();

			inParams.put("I_CATEGORY", category);
			inParams.put("I_SUBCATEGORY", subCat);
			inParams.put("I_FILE_DATE", filedate);
			inParams.put("I_ENTRY_BY", entry_by);

			glvisaacqBalance acqclassificaton = new glvisaacqBalance(
					getJdbcTemplate());
			Map<String, Object> outParams = acqclassificaton.execute(inParams);

			// logger.info("outParams msg1"+outParams.get("msg1"));
			logger.info("***** glBalance End ****");

			if (outParams.get("ERROR_MESSAGE") != null) {

				return false;
			} else {

				return true;
			}

		} catch (Exception e) {
			demo.logSQLException(e, "ReconProcessDaoImpl.ISSClassifydata");
			logger.error(" error in  ReconProcessDaoImpl.ISSClassifydata",
					new Exception(" ReconProcessDaoImpl.ISSClassifydata", e));
			return false;
		}

	}

	class glvisaacqBalance extends StoredProcedure {
		private static final String procName = "VISA_ACQ_GL_BALANCING_Proc ";

		glvisaacqBalance(JdbcTemplate JdbcTemplate) {
			super(JdbcTemplate, procName);
			setFunction(false);

			declareParameter(new SqlParameter("I_FILE_DATE",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_CATEGORY", Types.VARCHAR));
			declareParameter(new SqlParameter("I_SUBCATEGORY",
					Types.VARCHAR));
			declareParameter(new SqlParameter("I_ENTRY_BY", Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_CODE",
					Types.VARCHAR));
			declareParameter(new SqlOutParameter("ERROR_MESSAGE",
					Types.VARCHAR));
			compile();
		}
	}

	@Override
	public void generate_visaReports(SettlementBean settlementBean,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,
			String difference) {

			if(subCat.equals("ACQUIRER")) {
				
				visa_ACQ_REPORTS(settlementBean, response, closing_balance, cash_dispense, filedate, subCat, difference);
			}
		
	}
	
	public void visa_ACQ_REPORTS (SettlementBean settlementBeanObj,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,
			String difference) {
		


    	Connection con;
        Statement st;
        ResultSet rset = null;
        List<String> files = new ArrayList<>();
        //Row header = sheet.createRow(0);
       List<List<String>> Records = new ArrayList<>();
       //ArrayList<Excel_Bean> arr=new ArrayList<Excel_Bean>();
       OracleConn conn;
       try {
    	  
    	   
    	   con = getConnection();
    	   //1. 	GET FILES FROM main recon sequence table
    	  // String GET_FILES = "SELECT FILENAME FROM MAIN_FILESOURCE WHERE FILE_CATEGORY = '"+settlementBeanObj.getCategory()+"' AND FILE_SUBCATEGORY = '"+settlementBeanObj.getStsubCategory()+"'";
    	   List<String> stTableNames = new ArrayList<String>();
    	   
    	   stTableNames.add("MAIN_GL_VISA_ACQ_SWITCH");
    	   stTableNames.add("CUTOFF_GL_VISA_ACQ_SWITCH");
    	   stTableNames.add("MAIN_GL_VISA_ACQ_CBS");
    	   stTableNames.add("CUTOFF_GL_VISA_ACQ_CBS");
    	   stTableNames.add("MAIN_GL_VISA_ACQ_visa");

    	   //CREATE FOLDERS
    	   
    	   SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");

    	   
           java.util.Date date=sdf.parse(settlementBeanObj.getDatepicker());
           
            sdf=new SimpleDateFormat("ddMMMyyyy");
            
           String convdate = sdf.format(date);
           

          
           
           String stfile_name = "GL_balance_Report"+convdate+".xlsx";
          
		   
		   OutputStream outStream = response.getOutputStream();

			XSSFWorkbook wb = new XSSFWorkbook();

			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb, 1000);
			
			
			SXSSFSheet mainsheet = (SXSSFSheet) workbook1.createSheet("GL-BALANCE REPORT");
			
			SXSSFRow headerrow = mainsheet.createRow(0);
			headerrow.createCell(0).setCellValue("Cashnet Issuer Transaction Reconciliation");
			headerrow.createCell(1).setCellValue("Date");
			headerrow.createCell(2).setCellValue(settlementBeanObj.getDatepicker());
			
			SXSSFRow datarow1 = mainsheet.createRow(1);
			datarow1.createCell(0).setCellValue("Closing Balance as per finacle");
			datarow1.createCell(1).setCellValue(closing_balance);
			
			SXSSFRow datarow2 = mainsheet.createRow(2);
			datarow2.createCell(0).setCellValue("Total Cash Dispensed as per Euronet Report");
			datarow2.createCell(1).setCellValue(cash_dispense);
			
			
			SXSSFRow datarow3 = mainsheet.createRow(3);
			datarow3.createCell(0).setCellValue("Difference (A)");
			datarow3.createCell(1).setCellValue(difference);
			
			SXSSFRow datarow4 = mainsheet.createRow(4);
			datarow4.createCell(0).setCellValue("Cut-off Transaction in Switch_Unrecon");
			datarow4.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ACQ_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow5 = mainsheet.createRow(5);
			datarow5.createCell(0).setCellValue("Cut-off Transaction in CBS_Unrecon ");
			datarow5.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from cutoff_gl_CASHNET_ACQ_cbs", Integer.class));
			
			SXSSFRow datarow6 = mainsheet.createRow(6);
			datarow6.createCell(0).setCellValue("Reversal Transaction in Switch_Unrecon");
			datarow6.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_switch", Integer.class));
			
			//Cut-off Transaction in CBS_Unrecon ( 98 Nos.)
		
			SXSSFRow datarow7 = mainsheet.createRow(7);
			datarow7.createCell(0).setCellValue("EXCESS Transaction in CBS_Unrecon ");
			datarow7.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_cbs WHERE DCRS_REMARKS LIKE '%EXCESS-CBS%'", Integer.class));
			
			SXSSFRow datarow8 = mainsheet.createRow(8);
			datarow8.createCell(0).setCellValue("SHORT Transaction in CBS_Unrecon ");
			datarow8.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select sum(replace(amount,',')) from MAIN_GL_CASHNET_ACQ_cbs WHERE DCRS_REMARKS LIKE '%SHORT-CBS%'", Integer.class));
			
			SXSSFRow datarow9 = mainsheet.createRow(9);
			datarow9.createCell(0).setCellValue(" Network Unrecon (Excess) ");
			datarow9.createCell(1).setCellValue((double) getJdbcTemplate().queryForObject("select nvl(sum(replace(TXN_AMOUNT,',')),0) from MAIN_GL_CASHNET_ACQ_CASHNET ", Integer.class));
			

			
			
			
		
			
           int filecount = 0;
    	   for(String stTableName : stTableNames)
    	   {
    		   int datacount=1;
    		   SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet(stTableName.replace("MAIN_GL", "REVERSAL"));
    		   List<String> Column_list  = getColumnList(stTableName);
    			   int colcount = 0;
    			   String headercolumns="";
    			   SXSSFRow header = sheet.createRow(0);
    			   for(int i =0;i<Column_list.size() ;i++)
    			   {
    				  
    				   header.createCell(i).setCellValue(Column_list.get(i));
    				   
    			   }
    			  
    			   String GET_DATA = "Select * from "+stTableName;
    			    
    			   System.out.println(GET_DATA);
    			   PreparedStatement ps = con.prepareStatement(GET_DATA);
    			   rset = ps.executeQuery();
    			    
    			   try {
    				   
    				   int count = 0;
    				   while(rset.next()) {
    					   ++count;
    				   SXSSFRow data = sheet.createRow(datacount);
    				   int valuecnt=1;
    				   for(int i =0;i<Column_list.size() ;i++)
        			   {
        				  
    					   data.createCell(i).setCellValue(rset.getString(valuecnt));
        				   
    					   valuecnt++;
        			   }
    				   datacount++;
    				   } 
    				   
    				   if(count ==0){
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }
    				   /*else {
    					   
    					   SXSSFRow data = sheet.createRow(datacount);
    					   data.createCell(0).setCellValue("records not found");
    				   }*/
    				
    					
     			   
    			   } catch(Exception e)
     			   {
    				   e.printStackTrace();
     				   System.out.println("eXCEPTION IS IN fileoutputstream "+e);
     			   }
    			   
    			   
    		   
    		  
    	   }
    	   
    	   response.setContentType("application/vnd.ms-excel");
   		response.setHeader("Content-disposition", "attachment; filename="+stfile_name);
    	   
   		workbook1.write(outStream);
        outStream.close();
		response.getOutputStream().flush();
		   
    	  
		
           }
           catch(Exception fe)
           {
        	   fe.printStackTrace();
        	   System.out.println("Exception in zipping is "+fe);
           }
		   
    	   
    	   
    	   
    	   
    	 //  zos.close();
      
    
	}


	
	

		

}
