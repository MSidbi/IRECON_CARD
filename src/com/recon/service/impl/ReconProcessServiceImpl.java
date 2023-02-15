package com.recon.service.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.IReconProcessDao;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.service.IReconProcessService;

@Component
public class ReconProcessServiceImpl implements IReconProcessService {
	
	@Autowired IReconProcessDao reconprocess;

	@Override
	public String chkFileUpload(String Category, String filedate, List<CompareSetupBean> compareSetupBeans,String SubCat) throws Exception {
		
		return reconprocess.chkFileUpload(Category, filedate,compareSetupBeans,SubCat);
	}

	@Override
	public List<CompareSetupBean> getFileList(String category, String filedate,String subcat) throws Exception {
		
		return reconprocess.getFileList(category, filedate,subcat);
	}

	@Override
	public String validateFile(String category,
			List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception {
		
		return reconprocess.validateFile( category,compareSetupBeans,  filedate);
	}

	@Override
	public boolean processFile(String category,List<CompareSetupBean> compareSetupBeans, String filedate,String Createdby,String subCat) throws Exception {
		return reconprocess.processFile( category, compareSetupBeans,  filedate, Createdby,subCat);
		
	}

	@Override
	public boolean compareFiles(String category, String filedate,
			CompareBean setupBeans,String subcat,String dollar_val) throws Exception {
	
		return reconprocess.compareFiles(category, filedate, setupBeans, subcat,dollar_val);
	}

	@Override
	public CompareSetupBean chkStatus(List<CompareSetupBean> compareSetupBeans,
			String category, String filedate) throws Exception {
		
		return reconprocess.chkStatus( compareSetupBeans, category,  filedate);
	}

	@Override
	public HashMap<String, Object> checkRupayIntRecon(String filedate){
		
		return reconprocess.checkRupayIntRecon(filedate);
	}
	
	@Override
	public HashMap<String, Object> processRupayIntRecon(String filedate, String entryBy){
		
		return reconprocess.processRupayIntRecon(filedate, entryBy);
	}
	
	@Override
	public HashMap<String, Object> checkCardtoCardRecon(String filedate){
		
		return reconprocess.checkCardtoCardRecon(filedate);
	}
	
	@Override
	public boolean CardtoCardCompareData(String category,
			String filedate, String entry_by) throws ParseException, Exception
	{
		return reconprocess.CardtoCardCompareData(category, filedate, entry_by);
	}
	
	@Override
	public HashMap<String, Object> checkCardtoCardRawFiles(String filedate)
	{
		return reconprocess.checkCardtoCardRawFiles(filedate);
	}
	
	@Override
	public HashMap<String, Object> checkCardtoCardPrevRecon(String filedate){
		
		return reconprocess.checkCardtoCardPrevRecon(filedate);
	}
}
