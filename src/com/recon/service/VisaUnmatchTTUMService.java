package com.recon.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.UnMatchedTTUMBean;

public interface VisaUnmatchTTUMService {

	HashMap<String,Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkReconDateAndTTUMDataPresent(UnMatchedTTUMBean beanObj);
	
	boolean runTTUMProcess(UnMatchedTTUMBean beanObj);
	
	List<Object> getVisaTTUMData(UnMatchedTTUMBean beanObj);
	
	void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String zipName,HttpServletResponse response, boolean ZipFolder );
	
	boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj);
	
	Boolean VisaTtumRollback(UnMatchedTTUMBean beanObj);

	List<String> getVISATTUMDataForTXT(UnMatchedTTUMBean beanObj);
}
