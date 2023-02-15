package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.ManualFileDao;
import com.recon.dao.ManualRupayFileDao;
import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.service.ManualFileService;
import com.recon.service.ManualRupayFileService;

@Component
public class ManualRupayFileServiceImpl implements ManualRupayFileService {

	@Autowired
	ManualRupayFileDao manualfiledao;
	
	/*public void compareManualFile(List<String> tables,String stFile_date)
	{
		manualfiledao.compareManualFile(tables,stFile_date);
	}*/
	
	public int FilterRecords(FilterationBean filterbean)throws Exception
	{
		return manualfiledao.FilterRecords(filterbean);
	}
	
	public void compareManualFile(List<String> tables,CompareSetupBean setupBean)
	{
		manualfiledao.compareManualFile(tables, setupBean);
	}

	public void getReconRecords(FilterationBean filterBean)throws Exception
	{
		manualfiledao.getReconRecords(filterBean);
	}
	
	public void KnockOffRecords(FilterationBean filterBean)throws Exception
	{
		manualfiledao.KnockOffRecords(filterBean);
	}
	
	public void moveUnFilteredData(FilterationBean filterbean)
	{
		manualfiledao.moveUnFilteredData(filterbean);
	}
	
	public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id)
	{
		return manualfiledao.moveData(tables, comparebeanObj, inRec_set_Id);
	}
	
	public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)throws Exception
	{
		manualfiledao.moveToRecon(tables, comparebeanObj, inRec_Set_id);
	}
	
	@Override
	public void CleanTables(CompareBean comparebeanObj) throws Exception {
		
		manualfiledao.CleanTables( comparebeanObj);
	}
	
	@Override
	public String chkFileUpload(String Category,CompareSetupBean setupBean, String filedate, String subCat)
	{
		return manualfiledao.chkFileUpload(Category,setupBean, filedate, subCat);
	}
	
	/*public void updateActionTakenTTUMRecord(String stTable_Name,String stFile_date)throws Exception
	{
		manualfiledao.updateActionTakenTTUMRecord(stTable_Name,stFile_date);
	}
	
	public void KnockOffRecords(KnockOffBean knockOffBean)throws Exception
	{
		manualfiledao.KnockOffRecords(knockOffBean);
	}
	
	public int moveData(List<String> tables,String stFile_date)throws Exception
	{
		return manualfiledao.moveData(tables,stFile_date);
	}
	
	public void updateMatchedRecords(List<String> Table_list,String stFile_date) throws Exception
	{
		manualfiledao.updateMatchedRecords(Table_list,stFile_date);
	}
	
	public void moveToRecon(List<String> tables,String stFile_date)throws Exception
	{
		manualfiledao.moveToRecon(tables,stFile_date);
	}*/
}
