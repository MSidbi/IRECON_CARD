package com.recon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LotusConn {

	 private Connection Connection;
	 private String server;
	    private String port;
	    private String login;
	    private String password;
	    private String database;
	
	public Connection getConn() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");

        Connection = DriverManager.getConnection("jdbc:oracle:thin:@" + server + ":" + port + ":" + database + "", login, password);

        Connection.setAutoCommit(true);
        Connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return Connection;
    }
	
	public LotusConn()
	{
		 database = "lotus";
         login = "pf_new";
        password = "pfnew";
        server = "10.144.18.248";
        port = "1521";
	}
}
