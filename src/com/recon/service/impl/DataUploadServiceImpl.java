package com.recon.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.IDataUploadDao;
import com.recon.model.FileSourceBean;
import com.recon.service.IDataUploadService;


@Component
public class DataUploadServiceImpl implements IDataUploadService {
	
@Autowired
IDataUploadDao dataUploadDao;

@Override
public boolean uploadData(FileSourceBean sourceBean) {
	// TODO Auto-generated method stub
	return dataUploadDao.uploadData(sourceBean);
}


	
}
