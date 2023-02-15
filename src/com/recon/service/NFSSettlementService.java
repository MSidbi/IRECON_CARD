package com.recon.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.AddNewSolBean;
import com.recon.model.NFSSettlementBean;

public interface NFSSettlementService {
	
	public HashMap<String, Object> validatePrevFileUpload(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadDFSRawData(NFSSettlementBean banObj,MultipartFile file);
	
	//public HashMap<String, Object> validateNTSLUpload(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadNTSLFile(NFSSettlementBean beanObj,MultipartFile file);
	
	public HashMap<String,Object> ValidateDailySettProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> checkNFSMonthlyProcess(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> uploadMonthlyNTSLFile(NFSSettlementBean beanObj,MultipartFile file);
	
	public HashMap<String, Object> checkMonthlyNTSLUploaded(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateDailyInterchangeProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateForSettVoucher(NFSSettlementBean beanObj);
	
	public boolean checkSettVoucherProcess(NFSSettlementBean beanObj);

	HashMap<String,Object> ValidateForAdjTTUM(NFSSettlementBean beanObj);
	
	boolean checkAdjTTUMProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> ValidateCooperativeBank(NFSSettlementBean beanObj);
	
	public boolean checkCoopTTUMProcess(NFSSettlementBean beanObj);
	
	public HashMap<String,Object> validateSettDifference(NFSSettlementBean beanObj);
	
	boolean addCooperativeBank(String bankName, String accNumber);
	
	List<String> getNodalData(String state)throws Exception;
	
	boolean SaveNodalDetails(AddNewSolBean beanObj);
	
	HashMap<String,Object> CheckSettlementProcess(NFSSettlementBean beanObj);
	
	HashMap<String,Object> ValidateOtherSettProcess(NFSSettlementBean beanObj);
	
	
}
