package com.recon.service;

import java.util.List;
import java.util.Map;

import com.recon.model.LoginBean;
import com.recon.model.ProcessDtlBean;
import com.recon.model.UserBean;

public interface LoginService {

	/**
	 * Method Validates User Id and Password from AD.
	 * @param loginBean
	 * @throws Exception
	 */
	public void validateUser(LoginBean loginBean) throws Exception;
	
	/**
	 * Fetches user details, if legitimate and permitted. 
	 * @param loginBean
	 * @return
	 * @throws Exception
	 */
	public LoginBean getUserDetail(LoginBean loginBean) throws Exception;
	
	/**
	 * End User Session (Logout)
	 * @param loginBean
	 * @throws Exception
	 */
	public void invalidateUser(LoginBean loginBean) throws Exception;
	
	/**
	 * End User Session (Logout)
	 * @param loginBean
	 * @throws Exception
	 */
	public void closeSession(LoginBean loginBean) throws Exception;
	
	/**
	 * checking whether already one user has logged in from this ip
	 */
	public boolean checkIp(LoginBean loginBean) throws Exception;
	
	/**
	 * checking whether already one user has logged in from this ip
	 */
	public Map<String, Object> getAllSession(LoginBean loginBean) throws Exception;
	
	public List<ProcessDtlBean> getProcessdtls(String Flag);
	
}
