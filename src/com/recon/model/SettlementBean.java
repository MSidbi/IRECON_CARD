package com.recon.model;

public class SettlementBean {
	private String category;
	private String stsubCategory;
	private String stPath;
	private String datepicker;
	private String stMergerCategory;
	private String net_settl_amt;
	private String Man_Iss_represnment ="0";
	private String Man_ACQ_represnment ="0";
	private String User_id;
	private String fileName;
	
	
	

	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStsubCategory() {
		return stsubCategory;
	}

	public void setStsubCategory(String stsubCategory) {
		this.stsubCategory = stsubCategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private int inSettlement_id;
	private String stDatatype;

	public int getInSettlement_id() {
		return inSettlement_id;
	}

	public void setInSettlement_id(int inSettlement_id) {
		this.inSettlement_id = inSettlement_id;
	}

	public String getStDatatype() {
		return stDatatype;
	}

	public void setStDatatype(String stDatatype) {
		this.stDatatype = stDatatype;
	}

	public String getStPath() {
		return stPath;
	}

	public void setStPath(String stPath) {
		this.stPath = stPath;
	}

	public String getDatepicker() {
		return datepicker;
	}

	public void setDatepicker(String datepicker) {
		this.datepicker = datepicker;
	}

	public String getStMergerCategory() {
		return stMergerCategory;
	}

	public void setStMergerCategory(String stMergerCategory) {
		this.stMergerCategory = stMergerCategory;
	}

	public String getNet_settl_amt() {
		return net_settl_amt;
	}

	public void setNet_settl_amt(String net_settl_amt) {
		this.net_settl_amt = net_settl_amt;
	}

	public String getMan_Iss_represnment() {
		return Man_Iss_represnment;
	}

	public void setMan_Iss_represnment(String man_Iss_represnment) {
		Man_Iss_represnment = man_Iss_represnment;
	}

	public String getMan_ACQ_represnment() {
		return Man_ACQ_represnment;
	}

	public void setMan_ACQ_represnment(String man_ACQ_represnment) {
		Man_ACQ_represnment = man_ACQ_represnment;
	}

	public String getUser_id() {
		return User_id;
	}

	public void setUser_id(String user_id) {
		User_id = user_id;
	}
	



}
