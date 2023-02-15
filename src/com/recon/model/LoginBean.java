package com.recon.model;

import org.springframework.stereotype.Component;

@Component
public class LoginBean {

	private String user_id;
	private String password;
	private String user_name;
	private String user_type;
	private String user_status;
	private String last_login;
	private String session_id;
	private String ip_address;
	private String entry_dt;
	private String entry_by;
	private String updt_dt;
	private String updt_by;
	private String in_time;
	private String table_name;
	private String processType;
	
	
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	/**Field Name Constants. */
	public static final String USER_ID = "user_id";
	public static final String PASSWORD = "password";
	public static final String USER_NAME = "user_name";
	public static final String USER_TYPE = "user_type";
	public static final String USER_STATUS = "user_status";
	public static final String LAST_LOGIN = "last_login";
	public static final String SESSION_ID = "session_id";
	public static final String IP_ADDRESS = "ip_address";
	public static final String ENTRY_DT = "entry_dt";
	public static final String ENTRY_BY = "entry_by";
	public static final String UPDT_DT = "updt_dt";
	public static final String UPDT_BY = "updt_by";
	
	
	
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the user_name
	 */
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
	public String getUser_type() {
		return user_type;
	}
	/**
	 * @param user_type the user_type to set
	 */
	public void setUser_type(String user_type) {
		if(user_type == null){
			user_type = "";
		}
		this.user_type = user_type.trim();
	}
	/**
	 * @return the user_status
	 */
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
	 * @return the last_login
	 */
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
	 * @return the session_id
	 */
	public String getSession_id() {
		return session_id;
	}
	/**
	 * @param session_id the session_id to set
	 */
	public void setSession_id(String session_id) {
		if(session_id == null){
			session_id = "";
		}
		this.session_id = session_id.trim();
	}
	/**
	 * @return the ip_address
	 */
	public String getIp_address() {
		return ip_address;
	}
	/**
	 * @param ip_address the ip_address to set
	 */
	public void setIp_address(String ip_address) {
		if(ip_address == null){
			ip_address = "";
		}
		this.ip_address = ip_address.trim();
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
	public String getIn_time() {
		return in_time;
	}
	public void setIn_time(String in_time) {
		this.in_time = in_time;
	}
}
