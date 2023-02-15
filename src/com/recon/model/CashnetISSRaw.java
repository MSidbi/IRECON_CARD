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
@Table(name="CASHNET_CASHNET_ISS_RAWDATA")
public class CashnetISSRaw implements Serializable{
	
	@Id
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "PARTICIPANT_ID")
	private String PARTICIPANT_ID               ;
	
	@Column(name = "TRANSACTION_TYPE")
	private String TRANSACTION_TYPE             ;
	
	@Column(name = "FROM_ACCOUNT_TYPE")
	private String FROM_ACCOUNT_TYPE            ;
	
	@Column(name = "TO_ACCOUNT_TYPE")
	private String TO_ACCOUNT_TYPE              ;
	
	@Column(name = "TRANSACTION_SERIAL")
	private String TRANSACTION_SERIAL           ;
	
	@Column(name = "RESPONSE_CODE")
	private String RESPONSE_CODE                ;
	
	@Column(name = "PAN_NUMBER")
	private String PAN_NUMBER                   ;
	
	@Column(name = "MEMBER_NUMBER")
	private String MEMBER_NUMBER                ;
	
	@Column(name = "APPROVAL_NUMBER")
	private String APPROVAL_NUMBER              ;
	
	@Column(name = "SYSTEM_TRACE_AUDIT")
	private String SYSTEM_TRACE_AUDIT           ;
	
	@Column(name = "TRANSACTION_DATE")
	private String TRANSACTION_DATE             ;
	
	@Column(name = "TRANSACTION_TIME")
	private String TRANSACTION_TIME             ;
	
	@Column(name = "MERCHANT_CATEGORY_CODE")
	private String MERCHANT_CATEGORY_CODE       ;
	
	@Column(name = "SETTLEMENT_DATECARD")
	private String SETTLEMENT_DATECARD          ;
	
	@Column(name = "ACCEPTOR_ID")
	private String ACCEPTOR_ID                  ;
	
	@Column(name = "CARD_ACCEPTOR_TERMINAL_ID")
	private String CARD_ACCEPTOR_TERMINAL_ID    ;
	
	@Column(name = "CARD_ACCEPTOR_TERMINAL_LOC")
	private String CARD_ACCEPTOR_TERMINAL_LOC   ;
	
	@Column(name = "AQUIRER_ID")
	private String AQUIRER_ID                   ;
	
	@Column(name = "NETWORK_ID")
	private String NETWORK_ID                   ;
	
	@Column(name = "ACCOUNT_1_NUMBER")
	private String ACCOUNT_1_NUMBER             ;
	
	@Column(name = "ACCOUNT_1_BRANCH_ID")
	private String ACCOUNT_1_BRANCH_ID          ;
	
	@Column(name = "ACCOUNT_2_NUMBER")
	private String ACCOUNT_2_NUMBER             ;
	
	@Column(name = "ACCOUNT_2_BRANCH_ID")
	private String ACCOUNT_2_BRANCH_ID          ;
	
	@Column(name = "TRANSACTION_CURRENCY")
	private String TRANSACTION_CURRENCY         ;
	
	@Column(name = "TRANSACTION_AMOUNT")
	private String TRANSACTION_AMOUNT           ;
	
	@Column(name = "ACTUAL_TRANSACTION_AMOUNT")
	private String ACTUAL_TRANSACTION_AMOUNT    ;
	
	@Column(name = "TRANSACTION_ACTIVITY_FEE")
	private String TRANSACTION_ACTIVITY_FEE     ;
	
	@Column(name = "ISSUER_1_SETTLEMENT_CURRENCY")
	private String ISSUER_1_SETTLEMENT_CURRENCY ;
	
	@Column(name = "ISSUER_1_SETTLEMENT_AMOUNT")
	private String ISSUER_1_SETTLEMENT_AMOUNT   ;
	
	@Column(name = "ISSUER_1_SETTLEMENT_FEE")
	private String ISSUER_1_SETTLEMENT_FEE      ;
	
	@Column(name = "ISSUER_1_STL_PROCESSING_FEE")
	private String ISSUER_1_STL_PROCESSING_FEE  ;
	
	@Column(name = "CARDHOLDER_1_BILL_CURRENCY")
	private String CARDHOLDER_1_BILL_CURRENCY   ;
	
	@Column(name = "CARDHOLDER_1_BILLING_AMOUNT")
	private String CARDHOLDER_1_BILLING_AMOUNT  ;
	
	@Column(name = "CARDHOLDER_1_BILL_ACTV_FEE")
	private String CARDHOLDER_1_BILL_ACTV_FEE   ;
	
	@Column(name = "CARDHOLDER_1_BILL_PROC_FEE")
	private String CARDHOLDER_1_BILL_PROC_FEE   ;
	
	@Column(name = "CARDHOLDER_1_BILL_SVC_FEE")
	private String CARDHOLDER_1_BILL_SVC_FEE    ;
	
	@Column(name = "TRANS_CRDHLDR_1_CONV_RATE")
	private String TRANS_CRDHLDR_1_CONV_RATE    ;
	
	@Column(name = "STLMNT_CRDHLDR_1_CONV_RATE")
	private String STLMNT_CRDHLDR_1_CONV_RATE   ;
	
	@Column(name = "PART_ID")
	private String PART_ID                      ;
	
	@Column(name = "DCRS_TRAN_NO")
	private String DCRS_TRAN_NO                 ;
	
	@Column(name = "NEXT_TRAN_DATE")
	private String NEXT_TRAN_DATE               ;
	
	@Column(name = "CREATEDDATE")
	private Date CREATEDDATE                  ;
	
	@Column(name = "CREATEDBY")
	private String CREATEDBY                    ;
	
	@Column(name = "FILEDATE")
	private String FILEDATE                     ;
	
	@Column(name = "DCRS_REMARKS")
	private String DCRS_REMARKS                 ;

	public String getPARTICIPANT_ID() {
		return PARTICIPANT_ID;
	}

	public void setPARTICIPANT_ID(String pARTICIPANT_ID) {
		PARTICIPANT_ID = pARTICIPANT_ID;
	}

	public String getTRANSACTION_TYPE() {
		return TRANSACTION_TYPE;
	}

	public void setTRANSACTION_TYPE(String tRANSACTION_TYPE) {
		TRANSACTION_TYPE = tRANSACTION_TYPE;
	}

	public String getFROM_ACCOUNT_TYPE() {
		return FROM_ACCOUNT_TYPE;
	}

	public void setFROM_ACCOUNT_TYPE(String fROM_ACCOUNT_TYPE) {
		FROM_ACCOUNT_TYPE = fROM_ACCOUNT_TYPE;
	}

	public String getTO_ACCOUNT_TYPE() {
		return TO_ACCOUNT_TYPE;
	}

	public void setTO_ACCOUNT_TYPE(String tO_ACCOUNT_TYPE) {
		TO_ACCOUNT_TYPE = tO_ACCOUNT_TYPE;
	}

	public String getTRANSACTION_SERIAL() {
		return TRANSACTION_SERIAL;
	}

	public void setTRANSACTION_SERIAL(String tRANSACTION_SERIAL) {
		TRANSACTION_SERIAL = tRANSACTION_SERIAL;
	}

	public String getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}

	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		RESPONSE_CODE = rESPONSE_CODE;
	}

	public String getPAN_NUMBER() {
		return PAN_NUMBER;
	}

	public void setPAN_NUMBER(String pAN_NUMBER) {
		PAN_NUMBER = pAN_NUMBER;
	}

	public String getMEMBER_NUMBER() {
		return MEMBER_NUMBER;
	}

	public void setMEMBER_NUMBER(String mEMBER_NUMBER) {
		MEMBER_NUMBER = mEMBER_NUMBER;
	}

	public String getAPPROVAL_NUMBER() {
		return APPROVAL_NUMBER;
	}

	public void setAPPROVAL_NUMBER(String aPPROVAL_NUMBER) {
		APPROVAL_NUMBER = aPPROVAL_NUMBER;
	}

	public String getSYSTEM_TRACE_AUDIT() {
		return SYSTEM_TRACE_AUDIT;
	}

	public void setSYSTEM_TRACE_AUDIT(String sYSTEM_TRACE_AUDIT) {
		SYSTEM_TRACE_AUDIT = sYSTEM_TRACE_AUDIT;
	}

	public String getTRANSACTION_DATE() {
		return TRANSACTION_DATE;
	}

	public void setTRANSACTION_DATE(String tRANSACTION_DATE) {
		TRANSACTION_DATE = tRANSACTION_DATE;
	}

	public String getTRANSACTION_TIME() {
		return TRANSACTION_TIME;
	}

	public void setTRANSACTION_TIME(String tRANSACTION_TIME) {
		TRANSACTION_TIME = tRANSACTION_TIME;
	}

	public String getMERCHANT_CATEGORY_CODE() {
		return MERCHANT_CATEGORY_CODE;
	}

	public void setMERCHANT_CATEGORY_CODE(String mERCHANT_CATEGORY_CODE) {
		MERCHANT_CATEGORY_CODE = mERCHANT_CATEGORY_CODE;
	}

	public String getSETTLEMENT_DATECARD() {
		return SETTLEMENT_DATECARD;
	}

	public void setSETTLEMENT_DATECARD(String sETTLEMENT_DATECARD) {
		SETTLEMENT_DATECARD = sETTLEMENT_DATECARD;
	}

	public String getACCEPTOR_ID() {
		return ACCEPTOR_ID;
	}

	public void setACCEPTOR_ID(String aCCEPTOR_ID) {
		ACCEPTOR_ID = aCCEPTOR_ID;
	}

	public String getCARD_ACCEPTOR_TERMINAL_ID() {
		return CARD_ACCEPTOR_TERMINAL_ID;
	}

	public void setCARD_ACCEPTOR_TERMINAL_ID(String cARD_ACCEPTOR_TERMINAL_ID) {
		CARD_ACCEPTOR_TERMINAL_ID = cARD_ACCEPTOR_TERMINAL_ID;
	}

	public String getCARD_ACCEPTOR_TERMINAL_LOC() {
		return CARD_ACCEPTOR_TERMINAL_LOC;
	}

	public void setCARD_ACCEPTOR_TERMINAL_LOC(String cARD_ACCEPTOR_TERMINAL_LOC) {
		CARD_ACCEPTOR_TERMINAL_LOC = cARD_ACCEPTOR_TERMINAL_LOC;
	}

	public String getAQUIRER_ID() {
		return AQUIRER_ID;
	}

	public void setAQUIRER_ID(String aQUIRER_ID) {
		AQUIRER_ID = aQUIRER_ID;
	}

	public String getNETWORK_ID() {
		return NETWORK_ID;
	}

	public void setNETWORK_ID(String nETWORK_ID) {
		NETWORK_ID = nETWORK_ID;
	}

	public String getACCOUNT_1_NUMBER() {
		return ACCOUNT_1_NUMBER;
	}

	public void setACCOUNT_1_NUMBER(String aCCOUNT_1_NUMBER) {
		ACCOUNT_1_NUMBER = aCCOUNT_1_NUMBER;
	}

	public String getACCOUNT_1_BRANCH_ID() {
		return ACCOUNT_1_BRANCH_ID;
	}

	public void setACCOUNT_1_BRANCH_ID(String aCCOUNT_1_BRANCH_ID) {
		ACCOUNT_1_BRANCH_ID = aCCOUNT_1_BRANCH_ID;
	}

	public String getACCOUNT_2_NUMBER() {
		return ACCOUNT_2_NUMBER;
	}

	public void setACCOUNT_2_NUMBER(String aCCOUNT_2_NUMBER) {
		ACCOUNT_2_NUMBER = aCCOUNT_2_NUMBER;
	}

	public String getACCOUNT_2_BRANCH_ID() {
		return ACCOUNT_2_BRANCH_ID;
	}

	public void setACCOUNT_2_BRANCH_ID(String aCCOUNT_2_BRANCH_ID) {
		ACCOUNT_2_BRANCH_ID = aCCOUNT_2_BRANCH_ID;
	}

	public String getTRANSACTION_CURRENCY() {
		return TRANSACTION_CURRENCY;
	}

	public void setTRANSACTION_CURRENCY(String tRANSACTION_CURRENCY) {
		TRANSACTION_CURRENCY = tRANSACTION_CURRENCY;
	}

	public String getTRANSACTION_AMOUNT() {
		return TRANSACTION_AMOUNT;
	}

	public void setTRANSACTION_AMOUNT(String tRANSACTION_AMOUNT) {
		TRANSACTION_AMOUNT = tRANSACTION_AMOUNT;
	}

	public String getACTUAL_TRANSACTION_AMOUNT() {
		return ACTUAL_TRANSACTION_AMOUNT;
	}

	public void setACTUAL_TRANSACTION_AMOUNT(String aCTUAL_TRANSACTION_AMOUNT) {
		ACTUAL_TRANSACTION_AMOUNT = aCTUAL_TRANSACTION_AMOUNT;
	}

	public String getTRANSACTION_ACTIVITY_FEE() {
		return TRANSACTION_ACTIVITY_FEE;
	}

	public void setTRANSACTION_ACTIVITY_FEE(String tRANSACTION_ACTIVITY_FEE) {
		TRANSACTION_ACTIVITY_FEE = tRANSACTION_ACTIVITY_FEE;
	}

	public String getISSUER_1_SETTLEMENT_CURRENCY() {
		return ISSUER_1_SETTLEMENT_CURRENCY;
	}

	public void setISSUER_1_SETTLEMENT_CURRENCY(String iSSUER_1_SETTLEMENT_CURRENCY) {
		ISSUER_1_SETTLEMENT_CURRENCY = iSSUER_1_SETTLEMENT_CURRENCY;
	}

	public String getISSUER_1_SETTLEMENT_AMOUNT() {
		return ISSUER_1_SETTLEMENT_AMOUNT;
	}

	public void setISSUER_1_SETTLEMENT_AMOUNT(String iSSUER_1_SETTLEMENT_AMOUNT) {
		ISSUER_1_SETTLEMENT_AMOUNT = iSSUER_1_SETTLEMENT_AMOUNT;
	}

	public String getISSUER_1_SETTLEMENT_FEE() {
		return ISSUER_1_SETTLEMENT_FEE;
	}

	public void setISSUER_1_SETTLEMENT_FEE(String iSSUER_1_SETTLEMENT_FEE) {
		ISSUER_1_SETTLEMENT_FEE = iSSUER_1_SETTLEMENT_FEE;
	}

	public String getISSUER_1_STL_PROCESSING_FEE() {
		return ISSUER_1_STL_PROCESSING_FEE;
	}

	public void setISSUER_1_STL_PROCESSING_FEE(String iSSUER_1_STL_PROCESSING_FEE) {
		ISSUER_1_STL_PROCESSING_FEE = iSSUER_1_STL_PROCESSING_FEE;
	}

	public String getCARDHOLDER_1_BILL_CURRENCY() {
		return CARDHOLDER_1_BILL_CURRENCY;
	}

	public void setCARDHOLDER_1_BILL_CURRENCY(String cARDHOLDER_1_BILL_CURRENCY) {
		CARDHOLDER_1_BILL_CURRENCY = cARDHOLDER_1_BILL_CURRENCY;
	}

	public String getCARDHOLDER_1_BILLING_AMOUNT() {
		return CARDHOLDER_1_BILLING_AMOUNT;
	}

	public void setCARDHOLDER_1_BILLING_AMOUNT(String cARDHOLDER_1_BILLING_AMOUNT) {
		CARDHOLDER_1_BILLING_AMOUNT = cARDHOLDER_1_BILLING_AMOUNT;
	}

	public String getCARDHOLDER_1_BILL_ACTV_FEE() {
		return CARDHOLDER_1_BILL_ACTV_FEE;
	}

	public void setCARDHOLDER_1_BILL_ACTV_FEE(String cARDHOLDER_1_BILL_ACTV_FEE) {
		CARDHOLDER_1_BILL_ACTV_FEE = cARDHOLDER_1_BILL_ACTV_FEE;
	}

	public String getCARDHOLDER_1_BILL_PROC_FEE() {
		return CARDHOLDER_1_BILL_PROC_FEE;
	}

	public void setCARDHOLDER_1_BILL_PROC_FEE(String cARDHOLDER_1_BILL_PROC_FEE) {
		CARDHOLDER_1_BILL_PROC_FEE = cARDHOLDER_1_BILL_PROC_FEE;
	}

	public String getCARDHOLDER_1_BILL_SVC_FEE() {
		return CARDHOLDER_1_BILL_SVC_FEE;
	}

	public void setCARDHOLDER_1_BILL_SVC_FEE(String cARDHOLDER_1_BILL_SVC_FEE) {
		CARDHOLDER_1_BILL_SVC_FEE = cARDHOLDER_1_BILL_SVC_FEE;
	}

	public String getTRANS_CRDHLDR_1_CONV_RATE() {
		return TRANS_CRDHLDR_1_CONV_RATE;
	}

	public void setTRANS_CRDHLDR_1_CONV_RATE(String tRANS_CRDHLDR_1_CONV_RATE) {
		TRANS_CRDHLDR_1_CONV_RATE = tRANS_CRDHLDR_1_CONV_RATE;
	}

	public String getSTLMNT_CRDHLDR_1_CONV_RATE() {
		return STLMNT_CRDHLDR_1_CONV_RATE;
	}

	public void setSTLMNT_CRDHLDR_1_CONV_RATE(String sTLMNT_CRDHLDR_1_CONV_RATE) {
		STLMNT_CRDHLDR_1_CONV_RATE = sTLMNT_CRDHLDR_1_CONV_RATE;
	}

	public String getPART_ID() {
		return PART_ID;
	}

	public void setPART_ID(String pART_ID) {
		PART_ID = pART_ID;
	}

	public String getDCRS_TRAN_NO() {
		return DCRS_TRAN_NO;
	}

	public void setDCRS_TRAN_NO(String dCRS_TRAN_NO) {
		DCRS_TRAN_NO = dCRS_TRAN_NO;
	}

	public String getNEXT_TRAN_DATE() {
		return NEXT_TRAN_DATE;
	}

	public void setNEXT_TRAN_DATE(String nEXT_TRAN_DATE) {
		NEXT_TRAN_DATE = nEXT_TRAN_DATE;
	}

	public Date getCREATEDDATE() {
		return CREATEDDATE;
	}

	public void setCREATEDDATE(Date cREATEDDATE) {
		CREATEDDATE = cREATEDDATE;
	}

	public String getCREATEDBY() {
		return CREATEDBY;
	}

	public void setCREATEDBY(String cREATEDBY) {
		CREATEDBY = cREATEDBY;
	}

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}

	public String getDCRS_REMARKS() {
		return DCRS_REMARKS;
	}

	public void setDCRS_REMARKS(String dCRS_REMARKS) {
		DCRS_REMARKS = dCRS_REMARKS;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

}
