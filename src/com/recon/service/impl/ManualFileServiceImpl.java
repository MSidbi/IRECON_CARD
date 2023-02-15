package com.recon.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.ManualFileDao;
import com.recon.model.CompareBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;
import com.recon.service.ManualFileService;

@Component
public class ManualFileServiceImpl implements ManualFileService {

	@Autowired
	ManualFileDao manualfiledao;

	

	@Override
	public int FilterRecords(FilterationBean filterbean) throws Exception {
		// TODO Auto-generated method stub
		return manualfiledao.FilterRecords(filterbean);
	}

	@Override
	public void getReconRecords(KnockOffBean knockOffBean) throws Exception {
		// TODO Auto-generated method stub
		manualfiledao.getReconRecords(knockOffBean);
		
	}

	@Override
	public void updateActionTakenTTUMRecord(ManualFileBean manualFileBeanObj)
			throws Exception {
		manualfiledao.updateActionTakenTTUMRecord(manualFileBeanObj);
		
	}

	@Override
	public void KnockOffRecords(KnockOffBean knockOffBean) throws Exception {
			manualfiledao.KnockOffRecords(knockOffBean);		
	}

	@Override
	public int moveData(List<String> tables, ManualFileBean manBeanObj,
			int inRec_set_id) throws Exception {

		
		return manualfiledao.moveData(tables, manBeanObj, inRec_set_id);
	}

	@Override
	public void updateMatchedRecords(List<String> Table_list,
			ManualFileBean manFileBeanObj, int inRec_Set_id) throws Exception {
		// TODO Auto-generated method stub
		manualfiledao.updateMatchedRecords(Table_list, manFileBeanObj, inRec_Set_id);
		
	}

	@Override
	public void moveToRecon(List<String> tables,
			ManualFileBean manualFileBeanObj) throws Exception {

		manualfiledao.moveToRecon(tables, manualFileBeanObj);
	}

	@Override
	public void clearTables(List<String> tables, ManualFileBean manualFileObj)
			throws Exception {
		manualfiledao.clearTables(tables, manualFileObj);
		
	}

	@Override
	public void compareManualFile(ManualFileBean manualFileBeanObj) {
	
		manualfiledao.compareManualFile(manualFileBeanObj);
	}
	
	
	@Override
	public void TTUMRecords(List<String> Table_list,ManualFileBean manualBeanObj,int rec_set_id)throws Exception
	{
		manualfiledao.TTUMRecords(Table_list, manualBeanObj, rec_set_id);
	}
	
}
