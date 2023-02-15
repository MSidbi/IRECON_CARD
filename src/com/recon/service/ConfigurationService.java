package com.recon.service;



import com.recon.model.ConfigurationBean;

public interface ConfigurationService {

	
	/**
	 * getting max File id from db
	 * 
	 */
	public int getFileId(ConfigurationBean configBean)throws Exception;

	/**
	 * Making an entry in config tables
	 * 
	*/
	public void addConfigParams(ConfigurationBean configBean)throws Exception;
	

	/**
	 * Making an entry in Main_Filesource table
	 * 
	*/
	
	public boolean addFileSource(ConfigurationBean configBean)throws Exception;
	
	/**
	 * Check Table already exist or not
	 * 
	*/
	
	public boolean chkTblExistOrNot(ConfigurationBean configBean)throws Exception;
	
	
}
