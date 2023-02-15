package com.recon.dao;

import java.util.List;

import com.recon.model.CompareSetupBean;
import com.recon.model.ReconStatusBean;

public interface ReconStatusDao {

	
	public List<String> getAllCategories();
	
	public List<CompareSetupBean> getlastUploadDetails(ReconStatusBean reconbean);
	
	public List<CompareSetupBean> getReconStatusReport(ReconStatusBean reconbean);
}
