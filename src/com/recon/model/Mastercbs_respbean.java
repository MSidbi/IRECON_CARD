package com.recon.model;

import java.util.List;

public class Mastercbs_respbean {

	private List<String> stExcelHeader;
	public List<String> getStExcelHeader() {
		return stExcelHeader;
	}
	public void setStExcelHeader(List<String> stExcelHeader) {
		this.stExcelHeader = stExcelHeader;
	}
	private String account_number         ;
	private String currency_code          ;
	private String service_outlet         ;
	private String part_tran_type         ;
	private String transaction_amount     ;
	private String transaction_particulars;
	private String reference_number       ;
	private String ref_curr_code          ;
	private String ref_tran_amount        ;
	private String remarks                ;
	private String report_code            ;
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getCurrency_code() {
		return currency_code;
	}
	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}
	public String getService_outlet() {
		return service_outlet;
	}
	public void setService_outlet(String service_outlet) {
		this.service_outlet = service_outlet;
	}
	public String getPart_tran_type() {
		return part_tran_type;
	}
	public void setPart_tran_type(String part_tran_type) {
		this.part_tran_type = part_tran_type;
	}
	public String getTransaction_amount() {
		return transaction_amount;
	}
	public void setTransaction_amount(String transaction_amount) {
		this.transaction_amount = transaction_amount;
	}
	public String getTransaction_particulars() {
		return transaction_particulars;
	}
	public void setTransaction_particulars(String transaction_particulars) {
		this.transaction_particulars = transaction_particulars;
	}
	public String getReference_number() {
		return reference_number;
	}
	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}
	public String getRef_curr_code() {
		return ref_curr_code;
	}
	public void setRef_curr_code(String ref_curr_code) {
		this.ref_curr_code = ref_curr_code;
	}
	public String getRef_tran_amount() {
		return ref_tran_amount;
	}
	public void setRef_tran_amount(String ref_tran_amount) {
		this.ref_tran_amount = ref_tran_amount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getReport_code() {
		return report_code;
	}
	public void setReport_code(String report_code) {
		this.report_code = report_code;
	}

	
}
