package com.recon.service;

import java.util.HashMap;
import java.util.List;

import com.recon.model.NFSSettlementBean;

public interface EODReportService {
	
	HashMap<String, Object>  checkEODReportProcess(NFSSettlementBean beanObj);
	
	HashMap<String, Object>  checkCBSFileUpload(NFSSettlementBean beanObj);
	
	HashMap<String, Object>  runEODReport(NFSSettlementBean beanObj);
	
	List<Object> getEODReport(NFSSettlementBean beanObj);
	
	List<Object> getOneWayReconReport(NFSSettlementBean beanObj);
	
	HashMap<String, Object>  checkAllReconProcess(NFSSettlementBean beanObj);
	
	List<String> getEODVoucher(NFSSettlementBean beanObj);
	
}
