package com.recon.dao;

import com.recon.model.ConfigurationBean;

public interface ConfigurationDao {
	

	public int getFileId(ConfigurationBean configBean)throws Exception;
	
	public void addConfigParams(ConfigurationBean configBean)throws Exception;
	
	public boolean addFileSource(ConfigurationBean configBean)throws Exception;
	
	public boolean chkTblExistOrNot(ConfigurationBean configBean)throws Exception;

}
