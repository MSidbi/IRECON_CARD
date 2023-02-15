package com.recon.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.FileSourceBean;
import com.recon.model.ManualKnockoffBean;
import com.recon.model.NFSSettlementBean;

public interface RawFileRollbackDao {
	
	Map<String, Object> RawFileDateValidate(NFSSettlementBean beanObj);
	
	boolean CashnetRawFileRollback(NFSSettlementBean beanObj);
	
	Map<String, Object> ReconValidate(NFSSettlementBean beanObj);
	
	boolean VisaRawFileRollback(NFSSettlementBean beanObj);

	Map<String, Object> VisaRawFileDateValidate(NFSSettlementBean nfsSettlementBean);

}
