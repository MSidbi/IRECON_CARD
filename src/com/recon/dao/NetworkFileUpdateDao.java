package com.recon.dao;

import com.recon.model.NetworkFileUpdateBean;

public interface NetworkFileUpdateDao {

	public boolean checkUploadFlag(NetworkFileUpdateBean networkBean);
	
	public void EntryForFile(NetworkFileUpdateBean networkBean)throws Exception;
	
	public boolean checkProcessFlag(NetworkFileUpdateBean networkBean);
	
	public void DeleteData(NetworkFileUpdateBean networkBean);
}
