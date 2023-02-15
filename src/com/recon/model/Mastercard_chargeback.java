package com.recon.model;

public class Mastercard_chargeback {

	private String microfilm          ;
	private String ref_id             ;
	private String settlement_amount  ;
	private String settlement_currency;
	private String txn_amount         ;
	private String txn_currency   ;
	private String reason             ;
	private String documentation      ;
	private String remarks     ;
	public String getMicrofilm() {
		return microfilm;
	}
	public void setMicrofilm(String microfilm) {
		this.microfilm = microfilm;
	}
	public String getRef_id() {
		return ref_id;
	}
	public void setRef_id(String ref_id) {
		this.ref_id = ref_id;
	}
	public String getSettlement_amount() {
		return settlement_amount;
	}
	public void setSettlement_amount(String settlement_amount) {
		this.settlement_amount = settlement_amount;
	}
	public String getSettlement_currency() {
		return settlement_currency;
	}
	public void setSettlement_currency(String settlement_currency) {
		this.settlement_currency = settlement_currency;
	}
	public String getTxn_amount() {
		return txn_amount;
	}
	public void setTxn_amount(String txn_amount) {
		this.txn_amount = txn_amount;
	}
	public String getTxn_currency() {
		return txn_currency;
	}
	public void setTxn_currency(String txn_currency) {
		this.txn_currency = txn_currency;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getDocumentation() {
		return documentation;
	}
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
}
