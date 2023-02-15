package com.recon.model;

public class VisaUploadBean {

	private String createdBy;
	private String fileDate;
	private String fileName;
	private String fileType;
	//private String subCategory;
	
	
	
	public String getCreatedBy() {
		return createdBy;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	
}
