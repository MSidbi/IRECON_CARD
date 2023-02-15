package com.recon.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.UnMatchedTTUMBean;

public interface RupayUnmatchTTUMService {

	HashMap<String,Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkReconDateAndTTUMDataPresent(UnMatchedTTUMBean beanObj);
	
	boolean runTTUMProcess(UnMatchedTTUMBean beanObj);
	
	List<Object> getRupayTTUMData(UnMatchedTTUMBean beanObj);
	
	void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String zipName,HttpServletResponse response, boolean ZipFolder );
	
	boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj);
	
	Boolean RupayTtumRollback(UnMatchedTTUMBean beanObj);
	
	List<String> getRupayTTUMDataForTXT(UnMatchedTTUMBean beanObj);
}
