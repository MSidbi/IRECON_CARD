package com.recon.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.IForceMatchDao;
import com.recon.dao.ISettelmentDao;
import com.recon.model.ForceMatchBean;
import com.recon.model.SettlementTypeBean;
import com.recon.service.IForceMatchService;

@Component
public class ForceMatchServiceImpl implements IForceMatchService {
	
	@Autowired
	IForceMatchDao forcematchdao;

	@Override
	public ArrayList<ForceMatchBean> getReconData(String trim,
			String trim2, String trim3, String trim4) {
		// TODO Auto-generated method stub
		return forcematchdao.getReconData( trim,
				 trim2,  trim3,  trim4)  ;
	}

	@Override
	public ArrayList<ForceMatchBean> getChngReconData(String trim,
			String trim2, int jtStartIndex, int jtPageSize) {
		// TODO Auto-generated method stub
		return forcematchdao.getChngReconData( trim,
				 trim2,  jtStartIndex,  jtPageSize);
	}

	@Override
	public int getReconDataCount(String trim,String trim4) {
		// TODO Auto-generated method stub
		return forcematchdao.getReconDataCount( trim,trim4);
	}

}
