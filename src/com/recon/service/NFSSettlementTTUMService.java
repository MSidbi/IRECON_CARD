package com.recon.service;

import java.util.HashMap;
import java.util.List;

import com.recon.model.NFSSettlementBean;

public interface NFSSettlementTTUMService {

	HashMap<String, Object> validateMonthlySettlement(NFSSettlementBean beanObj);
	
	boolean runNFSMonthlyTTUM(NFSSettlementBean beanObj);
  
	List<Object> getMonthlyTTUMData(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> validateDailyInterchange(NFSSettlementBean beanObj);
	
	public boolean runDailyInterchangeTTUM(NFSSettlementBean beanObj);
	
	public List<Object> getDailyInterchangeTTUMData(NFSSettlementBean beanObj);
	
	boolean runSettlementVoucher(NFSSettlementBean beanObj);
	
	public List<Object> getSettlementVoucher(NFSSettlementBean beanObj);
	
	public List<Object> getAdjTTUM(NFSSettlementBean beanObj);
	
	public boolean runAdjTTUM(NFSSettlementBean beanObj);
	
	public boolean runCooperativeTTUM(NFSSettlementBean beanObj);
	
	public List<Object> getCooperativeTTUM(NFSSettlementBean beanObj);
	
	public boolean checkDailyInterchangeTTUMProcess(NFSSettlementBean beanObj);
	
	String ValidateLateReversalFile(NFSSettlementBean beanObj);
	
	List<Object> getLateReversalTTUMData(NFSSettlementBean beanObj);
	
	String checkReversalTTUMProcess(NFSSettlementBean beanObj)throws Exception;
	
	boolean runLateReversalTTUM(NFSSettlementBean beanObj);
	
	List<Object> getPBGBAdjTTUM(NFSSettlementBean beanObj);
}
