package com.recon.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.FileSourceBean;
import com.recon.model.ManualKnockoffBean;
import com.recon.model.NFSSettlementBean;

public interface SettlementRollbackDao {
	
	Map<String, Object> NFSvalidateSettlementProcess(NFSSettlementBean beanObj);
	
	Boolean NFSSettlementRollback(NFSSettlementBean beanObj);
	
	Map<String, Object> NFSSettVoucherValidation(NFSSettlementBean beanObj);
	
	Boolean NFSSettVoucherRollback(NFSSettlementBean beanObj);
	
	Map<String, Object> NtslValidation(NFSSettlementBean beanObj);
	
	Boolean nfsNTSLRollback(NFSSettlementBean beanObj);
	


}
