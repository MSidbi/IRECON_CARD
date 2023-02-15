package com.recon.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileSourceBean implements Serializable {
	
	int fileId;
	String fileName;
	String fileLocation;
	String filePath;
	String ftpUser;
	String ftpPwd;
	String tableName,activeFlag;
	String dataSeparator;
	int ftpPort;
	String tblHeader;
	
	
	public String getTblHeader() {
		return tblHeader;
	}
	public void setTblHeader(String tblHeader) {
		this.tblHeader = tblHeader;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFtpUser() {
		return ftpUser;
	}
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	public String getFtpPwd() {
		return ftpPwd;
	}
	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public int getFtpPort() {
		return ftpPort;
	}
	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDataSeparator() {
		return dataSeparator;
	}
	public void setDataSeparator(String dataSeparator) {
		this.dataSeparator = dataSeparator;
	}
	
	
	

}
