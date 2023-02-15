package com.recon.service;

import com.recon.model.NetworkFileUpdateBean;

public interface NetworkFileUpdateService {

	public boolean checkUploadFlag(NetworkFileUpdateBean networkBean);
	
	public void EntryForFile(NetworkFileUpdateBean networkBean)throws Exception;
	
	public boolean checkProcessFlag(NetworkFileUpdateBean networkBean);
	
	public void DeleteData(NetworkFileUpdateBean networkBean);
}
