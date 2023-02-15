package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.CompareDao;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.service.CompareService;

@Component
public class CompareServiceImpl implements CompareService {

	@Autowired
	CompareDao comparedao;
	
	public int moveData(List<String> tables,CompareBean comparebean,int inRec_Set_in)throws Exception
	{
		return comparedao.moveData(tables,comparebean,inRec_Set_in);
	}

	public void updateMatchedRecords(List<String> Table_list,CompareBean comparebeanObj,int inRec_Set_id)throws Exception
	{
		comparedao.updateMatchedRecords(Table_list,comparebeanObj,inRec_Set_id);
	}
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj)throws Exception
	{
		comparedao.moveToRecon(tables,comparebeanObj);
	}
	public void TTUMRecords(List<String> Table_list,CompareBean comparebeanObj,int rec_set_id)throws Exception
	{
		comparedao.TTUMRecords(Table_list,comparebeanObj,rec_set_id);
	}
	
	/*public String getStatus(CompareBean compareBean,String stProcess)throws Exception
	{
		return comparedao.getStatus(compareBean, stProcess);
	}*/
	
	public List<Integer> getRec_set_id(String stCategory)throws Exception
	{
		return comparedao.getRec_set_id(stCategory);
	}
	
	public List<String> getTableName(int inRec_Set_Id, String stCategory)throws Exception
	{
		return comparedao.getTableName(inRec_Set_Id,stCategory);
	}
	
	public void CleanTables(CompareBean comparebeanObj)throws Exception
	{
		CleanTables(comparebeanObj);
	}

	
	public void clearTables(List<String> tables, CompareBean compareBeanObj)
			throws Exception {
		 comparedao.clearTables(tables, compareBeanObj);
		
	}
	
	public void removeDuplicates(List<String> tables,CompareBean compareBeanObj,int inrec_set_id) throws Exception 
	{
		comparedao.removeDuplicates(tables, compareBeanObj, inrec_set_id);
	}
	
}
