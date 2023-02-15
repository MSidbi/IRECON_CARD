package com.recon.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.IFileSourceDao;
import com.recon.model.FileSourceBean;
import com.recon.service.IFileSourceService;

@Component
public class FileSourceServiceImpl implements IFileSourceService {
	
	@Autowired IFileSourceDao fileSourceDao;

	@Override
	public boolean uplodFTPFile(FileSourceBean ftpBean) {
	
		return fileSourceDao.uplodFTPFile(ftpBean);
	}

	@Override
	public FileSourceBean getFTPDetails(int fileId) {
		
		return fileSourceDao.getFTPDetails(fileId);
	}

	@Override
	public boolean uplodData(FileSourceBean ftpBean) {
	
		return fileSourceDao.uplodData(ftpBean);
	}

	@Override
	public List<FileSourceBean> getFileList() {
		
		return fileSourceDao.getFileList();
	}

	@Override
	public boolean uploadFile(byte[] file,int fileId) throws Exception {
		
		return fileSourceDao.uploadFile(file,fileId);
	}
	@Override
	public List<FileSourceBean> getFileDetails() {
		return fileSourceDao.getFileDetails();
	}
	
	@Override
	public boolean updateFileDetails(FileSourceBean ftpBean) {

		return fileSourceDao.updateFileDetails(ftpBean);
	}

	

}
