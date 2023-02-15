package com.recon.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="PENALTY_MATRIX")
public class PenaltyMatrix implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CATEGORY")
	private String CATEGORY;
	
	@Column(name = "SUB_CATEGORY")
	private String SUB_CATEGORY;
	
	@Column(name = "EFFECTIVE_DT")
	private String EFFECTIVE_DT;
	
	@Column(name = "SR_NO")
	private String SR_NO;
	
	@Column(name = "RANGE_FROM")
	private String RANGE_FROM;
	
	@Column(name = "RANGE_TO")
	private String RANGE_TO;
	
	@Column(name = "PENALTY_AMOUNT")
	private String PENALTY_AMOUNT;
	
	@Column(name = "GST")
	private String GST;
	
	@Column(name = "TDS")
	private String TDS;

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

	public String getEFFECTIVE_DT() {
		return EFFECTIVE_DT;
	}

	public void setEFFECTIVE_DT(String eFFECTIVE_DT) {
		EFFECTIVE_DT = eFFECTIVE_DT;
	}

	public String getSR_NO() {
		return SR_NO;
	}

	public void setSR_NO(String sR_NO) {
		SR_NO = sR_NO;
	}

	public String getRANGE_FROM() {
		return RANGE_FROM;
	}

	public void setRANGE_FROM(String rANGE_FROM) {
		RANGE_FROM = rANGE_FROM;
	}

	public String getRANGE_TO() {
		return RANGE_TO;
	}

	public void setRANGE_TO(String rANGE_TO) {
		RANGE_TO = rANGE_TO;
	}

	public String getPENALTY_AMOUNT() {
		return PENALTY_AMOUNT;
	}

	public void setPENALTY_AMOUNT(String pENALTY_AMOUNT) {
		PENALTY_AMOUNT = pENALTY_AMOUNT;
	}

	public String getGST() {
		return GST;
	}

	public void setGST(String gST) {
		GST = gST;
	}

	public String getTDS() {
		return TDS;
	}

	public void setTDS(String tDS) {
		TDS = tDS;
	}
	
	

}
