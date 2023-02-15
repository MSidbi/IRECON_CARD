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
@Table(name="PENALTY_TRANSACTION")
public class PenaltyTransaction implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CATEGORY")
	private String CATEGORY;
	
	@Column(name = "SUB_CATEGORY")
	private String SUB_CATEGORY;
	
	@Column(name = "CBK_REMARKS")
	private String CBK_REMARKS;
	
	@Column(name = "DAYS")
	private String DAYS;
	
	@Column(name = "PENALTY_AMT")
	private String PENALTY_AMT;
	
	@Column(name = "PENALTY_INVOKE")
	private String PENALTY_INVOKE;
	
	@Column(name = "PENALTY_INVOKE_USERID")
	private String PENALTY_INVOKE_USERID;
	
	@Column(name = "CARD_NUMBER")
	private String CARD_NUMBER;
	
	@Column(name = "RESPONSE_CODE")
	private String RESPONSE_CODE;
	
	@Column(name = "RRN")
	private String RRN;	
	
	@Column(name = "TRAN_DATE")
	private String TRAN_DATE;
	
	@Column(name = "USER_ID")
	private String USER_ID;
	
	@Column(name = "CREATED_DATE")
	private Date CREATED_DATE;
	
	@Column(name = "UPDATED_DATE")
	private Date UPDATED_DATE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getCBK_REMARKS() {
		return CBK_REMARKS;
	}

	public void setCBK_REMARKS(String cBK_REMARKS) {
		CBK_REMARKS = cBK_REMARKS;
	}

	public String getDAYS() {
		return DAYS;
	}

	public void setDAYS(String dAYS) {
		DAYS = dAYS;
	}

	public String getPENALTY_AMT() {
		return PENALTY_AMT;
	}

	public void setPENALTY_AMT(String pENALTY_AMT) {
		PENALTY_AMT = pENALTY_AMT;
	}

	public String getPENALTY_INVOKE() {
		return PENALTY_INVOKE;
	}

	public void setPENALTY_INVOKE(String pENALTY_INVOKE) {
		PENALTY_INVOKE = pENALTY_INVOKE;
	}

	public String getPENALTY_INVOKE_USERID() {
		return PENALTY_INVOKE_USERID;
	}

	public void setPENALTY_INVOKE_USERID(String pENALTY_INVOKE_USERID) {
		PENALTY_INVOKE_USERID = pENALTY_INVOKE_USERID;
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

	public String getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(String tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
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

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	
	

}
