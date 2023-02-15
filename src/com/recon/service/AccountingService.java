package com.recon.service;

import java.util.List;

import com.recon.model.GenerateTTUMBean;

public interface AccountingService {

	public List<List<GenerateTTUMBean>> getReportERecords(GenerateTTUMBean generatettumBeanObj,int inRec_Set_Id)throws Exception;

}
