package com.recon.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.validator.util.privilegedactions.GetConstructor;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class demo  {

	/*public static void main(String args[]){
		String abc = "00 0101104000250467";
		System.out.println(abc.replaceAll(" ", " ").replaceAll("^0*",""));
	}*/
	//Connection con = getConnection();
	
	private static final Logger logger = Logger.getLogger(demo.class);
	
	 public static void logSQLException(Exception e, String methodName) throws SQLException {
		// demo obj = new demo();
		 Connection con = null;
		 PreparedStatement ps=null;
		 try {
	            String query = "insert into dcrs_error_log values (?,?,sysdate())";
	            //System.out.println("logSQLException query==" + query);
	            OracleConn conn = new OracleConn();
				con = conn.getconn();
	            
	            ps = con.prepareStatement(query);
	            String errorCode = "";
	            if (e.getClass().getSimpleName().equalsIgnoreCase("SQLException")) {
	                errorCode += ((SQLException) e).getErrorCode();
	            }

	            ps.setString(1, "DCRS " + " --> " + e.getClass().getSimpleName() +  " --> " + e.getMessage());
	            ps.setString(2, methodName);
	            ps.executeUpdate();
	        } catch (Exception ex) {
	        	logger.error(" error in demo.logSQLException", new Exception("demo.logSQLException",ex));
	        } /*finally {
	        	if(ps!=null){
	        		ps.close();
	        	}
	        	if(con!=null){
	        		con.close();
	        	}
	        }*/
		
	}
}
