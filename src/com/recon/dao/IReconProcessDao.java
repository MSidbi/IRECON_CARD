package com.recon.dao;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;

public interface IReconProcessDao {

	public String chkFileUpload(String Category,String filedate, List<CompareSetupBean> compareSetupBeans, String subCat) throws Exception;
	
	public List<CompareSetupBean> getFileList(String category,String filedate,String subcat) throws Exception;

	public String validateFile(String category,List<CompareSetupBean> compareSetupBeans, String filedate) throws Exception;

	public boolean processFile(String category,List<CompareSetupBean> compareSetupBeans, String filedate,String Createdby,String subcat) throws Exception;
	
	public boolean compareFiles(String category, String filedate ,CompareBean setupBeans,String subcat,String dollar_val ) throws Exception ;

	public CompareSetupBean chkStatus(List<CompareSetupBean> compareSetupBeans,
			String category, String filedate) throws Exception;
	
	HashMap<String, Object> checkRupayIntRecon(String fileDate);
	
	HashMap<String, Object> processRupayIntRecon(String filedate, String entryBy);
	
	HashMap<String, Object> checkCardtoCardRecon(String filedate);
	
	boolean CardtoCardCompareData(String category,String filedate, String entry_by) throws ParseException, Exception ;
	
	HashMap<String, Object> checkCardtoCardRawFiles(String filedate);
	
	HashMap<String, Object> checkCardtoCardPrevRecon(String filedate);
	
}
