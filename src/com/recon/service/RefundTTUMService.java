package com.recon.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.NFSSettlementBean;
import com.recon.model.RefundTTUMBean;

public interface RefundTTUMService {

	HashMap<String,Object> validateRefundTTUM(RefundTTUMBean beanObj);
	
	boolean runRefundTTUMMatching(RefundTTUMBean beanObj);
	
	HashMap<String,Object> ValidateRefundProcessing(RefundTTUMBean beanObj);
	
	List<Object> getRefundTTUMProcessData(RefundTTUMBean beanObj);
	
	HashMap<String,Object> validateRefundTTUMGeneration(RefundTTUMBean beanObj);
	
	boolean runRefundTTUMGeneration(RefundTTUMBean beanObj);
	
	List<Object> getRefundTTUMData(RefundTTUMBean beanObj);
	
	HashMap<String,Object> validationForKnockoff(RefundTTUMBean beanObj);
	
	HashMap<String, Object> moveUnmatchedData(MultipartFile file,RefundTTUMBean beanObj);
	
	HashMap<String, Object> knockoffData(MultipartFile file, RefundTTUMBean beanObj);
	
	HashMap<String, Object> getRefundCountAmount(RefundTTUMBean beeanObj);
	
	HashMap<String, Object> checkFullTTUMProcess(RefundTTUMBean beeanObj);
	
	boolean runFullRefundTTUMGeneration(RefundTTUMBean beanObj);
	
	 List<Object> getFullRefundTTUMData(RefundTTUMBean beanObj);
	 
	 void generateExcelTTUM(String stPath, String FileName,List<Object> TTUMData,String TTUMName,HttpServletResponse response);
	 
	 void generateRupayRefund(String stPath,List<Object> ExcelData,String zipName,HttpServletResponse response);
	 
	 List<Object> getVisaRefundTTUMData(RefundTTUMBean beanObj);
	 
	 List<Object> getVisaFullRefundTTUMData(RefundTTUMBean beanObj);
	 
	 boolean RefundRollBack(RefundTTUMBean beanObj)	;
	
	 List<String> getRefundVoucher(RefundTTUMBean beanObj);

	List<Object> getRefundVoucherForRupay(RefundTTUMBean beanObj);
	
	HashMap<String, Object> validateFullTTUMProcess(RefundTTUMBean beanObj);
}
