package com.recon.service;

import java.util.HashMap;
import java.util.List;

import com.recon.model.NFSSettlementBean;
import com.recon.model.UnMatchedTTUMBean;

public interface CashnetGLStatementService {
	
	HashMap<String, Object> checkGLStatementProcess(UnMatchedTTUMBean beanObj);
	
	HashMap<String, Object> checkCashnetProcessing(UnMatchedTTUMBean beanObj);
	
	boolean runCashnetStatement(UnMatchedTTUMBean beanObj);
	
	Boolean CashnetStatementRollback(UnMatchedTTUMBean beanObj);
	
	List<Object> getCashnetGLStatement(UnMatchedTTUMBean beanObj);

}
