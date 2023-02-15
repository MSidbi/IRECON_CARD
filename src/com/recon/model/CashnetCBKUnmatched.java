package com.recon.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="CBK_CASHNET_UNMATCHED")
public class CashnetCBKUnmatched implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NETWORK")
	public String NETWORK            ;
	
	@Column(name = "RAISED_DATE")
	public String RAISED_DATE        ;
	
	@Column(name = "CARD_NUMBER")
	public String CARD_NUMBER        ;
	
	@Column(name = "RESPONSE_CODE")
	public String RESPONSE_CODE      ;
	
	@Column(name = "RRN")
	public String RRN                ;
	
	@Column(name = "AUTHORIZATION_CODE")
	public String AUTHORIZATION_CODE ;
	
	@Column(name = "SEQ_NUMBER")
	public String SEQ_NUMBER         ;
	
	@Column(name = "ACCOUNT_NUMBER")
	public String ACCOUNT_NUMBER     ;
	
	@Column(name = "TRAN_DATE")
	public String TRAN_DATE          ;
	
	@Column(name = "TRAN_TIME")
	public String TRAN_TIME          ;
	
	@Column(name = "MCC")
	public String MCC                ;
	
	@Column(name = "TERMINAL_ID")
	public String TERMINAL_ID        ;
	
	@Column(name = "TERMINAL_LOCATION")
	public String TERMINAL_LOCATION  ;
	
	@Column(name = "MERCHANT_LOCATION")
	public String MERCHANT_LOCATION  ;
	
	@Column(name = "CLAIM_DATE")
	public String CLAIM_DATE         ;
	
	@Column(name = "ARN")
	public String ARN                ;
	
	@Column(name = "DISPUTED_AMT")
	public String DISPUTED_AMT       ;
	
	@Column(name = "DISPUTE_REASON")
	public String DISPUTE_REASON     ;
	
	@Column(name = "DISPUTE_REMARKS")
	public String DISPUTE_REMARKS    ;
	
	@Column(name = "ACQ_AMT")
	public String ACQ_AMT            ;
	
	@Column(name = "ACQ_CURRENCY")
	public String ACQ_CURRENCY       ;
	
	@Column(name = "RAISED_ID")
	public String RAISED_ID          ;
	
	@Column(name = "STATUS")
	public String STATUS             ;
	
	@Column(name = "DISPUTE_TYPE")
	public String DISPUTE_TYPE       ;
	
	@Column(name = "ACTION_DATE")
	public String ACTION_DATE        ;
	
	@Column(name = "REMARKS")
	public String REMARKS            ;
	
	@Column(name = "DOWNLOAD_DATE")
	public Date DOWNLOAD_DATE      ;
	
	@Column(name = "DATE_CREATE")
	public Date DATE_CREATE        ;
	
	@Column(name = "CREATED_DATE")
	public Date CREATED_DATE       ;
	
	@Column(name = "UPDATED_DATE")
	public Date UPDATED_DATE       ;
	
	@Column(name = "DCRS_REMARKS")
	public String DCRS_REMARKS       ;
	
	@Column(name = "FILEDATE")
	public String FILEDATE           ;
	
	@Column(name = "CBK_REMARKS")
	private String CBK_REMARKS;
	
	

	public String getCBK_REMARKS() {
		return CBK_REMARKS;
	}

	public void setCBK_REMARKS(String cBK_REMARKS) {
		CBK_REMARKS = cBK_REMARKS;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNETWORK() {
		return NETWORK;
	}

	public void setNETWORK(String nETWORK) {
		NETWORK = nETWORK;
	}

	public String getRAISED_DATE() {
		return RAISED_DATE;
	}

	public void setRAISED_DATE(String rAISED_DATE) {
		RAISED_DATE = rAISED_DATE;
	}

	public String getCARD_NUMBER() {
		return CARD_NUMBER;
	}

	public void setCARD_NUMBER(String cARD_NUMBER) {
		CARD_NUMBER = cARD_NUMBER;
	}

	public String getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}

	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		RESPONSE_CODE = rESPONSE_CODE;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}

	public String getAUTHORIZATION_CODE() {
		return AUTHORIZATION_CODE;
	}

	public void setAUTHORIZATION_CODE(String aUTHORIZATION_CODE) {
		AUTHORIZATION_CODE = aUTHORIZATION_CODE;
	}

	public String getSEQ_NUMBER() {
		return SEQ_NUMBER;
	}

	public void setSEQ_NUMBER(String sEQ_NUMBER) {
		SEQ_NUMBER = sEQ_NUMBER;
	}

	public String getACCOUNT_NUMBER() {
		return ACCOUNT_NUMBER;
	}

	public void setACCOUNT_NUMBER(String aCCOUNT_NUMBER) {
		ACCOUNT_NUMBER = aCCOUNT_NUMBER;
	}

	public String getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(String tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
	}

	public String getTRAN_TIME() {
		return TRAN_TIME;
	}

	public void setTRAN_TIME(String tRAN_TIME) {
		TRAN_TIME = tRAN_TIME;
	}

	public String getMCC() {
		return MCC;
	}

	public void setMCC(String mCC) {
		MCC = mCC;
	}

	public String getTERMINAL_ID() {
		return TERMINAL_ID;
	}

	public void setTERMINAL_ID(String tERMINAL_ID) {
		TERMINAL_ID = tERMINAL_ID;
	}

	public String getTERMINAL_LOCATION() {
		return TERMINAL_LOCATION;
	}

	public void setTERMINAL_LOCATION(String tERMINAL_LOCATION) {
		TERMINAL_LOCATION = tERMINAL_LOCATION;
	}

	public String getMERCHANT_LOCATION() {
		return MERCHANT_LOCATION;
	}

	public void setMERCHANT_LOCATION(String mERCHANT_LOCATION) {
		MERCHANT_LOCATION = mERCHANT_LOCATION;
	}

	public String getCLAIM_DATE() {
		return CLAIM_DATE;
	}

	public void setCLAIM_DATE(String cLAIM_DATE) {
		CLAIM_DATE = cLAIM_DATE;
	}

	public String getARN() {
		return ARN;
	}

	public void setARN(String aRN) {
		ARN = aRN;
	}

	public String getDISPUTED_AMT() {
		return DISPUTED_AMT;
	}

	public void setDISPUTED_AMT(String dISPUTED_AMT) {
		DISPUTED_AMT = dISPUTED_AMT;
	}

	public String getDISPUTE_REASON() {
		return DISPUTE_REASON;
	}

	public void setDISPUTE_REASON(String dISPUTE_REASON) {
		DISPUTE_REASON = dISPUTE_REASON;
	}

	public String getDISPUTE_REMARKS() {
		return DISPUTE_REMARKS;
	}

	public void setDISPUTE_REMARKS(String dISPUTE_REMARKS) {
		DISPUTE_REMARKS = dISPUTE_REMARKS;
	}

	public String getACQ_AMT() {
		return ACQ_AMT;
	}

	public void setACQ_AMT(String aCQ_AMT) {
		ACQ_AMT = aCQ_AMT;
	}

	public String getACQ_CURRENCY() {
		return ACQ_CURRENCY;
	}

	public void setACQ_CURRENCY(String aCQ_CURRENCY) {
		ACQ_CURRENCY = aCQ_CURRENCY;
	}

	public String getRAISED_ID() {
		return RAISED_ID;
	}

	public void setRAISED_ID(String rAISED_ID) {
		RAISED_ID = rAISED_ID;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getDISPUTE_TYPE() {
		return DISPUTE_TYPE;
	}

	public void setDISPUTE_TYPE(String dISPUTE_TYPE) {
		DISPUTE_TYPE = dISPUTE_TYPE;
	}

	public String getACTION_DATE() {
		return ACTION_DATE;
	}

	public void setACTION_DATE(String aCTION_DATE) {
		ACTION_DATE = aCTION_DATE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	
	
	public Date getDOWNLOAD_DATE() {
		return DOWNLOAD_DATE;
	}

	public void setDOWNLOAD_DATE(Date dOWNLOAD_DATE) {
		DOWNLOAD_DATE = dOWNLOAD_DATE;
	}

	public Date getDATE_CREATE() {
		return DATE_CREATE;
	}

	public void setDATE_CREATE(Date dATE_CREATE) {
		DATE_CREATE = dATE_CREATE;
	}

	public Date getCREATED_DATE() {
		return CREATED_DATE;
	}

	public void setCREATED_DATE(Date cREATED_DATE) {
		CREATED_DATE = cREATED_DATE;
	}

	public Date getUPDATED_DATE() {
		return UPDATED_DATE;
	}

	public void setUPDATED_DATE(Date uPDATED_DATE) {
		UPDATED_DATE = uPDATED_DATE;
	}

	public String getDCRS_REMARKS() {
		return DCRS_REMARKS;
	}

	public void setDCRS_REMARKS(String dCRS_REMARKS) {
		DCRS_REMARKS = dCRS_REMARKS;
	}

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}

	
	
}
