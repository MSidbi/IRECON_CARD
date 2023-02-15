package com.recon.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.Module.SetupContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.ISearchDAO;
import com.recon.model.CompareSetupBean;
import com.recon.util.SearchData;

@Component
public class SearchDaoImpl extends JdbcDaoSupport implements ISearchDAO  {

	@Override
	public SearchData returndata(SearchData searchData) {
		
		List<String> headList = new ArrayList<String>();
		List<String> dataList = new ArrayList<String>();
		try{
		// GET Columns headers
		//String query = "SELECT column_name FROM   all_tab_cols WHERE  table_name = 'SETTLEMENT_CASHNET_ISS_CBS' order by segment_column_id asc";
		//System.out.println(query);
		
		
		/*headList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum) 
                    throws SQLException {
					return rs.getString(1);
					}
					});*/
		
		
		
		String GET_DATA ="";
		
		GET_DATA = getQuery(searchData);
		
		System.out.println(GET_DATA);
		
		//String query = "SELECT * FROM SETTLEMENT_CASHNET_ISS_CBS WHERE trim(DCRS_REMARKS) like 'UNRECON%' AND TO_DATE(FILEDATE,'DD/MM/YY') = TO_DATE('24/05/2018','DD/MM/YY') AND REMARKS='6522622202075971' ";
		
		Connection con = getConnection();
		PreparedStatement ps=  con.prepareStatement(GET_DATA);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		int columncount = metaData.getColumnCount();
		
		try{
		while(rs.next()) {
					
					
					for(int i=1;i<=columncount;i++ ) {
						
						dataList.add(rs.getString(i));
						
					}
					
					
				}
		
		
		
		
		for(int columns=1;columns<columncount+1;columns++) {
			
			headList.add(metaData.getColumnName(columns));
			
		}
		}catch(Exception ex) {
			
			ex.printStackTrace();
		} finally {
			
			rs.close();
			ps.close();
			
		}
		
		
		System.out.println("headerlis:----" +headList);
		
		
	
	/*List<Map<String,Object>> resultset =	getJdbcTemplate().queryForList(GET_DATA, new Object[]{});
	
	
	
	Map<String,Object> header =  resultset.get(0);
		Set headcol = header.keySet();
		Iterator<String > headkey =	headcol.iterator();
		//List<Node> nodes = new ArrayList<Node>(adj.keySet());
		while(headkey.hasNext()){
			String head =headkey.next();
			headList.add(head);
		}
		
		
		Set datacol = header.keySet();
		for(Map<String,Object> d:resultset ){
			
			Iterator<String > key = datacol.iterator();
			
			while(key.hasNext()){
				String col =key.next();
				System.out.println(col);
				dataList.add(d.get(col).toString());
			}
			
		}*/
		
		
		searchData.setExcelHeaders(headList);
		searchData.setData(dataList);
		
		return searchData;
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
				
		}
	}

	
	public String  getQuery(SearchData searchData) {
		
		
		String query="";
		//String amount=searchData.getAmount();
				
		//String card_No=searchData.getCard_No().equals("")?null:searchData.getCard_No();
		
		//String tran_date=searchData.getTran_date().equals("")?null:searchData.getTran_date();
		
		//String filedate=searchData.getFiledate().equals("")?null:searchData.getFiledate();

		
//		GET PARAMETER DETAILS FROM  SEARCH_DATA_PARAM TABLE
		
		String paramquery = "Select   FILENAME filename, FILE_CATEGORY category ,SUBCATEGORY subCategory,CARD_NO card_No  ,TRAN_DATE tran_date ,AMOUNT amount from SEARCH_DATA_PARAM where FILENAME ='"+searchData.getFilename() +"' and ";
				
		if(!searchData.getFilename().equals("SWITCH")) {
			
			if((searchData.getFilename().equals("CBS")|| !(searchData.getCategory().equals("ONUS")) )) {
				
				paramquery = paramquery +" FILE_CATEGORY ='-' and SUBCATEGORY='"+searchData.getSubCategory()+"' "; 
			} else {
				
				paramquery = paramquery +" FILE_CATEGORY ='"+searchData.getCategory()+"' and SUBCATEGORY='"+searchData.getSubCategory()+"' ";
			}
		}else {
			
			paramquery = paramquery +" FILE_CATEGORY ='-' and SUBCATEGORY='-' "; 
			
		}
		System.out.println(paramquery);
	// = getJdbcTemplate().queryForObject(paramquery,new Object[] { }, new SearchData());
		 SearchData parambean = (SearchData) getJdbcTemplate().queryForObject(paramquery, new Object[] {},  new searchdataRowMapper());
		
		
		
//		CREATE DYNAMIC QUERY TO GET DATA
		String dataquery="";
		String table_name= "";
		
		if((searchData.getCategory().equals("NFS"))|| searchData.getCategory().equals("CASHNET") ) {
			
			table_name= " Settlement_"+searchData.getCategory()+"_"+searchData.getSubCategory().substring(0, 3)+"_"+searchData.getFilename();
			
			
		} else if((searchData.getCategory().equals("MASTERCARD"))|| searchData.getFilename().equals("MASTERCARD") ) {
			
			if(searchData.getSubCategory().equals("ISSUER")) {
			
				table_name = " Settlement_"+searchData.getCategory()+"_POS ";
			}else if(searchData.getSubCategory().equals("ACQUIRER")) {
				
				table_name = " Settlement_"+searchData.getCategory()+"_ATM ";
			}
		} 
		
		else {
			
			table_name = " Settlement_"+searchData.getCategory()+"_"+searchData.getFilename();
		
		}
		
		dataquery = "Select * from "+table_name+" where 1=1 ";
		
		if(!(searchData.getCard_No()==null)){
			
			dataquery =dataquery+ " and "+parambean.getCard_No()+"  like '%"+searchData.getCard_No()+"%' ";
			
			//dataquery =dataquery+ " and trim("+parambean.getCard_No()+") = trim('"+searchData.getCard_No()+"') ";
		
		}if(!(searchData.getTran_date()==null) && !(searchData.getTran_date().equals(""))){
			
//			To convert Date 
			String date= convertdate(searchData);
			
			dataquery =dataquery+ " and trim("+parambean.getTran_date()+") = trim('"+date+"') ";
		
		}if(!(searchData.getAmount()==null) && !(searchData.getAmount().equals(""))){
			
			dataquery =dataquery+ " and trim("+parambean.getAmount()+") = trim("+searchData.getAmount()+") ";
		
		} if(!(searchData.getFiledate()==null) && !(searchData.getFiledate().equals("")) ) {
			
			dataquery =dataquery+ " and filedate = to_date('"+searchData.getFiledate()+"','dd/mm/yyyy')";
		}if((searchData.getFiledate()==null) ) {
			
			dataquery =dataquery+ " and filedate = (select max(filedate) from "+table_name+" )";
		}
			dataquery =dataquery+" and ROWNUM <= 50  order by filedate desc";
		
		
		return dataquery;
				
	}


