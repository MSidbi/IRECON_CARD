package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.AccountingDao;
import com.recon.model.GenerateTTUMBean;
import com.recon.service.AccountingService;

@Component
public class AccountingServiceImpl implements AccountingService {

	@Autowired
	AccountingDao accountingDao;
	
	public List<List<GenerateTTUMBean>> getReportERecords(GenerateTTUMBean generatettumBeanObj,int inRec_Set_Id)throws Exception
	{
		
		return accountingDao.getReportERecords(generatettumBeanObj,inRec_Set_Id);
		
	}

}
