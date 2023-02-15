package com.recon.dao;

import java.util.List;

import com.recon.model.RoleBean;

public interface RoleDao {

	/**
	 * Fetch Available Roles.
	 * @param roleBean
	 * @return
	 */
	public List<RoleBean> viewRole(RoleBean roleBean) throws Exception;

	/**
	 * Fetch User Roles.
	 * 
	 * @param roleBean
	 * @return
	 */
	public List<RoleBean> viewUserRole(RoleBean roleBean) throws Exception;

	/**
	 * Assign Roles to User
	 * 
	 * @param roles
	 * @param roleBean
	 * @throws Exception
	 */
	public void assignRole(List<Integer> roles, RoleBean roleBean) throws Exception;

	/**
	 * Revoke Roles from User
	 * 
	 * @param roles
	 * @param roleBean
	 * @throws Exception
	 */
	public void revokeRole(List<Integer> roles, RoleBean roleBean) throws Exception;
	
	/**
	 * Check if Role is assigned to User.
	 * @param roleBean
	 * @return
	 * @throws Exception
	 */
	public boolean checkRole(RoleBean roleBean) throws Exception;
}
