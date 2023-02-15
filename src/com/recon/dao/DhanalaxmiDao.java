package com.recon.dao;

import java.util.HashMap;
import java.util.List;

import com.recon.model.NFSSettlementBean;

public interface DhanalaxmiDao {

	HashMap<String, Object> checkFileProcess(NFSSettlementBean beanObj);

	List<String> getSettlementVoucher(NFSSettlementBean beanObj);
	
	HashMap<String, Object> checkCbsGlUpload(NFSSettlementBean beanObj);
	
	List<Object> getCbsGlExceptionReport(NFSSettlementBean beanObj);
	
}
