package com.recon.dao.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.recon.dao.IFileSourceDao;
import com.recon.util.OracleConn;
import com.recon.util.ReadCBSFile;
import com.recon.util.ReadCashNetFile;
import com.recon.util.ReadSwitchFile;
import com.recon.model.FileSourceBean;

@Component
public class FileSourceDaoImpl extends JdbcDaoSupport implements IFileSourceDao {

	@Override
	public boolean uplodFTPFile(FileSourceBean ftpBean) {
		Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        System.out.println("preparing the host information for sftp.");
        FileSourceBean daobean = null;
		
		boolean result= false;	
		try{
			 
					String path= "/user1/admin/testwork";
					FileOutputStream out= null;
			        JSch jsch = new JSch();
			        System.out.println(ftpBean.getFtpUser()+ftpBean.getFileLocation()+ftpBean.getFtpPort());
		            session = jsch.getSession(ftpBean.getFtpUser(), ftpBean.getFileLocation(), ftpBean.getFtpPort());
			        //session = jsch.getSession("admn2","10.143.136.142", 22);
		            
			        session.setPassword(ftpBean.getFtpPwd());
		            java.util.Properties config = new java.util.Properties();
		            config.put("StrictHostKeyChecking", "no");
	
		            session.setConfig(config);
		            session.connect();
		            System.out.println("Host connected.");
		            channel = session.openChannel("sftp");
		            channel.connect();
		            System.out.println("sftp channel opened and connected.");
		            channelSftp = (ChannelSftp) channel;
		            channelSftp.cd(ftpBean.getFilePath());
		            //File f = new File(fileName);
		            //File f = new File("E:\\WEB DEV\\FinacleUpload\\build\\web\\upload\\OP FLG.xls");
		           
	
		            //InputStream is= channelSftp.get("/user1/admin/testwork"+"/sushant.txt");
		           
		            InputStream is= channelSftp.get(ftpBean.getFilePath()+"/sushant.txt");
		            
		            BufferedReader br = new BufferedReader(new InputStreamReader(is));
		            out = new FileOutputStream("\\\\10.144.133.245\\led\\working\\SWT_31032017.txt");
		            String nxtline= null;
		            while((nxtline=br.readLine())!=null) {
	
		            	out.write(nxtline.getBytes());
		                System.out.println(nxtline);
	
		            }
		            result= true;
		            br.close();
		            is.close();
		            
				

	            return result;
		
		
		}catch(Exception ex){
			
			ex.printStackTrace();
			
			return result;
		}
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public FileSourceBean getFTPDetails(int fileId) {
	
		
		FileSourceBean ftpBean = null;
		
		try {
		
			
			String query="SELECT (ftpdtls.Fileid) as fileId ,Filelocation,Filepath,Ftpusername as ftpUser ,Ftppassword as ftpPwd ,Tablename as tableName,"
					+ " ActiveFlag as activeFlag ,FtpPort as ftpPort "
					+ " FROM Main_FTPDETAILS ftpdtls INNER JOIN Main_FILESOURCE filesrc "
					+ " ON   ftpdtls.Fileid = filesrc.FILEID"
					+ " WHERE ftpdtls.FILEID = "+fileId+" AND filesrc.ActiveFlag='A' ";
			
			System.out.println(query);
			
			
			ftpBean = (FileSourceBean) getJdbcTemplate().queryForObject(query,new BeanPropertyRowMapper(FileSourceBean.class));
			//rs = oracleConn.executeQuery(query);
			
			/*
			while(rs.next()){
				
				ftpBean= new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileLocation(rs.getString("Filelocation"));
				ftpBean.setFilePath(rs.getString("Filepath"));
				ftpBean.setFtpUser(rs.getString("Ftpusername"));
				ftpBean.setFtpPwd(rs.getString("Ftppassword"));
				ftpBean.setTableName(rs.getString("Tablename"));
				ftpBean.setActiveFlag(rs.getString("ActiveFlag"));
				ftpBean.setFtpPort(rs.getInt("ftpPort"));
				
			}*/
		
			return ftpBean;
		
		}catch(Exception ex) {
			
			ex.printStackTrace();
			return null;
			
		}
		
	}

	
	@Override
	public boolean uplodData(FileSourceBean ftpBean) {
		
		
		Connection con= getConnection(); 
		
		String separator = getDataSeparator(ftpBean.getFileId());
		
		if(separator==null) {
			
			separator="|";
			
		}
		
		ftpBean.setDataSeparator(separator);
		
		if(ftpBean.getFileId()== 01) {
			
		
			
			ReadSwitchFile readSwitchFile = new ReadSwitchFile();
			
			//return readSwitchFile.uploadSwitchData(null,null,null,null);
			//Added byt int8624
			HashMap<String,Object> output = readSwitchFile.uploadSwitchData(null,null,null,null);
			boolean flag = (boolean) output.get("result");
			return flag;
		} else if(ftpBean.getFileId()== 02) {
			
			ReadCBSFile cbsFile = new ReadCBSFile();
			//return cbsFile.uploadCBSData(ftpBean,con,null,null);
			return false;
			
		} else if(ftpBean.getFileId()== 03){
			
			ReadCashNetFile cashNetFile= new ReadCashNetFile();
			//cashNetFile.uplaodCashnetData(ftpBean);
			
		}
		
		return false;
		
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<FileSourceBean> getFileList() {
		
		List<FileSourceBean> ftpFileList = new ArrayList<FileSourceBean>();
		OracleConn oracleConn = null;
		ResultSet rs=null;
		try{
			
		/*	oracleConn= new OracleConn();*/
			
			String query="SELECT Fileid as fileId , FileName as fileName "
					+ " FROM Main_FILESOURCE filesrc "
					+ " WHERE ActiveFlag='A' ";
				
			
		/*	rs = oracleConn.executeQuery(query);*/
			
			/*while(rs.next()){
				
				FTPBean ftpBean = new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileName(rs.getString("FileName"));
				
				ftpFileList.add(ftpBean);
				
				
			}*/
			
			ftpFileList = getJdbcTemplate().query(query,new BeanPropertyRowMapper(FileSourceBean.class));
			
		}catch(Exception ex) {
			
			ex.printStackTrace();
			System.out.println(ex.toString());
			
		}finally{
			
			if(oracleConn!=null){
				
				try {
					oracleConn.CloseConnection();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
			
		}
		
		return ftpFileList;
	}
	

	public static void main(String[] args) {
		
		FileSourceDaoImpl daoImpl = new FileSourceDaoImpl();
		FileSourceBean ftpBean = new FileSourceBean();
		//daoImpl.getDataSeparator(01);
		daoImpl.getFileList();
	}

	@Override
	public boolean uploadFile(byte[] file,int fileId) throws Exception {
		
		try {
		byte[] bytes = file;
		String rootPath = "E:\\app";
		
		if(fileId == 01) {
			
			rootPath = rootPath+"\\Switch";
		}if(fileId == 02) {
			
			rootPath = rootPath+"\\CBS";
		}if(fileId == 03) {
			
			rootPath = rootPath+"\\CahsNet";
		}
		File dir = new File(rootPath + File.separator + "tmpFiles.txt");
		System.out.println("dir"+dir);
		if (!dir.exists())
			dir.mkdirs();

		// Create the file on server
		File serverFile = new File(dir.getAbsolutePath()
				+ File.separator + fileId);
		BufferedOutputStream stream = new BufferedOutputStream(
				new FileOutputStream(serverFile));
		stream.write(bytes);
		stream.close();

		System.out.println("serverFile.getAbsolutePath()"+serverFile.getAbsolutePath());
		logger.info("Server File Location="
				+ serverFile.getAbsolutePath());
		return true;
		
		}catch(Exception ex) {
			
			
			throw  ex;
			
			
		}
	}
	
	public String getDataSeparator(int fileId) {
		
	
		
		String query="SELECT dataseparator "
				+ " FROM Main_FILESOURCE filesrc "
				+ " WHERE FILEID = "+fileId+" AND ActiveFlag='A' ";
		
		System.out.println(query);
		String seprator =(String) getJdbcTemplate().queryForObject(query, String.class);
		
		System.out.println(seprator);
		
		
		
		return seprator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FileSourceBean> getFileDetails() {
		
		List<FileSourceBean> filelist = null;
		
		try {
			
			String query="SELECT filesrc.Fileid as fileId , filesrc.FileName as fileName ,filesrc.dataseparator ,filesrc.tablename,"
					+ " 	ftp.filelocation,ftp.filepath,ftp.ftpusername as ftpUser ,ftp.ftppassword as ftpPwd, filehd.columnheader as tblHeader,ftp.FtpPort as ftpPort , "
					+ " filesrc.Activeflag as activeFlag  "
					+ " FROM Main_FILESOURCE filesrc "
					+ "	INNER JOIN main_FtpDetails ftp"
					+ " ON filesrc.FILEID = ftp.FILEID"
					+ " LEFT JOIN main_fileheaders filehd"
					+ " ON filesrc.FILEID = filehd.FILEID";
					/*+ " WHERE filesrc.ActiveFlag='A' ";*/
				
			System.out.println("query" +query);
			
		/*	rs = oracleConn.executeQuery(query);*/
			
			/*while(rs.next()){
				
				FTPBean ftpBean = new FTPBean();
				ftpBean.setFileId(rs.getInt("Fileid"));
				ftpBean.setFileName(rs.getString("FileName"));
				
				ftpFileList.add(ftpBean);
				
				
			}*/
			
			filelist = getJdbcTemplate().query(query,new BeanPropertyRowMapper(FileSourceBean.class));
			
		}catch (Exception ex) {
			
			ex.printStackTrace();
			logger.error(ex.toString());
		}
		
		
		
		return filelist;
	}
	@Override
	public boolean updateFileDetails(FileSourceBean ftpBean) {
	try {
		boolean result = false;
		int count =0;
		String query="";
		
		System.out.println("System.out.println(ftpBean)"+ftpBean);
		
		if(ftpBean.getFileId()!=0) {
			
			System.out.println("into the if condition");
			
			System.out.println("flag"+ftpBean.getActiveFlag());
			count = getJdbcTemplate().update(
		                "UPDATE Main_fileSource "
		                + "	set FileName = ? ,dataseparator =? , ActiveFlag=?  "
		                + " WHERE fileid = ?", 
		                ftpBean.getFileName(),ftpBean.getDataSeparator(), ftpBean.getActiveFlag() ,ftpBean.getFileId());
			System.out.println("count"+count);
			
			count =getJdbcTemplate().update("UPDATE MAIN_FTPDETAILS "
					+ "set Filelocation=? ,filepath = ? , Ftpusername= ? ,ftppassword= ? ,ftpport= ? "
					   + " WHERE fileid = ?",
					   ftpBean.getFileLocation(),ftpBean.getFilePath(),ftpBean.getFtpUser(),ftpBean.getFtpPwd(),ftpBean.getFtpPort(),ftpBean.getFileId());
			System.out.println("count"+count);
			
			
			
			
			
			if(count>0) {
				
				System.out.println("Data updated successfully");
				result=true;
			}else{
				
				System.out.println("Data not updated.");
				result = false;
			}
			
		}
		
		
		return result;
	}catch(Exception ex) {
		
		ex.printStackTrace();
		System.out.println(ex);
		return false;
	}
	
	}
}
