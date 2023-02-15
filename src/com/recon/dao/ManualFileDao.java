package com.recon.dao;

import java.util.List;

import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;

public interface ManualFileDao {

	public void compareManualFile(ManualFileBean manualFileBeanObj);
	
	public int FilterRecords(FilterationBean filterbean)throws Exception;
	
	public void getReconRecords(KnockOffBean knockOffBean)throws Exception;
	
	public void updateActionTakenTTUMRecord(ManualFileBean manualFileBeanObj)throws Exception;
	
	public void KnockOffRecords(KnockOffBean knockOffBean)throws Exception;
	
	public int moveData(List<String> tables,ManualFileBean manBeanObj,int inRec_set_id)throws Exception;
	
	public void updateMatchedRecords(List<String> Table_list,ManualFileBean manFileBeanObj,int inRec_Set_id) throws Exception;
	
	public void moveToRecon(List<String> tables,ManualFileBean manualFileBeanObj)throws Exception;
	
	public void clearTables(List<String> tables,ManualFileBean manualFileObj)throws Exception;

	public void CleanTables(CompareBean comparebeanObj);
	
	public void TTUMRecords(List<String> Table_list,ManualFileBean manualBeanObj,int rec_set_id)throws Exception;
	
}
