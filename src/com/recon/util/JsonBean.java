package com.recon.util;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class JsonBean {
	private String Result;
	private List<JsonBean> Records;
	private int TotalRecordCount;
	private String Message;
	private String category;
	private String subCategory;
	private String filedate;
	private String tran_date;
	private String amount;
	private String card_No;
	private String filename;
	private String  dcrs_remarks;
	private String approvalCode;
	private String arn;
	private String termid;
	private String trace;
	private String local_time;
	private String foracid;
	private String uniqIdentifier;
	
	

	public String getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public String getTermid() {
		return termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public String getLocal_time() {
		return local_time;
	}

	public void setLocal_time(String local_time) {
		this.local_time = local_time;
	}

	public void setParams(String Result, List<JsonBean> list, int TotalRecordCount) {
		this.Result = Result;
		this.Records = list;
		this.TotalRecordCount = TotalRecordCount;
	}

	public void setParams(String Result, String Message) {
		this.Result = Result;
		this.Message = Message;
	}

	@JsonProperty("Result")
	public String getResult() {
		return Result;
	}

	public void setResult(String Result) {
		this.Result = Result;
	}

	@JsonProperty("Records")
	public List<JsonBean> getRecords() {
		return Records;
	}

	public void setRecords(List<JsonBean> Records) {
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

	public String getFiledate() {
		return filedate;
	}

	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}

	public String getTran_date() {
		return tran_date;
	}

	public void setTran_date(String tran_date) {
		this.tran_date = tran_date;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCard_No() {
		return card_No;
	}

	public void setCard_No(String card_No) {
		this.card_No = card_No;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDcrs_remarks() {
		return dcrs_remarks;
	}

	public void setDcrs_remarks(String dcrs_remarks) {
		this.dcrs_remarks = dcrs_remarks;
	}

	public String getForacid() {
		return foracid;
	}

	public void setForacid(String foracid) {
		this.foracid = foracid;
	}

	public String getUniqIdentifier() {
		return uniqIdentifier;
	}

	public void setUniqIdentifier(String uniqIdentifier) {
		this.uniqIdentifier = uniqIdentifier;
	}
	
}
