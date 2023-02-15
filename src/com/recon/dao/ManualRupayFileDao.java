package com.recon.dao;

import java.util.List;

import com.recon.model.CompareBean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FilterationBean;
import com.recon.model.KnockOffBean;
import com.recon.model.ManualFileBean;

public interface ManualRupayFileDao {


	
public int FilterRecords(FilterationBean filterbean)throws Exception;

public void compareManualFile(List<String> tables,CompareSetupBean setupBean);

public void getReconRecords(FilterationBean filterbean);


public void KnockOffRecords(FilterationBean filterBean)throws Exception;

public void moveUnFilteredData(FilterationBean filterbean);

public int moveData(List<String> tables,CompareBean comparebeanObj,int inRec_set_Id);

public void moveToRecon(List<String> tables,CompareBean comparebeanObj,int inRec_Set_id)throws Exception;

public void CleanTables(CompareBean comparebeanObj);

public String chkFileUpload(String Category,CompareSetupBean setupBean, String filedate, String subCat);


}
