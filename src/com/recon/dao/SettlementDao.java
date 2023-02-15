package com.recon.dao;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.recon.model.CashnetSettlementFile;
import com.recon.model.CompareSetupBean;
import com.recon.model.ManualFileBean;

public interface SettlementDao {
	
	public void manualReconToSettlement(ManualFileBean manualFileBeanObj)throws Exception;

	public int saveDataToCashnetSettlement(CashnetSettlementFile settlementFile, String createdby, String datepicker);

	public HashMap<String, Object> checkFileProcessedForCashnetSettlement(CompareSetupBean setupBean);

}