	private String convertdate(SearchData searchData) {
		
		try {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String date = searchData.getTran_date();
		Date trandate = sdf.parse(date);
		
		String query = "SELECT DATE_FORMAT from SEARCH_DATE_FORMAT where filename='"+searchData.getFilename()+"' AND  ";
		
		
		if(!searchData.getFilename().equals("SWITCH")) {
			
			if((searchData.getFilename().equals("CBS")|| !(searchData.getCategory().equals("ONUS")) )) {
				
				query = query +" FILE_CATEGORY ='-' and FILE_SUBCATEGORY='"+searchData.getSubCategory()+"' "; 
			} else {
				
				query = query +" FILE_CATEGORY ='"+searchData.getCategory()+"' and FILE_SUBCATEGORY='"+searchData.getSubCategory()+"' ";
			}
		}else {
			
			query = query +" FILE_CATEGORY ='-' and FILE_SUBCATEGORY='-' "; 
			
		}
		
		String date_format = getJdbcTemplate().queryForObject(query, String.class);
		logger.info("TRAN_DATE FORMAT:"+date_format);
		
		DateFormat targetFormat  = new SimpleDateFormat(date_format);
		String tran_date = targetFormat.format(trandate);
		
		logger.info("TRAN_DATE :"+tran_date);
		
		
		
		
		
		
		
		
		
		return tran_date;
		
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
			
		}
		
	}


	@Override
	public ArrayList<String> getttumdetails(SearchData searchdata) {
		
		try {
			
			String tablename="";
			List<String> ttumid = new ArrayList<String>();
						
			if(searchdata.getCategory().equalsIgnoreCase("CASHNET")) {
			
				if(searchdata.getFilename().equals("SWITCH")) {
					
					tablename="TTUM_CASHNET_ISS_SWITCH";
				} else if(searchdata.getFilename().equals("CBS")) {
						
						tablename="TTUM_CASHNET_ISS_CBS";
				}else {
					
					return null;
				}
				
				
			} else if(searchdata.getCategory().equalsIgnoreCase("VISA")) {
				
				if(searchdata.getFilename().equals("SWITCH")) {
					
					tablename="TTUM_VISA_ISS_SWITCH";
				} else if(searchdata.getFilename().equals("CBS")) {
						
						tablename="TTUM_VISA_ISS_CBS";
				} else if(searchdata.getFilename().equals("VISA")) {
					
					tablename="TTUM_VISA_VISA";
				} 
			
			} else if(searchdata.getCategory().equalsIgnoreCase("RUPAY")) {
				
				if(searchdata.getFilename().equals("SWITCH")) {
					
					tablename="TTUM_RUPAY_DOM_DISPUTE_BKUP";
				} else if(searchdata.getFilename().equals("CBS")) {
						
						tablename="TTUM_RUPAY_DOM_DISPUTE_BKUP";
				} else if(searchdata.getFilename().equals("RUPAY")) {
					
					tablename="TTUM_RUPAY_DOM_RUPAY";
				} 
				
				
				
				
			}
			
			String query ="SELECT distinct REFERENCE_NUMBER from "+tablename+" where remarks LIKE '"+searchdata.getCard_No()+"%' AND RECORDS_DATE = to_date('"+searchdata.getTran_date()+"','dd/mm/yyyy')  "; 
			
			if(searchdata.getAmount()!=null && !(searchdata.getAmount().equals(""))) {
				
				query= query+" TRANSACTION_AMOUNT = "+searchdata.getAmount();
				
			}
			
			logger.info(query);
			// ttumid = getJdbcTemplate().query(query, new RowMapper(String.class))(query, String.class);
			 
			 ttumid = getJdbcTemplate().query(query, new RowMapper<String>(){
                 public String mapRow(ResultSet rs, int rowNum) 
                                              throws SQLException {
                         return rs.getString(1);
                 }
            });
			
			
			
			
			
			
			return (ArrayList<String>) ttumid;
			
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return null;
			
		}
		
		
		
	}
	
	 
	
}	


 class searchdataRowMapper implements RowMapper
{
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		SearchData searchdata = new SearchData();
		searchdata.setCategory(rs.getString("category"));
		searchdata.setSubCategory(rs.getString("subCategory"));
		searchdata.setTran_date(rs.getString("tran_date"));
		searchdata.setAmount(rs.getString("amount"));
		searchdata.setCard_No(rs.getString("card_No"));
		
		
		return searchdata;
	}
	
}
