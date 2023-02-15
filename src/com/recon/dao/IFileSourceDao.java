package com.recon.dao;

import java.util.List;

import com.recon.model.FileSourceBean;

public interface IFileSourceDao {
	
	public boolean uplodFTPFile(FileSourceBean ftpBean);
	public FileSourceBean getFTPDetails(int fileId); 
	public boolean uplodData(FileSourceBean ftpBean);
	public List<FileSourceBean> getFileList();
	public boolean uploadFile(byte[]file,int fileId) throws Exception;
	public List<FileSourceBean> getFileDetails();
	public boolean updateFileDetails(FileSourceBean ftpBean);

}
