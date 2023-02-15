package com.recon.model;

public class RoleBean {

	private String user_id;
	private Integer page_id;
	private String page_name;
	private String page_url;
	private String page_title;
	private Integer page_level;
	private Integer parent_id;
	private String page_status;
	private String entry_dt;
	private String entry_by;
	private String updt_dt;
	private String updt_by;
	
	/**Field Name Constants. */
	public static final String USER_ID = "user_id";
	public static final String PAGE_ID = "page_id";
	public static final String PAGE_NAME = "page_name";
	public static final String PAGE_URL = "page_url";
	public static final String PAGE_TITLE = "page_title";
	public static final String PAGE_LEVEL = "page_level";
	public static final String PARENT_ID = "parent_id";
	public static final String PAGE_STATUS = "page_status";
	public static final String ENTRY_DT = "entry_dt";
	public static final String ENTRY_BY = "entry_by";
	public static final String UPDT_DT = "updt_dt";
	public static final String UPDT_BY = "updt_by";

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		if(user_id == null){
			user_id = "";
		}
		this.user_id = user_id.trim();
	}
	/**
	 * @return the page_id
	 */
	public Integer getPage_id() {
		return page_id;
	}
	/**
	 * @param page_id the page_id to set
	 */
	public void setPage_id(Integer page_id) {
		this.page_id = page_id;
	}
	/**
	 * @return the page_name
	 */
	public String getPage_name() {
		return page_name;
	}
	/**
	 * @param page_name the page_name to set
	 */
	public void setPage_name(String page_name) {
		if(page_name == null){
			page_name = "";
		}
		this.page_name = page_name.trim();
	}
	/**
	 * @return the page_url
	 */
	public String getPage_url() {
		return page_url;
	}
	/**
	 * @param page_url the page_url to set
	 */
	public void setPage_url(String page_url) {
		if(page_url == null){
			page_url = "";
		}
		this.page_url = page_url.trim();
	}
	/**
	 * @return the page_title
	 */
	public String getPage_title() {
		return page_title;
	}
	/**
	 * @param page_title the page_title to set
	 */
	public void setPage_title(String page_title) {
		if(page_title == null){
			page_title = "";
		}
		this.page_title = page_title.trim();
	}
	/**
	 * @return the page_level
	 */
	public Integer getPage_level() {
		return page_level;
	}
	/**
	 * @param page_level the page_level to set
	 */
	public void setPage_level(Integer page_level) {
		this.page_level = page_level;
	}
	/**
	 * @return the parent_id
	 */
	public Integer getParent_id() {
		return parent_id;
	}
	/**
	 * @param parent_id the parent_id to set
	 */
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}
	/**
	 * @return the page_status
	 */
	public String getPage_status() {
		return page_status;
	}
	/**
	 * @param page_status the page_status to set
	 */
	public void setPage_status(String page_status) {
		if(page_status == null){
			page_status = "";
		}
		this.page_status = page_status.trim();
	}
	/**
	 * @return the entry_dt
	 */
	public String getEntry_dt() {
		return entry_dt;
	}
	/**
	 * @param entry_dt the entry_dt to set
	 */
	public void setEntry_dt(String entry_dt) {
		if(entry_dt == null){
			entry_dt = "";
		}
		this.entry_dt = entry_dt.trim();
	}
	/**
	 * @return the entry_by
	 */
	public String getEntry_by() {
		return entry_by;
	}
	/**
	 * @param entry_by the entry_by to set
	 */
	public void setEntry_by(String entry_by) {
		if(entry_by == null){
			entry_by = "";
		}
		this.entry_by = entry_by.trim();
	}
	/**
	 * @return the updt_dt
	 */
	public String getUpdt_dt() {
		return updt_dt;
	}
	/**
	 * @param updt_dt the updt_dt to set
	 */
	public void setUpdt_dt(String updt_dt) {
		if(updt_dt == null){
			updt_dt = "";
		}
		this.updt_dt = updt_dt.trim();
	}
	/**
	 * @return the updt_by
	 */
	public String getUpdt_by() {
		return updt_by;
	}
	/**
	 * @param updt_by the updt_by to set
	 */
	public void setUpdt_by(String updt_by) {
		if(updt_by == null){
			updt_by = "";
		}
		this.updt_by = updt_by.trim();
	}

}
