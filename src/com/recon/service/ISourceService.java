package com.recon.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.FileSourceBean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.util.GenerateSettleTTUMBean;

public interface ISourceService {

	public List<ConfigurationBean> getFileDetails() throws Exception;
	public boolean updateFileDetails(ConfigurationBean ftpBean) throws Exception;
	public boolean addFileSource(ConfigurationBean configBean) throws Exception;
	public int getFileId(ConfigurationBean configBean);
	public boolean chkTblExistOrNot(ConfigurationBean configBean) throws Exception;
	public boolean addConfigParams(ConfigurationBean configBean) throws Exception;
	public String getHeaderList(int fileId);
	public ArrayList<ConfigurationBean> getCompareDetails(int fileId,String category, String subcat) throws Exception;
	public List<ConfigurationBean> getknockoffDetails(int fileId,String category, String subcat) throws Exception;
	public List<ConfigurationBean> getknockoffcrt(int fileId,String category, String subcat) throws Exception;
	public List<CompareSetupBean> getFileList(String category, String subcat) throws Exception;
	public List<CompareSetupBean> getFileTypeList(String category,String subcat, String table) throws Exception;
	public List<String> gettable_list();
	public List<String> getSubcategories(String category) throws Exception;
	public boolean generateCTF(SettlementBean settlementBean) throws IOException;
	public Settlement_FinalBean getReportDetails(SettlementBean settlementBean);
	public List<GenerateSettleTTUMBean> generateSettlTTUM(SettlementBean settlementBean);
	
	
	
	
}
