package com.recon.dao;

import java.util.List;
import java.util.Map;

import com.recon.model.LoginBean;
import com.recon.model.ProcessDtlBean;
import com.recon.model.UserBean;

public interface LoginDao {

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
	 * checking for 1 login through 1 Ip
	 */
	public boolean checkIp(LoginBean loginBean) throws Exception;
	
	/**
	 * getting all session on that system
	 */
	public Map<String, Object> getAllSession(LoginBean loginBean) throws Exception;

	public List<ProcessDtlBean> getProcessdtls(String flag);
	
	
}
