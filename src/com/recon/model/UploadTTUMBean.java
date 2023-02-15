package com.recon.model;

import java.util.List;

public class UploadTTUMBean {

	private List<String> stExcelHeader;
	public List<String> getStExcelHeader() {
		return stExcelHeader;
	}
	public void setStExcelHeader(List<String> stExcelHeader) {
		this.stExcelHeader = stExcelHeader;
	}
	private String stCategory;
	private String stSelectedFile1;
	public String getStSelectedFile1() {
		return stSelectedFile1;
	}
	public void setStSelectedFile1(String stSelectedFile1) {
		this.stSelectedFile1 = stSelectedFile1;
	}
	private String stSubCategory;
	private String stSelectedFile;
	private String stcard_number;
	private String stTrace;
	private String stamount;
	private String stMergerCategory;
	private String stDate;
	public String getEntry_by() {
		return entry_by;
	}
	public void setEntry_by(String entry_by) {
		this.entry_by = entry_by;
	}
	private int inRec_Set_Id;
	private String  entry_by;
	
	
	
	public int getInRec_Set_Id() {
		return inRec_Set_Id;
	}
	public void setInRec_Set_Id(int inRec_Set_Id) {
		this.inRec_Set_Id = inRec_Set_Id;
	}
	public String getStCategory() {
		return stCategory;
	}
	public void setStCategory(String stCategory) {
		this.stCategory = stCategory;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		System.out.println("subcat is "+stSubCategory);
		this.stSubCategory = stSubCategory;
	}
	public String getStSelectedFile() {
		return stSelectedFile;
	}
	public void setStSelectedFile(String stSelectedFile) {
		this.stSelectedFile = stSelectedFile;
	}
	public String getStcard_number() {
		return stcard_number;
	}
	public void setStcard_number(String stcard_number) {
		this.stcard_number = stcard_number;
	}
	public String getStTrace() {
		return stTrace;
	}
	public void setStTrace(String stTrace) {
		this.stTrace = stTrace;
	}
	public String getStamount() {
		return stamount;
	}
	public void setStamount(String stamount) {
		this.stamount = stamount;
	}
	public String getStMergerCategory() {
		return stMergerCategory;
	}
	public void setStMergerCategory(String stMergerCategory) {
		this.stMergerCategory = stMergerCategory;
	}
	public String getStDate() {
		return stDate;
	}
	public void setStDate(String stDate) {
		this.stDate = stDate;
	}
	
	
}
