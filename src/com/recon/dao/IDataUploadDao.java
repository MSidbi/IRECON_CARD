package com.recon.dao;

import com.recon.model.FileSourceBean;

public interface IDataUploadDao {
	
	public boolean uploadData(FileSourceBean sourceBean);

}
