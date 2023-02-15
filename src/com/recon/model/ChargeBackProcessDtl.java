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
@Table(name="CHARGE_BACK_PROCESS_DTL")
public class ChargeBackProcessDtl implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "FILEDATE")
	private String FILEDATE;
	
	@Column(name = "UPDLODBY")
	private String UPDLODBY;
	
	@Column(name = "UPLOADDATE")
	private Date UPLOADDATE;
	
	@Column(name = "FILE_CATEGORY")
	private String FILE_CATEGORY;
	
	@Column(name = "FILE_SUBCATEGORY")
	private String FILE_SUBCATEGORY;
	
	@Column(name = "CHARGEBACK")
	private String CHARGEBACK;
	
	@Column(name = "REPRESENTMENT")
	private String REPRESENTMENT;
	
	@Column(name = "PRE_ARBITRATION")
	private String PRE_ARBITRATION;
	
	@Column(name = "PRE_ARBITRATION_REJECT")
	private String PRE_ARBITRATION_REJECT;
	
	@Column(name = "ARBITRATION")
	private String ARBITRATION;
	
	@Column(name = "CREATED_DATE")
	private String CREATED_DATE;
	
	@Column(name = "UPDATED_DATE")
	private String UPDATED_DATE;

	@Column(name = "CREDIT")
	private String CREDIT;
	
	@Column(name = "DEBIT")
	private String DEBIT;
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCREDIT() {
		return CREDIT;
	}

	public void setCREDIT(String cREDIT) {
		CREDIT = cREDIT;
	}

	public String getDEBIT() {
		return DEBIT;
	}

	public void setDEBIT(String dEBIT) {
		DEBIT = dEBIT;
	}

	public String getFILEDATE() {
		return FILEDATE;
	}

	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}

	public String getUPDLODBY() {
		return UPDLODBY;
	}

	public void setUPDLODBY(String uPDLODBY) {
		UPDLODBY = uPDLODBY;
	}

	public Date getUPLOADDATE() {
		return UPLOADDATE;
	}

	public void setUPLOADDATE(Date uPLOADDATE) {
		UPLOADDATE = uPLOADDATE;
	}

	public String getFILE_CATEGORY() {
		return FILE_CATEGORY;
	}

	public void setFILE_CATEGORY(String fILE_CATEGORY) {
		FILE_CATEGORY = fILE_CATEGORY;
	}

	public String getFILE_SUBCATEGORY() {
		return FILE_SUBCATEGORY;
	}

	public void setFILE_SUBCATEGORY(String fILE_SUBCATEGORY) {
		FILE_SUBCATEGORY = fILE_SUBCATEGORY;
	}

	public String getCHARGEBACK() {
		return CHARGEBACK;
	}

	public void setCHARGEBACK(String cHARGEBACK) {
		CHARGEBACK = cHARGEBACK;
	}

	public String getREPRESENTMENT() {
		return REPRESENTMENT;
	}

	public void setREPRESENTMENT(String rEPRESENTMENT) {
		REPRESENTMENT = rEPRESENTMENT;
	}

	public String getPRE_ARBITRATION() {
		return PRE_ARBITRATION;
	}

	public void setPRE_ARBITRATION(String pRE_ARBITRATION) {
		PRE_ARBITRATION = pRE_ARBITRATION;
	}

	public String getPRE_ARBITRATION_REJECT() {
		return PRE_ARBITRATION_REJECT;
	}

	public void setPRE_ARBITRATION_REJECT(String pRE_ARBITRATION_REJECT) {
		PRE_ARBITRATION_REJECT = pRE_ARBITRATION_REJECT;
	}

	public String getARBITRATION() {
		return ARBITRATION;
	}

	public void setARBITRATION(String aRBITRATION) {
		ARBITRATION = aRBITRATION;
	}

	public String getCREATED_DATE() {
		return CREATED_DATE;
	}

	public void setCREATED_DATE(String cREATED_DATE) {
		CREATED_DATE = cREATED_DATE;
	}

	public String getUPDATED_DATE() {
		return UPDATED_DATE;
	}

	public void setUPDATED_DATE(String uPDATED_DATE) {
		UPDATED_DATE = uPDATED_DATE;
	}
	
	

}
