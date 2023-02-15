package com.recon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.NetworkFileUpdateDao;
import com.recon.model.NetworkFileUpdateBean;
import com.recon.service.NetworkFileUpdateService;

@Component
public class NetworkFileUpdateServiceImpl implements NetworkFileUpdateService {

	@Autowired NetworkFileUpdateDao networkFileUpdateDao;
	
	public boolean checkUploadFlag(NetworkFileUpdateBean networkBean)
	{
		return networkFileUpdateDao.checkUploadFlag(networkBean);
	}
	
	public void EntryForFile(NetworkFileUpdateBean networkBean)throws Exception
	{
		networkFileUpdateDao.EntryForFile(networkBean);
	}
	
	public boolean checkProcessFlag(NetworkFileUpdateBean networkBean)
	{
		return networkFileUpdateDao.checkProcessFlag(networkBean);
	}
	
	public void DeleteData(NetworkFileUpdateBean networkBean)
	{
		networkFileUpdateDao.DeleteData(networkBean);
	}
}
