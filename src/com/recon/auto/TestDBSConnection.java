package com.recon.auto;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBSConnection {

	private static Connection myConnection;
	private static String url;
	private static String login;
	private static String password; 
	 
	public static void main(String a[]) throws SQLException,ClassNotFoundException
	{
		Class.forName("oracle.jdbc.driver.OracleDriver");
        
        System.out.println("New Connection");

        login = "debitcard_recon";
        password = "debitcard_recon";
        url = "jdbc:oracle:thin:@203.112.157.164:1521:orcl";
        
        myConnection = DriverManager.getConnection(url, login, password);
        
        System.out.println("Connection is "+myConnection);
	}
	
}
