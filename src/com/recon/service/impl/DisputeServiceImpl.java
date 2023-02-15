package com.recon.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.DisputeDao;
import com.recon.service.DisputeService;
import com.recon.util.JsonBean;
import com.recon.util.SearchData;

@Component
public class DisputeServiceImpl implements DisputeService{
	
	@Autowired
	DisputeDao disputeDao;

	@Override
	public SearchData returndata(SearchData searchdata) throws Exception {
		return disputeDao.returndata(searchdata);
	}

	@Override
	public boolean updateForceMatchData(String category, String stSubCategory,
			String file_name, String filedate, String insert) throws Exception {
		return disputeDao.updateForceMatchData(category,stSubCategory,file_name,filedate,insert);
	}

	@Override
	public Map<String, Object> returndata1(JsonBean searchdata, int jtStartIndex,
			int jtPageSize) {
		// TODO Auto-generated method stub
		return disputeDao.returndata1(searchdata, jtStartIndex, jtPageSize);
	}

	@Override
	public boolean updateDispute(JsonBean searchdata) {
		return disputeDao.updateDispute(searchdata);
		
	}



}
