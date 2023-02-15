package com.recon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.VisaUploadBean;

public interface VisaSettlementService {
	
	HashMap<String, Object> checkFileAlreadyUpload(String fileDate, String subcate);
	
	boolean uploadFile(VisaUploadBean beanObj, MultipartFile file);
	
	boolean checkFileUpload(VisaUploadBean beanObj);
	
	HashMap<String, Object> checkSettlementProcess(VisaUploadBean beanObj);
	
	boolean runVisaSettlement(VisaUploadBean beanObj);
	
	List<Object> getSettlementData(VisaUploadBean beanObj);
	
	HashMap<String, Object> checkJVUploaded(VisaUploadBean beanObj);

	boolean readJVFile(VisaUploadBean beanObj, MultipartFile file);
	
	HashMap<String, Object> CheckTTUMProcessed(VisaUploadBean beanObj);
	
	List<Object> getSettlementTTUMData(VisaUploadBean beanObj);
	
	boolean runVisaSettlementTTUM(VisaUploadBean beanObj);
	
	boolean VisaSettRollback(VisaUploadBean beanObj);
}
