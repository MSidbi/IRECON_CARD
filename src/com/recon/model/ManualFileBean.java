package com.recon.model;

public class ManualFileBean implements Cloneable {

	private String stFileSelected;
	private String stCategory;
	private String stSubCategory;
	private String stManualFile;
	private String manFile;
	private String stEntryBy;
	
	private String stTable1;
	private String stTable2;
	private String stFile_header;
	private String stSearch_Pattern;
	private String stCondition;
	private String stStart_charpos;
	private String stChar_size;
	private String stPadding;
	private String stFileName;
	
	private String stUpdate_Column;
	private String stUpdateCol_Value;
	
	private String stFile_date;
	private String stMergerCategory;
	
	public String getStTable1() {
		return stTable1;
	}
	public void setStTable1(String stTable1) {
		this.stTable1 = stTable1;
	}
	public String getStTable2() {
		return stTable2;
	}
	public void setStTable2(String stTable2) {
		this.stTable2 = stTable2;
	}
	public String getStFile_header() {
		return stFile_header;
	}
	public void setStFile_header(String stFile_header) {
		this.stFile_header = stFile_header;
	}
	public String getStSearch_Pattern() {
		return stSearch_Pattern;
	}
	public void setStSearch_Pattern(String stSearch_Pattern) {
		this.stSearch_Pattern = stSearch_Pattern;
	}
	public String getStCondition() {
		return stCondition;
	}
	public void setStCondition(String stCondition) {
		this.stCondition = stCondition;
	}
	public String getStStart_charpos() {
		return stStart_charpos;
	}
	public void setStStart_charpos(String stStart_charpos) {
		this.stStart_charpos = stStart_charpos;
	}
	public String getStChar_size() {
		return stChar_size;
	}
	public void setStChar_size(String stChar_size) {
		this.stChar_size = stChar_size;
	}
	public String getStPadding() {
		return stPadding;
	}
	public void setStPadding(String stPadding) {
		this.stPadding = stPadding;
	}
	public String getStUpdate_Column() {
		return stUpdate_Column;
	}
	public void setStUpdate_Column(String stUpdate_Column) {
		this.stUpdate_Column = stUpdate_Column;
	}
	public String getStUpdateCol_Value() {
		return stUpdateCol_Value;
	}
	public void setStUpdateCol_Value(String stUpdateCol_Value) {
		this.stUpdateCol_Value = stUpdateCol_Value;
	}
	public String getStFile_date() {
		return stFile_date;
	}
	public void setStFile_date(String stFile_date) {
		this.stFile_date = stFile_date;
	}
	public String getStFileSelected() {
		return stFileSelected;
	}
	public void setStFileSelected(String stFileSelected) {
		this.stFileSelected = stFileSelected;
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
		this.stSubCategory = stSubCategory;
	}
	public String getStManualFile() {
		return stManualFile;
	}
	public void setStManualFile(String stManualFile) {
		this.stManualFile = stManualFile;
	}
	public String getStMergerCategory() {
		return stMergerCategory;
	}
	public void setStMergerCategory(String stMergerCategory) {
		this.stMergerCategory = stMergerCategory;
	}
	public String getStFileName() {
		return stFileName;
	}
	public void setStFileName(String stFileName) {
		this.stFileName = stFileName;
	}
	public String getManFile() {
		return manFile;
	}
	public void setManFile(String manFile) {
		this.manFile = manFile;
	}
	public String getStEntryBy() {
		return stEntryBy;
	}
	public void setStEntryBy(String stEntryBy) {
		this.stEntryBy = stEntryBy;
	}

	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
	}  
	

}
