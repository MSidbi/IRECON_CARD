package com.recon.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.UnMatchedTTUMBean;

public interface NFSUnmatchTTUMService {

	HashMap<String,Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkReconDateAndTTUMDataPresent(UnMatchedTTUMBean beanObj);
	
	boolean runTTUMProcess(UnMatchedTTUMBean beanObj);
	
	List<Object> getNFSTTUMData(UnMatchedTTUMBean beanObj);
	
	void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String zipName,HttpServletResponse response, boolean ZipFolder );
	
	boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj);
	
	Boolean NFSTtumRollback(UnMatchedTTUMBean beanObj);
	
	List<String> getNFSVoucher(UnMatchedTTUMBean beanObj);
}
