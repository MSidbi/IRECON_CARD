package com.recon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.SettlementDao;
import com.recon.model.ManualFileBean;
import com.recon.service.SettlementService;

@Component
public class SettlementServiceImpl implements SettlementService{

	@Autowired SettlementDao settlementDao;
	
	@Override
	public void manualReconToSettlement(ManualFileBean manualFileBeanObj)throws Exception
	{
		settlementDao.manualReconToSettlement(manualFileBeanObj);
	}
}
