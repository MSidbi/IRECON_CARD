package com.recon.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.UploadTTUMBean;

public interface TTUMDataUploadService {
	
	public String readFile(UploadTTUMBean UploadBean,MultipartFile file) throws Exception;
	

}
