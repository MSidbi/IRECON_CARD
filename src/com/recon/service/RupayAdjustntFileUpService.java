package com.recon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;

public interface RupayAdjustntFileUpService {
	//public String rupayAdjustmentFileUpload(String fileDate,MultipartFile file);
	
	HashMap<String, Object> rupayAdjustmentFileUpload(String fileDate,String createdBy,String cycle, String network ,MultipartFile file, String subcategory);
	
	HashMap<String, Object> validateAdjustmentTTUM(String fileDate, String adjType);
	
	boolean runAdjTTUM(String fileDate, String adjType, String createdBy);
	
	HashMap<String, Object> validateAdjustmentUpload(String fileDate, String cycle, String network, String subcategory, boolean presentmentFile);
	
	HashMap<String, Object> validateAdjustmentTTUMProcess(String fileDate, String adjType);
	
	List<Object> getAdjTTUM(String fileDate, String adjType);
	
	HashMap<String, Object> rupayIntPresentFileUpload(String fileDate,String createdBy,String cycle, String network ,MultipartFile file, String subcategory);

}
