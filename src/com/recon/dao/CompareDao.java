package com.recon.dao;

import java.util.List;

import com.recon.model.CompareBean;

public interface CompareDao {
	
public int moveData(List<String> tables,CompareBean comparebean,int inRec_Set_in)throws Exception;
	
	public void updateMatchedRecords(List<String> Table_list,CompareBean comparebeanObj , int inRec_Set_id)throws Exception;
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj)throws Exception;
	
	public void TTUMRecords(List<String> Table_list,CompareBean comparebeanObj,int rec_set_id)throws Exception;
	
	//public String getStatus(CompareBean compareBean,String stProcess);
	
	public List<Integer> getRec_set_id(String stCategory)throws Exception;
	
	public List<String> getTableName(int inRec_Set_Id,String stCategory)throws Exception;
	
	public void clearTables(List<String> tables, CompareBean compareBeanObj)
			throws Exception;
	
	public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception ;


}
