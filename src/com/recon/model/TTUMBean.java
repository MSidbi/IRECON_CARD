package com.recon.model;

public class TTUMBean {

	private String Acc_number;
	private String currency_Code;
	private String part_tran_type;
	private String trans_amount;
	private String trans_particular;
	private String remarks;
	private String fileDate;
	
	
	
	
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getAcc_number() {
		return Acc_number;
	}
	public void setAcc_number(String acc_number) {
		Acc_number = acc_number;
	}
	public String getCurrency_Code() {
		return currency_Code;
	}
	public void setCurrency_Code(String currency_Code) {
		this.currency_Code = currency_Code;
	}
	public String getPart_tran_type() {
		return part_tran_type;
	}
	public void setPart_tran_type(String part_tran_type) {
		this.part_tran_type = part_tran_type;
	}
	public String getTrans_amount() {
		return trans_amount;
	}
	public void setTrans_amount(String trans_amount) {
		this.trans_amount = trans_amount;
	}
	public String getTrans_particular() {
		return trans_particular;
	}
	public void setTrans_particular(String trans_particular) {
		this.trans_particular = trans_particular;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	
}
