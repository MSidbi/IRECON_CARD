package com.recon.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name="CASHNET_CHARGEBACK ")
public class CashnetChargeBack implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "adj_date")
	 private String   adj_date;
	
	@Column(name = "adj_time")
	 private String   adj_time;
	
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
	
	@Column(name = "rrn")
	 private String  rrn              ;
	
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
	
	@Column(name = "created_date")
	 private String  created_date     ;
	
	@Column(name = "updated_date")
	 private String  updated_date     ;

	@Column(name = "IP_ADDRESS")
	 private String  IP_ADDRESS     ;
	
	@Column(name = "USER_ID")
	 private String  USER_ID     ;
	
	@Column(name = "CATEGORY")
	 private String  CATEGORY     ;
	
	@Column(name = "SUB_CATEGORY")
	 private String  SUB_CATEGORY     ;
	
	
	@Column(name = "FILEDATE")
	 private String  FILEDATE     ;
	
	@Column(name = "FILENAME")
	 private String  FILENAME     ;
	
	
	@Column(name = "STL_AMOUNT")
	 private String  stl_amount     ;
	
	@Column(name = "FST_CBK_RECV_AMT")
	 private String  fst_cbk_recv_amt     ;
	
	@Column(name = "REPRSTMT_RECV_AMT")
	 private String  reprstmt_recv_amt     ;
	
	@Column(name = "PRE_ARB_ADJ_RECV_AMT")
	 private String  pre_arb_adj_recv_amt     ;
	
	@Column(name = "REG_AMOUNT")
	 private String  reg_amount     ;
	
	@Column(name = "EN_FEE")
	 private String  en_fee     ;
	
	
	
	public String getAdj_time() {
		return adj_time;
	}

	public void setAdj_time(String adj_time) {
		this.adj_time = adj_time;
	}

	public String getEn_fee() {
		return en_fee;
	}

	public void setEn_fee(String en_fee) {
		this.en_fee = en_fee;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
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

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}

	public String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public void setIP_ADDRESS(String iP_ADDRESS) {
		IP_ADDRESS = iP_ADDRESS;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getCATEGORY() {
		return CATEGORY;
	}

	public void setCATEGORY(String cATEGORY) {
		CATEGORY = cATEGORY;
	}

	public String getSUB_CATEGORY() {
		return SUB_CATEGORY;
	}

	public void setSUB_CATEGORY(String sUB_CATEGORY) {
		SUB_CATEGORY = sUB_CATEGORY;
	}

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	
	
}
