package com.recon.dao;

import java.util.List;

import com.recon.model.GenerateTTUMBean;

public interface AccountingDao {

	
	public List<List<GenerateTTUMBean>> getReportERecords(GenerateTTUMBean generatettumBeanObj,int inRec_Set_Id)throws Exception;
	

	
}
