package com.recon.service;

import java.util.List;

import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;

public interface ManualRupayFileService {
	
//	public void compareManualFile(List<String> tables,String stFile_date);
	
	public int FilterRecords(FilterationBean filterbean)throws Exception;
	
	public void compareManualFile(List<String> tables,CompareSetupBean setupBean);
	
	public void getReconRecords(FilterationBean filterBean)throws Exception;
	
//	public void updateActionTakenTTUMRecord(String stTable_Name,String stFile_date)throws Exception;
	
	public void KnockOffRecords(FilterationBean filterBean)throws Exception;
	
	public void moveUnFilteredData(FilterationBean filterbean);
	
	public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id);
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)throws Exception;
	
	
	public void CleanTables(CompareBean comparebeanObj) throws Exception ;
		
	
	public String chkFileUpload(String Category, CompareSetupBean setupBean,String filedate, String subCat);
	
	
//	public int moveData(List<String> tables,String stFile_date)throws Exception;
	
//	public void updateMatchedRecords(List<String> Table_list,String stFile_date) throws Exception;
	
//	public void moveToRecon(List<String> tables,String stFile_date)throws Exception;


}
