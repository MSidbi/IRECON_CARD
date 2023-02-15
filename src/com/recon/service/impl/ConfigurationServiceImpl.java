package com.recon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.ConfigurationDao;
import com.recon.model.ConfigurationBean;
import com.recon.service.ConfigurationService;

@Component
public class ConfigurationServiceImpl implements ConfigurationService{

	@Autowired
	ConfigurationDao configDao;

	@Override
	public int getFileId(ConfigurationBean configBean)throws Exception
	{
		return configDao.getFileId(configBean);
	}
	
	@Override
	public void addConfigParams(ConfigurationBean configBean)throws Exception
	{
		configDao.addConfigParams(configBean);
	}

	@Override
	public boolean addFileSource(ConfigurationBean configBean) throws Exception {
		
		return configDao.addFileSource(configBean);
	}

	@Override
	public boolean chkTblExistOrNot(ConfigurationBean configBean)
			throws Exception {
		
		return configDao.chkTblExistOrNot(configBean);
	}
}
