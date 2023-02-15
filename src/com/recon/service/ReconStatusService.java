package com.recon.service;

import java.util.List;

import com.recon.model.CompareSetupBean;
import com.recon.model.ReconStatusBean;

public interface ReconStatusService {
	
	public List<String> getAllCategories();
	
	public List<CompareSetupBean> getlastUploadDetails(ReconStatusBean reconbean);
	
	public List<CompareSetupBean> getReconStatusReport(ReconStatusBean reconbean);

}
