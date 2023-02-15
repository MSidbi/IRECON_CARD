package com.recon.model;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ChargeBackBean {


	private List<String> ExcelHeaders;
	
	private List<String> data;	
	
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

	private String NETWORK;
	
	private String CARD_NUMBER;

	private String RRN;

	private String SEQ_NUMBER;

	private String ACCOUNT_NUMBER;

	private String TRAN_DATE;

	private String TRAN_TIME;

	private String DISPUTED_AMT;

	private String DISPUTE_REASON;

	private String DISPUTE_REMARKS;

	private String ACQ_AMT;

	private String RAISED_ID;

	private String DOWNLOAD_DATE;

	private String DATE_CREATE;

	private String REMARKS;	

	private String SUB_CATEGORY;	

	private String RAISED_DATE;		
	
	private String RESPONSE_CODE;	
	
	private String AUTHORIZATION_CODE;	
	
	private String MCC;		
	
	private String TERMINAL_ID;		
	
	private String TERMINAL_LOCATION;	
	
	private String MERCHANT_LOCATION;	

	private String CLAIM_DATE;	

	private String ARN;

	private String ACQ_CURRENCY;	

	private String STATUS;	

	private String DISPUTE_TYPE;	

	private String ACTION_DATE;	
	
	 private String   ADJ_DATE;	
	
	 private String  ADJ_TYPE;

	 private String  ACQ;
	 
	 private String  ISR;	

	 private String  RESPONSE;

	 private String  TXN_DATE;	
	
	 private String  TXN_TIME;	

	 private String  ATM_ID;
	 
	 private String  CARD_NO;	
	
	 private String  CHB_DATE;	

	 private String  CHB_REF;	
	
	 private String  TXN_AMOUNT;
	 	
	 private String  ADJ_AMOUNT;
	
	 private String  ACQ_FEE;	
	
	 private String  ISS_FEE;	
	
	 private String  ISS_FEE_SW;	
	
	 private String  ADJ_FEE;	
	
	 private String  ADJ_REF;	
	
	 private String  ADJ_PROOF;	

	 private String  ADJCUSTPENALTY;	
	
	 private String  EMV_STATUS;	
	
	 private String  ADJ_REASON_CODE;	
	
	private String ETRMS_REF   ;	
	
	private String REF_DATE   ;	
	
	private String USER_ID ;	
	
	private String IP_ADDRESS   ;	
	
	private String CHARGEBACK  ;	
	
	private String CHARGEBACK_DATE  ;	
	
	private String REPRESENTMENT  ;	
	
	private String REPRESENTMENT_DATE  ;
	
	private String PRE_ARBITRATION  ;	
	
	private String PRE_ARBITRATION_DATE   ;	
	
	private String PRE_ARBITRATION_REJECT   ;	
	
	private String PRE_ARBITRATION_REJECT_DATE ;	
	
	private String ARBITRATION       ;	
	
	private String ARBITRATION_DATE ;	

	private String CREDIT_ADJUSTMENT ;	

	private String CREDIT_ADJUSTMENT_DATE   ;	

	private String DEBIT_ADJUSTMENT;	

	private String DEBIT_ADJUSTMENT_DATE   ; 	
	
	private Date CREATED_DATE;	

	private Date UPDATED_DATE;
	
	private String FILEDATE;
	
	private List<String> stExcelHeader;
	
	private String [] stExcelHeaderCSV;
	
	private String fileType;
	
	
	
	

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String[] getStExcelHeaderCSV() {
		return stExcelHeaderCSV;
	}

	public void setStExcelHeaderCSV(String[] stExcelHeaderCSV) {
		this.stExcelHeaderCSV = stExcelHeaderCSV;
	}

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		if(fILEDATE==null){
			fILEDATE = "";
		}
		FILEDATE = fILEDATE;
	}

	public List<String> getStExcelHeader() {
		return stExcelHeader;
	}

	public void setStExcelHeader(List<String> stExcelHeader) {
		this.stExcelHeader = stExcelHeader;
	}

	public String getNETWORK() {
		return NETWORK;
	}

	public void setNETWORK(String nETWORK) {
		if(nETWORK==null){
			nETWORK = "";
		}
		NETWORK = nETWORK;
	}

	public String getCARD_NUMBER() {
		return CARD_NUMBER;
	}

	public void setCARD_NUMBER(String cARD_NUMBER) {
		if(cARD_NUMBER==null){
			cARD_NUMBER = "";
		}
		CARD_NUMBER = cARD_NUMBER;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		if(rRN==null){
			rRN = "";
		}
		RRN = rRN;
	}

	public String getSEQ_NUMBER() {
		return SEQ_NUMBER;
	}

	public void setSEQ_NUMBER(String sEQ_NUMBER) {
		if(sEQ_NUMBER==null){
			sEQ_NUMBER = "";
		}
		SEQ_NUMBER = sEQ_NUMBER;
	}

	public String getACCOUNT_NUMBER() {
		return ACCOUNT_NUMBER;
	}

	public void setACCOUNT_NUMBER(String aCCOUNT_NUMBER) {
		if(aCCOUNT_NUMBER==null){
			aCCOUNT_NUMBER = "";
		}
		ACCOUNT_NUMBER = aCCOUNT_NUMBER;
	}

	public String getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(String tRAN_DATE) {
		if(tRAN_DATE==null){
			tRAN_DATE = "";
		}
		TRAN_DATE = tRAN_DATE;
	}

	public String getTRAN_TIME() {
		return TRAN_TIME;
	}

	public void setTRAN_TIME(String tRAN_TIME) {
		if(tRAN_TIME==null){
			tRAN_TIME = "";
		}
		TRAN_TIME = tRAN_TIME;
	}

	public String getDISPUTED_AMT() {
		return DISPUTED_AMT;
	}

	public void setDISPUTED_AMT(String dISPUTED_AMT) {
		if(dISPUTED_AMT==null){
			dISPUTED_AMT = "";
		}
		DISPUTED_AMT = dISPUTED_AMT;
	}

	public String getDISPUTE_REASON() {
		return DISPUTE_REASON;
	}

	public void setDISPUTE_REASON(String dISPUTE_REASON) {
		if(dISPUTE_REASON==null){
			dISPUTE_REASON = "";
		}
		DISPUTE_REASON = dISPUTE_REASON;
	}

	public String getDISPUTE_REMARKS() {
		return DISPUTE_REMARKS;
	}

	public void setDISPUTE_REMARKS(String dISPUTE_REMARKS) {
		if(dISPUTE_REMARKS==null){
			dISPUTE_REMARKS = "";
		}
		DISPUTE_REMARKS = dISPUTE_REMARKS;
	}

	public String getACQ_AMT() {
		return ACQ_AMT;
	}

	public void setACQ_AMT(String aCQ_AMT) {
		if(aCQ_AMT==null){
			aCQ_AMT = "";
		}
		ACQ_AMT = aCQ_AMT;
	}

	public String getRAISED_ID() {
		return RAISED_ID;
	}

	public void setRAISED_ID(String rAISED_ID) {
		if(rAISED_ID==null){
			rAISED_ID = "";
		}
		RAISED_ID = rAISED_ID;
	}

	public String getDOWNLOAD_DATE() {
		return DOWNLOAD_DATE;
	}

	public void setDOWNLOAD_DATE(String dOWNLOAD_DATE) {
		if(dOWNLOAD_DATE==null){
			dOWNLOAD_DATE = "";
		}
		DOWNLOAD_DATE = dOWNLOAD_DATE;
	}

	public String getDATE_CREATE() {
		return DATE_CREATE;
	}

	public void setDATE_CREATE(String dATE_CREATE) {
		if(dATE_CREATE==null){
			dATE_CREATE = "";
		}
		DATE_CREATE = dATE_CREATE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		if(rEMARKS==null){
			rEMARKS = "";
		}
		REMARKS = rEMARKS;
	}

	public String getSUB_CATEGORY() {
		return SUB_CATEGORY;
	}

	public void setSUB_CATEGORY(String sUB_CATEGORY) {
		if(sUB_CATEGORY==null){
			sUB_CATEGORY = "";
		}
		SUB_CATEGORY = sUB_CATEGORY;
	}

	public String getRAISED_DATE() {
		return RAISED_DATE;
	}

	public void setRAISED_DATE(String rAISED_DATE) {
		if(rAISED_DATE==null){
			rAISED_DATE = "";
		}
		RAISED_DATE = rAISED_DATE;
	}

	public String getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}

	public void setRESPONSE_CODE(String rESPONSE_CODE) {
		if(rESPONSE_CODE==null){
			rESPONSE_CODE = "";
		}
		RESPONSE_CODE = rESPONSE_CODE;
	}

	public String getAUTHORIZATION_CODE() {
		return AUTHORIZATION_CODE;
	}

	public void setAUTHORIZATION_CODE(String aUTHORIZATION_CODE) {
		if(aUTHORIZATION_CODE==null){
			aUTHORIZATION_CODE = "";
		}
		AUTHORIZATION_CODE = aUTHORIZATION_CODE;
	}

	public String getMCC() {
		return MCC;
	}

	public void setMCC(String mCC) {
		if(mCC==null){
			mCC = "";
		}
		MCC = mCC;
	}

	public String getTERMINAL_ID() {
		return TERMINAL_ID;
	}

	public void setTERMINAL_ID(String tERMINAL_ID) {
		if(tERMINAL_ID==null){
			tERMINAL_ID = "";
		}
		TERMINAL_ID = tERMINAL_ID;
	}

	public String getTERMINAL_LOCATION() {
		return TERMINAL_LOCATION;
	}

	public void setTERMINAL_LOCATION(String tERMINAL_LOCATION) {
		if(tERMINAL_LOCATION==null){
			tERMINAL_LOCATION = "";
		}
		TERMINAL_LOCATION = tERMINAL_LOCATION;
	}

	public String getMERCHANT_LOCATION() {
		return MERCHANT_LOCATION;
	}

	public void setMERCHANT_LOCATION(String mERCHANT_LOCATION) {
		if(mERCHANT_LOCATION==null){
			mERCHANT_LOCATION = "";
		}
		MERCHANT_LOCATION = mERCHANT_LOCATION;
	}

	public String getCLAIM_DATE() {
		return CLAIM_DATE;
	}

	public void setCLAIM_DATE(String cLAIM_DATE) {
		if(cLAIM_DATE==null){
			cLAIM_DATE = "";
		}
		CLAIM_DATE = cLAIM_DATE;
	}

	public String getARN() {
		return ARN;
	}

	public void setARN(String aRN) {
		if(aRN==null){
			aRN = "";
		}
		ARN = aRN;
	}

	public String getACQ_CURRENCY() {
		return ACQ_CURRENCY;
	}

	public void setACQ_CURRENCY(String aCQ_CURRENCY) {
		if(aCQ_CURRENCY==null){
			aCQ_CURRENCY = "";
		}
		ACQ_CURRENCY = aCQ_CURRENCY;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		if(sTATUS==null){
			sTATUS = "";
		}
		STATUS = sTATUS;
	}

	public String getDISPUTE_TYPE() {
		return DISPUTE_TYPE;
	}

	public void setDISPUTE_TYPE(String dISPUTE_TYPE) {
		if(dISPUTE_TYPE==null){
			dISPUTE_TYPE = "";
		}
		DISPUTE_TYPE = dISPUTE_TYPE;
	}

	public String getACTION_DATE() {
		return ACTION_DATE;
	}

	public void setACTION_DATE(String aCTION_DATE) {
		if(aCTION_DATE==null){
			aCTION_DATE = "";
		}
		ACTION_DATE = aCTION_DATE;
	}

	

	public String getADJ_DATE() {
		return ADJ_DATE;
	}

	public void setADJ_DATE(String aDJ_DATE) {
		if(aDJ_DATE==null){
			aDJ_DATE = "";
		}
		ADJ_DATE = aDJ_DATE;
	}

	public String getADJ_TYPE() {
		return ADJ_TYPE;
	}

	public void setADJ_TYPE(String aDJ_TYPE) {
		if(aDJ_TYPE==null){
			aDJ_TYPE = "";
		}
		ADJ_TYPE = aDJ_TYPE;
	}

	public String getACQ() {
		return ACQ;
	}

	public void setACQ(String aCQ) {
		if(aCQ==null){
			aCQ = "";
		}
		ACQ = aCQ;
	}

	public String getISR() {
		return ISR;
	}

	public void setISR(String iSR) {
		if(iSR==null){
			iSR = "";
		}
		ISR = iSR;
	}

	public String getRESPONSE() {
		return RESPONSE;
	}

	public void setRESPONSE(String rESPONSE) {
		if(rESPONSE==null){
			rESPONSE = "";
		}
		RESPONSE = rESPONSE;
	}

	public String getTXN_DATE() {
		return TXN_DATE;
	}

	public void setTXN_DATE(String tXN_DATE) {
		if(tXN_DATE==null){
			tXN_DATE = "";
		}
		TXN_DATE = tXN_DATE;
	}

	public String getTXN_TIME() {
		return TXN_TIME;
	}

	public void setTXN_TIME(String tXN_TIME) {
		if(tXN_TIME==null){
			tXN_TIME = "";
		}
		TXN_TIME = tXN_TIME;
	}

	public String getATM_ID() {
		return ATM_ID;
	}

	public void setATM_ID(String aTM_ID) {
		if(aTM_ID==null){
			aTM_ID = "";
		}
		ATM_ID = aTM_ID;
	}

	public String getCARD_NO() {
		return CARD_NO;
	}

	public void setCARD_NO(String cARD_NO) {
		if(cARD_NO==null){
			cARD_NO = "";
		}
		CARD_NO = cARD_NO;
	}

	public String getCHB_DATE() {
		return CHB_DATE;
	}

	public void setCHB_DATE(String cHB_DATE) {
		if(cHB_DATE==null){
			cHB_DATE = "";
		}
		CHB_DATE = cHB_DATE;
	}

	public String getCHB_REF() {
		return CHB_REF;
	}

	public void setCHB_REF(String cHB_REF) {
		if(cHB_REF==null){
			cHB_REF = "";
		}
		CHB_REF = cHB_REF;
	}

	public String getTXN_AMOUNT() {
		return TXN_AMOUNT;
	}

	public void setTXN_AMOUNT(String tXN_AMOUNT) {
		if(tXN_AMOUNT==null){
			tXN_AMOUNT = "";
		}
		TXN_AMOUNT = tXN_AMOUNT;
	}

	public String getADJ_AMOUNT() {
		return ADJ_AMOUNT;
	}

	public void setADJ_AMOUNT(String aDJ_AMOUNT) {
		if(aDJ_AMOUNT==null){
			aDJ_AMOUNT = "";
		}
		ADJ_AMOUNT = aDJ_AMOUNT;
	}

	public String getACQ_FEE() {
		return ACQ_FEE;
	}

	public void setACQ_FEE(String aCQ_FEE) {
		if(aCQ_FEE==null){
			aCQ_FEE = "";
		}
		ACQ_FEE = aCQ_FEE;
	}

	public String getISS_FEE() {
		return ISS_FEE;
	}

	public void setISS_FEE(String iSS_FEE) {
		if(iSS_FEE==null){
			iSS_FEE = "";
		}
		ISS_FEE = iSS_FEE;
	}

	public String getISS_FEE_SW() {
		return ISS_FEE_SW;
	}

	public void setISS_FEE_SW(String iSS_FEE_SW) {
		if(iSS_FEE_SW==null){
			iSS_FEE_SW = "";
		}
		ISS_FEE_SW = iSS_FEE_SW;
	}

	public String getADJ_FEE() {
		return ADJ_FEE;
	}

	public void setADJ_FEE(String aDJ_FEE) {
		if(aDJ_FEE==null){
			aDJ_FEE = "";
		}
		ADJ_FEE = aDJ_FEE;
	}

	public String getADJ_REF() {
		return ADJ_REF;
	}

	public void setADJ_REF(String aDJ_REF) {
		if(aDJ_REF==null){
			aDJ_REF = "";
		}
		ADJ_REF = aDJ_REF;
	}

	public String getADJ_PROOF() {
		return ADJ_PROOF;
	}

	public void setADJ_PROOF(String aDJ_PROOF) {
		if(aDJ_PROOF==null){
			aDJ_PROOF = "";
		}
		ADJ_PROOF = aDJ_PROOF;
	}

	public String getADJCUSTPENALTY() {
		return ADJCUSTPENALTY;
	}

	public void setADJCUSTPENALTY(String aDJCUSTPENALTY) {
		if(aDJCUSTPENALTY==null){
			aDJCUSTPENALTY = "";
		}
		ADJCUSTPENALTY = aDJCUSTPENALTY;
	}

	public String getEMV_STATUS() {
		return EMV_STATUS;
	}

	public void setEMV_STATUS(String eMV_STATUS) {
		if(eMV_STATUS==null){
			eMV_STATUS = "";
		}
		EMV_STATUS = eMV_STATUS;
	}

	public String getADJ_REASON_CODE() {
		return ADJ_REASON_CODE;
	}

	public void setADJ_REASON_CODE(String aDJ_REASON_CODE) {
		if(aDJ_REASON_CODE==null){
			aDJ_REASON_CODE = "";
		}
		ADJ_REASON_CODE = aDJ_REASON_CODE;
	}

	public String getETRMS_REF() {
		return ETRMS_REF;
	}

	public void setETRMS_REF(String eTRMS_REF) {
		if(eTRMS_REF==null){
			eTRMS_REF = "";
		}
		ETRMS_REF = eTRMS_REF;
	}

	public String getREF_DATE() {
		return REF_DATE;
	}

	public void setREF_DATE(String rEF_DATE) {
		if(rEF_DATE==null){
			rEF_DATE = "";
		}
		REF_DATE = rEF_DATE;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		if(uSER_ID==null){
			uSER_ID = "";
		}
		USER_ID = uSER_ID;
	}

	public String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public void setIP_ADDRESS(String iP_ADDRESS) {
		if(iP_ADDRESS==null){
			iP_ADDRESS = "";
		}
		IP_ADDRESS = iP_ADDRESS;
	}

	public String getCHARGEBACK() {
		return CHARGEBACK;
	}

	public void setCHARGEBACK(String cHARGEBACK) {
		if(cHARGEBACK==null){
			cHARGEBACK = "";
		}
		CHARGEBACK = cHARGEBACK;
	}

	public String getCHARGEBACK_DATE() {
		return CHARGEBACK_DATE;
	}

	public void setCHARGEBACK_DATE(String cHARGEBACK_DATE) {
		if(cHARGEBACK_DATE==null){
			cHARGEBACK_DATE = "";
		}
		CHARGEBACK_DATE = cHARGEBACK_DATE;
	}

	public String getREPRESENTMENT() {
		return REPRESENTMENT;
	}

	public void setREPRESENTMENT(String rEPRESENTMENT) {
		if(rEPRESENTMENT==null){
			rEPRESENTMENT = "";
		}
		REPRESENTMENT = rEPRESENTMENT;
	}

	public String getREPRESENTMENT_DATE() {
		return REPRESENTMENT_DATE;
	}

	public void setREPRESENTMENT_DATE(String rEPRESENTMENT_DATE) {
		if(rEPRESENTMENT_DATE==null){
			rEPRESENTMENT_DATE = "";
		}
		REPRESENTMENT_DATE = rEPRESENTMENT_DATE;
	}

	public String getPRE_ARBITRATION() {
		return PRE_ARBITRATION;
	}

	public void setPRE_ARBITRATION(String pRE_ARBITRATION) {
		if(pRE_ARBITRATION==null){
			pRE_ARBITRATION = "";
		}
		PRE_ARBITRATION = pRE_ARBITRATION;
	}

	public String getPRE_ARBITRATION_DATE() {
		return PRE_ARBITRATION_DATE;
	}

	public void setPRE_ARBITRATION_DATE(String pRE_ARBITRATION_DATE) {
		if(pRE_ARBITRATION_DATE==null){
			pRE_ARBITRATION_DATE = "";
		}
		PRE_ARBITRATION_DATE = pRE_ARBITRATION_DATE;
	}

	public String getPRE_ARBITRATION_REJECT() {
		return PRE_ARBITRATION_REJECT;
	}

	public void setPRE_ARBITRATION_REJECT(String pRE_ARBITRATION_REJECT) {
		if(pRE_ARBITRATION_REJECT==null){
			pRE_ARBITRATION_REJECT = "";
		}
		PRE_ARBITRATION_REJECT = pRE_ARBITRATION_REJECT;
	}

	public String getPRE_ARBITRATION_REJECT_DATE() {
		return PRE_ARBITRATION_REJECT_DATE;
	}

	public void setPRE_ARBITRATION_REJECT_DATE(String pRE_ARBITRATION_REJECT_DATE) {
		if(pRE_ARBITRATION_REJECT_DATE==null){
			pRE_ARBITRATION_REJECT_DATE = "";
		}
		PRE_ARBITRATION_REJECT_DATE = pRE_ARBITRATION_REJECT_DATE;
	}

	public String getARBITRATION() {
		return ARBITRATION;
	}

	public void setARBITRATION(String aRBITRATION) {
		if(aRBITRATION==null){
			aRBITRATION = "";
		}
		ARBITRATION = aRBITRATION;
	}

	public String getARBITRATION_DATE() {
		return ARBITRATION_DATE;
	}

	public void setARBITRATION_DATE(String aRBITRATION_DATE) {
		if(aRBITRATION_DATE==null){
			aRBITRATION_DATE = "";
		}
		ARBITRATION_DATE = aRBITRATION_DATE;
	}

	public String getCREDIT_ADJUSTMENT() {
		return CREDIT_ADJUSTMENT;
	}

	public void setCREDIT_ADJUSTMENT(String cREDIT_ADJUSTMENT) {
		if(cREDIT_ADJUSTMENT==null){
			cREDIT_ADJUSTMENT = "";
		}
		CREDIT_ADJUSTMENT = cREDIT_ADJUSTMENT;
	}

	public String getCREDIT_ADJUSTMENT_DATE() {
		return CREDIT_ADJUSTMENT_DATE;
	}

	public void setCREDIT_ADJUSTMENT_DATE(String cREDIT_ADJUSTMENT_DATE) {
		if(cREDIT_ADJUSTMENT_DATE==null){
			cREDIT_ADJUSTMENT_DATE = "";
		}
		CREDIT_ADJUSTMENT_DATE = cREDIT_ADJUSTMENT_DATE;
	}

	public String getDEBIT_ADJUSTMENT() {
		return DEBIT_ADJUSTMENT;
	}

	public void setDEBIT_ADJUSTMENT(String dEBIT_ADJUSTMENT) {
		if(dEBIT_ADJUSTMENT==null){
			dEBIT_ADJUSTMENT = "";
		}
		DEBIT_ADJUSTMENT = dEBIT_ADJUSTMENT;
	}

	public String getDEBIT_ADJUSTMENT_DATE() {
		return DEBIT_ADJUSTMENT_DATE;
	}

	public void setDEBIT_ADJUSTMENT_DATE(String dEBIT_ADJUSTMENT_DATE) {
		if(dEBIT_ADJUSTMENT_DATE==null){
			dEBIT_ADJUSTMENT_DATE = "";
		}
		DEBIT_ADJUSTMENT_DATE = dEBIT_ADJUSTMENT_DATE;
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
	
	
}
