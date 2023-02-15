package com.recon.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@SuppressWarnings("serial")
@Entity
@Table(name="main_filesource")
public class MainFilesource implements Serializable{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "FILEID")
	private String FILEID;
	
	@Column(name = "FILENAME")
	private String FILENAME;
	
	@Column(name = "TABLENAME")
	private String TABLENAME;
	
	@Column(name = "ACTIVEFLAG")
	private String ACTIVEFLAG;
	
	@Column(name = "DATASEPARATOR")
	private String DATASEPARATOR;
	
	@Column(name = "RDDATAFRM")
	private String RDDATAFRM;
	
	@Column(name = "CHARPATT")
	private String CHARPATT;
	
	@Column(name = "FILE_CATEGORY")
	private String FILE_CATEGORY;
	
	@Column(name = "FILTERATION")
	private String FILTERATION;
	
	@Column(name = "KNOCKOFF")
	private String KNOCKOFF;
	
	@Column(name = "FILE_SUBCATEGORY")
	private String FILE_SUBCATEGORY;
	
	@Column(name = "FILE_COUNT")
	private String FILE_COUNT;

	public String getFILEID() {
		return FILEID;
	}

	public void setFILEID(String fILEID) {
		FILEID = fILEID;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	public String getTABLENAME() {
		return TABLENAME;
	}

	public void setTABLENAME(String tABLENAME) {
		TABLENAME = tABLENAME;
	}

	public String getACTIVEFLAG() {
		return ACTIVEFLAG;
	}

	public void setACTIVEFLAG(String aCTIVEFLAG) {
		ACTIVEFLAG = aCTIVEFLAG;
	}

	public String getDATASEPARATOR() {
		return DATASEPARATOR;
	}

	public void setDATASEPARATOR(String dATASEPARATOR) {
		DATASEPARATOR = dATASEPARATOR;
	}

	public String getRDDATAFRM() {
		return RDDATAFRM;
	}

	public void setRDDATAFRM(String rDDATAFRM) {
		RDDATAFRM = rDDATAFRM;
	}

	public String getCHARPATT() {
		return CHARPATT;
	}

	public void setCHARPATT(String cHARPATT) {
		CHARPATT = cHARPATT;
	}

	public String getFILE_CATEGORY() {
		return FILE_CATEGORY;
	}

	public void setFILE_CATEGORY(String fILE_CATEGORY) {
		FILE_CATEGORY = fILE_CATEGORY;
	}

	public String getFILTERATION() {
		return FILTERATION;
	}

	public void setFILTERATION(String fILTERATION) {
		FILTERATION = fILTERATION;
	}

	public String getKNOCKOFF() {
		return KNOCKOFF;
	}

	public void setKNOCKOFF(String kNOCKOFF) {
		KNOCKOFF = kNOCKOFF;
	}

	public String getFILE_SUBCATEGORY() {
		return FILE_SUBCATEGORY;
	}

	public void setFILE_SUBCATEGORY(String fILE_SUBCATEGORY) {
		FILE_SUBCATEGORY = fILE_SUBCATEGORY;
	}

	public String getFILE_COUNT() {
		return FILE_COUNT;
	}

	public void setFILE_COUNT(String fILE_COUNT) {
		FILE_COUNT = fILE_COUNT;
	}

	
}
