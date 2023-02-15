package com.recon.service;

import java.util.List;

import com.recon.model.GenerateTTUMBean;

public interface GenerateRupayTTUMService {
	
	public void getTTUMSwitchRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateSwitchTTUM(GenerateTTUMBean generateTTUMBean,int inRec_Set_Id)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateCBSTTUM(GenerateTTUMBean generateTTUMBeanObj,List<GenerateTTUMBean> Data)throws Exception;
	
	public void TTUMRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public void TTUM_forDPart(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<GenerateTTUMBean> getCandDdifference(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> getMatchedRecordsTTUM(GenerateTTUMBean generateTTUMbean)throws Exception;
	
	public void getReportCRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> GenerateRupayTTUM(GenerateTTUMBean generateTTUMBean,int inRec_Set_Id)throws Exception;
	
	public void getRupayTTUMRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public void getReportERecords(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public void getSurchargeRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateCBSSurchargeTTUM(GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> getMatchedIntTxn(GenerateTTUMBean generateTTUMBeanObj,int inRec_Set_Id)throws Exception;
	
	public List<List<GenerateTTUMBean>> LevyCharges(List<List<GenerateTTUMBean>> Data,GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public String getLatestFileDate(GenerateTTUMBean generateTTUMBean);
	
	public void generateTTUM(List<List<GenerateTTUMBean>> generatettum_list)throws Exception;
	
	public void getFailedCBSRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception;

	public List<List<GenerateTTUMBean>> generateDisputeTTUM(
			GenerateTTUMBean ttumBean) throws Exception;

	public List<List<GenerateTTUMBean>> generateVisaDisputeTTUM(
			GenerateTTUMBean ttumBean) throws Exception;

}
