package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.CompareRupayDao;
import com.recon.model.CompareBean;
import com.recon.service.CompareRupayService;

@Component
public class CompareRupayServiceImpl implements CompareRupayService{
	@Autowired
	CompareRupayDao compareRupaydao;
	
	public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id) throws Exception
	{
		return compareRupaydao.moveData(tables,comparebeanObj,inRec_set_Id);
	}
	public void updateMatchedRecordsForRupay(List<String> Table_list,CompareBean compareBeanObj,int inRec_set_Id)throws Exception
	{
		compareRupaydao.updateMatchedRecordsForRupay(Table_list,compareBeanObj,inRec_set_Id);
	}
	public void updateMatchedRecordsForVisa(List<String> Table_list,CompareBean compareBeanObj,int inRec_set_Id)throws Exception
	{
		compareRupaydao.updateMatchedRecordsForVisa(Table_list,compareBeanObj,inRec_set_Id);
	}
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id)throws Exception
	{
		compareRupaydao.moveToRecon(tables,comparebeanObj,inRec_set_Id);
	}
	public void TTUMRecords(List<String> Table_list,CompareBean comparebeanObj,int inrec_set_id)throws Exception
	{
		compareRupaydao.TTUMRecords(Table_list,comparebeanObj,inrec_set_id);
	}
	
	public void alterMatchedandSettlementTables(CompareBean comparebeanObj,int inRec_Set_id)throws Exception
	{
		compareRupaydao.alterMatchedandSettlementTables(comparebeanObj,inRec_Set_id);
	}
	
	public List<Integer> getRec_set_id(String stCategory)throws Exception
	{
		return compareRupaydao.getRec_set_id(stCategory);
	}
	
	public List<List<String>> getTableName(int inRec_Set_Id,String stCategory)throws Exception
	{
		return compareRupaydao.getTableName(inRec_Set_Id,stCategory);
	}
	
	/*public List<Integer> getRec_domesticset_id(String stCategory)throws Exception
	{
		return compareRupaydao.getRec_domesticset_id(stCategory);
	}*/
	
	public void getReconRecords(List<String> tables,CompareBean compareBeanObj , int inRec_Set_id)throws Exception
	{
		compareRupaydao.getReconRecords(tables, compareBeanObj, inRec_Set_id);
	}
	
	public void CleanTables(CompareBean comparebeanObj)throws Exception
	{
		compareRupaydao.CleanTables(comparebeanObj);
	}

	public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception
	{
		compareRupaydao.removeDuplicates(tables, compareBeanObj, inrec_set_id);
	}
	
	public void removeDuplicatesFromCycle2(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception
	{
		compareRupaydao.removeDuplicatesFromCycle2(tables, compareBeanObj, inrec_set_id);
	}
	
	public void truncateTempTables(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)
	{
		compareRupaydao.truncateTempTables(tables, comparebeanObj, inRec_Set_id);
	}
}
