package com.recon.service;

import java.util.HashMap;
import java.util.List;

import com.recon.model.NFSSettlementBean;

public interface NFSSettlementCalService {

	public List<Object> getInterchangeData(NFSSettlementBean beanObj);
	
	public boolean runNFSMonthlyProc(NFSSettlementBean beanObj);
	
	public boolean runNFSDailyProc(NFSSettlementBean beanObj);
	
	public boolean runPBGBDailyProc(NFSSettlementBean beanObj);
	
	public List<Object> getDailySettlementReport(NFSSettlementBean beanObj);
	
	public HashMap<String, Object> skipSettlement(NFSSettlementBean beanObj);
	
	public boolean runDFSJCBDailyProc(NFSSettlementBean beanObj);
	
	public boolean runDailyInterchangeProc(NFSSettlementBean beanObj);
	
	public List<Object> getDailyInterchangeData(NFSSettlementBean beanObj);
	
}
