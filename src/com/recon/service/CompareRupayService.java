package com.recon.service;

import java.util.List;

import com.recon.model.CompareBean;

public interface CompareRupayService {

	public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id)throws Exception;
	
	public void updateMatchedRecordsForRupay(List<String> Table_list,CompareBean compareBeanObj,int inRec_set_Id)throws Exception;
	
	public void updateMatchedRecordsForVisa(List<String> Table_list,CompareBean compareBeanObj,int inRec_set_Id)throws Exception;
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id)throws Exception;
	
	public void TTUMRecords(List<String> Table_list,CompareBean comparebeanObj,int inrec_set_id)throws Exception;
	
	public void alterMatchedandSettlementTables(CompareBean comparebeanObj,int inRec_Set_id)throws Exception;
	
	public List<Integer> getRec_set_id(String stCategory)throws Exception;
	
	public List<List<String>> getTableName(int inRec_Set_Id,String stCategory)throws Exception;
	
//	public List<Integer> getRec_domesticset_id(String stCategory)throws Exception;
	
	public void getReconRecords(List<String> tables,CompareBean compareBeanObj , int inRec_Set_id)throws Exception;
	
	public void CleanTables(CompareBean comparebeanObj)throws Exception;
	
	public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception;
	
	public void removeDuplicatesFromCycle2(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception;
	
	public void truncateTempTables(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id);
}
