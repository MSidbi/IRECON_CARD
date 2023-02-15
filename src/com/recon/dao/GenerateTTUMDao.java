package com.recon.dao;

import java.util.List;

import com.recon.model.GenerateTTUMBean;
import com.recon.model.Mastercbs_respbean;

public interface GenerateTTUMDao {

	
	public List<List<GenerateTTUMBean>> generateTTUM(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public void TTUMRecords(List<String> Table_list,GenerateTTUMBean generateTTUMBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateTTUMForAMEX(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateTTUMForCARDTOCARD(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateTTUMForMastercard_C_Repo(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Switch(
			GenerateTTUMBean generatettumBeanObj) throws Exception;
	
	public List<List<Mastercbs_respbean>> generateTTUMForMastercard_Iss_cbs(
			GenerateTTUMBean generatettumBeanObj) throws Exception;
	public List<List<GenerateTTUMBean>> generateTTUMForMastercard(GenerateTTUMBean generatettumBeanObj)throws Exception;
	public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Issuer(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<List<GenerateTTUMBean>> generateTTUMForMastercard_Acq_cbs(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	//public List<List<GenerateTTUMBean>> generateTTUMForMastercard_ATM_DCC(GenerateTTUMBean generatettumBeanObj)throws Exception;

	public List<List<GenerateTTUMBean>> generateTTUMForMastercard_ATM_DCC(
			GenerateTTUMBean generatettumBeanObj) throws Exception;
public List<List<GenerateTTUMBean>> generatecashnetTTUM(GenerateTTUMBean generatettumBeanObj)throws Exception;
	
	public List<Integer>getRespCode (String category,String subcategory,String filename,String filedate) throws Exception;

	public List<Integer> getRespcode(String category, String subcategory, String filename, String filedate) throws Exception;
	
	public List<List<GenerateTTUMBean>> generateVISATTUM(
			GenerateTTUMBean generateTTUMBean) throws Exception;

	public List<List<GenerateTTUMBean>> generateNFSTTUM(
			GenerateTTUMBean generateTTUMBean);

	public String getLatestFileDate(GenerateTTUMBean generateTTUMBean);
}
