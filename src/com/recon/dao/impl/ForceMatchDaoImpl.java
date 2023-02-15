package com.recon.dao.impl;

import java.util.ArrayList;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.IForceMatchDao;
import com.recon.model.ForceMatchBean;
import com.recon.model.SettlementTypeBean;

@Component
public class ForceMatchDaoImpl extends JdbcDaoSupport implements IForceMatchDao {

	@Override
	public ArrayList<ForceMatchBean> getReconData(String tableName,String type,String date,String searchValue) {

	
		
		//getReconData1(tableName, type, date, searchValue);
		
		ArrayList<ForceMatchBean> settlementTypeBeans ;
		
		//String character = tableName.next();
		String result=null;
		String splitype[]=null;
		String formTablename=null;
		char c = tableName.charAt(0);
				
				if (Character.isDigit(c)) { 
				  
					result=formTablename;
					String getTable = "select filename from main_filesource where fileid="+tableName+"";

					formTablename = getJdbcTemplate().queryForObject(getTable,
							String.class);
					String stcat = getJdbcTemplate().queryForObject("SELECT FILE_CATEGORY FROM MAIN_FILESOURCE WHERE FILEID = ?", new Object[]{tableName},String.class);
					//splitype=type.split("\\_");
					tableName="SETTLEMENT"+"_"+stcat+"_"+formTablename;
				}

		
		String split_table[]=tableName.split("_");
		 
		 String concat_table=split_table[0]+"_"+split_table[2];
		
		try{
			String query ="";
			
			settlementTypeBeans=null;
			
			if(concat_table.trim().equals("SETTLEMENT_SWITCH")) {
				if(split_table[1].equals("AMEX") || split_table[1].equals("RUPAY"))
				{
					
					
					query ="SELECT PAN,TERMID,TRACE,dcrs_remarks FROM "+ tableName+" WHERE   dcrs_remarks like '%UNRECON%' " //rownum <= 500 AND
							+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";
							
							
					
					
				}else if(split_table[1].equals("ONUS"))
				{
					query ="SELECT PAN,TERMID,TRACE,dcrs_remarks FROM "+ tableName+" WHERE  dcrs_remarks like '%UNRECON%' " //rownum <= 500 AND
							+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";

					
					System.out.println(query);

				}
				}else if(concat_table.trim().equals("SETTLEMENT_CBS")){
				
				if(split_table[1].equals("AMEX") || split_table[1].equals("RUPAY"))
				{
					
					query ="SELECT  foracid,CONTRA_ACCOUNT,Tran_Date,dcrs_remarks ,PARTICULARALS  FROM "+ tableName+" WHERE dcrs_remarks like '%UNRECON%' "//rownum <= 500 AND
							+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";
							
							
					
				}
				else if(split_table[1].equals("ONUS"))
				{
				 query ="SELECT  ACCOUNT_NUMBER,CONTRA_ACCOUNT,TranDate,TRAN_PARTICULAR,dcrs_remarks  FROM "+ tableName+" WHERE  dcrs_remarks like '%UNRECON%' "//rownum <= 500 AND
						+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";
						
						
				
				System.out.println(query);
				
			}}
			
			settlementTypeBeans= (ArrayList<ForceMatchBean>) getJdbcTemplate().query(query, new BeanPropertyRowMapper(ForceMatchBean.class));
			
		}catch(Exception ex) {
			
			ex.printStackTrace();
			settlementTypeBeans=null;
		}
		return settlementTypeBeans;
	
	}

	@Override
	public int getReconDataCount(String table, String date) {
		
		String query ="";
		
	 int count= 0;
		
	 String split_table[]=table.split("_");
	 
	 String concat_table=split_table[0]+"_"+split_table[2];
	 try{
		 if(concat_table.equals("SETTLEMENT_SWITCH")) {
			 query ="SELECT count(*) FROM "+ table+" WHERE  dcrs_remarks LIKE '%UNRECON%' " //rownum <= 500 AND
					+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";
				
			
			System.out.println(query);
			
			}else if(concat_table.trim().equals("SETTLEMENT_CBS")){
				
				 query ="SELECT  count(*)  FROM "+ table+" WHERE dcrs_remarks LIKE '%UNRECON%' "//rownum <= 500 AND
						+ " AND to_date(filedate,'dd/mm/yy')= to_date('"+date+"','dd/mm/yy') ";
						
				
				
				System.out.println(query);
				
			}
		
		count = getJdbcTemplate().queryForObject(query, Integer.class);
	 }catch(Exception ex){
		 
		 ex.printStackTrace();
	 }
		
		return count;
	}

