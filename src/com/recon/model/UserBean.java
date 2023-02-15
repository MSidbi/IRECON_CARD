package com.recon.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class UserBean {

	private String user_id;
	private String user_name;
	//private String user_type;
	private String user_status;
	private String in_time;
	private String out_time;
	private String ip_address;
	private String last_login;
	private String entry_dt;
	private String entry_by;
	private String updt_dt;
	private String updt_by;
	
	/**Field Name Constants. */
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	//public static final String USER_TYPE = "user_type";
	public static final String USER_STATUS = "user_status";
	public static final String IN_TIME = "in_time";
	public static final String OUT_TIME = "out_time";
	public static final String IP_ADDRESS = "ip_address";
	public static final String LAST_LOGIN = "last_login";
	public static final String ENTRY_DT = "entry_dt";
	public static final String ENTRY_BY = "entry_by";
	public static final String UPDT_DT = "updt_dt";
	public static final String UPDT_BY = "updt_by";

	/**
	 * @return the user_id
	 */
	@JsonProperty("UserId")
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
	 * @return the user_name
	 */
	@JsonProperty("UserName")
	public String getUser_name() {
		return user_name;
	}
	/**
	 * @param user_name the user_name to set
	 */
	public void setUser_name(String user_name) {
		if(user_name == null){
			user_name = "";
		}
		this.user_name = user_name.trim();
	}
	/**
	 * @return the user_type
	 */
	/*@JsonProperty("UserType")
	public String getUser_type() {
		return user_type;
	}
	*//**
	 * @param user_type the user_type to set
	 *//*
	public void setUser_type(String user_type) {
		if(user_type == null){
			user_type = "";
		}
		this.user_type = user_type.trim();
	}*/
	/**
	 * @return the user_status
	 */
	@JsonProperty("UserStatus")
	public String getUser_status() {
		return user_status;
	}
	/**
	 * @param user_status the user_status to set
	 */
	public void setUser_status(String user_status) {
		if(user_status == null){
			user_status = "";
		}
		this.user_status = user_status.trim();
	}
	/**
	 * @return the in_time
	 */
	@JsonProperty("InTime")
	public String getIn_time() {
		return in_time;
	}
	/**
	 * @param in_time the in_time to set
	 */
	public void setIn_time(String in_time) {
		this.in_time = in_time;
	}
	/**
	 * @return the out_time
	 */
	@JsonProperty("OutTime")
	public String getOut_time() {
		return out_time;
	}
	/**
	 * @param out_time the out_time to set
	 */
	public void setOut_time(String out_time) {
		this.out_time = out_time;
	}
	/**
	 * @return the ip_address
	 */
	@JsonProperty("IPAddress")
	public String getIp_address() {
		return ip_address;
	}
	/**
	 * @param ip_address the ip_address to set
	 */
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	/**
	 * @return the last_login
	 */
	@JsonProperty("LastLogin")
	public String getLast_login() {
		return last_login;
	}
	/**
	 * @param last_login the last_login to set
	 */
	public void setLast_login(String last_login) {
		if(last_login == null){
			last_login = "";
		}
		this.last_login = last_login.trim();
	}
	/**
	 * @return the entry_dt
	 */
	@JsonProperty("EntryDate")
	public String getEntry_dt() {
		return entry_dt;
	}
	/**
	 * @param entry_dt the entry_dt to set
	 */
	public void setEntry_dt(String entry_dt) {
		if(entry_dt == null){
			entry_dt="";
		}
		this.entry_dt = entry_dt.trim();
	}
	/**
	 * @return the entry_by
	 */
	@JsonProperty("EntryBy")
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
	@JsonProperty("UpdateDate")
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
	@JsonProperty("UpdateBy")
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
