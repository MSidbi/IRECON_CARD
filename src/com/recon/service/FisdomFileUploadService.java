package com.recon.service;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.Act4Bean;
import com.recon.model.FisdomFileUploadBean;

public interface FisdomFileUploadService {
	
	HashMap<String, Object> validateFileAlreadyUploaded(FisdomFileUploadBean beanObj);
	
	HashMap<String , Object> readFiles(FisdomFileUploadBean beanObj, MultipartFile file);
	
	boolean checkTrackingTable(FisdomFileUploadBean beanObj);
	
	HashMap<String , Object> readGLFiles(Act4Bean beanObj, MultipartFile file);
	
	boolean validateGLAlreadyUpload(Act4Bean beanObj);

}
