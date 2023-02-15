package com.recon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;

public interface CashnetSettlementService {

	HashMap<String, Object> validationForMonthlyInterchange(NFSSettlementBean beanObj);
	
	boolean runCashnetInterchange(NFSSettlementBean beanObj);
	
	List<Object> getInterchangeData(NFSSettlementBean beanObj);
	HashMap<String, Object> checkInterchangeProcess(NFSSettlementBean beanObj);
	HashMap<String, Object> validationForDailyInterchange(NFSSettlementBean beanObj);
	List<Object> getDailyInterchangeData(NFSSettlementBean beanObj);
	boolean runCashnetDailyInterchange(NFSSettlementBean beanObj);
	HashMap<String, Object> checkDailyInterchangeProcess(NFSSettlementBean beanObj);
	HashMap<String, Object> validateForInterchangeTTUM(NFSSettlementBean beanObj);
	boolean runInterchangeTTUM(NFSSettlementBean beanObj);
	HashMap<String, Object> checkTTUMProcess(NFSSettlementBean beanObj);
	List<Object> getTTUMData(NFSSettlementBean beanObj);
	
	HashMap<String, Object> uploadAdjustmentFile(NFSSettlementBean beanObj,MultipartFile file);
	HashMap<String, Object> CheckAdjustmentFileUpload(NFSSettlementBean beanObj);
	HashMap<String, Object> CheckAdjNRawFileUpload(NFSSettlementBean beanObj);
	HashMap<String, Object> CheckSettlementProcess(NFSSettlementBean beanObj);
	boolean runCashnetSettlement(NFSSettlementBean beanObj);
	List<Object> getCashnetSettlementReport(NFSSettlementBean beanObj);
	boolean runCashnetSettlementVouch(NFSSettlementBean beanObj);
	HashMap<String, Object> CheckSettlementVoucher(NFSSettlementBean beanObj);
	List<Object> getCashnetSettVoucher(NFSSettlementBean beanObj);
	boolean CashnetSettlementRollback(NFSSettlementBean beanObj);
	
}
