package com.recon.service;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;

public interface AdjustmentFileService {

	public HashMap<String, Object> uploadAdjustmentFile(NFSSettlementBean banObj,MultipartFile file);
	
	public HashMap<String, Object> validateAdjustmentFileUpload(NFSSettlementBean beanObj);
	
	Boolean AdjustmentRollback(NFSSettlementBean beanObj);
}
