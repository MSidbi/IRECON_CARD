package com.recon.dao;

import java.util.List;
import java.util.Map;

import com.recon.model.UserBean;

public interface UserDao {

	/** 
	 * Fetch List of All System User.
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public List<UserBean> viewUser(UserBean userBean) throws Exception;

	/** 
	 * Fetch Specific User Detail.
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public UserBean viewUserDetail(UserBean userBean) throws Exception;

	/** 
	 * Modify User Detail.
	 * @param userBean
	 * @throws Exception
	 */
	public void modifyUser(UserBean userBean) throws Exception;

	/** 
	 * Add User Detail.
	 * @param userBean
	 * @throws Exception
	 */
	public void addUser(UserBean userBean) throws Exception;
	
	/**
	 * Delete User Detail.
	 * @param userBean
	 * @throws Exception
	 */
	public void deleteUser(UserBean userBean) throws Exception;

	/**
	 * Fetch Currently Live User.
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public List<UserBean> liveUser(UserBean userBean) throws Exception;
	
	
	/**
	 * End User Session.
	 * @param userBean
	 * @throws Exception
	 */
	public void endUserSession(UserBean userBean) throws Exception;

	/**
	 * Fetch Status of Currently Logged In User.
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> currentUserStatus(UserBean userBean) throws Exception;

	/**
	 * Fetch User Recent Login Details.
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public List<UserBean> userLog(UserBean userBean) throws Exception;
}