	@Override
	public ArrayList<ForceMatchBean> getChngReconData(String table,
	 String date, int jtStartIndex,
			int jtPageSize) {
		
		ArrayList<ForceMatchBean> settlementTypeBeans ;
		 String split_table[]=table.split("_");
		 
		 String concat_table=split_table[0]+"_"+split_table[2];
		try {
			String query = "";

			settlementTypeBeans = null;

			if (concat_table.trim().equals("SETTLEMENT_SWITCH")) {
				if(split_table[1].equals("AMEX") || split_table[1].equals("RUPAY")){
					
					query = "SELECT * FROM ( SELECT ROWNUM,PAN,TERMID,TRACE,FileDate,dcrs_remarks , '"+table+"' as SETLTBL, ROW_NUMBER() OVER( ORDER BY PAN) AS RN FROM " + table
							+ " WHERE   dcrs_remarks LIKE'%UNRECON%' " // rownum <= 500 AND
							+ " AND to_date(filedate,'dd/mm/yy')= to_date('"
							+ date + "','dd/mm/yy') ";

					
					query=query+") where RN between "+(jtStartIndex+1)+" and "+(jtPageSize+jtStartIndex)+"";
					

					System.out.println(query);
					
				}
				else if(split_table[1].equals("ONUS"))
				{
				query = "SELECT * FROM ( SELECT ROWNUM,PAN,TERMID,TRACE,FileDate,dcrs_remarks , '"+table+"' as SETLTBL, ROW_NUMBER() OVER( ORDER BY PAN) AS RN FROM " + table
						+ " WHERE   dcrs_remarks LIKE '%UNRECON%' " // rownum <= 500 AND
						+ " AND to_date(filedate,'dd/mm/yy')= to_date('"
						+ date + "','dd/mm/yy') ";

				
				query=query+") where RN between "+(jtStartIndex+1)+" and "+(jtPageSize+jtStartIndex)+"";
				

				System.out.println(query);

			} }else if (concat_table.trim().equals("SETTLEMENT_CBS")) {
				if(split_table[1].equals("AMEX") || split_table[1].equals("RUPAY"))
				{
					
					query = "SELECT * FROM ( SELECT  foracid,CONTRA_ACCOUNT,Tran_Date,dcrs_remarks,PARTICULARALS ,'"+table+"' as SETLTBL, ROW_NUMBER() OVER( ORDER BY foracid ) AS RN  FROM "
							+ table
							+ " WHEREdcrs_remarks LIKE '%UNRECON%' "// rownum <= 500 AND
							+ " AND to_date(filedate,'dd/mm/yy')= to_date('"
							+ date + "','dd/mm/yy') ";

					
					query=query+") where RN between "+(jtStartIndex+1)+" and "+(jtPageSize+jtStartIndex)+"";
					

					System.out.println(query);
					
					
				}
				else if(split_table[1].equals("ONUS"))
				{
				query = "SELECT * FROM ( SELECT  ACCOUNT_NUMBER,CONTRA_ACCOUNT,TranDate,FileDate,TRAN_PARTICULAR,remarks ,'"+table+"' as SETLTBL, ROW_NUMBER() OVER( ORDER BY ACCOUNT_NUMBER ) AS RN  FROM "
						+ table
						+ " WHERE remarks  LIKE '%UNRECON%' "// rownum <= 500 AND
						+ " AND to_date(filedate,'dd/mm/yy')= to_date('"
						+ date + "','dd/mm/yy') ";

				
				query=query+") where RN between "+(jtStartIndex+1)+" and "+(jtPageSize+jtStartIndex)+"";
				

				System.out.println(query);

			}}

			settlementTypeBeans = (ArrayList<ForceMatchBean>) getJdbcTemplate().query(query,new BeanPropertyRowMapper(ForceMatchBean.class));
			
		}catch(Exception ex) {
			
			ex.printStackTrace();
			settlementTypeBeans=null;
		}
		
		
		
		return settlementTypeBeans;
		
	}

	/*@Override
	public ArrayList<ForceMatchBean> getChngReconData(String trim,
			String trim2, int jtStartIndex, int jtPageSize) {
		
		return null;
	}*/

}
