package com.recon.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="CASHNET_CHARGEBACK_ACQUIER")
public class CashnetChargebackAcquier implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "NETWORK")
	private String NETWORK;
	
	@Column(name = "SUB_CATEGORY")
	private String SUB_CATEGORY;
	
	@Column(name = "RAISED_DATE")
	private String RAISED_DATE;	
	
	@Column(name = "CARD_NUMBER")
	private String CARD_NUMBER;
	
	@Column(name = "RESPONSE_CODE")
	private String RESPONSE_CODE;
	
	@Column(name = "RRN")
	private String RRN;	
	
	@Column(name = "AUTHORIZATION_CODE")
	private String AUTHORIZATION_CODE;
	
	@Column(name = "SEQ_NUMBER")
	private String SEQ_NUMBER;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String ACCOUNT_NUMBER;
	
	@Column(name = "TRAN_DATE")
	private String TRAN_DATE;
	
	@Column(name = "TRAN_TIME")
	private String TRAN_TIME;	
	
	@Column(name = "MCC")
	private String MCC;	
	
	@Column(name = "TERMINAL_ID")
	private String TERMINAL_ID;	
	
	@Column(name = "TERMINAL_LOCATION")
	private String TERMINAL_LOCATION;
	
	@Column(name = "MERCHANT_LOCATION")
	private String MERCHANT_LOCATION;
	
	@Column(name = "CLAIM_DATE")
	private String CLAIM_DATE;
	
	@Column(name = "ARN")
	private String ARN;
	
	@Column(name = "DISPUTED_AMT")
	private String DISPUTED_AMT;
	
	@Column(name = "DISPUTE_REASON")
	private String DISPUTE_REASON;
	
	@Column(name = "DISPUTE_REMARKS")
	private String DISPUTE_REMARKS;
	
	@Column(name = "ACQ_AMT")
	private String ACQ_AMT;	
	
	@Column(name = "ACQ_CURRENCY")
	private String ACQ_CURRENCY;
	
	@Column(name = "RAISED_ID")
	private String RAISED_ID;
	
	@Column(name = "STATUS")
	private String STATUS;
	
	@Column(name = "DISPUTE_TYPE")
	private String DISPUTE_TYPE;
	
	@Column(name = "ACTION_DATE")
	private String ACTION_DATE;
	
	@Column(name = "REMARKS")
	private String REMARKS;
	
	@Column(name = "DOWNLOAD_DATE")
	private Date DOWNLOAD_DATE;
	
	@Column(name = "DATE_CREATE")
	private Date DATE_CREATE;	
	
	@Column(name = "adj_date")
	 private String   adj_date;
	
	@Column(name = "adj_type")
	 private String  adj_type;
	
	@Column(name = "acq")
	 private String  acq;
	
	@Column(name = "isr")
	 private String  isr              ;
	
	@Column(name = "response")
	 private String  response         ;
	
	@Column(name = "txn_date")
	 private String  txn_date         ;
	
	@Column(name = "txn_time")
	 private String  txn_time         ;
	
	@Column(name = "atm_id")
	 private String  atm_id           ;
	
	@Column(name = "card_no")
	 private String  card_no          ;
	
	@Column(name = "chb_date")
	 private String  chb_date         ;
	
	@Column(name = "chb_ref")
	 private String  chb_ref          ;
	
	@Column(name = "txn_amount")
	 private String  txn_amount       ;
	
	@Column(name = "adj_amount")
	 private String  adj_amount       ;
	
	@Column(name = "acq_fee")
	 private String  acq_fee          ;
	
	@Column(name = "iss_fee")
	 private String  iss_fee          ;
	
	@Column(name = "iss_fee_sw")
	 private String  iss_fee_sw       ;
	
	@Column(name = "adj_fee")
	 private String  adj_fee          ;
	
	@Column(name = "adj_ref")
	 private String  adj_ref          ;
	
	@Column(name = "adj_proof")
	 private String  adj_proof        ;
	
	@Column(name = "adjcustpenalty")
	 private String  adjcustpenalty   ;
	
	@Column(name = "emv_status")
	 private String  emv_status       ;
	
	@Column(name = "adj_reason_code")
	 private String  adj_reason_code  ;
	
	@Column(name = "ETRMS_REF")
	private String ETRMS_REF   ;
	
	@Column(name = "REF_DATE")
	private String REF_DATE   ;
	
	@Column(name = "USER_ID")
	private String USER_ID ;
	
	@Column(name = "IP_ADDRESS")
	private String IP_ADDRESS   ;
	
	@Column(name = "CHARGEBACK")
	private String CHARGEBACK  ;
	
	@Column(name = "CHARGEBACK_DATE")
	private String CHARGEBACK_DATE  ;
	
	@Column(name = "REPRESENTMENT")
	private String REPRESENTMENT  ;
	
	@Column(name = "REPRESENTMENT_DATE")
	private String REPRESENTMENT_DATE  ;
	
	@Column(name = "PRE_ARBITRATION")
	private String PRE_ARBITRATION  ;
	
	@Column(name = "PRE_ARBITRATION_DATE")
	private String PRE_ARBITRATION_DATE   ;
	
	@Column(name = "PRE_ARBITRATION_REJECT")
	private String PRE_ARBITRATION_REJECT   ;
	
	@Column(name = "PRE_ARBITRATION_REJECT_DATE")
	private String PRE_ARBITRATION_REJECT_DATE ;
	
	@Column(name = "ARBITRATION")
	private String ARBITRATION       ;
	
	@Column(name = "ARBITRATION_DATE")
	private String ARBITRATION_DATE ;
	
	@Column(name = "CREDIT_ADJUSTMENT")
	private String CREDIT_ADJUSTMENT ;
	
	@Column(name = "CREDIT_ADJUSTMENT_DATE")
	private String CREDIT_ADJUSTMENT_DATE   ;
	
	@Column(name = "DEBIT_ADJUSTMENT")
	private String DEBIT_ADJUSTMENT;
	
	@Column(name = "DEBIT_ADJUSTMENT_DATE")
	private String DEBIT_ADJUSTMENT_DATE   ;  
	
	@Column(name = "CREATED_DATE")
	private Date CREATED_DATE;
	
	@Column(name = "UPDATED_DATE")
	private Date UPDATED_DATE;

	@Column(name = "FILEDATE")
	private String FILEDATE;
	
	@Column(name = "CBK_REMARKS")
	private String CBK_REMARKS;
	
	@Column(name = "stl_amount")
	 private String  stl_amount     ;
	
	@Column(name = "fst_cbk_recv_amt")
	 private String  fst_cbk_recv_amt     ;
	
	@Column(name = "reprstmt_recv_amt")
	 private String  reprstmt_recv_amt     ;
	
	@Column(name = "pre_arb_adj_recv_amt")
	 private String  pre_arb_adj_recv_amt     ;
	
	@Column(name = "reg_amount")
	 private String  reg_amount     ;
	
	@Column(name = "en_fee")
	 private String  en_fee     ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNETWORK() {
		return NETWORK;
	}

	public void setNETWORK(String nETWORK) {
		NETWORK = nETWORK;
	}

	public String getSUB_CATEGORY() {
		return SUB_CATEGORY;
	}

	public void setSUB_CATEGORY(String sUB_CATEGORY) {
		SUB_CATEGORY = sUB_CATEGORY;
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

	public String getAdj_date() {
		return adj_date;
	}

	public void setAdj_date(String adj_date) {
		this.adj_date = adj_date;
	}

	public String getAdj_type() {
		return adj_type;
	}

	public void setAdj_type(String adj_type) {
		this.adj_type = adj_type;
	}

	public String getAcq() {
		return acq;
	}

	public void setAcq(String acq) {
		this.acq = acq;
	}

	public String getIsr() {
		return isr;
	}

	public void setIsr(String isr) {
		this.isr = isr;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getTxn_date() {
		return txn_date;
	}

	public void setTxn_date(String txn_date) {
		this.txn_date = txn_date;
	}

	public String getTxn_time() {
		return txn_time;
	}

	public void setTxn_time(String txn_time) {
		this.txn_time = txn_time;
	}

	public String getAtm_id() {
		return atm_id;
	}

	public void setAtm_id(String atm_id) {
		this.atm_id = atm_id;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getChb_date() {
		return chb_date;
	}

	public void setChb_date(String chb_date) {
		this.chb_date = chb_date;
	}

	public String getChb_ref() {
		return chb_ref;
	}

	public void setChb_ref(String chb_ref) {
		this.chb_ref = chb_ref;
	}

	public String getTxn_amount() {
		return txn_amount;
	}

	public void setTxn_amount(String txn_amount) {
		this.txn_amount = txn_amount;
	}

	public String getAdj_amount() {
		return adj_amount;
	}

	public void setAdj_amount(String adj_amount) {
		this.adj_amount = adj_amount;
	}

	public String getAcq_fee() {
		return acq_fee;
	}

	public void setAcq_fee(String acq_fee) {
		this.acq_fee = acq_fee;
	}

	public String getIss_fee() {
		return iss_fee;
	}

	public void setIss_fee(String iss_fee) {
		this.iss_fee = iss_fee;
	}

	public String getIss_fee_sw() {
		return iss_fee_sw;
	}

	public void setIss_fee_sw(String iss_fee_sw) {
		this.iss_fee_sw = iss_fee_sw;
	}

	public String getAdj_fee() {
		return adj_fee;
	}

	public void setAdj_fee(String adj_fee) {
		this.adj_fee = adj_fee;
	}

	public String getAdj_ref() {
		return adj_ref;
	}

	public void setAdj_ref(String adj_ref) {
		this.adj_ref = adj_ref;
	}

	public String getAdj_proof() {
		return adj_proof;
	}

	public void setAdj_proof(String adj_proof) {
		this.adj_proof = adj_proof;
	}

	public String getAdjcustpenalty() {
		return adjcustpenalty;
	}

	public void setAdjcustpenalty(String adjcustpenalty) {
		this.adjcustpenalty = adjcustpenalty;
	}

	public String getEmv_status() {
		return emv_status;
	}

	public void setEmv_status(String emv_status) {
		this.emv_status = emv_status;
	}

	public String getAdj_reason_code() {
		return adj_reason_code;
	}

	public void setAdj_reason_code(String adj_reason_code) {
		this.adj_reason_code = adj_reason_code;
	}

	public String getETRMS_REF() {
		return ETRMS_REF;
	}

	public void setETRMS_REF(String eTRMS_REF) {
		ETRMS_REF = eTRMS_REF;
	}

	public String getREF_DATE() {
		return REF_DATE;
	}

	public void setREF_DATE(String rEF_DATE) {
		REF_DATE = rEF_DATE;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public void setIP_ADDRESS(String iP_ADDRESS) {
		IP_ADDRESS = iP_ADDRESS;
	}

	public String getCHARGEBACK() {
		return CHARGEBACK;
	}

	public void setCHARGEBACK(String cHARGEBACK) {
		CHARGEBACK = cHARGEBACK;
	}

	public String getCHARGEBACK_DATE() {
		return CHARGEBACK_DATE;
	}

	public void setCHARGEBACK_DATE(String cHARGEBACK_DATE) {
		CHARGEBACK_DATE = cHARGEBACK_DATE;
	}

	public String getREPRESENTMENT() {
		return REPRESENTMENT;
	}

	public void setREPRESENTMENT(String rEPRESENTMENT) {
		REPRESENTMENT = rEPRESENTMENT;
	}

	public String getREPRESENTMENT_DATE() {
		return REPRESENTMENT_DATE;
	}

	public void setREPRESENTMENT_DATE(String rEPRESENTMENT_DATE) {
		REPRESENTMENT_DATE = rEPRESENTMENT_DATE;
	}

	public String getPRE_ARBITRATION() {
		return PRE_ARBITRATION;
	}

	public void setPRE_ARBITRATION(String pRE_ARBITRATION) {
		PRE_ARBITRATION = pRE_ARBITRATION;
	}

	public String getPRE_ARBITRATION_DATE() {
		return PRE_ARBITRATION_DATE;
	}

	public void setPRE_ARBITRATION_DATE(String pRE_ARBITRATION_DATE) {
		PRE_ARBITRATION_DATE = pRE_ARBITRATION_DATE;
	}

	public String getPRE_ARBITRATION_REJECT() {
		return PRE_ARBITRATION_REJECT;
	}

	public void setPRE_ARBITRATION_REJECT(String pRE_ARBITRATION_REJECT) {
		PRE_ARBITRATION_REJECT = pRE_ARBITRATION_REJECT;
	}

	public String getPRE_ARBITRATION_REJECT_DATE() {
		return PRE_ARBITRATION_REJECT_DATE;
	}

	public void setPRE_ARBITRATION_REJECT_DATE(String pRE_ARBITRATION_REJECT_DATE) {
		PRE_ARBITRATION_REJECT_DATE = pRE_ARBITRATION_REJECT_DATE;
	}

	public String getARBITRATION() {
		return ARBITRATION;
	}

	public void setARBITRATION(String aRBITRATION) {
		ARBITRATION = aRBITRATION;
	}

	public String getARBITRATION_DATE() {
		return ARBITRATION_DATE;
	}

	public void setARBITRATION_DATE(String aRBITRATION_DATE) {
		ARBITRATION_DATE = aRBITRATION_DATE;
	}

	public String getCREDIT_ADJUSTMENT() {
		return CREDIT_ADJUSTMENT;
	}

	public void setCREDIT_ADJUSTMENT(String cREDIT_ADJUSTMENT) {
		CREDIT_ADJUSTMENT = cREDIT_ADJUSTMENT;
	}

	public String getCREDIT_ADJUSTMENT_DATE() {
		return CREDIT_ADJUSTMENT_DATE;
	}

	public void setCREDIT_ADJUSTMENT_DATE(String cREDIT_ADJUSTMENT_DATE) {
		CREDIT_ADJUSTMENT_DATE = cREDIT_ADJUSTMENT_DATE;
	}

	public String getDEBIT_ADJUSTMENT() {
		return DEBIT_ADJUSTMENT;
	}

	public void setDEBIT_ADJUSTMENT(String dEBIT_ADJUSTMENT) {
		DEBIT_ADJUSTMENT = dEBIT_ADJUSTMENT;
	}

	public String getDEBIT_ADJUSTMENT_DATE() {
		return DEBIT_ADJUSTMENT_DATE;
	}

	public void setDEBIT_ADJUSTMENT_DATE(String dEBIT_ADJUSTMENT_DATE) {
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

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}

	public String getCBK_REMARKS() {
		return CBK_REMARKS;
	}

	public void setCBK_REMARKS(String cBK_REMARKS) {
		CBK_REMARKS = cBK_REMARKS;
	}

	public String getStl_amount() {
		return stl_amount;
	}

	public void setStl_amount(String stl_amount) {
		this.stl_amount = stl_amount;
	}

	public String getFst_cbk_recv_amt() {
		return fst_cbk_recv_amt;
	}

	public void setFst_cbk_recv_amt(String fst_cbk_recv_amt) {
		this.fst_cbk_recv_amt = fst_cbk_recv_amt;
	}

	public String getReprstmt_recv_amt() {
		return reprstmt_recv_amt;
	}

	public void setReprstmt_recv_amt(String reprstmt_recv_amt) {
		this.reprstmt_recv_amt = reprstmt_recv_amt;
	}

	public String getPre_arb_adj_recv_amt() {
		return pre_arb_adj_recv_amt;
	}

	public void setPre_arb_adj_recv_amt(String pre_arb_adj_recv_amt) {
		this.pre_arb_adj_recv_amt = pre_arb_adj_recv_amt;
	}

	public String getReg_amount() {
		return reg_amount;
	}

	public void setReg_amount(String reg_amount) {
		this.reg_amount = reg_amount;
	}

	public String getEn_fee() {
		return en_fee;
	}

	public void setEn_fee(String en_fee) {
		this.en_fee = en_fee;
	}
	
	
	
}
