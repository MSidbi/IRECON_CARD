package com.recon.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

//import sun.org.mozilla.javascript.internal.regexp.SubString;

import com.recon.dao.DisputeDao;
import com.recon.util.JsonBean;
import com.recon.util.SearchData;
import com.recon.util.demo;

@Component
public class DisputeDaoImpl extends JdbcDaoSupport implements DisputeDao{

	@Override
	public SearchData returndata(SearchData searchdata) throws Exception{		
		List<String> headList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		List<String> matchHeadList = new ArrayList<String>();
		logger.info("***** DisputeDaoImpl.returndata Start ****");
		try{	
			String network = "";
			if (searchdata.getFilename().equals("network")){
				searchdata.setFilename(searchdata.getCategory());
			}
			
			
			// GET Columns headers
			String query = "SELECT distinct column_name FROM   all_tab_cols WHERE  table_name = 'SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getFilename()+"' order by 1";
			logger.info(query);
			
			
			headList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
	            public String mapRow(ResultSet rs, int rowNum) 
	                    throws SQLException {
						return rs.getString(1);
						}
						});
			
			logger.info(headList);
			
		
		String GET_DATA = "SELECT rownum,os1.* FROM SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getFilename()+" os1 WHERE (trim(DCRS_REMARKS) "
				+ "like '%"+searchdata.getSubCategory().substring(0,3)+"-UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%') AND "
				+ "TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY') and rownum<=10  order by rownum";
		
		Connection con = getConnection();
		PreparedStatement ps=  con.prepareStatement(GET_DATA);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		int columncount = metaData.getColumnCount();
		
		while(rs.next()) {
			
			
			for(int i=1;i<=columncount;i++ ) {
				
				dataList.add(rs.getString(i));
				
			}
			
			
		}
		
		for(int columns=1;columns<columncount+1;columns++) {
			
			headList.add(metaData.getColumnName(columns));
			
		}
		
		String query1 = "select MATCH_HEADER from DISPUTE_SEARCH_CRITERIA where CATEGORY = '"+searchdata.getCategory()+"' and SUB_CATEGORY = '"+searchdata.getSubCategory()+"' "
				+ " and FILENAME = '"+searchdata.getFilename()+"' order by SEQ_ID";
		PreparedStatement ps1=  con.prepareStatement(query1);
		ResultSet rs1 = ps1.executeQuery();
		while(rs1.next()) {
			matchHeadList.add(rs1.getString("MATCH_HEADER"));
		}
		
		searchdata.setExcelHeaders(headList);
		searchdata.setData(dataList);
		searchdata.setMatchHeadList(matchHeadList);
		
		logger.info("***** DisputeDaoImpl.returndata End ****");
		
		return searchdata;
		}catch(Exception e) {
			
			 demo.logSQLException(e, "DisputeDaoImpl.returndata");
			  logger.error(" error in DisputeDaoImpl.returndata", new Exception("DisputeDaoImpl.returndata",e));
			  //throw e;
			return null;
				
		}
	}

	@Override
	public boolean updateForceMatchData(String category, String stSubCategory,
			String file_name, String filedate, String insert) throws Exception {
		logger.info("***** DisputeDaoImpl.updateForceMatchData Start ****");
		try{
			List<String> matchHeadList = new ArrayList<String>();
			if(file_name.equals("network")){
				file_name=category;
			}
			if(insert==""){
				
				String query="Update settlement_"+category+"_"+file_name+" set dcrs_remarks = 'FORCE_MATCH' where TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+filedate+"','DD/MM/YY') "
						+ " and  (trim(DCRS_REMARKS) like '%"+stSubCategory.substring(0,3)+"UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%') and rownum<=10 ";
				logger.info("query=="+query);
				int rowupdate =  getJdbcTemplate().update(query);
				logger.info("rowupdate=="+rowupdate);
				if(rowupdate<1){
					return false;
				}
				
			}else{		
			
			String query1 = "select MATCH_HEADER from DISPUTE_SEARCH_CRITERIA where CATEGORY = '"+category+"' and SUB_CATEGORY = '"+stSubCategory+"' "
					+ " and FILENAME = '"+file_name+"' order by SEQ_ID";
			Connection con = getConnection();
			PreparedStatement ps1=  con.prepareStatement(query1);
			ResultSet rs1 = ps1.executeQuery();
			while(rs1.next()) {
				matchHeadList.add(rs1.getString("MATCH_HEADER"));
			}
			
			String temp[]=insert.split(",");
			for (int i=0;i<temp.length;i++){
				String temp1[]=temp[i].split("#");
				String val1=temp1[0];
				String val2 = temp1[1];
				String val3 = temp1[2];
				String val4 = temp1[3];
				
				String query="Update settlement_"+category+"_"+file_name+" set dcrs_remarks = 'FORCE_MATCH' where TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+filedate+"','DD/MM/YY') "
						+ " and  (trim(DCRS_REMARKS) like '%"+stSubCategory.substring(0,3)+"UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%')  and "+matchHeadList.get(0)+" in ('"+val1+"') "
						+ " and "+matchHeadList.get(1)+" in ('"+val2+"') and "+matchHeadList.get(2)+" in ('"+val3+"') and "+matchHeadList.get(3)+" in ('"+val4+"')";
				logger.info("query=="+query);
				int rowupdate =  getJdbcTemplate().update(query);
				logger.info("rowupdate=="+rowupdate);
				if(rowupdate<1){
					return false;
				}
			}
			
			}
			logger.info("***** DisputeDaoImpl.updateForceMatchData End ****");

		}catch(Exception e) {
			 demo.logSQLException(e, "DisputeDaoImpl.updateForceMatchData");
			  logger.error(" error in DisputeDaoImpl.updateForceMatchData", new Exception("DisputeDaoImpl.updateForceMatchData",e));
			//  throw e;
			return false;
				
		}
		return true;
	}


	@Override
	public Map<String, Object> returndata1(JsonBean searchdata,
			int jtStartIndex, int jtPageSize) {

		Map<String, Object> viewMap = new HashMap<String, Object>();
		final String category = searchdata.getCategory();
		final String subcat = searchdata.getSubCategory();
		String filename = searchdata.getFilename();
		String tablename = "";
		String subcat1="";

		String temp[] = filename.split("-");
		final String filename1 = temp[0];
		String fileid = temp[1];
		
		if(category.equals("CASHNET") || category.equals("NFS")){
			tablename = "SETTLEMENT_"+category+"_"+subcat.substring(0,3)+"_"+filename1;			
		}else if(!category.equals("MASTERCARD")){
			tablename = "SETTLEMENT_"+searchdata.getCategory()+"_"+filename1;
		}else{
			tablename = "SETTLEMENT_"+searchdata.getCategory()+"_"+filename1;
		}
		
		int start_index = 0;
		if (jtStartIndex != 0) {
			start_index = jtStartIndex;
		}
		int end_index = jtStartIndex + jtPageSize;
		
		/*String GET_DATA = "SELECT rownum,os1.* FROM SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getFilename()+" os1 WHERE (trim(DCRS_REMARKS) "
				+ "like '%"+searchdata.getSubCategory().substring(0,3)+"-UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%') AND "
				+ "TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY') and rownum<=10  order by rownum";*/
		String query = "";
		String query1 = "";
		if(!filename1.contains("CBS"))		{
		query = "select  * from (select os1.*,row_number() over( order by 1 desc)rn from "+tablename+" os1 where  (trim(DCRS_REMARKS) "
				+ "like '%UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%' OR trim(DCRS_REMARKS) LIKE '%UNMATCH%') ";
		if(!subcat.equals("-") && !category.equals("CASHNET") && !category.equals("NFS")){
			subcat1 = subcat.substring(0,3);
			query = query + " AND trim(DCRS_REMARKS) like '%"+subcat1+"%' ";
		}
		query = query + " and TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY')) "
				+ "where rn between "+start_index+"  and "+end_index+" order by 1 desc";
		
		
		query1 = "select  count(*) from (select os1.*,row_number() over( order by 1 desc)rn from "+tablename+" os1 where  (trim(DCRS_REMARKS) "
				+ "like '%UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%' OR trim(DCRS_REMARKS) LIKE '%UNMATCH%') ";
		if(!subcat.equals("-") && !category.equals("CASHNET") && !category.equals("NFS")){
			subcat1 = subcat.substring(0,3);
			query1 = query1 + " AND trim(DCRS_REMARKS) like '%"+subcat1+"%' ";
		}
		query1 = query1 + " and TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY')) ";
		
		}else{
			query = "select  * from (select os1.*,row_number() over( order by 1 desc)rn from glbl_cbs_unmatch os1 where "
					+ " TO_DATE(CBSFILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY') "
					+ " and foracid in (select search_pattern from main_compare_detail where file_id = "+fileid+"))"
					+ " where rn between "+start_index+"  and "+end_index+" order by 1 desc";
			
			query1 = "select  count(*) from (select os1.*,row_number() over( order by 1 desc)rn from glbl_cbs_unmatch os1 where "
					+ " TO_DATE(CBSFILEDATE,'DD/MM/YY') = TO_DATE('"+searchdata.getFiledate()+"','DD/MM/YY') "
					+ " and foracid in (select search_pattern from main_compare_detail where file_id = "+fileid+"))";
		}
		
		int search_count = getJdbcTemplate().queryForObject(query1, Integer.class);
		
		List<JsonBean> search_list =  (List<JsonBean>) getJdbcTemplate()
				.query(query,
						new Object[] {},
						new RowMapper<JsonBean>() {
							public JsonBean mapRow(ResultSet rs, int row)
									throws SQLException {
								JsonBean JsonBean = new JsonBean();
								JsonBean.setCategory(category);
								JsonBean.setSubCategory(subcat);
								JsonBean.setFilename(filename1);
								if(filename1.equals("SWITCH")){
									JsonBean.setCard_No(rs.getString("PAN"));
									JsonBean.setTran_date(rs.getString("LOCAL_DATE"));
									JsonBean.setAmount(rs.getString("AMOUNT"));
									JsonBean.setTermid(rs.getString("TERMID"));
									JsonBean.setLocal_time(rs.getString("LOCAL_TIME"));
									JsonBean.setTrace(rs.getString("TRACE"));
									JsonBean.setApprovalCode(rs.getString("AUTHNUM"));
									JsonBean.setArn(rs.getString("ISSUER"));
									JsonBean.setFiledate(rs.getString("FILEDATE"));
								}else if(!filename1.contains("CBS")){
									if(category.equals("RUPAY") ){
										JsonBean.setCard_No(rs.getString("PRIMARY_ACCOUNT_NUMBER"));
										JsonBean.setTran_date(rs.getString("DATE_SETTLEMENT"));
										JsonBean.setAmount(rs.getString("AMOUNT_SETTLEMENT"));	
										JsonBean.setApprovalCode(rs.getString("APPROVAL_CODE"));
										JsonBean.setArn(rs.getString("ACQUIRER_REFERENCE_DATA"));
										JsonBean.setUniqIdentifier(rs.getString("DATEANDTIME_LOCAL_TRANSACTION"));
									}else if(category.equals("VISA")){
										JsonBean.setCard_No(rs.getString("CARD_NUMBER"));
										JsonBean.setAmount(rs.getString("SOURCE_AMOUNT"));											
										JsonBean.setApprovalCode(rs.getString("AUTHORIZATION_CODE"));
										JsonBean.setArn(rs.getString("ARN"));
										JsonBean.setUniqIdentifier(rs.getString("DCRS_SEQ_NO"));
									}else if(category.equals("CASHNET") || category.equals("NFS")){
										JsonBean.setCard_No(rs.getString("PAN_NUMBER"));
										JsonBean.setTran_date(rs.getString("TRANSACTION_DATE"));
										JsonBean.setApprovalCode(rs.getString("APPROVAL_NUMBER"));																			
										if(category.equals("CASHNET") && subcat.equals("ISSUER")){
											JsonBean.setAmount(rs.getString("TRANSACTION_AMOUNT"));
											JsonBean.setArn(rs.getString("TRANSACTION_SERIAL"));//SYSTEM_TRACE_AUDIT//SYS_TRACE_AUDIT_NO
											JsonBean.setUniqIdentifier(rs.getString("SYSTEM_TRACE_AUDIT"));
										}else{
											JsonBean.setAmount(rs.getString("TXN_AMOUNT"));
											JsonBean.setArn(rs.getString("TXN_SERIAL_NO"));		//SYS_TRACE_AUDIT_NO		//SYS_TRACE_AUDIT_NO
											JsonBean.setUniqIdentifier(rs.getString("SYS_TRACE_AUDIT_NO"));
										}
									}else if(category.equals("POS") && filename1.equals("POS")){
										JsonBean.setCard_No(rs.getString("CARD_NUMBER"));
										JsonBean.setTran_date(rs.getString("PROCESSING_DATE"));
										JsonBean.setAmount(rs.getString("SOURCE_AMOUNT"));
										JsonBean.setApprovalCode(rs.getString("AUTHORIZATION_CODE"));
										JsonBean.setArn(rs.getString("ARN"));
										JsonBean.setUniqIdentifier(rs.getString("DCRS_SEQ_NO"));
									}									
									else if(category.equals("MASTERCARD")){
										if(filename1.equals("POS") && subcat.equals("ISSUER")){
											JsonBean.setCard_No(rs.getString("PAN"));
											JsonBean.setTran_date(rs.getString("DATE_VAL"));
											JsonBean.setAmount(rs.getString("AMOUNT"));
											JsonBean.setApprovalCode(rs.getString("APPROVAL_CODE"));
											JsonBean.setArn(rs.getString("AQUIERER_REF_NO"));	
											JsonBean.setUniqIdentifier(rs.getString("UNQ_IDENTIFIER"));						
										}else{
											JsonBean.setCard_No(rs.getString("PAN_NUM"));
											JsonBean.setTran_date(rs.getString("TRAN_DATE"));
											JsonBean.setAmount(rs.getString("COMPLTD_AMT_SETTMNT"));
											JsonBean.setApprovalCode(rs.getString("AUTH_ID"));
											JsonBean.setArn(rs.getString("TRACE_NUM"));
											JsonBean.setUniqIdentifier(rs.getString("REF_NO"));
										}
									}else if(category.equals("WCC")){
										JsonBean.setCard_No(rs.getString("CARD_NUMBER"));
										JsonBean.setAmount(rs.getString("DESTINATION_AMOUNT"));
										JsonBean.setApprovalCode(rs.getString("AUTHORIZATION_CODE"));
										JsonBean.setArn(rs.getString("ARN"));
										JsonBean.setUniqIdentifier(rs.getString("DCRS_SEQ_NO"));
									}else{
										
									}									
									JsonBean.setFiledate(rs.getString("FILEDATE"));
								}else{
									JsonBean.setCard_No(rs.getString("REMARKS"));
									JsonBean.setTran_date(rs.getString("TRAN_DATE"));
									JsonBean.setAmount(rs.getString("CBSAMOUNT"));
									JsonBean.setTermid(rs.getString("TRANID"));
									JsonBean.setLocal_time(rs.getString("PARTICULARALS"));
									JsonBean.setTrace(rs.getString("REFNO"));
									JsonBean.setFiledate(rs.getString("CBSFILEDATE"));
									JsonBean.setForacid(rs.getString("FORACID"));
									//JsonBean.setArn(rs.getString("ISSUER"));
								}
								
								return JsonBean;
							}
						});
		
		viewMap.put("search_count", search_count);
		viewMap.put("search_list", search_list);

		return viewMap;
		
	}

	@Override
	public boolean updateDispute(JsonBean searchdata) {
		logger.info("***** DisputeDaoImpl.updateDispute Start ****");
		try{
			String dt = "";
			String tablename = "";
			
			try {
				if(searchdata.getFilename().contains("CBS")){
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
					Date varDate=dateFormat.parse(searchdata.getFiledate().substring(0,11).toString());
				    dateFormat=new SimpleDateFormat("dd/MM/YY");
				    dt = dateFormat.format(varDate);	
				}else{
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date varDate=dateFormat.parse(searchdata.getFiledate().substring(0,10).toString());
				    dateFormat=new SimpleDateFormat("dd/MM/YY");
				    dt = dateFormat.format(varDate);			   
				}
			}catch (Exception e) {
			    // TODO: handle exception
			    e.printStackTrace();
			}
			if(searchdata.getCategory().equals("CASHNET") || searchdata.getCategory().equals("NFS")){
				tablename = "SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getSubCategory().substring(0,3)+"_"+searchdata.getFilename();			
			}else if(!searchdata.getCategory().equals("MASTERCARD")){
				tablename = "SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getFilename();
			}else{
				tablename = "SETTLEMENT_"+searchdata.getCategory()+"_"+searchdata.getFilename();
			}
			
				String query="Update "+tablename+" set dcrs_remarks = 'FORCE_MATCH' where TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('"+dt+"','DD/MM/YY') "
						+ " and (trim(DCRS_REMARKS) like '%UNRECON%' OR trim(DCRS_REMARKS) LIKE '%UNMATCHED%' OR trim(DCRS_REMARKS) LIKE '%UNMATCH%') ";
				if(!searchdata.getSubCategory().equals("-") && !searchdata.getCategory().equals("CASHNET") && !searchdata.getCategory().equals("NFS")){
					query = query + " AND trim(DCRS_REMARKS) like '%"+searchdata.getSubCategory().substring(0,3)+"%' ";
				}
				if(searchdata.getFilename().equals("SWITCH")){
					if(!searchdata.getCard_No().equals("")){
						query = query + " and pan = '"+searchdata.getCard_No()+"'" ;
					}
					if(!searchdata.getAmount().equals("")){
						query = query + " and amount = "+searchdata.getAmount();
					}
					if(!searchdata.getTran_date().equals("")){
						query = query + " and local_date = '"+searchdata.getTran_date()+"'";
					}
					if(!searchdata.getApprovalCode().equals("")){
						query = query + " and AUTHNUM = '"+searchdata.getApprovalCode()+"'";
					}
					if(!searchdata.getArn().equals("")){
						query = query + " and ISSUER = '"+searchdata.getArn()+"'";
					}
				}else if(!searchdata.getFilename().contains("CBS")){
					if(searchdata.getCategory().equals("RUPAY") ){
						if(!searchdata.getCard_No().equals("")){
							query = query + " and PRIMARY_ACCOUNT_NUMBER = '"+searchdata.getCard_No()+"'";
						}
						if(!searchdata.getAmount().equals("")){
							query = query + " and AMOUNT_SETTLEMENT = "+searchdata.getAmount();
						}
						if(!searchdata.getTran_date().equals("")){
							query = query + " and DATE_SETTLEMENT = '"+searchdata.getTran_date()+"'" ;
						}
						if(!searchdata.getApprovalCode().equals("")){
							query = query + " and APPROVAL_CODE = '"+searchdata.getApprovalCode()+"'";
						}
						if(!searchdata.getArn().equals("")){
							query = query + " and ACQUIRER_REFERENCE_DATA = '"+searchdata.getArn()+"'";
						}
						if(!searchdata.getUniqIdentifier().equals("")){
							query = query + " and DATEANDTIME_LOCAL_TRANSACTION = '"+searchdata.getUniqIdentifier()+"'";
						}
					}else if(searchdata.getCategory().equals("VISA")){
								if(!searchdata.getCard_No().equals("")){
									query = query + " and CARD_NUMBER = '"+searchdata.getCard_No()+"'";
								}						
								if(!searchdata.getAmount().equals("")){
									query = query + " and DESTINATION_AMOUNT = "+searchdata.getAmount();
								}
								if(!searchdata.getApprovalCode().equals("")){
									query = query + " and  AUTHORIZATION_CODE = '"+searchdata.getApprovalCode()+"'";
								}
								if(!searchdata.getArn().equals("")){
									query = query + " and ARN = '"+searchdata.getArn()+"'";
								}
								if(!searchdata.getUniqIdentifier().equals("")){
									query = query + " and DCRS_SEQ_NO = '"+searchdata.getUniqIdentifier()+"'";
								}
					}else if(searchdata.getCategory().equals("CASHNET") || searchdata.getCategory().equals("NFS")){	
						if(!searchdata.getCard_No().equals("")){
							query = query + " and PAN_NUMBER = '"+searchdata.getCard_No()+"'";
						}
						if(!searchdata.getTran_date().equals("")){
							query = query + " and TRANSACTION_DATE = '"+searchdata.getTran_date()+"'";
						}
						if(!searchdata.getApprovalCode().equals("")){
							query = query + " and APPROVAL_NUMBER = '"+searchdata.getApprovalCode()+"'";
						}					
						if(searchdata.getCategory().equals("CASHNET") && searchdata.getSubCategory().equals("ISSUER")){
							if(!searchdata.getAmount().equals("")){
								query = query + " and TRANSACTION_AMOUNT = "+searchdata.getAmount();
							}
							if(!searchdata.getArn().equals("")){
								query = query + " and TRANSACTION_SERIAL = '"+searchdata.getArn()+"'";
							}					
							if(!searchdata.getUniqIdentifier().equals("")){
								query = query + " and SYSTEM_TRACE_AUDIT = '"+searchdata.getUniqIdentifier()+"'";
							}
						}else{
							if(!searchdata.getAmount().equals("")){
								query = query + " and TXN_AMOUNT = "+searchdata.getAmount();
							}
							if(!searchdata.getArn().equals("")){
								query = query + " and TXN_SERIAL_NO = '"+searchdata.getArn()+"'";
							}		
							if(!searchdata.getUniqIdentifier().equals("")){
								query = query + " and SYS_TRACE_AUDIT_NO = '"+searchdata.getUniqIdentifier()+"'";
							}
						}
					}else if(searchdata.getCategory().equals("POS") && searchdata.getFilename().equals("POS")){
						if(!searchdata.getCard_No().equals("")){
							query = query + " and CARD_NUMBER = '"+searchdata.getCard_No()+"'";
						}
						if(!searchdata.getAmount().equals("")){
							query = query + " and SOURCE_AMOUNT = "+searchdata.getAmount();
						}
						if(!searchdata.getTran_date().equals("")){
							query = query + " and PROCESSING_DATE = '"+searchdata.getTran_date()+"'";
						}
						if(!searchdata.getApprovalCode().equals("")){
							query = query + " and AUTHORIZATION_CODE = '"+searchdata.getApprovalCode()+"'";
						}
						if(!searchdata.getArn().equals("")){
							query = query + " and ARN = '"+searchdata.getArn()+"'";
						}			
						if(!searchdata.getUniqIdentifier().equals("")){
							query = query + " and DCRS_SEQ_NO = '"+searchdata.getUniqIdentifier()+"'";
						}
					}else if(searchdata.getCategory().equals("MASTERCARD")){
					if(searchdata.getFilename().equals("POS") && searchdata.getSubCategory().equals("ISSUER")){
						if(!searchdata.getCard_No().equals("")){
							query = query + " and pan = '"+searchdata.getCard_No()+"'";
						}
						if(!searchdata.getAmount().equals("")){
							query = query + " and amount = "+searchdata.getAmount();
						}
						if(!searchdata.getTran_date().equals("")){
							query = query + " and DATE_VAL = '"+searchdata.getTran_date()+"'";
						}
						if(!searchdata.getApprovalCode().equals("")){
							query = query + " and APPROVAL_CODE = '"+searchdata.getApprovalCode()+"'";
						}
						if(!searchdata.getArn().equals("")){
							query = query + " and AQUIERER_REF_NO = '"+searchdata.getArn()+"'";
						}	
						if(!searchdata.getUniqIdentifier().equals("")){
							query = query + " and UNQ_IDENTIFIER = '"+searchdata.getUniqIdentifier()+"'";
						}
					}else{
						if(!searchdata.getCard_No().equals("")){
							query = query + " and PAN_NUM = '"+searchdata.getCard_No()+"'";
						}
						if(!searchdata.getAmount().equals("")){
							query = query + " and COMPLTD_AMT_SETTMNT = "+searchdata.getAmount();
						}
						if(!searchdata.getTran_date().equals("")){
							query = query + " and TRAN_DATE = '"+searchdata.getTran_date()+"'";
						}
						if(!searchdata.getApprovalCode().equals("")){
							query = query + " and AUTH_ID = '"+searchdata.getApprovalCode()+"'";
						}
						if(!searchdata.getArn().equals("")){
							query = query + " and TRACE_NUM = '"+searchdata.getArn()+"'";
						}	
						if(!searchdata.getUniqIdentifier().equals("")){
							query = query + " and REF_NO = '"+searchdata.getUniqIdentifier()+"'";
						}
					}
				}else if(searchdata.getCategory().equals("WCC")){
					if(!searchdata.getCard_No().equals("")){
						query = query + " and CARD_NUMBER = '"+searchdata.getCard_No()+"'";
					}
					if(!searchdata.getAmount().equals("")){
						query = query + " and DESTINATION_AMOUNT = "+searchdata.getAmount();
					}
					if(!searchdata.getApprovalCode().equals("")){
						query = query + " and AUTHORIZATION_CODE = '"+searchdata.getApprovalCode()+"'";
					}
					if(!searchdata.getArn().equals("")){
						query = query + " and ARN = '"+searchdata.getArn()+"'";
					}
					if(!searchdata.getUniqIdentifier().equals("")){
						query = query + " and DCRS_SEQ_NO = '"+searchdata.getUniqIdentifier()+"'";
					}
				}
		}else{
			query = "insert into  glbl_cbs_forcematch select * from glbl_cbs_unmatch where REMARKS = '"+searchdata.getCard_No()+"' and TRAN_DATE = '"+searchdata.getTran_date()+"' and "
					+ "CBSAMOUNT = "+searchdata.getAmount()+" and TRANID = '"+searchdata.getTermid()+"' and PARTICULARALS = '"+searchdata.getLocal_time()+"' and "
					+ "REFNO = '"+searchdata.getTrace()+"' and TO_DATE(CBSFILEDATE,'DD/MM/YY') = TO_DATE('"+dt+"','DD/MM/YY') and FORACID = '"+searchdata.getForacid()+"'";
		}
						
				logger.info("query=="+query);
				int rowupdate =  getJdbcTemplate().update(query);
				logger.info("rowupdate=="+rowupdate);
				logger.info("***** DisputeDaoImpl.updateDispute End ****");
				if(rowupdate==1){
					return true;
				}else{
					return false;
				}
				
			
			
			

		}catch(Exception e) {
			
			  logger.error(" error in DisputeDaoImpl.updateDispute", new Exception("DisputeDaoImpl.updateDispute",e));
			//  throw e;
			return false;
				
		}
		//return true;
		
		
	}

	

}
