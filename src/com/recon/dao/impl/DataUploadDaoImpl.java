package com.recon.dao.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.informix.util.dateUtil;
import com.recon.dao.IDataUploadDao;
import com.recon.model.FileSourceBean;
import com.recon.util.OracleConn;
import com.recon.util.ReadSwitchFile;


public class DataUploadDaoImpl extends JdbcDaoSupport implements IDataUploadDao{

	
	public FileSourceBean getFileDetails(FileSourceBean sourceBean) {
		
		FileSourceBean fileSourceBean = null;
		
		ResultSet rs;
		OracleConn oracleConn = null;
		
		try {
			
			oracleConn = new OracleConn();
			String query = "SELECT fs.Filename,fs.tablename,fs.dataseparator ,ftp.filelocation, ftp.filepath, ftp.ftpusername as FtpUser, ftp.ftppassword as FtpPwd , ftp.ftpport,hd.columnheader as TblHeader "
					+ " FROM Main_filesource fs INNER JOIN Main_ftpDetails ftp "
					+ " ON fs.FILEID = ftp.FILEID "
					+ " INNER JOIN main_fileheaders hd "
					+ " ON fs.FILEID = hd.FILEID "
					+ " WHERE fs.FILEID="+sourceBean.getFileId()+" AND fs.ActiveFlag='A'";
			
			
			
			//fileSourceBean = (FileSourceBean) getJdbcTemplate().query(query,new BeanPropertyRowMapper(FileSourceBean.class) );
			
			rs = oracleConn.executeQuery(query);
			
			
			while(rs.next()){
				
				fileSourceBean= new FileSourceBean();
				fileSourceBean.setFileName(rs.getString("Filename"));
				fileSourceBean.setFileLocation(rs.getString("Filelocation"));
				fileSourceBean.setFilePath(rs.getString("Filepath"));
				fileSourceBean.setFtpUser(rs.getString("Ftpuser"));
				fileSourceBean.setFtpPwd(rs.getString("Ftppwd"));
				fileSourceBean.setTableName(rs.getString("Tablename"));
				fileSourceBean.setFtpPort(rs.getInt("ftpPort"));
				fileSourceBean.setTblHeader(rs.getString("tblHeader"));
				fileSourceBean.setDataSeparator(rs.getString("dataseparator"));
				
				
				
				
				
				
				
				
			}
			
			 return fileSourceBean;
			
		
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			
			 return fileSourceBean;
		}
		
		
		
		
		
		
	}

	@Override
	public boolean uploadData(FileSourceBean sourceBean) {
		
			FileSourceBean bean = null;
		
			bean =	getFileDetails(sourceBean);
			
			
			
			
			if(bean != null){
				
				
				System.out.println(bean.getFileName());
				System.out.println(bean.getTableName());
				System.out.println(bean.getDataSeparator());
				System.out.println(bean.getFileLocation());
				System.out.println(bean.getFilePath());
				System.out.println(bean.getFtpUser());
				System.out.println(bean.getFtpPwd());
				System.out.println(bean.getFtpPort());
				System.out.println(bean.getTblHeader());
				
				readAnduploadData(bean,null);
				
				
				
			}
		return true;
	}
	


	public boolean readAnduploadData(FileSourceBean fileSourceBean,Connection con) {
		 
		try {
			
			 boolean result = false;
		  
	        System.out.println("Database connection established");
	        String newTargetFile =  fileSourceBean.getFileName();
	        String bnaId = "";
	        //String seefilename = "E:\\LED\\DCRS\\IDBI_SWITCH_05012017\\" + newTargetFile;
	       // System.out.println("PROCESSING FILE " + seefilename);
	        InputStream fis = null;
	        
	        String [] tblhdr = fileSourceBean.getTblHeader().split(",");
	        String strhdr="(";
	        System.out.println("tblhdr.length"+tblhdr.length);
	        
	        for(int hdr= 1;hdr<=tblhdr.length;hdr++) {
	        	
	        	if(hdr==tblhdr.length){
	        		
	        		strhdr = strhdr+"?)";
	        	}else {
	        		
	        		strhdr = strhdr+"?,";
	        	}
	        }
	        
	        
	        
	        
	       // try {
	       //     fis = new FileInputStream("\\\\10.144.133.245\\led\\working\\" + newTargetFile);
	      //  } catch (FileNotFoundException ex) {
	      //      Logger.getLogger(ReadSwitchFile.class.getName()).log(Level.SEVERE, null, ex);
	       // } 4/05/17 
	        BufferedReader br = new BufferedReader(new InputStreamReader(fis)); 
	        String thisLine = null;
	        

	        String insert = "INSERT INTO "+fileSourceBean.getTableName() +" values "+strhdr ;
	        
	        PreparedStatement ps = con.prepareStatement(insert);
	        int flag=0,batch=0;
	        
	       while ((thisLine = br.readLine()) != null) {
	            int srl = 1;

	            String []splitarray= null;

	            splitarray = thisLine.split(Pattern.quote(fileSourceBean.getDataSeparator()));

	            for(int i=0;i<splitarray.length;i++){

	                String value = splitarray[i];
	               
	               ps.setString(srl,value.trim());
	             

	                srl = srl+1;
	                
	                
	                
	                 ps.addBatch();
	                 flag++;
						
						if(flag == 200000)
						{
							flag = 1;
						
							//ps.executeBatch();
							System.out.println("Executed batch is "+batch);
							batch++;
						}
	                
	                


	            }
	            
	            System.out.println(insert +"-"+batch);
	           // ps.setString(28, "NA");
	            
	           // ps.execute();
	           
	          //  insrt = ps.executeUpdate(); 4/05/17
	        }

	        br.close();
	        ps.close();
	        
	        return true;
	     
	        } catch(Exception ex) {

	                System.out.println("Exception "+ ex);
	                return false;
	            }
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		FileSourceBean bean = new FileSourceBean();
		bean.setFileId(1);
		
		DataUploadDaoImpl daoImpl = new DataUploadDaoImpl();
		daoImpl.uploadData(bean);
		
		
		
	}

}
