package com.recon.util;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class SearchData {

	
	List<String> ExcelHeaders;
	List<String> data;
	List<String> matchHeadList;
	String category;
	String subCategory;
	String filedate;
	String tran_date;
	String amount;
	String card_No;
	String filename;
	String  dcrs_remarks;
	
	 private int TotalRecordCount;
	 private String Result;
	    private List<SearchData> Records;
	    
	    
	    public void setParams(String Result,List<SearchData> Records, int TotalRecordCount)
	    {
	    	this.Result = Result;
	    	this.Records = Records;
	    	this.TotalRecordCount = TotalRecordCount;
	    }
	
	
	public int getTotalRecordCount() {
			return TotalRecordCount;
		}
		public void setTotalRecordCount(int totalRecordCount) {
			TotalRecordCount = totalRecordCount;
		}
		public String getResult() {
			return Result;
		}
		public void setResult(String result) {
			Result = result;
		}
		public List<SearchData> getRecords() {
			return Records;
		}
		public void setRecords(List<SearchData> records) {
			Records = records;
		}
	public List<String> getMatchHeadList() {
		return matchHeadList;
	}
	public void setMatchHeadList(List<String> matchHeadList) {
		this.matchHeadList = matchHeadList;
	}
	
	@JsonProperty("card_No")
	public String getCard_No() {
		return card_No;
	}
	public void setCard_No(String card_No) {
		this.card_No = card_No;
	}
	public List<String> getExcelHeaders() {
		return ExcelHeaders;
	}
	public void setExcelHeaders(List<String> excelHeaders) {
		ExcelHeaders = excelHeaders;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	
	@JsonProperty("category")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	
	@JsonProperty("filedate")
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	
	@JsonProperty("tran_date")
	public String getTran_date() {
		return tran_date;
	}
	public void setTran_date(String tran_date) {
		this.tran_date = tran_date;
	}
	
	@JsonProperty("amount")
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@JsonProperty("filename")
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@JsonProperty("dcrs_remarks")
	public String getDcrs_remarks() {
		return dcrs_remarks;
	}
	public void setDcrs_remarks(String dcrs_remarks) {
		this.dcrs_remarks = dcrs_remarks;
	}
}
