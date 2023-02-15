package com.recon.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.NFSSettlementBean;
import com.recon.model.UnMatchedTTUMBean;

public interface RupayTTUMService {
	
	boolean runTTUMProcess(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkTTUMProcessed(UnMatchedTTUMBean beanObj);
	
	String createTTUMFile(UnMatchedTTUMBean beanObj);
	
	boolean checkAndMakeDirectory(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkReconProcessed(UnMatchedTTUMBean beanObj);
	
	List<Object> getTTUMData(UnMatchedTTUMBean beanObj);
	
	HashMap<String,Object> checkTranReconDate(UnMatchedTTUMBean beanObj);
	
	void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,String TTUMName,HttpServletResponse response , boolean ZipFolder );
	
	 List<Object> getNIHTTUMData(UnMatchedTTUMBean beanObj);
	 
	 List<Object> getVisaTTUMData(UnMatchedTTUMBean beanObj);
	 
	 List<Object> getRupayTTUMData(UnMatchedTTUMBean beanObj);
	 
	 void generateRupayExcelTTUM(String stPath,List<Object> ExcelData,String TTUMName,HttpServletResponse response );
	 
	 HashMap<String,Object> checkInternationalTTUMProcessed(UnMatchedTTUMBean beanObj);
	 
	 HashMap<String,Object> checkNIHRecords(UnMatchedTTUMBean beanObj);
	
	 boolean runInternationalTTUMProcess(UnMatchedTTUMBean beanObj);
	 
	 List<Object> getInternationalTTUMData(UnMatchedTTUMBean beanObj);
	 
	 void generateInternationalTTUMFile(String stPath, String FileName,List<Object> TTUMData );
	 
	 List<Object> getNIHReport(UnMatchedTTUMBean beanObj);
	 
	 HashMap<String,Object> checkCardToCardTTUMProcessed(UnMatchedTTUMBean beanObj);
	 
	 boolean runCardToCardTTUMProcess(UnMatchedTTUMBean beanObj);
	 
	 List<Object> getCardToCardTTUMData(UnMatchedTTUMBean beanObj);
	 
}
