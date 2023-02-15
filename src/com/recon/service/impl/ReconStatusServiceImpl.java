package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.ReconStatusDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.ReconStatusBean;
import com.recon.service.ReconStatusService;

@Component
public class ReconStatusServiceImpl implements ReconStatusService {

	@Autowired
	ReconStatusDao reconStatusDao;
	public List<String> getAllCategories()
	{
		return reconStatusDao.getAllCategories();
	}
	
	public List<CompareSetupBean> getlastUploadDetails(ReconStatusBean reconbean)
	{
		return reconStatusDao.getlastUploadDetails(reconbean);
	}
	
	public List<CompareSetupBean> getReconStatusReport(ReconStatusBean reconbean)
	{
		return reconStatusDao.getReconStatusReport(reconbean);
	}
}
