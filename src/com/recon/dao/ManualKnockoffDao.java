package com.recon.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.recon.model.FileSourceBean;
import com.recon.model.ManualKnockoffBean;

public interface ManualKnockoffDao {
	
	String ManualRollBack(String category, String subCategory, String fileDate);
	
	Map<String, Object> validateRollBackDate(String category, String subcategory , String filedate);
	
	List<FileSourceBean> getFiles(String stcategory,String subcategory);
	
	Map<String, Object> validateMastercardAcqRollBack(String category, String subcategory , String filedate);
	
	boolean knockoffAllData(ManualKnockoffBean manualKnockoffBean, MultipartFile file);
	
	int getUpdatedRecordCount(ManualKnockoffBean beanObj);
	
	boolean checkreconStatus(String category, String subCategory, String fileDate);

}
