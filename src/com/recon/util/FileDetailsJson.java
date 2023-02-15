package com.recon.util;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.recon.model.CompareSetupBean;


public class FileDetailsJson {
	


	 private String Result;
	    private List<CompareSetupBean> Records;
	    private List<String> Subcategories;
	    private List<Integer>respCode;
	    
	    private CompareSetupBean beanRecords;
	    private int TotalRecordCount;
	    private String Message;
	    
	    public void setParams(String Result,int TotalRecordCount,List<String> Subcategories)
	    {
	    	this.Result = Result;
	    	this.Subcategories = Subcategories;
	    	this.TotalRecordCount = TotalRecordCount;
	    }
	    public void setParams(String Result,int TotalRecordCount,List<Integer> respCode,List<String> Subcategories)
	    {
	    	this.Result = Result;
	    	this.respCode = respCode;
	    	this.TotalRecordCount = TotalRecordCount;
	    	this.Subcategories = Subcategories;
	    }

	    public void setParams(String Result, List<CompareSetupBean> Records,int TotalRecordCount) {
	        this.Result = Result;
	        this.Records = Records;
	        this.TotalRecordCount = TotalRecordCount;
	    }
	    public void setParams(String Result, CompareSetupBean Records,int TotalRecordCount) {
	        this.Result = Result;
	        this.beanRecords = Records;
	        this.TotalRecordCount = TotalRecordCount;
	    }

	    public void setParams(String Result, String Message) {
	        this.Result = Result;
	        this.Message = Message;
	    }

	    @JsonProperty("result")
	    public String getResult() {
	        return Result;
	    }

	    public void setResult(String Result) {
	        this.Result = Result;
	    }

	    @JsonProperty("records")
	    public List<CompareSetupBean> getRecords() {
	        return Records;
	    }

	    @JsonProperty("subcategories")
	    public List<String> getSubcategories()
	    {
	    	return Subcategories;
	    }
	    public void setRecords(List<CompareSetupBean> Records) {
	        this.Records = Records;
	    }

	    @JsonProperty("TotalRecordCount")
	    public int getTotalRecordCount() {
	        return TotalRecordCount;
	    }

	    public void setTotalRecordCount(int TotalRecordCount) {
	        this.TotalRecordCount = TotalRecordCount;
	    }

	    @JsonProperty("Message")
	    public String getMessage() {
	        return Message;
	    }

	    public void setMessage(String Message) {
	        this.Message = Message;
	    }
		
	    
	    @JsonProperty("beanRecords")
	    public CompareSetupBean getBeanRecords() {
			return beanRecords;
		}
		public void setBeanRecords(CompareSetupBean beanRecords) {
			this.beanRecords = beanRecords;
		}

		 @JsonProperty("respCode")
		public List<Integer> getRespCode() {
			return respCode;
		}

	    
	   





}
